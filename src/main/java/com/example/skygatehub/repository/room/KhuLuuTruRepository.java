package com.example.skygatehub.repository.room;

import com.example.skygatehub.entity.room.KhuLuuTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhuLuuTruRepository extends JpaRepository<KhuLuuTru, Integer> {
    Optional<KhuLuuTru> findByTenKhu(String tenKhu);
}

