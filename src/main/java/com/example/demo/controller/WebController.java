package com.example.demo.controller;


import ch.qos.logback.core.net.server.Client;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserInfo;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.service.docker.DockerService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private UserInfoRepository userInfoRepository;




    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }


    @RequestMapping("/to_register")
    public String register() {
            return "register";
    }

    @PostMapping("/register")
    public String addUser(String name,String password,String role ,Model model,HttpServletResponse response) throws IOException {
            //验证是否是管理员
            User user = userService.getLoginUser();
            List<Role> roles= user.getRoles();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            if(roles.get(0).getRolename().equals("Role_ADMIN")){
                String status = userService.save(name,password,role,model);

                if(status.equals("success")){
                    out.print("<script language=\"javascript\">alert('注册成功');window.location.href='index'</script>");
                    return "index";
                }
                else {
                    out.print("<script language=\"javascript\">alert('注册失败用户已存在');window.location.href='index'</script>");
                    return "index";
                }
            }
            else {
                out.print("<script language=\"javascript\">alert('没有权限');window.location.href='index'</script>");
                return "index";
            }
    }
//    @RequestMapping("/toResetPass")
//    public String toResetPass(){
//        return "index";
//    }
    @RequestMapping("/resetPass")
    public String resetPass(String name, String password , Map<String, Object> map){
        String status = userService.resetPass(password,map);
        return "login";

    }
//    @RequestMapping("/toManagerUser")
//    public String managerUser(Map<String, Object> map,Model model){
//        userService.listAllUser(map,model);
//        return "toManagerUser";
//    }


    @RequestMapping("/toPersonCenterSet")
    @ResponseBody
    public String managerUser(Map<String, Object> map,Model model) throws JSONException {
        List<UserInfo> userInfos = userService.listAllUserInfo(map,model);
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",userInfos);
        return jsonpObject.toJSONString();




    }

    @RequestMapping("/PersonCenterSet")
    public String toPersonCenterSet  (){return "toPersonCenterSet"; }






    @RequestMapping("/personInfoSet")
    @ResponseBody
    public String personInfoSet(HttpServletRequest request,@RequestParam String name ,int age ,String email,String telNum,String sex) {
        User user = userService.getLoginUser();
        Long id = user.getId();
        userService.personInfoSet(id,name,age,email,telNum,sex);
        return "-1";
    }


    @GetMapping("/startEx")
    public String experiment(ModelMap map){
        //创建容器时，先判断容器是否存在，再测试端口号是否存在，再创建
        System.out.print("这里是controll 开始实验");
        String url = userService.startEx();
        System.out.print(url);
        return"redirect:http://"+url;

    }

    @GetMapping("/stopEx")
    public String stopEx( HttpServletResponse response)throws IOException{
        String status = userService.stopEx();
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        if(status.equals("success")){
            out.print("<script language=\"javascript\">alert('停止成功');window.location.href='index'</script>");
            return "index";
        }
        else {
            out.print("<script language=\"javascript\">alert('停止失败，容器不存');window.location.href='index'</script>");
            return "index";
        }
    }
    @GetMapping("/deleteUser")
    @ResponseBody
    public int   deleteUser(HttpServletRequest request,@RequestParam Long id){
        System.out.println(id);
        userInfoRepository.deleteById(id);
        System.out.println("这里是删除用户");
        return -1;

    }



//    @RequestMapping("/questionnaire/addOption")
//    public String addOption(@ModelAttribute Option option,String optionName,HttpServletRequest request,Integer topicId,Model model)throws Exception {
//        System.out.println("yes22"+topicId);
//        option.setOptionName(request.getParameter("optionName");
//        option.setTopicId(topicId);
//        System.out.println(optionName+"yes33");
//        optionService.insert(option);
//        System.out.println("添加成功！");
//       /* PageHelper.startPage(1,15);
//
//        PageInfo<Option> pageInfo=new PageInfo<Option>(list,15);
//          model.addAttribute("pageInfo", pageInfo);*/这个是分页，
//        return "ok“;//返会一个字符串，如果像跳转到指定页面，也可以。
//    }




    @GetMapping("/deleteEx")
    public String deleteEx(Map<String, Object> map , HttpServletResponse response) throws IOException {
        String status = userService.deleteEx(map);

        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        if(status.equals("success")){
            out.print("<script language=\"javascript\">alert('删除成功');window.location.href='index'</script>");
            return "index";
        }
        else {
            out.print("<script language=\"javascript\">alert('删除失败，容器不存');window.location.href='index'</script>");
            return "index";
        }
    }
    @GetMapping("/test")
    public String test(){

        return "test";
    }











}
