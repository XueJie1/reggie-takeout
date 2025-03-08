package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    // 注入 口味 sevice ，存到 dish_flavor 口味表要用到
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品并保存口味数据
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品数据到数据库，由于 DishDto extends Dish，所以可以直接调 ServiceImpl<DishMapper, Dish> 的 save() 方法。
        this.save(dishDto);

        if (dishDto.getFlavors() != null) {
            // 获取菜品 ID，之后要存到口味表里。
            Long dishId = dishDto.getId();
            List<DishFlavor> flavorList = dishDto.getFlavors(); // 此时 flavorList 中没有 dishId，所以要手动添加
            for (int i = 0; i < flavorList.size(); i++) {
                flavorList.get(i).setDishId(dishId);
            }
            dishFlavorService.saveBatch(flavorList);
        } else {
            log.info("没有口味，所以不会向 dish_flavor 表中插入数据。");
        }
    }
}
