package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserDataUserEntity;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;

public class UserDbClient {
	private static final Config CFG = Config.getInstance();
	private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	public UserJson createUser(UserJson userJson) {
		return UserJson.fromEntity(
				Databases.xaTransaction(
						new Databases.XaFunction<>(connection -> {
							AuthUserEntity authUserEntity = new AuthUserEntity();
							authUserEntity.setUsername(userJson.username());
							authUserEntity.setPassword(pe.encode(RandomDataUtils.randomPassword()));
							authUserEntity.setEnabled(true);
							authUserEntity.setAccountNonExpired(true);
							authUserEntity.setCredentialsNonExpired(true);
							authUserEntity.setAccountNonLocked(true);
							new AuthUserDaoJdbc(connection).create(authUserEntity);

							new AuthAuthorityDaoJdbc(connection).create(
									Arrays.stream(Authority.values())
											.map(a -> {
														AuthorityEntity ae = new AuthorityEntity();
														ae.setAuthority(a);
														ae.setUserId(authUserEntity.getId());
														return ae;
													}
											).toArray(AuthorityEntity[]::new));

							return null;
						},
								CFG.authJdbcUrl(),
								Connection.TRANSACTION_READ_UNCOMMITTED
						),
						new Databases.XaFunction<>(connection -> {
							UserDataUserEntity user = new UserDataUserEntity();
							user.setUsername(userJson.username());
							user.setCurrency(userJson.currency());
							new UserDataUserDaoJdbc(connection).createUser(user);
							return user;
						},
								CFG.userdataJdbcUrl(),
								Connection.TRANSACTION_READ_UNCOMMITTED
						)
				));
	}
}
