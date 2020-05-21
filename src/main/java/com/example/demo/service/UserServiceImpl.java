package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.service.docker.DockerService;
import com.example.demo.service.node.NodeService;
import com.example.demo.util.MD5Util;
import com.github.dockerjava.api.model.Container;
import org.jcp.xml.dsig.internal.dom.DOMKeyInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.model.IModel;

import javax.xml.crypto.Data;
import java.io.ObjectStreamClass;
import java.math.BigInteger;
import java.sql.Connection;
import java.util.*;



@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ExInfoRepository exInfoRepository;
    @Autowired
    private DockerService dockerService;
    @Autowired
    private NodeInfoReposotory nodeInfoReposotory;
    @Autowired
    private NodeService nodeService;

//    @Value("${docker.ip}")
//    private String hostIp;


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


    //显示所有用户

    public List<UserInfo> listAllUserInfo(Map<String, Object> map, Model model){
        System.out.print("这是显示所有用户");
//        User user = this.getLoginUser();
//        List<Role> roles = user.getRoles();
//        System.out.print(roles.get(0));
        List<UserInfo> userInfos = userInfoRepository.findAll();
        map.put("userInfos",userInfos);
        model.addAttribute("queue",userInfos);
        return userInfos;


    }


    //获取当前登录的用户的账号信息
    public User getLoginUser(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails==null){
            System.out.print("请先登录");
            throw new UsernameNotFoundException("请先登录");
        }
        String userName = userDetails.getUsername();
        User user = userRepository.findByName(userName);
        if (user==null){
            System.out.print("该用户不存在");
            throw new UsernameNotFoundException("请先注册");
        }
        return user;
    }


    //获取当前登录用户的个人信息

    public UserInfo getMyInfo(){
        Long id = this.getLoginUser().getId();
        return userInfoRepository.findUserInfoById(id);
    }

    //个人信息设置
    public void  personInfoSet(Long id ,String name ,int age ,String email,String telNum,String sex){
        //获取当前登录的用户
        System.out.print("这里是个人信息设置");
        System.out.print(name);
//        System.out.print(userInfo);
        User userLogin = this.getLoginUser();
        String   role = userLogin.getRoles().get(0).getRolename().toString();
        System.out.println(role);

        String userName = userLogin.getUsername();
        UserInfo userInfo = new UserInfo(id,name,userName,age,email,telNum,role,sex);
        System.out.print(userInfo);
        userInfoRepository.save(userInfo);

    }



/*
    //开始实验
    *//*首先判断登录的用户是否已经分配端口
    有端口
          是否有容器
                有容器
                    容器是否在run
                        是 返回实验
                        否 启动并返回实验
                无容器
                    创建容器并启动

    无
        是否有容器
                有容器
                    删除并创建
                 无容器
                    创建

    *
    * */

    public String  startEx(){


        System.out.print("这里是开始实验");
        //获取用户名

        User user = this.getLoginUser();
        String userName = user.getUsername();
        Long id = user.getId();

        //查询可用集群
        List<NodeInfo> nodeInfos= nodeInfoReposotory.findAll();
        //根据资源情况调整
//        for(NodeInfo nodeInfo:nodeInfos){
//
////            float  memUsage = nodeService.getSysMemInfo();
//        }
        String hostip =nodeInfos.get(0).getIp();


        //查询用户端口
        ExInfo exInfo = exInfoRepository.findExInfoById(id);

        String status = dockerService.isExitContainerName(userName);


        //若有 则测试端口是否可用
        if (exInfo!=null){

            int port = exInfo.getPort();
            String nameFromPort = exInfo.getUserName();
            String ip = exInfo.getNodeIp();

            //查询用户容器是否存在
            System.out.println("这里是容器状态：");
            if(status.equals("Run")){
                System.out.println("这里是容器run");
//                dockerService.startContainerByName(userName);
                return ip+':'+port;
            }
            else if (status.equals("Stop")){
                System.out.println("这里是容器停止");
                dockerService.startContainerByName(userName);
                for (int num = 0 ;num < 5;num ++){
                    boolean portStatus  = dockerService.isHostConnectable(ip,port);
                    if (!portStatus){
                        break;
                    }
                }

                return ip+':'+port;
            }
            //若不存在则创建容器并启动
            else  {
                // 被占用则重新加一
                while (dockerService.isHostConnectable(hostip,port)){
                    //查询端口的最大值
                    int maxPort = exInfoRepository.max().toBigInteger().intValue();
                    //使用端口最大值加一
                    port = maxPort;
                    port = port+1;
                }
                ExInfo newExInfo = new ExInfo(id,port,userName,hostip);
                exInfoRepository.save(newExInfo);
                dockerService.createDocker(userName,port);


                for (int num = 0 ;num < 5;num ++){
                    boolean portStatus  = dockerService.isHostConnectable(hostip,port);
                    if (!portStatus){
                        break;
                    }
                }


                return hostip+":"+port;
            }


        }

        else {

            //查询用户容器是否存在
            if(status.equals("Run")){
                dockerService.stopByContainerName(userName);
                dockerService.deleteByContainerName(userName);
            }
            if (status.equals("Stop")){
                dockerService.deleteByContainerName(userName);
            }
            int maxPort = 10000;
            if (exInfoRepository.max()!=null){
                maxPort=exInfoRepository.max().toBigInteger().intValue();
            }
            int port = maxPort+1;
            while (dockerService.isHostConnectable(hostip,port)){
                //查询端口的最大值
                //使用端口最大值加一
                port = port+1;
            }

            ExInfo exInfo1 = new ExInfo(id,port,userName,hostip);
            exInfoRepository.save(exInfo1);
            dockerService.createDocker(userName,port);




            for (int num = 0 ;num < 5;num ++){
                boolean portStatus  = dockerService.isHostConnectable(hostip,port);
                if (!portStatus){
                    break;
                }
            }

            return hostip+":"+port;
        }


    }
