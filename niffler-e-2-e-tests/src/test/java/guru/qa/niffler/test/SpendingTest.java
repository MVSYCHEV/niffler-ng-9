package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {
	private static final Config CFG = Config.getInstance();

	@User(
			username = "sychevTest", // TODO Добавить генерацию рандомного юзера с новой категорией
			spends = @Spending(
					amount = 89990.00,
					description = "New Category",
					category = "categoryNameRandom"
			))
	@DisabledByIssue("3")
	@Test
	void mainPageShouldBeDisplayedAfterSuccessLogin(SpendJson spendJson) {
		final String newDescription = "New Description";

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage("sychevTest", "12341234")
				.submit()
				.checkThatPageLoaded()
				.editSpending(spendJson.description())
				.setNewSpendingDescription(newDescription)
				.save()
				.checkThatTableContainsSpending(newDescription);
	}
}
