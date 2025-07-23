package guru.qa.niffler.data.mapper.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserEntityExtractor implements ResultSetExtractor<AuthUserEntity> {
	public static final AuthUserEntityExtractor instance = new AuthUserEntityExtractor();

	private AuthUserEntityExtractor() {
	}

	@Override
	public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
		UUID userId = null;

		while (rs.next()) {
			userId = rs.getObject("id", UUID.class);
			AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
				AuthUserEntity newUser = new AuthUserEntity();
				newUser.setId(id);
				try {
					newUser.setUsername(rs.getString("username"));
					newUser.setPassword(rs.getString("password"));
					newUser.setEnabled(rs.getBoolean("enabled"));
					newUser.setAccountNonExpired(rs.getBoolean("account_non_expired"));
					newUser.setAccountNonLocked(rs.getBoolean("account_non_locked"));
					newUser.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				return newUser;
			});

			AuthorityEntity authority = new AuthorityEntity();
			authority.setId(rs.getObject("authority_id", UUID.class));
			authority.setAuthority(Authority.valueOf(rs.getString("authority")));
			user.getAuthorities().add(authority);
		}
		return userMap.get(userId);
	}

//	public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
//		Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
//		UUID userId = null;
//
//		while (rs.next()) {
//			userId = rs.getObject("id", UUID.class);
//
//			AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {…});
//			AuthorityEntity authority = new AuthorityEntity();
//			authority.setId(rs.getObject("authority_id", UUID.class));
//			authority.setAuthority(Authority.valueOf(rs.getString("authority")));
//			user.getAuthorities().add(authority);
//		}
//		return userMap.get(userId);
//	}
}
