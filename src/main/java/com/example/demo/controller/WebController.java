package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by linziyu on 2018/5/13.
 * 视图分发类
 *
 */

@Controller
public class WebController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;


    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }


    @RequestMapping("/to_register")
    public String register() {
            return "register";
    }

    @PostMapping("/register")
    public String addUser(String name,String password,String role ,Model model) {
            String status = userService.save(name,password,role,model);
            if (status.equals("success")){
                return "login";
            }
            else {
                return "register_false";
            }

    }
    @RequestMapping("/toResetPass")
    public String toResetPass(){
        return "toResetPass";
    }
    @RequestMapping("/resetPass")
    public String resetPass(String name, String password , Map<String, Object> map){
        String status = userService.resetPass(password,map);
        return "login";

    }
    @RequestMapping("/managerUser")
    public String managerUser(){
        return "123";
    }

}
