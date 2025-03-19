package com.example.dosirakbe.domain.activity_log.controller;

import com.example.dosirakbe.domain.activity_log.dto.response.ActivityLogResponse;
import com.example.dosirakbe.domain.activity_log.service.ActivityLogService;
import com.example.dosirakbe.domain.auth.dto.response.CustomOAuth2User;
import com.example.dosirakbe.global.util.ApiResult;
import com.example.dosirakbe.global.util.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * packageName    : com.example.dosirakbe.domain.activity_log.controller<br>
 * fileName       : ActivityLogController<br>
 * author         : Fiat_lux<br>
 * date           : 11/03/24<br>
 * description    : 활동 로그 관련 API 요청을 처리하는 컨트롤러 클래스입니다.<br>
 * ===========================================================<br>
 * DATE              AUTHOR             NOTE<br>
 * -----------------------------------------------------------<br>
 * 11/03/24        Fiat_lux                최초 생성<br>
 * 12/20/24        Fiat_lux                api 수정<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/activity-logs")
public class ActivityLogController {
    private final ActivityLogService activityLogService;

    /**
     * 특정 날짜의 활동 로그를 조회합니다.
     *
     * <p>
     * 이 메서드는 인증된 사용자의 지정된 날짜에 해당하는 활동 로그를 조회하여 반환합니다.
     * </p>
     *
     * @param customOAuth2User 인증된 사용자 정보
     * @param date             조회할 날짜 (yyyy-MM-dd 형식)
     * @return 지정된 날짜의 활동 로그를 포함한 {@link ApiResult} 객체
     */
    @GetMapping
    public ResponseEntity<ApiResult<List<ActivityLogResponse>>> getTodayActivityLog(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                                    @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Long userId = getUserIdByOAuth(customOAuth2User);
        List<ActivityLogResponse> activityLogs = activityLogService.getThatDateActivityLog(userId, date);

        ApiResult<List<ActivityLogResponse>> result = ApiResult.<List<ActivityLogResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("Activity history for today retrieved successfully")
                .data(activityLogs)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /**
     * 특정 월의 첫째 날의 활동 로그를 조회합니다.
     *
     * <p>
     * 이 메서드는 인증된 사용자의 지정된 월의 첫째 날에 해당하는 활동 로그를 조회하여 반환합니다.
     * </p>
     *
     * @param customOAuth2User 인증된 사용자 정보
     * @param month            조회할 월 (yyyy-MM 형식)
     * @return 지정된 월의 첫째 날의 활동 로그를 포함한 {@link ApiResult} 객체
     */
    @GetMapping("/first-day/{month}")
    public ResponseEntity<ApiResult<List<ActivityLogResponse>>> getActivityLogForFirstDayOfMonth(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                                                 @PathVariable("month") @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        Long userId = getUserIdByOAuth(customOAuth2User);
        List<ActivityLogResponse> firstDayLogs = activityLogService.getActivityLogForFirstDayOfMonth(userId, month);

        ApiResult<List<ActivityLogResponse>> result = ApiResult.<List<ActivityLogResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("Activity history for first date retrieved successfully")
                .data(firstDayLogs)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    /**
     * 인증된 사용자의 ID를 추출합니다.
     *
     * <p>
     * 이 메서드는 인증된 {@link CustomOAuth2User} 객체로부터 사용자의 ID를 추출하여 반환합니다.
     * </p>
     *
     * @param customOAuth2User 인증된 사용자 정보
     * @return 사용자의 고유 ID
     */
    private Long getUserIdByOAuth(CustomOAuth2User customOAuth2User) {
        return customOAuth2User.getUserDTO().getUserId();
    }
}