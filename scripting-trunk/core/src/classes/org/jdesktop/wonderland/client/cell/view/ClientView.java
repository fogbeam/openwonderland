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
package org.jdesktop.wonderland.client.cell.view;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.ViewCreateResponseMessage;

/**
 *
 * @author paulby
 */
public interface ClientView {

    /**
     * Return the id of this view
     * @return
     */
    public String getViewID();
    
    /**
     * Notification that the server view initialization has taken place
     * @param msg
     */
    public void serverInitialized(ViewCreateResponseMessage msg);
    
    /**
     * The ViewCell for this view has been configured on this client
     * @param cell
     */
    public void viewCellConfigured(CellID cellID);
}