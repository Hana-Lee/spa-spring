package kr.co.leehana.models;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Hana Lee on 2015-10-13 오후 3:30
 *
 * @author {@link "mailto:i@leehana.co.kr" "Hana Lee"}
 * @since 2015-10-13 오후 3:30
 */
public class UserDto {

	@Data
	public static class Create {
		@NotBlank
		@Size(min = 5)
		private String username;

		@NotBlank
		@Size(min = 5)
		private String password;
	}

	@Data
	public static class Response {
		private Long id;
		private String username;
		private String fullName;
		private String email;
		private Date joined;
		private Date updated;
	}

	@Data
	public static class Update {
		private String password;
		private String fullName;
	}
}
