package com.cooksys.twitter.mappers;

import com.cooksys.twitter.dtos.TweetRequestDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.entities.Tweet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T13:32:07-0500",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
*/
@Component
public class TweetMapperImpl implements TweetMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Tweet requestDtoToEntity(TweetRequestDto tweetRequestDto) {
        if ( tweetRequestDto == null ) {
            return null;
        }

        Tweet tweet = new Tweet();

        tweet.setContent( tweetRequestDto.getContent() );

        return tweet;
    }

    @Override
    public List<Tweet> requestDtosToEntities(List<TweetRequestDto> tweetRequestDtos) {
        if ( tweetRequestDtos == null ) {
            return null;
        }

        List<Tweet> list = new ArrayList<Tweet>( tweetRequestDtos.size() );
        for ( TweetRequestDto tweetRequestDto : tweetRequestDtos ) {
            list.add( requestDtoToEntity( tweetRequestDto ) );
        }

        return list;
    }

    @Override
    public TweetResponseDto entityToResponseDto(Tweet tweet) {
        if ( tweet == null ) {
            return null;
        }

        TweetResponseDto tweetResponseDto = new TweetResponseDto();

        tweetResponseDto.setAuthor( userMapper.entityToResponseDto( tweet.getAuthor() ) );
        tweetResponseDto.setId( tweet.getId() );
        tweetResponseDto.setPosted( tweet.getPosted() );
        tweetResponseDto.setContent( tweet.getContent() );
        tweetResponseDto.setInReplyTo( entityToResponseDto( tweet.getInReplyTo() ) );
        tweetResponseDto.setRepostOf( entityToResponseDto( tweet.getRepostOf() ) );

        return tweetResponseDto;
    }

    @Override
    public List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets) {
        if ( tweets == null ) {
            return null;
        }

        List<TweetResponseDto> list = new ArrayList<TweetResponseDto>( tweets.size() );
        for ( Tweet tweet : tweets ) {
            list.add( entityToResponseDto( tweet ) );
        }

        return list;
    }
}
