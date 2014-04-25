package timezra.uml.testing.papyrus.palettes.rules;

import java.io.IOException;

import javax.inject.Provider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.papyrus.infra.core.resource.ModelSet;

import timezra.uml.testing.papyrus.palettes.Activator;

public class SavesAModel extends WorkspaceResource {

	private final Provider<ModelSet> theModelSet;

	public SavesAModel(final Provider<ModelSet> theModelSet) {
		this.theModelSet = theModelSet;
	}

	@Override
	protected final void before() throws Throwable {
		run((final IProgressMonitor pm) -> {
			try {
				theModelSet.get().save(pm);
			} catch (final IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}
		});
	}
}
