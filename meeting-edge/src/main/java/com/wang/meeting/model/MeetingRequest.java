package com.wang.meeting.model;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetingRequest {
    private String id = UUID.randomUUID().toString();

    private String traceId;

    private String type;

    private String action;

    private String data;

    public void setData(Object data) {
        if (data instanceof String) {
            this.data = (String) data;
        } else {
            this.data = JSON.toJSONString(data);
        }
    }
}
