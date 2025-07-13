package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpendDbClient {
	private static final Config CFG = Config.getInstance();
	private final CategoryDao categoryDao = new CategoryDaoJdbc();
	private final SpendDao spendDao = new SpendDaoJdbc();

	private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
			CFG.spendJdbcUrl()
	);

	public SpendJson createSpend(SpendJson spend) {
		return jdbcTxTemplate.execute(() -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					if (spendEntity.getCategory().getId() == null) {
						CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
						spendEntity.setCategory(categoryEntity);
					}
					return SpendJson.fromEntity(
							spendDao.create(spendEntity)
					);
				}
		);
	}

	public SpendJson getSpend(UUID id) {
		return jdbcTxTemplate.execute(() -> {
					Optional<SpendEntity> spendEntity = spendDao.findSpendById(id);
					UUID categoryId = spendEntity
							.map(spend -> spend.getCategory().getId())
							.orElseThrow(() -> new NoSuchElementException("No value present"));
					Optional<CategoryEntity> categoryEntity = categoryDao.findById(categoryId);
					categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
					return SpendJson.fromEntity(spendEntity.get());
				}
		);
	}

	public List<SpendJson> getSpends(String username) {
		return jdbcTxTemplate.execute(() -> {
					List<SpendEntity> entities = spendDao.findAllByUsername(username);

					for (SpendEntity spend : entities) {
						UUID categoryId = spend.getCategory().getId();
						Optional<CategoryEntity> categoryEntity = categoryDao.findById(categoryId);
						categoryEntity.ifPresent(category -> spend.setCategory(category));
					}

					return entities.stream()
							.map(entity -> SpendJson.fromEntity(entity))
							.collect(Collectors.toList());
				}
		);
	}

	public void deleteSpend(SpendEntity spend) {
		jdbcTxTemplate.execute(() -> {
					spendDao.deleteSpend(spend);
					return null;
				}
		);
	}

	public CategoryJson createCategory(CategoryJson categoryJson) {
		return jdbcTxTemplate.execute(() -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
					return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
				}
		);
	}

	public CategoryJson getCategory(String username, String categoryName) {
		return jdbcTxTemplate.execute(() -> {
					Optional<CategoryEntity> categoryEntity = categoryDao.findByUsernameAndCategoryName(username, categoryName);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				}
		);
	}

	public List<CategoryJson> getCategories(String username) {
		return jdbcTxTemplate.execute(() -> {
					List<CategoryEntity> entities = categoryDao.findAllByUsername(username);
					return entities.stream()
							.map(entity -> CategoryJson.fromEntity(entity))
							.collect(Collectors.toList());
				}
		);
	}

	public void deleteCategory(CategoryEntity category) {
		String username = category.getUsername();
		UUID categoryId = category.getId();

		List<SpendJson> spends = getSpends(username);
		spends.stream()
				.filter(spendJson -> spendJson.category().id().equals(categoryId))
				.forEach(spendJson -> deleteSpend(SpendEntity.fromJson(spendJson)));

		jdbcTxTemplate.execute(() -> {
					categoryDao.deleteCategory(category);
					return null;
				}
		);
	}
}
