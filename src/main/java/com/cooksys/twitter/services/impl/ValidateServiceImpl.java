package com.cooksys.twitter.services.impl;

import org.springframework.stereotype.Service;

import com.cooksys.twitter.repositories.HashtagRepository;
import com.cooksys.twitter.repositories.UserRepository;
import com.cooksys.twitter.services.ValidateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

	private final HashtagRepository hashtagRepository;

	private final UserRepository userRepository;

	@Override
	public boolean getHashtagExists(String label) {
		// Since labels are unique but case-insensitive, ignore the case
		// while checking
		// Check if the label exists
		System.out.println(label);
		if (label != null && label.length() > 0 && hashtagRepository.findByLabelIgnoreCase(label).isPresent()) {
			return true;
		}
		// If it doesn't, then false
		return false;
	}

	@Override
	public boolean getUsernameExists(String username) {
		// Check if the username exists
		if (userRepository.findByCredentialsUsername(username).isPresent()) {
			return true;
		}
		// If it doesn't then return false
		return false;
	}

	@Override
	public boolean getUsernameAvailable(String username) {
		// Check if the username exists and that the user is already deleted (making the
		// username available)
		if (getUsernameExists(username) && userRepository.findByCredentialsUsername(username).get().isDeleted()) {
			return true;
		} else if (!getUsernameExists(username)) {
			// If the user doesn't exist, then the name is available
			return true;
		}
		// If it doesn't then return false
		return false;
	}

}
