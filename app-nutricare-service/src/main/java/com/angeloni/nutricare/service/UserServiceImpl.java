package com.angeloni.nutricare.service;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angeloni.nutricare.dto.LoginDto;
import com.angeloni.nutricare.dto.UserDto;
import com.angeloni.nutricare.entity.UserEntity;
import com.angeloni.nutricare.repository.UserRepository;
import com.angeloni.nutricare.util.JwtTokenUtil;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;    

	@Autowired
    private JwtTokenUtil jwtTokenUtil; 
    
    @Autowired
    private ModelMapper modelMapper;

	@Override
	@Transactional
	public String registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return UserService.USERNAME_ALREADY_EXISTS;
        }
        if (userRepository.findByEmail(userDto.getPassword()).isPresent()) {
            return UserService.EMAIL_ALREADY_EXISTS;
        }
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);
        UserEntity user = modelMapper.map(userDto, UserEntity.class);
        userRepository.save(user);
        return UserService.REGISTRATION_SUCCESS;
	}

	@Override
	public String loginUser(LoginDto loginDto) {
		Boolean isAuthenticated = authenticate(loginDto.getLogin(), loginDto.getPassword());
		if(isAuthenticated) {
			String token = jwtTokenUtil.generateToken(loginDto.getLogin());
			//TODO TORNO response entity con stato ok e body ("Bearer " + token)
		}
		//TODO TORNO ECCEZIONE PER 401 STATUS
		return null;
	}
	
	private Boolean authenticate(String login, String password) {
       return userRepository.findByUsernameOrEmail(login, login).map(u -> passwordEncoder.matches(password, u.getPassword())).orElse(Boolean.FALSE);
	}
}
