package com.example.demo.repository;

import com.example.demo.entity.ExInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ExInfoRepository extends JpaRepository<ExInfo,Long> {
    ExInfo findExInfoById(Long id);

    @Query(value = "SELECT max(port) FROM ExInfo ")
    public BigDecimal max();
}
