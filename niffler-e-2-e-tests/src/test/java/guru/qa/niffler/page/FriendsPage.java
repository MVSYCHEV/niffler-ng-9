package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
	private final SelenideElement friendsTable = $("#simple-tabpanel-friends");
	private final SelenideElement requests = $("#requests");
	private final SelenideElement thereAreNoUsersYetTitle = $("p.MuiTypography-root.MuiTypography-h6.css-1m7obeg");
	private final SelenideElement nextButton = $(By.id("page-next"));

	public FriendsPage checkThatPageLoaded() {
		friendsTable.should(visible);
		return this;
	}

	public void checkFriendByName(String friendName) {
		while (true) {
			ElementsCollection rows = friendsTable.$$("tbody tr");
			SelenideElement friendRow = rows.findBy(text(friendName));

			if (friendRow.exists()) {
				friendRow.shouldBe(visible);
				return;
			}
			if (nextButton.is(visible)) {
				nextButton.click();
			} else {
				break;
			}
		}
	}

	public void checkFriendsListIsEmpty() {
		thereAreNoUsersYetTitle.should(visible);
	}

	public void checkFriendsListHasRequestFromUser(String userName) {
		requests.$$("tbody tr").find(text(userName)).should(visible);
	}
}
