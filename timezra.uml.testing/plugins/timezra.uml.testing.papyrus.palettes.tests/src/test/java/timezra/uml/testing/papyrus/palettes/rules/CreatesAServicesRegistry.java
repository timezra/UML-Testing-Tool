package timezra.uml.testing.papyrus.palettes.rules;

import javax.inject.Provider;

import org.eclipse.papyrus.infra.core.Activator;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ExtensionServicesRegistry;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServiceMultiException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.junit.rules.ExternalResource;

public class CreatesAServicesRegistry extends ExternalResource implements Provider<ServicesRegistry> {

	private ServicesRegistry theServicesRegistry;

	@Override
	protected final void before() throws Throwable {
		theServicesRegistry = new ExtensionServicesRegistry(Activator.PLUGIN_ID);
		try {
			theServicesRegistry.startServicesByClassKeys(ModelSet.class);
		} catch (final ServiceException ex) {
			// ok to ignore
		}
	}

	@Override
	protected final void after() {
		try {
			theServicesRegistry.disposeRegistry();
		} catch (final ServiceMultiException e) {
			throw new IllegalStateException("Problem disposing the services registry.", e);
		}
	}

	@Override
	public ServicesRegistry get() {
		return theServicesRegistry;
	}
}
