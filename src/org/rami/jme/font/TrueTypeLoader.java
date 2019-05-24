/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;

/**
 *register this class in the AssetManager to enable loading TTF fonts
 * <pre>
 * assetManager.registerLoader(TrueTypeLoader.class, "ttf");
 * </pre>
 * @author Rami Manaf Abdullah
 */
public class TrueTypeLoader implements AssetLoader{

    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        try {
            return new TrueTypeFont(assetInfo.getManager(), Font.createFont(0, assetInfo.openStream()));
        } catch (FontFormatException ex) {
            throw new RuntimeException(ex);
        }
    }
}
