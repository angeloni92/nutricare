package com.angeloni.nutricare.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.InvalidCredentialsException;
import com.angeloni.nutricare.repository.UserRepository;
import com.angeloni.nutricare.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;    

	@Autowired
    private JwtTokenUtil jwtTokenUtil; 
    
    @Autowired
    private ModelMapper modelMapper;

    /**
     * Registers a new user by validating the input, encoding the password, and saving the user details.
     *
     * @param userDto {@link UserDto} containing the user's registration details, such as username, email, and password
     * @return a {@link String} indicating the result of the registration process:
     *         <ul>
     *             <li>{@link UserService#USERNAME_ALREADY_EXISTS} if the username is already taken</li>
     *             <li>{@link UserService#EMAIL_ALREADY_EXISTS} if the email is already registered</li>
     *             <li>{@link UserService#REGISTRATION_SUCCESS} if the registration is successful</li>
     *         </ul>
     *
     * This method checks for the existence of the username and email in the database.
     * If valid, it encodes the password, maps the {@link UserDto} to a {@link UserEntity}, and saves the entity to the database.
     *
     * This method is annotated with {@link Transactional}, ensuring the operation is atomic and rolled back if any failure occurs.
     */
	@Override
	@Transactional
	public String registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return UserService.USERNAME_ALREADY_EXISTS;
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return UserService.EMAIL_ALREADY_EXISTS;
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        userRepository.save(user);
        return UserService.REGISTRATION_SUCCESS;
	}

	/**
	 * Authenticates a user and generates a JWT token upon successful login.
	 *
	 * @param loginDto {@link LoginDto} containing the user's login credentials (username/email and password)
	 * @return a {@link String} containing the JWT token if authentication is successful
	 * @throws {@link InvalidCredentialsException} if the provided credentials are invalid
	 *
	 * This method validates the user's login credentials using the {@code authenticate} method.
	 * If authenticated, it generates a JWT token using {@code jwtTokenUtil.generateToken}.
	 * If authentication fails, it throws an {@link InvalidCredentialsException} with an appropriate error message.
	 */
	@Override
	public String loginUser(LoginDto loginDto) {
		log.info("START login");
		String token = null;
		Boolean isAuthenticated = authenticate(loginDto.getLogin(), loginDto.getPassword());
		if(isAuthenticated) {
			token = jwtTokenUtil.generateToken(loginDto.getLogin());
		}else {
			throw new InvalidCredentialsException(UserService.INVALID_CREDENTIAL_ERROR_MESSAGE);
		}
		return token;
	}
	
	/**
	 * Authenticates a user by validating the provided login (username or email) and password.
	 *
	 * @param login {@link String} the user's login credential, which can be either a username or an email
	 * @param password {@link String} the raw password provided by the user
	 * @return a {@link Boolean} indicating whether the authentication is successful:
	 *         <ul>
	 *             <li>{@link Boolean#TRUE} if a user is found with the provided login and the password matches</li>
	 *             <li>{@link Boolean#FALSE} if the user is not found or the password does not match</li>
	 *         </ul>
	 *
	 * This method searches for a user in the database using the provided login (username or email).
	 * If a user is found, the raw password is validated against the encoded password stored in the database using {@link BCryptPasswordEncoder#matches}.
	 * If no user is found, or the password validation fails, it returns {@link Boolean#FALSE}.
	 */
	private Boolean authenticate(String login, String password) {
       return userRepository.findByUsernameOrEmail(login, login).map(u -> passwordEncoder.matches(password, u.getPassword())).orElse(Boolean.FALSE);
	}
}
