package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.CurrencyValues;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitButton = $("#save");
  private final SelenideElement inputAmount = $("#amount");
  private final SelenideElement currencyInput = $("#currency");
  private final SelenideElement categoryInput = $("#category");

  @Step("Задать расходу новое описание '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Задать расходу сумму '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingAmount(double amount) {
    inputAmount.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Выбрать валюту расхода '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingCurrency(CurrencyValues currency) {
    currencyInput.click();
    $$("li[role='option']").find(text(currency.name())).click();
    return this;
  }

  @Step("Задать расходу категорию '{0}'")
  @Nonnull
  public EditSpendingPage setNewSpendingCategory(String category) {
    categoryInput.clear();
    categoryInput.setValue(category);
    return this;
  }

  @Step("Нажать кнопку 'Save'")
  @Nonnull
  public MainPage save() {
    submitButton.click();
    return new MainPage();
  }
}
