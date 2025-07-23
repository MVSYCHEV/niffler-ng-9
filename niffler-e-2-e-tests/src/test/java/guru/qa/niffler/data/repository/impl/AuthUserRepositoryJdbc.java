package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	private static final Config CFG = Config.getInstance();

	@Override
	public AuthUserEntity create(AuthUserEntity user) {
		try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
						"VALUES ( ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		     PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
				     "INSERT into authority (authority, user_id) VALUES (?, ?)"
		     )) {
			userPs.setString(1, user.getUsername());
			userPs.setObject(2, pe.encode(user.getPassword()));
			userPs.setBoolean(3, user.getEnabled());
			userPs.setBoolean(4, user.getAccountNonExpired());
			userPs.setBoolean(5, user.getAccountNonLocked());
			userPs.setBoolean(6, user.getCredentialsNonExpired());

			userPs.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = userPs.getGeneratedKeys()) {
				if (rs.next()) {
					generatedKey = rs.getObject("id", UUID.class);
				} else {
					throw new SQLException("Can`t find id in ResultSet");
				}
			}
			user.setId(generatedKey);

			for (AuthorityEntity a : user.getAuthorities()) {
				authorityPs.setObject(1, a.getAuthority().name());
				authorityPs.setObject(2, generatedKey);
				authorityPs.addBatch();
				authorityPs.clearParameters();
			}
			authorityPs.executeBatch();

			return user;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<AuthUserEntity> findById(UUID id) {
		try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE id = ?")) {
			ps.setObject(1, id);

			ps.execute();

			try (ResultSet rs = ps.getResultSet()) {
				AuthUserEntity user = null;
				List<AuthorityEntity> authorityEntities = new ArrayList<>();
				while (rs.next()) {
					if (user == null) {
						user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
					}

					AuthorityEntity ae = new AuthorityEntity();
					ae.setUser(user);
					ae.setId(rs.getObject("a.id", UUID.class));
					ae.setAuthority(Authority.valueOf(rs.getString("authority")));
					authorityEntities.add(ae);

					AuthUserEntity result = new AuthUserEntity();
					result.setId(rs.getObject("id", UUID.class));
					result.setUsername(rs.getString("username"));
					result.setPassword(rs.getString("password"));
					result.setEnabled(rs.getBoolean("enabled"));
					result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
					result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
					result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
				}
				if (user == null) {
					return Optional.empty();
				} else {
					user.setAuthorities(authorityEntities);
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<AuthUserEntity> findByUsername(String username) {
		try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"select * from \"user\" u join authority a on u.id = a.user_id where u.username = ?"
		)) {
			ps.setString(1, username);

			ps.execute();

			try (ResultSet rs = ps.getResultSet()) {
				AuthUserEntity user = null;
				List<AuthorityEntity> authorityEntities = new ArrayList<>();
				while (rs.next()) {
					if (user == null) {
						user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
					}

					AuthorityEntity ae = new AuthorityEntity();
					ae.setUser(user);
					ae.setId(rs.getObject("a.id", UUID.class));
					ae.setAuthority(Authority.valueOf(rs.getString("authority")));
					authorityEntities.add(ae);
				}
				if (user == null) {
					return Optional.empty();
				} else {
					user.setAuthorities(authorityEntities);
					return Optional.of(user);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<AuthUserEntity> findAll() {
		List<AuthUserEntity> authUserEntities = new ArrayList<>();
		try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM \"user\"")) {
			preparedStatement.execute();

			try (ResultSet resultSet = preparedStatement.getResultSet()) {
				while (resultSet.next()) {
					AuthUserEntity authUserEntity = new AuthUserEntity();
					authUserEntity.setId(resultSet.getObject("id", UUID.class));
					authUserEntity.setUsername(resultSet.getString("username"));
					authUserEntity.setPassword(resultSet.getString("password"));
					authUserEntity.setEnabled(resultSet.getBoolean("enabled"));
					authUserEntity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
					authUserEntity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
					authUserEntity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
					authUserEntities.add(authUserEntity);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return authUserEntities;
	}
}
