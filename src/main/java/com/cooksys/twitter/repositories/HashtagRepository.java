package com.cooksys.twitter.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cooksys.twitter.entities.Hashtag;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

//	List<Hashtag> findAllByDeletedFalse();
	
//	List<Hashtag> findByLabelAndDeletedFalse();
	
	Optional<Hashtag> findByLabel(String label);
	
//	List<Hashtag> findAllByLabel(String label);

	List<Hashtag> findAll();

	Optional<Hashtag> findByLabelIgnoreCase(String tag);
}
