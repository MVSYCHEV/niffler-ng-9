package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

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

	@Nonnull
	public RegisterPage fillUserName(String username) {
		usernameInput.setValue(username);
		return this;
	}

	@Nonnull
	public RegisterPage fillPassword(String password) {
		passwordInput.setValue(password);
		return this;
	}

	@Nonnull
	public RegisterPage submitPassword(String password) {
		submitPasswordInput.setValue(password);
		return this;
	}

	@Nonnull
	public RegisterPage fillRegisterPage(String username, String password) {
		fillUserName(username).fillPassword(password).submitPassword(password);
		return this;
	}

	@Nonnull
	public RegisterPage sighUp() {
		signUpButton.click();
		return this;
	}

	@Nonnull
	public LoginPage signUpThenSignIn() {
		signUpButton.click();
		signInButton.click();
		return new LoginPage();
	}

	public void checkErrorUserAlreadyExist(String userName) {
		this.errorMessage.should(visible).shouldHave(text("Username `" + userName + "` already exists"));
	}

	public void checkErrorPasswordsShouldBeEqual() {
		this.errorMessage.should(visible).shouldHave(text("Passwords should be equal"));
	}
}
