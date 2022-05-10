package com.yu.recyclinghelper.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ChallengeData {
    private String title;
    private String content;
    private int resId;
}
