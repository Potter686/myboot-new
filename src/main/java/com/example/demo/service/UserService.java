package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * Created by linziyu on 2018/5/13.
 * 用户类Service
 *
 */
public interface UserService {
    String save(String  name , String password , String role, Model model);//保存用户
    Page<User> PageByUser(Integer page, Integer size);//对用户数据进行分页
    String resetPass(String password, Map<String, Object> map);   //重置密码

}
