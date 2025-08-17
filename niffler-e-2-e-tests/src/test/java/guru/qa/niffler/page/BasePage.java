package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;

public abstract class BasePage<T extends BasePage<?>> {
	protected final SelenideElement alert = Selenide.$("div[role='alert'] div.MuiAlert-message");

	@SuppressWarnings("unchecked")
	@Step("Проверить, что появился алерт с текстом '{0}'")
	public T checkAlert(String text) {
		alert.shouldHave(text(text));
		return (T) this;
	}
}
