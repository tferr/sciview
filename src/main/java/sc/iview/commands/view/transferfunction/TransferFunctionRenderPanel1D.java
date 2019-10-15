package sc.iview.commands.view.transferfunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JPanel;

import org.scijava.event.EventHandler;
import org.scijava.object.ObjectService;
import sc.iview.event.NodeAddedEvent;
import sc.iview.event.NodeChangedEvent;
import sc.iview.event.NodeRemovedEvent;

import static sc.iview.commands.view.transferfunction.TransferFunction1D.calculateDrawPoint;
import static sc.iview.commands.view.transferfunction.WindowUtils.transformWindowNormalSpace;

/**
 * Transfer function interaction panel similar to paraview
 * @author michael
 *
 */
public class TransferFunctionRenderPanel1D extends JPanel {

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;
	
	private ObjectService objectService = null;

	private final TransferFunctionContextMenu contextMenue;

	private final TransferFunctionPointInteractor pointInteractor;

	private TransferFunction1D transferFunction;
	
	private final int pointRadius = 10;

	private boolean logscale = true;

	/**
	 * Adds all control listeners
	 */
	private void addControls(){
		addMouseListener(contextMenue.getMouseListener());

		addMouseMotionListener(pointInteractor.getMouseMotionListener());

		addMouseListener(pointInteractor.getMouseListener());
	}
	
	/**
	 * constructor
	 * @param tf the transfer function to use
	 * @param objectService the data manager to use
	 */
	public TransferFunctionRenderPanel1D(final TransferFunction1D tf, final ObjectService objectService){

		initWindow();
		setTransferFunction(tf);
		pointInteractor = new TransferFunctionPointInteractor(this);
		setObjectService(objectService);
		
		//resizeHandler = new TransferFunctionWindowResizeHandler(getSize(),transferFunction);
		contextMenue = new TransferFunctionContextMenu(this);
		addControls();
	}

	/**
	 * Initializes UI 
	 */
	private void initWindow() {
		setSize(640, 100);
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		//setMaximumSize(getSize());
	}

	
	/**
	 * @return the transferFunction
	 */
	public TransferFunction1D getTransferFunction() {
		return transferFunction;
	}

	/**
	 * @param transferFunction the transferFunction to set
	 */
	public void setTransferFunction(TransferFunction1D transferFunction) {
		this.transferFunction = transferFunction;
		this.transferFunction.addTransferFunctionListener(new TransferFunctionAdapter() {
			
			@Override
			public void functionPointChanged(TransferFunction1D transferFunction) {
				repaint();
			}
		});
	}

	/**
	 * @return true if the volume distribution is drawn logarithmic and false if not
	 */
	public boolean isLogscaleDistribution() {
		return logscale;
	}

	/**
	 * Defines whether the distributions of volume values in the panel should be drawn in log scale
	 * @param logscale the log scale flag
	 */
	public void setLogscaleDistribution(boolean logscale) {
		this.logscale = logscale;
		repaint();
	}

	/**
	 * @return the data manager which is currently in use
	 */
	public ObjectService getObjectService() {
		return objectService;
	}

	/**
	 * Set a new data manager to use
	 * @param objectService the object service
	 */
	public void setObjectService(ObjectService objectService) {
		this.objectService = objectService;
		objectService.eventService().subscribe(this);
	}

	@EventHandler
    protected void onNodeAdded(NodeAddedEvent event) {
        repaint();
    }

    @EventHandler
    protected void onNodeRemoved(NodeRemovedEvent event) {
        repaint();
    }

    @EventHandler
    protected void onNodeChanged(NodeChangedEvent event) {
    	repaint();
    }

	/**
	 * Paints the color gradients
	 * @param g the painter of the panel
	 */
	private void paintSkala(Graphics g){
		//paint gradient image
		//error check
		TreeMap<Point2D.Float, Color> colors = transferFunction.getColors();
		if(colors.size() < 2){
			return;
		}

		//get painter
		Graphics2D painter = (Graphics2D) g;


		Point2D.Float latestPoint = colors.firstKey();
		for(Point2D.Float currentPoint: colors.keySet()){
			//skip first iteration
			if(currentPoint.equals( latestPoint)){
				continue;
			}
			Point beginGradient = transformWindowNormalSpace(calculateDrawPoint(latestPoint,transferFunction,getSize()), getSize());
			Point endGradient = transformWindowNormalSpace(calculateDrawPoint(currentPoint,transferFunction,getSize()), getSize());
			
			beginGradient.setLocation(beginGradient.x, 0);
			endGradient.setLocation(endGradient.x, 0);
			//gradient
			GradientPaint gradient = new GradientPaint(
					beginGradient, colors.get(latestPoint),
					endGradient, colors.get(currentPoint));


			//draw gradient
			painter.setPaint(gradient);
			painter.fillRect(beginGradient.x, 0, 
					endGradient.x, getHeight());
			latestPoint = currentPoint;
		}
	}
	
