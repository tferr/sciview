package sc.iview.commands.view.transferfunction;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;

/**
 * Delivers drag point interactions and double click events on the transfer function panel
 * @author michael
 *
 */
public class TransferFunctionPointInteractor {

	private final TransferFunctionPointMenuActionContainer colorActions;

	private final TransferFunctionRenderPanel1D parent;

	private Point2D.Float selectedPoint = null;

	private final MouseListener mouseListener = new MouseAdapter() {

		/**
		 * Stop dragging and release drag point
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			if(e.getButton() != MouseEvent.BUTTON1){
				return;
			}
			selectedPoint = null;
			e.consume();
		}

		/**
		 * Start dragging and initializes drag point on left button click
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			//drag point
			if(e.getClickCount() != 1||e.getButton() != MouseEvent.BUTTON1){
				return;
			}

			TransferFunction1D tf = parent.getTransferFunction();
			Dimension size = parent.getSize();
			Point windowPoint = transformWindowNormalSpace( e.getPoint(), size);
			selectedPoint = tf.getNearestValidPoint(calculateTransferFunctionPoint(windowPoint, tf, size),Float.MAX_VALUE);
		}

		/**
		 * Inserts transfer function point on left button double click
		 */
		@Override
		public void mouseClicked(MouseEvent e) {

			//insert new point
			if(e.getClickCount() ==2 && e.getButton() == MouseEvent.BUTTON1){
				Dimension size = parent.getSize();
				Point windowPoint = transformWindowNormalSpace(e.getPoint(), size);
				colorActions.setInteractionPoint(windowPoint);
				colorActions.getInsertAction().actionPerformed(null);
				e.consume();
			}
		}
	};

	private final MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {

		/**
		 * Drags transfer function point on the panel.
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			Point query = e.getPoint();
			Rectangle drawArea = parent.getVisibleRect();

			//ensure drag point is in the panel area
			query.setLocation(Math.min(drawArea.x + drawArea.getWidth()-1.0,Math.max(drawArea.x, query.getX())),
					Math.min(drawArea.y + drawArea.getHeight()-1.0,Math.max(drawArea.y, query.getY())));

			//valid drag configuration?
			if(selectedPoint == null && e.getButton() != MouseEvent.BUTTON1){
				return;
			}			

			Dimension size = parent.getSize();
			TransferFunction1D tf = parent.getTransferFunction();
			Point2D.Float oldPoint = selectedPoint;
			Point windowPoint = transformWindowNormalSpace(query, parent.getSize());
			Point2D.Float newPoint = calculateTransferFunctionPoint(windowPoint, tf, size);

			parent.getTransferFunction().moveColor(oldPoint,newPoint);
			selectedPoint = newPoint;
			e.consume();
		}
	}; 

	/**
	 * @return the selectedPoint in transfer function space
	 */
	public Point2D.Float getSelectedPoint() {
		if(null == selectedPoint){
			return null;
		}
		return new Point2D.Float(selectedPoint.x,selectedPoint.y);
	}

	/**
	 * @return the mouseListener
	 */
	public MouseListener getMouseListener() {
		return mouseListener;
	}

	/**
	 * @return the mouseMotionListener
	 */
	public MouseMotionListener getMouseMotionListener() {
		return mouseMotionListener;
	}

	/**
	 * Constructor
	 * @param parent
	 */
	public TransferFunctionPointInteractor(final TransferFunctionRenderPanel1D parent){
		this.parent = parent;
		this.colorActions = new TransferFunctionPointMenuActionContainer(parent, parent.getTransferFunction());
	}
}
