package ru.shalagin.PP_3_1_5_Rest_controllers.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class UserErrorResponse {
    private String message;
    private long timestamp;
}