	/**
	 * Draw a transfer function point 
	 * @param painter the painter of the panel
	 * @param point the point center in panel space
	 */
	private void drawPointIcon(Graphics2D painter, final Point point){

		painter.setStroke(new BasicStroke(3));
		painter.drawOval(point.x-pointRadius, point.y-pointRadius, 
				pointRadius*2, pointRadius*2);
	}

	/**
	 * Draw line segments representing the transfer function
	 * @param g  the painter of the panel
	 */
	private void paintLines(Graphics g){
		TreeMap<Point2D.Float,Color> functionPoints = transferFunction.getColors();
		if(functionPoints.size() < 2){
			return;
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		//draw line and points
		Point2D.Float latestRenderedPoint = functionPoints.firstKey();
		for( Point2D.Float currentPoint: functionPoints.keySet()){
		
			//skip first point to get a valid line
			if(!currentPoint.equals( latestRenderedPoint) ){
				Point a = transformWindowNormalSpace(calculateDrawPoint(latestRenderedPoint,transferFunction,getSize()), getSize());
				Point b = transformWindowNormalSpace(calculateDrawPoint(currentPoint,transferFunction,getSize()), getSize());
				
				//print line
				g2d.setStroke(new BasicStroke(5));
				g2d.drawLine(a.x, 
						a.y,
						b.x, 
						b.y);
			}
			latestRenderedPoint = currentPoint;
		}
	}

	/**
	 * Draws the control points of the transfer function 
	 * @param g the painter of the panel
	 */
	private void paintPoints(Graphics g){
		//print points	
		Graphics2D g2d = (Graphics2D) g;	
		
		for( Point2D.Float currentPoint: transferFunction.getColors().keySet()){
			
			//highlight currently selected (dragged) point
			if(currentPoint.equals(pointInteractor.getSelectedPoint())){
				g2d.setColor(Color.gray);
			}
			
			Point drawPoint = transformWindowNormalSpace(calculateDrawPoint(currentPoint,transferFunction,getSize()), getSize());
			drawPointIcon(g2d, drawPoint);
			g2d.setColor(Color.black);
		}	
	}

	/**
	 * Paint the value distribution on the panel
	 * @param g the painter of the panel
	 */
	private void paintDistributions(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		// TODO: make a new version of this
//		Set<Integer> volumeKeys = volumeDataManager.getVolumeKeys();
//		
//		float maxYValue =volumeDataManager.getGlobalMaxOccurance();
//		float maxXValue = volumeDataManager.getGlobalMaxVolumeValue();
//		if(logscale){
//			maxYValue = (float) Math.log10(maxYValue);
//		}
//		float xyScale [] = new float[]{
//				(float)getWidth()/maxXValue,
//				(float)getHeight()/maxYValue
//		};
//
//		//iterate volumes to draw
//		for(Integer i : volumeKeys){
//			Color volumeColor = getColorOfVolume(i);
//			VolumeDataBlock data = volumeDataManager.getVolume(i);
//			TreeMap<Float,Integer> distribution = data.getValueDistribution();
//
//			//sample
//			for(Float volume: distribution.keySet()){
//				Integer occurance = distribution.get(volume);
//				float coord[]={volume,occurance};
//				if(logscale ){
//					coord[1] = (float) Math.log10(coord[1]);
//				}
//					for(int j =0; j< coord.length;j++){
//						coord[j]*=xyScale[j];
//					}
//
//				Point drawPoint = new Point((int)coord[0],(int)coord[1]);
//				drawPoint = transformWindowNormalSpace(drawPoint, getSize());
//				g2d.setColor(volumeColor);
//				g2d.drawRect(drawPoint.x, drawPoint.y, 1,1);
//			}
//		}
	} 
	
	/**
	 * Draw panel as overlapping layers: lowest Colors then Volume distribution then Function line then Controle points 
	 * @param g the painter of the panel
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		paintSkala(g);
		
		paintDistributions(g);
		
		paintLines(g);
		
		paintPoints(g);
	}
}
