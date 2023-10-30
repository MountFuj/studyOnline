package com.zy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.media.mapper.MediaFilesMapper;
import com.zy.media.mapper.MediaProcessMapper;
import com.zy.media.model.dto.QueryMediaParamsDto;
import com.zy.media.model.dto.UploadFileParamsDto;
import com.zy.media.model.dto.UploadFileResultDto;
import com.zy.media.model.po.MediaFiles;
import com.zy.media.model.po.MediaProcess;
import com.zy.media.service.MediaFileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.attribute.standard.Media;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;

    @Autowired
    MinioClient minioClient;
    @Autowired
    MediaFileService currentProxy;

    @Autowired

    //普通文件存储的桶
    @Value("${minio.bucket.files}")
    private String bucket_files;
    //视频文件存储的桶
    @Value("${minio.bucket.videofiles}")
    private String bucket_videofiles;
    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath) {
        //文件名
        String filename = uploadFileParamsDto.getFilename();
        //先得到扩展名
        String extension = filename.substring(filename.lastIndexOf("."));
        //得到mimeType
        String mimeType = getMimeType(extension);
        //子目录
        String defaultFolderPath = getDefaultFolderPath();
        //文件md5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        //对象名
        String objectName=defaultFolderPath+fileMd5+extension;
        //上传文件到minio
        boolean result = addMediaFilesToMinio(localFilePath, mimeType, bucket_files, objectName);
        if(!result){
            StudyOnlineException.cast("上传文件失败");
        }
        //入库文件信息
        MediaFiles mediaFiles = currentProxy.addMediaFileToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        if(mediaFiles==null){
            StudyOnlineException.cast("文件上传后保存信息失败");
        }
        //准备返回的对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles,uploadFileResultDto);
        return uploadFileResultDto;
    }
    //根据扩展名获取mineType
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //将文件上传到minio
    public boolean addMediaFilesToMinio(String localFilePath,String mimeType,String bucket,String objectName){
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            //上传文件
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}",bucket,objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错了,bucket:{},objectName:{},错误信息:{}",bucket,objectName,e.getMessage());
        }
        return false;
    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String folder=simpleDateFormat.format(new Date()).replace("-","/")+"/";
        return folder;
    }

    //提供文件，生成文件md5值
   private String getFileMd5(File file){
       try {
           FileInputStream fileInputStream = new FileInputStream(file);
           String fileMd5 = DigestUtils.md5Hex(fileInputStream);
           return fileMd5;
       } catch (Exception e) {
           e.printStackTrace();
           return null;
       }
   }
    //保存文件信息到数据库
    @Transactional
    public MediaFiles addMediaFileToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName){
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if(mediaFiles==null){
            mediaFiles=new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto,mediaFiles);
            //文件id
            mediaFiles.setId(fileMd5);
            //机构id
            mediaFiles.setCompanyId(companyId);
            //桶
            mediaFiles.setBucket(bucket);
            //file_path
            mediaFiles.setFilePath(objectName);
            //file_id
            mediaFiles.setFileId(fileMd5);
            //url
            mediaFiles.setUrl("/"+bucket+"/"+objectName);
            //上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //状态
            mediaFiles.setStatus("1");
            //审核状态
            mediaFiles.setAuditStatus("002003");
            //插入数据库
            int insert = mediaFilesMapper.insert(mediaFiles);
            if(insert<=0){
                log.debug("向数据库保存文件失败");
                return null;
            }
            return mediaFiles;
        }
        return mediaFiles;
    }
}
