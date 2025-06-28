package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UsersQueueExtension.class)
public class UsersQueueTest {

	@Test
	void testWithEmptyUser0(@UserType(empty = true) StaticUser user, @UserType(empty = false) StaticUser user1) throws InterruptedException {
		Thread.sleep(1000);
		System.out.println(user);
		System.out.println(user1);
	}

	@Test
	void testWithEmptyUser1(@UserType(empty = false) StaticUser user) throws InterruptedException {
		Thread.sleep(1000);
		System.out.println(user);
	}

	@Test
	void testWithEmptyUser2(@UserType(empty = false) StaticUser user) throws InterruptedException {
		Thread.sleep(1000);
		System.out.println(user);
	}
}
