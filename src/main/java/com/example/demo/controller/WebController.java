package com.example.demo.controller;


import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserInfo;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.docker.DockerService;
import com.example.demo.util.MD5Util;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import io.swagger.annotations.ApiOperation;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.repository.init.ResourceReader.Type.JSON;


@Controller
public class WebController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DockerService dockerService;


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
            //验证是否是管理员
            User user = userService.getLoginUser();
            List<Role> roles= user.getRoles();
            if(roles.get(0).getRolename().equals("Role_ADMIN")){
                String status = userService.save(name,password,role,model);
                if (status.equals("success")){
                    return "login";
                }
                else {
                    return "register_false";
                }


            }
            else {
                return "index";
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
    @RequestMapping("/toManagerUser")
    public String managerUser(Model model){
        userService.listAllUser(model);
        return "toManagerUser";
    }

//    @RequestMapping("/toPersonCenterSet")
//    public String toPersonCenterSet  (){return "toPersonCenterSet"; }

    @RequestMapping(value = "/personCenterSet",method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "更新个人信息", notes = "更新个人信息")
    public String personCenterSet( String name ,int age ,String email,String telNum) {
        userService.personCenterSet(name,age,email,telNum);
        return "index";
    }
    @RequestMapping("/toPersonCenterSet")
    public String test() {
        dockerService.getALlStopContainer();
        // 连接docker服务器
//        DockerClient dockerClient = DockerClientBuilder
//                .getInstance("tcp://172.17.187.102:2375").build();
//        // 获取服务器信息
//        Info info = dockerClient.infoCmd().exec();
//        System.out.println(info);
        return "index";
    }

    @GetMapping("/startEx")
    public String experiment(ModelMap map){
        //创建容器时，先判断容器是否存在，再测试端口号是否存在，再创建
        System.out.print("这里是controll 开始实验");
        String url = userService.startEx();
        System.out.print(url);
//        map.addAttribute("host", "http://"+url);
//        map.addAttribute("host", "http://blog.didispace.com");
        return"redirect:http://"+url;

    }








}
