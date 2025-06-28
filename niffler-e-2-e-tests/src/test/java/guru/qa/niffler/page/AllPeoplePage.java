package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {
	private final SelenideElement allTable = $("#all");

	public AllPeoplePage checkThatPageLoaded() {
		allTable.should(visible);
		return this;
	}

	public void checkAllTableHasOutcomeRequestToUser(String userName) {
		allTable.$$("tbody tr").get(0).$$("td").get(0).shouldHave(text(userName));
	}
}
