package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage {
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement createNewAccountButton = $("#register-button");
  private final SelenideElement errorMessage = $("p.form__error");

  @Nonnull
  public LoginPage fillLoginPage(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Nonnull
  public MainPage submit() {
    submitButton.click();
    return new MainPage();
  }

  @Nonnull
  public RegisterPage createNewAccount() {
    createNewAccountButton.click();
    return new RegisterPage();
  }

  public void submitAndCheckErrorInvalidCredentials() {
    submitButton.click();
    this.errorMessage.should(visible).shouldHave(text("Неверные учетные данные пользователя"));
  }
}
