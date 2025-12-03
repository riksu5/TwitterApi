package com.cooksys.twitter.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.cooksys.twitter.dtos.ContextDto;
import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetRequestDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.dtos.UserResponseDto;
import com.cooksys.twitter.entities.Credentials;
import com.cooksys.twitter.entities.Hashtag;
import com.cooksys.twitter.entities.Tweet;
import com.cooksys.twitter.entities.User;
import com.cooksys.twitter.exceptions.BadRequestException;
import com.cooksys.twitter.exceptions.NotAuthorizedException;
import com.cooksys.twitter.exceptions.NotFoundException;
//import com.cooksys.twitter.mappers.CredentialsMapper;
import com.cooksys.twitter.mappers.HashtagMapper;
import com.cooksys.twitter.mappers.TweetMapper;
import com.cooksys.twitter.mappers.UserMapper;
import com.cooksys.twitter.repositories.HashtagRepository;
import com.cooksys.twitter.repositories.TweetRepository;
import com.cooksys.twitter.repositories.UserRepository;
import com.cooksys.twitter.services.TweetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final UserRepository userRepository;
	private final UserMapper userMapper;

//	private final CredentialsMapper credentialsMapper;

	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;

	private List<Tweet> reverseChronoSort(List<Tweet> tweets) {
		tweets.sort((Tweet t1, Tweet t2) -> t1.getPosted().compareTo(t2.getPosted()));
		return tweets.reversed();
	}

	private List<Tweet> chronoSort(List<Tweet> tweets) {
		tweets.sort((Tweet t1, Tweet t2) -> t1.getPosted().compareTo(t2.getPosted()));
		return tweets;
	}

	private boolean credentialsMatch(CredentialsDto credentialsDto, Credentials credentials) {
		if (credentialsDto == null || credentialsDto.getUsername() == null || credentialsDto.getPassword() == null
				|| credentials == null || credentials.getUsername() == null || credentials.getPassword() == null) {
			return false;
		}

		if (credentialsDto.getUsername().equals(credentials.getUsername())
				&& credentialsDto.getPassword().equals(credentials.getPassword())) {
			return true;
		}
		return false;
	}

	@Override
	public List<TweetResponseDto> getAllTweets() {
		// Find all non-deleted tweets
		List<Tweet> tweets = tweetRepository.findAllByDeletedFalse();

		// Sort in chronological order and then reverse it
		tweets = reverseChronoSort(tweets);

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public TweetResponseDto createTweet(TweetRequestDto tweetRequestDto) {

		// Check if the tweet has only the required properties to make a SIMPLE tweet
		if (tweetRequestDto.getCredentials() == null || tweetRequestDto.getCredentials().getUsername() == null
				|| tweetRequestDto.getCredentials().getPassword() == null || tweetRequestDto.getContent() == null) {
			// Throw error
			throw new BadRequestException("POST: credentials (username and password) and content are required fields.");
		}

		// Check if the intended user matches the creds
		Optional<User> checkAuthor = userRepository
				.findByCredentialsUsernameAndDeletedFalse(tweetRequestDto.getCredentials().getUsername());
		if (checkAuthor.isEmpty()) {
			throw new NotFoundException(
					"POST: The user " + tweetRequestDto.getCredentials().getUsername() + " was not found.");
		}

		if (!credentialsMatch(tweetRequestDto.getCredentials(), checkAuthor.get().getCredentials())) {
			throw new NotAuthorizedException("POST: The credentials are incorrect.");
		}
		User author = checkAuthor.get();

		// Create the tweet entity
		Tweet tweet = tweetMapper.requestDtoToEntity(tweetRequestDto);

		// Set the tweet author
		tweet.setAuthor(author);

		// Save the tweet to the repository so it's created
		tweetRepository.saveAndFlush(tweet);

		// Set tweet-user relationship
		List<Tweet> authorTweets = author.getTweets();
		authorTweets.add(tweet);
		author.setTweets(authorTweets);
		userRepository.saveAndFlush(author);

		// Process the tweet for @{username} mentions using regex
		Pattern pattern = Pattern.compile("@(\\w+)");
		Matcher match = pattern.matcher(tweet.getContent());

		List<String> mentions = new ArrayList<String>();

		while (match.find()) {
			mentions.add(match.group(1));
		}

		// Add them to Tweet and User as needed
		List<User> usersMentioned = new ArrayList<User>();
		for (String username : mentions) {
			// Check if it's a valid user
			Optional<User> userToCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
			if (userToCheck.isPresent()) {
				// If so, then get the user and add them to Tweet usersMentioned and User
				// tweetsUserIsMentionedIn
				User userMentioned = userToCheck.get();
				usersMentioned.add(userMentioned);

				List<Tweet> mentionedTweets = userMentioned.getTweetsUserIsMentionedIn();
				mentionedTweets.add(tweet);
				userMentioned.setTweetsUserIsMentionedIn(mentionedTweets);
				userRepository.save(userMentioned);
			}
		}
		tweet.setUsersMentioned(usersMentioned);
		// (Save and) Flush the changes
		userRepository.flush();
		tweetRepository.save(tweet);

		// Process the tweet for #{hashtag} tags using regex
		pattern = Pattern.compile("#(\\w+)");
		match = pattern.matcher(tweet.getContent());

		List<String> tags = new ArrayList<String>();

		while (match.find()) {
			// Setting to lowercase because case insensitive
			tags.add(match.group(1));
		}

		// Add them to Tweet and Hashtag as needed
		List<Hashtag> hashtags = new ArrayList<>();
		for (String tag : tags) {
//			tag = tag.toLowerCase();
			// Check if the tag already exists
			Optional<Hashtag> findTag = hashtagRepository.findByLabelIgnoreCase(tag);
			Hashtag foundTag;
			if (findTag.isEmpty()) {
				// We need to create the hashtag and update fields
				Hashtag newHashtag = new Hashtag();
				newHashtag.setLabel(tag);
				List<Tweet> tweetsUsedIn = new ArrayList<Tweet>();
				tweetsUsedIn.add(tweet);
				newHashtag.setTweetsUsedIn(tweetsUsedIn);
				// Save the new tag to the hashtagRepository
				foundTag = hashtagRepository.save(newHashtag);
			} else {
				foundTag = findTag.get();
				List<Tweet> tweetsUsedIn = foundTag.getTweetsUsedIn();
				tweetsUsedIn.add(tweet);
				foundTag.setTweetsUsedIn(tweetsUsedIn);
				hashtagRepository.save(foundTag);
			}
			// Add the tag to the Tweet
//			foundTag = findTag.get();
			if (!hashtags.contains(foundTag)) {
				hashtags.add(foundTag);
			}
		}
		tweet.setHashtags(hashtags);

		// (Save and) Flush the changes
		hashtagRepository.flush();
		tweetRepository.saveAndFlush(tweet);

		return tweetMapper.entityToResponseDto(tweet);

	}

	@Override
	public TweetResponseDto getTweetById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET; A tweet with id " + id + " was not found.");
		}

		return tweetMapper.entityToResponseDto(tweetToCheck.get());
	}

	@Override
	public TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("DELETE: A tweet with id " + id + " was not found.");
		}

		// Check if the credentials match
		Optional<User> userToCheck = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
		if (userToCheck.isEmpty()
				|| (userToCheck.isPresent() && !credentialsMatch(credentialsDto, userToCheck.get().getCredentials()))) {
			throw new NotAuthorizedException("DELETE: Either the user doesn't exist or the credentials are bad.");
		}

		// If they do, delete the tweet
		Tweet tweet = tweetToCheck.get();
		tweet.setDeleted(true);

		return tweetMapper.entityToResponseDto(tweetRepository.saveAndFlush(tweet));
	}

	@Override
	public void createTweetLike(Long id, CredentialsDto credentialsDto) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("POST: A tweet with id " + id + " was not found.");
		}

		// Check if the credentials match
		Optional<User> userToCheck = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
		if (userToCheck.isEmpty()
				|| (userToCheck.isPresent() && !credentialsMatch(credentialsDto, userToCheck.get().getCredentials()))) {
			throw new NotAuthorizedException("POST: Either the user doesn't exist or the credentials are bad.");
		}

		// If they do, like the tweet from Tweet and User side
		Tweet tweet = tweetToCheck.get();
		User user = userToCheck.get();
		List<User> usersLiked = tweet.getUsersThatLiked();
		List<Tweet> tweetsLiked = user.getLikedTweets();
		// Check to make sure we aren't duplicating before actually adding
		if (!usersLiked.contains(user)) {
			usersLiked.add(user);
			tweet.setUsersThatLiked(usersLiked);
			tweetsLiked.add(tweet);
			user.setLikedTweets(tweetsLiked);
			// Save and flush changes
			tweetRepository.saveAndFlush(tweet);
			userRepository.saveAndFlush(user);
		}

		return;
	}

	@Override
	public TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto) {

		// Check if the id provided corresponds to an existing, non-deleted tweet
		Optional<Tweet> inReplyToTweetCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (inReplyToTweetCheck.isEmpty()) {
			throw new NotFoundException("POST: Tweet with id " + id + " not found.");
		}

		// Create a simple tweet and then add the relationship
		// It has all the necessary checks and will parse the regex
		TweetResponseDto simpleTweet = createTweet(tweetRequestDto);

		// Find the proper/full tweet using the id from the response
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(simpleTweet.getId());
		if (tweetToCheck.isEmpty()) {
			throw new RuntimeException("Error when creating tweet");
		}
		Tweet tweet = tweetToCheck.get();

		// Create the reply relationship
		tweet.setInReplyTo(inReplyToTweetCheck.get());

		// Save and flush the results
		return tweetMapper.entityToResponseDto(tweetRepository.saveAndFlush(tweet));
	}

	@Override
	public TweetResponseDto createTweetRepost(Long id, CredentialsDto credentialsDto) {

		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("POST: A tweet with id " + id + " was not found.");
		}

		// Check if the credentials match
		Optional<User> userToCheck = userRepository.findByCredentialsUsername(credentialsDto.getUsername());
		if (userToCheck.isEmpty()
				|| (userToCheck.isPresent() && !credentialsMatch(credentialsDto, userToCheck.get().getCredentials()))) {
			throw new NotAuthorizedException("POST: Either the user doesn't exist or the credentials are bad.");
		}

		// Create the REPOST tweet
		Tweet tweet = new Tweet();
		tweetRepository.save(tweet);

		// Set the tweet properties
		User author = userToCheck.get();
		tweet.setAuthor(author);
		tweet.setRepostOf(tweetToCheck.get());

		return tweetMapper.entityToResponseDto(tweetRepository.saveAndFlush(tweet));
	}

	@Override
	public List<HashtagDto> getTagsById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Get tweet
		Tweet tweet = tweetToCheck.get();

		return hashtagMapper.entitiesToDtos(tweet.getHashtags());
	}

	@Override
	public List<UserResponseDto> getLikesById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Get tweet and list of users who liked
		Tweet tweet = tweetToCheck.get();
		List<User> usersLiked = tweet.getUsersThatLiked();
		// Figure out the ones that are active
		List<User> activeUsers = new ArrayList<User>();
		for (User u : usersLiked) {
			if (!u.isDeleted()) {
				activeUsers.add(u);
			}
		}

		return userMapper.entitiesToResponseDtos(activeUsers);
	}

	@Override
	public ContextDto getContextById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Create the ContextDto
		ContextDto context = new ContextDto();
		context.setTarget(tweetMapper.entityToResponseDto(tweetToCheck.get()));

		// Find the previous replies by using the inReplyTo property
		List<Tweet> before = new ArrayList<Tweet>();
		Tweet prevTweet = tweetToCheck.get().getInReplyTo();
		while (prevTweet != null) {
			before.add(prevTweet);
			prevTweet = prevTweet.getInReplyTo();
		}

		// Order chronologically
		before = chronoSort(before);

		// Find the replies stemming from the target
		List<Tweet> after = new ArrayList<Tweet>();
		List<Tweet> checkTweets = new ArrayList<Tweet>();
		checkTweets.add(tweetToCheck.get());

		for (int i = 0; i < checkTweets.size(); i++) {
			Tweet nextTweet = checkTweets.get(i);
			List<Tweet> replies = tweetRepository.findByinReplyTo(nextTweet);
			for (Tweet tweet : replies) {
				if (!tweet.isDeleted()) {
					// Only add the tweet to after if it's not deleted
					after.add(tweet);
				}
				// Either way, add tweet to the list so we can search for other tweets in the
				// outer loop
				checkTweets.add(tweet);
			}
		}

		// Order chronologically
		after = chronoSort(after);

		// Set ContextDto fields
		context.setBefore(tweetMapper.entitiesToResponseDtos(before));
		context.setAfter(tweetMapper.entitiesToResponseDtos(after));

		return context;
	}

	@Override
	public List<TweetResponseDto> getRepliesById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Find direct reply tweets based on inReplyTo property, and make sure they
		// weren't deleted
		List<Tweet> replies = tweetRepository.findByinReplyToAndInReplyTo_DeletedFalse(tweetToCheck.get());

		return tweetMapper.entitiesToResponseDtos(replies);
	}

	@Override
	public List<TweetResponseDto> getRepostsById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Find direct repost tweets based on repostOf property, and make sure they
		// weren't deleted
		List<Tweet> reposts = tweetRepository.findByRepostOfAndRepostOf_DeletedFalse(tweetToCheck.get());

		return tweetMapper.entitiesToResponseDtos(reposts);
	}

	@Override
	public List<UserResponseDto> getMentionsById(Long id) {
		// Check if the tweet exists or has been deleted
		Optional<Tweet> tweetToCheck = tweetRepository.findByIdAndDeletedFalse(id);
		if (tweetToCheck.isEmpty()) {
			throw new NotFoundException("GET: A tweet with id " + id + " was not found.");
		}

		// Find users mentioned in the tweet and exclude deleted users from the final
		// list
		List<User> usersMentioned = tweetToCheck.get().getUsersMentioned();
		List<User> activeUsers = new ArrayList<User>();
		for (User user : usersMentioned) {
			if (!user.isDeleted()) {
				activeUsers.add(user);
			}
		}

		return userMapper.entitiesToResponseDtos(activeUsers);
	}

}
