package kr.co.leehana.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Hana Lee on 2015-10-12 오전 11:23
 *
 * @author {@link "mailto:i@leehana.co.kr" "Hana Lee"}
 * @since 2015-10-12 오전 11:23
 */
@Entity
@EqualsAndHashCode(of = {"username"})
@ToString
@NoArgsConstructor
public class User implements Serializable {

	private static final long serialVersionUID = -2575067027478784346L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	private long id;

	@Column(unique = true)
	@Getter
	@Setter
	private String username;

	@JsonIgnore
	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private String fullName;

	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date joined;

	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date updated;

	@Getter
	private boolean admin;
}
