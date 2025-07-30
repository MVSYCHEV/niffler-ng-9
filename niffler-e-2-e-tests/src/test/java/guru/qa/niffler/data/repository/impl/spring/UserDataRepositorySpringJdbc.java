package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.dao.impl.spring.UserDataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDataRepositorySpringJdbc implements UserDataUserRepository {
	private final static UserDataUserDao USER_DATA_USER_DAO = new UserDataUserDaoSpringJdbc();
	private static final String URL = Config.getInstance().userdataJdbcUrl();

	@Override
	public UserEntity create(UserEntity user) {
		return USER_DATA_USER_DAO.createUser(user);
	}

	@Override
	public Optional<UserEntity> findById(UUID id) {
		return USER_DATA_USER_DAO.findById(id);
	}

	@Override
	public Optional<UserEntity> findByUsername(String username) {
		return USER_DATA_USER_DAO.findByUsername(username);
	}

	@Override
	public UserEntity update(UserEntity user) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		jdbcTemplate.update("UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, " +
						"photo_small = ? WHERE id = ?",
				user.getCurrency().name(), user.getFirstname(), user.getSurname(), user.getPhoto(),
				user.getPhotoSmall(), user.getId());

		FriendshipEntity[] friendshipEntities = user.getFriendshipRequests().toArray(new FriendshipEntity[0]);
		jdbcTemplate.batchUpdate(
				"INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setObject(1, user.getId());
						ps.setString(2, String.valueOf(friendshipEntities[i].getAddressee().getId()));
						ps.setString(3, friendshipEntities[i].getStatus().name());
					}

					@Override
					public int getBatchSize() {
						return friendshipEntities.length;
					}
				}
		);
		return findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public void sendInvitation(UserEntity requester, UserEntity addressee) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
							"VALUES ( ?, ?, ?, ?)"
			);
			ps.setObject(1, requester.getId());
			ps.setObject(2, addressee.getId());
			ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
			ps.setDate(4, new Date(System.currentTimeMillis()));
			return ps;
		});
	}

	@Override
	public void addFriend(UserEntity requester, UserEntity addressee) {
		final String sql = "INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
				"VALUES ( ?, ?, ?, ?)";
		final Date date = new Date(System.currentTimeMillis());
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setObject(1, requester.getId());
			ps.setObject(2, addressee.getId());
			ps.setString(3, String.valueOf(FriendshipStatus.ACCEPTED));
			ps.setDate(4, date);
			return ps;
		});
		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setObject(1, addressee.getId());
			ps.setObject(2, requester.getId());
			ps.setString(3, String.valueOf(FriendshipStatus.ACCEPTED));
			ps.setDate(4, date);
			return ps;
		});
	}

	@Override
	public void remove(UserEntity user) {
		USER_DATA_USER_DAO.delete(user);
	}
}
