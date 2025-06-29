package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
	private final SpendApiClient spendApiClient = new SpendApiClient();

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(
				context.getRequiredTestMethod(),
				User.class
		).ifPresent(
				annotations -> {
					if (annotations.categories().length != 0) {
						Category category = annotations.categories()[0];
						String categoryName = RandomDataUtils.randomCategoryName();
						CategoryJson categoryJson = defaultCategory(annotations.username());
						CategoryJson createdCategory = spendApiClient.addCategory(categoryJson);
						if (category.isArchived()) {
							CategoryJson archivedCategory = new CategoryJson(
									createdCategory.id(),
									categoryName,
									annotations.username(),
									true
							);
							createdCategory = spendApiClient.updateCategory(archivedCategory);
						}
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
							spendApiClient.updateCategory(archivedCategory);
						} else if (createdCategory == null && annotations.categories().length == 0 && annotations.spends().length != 0) {
							spendApiClient.addCategory(categoryWithName(annotations.username(), annotations.spends()[0].category()));
						}
					}
				});
	}

	private CategoryJson defaultCategory(String userName) {
		return new CategoryJson(
				null,
				RandomDataUtils.randomCategoryName(),
				userName,
				false
		);
	}

	private CategoryJson categoryWithName(String userName, String categoryName) {
		return new CategoryJson(
				null,
				categoryName,
				userName,
				false
		);
	}
}
