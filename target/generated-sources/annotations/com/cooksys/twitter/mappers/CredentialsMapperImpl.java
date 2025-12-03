package com.cooksys.twitter.mappers;

import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.entities.Credentials;
import org.springframework.stereotype.Component;

/*
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-21T13:32:07-0500",
    comments = "version: 1.2.0.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
*/
@Component
public class CredentialsMapperImpl implements CredentialsMapper {

    @Override
    public Credentials dtoToEntity(CredentialsDto credentialsDto) {
        if ( credentialsDto == null ) {
            return null;
        }

        Credentials credentials = new Credentials();

        credentials.setUsername( credentialsDto.getUsername() );
        credentials.setPassword( credentialsDto.getPassword() );

        return credentials;
    }

    @Override
    public CredentialsDto entityToDto(Credentials credentials) {
        if ( credentials == null ) {
            return null;
        }

        CredentialsDto credentialsDto = new CredentialsDto();

        credentialsDto.setUsername( credentials.getUsername() );
        credentialsDto.setPassword( credentials.getPassword() );

        return credentialsDto;
    }
}
