package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ProfilePage {
	private final SelenideElement uploadNewPictureButton = $("span[role='button']");
	private final SelenideElement usernameInput = $("label[for='username']");
	private final SelenideElement nameInput = $("#name");
	private final SelenideElement saveChangesButton = $("button[type='submit']");
	private final SelenideElement addNewCategoryInput = $("#category");
	private final SelenideElement showArchivedCheckbox = $("input[type='checkbox']");
	private final SelenideElement categories = $("div.MuiGrid-root.MuiGrid-container.css-17e75sl");

	private final Calendar calendar = new Calendar($(".ProfileCalendar"));

	public void findArchiveCategory(String categoryName) {
		showArchivedCheckbox.click();
		findActiveCategory(categoryName);
	}

	public void findActiveCategory(String categoryName) {
		categories.shouldHave(text(categoryName));
	}
}
