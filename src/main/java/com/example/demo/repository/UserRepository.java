package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

/**
 * Created by linziyu on 2018/5/13.
 *用户操作类，进行基本的CRUD操作
 *
 *
 */
public interface UserRepository extends JpaRepository<User,Long>{
    User findByName(String name);

//    @Query("update user1 u where u.name=:name")
//    User findUser(@Param("name") String name);

}
