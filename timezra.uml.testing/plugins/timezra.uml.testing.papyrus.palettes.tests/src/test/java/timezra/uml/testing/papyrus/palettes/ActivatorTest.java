package timezra.uml.testing.papyrus.palettes;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ActivatorTest {

	@Test
	public void can_activate_the_plugin() {
		assertNotNull(Activator.getPlugin());
	}
}
