package guru.qa.niffler.data.dao.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserDataUserDaoSpringJdbc implements UserDataUserDao {
	private static final Config CFG = Config.getInstance();

	@Override
	@Nonnull
	public UserEntity createUser(UserEntity user) {
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
	@Nonnull
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
	@Nonnull
	public Optional<UserEntity> findByUsername(String username) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		return Optional.of(
				jdbcTemplate.queryForObject(
						"SELECT * FROM \"user\" WHERE username = ?",
						UserDataUserEntityRowMapper.instance,
						username
				)
		);
	}

	@Override
	@Nonnull
	public List<UserEntity> findAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		return jdbcTemplate.query(
				"SELECT * FROM \"user\"",
				UserDataUserEntityRowMapper.instance
		);
	}

	@Override
	public void delete(UserEntity user) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
		jdbcTemplate.update(
				"DELETE FROM \"user\" WHERE id = ?",
				user.getId()
		);
	}
}
