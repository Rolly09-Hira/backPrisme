package com.prisme.back.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckResponse {
    private boolean exists;
}
