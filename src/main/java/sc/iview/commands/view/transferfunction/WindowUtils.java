package sc.iview.commands.view.transferfunction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;

/**
 * Functions for handling java drawings
 * @author michael
 *
 */
public class WindowUtils {

	/**
	 * Transfers a point between normal space to java inverted y space 
	 * @param pointInNormalSpace the point in a space where y Axis goes from down to up
	 * @param windowSize the window size
	 * @return the point in a space where y Axis goes from up to down
	 */
	public static Point transformWindowNormalSpace(final Point pointInNormalSpace, 
			final Dimension windowSize){
		return new Point(pointInNormalSpace.x,
				(windowSize.height-1)- pointInNormalSpace.y);

	}

	/**
	 * Returns the color components normalized from 0 to 1
	 * @param color Color to extract components
	 * @return the converted rgba component
	 */
	public static float[] getNormalizedColor(Color color){
		float rgba[] = {0,0,0,(float)(color.getAlpha())/255.f};
		color.getColorComponents(rgba);
		return rgba;
	}
	
	/**
	 * Return the left aligned input component
	 * @param c the component to left aligne
	 * @return the left aligned input reference
	 */
	public static JComponent aligneLeft(final JComponent c){
		c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		return c;
	}
}
