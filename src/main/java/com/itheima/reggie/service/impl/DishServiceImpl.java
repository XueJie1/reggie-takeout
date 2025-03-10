package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private DishService dishService;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
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

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        // 查询菜品信息并复制到 dishDto
        Dish dishInfo = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dishInfo, dishDto);
        // 查询口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        // 把查到的口味数据复制到 dishDto 中
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更改菜品和口味数据。
     * 口味数据因为在另一个表中，所以可以先删除在添加，即使什么也没动
     * @param dishDto
     * @return
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 更新dish表
        this.updateById(dishDto);
        // 删除 flavor 原有数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 往 flavor 添加数据，注意手动添加 dishId
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(int i = 0; i < flavors.size();i++) {
            flavors.get(i).setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        dishMapper.deleteBatchIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        dishFlavorService.remove(queryWrapper);
    }

    /**
     * 更改菜品状态（启售/停售）
     * @param status 0:从启售到停售，1:从停售到启售
     * @param ids 要启售/停售的id
     */
    @Override
    public void changeStatus(int status, List<Long> ids) {
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", status).in("id", ids);
        this.update(updateWrapper);
    }
}
