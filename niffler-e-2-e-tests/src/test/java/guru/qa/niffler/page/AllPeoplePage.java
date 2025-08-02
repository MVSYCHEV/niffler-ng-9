package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AllPeoplePage {
	private final SelenideElement allTable = $("#all");

	@Nonnull
	public AllPeoplePage checkThatPageLoaded() {
		allTable.should(visible);
		return this;
	}

	public void checkAllTableHasOutcomeRequestToUser(String userName) {
		allTable.$$("tbody tr").get(0).$("td").shouldHave(text(userName));
	}
}
