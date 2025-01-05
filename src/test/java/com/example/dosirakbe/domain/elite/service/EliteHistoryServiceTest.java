package com.example.dosirakbe.domain.elite.service;

import com.example.dosirakbe.domain.elite.dto.EliteHistoryResponseDto;
import com.example.dosirakbe.domain.elite.entity.EliteHistory;
import com.example.dosirakbe.domain.elite.repository.EliteHistoryRepository;
import com.example.dosirakbe.domain.elite.entity.Problem;
import com.example.dosirakbe.domain.elite.repository.ProblemRepository;
import com.example.dosirakbe.domain.user.entity.User;
import com.example.dosirakbe.domain.user.repository.UserRepository;
import com.example.dosirakbe.global.util.ApiException;
import com.example.dosirakbe.global.util.ExceptionEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliteHistoryServiceTest {

    @InjectMocks
    private EliteHistoryService eliteHistoryService;

    @Mock
    private EliteHistoryRepository eliteHistoryRepository;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private UserRepository userRepository;

    private User user;
    private Problem problem;
    private EliteHistory eliteHistory;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        problem = mock(Problem.class);
        eliteHistory = mock(EliteHistory.class);

        when(user.getUserId()).thenReturn(1L);
        when(problem.getProblemId()).thenReturn(101L);
        when(problem.getDescription()).thenReturn("Test Problem Description");
        when(problem.getAnswer()).thenReturn("Correct Answer");
        when(eliteHistory.getHistoryId()).thenReturn(1001L);
        when(eliteHistory.getProblemId()).thenReturn(1L);
        when(eliteHistory.getUserId()).thenReturn(1L);
        when(eliteHistory.isCorrect()).thenReturn(true);
    }

    @DisplayName("사용자의 문제 풀이 기록과 문제 정보를 정상적으로 조회한다.")
    @Test
    void findEliteHistoryWithProblemByUserIdTest() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eliteHistoryRepository.findByUserId(userId)).thenReturn(List.of(eliteHistory));

        List<EliteHistoryResponseDto> responses = eliteHistoryService.findEliteHistoryWithProblemByUserId(userId);

        assertNotNull(responses);
        assertEquals(1, responses.size());

        EliteHistoryResponseDto response = responses.get(0);
        assertEquals(1001L, response.getId());
        assertEquals(101L, response.getProblemId());
        assertEquals(1L, response.getUserId());
        assertTrue(response.isCorrect());
        assertEquals("Test Problem Description", response.getProblemDesc());
        assertEquals("Correct Answer", response.getProblemAns());

        verify(userRepository, times(1)).findById(userId);
        verify(eliteHistoryRepository, times(1)).findByUserId(userId);
    }

    @DisplayName("사용자의 문제 풀이 기록을 정상적으로 저장한다.")
    @Test
    void recordAnswerTest() {
        Long userId = 1L;
        Long problemId = 101L;
        boolean isCorrect = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(problemRepository.findByProblemId(problemId)).thenReturn(problem);
        when(eliteHistoryRepository.save(any(EliteHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eliteHistoryService.recordAnswer(userId, problemId, isCorrect);

        verify(userRepository, times(1)).findById(userId);
        verify(problemRepository, times(1)).findByProblemId(problemId);
        verify(eliteHistoryRepository, times(1)).save(any(EliteHistory.class));

    }

    @DisplayName("존재하지 않는 사용자의 경우, ApiException이 발생한다.")
    @Test
    void recordAnswer_UserNotFound_ThrowsException() {
        Long userId = 1L;
        Long problemId = 101L;
        boolean isCorrect = true;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> {
            eliteHistoryService.recordAnswer(userId, problemId, isCorrect);
        });

        assertEquals(ExceptionEnum.DATA_NOT_FOUND, exception.getError());

        verify(userRepository, times(1)).findById(userId);
        verify(problemRepository, times(0)).findByProblemId(problemId);
        verify(eliteHistoryRepository, times(0)).save(any(EliteHistory.class));
    }

    @DisplayName("존재하지 않는 문제의 경우, ApiException이 발생한다.")
    @Test
    void recordAnswer_ProblemNotFound_ThrowsException() {
        Long userId = 1L;
        Long problemId = 101L;
        boolean isCorrect = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(problemRepository.findByProblemId(problemId)).thenReturn(problem);

        ApiException exception = assertThrows(ApiException.class, () -> {
            eliteHistoryService.recordAnswer(userId, problemId, isCorrect);
        });

        assertEquals(ExceptionEnum.DATA_NOT_FOUND, exception.getError());

        verify(userRepository, times(1)).findById(userId);
        verify(problemRepository, times(1)).findByProblemId(problemId);
        verify(eliteHistoryRepository, times(0)).save(any(EliteHistory.class));
    }
}
