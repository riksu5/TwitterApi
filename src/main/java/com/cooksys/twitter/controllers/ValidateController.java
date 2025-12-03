package com.cooksys.twitter.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.twitter.services.ValidateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/validate")
@RequiredArgsConstructor
public class ValidateController {

	private final ValidateService validateService;
	
	// Add required endpoints here
	@GetMapping("/tag/exists/{label}")
	public boolean getHashtagExists(@PathVariable("label") String label) {
		return validateService.getHashtagExists(label);
	}
	
	@GetMapping("/username/exists/@{username}")
	public boolean getUsernameExists(@PathVariable String username) {
		return validateService.getUsernameExists(username);
	}
	
	@GetMapping("/username/available/@{username}")
	public boolean getUsernameAvailable(@PathVariable String username) {
		return validateService.getUsernameAvailable(username);
	}
}
