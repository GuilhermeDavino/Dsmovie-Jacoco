package com.devsuperior.dsmovie.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	private UserEntity user;
	
	@Mock
	private CustomUserUtil userUtil;
	
	private String existingUsername, nonExistingUsername;
	
	private List<UserDetailsProjection> list;
	
	@BeforeEach
	void setUp() throws Exception {
		user = UserFactory.createUserEntity();
		existingUsername = "maria@gmail.com";
		nonExistingUsername = "user@gmail.com";
		list = UserDetailsFactory.createCustomAdminClientUser(existingUsername);
		
	
		
		Mockito.when(repository.findByUsername(existingUsername)).thenReturn(Optional.of(user));
		Mockito.doThrow(UsernameNotFoundException.class).when(repository).findByUsername(nonExistingUsername);
		Mockito.when(repository.searchUserAndRolesByUsername(existingUsername)).thenReturn(list);
		Mockito.when(repository.searchUserAndRolesByUsername(nonExistingUsername)).thenReturn(new ArrayList<>());
	}
	
	

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);
		UserEntity result = service.authenticated();
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), user.getId());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(nonExistingUsername);
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			UserEntity result = service.authenticated();
		});
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		UserDetails result = service.loadUserByUsername(existingUsername);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), existingUsername);
	}

	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			@SuppressWarnings("unused")
			UserDetails result = service.loadUserByUsername(nonExistingUsername);
		});
	}
}
