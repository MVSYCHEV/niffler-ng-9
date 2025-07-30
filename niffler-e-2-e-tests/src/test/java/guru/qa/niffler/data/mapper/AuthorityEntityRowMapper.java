package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {
	public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

	private AuthorityEntityRowMapper() {
	}

	@Override
	public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
		AuthorityEntity authorityEntity = new AuthorityEntity();
		authorityEntity.setId(rs.getObject("id", UUID.class));
		authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));

		AuthUserEntity authUser = new AuthUserEntity();
		authUser.setId(rs.getObject("user_id", UUID.class));
		authorityEntity.setUser(authUser);
		return authorityEntity;
	}
}
