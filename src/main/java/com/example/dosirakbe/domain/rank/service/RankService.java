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


    //24시간에 한번
    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = "ranking", allEntries = true)
    public void updateRank() {
        getRankedUsers();
    }

    @Cacheable(value = "ranking")
    public List<RankResponse> getRankedUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "reward"));
        List<RankResponse> rankedUsers = new ArrayList<>();

        int rank = 0;

        int prevReward = -1;

        int equivalentUser = 0;

        for (User user : users) {
            if (user.getNickName() != null && user.getNickName().startsWith("(알 수 없음)")) {
                continue;
            }

            if (user.getReward() != prevReward) {
                rank += equivalentUser + 1;
                equivalentUser = 0;
            } else {
                equivalentUser++;
            }

            rankedUsers.add(new RankResponse(
                    user.getUserId(),
                    user.getProfileImg(),
                    rank,
                    user.getNickName(),
                    user.getReward()
            ));

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
