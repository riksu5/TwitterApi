package com.cooksys.twitter.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_table")
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@Embedded
	private Credentials credentials;

	@CreationTimestamp
	private Timestamp joined;

	private boolean deleted = false;

	@Embedded
	private Profile profile;

	// For the followers_following table
	// In the Many-To-Many Spring documentation, it says the joinColumn attr. will
	// connect to the owner side of the relationship, and inverse should connect to
	// the opposite side.
	// This means that following_id should be the owner side, b/c if we use
	// follower_id we will end up with a list where user.id is found in the follower
	// side, when we really want to find when user.id matches following_id (meaning
	// we will get the followers who follow our user).
	@ManyToMany
	@JoinTable(name = "followers_following", joinColumns = @JoinColumn(name = "following_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
	private List<User> followers;

	@ManyToMany(mappedBy = "followers")
	private List<User> following;

	// For the connection between users and tweets
	@OneToMany(mappedBy = "author")
	private List<Tweet> tweets;

	// For the user_likes table
	@ManyToMany
	@JoinTable(name = "user_likes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tweet_id"))
	private List<Tweet> likedTweets;

	// For the user_mentions table
	@ManyToMany
	@JoinTable(name = "user_mentions", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "tweet_id"))
	private List<Tweet> tweetsUserIsMentionedIn;
}
