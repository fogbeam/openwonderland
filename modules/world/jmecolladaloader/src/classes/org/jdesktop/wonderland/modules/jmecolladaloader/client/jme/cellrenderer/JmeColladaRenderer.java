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
package org.jdesktop.wonderland.modules.jmecolladaloader.client.jme.cellrenderer;

import com.jme.scene.Spatial;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.cellrenderer.*;
import com.jme.bounding.BoundingBox;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.util.export.SavableString;
import com.jme.util.geom.TangentBinormalGenerator;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.collada.ColladaImporter;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.WorldManager.ConfigLoadListener;
import org.jdesktop.mtgame.shader.DiffuseMap;
import org.jdesktop.mtgame.shader.DiffuseNormalMap;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.modules.jmecolladaloader.client.cell.JmeColladaCell;

/**
 * A cell renderer that uses the JME Collada loader
 * 
 * @author paulby
 */
public class JmeColladaRenderer extends BasicRenderer {

    private Node model;
    private Iterable<Entity> entityGroup = null;

    public JmeColladaRenderer(Cell cell) {
        super(cell);
    }
    
    @Override
    protected Node createSceneGraph(Entity entity) {
        System.err.println("******* CREATE JmeColladaRenderer");

        try {
            // We need to handle null model uri's better!
            Node ret = new Node();
            if (((JmeColladaCell)cell).getModelURI() != null) {
                URL url = getAssetURL(((JmeColladaCell) cell).getModelURI());
                ResourceLocator resourceLocator = new AssetResourceLocator(url);

                ResourceLocatorTool.addResourceLocator(
                        ResourceLocatorTool.TYPE_TEXTURE,
                        resourceLocator);
                // loadColladaAsset has the side effect of setting the model variable
                ret = loadColladaAsset(cell.getCellID().toString(), url);

                ResourceLocatorTool.removeResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, resourceLocator);
            } else if (((JmeColladaCell)cell).getModelGroupURI()!=null) {
                // Bulk of work done in addDefaultComponents
                model = null;
            } else {
                model = new Node();
                ret.attachChild(model);
            }

            if (model!=null) {
                // Adjust model origin wrt to cell
                if (((JmeColladaCell)cell).getGeometryTranslation()!=null)
                    model.setLocalTranslation(((JmeColladaCell)cell).getGeometryTranslation());
                if (((JmeColladaCell)cell).getGeometryRotation()!=null)
                    model.setLocalRotation(((JmeColladaCell)cell).getGeometryRotation());
                if (((JmeColladaCell)cell).getGeometryScale()!=null)
                    model.setLocalScale(((JmeColladaCell)cell).getGeometryScale());
                model.setName("JmeColladaRenderer_Model");
            }

            return ret;
        } catch (MalformedURLException ex) {
            Logger.getLogger(JmeColladaRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    protected void addDefaultComponents(Entity entity, Node rootNode) {
        super.addDefaultComponents(entity, rootNode);
        if (model==null) {
            try {
                URL url = getAssetURL(((JmeColladaCell) cell).getModelGroupURI());
                ClientContextJME.getWorldManager().loadConfiguration(url, new LoadListener(entity));
            } catch (MalformedURLException ex) {
                Logger.getLogger(JmeColladaRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Loads a collada cell from the asset manager given an asset URL
     *
     * @param name the name to put in the returned node
     */
    protected Node loadColladaAsset(String name, URL url) {
        Node node = new Node();
        
        try {
            InputStream input;
            
            if (url.getFile().endsWith(".gz")) {
                input = new GZIPInputStream(url.openStream());
            } else {
                input = url.openStream();
            }


            model = loadModel(input, name);

            node.attachChild(model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading Collada file "+((JmeColladaCell)cell).getModelURI(), e);
        }
        
        // Make sure all the geometry has model bounds
        TreeScan.findNode(node, Geometry.class, new ProcessNodeInterface() {

            public boolean processNode(Spatial node) {
                Geometry g = (Geometry)node;
                if (g.getModelBound()==null) {
                    g.setModelBound(new BoundingBox());
                    g.updateModelBound();
                }

                return true;
            }

        }, false, true);

        return node;
    }

    public static Node loadModel(InputStream in, String name) {
        Node modelNode;
        ColladaImporter.load(in, name);
        modelNode = ColladaImporter.getModel();

        // Adjust the scene transform to match the scale and axis specified in
        // the collada file
        float unitMeter = ColladaImporter.getInstance().getUnitMeter();
        modelNode.setLocalScale(unitMeter);

        String upAxis = ColladaImporter.getInstance().getUpAxis();
        if (upAxis.equals("Z_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {-(float)Math.PI/2, 0f, 0f}));
        } else if (upAxis.equals("X_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {0f, 0f, (float)Math.PI/2}));
        } // Y_UP is the Wonderland default


        ColladaImporter.cleanUp();
        
        return modelNode;
    }


    class LoadListener implements ConfigLoadListener {

        private Entity rootEntity;

        public LoadListener(Entity rootEntity) {
            this.rootEntity = rootEntity;
        }

        public void entityLoaded(Entity entity) {
            BasicRenderer.entityAddChild(rootEntity, entity);
            System.err.println("entity loaded");
        }

    }
}
