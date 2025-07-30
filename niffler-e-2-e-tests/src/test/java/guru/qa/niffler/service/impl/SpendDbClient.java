package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.spring.SpendRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;

public class SpendDbClient implements SpendClient {
	private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
			Config.getInstance().spendJdbcUrl()
	);
	/**
	 * Стандартный механизм создания spring-транзакций, но не будет работать с обычным JDBC
	 */
	private final TransactionTemplate txTemplate = new TransactionTemplate(
			new JdbcTransactionManager(
					dataSource(Config.getInstance().spendJdbcUrl())
			)
	);
//	private final SpendRepository spendRepository = new SpendRepositoryHibernate();
//	private final SpendRepository spendRepository = new SpendRepositoryJdbc();
	private final SpendRepository spendRepository = new SpendRepositorySpringJdbc();

	@Override
	public SpendJson create(SpendJson spend) {
		return xaTransactionTemplate.execute(() -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					return SpendJson.fromEntity(spendRepository.create(spendEntity));
				}
		);
	}

	@Override
	public SpendJson update(SpendJson spend) {
		return xaTransactionTemplate.execute(() -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					return SpendJson.fromEntity(spendRepository.update(spendEntity));
				}
		);
	}

	@Override
	public SpendJson findById(UUID id) {
		return xaTransactionTemplate.execute(() -> {
			Optional<SpendEntity> spendEntity = spendRepository.findById(id);
			SpendJson spendJson = null;
			if (!spendEntity.isEmpty()) {
				spendJson = SpendJson.fromEntity(spendEntity.get());
			}
			return spendJson;
		});
	}

	@Override
	public SpendJson findByUsernameAndSpendDescription(String username, String description) {
		return xaTransactionTemplate.execute(() -> {
					Optional<SpendEntity> spendEntity = spendRepository.findByUsernameAndSpendDescription(username, description);
					SpendJson spendJson = null;
					if (!spendEntity.isEmpty()) {
						spendJson = SpendJson.fromEntity(spendEntity.get());
					}
					return spendJson;
				}
		);
	}

	@Override
	public List<SpendJson> findAll(String username) {
		return xaTransactionTemplate.execute(() -> {
					List<SpendEntity> entities = spendRepository.findAllByUsername(username);
					return entities.stream()
							.map(SpendJson::fromEntity)
							.collect(Collectors.toList());
				}
		);
	}

	@Override
	public void remove(SpendJson spend) {
		xaTransactionTemplate.execute(() -> {
					spendRepository.remove(SpendEntity.fromJson(spend));
					return null;
				}
		);
	}

	@Override
	public CategoryJson create(CategoryJson category) {
		return xaTransactionTemplate.execute(() -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
					return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
				}
		);
	}

	@Override
	public CategoryJson update(CategoryJson category) {
		return xaTransactionTemplate.execute(() -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
					return CategoryJson.fromEntity(spendRepository.update(categoryEntity));
				}
		);
	}

	@Override
	public CategoryJson findCategoryById(UUID id) {
		return xaTransactionTemplate.execute(() -> {
					Optional<CategoryEntity> categoryEntity = spendRepository.findCategoryById(id);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				}
		);
	}

	@Override
	public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
		return xaTransactionTemplate.execute(() -> {
					Optional<CategoryEntity> categoryEntity = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				}
		);
	}

	@Override
	public List<CategoryJson> findAllCategories(String username) {
		return xaTransactionTemplate.execute(() -> {
					List<CategoryEntity> entities = spendRepository.findAllCategoriesByUsername(username);
					return entities.stream()
							.map(entity -> CategoryJson.fromEntity(entity))
							.collect(Collectors.toList());
				}
		);
	}

	@Override
	public void remove(CategoryJson category) {
		CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
		String username = categoryEntity.getUsername();
		UUID categoryId = categoryEntity.getId();

		List<SpendJson> spends = findAll(username);
		spends.stream()
				.filter(spendJson -> spendJson.category().id().equals(categoryId))
				.forEach(this::remove);

		xaTransactionTemplate.execute(() -> {
					spendRepository.removeCategory(categoryEntity);
					return null;
				}
		);
	}
}
