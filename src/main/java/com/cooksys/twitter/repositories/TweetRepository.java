package com.cooksys.twitter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.twitter.entities.Tweet;
import com.cooksys.twitter.entities.User;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
	
	List<Tweet> findAllByDeletedFalse();
	
	List<Tweet> findByAuthorAndDeletedFalse(User user);
	
	List<Tweet> findByAuthorCredentialsUsernameAndDeletedFalse(User user);
	
	List<Tweet> findByAuthor(User user);
	
	Optional<Tweet> findById(Long id);
	
	Optional<Tweet> findByIdAndDeletedFalse(Long id);
	
	List<Tweet> findByinReplyToAndInReplyTo_DeletedFalse(Tweet inReplyTo);
	
	List<Tweet> findByinReplyTo(Tweet inReplyTo);
	
	List<Tweet> findByRepostOfAndRepostOf_DeletedFalse(Tweet repostOf);
}
