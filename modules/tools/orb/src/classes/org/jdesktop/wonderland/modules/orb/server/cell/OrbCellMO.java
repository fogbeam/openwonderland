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
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.orb.server.cell;

import org.jdesktop.wonderland.modules.orb.common.OrbCellSetup;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.ZeroVolumeSpatializer;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;

import com.sun.voip.CallParticipant;
import com.sun.voip.client.connector.CallStatus;

import java.io.IOException;
import java.lang.String;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.jdesktop.wonderland.common.cell.config.CellConfig;

import org.jdesktop.wonderland.common.cell.setup.BasicCellSetup;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.config.CellConfig;

import org.jdesktop.wonderland.common.cell.setup.BasicCellSetup;

import org.jdesktop.wonderland.modules.orb.common.OrbCellSetup;
import org.jdesktop.wonderland.modules.orb.common.OrbCellConfig;

import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;

import org.jdesktop.wonderland.server.setup.BasicCellSetupHelper;
import org.jdesktop.wonderland.server.setup.BeanSetupMO;

import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.CommsManagerFactory;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

/**
 * A server cell that provides Orb functionality
 * @author jprovino
 */
public class OrbCellMO extends CellMO implements BeanSetupMO {

    private static final Logger logger =
        Logger.getLogger(OrbCellMO.class.getName());
     
    private ManagedReference<OrbMessageHandler> orbMessageHandlerRef;

    public OrbCellMO() {
	if (orbMessageHandlerRef == null) {
	    addComponent(new ChannelComponentMO(this));

            orbMessageHandlerRef = AppContext.getDataManager().createReference(
		new OrbMessageHandler(this));
	}
    }
    
    public OrbCellMO(Vector3f center, float size) {
        super(new BoundingBox(new Vector3f(), size, size, size), 
	    new CellTransform(null, center));

	if (orbMessageHandlerRef == null) {
	    addComponent(new ChannelComponentMO(this));

            orbMessageHandlerRef = AppContext.getDataManager().createReference(
		new OrbMessageHandler(this));
	}
    }

    public OrbCellMO(String callID) {
	if (orbMessageHandlerRef == null) {
	    addComponent(new ChannelComponentMO(this));

	    logger.fine("Adding channel component...");

            orbMessageHandlerRef = AppContext.getDataManager().createReference(
		new OrbMessageHandler(this));
	}

	orbMessageHandlerRef.get().setCallID(callID);
    }

    @Override
    protected String getClientCellClassName(ClientSession clientSession, 
	    ClientCapabilities capabilities) {

        return "org.jdesktop.wonderland.modules.orb.client.cell.OrbCell";
    }

    @Override
    public CellConfig getCellConfig(ClientSession clientSession, 
	    ClientCapabilities capabilities) {

        OrbCellConfig config = new OrbCellConfig();

        config.addClientComponentClasses(new String[] {
              "org.jdesktop.wonderland.client.cell.ChannelComponent"
        });

	return config;
    }

    @Override
    public void setupCell(BasicCellSetup setup) {
        super.setupCell(setup);

	OrbCellSetup pcs = (OrbCellSetup) setup;
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
        OrbCellSetup setup = new OrbCellSetup();

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
