package timezra.uml.testing.papyrus.palettes;

import static org.eclipse.papyrus.uml.diagram.common.util.URIUtil.getFile;
import static org.eclipse.papyrus.uml.tools.model.UmlUtils.getUmlResource;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.papyrus.editor.PapyrusMultiDiagramEditor;
import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.uml.diagram.clazz.CreateClassDiagramCommand;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Package;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import timezra.uml.testing.papyrus.palettes.rules.AppliesTheUTP;
import timezra.uml.testing.papyrus.palettes.rules.CreatesAPapyrusModel;
import timezra.uml.testing.papyrus.palettes.rules.CreatesAProject;
import timezra.uml.testing.papyrus.palettes.rules.CreatesAServicesRegistry;
import timezra.uml.testing.papyrus.palettes.rules.OpensEditor;
import timezra.uml.testing.papyrus.palettes.rules.SavesAModel;

public class ClassDiagramPaletteTest {

	private CreatesAServicesRegistry theServicesRegistry;
	private CreatesAProject theProject;
	private CreatesAPapyrusModel thePapyrusModel;
	private OpensEditor<IMultiDiagramEditor> theEditor;

	@Rule
	public final TestRule ruleChain = RuleChain
			.outerRule(theServicesRegistry = new CreatesAServicesRegistry())
			.around(theProject = new CreatesAProject(getClass().getSimpleName()))
			.around(thePapyrusModel = new CreatesAPapyrusModel(theServicesRegistry, theProject, "model.di",
					new CreateClassDiagramCommand()))
			.around(new AppliesTheUTP(thePapyrusModel))
			.around(new SavesAModel(thePapyrusModel))
			.around(theEditor = new OpensEditor<IMultiDiagramEditor>(
					() -> getFile(getUmlResource(thePapyrusModel.get()).getURI()), PapyrusMultiDiagramEditor.EDITOR_ID));

	@Test
	public void can_create_a_test_context() {
		final IGraphicalEditPart theActivePart = ((IDiagramWorkbenchPart) theEditor.get().getActiveEditor())
				.getDiagramEditPart();
		final EditPartViewer theEditPartViewer = theActivePart.getViewer();
		doubleClick(findThePaletteTool(theEditPartViewer, "UTP/Test Context (Class)"), theEditPartViewer);

		final Element theTestContext = ((Package) theActivePart.resolveSemanticElement())
				.getPackagedElement("TestContext1");

		assertThat(theTestContext, instanceOf(Class.class));
		assertThat(theTestContext.getAppliedStereotype("utp::TestContext"), notNullValue());
	}

	private Tool findThePaletteTool(final EditPartViewer theEditPartViewer, final String toolPath) {
		final EditDomain theDomain = theEditPartViewer.getEditDomain();
		final PaletteViewer thePaletteViewer = theDomain.getPaletteViewer();
		final ToolEntry toolEntry = findByLabel(thePaletteViewer.getPaletteRoot(), toolPath);
		thePaletteViewer.setActiveTool(toolEntry);

		final Tool theTool = toolEntry.createTool();
		theTool.setViewer(theEditPartViewer);
		theTool.setEditDomain(theDomain);

		return theTool;
	}

	@SuppressWarnings("unchecked")
	private <T extends PaletteEntry> T findByLabel(final PaletteContainer thePaletteContainer, final String theLabel) {
		final String[] path = theLabel.split("/");
		PaletteEntry nextEntry = thePaletteContainer;
		NEXT_SEGMENT: for (final String segment : path) {
			if (nextEntry instanceof PaletteContainer) {
				for (final Object o : ((PaletteContainer) nextEntry).getChildren()) {
					final PaletteEntry paletteEntry = (PaletteEntry) o;
					if (segment.equals(paletteEntry.getLabel())) {
						nextEntry = paletteEntry;
						continue NEXT_SEGMENT;
					}
				}
				return null;
			} else {
				return null;
			}
		}
		return (T) nextEntry;
	}

	private void doubleClick(final Tool theTool, final EditPartViewer theEditPartViewer) {
		final Event theEvent = new Event();
		theEvent.widget = theEditPartViewer.getControl();
		final MouseEvent mouseEvent = new MouseEvent(theEvent);
		mouseEvent.button = 1;
		theTool.mouseDoubleClick(mouseEvent, theEditPartViewer);
	}
}
