/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font.nifty;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.spi.time.impl.AccurateTimeProvider;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class TTFSupportedNiftyJmeDisplay extends NiftyJmeDisplay {

    public TTFSupportedNiftyJmeDisplay(AssetManager assetManager,
            InputManager inputManager,
            AudioRenderer audioRenderer,
            ViewPort vp) {
        super(assetManager, inputManager, audioRenderer, vp);
        renderDev = new TTFSupportedRenderDevice(this);
        nifty = new Nifty(renderDev, soundDev, inputSys, new AccurateTimeProvider());
        inputSys.setNifty(nifty);
        nifty.getResourceLoader().removeAllResourceLocations();
        nifty.getResourceLoader().addResourceLocation(resourceLocation);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

}
