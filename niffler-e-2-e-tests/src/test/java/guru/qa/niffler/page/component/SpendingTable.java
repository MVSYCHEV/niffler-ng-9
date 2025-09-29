package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.condition.StatConditions;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendsCondition.spends;
import static guru.qa.niffler.condition.StatConditions.color;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class SpendingTable {
	private final SelenideElement self;
	private final By periodButton = By.id("period");
	private final By deleteButton = By.id("delete");
	private final By deleteButtonAlert = By.cssSelector("span.MuiTouchRipple-root.css-w0pj6f");
	private final By searchInput = By.cssSelector("input.MuiInputBase-input.css-mnn31");
	private final ElementsCollection widgets = $("#legend-container").$$("li");
	private final SelenideElement spendingDiagram = $("canvas[role='img']");
	private final ElementsCollection tableRows = get().$$("tbody tr");

	public SpendingTable(@Nonnull SelenideElement self) {
		this.self = self;
	}

	public SpendingTable() {
		this.self = $("#spendings");
	}

	public SelenideElement get() {
		return self;
	}

	@Step("Кликнуть на кнопку фильтра периода и выбрать '{0}'")
	@Nonnull
	public SpendingTable selectPeriod(DataFilterValues period) {
		self.$(periodButton).should(visible).click();
		self.$("li[data-value=" + period + "]").should(visible).click();
		return this;
	}

	@Step("В таблице расходов найти расход в описанием '{0}' и нажать на иконку карандашика")
	@Nonnull
	public EditSpendingPage editSpending(String description) {
		self.$$("tbody tr").find(text(description))
				.$$("td")
				.get(5)
				.click();
		return new EditSpendingPage();
	}

	@Step("В таблице расходов найти расход в описанием '{0}', кликнуть на описание, кликнуть на кнопку 'Delete' и " +
			"подтвердить удаление")
	@Nonnull
	public SpendingTable deleteSpending(String description) {
		self.$$("tbody tr").find(text(description)).should(visible).click();
		self.$(deleteButton).click();
		self.$(deleteButtonAlert).should(visible).click();
		return this;
	}

	@Step("В таблице расходов найти через поиск расход '{0}'")
	@Nonnull
	public SpendingTable searchSpendingByDescription(String description) {
		self.$(searchInput).should(visible).setValue(description).pressEnter();
		ElementsCollection rows = self.$$("tbody tr");
		SelenideElement descriptionRow = rows.findBy(text(description));
		descriptionRow.should(visible);
		return this;
	}

	@Nonnull
	public SpendingTable checkTableContains(String... expectedSpends) {
		for (String spends : expectedSpends) {
			searchSpendingByDescription(spends);
		}
		return this;
	}

	@Step("Проверить, что таблица расходов содержит '{0}' расходов")
	@Nonnull
	public SpendingTable checkTableSize(int expectedSize) {
		self.$("tbody").$$("tr").shouldHave(size(expectedSize));
		return this;
	}

	@Step("Виджеты под диаграммой расходов содержат текст '{widgetsTexts}'")
	public SpendingTable checkWidgets(String... widgetsTexts) {
		widgets.shouldHave(texts(widgetsTexts));
		return this;
	}

	@Step("Проверить виджеты в порядке '{0}'")
	public SpendingTable checkBubbles(Widget... expectedWidgets) {
		widgets.shouldHave(StatConditions.statWidgets(expectedWidgets));
		return this;
	}

	@Step("Проверить виджеты в любом порядке '{0}'")
	public SpendingTable checkBubblesInAnyOrder(Widget... expectedWidgets) {
		widgets.shouldHave(StatConditions.statWidgetInAnyOrder(expectedWidgets));
		return this;
	}

	@Step("Проверить, что виджеты содержат '{0}'")
	public SpendingTable checkBubbleContains(Widget... expectedWidgets) {
		widgets.shouldHave(StatConditions.statWidgetContains(expectedWidgets));
		return this;
	}

	@Step("Проверить изображение диаграммы расходов")
	public SpendingTable checkSpendingDiagram(String expectedImagePath) throws IOException {
		BufferedImage expected = ImageIO.read(new ClassPathResource(expectedImagePath).getInputStream());
		Selenide.sleep(5000);
		assertFalse(new ScreenDiffResult(
						expected,
						spendingDiagramScreenshot()
				),
				ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE);
		return this;
	}

	@Step("Проверить, что виджеты содержат цвета {expectedColors}")
	@Nonnull
	public SpendingTable checkWidgets(Color... expectedColors) {
		widgets.should(color(expectedColors));
		return this;
	}

	@Step("Получить скриншот диаграммы расходов")
	@Nonnull
	public BufferedImage spendingDiagramScreenshot() throws IOException {
		return ImageIO.read(requireNonNull(spendingDiagram.screenshot()));
	}

	@Step("Проверить, что в таблицы отображаются расходы '{0}'")
	public SpendingTable checkSpends(SpendJson... expectedSpends) {
		tableRows.shouldHave(spends(expectedSpends));
		return this;
	}
}
