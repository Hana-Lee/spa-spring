package kr.co.leehana.controllers;

import kr.co.leehana.commons.ErrorResponse;
import kr.co.leehana.exceptions.UserNotFoundException;
import kr.co.leehana.exceptions.UserDuplicatedException;
import kr.co.leehana.models.User;
import kr.co.leehana.models.UserDto;
import kr.co.leehana.repositories.UserRepository;
import kr.co.leehana.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Hana Lee on 2015-10-12 오전 11:27
 *
 * @author {@link "mailto:i@leehana.co.kr" "Hana Lee"}
 * @since 2015-10-12 오전 11:27
 */
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;

	@RequestMapping(value = "/users", method = POST)
	public ResponseEntity createUser(@RequestBody @Valid UserDto.Create create, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setErrorCode("bad.request");
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		User newUser = userService.createUser(create);
		return new ResponseEntity<>(newUser, HttpStatus.CREATED);
//		return new ResponseEntity<>(modelMapper.map(newUser, UserDto.Response.class), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/users", method = GET)
	@ResponseStatus(value = HttpStatus.OK)
	public PageImpl<UserDto.Response> getUsers(Pageable pageable) {
		Page<User> pages = userRepository.findAll(pageable);
		List<UserDto.Response> content = pages.getContent().parallelStream().map(user -> modelMapper.map
				(user, UserDto.Response.class)).collect(Collectors.toList());
		return new PageImpl<>(content, pageable, pages.getTotalElements());
	}

	@RequestMapping(value = "/users/{id}", method = GET)
	@ResponseStatus(HttpStatus.OK)
	public UserDto.Response getUser(@PathVariable Long id) {
		return modelMapper.map(userService.getUser(id), UserDto.Response.class);
	}

	// 전체 업데이트 (전체 필드 업데이트 PUT)
	// 부분 업데이트(username or username, password or username, fullName : PATCH)
	@RequestMapping(value = "/users/{id}", method = PUT)
	public ResponseEntity updateUser(@PathVariable Long id, @RequestBody @Valid UserDto.Update updateDto,
	                                 BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		User updatedUser = userService.updateUser(id, updateDto);
		return new ResponseEntity<>(modelMapper.map(updatedUser, UserDto.Response.class), HttpStatus.OK);
	}

	@RequestMapping(value = "/users/{id}", method = DELETE)
	public ResponseEntity deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(UserDuplicatedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUserDuplicatedException(UserDuplicatedException ex) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + ex.getUsername() + "] 중복된 username 입니다.");
		errorResponse.setErrorCode("duplicated.username.exception");
		return errorResponse;
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[" + e.getId() + "] 에 해당하는 계정이 없습니다.");
		errorResponse.setErrorCode("user.not.found.exception");
		return errorResponse;
	}
}
