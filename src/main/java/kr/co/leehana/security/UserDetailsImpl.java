package kr.co.leehana.security;

import kr.co.leehana.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Hana Lee on 2015-10-22 20:28
 *
 * @author Hana Lee
 * @since 2015-10-22 20:28
 */
public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {

	public UserDetailsImpl(User user) {
		super(user.getUsername(), user.getPassword(), authorities(user));
	}

	private static Collection<? extends GrantedAuthority> authorities(User user) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		if (user.isAdmin()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}
		return authorities;
	}
}
