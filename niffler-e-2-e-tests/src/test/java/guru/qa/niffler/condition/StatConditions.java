package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.page.component.Widget;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

	@Nonnull
	public static WebElementCondition color(Color expectedColor) {
		return new WebElementCondition("color " + expectedColor.rgb) {
			@NotNull
			@Override
			public CheckResult check(Driver driver, WebElement element) {
				final String rgba = element.getCssValue("background-color");
				return new CheckResult(
						expectedColor.rgb.equals(rgba),
						rgba
				);
			}
		};
	}

	@Nonnull
	public static WebElementsCondition statWidgetContains(Widget... widgets) {
		return new WebElementsCondition() {
			private final String expectedBubblesString = Arrays.stream(widgets).toList().toString();

			@Nonnull
			@Override
			public CheckResult check(Driver driver, List<WebElement> elements) {
				if (ArrayUtils.isEmpty(widgets)) {
					throw new IllegalArgumentException("No expected widgets given");
				}

				final List<Widget> actualWidgets = elements.stream()
						.map(widget -> new Widget(
										Color.getFromRgba(widget.getCssValue("background-color")),
										widget.getText()
								)
						).toList();
				final List<Widget> expectedWidgets = Arrays.stream(widgets).toList();
				boolean passed = actualWidgets.containsAll(expectedWidgets);

				if (!passed) {
					final String message = String.format(
							"Widgets mismatch (expected: %s, actual: %s)", expectedWidgets, actualWidgets
					);
					return rejected(message, actualWidgets);
				}
				return accepted();
			}

			@Nonnull
			@Override
			public String toString() {
				return expectedBubblesString;
			}
		};
	}

	@Nonnull
	public static WebElementsCondition statWidgetInAnyOrder(@Nonnull Widget... widgets) {
		return new WebElementsCondition() {
			private final String expectedBubblesString = Arrays.stream(widgets).toList().toString();

			@NotNull
			@Override
			public CheckResult check(Driver driver, List<WebElement> elements) {
				if (ArrayUtils.isEmpty(widgets)) {
					throw new IllegalArgumentException("No expected widgets given");
				}
				if (widgets.length != elements.size()) {
					final String message = String.format("List size mismatch (expected: %s, actual: %s)", widgets.length, elements.size());
					return rejected(message, elements);
				}

				final List<Widget> actualWidgets = elements.stream()
						.map(widget -> new Widget(
										Color.getFromRgba(widget.getCssValue("background-color")),
										widget.getText()
								)
						).toList();
				final List<Widget> expectedWidgets = Arrays.stream(widgets).toList();
				boolean passed = actualWidgets.size() == expectedWidgets.size() && actualWidgets.containsAll(expectedWidgets);

				if (!passed) {
					final String message = String.format(
							"Widgets mismatch (expected: %s, actual: %s)", expectedWidgets, actualWidgets
					);
					return rejected(message, actualWidgets);
				}
				return accepted();
			}

			@Override
			public String toString() {
				return expectedBubblesString;
			}
		};
	}

	public static WebElementsCondition statWidgets(Widget... widgets) {
		return new WebElementsCondition() {
			private final String expectedBubbles = Arrays.stream(widgets).toList().toString();

			@NotNull
			@Override
			public CheckResult check(Driver driver, List<WebElement> elements) {
				if (ArrayUtils.isEmpty(widgets)) {
					throw new IllegalArgumentException("No expected widgets given");
				}
				if (widgets.length != elements.size()) {
					final String message = String.format("List size mismatch (expected: %s, actual: %s)", widgets.length, elements.size());
					return rejected(message, elements);
				}

				boolean passed = true;
				final List<Widget> actualWidgets = new ArrayList<>();

				for (int i = 0; i < elements.size(); i++) {
					final WebElement elementToCheck = elements.get(i);
					final Color expectedColor = widgets[i].color();
					final String expectedText = widgets[i].text();

					final String actualColor = elementToCheck.getCssValue("background-color");
					final String actualText = elementToCheck.getText();

					actualWidgets.add(new Widget(Color.getFromRgba(actualColor), actualText));
					if (passed) {
						passed = expectedColor.rgb.equals(actualColor) && expectedText.equals(actualText);
					}
				}

				if (!passed) {
					final String message = String.format(
							"Widgets mismatch (expected: %s, actual: %s)", expectedBubbles, actualWidgets
					);
					return rejected(message, actualWidgets);
				}
				return accepted();
			}

			@NotNull
			@Override
			public String toString() {
				return expectedBubbles;
			}
		};
	}

	@Nonnull
	public static WebElementsCondition color(@Nonnull Color... expectedColors) {
		return new WebElementsCondition() {

			private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

			@NotNull
			@Override
			public CheckResult check(Driver driver, List<WebElement> elements) {
				if (ArrayUtils.isEmpty(expectedColors)) {
					throw new IllegalArgumentException("No expected colors given");
				}
				if (expectedColors.length != elements.size()) {
					final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
					return rejected(message, elements);
				}

				boolean passed = true;
				final List<String> actualRgbaList = new ArrayList<>();
				for (int i = 0; i < elements.size(); i++) {
					final WebElement elementToCheck = elements.get(i);
					final Color colorToCheck = expectedColors[i];
					final String rgba = elementToCheck.getCssValue("background-color");
					actualRgbaList.add(rgba);
					if (passed) {
						passed = colorToCheck.rgb.equals(rgba);
					}
				}

				if (!passed) {
					final String actualRgba = actualRgbaList.toString();
					final String message = String.format(
							"List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
					);
					return rejected(message, actualRgba);
				}
				return accepted();
			}

			@NotNull
			@Override
			public String toString() {
				return expectedRgba;
			}
		};
	}
}
