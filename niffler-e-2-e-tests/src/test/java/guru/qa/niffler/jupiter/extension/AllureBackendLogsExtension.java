package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
	public static final String caseName = "Niffler backend logs";

	@SneakyThrows
	@Override
	public void afterSuite() {
		createLogsTestCase("Niffler-auth log", "./logs/niffler-auth/app.log");
		createLogsTestCase("Niffler-currency log", "./logs/niffler-currency/app.log");
		createLogsTestCase("Niffler-gateway log", "./logs/niffler-gateway/app.log");
		createLogsTestCase("Niffler-spend log", "./logs/niffler-spend/app.log");
		createLogsTestCase("Niffler-userdata log", "./logs/niffler-userdata/app.log");
	}

	@SneakyThrows
	private void createLogsTestCase(String name, String pathToLogsFile) {
		final AllureLifecycle allureLifecycle = Allure.getLifecycle();
		final String caseId = UUID.randomUUID().toString();
		allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(name));
		allureLifecycle.startTestCase(caseId);

		allureLifecycle.addAttachment(
				name,
				"text/html",
				".log",
				Files.newInputStream(
						Path.of(pathToLogsFile)
				)
		);

		allureLifecycle.stopTestCase(caseId);
		allureLifecycle.writeTestCase(caseId);
	}
}
