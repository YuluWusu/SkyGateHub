package com.example.skygatehub.account.repository;

import com.example.skygatehub.account.entity.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    Optional<VaiTro> findByMaVaiTro(String maVaiTro);

    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);
}
