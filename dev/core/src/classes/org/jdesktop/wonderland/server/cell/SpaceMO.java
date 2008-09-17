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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.Collection;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.TimeManager;

/**
 * Spaces provide a mechanism to provide a high level spatial search for cells.
 * 
 * The current SpaceMO implementation flattens the cell hierarchy and adds
 * all cells from a graph to the same list. For very large cells with lots of children
 * this could result is more time traversing the list (when a single check on the root
 * would have discounted the entire graph), but this is not expected to be
 * a common case.
 * 
 * If it turns out we are doing a lot of extra comparisons the hierarchy
 * can be added without changing this api.
 * 
 * @author paulby
 */
@InternalAPI
public abstract class SpaceMO implements ManagedObject, Serializable {

    // All the static cells in this space
    private CellListMO staticCellList;
    
    // All the dynamic cells in this space
    private CellListMO dynamicCellList;
    
    protected BoundingVolume worldBounds;
    protected Vector3f position;
    private SpaceID spaceID;
    
    private final static Logger logger = Logger.getLogger(SpaceMO.class.getName());
    
    SpaceMO(BoundingVolume bounds, Vector3f position, SpaceID spaceID) {
        this.position = new Vector3f(position);
        this.worldBounds = bounds.clone(null);
        this.worldBounds.setCenter(position);
        this.spaceID = spaceID;
        dynamicCellList = new CellListMO();
        staticCellList = new CellListMO();
    }
    
    public SpaceID getSpaceID() {
        return spaceID;
    }
    
    /**
     * Add the cell to this space. Called from CellMO.addToSpace
     * 
     * NOTE, a cells parent MUST have been added to the list previously.
     * 
     * @param cell
     */
    void addCell(CellMO cell) {
        logger.fine("Space "+spaceID+"  adding Cell "+cell.getName()+" "+cell.getCellID());
        CellListMO cellList;
        if (!cell.isMovable()) {
            cellList = staticCellList;
        } else {
            cellList = dynamicCellList;
        }
        
        // debug test (TODO deleteme)
        CellID parentID = cell.getParent().getCellID();
        if (!parentID.equals(CellManagerMO.getRootCellID()) && !(cell instanceof RootCellMO) && !cellList.contains(parentID)) {
            throw new RuntimeException("CELL PARENT IS NOT IN SPACE LIST child "+cell.getCellID()+"  parent "+parentID);
        }
        // End debug test
        
        CellDescription cellDesc = cellList.addCell(cell);
        
        // Update the transform time stamp so this cell appears to have changed
        // Forcing it to be picked up by any ViewCache revalidations
        cellDesc.setLocalTransform(cell.getLocalTransform(null), TimeManager.getWonderlandTime());
        
//        System.out.println("Cell "+cell.getName()+" entering space "+position);
    }
    
    /**
     * Remove the cell from this space. Called from CellMO.removeFromSpace
     * 
     * @param cell
     */
    void removeCell(CellMO cell) {
        CellListMO cellList;
        if (cell.isMovable()) {
            cellList = dynamicCellList;
        } else {
            cellList = staticCellList;
        }
        
        cellList.removeCell(cell);        
//        System.out.println("Cell "+cell.getName()+" left space "+position);
    }
    
    /**
     * The cell isMovable state has changed, update the Space to represent 
     * the change. Movable cells are dynamic, in that the cell is check regularly
     * to determine if any changes have occured that need sending to the client.
     * 
     * @param cellMO the cellMO that has changed
     * @param isMovable the new state of the cellMO
     */
    void setCellDynamic(CellMO cellMO, boolean isDynamic) {
        if (isDynamic) {
            // Cell was static, changing to dynamic
            staticCellList.removeCell(cellMO);
            dynamicCellList.addCell(cellMO);
        } else {
            throw new RuntimeException("Not Implemented");
        }
    }
    
    void notifyCellTransformChanged(CellMO cell, long timestamp) {
        if (cell.isMovable()) {
            dynamicCellList.notifyCellTransformChanged(cell, timestamp);
        } else {
            staticCellList.notifyCellTransformChanged(cell, timestamp);
        }
    }
    
    void notifyCellWorldBoundsChanged(CellMO cell, long timestamp) {
        if (cell.isMovable()) {
            dynamicCellList.notifyCellWorldBoundsChanged(cell, timestamp);
        } else {
            staticCellList.notifyCellWorldBoundsChanged(cell, timestamp);
        }
    }
    
    void notifyCellDetached(CellMO cell, long timestamp) {
        if (cell.isMovable()) {
            dynamicCellList.removeCell(cell);
        } else {
            staticCellList.removeCell(cell);
        }
        
    }
    
    /**
     * Return the world bounds of this space
     * 
     * @param result
     * @return
     */
    public BoundingVolume getWorldBounds(BoundingVolume result) {
        return worldBounds.clone(result);
    }
    
