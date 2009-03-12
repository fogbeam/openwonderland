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
package org.jdesktop.wonderland.common;

import java.io.File;
import java.net.URISyntaxException;

/**
 * The ArtURI class uniquely identifies an art resource within a module in the
 * sytem.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class ArtURI extends ModuleURI {
    
    /**
     * Constructor which takes the string represents of the URI.
     * 
     * @param uri The string URI representation
     * @throw URISyntaxException If the URI is not well-formed
     */
    public ArtURI(String uri) throws URISyntaxException {
        super(uri);
    }

    /**
     * Constructor which takes the module name, host name and host port, and
     * asset path. This host name and port is given as: <host name>:<port>
     */
    public ArtURI(String moduleName, String hostNameAndPort, String assetPath) {
        super("wla", moduleName, hostNameAndPort, assetPath);
    }
    
    @Override
    public String getRelativePathInModule() {
        return "art" + File.separator + this.getAssetPath();
    }

    @Override
    public ModuleURI getAnnotatedURI(String hostNameAndPort) throws URISyntaxException {
        return new ArtURI(getModuleName(), hostNameAndPort, getAssetPath());
    }
}