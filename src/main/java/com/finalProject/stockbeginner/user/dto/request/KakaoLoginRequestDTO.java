package com.finalProject.stockbeginner.user.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoLoginRequestDTO {

    @NotBlank
    @Email
    private String email;



}