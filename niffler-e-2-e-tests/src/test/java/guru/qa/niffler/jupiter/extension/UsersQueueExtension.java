package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
		BeforeTestExecutionCallback,
		AfterTestExecutionCallback,
		ParameterResolver {

	public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

	public record StaticUser(
			String username,
			String password,
			String friend,
			String income,
			String outcome) {
	}

	private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
	private static final Queue<StaticUser> WITH_FRIEND_USER = new ConcurrentLinkedQueue<>();
	private static final Queue<StaticUser> WITH_INCOME_REQUEST_USER = new ConcurrentLinkedQueue<>();
	private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USER = new ConcurrentLinkedQueue<>();

	static {
		EMPTY_USERS.add(new StaticUser("emptyUser", "123", null, null, null));
		WITH_FRIEND_USER.add(new StaticUser("userWithFriend", "123", "sychev", null, null));
		WITH_INCOME_REQUEST_USER.add(new StaticUser("incomeRequestUser", "123", null, "outcomeRequestUser", null));
		WITH_OUTCOME_REQUEST_USER.add(new StaticUser("outcomeRequestUser", "123", null, null, "incomeRequestUser"));
	}

	public enum Type {
		EMPTY, WITH_FRIEND, WITH_INCOME_FRIEND, WITH_OUTCOME_REQUEST
	}

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface UserType {
		Type value() default Type.EMPTY;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		Map<UserType, StaticUser> mapOfUsers = new HashMap<>();
		Arrays.stream(context.getRequiredTestMethod().getParameters())
				.filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
				.forEach(p -> {
					UserType userType = p.getAnnotation(UserType.class);
					StopWatch stopWatch = StopWatch.createStarted();
					StaticUser user = getQueueByType(userType).poll();
					while (user == null && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
						user = getQueueByType(userType).poll();
					}
					mapOfUsers.put(userType, user);
				});
		context.getStore(NAMESPACE).put(context.getUniqueId(), mapOfUsers);
		Allure.getLifecycle().updateTestCase(testCase ->
				testCase.setStart(new Date().getTime())
		);
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
		Map<UserType, StaticUser> map = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
		for (Map.Entry<UserType, StaticUser> entry : map.entrySet()) {
			Queue<StaticUser> queue = getQueueByType(entry.getKey());
			queue.add(entry.getValue());
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
				&& AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		Map<UserType, StaticUser> users = extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class);
		return users.get(parameterContext.getParameter().getAnnotation(UserType.class));
	}

	private Queue<StaticUser> getQueueByType(UserType userType) {
		return switch (userType.value()) {
			case Type.EMPTY -> EMPTY_USERS;
			case Type.WITH_FRIEND -> WITH_FRIEND_USER;
			case Type.WITH_INCOME_FRIEND -> WITH_INCOME_REQUEST_USER;
			case Type.WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USER;
		};
	}
}
