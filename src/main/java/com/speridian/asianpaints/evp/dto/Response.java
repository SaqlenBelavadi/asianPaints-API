package com.speridian.asianpaints.evp.dto;

import lombok.*;

@Data
@AllArgsConstructor
@Builder
@ToString
@NoArgsConstructor
public class Response {
    String message;
    String reason;

}
