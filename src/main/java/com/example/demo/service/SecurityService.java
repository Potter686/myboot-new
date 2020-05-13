package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.jws.WebParam;
import javax.persistence.Id;
import java.util.Collection;
import java.util.List;
import java.util.Map;


//@Service
public class SecurityService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByName(s);


        System.out.print("这是用户登录");
        if (user == null) {
            throw new  UsernameNotFoundException("用户不存在");
        }
////        获取用户的角色
////        List<Role> roles = user.getRoles();
////        Role role = roles.get(0);
////        String user_role_name = role.getRolename();
        return user;
    }
}
