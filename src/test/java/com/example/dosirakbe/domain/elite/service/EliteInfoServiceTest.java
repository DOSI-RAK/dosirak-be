package com.example.dosirakbe.domain.elite.service;


import com.example.dosirakbe.domain.elite.dto.EliteInfoDto;
import com.example.dosirakbe.domain.elite.entity.EliteInfo;
import com.example.dosirakbe.domain.elite.repository.EliteInfoRepository;
import com.example.dosirakbe.global.util.ApiException;
import com.example.dosirakbe.global.util.ExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliteInfoServiceTest {

    @InjectMocks
    private EliteInfoService eliteInfoService;

    @Mock
    private EliteInfoRepository eliteInfoRepository;

    private EliteInfo eliteInfo;

    @BeforeEach
    void setUp() {
        // 테스트에 필요한 엘리트 통계 객체를 설정
        eliteInfo = mock(EliteInfo.class);

        when(eliteInfo.getInfoId()).thenReturn(1L);
        when(eliteInfo.getUserId()).thenReturn(1L);
        when(eliteInfo.getCorrectAnswers()).thenReturn(50);
        when(eliteInfo.getIncorrectAnswers()).thenReturn(30);
        when(eliteInfo.getTotalAnswers()).thenReturn(80);
    }

    @DisplayName("특정 사용자 ID로 엘리트 통계 정보를 조회한다.")
    @Test
    void findEliteInfoByUserIdTest() {
        Long userId = 1L;

        // mock된 리포지토리에서 사용자의 엘리트 통계 정보를 반환하도록 설정
        when(eliteInfoRepository.findByUserId(userId)).thenReturn(Optional.of(eliteInfo));

        // 서비스 메서드 호출
        Optional<EliteInfoDto> eliteInfoDto = eliteInfoService.findEliteInfoByUserId(userId);

        // 결과 검증
        assertTrue(eliteInfoDto.isPresent(), "EliteInfoDto should be present");
        assertEquals(1L, eliteInfoDto.get().getId(), "ID should match");
        assertEquals(1L, eliteInfoDto.get().getUserId(), "User ID should match");
        assertEquals(50, eliteInfoDto.get().getCorrectAnswers(), "Correct Answers should match");
        assertEquals(30, eliteInfoDto.get().getIncorrectAnswers(), "Incorrect Answers should match");
        assertEquals(80, eliteInfoDto.get().getTotalAnswers(), "Total Answers should match");

        // 리포지토리 메서드가 한 번 호출되었는지 검증
        verify(eliteInfoRepository, times(1)).findByUserId(userId);
    }

    @DisplayName("존재하지 않는 사용자의 경우, 엘리트 통계 정보가 없으면 빈 Optional을 반환한다.")
    @Test
    void findEliteInfoByUserId_UserNotFoundTest() {
        Long userId = 1L;

        // mock된 리포지토리에서 사용자의 엘리트 통계 정보가 없을 경우 빈 Optional을 반환하도록 설정
        when(eliteInfoRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // 서비스 메서드 호출
        Optional<EliteInfoDto> eliteInfoDto = eliteInfoService.findEliteInfoByUserId(userId);

        // 결과 검증
        assertFalse(eliteInfoDto.isPresent(), "EliteInfoDto should not be present");

        // 리포지토리 메서드가 한 번 호출되었는지 검증
        verify(eliteInfoRepository, times(1)).findByUserId(userId);
    }
}
