package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {
	private static final SpendDao SPEND_DAO = new SpendDaoJdbc();
	private static final CategoryDao CATEGORY_DAO = new CategoryDaoJdbc();

	@Override
	public SpendEntity create(SpendEntity spend) {
		if (spend.getCategory().getId() == null || CATEGORY_DAO.findById(spend.getCategory().getId()).isEmpty()) {
			CategoryEntity categoryEntity = CATEGORY_DAO.create(spend.getCategory());
			spend.setCategory(categoryEntity);
		}
		return SPEND_DAO.create(spend);
	}

	@Override
	public SpendEntity update(SpendEntity spend) {
		SPEND_DAO.update(spend);
		CATEGORY_DAO.update(spend.getCategory());
		return spend;
	}

	@Override
	public CategoryEntity createCategory(CategoryEntity category) {
		return CATEGORY_DAO.create(category);
	}

	@Override
	public CategoryEntity update(CategoryEntity category) {
		return CATEGORY_DAO.update(category);
	}

	@Override
	public Optional<CategoryEntity> findCategoryById(UUID id) {
		return CATEGORY_DAO.findById(id);
	}

	@Override
	public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
		return CATEGORY_DAO.findByUsernameAndCategoryName(username, categoryName);
	}

	@Override
	public Optional<SpendEntity> findById(UUID id) {
		Optional<SpendEntity> spendEntity = SPEND_DAO.findSpendById(id);
		if (spendEntity.isPresent()) {
			UUID categoryId = spendEntity
					.map(spend -> spend.getCategory().getId())
					.orElseThrow(() -> new NoSuchElementException("No value present"));
			Optional<CategoryEntity> categoryEntity = CATEGORY_DAO.findById(categoryId);
			categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
		}
		return spendEntity;
	}

	@Override
	public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String spendDescription) {
		Optional<SpendEntity> spendEntity = SPEND_DAO.findByUsernameAndSpendDescription(username, spendDescription);
		if (spendEntity.isPresent()) {
			UUID categoryId = spendEntity
					.map(spend -> spend.getCategory().getId())
					.orElseThrow(() -> new NoSuchElementException("No value present"));
			Optional<CategoryEntity> categoryEntity = CATEGORY_DAO.findById(categoryId);
			categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
		}
		return spendEntity;
	}

	@Override
	public List<CategoryEntity> findAllCategoriesByUsername(String username) {
		return CATEGORY_DAO.findAllByUsername(username);
	}

	@Override
	public List<SpendEntity> findAllByUsername(String username) {
		List<SpendEntity> entities = SPEND_DAO.findAllByUsername(username);

		for (SpendEntity spend : entities) {
			UUID categoryId = spend.getCategory().getId();
			Optional<CategoryEntity> categoryEntity = CATEGORY_DAO.findById(categoryId);
			categoryEntity.ifPresent(spend::setCategory);
		}
		return entities;
	}

	@Override
	public void remove(SpendEntity spend) {
		SPEND_DAO.deleteSpend(spend);
	}

	@Override
	public void removeCategory(CategoryEntity category) {
		String username = category.getUsername();
		UUID categoryId = category.getId();

		List<SpendEntity> spends = SPEND_DAO.findAllByUsername(username);
		spends.stream()
				.filter(spendEntity -> spendEntity.getCategory().getId().equals(categoryId))
				.forEach(SPEND_DAO::deleteSpend);

		CATEGORY_DAO.deleteCategory(category);
	}
}
