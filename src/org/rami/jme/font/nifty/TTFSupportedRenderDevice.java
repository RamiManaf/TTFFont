/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font.nifty;

import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.niftygui.RenderDeviceJme;
import com.jme3.niftygui.RenderFontJme;
import com.jme3.niftygui.RenderImageJme;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.render.BlendMode;
import de.lessvoid.nifty.spi.render.MouseCursor;
import de.lessvoid.nifty.spi.render.RenderFont;
import de.lessvoid.nifty.spi.render.RenderImage;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.resourceloader.NiftyResourceLoader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.rami.jme.font.TrueTypeText;

/**
 *
 * @author Rami Manaf Abdullah
 */
public class TTFSupportedRenderDevice extends RenderDeviceJme {

    private TTFSupportedNiftyJmeDisplay display;
    private RenderManager rm;
    private Renderer r;
    private HashMap<CachedTextKey, Object> textCacheLastFrame = new HashMap<>();
    private HashMap<CachedTextKey, Object> textCacheCurrentFrame = new HashMap<>();
    private final Quad quad = new Quad(1, -1, true);
    private final Geometry quadGeom = new Geometry("nifty-quad", quad);
    private boolean clipWasSet = false;
    private VertexBuffer quadDefaultTC = quad.getBuffer(VertexBuffer.Type.TexCoord);
    private VertexBuffer quadModTC = quadDefaultTC.clone();
    private VertexBuffer quadColor;
    private Matrix4f tempMat = new Matrix4f();
    private ColorRGBA tempColor = new ColorRGBA();
    private RenderState renderState = new RenderState();

    private Material colorMaterial;
    private Material textureColorMaterial;
    private Material vertexColorMaterial;

    public TTFSupportedRenderDevice(NiftyJmeDisplay d) {
        super(d);
        this.display = (TTFSupportedNiftyJmeDisplay) d;
        quadColor = new VertexBuffer(VertexBuffer.Type.Color);
        quadColor.setNormalized(true);
        ByteBuffer bb = BufferUtils.createByteBuffer(4 * 4);
        quadColor.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.UnsignedByte, bb);
        quad.setBuffer(quadColor);

        quadModTC.setUsage(VertexBuffer.Usage.Stream);

