package guru.qa.niffler.model;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public record TestData(
		String password,
		List<UserJson> friends,
		List<UserJson> incomeInvitations,
		List<UserJson> outcomeInvitations,
		List<CategoryJson> categories,
		List<SpendJson> spendings) {

	@Nonnull
	public TestData addCategories(List<CategoryJson> categories) {
		return new TestData(
				this.password,
				this.friends,
				this.incomeInvitations,
				this.outcomeInvitations,
				categories,
				this.spendings
		);
	}

	@Nonnull
	public TestData addSpendings(List<SpendJson> spendings) {
		return new TestData(
				this.password,
				this.friends,
				this.incomeInvitations,
				this.outcomeInvitations,
				this.categories,
				spendings
		);
	}
}
