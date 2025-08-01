package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {
	private static final Config CFG = Config.getInstance();

	@User(
			spends = @Spending(
					amount = 89990.00,
					description = "New Category",
					category = "categoryNameRandom"
			))
	@DisabledByIssue("3")
	@Test
	void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
		final SpendJson spend = user.testData().spendings().getFirst();
		final String newDescription = "New Description!!";

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.editSpending(spend.description())
				.setNewSpendingDescription(newDescription)
				.save()
				.checkThatTableContainsSpending(newDescription);
	}

	@Test
	void checkLostSpends() {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage("idell.bayer", "12345")
				.submit()
				.checkThatPageLoaded()
				.checkThatTableContainsSpending("Second Page Descr");
	}
}
