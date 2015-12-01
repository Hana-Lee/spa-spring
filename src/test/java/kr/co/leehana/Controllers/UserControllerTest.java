package kr.co.leehana.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.leehana.App;
import kr.co.leehana.models.User;
import kr.co.leehana.models.UserDto;
import kr.co.leehana.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Hana Lee on 2015-10-13 오후 3:52
 *
 * @author {@link "mailto:i@leehana.co.kr" "Hana Lee"}
 * @since 2015-10-13 오후 3:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@Transactional
public class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private Filter springSecurityFilterChain;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilter(springSecurityFilterChain)
				.build();
	}

	@Test
	public void createAccount() throws Exception {
		UserDto.Create createDto = userCreateDtoFixture();

		ResultActions resultActions = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isCreated());
		//{"id":1,"username":"voyaging","email":null,"fullName":null,"joined":1444821003172,"updated":1444821003172}
		resultActions.andExpect(jsonPath("$.username", is("voyaging")));

		resultActions = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON).content(objectMapper
				.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
		//{"message":"[voyaging] 중복된 username 입니다.","errorCode":"duplicated.username.exception","errors":null}
		resultActions.andExpect(jsonPath("$.errorCode", is("duplicated.username.exception")));
	}

	@Test
	public void createAccount_BadRequest() throws Exception {
		UserDto.Create createDto = new UserDto.Create();
		createDto.setUsername(" ");
		createDto.setPassword("1234");

		ResultActions resultActions = mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());
	}

	@Test
	public void getAccounts() throws Exception {
		UserDto.Create createDto = userCreateDtoFixture();

		userService.createUser(createDto);

		ResultActions resultGetActions = mockMvc.perform(get("/accounts")
				.with(httpBasic(createDto.getUsername(), createDto.getPassword())));

		// {"content":[{"id":1,"username":"voyaging","fullName":null,"email":null,"joined":1444836037938,
		// "updated":1444836037938}],"totalElements":1,"totalPages":1,"last":true,"number":0,"size":20,"sort":null,
		// "numberOfElements":1,"first":true}
		resultGetActions.andDo(print());
		resultGetActions.andExpect(status().isOk());
	}

	private UserDto.Create userCreateDtoFixture() {
		UserDto.Create createDto = new UserDto.Create();
		createDto.setUsername("voyaging");
		createDto.setPassword("password");
		return createDto;
	}

	@Test
	public void getAccount() throws Exception {
		UserDto.Create createDto = userCreateDtoFixture();
		User user = userService.createUser(createDto);
		ResultActions resultActions = mockMvc.perform(get("/accounts/" + user.getId())
				.with(httpBasic(createDto.getUsername(), createDto.getPassword())));
		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
	}

	@Test
	public void updateAccount() throws Exception {
		UserDto.Create createDto = userCreateDtoFixture();
		User user = userService.createUser(createDto);

		UserDto.Update updateDto = new UserDto.Update();
		updateDto.setFullName("Hana Lee");
		updateDto.setPassword("pass");

		ResultActions resultActions = mockMvc.perform(put("/accounts/" + user.getId())
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto))
				.with(httpBasic(createDto.getUsername(), createDto.getPassword())));

		resultActions.andDo(print());
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.fullName", is("Hana Lee")));

		// 응답으로 password 를 넘겨주지 않기 때문에 테스트가 불가.
		// resultActions.andExpect(jsonPath("$.password", is("pass")));
	}

	@Test
	public void deleteAccount() throws Exception {
		UserDto.Create createDto = userCreateDtoFixture();
		User user = userService.createUser(createDto);

		final String username = createDto.getUsername();
		final String password = createDto.getPassword();

		ResultActions resultActions = mockMvc.perform(delete("/accounts/2")
				.with(httpBasic(username, password)));

		resultActions.andDo(print());
		resultActions.andExpect(status().isBadRequest());

		resultActions = mockMvc.perform(delete("/accounts/" + user.getId())
				.with(httpBasic(username, password)));
		resultActions.andDo(print());
		resultActions.andExpect(status().isNoContent());
	}
}