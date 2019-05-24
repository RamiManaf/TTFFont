package org.rami.jme.font;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import de.lessvoid.nifty.Nifty;
import org.rami.jme.font.nifty.TTFSupportedNiftyJmeDisplay;

/**
 * This is the TrueTypeFontTest Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author normenhansen
 */
public class TrueTypeFontTest extends SimpleApplication {

    public static void main(String[] args) {
        TrueTypeFontTest app = new TrueTypeFontTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        TrueTypeFont font = (TrueTypeFont) assetManager.loadAsset("Interface/arial.ttf");
        font.setColor(ColorRGBA.Blue);
        font.setSize(40);
        TrueTypeText text = font.render("hi");
        text.setLocalTranslation(settings.getWidth() / 2 - text.getWidth() / 2, settings.getHeight() / 2 - text.getHeight() / 2, 0);
        guiNode.attachChild(text);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

}
