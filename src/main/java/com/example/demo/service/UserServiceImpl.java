package com.example.demo.service;

import com.example.demo.entity.ExInfo;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.entity.UserInfo;
import com.example.demo.repository.ExInfoRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.docker.DockerService;
import com.example.demo.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
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

    static String hostIp = "192.168.142.130";


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
        String  role = userLogin.getRoles().get(0).toString();
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

        //查询用户端口
        ExInfo exInfo = exInfoRepository.findExInfoById(id);

        String status = dockerService.isExitContainerName(userName);


        //若有 则测试端口是否可用
        if (exInfo!=null){

            int port = exInfo.getPort();
            String nameFromPort = exInfo.getUserName();

            //查询用户容器是否存在
            System.out.println("这里是容器状态：");
            if(status.equals("Run")){
                System.out.println("这里是容器run");
//                dockerService.startContainerByName(userName);
                return hostIp+':'+port;
            }
            else if (status.equals("Stop")){
                System.out.println("这里是容器停止");
                dockerService.startContainerByName(userName);
                for (int num = 0 ;num < 5;num ++){
                    boolean portStatus  = dockerService.isHostConnectable(hostIp,port);
                    if (!portStatus){
                        break;
                    }
                }

                return hostIp+':'+port;
            }
            //若不存在则创建容器并启动
            else  {
                // 被占用则重新加一
                while (dockerService.isHostConnectable(hostIp,port)){
                    //查询端口的最大值
                    int maxPort = exInfoRepository.max().toBigInteger().intValue();
                    //使用端口最大值加一
                    port = maxPort;
                    port = port+1;
                }
                ExInfo newExInfo = new ExInfo(id,port,userName);
                exInfoRepository.save(newExInfo);
                dockerService.createDocker(userName,port);


                for (int num = 0 ;num < 5;num ++){
                    boolean portStatus  = dockerService.isHostConnectable(hostIp,port);
                    if (!portStatus){
                        break;
                    }
                }


                return hostIp+":"+port;
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
            int maxPort = exInfoRepository.max().toBigInteger().intValue();
            int port = maxPort+1;
            while (dockerService.isHostConnectable(hostIp,port)){
                //查询端口的最大值
                //使用端口最大值加一
                port = port+1;
            }

            ExInfo exInfo1 = new ExInfo(id,port,userName);
            exInfoRepository.save(exInfo1);
            dockerService.createDocker(userName,port);




            for (int num = 0 ;num < 5;num ++){
                boolean portStatus  = dockerService.isHostConnectable(hostIp,port);
                if (!portStatus){
                    break;
                }
            }

            return hostIp+":"+port;
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


}
