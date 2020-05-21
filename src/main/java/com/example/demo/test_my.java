package com.example.demo;

import com.example.demo.entity.NodeInfo;
import com.example.demo.repository.ExInfoRepository;
import com.example.demo.repository.NodeInfoReposotory;
import com.example.demo.service.UserService;
import com.example.demo.service.docker.DockerService;
import com.example.demo.service.node.NodeService;
import io.swagger.annotations.Scope;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.*;
import java.awt.*;
import javax.swing.*;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;



@RunWith(SpringRunner.class)
@SpringBootTest

@EnableScheduling
public class test_my {
//    @Autowired
//    private DockerService dockerService;
//    @Autowired
//    private ExInfoRepository exInfoRepository;

    @Autowired
    private NodeService nodeService;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Autowired
//    private  UserService userService;
//
//    @Autowired
//    private ExInfoRepository exInfoRepository;
//    @Autowired
//    private NodeInfoReposotory nodeInfoReposotory;

//    private List<Session> sessions;
//
//    public void  getHosts(){
//        List<NodeInfo> nodeInfos = nodeInfoReposotory.findAll();
//        JSch jsch = new JSch(); // 创建JSch对象
//
//    }



    @Test

    public void test_adfads()throws Exception{

        System.out.print("这里是测试结果");
        nodeService.Connect();


            nodeService.getInfo();

//        String cmd = "ls";
//        JSch sshSingleton = new JSch();
////从配置文件中加载用户名和密码
//        Properties userProp = new Properties();
//        userProp.load(new FileReader("user.properties"));
//        String userName = userProp.getProperty("username");
//        String password = userProp.getProperty("password");
////从配置文件中加载服务器信息
//        Properties serversProp = new Properties();
//        serversProp.load(new FileReader("application.properties"));
//
//        for (Map.Entry<Object, Object> serverProp : serversProp.entrySet()) {
//            String name = (String) serverProp.getKey();
//            String server = (String) serverProp.getValue();
//
//            System.out.println("Start working on: " + name);
//            Session session = sshSingleton.getSession(userName, server);
//            session.setPassword(password);
//            Properties config = new Properties();
////设置 SSH 连接时不进行公钥确认
//            config.put("StrictHostKeyChecking", "no");
//            session.setConfig(config);
//            session.connect();
////打开命令执行管道
//            ChannelExec channel = (ChannelExec) session.openChannel("exec");
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    channel.getInputStream()));
//            channel.setCommand(cmd);
//            channel.connect();
////读取命令输出信息
//            String msg;
//            while ((msg = in.readLine()) != null) {
//                System.out.println(msg);
//            }
//
//            channel.disconnect();
//            session.disconnect();
//        }






//
//        System.out.print(exInfoRepository.findExInfoById(3L));

    }
}
