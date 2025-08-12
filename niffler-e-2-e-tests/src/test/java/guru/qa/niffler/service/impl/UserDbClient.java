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
import io.qameta.allure.Step;
import jaxb.userdata.FriendshipStatus;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
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
	@Step("Через BD создать пользователя с именем '{0}' и паролем '{1}'")
	@Nonnull
	public UserJson createUser(String username, String password) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
			AuthUserEntity authUserEntity = authUserEntity(username, password);
			authUserRepository.create(authUserEntity);
			UserEntity userEntity = userDataUserRepository.create(userEntity(username));
			return UserJson.fromEntity(userEntity, null);
		}));
	}

	@Override
	@Step("Через BD обновить пользователя '{0}'")
	@Nonnull
	public AuthUserJson update(AuthUserJson authUserJson) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
			AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
			return AuthUserJson.fromEntity(authUserRepository.update(authUserEntity));
		}));
	}

	@Override
	@Step("Через BD найти пользователя с id '{0}'")
	@Nonnull
	public AuthUserJson getAuthUserById(UUID id) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
			Optional<AuthUserEntity> authUserEntity = authUserRepository.findById(id);
			return AuthUserJson.fromEntity(authUserEntity.get());
		}));
	}

	@Override
	@Step("Через BD найти пользователя с именем '{0}'")
	@Nonnull
	public AuthUserJson getAuthUserByName(String username) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
			Optional<AuthUserEntity> authUserEntity = authUserRepository.findByUsername(username);
			return AuthUserJson.fromEntity(authUserEntity.get());
		}));
	}

	@Override
	@Step("Через BD найти всех пользователей")
	@Nonnull
	public List<AuthUserJson> findAll() {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					List<AuthUserEntity> authUserEntities = authUserRepository.findAll();
					return authUserEntities.stream()
							.map(AuthUserJson::fromEntity)
							.collect(Collectors.toList());
				}
		));
	}

	@Override
	@Step("Через BD обновить пользователя '{0}'")
	@Nonnull
	public UserJson update(UserJson userJson) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					UserEntity userEntity = UserEntity.fromJson(userJson);
					return UserJson.fromEntity(userDataUserRepository.update(userEntity), null);
				}
		));
	}

	@Override
	@Step("Через BD найти пользователя с id '{0}'")
	@Nonnull
	public UserJson getUserById(UUID id) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					Optional<UserEntity> userEntity = userDataUserRepository.findById(id);
					return UserJson.fromEntity(userEntity.get(), null);
				}
		));
	}

	@Override
	@Step("Через BD найти пользователя с именем '{0}'")
	@Nonnull
	public UserJson getUserByName(String username) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					Optional<UserEntity> userEntity = userDataUserRepository.findByUsername(username);
					return UserJson.fromEntity(userEntity.get(), null);
				}
		));
	}

	@Override
	@Step("Через BD для юзера '{0}' добавить '{1}' входящих предложений дружить")
	@Nonnull
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
	@Step("Через BD для юзера '{0}' добавить '{1}' исходящих предложений дружить")
	@Nonnull
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
	@Step("Через BD для юзера '{0}' добавить '{1}' друзей")
	@Nonnull
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
	@Step("Через BD удалить юзера '{0}'")
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
