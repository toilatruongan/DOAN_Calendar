package com.project.urban.Controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.urban.DTO.UserDTO;
import com.project.urban.Entity.User;
import com.project.urban.Exception.ResourceNotFoundException;
import com.project.urban.Service.EmailServiice;
import com.project.urban.Service.UserService;

@RestController
@RequestMapping("/api/reset-password")
public class ResetPasswordController {
	@Autowired
    private UserService userService;
	
	@Autowired
    private EmailServiice emailService; // Dịch vụ gửi email
    
    // ... Các phương thức khác
    
	@PostMapping("/sendResetPasswordEmail")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestParam String email) {
        try {
            // Kiểm tra xem người dùng có tồn tại trong hệ thống không
            UserDTO user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            // Tạo và lưu mã token cho người dùng
            String token = generateResetPasswordToken();
            userService.updateResetPasswordToken(token, email);

            // Tạo link đặt lại mật khẩu
            String resetPasswordLink = "https://your-website.com/reset-password?token=" + token;

            // Gửi email chứa mã token đến địa chỉ email người dùng
            emailService.sendEmail(email, resetPasswordLink);

            return ResponseEntity.ok("Reset password email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    private String generateResetPasswordToken() {
        // Tạo một chuỗi ngẫu nhiên có độ dài mong muốn cho mã token
        int tokenLength = 10; // Độ dài mã token
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder token = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < tokenLength; i++) {
            int index = random.nextInt(characters.length());
            token.append(characters.charAt(index));
        }

        return token.toString();
    }
	@PostMapping("/resetPasswordToken")
    public ResponseEntity<?> updateResetPasswordToken(@RequestParam String token, @RequestParam String email) {
        try {
            ResourceNotFoundException result = userService.updateResetPasswordToken(token, email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/resetPasswordToken")
    public ResponseEntity<?> getByResetPasswordToken(@RequestParam String token) {
        User user = userService.getByResetPasswordToken(token);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody User user, @RequestParam String newPassword) {
        try {
            userService.updatePassword(user, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
