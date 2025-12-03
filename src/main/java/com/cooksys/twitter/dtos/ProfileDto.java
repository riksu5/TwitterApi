package com.cooksys.twitter.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileDto {

	// Optional
	private String firstName;
	
	// Optional
	private String lastName;
	
	private String email;
	
	// Optional
	private String phone;
}
