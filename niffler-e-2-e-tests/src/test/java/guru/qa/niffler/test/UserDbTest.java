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

	@Test
	void checkCorrectCreatingUserWithChainedTransaction() {
		UserJson userJson = new UserJson(
				null,
				"Sychev Is Testing Chain-3",
				"Sychev",
				"Test",
				null,
				CurrencyValues.EUR,
				null,
				null
		);

		UserDbClient dbClient = new UserDbClient();
		dbClient.createUserChained(userJson);
		/**
		 1) JDBC_DAO - Пользователь создается в обеих таблица и в таблице authority
		 2) SPRING_JDBC_DAO - Пользователь создается в обеих таблица и в таблице authority
		 */
	}

	@Test
	void checkCreatingUserWithChainedTransactionAndAuthNameNull() {
		UserJson userJson = new UserJson(
				null,
				null,
				"SychevSychev",
				"Test",
				null,
				CurrencyValues.EUR,
				null,
				null
		);

		// Задаем authUser.userName == null

		UserDbClient dbClient = new UserDbClient();
		dbClient.createUserChained(userJson);

		/**
		 1) JDBC_DAO - Пользователь не создался ни в одной таблице
		 2) SPRING_JDBC_DAO - Пользователь не создался ни в одной таблице
		 */

	}

	@Test
	void checkCreatingUserWithChainedTransactionAndAuthorityIdNull() {
		UserJson userJson = new UserJson(
				null,
				"Testing Chained With Authority User Id Null",
				"Sychev",
				"Test",
				null,
				CurrencyValues.EUR,
				null,
				null
		);

		// Внутри createUserChained задаем authority.userId == null

		UserDbClient dbClient = new UserDbClient();
		dbClient.createUserChained(userJson);

		/**
		 1) JDBC_DAO - Пользователь СОЗДАЛСЯ (!) в таблице auth.user и не создался в authority / userData.user
		 2) SPRING_JDBC_DAO - Пользователь не создался ни в одной таблице
		 */
	}

	@Test
	void checkCreatingUserWithChainedTransactionAndAUserDataUserNameNull() {
		UserJson userJson = new UserJson(
				null,
				"Testing Chained WithUserDataUserNameNull-3",
				"Sychev",
				"Test",
				null,
				CurrencyValues.EUR,
				null,
				null
		);

		// Внутри createUserChained задаем userData.userName == null

		UserDbClient dbClient = new UserDbClient();
		dbClient.createUserChained(userJson);

		/**
		 1) JDBC_DAO - Пользователь СОЗДАЛСЯ (!) в таблице auth.user / authority и не создался в userData.user
		 2) SPRING_JDBC_DAO - Пользователь не создался ни в одной таблице
		 */

	}
}
