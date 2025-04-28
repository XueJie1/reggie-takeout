// MyMetaObjectHandler.java
package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充公共字段，如 updateTime等
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充: [insert]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long userId = BaseContext.getCurrentId(); // Change to getCurrentId
        if (userId != null) {
            metaObject.setValue("createUser", userId);
            metaObject.setValue("updateUser", userId);
        } else {
            log.error("userId 为空！目前的userId:[{}]", userId); // 打印 userId 的值
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充: [update]");
        log.info(metaObject.toString());
        // updateFill 只更新 updateTime 和 updateUser
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long userId = BaseContext.getCurrentId(); // Change to getCurrentId
        if (userId != null) {
            metaObject.setValue("updateUser", userId);
        }else{
            log.error("userId 为空！目前的userId:[{}]", userId); // 打印 userId 的值
        }
    }
}
