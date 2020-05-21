package com.example.demo.service.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;

import java.util.List;

public interface DockerService {
    //获取docker连接
     DockerClient getClientCon();
     //创建并运行container
     String createDocker(String userName,int port1);
     //获取所有运行停止的容器
     List<Container> getALlStopContainer();
     //获取所有运行中的容器
     List<Container> getALlRunContainer();
     //查看容器是否存在以及状态
     String isExitContainerName(String name);
     //删除容器
     boolean deleteByContainerName(String containerName);
     //停止容器
     boolean stopByContainerName(String containerName);
     // 启动容器
     boolean startContainerByName(String name );

     //查看端口是否可用
     boolean isHostConnectable(String host, int port);
     boolean deleteByid(Long id);

}
