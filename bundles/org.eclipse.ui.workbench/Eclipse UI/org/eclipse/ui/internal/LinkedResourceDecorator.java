/*******************************************************************************
 * Copyright (c) 2002 International Business Machines Corp. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0 
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.PlatformUI;

/**
 * A LinkedResourceDecorator decorates an element's image with a linked 
 * resource overlay. 
 * 
 * @since 2.1
 */
public class LinkedResourceDecorator implements ILightweightLabelDecorator {
	private static final String ICON_NAME = "link_ovr.gif";
	private static final ImageDescriptor LINKED;

	static {
		String fileName = WorkbenchImages.ICONS_PATH + "ovr16/" + ICON_NAME;

		LINKED =
			WorkbenchImages.getImageDescriptorFromPlugin(
				Platform.getPlugin(PlatformUI.PLUGIN_ID).getDescriptor(),
				fileName);
	}

	/**
	 * Creates a new <code>LinkedResourceDecorator</code>.
	 */
	public LinkedResourceDecorator() {
	}
	/**
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}
	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// no resources to dispose
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}
	/**
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/**
	 * Adds the linked resource overlay if the given element is a linked
	 * resource.
	 * 
	 * @param element element to decorate
	 * @param decoration. The decoration we are adding to
	 * @return the linked resource overlay or null if element is not a 
	 * 	linked resource.
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#getOverlay(java.lang.Object)
	 */
	public void decorate(Object element, IDecoration decoration) {

		if (element instanceof IResource == false)
			return;
		IResource resource = (IResource) element;
		if (resource.isLinked()) {
			decoration.addOverlay(LINKED);
		}

	}

}
