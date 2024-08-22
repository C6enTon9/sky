package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {

        log.info("文件上传{}",file);

        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取文件名后缀 #ccc.png

//            String[] split = originalFilename.split(".");
//            String suffix = split[split.length - 1];

            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

            //构造新的文件名
            String fileName = UUID.randomUUID().toString() + suffix;


            String filePath = aliOssUtil.upload(file.getBytes(), fileName);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败:{}",e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);

    }
}
