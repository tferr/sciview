package sc.iview.commands.view.transferfunction;

import com.jogamp.opengl.math.VectorUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * store data for 1D Transfer functions
 * @author michael
 *
 */
public class TransferFunction1D {

	private List<TransferFunctionListener> transferFunctionListeners = new ArrayList<TransferFunctionListener>();


	//max text size so scaling is needed for the transfer function data 
	private final int maxFunctionPointSamples =2048 -1;

	/**
	 * order points first by x then by y
	 */
	private final Comparator<Point2D.Float> pointOrderXOperator = new Comparator<Point2D.Float>() {

		@Override
		public int compare(Point2D.Float a, Point2D.Float b) {
			//same x
			if(a.x == b.x){
				return (int)Math.signum(a.y-b.y);
			}
			return (int)Math.signum(a.x-b.x);
		}
	};

	private Point2D.Float maxCoordinates;

	private final Point2D.Float minCoordinates = new Point2D.Float(0,0);

	private final TreeMap<Point2D.Float,Color> colors = new TreeMap<Point2D.Float, Color>(pointOrderXOperator);

	private float maxVolumeValue;

	/**
	 * Checks if a certain point is within the min and max coordinates of the transfer function
	 * @param point the point to check
	 * @return true if the point is valid
	 */
	private boolean isPointValid(Point2D.Float point){
		//min
		if(point.x < minCoordinates.x || point.y < minCoordinates.y ){
			return false;
		}

		//max
		if(point.x > maxCoordinates.x || point.y > maxCoordinates.y ){
			return false;
		}
		return true;
	}

	/**
	 * Tells all listeners that the transfer function sampler changed
	 */
	private void fireSamplerChangedEventAll(){
		for(TransferFunctionListener listener: transferFunctionListeners){
			listener.classifierChanged(this);
		}
	}

	/**
	 * Tells all listeners that the function points have changed
	 */
	private void fireFunctionPointChangedEventAll(){

		for(TransferFunctionListener listener:transferFunctionListeners){
			listener.functionPointChanged(this);
		}
	}

	/**
	 * Resets the transfer function to a canonical configuration
	 */
	public void resetColors(){
		colors.clear();
		colors.put(new Point2D.Float(minCoordinates.x, minCoordinates.y), Color.BLUE);
		colors.put(new Point2D.Float(maxCoordinates.x/2f,maxCoordinates.y/2f), Color.WHITE);
		colors.put(new Point2D.Float(maxCoordinates.x,maxCoordinates.y),Color.RED);

		fireFunctionPointChangedEventAll();
	}

	/**
	 * Initializes the transfer function with standard parameters
	 * @param maxCoordinates  the highest valid coordinate 
	 */
	private void init( Point2D.Float maxCoordinates){
		this.maxCoordinates = maxCoordinates;

		resetColors();
	}

	/**
	 * Constructor
	 * @param maxVolume the highest volume value
	 * @param maxTau the highest absorbation value
	 */
	public TransferFunction1D(float maxVolume,  float maxTau) {
		Double maxV = Math.ceil(maxVolume);
		Double maxT = Math.ceil(maxTau);
		init(new Point2D.Float(maxV.intValue(),maxT.intValue()));
	}

	/**
	 * Constructor
	 */
	public TransferFunction1D(){
		init(new Point2D.Float(256,1.f));
	}

	/**
	 * removes a color point if it is contained and not the first or last point.
	 * @param point the point to remove
	 */
	public void removeControlPoint(Point2D.Float point){
		if(!colors.containsKey(point)){
			return;
		}
		if(point.equals(colors.firstKey()) || point.equals(colors.lastKey())){
			return;
		}
		colors.remove(point);
	}

	/**
	 * @return the colors
	 */
	public TreeMap<Point2D.Float, Color> getColors() {
		return new TreeMap<Point2D.Float, Color>(colors);
	}

