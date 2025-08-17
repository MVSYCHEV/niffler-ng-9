package guru.qa.niffler.test;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

@Order(Integer.MAX_VALUE)
@Isolated
public class LastTest {

	@Test
	void firstTest() {
		System.out.println("This is last Test");
	}
}
