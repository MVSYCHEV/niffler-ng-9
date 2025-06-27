package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.DataUtils;
import guru.qa.niffler.utils.Users;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegisterTest {
	private static final Config CFG = Config.getInstance();

	@Test
	void shouldRegisterNewUser() {
		String userName = DataUtils.getRandomUserName();
		String userPassword = DataUtils.getRandomUserPassword();
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.createNewAccount()
				.fillRegisterPage(userName, userPassword)
				.signUpThenSignIn()
				.fillLoginPage(userName, userPassword)
				.submit()
				.checkThatPageLoaded();
	}

	@Test
	void shouldNotRegisterUserWithExistingUsername() {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.createNewAccount()
				.fillRegisterPage(Users.SYCHEV_TEST_USER_NAME, "123")
				.sighUp()
				.checkErrorUserAlreadyExist(Users.SYCHEV_TEST_USER_NAME);
	}

	@Test
	void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
		Selenide.open(CFG.frontUrl(), LoginPage.class)
				.createNewAccount()
				.fillUserName(Users.SYCHEV_TEST_USER_NAME)
				.fillPassword("123")
				.submitPassword("321")
				.sighUp()
				.checkErrorPasswordsShouldBeEqual();
	}
}
