package com.example.demo.entity;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Entity
@Data
@NoArgsConstructor

@Table(name = "info1") //设置对应表名字
@ApiModel(description="userInfo")
public class UserInfo {
    @Id
    @ApiModelProperty("id")
    private Long id;  //用户信息id

    @ApiModelProperty("name")
    @NotEmpty
    @Size(min=2,max = 6)
    private String name;  //用户姓名

    @Max(100)
    @Min(0)
    private int age;
    @Email
    @NotEmpty
    private String email;
//    @Size(min=11,max = 11)
    @NotEmpty
    private String  telNum;
    public UserInfo(Long id ,String name,int age,String email,String telNum){
        this.id=id;
        this.name=name;
        this.age=age;
        this.email=email;
        this.telNum=telNum;
    }


}
