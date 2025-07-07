package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	private final Connection connection;

	public AuthUserDaoJdbc(Connection connection) {
		this.connection = connection;
	}

	@Override
	public AuthUserEntity create(AuthUserEntity user) {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
						"VALUES ( ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setObject(2, pe.encode(user.getPassword()));
			preparedStatement.setBoolean(3, user.getEnabled());
			preparedStatement.setBoolean(4, user.getAccountNonExpired());
			preparedStatement.setBoolean(5, user.getAccountNonLocked());
			preparedStatement.setBoolean(6, user.getCredentialsNonExpired());

			preparedStatement.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
				if (rs.next()) {
					generatedKey = rs.getObject("id", UUID.class);
				} else {
					throw new SQLException("Can`t find id in ResultSet");
				}
			}
			user.setId(generatedKey);
			return user;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
