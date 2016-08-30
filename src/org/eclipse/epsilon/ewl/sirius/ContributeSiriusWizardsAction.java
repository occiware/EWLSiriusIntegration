package org.eclipse.epsilon.ewl.sirius;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.epsilon.ewl.emf.AbstractContributeWizardsAction;
import org.eclipse.epsilon.ewl.emf.WorkbenchPartRefresher;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;

public class ContributeSiriusWizardsAction extends AbstractContributeWizardsAction	 {

	protected EObject getEObject(Object selected) {

		
		  
		if (selected instanceof IGraphicalEditPart) {
			IGraphicalEditPart gep = (IGraphicalEditPart) selected;
			EObject semanticElement = gep.resolveSemanticElement();
			if (semanticElement instanceof DSemanticDecorator) {
				DSemanticDecorator sem1 = (DSemanticDecorator) semanticElement;
				System.out.println(sem1.getTarget());
				if (sem1 != null && sem1.getTarget() != null) {
					return sem1.getTarget();
				}
			}
		}
		return null;
	}

	protected EditingDomain getEditingDomain() {
		if (targetPart instanceof DiagramEditor) {
			return ((DiagramEditor) targetPart).getDiagramEditPart().getEditingDomain();
		} else {
			return null;
		}
	}
	

	protected void execute(Command command) {
		System.err.println("passe par la");
		EditingDomain editingDomain = getEditingDomain();		
		if (editingDomain != null) {
			System.err.println("passe par la1");
			editingDomain.getCommandStack().execute(command);
		}
		else {
			System.err.println("passe par la2");

			command.execute();
		}
	}


	@Override
	protected WorkbenchPartRefresher getWorkbenchPartRefresher() {
		return new WorkbenchPartRefresher() {

			@Override
			public void refresh() {
				DiagramEditor editor = (DiagramEditor) part;
				EObject root = editor.getDiagram().getElement();
				refresh(root);
			}

			protected void refresh(EObject eObject) {

				List<CanonicalEditPolicy> editPolicies = CanonicalEditPolicy.getRegisteredEditPolicies(eObject);
				for (Iterator<CanonicalEditPolicy> it = editPolicies.iterator(); it.hasNext();) {
					CanonicalEditPolicy nextEditPolicy = it.next();
					nextEditPolicy.refresh();
				}
				for (EObject content : eObject.eContents()) {
					refresh(content);
				}
			}

		};
	}

}