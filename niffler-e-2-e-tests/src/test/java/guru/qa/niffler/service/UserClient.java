package guru.qa.niffler.service;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.List;
import java.util.UUID;

public interface UserClient {
	UserJson createUser(String username, String password);

	AuthUserJson update(AuthUserJson authUserJson);

	AuthUserJson getAuthUserById(UUID id);

	AuthUserJson getAuthUserByName(String username);

	List<AuthUserJson> findAll();

	UserJson update(UserJson userJson);

	UserJson getUserById(UUID id);

	UserJson getUserByName(String username);

	List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

	List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

	List<UserJson> addFriend(UserJson targetUser, int count);

	void removeUser(AuthUserJson authUserJson);
}
