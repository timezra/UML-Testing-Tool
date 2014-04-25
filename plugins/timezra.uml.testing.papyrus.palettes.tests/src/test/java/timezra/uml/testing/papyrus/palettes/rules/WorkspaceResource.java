package timezra.uml.testing.papyrus.palettes.rules;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.rules.ExternalResource;

public class WorkspaceResource extends ExternalResource {
	protected final void run(final IWorkspaceRunnable theRunnable) throws CoreException {
		ResourcesPlugin.getWorkspace().run(theRunnable, new NullProgressMonitor());
	}

	protected final IProject get_the_project(final String name) {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}
}
