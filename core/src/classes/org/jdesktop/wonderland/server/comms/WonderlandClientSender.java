/**
 * Project Wonderland
 *
 * $RCSfile:$
 *
 * Copyright (c) 2004-2007, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision:$
 * $Date:$
 * $State:$
 */
package org.jdesktop.wonderland.server.comms;

import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ClientSession;
import java.util.Set;
import org.jdesktop.wonderland.ExperimentalAPI;
import org.jdesktop.wonderland.common.comms.ClientType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * A sender that sends messages to clients connected with
 * the given ClientType.  The list of clients available with
 * the given type is maintained by the WonderlandSessionListener in response 
 * to attach and detach requests.  
 * <p>
 * Senders that implement WonderlandClientSender are serializable, and 
 * will be valid for as long as a handler is registered for the given
 * client type.  If the handler is unregistered, the sender will be closed,
 * and any further attempts to use it will result in an exception. 
 * 
 * @author jkaplan
 */
@ExperimentalAPI
public interface WonderlandClientSender {
    /**
     * Get the client type this channel sends to
     * @return the client type
     */
    public ClientType getClientType();
    
    /**
     * Get all sessions associated with this sender.  This will return a 
     * set containing all sessions connected with the given type.
     * <p>
     * To send to a particular client of the given type, use the 
     * <code>send(ClientSession, Message)</code> method.
     * @return a set of session ids that are connected via the given
     * client type. 
     */
    public Set<ClientSession> getSessions();
     
    /**
     * Return if there are any sessions associated with this sender.
     * @return true if this channel has any sessions, or false if not
     */
    public boolean hasSessions();
    
    /**
     * Send a message to all session connected via this sender's client type.  
     * The message will be handled on each remote client by the WonderlandClient 
     * attached with the ClientType of this sender.
     * @param message the message to send
     * @throws IllegalStateException if the handler for this client type
     * has been unregistered
     */
    public void send(Message message);
     
    /**
     * Send a message to a single session connected via this sender.  The
     * message will be handled by the WonderlandClient attached with the
     * ClientType of this channel.
     * <p>
     * Note that this method does not check whether the given session has
     * attached the client that this channel sends to.  If the session does not
     * have an attached client for the given type, the remote client will not
     * be able to process the message.
     * 
     * @param session the session to send to
     * @param message the message to send
     * @throws IllegalStateException if the handler for this client type
     * has been unregistered
     */
    public void send(ClientSession session, Message message);
    
    /**
     * Send a message to a set of sessions connected via this sender.  The
     * message will be handled on each remote client by the WonderlandClient 
     * attached with the ClientType of this channel.
     * <p>
     * Note that this method does not check whether the given sessions have
     * attached the client that this channel sends to.  If the sessions do not
     * have an attached client for the given type, the remote clients will not
     * be able to process the message.
     * 
     * @param sessions the sessions to send to
     * @param message the message to send
     * @throws IllegalStateException if the handler for this client type
     * has been unregistered
     */
    public void send(Set<ClientSession> sessions, Message message);
    
    /**
     * Send a message to all sessions joined to the given channel.  The
     * message will be handled on each remote client by the WonderlandClient 
     * attached with the ClientType of this channel. 
     * <p>
     * Note that this method does not check whether the given sessions have
     * attached the client that this channel sends to.  If the sessions do not
     * have an attached client for the given type, the remote clients will not
     * be able to process the message.
     * 
     * @param sessions the sessions to send to
     * @param message the message to send
     * @throws IllegalStateException if the handler for this client type
     * has been unregistered
     */
    public void send(Channel channel, Message message);
}
