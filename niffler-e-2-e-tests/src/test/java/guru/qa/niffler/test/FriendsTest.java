package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Order(2)
@Isolated
@WebTest
public class FriendsTest {
	private static final Config CFG = Config.getInstance();

	@Test
	@User(
			friends = 1
	)
	void friendShouldBePresentInFriendsTable(UserJson user) {
		final UserJson friend = user.testData().friends().getFirst();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendByName(friend.username());
	}

	@Test
	@User
	void friendTableShouldBeEmptyForNewUser(UserJson user) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendsListIsEmpty();
	}

	@Test
	@User(
			incomeInvitations = 1
	)
	void incomeInvitationBePresentInFriendTable(UserJson user) {
		final UserJson income = user.testData().incomeInvitations().getFirst();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendsListHasRequestFromUser(income.username());
	}

	@Test
	@User(
			outcomeInvitations = 1
	)
	void outcomeInvitationBePresentInAllPeopleTable(UserJson user) {
		final UserJson outcome = user.testData().outcomeInvitations().getFirst();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openAllPeople()
				.checkThatPageLoaded()
				.checkAllTableHasOutcomeRequestToUser(outcome.username());
	}

	@Test
	void checkLotsFriends() {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage("idell.bayer", "12345")
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendByName("phylicia.windler");
	}

	@Test
	@User(
			incomeInvitations = 1
	)
	void checkAcceptFriendRequest(UserJson user) {
		final UserJson income = user.testData().incomeInvitations().getFirst();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.acceptFriendRequestFromUser(income.username());
	}

	@Test
	@User(
			incomeInvitations = 1
	)
	void checkDeclineFriendRequest(UserJson user) {
		final UserJson income = user.testData().incomeInvitations().getFirst();

		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.testData().password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.declineFriendRequestFromUser(income.username());
	}
}
