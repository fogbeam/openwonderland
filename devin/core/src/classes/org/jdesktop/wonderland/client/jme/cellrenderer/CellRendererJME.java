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
package org.jdesktop.wonderland.client.jme.cellrenderer;

import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Interface for all JME based Cell Renderers
 * 
 * @author paulby
 */
@ExperimentalAPI
public interface CellRendererJME extends CellRenderer {

    /**
     * Return the 3D entity for this cell.
     * 
     * TODO - this is 3D specific, should have a generic mechanism
     * @return
     */
    public Entity getEntity();
    
}