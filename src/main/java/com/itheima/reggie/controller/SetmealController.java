package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     *
     * @param page 第几页
     * @param pageSize 每页有几个
     * @param name 查询名
     * @return
     */
    @GetMapping("page")
    public R<Page> page(int page, int pageSize, String name) {
//        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
//        Page<SetmealDto> dtoPageInfo = new Page<>(page, pageSize);
//
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.like(name != null, Setmeal::getName, name);
//        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//        setmealService.page(pageInfo, queryWrapper);
//        // 拷贝到 dtoPageInfo, 由于 records 是 List<T> 类型，其泛型不一样，需要特殊处理
//        BeanUtils.copyProperties(pageInfo, dtoPageInfo, "records");
//        List<Setmeal> records = pageInfo.getRecords();
//        List<SetmealDto> list = records.stream().map((item) -> {
//            SetmealDto dishDto = new SetmealDto();
//            BeanUtils.copyProperties(item, dishDto);
//            Long categoryId = item.getCategoryId(); // 分类 ID
//            Category category = categoryService.getById(categoryId);
//            if (category != null) {
//                String categoryName = category.getName();
//                dishDto.setCategoryName(categoryName);
//            }
//            return dishDto;
//        }).collect(Collectors.toList());
//        dtoPageInfo.setRecords(list);
        Page<SetmealDto> page1 = setmealService.page(page, pageSize, name);
        return R.success(page1);
    }

    /**
     * 更改套餐信息时，处理信息的回显
     * @param id 套餐 id
     * @return SetmealDto
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto result = setmealService.getByIdWithDish(id);
        return R.success(result);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("数据修改成功");
    }
}
