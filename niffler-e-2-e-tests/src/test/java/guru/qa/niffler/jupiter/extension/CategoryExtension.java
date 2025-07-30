package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.SpendApiClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;
import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements
		BeforeEachCallback,
		AfterTestExecutionCallback,
		ParameterResolver {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);

	private final SpendApiClient spendApiClient = new SpendApiClient();

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
				.ifPresent(userAnno -> {
					if (ArrayUtils.isNotEmpty(userAnno.categories())) {
						final @Nullable UserJson createdUser = UserExtension.createdUser();

						final List<CategoryJson> result = new ArrayList<>();
						for (Category categoryAnno : userAnno.categories()) {
							final String username = createdUser != null ? createdUser.username() : userAnno.username();
							if (!"".equals(username)) {
								CategoryJson category = new CategoryJson(
										null,
										randomCategoryName(),
										username,
										categoryAnno.isArchived()
								);

								CategoryJson created = spendApiClient.create(category);
								if (categoryAnno.isArchived()) {
									CategoryJson archivedCategory = new CategoryJson(
											created.id(),
											created.name(),
											created.username(),
											true
									);
									created = spendApiClient.update(archivedCategory);
								}
								result.add(created);
							}
						}

						if (createdUser != null) {
							createdUser.testData().categories().addAll(result);
						} else {
							context.getStore(NAMESPACE).put(
									context.getUniqueId(),
									result.stream().toArray(CategoryJson[]::new)
							);
						}
					}
				});
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
		CategoryJson[] categories = createdCategory();
		if (categories != null) {
			for (CategoryJson category : categories) {
				if (category != null && !category.archived()) {
					category = new CategoryJson(
							category.id(),
							category.name(),
							category.username(),
							true
					);
					spendApiClient.update(category);
				}
			}
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
			ParameterResolutionException {
		return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson[].class);
	}

	@Override
	public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
			ParameterResolutionException {
		return createdCategory();
	}

	public static CategoryJson[] createdCategory() {
		final ExtensionContext methodContext = context();
		return methodContext.getStore(NAMESPACE)
				.get(methodContext.getUniqueId(), CategoryJson[].class);
	}
}
