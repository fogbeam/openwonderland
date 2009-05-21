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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.bounding.BoundingSphere;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveSource;
import org.jdesktop.wonderland.client.jme.cellrenderer.*;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import imi.character.CharacterAttributes;
import imi.character.CharacterMotionListener;
import imi.character.avatar.MaleAvatarAttributes;
import imi.character.statemachine.GameContextListener;
import imi.character.statemachine.GameState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.scene.PMatrix;
import imi.scene.PTransform;
import imi.scene.processors.CharacterAnimationProcessor;
import imi.scene.processors.CharacterProcessor;
import imi.scene.processors.JSceneEventProcessor;
import imi.utils.input.AvatarControlScheme;
import java.net.URL;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.ProcessorCollectionComponent;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.MovableAvatarComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveListener;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.AvatarCell.AvatarActionTrigger;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent.AvatarConfigChangeListener;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.AvatarConfigMessage;

/**
 * Renderer for Avatars, using the new avatar system
 * 
 * @author paulby
 */
@ExperimentalAPI
public class AvatarImiJME extends BasicRenderer implements AvatarActionTrigger {

    private WlAvatarCharacter pendingAvatar = null;
    private WlAvatarCharacter avatarCharacter = null;
    private boolean selectedForInput = false;

//    private AvatarRendererChangeRequestEvent.AvatarQuality quality = AvatarRendererChangeRequestEvent.AvatarQuality.High;
    private CharacterMotionListener characterMotionListener;
    private GameContextListener gameContextListener;
    private int currentTrigger = -1;
    private boolean currentPressed = false;
    private float positionMinDistanceForPull = 0.1f;
    private float positionMaxDistanceForPull = 3.0f;
    private String username;
    private AvatarControlScheme controlScheme = null;
    private NameTagNode nameTag;

    private ProcessorComponent cameraChainedProcessor = null;  // The processor to which the camera is chained

    private CellMoveListener cellMoveListener = null;

    private CellStatus status = CellStatus.DISK;

    private Entity rootEntity = null;

