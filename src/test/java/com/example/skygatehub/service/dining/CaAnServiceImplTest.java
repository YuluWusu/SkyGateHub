package com.example.skygatehub.service.dining;

import com.example.skygatehub.dto.dining.CaAnRequest;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.service.dining.impl.CaAnServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaAnServiceImplTest {

    @Mock
    private CaAnRepository caAnRepository;

    @InjectMocks
    private CaAnServiceImpl service;

    private CaAnRequest request(String ma, LocalTime start, LocalTime end) {
        CaAnRequest r = new CaAnRequest();
        r.setMaCaAn(ma);
        r.setTenCaAn("Bữa trưa");
        r.setGioBatDau(start);
        r.setGioKetThuc(end);
        return r;
    }

    private CaAn ca(Long id, String ma) {
        CaAn c = new CaAn();
        c.setId(id);
        c.setMaCaAn(ma);
        c.setTenCaAn("Bữa trưa");
        return c;
    }

    @Test
    void createRejectsEndBeforeStart() {
        CaAnRequest r = request("TRUA", LocalTime.of(12, 0), LocalTime.of(10, 0));
        when(caAnRepository.existsByMaCaAn("TRUA")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(r));
        verify(caAnRepository, never()).save(any());
    }

    @Test
    void createRejectsDuplicateCode() {
        CaAnRequest r = request(" trua ", LocalTime.of(11, 0), LocalTime.of(13, 0));
        when(caAnRepository.existsByMaCaAn("TRUA")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(r));
        verify(caAnRepository, never()).save(any());
    }

    @Test
    void deleteRejectsShiftWithLinks() {
        CaAn c = ca(1L, "TRUA");
        when(caAnRepository.findById(1L)).thenReturn(Optional.of(c));
        when(caAnRepository.countLinksByMaCaAn("TRUA")).thenReturn(3L);

        assertThrows(IllegalStateException.class, () -> service.delete(1L));
        verify(caAnRepository, never()).delete(any());
    }

    @Test
    void deleteRemovesShiftWithoutLinks() {
        CaAn c = ca(1L, "TRUA");
        when(caAnRepository.findById(1L)).thenReturn(Optional.of(c));
        when(caAnRepository.countLinksByMaCaAn("TRUA")).thenReturn(0L);

        service.delete(1L);
        verify(caAnRepository).delete(c);
    }
}
