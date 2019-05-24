package org.rami.jme.font;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import de.lessvoid.nifty.Nifty;
import org.rami.jme.font.nifty.TTFSupportedNiftyJmeDisplay;

/**
 *In the xml file you should pass the ttf file path that ENDs with .ttf in the small letter form. Write after the path the font size then the font style (see TrueTypeFont.java for style values). All the parameters are required
 * see Interface/Test.xml
 * @Rami Manaf Abdullah
 */
public class NiftyTest extends SimpleApplication {

    public static void main(String[] args) {
        NiftyTest app = new NiftyTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        assetManager.registerLoader(TrueTypeLoader.class, "ttf");
        TTFSupportedNiftyJmeDisplay display = new TTFSupportedNiftyJmeDisplay(assetManager, inputManager, audioRenderer, viewPort);
        guiViewPort.addProcessor(display);
        Nifty nifty = display.getNifty();
        nifty.loadControlFile("nifty-default-controls.xml");
        nifty.fromXml("Interface/Test.xml", "Test");
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
