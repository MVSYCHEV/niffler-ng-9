package guru.qa.niffler.utils;

import java.util.Random;

public class DataUtils {
	public static String getRandomUserName() {
		return "testUser" + new Random().nextInt(10000);
	}

	public static String getRandomUserPassword() {
		return "password" + new Random().nextInt(10000);
	}
}
