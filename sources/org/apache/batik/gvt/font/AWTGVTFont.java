/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt.font;

import java.awt.Font;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;

import java.awt.geom.AffineTransform;

import java.text.CharacterIterator;

import java.util.HashMap;
import java.util.Map;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.StringCharacterIterator;

import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;


/**
 * This is a wrapper class for a java.awt.Font instance.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class AWTGVTFont implements GVTFont {
    private Font  awtFont;
    private float size;
    private float scale;

    /**
     * Creates a new AWTGVTFont that wraps the given Font.
     *
     * @param font The font object to wrap.
     */
    public AWTGVTFont(Font font) {
        if (font.getSize2D() < 1f) {
            size = font.getSize2D();
            awtFont = font.deriveFont(10f);
        }
        scale = size/awtFont.getSize2D();
    }

    /**
     * Creates a new AWTGVTFont that wraps the given Font.
     *
     * @param font The font object to wrap.
     * @param scale The scale factor to apply to font...
     */
    public AWTGVTFont(Font font, float scale) {
        if (font.getSize2D() < 1f) {
            this.size = font.getSize2D()*scale;;
            this.awtFont = font.deriveFont(10f);
        }
        this.scale = size/awtFont.getSize2D();
    }

    /**
     * Creates a new AWTGVTFont with the specified attributes.
     *
     * @param attributes Contains attributes of the font to create.
     */
    public AWTGVTFont(Map attributes) {
        Float sz = (Float)attributes.get(TextAttribute.SIZE);
        if ((sz != null) && (sz.floatValue() < 1f)) {
            size = sz.floatValue();
            attributes.put(TextAttribute.SIZE, new Float(10f));
            awtFont = new Font(attributes);
        } else {
            awtFont = new Font(attributes);
            size = awtFont.getSize2D();
        }

        scale = size/awtFont.getSize2D();
    }

    /**
     * Creates a new AWTGVTFont from the specified name, style and point size.
     *
     * @param name The name of the new font.
     * @param style The required font style.
     * @param size The required font size.
     */
    public AWTGVTFont(String name, int style, int size) {
        this.awtFont = new Font(name, style, size);
        this.size  = size;
        this.scale = 1;
    }

    /**
     * Checks if this font can display the specified character.
     *
     * @param c The character to check.
     * @return Whether or not the character can be displayed.
     */
    public boolean canDisplay(char c) {
        return awtFont.canDisplay(c);
    }

    /**
     * Indicates whether or not this font can display the characters in the
     * specified text starting at start and ending at limit.
     *
     * @param text An array containing the characters to check.
     * @param start The index of the first character to check.
     * @param limit The index of the last character to check.
     *
     * @return The index of the first char this font cannot display. Will be
     * -1 if it can display all characters in the specified range.
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
        return awtFont.canDisplayUpTo(text, start, limit);
    }

    /**
     *  Indicates whether or not this font can display the the characters in
     *  the specified CharacterIterator starting at start and ending at limit.
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {
        return awtFont.canDisplayUpTo(iter, start, limit);
    }

    /**
     *  Indicates whether or not this font can display a specified String.
     */
    public int canDisplayUpTo(String str) {
        return awtFont.canDisplayUpTo(str);
    }

    /**
     *  Returns a new GlyphVector object created with the specified array of
     *  characters and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            char[] chars) {

        StringCharacterIterator sci = 
	    new StringCharacterIterator(new String(chars));
        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, chars), 
                                     this, scale, sci);
    }

    /**
     * Returns a new GlyphVector object created with the specified
     * CharacterIterator and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            CharacterIterator ci) {

        if (ci instanceof AttributedCharacterIterator) {
            AttributedCharacterIterator aci = (AttributedCharacterIterator)ci;
            if (ArabicTextHandler.containsArabic(aci)) {
                String str = ArabicTextHandler.createSubstituteString(aci);

                return createGlyphVector(frc, str);
            }
        }

        return new AWTGVTGlyphVector(awtFont.createGlyphVector(frc, ci), 
                                     this, scale, ci);
    }

    /**
     *  Returns a new GlyphVector object created with the specified integer
     *  array and the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            int[] glyphCodes, 
                                            CharacterIterator ci) {
        return new AWTGVTGlyphVector
            (awtFont.createGlyphVector(frc, glyphCodes), 
             this, scale, ci);
    }

    /**
     * Returns a new GlyphVector object created with the specified String and
     * the specified FontRenderContext.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc, String str) {

        StringCharacterIterator sci = new StringCharacterIterator(str);

        return new AWTGVTGlyphVector
	    (awtFont.createGlyphVector(frc, str), this, scale, sci);
    }

    /**
     * Creates a new Font object by replicating the current Font object and
     * applying a new size to it.
     */
    public GVTFont deriveFont(float size) {
        return new AWTGVTFont(awtFont, size/this.size);
    }

    /**
     *  Returns a LineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(char[] chars, 
                                         int beginIndex, 
                                         int limit,
                                         FontRenderContext frc) {
        return new GVTLineMetrics
            (awtFont.getLineMetrics(chars, beginIndex, limit, frc), scale);
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(CharacterIterator ci, 
                                         int beginIndex,
                                         int limit, 
                                         FontRenderContext frc) {
        return new GVTLineMetrics
	    (awtFont.getLineMetrics(ci, beginIndex, limit, frc), scale);
    }

    /**
     *  Returns a GVTLineMetrics object created with the specified String and
     *  FontRenderContext.
     */
    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        return new GVTLineMetrics(awtFont.getLineMetrics(str, frc), scale);
    }

    /**
     * Returns a GVTLineMetrics object created with the specified arguments.
     */
    public GVTLineMetrics getLineMetrics(String str, 
                                         int beginIndex, 
                                         int limit,
                                         FontRenderContext frc) {
        return new GVTLineMetrics
	    (awtFont.getLineMetrics(str, beginIndex, limit, frc), scale);
    }

    /**
     * Returns the size of this font.
     */
    public float getSize() {
        return size;
    }

    /**
     * Returns the horizontal kerning value for this glyph pair.
     */
    public float getHKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    /**
     * Returns the vertical kerning value for this glyph pair.
     */
    public float getVKern(int glyphCode1, int glyphCode2) {
        return 0f;
    }

    /**
     * Returns a string representation of this font. This is for debugging
     * purposes only.
     */
    public String toString() {
        return awtFont.getFontName();
    }
}

