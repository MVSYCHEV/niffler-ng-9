package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
	private final SpendDbClient spendDbClient = new SpendDbClient();

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(
				context.getRequiredTestMethod(),
				User.class
		).ifPresent(
				annotations -> {
					if (annotations.categories().length != 0) {
						Category category = annotations.categories()[0];
						CategoryJson categoryJson = category.isArchived() ?
								archivedWithName(annotations.username(), RandomDataUtils.randomCategoryName()) :
								defaultCategoryWithName(annotations.username(), RandomDataUtils.randomCategoryName());
						CategoryJson createdCategory = spendDbClient.createCategory(categoryJson);
						context.getStore(NAMESPACE).put(
								context.getUniqueId(),
								createdCategory
						);
					}
				}
		);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return createdCategory();
	}

	public static CategoryJson createdCategory() {
		final ExtensionContext methodContext = context();
		return methodContext.getStore(NAMESPACE)
				.get(methodContext.getUniqueId(), CategoryJson.class);
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
		AnnotationSupport.findAnnotation(
				context.getRequiredTestMethod(),
				User.class
		).ifPresent(
				annotations -> {
					if (annotations.categories().length != 0) {
						CategoryJson createdCategory = (CategoryJson) context.getStore(NAMESPACE).get(context.getUniqueId());
						if (createdCategory != null && !createdCategory.archived()) {
							CategoryJson archivedCategory = new CategoryJson(
									createdCategory.id(),
									createdCategory.name(),
									createdCategory.username(),
									true
							);
							spendDbClient.deleteCategory(CategoryEntity.fromJson(createdCategory));
							spendDbClient.createCategory(archivedCategory);
						} else if (createdCategory == null && annotations.categories().length == 0 && annotations.spends().length != 0) {
							spendDbClient.createCategory(defaultCategoryWithName(annotations.username(), annotations.spends()[0].category()));
						}
					}
				});
	}

	private static CategoryJson defaultCategory(String userName) {
		return defaultCategoryWithName(userName, RandomDataUtils.randomCategoryName());
	}

	private static CategoryJson defaultCategoryWithName(String userName, String categoryName) {
		return new CategoryJson(
				null,
				categoryName,
				userName,
				false
		);
	}

	private static CategoryJson archivedCategory(String userName) {
		return archivedWithName(userName, RandomDataUtils.randomCategoryName());
	}

	private static CategoryJson archivedWithName(String userName, String categoryName) {
		return new CategoryJson(
				null,
				categoryName,
				userName,
				true
		);
	}
}
