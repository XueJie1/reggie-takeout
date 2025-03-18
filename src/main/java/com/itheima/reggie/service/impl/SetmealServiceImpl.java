package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Lazy
    @Autowired
    private SetmealDishService setmealDishService;
    @Lazy
    @Autowired
    private SetmealService setmealService;
    @Lazy
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     *
     * @param setmealDto 里头有套餐信息，以及套餐所包含的菜品信息
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        // 保存 SetmealDish
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 套餐分页查询
     *
     * @param page     第几页
     * @param pageSize 每页有几个
     * @param name     查询名
     * @return Page<SetmealDto>
     */
    public Page<SetmealDto> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, queryWrapper);
        // 拷贝到 dtoPage, 因为 Setmeal 和 SetmealDto 中的 records 虽同为 List<T> 类型，但泛型不同，所以需排除此属性
        BeanUtils.copyProperties(pageInfo, dtoPage);
        // 处理 records 数据
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item, dto);
            // 查找 categoryId
            Long categoryId = item.getCategoryId();
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String categoryName = byId.getName();
                dto.setCategoryName(categoryName);
            }
            return dto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    /**
     * 更改套餐信息时，处理信息的回显
     *
     * @param id 套餐 id
     * @return SetmealDto
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        SetmealDto result = new SetmealDto();
        // 先查找 Setmeal
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, result);
        // 再查找 List<SetmealDish> （套餐与菜品的关联信息）
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        // 把查到的List<SetmealDish> 赋值给 result
        if (list != null) {
            result.setSetmealDishes(list);
        } else {
            log.info("未查询到有与套餐 id 为 {} 的套餐相关联的菜品。", id);
        }
        return result;
    }

    /**
     * 同时更新 setmeal 和 setmeal_dish（套餐下有哪些菜品） 两个表
     *
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        // 1. 更新 setmeal 表
        this.updateById(setmealDto);
        // 2. 更新 setmeal_dish 表
        // 2.1 先删除原有的数据
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        setmealDishService.remove(queryWrapper);
        // 2.2 再插入更改后的数据
        // 由于前端传入的 setmealDto 中的 setmealDishes（套餐和菜品的关系）中的 setmealId 为空，所以需要手动插入。
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> setmealDishesUpdated = setmealDishes.stream().map(item -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishesUpdated);

    }
}
