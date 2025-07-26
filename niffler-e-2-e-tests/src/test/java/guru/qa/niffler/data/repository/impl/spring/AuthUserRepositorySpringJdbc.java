package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.mapper.extractor.AuthUsersEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
	private static final String URL = Config.getInstance().authJdbcUrl();
	private static final AuthUserDao AUTH_USER_DAO = new AuthUserDaoSpringJdbc();
	private static final AuthAuthorityDao AUTHORITY_DAO = new AuthAuthorityDaoSpringJdbc();

	@Override
	public AuthUserEntity create(AuthUserEntity user) {
		AuthUserEntity authUser = AUTH_USER_DAO.create(user);
		AUTHORITY_DAO.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
		return authUser;
	}

	@Override
	public AuthUserEntity update(AuthUserEntity user) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		jdbcTemplate.update("UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
						"account_non_locked = ?, credentials_non_expired = ? WHERE id = ?",
				user.getUsername(), user.getPassword(), user.getEnabled(), user.getAccountNonExpired(),
				user.getAccountNonLocked(), user.getCredentialsNonExpired(), user.getId());
		return findById(user.getId()).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public Optional<AuthUserEntity> findById(UUID id) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		return Optional.of(
				jdbcTemplate.query(
						"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
								"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
								"JOIN authority a ON u.id = a.user_id WHERE u.id = ?",
						AuthUserEntityExtractor.instance,
						id
				)
		);
	}

	@Override
	public Optional<AuthUserEntity> findByUsername(String username) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		return Optional.of(
				jdbcTemplate.query(
						"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
								"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
								"JOIN authority a ON u.id = a.user_id WHERE u.username = ?",
						AuthUserEntityExtractor.instance,
						username
				)
		);
	}

	@Override
	public List<AuthUserEntity> findAll() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		return jdbcTemplate.query(
				"SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
						"u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
						"JOIN authority a ON u.id = a.user_id",
				AuthUsersEntityExtractor.instance
		);
	}

	@Override
	public void remove(AuthUserEntity user) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(URL));
		jdbcTemplate.update("DELETE FROM authority WHERE user_id = ?", user.getId());
		jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
	}
}
