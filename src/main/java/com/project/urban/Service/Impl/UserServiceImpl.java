package com.project.urban.Service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.urban.DTO.LoginDTO;
import com.project.urban.DTO.UserDTO;
import com.project.urban.Entity.User;
import com.project.urban.Exception.Constant;
import com.project.urban.Exception.ErrorConstant;
import com.project.urban.Exception.InvalidDataException;
import com.project.urban.Exception.ResourceNotFoundException;
import com.project.urban.Exception.ResponseCode;
import com.project.urban.Repository.UserRepository;
import com.project.urban.Service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDTO createUser(UserDTO userDTO) {

		// Mã hóa password
		String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

		ModelMapper modelMapper = new ModelMapper();
		User user = modelMapper.map(userDTO, User.class);
		user.setRole("Role_User");
		user.setPassword(hashedPassword); // Lưu password đã mã hóa
		User savedUser = userRepository.save(user);

		UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);

		return savedUserDTO;
	}

	@Override
	public UserDTO getUserById(Long userId) {
		ModelMapper modelMapper = new ModelMapper();

		Optional<User> optionalUser = userRepository.findById(userId);
		User user = optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		return modelMapper.map(user, UserDTO.class);
	}

	@Override
	public UserDTO getUserByEmail(String userEmail) {
		ModelMapper modelMapper = new ModelMapper();

		Optional<User> optionalUser = userRepository.findByEmail(userEmail);
		User user = optionalUser.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		return modelMapper.map(user, UserDTO.class);
	}
	
	@Override
	public List<UserDTO> getAllUsers() {
		List<UserDTO> allUsers = new ArrayList<>();
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			throw new InvalidDataException(ErrorConstant.NOT_FOUND, ErrorConstant.USER_NOT_FOUND);
		}
		ModelMapper modelMapper = new ModelMapper();
		for (User user : users) {
			UserDTO userDTO = modelMapper.map(user, UserDTO.class);
			allUsers.add(userDTO);
		}
		return allUsers;
	}

	@Override
	public UserDTO updateUser(UserDTO userDTO) {
		// Lấy đối tượng User từ cơ sở dữ liệu
		User existingUser = userRepository.findById(userDTO.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		// Ánh xạ các trường của đối tượng UserDTO vào đối tượng User
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.map(userDTO, existingUser);

		// Lưu đối tượng User đã cập nhật vào cơ sở dữ liệu
		User updatedUser = userRepository.save(existingUser);

		// Ánh xạ đối tượng User đã cập nhật thành đối tượng UserDTO và trả về
		UserDTO updatedUserDTO = modelMapper.map(updatedUser, UserDTO.class);
		return updatedUserDTO;
	}

	@Override
	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		userRepository.deleteById(user.getId());
	}

	@Override
	public ResourceNotFoundException loginUser(LoginDTO loginDTO) {
		Optional<User> user = userRepository.findByEmail(loginDTO.getEmail());
		if (user.isEmpty()) {
			return new ResourceNotFoundException(ResponseCode.CODE_404, ErrorConstant.NOT_FOUND, null);
		} else {
			User foundUser = user.get();
			if (foundUser.getPassword() != null) {
				boolean isPwdRight = passwordEncoder.matches(loginDTO.getPassword(), foundUser.getPassword());
				if (isPwdRight) {
					return new ResourceNotFoundException(ResponseCode.CODE_200, Constant.LOGIN_SUCCESS, user);
				}
			}
		}
		return new ResourceNotFoundException(ResponseCode.CODE_500, ErrorConstant.INTERNAL_SERVER_ERROR, null);
	}
	
	//REST PASSWORD 
	@Override
	public ResourceNotFoundException updateResetPasswordToken(String token, String email) {
	    Optional<User> optionalUser = userRepository.findByEmail(email);
	    if (optionalUser.isEmpty()) {
	        return new ResourceNotFoundException(ResponseCode.CODE_404, ErrorConstant.NOT_FOUND, null);
	    } else {
	        User user = optionalUser.get();
	        user.setResetPasswordToken(token);
	        userRepository.save(user);
	        return new ResourceNotFoundException(ResponseCode.CODE_200, Constant.RESTPASWORD_SUCCESS, email);
	    }
	}
    @Override
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
}
