package com.example.demo.service.docker;


import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.util.MD5Util;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.sun.net.httpserver.Authenticator;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DockerServiceImpl implements DockerService {

    @Override
    public DockerClient getClientCon(){
        return DockerClientBuilder
                .getInstance("tcp://192.168.142.129:2375").build();
    }

    //创建并启动容器
    @Override
    public String createDocker(String userName,int port1) {
        //连接docker
        DockerClient dockerClient = this.getClientCon();
        CreateContainerResponse container1 = dockerClient.createContainerCmd("nodered/node-red-docker:latest")
                .withName(userName) //给容器命名
                .withPortBindings(PortBinding.parse(port1+":1880")) //Apache端口是80，映射到主机的8080端口
//                .withBinds(Bind.parse("/home/user/hangge/htdocs:/usr/local/apache2/htdocs")) //目录挂载
                .exec();
//        return container1;
        //运行容器
        dockerClient.startContainerCmd(container1.getId()).exec();
        return "success";
    }



    //获取所有运行结束的容器
    @Override
    public List<Container> getALlStopContainer(){
        //获取所有运行结束的容器

        DockerClient dockerClient = this.getClientCon();
        return dockerClient.listContainersCmd().withStatusFilter("exited").exec() ;
    }

    //获取所有运行中的容器
    @Override
    public List<Container> getALlRunContainer(){
        //获取所有运行结束的容器
        DockerClient dockerClient = this.getClientCon();
        return dockerClient.listContainersCmd().exec();
    }
    //获取容器姓名是否存在
    public String  isExitContainerName(String name){

        //在运行中的容器中查询
        List<Container> containers = this.getALlRunContainer();
        for (Container container:containers){
            if (name.equals(container.getNames()[0].replace("/",""))){
                return "Run";
            }
        }
        //在已经停止的容器中查询
        List<Container> containers1 = this.getALlStopContainer();

        for(Container container1:containers1){

            if (name.equals(container1.getNames()[0].replace("/",""))){
                return "Stop";
            }
        }
        return "None";

    }

    //删除容器
    public boolean deleteByContainerName(String containerName){
        DockerClient dockerClient = this.getClientCon();
        dockerClient.removeContainerCmd(containerName).exec();
        return true;

    }
    //停止容器
    public boolean stopByContainerName(String containerName){
        DockerClient dockerClient = this.getClientCon();
        dockerClient.stopContainerCmd(containerName).exec();
        return true;
    }

    //查看端口是否可用
    public  boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    public boolean startContainerByName(String name){
        DockerClient dockerClient = this.getClientCon();
        dockerClient.startContainerCmd(name).exec();
        return true;

    }






}
