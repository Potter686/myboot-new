package com.example.demo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "ex_info")
@Entity
@Data
public class ExInfo {
    @Id
    private Long id;
    private int port;
    private String userName;
    private  String nodeIp;

    public ExInfo(Long id ,int port ,String userName,String nodeIp){
        this.id=id;
        this.port =port;
        this.userName=userName;
        this.nodeIp=nodeIp;
    }
    public ExInfo(){

    }


}