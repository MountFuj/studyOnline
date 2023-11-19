package com.zy.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.base.model.RestResponse;
import com.zy.media.mapper.MediaFilesMapper;
import com.zy.media.mapper.MediaProcessMapper;
import com.zy.media.model.dto.QueryMediaParamsDto;
import com.zy.media.model.dto.UploadFileParamsDto;
import com.zy.media.model.dto.UploadFileResultDto;
import com.zy.media.model.po.MediaFiles;
import com.zy.media.model.po.MediaProcess;
import com.zy.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    MediaProcessMapper mediaProcessMapper;
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
        String objectName = defaultFolderPath + fileMd5 + extension;
        //上传文件到minio
        boolean result = addMediaFilesToMinio(localFilePath, mimeType, bucket_files, objectName);
        if (!result) {
            StudyOnlineException.cast("上传文件失败");
        }
        //入库文件信息
        MediaFiles mediaFiles = currentProxy.addMediaFileToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
        if (mediaFiles == null) {
            StudyOnlineException.cast("文件上传后保存信息失败");
        }
        //准备返回的对象
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
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
    public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(localFilePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            //上传文件
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("上传文件到minio成功,bucket:{},objectName:{}", bucket, objectName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件出错了,bucket:{},objectName:{},错误信息:{}", bucket, objectName, e.getMessage());
        }
        return false;
    }

    @Override
    public MediaFiles getFileById(String mediaId) {
        if (StringUtils.isNotBlank(mediaId)) {
            MediaFiles mediaFiles = mediaFilesMapper.selectById(mediaId);
            return mediaFiles;
        }
        return null;
    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String folder = simpleDateFormat.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    //提供文件，生成文件md5值
    private String getFileMd5(File file) {
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
    public MediaFiles addMediaFileToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        //根据文件名称取出媒体类型
        //扩展名
        String extension = null;
        if(objectName.indexOf(".")>=0){
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        //获取扩展名对应的媒体类型
        String contentType = getMimeType(extension);
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
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
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            //上传时间
            mediaFiles.setCreateDate(LocalDateTime.now());
            //状态
            mediaFiles.setStatus("1");
            //审核状态
            mediaFiles.setAuditStatus("002003");
            //插入数据库
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.debug("向数据库保存文件失败");
                return null;
            }
            //如果是avi视频添加到视频待处理表
            if(contentType.equals("video/x-msvideo")) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                mediaProcess.setStatus("1");//未处理
                mediaProcess.setUrl(null);
                mediaProcessMapper.insert(mediaProcess);
            }
            return mediaFiles;
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //objectName
            String objectName = mediaFiles.getFilePath();
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build();
            //查询minio
            try {
                FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
                if (inputStream != null) {
                    return RestResponse.success(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        //分块路径是md5前两位为子目录，chunk存储分块文件
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket_videofiles)
                .object(chunkFileFolderPath + chunkIndex)
                .build();
        //查询minio
        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                return RestResponse.success(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> uploadChunk(String fileMd5, int chunk, String localChunkFilePath) {
        //分块文件路径
        String chunkFilePath = getChunkFileFolderPath(fileMd5) + chunk;
        String mimeType = getMimeType(null);
        //将分块文件上传到minio
        boolean b = addMediaFilesToMinio(localChunkFilePath, mimeType, bucket_videofiles, chunkFilePath);
        if (!b) {
            return RestResponse.validfail(false, "上传分块失败");
        }
        return RestResponse.success(true);
    }

    @Override
    public RestResponse mergeChunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //找到分块文件调用minio的sdk进行文件合并
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal).map(i -> ComposeSource.builder().bucket(bucket_videofiles).object(chunkFileFolderPath+i).build())
                .collect(Collectors.toList());
        //指定合并后的objectName等信息
        //扩展名
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        String objectName = getFilePathByMd5(fileMd5, extension);
        ComposeObjectArgs composeObjectArgs=ComposeObjectArgs.builder()
                .bucket(bucket_videofiles)
                .sources(sources)
                .object(objectName)
                .build();
        try {
            minioClient.composeObject(composeObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("合并文件出错,bucket:{},objectName:{},错误信息:{}",bucket_videofiles,objectName,e.getMessage());
            return RestResponse.validfail(false,"合并分块异常");
        }
        //校验合并后和原始文件是否一致
        File file = downloadFileFromMinio(bucket_videofiles, objectName);
        try(FileInputStream fileInputStream=new FileInputStream(file)){
            //计算合并后文件的md5
            String md5Hex = DigestUtils.md5Hex(fileInputStream);
            if(!fileMd5.equals(md5Hex)){
                log.error("原始文件和合并后的文件不一致，原始文件:{},合并后的文件:{}",fileMd5,md5Hex);
                return RestResponse.validfail(false,"文件校验失败");
            }
            uploadFileParamsDto.setFileSize(file.length());
        }catch (Exception e){
            return RestResponse.validfail(false,"文件校验失败");
        }
        //文件信息入库
        MediaFiles mediaFiles =currentProxy.addMediaFileToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videofiles, objectName);
        if(mediaFiles==null){
            return RestResponse.validfail(false,"文件入库失败");
        }
        //清理分块文件
        clearChunkFiles(chunkFileFolderPath,chunkTotal);
        return RestResponse.success(true);
    }

    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    private String getFilePathByMd5(String fileMd5,String fileExt){
        return fileMd5.substring(0,1)+"/"+fileMd5.substring(1,2)+"/"+fileMd5+"/"+fileMd5+fileExt;
    }
    public File downloadFileFromMinio(String bucket,String objectName){
        //临时文件
        File minioFile=null;
        FileOutputStream outputStream=null;
        try {
            InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            //创建临时文件
            minioFile= File.createTempFile("minio",".merge");
            outputStream=new FileOutputStream(minioFile);
            IOUtils.copy(stream,outputStream);
            return minioFile;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    private void clearChunkFiles(String chunkFileFolderPath,int chunkTotal){
        Iterable<DeleteObject> objects=Stream.iterate(0,i->++i).limit(chunkTotal).map(i->new DeleteObject(chunkFileFolderPath+i)).collect(Collectors.toList());
        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket(bucket_videofiles).objects(objects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        //真正删除
        results.forEach(f->{
            try {
                DeleteError deleteError = f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
