package com.example.dosirakbe.domain.elite.service;

import com.example.dosirakbe.domain.elite.dto.ProblemDto;
import com.example.dosirakbe.domain.elite.entity.Problem;
import com.example.dosirakbe.domain.elite.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @InjectMocks
    private ProblemService problemService;

    @Mock
    private ProblemRepository problemRepository;

    private Problem problem;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 문제 객체를 설정
        problem = mock(Problem.class);
        when(problem.getProblemId()).thenReturn(1L);
        when(problem.getDescription()).thenReturn("Test Problem");
        when(problem.getAnswer()).thenReturn("Test Answer");
    }

    @DisplayName("사용자가 풀지 않은 문제를 랜덤으로 조회한다.")
    @Test
    void getRandomProblemNotSolvedByUserTest() {
        Long userId = 1L;

        // 문제 리스트 반환 설정
        List<Problem> problems = Arrays.asList(problem, problem);
        when(problemRepository.findProblemsNotSolvedByUser(userId)).thenReturn(problems);

        // 서비스 메서드 호출
        ProblemDto problemDto = problemService.getRandomProblemNotSolvedByUser(userId);

        // 결과 검증
        assertNotNull(problemDto, "ProblemDto should not be null");
        assertEquals(1L, problemDto.getId(), "Problem ID should match");
        assertEquals("Test Problem", problemDto.getDescription(), "Problem Description should match");
        assertEquals("Test Answer", problemDto.getAnswer(), "Problem Answer should match");

        // 리포지토리 메서드 호출 횟수 검증
        verify(problemRepository, times(1)).findProblemsNotSolvedByUser(userId);
    }

    @DisplayName("사용자가 풀지 않은 문제가 없으면 null을 반환한다.")
    @Test
    void getRandomProblemNotSolvedByUser_NoProblemsTest() {
        Long userId = 1L;

        // 풀지 않은 문제가 없을 경우 빈 리스트 반환
        when(problemRepository.findProblemsNotSolvedByUser(userId)).thenReturn(List.of());

        // 서비스 메서드 호출
        ProblemDto problemDto = problemService.getRandomProblemNotSolvedByUser(userId);

        // 결과 검증
        assertNull(problemDto, "ProblemDto should be null when no problems are found");

        // 리포지토리 메서드 호출 횟수 검증
        verify(problemRepository, times(1)).findProblemsNotSolvedByUser(userId);
    }

    @DisplayName("특정 문제 ID로 문제를 조회한다.")
    @Test
    void findProblemByIdTest() {
        Long problemId = 1L;

        // 특정 문제를 반환하도록 설정
        when(problemRepository.findByProblemId(problemId)).thenReturn(problem);

        // 서비스 메서드 호출
        ProblemDto problemDto = problemService.findProblemById(problemId);

        // 결과 검증
        assertNotNull(problemDto, "ProblemDto should not be null");
        assertEquals(1L, problemDto.getId(), "Problem ID should match");
        assertEquals("Test Problem", problemDto.getDescription(), "Problem Description should match");
        assertEquals("Test Answer", problemDto.getAnswer(), "Problem Answer should match");

        // 리포지토리 메서드 호출 횟수 검증
        verify(problemRepository, times(1)).findByProblemId(problemId);
    }

    @DisplayName("존재하지 않는 문제 ID로 조회할 경우 null을 반환한다.")
    @Test
    void findProblemById_NotFoundTest() {
        Long problemId = 1L;

        // 해당 문제가 없을 경우 null을 반환하도록 설정
        when(problemRepository.findByProblemId(problemId)).thenReturn(null);

        // 서비스 메서드 호출
        ProblemDto problemDto = problemService.findProblemById(problemId);

        // 결과 검증
        assertNull(problemDto, "ProblemDto should be null when the problem is not found");

        // 리포지토리 메서드 호출 횟수 검증
        verify(problemRepository, times(1)).findByProblemId(problemId);
    }
}
