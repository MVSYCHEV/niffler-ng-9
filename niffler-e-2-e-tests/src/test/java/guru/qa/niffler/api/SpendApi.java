package guru.qa.niffler.api;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Date;
import java.util.List;

public interface SpendApi {

  /**
   * Spend Controller
   */

  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spending);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spending);

  @GET("internal/spends/{id}")
  Call<SpendJson> getSpend(@Path("id") String id, @Query("username") String username);

  @GET("internal/spends/all")
  Call<List<SpendJson>> getSpends(
          @Query("username") String username,
          @Query("filterCurrency") CurrencyValues filterCurrency,
          @Query("from") Date from,
          @Query("to") Date to
  );

  @DELETE("internal/spends/remove")
  Call<Void> deleteSpends(@Query("username") String username, @Query("ids") List<String> ids);

  /**
   * Categories Controller
   */

  @GET("internal/categories/all")
  Call<List<CategoryJson>> getCategories(@Query("username") String userName, @Query("excludeArchived") boolean excludeArchived);

  @POST("internal/categories/add")
  Call<CategoryJson> addCategory(@Body CategoryJson category);

  @PATCH("internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson category);
}
