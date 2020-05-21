package com.example.demo.controller;


import ch.qos.logback.core.net.server.Client;
import com.alibaba.fastjson.JSON;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.UserService;
import com.example.demo.service.docker.DockerService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.node.NodeService;
import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONException;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
import java.text.SimpleDateFormat;
import java.util.*;


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

    @Autowired
    private NodeService nodeService;
    @Autowired
    private ExInfoRepository exInfoRepository;

    @Autowired
    private NodeInfoReposotory nodeInfoReposotory;


    @RequestMapping("/index")
    public String index() {
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


    @RequestMapping("/getMyInfo")
    @ResponseBody
    public String getMyInfo(Map<String, Object> map,Model model) throws JSONException {
        UserInfo userInfos = userService.getMyInfo();
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",userInfos);
        return jsonpObject.toJSONString();
    }





//,String email,String telNum,String sex
    @RequestMapping("/personInfoSet")
    public String personInfoSet(HttpServletResponse response, String name ,int age,String email,String telNum,String sex )throws IOException {
        System.out.println("这里是个人信息设置");
        System.out.println(name);
        User user = userService.getLoginUser();
        Long id = user.getId();
        userService.personInfoSet(id,name,age,email,telNum,sex);
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print("<script language=\"javascript\">alert('设置成功');window.location.href='index'</script>");

//        return "-1";
        return "index";
    }



    @RequestMapping("/addNode")
    public String addNode(HttpServletResponse response, String host ,String sshUser,String sshPass,int sshPort )throws IOException {
        System.out.println("这里是添加容器");
        List<NodeInfo> nodeInfos=nodeInfoReposotory.findAll();
        for (NodeInfo nodeInfo1:nodeInfos){
            if (nodeInfo1.getIp().equals(host)){
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.print("<script language=\"javascript\">alert('节点已存在');window.location.href='index'</script>");
                return "index";
            }
        }

        boolean status = nodeService.testConnect(host,sshUser,sshPass,sshPort);
        if (status){
            nodeInfoReposotory.save(new NodeInfo( host,sshUser,sshPass,sshPort));
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("<script language=\"javascript\">alert('添加成功');window.location.href='index'</script>");


        }
        else {
            response.setContentType("text/html;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("<script language=\"javascript\">alert('请检查节点ip以及host');window.location.href='index'</script>");
        }

//        return "-1";
        return "index";
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

    @GetMapping("/deleteDocker")
    @ResponseBody
    public int   deleteDocker(HttpServletRequest request,@RequestParam Long  id){
//        System.out.println(id);
//        userInfoRepository.deleteById(id);
        System.out.println(id);
        dockerService.deleteByid(id);
        exInfoRepository.deleteById(id);
        System.out.println("这里是删除容器");
        return -1;

    }

    @GetMapping("/cgDockerStatus")
    @ResponseBody
    public int   cgDockerStatus(HttpServletRequest request,@RequestParam Long  id ,String status){
//        System.out.println(id);
//        userInfoRepository.deleteById(id);
        System.out.println("这里是容器状态");
        System.out.println(id);
        System.out.println(status);
        String containerName = exInfoRepository.findExInfoById(id).getUserName();
        String reallStatus = dockerService.isExitContainerName(containerName);
        if ((reallStatus.toLowerCase().equals("run")&&status.toLowerCase().equals("false"))|| (reallStatus.toLowerCase().equals("stop")&&status.toLowerCase().equals("true")) ){
            switch (reallStatus.toLowerCase()){
                case "run":
                    dockerService.stopByContainerName(containerName);
                    break;
                case "stop":
                    dockerService.startContainerByName(containerName);
                    break;
                default:
                    break;
            }

        }

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

//    @RequestMapping("/toTest")
//    public String toTest(){
//        return "test";
//    }



    @RequestMapping("/toNodeInfo")
    public String toNodeInfo() {
        System.out.println("这里是获取集群信息");
        return "nodeInfo";
    }

    @RequestMapping("/getNodeInfo")
    @ResponseBody
    public String getNodeInfo()throws Exception{
        System.out.println("这里是获取redis 信息");
        Map<String, Object> nodeInfos = nodeService.getFromRedis();
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",nodeInfos);
//        System.out.println(jsonpObject.toJSONString());
        return jsonpObject.toJSONString();
    }


    @RequestMapping("/getNodeIp")
    @ResponseBody
    public String getNodeIp() {
        System.out.println("这里是获取节点ip");
        List<NodeInfo> nodeInfos = nodeInfoReposotory.findAll();
        List<String> hosts =new ArrayList<>();
        for(NodeInfo nodeInfo:nodeInfos){
            hosts.add(nodeInfo.getIp());
        }
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",hosts);
//        System.out.println(jsonpObject.toJSONString());
        return jsonpObject.toJSONString();
    }




    //管理容器
    @RequestMapping("/toManagerDocker")
    public String toManagerDocker(){
        return "dockerInfo";
    }

    @RequestMapping("/managerDocker")
    @ResponseBody
    public String managerDocker(){
        List<DockerInfo> dockerInfos =userService.getAllDocker();
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",dockerInfos);
        return jsonpObject.toJSONString();
    }




    //管理容器
    @RequestMapping("/toManagerNodes")
    public String toManagerNodes(){
        return "managerNodes";
    }

    @RequestMapping("/managerNodes")
    @ResponseBody
    public String managerNodes(){
        List<NodeInfo> nodeInfos =nodeInfoReposotory.findAll();
        JSONObject jsonpObject = new JSONObject();
        jsonpObject.put("code",0);
        jsonpObject.put("msg","");
        jsonpObject.put("count",1000);
        jsonpObject.put("data",nodeInfos);
        return jsonpObject.toJSONString();
    }



    @RequestMapping("/nodeSet")
    @ResponseBody
    public String nodeSet(HttpServletResponse response, Long id ,String  ip,String userName,String password,int sshPort )throws IOException{
        System.out.println("这里是节点信息设置");
//        System.out.println(name);
        nodeInfoReposotory.save(new NodeInfo(id,ip,userName,password,sshPort));
//        nodeService.Connect() ;
        return "-1";

    }
    @RequestMapping("/deleteNode")
    @ResponseBody
    public String deleteNode(HttpServletResponse response, Long id  )throws IOException{
        System.out.println("这里是删除节点");
//        System.out.println(name);
        nodeInfoReposotory.deleteById(id);
//        nodeService.Connect() ;
        return "-1";

    }





}
