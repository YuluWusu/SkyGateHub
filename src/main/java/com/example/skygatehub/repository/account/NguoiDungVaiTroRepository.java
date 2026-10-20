package com.example.skygatehub.repository.account;

import com.example.skygatehub.entity.account.NguoiDungVaiTro;
import com.example.skygatehub.entity.account.NguoiDungVaiTroId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguoiDungVaiTroRepository extends JpaRepository<NguoiDungVaiTro, NguoiDungVaiTroId> {

    List<NguoiDungVaiTro> findByMaNguoiDung(String maNguoiDung);

    List<NguoiDungVaiTro> findByMaVaiTro(String maVaiTro);

    void deleteByMaNguoiDung(String maNguoiDung);
}

