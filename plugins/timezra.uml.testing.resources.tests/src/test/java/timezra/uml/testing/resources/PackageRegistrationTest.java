package timezra.uml.testing.resources;

import static org.eclipse.uml2.uml.UMLPlugin.getEPackageNsURIToProfileLocationMap;
import static org.eclipse.uml2.uml.resources.util.UMLResourcesUtil.init;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Profile;
import org.junit.Test;

public class PackageRegistrationTest {
	@Test
	public void should_register_the_uml_testing_package() {
		final EObject utp = init(new ResourceSetImpl()).getEObject(
				getEPackageNsURIToProfileLocationMap().get("http://www.omg.org/spec/UTP/20120801/utp_1.2"), true);

		assertThat(utp, instanceOf(Profile.class));
	}
}
