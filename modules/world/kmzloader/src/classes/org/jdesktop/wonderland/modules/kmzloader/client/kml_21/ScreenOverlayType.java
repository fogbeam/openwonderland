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
 * <p>Java class for ScreenOverlayType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ScreenOverlayType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://earth.google.com/kml/2.1}OverlayType">
 *       &lt;sequence>
 *         &lt;element name="overlayXY" type="{http://earth.google.com/kml/2.1}vec2Type" minOccurs="0"/>
 *         &lt;element name="screenXY" type="{http://earth.google.com/kml/2.1}vec2Type" minOccurs="0"/>
 *         &lt;element name="rotationXY" type="{http://earth.google.com/kml/2.1}vec2Type" minOccurs="0"/>
 *         &lt;element name="size" type="{http://earth.google.com/kml/2.1}vec2Type" minOccurs="0"/>
 *         &lt;element name="rotation" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScreenOverlayType", propOrder = {
    "overlayXY",
    "screenXY",
    "rotationXY",
    "size",
    "rotation"
})
public class ScreenOverlayType
    extends OverlayType
{

    protected Vec2Type overlayXY;
    protected Vec2Type screenXY;
    protected Vec2Type rotationXY;
    protected Vec2Type size;
    @XmlElement(defaultValue = "0")
    protected Float rotation;

    /**
     * Gets the value of the overlayXY property.
     * 
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *     
     */
    public Vec2Type getOverlayXY() {
        return overlayXY;
    }

    /**
     * Sets the value of the overlayXY property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *     
     */
    public void setOverlayXY(Vec2Type value) {
        this.overlayXY = value;
    }

    /**
     * Gets the value of the screenXY property.
     * 
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *     
     */
    public Vec2Type getScreenXY() {
        return screenXY;
    }

    /**
     * Sets the value of the screenXY property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *     
     */
    public void setScreenXY(Vec2Type value) {
        this.screenXY = value;
    }

    /**
     * Gets the value of the rotationXY property.
     * 
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *     
     */
    public Vec2Type getRotationXY() {
        return rotationXY;
    }

    /**
     * Sets the value of the rotationXY property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *     
     */
    public void setRotationXY(Vec2Type value) {
        this.rotationXY = value;
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link Vec2Type }
     *     
     */
    public Vec2Type getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vec2Type }
     *     
     */
    public void setSize(Vec2Type value) {
        this.size = value;
    }

    /**
     * Gets the value of the rotation property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getRotation() {
        return rotation;
    }

    /**
     * Sets the value of the rotation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setRotation(Float value) {
        this.rotation = value;
    }

}
