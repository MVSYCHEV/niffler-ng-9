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
import io.qameta.allure.Step;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
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
	@Step("Через BD создать новый расход '{0}'")
	@Nonnull
	public SpendJson create(SpendJson spend) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					return SpendJson.fromEntity(spendRepository.create(spendEntity));
				}
		));
	}

	@Override
	@Step("Через BD отредактировать расход '{0}'")
	@Nonnull
	public SpendJson update(SpendJson spend) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					SpendEntity spendEntity = SpendEntity.fromJson(spend);
					return SpendJson.fromEntity(spendRepository.update(spendEntity));
				}
		));
	}

	@Override
	@Step("Через BD найти расход с id '{0}'")
	@Nonnull
	public SpendJson findById(UUID id) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
			Optional<SpendEntity> spendEntity = spendRepository.findById(id);
			SpendJson spendJson = null;
			if (!spendEntity.isEmpty()) {
				spendJson = SpendJson.fromEntity(spendEntity.get());
			}
			return spendJson;
		}));
	}

	@Override
	@Step("Через BD найти расход у пользователя '{0}' и описанием '{1}'")
	@Nonnull
	public SpendJson findByUsernameAndSpendDescription(String username, String description) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					Optional<SpendEntity> spendEntity = spendRepository.findByUsernameAndSpendDescription(username, description);
					SpendJson spendJson = null;
					if (!spendEntity.isEmpty()) {
						spendJson = SpendJson.fromEntity(spendEntity.get());
					}
					return spendJson;
				}
		));
	}

	@Override
	@Step("Через BD найти все расходы у пользователя '{0}'")
	@Nonnull
	public List<SpendJson> findAll(String username) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					List<SpendEntity> entities = spendRepository.findAllByUsername(username);
					return entities.stream()
							.map(SpendJson::fromEntity)
							.collect(Collectors.toList());
				}
		));
	}

	@Override
	@Step("Через BD удалить расход '{0}'")
	public void remove(SpendJson spend) {
		xaTransactionTemplate.execute(() -> {
					spendRepository.remove(SpendEntity.fromJson(spend));
					return null;
				}
		);
	}

	@Override
	@Step("Через BD создать новую категорию '{0}'")
	@Nonnull
	public CategoryJson create(CategoryJson category) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
					return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
				}
		));
	}

	@Override
	@Step("Через BD отредактировать категорию '{0}'")
	@Nonnull
	public CategoryJson update(CategoryJson category) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
					return CategoryJson.fromEntity(spendRepository.update(categoryEntity));
				}
		));
	}

	@Override
	@Step("Через BD найти категорию с id '{0}'")
	@Nonnull
	public CategoryJson findCategoryById(UUID id) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					Optional<CategoryEntity> categoryEntity = spendRepository.findCategoryById(id);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				}
		));
	}

	@Override
	@Step("Через BD найти категорию с именем '{0}' и описанием '{1}'")
	@Nonnull
	public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					Optional<CategoryEntity> categoryEntity = spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName);
					CategoryJson categoryJson = null;
					if (!categoryEntity.isEmpty()) {
						categoryJson = CategoryJson.fromEntity(categoryEntity.get());
					}
					return categoryJson;
				}
		));
	}

	@Override
	@Step("Через BD найти все категории у пользователя '{0}'")
	@Nonnull
	public List<CategoryJson> findAllCategories(String username) {
		return requireNonNull(xaTransactionTemplate.execute(() -> {
					List<CategoryEntity> entities = spendRepository.findAllCategoriesByUsername(username);
					return entities.stream()
							.map(entity -> CategoryJson.fromEntity(entity))
							.collect(Collectors.toList());
				}
		));
	}

	@Override
	@Step("Через BD удалить категорию '{0}'")
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
