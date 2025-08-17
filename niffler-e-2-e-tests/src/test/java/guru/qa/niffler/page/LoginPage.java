package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement createNewAccountButton = $("#register-button");
  private final SelenideElement errorMessage = $("p.form__error");

  @Step("На странице логина ввести имя '{0}' и пароль '{1}'")
  @Nonnull
  public LoginPage fillLoginPage(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Step("Кликнуть на кнопку 'Submit'")
  @Nonnull
  public MainPage submit() {
    submitButton.click();
    return new MainPage();
  }

  @Step("Кликнуть на кнопку регистрации нового юзера")
  @Nonnull
  public RegisterPage createNewAccount() {
    createNewAccountButton.click();
    return new RegisterPage();
  }

  @Step("Проверить, что отображается текст 'Неверные учетные данные пользователя'")
  public void submitAndCheckErrorInvalidCredentials() {
    submitButton.click();
    this.errorMessage.should(visible).shouldHave(text("Неверные учетные данные пользователя"));
  }
}
