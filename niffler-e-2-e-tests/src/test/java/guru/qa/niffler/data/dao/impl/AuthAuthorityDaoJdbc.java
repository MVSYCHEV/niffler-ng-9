package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
	private final Connection connection;

	public AuthAuthorityDaoJdbc(Connection connection) {
		this.connection = connection;
	}

	@Override
	public AuthorityEntity create(AuthorityEntity authorityEntity) {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"INSERT into authority (authority, user_id) " +
						"VALUES (?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			preparedStatement.setObject(1, authorityEntity.getAuthority().name());
			preparedStatement.setObject(2, authorityEntity.getUserId());

			preparedStatement.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
				if (rs.next()) {
					generatedKey = rs.getObject("id", UUID.class);
				} else {
					throw new SQLException("Can`t find id in ResultSet");
				}
			}
			authorityEntity.setId(generatedKey);
			return authorityEntity;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
