package com.ssafy.hp.recommend.service;

import com.ssafy.hp.config.DjangoConnectConfig;
import com.ssafy.hp.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    @Override
    public JSONArray recommendExercise(String uri, User user) {
        return DjangoConnectConfig.connect(uri+"/"+user.getUserId());
    }
    @Override
    public JSONArray recommendExercise(String uri, User user, String exerciseId) {
        return DjangoConnectConfig.connect(uri+"/"+user.getUserId()+"/"+exerciseId);
    }

    @Override
    public JSONArray recommendPill(String uri, User user) {
        return DjangoConnectConfig.connect(uri+"/"+user.getUserId());
    }

    @Override
    public JSONArray recommendPill(String uri, User user, String pillId) {
        return DjangoConnectConfig.connect(uri+"/"+user.getUserId()+"/"+pillId);
    }


}
