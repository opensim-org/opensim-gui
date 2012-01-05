/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * OpenSimRoundSliderUI.java
 *
 * Created on August 1, 2007, 12:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

//package javax.swing.plaf.basic;
package org.opensim.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Adjustable;
import java.awt.Image;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Insets;
import java.awt.Color;
import java.awt.IllegalComponentStateException;
import java.awt.Polygon;
import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Dictionary;
import java.util.Enumeration;

import javax.swing.border.AbstractBorder;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import sun.swing.DefaultLookup;
import sun.swing.UIAction;

import org.openide.util.Utilities;

/**
 *
 * @author Jeff Reinbolt
 */
public class OpenSimRoundSliderUI extends SliderUI
{
    // use static class members to avoid reloading/creation of image for every slider in the system. -Ayman
    //
    static Image sliderKnobArrowImg = Utilities.loadImage("org/opensim/view/sliderKnobArrow.png");
    static Image sliderKnobImg=Utilities.loadImage("org/opensim/view/sliderKnob.png");
    static Image sliderKnobArrow_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnobArrow_disabled.png");
    static Image sliderKnob_disabledImg=Utilities.loadImage("org/opensim/view/sliderKnob_disabled.png");
    
    // Old actions forward to an instance of this.
    private static final Actions SHARED_ACTION = new Actions();

    public static final int POSITIVE_SCROLL = +1;
    public static final int NEGATIVE_SCROLL = -1;
    public static final int MIN_SCROLL = -2;
    public static final int MAX_SCROLL = +2;

    protected Timer scrollTimer;
    protected JSlider slider;

    protected Insets focusInsets = null;
    protected Insets insetCache = null;
    protected boolean leftToRightCache = true;
    protected Rectangle focusRect = null;
    protected Rectangle contentRect = null;
    protected Rectangle labelRect = null;
    protected Rectangle tickRect = null;
    protected Rectangle trackRect = null;
    protected Rectangle thumbRect = null;

    protected int trackBuffer = 0;  // The distance that the track is from the side of the control

    private transient boolean isDragging;

    protected TrackListener trackListener;
    protected ChangeListener changeListener;
    protected ComponentListener componentListener;
    protected FocusListener focusListener;
    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;

    // Colors
    private Color shadowColor;
    private Color highlightColor;
    private Color focusColor;

    protected Color getShadowColor() {
        return shadowColor;
    }

    protected Color getHighlightColor() {
        return highlightColor;
    }

    protected Color getFocusColor() {
        return focusColor;
    }
    
    /////////////////////////////////////////////////////////////////////////////
    // ComponentUI Interface Implementation methods
    /////////////////////////////////////////////////////////////////////////////
    public static ComponentUI createUI(JComponent b)    {
        return new OpenSimRoundSliderUI((JSlider)b);
    }

    public OpenSimRoundSliderUI(JSlider b){
    }

    public void installUI(JComponent c)   {
        slider = (JSlider) c;

        slider.setEnabled(slider.isEnabled());
        LookAndFeel.installProperty(slider, "opaque", Boolean.TRUE);

        isDragging = false;
        trackListener = createTrackListener( slider );
        changeListener = createChangeListener( slider );
        componentListener = createComponentListener( slider );
        focusListener = createFocusListener( slider );
        scrollListener = createScrollListener( slider );
	propertyChangeListener = createPropertyChangeListener( slider );

	installDefaults( slider );
	installListeners( slider );
	installKeyboardActions( slider );

        scrollTimer = new Timer( 100, scrollListener );
        scrollTimer.setInitialDelay( 300 );   

	insetCache = slider.getInsets();
	leftToRightCache = slider.getComponentOrientation().isLeftToRight();
	focusRect = new Rectangle();
	contentRect = new Rectangle();
	labelRect = new Rectangle();
	tickRect = new Rectangle();
	trackRect = new Rectangle();
	thumbRect = new Rectangle();

	calculateGeometry(); // This figures out where the labels, ticks, track, and thumb are.
    }   

    public void uninstallUI(JComponent c) {
        if ( c != slider )
            throw new IllegalComponentStateException(
                                                    this + " was asked to deinstall() " 
                                                    + c + " when it only knows about " 
                                                    + slider + ".");

        LookAndFeel.uninstallBorder(slider);

        scrollTimer.stop();
        scrollTimer = null;

	uninstallListeners( slider );
	uninstallKeyboardActions(slider);

	focusInsets = null;
	insetCache = null;
	leftToRightCache = true;
	focusRect = null;
	contentRect = null;
	labelRect = null;
	tickRect = null;
	trackRect = null;
        thumbRect = null;
        trackListener = null;
        changeListener = null;
        componentListener = null;
        focusListener = null;
        scrollListener = null;
	propertyChangeListener = null;
        slider = null;
    }

    protected void installDefaults( JSlider slider ) {
        LookAndFeel.installBorder(slider, "Slider.border");
        LookAndFeel.installColors(slider, "Slider.background", "Slider.foreground");
        highlightColor = UIManager.getColor("Slider.highlight");

        shadowColor = UIManager.getColor("Slider.shadow");
        focusColor = UIManager.getColor("Slider.focus");

	focusInsets = (Insets)UIManager.get( "Slider.focusInsets" );
    }
    
    // -------------------------------------------------------------------------
    
    protected Dimension getThumbSize() {
        Dimension size = new Dimension();

        size.width = 22;
	size.height = 12;
	
	return size;
    }
        
    public void paintTrack(Graphics g)  {   
        
        int cy, cw, ch;

        Rectangle contentBounds = contentRect;
        cy = contentBounds.height;
        cw = contentBounds.width;
        ch = 5;

        g.translate(contentBounds.x, contentBounds.y);

        if (slider.isEnabled()) {
            g.setColor(new Color(247, 246, 238));
        }
        else {
            g.setColor(new Color(242, 241, 233));
        }

        // Draw oval with lineWidth > 1 pixel
        int left, top, width, height, lineWidth;
        left = 0;
        top = 0;
        width = cw;
        height = cy;
        lineWidth = ch;
        for(int i=0; i<lineWidth; i++) {
          g.drawOval(left, top, width, height);
          if((i+1)<lineWidth) {
            g.drawOval(left, top, width-1, height-1);
            g.drawOval(left+1, top, width-1, height-1);
            g.drawOval(left, top+1, width-1, height-1);
            g.drawOval(left+1, top+1, width-1, height-1);
            left = left + 1;
            top = top + 1;
            width = width - 2;
            height = height - 2;
          }
        }

        Color shadowColor = getShadowColor();
        Color highlightColor = getHighlightColor();
        Color blendShadowColor = shadowColor.brighter();
        Color blendHighlightColor = highlightColor.darker();

        g.setColor(shadowColor);
        g.drawArc(0, 0, cw, cy, 45, 180); // top-left outside
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 225, 180); // bottom-right inside

