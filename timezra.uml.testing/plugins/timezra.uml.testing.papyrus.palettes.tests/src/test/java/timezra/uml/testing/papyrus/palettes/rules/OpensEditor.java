package timezra.uml.testing.papyrus.palettes.rules;

import javax.inject.Provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.junit.rules.ExternalResource;

public class OpensEditor<E extends IEditorPart> extends ExternalResource implements Provider<E> {

	private final Provider<IFile> theFile;
	private final String theEditorId;
	private E theEditor;

	public OpensEditor(final Provider<IFile> theFile, final String theEditorId) {
		this.theFile = theFile;
		this.theEditorId = theEditorId;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected final void before() throws Throwable {
		theEditor = (E) IDE.openEditor(getTheActivePage(), theFile.get(), theEditorId, true);
	}

	@Override
	protected final void after() {
		theEditor.getSite().getPage().closeEditor(theEditor, false);
	}

	private IWorkbenchPage getTheActivePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	@Override
	public E get() {
		return theEditor;
	}
}
