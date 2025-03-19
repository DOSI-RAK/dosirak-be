package com.example.dosirakbe.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestUserService {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void insertUsersInBatch() {
        int batchSize = 10000;
        List<Object[]> batchArgs = new ArrayList<>();

        for (int i = 1; i <= 1_000_000; i++) {
            batchArgs.add(new Object[]{
                    "User" + i,
                    "social_" + UUID.randomUUID(),
                    "Nick" + i,
                    "user" + i + "@example.com",
                    "https://example.com/profile" + i + ".png",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    true,
                    0,
                    BigDecimal.ZERO
            });

            if (i % batchSize == 0) {
                executeBatchInsert(batchArgs);
                batchArgs.clear();

                //System.out.println(i + " users inserted.");
            }
        }

        if (!batchArgs.isEmpty()) {
            executeBatchInsert(batchArgs);
        }


    }

    private void executeBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO users (name, user_name, nick_name, email, profile_img, created_at, updated_at, user_valid, reward, track_distance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
