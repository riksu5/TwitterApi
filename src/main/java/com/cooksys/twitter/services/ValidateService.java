package com.cooksys.twitter.services;

public interface ValidateService {

	boolean getHashtagExists(String label);

	boolean getUsernameExists(String username);

	boolean getUsernameAvailable(String username);

}
