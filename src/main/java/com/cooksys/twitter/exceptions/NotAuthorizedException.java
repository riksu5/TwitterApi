package com.cooksys.twitter.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotAuthorizedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8112077245330785031L;
	
	private String message;

}
