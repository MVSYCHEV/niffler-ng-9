package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
	private final Connection connection;

	public AuthAuthorityDaoJdbc(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void create(AuthorityEntity... authority) {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
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
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT * FROM authority"
		)) {
			preparedStatement.execute();

			try (ResultSet resultSet = preparedStatement.getResultSet()) {
				while (resultSet.next()) {
					AuthorityEntity authorityEntity = new AuthorityEntity();
					authorityEntity.setId(resultSet.getObject("id", UUID.class));
					authorityEntity.setUserId(resultSet.getObject("user_id", UUID.class));
					authorityEntity.setAuthority(resultSet.getObject("authority", Authority.class));
					authorityEntities.add(authorityEntity);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return authorityEntities;
	}
}
