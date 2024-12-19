package com.example.dosirakbe.domain.elite.controller;

import com.example.dosirakbe.domain.auth.dto.response.CustomOAuth2User;
import com.example.dosirakbe.domain.elite.dto.EliteInfoDto;
import com.example.dosirakbe.domain.elite.service.EliteInfoService;
import com.example.dosirakbe.global.util.ApiResult;
import com.example.dosirakbe.global.util.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Optional;


/**
 * packageName    : com.example.dosirakbe.domain.elite.controller<br>
 * fileName       : EliteInfoController<br>
 * author         : femmefatlaehaein<br>
 * date           : 11/23/24<br>
 * description    : 사용자의 엘리트 정보(Elite Info)를 관리하고 제공하는 Controller 클래스입니다.<br>
 * ===========================================================<br>
 * DATE              AUTHOR             NOTE<br>
 * -----------------------------------------------------------<br>
 * 11/23/24        femmefatlaehaein       최초 생성<br>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EliteInfoController {

    private final EliteInfoService eliteInfoService;

    /**
     * 사용자 ID를 기준으로 엘리트 정보를 조회합니다.<br>
     * 인증된 사용자 정보를 기반으로 사용자 ID를 추출하고 해당 정보를 조회합니다.<br>
     *
     * @param customOAuth2User 인증된 사용자 정보를 포함한 객체
     * @return 사용자 엘리트 정보(EliteInfoDto)를 포함한 응답<br>
     *         - 성공: HTTP 200 응답과 함께 엘리트 정보 반환<br>
     *         - 실패: HTTP 404 응답과 함께 에러 메시지 반환
     */
    @GetMapping("/elite-infos/user")
    public ResponseEntity<ApiResult<EliteInfoDto>> getEliteInfoByUserId(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
            ) {

        Long userId = getUserIdByOAuth(customOAuth2User);
        Optional<EliteInfoDto> eliteInfo = eliteInfoService.findEliteInfoByUserId(userId);
        if (eliteInfo.isPresent()) {
            return ResponseEntity.ok(
                    ApiResult.<EliteInfoDto>builder()
                            .status(StatusEnum.SUCCESS)
                            .message("사용자 정보 조회 성공")
                            .data(eliteInfo.get())
                            .build()
            );
        } else {
            return ResponseEntity.status(404).body(
                    ApiResult.<EliteInfoDto>builder()
                            .status(StatusEnum.ERROR)
                            .message("해당 사용자 정보를 찾을 수 없습니다")
                            .data(null)
                            .build()
            );
        }
    }

    /**
     * OAuth2 인증 정보를 기반으로 사용자 ID를 가져옵니다.<br>
     *
     * @param customOAuth2User 인증된 사용자 정보를 포함한 객체
     * @return 인증된 사용자의 ID
     */
    private Long getUserIdByOAuth(CustomOAuth2User customOAuth2User) {
        return customOAuth2User.getUserDTO().getUserId();
    }

}
