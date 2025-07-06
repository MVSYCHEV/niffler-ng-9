package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
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
		return transaction(connection -> {
					Optional<SpendEntity> spendEntity = new SpendDaoJdbc(connection)
							.findSpendById(id);
					SpendJson spendJson = null;
					if (!spendEntity.isEmpty()) {
						spendJson = SpendJson.fromEntity(spendEntity.get());
					}
					return spendJson;
				},
				CFG.spendJdbcUrl()
		);
	}

	public List<SpendJson> getSpends(String username) {
		return transaction(connection -> {
					List<SpendEntity> entities = new SpendDaoJdbc(connection).findAllByUsername(username);
					return entities.stream()
							.map(entity -> SpendJson.fromEntity(entity))
							.collect(Collectors.toList());
				},
				CFG.spendJdbcUrl()
		);
	}

	public void deleteSpend(SpendEntity spend) {
		transaction(connection -> {
					new SpendDaoJdbc(connection).deleteSpend(spend);
				},
				CFG.spendJdbcUrl()
		);
	}

	public CategoryJson createCategory(CategoryJson categoryJson) {
		return transaction(connection -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
					return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(categoryEntity));
				},
				CFG.spendJdbcUrl()
		);
	}

	public CategoryJson getCategory(String username, String categoryName) {
		return transaction(connection -> {
					Optional<CategoryEntity> categoryEntity =
							new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				},
				CFG.spendJdbcUrl()
		);
	}

	public List<CategoryJson> getCategories(String username) {
		return transaction(connection -> {
					List<CategoryEntity> entities = new CategoryDaoJdbc(connection).findAllByUsername(username);
					return entities.stream()
							.map(entity -> CategoryJson.fromEntity(entity))
							.collect(Collectors.toList());
				},
				CFG.spendJdbcUrl());
	}

	public void deleteCategory(CategoryEntity category) {
		transaction(connection -> {
					String username = category.getUsername();
					UUID categoryId = category.getId();

					getSpends(username).stream()
							.filter(spendJson -> spendJson.category().id().equals(categoryId))
							.forEach(spendJson -> new SpendDaoJdbc(connection).deleteSpend(SpendEntity.fromJson(spendJson)));

					new CategoryDaoJdbc(connection).deleteCategory(category);
				},
				CFG.spendJdbcUrl());
	}
}
