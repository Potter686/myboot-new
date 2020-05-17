package com.example.demo;

import com.example.demo.repository.ExInfoRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.beans.factory.annotation.Value;


import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import java.io.*;


@RunWith(SpringRunner.class)
@SpringBootTest


public class test_my {
//    @Autowired
//    private DockerService dockerService;
//    @Autowired
//    private ExInfoRepository exInfoRepository;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;




    @Test
    public void test_adfads(){

        System.out.print("这里是测试结果");
//        System.out.print(dockerService.deleteByContainerName("123"));
//        System.out.print( dockerService.isHostConnectable("172.17.187.102",12312));
//        System.out.print(dockerService.createDocker("12312",12345));
//        System.out.print(dockerService.createDocker("123122",12345));
//        System.out.println(ip);
        stringRedisTemplate.opsForValue().set("bbb", "123");
        Connection connection=nodeService.sshConnect();
//        nodeService.getSysMemInfo(connection);
//        Assert.assertEquals("111", stringRedisTemplate.opsForValue().get("aaa"));
//        System.out.println(nodeService.sshConnect());



//
//        System.out.print(exInfoRepository.findExInfoById(3L));

    }
}
