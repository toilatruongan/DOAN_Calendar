package com.project.urban.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.urban.DTO.LoginDTO;
import com.project.urban.DTO.UserDTO;
import com.project.urban.DTO.UserEditDTO;
import com.project.urban.Exception.ResourceNotFoundException;

@Service
public interface UserService {

	UserDTO createUser(UserDTO userDTO);

	UserDTO getUserById(Long userId);

	List<UserDTO> getAllUsers();

	UserEditDTO updateUser(UserEditDTO userEditDTO);

	void deleteUser(Long userId);

	ResourceNotFoundException loginUser(LoginDTO loginDTO);

}
