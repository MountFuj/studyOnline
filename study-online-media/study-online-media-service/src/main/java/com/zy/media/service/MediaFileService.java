package com.zy.media.service;


import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.base.model.RestResponse;
import com.zy.media.model.dto.QueryMediaParamsDto;
import com.zy.media.model.dto.UploadFileParamsDto;
import com.zy.media.model.dto.UploadFileResultDto;
import com.zy.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
 public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

 public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto,String localFilePath);
 public MediaFiles addMediaFileToDb(Long companyId,String fileMd5,UploadFileParamsDto uploadFileParamsDto,String bucket,String objectName);
/***
 * @description 检查文件是否存在
 * @param fileMd5
 * @return
 * @author zhangyu
 * @date
*/
 public RestResponse<Boolean> checkFile(String fileMd5);

/***
 * @description 检查分块是否存在
 * @param fileMd5
 * @param chunkIndex
 * @return
 * @author zhangyu
 * @date
*/
 public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
 //上传分块
 public RestResponse<Boolean> uploadChunk(String fileMd5,int chunk,String localChunkFilePath);
 //合并分块
 public RestResponse mergeChunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);
 public File downloadFileFromMinio(String bucket, String objectName);
 public boolean addMediaFilesToMinio(String localFilePath, String mimeType, String bucket, String objectName);

    MediaFiles getFileById(String mediaId);
}
