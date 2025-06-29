package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Random;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {
	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
	private final SpendApiClient spendApiClient = new SpendApiClient();

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(
				context.getRequiredTestMethod(),
				Category.class
		).ifPresent(
				category -> {
					String categoryName = "Category Name " + new Random().nextInt(10000);
					CategoryJson categoryJson = new CategoryJson(
							null,
							categoryName,
							category.username(),
							false
					);
					CategoryJson createdCategory = spendApiClient.addCategory(categoryJson);
					if (category.isArchived()) {
						CategoryJson archivedCategory = new CategoryJson(
								createdCategory.id(),
								categoryName,
								category.username(),
								true
						);
						createdCategory = spendApiClient.updateCategory(archivedCategory);
					}
					context.getStore(NAMESPACE).put(
							context.getUniqueId(),
							createdCategory
					);
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
				Category.class
		).ifPresent(category -> {
			CategoryJson createdCategory = (CategoryJson) context.getStore(NAMESPACE).get(context.getUniqueId());
			if (!createdCategory.archived()) {
				CategoryJson archivedCategory = new CategoryJson(
						createdCategory.id(),
						createdCategory.name(),
						createdCategory.username(),
						true
				);
				spendApiClient.updateCategory(archivedCategory);
			}
		});
	}
}
