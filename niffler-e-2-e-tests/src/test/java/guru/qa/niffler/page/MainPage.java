package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
	private final SelenideElement spendingTable = $("#spendings");
	private final SelenideElement avatar = $("button.MuiButtonBase-root.MuiIconButton-root.MuiIconButton-colorInherit.MuiIconButton-sizeLarge.css-1obba8g");
	private final Header header = new Header();
	private final SpendingTable spends = new SpendingTable();

	@Step("Проверить, что на главной странице отображается таблица расходов")
	@Nonnull
	public MainPage checkThatPageLoaded() {
		spendingTable.should(visible);
		return this;
	}

	@Nonnull
	public EditSpendingPage editSpending(String description) {
		return spends.editSpending(description);
	}

	public void checkThatTableContainsSpending(String description) {
		spends.searchSpendingByDescription(description);
	}

	@Nonnull
	public ProfilePage openProfile() {
		return header.toProfilePage();
	}

	@Nonnull
	public FriendsPage openFriends() {
		return header.toFriendsPage();
	}

	@Nonnull
	public AllPeoplePage openAllPeople() {
		return header.toAllPeoplesPage();
	}

	@Nonnull
	public EditSpendingPage addNewSpending() {
		header.addSpending();
		return new EditSpendingPage();
	}

	@Step("Проверить, что на главном экране отображается стандартная картинка юзера")
	public void checkDefaultAvatar(BufferedImage expected) throws IOException {
		BufferedImage actual = ImageIO.read(avatar.screenshot());
		assertFalse(new ScreenDiffResult(
				expected,
				actual
		));
	}

	public SpendingTable findSpendingTable() {
		return spends;
	}
}
