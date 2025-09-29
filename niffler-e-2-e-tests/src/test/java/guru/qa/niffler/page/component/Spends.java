package guru.qa.niffler.page.component;

import guru.qa.niffler.model.spend.SpendJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record Spends(
	String category,
	String amount,
	String description,
	String date
) {
	public static Spends fromJson(SpendJson spendJson) {
		return new Spends(
				spendJson.category().name(),
				spendJson.amount() + " ₽",
				spendJson.description(),
				LocalDate.parse(spendJson.spendDate().toString()).format(DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH))
		);
	}
}
