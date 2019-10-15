package sc.iview.commands.view.transferfunction;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;

/**
 * Represents the context menu of the transfer function panel and delivers standard interactions
 * @author michael
 *
 */
public class TransferFunctionContextMenu extends JPopupMenu{

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;

	private final TransferFunctionRenderPanel1D parent;

	private Point colorPickPoint = null;
	
	private TransferFunctionPointMenuActionContainer colorActions;
	
	private final MouseListener mouseListenrer = new MouseAdapter() {
		
		/**
		 * Open context menu on right button click and storing the position of the action 
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			//right button check
			if(e.getButton() != MouseEvent.BUTTON3){
				return;
			}
			colorPickPoint = new Point(transformWindowNormalSpace( e.getPoint(),parent.getSize()));
			colorActions.setInteractionPoint(colorPickPoint);
			show(parent, e.getX(), e.getY());
			e.consume();
		}
	};
	
	/**
	 * Create actions from the action container
	 */
	private void initActions(){
		
		add(colorActions.getInsertAction());
		
		add(colorActions.getSetColorAction());
		
		add(colorActions.getDeleteAction());
		
		add(colorActions.getResetAction());
	}

	/**
	 * Constructor
	 * @param parent the parent render panel to add the context menu
	 */
	public TransferFunctionContextMenu(final TransferFunctionRenderPanel1D parent){
		this.parent = parent;
		this.colorActions = new TransferFunctionPointMenuActionContainer(parent, parent.getTransferFunction()); 
		initActions();
	}
	
	/**
	 * Returns the mouse listener to adapt the context menu to a panel
	 * @return the used mouse listener
	 */
	public MouseListener getMouseListener(){
		return mouseListenrer;
	}
}
