package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.Assertions;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SpendApiClient implements SpendClient {
	private static final Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(Config.getInstance().spendUrl())
			.addConverterFactory(JacksonConverterFactory.create())
			.build();

	private final SpendApi spendApi = retrofit.create(SpendApi.class);

	@Override
	public SpendJson create(SpendJson spend) {
		final Response<SpendJson> response;
		try {
			response = spendApi.addSpend(spend).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(201, response.code());
		return response.body();
	}

	@Override
	public SpendJson update(SpendJson spend) {
		final Response<SpendJson> response;
		try {
			response = spendApi.editSpend(spend).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}

	@Override
	public SpendJson findById(UUID id) {
		throw new UnsupportedOperationException("Can`t find spend by ID through API");
	}

	@Override
	public SpendJson findByUsernameAndSpendDescription(String username, String description) {
		throw new UnsupportedOperationException("Can`t find spend by username and description through API");
	}

	@Override
	public List<SpendJson> findAll(String username) {
		throw new UnsupportedOperationException("Can`t find all spends by username through API");
	}

	@Override
	public void remove(SpendJson spend) {
		final Response<Void> response;
		String id = String.valueOf(spend.id());
		List<String> ids = List.of(id);
		try {
			response = spendApi.deleteSpends(spend.username(), ids).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(202, response.code());
	}

	@Override
	public CategoryJson create(CategoryJson category) {
		final Response<CategoryJson> response;
		try {
			response = spendApi.addCategory(category).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}

	@Override
	public CategoryJson update(CategoryJson category) {
		final Response<CategoryJson> response;
		try {
			response = spendApi.updateCategory(category).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}

	@Override
	public CategoryJson findCategoryById(UUID id) {
		throw new UnsupportedOperationException("Can`t find category by ID through API");
	}

	@Override
	public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
		throw new UnsupportedOperationException("Can`t find category by username and categoryName through API");
	}

	@Override
	public List<CategoryJson> findAllCategories(String username) {
		final Response<List<CategoryJson>> response;
		try {
			response = spendApi.getCategories(username, false).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}

	@Override
	public void remove(CategoryJson category) {
		throw new UnsupportedOperationException("Can`t remove category through API");
	}

	public SpendJson getSpend(String id, String username) {
		final Response<SpendJson> response;
		try {
			response = spendApi.getSpend(id, username).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}

	public List<SpendJson> getSpends(String username,
	                                 CurrencyValues filterCurrency,
	                                 Date from,
	                                 Date to) {
		final Response<List<SpendJson>> response;
		try {
			response = spendApi.getSpends(username, filterCurrency, from, to).execute();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Assertions.assertEquals(200, response.code());
		return response.body();
	}
}
