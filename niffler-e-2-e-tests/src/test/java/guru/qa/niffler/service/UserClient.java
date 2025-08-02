package guru.qa.niffler.service;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserClient {
	@Nonnull
	UserJson createUser(String username, String password);

	@Nonnull
	AuthUserJson update(AuthUserJson authUserJson);

	@Nonnull
	AuthUserJson getAuthUserById(UUID id);

	@Nonnull
	AuthUserJson getAuthUserByName(String username);

	@Nonnull
	List<AuthUserJson> findAll();

	@Nonnull
	UserJson update(UserJson userJson);

	@Nonnull
	UserJson getUserById(UUID id);

	@Nonnull
	UserJson getUserByName(String username);

	@Nonnull
	List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

	@Nonnull
	List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

	@Nonnull
	List<UserJson> addFriend(UserJson targetUser, int count);

	void removeUser(AuthUserJson authUserJson);
}
