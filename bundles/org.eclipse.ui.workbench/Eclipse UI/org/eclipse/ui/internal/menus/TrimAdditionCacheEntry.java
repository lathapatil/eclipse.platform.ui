/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.menus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.expressions.Expression;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.menus.IWidget;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.util.Util;
import org.eclipse.ui.menus.IWorkbenchWidget;

/**
 * Handles the top level caching for 3.2 style trim
 * contributions.
 * 
 * @since 3.3
 * 
 */
public class TrimAdditionCacheEntry extends MenuCacheEntry {
	private IConfigurationElement additionElement;
	private MenuLocationURI uri = null;
	
	/**
	 * The map contains {@link IWorkbenchWidget} entries
	 * for widgets that have failed to load on a previous
	 * attempt. Used to prevent multiple retries at
	 * loading a widget (which spams the log).
	 */
	private Map failedWidgets = new HashMap();
	/**
	 * Maps the widget back to it's configurtation element
	 */
	private Map widgetToConfigElementMap = new HashMap();
	

	// Caches

	/**
	 * Maps an IContributionItem to its corresponding IConfigurationElement
	 */
	Map iciToConfigElementMap = new HashMap();

	public TrimAdditionCacheEntry(IConfigurationElement element,
			MenuLocationURI uri, IMenuService service) {
		super(service);
		this.additionElement = element;
		this.uri = uri;

		
		if (additionElement == null || this.uri == null)
			menuService = null;
	}

	/**
	 * Populate the list
	 * 
	 * @param additions
	 */
	public void getContributionItems(List additions) {
		additions.clear();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.menus.MenuCacheEntry#generateSubCaches()
	 */
	public void generateSubCaches() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.menus.MenuCacheEntry#getVisibleWhenForItem(org.eclipse.jface.action.IContributionItem)
	 */
	public Expression getVisibleWhenForItem(IContributionItem item) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public String getId() {
		return additionElement.getAttribute(IWorkbenchRegistryConstants.ATT_ID);
	}

	/**
	 * @return
	 */
	public boolean isAtStart() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return The list of IConfigurationElements representing
	 * widgets to be added into this 'group'
	 */
	private List getWidgetConfigs() {
		List widgetConfigs = new ArrayList();
		
		// Return to the 'root' of the config tree and gather all elements
		// for this 'group'. Note that while this is sub-optimal
		// performace-wise that there are expected to be -very-
		// few contributions in total (i.e. 10's, not 100's)
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		final IConfigurationElement[] widgetElements = registry
				.getConfigurationElementsFor(IWorkbenchRegistryConstants.EXTENSION_MENUS);

		// Locate all 'widget' additions appropriate for -this- group
		for (int i = 0; i < widgetElements.length; i++) {
			// Only process 'widget' entries
			if (!IWorkbenchRegistryConstants.TAG_WIDGET.equals(widgetElements[i].getName()))
				continue;
			
			// Define the initial URI spec
			if (widgetElements[i].getChildren(IWorkbenchRegistryConstants.TAG_LOCATION).length > 0) {
				IConfigurationElement location = widgetElements[i].getChildren(IWorkbenchRegistryConstants.TAG_LOCATION)[0];
				if (location.getChildren(IWorkbenchRegistryConstants.TAG_BAR).length > 0) {
					IConfigurationElement bar = location.getChildren(IWorkbenchRegistryConstants.TAG_BAR)[0];

					// The bar's path represents the 'group' it should go into
					String path = bar.getAttribute(IWorkbenchRegistryConstants.ATT_PATH);
					if (path != null && path.equals(getId()))
							widgetConfigs.add(widgetElements[i]);
				}
			}
		}
		
		return widgetConfigs;
	}
	
	/**
	 * Attempts to load -all- widgets for this entry and
	 * keeps track of the successful loads only. Only elements
	 * who can be successfully loaded will be seen by the
	 * builder.
	 * 
	 * @return The list of <code>IWorkbenchWidget</code> entries
	 * that have been successfully loaded 
	 */
	public List getWidgets() {
		List loadedWidgets = new ArrayList();
		
		// Get the widget config elements for this 'group'
		List widgetConfigs = getWidgetConfigs();
		for (Iterator iterator = widgetConfigs.iterator(); iterator
				.hasNext();) {
			IConfigurationElement widgetCE = (IConfigurationElement) iterator.next();
			
			// skip elements that are known to fail
			if (failedWidgets.containsKey(widgetCE))
				continue;
			
			IWorkbenchWidget loadedWidget = loadWidget(widgetCE);

			// Either add it to the 'valid' list or mark it
			// as failed
			if (loadedWidget != null) {
				loadedWidgets.add(loadedWidget);
				widgetToConfigElementMap.put(loadedWidget, widgetCE);
			}
			else
				failedWidgets.put(widgetCE, widgetCE);
		}
		
		return loadedWidgets;
	}

	/**
	 * Attempts to load the executable extension defined within the given
	 * configuration element. An error is logged for any widget that fails
	 * to load.
	 * 
	 * @param widgetCE The {@link IConfigurationElement} containing the
	 * widget's 'class' specification.
	 * 
	 * @return The loaded {@link IWorkbenchWidget} or <code>null</code>
	 * if the loading fails
	 */
	private IWorkbenchWidget loadWidget(IConfigurationElement widgetCE) {
		return (IWorkbenchWidget) Util.safeLoadExecutableExtension(widgetCE,
					IWorkbenchRegistryConstants.ATT_CLASS,
					IWorkbenchWidget.class);
	}

	/**
	 * @param widget The {@link IWorkbenchWidget} to get the defining configuration
	 * element for.
	 * 
	 * @return The defining {@link IConfigurationElement}
	 */
	public IConfigurationElement getElement(IWorkbenchWidget widget) {
		return (IConfigurationElement) widgetToConfigElementMap.get(widget);
	}

	/**
	 * Returns whether or not the defining {@link IConfigurationElement}
	 * declares that the widget shoud be resizeable.
	 * 
	 * @param widgetElement the {@link IConfigurationElement} declaring this
	 * widget.
	 * 
	 * @return <code>true</code> iff the resulting widget should be resizeable
	 */
	public boolean isResizeable(IConfigurationElement widgetElement) {
		return false;
	}

	/**
	 * @param widget
	 */
	public void removeWidget(IWidget widget) {
		widgetToConfigElementMap.remove(widget);
	}
}
