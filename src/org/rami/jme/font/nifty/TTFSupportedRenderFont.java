/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font.nifty;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import de.lessvoid.nifty.spi.render.RenderFont;
import org.rami.jme.font.TrueTypeFont;
import org.rami.jme.font.TrueTypeText;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class TTFSupportedRenderFont implements RenderFont {

    private TrueTypeFont ttf;

    public TTFSupportedRenderFont(String name, AssetManager assetManager) {
        ttf = (TrueTypeFont) assetManager.loadAsset(name);
        ttf.setAntialiased(true);
    }
    
    public TrueTypeText createText(String text, ColorRGBA color){
        ttf.setColor(color);
        return ttf.render(text);
    }
    
    public void setStyle(int style){
        ttf.setStyle(style);
    }
    
    public void setSize(float size){
        ttf.setSize(size);
    }

    @Override
    public int getWidth(String text) {
        return ttf.getWidth(text);
    }

    @Override
    public int getWidth(String text, float size) {
        ttf.setSize(size);
        return ttf.getWidth(text);
    }

    @Override
    public int getHeight() {
        return ttf.getHeight();
    }

    @Override
    public int getCharacterAdvance(char currentCharacter, char nextCharacter, float size) {
        return -1;
    }

    @Override
    public void dispose() {
        ttf = null;
    }

}
