package com.highlight.highlight_backend.dto.dashboard;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminDashBoardStatsResponseDto {

    private Long inProgress;

    private Long completed;

    private Long pending;

}

