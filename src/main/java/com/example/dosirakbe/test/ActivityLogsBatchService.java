package com.example.dosirakbe.test;

import com.example.dosirakbe.domain.activity_log.entity.ActivityType;
import com.example.dosirakbe.domain.user.entity.User;
import com.example.dosirakbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ActivityLogsBatchService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final Random random = new Random();

    @Transactional
    public void insertActivityLogsInBatch() {
        int batchSize = 10_000;
        List<Object[]> batchArgs = new ArrayList<>();

        List<User> users = userRepository.findAll();
        Collections.shuffle(users);

        int userCount = users.size();

        for (int i = 0; i < 100_000_000; i++) {
            User user = users.get(i % userCount);
            LocalDateTime randomDate = getRandomDate();

            batchArgs.add(new Object[]{
                    (long) random.nextInt(1_000_000), // 랜덤 content_id
                    user.getUserId(),
                    ActivityType.values()[random.nextInt(ActivityType.values().length)].name(),
                    BigDecimal.valueOf(random.nextDouble() * 10), // 0~10km 이동 거리
                    randomDate
            });

            if (batchArgs.size() >= batchSize) {
                executeBatchInsert(batchArgs);
                batchArgs.clear();

                //System.out.println(i + " activity logs inserted.");
            }
        }

        if (!batchArgs.isEmpty()) {
            executeBatchInsert(batchArgs);
        }

        //System.out.println("✅ 1억 개의 활동 로그 삽입 완료!");
    }

    private void executeBatchInsert(List<Object[]> batchArgs) {
        String sql = "INSERT INTO activity_logs (content_id, user_id, activity_type, distance, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * 2024년 1월 ~ 2025년 2월 사이의 랜덤 날짜를 반환합니다.
     */
    private LocalDateTime getRandomDate() {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 2, 28, 23, 59);
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        return startDate.plusDays(random.nextInt((int) daysBetween + 1))
                .plusHours(random.nextInt(24))
                .plusMinutes(random.nextInt(60))
                .plusSeconds(random.nextInt(60));
    }
}