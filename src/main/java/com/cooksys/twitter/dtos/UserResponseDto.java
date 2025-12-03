package com.cooksys.twitter.dtos;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserResponseDto {

	private String username;

	private ProfileDto profile;

	@CreationTimestamp
	private Timestamp joined;
}
