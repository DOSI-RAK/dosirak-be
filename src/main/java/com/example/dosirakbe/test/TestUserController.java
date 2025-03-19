package com.example.dosirakbe.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/users")
public class TestUserController {
    private final TestUserService testUserService;
    private final ActivityLogsBatchService activityLogsBatchService;

    @PostMapping
    public ResponseEntity<Void> createUser() {
        testUserService.insertUsersInBatch();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/user-activity")
    public ResponseEntity<Void> createUserActivity() {
        activityLogsBatchService.insertActivityLogsInBatch();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
