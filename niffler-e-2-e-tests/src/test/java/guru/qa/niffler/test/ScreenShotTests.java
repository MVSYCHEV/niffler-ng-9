package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class ScreenShotTests {
	private static final Config CFG = Config.getInstance();

	@User(
			spends = @Spending(
					amount = 1000.00,
					description = "New Category",
					category = "categoryNameRandomWithoutArchive"
			))
	@ScreenShotTest("img/thousandRubles.png")
	void checkStatWithoutArchive(UserJson user, String expectedImagePath) throws IOException {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.findSpendingTable()
				.checkWidgets("categoryNameRandomWithoutArchive 1000 ₽")
				.checkSpendingDiagram(expectedImagePath)
				.editSpending("New Category")
				.setNewSpendingAmount(50.0)
				.save()
				.findSpendingTable()
				.checkWidgets("categoryNameRandomWithoutArchive 50 ₽")
				.checkSpendingDiagram("img/fiftyRubles.png")
				.checkWidgets(Color.yellow);
	}

	@ScreenShotTest("img/spendsWithArchive.png")
	void checkStatWithArchive(String expectedImagePath) throws IOException {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage("leesa.damore", "12345")
				.submit()
				.checkThatPageLoaded()
				.findSpendingTable()
				.checkSpendingDiagram(expectedImagePath)
				.checkWidgets(Color.yellow, Color.green);
	}

	@ScreenShotTest("img/defaultAvatarMainPage.png")
	void checkDefaultAvatarMainScreen(BufferedImage expected) throws IOException {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage("idell.bayer", "12345")
				.submit()
				.checkDefaultAvatar(expected);
	}
}
