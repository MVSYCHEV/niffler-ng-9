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
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserDbClient {
	private static final Config CFG = Config.getInstance();
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

//	private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
//	private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
//	private final UserDataUserDao udUserDao = new UserDataUserDaoSpringJdbc();

		private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
	private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();


	private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
	private final UserDataUserDao udUserDao = new UserDataUserDaoJdbc();

	/**
	 * Стандартный механизм создания spring-транзакций, но не будет работать с обычным JDBC
	 */
//	private final TransactionTemplate txTemplate = new TransactionTemplate(
//			new JdbcTransactionManager(
//					dataSource(CFG.authJdbcUrl())
//			)
//	);

	private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
			CFG.authJdbcUrl(),
			CFG.userdataJdbcUrl()
	);

	TransactionTemplate txTemplate = new TransactionTemplate(
			new ChainedTransactionManager(
					new JdbcTransactionManager(
							dataSource(CFG.authJdbcUrl())
					),
					new JdbcTransactionManager(
							dataSource(CFG.userdataJdbcUrl())
					)
			)
	);

	public UserJson createUserChained(UserJson userJson) {
		return txTemplate.execute(status -> {
			AuthUserEntity authUserEntity = new AuthUserEntity();
			authUserEntity.setUsername(userJson.username());
			authUserEntity.setPassword(pe.encode("12345"));
			authUserEntity.setEnabled(true);
			authUserEntity.setAccountNonExpired(true);
			authUserEntity.setCredentialsNonExpired(true);
			authUserEntity.setAccountNonLocked(true);
			authUserDao.create(authUserEntity);

			authAuthorityDao.create(
					Arrays.stream(Authority.values())
							.map(a -> {
										AuthorityEntity ae = new AuthorityEntity();
										ae.setAuthority(a);
										ae.setUser(authUserEntity);
										return ae;
									}
							).toArray(AuthorityEntity[]::new));

			UserEntity user = new UserEntity();
			user.setUsername(userJson.username());
			user.setCurrency(userJson.currency());
			udUserDao.createUser(user);
			return UserJson.fromEntity(user);
		});
	}

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
			authUserRepository.create(authUserEntity);

			UserEntity user = new UserEntity();
			user.setUsername(userJson.username());
			user.setCurrency(userJson.currency());
			udUserDao.createUser(user);
			return UserJson.fromEntity(user);
		});
	}
}