	/**
	 * Sets the color of a certain point in the panel.
	 * @param point Position on the panel area.
	 * @param color Color to be set.
	 */
	public void setColor(final Point2D.Float point, final Color color){
		if(!isPointValid(point)){
			throw new IndexOutOfBoundsException("Point: "+point+" was not in tf space");
		}

		colors.put(point, color);

		fireFunctionPointChangedEventAll();
	}

	/**
	 * Adds a listener to the transfer function panel
	 * @param listener the lsitener to add
	 */
	public void addTransferFunctionListener(final TransferFunctionListener listener){
		transferFunctionListeners.add(listener);

		listener.functionPointChanged(this);
	}

	/**
	 * @return the maxOrdinates
	 */
	public Point2D.Float getMaxOrdinates() {
		return maxCoordinates;
	}

	/**
	 * @param maxOrdinates the maxOrdinates to set
	 */
	public void setMaxOrdinates(Point2D.Float maxOrdinates) {
		maxVolumeValue = maxOrdinates.x;
		maxOrdinates.x  = (maxOrdinates.x > maxFunctionPointSamples)?maxFunctionPointSamples:maxOrdinates.x;

		Point2D.Float oldMax = new Point2D.Float(this.maxCoordinates.x,this.maxCoordinates.y);

		this.maxCoordinates = maxOrdinates;

		rescale(oldMax);

		fireFunctionPointChangedEventAll();
	}

	/**
	 * re-scales the transfer function from 0 to max coordinate in each dim 
	 * @param oldMax the old maximum coordinate
	 */
	private void rescale(Point2D.Float oldMax){
		float [] scaleFactors = {(float)maxCoordinates.x/(float)oldMax.x, 
				(float)maxCoordinates.y/(float)oldMax.y };

		//scale color points
		TreeMap<Point2D.Float, Color> newColorMap = new TreeMap<Point2D.Float, Color>(pointOrderXOperator);
		for(Point2D.Float position:colors.keySet()){
			float newX = scaleFactors[0]*(float)position.getX();
			float newY = scaleFactors[1]*(float)position.getY();
			Color color = colors.get(position);
			newColorMap.put(new Point2D.Float(newX,newY),color);
		}
		colors.clear();
		colors.putAll(newColorMap);
	}

	/**
	 * Interpolates the color value of a x component of a certain transfer function point 
	 * @param index the control point
	 * @return the color
	 */
	private Color getColorComponent(Point2D.Float index){
		float [] result = {0,0,0};


		//get RGB
		Point2D.Float nextIndex = colors.ceilingKey(index);
		if(nextIndex == null){
			return colors.lastEntry().getValue();
		}

		if(nextIndex == index){
			return colors.get(index);
		}
		Point2D.Float previousIndex = colors.lowerKey(index);
		float colorDiff = nextIndex.x-previousIndex.x;
		float colorOffset = index.x - previousIndex.x;

		float[] colorPrev ={0,0,0};
		float[] colorNext ={0,0,0};

		colors.get(previousIndex).getColorComponents(colorPrev);
		colors.get(nextIndex).getColorComponents(colorNext);

		//interpolation
		float []tmpColor= {0,0,0};
		VectorUtil.subVec3(tmpColor, colorNext, colorPrev);
		VectorUtil.scaleVec3(tmpColor,tmpColor,colorOffset/colorDiff);
		VectorUtil.addVec3(result, colorPrev,tmpColor );

		return new Color(result[0],result[1],result[2],0);
	}

	/**
	 * Calculates the normalized alpha value in case the transfer function uses unnormalized ones.
	 * @param unNormalizedValue the value
	 * @return the normalized value
	 */
	private float getNormalizedAlphaValue(float unNormalizedValue){
		float normFactor = 1f/ (float)getMaxOrdinates().y;

		float calculatedValue =  unNormalizedValue*normFactor;

		return Math.min(1, Math.max(calculatedValue, 0));

	}

