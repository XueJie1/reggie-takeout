package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DishService extends IService<Dish> {
    // 同时插入两张表：dish, dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    // 同时查询两张表：dish, dish_flavor
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);

    public void deleteBatch(List<Long> ids);
    public void changeStatus(int status, List<Long> ids);
}
