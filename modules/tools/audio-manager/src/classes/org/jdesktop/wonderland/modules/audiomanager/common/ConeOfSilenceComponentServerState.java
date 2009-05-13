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
package org.jdesktop.wonderland.modules.audiomanager.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * The ConeOfSilenceCellServerState class is the cell that renders a coneofsilence cell in
 * world.
 * 
 * @author jprovino
 */
@XmlRootElement(name="cone-of-silence-component")
@ServerState
public class ConeOfSilenceComponentServerState extends CellComponentServerState {

    @XmlElement(name = "name")
    private String name = "ConeOfSilence";
    @XmlElement(name = "fullVolumeRadius")
    private float fullVolumeRadius = 1.5f;

    /** Default constructor */
    public ConeOfSilenceComponentServerState() {
    }

    public ConeOfSilenceComponentServerState(String name, float fullVolumeRadius) {
        this.name = name;
        this.fullVolumeRadius = fullVolumeRadius;
    }

    public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.modules.audiomanager.server.ConeOfSilenceComponentMO";
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public String getName() {
        return name;
    }

    public void setFullVolumeRadius(float fullVolumeRadius) {
        this.fullVolumeRadius = fullVolumeRadius;
    }

    @XmlTransient
    public float getFullVolumeRadius() {
        return fullVolumeRadius;
    }

}