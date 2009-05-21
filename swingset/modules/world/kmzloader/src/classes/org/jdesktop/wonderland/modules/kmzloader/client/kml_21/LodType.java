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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.09.19 at 09:37:59 AM PDT 
//


package org.jdesktop.wonderland.modules.kmzloader.client.kml_21;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LodType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://earth.google.com/kml/2.1}ObjectType">
 *       &lt;all>
 *         &lt;element name="minLodPixels" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="maxLodPixels" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="minFadeExtent" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="maxFadeExtent" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LodType", propOrder = {
    "minLodPixels",
    "maxLodPixels",
    "minFadeExtent",
    "maxFadeExtent"
})
public class LodType
    extends ObjectType
{

    @XmlElement(defaultValue = "0")
    protected Float minLodPixels;
    @XmlElement(defaultValue = "-1")
    protected Float maxLodPixels;
    @XmlElement(defaultValue = "0")
    protected Float minFadeExtent;
    @XmlElement(defaultValue = "0")
    protected Float maxFadeExtent;

    /**
     * Gets the value of the minLodPixels property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMinLodPixels() {
        return minLodPixels;
    }

    /**
     * Sets the value of the minLodPixels property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMinLodPixels(Float value) {
        this.minLodPixels = value;
    }

    /**
     * Gets the value of the maxLodPixels property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMaxLodPixels() {
        return maxLodPixels;
    }

    /**
     * Sets the value of the maxLodPixels property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMaxLodPixels(Float value) {
        this.maxLodPixels = value;
    }

    /**
     * Gets the value of the minFadeExtent property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMinFadeExtent() {
        return minFadeExtent;
    }

    /**
     * Sets the value of the minFadeExtent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMinFadeExtent(Float value) {
        this.minFadeExtent = value;
    }

    /**
     * Gets the value of the maxFadeExtent property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMaxFadeExtent() {
        return maxFadeExtent;
    }

    /**
     * Sets the value of the maxFadeExtent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMaxFadeExtent(Float value) {
        this.maxFadeExtent = value;
    }

}