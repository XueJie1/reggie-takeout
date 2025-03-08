package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增菜品
     * @param dishDto
     * @return 信息
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("菜品：{}", dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * 由于 dish 表中只有分类id（categoryid），所以需要再从 category 表中查询分类名称并整合到返回值中。
     * @param page 页码数，即第几页
     * @param pageSize 一页有几项
     * @param name 查询名字，可为空
     * @return Page类型的分页数据
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 1. 分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageDto = new Page<>();
        // 2. 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 3. 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name) && name != null, Dish::getName, name);
        // 4. 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 5. 进行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =  records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); // 分类 ID
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        pageDto.setRecords(list);
        return R.success(pageDto);
    }
}