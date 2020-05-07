package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;


public interface UserService {
    String save(String  name , String password , String role, Model model);//保存用户
    Page<User> PageByUser(Integer page, Integer size);//对用户数据进行分页
    String resetPass(String password, Map<String, Object> map);   //重置密码
    void listAllUser(Model model); //显示所有用户
    User getLoginUser(); //获取当前登录的用户
    void personCenterSet(String name ,int age ,String email,String telNum); //修改以及添加个人信息

     String startEx();  //开始实验

     String  stopEx() ; // 停止实验
     String  deleteEx(Map<String, Object> map); //删除容器
}
