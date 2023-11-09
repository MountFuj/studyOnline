package com.zy.media.service.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.zy.base.utils.Mp4VideoUtil;
import com.zy.media.model.po.MediaProcess;
import com.zy.media.service.MediaFileService;
import com.zy.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
public class VideoTask {
    @Autowired
    MediaProcessService mediaProcessService;
    @Autowired
    MediaFileService mediaFileService;
    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //确定cpu的核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理的任务
        List<MediaProcess> mediaProcessList = mediaProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        //任务数量
        int size = mediaProcessList.size();
        log.debug("当前任务数量,size:{}",size);
        if(size<=0){
            return;
        }
        //创建一个线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatch=new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {
                    Long taskId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();
                    //开启任务
                    boolean b = mediaProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败,任务id:{}", taskId);
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "抢占任务失败");
                        return;
                    }
                    //执行视频转码
                    String bucket = mediaProcess.getBucket();
                    String filePath = mediaProcess.getFilePath();
                    //下载文件到本地
                    File file = mediaFileService.downloadFileFromMinio(bucket, filePath);
                    if (file == null) {
                        log.debug("文件下载出错,bucket:{},objectName:{}", bucket, filePath);
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "下载文件到本地失败");
                        return;
                    }
                    //ffmpeg的路径
                    String ffmpeg_path = ffmpegpath;//ffmpeg的安装位置
                    //源avi视频的路径
                    String video_path = file.getAbsolutePath();
                    //创建临时文件
                    File mp4File = null;
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常,{}", e.getMessage());
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "创建临时文件失败");
                        return;
                    }
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    //转换后mp4文件的路径
                    String mp4_path = mp4File.getAbsolutePath();
                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败,errorMessage:{}", result);
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频转码失败");
                        return;
                    }
                    //上传到minio
                    boolean b1 = mediaFileService.addMediaFilesToMinio(mp4_path, "video/mp4", bucket, filePath);
                    if (!b1) {
                        log.debug("视频上传到minio失败");
                        mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, null, "视频上传到minio失败");
                        return;
                    }
                    //url
                    String filePathByMd5 = getFilePathByMd5(fileId, ".mp4");
                    //保存任务处理结果
                    mediaProcessService.saveProcessFinishStatus(taskId, "3", fileId, filePathByMd5, "");
                }finally {
                    //计数器减一
                    countDownLatch.countDown();
                }
            });
        });
        //阻塞 指定最大阻塞时长 避免阻塞死程序
        countDownLatch.await(30,TimeUnit.MINUTES);
    }
    private String getFilePathByMd5(String fileMd5,String fileExt){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+fileExt;
    }
}
