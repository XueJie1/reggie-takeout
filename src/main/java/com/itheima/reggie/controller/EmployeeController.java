package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
//import org.springframework.util.StringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        // 1. md5加密传过来的密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes()); // 加密

        // 2. 根据传过来的用户名查询数据库
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        if (emp == null){
            return R.error("登录失败");
        }
        // 4. 密码比对
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败：密码错误");
        }
        // 5. 查看员工状态（正常/已封禁）
        if (emp.getStatus() == 0){
            return R.error("登录失败：账号已封禁");
        }

        // 6. 登录成功
        httpServletRequest.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }
    // 讲师
//    /**
//     * 保存
//     * @param employee
//     * @return
//     */
//    @PostMapping
//    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
//        log.info("新增员工，员工信息：{}", employee.toString());
//
//        // 获得当前用户的 id
//        Long id = (Long) request.getSession().getAttribute("employee");
//        employeeService.save( employee, id);
//        return R.success("新增员工成功");
//    }
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("新增员工，员工信息{}", employee);
        Long id = (Long) request.getSession().getAttribute("employee");
        R<String> result = employeeService.addEmployee(employee, id);
        return result;
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        log.info("page = {}, pagesize = {}, name = {}", page, pageSize, name);
        // 1. 构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        // 2. 构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 3. 进行分页查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
        Long id = (Long) request.getSession().getAttribute("employee");
        if (id != 1) {
            return R.error("没有权限");
        }
        employeeService.update(employee, id);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
