package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type.*;

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
	void friendTableShouldBeEmptyForNewUser(@UserType() StaticUser user) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.password())
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
	void outcomeInvitationBePresentInAllPeopleTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.password())
				.submit()
				.checkThatPageLoaded()
				.openAllPeople()
				.checkThatPageLoaded()
				.checkAllTableHasOutcomeRequestToUser(user.outcome());
	}
}
