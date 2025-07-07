package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.sql.Connection;
import java.util.List;
import java.util.NoSuchElementException;
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
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public SpendJson getSpend(UUID id) {
		return transaction(connection -> {
					Optional<SpendEntity> spendEntity = new SpendDaoJdbc(connection)
							.findSpendById(id);
					UUID categoryId = spendEntity
							.map(spend -> spend.getCategory().getId())
							.orElseThrow(() -> new NoSuchElementException("No value present"));
					Optional<CategoryEntity> categoryEntity = new CategoryDaoJdbc(connection).findById(categoryId);
					categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
					return SpendJson.fromEntity(spendEntity.get());
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public List<SpendJson> getSpends(String username) {
		return transaction(connection -> {
					List<SpendEntity> entities = new SpendDaoJdbc(connection).findAllByUsername(username);

					for (SpendEntity spend : entities) {
						UUID categoryId = spend.getCategory().getId();
						Optional<CategoryEntity> categoryEntity = new CategoryDaoJdbc(connection).findById(categoryId);
						categoryEntity.ifPresent(category -> spend.setCategory(category));
					}

					return entities.stream()
							.map(entity -> SpendJson.fromEntity(entity))
							.collect(Collectors.toList());
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public void deleteSpend(SpendEntity spend) {
		transaction(connection -> {
					new SpendDaoJdbc(connection).deleteSpend(spend);
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public CategoryJson createCategory(CategoryJson categoryJson) {
		return transaction(connection -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
					return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(categoryEntity));
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public CategoryJson getCategory(String username, String categoryName) {
		return transaction(connection -> {
					Optional<CategoryEntity> categoryEntity =
							new CategoryDaoJdbc(connection).findByUsernameAndCategoryName(username, categoryName);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}

	public List<CategoryJson> getCategories(String username) {
		return transaction(connection -> {
					List<CategoryEntity> entities = new CategoryDaoJdbc(connection).findAllByUsername(username);
					return entities.stream()
							.map(entity -> CategoryJson.fromEntity(entity))
							.collect(Collectors.toList());
				},
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
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
				CFG.spendJdbcUrl(),
				Connection.TRANSACTION_READ_UNCOMMITTED
		);
	}
}
