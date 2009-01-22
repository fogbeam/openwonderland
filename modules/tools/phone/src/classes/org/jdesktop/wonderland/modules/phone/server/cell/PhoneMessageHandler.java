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
package org.jdesktop.wonderland.modules.phone.server.cell;

import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.modules.phone.common.CallListing;

import org.jdesktop.wonderland.modules.phone.common.messages.CallInvitedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.CallEndedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.CallEstablishedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.LockUnlockMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.LockUnlockResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlayTreatmentMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PhoneControlMessage;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.ZeroVolumeSpatializer;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;

import com.sun.voip.CallParticipant;
import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;


import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.MultipleParentException;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState.Origin;

import org.jdesktop.wonderland.server.UserManager;

import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellMOFactory;

import org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO;

import org.jdesktop.wonderland.modules.orb.common.OrbCellServerState;

import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

import org.jdesktop.wonderland.server.cell.ChannelComponentImplMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * A server cell that provides conference phone functionality
 * @author jprovino
 */
public class PhoneMessageHandler extends AbstractComponentMessageReceiver
	implements Serializable {

    private static final Logger logger =
        Logger.getLogger(PhoneMessageHandler.class.getName());
     
    private ManagedReference<PhoneStatusListener> phoneStatusListenerRef;

    private int callNumber = 0;

    public PhoneMessageHandler(PhoneCellMO phoneCellMO) {
	super(phoneCellMO);

	PhoneStatusListener phoneStatusListener = new PhoneStatusListener(phoneCellMO);
	
	phoneStatusListenerRef =  AppContext.getDataManager().createReference(phoneStatusListener);

        ChannelComponentMO channelComponentMO = getChannelComponent();

        channelComponentMO.addMessageReceiver(EndCallMessage.class, this);
        channelComponentMO.addMessageReceiver(JoinCallMessage.class, this);
        channelComponentMO.addMessageReceiver(LockUnlockMessage.class, this);
        channelComponentMO.addMessageReceiver(PlaceCallMessage.class, this);
        channelComponentMO.addMessageReceiver(PlayTreatmentMessage.class, this);
    }

    public void done() {
	ChannelComponentMO channelComponentMO = getChannelComponent();

	channelComponentMO.removeMessageReceiver(EndCallMessage.class);
	channelComponentMO.removeMessageReceiver(JoinCallMessage.class);
	channelComponentMO.removeMessageReceiver(LockUnlockMessage.class);
	channelComponentMO.removeMessageReceiver(PlaceCallMessage.class);
	channelComponentMO.removeMessageReceiver(PlayTreatmentMessage.class);
    }

    public void messageReceived(final WonderlandClientSender sender, 
	    final WonderlandClientID clientID, final CellMessage message) {

	PhoneControlMessage msg = (PhoneControlMessage) message;

	logger.fine("got message " + msg);

	PhoneCellMO phoneCellMO = (PhoneCellMO) getCell();

	if (message instanceof LockUnlockMessage) {
	    LockUnlockMessage m = (LockUnlockMessage) message;

	    boolean successful = true;

	    if (m.getPassword() != null) {
		successful = m.getPassword().equals(phoneCellMO.getPassword());
	    }

	    if (successful) {
		phoneCellMO.setLocked(!phoneCellMO.getLocked());
	        phoneCellMO.setKeepUnlocked(m.keepUnlocked());
	    }

	    logger.fine("locked " + phoneCellMO.getLocked() + " successful " 
		+ successful + " pw " + m.getPassword());

            LockUnlockResponseMessage response = 
		new LockUnlockResponseMessage(phoneCellMO.getCellID(), phoneCellMO.getLocked(), successful);

	    sender.send(response);
	    return;
        }

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

        CallListing listing = msg.getCallListing();
              
	String externalCallID = getExternalCallID(listing);

	Call externalCall = vm.getCall(externalCallID);

	Player externalPlayer = null;

	if (externalCall != null) {
	    externalPlayer = externalCall.getPlayer();
	}

	String softphoneCallID = listing.getSoftphoneCallID();

	Call softphoneCall = null;

	Player softphonePlayer = null;

	AudioGroup audioGroup = null;

	String audioGroupId = null;

	if (softphoneCallID != null) {
	    softphoneCall = vm.getCall(softphoneCallID);

	    if (softphoneCall != null) {
	        softphonePlayer = softphoneCall.getPlayer();
	    }
        
	    audioGroupId = softphoneCallID + "_" + externalCallID;

	    audioGroup = vm.getAudioGroup(audioGroupId);
	}

	logger.fine("EXTERNAL CALLID IS " + externalCallID + " " + msg
	    + " softphone callID " + softphoneCallID + " softphone call " 
	    + softphoneCall + " softphone player " + softphonePlayer);

	if (message instanceof PlayTreatmentMessage) {
	    PlayTreatmentMessage m = (PlayTreatmentMessage) message;

	    logger.fine("play treatment " + m.getTreatment() 
		+ " to " + listing.getExternalCallID() + " echo " + m.echo());

            if (listing.simulateCalls() == true) {
		return;
	    }

	    try {
		externalCall.playTreatment(m.getTreatment());
	    } catch (IOException e) {
		logger.warning("Unable to play treatment to " + externalCall + ":  "
		    + e.getMessage());
	    }

	    if (m.echo() == false) {
		return;
	    }

	    logger.fine("echoing treatment to " + softphoneCallID);

	    try {
		softphoneCall.playTreatment(m.getTreatment());
	    } catch (IOException e) {
		logger.warning("Unable to play treatment to " + softphoneCall + ":  "
		    + e.getMessage());
	    }

	    return;
	}

	if (msg instanceof PlaceCallMessage) {
            //Our phone cell is asking us to begin a new call.

	    if (listing.simulateCalls() == false) {
		relock(sender);
	    }

	    logger.fine("Got place call message " + externalCallID);

	    phoneStatusListenerRef.get().mapCall(externalCallID, clientID, 
		listing);

	    PlayerSetup playerSetup = new PlayerSetup();
	    //playerSetup.x =  translation.x;
	    //playerSetup.y =  translation.y;
	    //playerSetup.z =  translation.z;
	    playerSetup.isOutworlder = true;
	    playerSetup.isLivePlayer = true;

            if (listing.simulateCalls()) {
                FakeVoiceManager.getInstance().setupCall(
		    externalCallID, listing.getContactNumber());
            } else {                               
		CallSetup setup = new CallSetup();
	
		CallParticipant cp = new CallParticipant();

		setup.cp = cp;

		try {
		    setup.bridgeInfo = vm.getVoiceBridge();
	 	} catch (IOException e) {
		    logger.warning("Unable to get voice bridge for call " + cp + ":  "
			+ e.getMessage());
		    return;
		}

		cp.setPhoneNumber(listing.getContactNumber());
		cp.setCallId(externalCallID);
		cp.setConferenceId(vm.getConferenceId());
		cp.setVoiceDetection(true);
		cp.setDtmfDetection(true);
		cp.setVoiceDetectionWhileMuted(true);
		cp.setHandleSessionProgress(true);

        	if (listing.simulateCalls()) { 
            	    FakeVoiceManager.getInstance().addCallStatusListener(
			phoneStatusListenerRef.get(), externalCallID);
		} else {
		    setup.listener = phoneStatusListenerRef.get();
		}

		try {
                    externalCall = vm.createCall(externalCallID, setup);
	 	} catch (IOException e) {
		    logger.warning("Unable to create call " + cp + ":  "
			+ e.getMessage());
		    return;
		}

	    	externalPlayer = vm.createPlayer(externalCallID, playerSetup);

		externalCall.setPlayer(externalPlayer);

		logger.fine("set external player");

		externalPlayer.setCall(externalCall);

		logger.fine("set external call");

                if (listing.isPrivate()) {
		    /*
		     * Allow caller and callee to hear each other
		     */
		    AudioGroupSetup audioGroupSetup = new AudioGroupSetup();
		    audioGroupSetup.spatializer = new FullVolumeSpatializer();

		    audioGroup = vm.createAudioGroup(audioGroupId, audioGroupSetup);
		    audioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.EXCLUSIVE));
		    audioGroup.addPlayer(softphonePlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.EXCLUSIVE));
		} else {
		    AudioGroup defaultLivePlayerAudioGroup = 
		        vm.getDefaultLivePlayerAudioGroup();

		    defaultLivePlayerAudioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.PUBLIC));

		    AudioGroup defaultStationaryPlayerAudioGroup = 
		        vm.getDefaultStationaryPlayerAudioGroup();

		    defaultStationaryPlayerAudioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(false, 
		        AudioGroupPlayerInfo.ChatType.PUBLIC));
		}

		logger.fine("done with audio groups");
            }
            
	    if (externalCall != null) {
	        externalCallID = externalCall.getId();
	    }

	    logger.fine("Setting actual call id to " + externalCallID);

	    listing.setExternalCallID(externalCallID);  // set actual call Id

            //Check implicit privacy settings
            if (listing.isPrivate()) {
                /** HARRISNOTE: We need our client name later in order to 
                 * setup private spatializers. But because we didn't know 
                 * our proper client name in the PhoneCell, we update the 
                 * callListing now that we do.
                 **/
		listing.setPrivateClientName(externalCallID);

                /*
		 * Set the call audio to whisper mode until the caller 
		 * chooses to join the call.
		 */
                if (listing.simulateCalls() == false) {
                    //Mute the two participants to the outside world
                    logger.fine("attenuate other groups");
		    softphonePlayer.attenuateOtherGroups(audioGroup, 0, 0);
                    logger.fine("back from attenuate other groups");
                }
            } else {
                spawnOrb(externalCallID, listing.simulateCalls());
	    }

            if (listing.simulateCalls() == false) {
                //Place the calls audio at the phones position
                //translation = new vector3f();                
                //getOriginWorld().get(translation);                
                //externalPlayer.moved(translation.x, translation.y, translation.z, 0);
            }
          
            /*
	     * Send PLACE_CALL_RESPONSE message back to all the clients 
	     * to signal success.
	     */
            sender.send(clientID, new PlaceCallResponseMessage(
		phoneCellMO.getCellID(), listing, true));

	    logger.fine("back from notifying user");
	    return;
	}

	if (msg instanceof JoinCallMessage) {
            //Our phone cell wants us to join the call into the world.
            
            if (listing.simulateCalls() == false) {
                //Stop any current ringing.
	        try {
                    softphoneCall.stopTreatment("ring_tone.au");
	        } catch (IOException e) {
		    logger.fine("Unable to stop treatment to " + softphoneCall + ":  "
		        + e.getMessage());
	        }

		AudioGroup defaultLivePlayerAudioGroup = 
		    vm.getDefaultLivePlayerAudioGroup();

		defaultLivePlayerAudioGroup.addPlayer(externalPlayer, 
		    new AudioGroupPlayerInfo(true, 
		    AudioGroupPlayerInfo.ChatType.PUBLIC));

		AudioGroup defaultStationaryPlayerAudioGroup = 
		    vm.getDefaultStationaryPlayerAudioGroup();

		defaultStationaryPlayerAudioGroup.addPlayer(externalPlayer, 
		    new AudioGroupPlayerInfo(false, 
		    AudioGroupPlayerInfo.ChatType.PUBLIC));

	        softphonePlayer.attenuateOtherGroups(audioGroup, 
		    AudioGroup.DEFAULT_SPEAKING_ATTENUATION,
		    AudioGroup.DEFAULT_LISTEN_ATTENUATION);

	        vm.removeAudioGroup(audioGroupId);
            }
            
            listing.setPrivateClientName("");
              
            //Inform the PhoneCells that the call has been joined successfully
            sender.send(clientID, new JoinCallResponseMessage(
		phoneCellMO.getCellID(), listing, true));
            
            spawnOrb(externalCallID, false);
	    return;
	}

	if (msg instanceof EndCallMessage) {
	    logger.fine("simulate is " + listing.simulateCalls() 
		+ " external call " + externalCall);

            if (listing.simulateCalls() == false) {
		relock(sender);

		if (externalCall != null) {
		    try {
                        vm.endCall(externalCall, true);
	            } catch (IOException e) {
		        logger.warning(
			    "Unable to end call " + externalCall + ":  "
		            + e.getMessage());
	            }
		}

		if (audioGroup != null) {
                    if (listing.isPrivate()) {
	        	softphonePlayer.attenuateOtherGroups(audioGroup, 
			    AudioGroup.DEFAULT_SPEAKING_ATTENUATION,
		    	    AudioGroup.DEFAULT_LISTEN_ATTENUATION);
	            }

	            vm.removeAudioGroup(audioGroupId);
		}
            } else {                
                FakeVoiceManager.getInstance().endCall(externalCallID);
            }         
            
            //Send SUCCESS to phone cell
            sender.send(clientID, new EndCallResponseMessage(
		phoneCellMO.getCellID(), listing, true, 
		"User requested call end"));
	    return;
        } 

	logger.fine("Uknown message type:  " + msg);
    }
   
    private void relock(WonderlandClientSender sender) {
	PhoneCellMO phoneCellMO = (PhoneCellMO) getCell();

	if (phoneCellMO.getKeepUnlocked() == false && phoneCellMO.getLocked() == false) {
	    phoneCellMO.setLocked(true);

            LockUnlockResponseMessage response = new LockUnlockResponseMessage(phoneCellMO.getCellID(), true, true);

            sender.send(response);
	}
    }

    private String getExternalCallID(CallListing listing) {
	String externalCallID = listing.getExternalCallID();

	if (externalCallID != null && externalCallID.length() > 0) {
	    logger.fine("using existing call id " + externalCallID);
	    return externalCallID;
	}

	synchronized (this) {
	    callNumber++;

            return getCell().getCellID() + "_" + callNumber;
	}
    }

    private void spawnOrb(String externalCallID, boolean simulateCalls) {
	/*
	 * XXX I was trying to get this to delay for 2 seconds,
	 * But there are no managers in the system context in which run() runs.
	 */
        //Spawn the Orb to represent the new public call.

	logger.fine("Spawning orb...");

	CellMO cellMO = getCell();

	BoundingVolume boundingVolume = cellMO.getWorldBounds();

	Vector3f center = new Vector3f();

	boundingVolume.getCenter(center);

	center.setY((float)1.5);

	System.out.println("phone bounding volume:  " + boundingVolume
	    + " center " + center);

        String cellType = 
	    "org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO";

        OrbCellMO orbCellMO = (OrbCellMO) CellMOFactory.loadCellMO(cellType, 
	    center, (float) .5, externalCallID, simulateCalls);

	if (orbCellMO == null) {
	    logger.warning("Unable to spawn orb");
	    return;
	}

	OrbCellServerState orbCellServerState = new OrbCellServerState();

	orbCellServerState.setOrigin(new Origin(center));

	try {
            orbCellMO.setServerState(orbCellServerState);
        } catch (ClassCastException e) {
            logger.warning("Error setting up new cell " +
                orbCellMO.getName() + " of type " +
                orbCellMO.getClass() + e.getMessage());
            return;
        }

	try {
	    CellManagerMO.getCellManager().insertCellInWorld(orbCellMO);
	} catch (MultipleParentException e) {
	    logger.warning("Can't insert orb in world:  " + e.getMessage());
	    return;
	}
    }

}
