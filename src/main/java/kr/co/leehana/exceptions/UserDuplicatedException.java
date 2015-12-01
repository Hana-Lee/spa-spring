package kr.co.leehana.exceptions;

/**
 * Created by Hana Lee on 2015-10-14 오후 7:46
 *
 * @author {@link "mailto:leehana@eyeq.co.kr" "Hana Lee"}
 * @since 2015-10-14 오후 7:46
 */
public class UserDuplicatedException extends RuntimeException {

	private String username;

	public UserDuplicatedException(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