	/**
	 * Interpolates the alpha value of a x component of a certain transfer function point .
	 * @param index the point
	 * @return the interpolated alpha value
	 */
	private float getAlpha (Point2D.Float index){
		//get alpha
		Point2D.Float nextIndex = colors.ceilingKey(index);
		float nextAlpha = getNormalizedAlphaValue(nextIndex.y);

		if(nextIndex.x == index.x){
			return nextAlpha;
		}
		Point2D.Float previousIndex = colors.lowerKey(index);
		float prevAlpha = getNormalizedAlphaValue(previousIndex.y);

		float colorDiff = nextIndex.x-previousIndex.x;
		float colorOffset = index.x - previousIndex.x;
		
		//interpolation
		float m = (nextAlpha - prevAlpha)/colorDiff;
		return m* colorOffset + prevAlpha;
	} 

	/**
	 * Samples the color of a x component of a certain transfer function point .
	 * @param index the point 
	 * @return the rgb color components
	 */
	private float[] getColorForXSampleTransferSpace(Point2D.Float index){

		float [] result = {0,0,0,0};

		getColorComponent(index).getColorComponents(result);

		result[3] = getAlpha(index); 

		return result;
	}

	/**
	 * Create a discrete transfer function by sampling 
	 * @return the sample-index color mapping
	 */
	public final TreeMap<Integer, Color> sampleColors() {		
		TreeMap<Integer, Color> returnColors = new TreeMap<Integer, Color>();
		TreeMap<Integer, Long> multiplicities = new TreeMap<Integer, Long>();
		TreeMap<Integer, float[]> colorComponents = new TreeMap<Integer, float[]>();

		float[] currentColor = null;

		//get colors from line
		for(Point2D.Float index : colors.keySet()){
			int intIndex = (int)index.x;
			if(!multiplicities.containsKey(intIndex)){
				multiplicities.put(intIndex, 0l);
				colorComponents.put(intIndex, new float[]{0,0,0,0});
			}

			currentColor = getColorForXSampleTransferSpace(index);
			if(currentColor == null){
				continue;
			}

			float[] colorComponent = colorComponents.get(intIndex);
			for(int i = 0; i < colorComponent.length; i++){
				colorComponent[i]+=currentColor[i];
			}
			Long num = multiplicities.get(intIndex) + 1;
			multiplicities.put(intIndex, num);
		}

		//create colors
		for(int key: colorComponents.keySet()){
			float[] component = colorComponents.get(key);
			long multiplicity = multiplicities.get(key);
			Color resultColor = null;
			try {
				resultColor = new Color(
						component[0]/((float)multiplicity),
						component[1]/((float)multiplicity),
						component[2]/((float)multiplicity),
						component[3]/((float)multiplicity));
			} catch (Exception e) {
				e.printStackTrace();
			}
			returnColors.put(key, resultColor);
		}

		return returnColors;
	}

	/**
	 * updates a color point
	 * @param oldPoint the current point
	 * @param newPoint the new point
	 */
	public void moveColor(Point2D.Float oldPoint, Point2D.Float newPoint) {

		Color color = colors.get(oldPoint);
		if(color == null){
			return;
		}

		if(!isPointValid(newPoint)){
			throw new IndexOutOfBoundsException("Point: "+newPoint+" was not in tf space");
		}
		//last and first point must not be altered in x
		if(oldPoint.equals(colors.firstKey()) || oldPoint.equals(colors.lastKey())){
			newPoint.x = oldPoint.x;
		}

		colors.remove(oldPoint);
		colors.put(newPoint, color);
		fireFunctionPointChangedEventAll();
	}

//	/**
//	 * Get the transfer function shader code, defined by the current classifier
//	 * @return the classification shader code
//	 */
//	public IFunction getTransferFunctionShaderCode(){
//		return sampler.getShaderCode();
//	}

//	/**
//	 * @return the sampler
//	 */
//	public ITransferFunctionSampler getSampler() {
//		return sampler;
//	}

//	/**
//	 * @param sampler the sampler to set
//	 */
//	public void setSampler(ITransferFunctionSampler sampler) {
//		this.sampler = sampler;
//		fireSamplerChangedEventAll();
//	}

