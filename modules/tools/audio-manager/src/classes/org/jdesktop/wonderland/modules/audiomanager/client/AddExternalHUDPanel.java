/*
 * AddExternalHUDPanel.java
 *
 * Created on July 20, 2009, 2:41 PM
 */

package org.jdesktop.wonderland.modules.audiomanager.client;

import java.util.ArrayList;

import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatInfoRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.client.comms.WonderlandSession;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;

import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent;
import org.jdesktop.wonderland.client.hud.HUDComponentEvent.ComponentEventType;
import org.jdesktop.wonderland.client.hud.HUDComponentListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

/**
 *
 * @author  jp
 */
public class AddExternalHUDPanel extends javax.swing.JPanel implements DisconnectListener {

    private static final Logger logger = Logger.getLogger(AddInWorldHUDPanel.class.getName());

    private AudioManagerClient client;
    private WonderlandSession session;
    private PresenceManager pm;
    private PresenceInfo myPresenceInfo;

    private InCallHUDPanel inCallHUDPanel;
    private HUDComponent inCallHUDComponent;

    private HUDComponent addExternalHUDComponent;

    private PresenceInfo caller;
    private String group;

    private ChatType chatType = ChatType.PRIVATE;

    private PropertyChangeSupport listeners;

    /** Creates new form AddExternalHUDPanel */
    public AddExternalHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo) {

	this(client, session, myPresenceInfo, null);
    }

    public AddExternalHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, InCallHUDPanel inCallPanel) {

        this.client = client;
        this.session = session;
        this.myPresenceInfo = myPresenceInfo;
        this.inCallHUDPanel = inCallPanel;

        initComponents();

	pm = PresenceManagerFactory.getPresenceManager(session);

	inviteButton.setEnabled(false);

	if (inCallPanel != null) {
	    locationSet = true;
            inCallHUDComponent = this.inCallHUDPanel.getHUDComponent();
	} else {
	    this.inCallHUDPanel = new InCallHUDPanel(client, session, myPresenceInfo, myPresenceInfo,
		myPresenceInfo.usernameAlias + "-phone");

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            inCallHUDComponent = mainHUD.createComponent(this.inCallHUDPanel);

            this.inCallHUDPanel.setHUDComponent(inCallHUDComponent);

            mainHUD.addComponent(inCallHUDComponent);
            inCallHUDComponent.addComponentListener(new HUDComponentListener() {
                public void HUDComponentChanged(HUDComponentEvent e) {
                    if (e.getEventType().equals(ComponentEventType.CLOSED)) {
			inCallHUDPanel = null;
			inCallHUDComponent = null;
                    }
                }
            });
	}

	caller = this.inCallHUDPanel.getCaller();
        group = this.inCallHUDPanel.getGroup();
	groupNameTextField.setText(group);

	privacyDescription.setText(VoiceChatMessage.PRIVATE_DESCRIPTION);
    }

    public void setHUDComponent(HUDComponent addExternalHUDComponent) {
	this.addExternalHUDComponent = addExternalHUDComponent;
	inCallHUDPanel.setAddExternalHUDPanel(this, addExternalHUDComponent);
    }

    /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(listener);
        }
    }

    public void disconnected() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        	addExternalHUDComponent.setVisible(false);
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        addExternalUserLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        phoneNumberTextField = new javax.swing.JTextField();
        inviteButton = new javax.swing.JButton();
        privateRadioButton = new javax.swing.JRadioButton();
        secretRadioButton = new javax.swing.JRadioButton();
        speakerPhoneRadioButton = new javax.swing.JRadioButton();
        cancelButton = new javax.swing.JButton();
        groupNameTextField = new javax.swing.JLabel();
        privacyDescription = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();

        addExternalUserLabel.setText("Call External User:");

        jLabel2.setText("Name:");

        jLabel3.setText("Phone #:");

        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });
        nameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                nameTextFieldKeyReleased(evt);
            }
        });

        phoneNumberTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneNumberTextFieldActionPerformed(evt);
            }
        });
        phoneNumberTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                phoneNumberTextFieldKeyReleased(evt);
            }
        });

        inviteButton.setText("Invite");
        inviteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inviteButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(privateRadioButton);
        privateRadioButton.setSelected(true);
        privateRadioButton.setText("Private");
        privateRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                privateRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(secretRadioButton);
        secretRadioButton.setText("Secret");
        secretRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(speakerPhoneRadioButton);
        speakerPhoneRadioButton.setText("SpeakerPhone");
        speakerPhoneRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speakerPhoneRadioButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(layout.createSequentialGroup()
                            .addContainerGap()
                            .add(privacyDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                            .addContainerGap()
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                                        .add(jLabel3))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(phoneNumberTextField)
                                        .add(nameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 28, Short.MAX_VALUE))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(addExternalUserLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 132, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                    .add(groupNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                                .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                                    .add(privateRadioButton)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(secretRadioButton)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(speakerPhoneRadioButton)))))
                    .add(layout.createSequentialGroup()
                        .add(104, 104, 104)
                        .add(cancelButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(inviteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(statusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 249, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addExternalUserLabel)
                    .add(groupNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(11, 11, 11)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(phoneNumberTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(secretRadioButton)
                    .add(speakerPhoneRadioButton)
                    .add(privateRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(privacyDescription, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(inviteButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void privateRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_privateRadioButtonActionPerformed
    chatType = ChatType.PRIVATE;
    privacyDescription.setText(VoiceChatMessage.PRIVATE_DESCRIPTION);
}//GEN-LAST:event_privateRadioButtonActionPerformed

private void nameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nameTextFieldKeyReleased
    inviteButton.setEnabled(nameTextField.getText().length() > 0 & phoneNumberTextField.getText().length() > 0);
}//GEN-LAST:event_nameTextFieldKeyReleased

private void phoneNumberTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneNumberTextFieldKeyReleased
    inviteButton.setEnabled(nameTextField.getText().length() > 0 & phoneNumberTextField.getText().length() > 0);
}//GEN-LAST:event_phoneNumberTextFieldKeyReleased

private void secretRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretRadioButtonActionPerformed
    chatType = ChatType.SECRET;
    privacyDescription.setText(VoiceChatMessage.SECRET_DESCRIPTION);
}//GEN-LAST:event_secretRadioButtonActionPerformed

private void speakerPhoneRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakerPhoneRadioButtonActionPerformed
    chatType = ChatType.PUBLIC;
    privacyDescription.setText(VoiceChatMessage.PUBLIC_DESCRIPTION);
}//GEN-LAST:event_speakerPhoneRadioButtonActionPerformed

private boolean locationSet;

private void inviteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inviteButtonActionPerformed
    PresenceInfo[] info = pm.getAllUsers();

    String name = nameTextField.getText();

    for (int i = 0; i < info.length; i++) {
        if (info[i].usernameAlias.equals(name) ||
		info[i].userID.getUsername().equals(name)) {

            statusLabel.setText("Name is already being used!");
            return;
        }
    }

    statusLabel.setText("");

    inCallHUDPanel.callUser(nameTextField.getText(), phoneNumberTextField.getText());

    inCallHUDComponent.setVisible(true);

    if (locationSet == false) {
	locationSet = true;
   
        inCallHUDComponent.setLocation(addExternalHUDComponent.getX() - addExternalHUDComponent.getWidth(), 
	    addExternalHUDComponent.getY() + addExternalHUDComponent.getHeight() - inCallHUDComponent.getHeight());
    }
}//GEN-LAST:event_inviteButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    addExternalHUDComponent.setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
    if (inviteButton.isEnabled()) {
	inviteButton.doClick();
    }
}//GEN-LAST:event_nameTextFieldActionPerformed

private void phoneNumberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneNumberTextFieldActionPerformed
    if (inviteButton.isEnabled()) {
	inviteButton.doClick();
    }
}//GEN-LAST:event_phoneNumberTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addExternalUserLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel groupNameTextField;
    private javax.swing.JButton inviteButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTextField phoneNumberTextField;
    private javax.swing.JLabel privacyDescription;
    private javax.swing.JRadioButton privateRadioButton;
    private javax.swing.JRadioButton secretRadioButton;
    private javax.swing.JRadioButton speakerPhoneRadioButton;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables

}