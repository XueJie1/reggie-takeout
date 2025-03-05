package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Override
    public R<String> addEmployee(Employee employee, Long id) {
        // 设置初始密码，经过md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

            boolean save = super.save(employee);
            if (save) {
                return R.success("新增员工成功");
            } else {
                return R.error("新增员工失败");
            }

    }

    @Override
    public void update(Employee employee, Long updateUserId) {
        super.updateById(employee);
    }

}
