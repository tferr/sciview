package sc.iview.commands.view.transferfunction;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;

import graphics.scenery.volumes.TransferFunction;

import static sc.iview.commands.view.transferfunction.TransferFunction1D.calculateTransferFunctionPoint;

/**
 * Container class for menu entries for manipulating transfer function points
 * @author michael
 *
 */
public class TransferFunctionPointMenuActionContainer {

	private final JComponent parent;
	
	private final TransferFunction1D tf;
	
	private Point interactionPoint;
	
	private final Action insertAction = new AbstractAction("Insert point") {

		/**
		 * default version
		 */
		private static final long serialVersionUID = 1L;
		 

		@Override
		public void actionPerformed(ActionEvent e) {
			Color color = JColorChooser.showDialog(new JFrame(), "Select color for point", Color.black);

			//nothing choosen
			if(color == null){
				return;
			}
			
			Dimension winSize = parent.getSize();
			Point2D.Float colorPoint = calculateTransferFunctionPoint(interactionPoint, tf, winSize);
			tf.setColor(colorPoint, color);
		}
	};
	
	private final Action setColorAction = new AbstractAction("Set color of point") {
		
		/**
		 * default id 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			Dimension winSize = parent.getSize();
			Point2D.Float colorPoint = tf.getNearestValidPoint(calculateTransferFunctionPoint(interactionPoint, tf, winSize),Float.MAX_VALUE);
			if(colorPoint == null){
				return;
			}
			
			Color color = JColorChooser.showDialog(new JFrame(), "Select color for point", Color.black);

			//nothing choosen
			if(color == null){
				return;
			}
			
			tf.setColor(colorPoint, color);
		}
	};
	
	private final Action resetAction = new AbstractAction("Reset points") {

		/**
		 * default version
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			tf.resetColors();
		}
	};
	
	private final Action deleteAction = new AbstractAction("Delete point"){
		/**
		 * default version
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Dimension winSize = parent.getSize();
			Point2D.Float colorPoint = tf.getNearestValidPoint(calculateTransferFunctionPoint(interactionPoint, tf, winSize),Float.MAX_VALUE);
			if(colorPoint == null){
				return;
			}
			tf.removeControlPoint(colorPoint);
		}
	};

	/**
	 * @return the interactionPoint
	 */
	public Point getInteractionPoint() {
		return interactionPoint;
	}

	/**
	 * @param interactionPoint the interactionPoint to set
	 */
	public void setInteractionPoint(Point interactionPoint) {
		this.interactionPoint = interactionPoint;
	}

	/**
	 * Constructor
	 * @param parent
	 * @param tf
	 */
	public TransferFunctionPointMenuActionContainer(JComponent parent, TransferFunction1D tf) {
		super();
		this.parent = parent;
		this.tf = tf;
	}

	/**
	 * @return the insertAction
	 */
	public Action getInsertAction() {
		return insertAction;
	}

	/**
	 * @return the resetAction
	 */
	public Action getResetAction() {
		return resetAction;
	}

	/**
	 * @return the deleteAction
	 */
	public Action getDeleteAction() {
		return deleteAction;
	}

	/**
	 * @return the setColorAction
	 */
	public Action getSetColorAction() {
		return setColorAction;
	}	
}