    public AvatarImiJME(Cell cell) {
        super(cell);
        assert (cell != null);
        final Cell c = cell;

        // Listen for avatar configuration changes.
        cell.getComponent(AvatarConfigComponent.class).addAvatarConfigChageListener(new AvatarConfigChangeListener() {
            public void AvatarConfigChanged(AvatarConfigMessage msg) {
                URL configURL=null;
                try {
                    configURL = new URL(msg.getModelConfigURL());
                    logger.info("Config " + configURL + "  user="+username+"  "+selectedForInput);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(AvatarImiJME.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }

                changeAvatar(loadAvatar(configURL));
            }
        });

        if (cell instanceof AvatarCell)
            username = ((AvatarCell) cell).getIdentity().getUsername();
        else
            username = "npc"; // HACK !

        characterMotionListener = new CharacterMotionListener() {
            public void transformUpdate(Vector3f translation, PMatrix rotation) {
                ((MovableAvatarComponent) c.getComponent(MovableComponent.class)).localMoveRequest(new CellTransform(rotation.getRotation(), translation));
                };
            
        };

        // This info will be sent to the other clients to animate the avatar
        gameContextListener = new GameContextListener() {

            public void trigger(boolean pressed, int trigger, Vector3f translation, Quaternion rotation) {
                synchronized (this) {
                    currentTrigger = trigger;
                    currentPressed = pressed;
                }
                GameState state = avatarCharacter.getContext().getCurrentState();
                String animationName=null;
                if (state instanceof CycleActionState) {
                    animationName = avatarCharacter.getContext().getState(CycleActionState.class).getAnimationName();
                }
                ((MovableAvatarComponent) c.getComponent(MovableComponent.class)).localMoveRequest(new CellTransform(rotation, translation), trigger, pressed, animationName, null);
            }
        };

        // TODO - not fully implemented/tested
        ClientContext.getInputManager().addGlobalEventListener(new EventClassListener() {

            private Class[] consumeClasses = new Class[]{
                AvatarRendererChangeRequestEvent.class,
                AvatarNameEvent.class
            };

            @Override
            public Class[] eventClassesToConsume() {
                return consumeClasses;
            }

            @Override
            public void commitEvent(Event event) {
                if (event instanceof AvatarNameEvent) {
                    AvatarNameEvent e = (AvatarNameEvent) event;

                    if (e.getUsername().equals(username)) {
                        if (nameTag == null) {
                            logger.warning("[AvatarImiJME] warning: setting " +
                                "avatar name when name tag is null. " + e);
                            return;
                        }

                        nameTag.setNameTag(e.getEventType(), username, 
                                           e.getUsernameAlias(),
                                           e.getForegroundColor(), e.getFont());
                    }
                } else if (event instanceof AvatarRendererChangeRequestEvent) {
                    handleAvatarRendererChangeRequest((AvatarRendererChangeRequestEvent)event);
                }
            }

            @Override
            public void computeEvent(Event evtIn) {
            }
        });

    }

    /**
     * @param status
     */
    @Override
    public void setStatus(CellStatus status) {
        super.setStatus(status);
        this.status = status;
        switch(status) {
            case DISK :
                break;
            case INACTIVE :
                break;
            case ACTIVE :
                if (cellMoveListener!=null) {
                    cell.getComponent(MovableComponent.class).removeServerCellMoveListener(cellMoveListener);
                    cellMoveListener = null;
                }
                if (avatarCharacter==null) {
                    AvatarConfigComponent configComp = cell.getComponent(AvatarConfigComponent.class);
                    URL configURL = null;
                    if (configComp!=null)
                        configURL = configComp.getAvatarConfigURL();
                    pendingAvatar = (WlAvatarCharacter) loadAvatar(configURL);
                } else {
                    ClientContextJME.getWorldManager().removeEntity(avatarCharacter);
                    pendingAvatar = null;
                }
                
                changeAvatar(pendingAvatar);
                if (cellMoveListener==null) {
                    cellMoveListener = new CellMoveListener() {
                        public void cellMoved(CellTransform transform, CellMoveSource source) {
                            if (source==CellMoveSource.REMOTE) {
    //                            System.err.println("REMOTE MOVE "+transform.getTranslation(null));
                                if (avatarCharacter!=null) {
                                    if (avatarCharacter.getModelInst()==null) {  // Extra debug check
                                        logger.severe("MODEL INST IS NULL !");
                                        Thread.dumpStack();
                                        return;
                                    }
                                    avatarCharacter.getModelInst().setTransform(new PTransform(transform.getRotation(null), transform.getTranslation(null), new Vector3f(1,1,1)));
                                }
                            }
                        }
                    };
                }
                cell.getComponent(MovableComponent.class).addServerCellMoveListener(cellMoveListener);
                break;
        }
    }

    @Override
    protected Entity createEntity() {
        assert(rootEntity==null);
        rootEntity = new Entity("AvatarRoot");
        return rootEntity;
    }

    private void handleAvatarRendererChangeRequest(AvatarRendererChangeRequestEvent event) {
        switch (event.getQuality()) {
            case High :
                URL avatarConfigURL = null;
                avatarConfigURL = cell.getComponent(AvatarConfigComponent.class).getAvatarConfigURL();
                changeAvatar(loadAvatar(avatarConfigURL));
                break;
            case Medium :
                changeAvatar(loadAvatar(null));
                break;
            case Low :
                changeAvatar(loadAvatar(null));
                break;
        }
    }

    /**
     * Change the current avatar to the newAvatar
     * 
     * @param newAvatar
     */
    void changeAvatar(WlAvatarCharacter newAvatar) {
        synchronized(this) {
            WorldManager wm = ClientContextJME.getWorldManager();

            LoadingInfo.startedLoading(cell.getCellID(), newAvatar.getName());

            PMatrix currentLocation = null;

            if (avatarCharacter != null) {
                currentLocation = avatarCharacter.getModelInst().getTransform().getWorldMatrix(true);
                rootEntity.removeEntity(avatarCharacter);
                if (nameTag!=null) { // THis must be done after the entity is no longer live
                    avatarCharacter.getJScene().getExternalKidsRoot().detachChild(nameTag);
                }

                enableInputListeners(false);
                avatarCharacter.destroy();
            }

            avatarCharacter = newAvatar;

            if (newAvatar==null)
                return;

            RenderComponent rc = (RenderComponent) avatarCharacter.getComponent(RenderComponent.class);

            if (rc != null) {
                addDefaultComponents(avatarCharacter, rc.getSceneRoot());
            } else {
                logger.warning("NO RenderComponent for Avatar");
            }

            if (currentLocation != null && avatarCharacter.getModelInst()!=null) {
                avatarCharacter.getModelInst().setTransform(new PTransform(currentLocation));
            }

            if (nameTag!=null) {
                avatarCharacter.getJScene().getExternalKidsRoot().attachChild(nameTag);
            }

            rootEntity.addEntity(avatarCharacter);

            selectForInput(selectedForInput);

            LoadingInfo.finishedLoading(cell.getCellID(), newAvatar.getName());
        }
    }

    @Override
    protected void addRenderState(Node node) {
        // Nothing to do
    }

    @Override
    public void cellTransformUpdate(CellTransform transform) {
        // Don't call super, we don't use a MoveProcessor for avatars

        if (!selectedForInput && avatarCharacter != null && avatarCharacter.getController().getModelInstance()!=null ) {
            // If the user is being steered by AI, do not mess it up
            // (objects that the AI is dealing with gota be synced)
//            System.err.println("Steering "+avatarCharacter.getContext().getSteering().isEnabled()+"  "+avatarCharacter.getContext().getSteering().getCurrentTask());
            if (avatarCharacter.getContext().getSteering().isEnabled() && avatarCharacter.getContext().getSteering().getCurrentTask() != null) {
                System.err.println("Avatar steering !");
            } else {
                Vector3f pos = transform.getTranslation(null);
                Vector3f dir = new Vector3f(0, 0, -1);
                transform.getRotation(null).multLocal(dir);
//                System.err.println("Setting pos "+pos);
                PMatrix local = avatarCharacter.getController().getModelInstance().getTransform().getLocalMatrix(true);
                final Vector3f currentPosition = local.getTranslation();
                float currentDistance = currentPosition.distance(pos);
                if (currentDistance < positionMaxDistanceForPull) {
                    pos.set(currentPosition);
                }

            }
        }
    }

    /**
     * Load and return the avatar. To make this the current avatar changeAvatar()
     * must be called
     * 
     * @param avatarConfigURL
     * @return
     */
    protected WlAvatarCharacter loadAvatar(URL avatarConfigURL) {
        WlAvatarCharacter ret=null;
            WorldManager wm = ClientContextJME.getWorldManager();
            PMatrix origin = new PMatrix();
            CellTransform transform = cell.getLocalTransform();
            origin.setTranslation(transform.getTranslation(null));
            origin.setRotation(transform.getRotation(null));

            // Set the base URL
            WonderlandSession session = cell.getCellCache().getSession();
            ServerSessionManager manager = session.getSessionManager();
            String serverHostAndPort = manager.getServerNameAndPort();
            String baseURL = "wla://avatarbaseart@" + serverHostAndPort + "/";

            logger.info("[AvatarImiJme] AVATAR CONFIG URL "+avatarConfigURL);

            LoadingInfo.startedLoading(cell.getCellID(), username);
            try {
                // Create the character, but don't add the entity to wm
                String avatarDetail = System.getProperty("avatar.detail", "high");
                if (avatarConfigURL == null || avatarDetail.equalsIgnoreCase("low")) {
                    CharacterAttributes attributes = new MaleAvatarAttributes(username, false);

                    // Setup simple model
                    attributes.setUseSimpleStaticModel(true, null);
                    attributes.setBaseURL(baseURL);
                    ret = new WlAvatarCharacter(attributes, wm);
    //                Spatial placeHolder = (Spatial) BinaryImporter.getInstance().load(new URL(baseURL+"assets/models/collada/Avatars/placeholder.bin"));

                    URL url = new URL(baseURL+"assets/models/collada/Avatars/StoryTeller.kmz/models/StoryTeller.wbm");
                    ResourceLocator resourceLocator = new RelativeResourceLocator(url);

                    ResourceLocatorTool.addThreadResourceLocator(
                            ResourceLocatorTool.TYPE_TEXTURE,
                            resourceLocator);
                    Spatial placeHolder = (Spatial) BinaryImporter.getInstance().load(url);
                    ResourceLocatorTool.removeThreadResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, resourceLocator);

                    ret.getJScene().getExternalKidsRoot().attachChild(placeHolder);
                } else {
                    ret = new WlAvatarCharacter(avatarConfigURL, wm, "wla://avatarbaseart@" + serverHostAndPort + "/");
                }

                ret.getModelInst().getTransform().getLocalMatrix(true).set(origin);

                // TODO - remove hardcoded npc support
                if (username.equals("npc") && avatarConfigURL!=null) {
                    String u = avatarConfigURL.getFile();
                    username=u.substring(u.lastIndexOf('/')+1, u.lastIndexOf('.'));
                }


                Node external = ret.getJScene().getExternalKidsRoot();
                ZBufferState zbuf = (ZBufferState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_ZBUFFER);
                zbuf.setEnabled(true);
                zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
                external.setRenderState(zbuf);

                NameTagComponent nameTagComp = cell.getComponent(NameTagComponent.class);
                if (nameTagComp==null) {
                    nameTagComp = new NameTagComponent(cell, username, 2);
                    cell.addComponent(nameTagComp);
                }
                nameTag = nameTagComp.getNameTagNode();
                external.attachChild(nameTag);
                external.setModelBound(new BoundingSphere());
                external.updateModelBound();

        //        JScene jscene = avatar.getJScene();
        //        jscene.renderToggle();      // both renderers
        //        jscene.renderToggle();      // jme renderer only
        //        jscene.setRenderPRendererMesh(true);  // Force pRenderer to be instantiated
        //        jscene.toggleRenderPRendererMesh();   // turn off mesh
        //        jscene.toggleRenderBoundingVolume();  // turn off bounds

            } catch(Exception e) {
                String badURL=null;
                if (avatarConfigURL!=null)
                    badURL = avatarConfigURL.toExternalForm();
                Logger.getLogger(AvatarImiJME.class.getName()).log(Level.SEVERE, "Error loading avatar "+badURL, e);
            } finally {
                LoadingInfo.finishedLoading(cell.getCellID(), username);
            }
            return ret;
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        // Nothing to do here
        return null;
    }

