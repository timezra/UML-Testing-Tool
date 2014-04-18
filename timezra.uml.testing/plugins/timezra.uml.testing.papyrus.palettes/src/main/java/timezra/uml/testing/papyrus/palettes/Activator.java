package timezra.uml.testing.papyrus.palettes;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

public class Activator extends EMFPlugin {

	public static final String PLUGIN_ID = "timezra.uml.testing.papyrus.palettes"; //$NON-NLS-1$

	public static final Activator INSTANCE = new Activator();

	private static Implementation plugin;

	public Activator() {
		super(new ResourceLocator[] {});
	}

	@Override
	public ResourceLocator getPluginResourceLocator() {
		return plugin;
	}

	public static Implementation getPlugin() {
		return plugin;
	}

	public static class Implementation extends EclipsePlugin {

		public Implementation() {
			super();

			plugin = this;
		}
	}
}