        // Load the 3 material types separately to avoid
        // reloading the shader when the defines change.
        // Material with a single color (no texture or vertex color)
        colorMaterial = new Material(display.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

        // Material with a texture and a color (no vertex color)
        textureColorMaterial = new Material(display.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

        // Material with vertex color, used for gradients (no texture)
        vertexColorMaterial = new Material(display.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        vertexColorMaterial.setBoolean("VertexColor", true);

        // Shared render state for all materials
        renderState.setDepthTest(false);
        renderState.setDepthWrite(false);
    }

    @Override
    public RenderFont createFont(String filename) {
        Pattern pattern = Pattern.compile("(.*\\.ttf)([0-9]+)([0123]{1})");
        Matcher m = pattern.matcher(filename);
        if (m.matches()) {
            TTFSupportedRenderFont font = new TTFSupportedRenderFont(m.group(1), display.getAssetManager());;
            font.setSize(Integer.parseInt(m.group(2)));
            font.setStyle(Integer.parseInt(m.group(3)));
            return font;
        } else {
            return new RenderFontJme(filename, display);
        }
    }

    @Override
    public void renderFont(RenderFont font, String str, int x, int y, Color color, float sizeX, float sizeY) {
        if (str.length() == 0) {
            return;
        }
        if (font instanceof TTFSupportedRenderFont) {
            CachedTextKey key = new CachedTextKey(font, str/*, colorRgba*/);
            TrueTypeText text = (TrueTypeText) textCacheLastFrame.get(key);
            if (text == null) {
                text = ((TTFSupportedRenderFont) font).createText(str, convertColor(color, tempColor));
            }
            textCacheCurrentFrame.put(key, text);
            float x0 = x; //+ 0.5f * width * (1f - sizeX);
            float y0 = y; // + 0.5f * height * (1f - sizeY);

            tempMat.loadIdentity();
            tempMat.setTranslation(x0, getHeight() - y0 - text.getHeight(), 0);
            tempMat.setScale(text.getWidth() * sizeX, text.getHeight() * sizeY, 0);

            rm.setWorldMatrix(tempMat);
            rm.setForcedRenderState(renderState);
            text.getMaterial().render(text, rm);
        } else {
            RenderFontJme jmeFont = (RenderFontJme) font;
            ColorRGBA colorRgba = convertColor(color, tempColor);
            CachedTextKey key = new CachedTextKey(jmeFont.getFont(), str/*, colorRgba*/);
            BitmapText text = (BitmapText) textCacheLastFrame.get(key);
            if (text == null) {
                text = jmeFont.createText();
                text.setText(str);
                text.updateLogicalState(0);
            }
            textCacheCurrentFrame.put(key, text);

//        float width = text.getLineWidth();
//        float height = text.getLineHeight();
            float x0 = x; //+ 0.5f * width * (1f - sizeX);
            float y0 = y; // + 0.5f * height * (1f - sizeY);

            tempMat.loadIdentity();
            tempMat.setTranslation(x0, getHeight() - y0, 0);
            tempMat.setScale(sizeX, sizeY, 0);

            rm.setWorldMatrix(tempMat);
            rm.setForcedRenderState(renderState);
            text.setColor(colorRgba);
            text.updateLogicalState(0);
            text.render(rm, colorRgba);
        }
    }

    private static class CachedTextKey {

        Object font;
        String text;
//        ColorRGBA color;

        public CachedTextKey(Object font, String text/*, ColorRGBA color*/) {
            this.font = font;
            this.text = text;
//            this.color = color;
        }

        @Override
        public boolean equals(Object other) {
            CachedTextKey otherKey = (CachedTextKey) other;
            return font.equals(otherKey.font)
                    && text.equals(otherKey.text)/* &&
                   color.equals(otherKey.color)*/;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + font.hashCode();
            hash = 53 * hash + text.hashCode();
//            hash = 53 * hash + color.hashCode();
            return hash;
        }
    }

    @Override
    public void setResourceLoader(NiftyResourceLoader niftyResourceLoader) {
    }

    @Override
    public void setRenderManager(RenderManager rm) {
        this.rm = rm;
        this.r = rm.getRenderer();
    }

    // TODO: Cursor support
    @Override
    public MouseCursor createMouseCursor(String str, int x, int y) {
        return new MouseCursor() {
            public void dispose() {
            }

            @Override
            public void enable() {
            }

            @Override
            public void disable() {
            }
        };
    }

    @Override
    public void enableMouseCursor(MouseCursor cursor) {
    }

    @Override
    public void disableMouseCursor() {
    }

    @Override
    public RenderImage createImage(String filename, boolean linear) {
        //System.out.println("createImage(" + filename + ", " + linear + ")");
        return new RenderImageJme(filename, linear, display);
    }

    @Override
    public void beginFrame() {
    }

    @Override
    public void endFrame() {
        HashMap<CachedTextKey, Object> temp = textCacheLastFrame;
        textCacheLastFrame = textCacheCurrentFrame;
        textCacheCurrentFrame = temp;
        textCacheCurrentFrame.clear();
        rm.setForcedRenderState(null);
    }

    @Override
    public int getWidth() {
        return display.getWidth();
    }

    @Override
    public int getHeight() {
        return display.getHeight();
    }

    @Override
    public void clear() {
    }

    @Override
    public void setBlendMode(BlendMode blendMode) {
        renderState.setBlendMode(convertBlend(blendMode));
    }

    private RenderState.BlendMode convertBlend(BlendMode blendMode) {
        if (blendMode == null) {
            return RenderState.BlendMode.Off;
        } else if (blendMode == BlendMode.BLEND) {
            return RenderState.BlendMode.Alpha;
        } else if (blendMode == BlendMode.MULIPLY) {
            return RenderState.BlendMode.Modulate;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private int convertColor(Color color) {
        int color2 = 0;
        color2 |= ((int) (255.0 * color.getAlpha())) << 24;
        color2 |= ((int) (255.0 * color.getBlue())) << 16;
        color2 |= ((int) (255.0 * color.getGreen())) << 8;
        color2 |= ((int) (255.0 * color.getRed()));
        return color2;
    }

    private ColorRGBA convertColor(Color inColor, ColorRGBA outColor) {
        return outColor.set(inColor.getRed(), inColor.getGreen(), inColor.getBlue(), inColor.getAlpha());
    }

    @Override
    public void renderImage(RenderImage image, int x, int y, int w, int h,
            int srcX, int srcY, int srcW, int srcH,
            Color color, float scale,
            int centerX, int centerY) {

        RenderImageJme jmeImage = (RenderImageJme) image;
        Texture2D texture = jmeImage.getTexture();

        textureColorMaterial.setColor("Color", convertColor(color, tempColor));
        textureColorMaterial.setTexture("ColorMap", texture);

        float imageWidth = jmeImage.getWidth();
        float imageHeight = jmeImage.getHeight();
        FloatBuffer texCoords = (FloatBuffer) quadModTC.getData();

        float startX = srcX / imageWidth;
        float startY = srcY / imageHeight;
        float endX = startX + (srcW / imageWidth);
        float endY = startY + (srcH / imageHeight);

        startY = 1f - startY;
        endY = 1f - endY;

        texCoords.rewind();
        texCoords.put(startX).put(startY);
        texCoords.put(endX).put(startY);
        texCoords.put(endX).put(endY);
        texCoords.put(startX).put(endY);
        texCoords.flip();
        quadModTC.updateData(texCoords);

        quad.clearBuffer(VertexBuffer.Type.TexCoord);
        quad.setBuffer(quadModTC);

        float x0 = centerX + (x - centerX) * scale;
        float y0 = centerY + (y - centerY) * scale;

        tempMat.loadIdentity();
        tempMat.setTranslation(x0, getHeight() - y0, 0);
        tempMat.setScale(w * scale, h * scale, 0);

        rm.setWorldMatrix(tempMat);
        rm.setForcedRenderState(renderState);
        textureColorMaterial.render(quadGeom, rm);

        //System.out.format("renderImage2(%s, %d, %d, %d, %d, %d, %d, %d, %d, %s, %f, %d, %d)\n", texture.getKey().toString(),
        //                                                                                       x, y, w, h, srcX, srcY, srcW, srcH,
        //                                                                                       color.toString(), scale, centerX, centerY);
    }

    @Override
    public void renderImage(RenderImage image, int x, int y, int width, int height,
            Color color, float imageScale) {

        RenderImageJme jmeImage = (RenderImageJme) image;

        textureColorMaterial.setColor("Color", convertColor(color, tempColor));
        textureColorMaterial.setTexture("ColorMap", jmeImage.getTexture());

        quad.clearBuffer(VertexBuffer.Type.TexCoord);
        quad.setBuffer(quadDefaultTC);

        float x0 = x + 0.5f * width * (1f - imageScale);
        float y0 = y + 0.5f * height * (1f - imageScale);

        tempMat.loadIdentity();
        tempMat.setTranslation(x0, getHeight() - y0, 0);
        tempMat.setScale(width * imageScale, height * imageScale, 0);

        rm.setWorldMatrix(tempMat);
        rm.setForcedRenderState(renderState);
        textureColorMaterial.render(quadGeom, rm);

        //System.out.format("renderImage1(%s, %d, %d, %d, %d, %s, %f)\n", jmeImage.getTexture().getKey().toString(), x, y, width, height, color.toString(), imageScale);
    }

    @Override
    public void renderQuad(int x, int y, int width, int height, Color color) {
        //We test for alpha >0 as an optimization to prevent the render of completely transparent quads.
        //Nifty use layers that are often used for logical positionning and not rendering.
        //each layer is rendered as a quad, but that can bump up the number of geometry rendered by a lot.
        //Since we disable depth write, there is absolutely no point in rendering those quads
        //This optimization can result in a huge increase of perfs on complex Nifty UIs.
        if (color.getAlpha() > 0) {
            colorMaterial.setColor("Color", convertColor(color, tempColor));

            tempMat.loadIdentity();
            tempMat.setTranslation(x, getHeight() - y, 0);
            tempMat.setScale(width, height, 0);

            rm.setWorldMatrix(tempMat);
            rm.setForcedRenderState(renderState);
            colorMaterial.render(quadGeom, rm);
        }

        //System.out.format("renderQuad1(%d, %d, %d, %d, %s)\n", x, y, width, height, color.toString());
    }

    @Override
    public void renderQuad(int x, int y, int width, int height,
            Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {

        ByteBuffer buf = (ByteBuffer) quadColor.getData();
        buf.rewind();

        buf.putInt(convertColor(topRight));
        buf.putInt(convertColor(topLeft));

        buf.putInt(convertColor(bottomLeft));
        buf.putInt(convertColor(bottomRight));

        buf.flip();
        quadColor.updateData(buf);

        tempMat.loadIdentity();
        tempMat.setTranslation(x, getHeight() - y, 0);
        tempMat.setScale(width, height, 0);

        rm.setWorldMatrix(tempMat);
        rm.setForcedRenderState(renderState);
        vertexColorMaterial.render(quadGeom, rm);

        //System.out.format("renderQuad2(%d, %d, %d, %d, %s, %s, %s, %s)\n", x, y, width, height, topLeft.toString(),
        //                                                                                        topRight.toString(),
        //                                                                                        bottomRight.toString(),
        //                                                                                        bottomLeft.toString());
    }

    @Override
    public void enableClip(int x0, int y0, int x1, int y1) {
        clipWasSet = true;
        r.setClipRect(x0, getHeight() - y1, x1 - x0, y1 - y0);
    }

    @Override
    public void disableClip() {
        if (clipWasSet) {
            r.clearClipRect();
            clipWasSet = false;
        }
    }

}
