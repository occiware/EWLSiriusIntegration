package org.eclipse.epsilon.ewl.sirius;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CanonicalEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.RTFTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class ContributeKCVLUriWizardAction implements IObjectActionDelegate, IMenuCreator, MenuListener {
	protected ISelection selection;
	protected IWorkbenchPart targetPart;

	/**
	 * Constructor for ContributeWizardsAction.
	 */
	public ContributeKCVLUriWizardAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		// do nothing
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		action.setMenuCreator(this);
	}

	@Override
	public void dispose() {
	}

	@Override
	public Menu getMenu(Menu parent) {
		Menu wizardsMenu = new Menu(parent);
		wizardsMenu.addMenuListener(this);
		return wizardsMenu;
	}

	@Override
	public Menu getMenu(Control parent) {
		Menu wizardsMenu = new Menu(parent);
		wizardsMenu.addMenuListener(this);
		return wizardsMenu;
	}

	@Override
	public void menuHidden(MenuEvent e) {
		// TODO Auto-generated method stub

	}



	private Set<String> getEObjectURIsFor(Collection<EObject> eObjects) {
		final Set<String> eObjectURIs = new TreeSet<String>();
		for (EObject eObject : eObjects) {
			
			eObjectURIs.add(EcoreUtil.getURI(eObject).toString());
//			String eObjectNsURI = getTopEPackage(eObject).getNsURI();
//			eObjectURIs.add(eObjectNsURI);
		}
		return eObjectURIs;
	}

	

	@Override
	public void menuShown(MenuEvent e) {
		// populateWizardsMenu((Menu)e.widget);
		for (MenuItem item : ((Menu) e.widget).getItems()) {
			item.dispose();
		}

		List<EObject> eObjects = new ArrayList<EObject>();
		
		if ((selection instanceof IStructuredSelection)) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Iterator<?> it = structuredSelection.iterator();

			while (it.hasNext()) {
				Object next = it.next();

				EObject eObject = getEObject(next);
				if (eObject != null) {
					eObjects.add(eObject);
				}
			}

			final Set<String> eObjectURIs = getEObjectURIsFor(eObjects);

			final MenuItem wizardItem = new MenuItem((Menu) e.widget, SWT.NONE);
			wizardItem.setText("Copy URI(s) to clipboard");
			wizardItem.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					// do nothing
				}

				public void widgetSelected(SelectionEvent e) {
										
					EditingDomain editingDomain = getEditingDomain();
					if (editingDomain != null) {
						StringBuilder b = new StringBuilder();
						for (String s  :eObjectURIs){
							b.append("\"" + s + "\"\n");
						}
						 Clipboard clipboard = new Clipboard(e.display);
					        String plainText = b.toString();
					        String rtfText = "{\\rtf1\\b "+b.toString()+"}";
					        TextTransfer textTransfer = TextTransfer.getInstance();
					        RTFTransfer rftTransfer = RTFTransfer.getInstance();
					        clipboard.setContents(new String[]{plainText, rtfText}, new Transfer[]{textTransfer, rftTransfer});
					        clipboard.dispose();
						// editingDomain.getCommandStack().execute(command);
						
					} else {
						StringBuilder b = new StringBuilder();
						for (String s  :eObjectURIs){
							b.append("\"" + s + "\"\n");
						}
						 Clipboard clipboard = new Clipboard(e.display);
					        String plainText = b.toString();
					        String rtfText = "{\\rtf1\\b "+b.toString()+"}";
					        TextTransfer textTransfer = TextTransfer.getInstance();
					        RTFTransfer rftTransfer = RTFTransfer.getInstance();
					        clipboard.setContents(new String[]{plainText, rtfText}, new Transfer[]{textTransfer, rftTransfer});
					        clipboard.dispose();
						
					}
					refresh();
					
					
				}
			});
		}

	}
	
	
	public void refresh() {
		DiagramEditor editor = (DiagramEditor) targetPart;
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

	protected EObject getEObject(Object selected) {

		if (selected instanceof IGraphicalEditPart) {
			IGraphicalEditPart gep = (IGraphicalEditPart) selected;
			EObject semanticElement = gep.resolveSemanticElement();
			if (semanticElement instanceof DSemanticDecorator) {
				DSemanticDecorator sem1 = (DSemanticDecorator) semanticElement;
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


	

}
