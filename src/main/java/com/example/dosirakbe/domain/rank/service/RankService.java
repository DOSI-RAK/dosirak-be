package com.example.dosirakbe.domain.rank.service;

import com.example.dosirakbe.domain.rank.dto.response.RankResponse;
import com.example.dosirakbe.domain.user.entity.User;
import com.example.dosirakbe.domain.user.repository.UserRepository;
import com.example.dosirakbe.global.util.ApiException;
import com.example.dosirakbe.global.util.ExceptionEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RankService {

    @Autowired
    private UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = "ranking", allEntries = true)
    public void updateRank() {
        getRankedUsers();
    }

    @Cacheable(value = "ranking")
    public List<RankResponse> getRankedUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "reward"));
        List<RankResponse> rankedUsers = new ArrayList<>();

        int rank = 1;
        int prevReward = -1;

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user.getReward() != prevReward) {
                rank = i + 1; // 새로운 순위
            }
            rankedUsers.add(new RankResponse(user.getUserId(), user.getProfileImg(), rank, user.getNickName(), user.getReward()));
            prevReward = user.getReward();
        }

        return rankedUsers;
    }

    public RankResponse getRankByUserId(Long userId) {

        List<RankResponse> rankedUsers = getRankedUsers();

        return rankedUsers.stream()
                .filter(rankResponse -> rankResponse.getUserId().equals(userId))
                .findFirst()
                .orElseGet(() -> {

                    int allRank = rankedUsers.isEmpty() ? 0 : rankedUsers.get(rankedUsers.size() - 1).getRank();
                    int rank = allRank + 1;

                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ApiException(ExceptionEnum.DATA_NOT_FOUND));

                    return new RankResponse(
                            user.getUserId(),
                            user.getProfileImg(),
                            rank,
                            user.getNickName(),
                            user.getReward()
                    );
                });
    }

}