package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDataUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserDataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserDataUserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

public class UserDbClient {
	private static final Config CFG = Config.getInstance();
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
	private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
	private final UserDataUserDao udUserDao = new UserDataUserDaoSpringJdbc();

	/**
	 Стандартный механизм создания spring-транзакций, но не будет работать с обычным JDBC
	 */
	private final TransactionTemplate txTemplate = new TransactionTemplate(
			new JdbcTransactionManager(
					DataSources.dataSource(CFG.authJdbcUrl())
			)
	);

	private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
			CFG.authJdbcUrl(),
			CFG.userdataJdbcUrl()
	);

	public UserJson createUser(UserJson userJson) {
		return xaTransactionTemplate.execute(() -> {
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
										ae.setUserId(authUserEntity.getId());
										return ae;
									}
							).toArray(AuthorityEntity[]::new));

			UserDataUserEntity user = new UserDataUserEntity();
			user.setUsername(userJson.username());
			user.setCurrency(userJson.currency());
			udUserDao.createUser(user);
			return UserJson.fromEntity(user);
		});
	}
}
