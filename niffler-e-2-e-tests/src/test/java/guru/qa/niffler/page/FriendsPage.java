package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage {
	private final SelenideElement friendsTable = $("#simple-tabpanel-friends");
	private final SelenideElement requests = $("#requests");
	private final SelenideElement thereAreNoUsersYetTitle = $("p.MuiTypography-root.MuiTypography-h6.css-1m7obeg");
	private final SelenideElement searchInput = $("input.MuiInputBase-input.css-mnn31");

	@Nonnull
	public FriendsPage checkThatPageLoaded() {
		friendsTable.should(visible);
		return this;
	}

	public void checkFriendByName(String friendName) {
		searchInput.should(visible).setValue(friendName).pressEnter();
		ElementsCollection rows = friendsTable.$$("tbody tr");
		SelenideElement friendRow = rows.findBy(text(friendName));
		friendRow.should(visible);
	}

	public void checkFriendsListIsEmpty() {
		thereAreNoUsersYetTitle.should(visible);
	}

	public void checkFriendsListHasRequestFromUser(String userName) {
		requests.$$("tbody tr").find(text(userName)).should(visible);
	}
}
