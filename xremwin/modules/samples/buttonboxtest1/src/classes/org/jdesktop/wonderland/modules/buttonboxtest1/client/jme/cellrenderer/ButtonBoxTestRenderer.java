/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.modules.buttonboxtest1.client.jme.cellrenderer;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;
import org.jdesktop.wonderland.client.input.EventListener;

/**
 * This renders a 3D representation of button box test cell.
 * This is a button box with three buttons: a red button, a green button,
 * and a blue button.
 */
public class ButtonBoxTestRenderer extends BasicRenderer {

    private static float BASE_HEIGHT = 1.5f;
    private static float BASE_DEPTH = 1f;

    private static int NUM_BUTTONS = 3;
    private static float BUTTON_WIDTH = 1f;
    private static float BUTTON_HEIGHT = 1f;
    private static float BUTTON_DEPTH = 0.5f;
    private static float BUTTON_SPACING = 0.5f;

    private static ColorRGBA BASE_COLOR = ColorRGBA.gray;
    private static ColorRGBA[] BUTTON_COLORS = new ColorRGBA[] {
        ColorRGBA.red, ColorRGBA.green, ColorRGBA.blue
    };

    private ButtonBox buttonBox;

    public ButtonBoxTestRenderer(Cell cell) {
        super(cell);
    }

    @Override
    protected Node createSceneGraph(Entity entity) {

        // Create the root node of the test
        Node sceneRoot = new Node("Button Box Test Scene Root Node");

        // Attach root node to to the root entity by placing it into an attached render component
        RenderComponent rc =
                ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(sceneRoot);
        entity.addComponent(RenderComponent.class, rc);

        // Create the button box
	buttonBox = new ButtonBox(BASE_HEIGHT, BASE_DEPTH, NUM_BUTTONS, BUTTON_WIDTH,
                                           BUTTON_HEIGHT, BUTTON_DEPTH, BUTTON_SPACING);
	buttonBox.setBaseColor(BASE_COLOR);
	for (int i=0; i<NUM_BUTTONS; i++) {
	    buttonBox.setButtonColor(i, BUTTON_COLORS[i]);
	}

        // Attach button box to the top-most entity
        buttonBox.attachToEntity(entity);

        return sceneRoot;
    }

    /**
     * Attach event listeners to the button box.
     * @param baseListener The listener for events which happen when the cursor is over the base.
     * @param buttonListener The listener of events which happen when the cursor is over a button.
     */
    public void addEventListeners (EventListener baseListener, EventListener buttonListener) {
        buttonBox.addEventListeners(baseListener, buttonListener);
    }

    /**
     * Detaches both listeners from the button box. The button box will no longer be input sensitive.
     */
    public void removeEventListeners () {
        buttonBox.removeEventListeners();
    }
}

