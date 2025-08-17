package com.project.lms.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class PaginationUtil {

    public static Pageable preparePaginationUtil(Optional<Integer> page,
                                                 Integer defaultSize,
                                                 String sortBy,
                                                 String sortOrder) {
        int currentPage = page.orElse(1) - 1;
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy) : Sort.by(sortBy).descending();
        return PageRequest.of(currentPage, defaultSize, sort);
    }
}
