package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AllPeoplePage {
	private final SelenideElement allTable = $("#all");

	@Step("Проверить, что таблица 'All people' отображается")
	@Nonnull
	public AllPeoplePage checkThatPageLoaded() {
		allTable.should(visible);
		return this;
	}

	@Step("Проверить через поиск, что таблица 'All people' содержит имя '{0}'")
	public void checkAllTableHasOutcomeRequestToUser(String userName) {
		allTable.$$("tbody tr").get(0).$("td").shouldHave(text(userName));
	}
}
