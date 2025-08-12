package guru.qa.niffler.page.component;


import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class Calendar {
	private final SelenideElement self;

	public Calendar(@Nonnull SelenideElement self) {
		this.self = self;
	}

	@Step("В календаре выбрать дату '{0}'")
	@Nonnull
	public Calendar selectDateInCalendar(Date date) {
		long timestamp = date.getTime();
		self.$("button[data-timestamp=" + timestamp + "]").should(visible).click();
		return this;
	}
}
