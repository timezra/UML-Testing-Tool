package timezra.uml.testing.papyrus.palettes;

import static org.eclipse.papyrus.uml.extensionpoints.profile.RegisteredProfile.getRegisteredProfile;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProfileRegistrationTest {
	@Test
	public void should_register_the_uml_testing_profile() {
		assertThat(getRegisteredProfile("UTP"), notNullValue());
	}
}