//停止容器
    public String  stopEx(){
        User user = this.getLoginUser();
        String userName = user.getUsername();
//        Long id = user.getId();
//        ExInfo exInfo = exInfoRepository.findExInfoById(id);


//        查询容器状态
        String status = dockerService.isExitContainerName(userName);
        if (status.equals("Run")){
            dockerService.stopByContainerName(userName);
            return "success";
        }
        else if (status.equals("Stop")){
            return "success";
        }
        else {
            return "failure";
        }



    }

    //用户删除自己的容器
    public String  deleteEx(Map<String, Object> map){
        User user = this.getLoginUser();
        String userName = user.getUsername();
        //        查询容器状态
        String status = dockerService.isExitContainerName(userName);
        if (status.equals("Run")){
            dockerService.stopByContainerName(userName);
            dockerService.deleteByContainerName(userName);
            return "success";
//            map.put("stop","停止成功");
        }
        else if (status.equals("Stop")){
            dockerService.deleteByContainerName(userName);
            System.out.print("提示停止成功");
            return "success";
//            map.put("stop","停止成功");
        }
        else {
            return "failure";
//            System.out.println("容器不存在");
        }

    }

    //获取所有容器信息
    public List<DockerInfo> getAllDocker(){
//        Map<String,Object> map = new HashMap<String,Object>();
        List<DockerInfo> dockerInfos=new ArrayList<>();
//        DockerInfo dockerInfo = new DockerInfo();
        List<ExInfo> exInfos = exInfoRepository.findAll();
        if (exInfos.size()>0) {
            for (ExInfo exInfo : exInfos) {
                DockerInfo dockerInfo = new DockerInfo(exInfo.getId(), exInfo.getUserName(), exInfo.getPort(), "None");
                dockerInfos.add(dockerInfo);
//                map.put(exInfo.getUserName(), "None");
            }
            List<Container> containers = dockerService.getALlRunContainer();
            for (Container container : containers) {
                String containerName = container.getNames()[0].replace("/", "");
                for (DockerInfo dockerInfo1 : dockerInfos) {
                    if (dockerInfo1.getDockerName().equals(containerName)) {
                        dockerInfo1.setStatus("run");
                    }
                }

//                if (map.containsKey(containerName)) {
//
//                    map.put(containerName, "run");
//
//                }
//            System.out.println(container.getPorts()[0].getPublicPort().toString());  //获取容器端口

            }
            List<Container> containers1 = dockerService.getALlStopContainer();
            for (Container container1 : containers1) {
//            System.out.println(container1.getPorts()[0].getPrivatePort().toString());
                String containerName1 = container1.getNames()[0].replace("/", "");
                for (DockerInfo dockerInfo2 : dockerInfos) {
                    if (dockerInfo2.getDockerName().equals(containerName1)) {
                        dockerInfo2.setStatus("stop");
                    }
//                if (map.containsKey(containerName1)) {
//                    map.put(containerName1, "stop");
//                }
                }
            }
        }
        System.out.println(dockerInfos);


        return dockerInfos;
    }


}
