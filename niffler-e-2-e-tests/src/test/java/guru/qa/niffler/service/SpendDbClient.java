package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
	private static final Config CFG = Config.getInstance();

//	public SpendJson createSpend(SpendJson spend) {
//		SpendEntity spendEntity = SpendEntity.fromJson(spend);
//		if (spendEntity.getCategory().getId() == null) {
//			CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
//			spendEntity.setCategory(categoryEntity);
//		}
//		return SpendJson.fromEntity(
//				spendDao.create(spendEntity)
//		);
//	}

	public SpendJson createSpend(SpendJson spend) {
		return transaction(connection -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					if (spendEntity.getCategory().getId() == null) {
						CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
								.create(spendEntity.getCategory());
						spendEntity.setCategory(categoryEntity);
					}
					return SpendJson.fromEntity(
							new SpendDaoJdbc(connection).create(spendEntity)
					);
				},
				CFG.spendJdbcUrl()
		);
	}


	public SpendJson getSpend(UUID id) {
		Optional<SpendEntity> spendEntity = spendDao.findSpendById(id);
		SpendJson spendJson = null;
		if (!spendEntity.isEmpty()) {
			spendJson = SpendJson.fromEntity(spendEntity.get());
		}
		return spendJson;
	}

	public List<SpendJson> getSpends(String username) {
		List<SpendEntity> entities = spendDao.findAllByUsername(username);
		return entities.stream()
				.map(entity -> SpendJson.fromEntity(entity))
				.collect(Collectors.toList());
	}

	public void deleteSpend(SpendEntity spend) {
		spendDao.deleteSpend(spend);
	}

	public CategoryJson createCategory(CategoryJson categoryJson) {
		CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
		return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
	}

	public CategoryJson getCategory(String username, String categoryName) {
		Optional<CategoryEntity> categoryEntity =
				categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
		CategoryJson categoryJson = null;
		if (!categoryEntity.isEmpty()) {
			categoryJson = CategoryJson.fromEntity(categoryEntity.get());
		}
		return categoryJson;
	}

	public List<CategoryJson> getCategories(String username) {
		List<CategoryEntity> entities = categoryDao.findAllByUsername(username);
		return entities.stream()
				.map(entity -> CategoryJson.fromEntity(entity))
				.collect(Collectors.toList());
	}

	public void deleteCategory(CategoryEntity category) {
		String username = category.getUsername();
		UUID categoryId = category.getId();

		getSpends(username).stream()
				.filter(spendJson -> spendJson.category().id().equals(categoryId))
				.forEach(spendJson -> spendDao.deleteSpend(SpendEntity.fromJson(spendJson)));

		categoryDao.deleteCategory(category);
	}
}
