package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.Connections;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
	private static final Config CFG = Config.getInstance();

	@Override
	@SuppressWarnings("resource")
	public void create(AuthorityEntity... authority) {
		try (PreparedStatement preparedStatement = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"INSERT into authority (user_id, authority) VALUES (?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			for (AuthorityEntity a : authority) {
				preparedStatement.setObject(1, a.getUser().getId());
				preparedStatement.setObject(2, a.getAuthority().name());
				preparedStatement.addBatch();
				preparedStatement.clearParameters();
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@SuppressWarnings("resource")
	public List<AuthorityEntity> findAll() {
		List<AuthorityEntity> authorityEntities = new ArrayList<>();
		try (PreparedStatement preparedStatement = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM authority"
		)) {
			preparedStatement.execute();

			try (ResultSet resultSet = preparedStatement.getResultSet()) {
				while (resultSet.next()) {
					AuthorityEntity authorityEntity = new AuthorityEntity();
					authorityEntity.setId(resultSet.getObject("id", UUID.class));
					authorityEntity.setAuthority(Authority.valueOf(resultSet.getString("authority")));

					AuthUserEntity authUser = new AuthUserEntity();
					authUser.setId(resultSet.getObject("user_id", UUID.class));
					authorityEntity.setUser(authUser);

					authorityEntities.add(authorityEntity);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return authorityEntities;
	}
}
