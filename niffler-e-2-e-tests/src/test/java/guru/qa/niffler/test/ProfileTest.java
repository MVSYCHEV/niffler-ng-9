package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.Users;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {
	private static final Config CFG = Config.getInstance();
	private final String name = "sychevTest";

	@User(username = name, categories = @Category(isArchived = true))
	@Test
	void archivedCategoryShouldPresentInCategoriesList(CategoryJson[] categoryJson) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(Users.SYCHEV_TEST_USER_NAME, Users.SYCHEV_TEST_USER_PASSWORD)
				.submit()
				.checkThatPageLoaded()
				.openProfile()
				.findArchiveCategory(categoryJson[0].name());
	}

	@User(username = name, categories = @Category())
	@Test
	void activeCategoryShouldPresentInCategoriesList(CategoryJson[] categoryJson) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(Users.SYCHEV_TEST_USER_NAME, Users.SYCHEV_TEST_USER_PASSWORD)
				.submit()
				.checkThatPageLoaded()
				.openProfile()
				.findActiveCategory(categoryJson[0].name());
	}

	@User
	@Test
	void checkChangeProfile(UserJson user) {
		String userName = RandomDataUtils.randomName();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openProfile()
				.setNewName(userName)
				.saveChanges()
				.goToMain()
				.openProfile()
				.checkProfileName(userName);
	}
}
