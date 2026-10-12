package com.example.skygatehub.account.repository;

import com.example.skygatehub.account.entity.VaiTro;
import com.example.skygatehub.common.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Integer> {

    Optional<VaiTro> findByTenVaiTro(RoleName tenVaiTro);
}
