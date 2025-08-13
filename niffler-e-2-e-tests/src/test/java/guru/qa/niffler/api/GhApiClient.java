package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.service.RestClient;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public final class GhApiClient extends RestClient {
	private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";
	private final GhApi ghApi;

	public GhApiClient() {
		super(CFG.ghUrl());
		this.ghApi = create(GhApi.class);
	}

	@Nonnull
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
