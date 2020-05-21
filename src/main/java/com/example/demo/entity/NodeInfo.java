package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Table(name = "nodeInfo")
@Entity
@Data
public class NodeInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ip ;
    private String userName;
    private String password;
    private int sshPort;

    public NodeInfo(Long id ,String ip ,String userName,String password,int sshPort){
        this.id=id;
        this.ip =ip;
        this.userName=userName;
        this.password=password;
        this.sshPort=sshPort;
    }
    public NodeInfo(){

    }
    public NodeInfo(String ip ,String userName,String password,int sshPort){

        this.ip =ip;
        this.userName=userName;
        this.password=password;
        this.sshPort=sshPort;
    }

}
