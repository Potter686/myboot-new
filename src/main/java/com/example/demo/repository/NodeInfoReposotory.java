package com.example.demo.repository;

import com.example.demo.entity.NodeInfo;
import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeInfoReposotory extends JpaRepository<NodeInfo,Long> {
    NodeInfo findNodeInfoById(Long id);

}
