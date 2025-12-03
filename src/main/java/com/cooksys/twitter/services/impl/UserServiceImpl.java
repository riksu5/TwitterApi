package com.cooksys.twitter.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.dtos.ProfileDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.dtos.UserRequestDto;
import com.cooksys.twitter.dtos.UserResponseDto;
import com.cooksys.twitter.entities.Credentials;
import com.cooksys.twitter.entities.Profile;
import com.cooksys.twitter.entities.Tweet;
import com.cooksys.twitter.entities.User;
import com.cooksys.twitter.exceptions.BadRequestException;
import com.cooksys.twitter.exceptions.NotAuthorizedException;
import com.cooksys.twitter.exceptions.NotFoundException;
import com.cooksys.twitter.mappers.CredentialsMapper;
import com.cooksys.twitter.mappers.ProfileMapper;
import com.cooksys.twitter.mappers.TweetMapper;
import com.cooksys.twitter.mappers.UserMapper;
import com.cooksys.twitter.repositories.UserRepository;
import com.cooksys.twitter.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;

//	private final TweetRepository tweetRepository;
	private final TweetMapper tweetMapper;

	private final ProfileMapper profileMapper;

	private final CredentialsMapper credentialsMapper;

	private void validateUserRequest(UserRequestDto userRequestDto) {
		// Credentials are fully required, and only email is mandatory from the profile
		if (userRequestDto.getCredentials() == null || userRequestDto.getCredentials().getUsername() == null
				|| userRequestDto.getCredentials().getPassword() == null || userRequestDto.getProfile() == null
				|| userRequestDto.getProfile().getEmail() == null) {
			// Throw a BadRequest error
			throw new BadRequestException(
					"The credentials (username and password) and profile email are required fields");
		}
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

	private Optional<User> userNotDeletedAndCredentialsMatch(String username, CredentialsDto credentialsDto) {
		Optional<User> checkUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (checkUser.isEmpty()) {
			throw new NotFoundException("The username " + username + " was not found or is deleted.");
		}

		// Check if credentials match
		if (!credentialsMatch(credentialsDto, checkUser.get().getCredentials())) {
			// If no match, throw error
			throw new NotAuthorizedException("Credentials don't match for username " + username + ".");
		}

		// If the user is not deleted and credentials match, then return the user
		return checkUser;
	}

	private boolean followableUserCheck(String username, CredentialsDto credentialsDto) {
		// Check if the user we want to follow exists as well as the user that wants to
		// do the following
		Optional<User> checkUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		Optional<User> findUser = userRepository.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername());
		if (checkUser.isPresent() && findUser.isPresent()
				&& findUser.get().getCredentials().equals(credentialsMapper.dtoToEntity(credentialsDto))) {
			// If the followable user exists, and we were provided valid creds, return true
			return true;
		}
		return false;
	}

	@Override
	public List<UserResponseDto> getAllUsers() {
		// Get all non-deleted users and map to responseDtos
		return userMapper.entitiesToResponseDtos(userRepository.findAllByDeletedFalse());
	}

	@Override
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		// Check that the request was sent properly
		validateUserRequest(userRequestDto);

		// Check if the username is available
		Optional<User> checkUser = userRepository
				.findByCredentialsUsername(userRequestDto.getCredentials().getUsername());
		if (checkUser.isPresent()) {
			User userExists = checkUser.get();
//			if (userExists.isDeleted() && userExists.getCredentials().getPassword() == userRequestDto.getCredentials().getPassword()) {
			if (userExists.isDeleted()
					&& credentialsMatch(userRequestDto.getCredentials(), userExists.getCredentials())) {
				// If the user credentials match that of a previously deleted user, just
				// reactivate that user
				userExists.setDeleted(false);
				// Set the profile up based on the passed in info ???
				userExists.setProfile(profileMapper.dtoToEntity(userRequestDto.getProfile()));
				return userMapper.entityToResponseDto(userRepository.saveAndFlush(userExists));
			} else {
				// If the username found is deleted and the creds don't match, just error
				throw new BadRequestException("POST: The username " + userRequestDto.getCredentials().getUsername()
						+ " has already been taken.");
			}
		}

		// If the username is available (not found), then create a new User
		User newUser = userMapper.requestDtoToEntity(userRequestDto);
		return userMapper.entityToResponseDto(userRepository.saveAndFlush(newUser));
	}

	@Override
	public UserResponseDto getUserByUsername(String username) {
		Optional<User> checkUser = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (checkUser.isEmpty()) {
			// If the user doesn't exist, throw an error
			throw new NotFoundException("GET: There is no user with the username " + username);
		}

		// Otherwise, return the user
		return userMapper.entityToResponseDto(checkUser.get());
	}

	@Override
	public UserResponseDto updateUser(String username, UserRequestDto userRequestDto) {
		// Check if the user exists and is not deleted, and if the credentials match
		Optional<User> checkUser = userNotDeletedAndCredentialsMatch(username, userRequestDto.getCredentials());
		if (checkUser.isEmpty()) {
			throw new NotFoundException("PATCH: The username " + username + " was invalid.");
		}

		// If matching creds, then update info
		User userExists = checkUser.get();
//		userExists.setProfile(profileMapper.dtoToEntity(userRequestDto.getProfile()));
		// Update only the changed fields (don't overwrite email with null)
		ProfileDto changes = userRequestDto.getProfile();
		Profile userProfile = userExists.getProfile();
		// If it's not null, it could still be empty, so check every field
		if (changes != null) {
			if (changes.getFirstName() != null) {
				userProfile.setFirstName(changes.getFirstName());
			}

			if (changes.getLastName() != null) {
				userProfile.setLastName(changes.getLastName());
			}

			if (changes.getEmail() != null) {
				userProfile.setEmail(changes.getEmail());
			}

			if (changes.getPhone() != null) {
				userProfile.setPhone(changes.getPhone());
			}
		} else {
			throw new BadRequestException("POST: No profile was provided");
		}

		return userMapper.entityToResponseDto(userRepository.saveAndFlush(userExists));
	}

	@Override
	public UserResponseDto deleteUser(String username, CredentialsDto credentialsDto) {
		// Check if the user exists and is not deleted, and if the credentials match
		Optional<User> checkUser = userNotDeletedAndCredentialsMatch(username, credentialsDto);
		if (checkUser.isEmpty()) {
			throw new NotFoundException("PATCH: The username " + username + " was invalid.");
		}

		// If the user exists, then delete it
		User userExists = checkUser.get();
		userExists.setDeleted(true);
		return userMapper.entityToResponseDto(userRepository.saveAndFlush(userExists));
	}

	@Override
	public void followUser(String username, CredentialsDto credentialsDto) {
		// Check if the username is for a non-deleted user, and the creds are for a
		// non-deleted user
		if (!followableUserCheck(username, credentialsDto)) {
			// If not, throw error
			throw new BadRequestException(
					"POST: There is a problem with the username " + username + " or the credentials " + credentialsDto);
		}

		// Otherwise, create the follow/following relationship
		// First get the identities that we need to add to the respective lists
		User userToFollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username).get();
		User userThatFollows = userRepository.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername())
				.get();
		// Get the following list for the user that wants to follow username and add
		// username to the list
		List<User> following = userThatFollows.getFollowing();
		// Check if the userToFollow is already on the following list
		if (following.contains(userToFollow)) {
			throw new BadRequestException("The user " + username + "is already followed");
		}
		following.add(userToFollow);
		userThatFollows.setFollowing(following);
		userRepository.saveAndFlush(userThatFollows);
		// Get the followers list for the user that is being followed, and add the
		// credentials user
		List<User> followers = userToFollow.getFollowers();
		// Check if the userThatFollows is already on the follower list
		if (followers.contains(userThatFollows)) {
			throw new BadRequestException("You do already follow the user " + username);
		}
		followers.add(userThatFollows);
		userToFollow.setFollowers(followers);
		userRepository.saveAndFlush(userToFollow);
		return;
	}

	@Override
	public void unfollowUser(String username, CredentialsDto credentialsDto) {
		// Check if the username is for a non-deleted user, and the creds are for a
		// non-deleted user
		if (!followableUserCheck(username, credentialsDto)) {
			// If not, throw error
			throw new BadRequestException(
					"POST: There is a problem with the username " + username + " or the credentials " + credentialsDto);
		}

		// Otherwise, undo the follow/following relationship
		// First get the identities that we need to remove from the respective lists
		User userToUnfollow = userRepository.findByCredentialsUsernameAndDeletedFalse(username).get();
		User userThatUnfollows = userRepository.findByCredentialsUsernameAndDeletedFalse(credentialsDto.getUsername())
				.get();
		// Get the following list for the user that wants to unfollow username and
		// remove username from the list
		List<User> following = userThatUnfollows.getFollowing();
		// Check if the userToUnfollow is even on the following list
		if (!following.contains(userToUnfollow)) {
			throw new NotFoundException("The user " + username + "is already unfollowed");
		}
		following.remove(userToUnfollow);
		userThatUnfollows.setFollowing(following);
		// Get the followers list for the user that is being unfollowed, and remove the
		// credentials user
		List<User> followers = userToUnfollow.getFollowers();
		// Check if the userThatUnfollows is even on the follower list
		if (!followers.contains(userThatUnfollows)) {
			throw new NotFoundException("You do not follow the user " + username);
		}
		followers.remove(userThatUnfollows);
		userToUnfollow.setFollowers(followers);
		userRepository.saveAndFlush(userThatUnfollows);
		userRepository.saveAndFlush(userToUnfollow);
		return;
	}

	@Override
	public List<TweetResponseDto> getUserFeed(String username) {
		// Find out if the user is active
		Optional<User> userCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (userCheck.isEmpty()) {
			throw new NotFoundException("The user " + username + " was not found.");
		}

		// Otherwise, compile a list of the user's tweets
		User userExists = userCheck.get();
		List<Tweet> tweets = userExists.getTweets();
		// Add tweets by the user's following list
		List<User> following = userExists.getFollowing();
		for (User u : following) {
			tweets.addAll(u.getTweets());
		}

		// Get it in chronological order
		tweets.sort((Tweet t1, Tweet t2) -> t1.getPosted().compareTo(t2.getPosted()));
		// Reverse the list
		tweets = tweets.reversed();

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public List<TweetResponseDto> getUserTweets(String username) {
		// Find out if the user is active
		Optional<User> userCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (userCheck.isEmpty()) {
			throw new NotFoundException("The user " + username + " was not found.");
		}

		// Otherwise, compile a list of the user's tweets
		User userExists = userCheck.get();
		List<Tweet> tweets = userExists.getTweets();

		// Get it in chronological order
		tweets.sort((Tweet t1, Tweet t2) -> t1.getPosted().compareTo(t2.getPosted()));
		// Reverse the list (reverse chrono order)
		tweets = tweets.reversed();

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public List<TweetResponseDto> getUserMentions(String username) {
		// Find out if the user is active
		Optional<User> userCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (userCheck.isEmpty()) {
			throw new NotFoundException("The user " + username + " was not found.");
		}

		// Otherwise, compile a list of the tweets the user is mentioned in
		User userExists = userCheck.get();
		List<Tweet> tweets = userExists.getTweetsUserIsMentionedIn();

		// Get it in chronological order
		tweets.sort((Tweet t1, Tweet t2) -> t1.getPosted().compareTo(t2.getPosted()));
		// Reverse the list (reverse chrono order)
		tweets = tweets.reversed();

		return tweetMapper.entitiesToResponseDtos(tweets);
	}

	@Override
	public List<UserResponseDto> getUserFollowers(String username) {
		// Find out if the user is active
		Optional<User> userCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (userCheck.isEmpty()) {
			throw new NotFoundException("The user " + username + " was not found.");
		}

		// Otherwise, get the list of followers
		User userExists = userCheck.get();
		List<User> followers = userExists.getFollowers();

		// Only keep track of the users that are still active
		List<User> activeFollowers = new ArrayList<User>();
		for (User u : followers) {
			if (!u.isDeleted()) {
				activeFollowers.add(u);
			}
		}

		return userMapper.entitiesToResponseDtos(activeFollowers);
	}

	@Override
	public List<UserResponseDto> getUserFollowing(String username) {
		// Find out if the user is active
		Optional<User> userCheck = userRepository.findByCredentialsUsernameAndDeletedFalse(username);
		if (userCheck.isEmpty()) {
			throw new NotFoundException("The user " + username + " was not found.");
		}

		// Otherwise, get the list of followers
		User userExists = userCheck.get();
		List<User> following = userExists.getFollowing();

		// Only keep track of the users that are still active
		List<User> activeFollowing = new ArrayList<User>();
		for (User u : following) {
			if (!u.isDeleted()) {
				activeFollowing.add(u);
			}
		}

		return userMapper.entitiesToResponseDtos(activeFollowing);
	}

}
