package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
	private final SpendApiClient spendApiClient = new SpendApiClient();

	@Override
	public void beforeEach(ExtensionContext context) {
		AnnotationSupport.findAnnotation(
				context.getRequiredTestMethod(),
				User.class
		).ifPresent(
				annotations -> {
					if (annotations.spends().length != 0) {
						Spending spending = annotations.spends()[0];
						SpendJson spendJson = new SpendJson(
								null,
								new Date(),
								new CategoryJson(
										null,
										spending.category(),
										annotations.username(),
										false
								),
								spending.currency(),
								spending.amount(),
								spending.description(),
								annotations.username()
						);
						context.getStore(NAMESPACE).put(
								context.getUniqueId(),
								spendApiClient.addSpend(spendJson)
						);
					}
				}
		);
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
	}

	@Override
	public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return createdSpending();
	}

	public static SpendJson createdSpending() {
		final ExtensionContext methodContext = context();
		return methodContext.getStore(NAMESPACE)
				.get(methodContext.getUniqueId(), SpendJson.class);
	}
}
