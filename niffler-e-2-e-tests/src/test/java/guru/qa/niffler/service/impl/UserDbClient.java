package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserDataRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.utils.RandomDataUtils;
import jaxb.userdata.FriendshipStatus;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class UserDbClient implements UserClient {
	private static final Config CFG = Config.getInstance();
	/**
	 * Работает со всеми DAO
	 */
	private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
			CFG.authJdbcUrl(),
			CFG.userdataJdbcUrl()
	);
	/**
	 * Работает для обычных DAO (не Spring)
	 */
	private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
			CFG.userdataJdbcUrl()
	);
	/**
	 * Стандартный механизм создания spring-транзакций, но не будет работать с обычным JDBC
	 */
	private final TransactionTemplate txTemplate = new TransactionTemplate(
			new JdbcTransactionManager(
					dataSource(CFG.authJdbcUrl())
			)
	);
//	private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
//	private final UserDataUserRepository userDataUserRepository = new UserdataUserRepositoryHibernate();
//	private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
//	private final UserDataUserRepository userDataUserRepository = new UserDataRepositoryJdbc();
	private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc();
	private final UserDataUserRepository userDataUserRepository = new UserDataRepositorySpringJdbc();

	@Override
	public UserJson createUser(String username, String password) {
		return xaTransactionTemplate.execute(() -> {
			AuthUserEntity authUserEntity = authUserEntity(username, password);
			authUserRepository.create(authUserEntity);
			UserEntity userEntity = userDataUserRepository.create(userEntity(username));
			return UserJson.fromEntity(userEntity, null);
		});
	}

	@Override
	public AuthUserJson update(AuthUserJson authUserJson) {
		return xaTransactionTemplate.execute(() -> {
			AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
			return AuthUserJson.fromEntity(authUserRepository.update(authUserEntity));
		});
	}

	@Override
	public AuthUserJson getAuthUserById(UUID id) {
		return xaTransactionTemplate.execute(() -> {
			Optional<AuthUserEntity> authUserEntity = authUserRepository.findById(id);
			return AuthUserJson.fromEntity(authUserEntity.get());
		});
	}

	@Override
	public AuthUserJson getAuthUserByName(String username) {
		return xaTransactionTemplate.execute(() -> {
			Optional<AuthUserEntity> authUserEntity = authUserRepository.findByUsername(username);
			return AuthUserJson.fromEntity(authUserEntity.get());
		});
	}

	@Override
	public List<AuthUserJson> findAll() {
		return xaTransactionTemplate.execute(() -> {
					List<AuthUserEntity> authUserEntities = authUserRepository.findAll();
					return authUserEntities.stream()
							.map(AuthUserJson::fromEntity)
							.collect(Collectors.toList());
				}
		);
	}

	@Override
	public UserJson update(UserJson userJson) {
		return xaTransactionTemplate.execute(() -> {
					UserEntity userEntity = UserEntity.fromJson(userJson);
					return UserJson.fromEntity(userDataUserRepository.update(userEntity), null);
				}
		);
	}

	@Override
	public UserJson getUserById(UUID id) {
		return xaTransactionTemplate.execute(() -> {
					Optional<UserEntity> userEntity = userDataUserRepository.findById(id);
					return UserJson.fromEntity(userEntity.get(), null);
				}
		);
	}

	@Override
	public UserJson getUserByName(String username) {
		return xaTransactionTemplate.execute(() -> {
					Optional<UserEntity> userEntity = userDataUserRepository.findByUsername(username);
					return UserJson.fromEntity(userEntity.get(), null);
				}
		);
	}

	@Override
	public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
		final List<UserJson> result = new ArrayList<>();
		if (count > 0) {
			UserEntity targetEntity = userDataUserRepository.findById(
					targetUser.id()
			).orElseThrow();
			for (int i = 0; i < count; i++) {
				xaTransactionTemplate.execute(() -> {
							String username = RandomDataUtils.randomUserName();
							AuthUserEntity authUser = authUserEntity(username, "12345");
							authUserRepository.create(authUser);
							UserEntity adressee = userDataUserRepository.create(userEntity(username));
							userDataUserRepository.sendInvitation(adressee, targetEntity);
							result.add(UserJson.fromEntity(
									adressee,
									FriendshipStatus.INVITE_RECEIVED
							));
							return null;
						}
				);
			}
		}
		return result;
	}

	@Override
	public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
		final List<UserJson> result = new ArrayList<>();
		if (count > 0) {
			UserEntity targetEntity = userDataUserRepository.findById(
					targetUser.id()
			).orElseThrow();

			for (int i = 0; i < count; i++) {
				xaTransactionTemplate.execute(() -> {
							String username = RandomDataUtils.randomUserName();
							AuthUserEntity authUser = authUserEntity(username, "12345");
							authUserRepository.create(authUser);
							UserEntity adressee = userDataUserRepository.create(userEntity(username));
							userDataUserRepository.sendInvitation(targetEntity, adressee);
							result.add(UserJson.fromEntity(
									adressee,
									FriendshipStatus.INVITE_RECEIVED
							));
							return null;
						}
				);
			}
		}
		return result;
	}

	@Override
	public List<UserJson> addFriend(UserJson targetUser, int count) {
		final List<UserJson> result = new ArrayList<>();
		if (count > 0) {
			UserEntity targetEntity = userDataUserRepository.findById(
					targetUser.id()
			).orElseThrow();

			for (int i = 0; i < count; i++) {
				xaTransactionTemplate.execute(() -> {
							String username = RandomDataUtils.randomUserName();
							AuthUserEntity authUser = authUserEntity(username, "12345");
							authUserRepository.create(authUser);
							UserEntity adressee = userDataUserRepository.create(userEntity(username));
							userDataUserRepository.addFriend(targetEntity, adressee);
							result.add(UserJson.fromEntity(
									adressee,
									FriendshipStatus.FRIEND
							));
							return null;
						}
				);
			}
		}
		return result;
	}

	@Override
	public void removeUser(AuthUserJson authUserJson) {
		xaTransactionTemplate.execute(() -> {
					AuthUserEntity authUser = AuthUserEntity.fromJson(authUserJson);
					authUserRepository.remove(authUser);
					Optional<UserEntity> userEntity = userDataUserRepository.findByUsername(authUser.getUsername());
					userDataUserRepository.remove(userEntity.get());
					return null;
				}
		);
	}

	private UserEntity userEntity(String username) {
		UserEntity user = new UserEntity();
		user.setUsername(username);
		user.setCurrency(CurrencyValues.EUR);
		return user;
	}

	private AuthUserEntity authUserEntity(String username, String password) {
		final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		AuthUserEntity authUserEntity = new AuthUserEntity();
		authUserEntity.setUsername(username);
		authUserEntity.setPassword(pe.encode(password));
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
		return authUserEntity;
	}
}
