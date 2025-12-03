package com.cooksys.twitter.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Profile {

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

}
