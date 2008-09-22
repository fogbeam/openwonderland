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
package org.jdesktop.wonderland.modules.sample.server;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.sun.sgs.app.ClientSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.config.CellConfig;
import org.jdesktop.wonderland.modules.sample.common.SampleCellConfig;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.setup.BasicCellSetup;
import org.jdesktop.wonderland.server.setup.BasicCellSetupHelper;
import org.jdesktop.wonderland.server.setup.BeanSetupMO;


/**
 * A sample cell
 * @author jkaplan
 */
@ExperimentalAPI
public class SampleCellMO extends CellMO implements BeanSetupMO { 
    	
    /** Default constructor, used when cell is created via WFS */
    public SampleCellMO() {
    }

    public SampleCellMO(Vector3f center, float size) {
        super(new BoundingBox(new Vector3f(), size, size, size), new CellTransform(null, center));
    }
    
    @Override 
    protected String getClientCellClassName(ClientSession clientSession, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.sample.client.SampleCell";
    }

    @Override
    public CellConfig getClientStateData(ClientSession clientSession, ClientCapabilities capabilities) {
        return new SampleCellConfig();
    }

    @Override
    public void setupCell(BasicCellSetup setup) {
        super.setupCell(setup);
    }

    @Override
    public void reconfigureCell(BasicCellSetup setup) {
        super.reconfigureCell(setup);
        setupCell(setup);
    }

     /**
     * Return a new BasicCellSetup Java bean class that represents the current
     * state of the cell.
     * 
     * @return a JavaBean representing the current state
     */
    public BasicCellSetup getCellMOSetup() {
        /* Create a new BasicCellState and populate its members */
        SampleCellSetup setup = new SampleCellSetup();
        
        /* Set the bounds of the cell */
        BoundingVolume bounds = this.getLocalBounds();
        if (bounds != null) {
            setup.setBounds(BasicCellSetupHelper.getSetupBounds(bounds));
        }

        /* Set the origin, scale, and rotation of the cell */
        CellTransform transform = this.getLocalTransform(null);
        if (transform != null) {
            setup.setOrigin(BasicCellSetupHelper.getSetupOrigin(transform));
            setup.setRotation(BasicCellSetupHelper.getSetupRotation(transform));
            setup.setScaling(BasicCellSetupHelper.getSetupScaling(transform));
        }
        return setup;
    }
}
