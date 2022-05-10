package com.yu.recyclinghelper.VO;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignData implements Serializable {
    private String title;
    private String content;
    private int resId;
    private String explain;
    private String url;
}
