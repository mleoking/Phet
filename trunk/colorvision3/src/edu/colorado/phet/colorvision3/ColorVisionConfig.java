/* ColorVisionConfig */

package edu.colorado.phet.colorvision3;

/**
 * ColorVisionConfig contains global configuration values for the Color Vision application.
 * See ColorVisionStrings.properties for localized Strings that are visible to the user.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$
 */
public class ColorVisionConfig
{
  // Resource bundles
  public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/ColorVisionStrings";

	// Clock constants
	public static final double TIME_STEP = 1;
	public static final int WAIT_TIME = 50;

	// Images
	public static final String IMAGES_DIRECTORY = "images/";
	public static final String HEAD_IMAGE = IMAGES_DIRECTORY + "head.gif";
	public static final String SPOTLIGHT_IMAGE = IMAGES_DIRECTORY + "spotlight.gif";
	
	// Dimensions
	public static final int APP_FRAME_WIDTH = 1024;
	public static final int APP_FRAME_HEIGHT = 768;

	/**
	 * This class is not intended for instantiation.
	 */ 
	private ColorVisionConfig() {
	}
}

/* end of file */