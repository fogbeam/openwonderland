/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.coneofsilence.common;

import org.jdesktop.wonderland.common.cell.state.CellClientState;

/**
 * The ConeOfSilenceCellSetup class is the cell that renders a coneofsilence cell in
 * world.
 * 
 * @author jkaplan
 */
public class ConeOfSilenceCellClientState extends CellClientState {

    private String name;

    private float fullVolumeRadius;

    /** Default constructor */
    public ConeOfSilenceCellClientState() {
    }
    
    public ConeOfSilenceCellClientState(String name, float fullVolumeRadius) {
	this.name = name;
	this.fullVolumeRadius = fullVolumeRadius;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFullVolumeRadius(float fullVolumeRadius) {
        this.fullVolumeRadius = fullVolumeRadius;
    }

    public float getFullVolumeRadius() {
        return fullVolumeRadius;
    }

}