package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendingTable {
	private final SelenideElement self;
	private final By periodButton = By.id("period");
	private final By deleteButton = By.id("delete");
	private final By deleteButtonAlert = By.cssSelector("span.MuiTouchRipple-root.css-w0pj6f");
	private final By searchInput = By.cssSelector("input.MuiInputBase-input.css-mnn31");

	public SpendingTable(@Nonnull SelenideElement self) {
		this.self = self;
	}

	public SpendingTable() {
		this.self = $("#spendings");
	}

	@Step("Кликнуть на кнопку фильтра периода и выбрать '{0}'")
	@Nonnull
	public SpendingTable selectPeriod(DataFilterValues period) {
		self.$(periodButton).should(visible).click();
		self.$("li[data-value=" + period + "]").should(visible).click();
		return this;
	}

	@Step("В таблице расходов найти расход в описанием '{0}' и нажать на иконку карандашика")
	@Nonnull
	public EditSpendingPage editSpending(String description) {
		self.$$("tbody tr").find(text(description))
				.$$("td")
				.get(5)
				.click();
		return new EditSpendingPage();
	}

	@Step("В таблице расходов найти расход в описанием '{0}', кликнуть на описание, кликнуть на кнопку 'Delete' и " +
			"подтвердить удаление")
	@Nonnull
	public SpendingTable deleteSpending(String description) {
		self.$$("tbody tr").find(text(description)).should(visible).click();
		self.$(deleteButton).click();
		self.$(deleteButtonAlert).should(visible).click();
		return this;
	}

	@Step("В таблице расходов найти через поиск расход '{0}'")
	@Nonnull
	public SpendingTable searchSpendingByDescription(String description) {
		self.$(searchInput).should(visible).setValue(description).pressEnter();
		ElementsCollection rows = self.$$("tbody tr");
		SelenideElement descriptionRow = rows.findBy(text(description));
		descriptionRow.should(visible);
		return this;
	}

	@Nonnull
	public SpendingTable checkTableContains(String... expectedSpends) {
		for (String spends : expectedSpends) {
			searchSpendingByDescription(spends);
		}
		return this;
	}

	@Step("Проверить, что таблица расходов содержит '{0}' расходов")
	@Nonnull
	public SpendingTable checkTableSize(int expectedSize) {
		self.$("tbody").$$("tr").shouldHave(size(expectedSize));
		return this;
	}
}
