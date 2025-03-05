package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载功能
 * @author zeyic
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {
    // 在application.yaml中配置自定义变量来自定义文件上传路径
//    @Value("${reggie.base-upload-path}")
    private String basePath = System.getProperty("user.dir");   // 把项目根目录作为上传根目录

    /**
     * 文件上传
     * @param file 前端传过来的文件
     * @return Result 对象，如果上传成功，data 中应该存有上传文件后保存在服务器的文件名。
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("file upload: {}", file.getName());
        // 文件转存
        // 可以在application.yaml中配置自定义变量来自定义文件上传路径
        if (!file.isEmpty()) {
            try {
                // 获取文件名
                String originFileName = file.getOriginalFilename();
                // 获得文件后缀（含点，比如".jpg"）
                String suffix = originFileName.substring(originFileName.lastIndexOf("."));
                // 随机文件名，如 123.png
                String UUIDFileName = UUID.randomUUID().toString() + suffix;
                // 完整路径，如 /home/user/uploads/123.png
                String filePath = basePath + UUIDFileName;
                File dest = new File(filePath);
                // 如果文件的父目录不存在，则创建
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();  // 创建目录
                }
                file.transferTo(dest);

                log.info("file upload: {}. Meow~", file.getName());
                // 注意此处返回的是文件名（不含路径）
                return R.success(UUIDFileName);
            } catch(IOException e) {
                e.printStackTrace();
                return R.error("文件上传失败");
            }
        } else {
            return R.error("服务器得到了空文件");
        }
    }

    /**
     * 文件下载，把刚刚上传到服务器的图片回显到浏览器
     * @param name 文件名，不含 basePath
     * @param response HttpServletResponse
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        // 1. 根据 name 读取图片(IO流中的 FileInputStream)
        String path = basePath + name;
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            // 2. 把读取到的图片通过 response 中的输出流写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");  // 设置响应头，告诉浏览器返回的是图片
            byte[] bytes = new byte[1024 * 50];
            int length = 0;
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();   // 刷新缓冲区
            }
            // 关闭两个流
            inputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
            log.error("找不到文件");
            e.printStackTrace();
        } catch (IOException e) {
//            throw new RuntimeException(e);
            log.error("IOException");
            e.printStackTrace();
        }
    }
}
