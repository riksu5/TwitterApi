package com.cooksys.twitter.mappers;

import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.dtos.ProfileDto;
import com.cooksys.twitter.dtos.UserRequestDto;
import com.cooksys.twitter.dtos.UserResponseDto;
import com.cooksys.twitter.entities.Credentials;
import com.cooksys.twitter.entities.Profile;
import com.cooksys.twitter.entities.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T13:32:07-0500",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
*/
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User requestDtoToEntity(UserRequestDto userRequestDto) {
        if ( userRequestDto == null ) {
            return null;
        }

        User user = new User();

        user.setCredentials( credentialsDtoToCredentials( userRequestDto.getCredentials() ) );
        user.setProfile( profileDtoToProfile( userRequestDto.getProfile() ) );

        return user;
    }

    @Override
    public List<User> requestDtosToEntities(List<UserRequestDto> userRequestDtos) {
        if ( userRequestDtos == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>( userRequestDtos.size() );
        for ( UserRequestDto userRequestDto : userRequestDtos ) {
            list.add( requestDtoToEntity( userRequestDto ) );
        }

        return list;
    }

    @Override
    public UserResponseDto entityToResponseDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setJoined( user.getJoined() );
        userResponseDto.setProfile( profileToProfileDto( user.getProfile() ) );
        String username = userCredentialsUsername( user );
        if ( username != null ) {
            userResponseDto.setUsername( username );
        }

        return userResponseDto;
    }

    @Override
    public List<UserResponseDto> entitiesToResponseDtos(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponseDto> list = new ArrayList<UserResponseDto>( users.size() );
        for ( User user : users ) {
            list.add( entityToResponseDto( user ) );
        }

        return list;
    }

    protected Credentials credentialsDtoToCredentials(CredentialsDto credentialsDto) {
        if ( credentialsDto == null ) {
            return null;
        }

        Credentials credentials = new Credentials();

        credentials.setUsername( credentialsDto.getUsername() );
        credentials.setPassword( credentialsDto.getPassword() );

        return credentials;
    }

    protected Profile profileDtoToProfile(ProfileDto profileDto) {
        if ( profileDto == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setFirstName( profileDto.getFirstName() );
        profile.setLastName( profileDto.getLastName() );
        profile.setEmail( profileDto.getEmail() );
        profile.setPhone( profileDto.getPhone() );

        return profile;
    }

    protected ProfileDto profileToProfileDto(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();

        profileDto.setFirstName( profile.getFirstName() );
        profileDto.setLastName( profile.getLastName() );
        profileDto.setEmail( profile.getEmail() );
        profileDto.setPhone( profile.getPhone() );

        return profileDto;
    }

    private String userCredentialsUsername(User user) {
        if ( user == null ) {
            return null;
        }
        Credentials credentials = user.getCredentials();
        if ( credentials == null ) {
            return null;
        }
        String username = credentials.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
