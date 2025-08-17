package com.project.lms.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RatingDto {
    private int rating;

    private String review;
}
