package timezra.uml.testing.papyrus.palettes.rules;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.eclipse.papyrus.infra.core.resource.sasheditor.DiModelUtils.getDiResource;
import static org.eclipse.papyrus.infra.gmfdiag.common.model.NotationUtils.getNotationResource;
import static org.eclipse.papyrus.uml.tools.model.UmlUtils.getUmlResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.inject.Provider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.papyrus.infra.core.editor.BackboneException;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.sasheditor.DiModelUtils;
import org.eclipse.papyrus.infra.core.sasheditor.contentprovider.IPageManager;
import org.eclipse.papyrus.infra.core.sasheditor.di.contentprovider.TransactionalDiSashModelMngr;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.gmfdiag.common.AbstractPapyrusGmfCreateDiagramCommandHandler;
import org.eclipse.papyrus.uml.diagram.wizards.category.DiagramCategoryDescriptor;
import org.eclipse.papyrus.uml.diagram.wizards.category.DiagramCategoryRegistry;
import org.eclipse.papyrus.uml.diagram.wizards.category.NewPapyrusModelCommand;

import timezra.uml.testing.papyrus.palettes.Activator;

public class CreatesAPapyrusModel extends WorkspaceResource implements Provider<ModelSet> {

	private final Provider<ServicesRegistry> theServicesRegistry;
	private final Provider<IProject> theProject;
	private final String theModelName;
	private final Iterable<AbstractPapyrusGmfCreateDiagramCommandHandler> diagramCreationCommands;
	private ModelSet theModelSet;

	public CreatesAPapyrusModel(final Provider<ServicesRegistry> theServicesRegistry,
			final Provider<IProject> theProject, final String theModelName,
			final AbstractPapyrusGmfCreateDiagramCommandHandler... diagramCreationCommands) {
		this.theServicesRegistry = theServicesRegistry;
		this.theProject = theProject;
		this.theModelName = theModelName;
		this.diagramCreationCommands = asList(diagramCreationCommands);
	}

	@Override
	protected final void before() throws Throwable {
		final IFile thePapyrusModel = theProject.get().getFile(theModelName);

		run((final IProgressMonitor pm) -> {
			thePapyrusModel.create(new ByteArrayInputStream(new byte[0]), true, pm);
			final URI theURI = URI.createPlatformResourceURI(thePapyrusModel.getFullPath().toString(), true);
			try {
				theModelSet = theServicesRegistry.get().getService(ModelSet.class);
				theModelSet.getTransactionalEditingDomain().getCommandStack()
						.execute(new NewPapyrusModelCommand(theModelSet, theURI));
				initServicesRegistry();

				getDiagramCategoryMap().get("uml").getCommand().createModel(theModelSet);
				diagramCreationCommands
				.forEach((final AbstractPapyrusGmfCreateDiagramCommandHandler command) -> command
						.createDiagram(theModelSet, null, command.getCreatedDiagramType()));

				TransactionalDiSashModelMngr.createIPageMngr(DiModelUtils.getDiResource(theModelSet),
						theModelSet.getTransactionalEditingDomain());

				theModelSet.save(new NullProgressMonitor());
			} catch (final ServiceException | BackboneException | IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}

			theModelSet.createModels(theURI);
		});
	}

	private void initServicesRegistry() throws ServiceException {
		try {
			theServicesRegistry.get().startRegistry();
		} catch (final ServiceException ex) {
			// ok to ignore
		}
		theServicesRegistry.get().getService(IPageManager.class);
	}

	private Map<String, DiagramCategoryDescriptor> getDiagramCategoryMap() {
		return DiagramCategoryRegistry.getInstance().getDiagramCategoryMap();
	}

	@Override
	protected final void after() {
		try {
			run((final IProgressMonitor __) -> {
				final Exception aggregator = new Exception();
				getModelResources().forEach((final Resource resource) -> {
					try {
						resource.delete(emptyMap());
					} catch (final IOException e) {
						aggregator.addSuppressed(e);
					}
				});
				if (aggregator.getSuppressed().length > 0) {
					throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, aggregator.getMessage(),
							aggregator));
				}
			});
		} catch (final CoreException e) {
			throw new IllegalStateException("Problem deleting the ModelSet " + theModelSet.getURIWithoutExtension(), e);
		}
	}

	private Iterable<Resource> getModelResources() {
		if (theModelSet == null) {
			return Collections.emptyList();
		}
		final Collection<Resource> resources = new ArrayList<>(3);
		final Resource theUmlResource = getUmlResource(theModelSet);
		if (theUmlResource != null) {
			resources.add(theUmlResource);
		}
		final Resource theDiagramResource = getDiResource(theModelSet);
		if (theDiagramResource != null) {
			resources.add(theDiagramResource);
		}
		final Resource theNotationResource = getNotationResource(theModelSet);
		if (theNotationResource != null) {
			resources.add(theNotationResource);
		}
		return resources;
	}

	@Override
	public ModelSet get() {
		return theModelSet;
	}
}
