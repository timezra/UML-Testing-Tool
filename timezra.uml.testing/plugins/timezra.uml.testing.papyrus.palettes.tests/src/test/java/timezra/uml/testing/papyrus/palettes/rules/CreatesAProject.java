package timezra.uml.testing.papyrus.palettes.rules;

import javax.inject.Provider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CreatesAProject extends WorkspaceResource implements Provider<IProject> {

	private final String theProjectName;
	private IProject theProject;

	public CreatesAProject(final String theProjectName) {
		this.theProjectName = theProjectName;
	}

	@Override
	protected final void before() throws Throwable {
		theProject = get_the_project(theProjectName);
		run((final IProgressMonitor pm) -> {
			theProject.create(pm);
			theProject.open(pm);
		});
	}

	@Override
	protected final void after() {
		try {
			if (theProject.exists()) {
				run((final IProgressMonitor pm) -> theProject.delete(true, true, pm));
			}
		} catch (final CoreException e) {
			throw new IllegalStateException("Problem deleting the project " + theProjectName, e);
		}
	}

	@Override
	public final IProject get() {
		return theProject;
	}
}
