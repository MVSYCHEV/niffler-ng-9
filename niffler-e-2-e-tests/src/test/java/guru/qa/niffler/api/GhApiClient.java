package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class GhApiClient {

	private static final Config CFG = Config.getInstance();
	private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

	private static final Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(CFG.ghUrl())
			.addConverterFactory(JacksonConverterFactory.create())
			.build();

	private final GhApi ghApi = retrofit.create(GhApi.class);

	@SneakyThrows
	public String issueState(String issueNumber) {
		Response<JsonNode> response;
		try {
			response = ghApi.issue(
					"Bearer " + System.getenv(GH_TOKEN_ENV),
					issueNumber
			).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return requireNonNull(response.body()).get("state").asText();
	}
}
