package timezra.uml.testing.papyrus.palettes.rules;

import static org.eclipse.papyrus.uml.extensionpoints.profile.RegisteredProfile.getRegisteredProfile;
import static org.eclipse.papyrus.uml.tools.model.UmlUtils.getUmlResource;

import javax.inject.Provider;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Profile;
import org.junit.rules.ExternalResource;

public class AppliesTheUTP extends ExternalResource {

	private final Provider<ModelSet> thePapyrusModel;
	private Model theRootModel;
	private Profile theUTP;

	public AppliesTheUTP(final Provider<ModelSet> thePapyrusModel) {
		this.thePapyrusModel = thePapyrusModel;
	}

	@Override
	protected final void before() throws Throwable {
		final ModelSet theModelSet = thePapyrusModel.get();
		theRootModel = (Model) getUmlResource(theModelSet).getContents().get(0);
		theUTP = (Profile) theModelSet.getResource(getRegisteredProfile("UTP").uri, true).getContents().get(0);
		final TransactionalEditingDomain theDomain = theModelSet.getTransactionalEditingDomain();
		theDomain.getCommandStack().execute(new RecordingCommand(theDomain) {
			@Override
			protected void doExecute() {
				theRootModel.applyProfile(theUTP);
			}
		});
	}

	@Override
	protected final void after() {
		if (theRootModel != null && theUTP != null) {
			final TransactionalEditingDomain theDomain = thePapyrusModel.get().getTransactionalEditingDomain();
			theDomain.getCommandStack().execute(new RecordingCommand(theDomain) {
				@Override
				protected void doExecute() {
					theRootModel.unapplyProfile(theUTP);
				}
			});
		}
	}
}
