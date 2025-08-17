package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserDataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.RestClient;
import org.apache.commons.lang3.time.StopWatch;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AuthUserApiClient extends RestClient {
	private final AuthApi authApi;

	public AuthUserApiClient(UserDataApi userDataApi) {
		super(CFG.authUrl(), true);
		this.authApi = create(AuthApi.class);
	}

	@Nonnull
	public UserJson createNewUser(String username, String password, String passwordSubmit) {
		try {
			authApi.requestRegisterForm().execute();
			authApi.register(
					username,
					password,
					passwordSubmit,
					ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
			).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		StopWatch stopWatch = StopWatch.createStarted();
		UserDataApiClient userDataApiClient = new UserDataApiClient();
		while (stopWatch.getTime(TimeUnit.MILLISECONDS) < 50) {
			UserJson userJson = userDataApiClient.getCurrentUser(username);
			if (userJson != null && userJson.id() != null) {
				return userJson;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		throw new RuntimeException("User was not created");
	}
}
