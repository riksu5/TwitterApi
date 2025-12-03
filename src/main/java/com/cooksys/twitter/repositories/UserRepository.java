package com.cooksys.twitter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.twitter.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findAllByDeletedFalse();
	
	Optional<User> findByCredentialsUsername(String username);
	
	Optional<User> findByCredentialsUsernameAndDeletedFalse(String username);
}
