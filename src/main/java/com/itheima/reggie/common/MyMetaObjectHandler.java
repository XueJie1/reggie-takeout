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
        Long userId = BaseContext.getCurrentId();
        // If userId is null, assume it's an admin operation and use a default admin ID (e.g., 1L)
        if (userId == null) {
            // TODO: Confirm the actual admin user ID or mechanism to get it
            userId = 1L; // Assuming 1L is the admin user ID
            log.warn("userId is null during insert operation, using default admin ID: {}", userId);
        }
        metaObject.setValue("createUser", userId);
        metaObject.setValue("updateUser", userId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充: [update]");
        log.info(metaObject.toString());
        // updateFill 只更新 updateTime 和 updateUser
        metaObject.setValue("updateTime", LocalDateTime.now());
        Long userId = BaseContext.getCurrentId();
        // If userId is null, assume it's an admin operation and use a default admin ID (e.g., 1L)
        if (userId == null) {
            // TODO: Confirm the actual admin user ID or mechanism to get it
            userId = 1L; // Assuming 1L is the admin user ID
            log.warn("userId is null during update operation, using default admin ID: {}", userId);
        }
        metaObject.setValue("updateUser", userId);
    }
}
