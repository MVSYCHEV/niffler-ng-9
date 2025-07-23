package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserDataRepositorySpringJdbc implements UserDataUserRepository {
	private static final Config CFG = Config.getInstance();

	@Override
	public UserEntity create(UserEntity user) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		KeyHolder kh = new GeneratedKeyHolder();
		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
							"VALUES (?,?,?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS
			);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getCurrency().name());
			ps.setString(3, user.getFirstname());
			ps.setString(4, user.getSurname());
			ps.setBytes(5, user.getPhoto());
			ps.setBytes(6, user.getPhotoSmall());
			ps.setString(7, user.getFullname());
			return ps;
		}, kh);

		final UUID generatedKey = (UUID) kh.getKeys().get("id");
		user.setId(generatedKey);
		return user;
	}

	@Override
	public Optional<UserEntity> findById(UUID id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		return Optional.of(
				jdbcTemplate.queryForObject(
						"SELECT * FROM \"user\" WHERE id = ?",
						UserDataUserEntityRowMapper.instance,
						id
				)
		);
	}

	@Override
	public Optional<UserEntity> findByUsername(String username) {
		return Optional.empty();
	}

	@Override
	public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
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
	public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		jdbcTemplate.update(con -> {
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO \"friendship\" (requester_id, addressee_id, status, created_date) " +
							"VALUES ( ?, ?, ?, ?)"
			);
			ps.setObject(1, addressee.getId());
			ps.setObject(2, requester.getId());
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
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
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
}
