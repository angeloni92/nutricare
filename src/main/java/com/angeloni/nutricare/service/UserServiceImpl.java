package com.angeloni.nutricare.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.CommonResponseDto;
import com.angeloni.nutricare.dto.LoginRequestDto;
import com.angeloni.nutricare.dto.LoginResponseDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.exception.EmailException;
import com.angeloni.nutricare.exception.InvalidCredentialsException;
import com.angeloni.nutricare.exception.NotFoundException;
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
    
    @Autowired
    private EmailService emailService;
    
    @Value("${server.host}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * Registers a new user in the system.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Checks if the username or email is already in use. If so, returns the appropriate error message.</li>
     *   <li>Encodes the provided password and updates the {@link UserDto} with the encoded value.</li>
     *   <li>Maps the {@link UserDto} to a {@link UserEntity} and sets a unique confirmation token.</li>
     *   <li>Saves the user entity to the database.</li>
     *   <li>Sends a confirmation email to the user's email address.</li>
     * </ol>
     * 
     * @param userDto the DTO containing user information, including username, email, and password.
     * @return a string indicating the result of the registration:
     *         <ul>
     *           <li>{@link UserService#USERNAME_ALREADY_EXISTS} if the username is already taken.</li>
     *           <li>{@link UserService#EMAIL_ALREADY_EXISTS} if the email is already in use.</li>
     *           <li>{@link UserService#CONFIRM_REGISTRATION} if the registration is successful and the confirmation email is sent.</li>
     *         </ul>
     * 
     * @throws IllegalArgumentException if the provided {@link UserDto} is null or contains invalid data.
     * @see UserDto
     * @see UserEntity
     */
	@Override
	@Transactional
	public CommonResponseDto registerUser(UserDto userDto) {
		log.info(String.format("START register user with username: [%s]", userDto.getUsername()));
		CommonResponseDto authResponseDto = new CommonResponseDto();
		Boolean isUsernamePresent = userRepository.findByUsername(userDto.getUsername()).isPresent();
		Boolean isEmailPresent = userRepository.findByEmail(userDto.getEmail()).isPresent();
        if (isUsernamePresent || isEmailPresent) {
        	authResponseDto.setStatus(UserService.ERROR_STATUS);
        	authResponseDto.setMessage(isUsernamePresent ? UserService.USERNAME_ALREADY_EXISTS : UserService.EMAIL_ALREADY_EXISTS);
            return authResponseDto;
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        user.setConfirmationToken(UUID.randomUUID().toString());;
        userRepository.save(user);
        log.info(String.format("SEND email to confirm user with username: [%s]", userDto.getUsername()));
        sendConfirmationEmail(user);
        authResponseDto.setStatus(UserService.SUCCESS_STATUS);
        authResponseDto.setMessage(UserService.CONFIRM_REGISTRATION);
        return authResponseDto;
	}

	/**
	 * Authenticates a user and generates a JWT token upon successful login.
	 *
	 * @param loginDto {@link LoginRequestDto} containing the user's login credentials (username/email and password)
	 * @return a {@link String} containing the JWT token if authentication is successful
	 * @throws {@link InvalidCredentialsException} if the provided credentials are invalid
	 *
	 * This method validates the user's login credentials using the {@code authenticate} method.
	 * If authenticated, it generates a JWT token using {@code jwtTokenUtil.generateToken}.
	 * If authentication fails, it throws an {@link InvalidCredentialsException} with an appropriate error message.
	 */
	@Override
	public LoginResponseDto loginUser(LoginRequestDto loginDto) {
		log.info(String.format("START login user with username or email: [%s]", loginDto.getUsername()));
		LoginResponseDto loginResponseDto = new LoginResponseDto();
		String token = null;
		Boolean isAuthenticated = authenticate(loginDto.getUsername(), loginDto.getPassword());
		if(isAuthenticated) {
			token = jwtTokenUtil.generateToken(loginDto.getUsername());
		}else {
			throw new InvalidCredentialsException(UserService.INVALID_CREDENTIAL_ERROR_MESSAGE);
		}
		log.info(String.format("Username or email: [%s] succesfully logged", loginDto.getUsername()));
		loginResponseDto.setStatus(UserService.SUCCESS_STATUS);
		loginResponseDto.setToken(UserService.BEARER + token);
		return loginResponseDto;
	}
	
	/**
	 * Confirms a user's email address based on a confirmation token.
	 * <p>
	 * This method performs the following steps:
	 * <ol>
	 *   <li>Logs the start of the email confirmation process.</li>
	 *   <li>Retrieves the user associated with the provided confirmation token.</li>
	 *   <li>Throws a {@link NotFoundException} if the token is invalid or not associated with any user.</li>
	 *   <li>Checks if the email is already confirmed. If so, returns a specific message indicating this.</li>
	 *   <li>Updates the user's email confirmation status if it was not already confirmed.</li>
	 *   <li>Logs the successful confirmation of the email.</li>
	 * </ol>
	 * 
	 * @param token the unique confirmation token associated with the user's account.
	 * @return a string indicating the result of the confirmation process:
	 *         <ul>
	 *           <li>{@link UserService#INVALID_CONFERMATION_TOKEN} if the token is invalid or not found.</li>
	 *           <li>{@link UserService#EMAIL_IS_ALREADY_CONFIRMED} if the email was already confirmed previously.</li>
	 *           <li>{@link UserService#EMAIL_SUCCESSFULLY_CONFIRMED} if the email confirmation process completes successfully.</li>
	 *         </ul>
	 * 
	 * @throws NotFoundException if the confirmation token is invalid or not associated with any user.
	 * @see UserService
	 * @see UserEntity
	 */
	@Override
	@Transactional
	public String confirmEmail(String token) {
		log.info("START confirm email");
	    UserEntity user = userRepository.findByConfirmationToken(token).orElseThrow(() -> new NotFoundException(UserService.INVALID_CONFERMATION_TOKEN));	    
	    if (user.getEmailConfirmed()) {
	        return UserService.EMAIL_IS_ALREADY_CONFIRMED;
	    }
	    updateUserConfirmed(user);
	    log.info("EMAIL confirmed");
	    return UserService.EMAIL_SUCCESSFULLY_CONFIRMED;
	}
	
	/**
	 * Updates a user's status to confirm their email address.
	 * <p>
	 * This method performs the following actions:
	 * <ol>
	 *   <li>Sets the user's {@code emailConfirmed} property to {@link Boolean#TRUE}.</li>
	 *   <li>Clears the user's {@code confirmationToken} by setting it to {@code null}.</li>
	 *   <li>Saves the updated user entity to the repository.</li>
	 * </ol>
	 * 
	 * @param user the {@link UserEntity} whose email confirmation status needs to be updated.
	 *             Must not be {@code null}.
	 * 
	 * @throws IllegalArgumentException if the provided {@link UserEntity} is {@code null}.
	 * 
	 * @see UserEntity
	 * @see UserRepository
	 */
	private void updateUserConfirmed(UserEntity user) {
		user.setEmailConfirmed(Boolean.TRUE);
	    user.setConfirmationToken(null);
	    userRepository.save(user);
	}
	
	/**
	 * Authenticates a user by validating the provided login (username or email) and password.
	 *
	 * @param username {@link String} the user's login credential, username
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
	private Boolean authenticate(String username, String password) {
       return userRepository.findByUsernameAndEmailConfirmedTrue(username).map(u -> passwordEncoder.matches(password, u.getPassword())).orElse(Boolean.FALSE);
	}
	
	/**
	 * Sends a confirmation email to the specified user.
	 * <p>
	 * This method generates a confirmation link using the user's confirmation token, 
	 * and sends an email to the user containing the link. If an error occurs while sending the email,
	 * the user record is deleted from the repository, and an {@link EmailException} is thrown.
	 * 
	 * @param userTo the {@link UserEntity} representing the user to whom the confirmation email will be sent.
	 *               Must not be {@code null}.
	 * 
	 * @throws IllegalArgumentException if the provided {@link UserEntity} is {@code null}.
	 * @throws EmailException if there is an error while sending the email.
	 * 
	 * @see UserEntity
	 * @see EmailService
	 * @see UserRepository
	 */
	private void sendConfirmationEmail(UserEntity userTo) {
		 String confirmationLink = generateConfirmationLink(userTo.getConfirmationToken());
		 try {
	            emailService.sendEmail(
	                userTo.getEmail(),
	                UserService.EMAIL_CONFIRM_REGISTRATION_SUBJECT,
	                String.format(UserService.CONFIRM_REGISTRATION_EMAIL_FORMAT, confirmationLink)
	            );
	        } catch (Exception e) {
	        	log.error(String.format("Error sending email: [%s]", e.getMessage()));
	            userRepository.delete(userTo); ;
	            throw new EmailException(UserService.ERROR_SENDING_EMAIL_MSG);
	        }
	}
	
	/**
	 * Generates a confirmation link using the provided confirmation token.
	 * <p>
	 * This method constructs a URL by formatting a predefined template with the server's host, port, 
	 * context path, and the user's confirmation token.
	 * 
	 * @param confirmationToken the unique token associated with the user's email confirmation process. 
	 *                          Must not be {@code null} or empty.
	 * 
	 * @return a {@link String} representing the complete confirmation link.
	 * 
	 * @throws IllegalArgumentException if the {@code confirmationToken} is {@code null} or empty.
	 * 
	 * @see UserService#CONFIRMATION_LINK_FORMAT
	 */
    private String generateConfirmationLink(String confirmationToken) {
        return String.format(UserService.CONFIRMATION_LINK_FORMAT, serverHost, serverPort, contextPath, confirmationToken);
    }

}
