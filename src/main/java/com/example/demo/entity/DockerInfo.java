package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Entity;

@Data
public class DockerInfo {
    private Long id;
    private int port;
    private String dockerName;
    private String status;
    public DockerInfo(Long id ,String dockerName,int port,String status){
        this.id=id;
        this.dockerName=dockerName;
        this.status=status;
        this.port=port;

    }
}
