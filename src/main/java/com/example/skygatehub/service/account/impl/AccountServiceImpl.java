package com.example.skygatehub.service.account.impl;

import com.example.skygatehub.common.security.PasswordHasher;
import com.example.skygatehub.dto.account.UserCreateRequest;
import com.example.skygatehub.dto.account.UserResponse;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.account.NguoiDungVaiTro;
import com.example.skygatehub.entity.account.VaiTro;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.account.NguoiDungVaiTroRepository;
import com.example.skygatehub.repository.account.VaiTroRepository;
import com.example.skygatehub.service.account.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final NguoiDungVaiTroRepository nguoiDungVaiTroRepository;
    private final PasswordHasher passwordHasher;

    public AccountServiceImpl(NguoiDungRepository nguoiDungRepository,
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
    public Page<UserResponse> layTatCaNguoiDung(Pageable phanTrang) {
        Page<NguoiDung> trangNguoiDung = nguoiDungRepository.findAll(phanTrang);
        return trangNguoiDung.map(this::chuyenThanhUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> timKiemNguoiDung(String tuKhoa, Pageable phanTrang) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return layTatCaNguoiDung(phanTrang);
        }
        Page<NguoiDung> trangNguoiDung = nguoiDungRepository.timKiemNguoiDung(tuKhoa.trim(), phanTrang);
        return trangNguoiDung.map(this::chuyenThanhUserResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse layNguoiDungTheoId(Long id) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findById(id);
        return nguoiDungOptional.map(this::chuyenThanhUserResponse).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse layNguoiDungTheoMa(String maNguoiDung) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findByMaNguoiDung(maNguoiDung);
        return nguoiDungOptional.map(this::chuyenThanhUserResponse).orElse(null);
    }

    @Override
    public UserResponse taoNguoiDung(UserCreateRequest yeuCau) {
        String tenDangNhap = yeuCau.getTenDangNhap().trim();
        if (nguoiDungRepository.existsByTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại: " + tenDangNhap);
        }

        String maNguoiDung = yeuCau.getMaNguoiDung();
        if (maNguoiDung == null || maNguoiDung.trim().isEmpty()) {
            maNguoiDung = "ND" + System.currentTimeMillis() % 1000000;
            while (nguoiDungRepository.findByMaNguoiDung(maNguoiDung).isPresent()) {
                maNguoiDung = "ND" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            }
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setMaNguoiDung(maNguoiDung);
        nguoiDung.setTenDangNhap(tenDangNhap);
        nguoiDung.setMatKhauHash(passwordHasher.encode(yeuCau.getMatKhau() != null ? yeuCau.getMatKhau().trim() : "123456"));
        nguoiDung.setHoTen(yeuCau.getHoTen().trim());
        nguoiDung.setEmail(yeuCau.getEmail() != null ? yeuCau.getEmail().trim() : null);
        nguoiDung.setSoDienThoai(yeuCau.getSoDienThoai() != null ? yeuCau.getSoDienThoai().trim() : null);
        nguoiDung.setTrangThai(yeuCau.getTrangThai() != null ? yeuCau.getTrangThai() : true);
        nguoiDung.setCanDoiMatKhau(yeuCau.getCanDoiMatKhau() != null ? yeuCau.getCanDoiMatKhau() : false);
        nguoiDung.setNgayTao(LocalDateTime.now());
        nguoiDung.setNgayCapNhat(LocalDateTime.now());

        NguoiDung nguoiDungDaLuu = nguoiDungRepository.save(nguoiDung);

        if (yeuCau.getMaVaiTro() != null && !yeuCau.getMaVaiTro().trim().isEmpty()) {
            NguoiDungVaiTro nguoiDungVaiTro = new NguoiDungVaiTro();
            nguoiDungVaiTro.setMaNguoiDung(nguoiDungDaLuu.getMaNguoiDung());
            nguoiDungVaiTro.setMaVaiTro(yeuCau.getMaVaiTro().trim());
            nguoiDungVaiTro.setNgayGan(LocalDateTime.now());
            nguoiDungVaiTroRepository.save(nguoiDungVaiTro);
        }

        return chuyenThanhUserResponse(nguoiDungDaLuu);
    }

    @Override
    public UserResponse capNhatNguoiDung(Long id, UserCreateRequest yeuCau) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findById(id);
        if (nguoiDungOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id);
        }

        NguoiDung nguoiDung = nguoiDungOptional.get();
        if (yeuCau.getHoTen() != null) nguoiDung.setHoTen(yeuCau.getHoTen().trim());
        if (yeuCau.getEmail() != null) nguoiDung.setEmail(yeuCau.getEmail().trim());
        if (yeuCau.getSoDienThoai() != null) nguoiDung.setSoDienThoai(yeuCau.getSoDienThoai().trim());
        if (yeuCau.getTrangThai() != null) nguoiDung.setTrangThai(yeuCau.getTrangThai());
        if (yeuCau.getCanDoiMatKhau() != null) nguoiDung.setCanDoiMatKhau(yeuCau.getCanDoiMatKhau());
        if (yeuCau.getMatKhau() != null && !yeuCau.getMatKhau().trim().isEmpty()) {
            nguoiDung.setMatKhauHash(passwordHasher.encode(yeuCau.getMatKhau().trim()));
        }
        nguoiDung.setNgayCapNhat(LocalDateTime.now());

        NguoiDung nguoiDungCapNhat = nguoiDungRepository.save(nguoiDung);

        if (yeuCau.getMaVaiTro() != null && !yeuCau.getMaVaiTro().trim().isEmpty()) {
            nguoiDungVaiTroRepository.deleteByMaNguoiDung(nguoiDungCapNhat.getMaNguoiDung());
            NguoiDungVaiTro nguoiDungVaiTro = new NguoiDungVaiTro();
            nguoiDungVaiTro.setMaNguoiDung(nguoiDungCapNhat.getMaNguoiDung());
            nguoiDungVaiTro.setMaVaiTro(yeuCau.getMaVaiTro().trim());
            nguoiDungVaiTro.setNgayGan(LocalDateTime.now());
            nguoiDungVaiTroRepository.save(nguoiDungVaiTro);
        }

        return chuyenThanhUserResponse(nguoiDungCapNhat);
    }

    @Override
    public void chuyenTrangThaiNguoiDung(Long id) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findById(id);
        if (nguoiDungOptional.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOptional.get();
            nguoiDung.setTrangThai(!Boolean.TRUE.equals(nguoiDung.getTrangThai()));
            nguoiDung.setNgayCapNhat(LocalDateTime.now());
            nguoiDungRepository.save(nguoiDung);
        }
    }

    @Override
    public void xoaNguoiDung(Long id) {
        Optional<NguoiDung> nguoiDungOptional = nguoiDungRepository.findById(id);
        if (nguoiDungOptional.isPresent()) {
            NguoiDung nguoiDung = nguoiDungOptional.get();
            nguoiDungVaiTroRepository.deleteByMaNguoiDung(nguoiDung.getMaNguoiDung());
            nguoiDungRepository.delete(nguoiDung);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaiTro> layTatCaVaiTro() {
        return vaiTroRepository.findAll();
    }

    @Override
    public void capNhatVaiTroNguoiDung(String maNguoiDung, List<String> danhSachMaVaiTro) {
        nguoiDungVaiTroRepository.deleteByMaNguoiDung(maNguoiDung);
        if (danhSachMaVaiTro != null) {
            for (String maVaiTro : danhSachMaVaiTro) {
                NguoiDungVaiTro nguoiDungVaiTro = new NguoiDungVaiTro();
                nguoiDungVaiTro.setMaNguoiDung(maNguoiDung);
                nguoiDungVaiTro.setMaVaiTro(maVaiTro);
                nguoiDungVaiTro.setNgayGan(LocalDateTime.now());
                nguoiDungVaiTroRepository.save(nguoiDungVaiTro);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return layTatCaNguoiDung(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        return timKiemNguoiDung(keyword, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return layNguoiDungTheoId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByMa(String maNguoiDung) {
        return layNguoiDungTheoMa(maNguoiDung);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        return taoNguoiDung(request);
    }

    @Override
    public UserResponse updateUser(Long id, UserCreateRequest request) {
        return capNhatNguoiDung(id, request);
    }

    @Override
    public void toggleUserStatus(Long id) {
        chuyenTrangThaiNguoiDung(id);
    }

    @Override
    public void deleteUser(Long id) {
        xoaNguoiDung(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaiTro> getAllRoles() {
        return layTatCaVaiTro();
    }

    @Override
    public void updateUserRoles(String maNguoiDung, List<String> roleCodes) {
        capNhatVaiTroNguoiDung(maNguoiDung, roleCodes);
    }

    private UserResponse chuyenThanhUserResponse(NguoiDung nguoiDung) {
        List<NguoiDungVaiTro> danhSachVaiTro = nguoiDungVaiTroRepository.findByMaNguoiDung(nguoiDung.getMaNguoiDung());
        List<String> danhSachTenVaiTro = new ArrayList<>();
        for (NguoiDungVaiTro r : danhSachVaiTro) {
            Optional<VaiTro> vaiTroOptional = vaiTroRepository.findByMaVaiTro(r.getMaVaiTro());
            if (vaiTroOptional.isPresent()) {
                danhSachTenVaiTro.add(vaiTroOptional.get().getTenVaiTro());
            } else {
                danhSachTenVaiTro.add(r.getMaVaiTro());
            }
        }

        return new UserResponse(
                nguoiDung.getId(),
                nguoiDung.getMaNguoiDung(),
                nguoiDung.getTenDangNhap(),
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getTrangThai(),
                nguoiDung.getCanDoiMatKhau(),
                danhSachTenVaiTro,
                nguoiDung.getNgayTao(),
                nguoiDung.getNgayCapNhat()
        );
    }
}
