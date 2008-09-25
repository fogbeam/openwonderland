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
package org.jdesktop.wonderland.client.protocols.wlj;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.JarURI;

/**
 * The WljURLConnection class is the URL connection to URLs that have the 'wlj'
 * protocol. This is used for Wonderland plugin JARs and interfaces with the asset
 * manager to properly load resources.
 * <p>
 * The format of the URL is:
 * <p>
 * wlj://<module name>/<jar path>
 * <p>
 * where <module name> is the name of the module that contains the asset, and
 * <jar path> is the relative path of the jar within the module and has the
 * form: <plugin name>/<jar type>/<jar name>, where <plugin name> is the unique
 * name of the plugin, <jar type> is either "client", "server", or "common",
 * and <jar name> is the name of the jar file (with the .jar extension)
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WljURLConnection extends URLConnection {

    /**
     * Constructor, takes the 'wlj' protocol URL as an argument
     * 
     * @param url A URL with a 'wlj' protocol
     */
    public WljURLConnection(URL url) {
        super(url);
    }
    
    @Override
    public void connect() throws IOException {
        System.out.println("Connect to "+url);
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            /* Forms an ResourceURI given the URL and fetches from the asset manager */
            JarURI uri = new JarURI(this.url.toExternalForm());
            System.out.println("OPENING URI " + uri.toString());
            Asset asset = AssetManager.getAssetManager().getAsset(uri, AssetType.FILE);
            if (asset == null || AssetManager.getAssetManager().waitForAsset(asset) == false) {
                return null;
            }
            return new FileInputStream(asset.getLocalCacheFile());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WljURLConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException excp) {
            Logger.getLogger(WljURLConnection.class.getName()).log(Level.SEVERE, null, excp);
        }
        return null;
        
    }
}
