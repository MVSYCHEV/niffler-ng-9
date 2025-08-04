package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header {
	private final SelenideElement self;
	private final By profileIcon = By.cssSelector("svg[data-testid='PersonIcon']");
	private final By friendsInMenu = By.cssSelector("a[href*='/people/friends']");
	private final By allPeopleInMenu = By.cssSelector("a[href='/people/all']");
	private final By profileInMenu = By.cssSelector("li[tabindex='0']");
	private final By signOutInMenu = By.cssSelector("span.MuiTouchRipple-root.css-w0pj6f");
	private final By newSpendingButton = By.cssSelector("a[href='/spending']");
	private final By mainPage = By.cssSelector("a[href='/main']");
	private final SelenideElement menu = $("ul[role='menu']");
	private final ElementsCollection menuItems = menu.$$("li");

	public Header(@Nonnull SelenideElement self) {
		this.self = self;
	}

	public Header() {
		this.self = $("#root header");
	}

	@Step("Проверить, что в хедере отображается текст 'Niffler'")
	public void checkHeaderText() {
		self.$("h1").shouldHave(text("Niffler"));
	}

	@Step("Кликнуть на иконку профиля, кликнуть на 'Friends'")
	@Nonnull
	public FriendsPage toFriendsPage() {
		self.$(profileIcon).should(visible).click();
		menuItems.find(text("Friends")).click();
		return new FriendsPage();
	}

	@Step("Кликнуть на иконку профиля, кликнуть на 'All People'")
	@Nonnull
	public AllPeoplePage toAllPeoplesPage() {
		self.$(profileIcon).should(visible).click();
		menuItems.find(text("All people")).click();
		return new AllPeoplePage();
	}

	@Step("Кликнуть на иконку профиля, кликнуть на 'Profile'")
	@Nonnull
	public ProfilePage toProfilePage() {
		self.$(profileIcon).should(visible).click();
		menuItems.find(text("Profile")).click();
		return new ProfilePage();
	}

	@Step("Кликнуть на иконку профиля, кликнуть на 'Sign Out'")
	@Nonnull
	public LoginPage signOut() {
		self.$(profileIcon).should(visible).click();
		menuItems.find(text("Sign out")).click();
		return  new LoginPage();
	}

	@Step("Кликнуть на кнопку 'New Spending'")
	@Nonnull
	public EditSpendingPage addSpending() {
		self.$(newSpendingButton).should(visible).click();
		return new EditSpendingPage();
	}

	@Step("Кликнуть на текст 'Niffler'")
	@Nonnull
	public MainPage toMainPage() {
		self.$(mainPage).should(visible).click();
		return new MainPage();
	}
}
