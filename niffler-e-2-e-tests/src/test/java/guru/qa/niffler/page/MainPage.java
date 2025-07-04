package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
  private final SelenideElement spendingTable = $("#spendings");
  private final SelenideElement profileIcon = $("svg[data-testid='PersonIcon']");
  private final SelenideElement profileInMenu = $("li[tabindex='0']");
  private final SelenideElement friendsInMenu = $("a[href='/people/friends']");
  private final SelenideElement allPeopleInMenu = $("a[href='/people/all']");

  public MainPage checkThatPageLoaded() {
    spendingTable.should(visible);
    return this;
  }

  public EditSpendingPage editSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public MainPage checkThatTableContainsSpending(String description) {
    spendingTable.$$("tbody tr").find(text(description))
        .should(visible);
    return this;
  }

  public ProfilePage openProfile() {
    profileIcon.click();
    profileInMenu.click();
    return new ProfilePage();
  }

  public FriendsPage openFriends() {
    profileIcon.click();
    friendsInMenu.click();
    return new FriendsPage();
  }

  public AllPeoplePage openAllPeople() {
    profileIcon.click();
    allPeopleInMenu.click();
    return new AllPeoplePage();
  }
}
