package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public R<String> remove(Long id) {
        // 检查要删除的是否关联了菜品或套餐，如果已经关联，则抛出一个异常
        // 添加查询条件，根据分类id查询
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int dishCount = dishService.count(dishQueryWrapper);
        // 判断是否关联了菜品，如果已经关联，则抛出一个异常
        if (dishCount > 0) {
            // 已经关联，抛出异常
            throw new CustomException("要删除的分类已关联菜品，无法删除。");
        }

        // 套餐
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setmealCount = setmealService.count(setmealQueryWrapper);
        if (setmealCount > 0) {
            // 已经关联，抛出异常
            throw new CustomException("要删除的分类已关联套餐，无法删除。");
        }

        // 都没有关联，正常删除
        return super.removeById(id) ? R.success("删除成功") : R.error("删除失败");
    }
}
