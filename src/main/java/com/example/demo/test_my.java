package com.example.demo;

import com.example.demo.repository.ExInfoRepository;
import com.example.demo.service.docker.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;
@RunWith(SpringRunner.class)
@SpringBootTest
public class test_my {
    @Autowired
    private DockerService dockerService;
    @Autowired
    private ExInfoRepository exInfoRepository;
    @Test
    public void test_adfads(){

        System.out.print("这里是测试结果");
//        System.out.print(dockerService.deleteByContainerName("123"));
//        System.out.print( dockerService.isHostConnectable("172.17.187.102",12312));
//        System.out.print(dockerService.createDocker("12312",12345));
//        System.out.print(dockerService.createDocker("123122",12345));
        System.out.print(exInfoRepository.max());

        System.out.print(exInfoRepository.findExInfoById(3L));

    }
}
