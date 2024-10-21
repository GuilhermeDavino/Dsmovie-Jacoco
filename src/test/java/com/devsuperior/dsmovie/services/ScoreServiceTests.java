package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;
import com.devsuperior.dsmovie.tests.UserFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private ScoreRepository repository;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private UserService userService;
	
	private UserEntity user;
	private MovieEntity movie;
	private Long existingId, nonExistingId;
	private ScoreDTO dto;
	private ScoreEntity score;
	
	@BeforeEach
	void setUp() throws Exception {
		user = UserFactory.createUserEntity();
		movie = MovieFactory.createMovieEntity();
		existingId = 1L;
		nonExistingId = 2L;
		dto = ScoreFactory.createScoreDTO();
		score = ScoreFactory.createScoreEntity();
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		Mockito.when(movieRepository.findById(existingId)).thenReturn(Optional.of(movie));
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).findById(nonExistingId);
		Mockito.when(repository.saveAndFlush(score)).thenReturn(score);
		Mockito.when(movieRepository.save(movie)).thenReturn(movie);
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).findById(nonExistingId);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {
		MovieDTO result = service.saveScore(dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movie.getId());
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		dto = new ScoreDTO(nonExistingId, 4.5);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			MovieDTO result = service.saveScore(dto);
		});
	}
}
