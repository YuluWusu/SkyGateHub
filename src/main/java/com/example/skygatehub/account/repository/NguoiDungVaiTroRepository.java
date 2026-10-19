package com.example.skygatehub.account.repository;

import com.example.skygatehub.account.entity.NguoiDungVaiTro;
import com.example.skygatehub.account.entity.NguoiDungVaiTroId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguoiDungVaiTroRepository extends JpaRepository<NguoiDungVaiTro, NguoiDungVaiTroId> {

    List<NguoiDungVaiTro> findByMaNguoiDung(String maNguoiDung);

    List<NguoiDungVaiTro> findByMaVaiTro(String maVaiTro);

    void deleteByMaNguoiDung(String maNguoiDung);
}
