package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.entity.UserInfo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo,Long> {
    UserInfo findUserInfoById(Long id);
}
