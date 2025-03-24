package com.example.mariadb_demo.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class UserDTO {
    @NotEmpty(message = "사용자 이름은 필수항목입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호는 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "전화번호는 필수항목입니다.")
    private String phone;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;
}
