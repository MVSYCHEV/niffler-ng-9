package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

	SpendEntity create(SpendEntity spend);

	SpendEntity update(SpendEntity spend);

	CategoryEntity createCategory(CategoryEntity category);

	CategoryEntity update(CategoryEntity category);

	Optional<CategoryEntity> findCategoryById(UUID id);

	Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

	List<SpendEntity> findAllByUsername(String username);

	Optional<SpendEntity> findById(UUID id);

	Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription);

	List<CategoryEntity> findAllCategoriesByUsername(String username);

	void remove(SpendEntity spend);

	void removeCategory(CategoryEntity category);
}
