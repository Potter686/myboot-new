package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.model.IModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linziyu on 2018/5/13.
 * 用户Service实现类
 */

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    //用户注册
    @Override
    public String save(String name , String password , String role, Model  model)
    {
        System.out.print("这里是用户注册");

//        验证用户是否存在
        User user1 = userRepository.findByName(name);
        if (user1!=null){
            return "false";
//            throw new UsernameNotFoundException("用户已经存在");
        }

        String encodePassword = MD5Util.encode(password);
        User user = new User(name,encodePassword);
        List<Role> roles = new ArrayList<>();

        System.out.print("分割");
        System.out.print(role);
        System.out.print("分割");

        Role role1 = roleRepository.findByRolename(role);

        System.out.print("分割");
        System.out.print(role1);
        System.out.print("分割");

        roles.add(role1);
        user.setRoles(roles);
        userRepository.save(user);
        List<User> users = new ArrayList<>();
        users.add(user);
        model.addAttribute("user_add",users);
        return "success";

    }

    @Override
    public Page<User> PageByUser(Integer page, Integer size) {
        Pageable pageable = PageRequest.of (page,size, Sort.Direction.ASC, "id");
        return userRepository.findAll(pageable);
    }


    // 用户修改密码
    @Override
    public String resetPass(String password , Map<String, Object> map){



        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.print("这里是用户修改密码");
        String passwordMD = MD5Util.encode(password);
        String login_name = userDetails.getUsername();
        User user = userRepository.findByName(login_name);

        if (user==null){
            map.put("msg", "用户不存在");
            return "用户不存在";
//            throw new UsernameNotFoundException("用户已经存在");
        }
//        Long id = user.getId();
        user.setPassword(passwordMD);
        userRepository.save(user);

        map.put("msg","密码重置成功");
        return "success";
    }

}
