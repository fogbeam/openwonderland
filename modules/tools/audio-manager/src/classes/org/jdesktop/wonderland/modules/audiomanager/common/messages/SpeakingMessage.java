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
package org.jdesktop.wonderland.modules.audiomanager.common.messages;

import org.jdesktop.wonderland.common.ExperimentalAPI;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Message indicating whether someone started or stopped speaking.
 * @author jprovino
 */
@ExperimentalAPI
public class SpeakingMessage extends Message {

    private String callID;
    private String username;
    private boolean isSpeaking;

    public SpeakingMessage(String callID, String username, boolean isSpeaking) {
	this.callID = callID;
	this.username = username;
	this.isSpeaking = isSpeaking;
    }

    public String getCallID() {
	return callID;
    }

    public String getUsername() {
	return username;
    }

    public boolean isSpeaking() {
	return isSpeaking;
    }

}