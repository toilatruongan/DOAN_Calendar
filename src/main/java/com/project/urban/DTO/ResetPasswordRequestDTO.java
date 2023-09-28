package com.project.urban.DTO;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
	private String email;
    private String newPassword;
}