        g.setColor(highlightColor);
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 45, 180); // top-left inside
        g.drawArc(0, 0, cw, cy, 225, 180); // bottom-right outside

        int blendAngle = 20;

        g.setColor(blendHighlightColor);
        g.drawArc(0, 0, cw, cy, 45, blendAngle); // top-left outside
        g.drawArc(0, 0, cw, cy, 45+180-blendAngle, blendAngle); // top-left outside
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 225, blendAngle); // bottom-right inside
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 225+180-blendAngle, blendAngle); // bottom-right inside

        g.setColor(blendShadowColor);
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 45, blendAngle); // top-left inside
        g.drawArc(ch, ch, cw-2*ch, cy-2*ch, 45+180-blendAngle, blendAngle); // top-left inside
        g.drawArc(0, 0, cw, cy, 225, blendAngle); // bottom-right outside
        g.drawArc(0, 0, cw, cy, 225+180-blendAngle, blendAngle); // bottom-right outside

        g.translate(-contentBounds.x, -contentBounds.y);
    }

    public void paintThumb(Graphics g)  {    
        Rectangle knobBounds = thumbRect;
        Image sliderKnob;

        if (slider.isEnabled()) {
            if (slider.getPaintTicks()) {
                sliderKnob = sliderKnobArrowImg;
            }
            else {
                sliderKnob = sliderKnobImg;
            }
        }
        else {
            if (slider.getPaintTicks()) {
                sliderKnob = sliderKnobArrow_disabledImg;
            }
            else {
                sliderKnob = sliderKnob_disabledImg;
            }
        }

        g.drawImage(sliderKnob,knobBounds.x,knobBounds.y,slider);
    }

    // -------------------------------------------------------------------------

    protected TrackListener createTrackListener(JSlider slider) {
        return new TrackListener();
    }

    protected ChangeListener createChangeListener(JSlider slider) {
        return getHandler();
    }

    protected ComponentListener createComponentListener(JSlider slider) {
        return getHandler();
    }

    protected FocusListener createFocusListener(JSlider slider) {
        return getHandler();
    }

    protected ScrollListener createScrollListener( JSlider slider ) {
        return new ScrollListener();
    }

    protected PropertyChangeListener createPropertyChangeListener(
            JSlider slider) {
        return getHandler();
    }

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected void installListeners( JSlider slider ) {
        slider.addMouseListener(trackListener);
        slider.addMouseMotionListener(trackListener);
        slider.addFocusListener(focusListener);
        slider.addComponentListener(componentListener);
        slider.addPropertyChangeListener( propertyChangeListener );
        slider.getModel().addChangeListener(changeListener);
    }

    protected void uninstallListeners( JSlider slider ) {
        slider.removeMouseListener(trackListener);
        slider.removeMouseMotionListener(trackListener);
        slider.removeFocusListener(focusListener);
        slider.removeComponentListener(componentListener);
        slider.removePropertyChangeListener( propertyChangeListener );
        slider.getModel().removeChangeListener(changeListener);
        handler = null;
    }

    protected void installKeyboardActions( JSlider slider ) {
	InputMap km = getInputMap(JComponent.WHEN_FOCUSED, slider);
	SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED, km);
        LazyActionMap.installLazyActionMap(slider, OpenSimRoundSliderUI.class,
                "Slider.actionMap");
    }

    InputMap getInputMap(int condition, JSlider slider) {
        if (condition == JComponent.WHEN_FOCUSED) {
            InputMap keyMap = (InputMap)DefaultLookup.get(slider, this,
                  "Slider.focusInputMap");
            InputMap rtlKeyMap;

            if (slider.getComponentOrientation().isLeftToRight() ||
                ((rtlKeyMap = (InputMap)DefaultLookup.get(slider, this,
                          "Slider.focusInputMap.RightToLeft")) == null)) {
                return keyMap;
            } else {
                rtlKeyMap.setParent(keyMap);
                return rtlKeyMap;
            }
        }
        return null;
    }

    /**
     * Populates ComboBox's actions.
     */
    static void loadActionMap(LazyActionMap map) {
        map.put(new Actions(Actions.POSITIVE_UNIT_INCREMENT));
        map.put(new Actions(Actions.POSITIVE_BLOCK_INCREMENT));
        map.put(new Actions(Actions.NEGATIVE_UNIT_INCREMENT));
        map.put(new Actions(Actions.NEGATIVE_BLOCK_INCREMENT));
        map.put(new Actions(Actions.MIN_SCROLL_INCREMENT));
        map.put(new Actions(Actions.MAX_SCROLL_INCREMENT));
    }

    protected void uninstallKeyboardActions( JSlider slider ) {
	SwingUtilities.replaceUIActionMap(slider, null);
	SwingUtilities.replaceUIInputMap(slider, JComponent.WHEN_FOCUSED,
					 null);
    }

    public Dimension getPreferredHorizontalSize() {
        Dimension horizDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.horizontalSize");
        if (horizDim == null) {
            horizDim = new Dimension(144, 144);
        }
        return horizDim;
    }

    public Dimension getPreferredVerticalSize() {
        Dimension vertDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.verticalSize");
        if (vertDim == null) {
            vertDim = new Dimension(144, 144);
        }
        return vertDim;
    }

    public Dimension getMinimumHorizontalSize() {
        Dimension minHorizDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.minimumHorizontalSize");
        if (minHorizDim == null) {
            minHorizDim = new Dimension(144, 144);
        }
        return minHorizDim;
    }

    public Dimension getMinimumVerticalSize() {
        Dimension minVertDim = (Dimension)DefaultLookup.get(slider,
                this, "Slider.minimumVerticalSize");
        if (minVertDim == null) {
            minVertDim = new Dimension(144, 144);
        }
        return minVertDim;
    }

    public Dimension getPreferredSize(JComponent c)    {
        recalculateIfInsetsChanged();
        Dimension d;
        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d = new Dimension(getPreferredVerticalSize());
	    d.width = insetCache.left + insetCache.right;
	    d.width += focusInsets.left + focusInsets.right;
	    d.width += trackRect.width + tickRect.width + labelRect.width;
        }
        else {
            d = new Dimension(getPreferredHorizontalSize());
	    d.height = insetCache.top + insetCache.bottom;
	    d.height += focusInsets.top + focusInsets.bottom;
	    d.height += trackRect.height + tickRect.height + labelRect.height;
        }

        return d;
    }

    public Dimension getMinimumSize(JComponent c)  {
        recalculateIfInsetsChanged();
        Dimension d;

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d = new Dimension(getMinimumVerticalSize());
	    d.width = insetCache.left + insetCache.right;
	    d.width += focusInsets.left + focusInsets.right;
	    d.width += trackRect.width + tickRect.width + labelRect.width;
        }
        else {
            d = new Dimension(getMinimumHorizontalSize());
	    d.height = insetCache.top + insetCache.bottom;
	    d.height += focusInsets.top + focusInsets.bottom;
	    d.height += trackRect.height + tickRect.height + labelRect.height;
        }

        return d;
    }

    public Dimension getMaximumSize(JComponent c) {
        Dimension d = getPreferredSize(c);
        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            d.height = Short.MAX_VALUE;
        }
        else {
            d.width = Short.MAX_VALUE;
        }

        return d;
    }

    protected void calculateGeometry() {
        calculateFocusRect();
        calculateContentRect(); 
	calculateThumbSize();
	calculateTrackBuffer();
	calculateTrackRect();
	calculateTickRect();
	calculateLabelRect();
	calculateThumbLocation();
    }
  
    protected void calculateFocusRect() {
        focusRect.x = insetCache.left;
	focusRect.y = insetCache.top;
	focusRect.width = slider.getWidth() - (insetCache.left + insetCache.right);
	focusRect.height = slider.getHeight() - (insetCache.top + insetCache.bottom);
    }
  
    protected void calculateThumbSize() {
	Dimension size = getThumbSize();
	thumbRect.setSize( size.width, size.height );
    }
  
    protected void calculateContentRect() {
        contentRect.x = focusRect.x + focusInsets.left;
        contentRect.y = focusRect.y + focusInsets.top;
        contentRect.width = focusRect.width - (focusInsets.left + focusInsets.right);
        contentRect.height = focusRect.height - (focusInsets.top + focusInsets.bottom);
    }

    protected void calculateThumbLocation() {
        if ( slider.getSnapToTicks() ) {
	    int sliderValue = slider.getValue();
	    int snappedValue = sliderValue; 
	    int majorTickSpacing = slider.getMajorTickSpacing();
	    int minorTickSpacing = slider.getMinorTickSpacing();
	    int tickSpacing = 0;
	    
	    if ( minorTickSpacing > 0 ) {
	        tickSpacing = minorTickSpacing;
	    }
	    else if ( majorTickSpacing > 0 ) {
	        tickSpacing = majorTickSpacing;
	    }

	    if ( tickSpacing != 0 ) {
	        // If it's not on a tick, change the value
	        if ( (sliderValue - slider.getMinimum()) % tickSpacing != 0 ) {
		    float temp = (float)(sliderValue - slider.getMinimum()) / (float)tickSpacing;
		    int whichTick = Math.round( temp );
		    snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
		}
		
		if( snappedValue != sliderValue ) { 
		    slider.setValue( snappedValue );
		}
	    }
	}
	
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
            int valuePosition = xPositionForValue(slider.getValue());

	    thumbRect.x = valuePosition - (thumbRect.width / 2);
	    thumbRect.y = trackRect.y;
        }
        else {
            int valuePosition = yPositionForValue(slider.getValue());
	    
	    thumbRect.x = trackRect.x;
	    thumbRect.y = valuePosition - (thumbRect.height / 2);
        }
    }

    protected void calculateTrackBuffer() {
        if ( slider.getPaintLabels() && slider.getLabelTable()  != null ) {
            Component highLabel = getHighestValueLabel();
            Component lowLabel = getLowestValueLabel();

            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = Math.max( highLabel.getBounds().width, lowLabel.getBounds().width ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.width / 2 );
            }
            else {
                trackBuffer = Math.max( highLabel.getBounds().height, lowLabel.getBounds().height ) / 2;
                trackBuffer = Math.max( trackBuffer, thumbRect.height / 2 );
            }
        }
        else {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                trackBuffer = thumbRect.width / 2;
            }
            else {
                trackBuffer = thumbRect.height / 2;
            }
        }
    }

  
    protected void calculateTrackRect() {
	int centerSpacing = 0; // used to center sliders added using BorderLayout.CENTER (bug 4275631)
        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    centerSpacing = thumbRect.height;
	    if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	    if ( slider.getPaintLabels() ) centerSpacing += getHeightOfTallestLabel();
	    trackRect.x = contentRect.x + trackBuffer;
	    trackRect.y = contentRect.y + (contentRect.height - centerSpacing - 1)/2;
	    trackRect.width = contentRect.width - (trackBuffer * 2);
	    trackRect.height = thumbRect.height;
	}
	else {
	    centerSpacing = thumbRect.width;
	    if (slider.getComponentOrientation().isLeftToRight()) {
		if ( slider.getPaintTicks() ) centerSpacing += getTickLength();
	    	if ( slider.getPaintLabels() ) centerSpacing += getWidthOfWidestLabel();
	    } else {
	        if ( slider.getPaintTicks() ) centerSpacing -= getTickLength();
	    	if ( slider.getPaintLabels() ) centerSpacing -= getWidthOfWidestLabel();
	    }
	    trackRect.x = contentRect.x + (contentRect.width - centerSpacing - 1)/2;
	    trackRect.y = contentRect.y + trackBuffer;
	    trackRect.width = thumbRect.width;
	    trackRect.height = contentRect.height - (trackBuffer * 2);
	}

    }

    /**
     * Gets the height of the tick area for horizontal sliders and the width of the
     * tick area for vertical sliders.  BasicSliderUI uses the returned value to
     * determine the tick area rectangle.  If you want to give your ticks some room,
     * make this larger than you need and paint your ticks away from the sides in paintTicks().
     */
    protected int getTickLength() {
        return 8;
    }

    protected void calculateTickRect() {
	if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	    tickRect.x = trackRect.x;
	    tickRect.y = trackRect.y + trackRect.height;
	    tickRect.width = trackRect.width;
	    tickRect.height = getTickLength();
	    
	    if ( !slider.getPaintTicks() ) {
	        --tickRect.y;
		tickRect.height = 0;
	    }
	}
	else {
	    if(slider.getComponentOrientation().isLeftToRight()) {
	        tickRect.x = trackRect.x + trackRect.width;
		tickRect.width = getTickLength();
	    }
	    else {
	        tickRect.width = getTickLength();
	        tickRect.x = trackRect.x - tickRect.width;
	    }
	    tickRect.y = trackRect.y;
	    tickRect.height = trackRect.height;

	    if ( !slider.getPaintTicks() ) {
	        --tickRect.x;
		tickRect.width = 0;
	    }
	}
    }

    protected void calculateLabelRect() {
        if ( slider.getPaintLabels() ) {
	    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	        labelRect.x = tickRect.x - trackBuffer;
		labelRect.y = tickRect.y + tickRect.height;
		labelRect.width = tickRect.width + (trackBuffer * 2);
                labelRect.height = getHeightOfTallestLabel();
            }
            else {
	        if(slider.getComponentOrientation().isLeftToRight()) {
		    labelRect.x = tickRect.x + tickRect.width;
		    labelRect.width = getWidthOfWidestLabel();
		}
		else {
		    labelRect.width = getWidthOfWidestLabel();
		    labelRect.x = tickRect.x - labelRect.width;
		}
		labelRect.y = tickRect.y - trackBuffer;
		labelRect.height = tickRect.height + (trackBuffer * 2);
            }
        }
        else {
            if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
	        labelRect.x = tickRect.x;
		labelRect.y = tickRect.y + tickRect.height;
		labelRect.width = tickRect.width;
		labelRect.height = 0;
            }
            else {
	        if(slider.getComponentOrientation().isLeftToRight()) {
		    labelRect.x = tickRect.x + tickRect.width;
		}
		else {
		    labelRect.x = tickRect.x;
		}
		labelRect.y = tickRect.y;
		labelRect.width = 0;
		labelRect.height = tickRect.height;
            }
        }
    }

    public class PropertyChangeHandler implements PropertyChangeListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void propertyChange( PropertyChangeEvent e ) {
            getHandler().propertyChange(e);
        }
    }

    protected int getWidthOfWidestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int widest = 0;
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            while ( keys.hasMoreElements() ) {
                Component label = (Component)dictionary.get( keys.nextElement() );
                widest = Math.max( label.getPreferredSize().width, widest );
            }
        }
        return widest;
    }

    protected int getHeightOfTallestLabel() {
        Dictionary dictionary = slider.getLabelTable();
        int tallest = 0;
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            while ( keys.hasMoreElements() ) {
                Component label = (Component)dictionary.get( keys.nextElement() );
                tallest = Math.max( label.getPreferredSize().height, tallest );
            }
        }
        return tallest;
    }

    protected int getWidthOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int width = 0;

        if ( label != null ) {
            width = label.getPreferredSize().width;
        }

        return width;
    }

    protected int getWidthOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int width = 0;

        if ( label != null ) {
            width = label.getPreferredSize().width;
        }

        return width;
    }

    protected int getHeightOfHighValueLabel() {
        Component label = getHighestValueLabel();
        int height = 0;

        if ( label != null ) {
            height = label.getPreferredSize().height;
        }

        return height;
    }

    protected int getHeightOfLowValueLabel() {
        Component label = getLowestValueLabel();
        int height = 0;

        if ( label != null ) {
            height = label.getPreferredSize().height;
        }

        return height;
    }

    protected boolean drawInverted() {
        if (slider.getOrientation()==JSlider.HORIZONTAL) {
	    if(slider.getComponentOrientation().isLeftToRight()) {
	        return slider.getInverted();
	    } else {
	        return !slider.getInverted();
	    }
	} else {
	    return slider.getInverted();
	}
    }

    /**
     * Returns the label that corresponds to the highest slider value in the label table.
     * @see JSlider#setLabelTable
     */
    protected Component getLowestValueLabel() {
        Dictionary dictionary = slider.getLabelTable();
        Component label = null;

        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            if ( keys.hasMoreElements() ) {
                int lowestValue = ((Integer)keys.nextElement()).intValue();

                while ( keys.hasMoreElements() ) {
                    int value = ((Integer)keys.nextElement()).intValue();
                    lowestValue = Math.min( value, lowestValue );
                }

                label = (Component)dictionary.get( new Integer( lowestValue ) );
            }
        }

        return label;
    }

    /**
     * Returns the label that corresponds to the lowest slider value in the label table.
     * @see JSlider#setLabelTable
     */
    protected Component getHighestValueLabel() {
        Dictionary dictionary = slider.getLabelTable();
        Component label = null;

        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            if ( keys.hasMoreElements() ) {
                int highestValue = ((Integer)keys.nextElement()).intValue();

                while ( keys.hasMoreElements() ) {
                    int value = ((Integer)keys.nextElement()).intValue();
                    highestValue = Math.max( value, highestValue );
                }

                label = (Component)dictionary.get( new Integer( highestValue ) );
            }
        }

        return label;
    }

    @Override
    public void paint( Graphics g, JComponent c )   {
        recalculateIfInsetsChanged();
	recalculateIfOrientationChanged();
	Rectangle clip = g.getClipBounds();

	if ( !clip.intersects(trackRect) && slider.getPaintTrack())
	    calculateGeometry();

	if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
	    paintTrack( g );
	}
        if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
            paintTicks( g );
        }
        if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
            paintLabels( g );
        }
	if ( slider.hasFocus() && clip.intersects( focusRect ) ) {
	    paintFocus( g );      
	}
	if ( clip.intersects( thumbRect ) ) {
	    paintThumb( g );
	}
    }

    protected void recalculateIfInsetsChanged() {
        Insets newInsets = slider.getInsets();
        if ( !newInsets.equals( insetCache ) ) {
	    insetCache = newInsets;
	    calculateGeometry();
	}
    }

    protected void recalculateIfOrientationChanged() {
        boolean ltr = slider.getComponentOrientation().isLeftToRight();
        if ( ltr!=leftToRightCache ) {
	    leftToRightCache = ltr;
	    calculateGeometry();
	}
    }

    public void paintFocus(Graphics g)  {        
	g.setColor( getFocusColor() );

	BasicGraphicsUtils.drawDashedRect( g, focusRect.x, focusRect.y,
					   focusRect.width, focusRect.height );
    }

    public void paintTicks(Graphics g)  {        
        Rectangle tickBounds = tickRect;
        int i;
        int maj, min, max;
        int w = tickBounds.width;
        int h = tickBounds.height;
        int centerEffect, tickHeight;

        g.setColor(DefaultLookup.getColor(slider, this, "Slider.tickColor", Color.black));

        maj = slider.getMajorTickSpacing();
        min = slider.getMinorTickSpacing();

        if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
           g.translate( 0, tickBounds.y);

            int value = slider.getMinimum();
            int xPos = 0;

            if ( slider.getMinorTickSpacing() > 0 ) {
                while ( value <= slider.getMaximum() ) {
                    xPos = xPositionForValue( value );
                    paintMinorTickForHorizSlider( g, tickBounds, xPos );
                    value += slider.getMinorTickSpacing();
                }
            }

            if ( slider.getMajorTickSpacing() > 0 ) {
                value = slider.getMinimum();

                while ( value <= slider.getMaximum() ) {
                    xPos = xPositionForValue( value );
                    paintMajorTickForHorizSlider( g, tickBounds, xPos );
                    value += slider.getMajorTickSpacing();
                }
            }

            g.translate( 0, -tickBounds.y);
        }
        else {
           g.translate(tickBounds.x, 0);

            int value = slider.getMinimum();
            int yPos = 0;

            if ( slider.getMinorTickSpacing() > 0 ) {
	        int offset = 0;
	        if(!slider.getComponentOrientation().isLeftToRight()) {
		    offset = tickBounds.width - tickBounds.width / 2;
		    g.translate(offset, 0);
		}

                while ( value <= slider.getMaximum() ) {
                    yPos = yPositionForValue( value );
                    paintMinorTickForVertSlider( g, tickBounds, yPos );
                    value += slider.getMinorTickSpacing();
                }

		if(!slider.getComponentOrientation().isLeftToRight()) {
		    g.translate(-offset, 0);
		}
            }

            if ( slider.getMajorTickSpacing() > 0 ) {
                value = slider.getMinimum();
	        if(!slider.getComponentOrientation().isLeftToRight()) {
		    g.translate(2, 0);
		}

                while ( value <= slider.getMaximum() ) {
                    yPos = yPositionForValue( value );
                    paintMajorTickForVertSlider( g, tickBounds, yPos );
                    value += slider.getMajorTickSpacing();
                }

	        if(!slider.getComponentOrientation().isLeftToRight()) {
		    g.translate(-2, 0);
		}
            }
            g.translate(-tickBounds.x, 0);
        }
    }

    protected void paintMinorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, tickBounds.height / 2 - 1 );
    }

    protected void paintMajorTickForHorizSlider( Graphics g, Rectangle tickBounds, int x ) {
        g.drawLine( x, 0, x, tickBounds.height - 2 );
    }

    protected void paintMinorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.drawLine( 0, y, tickBounds.width / 2 - 1, y );
    }

    protected void paintMajorTickForVertSlider( Graphics g, Rectangle tickBounds, int y ) {
        g.drawLine( 0, y,  tickBounds.width - 2, y );
    }

    public void paintLabels( Graphics g ) {
        Rectangle labelBounds = labelRect;

        Dictionary dictionary = slider.getLabelTable();
        if ( dictionary != null ) {
            Enumeration keys = dictionary.keys();
            int minValue = slider.getMinimum();
            int maxValue = slider.getMaximum();
            while ( keys.hasMoreElements() ) {
                Integer key = (Integer)keys.nextElement();
                int value = key.intValue();
                if (value >= minValue && value <= maxValue) {
                    Component label = (Component)dictionary.get( key );
                    if ( slider.getOrientation() == JSlider.HORIZONTAL ) {
                        g.translate( 0, labelBounds.y );
                        paintHorizontalLabel( g, value, label );
                        g.translate( 0, -labelBounds.y );
                    }
                    else {
                        int offset = 0;
                        if (!slider.getComponentOrientation().isLeftToRight()) {
                            offset = labelBounds.width -
                                label.getPreferredSize().width;
                        }
                        g.translate( labelBounds.x + offset, 0 );
                        paintVerticalLabel( g, value, label );
                        g.translate( -labelBounds.x - offset, 0 );
                    }
                }
            }
        }

    }

    /**
     * Called for every label in the label table.  Used to draw the labels for horizontal sliders.
     * The graphics have been translated to labelRect.y already.
     * @see JSlider#setLabelTable
     */
    protected void paintHorizontalLabel( Graphics g, int value, Component label ) {
        int labelCenter = xPositionForValue( value );
        int labelLeft = labelCenter - (label.getPreferredSize().width / 2);
        g.translate( labelLeft, 0 );
        label.paint( g );
        g.translate( -labelLeft, 0 );
    }

    /**
     * Called for every label in the label table.  Used to draw the labels for vertical sliders.
     * The graphics have been translated to labelRect.x already.
     * @see JSlider#setLabelTable
     */
    protected void paintVerticalLabel( Graphics g, int value, Component label ) {
        int labelCenter = yPositionForValue( value );
        int labelTop = labelCenter - (label.getPreferredSize().height / 2);
        g.translate( 0, labelTop );
        label.paint( g );
        g.translate( 0, -labelTop );
    }

    // Used exclusively by setThumbLocation()
    private static Rectangle unionRect = new Rectangle();

    public void setThumbLocation(int x, int y)  {
        unionRect.setBounds( contentRect );

        thumbRect.setLocation( x, y );

	SwingUtilities.computeUnion( thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height, unionRect ); 
        slider.repaint( unionRect.x, unionRect.y, unionRect.width, unionRect.height );
    }

    public void scrollByBlock(int direction)    {
        synchronized(slider)    {

            int oldValue = slider.getValue();
            int blockIncrement =
                (slider.getMaximum() - slider.getMinimum()) / 10;
            if (blockIncrement <= 0 &&
                slider.getMaximum() > slider.getMinimum()) {

                blockIncrement = 1;
            }

            int delta = blockIncrement * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);
            slider.setValue(oldValue + delta);          
        }
    }

    public void scrollByUnit(int direction) {
        synchronized(slider)    {

            int oldValue = slider.getValue();
            int delta = 1 * ((direction > 0) ? POSITIVE_SCROLL : NEGATIVE_SCROLL);

            slider.setValue(oldValue + delta);  
        }       
    }

    /**
     * This function is called when a mousePressed was detected in the track, not
     * in the thumb.  The default behavior is to scroll by block.  You can
     *  override this method to stop it from scrolling or to add additional behavior.
     */
    protected void scrollDueToClickInTrack( int dir ) {
        scrollByBlock( dir );
    }

    protected int xPositionForValue( int value )    {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackLength = trackRect.width;
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / valueRange;
        int trackLeft = trackRect.x;
        int trackRight = trackRect.x + (trackRect.width - 1);
        int xPosition;

        if ( !drawInverted() ) {
            xPosition = trackLeft;
            xPosition += Math.round( pixelsPerValue * ((double)value - min) );
        }
        else {
            xPosition = trackRight;
            xPosition -= Math.round( pixelsPerValue * ((double)value - min) );
        }

        xPosition = Math.max( trackLeft, xPosition );
        xPosition = Math.min( trackRight, xPosition );

        return xPosition;
    }

    protected int yPositionForValue( int value )  {
        int min = slider.getMinimum();
        int max = slider.getMaximum();
        int trackLength = trackRect.height; 
        double valueRange = (double)max - (double)min;
        double pixelsPerValue = (double)trackLength / (double)valueRange;
        int trackTop = trackRect.y;
        int trackBottom = trackRect.y + (trackRect.height - 1);
        int yPosition;

        if ( !drawInverted() ) {
            yPosition = trackTop;
            yPosition += Math.round( pixelsPerValue * ((double)max - value ) );
        }
        else {
            yPosition = trackTop;
            yPosition += Math.round( pixelsPerValue * ((double)value - min) );
        }

        yPosition = Math.max( trackTop, yPosition );
        yPosition = Math.min( trackBottom, yPosition );

        return yPosition;
    }

    /**
     * Returns a value give a y position.  If yPos is past the track at the top or the
     * bottom it will set the value to the min or max of the slider, depending if the
     * slider is inverted or not.
     */
    public int valueForYPosition( int yPos ) {
        int value;
	final int minValue = slider.getMinimum();
	final int maxValue = slider.getMaximum();
	final int trackLength = trackRect.height;
	final int trackTop = trackRect.y;
	final int trackBottom = trackRect.y + (trackRect.height - 1);
	
	if ( yPos <= trackTop ) {
	    value = drawInverted() ? minValue : maxValue;
	}
	else if ( yPos >= trackBottom ) {
	    value = drawInverted() ? maxValue : minValue;
	}
	else {
	    int distanceFromTrackTop = yPos - trackTop;
	    double valueRange = (double)maxValue - (double)minValue;
	    double valuePerPixel = valueRange / (double)trackLength;
	    int valueFromTrackTop = (int)Math.round( distanceFromTrackTop * valuePerPixel );

	    value = drawInverted() ? minValue + valueFromTrackTop : maxValue - valueFromTrackTop;
	}
	
	return value;
    }
  
    /**
     * Returns a value give an x position.  If xPos is past the track at the left or the
     * right it will set the value to the min or max of the slider, depending if the
     * slider is inverted or not.
     */
    public int valueForXPosition( int xPos ) {
        int value;
	final int minValue = slider.getMinimum();
	final int maxValue = slider.getMaximum();
	final int trackLength = trackRect.width;
	final int trackLeft = trackRect.x; 
	final int trackRight = trackRect.x + (trackRect.width - 1);
	
	if ( xPos <= trackLeft ) {
	    value = drawInverted() ? maxValue : minValue;
	}
	else if ( xPos >= trackRight ) {
	    value = drawInverted() ? minValue : maxValue;
	}
	else {
	    int distanceFromTrackLeft = xPos - trackLeft;
	    double valueRange = (double)maxValue - (double)minValue;
	    double valuePerPixel = valueRange / (double)trackLength;
	    int valueFromTrackLeft = (int)Math.round( distanceFromTrackLeft * valuePerPixel );
	    
	    value = drawInverted() ? maxValue - valueFromTrackLeft :
	      minValue + valueFromTrackLeft;
	}
	
	return value;
    }


    private class Handler implements ChangeListener,
            ComponentListener, FocusListener, PropertyChangeListener {
        // Change Handler
        public void stateChanged(ChangeEvent e) {
	    if (!isDragging) {
	        calculateThumbLocation();
		slider.repaint();
	    }
        }

        // Component Handler
        public void componentHidden(ComponentEvent e) { }
        public void componentMoved(ComponentEvent e) { }
        public void componentResized(ComponentEvent e) {
	    calculateGeometry();
	    slider.repaint();
        }
        public void componentShown(ComponentEvent e) { }

        // Focus Handler
        public void focusGained(FocusEvent e) { slider.repaint(); }
        public void focusLost(FocusEvent e) { slider.repaint(); }

        // Property Change Handler
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            if ("orientation".equals(propertyName) ||
                    "inverted".equals(propertyName) ||
                    "labelTable".equals(propertyName) ||
                    "majorTickSpacing".equals(propertyName) ||
                    "minorTickSpacing".equals(propertyName) ||
                    "paintTicks".equals(propertyName) ||
                    "paintTrack".equals(propertyName) ||
                    "paintLabels".equals(propertyName)) {
                calculateGeometry();
                slider.repaint();
            } else if ("componentOrientation".equals(propertyName)) {
                calculateGeometry();
                slider.repaint();
                InputMap km = getInputMap(JComponent.WHEN_FOCUSED, slider);
                SwingUtilities.replaceUIInputMap(slider,
                    JComponent.WHEN_FOCUSED, km);
            } else if (propertyName == "model") {
                ((BoundedRangeModel)e.getOldValue()).removeChangeListener(
                    changeListener);
                ((BoundedRangeModel)e.getNewValue()).addChangeListener(
                    changeListener);
                calculateThumbLocation();
                slider.repaint();
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /// Model Listener Class
    /////////////////////////////////////////////////////////////////////////        
    /**
     * Data model listener.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class ChangeHandler implements ChangeListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void stateChanged(ChangeEvent e) {
            getHandler().stateChanged(e);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    /// Track Listener Class
    /////////////////////////////////////////////////////////////////////////        
    /**
     * Track mouse movements.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class TrackListener extends MouseInputAdapter {
        protected transient int offsetX, offsetY;
        protected transient int currentMouseX, currentMouseY;

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            offsetX = 0;
            offsetY = 0;
            scrollTimer.stop();

            // This is the way we have to determine snap-to-ticks.  It's
            // hard to explain but since ChangeEvents don't give us any
            // idea what has changed we don't have a way to stop the thumb
            // bounds from being recalculated.  Recalculating the thumb
            // bounds moves the thumb over the current value (i.e., snapping
            // to the ticks).
            if (slider.getSnapToTicks() /*|| slider.getSnapToValue()*/ ) {
                isDragging = false;
                slider.setValueIsAdjusting(false);
            }
            else {
                slider.setValueIsAdjusting(false);
                isDragging = false;
            }
            slider.repaint();
        }

        /**
        * If the mouse is pressed above the "thumb" component
        * then reduce the scrollbars value by one page ("page up"), 
        * otherwise increase it by one page.  If there is no 
        * thumb then page up if the mouse is in the upper half
        * of the track.
        */
        @Override
        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (slider.isRequestFocusEnabled()) {
                slider.requestFocus();
            }

            // Clicked in the Thumb area?
            if (thumbRect.contains(currentMouseX, currentMouseY)) {
//                switch (slider.getOrientation()) {
//                case JSlider.VERTICAL:
                    offsetY = currentMouseY - thumbRect.y;
//                    break;
//                case JSlider.HORIZONTAL:
                    offsetX = currentMouseX - thumbRect.x;
//                    break;
//                }
                isDragging = true;
                return;
            }
            isDragging = false;
            slider.setValueIsAdjusting(true);

            Dimension sbSize = slider.getSize();
            int direction = POSITIVE_SCROLL;

