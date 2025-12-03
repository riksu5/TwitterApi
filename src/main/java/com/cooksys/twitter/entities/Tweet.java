package com.cooksys.twitter.entities;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tweet {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User author;

	@CreationTimestamp
	private Timestamp posted;

	private boolean deleted = false;

	private String content;

	@ManyToOne
	@JoinColumn(name = "replied_to")
	private Tweet inReplyTo;

	@ManyToOne
	@JoinColumn(name = "reposted_from")
	private Tweet repostOf;

	// For the user_likes table
	@ManyToMany(mappedBy = "likedTweets")
	private List<User> usersThatLiked;

	// For the user_mentions table
	@ManyToMany(mappedBy = "tweetsUserIsMentionedIn")
	private List<User> usersMentioned;
	
	// For the tweet_hashtags table
	@ManyToMany
	@JoinTable(name = "tweet_hashtags", joinColumns = @JoinColumn(name = "tweet_id"), inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
	List<Hashtag> hashtags;
}