    /**
     * Returns the list of dynamic cells within the specified bounds.
     * The list is sorted so that a cell parent always proceeds it in the list.
     * 
     * @param spaces
     * @param bounds
     * @param results
     * @param stats
     * @return
     */
    public CellListMO getDynamicCells(Collection<ManagedReference<SpaceMO>> spaces, 
                                      BoundingVolume bounds, 
                                      CellListMO results, 
                                      CacheStats stats) {        
        return getDynamicCells(spaces, bounds, results, stats, 0L);
    }
    
    public CellListMO getDynamicCells(Collection<ManagedReference<SpaceMO>> spaces, 
                                      BoundingVolume bounds, 
                                      CellListMO results, 
                                      CacheStats stats, 
                                      long changedSince) {
        
        if (results==null)
            results = new CellListMO();
        int cellCount = 0;
        for(ManagedReference<SpaceMO> spaceRef : spaces) {
            cellCount += spaceRef.get().getDynamicCells(results, bounds, stats, changedSince);
        }

//        System.out.println("Checked "+spaces.size()+" spaces and "+cellCount+" cells");
        
        return results;
    }
    
    public CellListMO getStaticCells(Collection<ManagedReference<SpaceMO>> spaces, 
                                     BoundingVolume bounds, 
                                     CellListMO results, 
                                     CacheStats stats) {
        
        if (results==null)
            results = new CellListMO();
        
        int cellCount = 0;
//        System.out.println("Neighbours ");
        for(ManagedReference<SpaceMO> spaceRef : spaces) {
//            System.out.print(spaceRef.get().getSpaceID()+" ");
            cellCount += spaceRef.get().getStaticCells(results, bounds, stats);
        }
//        System.out.println();

//        System.out.println("Checked "+spaces.size()+" spaces and "+cellCount+" cells");
        
        return results;
    }
    
    /**
     * Add all modifiable cells in this space whos bounds intersect with the parameter
     * bounds to the list. Return the number of items added to the list during this call
     * 
     * @param list
     * @param bounds
     */
    private int getDynamicCells(CellListMO list, BoundingVolume bounds, CacheStats stats, long changedSince) {
        if (dynamicCellList.getChangeTimestamp()>changedSince-TimeManager.getTimeDrift()) {
            // List has changed recently, so check contents
//        System.err.println("Checking list "+dynamicCellList.size());
            for(CellDescription cellDesc : dynamicCellList.getCells()) {
    //            System.err.println(cellDesc.getCellID()+"  "+cellDesc.getTransformTimestamp()+">"+(changedSince-TimeManager.getTimeDrift()));
                if (cellDesc.getTransformTimestamp()>changedSince-TimeManager.getTimeDrift() && cellDesc.getWorldBounds().intersects(bounds)) {
                    list.addCell(cellDesc);
                    if (stats!=null) {
                        stats.logCellIntersect(this, cellDesc);
                    }
    //                System.out.println("intersect with "+cellDesc.getCellID());
                }
            }
            return dynamicCellList.size();
        } else {
            // List has not changed, nothing to do....
            return 0;
        }
        
    }
    
    /**
     * Add all stationary cells in this space whos bounds intersect with the parameter
     * bounds to the list. Return the number of items added to the list during this call
     * 
     * @param list
     * @param bounds
     */
    private int getStaticCells(CellListMO list, BoundingVolume bounds, CacheStats stats) {
//        return getCells(list, bounds, stationaryCellListRef.get());
        return getCells(list, bounds, staticCellList, stats);
    }
    
    private int getCells(CellListMO list, BoundingVolume bounds, CellListMO localList, CacheStats stats) {
        for(CellDescription cellDesc : localList.getCells()) {
            // Check if list already contains cellDesc to avoid DS datastore get
//            if (!list.contains(cellDesc) && CellManagerMO.getCell(cellDesc.getCellID()).getWorldBounds().intersects(bounds)) {
            if (!list.contains(cellDesc) && cellDesc.getWorldBounds().intersects(bounds)) {
                list.addCell(cellDesc);
//                System.out.println("intersect with "+cellDesc.getCellID());
                if (stats!=null) {
                    stats.logCellIntersect(this, cellDesc);
                }
            }
        }
        return localList.size();
    }
    
    /**
     * Return the spaces within the bounding volume. This space must be within
     * the volume otherwise null will be returned. Use CellManagerMO.getSpaces(...)
     * when you don't have a space from which to start the search.
     * 
     * @param v
     * @return
     */
    
    abstract Collection<ManagedReference<SpaceMO>> getSpaces(BoundingVolume v);
    
    /**
     * Return all spaces that are adjacent to this space
     * @return
     */
    abstract Collection<ManagedReference<SpaceMO>> getAdjacentSpaces();
    
}
