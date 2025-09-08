package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserDataApi;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.RestClient;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;

import java.io.IOException;

public class UserDataApiClient extends RestClient {
	private final UserDataApi userDataApi;

	public UserDataApiClient() {
		super(CFG.userdataUrl());
		userDataApi = create(UserDataApi.class);
	}

	public UserJson getCurrentUser(String username) {
		final Response<UserJson> response;

		try {
			response = userDataApi.currentUser(username).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}
}
