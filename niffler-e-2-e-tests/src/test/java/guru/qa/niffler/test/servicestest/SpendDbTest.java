package guru.qa.niffler.test.servicestest;

import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Order(3)
@Isolated
@ExtendWith(ClientResolver.class)
public class SpendDbTest {
	SpendClient userClient;
	private final String username = "robby.effertz";

	@Test
	void checkCreateSend() {
		SpendJson spendJson = userClient.create(getNewSpend("Test Spring Jdbc 13"));
		Assertions.assertNotNull(spendJson);
	}

	@Test
	void checkSpendFindByUsernameAndUpdate() {
		SpendJson spendJson = userClient.findByUsernameAndSpendDescription(username, "Test Spring Jdbc 13");

		double newAmount = 676.00;
		SpendJson updatedSpend = new SpendJson(
				spendJson.id(),
				spendJson.spendDate(),
				spendJson.category(),
				spendJson.currency(),
				newAmount,
				spendJson.description(),
				spendJson.username()
		);
		userClient.update(updatedSpend);
		SpendJson updateJson = userClient.findByUsernameAndSpendDescription(username, "Test Jdbc 12");
		Assertions.assertEquals(newAmount, updateJson.amount());
	}

	@Test
	void checkSpendFindById() {
		final String description = "Check Find By Id Spring Jdbc";
		SpendJson spendJson = userClient.create(getNewSpend(description));
		UUID id = spendJson.id();
		SpendJson spendJsonById = userClient.findById(id);
		Assertions.assertEquals(description, spendJsonById.description());
	}

	@Test
	void checkAllSpends() {
		List<SpendJson> allSpends = userClient.findAll(username);
		System.out.println(allSpends.size());
		Assertions.assertFalse(allSpends.isEmpty());
	}

	@Test
	void checkRemoveSpend() {
		SpendJson spendJson = userClient.create(getNewSpend("Spend Removing"));
		UUID id = spendJson.id();
		userClient.remove(spendJson);
		SpendJson afterRemoving = userClient.findById(id);
		Assertions.assertNull(afterRemoving);
	}

	@Test
	void checkCategoryCreate() {
		CategoryJson categoryJson = userClient.create(getNewCategory());
		Assertions.assertNotNull(categoryJson);
	}

	@Test
	void checkCategoryFindByUsernameAndUpdate() {
		CategoryJson categoryJson = userClient.findCategoryByUsernameAndCategoryName(username, "Practical Silk Shoes");
		CategoryJson updatedCategory = new CategoryJson(
				categoryJson.id(),
				categoryJson.name(),
				categoryJson.username(),
				false
		);

		CategoryJson receivedCategory = userClient.update(updatedCategory);
		Assertions.assertFalse(receivedCategory.archived());
	}

	@Test
	void checkCategoryFindById() {
		CategoryJson categoryJson = userClient.create(getNewCategory());
		UUID id = categoryJson.id();
		CategoryJson categoryById = userClient.findCategoryById(id);
		Assertions.assertEquals(categoryJson.name(), categoryById.name());
	}

	@Test
	void checkCategoriesFindAll() {
		List<CategoryJson> categories = userClient.findAllCategories(username);
		System.out.println(categories.size());
		Assertions.assertFalse(categories.isEmpty());
	}

	@Test
	void checkRemoveCategory() {
		CategoryJson categoryJson = userClient.create(getNewCategory());
		UUID id = categoryJson.id();
		userClient.remove(categoryJson);
		CategoryJson categoryAfterRemoving = userClient.findCategoryById(id);
		Assertions.assertNull(categoryAfterRemoving);
	}

	@Test
	void checkRemoveCategoryWithSpend() {
		SpendJson spendJson = userClient.create(getNewSpend("123"));
		UUID spendId = spendJson.id();
		CategoryJson categoryJson = spendJson.category();
		UUID categoryId = categoryJson.id();
		userClient.remove(categoryJson);
		SpendJson afterRemovingSpend = userClient.findById(spendId);
		CategoryJson afterRemovingCategory = userClient.findCategoryById(categoryId);
		Assertions.assertNull(afterRemovingSpend);
		Assertions.assertNull(afterRemovingCategory);
	}

	private CategoryJson getNewCategory() {
		return new CategoryJson(
				null,
				RandomDataUtils.randomCategoryName(),
				username,
				true
		);
	}

	private SpendJson getNewSpend(String spendDescription) {
		return new SpendJson(
				null,
				new Date(),
				new CategoryJson(
						null,
						RandomDataUtils.randomCategoryName(),
						username,
						true
				),
				CurrencyValues.USD,
				100.00,
				spendDescription,
				username
		);
	}

}
