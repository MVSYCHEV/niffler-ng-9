package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.Connections;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {
	private static final String URL = Config.getInstance().authJdbcUrl();
	private static final AuthUserDao AUTH_USER_DAO = new AuthUserDaoJdbc();
	private static final AuthAuthorityDao AUTHORITY_DAO = new AuthAuthorityDaoJdbc();

	@Override
	@Nonnull
	public AuthUserEntity create(AuthUserEntity user) {
		AuthUserEntity authUser = AUTH_USER_DAO.create(user);
		AUTHORITY_DAO.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
		return authUser;
	}

	@Override
	@Nonnull
	@SuppressWarnings("resource")
	public AuthUserEntity update(AuthUserEntity user) {
		final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		try (PreparedStatement preparedStatement = Connections.holder(URL).connection().prepareStatement(
				"UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
						"account_non_locked = ?, credentials_non_expired = ? WHERE id = ?"
		)) {
			preparedStatement.setString(1, user.getUsername());
			preparedStatement.setObject(2, pe.encode(user.getPassword()));
			preparedStatement.setBoolean(3, user.getEnabled());
			preparedStatement.setBoolean(4, user.getAccountNonExpired());
			preparedStatement.setBoolean(5, user.getAccountNonLocked());
			preparedStatement.setBoolean(6, user.getCredentialsNonExpired());
			preparedStatement.setObject(7, user.getId());

			preparedStatement.executeUpdate();
			Optional<AuthUserEntity> updatedUser = AUTH_USER_DAO.findById(user.getId());
			return updatedUser.get();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Nonnull
	@SuppressWarnings("resource")
	public Optional<AuthUserEntity> findById(UUID id) {
		try (PreparedStatement ps = holder(URL).connection().prepareStatement(
				"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
						"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
						"JOIN authority a ON u.id = a.user_id WHERE u.id = ?")) {
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
					ae.setId(rs.getObject("authority_id", UUID.class));
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
	@Nonnull
	@SuppressWarnings("resource")
	public Optional<AuthUserEntity> findByUsername(String username) {
		try (PreparedStatement ps = holder(URL).connection().prepareStatement(
				"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
						"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
						"JOIN authority a ON u.id = a.user_id WHERE u.username = ?"
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
					ae.setId(rs.getObject("authority_id", UUID.class));
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
	@Nonnull
	@SuppressWarnings("resource")
	public List<AuthUserEntity> findAll() {
		List<AuthUserEntity> authUserEntities = new ArrayList<>();
		try (PreparedStatement preparedStatement = holder(URL).connection().prepareStatement(
				"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
						"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
						"JOIN authority a ON u.id = a.user_id"
		)) {
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

	@Override
	@SuppressWarnings("resource")
	public void remove(AuthUserEntity user) {
		try (PreparedStatement psAuthority = Connections.holder(URL).connection().prepareStatement(
				"DELETE FROM \"authority\" WHERE user_id = ?");
				PreparedStatement psUser = Connections.holder(URL).connection().prepareStatement(
				"DELETE FROM \"user\" WHERE id = ?")
		) {
			psAuthority.setObject(1, user.getId());
			int deleteRowsAuthority = psAuthority.executeUpdate();
			if (deleteRowsAuthority == 0) {
				throw new SQLException("Deleting authorities failed, no rows was deleted.");
			}

			psUser.setObject(1, user.getId());
			int deleteRowsUser = psUser.executeUpdate();
			if (deleteRowsUser == 0) {
				throw new SQLException("Deleting user failed, no rows was deleted.");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
