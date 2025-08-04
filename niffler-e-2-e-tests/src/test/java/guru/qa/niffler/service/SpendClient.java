package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendClient {
	/**
	 * Spends
	 */
	@Nullable
	SpendJson create(SpendJson spend);

	@Nullable
	SpendJson update(SpendJson spend);

	@Nullable
	SpendJson findById(UUID id);

	@Nullable
	SpendJson findByUsernameAndSpendDescription(String username, String description);

	@Nonnull
	List<SpendJson> findAll(String username);

	void remove(SpendJson spend);

	/**
	 * Categories
	 */

	@Nullable
	CategoryJson create(CategoryJson category);

	@Nullable
	CategoryJson update(CategoryJson category);

	@Nullable
	CategoryJson findCategoryById(UUID id);

	@Nullable
	CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName);

	@Nonnull
	List<CategoryJson> findAllCategories(String username);

	void remove(CategoryJson category);
}
