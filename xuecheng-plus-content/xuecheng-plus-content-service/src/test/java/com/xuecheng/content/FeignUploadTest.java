package com.xuecheng.content;

import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Test
    public void test() throws IOException {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("D:\\Project\\SpringCloud\\XuCheng\\upload\\2.html"));
        String upload = mediaServiceClient.upload(multipartFile, "course/2.html");
        if(upload==null){
            System.out.println("走了降级服务");
        }
    }

}
