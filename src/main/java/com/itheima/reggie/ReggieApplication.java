package com.itheima.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("Project Riggie Takeout launched successfully.\n瑞吉外卖启动成功。\n 您可以通过 http://localhost:8080/backend/page/login/login.html 登录管理后台。\n 通过：http://localhost:8080/front/page/login.html 登录客户端");
    }
}