    /**
     * Returns the WlAvatarCharacter object for this renderer. The WlAvatarCharacter
     * provides the control points in the avatar system.
     * @return
     */
    public WlAvatarCharacter getAvatarCharacter() {
        return avatarCharacter;
    }

    public void selectForInput(boolean selected) {
        selectedForInput = selected;
        enableInputListeners(selected);
    }

    private void enableInputListeners(boolean enabled) {
        if (avatarCharacter!=null) {
             WorldManager wm = ClientContextJME.getWorldManager();

            ((WlAvatarContext) avatarCharacter.getContext()).getSteering().setEnable(false);

            if (controlScheme == null && enabled) {
                controlScheme = (AvatarControlScheme) ((JSceneEventProcessor) wm.getUserData(JSceneEventProcessor.class)).setDefault(new AvatarControlScheme(avatarCharacter));
            }
            if (enabled) {
                // Listen for avatar movement and update the cell
                avatarCharacter.getController().addCharacterMotionListener(characterMotionListener);

                // Listen for game context changes
                avatarCharacter.getContext().addGameContextListener(gameContextListener);
                avatarCharacter.selectForInput();
                controlScheme.getAvatarTeam().add(avatarCharacter);
                controlScheme.setAvatar(avatarCharacter);

                // Chain the camera processor to the avatar motion processor for
                // smooth animation. For animated avatars we use CharacterAnimationProcessor for the simple
                // avatar CharacterProcessor
                ProcessorCollectionComponent pcc = avatarCharacter.getComponent(ProcessorCollectionComponent.class);
                ProcessorComponent characterProcessor = null;
                ProcessorComponent characterAnimationProcessor = null;
                for(ProcessorComponent pc : pcc.getProcessors()) {
                    if (pc instanceof CharacterProcessor)
                        characterProcessor = pc;
                    else if (pc instanceof CharacterAnimationProcessor) {
                        characterAnimationProcessor = pc;
                        break;
                    }
                }

                cameraChainedProcessor=null;
                if (characterAnimationProcessor!=null) {
                    cameraChainedProcessor = characterAnimationProcessor;
                } else if (characterProcessor!=null)
                    cameraChainedProcessor = characterProcessor;

                if (cameraChainedProcessor!=null) {
                    cameraChainedProcessor.addToChain(ViewManager.getViewManager().getCameraProcessor());
                    cameraChainedProcessor.setRunInRenderer(true);
                }

            } else {
                avatarCharacter.getController().removeCharacterMotionListener(characterMotionListener);
                avatarCharacter.getContext().removeGameContextListener(gameContextListener);
                if (controlScheme!=null) {
                    controlScheme.getAvatarTeam().remove(avatarCharacter);
                    controlScheme.setAvatar(null);
                }

                if (cameraChainedProcessor!=null) {
                    cameraChainedProcessor.removeFromChain(ViewManager.getViewManager().getCameraProcessor());
                    cameraChainedProcessor = null;
                }
            }
        }
    }

