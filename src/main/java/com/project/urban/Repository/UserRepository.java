package com.project.urban.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.urban.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("SELECT u FROM User u WHERE u.email = ?1")
	Optional<User> findByEmail(String email);
	
	Optional<User> findOneByEmailAndPassword(String email, String password);
	
	Boolean existsByEmail(String email);
	
	public User findByResetPasswordToken(String token);


}