	/**
	 * Calculates the draw point of a transfer function  coordinate in window space
	 * @param transferFunctionPoint the point in transfer function space
	 * @param transferFunction the transfer function
	 * @param drawAreaSize the paint area size
	 * @return the paint area point
	 */
	public static Point calculateDrawPoint(Point2D.Float transferFunctionPoint, 
			TransferFunction1D transferFunction,
			Dimension drawAreaSize){
		float xyScale[] = {(float)(drawAreaSize.getWidth()-1)/ transferFunction.getMaxOrdinates().x,
				(float)(drawAreaSize.getHeight()-1)/transferFunction.getMaxOrdinates().y};
		Point drawPoint = new Point((int)Math.round(transferFunctionPoint.getX() * xyScale[0]),
				(int)Math.round(transferFunctionPoint.getY() * xyScale[1]));
		return drawPoint;
	}

	/**
	 * Calculates the transfer function point from a given window space point 
	 * @param windowSpacePoint the paint area point
	 * @param transferFunction the transfer function
	 * @param drawAreaSize the paint area size
	 * @return the point in transfer function space
	 */
	public static Point2D.Float calculateTransferFunctionPoint(Point windowSpacePoint, 
			TransferFunction1D transferFunction,
			Dimension drawAreaSize){
		float xyScale[] = {transferFunction.getMaxOrdinates().x/(float)(drawAreaSize.getWidth()-1),
				transferFunction.getMaxOrdinates().y/(float)(drawAreaSize.getHeight()-1)};
		Point2D.Float functionPoint = new Point2D.Float((float)windowSpacePoint.getX() * xyScale[0],
				(float)windowSpacePoint.getY() * xyScale[1]);
		return functionPoint;
	}

	/**
	 * Finds the nearest transfer function point for a given point in a given distance
	 * @param p the query point
	 * @param maxDist the maximal queried distance
	 * @return the nearest point
	 */
	public Point2D.Float getNearestValidPoint(final Point2D.Float p, float maxDist){
		Point2D.Float query = null;
		TreeMap<Point2D.Float, Color> colors = getColors();
		Point2D.Float upperPoint = colors.higherKey(p);
		Point2D.Float lowerPoint = colors.lowerKey(p);

		float dists[] = {Float.MAX_VALUE,Float.MAX_VALUE};
		//valid points ?
		if(lowerPoint != null ){
			dists[0]= Math.min(dists[0],(float)p.distance(lowerPoint));
		}
		if(upperPoint != null ){
			dists[1]= Math.min(dists[1],(float)p.distance(upperPoint));
		}

		//min point ? 
		if(dists[0] < dists[1]){
			if(dists[0] <= maxDist){
				query = lowerPoint;
			}
		}else{
			if(dists[1] <= maxDist){
				query = upperPoint;
			}
		}

		return query;
	}

	/**
	 * Transforms a transfer function point to a a volume data value by scaling, since there are only maxFunctionPointSamples spaces for textures
	 * @param tfVolumeValue the transfer function value
	 * @return the corresponding volume value
	 */
	public float getDataVolumeValue(float tfVolumeValue){
		return (maxVolumeValue/maxCoordinates.x)*tfVolumeValue;
	}
	
	/**
	 * Transforms a volume data to the transfer function space by scaling, since there are only maxFunctionPointSamples spaces for textures
	 * @param dataVolumeValue the volume value
	 * @return the corresponding transfer function value
	 */
	public float getTransferFunctionVolumeValue(float dataVolumeValue){
		return (maxCoordinates.x/maxVolumeValue)*dataVolumeValue;
	}
}
