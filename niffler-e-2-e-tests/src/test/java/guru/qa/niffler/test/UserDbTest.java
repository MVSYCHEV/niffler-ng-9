package guru.qa.niffler.test;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

public class UserDbTest {

	@Test
	void checkCreatingUser() {
		UserJson userJson = new UserJson(
				null,
				"Sychev Test AX-5",
				"Sychev",
				"Test",
				null,
				CurrencyValues.EUR,
				null,
				null
		);

		UserDbClient dbClient = new UserDbClient();
		dbClient.createUser(userJson);
	}
}
