/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rami.jme.font;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *The loaded font from the AssetManager. You can use {@link TrueTypeFont#render(java.lang.String)} method to render the text you want and attach it to the guiNode.
 * @author Rami Manaf Abdullah
 */
public class TrueTypeFont {

    public static final int PLAIN = Font.PLAIN;
    public static final int BOLD = Font.BOLD;
    public static final int ITALIC = Font.ITALIC;
    public static final int BOLD_ITALIC = 3;

    private Font ttf;
    private float size;
    private int style;
    private ColorRGBA color;
    private boolean antialiased;
    private AssetManager assetManager;

    public TrueTypeFont(AssetManager assetManager, Font ttf) {
        this.assetManager = assetManager;
        this.ttf = ttf;
        size = 20;
        style = PLAIN;
        color = ColorRGBA.Black;
        antialiased = false;
    }

    /**
     * render the passed text
     * @param text the text to be rendered
     * @return the spatial to attach to the guiNode
     */
    public TrueTypeText render(String text) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(ttf);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        if (antialiased) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(ttf);
        g2d.setColor(new Color(color.r, color.g, color.b, color.a));
        fm = g2d.getFontMetrics();
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();
        return new TrueTypeText(assetManager, new Texture2D(new AWTLoader().load(img, true)));
    }
    
    public int getStyle(){
        if(ttf.isBold() && ttf.isItalic())return BOLD_ITALIC;
        if(ttf.isBold()) return BOLD;
        if(ttf.isItalic()) return ITALIC;
        return PLAIN;
    }

    public float getSize() {
        return size;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public boolean isAntialiased() {
        return antialiased;
    }

    public void setSize(float size) {
        ttf = ttf.deriveFont(size);
    }

    public void setStyle(int style) {
        ttf = ttf.deriveFont(style);
    }

    public void setColor(ColorRGBA color) {
        this.color = color;
    }

    public void setAntialiased(boolean antialiased) {
        this.antialiased = antialiased;
    }

    public int getWidth(String text) {
        return (int) ttf.getStringBounds(text, new FontRenderContext(new AffineTransform(), antialiased, true)).getWidth();
    }
    
    public int getHeight(){
        return (int) ttf.getMaxCharBounds(new FontRenderContext(new AffineTransform(), antialiased, true)).getHeight();
    }

}
