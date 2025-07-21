package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.tpl.Connections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
	private static final Config CFG = Config.getInstance();

	@Override
	public void create(AuthorityEntity... authority) {
		try (PreparedStatement preparedStatement = Connections.holder(CFG.authJdbcUrl()).connection().prepareStatement(
				"INSERT into authority (authority, user_id) " +
						"VALUES (?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			for (AuthorityEntity a : authority) {
				preparedStatement.setObject(1, a.getAuthority().name());
				preparedStatement.setObject(2, a.getUserId());
				preparedStatement.addBatch();
				preparedStatement.clearParameters();
			}
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
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
					authorityEntity.setUserId(resultSet.getObject("user_id", UUID.class));
					authorityEntity.setAuthority(Authority.valueOf(resultSet.getString("authority")));
					authorityEntities.add(authorityEntity);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return authorityEntities;
	}
}
