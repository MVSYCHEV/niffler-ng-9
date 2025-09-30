package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.component.Spends;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class SpendsCondition {
	public static WebElementsCondition spends(SpendJson... expectedSpends) {
		return new WebElementsCondition() {
			private final List<Spends> expectedSpendsCollection =
					Arrays.stream(expectedSpends).map(Spends::fromJson).toList();

			@NotNull
			@Override
			public CheckResult check(@NotNull Driver driver, @NotNull List<WebElement> elements) {
				if (ArrayUtils.isEmpty(expectedSpends)) {
					throw new IllegalArgumentException("No expected spends given");
				}

				boolean passed = true;
				List<Spends> actualSpendsCollection = new ArrayList<>();

				for (int i = 0; i < elements.size(); i++) {
					final Spends expectedSpend = expectedSpendsCollection.get(i);
					final WebElement elementToCheck = elements.get(i);

					Spends actualSpend = extractSpendFromElement(elementToCheck);
					actualSpendsCollection.add(actualSpend);

					if (passed) {
						passed = actualSpend.equals(expectedSpend);
					}
				}

				if (!passed) {
					final String actualSpends = actualSpendsCollection.toString();
					final String message = String.format(
							"List of spends mismatch (expected: %s, actual: %s)", expectedSpendsCollection, actualSpends
					);
					return rejected(message, actualSpends);
				}
				return accepted();
			}

			private Spends extractSpendFromElement(WebElement element) {
				List<String> cellValues = extractCellValues(element);
				return new Spends(
						cellValues.get(0),
						cellValues.get(1),
						cellValues.get(2),
						cellValues.get(3)
				);
			}

			private List<String> extractCellValues(WebElement element) {
				return IntStream.range(2, 6)
						.mapToObj(i -> element.findElement(By.cssSelector("td:nth-child(" + i + ")")))
						.map(WebElement::getText)
						.toList();
			}

			@NotNull
			@Override
			public String toString() {
				return expectedSpendsCollection.toString();
			}
		};
	}
}
