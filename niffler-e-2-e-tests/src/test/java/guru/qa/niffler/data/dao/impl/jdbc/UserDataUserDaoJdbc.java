package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.Connections;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataUserDaoJdbc implements UserDataUserDao {
	private static final Config CFG = Config.getInstance();

	@Override
	public UserEntity createUser(UserEntity user) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"INSERT INTO \"user\" (username, currency, full_name, firstname, surname, photo, photo_small) " +
						"VALUES ( ?, ?, ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS
		)) {
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getCurrency().name());
			ps.setString(3, user.getFullname());
			ps.setString(4, user.getFirstname());
			ps.setString(5, user.getSurname());
			ps.setBytes(6, user.getPhoto());
			ps.setBytes(7, user.getPhotoSmall());

			ps.executeUpdate();

			final UUID generatedKey;
			try (ResultSet rs = ps.getGeneratedKeys()) {
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
	public Optional<UserEntity> findById(UUID id) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM \"user\" WHERE id = ?"
		)) {
			ps.setObject(1, id);
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					UserEntity userEntity = new UserEntity();
					userEntity.setId(rs.getObject("id", UUID.class));
					userEntity.setUsername(rs.getString("username"));

					String currencyStr = rs.getString("currency");
					CurrencyValues currency = CurrencyValues.valueOf(currencyStr);
					userEntity.setCurrency(currency);

					userEntity.setFullname(rs.getString("full_name"));
					userEntity.setFirstname(rs.getString("firstname"));
					userEntity.setSurname(rs.getString("surname"));
					userEntity.setPhoto(rs.getBytes("photo"));
					userEntity.setPhotoSmall(rs.getBytes("photo_small"));
					return Optional.of(userEntity);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<UserEntity> findByUsername(String username) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM \"user\" WHERE username = ?"
		)) {
			ps.setObject(1, username);
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					UserEntity userEntity = new UserEntity();
					userEntity.setId(rs.getObject("id", UUID.class));
					userEntity.setUsername(rs.getString("username"));

					String currencyStr = rs.getString("currency");
					CurrencyValues currency = CurrencyValues.valueOf(currencyStr);
					userEntity.setCurrency(currency);

					userEntity.setFullname(rs.getString("full_name"));
					userEntity.setFirstname(rs.getString("firstname"));
					userEntity.setSurname(rs.getString("surname"));
					userEntity.setPhoto(rs.getBytes("photo"));
					userEntity.setPhotoSmall(rs.getBytes("photo_small"));
					return Optional.of(userEntity);
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<UserEntity> findAll() {
		List<UserEntity> userDataUserEntities = new ArrayList<>();
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"SELECT * FROM \"user\""
		)) {
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					UserEntity userEntity = new UserEntity();
					userEntity.setId(rs.getObject("id", UUID.class));
					userEntity.setUsername(rs.getString("username"));

					String currencyStr = rs.getString("currency");
					CurrencyValues currency = CurrencyValues.valueOf(currencyStr);
					userEntity.setCurrency(currency);

					userEntity.setFullname(rs.getString("full_name"));
					userEntity.setFirstname(rs.getString("firstname"));
					userEntity.setSurname(rs.getString("surname"));
					userEntity.setPhoto(rs.getBytes("photo"));
					userEntity.setPhotoSmall(rs.getBytes("photo_small"));
					userDataUserEntities.add(userEntity);
				}
			}
			return userDataUserEntities;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void delete(UserEntity user) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"DELETE FROM \"user\" WHERE id = ?"
		)) {
			ps.setObject(1, user.getId());
			int deleteRows = ps.executeUpdate();
			if (deleteRows == 0) {
				throw new SQLException("Deleting category failed, no rows was deleted.");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