    public void trigger(int trigger, boolean pressed, String animationName) {
        if (!selectedForInput && avatarCharacter != null) {
            // Sync to avoid concurrent updates of currentTrigger and currentPressed
            synchronized (this) {
                if (currentTrigger == trigger && currentPressed == pressed) {
                    return;
                }

                try {
                    if (pressed) {
                        if (animationName != null) {
                            ((WlAvatarContext) avatarCharacter.getContext()).setMiscAnimation(animationName);
                        }

                        avatarCharacter.getContext().triggerPressed(trigger);
                    } else {
                        avatarCharacter.getContext().triggerReleased(trigger);
                    }

                    currentTrigger = trigger;
                    currentPressed = pressed;
                } catch(Exception e) {
                    // We can get this if a user is viewing a female avatar but
                    // has not yet set female as the default. 
                }
            }
        }
    }
    /**
     * Hack for the binary loader, this will need to be made general purpose once
     * we implement a core binary loader
     */
    class RelativeResourceLocator implements ResourceLocator {

        private String modulename;
        private String path;
        private String protocol;

        /**
         * Locate resources for the given file
         * @param url
         */
        public RelativeResourceLocator(URL url) {
            // The modulename can either be in the "user info" field or the
            // "host" field. If "user info" is null, then use the host name.
//            System.out.println("ASSET RESOURCE LOCATOR FOR URL " + url.toExternalForm());

            if (url.getUserInfo() == null) {
                modulename = url.getHost();
            }
            else {
                modulename = url.getUserInfo();
            }
            path = url.getPath();
            path = path.substring(0, path.lastIndexOf('/')+1);
            protocol = url.getProtocol();

//            System.out.println("MODULE NAME " + modulename + " PATH " + path);
        }

        public URL locateResource(String resource) {
//            System.err.println("Looking for resource "+resource);
//            System.err.println("Module "+modulename+"  path "+path);
            try {

                    String urlStr = trimUrlStr(protocol + "://"+modulename+path+".." + resource);

                    URL url = getAssetURL(urlStr);
//                    System.err.println("Using " + url.toExternalForm());
                    return url;

            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Unable to locateResource "+resource, ex);
                return null;
            }
        }

        /**
         * Trim ../ from url
         * @param urlStr
         */
        private String trimUrlStr(String urlStr) {
            int pos = urlStr.indexOf("/../");
            if (pos==-1)
                return urlStr;

            StringBuilder buf = new StringBuilder(urlStr);
            int start = pos;
            while(buf.charAt(--start)!='/') {}
            buf.replace(start, pos+4, "/");
//            System.out.println("Trimmed "+buf.toString());

           return buf.toString();
        }

    }

    class DebugNode extends Node {
        public void draw(Renderer r) {
            System.err.println("START**********************************");
            super.draw(r);
            System.err.println("END ***********************************");
        }
    }
}