//            switch (slider.getOrientation()) {
//            case JSlider.VERTICAL:
                if ( thumbRect.isEmpty() ) {
                    int scrollbarCenter = sbSize.height / 2;
                    if ( !drawInverted() ) {
                        direction = (currentMouseY < scrollbarCenter) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < scrollbarCenter) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
                else {
                    int thumbY = thumbRect.y;
                    if ( !drawInverted() ) {
                        direction = (currentMouseY < thumbY) ?
                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
                    }
                    else {
                        direction = (currentMouseY < thumbY) ?
                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
                    }
                }
//                break;                    
//            case JSlider.HORIZONTAL:
//                if ( thumbRect.isEmpty() ) {
//                    int scrollbarCenter = sbSize.width / 2;
//                    if ( !drawInverted() ) {
//                        direction = (currentMouseX < scrollbarCenter) ?
//                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
//                    }
//                    else {
//                        direction = (currentMouseX < scrollbarCenter) ?
//                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
//                    }
//                }
//                else {
//                    int thumbX = thumbRect.x;
//                    if ( !drawInverted() ) {
//                        direction = (currentMouseX < thumbX) ?
//                            NEGATIVE_SCROLL : POSITIVE_SCROLL;
//                    }
//                    else {
//                        direction = (currentMouseX < thumbX) ?
//                            POSITIVE_SCROLL : NEGATIVE_SCROLL;
//                    }
//                }
//                break;
//            }
            scrollDueToClickInTrack(direction);
            Rectangle r = thumbRect;
            if (!r.contains(currentMouseX, currentMouseY)) {
                if (shouldScroll(direction)) {
                    scrollTimer.stop();
                    scrollListener.setDirection(direction);
                    scrollTimer.start();
                }
            }
        }

        public boolean shouldScroll(int direction) {
            Rectangle r = thumbRect;
//            if (slider.getOrientation() == JSlider.VERTICAL) {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.y + r.height  <= currentMouseY) {
                        return false;
                    }
                }
                else if (r.y >= currentMouseY) {
                    return false;
                }
