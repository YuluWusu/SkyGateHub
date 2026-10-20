package com.example.skygatehub.repository.payment;

import com.example.skygatehub.common.enums.PhamViCoc;
import com.example.skygatehub.entity.payment.ChinhSachCoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChinhSachCocRepository extends JpaRepository<ChinhSachCoc, Long> {

    Optional<ChinhSachCoc> findByMaChinhSachCoc(String maChinhSachCoc);

    List<ChinhSachCoc> findByPhamViApDung(PhamViCoc phamViApDung);
}

