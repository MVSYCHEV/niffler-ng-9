package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
}
