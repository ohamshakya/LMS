package com.project.lms.user.service;

import java.util.List;

public interface CollaborativeFilteringService {
    List<Integer> recommend(Integer userId, int topN);
}
