package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.UserDataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.tpl.Connections;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDataRepositoryJdbc implements UserDataUserRepository {
	private static final UserDataUserDao USER_DATA_USER_DAO_JDBC = new UserDataUserDaoJdbc();
	private static final Config CFG = Config.getInstance();

	@Override
	public UserEntity create(UserEntity user) {
		return USER_DATA_USER_DAO_JDBC.createUser(user);
	}

	@Override
	public Optional<UserEntity> findById(UUID id) {
		return USER_DATA_USER_DAO_JDBC.findById(id);
	}

	@Override
	public Optional<UserEntity> findByUsername(String username) {
		return USER_DATA_USER_DAO_JDBC.findByUsername(username);
	}

	@Override
	public UserEntity update(UserEntity user) {
		try (PreparedStatement usersPs = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				"UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ? " +
						"WHERE id = ?");
		     PreparedStatement friendsPs = Connections.holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
				     "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)"
		)) {
			usersPs.setString(1, user.getCurrency().name());
			usersPs.setString(2, user.getFirstname());
			usersPs.setString(3, user.getSurname());
			usersPs.setBytes(4, user.getPhoto());
			usersPs.setBytes(5, user.getPhotoSmall());
			usersPs.setObject(6, user.getId());
			usersPs.executeUpdate();

			for (FriendshipEntity fe : user.getFriendshipRequests()) {
				friendsPs.setObject(1, user.getId());
				friendsPs.setObject(2, fe.getAddressee().getId());
				friendsPs.setString(3, fe.getStatus().name());
				friendsPs.addBatch();
				friendsPs.clearParameters();
			}
			friendsPs.executeBatch();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return user;
	}

	@Override
	public void sendInvitation(UserEntity requester, UserEntity addressee) {
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
			addresseeFriend.setDate(4, date);
			addresseeFriend.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(UserEntity user) {
		USER_DATA_USER_DAO_JDBC.delete(user);
	}
}
