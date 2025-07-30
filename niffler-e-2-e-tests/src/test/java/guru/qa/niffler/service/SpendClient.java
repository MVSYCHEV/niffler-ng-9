package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.UUID;

public interface SpendClient {
	/**
	 * Spends
	 */
	SpendJson create(SpendJson spend);

	SpendJson update(SpendJson spend);

	SpendJson findById(UUID id);

	SpendJson findByUsernameAndSpendDescription(String username, String description);

	List<SpendJson> findAll(String username);

	void remove(SpendJson spend);

	/**
	 * Categories
	 */

	CategoryJson create(CategoryJson category);

	CategoryJson update(CategoryJson category);

	CategoryJson findCategoryById(UUID id);

	CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName);

	List<CategoryJson> findAllCategories(String username);

	void remove(CategoryJson category);
}
