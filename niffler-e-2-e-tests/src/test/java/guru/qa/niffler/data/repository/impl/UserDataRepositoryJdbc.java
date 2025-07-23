package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.tpl.Connections;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserDataRepositoryJdbc implements UserDataUserRepository {
	private static final Config CFG = Config.getInstance();

	@Override
	public UserEntity create(UserEntity user) {
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
	public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
						"VALUES ( ?, ?, ?, ?)"
		)) {
			ps.setObject(1, requester.getId());
			ps.setObject(2, addressee.getId());
			ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
			ps.setDate(4, new Date(System.currentTimeMillis()));
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
		try (PreparedStatement ps = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
						"VALUES ( ?, ?, ?, ?)"
		)) {
			ps.setObject(1, addressee.getId());
			ps.setObject(2, requester.getId());
			ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
			ps.setDate(4, new Date(System.currentTimeMillis()));
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void addFriend(UserEntity requester, UserEntity addressee) {
		final String sql = "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
				"VALUES ( ?, ?, ?, ?)";
		final Date date = new Date(System.currentTimeMillis());
		try (PreparedStatement requesterFriend = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				sql);
		     PreparedStatement addresseeFriend = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				     sql
		     )
		) {
			requesterFriend.setObject(1, requester.getId());
			requesterFriend.setObject(2, addressee.getId());
			requesterFriend.setString(3, String.valueOf(FriendshipStatus.ACCEPTED));
			requesterFriend.setDate(4, date);
			requesterFriend.executeUpdate();

			addresseeFriend.setObject(1, addressee.getId());
			addresseeFriend.setObject(2, requester.getId());
			addresseeFriend.setString(3, String.valueOf(FriendshipStatus.ACCEPTED));
			addresseeFriend.setDate(4,date);
			addresseeFriend.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
