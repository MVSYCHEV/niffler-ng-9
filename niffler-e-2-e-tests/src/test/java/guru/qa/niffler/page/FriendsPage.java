package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage {
	private final SelenideElement friendsTable = $("#simple-tabpanel-friends");
	private final SelenideElement requests = $("#requests");
	private final SelenideElement thereAreNoUsersYetTitle = $("p.MuiTypography-root.MuiTypography-h6.css-1m7obeg");
	private final SelenideElement searchInput = $("input.MuiInputBase-input.css-mnn31");

	@Step("Проверить, что таблица друзей отображается")
	@Nonnull
	public FriendsPage checkThatPageLoaded() {
		friendsTable.should(visible);
		return this;
	}

	@Step("Проверить, что таблица друзей содержит имя '{0}'")
	public void checkFriendByName(String friendName) {
		searchInput.should(visible).setValue(friendName).pressEnter();
		ElementsCollection rows = friendsTable.$$("tbody tr");
		SelenideElement friendRow = rows.findBy(text(friendName));
		friendRow.should(visible);
	}

	@Step("Проверить, что таблица друзей пустая")
	public void checkFriendsListIsEmpty() {
		thereAreNoUsersYetTitle.should(visible);
	}

	@Step("Проверить, что в таблице друзей есть запрос от '{0}'")
	public void checkFriendsListHasRequestFromUser(String userName) {
		requests.$$("tbody tr").find(text(userName)).should(visible);
	}

	@Step("Проверить, что таблица друзей содержит предложение от '{0}' и принять его")
	@Nonnull
	public FriendsPage acceptFriendRequestFromUser(String user) {
		checkFriendsListHasRequestFromUser(user);
		SelenideElement friendRow = requests.$$("tr").find(text(user));
		friendRow.$(byText("Accept")).click();
		checkFriendByName(user);
		return this;
	}

	@Step("Проверить, что таблица друзей содержит предложение от '{0}' и отклонить его")
	@Nonnull
	public FriendsPage declineFriendRequestFromUser(String user) {
		checkFriendsListHasRequestFromUser(user);
		SelenideElement friendRow = requests.$$("tr").find(text(user));
		friendRow.$(byText("Decline")).click();
		$("div[role='dialog']").$(byText("Decline")).click();
		checkFriendsListIsEmpty();
		return this;
	}
}
