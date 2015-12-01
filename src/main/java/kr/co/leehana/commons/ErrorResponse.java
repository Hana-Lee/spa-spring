package kr.co.leehana.commons;

import lombok.Data;

import java.util.List;

/**
 * Created by Hana Lee on 2015-10-14 오후 7:33
 *
 * @author {@link "mailto:leehana@eyeq.co.kr" "Hana Lee"}
 * @since 2015-10-14 오후 7:33
 */
@Data
public class ErrorResponse {
	private String message;
	private String errorCode;
	private List<FieldError> errors;

	public static class FieldError {
		private String field;
		private String value;
		private String reason;
	}
}