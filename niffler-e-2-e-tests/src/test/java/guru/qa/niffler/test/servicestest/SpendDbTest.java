package guru.qa.niffler.test.servicestest;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SpendDbTest {
	SpendClient userClient = new SpendDbClient();
	private final String username = "robby.effertz";

	@Test
	void checkCreateSend() {
		SpendJson spendJson = userClient.create(getNewSpend("Test Spring Jdbc 11"));
		Assertions.assertNotNull(spendJson);
	}

	@Test // TODO Не работает с repository JDBC (хотя тест зеленый, но данные не попадают в базу)
	void checkSpendFindByUsernameAndUpdate() {
		SpendJson spendJson = userClient.findByUsernameAndSpendDescription(username, "Test Spring Jdbc 11");

		SpendJson updatedSpend = new SpendJson(
				spendJson.id(),
				spendJson.spendDate(),
				spendJson.category(),
				spendJson.currency(),
				777.00,
				spendJson.description(),
				spendJson.username()
		);
		SpendJson receivedSpend = userClient.update(updatedSpend);
		System.out.println(receivedSpend);
		Assertions.assertEquals(777.00, receivedSpend.amount());
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
