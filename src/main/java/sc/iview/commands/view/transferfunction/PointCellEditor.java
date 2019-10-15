package sc.iview.commands.view.transferfunction;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * JTable Editor and render class for a 2D point
 * Representation as jspinners 
 * @author michael
 *
 */
public class PointCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;
	
	private Point2D.Float currentPoint;

	private final TransferFunctionDataPanel parent;
	
	private final JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100000.0, 1));

	private final JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1, 0.1));

	private final JPanel editorPanel = new JPanel();

	/**
	 * Create the panel UI for the class
	 */
	private void buildUI(){
		editorPanel.setLayout(new BoxLayout(editorPanel, BoxLayout.X_AXIS));
		editorPanel.add(new JLabel("x:"));
		editorPanel.add(xSpinner);
		xSpinner.setPreferredSize(new Dimension(250, xSpinner.getHeight()));
		ySpinner.setPreferredSize(new Dimension(250, ySpinner.getHeight()));
		editorPanel.add(new JLabel("y:"));
		editorPanel.add(ySpinner);
	};

	/**
	 * Constructor
	 */
	public PointCellEditor(TransferFunctionDataPanel parent){
		this .parent = parent;
		buildUI();

		createControls();
	}

	/**
	 * Creates the handler for point changes 
	 */
	private void createControls() {
		xSpinner.addChangeListener(new ChangeListener() {

			/**
			 * handles changes of x values 
			 */
			@Override
			public void stateChanged(ChangeEvent e) {
				if(currentPoint == null){
					return;
				}
				currentPoint.x =parent.getTransferFunction().getTransferFunctionVolumeValue(((Number)(xSpinner.getValue())).floatValue());
				fireEditingStopped();
			}
		});

		ySpinner.addChangeListener(new ChangeListener() {
			
			/**
			 * handles changes of y values 
			 */
			@Override
			public void stateChanged(ChangeEvent e) {
				if(currentPoint == null){
					return;
				}
				currentPoint.y = ((Number)(ySpinner.getValue())).floatValue();
				fireEditingStopped();
			}
		});
	}

	/**
	 * Return the table value of the current point 
	 */
	@Override
	public Point2D.Float getCellEditorValue() {
		return currentPoint;
	}

	/**
	 * Sets spinner values and returns the panel as editor  
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		currentPoint = (Point2D.Float)value;
		xSpinner.setValue(parent.getTransferFunction().getDataVolumeValue( currentPoint.x));
		ySpinner.setValue(currentPoint.y);
		return editorPanel;
	}

	/**
	 * Sets spinner values and returns the panel as renderer  
	 */
	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object value, boolean arg2, boolean arg3, int arg4,
			int arg5) {  	       
		currentPoint = (Point2D.Float)value;
		xSpinner.setValue(parent.getTransferFunction().getDataVolumeValue(currentPoint.x));
		ySpinner.setValue(currentPoint.y);  
		return editorPanel ;
	}
}
