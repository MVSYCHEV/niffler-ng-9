package guru.qa.niffler.test;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Order(1)
@Isolated
public class FirstTest {

	@Test
	void firstTest() {
		System.out.println("This is first Test");
	}
}
