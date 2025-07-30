package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.UserDataRepositoryJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserDbClient {
	private static final Config CFG = Config.getInstance();
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

//	private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
//	private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
//	private final UserDataUserDao udUserDao = new UserDataUserDaoSpringJdbc();

	private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
	private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
	private final UserDataUserDao udUserDao = new UserDataUserDaoJdbc();

	private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
	private final AuthUserRepository authUserRepositorySpring = new AuthUserRepositorySpringJdbc();

	private final UserDataRepositoryJdbc userDataRepositoryJdbc = new UserDataRepositoryJdbc();


	/**
	 * Стандартный механизм создания spring-транзакций, но не будет работать с обычным JDBC
	 */
	private final TransactionTemplate txTemplate = new TransactionTemplate(
			new JdbcTransactionManager(
					dataSource(CFG.authJdbcUrl())
			)
	);

	private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
			CFG.authJdbcUrl(),
			CFG.userdataJdbcUrl()
	);

	private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
			CFG.userdataJdbcUrl()
	);

//	TransactionTemplate txTemplate = new TransactionTemplate(
//			new ChainedTransactionManager(
//					new JdbcTransactionManager(
//							dataSource(CFG.authJdbcUrl())
//					),
//					new JdbcTransactionManager(
//							dataSource(CFG.userdataJdbcUrl())
//					)
//			)
//	);

	// Не использовать, добавлено для проверки гипотез
//	public UserJson createUserChained(UserJson userJson) {
//		return txTemplate.execute(status -> {
//			AuthUserEntity authUserEntity = new AuthUserEntity();
//			authUserEntity.setUsername(userJson.username());
//			authUserEntity.setPassword(pe.encode("12345"));
//			authUserEntity.setEnabled(true);
//			authUserEntity.setAccountNonExpired(true);
//			authUserEntity.setCredentialsNonExpired(true);
//			authUserEntity.setAccountNonLocked(true);
//			authUserDao.create(authUserEntity);
//
//			authAuthorityDao.create(
//					Arrays.stream(Authority.values())
//							.map(a -> {
//										AuthorityEntity ae = new AuthorityEntity();
//										ae.setAuthority(a);
//										ae.setUser(authUserEntity);
//										return ae;
//									}
//							).toArray(AuthorityEntity[]::new));
//
//			UserEntity user = new UserEntity();
//			user.setUsername(userJson.username());
//			user.setCurrency(userJson.currency());
//			udUserDao.createUser(user);
//			return UserJson.fromEntity(user);
//		});
//	}

	public UserJson createUser(UserJson userJson) {
		return xaTransactionTemplate.execute(() -> {
			AuthUserEntity authUserEntity = new AuthUserEntity();
			authUserEntity.setUsername(userJson.username());
			authUserEntity.setPassword(pe.encode("12345"));
			authUserEntity.setEnabled(true);
			authUserEntity.setAccountNonExpired(true);
			authUserEntity.setCredentialsNonExpired(true);
			authUserEntity.setAccountNonLocked(true);
			authUserEntity.setAuthorities(
					Arrays.stream(Authority.values())
							.map(a -> {
										AuthorityEntity ae = new AuthorityEntity();
										ae.setAuthority(a);
										ae.setUser(authUserEntity);
										return ae;
									}
							).toList()
			);
//			authUserRepository.create(authUserEntity);
			authUserRepositorySpring.create(authUserEntity);

			UserEntity user = new UserEntity();
			user.setUsername(userJson.username());
			user.setCurrency(userJson.currency());
			udUserDao.createUser(user);
			return UserJson.fromEntity(user);
		});
	}

	public AuthUserEntity getAuthUserById(UUID id) {
		return xaTransactionTemplate.execute(() -> {
			Optional<AuthUserEntity> authUserEntity = authUserRepositorySpring.findById(id);
			return authUserEntity.get();
		});
	}

	public List<AuthUserEntity> findAll() {
		return xaTransactionTemplate.execute(() -> authUserRepositorySpring.findAll());
	}

	public UserJson getUserByName(String name) {
		return jdbcTxTemplate.execute(() -> {
			Optional<UserEntity> user = udUserDao.findByUsername(name);
			return UserJson.fromEntity(user.get());
		});
	}

	public void addIncomeInvitation(UserJson requester, UserJson addressee) {
		jdbcTxTemplate.execute(() -> {
			UserEntity requesterEntity = new UserEntity();
			requesterEntity.setId(requester.id());
			UserEntity addresseeEntity = new UserEntity();
			addresseeEntity.setId(addressee.id());
			userDataRepositoryJdbc.addIncomeInvitation(requesterEntity, addresseeEntity);
			return null;
		});
	}

	public void addOutcomeInvitation(UserJson requester, UserJson addressee) {
		jdbcTxTemplate.execute(() -> {
			UserEntity requesterEntity = new UserEntity();
			requesterEntity.setId(requester.id());
			UserEntity addresseeEntity = new UserEntity();
			addresseeEntity.setId(addressee.id());
			userDataRepositoryJdbc.addOutcomeInvitation(requesterEntity, addresseeEntity);
			return null;
		});
	}

	public void addFriend(UserJson requester, UserJson addressee) {
		jdbcTxTemplate.execute(() -> {
			UserEntity requesterEntity = new UserEntity();
			requesterEntity.setId(requester.id());
			UserEntity addresseeEntity = new UserEntity();
			addresseeEntity.setId(addressee.id());
			userDataRepositoryJdbc.addFriend(requesterEntity, addresseeEntity);
			return null;
		});
	}
}
