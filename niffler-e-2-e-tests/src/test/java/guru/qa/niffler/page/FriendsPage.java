package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
	private final SelenideElement friendsTable = $("#simple-tabpanel-friends");
	private final SelenideElement requests = $("#requests");
	private final SelenideElement thereAreNoUsersYetTitle = $("p.MuiTypography-root.MuiTypography-h6.css-1m7obeg");

	public FriendsPage checkThatPageLoaded() {
		friendsTable.should(visible);
		return this;
	}

	public void checkFriendByName(String friendName) {
		friendsTable.$$("tbody tr").find(text(friendName)).should(visible);
	}

	public void checkFriendsListIsEmpty() {
		thereAreNoUsersYetTitle.should(visible);
	}

	public void checkFriendsListHasRequestFromUser(String userName) {
		requests.$$("tbody tr").find(text(userName)).should(visible);
	}
}
