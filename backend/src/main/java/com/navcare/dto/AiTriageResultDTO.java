package com.navcare.dto;

import lombok.Data;

@Data
public class AiTriageResultDTO {

    private String specialty;
    private String urgency;
    private String summary;
}
