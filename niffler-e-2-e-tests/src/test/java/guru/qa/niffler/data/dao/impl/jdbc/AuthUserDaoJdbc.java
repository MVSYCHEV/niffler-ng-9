package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.tpl.Connections;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	private static final Config CFG = Config.getInstance();

	@Override
	@Nonnull
	@SuppressWarnings("resource")
	public AuthUserEntity create(AuthUserEntity user) {
		try (PreparedStatement preparedStatement = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement(
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

	@Override
	@Nonnull
	@SuppressWarnings("resource")
	public Optional<AuthUserEntity> findById(UUID id) {
		try (PreparedStatement ps = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
			ps.setObject(1, id);

			ps.execute();

			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					AuthUserEntity result = new AuthUserEntity();
					result.setId(rs.getObject("id", UUID.class));
					result.setUsername(rs.getString("username"));
					result.setPassword(rs.getString("password"));
					result.setEnabled(rs.getBoolean("enabled"));
					result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
					result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
					result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
					return Optional.of(result);
				} else {
					return Optional.empty();
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
		try ( PreparedStatement preparedStatement = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement("SELECT * FROM \"user\"")) {
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
