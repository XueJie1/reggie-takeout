package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 套餐菜品关系
 */
@Data
public class SetmealDish implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)    // 解决序列化时Long类型数据精度丢失的问题
    private Long id;


    //套餐id
    @JsonFormat(shape = JsonFormat.Shape.STRING)    // 解决序列化时Long类型数据精度丢失的问题
    private Long setmealId;


    //菜品id
    @JsonFormat(shape = JsonFormat.Shape.STRING)    // 解决序列化时Long类型数据精度丢失的问题
    private Long dishId;


    //菜品名称 （冗余字段）
    private String name;

    //菜品原价
    private BigDecimal price;

    //份数
    private Integer copies;


    //排序
    private Integer sort;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)    // 解决序列化时Long类型数据精度丢失的问题
    private Long createUser;


    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING)    // 解决序列化时Long类型数据精度丢失的问题
    private Long updateUser;


    //是否删除
    private Integer isDeleted;
}