//            }
//            else {
                if (drawInverted() ? direction < 0 : direction > 0) {
                    if (r.x + r.width  >= currentMouseX) {
                        return false;
                    }
                }
                else if (r.x <= currentMouseX) {
                    return false;
                }
//            }

            if (direction > 0 && slider.getValue() + slider.getExtent() >=
                    slider.getMaximum()) {
                return false;
            }
            else if (direction < 0 && slider.getValue() <=
                    slider.getMinimum()) {
                return false;
            }

            return true;
        }

        /** 
        * Set the models value to the position of the top/left
        * of the thumb relative to the origin of the track.
        */
        @Override
        public void mouseDragged(MouseEvent e) {
            int thumbMiddle = 0;

            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (!isDragging) {
                return;
            }

            slider.setValueIsAdjusting(true);

//            switch (slider.getOrientation()) {
//            case JSlider.VERTICAL:      
                int halfThumbHeight = thumbRect.height / 2;
                int thumbTop = e.getY() - offsetY;
                int trackTop = contentRect.y;
                int trackBottom = contentRect.y + (contentRect.height - 1);
//                int vMax = yPositionForValue(slider.getMaximum() -
//                                            slider.getExtent());
//                if (drawInverted()) {
//                    trackBottom = vMax;
//                }
//                else {
//                    trackTop = vMax;
//                }
                thumbTop = Math.max(thumbTop, trackTop - halfThumbHeight);
                thumbTop = Math.min(thumbTop, trackBottom - halfThumbHeight);

                setThumbLocation(thumbRect.x, thumbTop);

                thumbMiddle = thumbTop + halfThumbHeight;
                slider.setValue( valueForYPosition( thumbMiddle ) );
//                break;
//            case JSlider.HORIZONTAL:
                int halfThumbWidth = thumbRect.width / 2;
                int thumbLeft = e.getX() - offsetX;
                int trackLeft = contentRect.x;
                int trackRight = contentRect.x + (contentRect.width - 1);
//                int hMax = xPositionForValue(slider.getMaximum() -
//                                            slider.getExtent());
//                if (drawInverted()) {
//                    trackLeft = hMax;
//                }
//                else {
//                    trackRight = hMax;
//                }
                thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                setThumbLocation(thumbLeft, thumbRect.y);

                thumbMiddle = thumbLeft + halfThumbWidth;
                slider.setValue(valueForXPosition(thumbMiddle));
//                break;
//            default:
//                return;
//            }
        }

        @Override
        public void mouseMoved(MouseEvent e) { }
    }

    /**
     * Scroll-event listener.
     *
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class ScrollListener implements ActionListener {
        // changed this class to public to avoid bogus IllegalAccessException
        // bug in InternetExplorer browser.  It was protected.  Work around
        // for 4109432
        int direction = POSITIVE_SCROLL;
        boolean useBlockIncrement;

        public ScrollListener() {
            direction = POSITIVE_SCROLL;
            useBlockIncrement = true;
        }

        public ScrollListener(int dir, boolean block)   {
            direction = dir;
            useBlockIncrement = block;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public void setScrollByBlock(boolean block) {
            this.useBlockIncrement = block;
        }

        public void actionPerformed(ActionEvent e) {
            if (useBlockIncrement) {
                scrollByBlock(direction);
            }
            else {
                scrollByUnit(direction);
            }
            if (!trackListener.shouldScroll(direction)) {
                ((Timer)e.getSource()).stop();
            }
        }
    }

    /**
     * Listener for resizing events.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class ComponentHandler extends ComponentAdapter {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void componentResized(ComponentEvent e)  {
            getHandler().componentResized(e);
        }
    };  

    /**
     * Focus-change listener.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class FocusHandler implements FocusListener {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this      
        // class calls into the Handler.
        public void focusGained(FocusEvent e) {
            getHandler().focusGained(e);
        }

        public void focusLost(FocusEvent e) {
            getHandler().focusLost(e);
        }
    }

    /**
     * As of Java 2 platform v1.3 this undocumented class is no longer used.
     * The recommended approach to creating bindings is to use a
     * combination of an <code>ActionMap</code>, to contain the action,
     * and an <code>InputMap</code> to contain the mapping from KeyStroke
     * to action description. The InputMap is is usually described in the
     * LookAndFeel tables.
     * <p>
     * Please refer to the key bindings specification for further details.
     * <p>
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    public class ActionScroller extends AbstractAction {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Actions. If you need to add
        // new functionality add it to the Actions, but make sure this
        // class calls into the Actions.
        int dir;
        boolean block;
        JSlider slider;

        public ActionScroller( JSlider slider, int dir, boolean block) {
            this.dir = dir;
            this.block = block;
            this.slider = slider;
        }

        public void actionPerformed(ActionEvent e) {
            SHARED_ACTION.scroll(slider, OpenSimRoundSliderUI.this, dir, block);
	}

	public boolean isEnabled() { 
	    boolean b = true;
	    if (slider != null) {
		b = slider.isEnabled();
	    }
	    return b;
	}

    };


    /**
     * A static version of the above.
     */
    static class SharedActionScroller extends AbstractAction {
        // NOTE: This class exists only for backward compatability. All
        // its functionality has been moved into Actions. If you need to add
        // new functionality add it to the Actions, but make sure this
        // class calls into the Actions.
        int dir;
        boolean block;

        public SharedActionScroller(int dir, boolean block) {
            this.dir = dir;
            this.block = block;
        }

        public void actionPerformed(ActionEvent evt) {
            JSlider slider = (JSlider)evt.getSource();
            OpenSimRoundSliderUI ui = (OpenSimRoundSliderUI) slider.getUI();
            //= (OpenSimRoundSliderUI)BasicLookAndFeel.getUIOfType(slider.getUI(), OpenSimRoundSliderUI.class);
            if (OpenSimRoundSliderUI.class.isInstance(slider.getUI())) {
                // do nothing
            } else {
                return;
            }
            SHARED_ACTION.scroll(slider, ui, dir, block);
	}
    }

    private static class Actions extends UIAction {
        public static final String POSITIVE_UNIT_INCREMENT =
            "positiveUnitIncrement";
        public static final String POSITIVE_BLOCK_INCREMENT =
            "positiveBlockIncrement";
        public static final String NEGATIVE_UNIT_INCREMENT =
            "negativeUnitIncrement";
        public static final String NEGATIVE_BLOCK_INCREMENT =
            "negativeBlockIncrement";
        public static final String MIN_SCROLL_INCREMENT = "minScroll";
        public static final String MAX_SCROLL_INCREMENT = "maxScroll";


        Actions() {
            super(null);
        }

        public Actions(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent evt) {
            JSlider slider = (JSlider)evt.getSource();
            OpenSimRoundSliderUI ui = (OpenSimRoundSliderUI) slider.getUI();
            //= (OpenSimRoundSliderUI)BasicLookAndFeel.getUIOfType(slider.getUI(), OpenSimRoundSliderUI.class);
            if (OpenSimRoundSliderUI.class.isInstance(slider.getUI())) {
                // do nothing
            } else {
                return;
            }
            String name = getName();

            if (POSITIVE_UNIT_INCREMENT == name) {
                scroll(slider, ui, POSITIVE_SCROLL, false);
            } else if (NEGATIVE_UNIT_INCREMENT == name) {
                scroll(slider, ui, NEGATIVE_SCROLL, false);
            } else if (POSITIVE_BLOCK_INCREMENT == name) {
                scroll(slider, ui, POSITIVE_SCROLL, true);
            } else if (NEGATIVE_BLOCK_INCREMENT == name) {
                scroll(slider, ui, NEGATIVE_SCROLL, true);
            } else if (MIN_SCROLL_INCREMENT == name) {
                scroll(slider, ui, MIN_SCROLL, false);
            } else if (MAX_SCROLL_INCREMENT == name) {
                scroll(slider, ui, MAX_SCROLL, false);
            }
        }

        private void scroll(JSlider slider, OpenSimRoundSliderUI ui, int direction,
                boolean isBlock) {
            boolean invert = slider.getInverted();

            if (direction == NEGATIVE_SCROLL || direction == POSITIVE_SCROLL) {
                if (invert) {
                    direction = (direction == POSITIVE_SCROLL) ?
                        NEGATIVE_SCROLL : POSITIVE_SCROLL;
                }

                if (isBlock) {
                    ui.scrollByBlock(direction);
                } else {
                    ui.scrollByUnit(direction);
                }
            } else {  // MIN or MAX
                if (invert) {
                    direction = (direction == MIN_SCROLL) ?
                        MAX_SCROLL : MIN_SCROLL;
                }

                slider.setValue((direction == MIN_SCROLL) ?
                    slider.getMinimum() : slider.getMaximum());
            }
        }
    }
    
    /**
 * An ActionMap that populates its contents as necessary. The
 * contents are populated by invoking the <code>loadActionMap</code>
 * method on the passed in Object.
 *
 * @version 1.5, 12/19/03
 * @author Scott Violet
 */
    static class LazyActionMap extends ActionMapUIResource {
    /**
     * Object to invoke <code>loadActionMap</code> on. This may be
     * a Class object.
     */
    private transient Object _loader;

    /**
     * Installs an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p>
     * This should be used if the ActionMap can be shared.
     *
     * @param c JComponent to install the ActionMap on.
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    static void installLazyActionMap(JComponent c, Class loaderClass,
                                     String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        SwingUtilities.replaceUIActionMap(c, map);
    }

    /**
     * Returns an ActionMap that will be populated by invoking the
     * <code>loadActionMap</code> method on the specified Class
     * when necessary.
     * <p>
     * This should be used if the ActionMap can be shared.
     *
     * @param c JComponent to install the ActionMap on.
     * @param loaderClass Class object that gets loadActionMap invoked
     *                    on.
     * @param defaultsKey Key to use to defaults table to check for
     *        existing map and what resulting Map will be registered on.
     */
    private ActionMap getActionMap(Class loaderClass,
                                  String defaultsKey) {
        ActionMap map = (ActionMap)UIManager.get(defaultsKey);
        if (map == null) {
            map = new LazyActionMap(loaderClass);
            UIManager.getLookAndFeelDefaults().put(defaultsKey, map);
        }
        return map;
    }


    private LazyActionMap(Class loader) {
        _loader = loader;
    }

    public void put(Action action) {
        put(action.getValue(Action.NAME), action);
    }

        @Override
    public void put(Object key, Action action) {
        loadIfNecessary();
        super.put(key, action);
    }

    public Action get(Object key) {
        loadIfNecessary();
        return super.get(key);
    }

    public void remove(Object key) {
        loadIfNecessary();
        super.remove(key);
    }

    public void clear() {
        loadIfNecessary();
        super.clear();
    }

    public Object[] keys() {
        loadIfNecessary();
        return super.keys();
    }

    public int size() {
        loadIfNecessary();
        return super.size();
    }

    public Object[] allKeys() {
        loadIfNecessary();
        return super.allKeys();
    }

    public void setParent(ActionMap map) {
        loadIfNecessary();
        super.setParent(map);
    }

    private void loadIfNecessary() {
        if (_loader != null) {
            Object loader = _loader;

            _loader = null;
            Class klass = (Class)loader;
            try {
                Method method = klass.getDeclaredMethod("loadActionMap",
                                      new Class[] { LazyActionMap.class });
                method.invoke(klass, new Object[] { this });
            } catch (NoSuchMethodException nsme) {
                assert false : "LazyActionMap unable to load actions " +
                        klass;
            } catch (IllegalAccessException iae) {
                assert false : "LazyActionMap unable to load actions " +
                        iae;
            } catch (InvocationTargetException ite) {
                assert false : "LazyActionMap unable to load actions " +
                        ite;
            } catch (IllegalArgumentException iae) {
                assert false : "LazyActionMap unable to load actions " +
                        iae;
            }
        }
    }
}

    
}


