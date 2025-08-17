package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

	private final SelenideElement uploadNewPictureButton = $("span[role='button']");
	private final SelenideElement usernameInput = $("label[for='username']");
	private final SelenideElement nameInput = $("#name");
	private final SelenideElement saveChangesButton = $("button[type='submit']");
	private final SelenideElement addNewCategoryInput = $("#category");
	private final SelenideElement showArchivedCheckbox = $("input[type='checkbox']");
	private final SelenideElement categories = $("div.MuiGrid-root.MuiGrid-container.css-17e75sl");

	Header header = new Header();

	@Step("Проверить, что у пользователя есть архивная категория '{0}'")
	public void findArchiveCategory(String categoryName) {
		showArchivedCheckbox.click();
		findActiveCategory(categoryName);
	}

	@Step("Проверить, что у пользователя есть активная категория '{0}'")
	public void findActiveCategory(String categoryName) {
		categories.shouldHave(text(categoryName));
	}

	@Step("Задать пользователю новое имя '{0}'")
	@Nonnull
	public ProfilePage setNewName(String name) {
		nameInput.clear();
		nameInput.setValue(name);
		return this;
	}

	@Step("Нажать на кнопку 'Save changes'")
	@Nonnull
	public ProfilePage saveChanges() {
		saveChangesButton.click();
		checkAlert("Profile successfully update");
		return this;
	}

	@Step("Перейти на главную страницу кликом на Niffler")
	@Nonnull
	public MainPage goToMain() {
		header.toMainPage();
		return new MainPage();
	}

	@Step("Проверить, что поле username содержит значение '{0}'")
	public void checkProfileName(String userName) {
		nameInput.shouldHave(value(userName));
	}
}
