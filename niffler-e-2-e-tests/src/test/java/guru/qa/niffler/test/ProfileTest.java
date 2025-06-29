package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.Users;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {
	private static final Config CFG = Config.getInstance();

	@Category(username = "sychevTest", isArchived = true)
	@Test
	void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(Users.SYCHEV_TEST_USER_NAME, Users.SYCHEV_TEST_USER_PASSWORD)
				.submit()
				.checkThatPageLoaded()
				.openProfile()
				.findArchiveCategory(category.name());
	}

	@Category(username = "sychevTest")
	@Test
	void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(Users.SYCHEV_TEST_USER_NAME, Users.SYCHEV_TEST_USER_PASSWORD)
				.submit()
				.checkThatPageLoaded()
				.openProfile()
				.findActiveCategory(category.name());
	}
}
