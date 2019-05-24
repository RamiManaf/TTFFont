/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

/**
 *This is a spatial that you can attach it to the guiNode.
 * @author Rami Manaf Abdullah
 */
public class TrueTypeText extends Picture {

    public TrueTypeText(String name, AssetManager assetManager, Texture2D texture) {
        super(name, false);
        setTexture(assetManager, texture, true);
        setWidth(texture.getImage().getWidth());
        setHeight(texture.getImage().getHeight());
    }

    public TrueTypeText(AssetManager assetManager, Texture2D texture) {
        this("TrueTypeText", assetManager, texture);
    }

    public float getWidth() {
        return getLocalScale().getX();
    }

    public float getHeight() {
        return getLocalScale().getY();
    }

}
