package org.eclipse.epsilon.ewl.sirius;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.epsilon.ewl.emf.AbstractContributeWizardsAction;
import org.eclipse.epsilon.ewl.emf.WorkbenchPartRefresher;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.navigator.CommonNavigator;

public class ContributeEmfWizardsAction extends AbstractContributeWizardsAction	 {

	protected EObject getEObject(Object selected) {

		return (EObject) selected;
	}

	protected EditingDomain getEditingDomain() {
	    TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(((IStructuredSelection) selection).getFirstElement());
	    return domain;
	}
	

	protected void execute(Command command) {
		EditingDomain editingDomain = getEditingDomain();		
		if (editingDomain != null) {
			editingDomain.getCommandStack().execute(command);
		}
		else {
			command.execute();
		}
	}


	@Override
	protected WorkbenchPartRefresher getWorkbenchPartRefresher() {
		return new WorkbenchPartRefresher() {

			@Override
			public void refresh() {
				CommonNavigator editor = (CommonNavigator) part;
				editor.getCommonViewer().refresh();
				
			}


		};
	}

}