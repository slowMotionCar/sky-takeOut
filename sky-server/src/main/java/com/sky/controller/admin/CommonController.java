package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @Description CommonController
 * @Author Zhilin
 * @Date 2023-09-24
 */
@RestController
@Slf4j
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传接口
     *
     * @param file
     * @return
     */
    @ApiOperation("文件上传接口")
    @PostMapping("/admin/common/upload")
    public Result<String> uploadFiles(@RequestBody MultipartFile file) {


        log.info("{}文件正在上传", file);
        try {

            String originalFilename = file.getOriginalFilename();
            String[] split = originalFilename.split("\\.");
            String lastIndex = split[1];
            String string = UUID.randomUUID().toString();
            String newName = string + lastIndex;
            String uploadName = aliOssUtil.upload(file.getBytes(), newName);
            return Result.success(uploadName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
