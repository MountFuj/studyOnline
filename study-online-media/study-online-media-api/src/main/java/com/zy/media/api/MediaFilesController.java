package com.zy.media.api;

import com.zy.base.exception.StudyOnlineException;
import com.zy.base.model.PageParams;
import com.zy.base.model.PageResult;
import com.zy.media.model.dto.QueryMediaParamsDto;
import com.zy.media.model.dto.UploadFileParamsDto;
import com.zy.media.model.dto.UploadFileResultDto;
import com.zy.media.model.po.MediaFiles;
import com.zy.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {


    @Autowired
    MediaFileService mediaFileService;


    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
    }

    @ApiOperation("上传图片接口")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadFileResultDto upload(@RequestPart("filedata")MultipartFile filedata) throws Exception {
       //准备上传文件的信息
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //原始文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //文件类型
        uploadFileParamsDto.setFileType("001001");
        //接收到文件了
        //创建临时文件
        File tempFile = File.createTempFile("mino", ".temp");
        filedata.transferTo(tempFile);
        //机构id
        Long companyId=22L;
        //文件路径
        String absolutePath = tempFile.getAbsolutePath();
        //调用service
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath);
        return uploadFileResultDto;
    }
}
