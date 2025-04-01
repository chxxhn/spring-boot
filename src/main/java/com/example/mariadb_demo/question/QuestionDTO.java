package com.example.mariadb_demo.question;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuestionDTO {

    @NotEmpty(message = "제목은 필수항목입니다.")
    private String title;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    @NotEmpty(message = "비밀글 체크는 필수항목입니다.")
    private boolean secret;

}
