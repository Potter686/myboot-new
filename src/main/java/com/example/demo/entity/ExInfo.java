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

    public ExInfo(Long id ,int port ,String userName){
        this.id=id;
        this.port =port;
        this.userName=userName;
    }
    public ExInfo(){

    }


}