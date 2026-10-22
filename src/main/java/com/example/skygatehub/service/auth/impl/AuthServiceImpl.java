package com.example.skygatehub.service.auth.impl;

import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.PasswordHasher;
import com.example.skygatehub.dto.auth.LoginRequest;
import com.example.skygatehub.dto.auth.RegisterRequest;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.account.NguoiDungVaiTro;
import com.example.skygatehub.entity.account.VaiTro;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.account.NguoiDungVaiTroRepository;
import com.example.skygatehub.repository.account.VaiTroRepository;
import com.example.skygatehub.service.auth.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final PasswordHasher passwordHasher;

    public AuthServiceImpl(NguoiDungRepository nguoiDungRepository,
                           VaiTroRepository vaiTroRepository,
                           NguoiDungVaiTroRepository nguoiDungVaiTroRepository,
                           PasswordHasher passwordHasher) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.nguoiDungVaiTroRepository = nguoiDungVaiTroRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUser dangNhap(LoginRequest yeuCau) {
        if (yeuCau == null || yeuCau.getTenDangNhap() == null || yeuCau.getMatKhau() == null) {
            return null;
        }

        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findByTenDangNhap(yeuCau.getTenDangNhap().trim());
        if (nguoiDungOptional.isEmpty()) {
            return null;
        }

        NguoiDung nguoiDung = nguoiDungOptional.get();
        if (Boolean.FALSE.equals(nguoiDung.getTrangThai())) {
            return null;
        }

        if (!passwordHasher.matches(yeuCau.getMatKhau().trim(), nguoiDung.getMatKhauHash())) {
            return null;
        }

        List<NguoiDungVaiTro> danhSachVaiTroNguoiDung = nguoiDungVaiTroRepository.findByMaNguoiDung(nguoiDung.getMaNguoiDung());
        List<String> danhSachMaVaiTro = new ArrayList<>();
        for (NguoiDungVaiTro ndvt : danhSachVaiTroNguoiDung) {
            danhSachMaVaiTro.add(ndvt.getMaVaiTro());
            Optional<VaiTro> vaiTroOptional = vaiTroRepository.findByMaVaiTro(ndvt.getMaVaiTro());
            if (vaiTroOptional.isPresent()) {
                danhSachMaVaiTro.add(vaiTroOptional.get().getTenVaiTro());
            }
        }

        return new CurrentUser(
                nguoiDung.getId(),
                nguoiDung.getMaNguoiDung(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getCanDoiMatKhau(),
                danhSachMaVaiTro
        );
    }

    @Override
    public NguoiDung dangKy(RegisterRequest yeuCau) {
        if (yeuCau == null || yeuCau.getTenDangNhap() == null || yeuCau.getMatKhau() == null) {
            throw new IllegalArgumentException("Thông tin đăng ký không hợp lệ");
        }

        String tenDangNhap = yeuCau.getTenDangNhap().trim();
        if (nguoiDungRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + tenDangNhap);
        }

        String maNguoiDung = "ND" + System.currentTimeMillis() % 1000000;
        while (nguoiDungRepository.findByMaNguoiDung(maNguoiDung).isPresent()) {
            maNguoiDung = "ND" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        NguoiDung nguoiDungMoi = new NguoiDung();
        nguoiDungMoi.setMaNguoiDung(maNguoiDung);
        nguoiDungMoi.setTenDangNhap(tenDangNhap);
        nguoiDungMoi.setMatKhauHash(passwordHasher.encode(yeuCau.getMatKhau().trim()));
        nguoiDungMoi.setHoTen(yeuCau.getHoTen() != null ? yeuCau.getHoTen().trim() : tenDangNhap);
        nguoiDungMoi.setEmail(yeuCau.getEmail() != null ? yeuCau.getEmail().trim() : null);
        nguoiDungMoi.setSoDienThoai(yeuCau.getSoDienThoai() != null ? yeuCau.getSoDienThoai().trim() : null);
        nguoiDungMoi.setTrangThai(true);
        nguoiDungMoi.setCanDoiMatKhau(false);
        nguoiDungMoi.setNgayTao(LocalDateTime.now());
        nguoiDungMoi.setNgayCapNhat(LocalDateTime.now());

        NguoiDung nguoiDungDaLuu = nguoiDungRepository.save(nguoiDungMoi);

        String maVaiTro = yeuCau.getMaVaiTro();
        if (maVaiTro != null && !maVaiTro.trim().isEmpty()) {
            Optional<VaiTro> vaiTroOptional = vaiTroRepository.findByMaVaiTro(maVaiTro.trim());
            if (vaiTroOptional.isEmpty()) {
                vaiTroOptional = vaiTroRepository.findByTenVaiTro(maVaiTro.trim());
            }
            if (vaiTroOptional.isPresent()) {
                NguoiDungVaiTro nguoiDungVaiTro = new NguoiDungVaiTro();
                nguoiDungVaiTro.setMaNguoiDung(nguoiDungDaLuu.getMaNguoiDung());
                nguoiDungVaiTro.setMaVaiTro(vaiTroOptional.get().getMaVaiTro());
                nguoiDungVaiTro.setNgayGan(LocalDateTime.now());
                nguoiDungVaiTroRepository.save(nguoiDungVaiTro);
            }
        }

        return nguoiDungDaLuu;
    }

    @Override
    public boolean doiMatKhau(String maNguoiDung, String matKhauCu, String matKhauMoi) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        if (nguoiDungOptional.isEmpty()) {
            return false;
        }

        NguoiDung nguoiDung = nguoiDungOptional.get();
        if (!passwordHasher.matches(matKhauCu, nguoiDung.getMatKhauHash())) {
            return false;
        }

        nguoiDung.setMatKhauHash(passwordHasher.encode(matKhauMoi));
        nguoiDung.setCanDoiMatKhau(false);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
        return true;
    }

    @Override
    public boolean capNhatThongTinVaDoiMatKhau(String maNguoiDung, String hoTen, String email, String soDienThoai, String matKhauCu, String matKhauMoi) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        if (nguoiDungOptional.isEmpty()) {
            return false;
        }

        NguoiDung nguoiDung = nguoiDungOptional.get();
        if (!passwordHasher.matches(matKhauCu, nguoiDung.getMatKhauHash())) {
            return false;
        }

        if (hoTen != null && !hoTen.trim().isEmpty()) {
            nguoiDung.setHoTen(hoTen.trim());
        }
        if (email != null && !email.trim().isEmpty()) {
            nguoiDung.setEmail(email.trim());
        }
        if (soDienThoai != null && !soDienThoai.trim().isEmpty()) {
            nguoiDung.setSoDienThoai(soDienThoai.trim());
        }

        nguoiDung.setMatKhauHash(passwordHasher.encode(matKhauMoi));
        nguoiDung.setCanDoiMatKhau(false);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUser login(LoginRequest request) {
        return dangNhap(request);
    }

    @Override
    public NguoiDung register(RegisterRequest request) {
        return dangKy(request);
    }

    @Override
    public boolean changePassword(String maNguoiDung, String oldPassword, String newPassword) {
        return doiMatKhau(maNguoiDung, oldPassword, newPassword);
    }
}
