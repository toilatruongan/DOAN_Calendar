package com.project.urban.Entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = { "email" }) })
@JsonIgnoreProperties
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;

	@Column(name = "password", length = 250, nullable = false)
	@NotBlank(message = "Password is required")
	private String password;

	@Column(name = "email", length = 50)
	@Size(max = 50, message = "Email must be less than 50 characters")
	private String email;

	@Column(name = "name", length = 50, nullable = false)
	@Size(max = 50, message = "Your name must be less than 50 characters")
	@NotBlank(message = "Your name is required")
	private String name;

	private String role;
	// relationship
	
	@Column(name = "reset_password_token")
    private String resetPasswordToken;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Event> Events = new ArrayList<>();
}
