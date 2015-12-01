package kr.co.leehana.services;

import kr.co.leehana.models.UserDto;
import kr.co.leehana.models.User;
import kr.co.leehana.repositories.UserRepository;
import kr.co.leehana.exceptions.UserNotFoundException;
import kr.co.leehana.exceptions.UserDuplicatedException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Hana Lee on 2015-10-13 오후 3:16
 *
 * @author {@link "mailto:i@leehana.co.kr" "Hana Lee"}
 * @since 2015-10-13 오후 3:16
 */
@Service
@Transactional
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public User createUser(UserDto.Create dto) {
		User user = modelMapper.map(dto, User.class);
		String username = dto.getUsername();
		if (userRepository.findByUsername(username) != null) {
			log.error("user duplicated exception. {}", username);
			throw new UserDuplicatedException(username);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		final Date now = new Date();
		user.setJoined(now);
		user.setUpdated(now);

		return userRepository.save(user);
	}

	public User updateUser(Long id, UserDto.Update updateDto) {
		User user = getUser(id);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setFullName(updateDto.getFullName());

		return userRepository.save(user);
	}

	public User getUser(Long id) {
		User user = userRepository.findOne(id);
		if (user == null) {
			throw new UserNotFoundException(id);
		}

		return user;
	}

	public void deleteUser(Long id) {
		userRepository.delete(getUser(id));
	}
}
