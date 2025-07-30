package guru.qa.niffler.test.servicestest;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.impl.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class UserDbTest {
	UserClient userClient = new UserDbClient();
	// Sychev Test Hibernate
	// Sychev Test Jdbc
	// Sychev Test SpringJdbc
	private final String name = "Sychev Test SpringJdbc 2";

	@Test
	void checkCreatingUser() {
		UserJson user = userClient.createUser(name, "12345");
		Assertions.assertNotNull(user);
	}

	@Test
	void checkAuthUserFindByUserNameAndUpdateUser() {
		boolean changedValue = false;
		AuthUserJson authUserJson = userClient.getAuthUserByName(name);
		AuthUserJson updatedUserJson = new AuthUserJson(
				authUserJson.id(),
				authUserJson.username(),
				authUserJson.password(),
				authUserJson.enabled(),
				changedValue,
				authUserJson.accountNonLocked(),
				authUserJson.credentialsNonExpired(),
				authUserJson.authorities()
		);
		AuthUserJson receivedUser = userClient.update(updatedUserJson);
		Assertions.assertEquals(changedValue, receivedUser.accountNonExpired());
	}

	@Test
	void checkFindAuthUserById() {
		AuthUserJson userByName = userClient.getAuthUserByName(name);
		UUID id = userByName.id();
		AuthUserJson userById = userClient.getAuthUserById(id);
		Assertions.assertEquals(name, userById.username());
	}

	@Test
	void checkFindAllAuthUser() {
		List<AuthUserJson> allUsers = userClient.findAll();
		System.out.println("Size is " + allUsers.size());
		Assertions.assertFalse(allUsers.isEmpty());
	}

	@Test
	void checkUserDataFindByNameAndUpdate() {
		String newFirstName = "This is new First Name";
		UserJson user = userClient.getUserByName(name);
		UserJson updatedUser = new UserJson(
				user.id(),
				user.username(),
				newFirstName,
				user.surname(),
				user.fullname(),
				user.currency(),
				user.photo(),
				user.photoSmall()
		);
		UserJson receivedUser = userClient.update(updatedUser);
		System.out.println(receivedUser);
		Assertions.assertEquals(newFirstName, receivedUser.firstname());
	}

	@Test
	void checkUserDataFindById() {
		UserJson userByName = userClient.getUserByName(name);
		UUID id = userByName.id();
		UserJson userById = userClient.getUserById(id);
		Assertions.assertEquals(name, userById.username());
	}

	@Test
	void checkSendInvitation() {
		UserJson requester = userClient.createUser(RandomDataUtils.randomUserName(), "12345");
		UserJson addressee = userClient.createUser(RandomDataUtils.randomUserName(), "12345");
		userClient.sendFriendInvitation(requester, addressee);
		// Можно проверить визуально или заглянуть в базу, должна быть строчка с PENDING
	}

	@Test
	void checkAddFriend() {
		UserJson requester = userClient.createUser(RandomDataUtils.randomUserName(), "12345");
		UserJson addressee = userClient.createUser(RandomDataUtils.randomUserName(), "12345");
		userClient.addFriend(requester, addressee);
		// Можно проверить визуально или заглянуть в базу, должны быть две строчки с ACCEPTED
	}

	@Test
	void removeUser() {
		AuthUserJson userJson = userClient.getAuthUserByName(name);
		userClient.removeUser(userJson);
	}
}
