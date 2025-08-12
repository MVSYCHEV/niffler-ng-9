package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage {
	private final SelenideElement usernameInput = $("#username");
	private final SelenideElement passwordInput = $("#password");
	private final SelenideElement submitPasswordInput = $("#passwordSubmit");
	private final SelenideElement signUpButton = $("#register-button");
	private final SelenideElement signInButton = $("a.form_sign-in");
	private final SelenideElement errorMessage = $("span.form__error");

	@Step("Ввести имя '{0}'")
	@Nonnull
	public RegisterPage fillUserName(String username) {
		usernameInput.setValue(username);
		return this;
	}

	@Step("Ввести пароль '{0}'")
	@Nonnull
	public RegisterPage fillPassword(String password) {
		passwordInput.setValue(password);
		return this;
	}

	@Step("Подтвердить пароль '{0}'")
	@Nonnull
	public RegisterPage submitPassword(String password) {
		submitPasswordInput.setValue(password);
		return this;
	}

	@Step("Заполнить страницу регистрации")
	@Nonnull
	public RegisterPage fillRegisterPage(String username, String password) {
		fillUserName(username).fillPassword(password).submitPassword(password);
		return this;
	}

	@Step("Кликнуть на Sign Up")
	@Nonnull
	public RegisterPage sighUp() {
		signUpButton.click();
		return this;
	}

	@Step("Кликнуть на Sign Up, затем на Sign In")
	@Nonnull
	public LoginPage signUpThenSignIn() {
		signUpButton.click();
		signInButton.click();
		return new LoginPage();
	}

	@Step("Проверить, что отображается ошибка Username '{0}' already exists")
	public void checkErrorUserAlreadyExist(String userName) {
		this.errorMessage.should(visible).shouldHave(text("Username `" + userName + "` already exists"));
	}

	@Step("Проверить, что отображается ошибка Passwords should be equal")
	public void checkErrorPasswordsShouldBeEqual() {
		this.errorMessage.should(visible).shouldHave(text("Passwords should be equal"));
	}
}
