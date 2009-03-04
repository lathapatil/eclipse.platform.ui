/*******************************************************************************
 * Copyright (c) 2008, 2009 Oakland Software Incorporated and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Oakland Software Incorporated - initial API and implementation
 *.....IBM Corporation - fixed dead code warning
 *******************************************************************************/
package org.eclipse.ui.tests.navigator;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.tests.harness.util.DisplayHelper;
import org.eclipse.ui.tests.navigator.extension.TestLabelProvider;
import org.eclipse.ui.tests.navigator.extension.TestLabelProviderCyan;
import org.eclipse.ui.tests.navigator.extension.TestLabelProviderGreen;

public class LabelProviderTest extends NavigatorTestBase {

	private static final boolean DEBUG = false;

	public LabelProviderTest() {
		_navigatorInstanceId = "org.eclipse.ui.tests.navigator.OverrideTestView";
	}

	public void testBlankLabelProvider() throws Exception {

		TestLabelProvider._blank = true;
		
		_contentService.bindExtensions(new String[] { TEST_CONTENT_OVERRIDDEN1,
				TEST_CONTENT_OVERRIDE1 }, false);
		_contentService.getActivationService().activateExtensions(
				new String[] { TEST_CONTENT_OVERRIDE1, TEST_CONTENT_OVERRIDDEN1 }, true);

		refreshViewer();

		TreeItem[] rootItems = _viewer.getTree().getItems();

		if (DEBUG) {
			DisplayHelper.sleep(Display.getCurrent(), 10000000);
		}

		if (!rootItems[0].getText().equals(""))
			fail("Wrong text: " + rootItems[0].getText());
	}

	// bug 252293 [CommonNavigator] LabelProviders do not obey override rules
	public void testSimpleResFirst() throws Exception {

		_contentService.bindExtensions(new String[] { TEST_CONTENT_OVERRIDDEN1,
				TEST_CONTENT_OVERRIDE1 }, false);
		_contentService.getActivationService().activateExtensions(
				new String[] { TEST_CONTENT_OVERRIDE1, TEST_CONTENT_OVERRIDDEN1 }, true);

		refreshViewer();

		TreeItem[] rootItems = _viewer.getTree().getItems();

		if (DEBUG) {
			DisplayHelper.sleep(Display.getCurrent(), 10000000);
		}

		if (!rootItems[0].getText().startsWith("Green"))
			fail("Wrong text: " + rootItems[0].getText());
		assertEquals(TestLabelProviderGreen.getTestColor(), rootItems[0]
				.getBackground(0));
	}

	
	// bug 252293 [CommonNavigator] LabelProviders do not obey override rules
	public void testSimpleResLast() throws Exception {

		_contentService.bindExtensions(new String[] { TEST_CONTENT_OVERRIDDEN2,
				TEST_CONTENT_OVERRIDE2 }, false);
		_contentService.getActivationService().activateExtensions(
				new String[] { TEST_CONTENT_OVERRIDDEN2, TEST_CONTENT_OVERRIDE2 }, true);

		refreshViewer();
		TreeItem[] rootItems = _viewer.getTree().getItems();
		if (!rootItems[0].getText().startsWith("Cyan"))
			fail("Wrong text: " + rootItems[0].getText());
		assertEquals(TestLabelProviderCyan.getTestColor(), rootItems[0]
				.getBackground(0));
	}
	
}
