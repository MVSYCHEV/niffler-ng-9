package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendClient {
	/**
	 * Spends
	 */
	@Nonnull
	SpendJson create(SpendJson spend);

	@Nonnull
	SpendJson update(SpendJson spend);

	@Nonnull
	SpendJson findById(UUID id);

	@Nonnull
	SpendJson findByUsernameAndSpendDescription(String username, String description);

	@Nonnull
	List<SpendJson> findAll(String username);

	void remove(SpendJson spend);

	/**
	 * Categories
	 */

	@Nonnull
	CategoryJson create(CategoryJson category);

	@Nonnull
	CategoryJson update(CategoryJson category);

	@Nonnull
	CategoryJson findCategoryById(UUID id);

	@Nonnull
	CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName);

	@Nonnull
	List<CategoryJson> findAllCategories(String username);

	void remove(CategoryJson category);
}
