package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField {
	private final SelenideElement clearSearchInputBtn = $("#input-clear");
	private final SelenideElement self;

	public SearchField(@Nonnull SelenideElement self) {
		this.self = self;
	}

	public SearchField() {
		this.self = $("input[aria-label='search']");
	}

	@Step("В строке поиска задать текст '{0}'")
	@Nonnull
	public SearchField search(@Nonnull String text) {
		clearIfNotEmpty();
		self.setValue(text).pressEnter();
		return this;
	}

	@Step("Очистить строку поиска кликом на иконку крестика")
	@Nonnull
	public SearchField clearIfNotEmpty() {
		if (self.is(not(empty))) {
			clearSearchInputBtn.should(visible).click();
			self.should(empty);
		}
		return this;
	}
}
