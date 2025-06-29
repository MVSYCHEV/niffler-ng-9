package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.Type.*;

@WebTest
public class FriendsTest {
	private static final Config CFG = Config.getInstance();

	@Test
	void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendByName(user.friend());
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
	void incomeInvitationBePresentInFriendTable(@UserType(WITH_INCOME_FRIEND) StaticUser user) {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.fillLoginPage(user.username(), user.password())
				.submit()
				.checkThatPageLoaded()
				.openFriends()
				.checkThatPageLoaded()
				.checkFriendsListHasRequestFromUser(user.income());
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
