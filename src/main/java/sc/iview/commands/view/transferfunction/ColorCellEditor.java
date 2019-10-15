package sc.iview.commands.view.transferfunction;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Cell editor for color cells in JTables represented as a button
 * @author michael
 *
 */
public class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer{

	/**
	 * default version
	 */
	private static final long serialVersionUID = 1L;
	
	private Color currentColor;
	
	private JButton button;
	
	/**
	 * Constructor
	 */
	public ColorCellEditor(){
		button = new JButton();
		button.setBorderPainted(false);
		button.setBackground(Color.BLACK);
		button.addActionListener(new ActionListener() {
			
			/**
			 * change color handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				Color color = JColorChooser.showDialog(new JFrame(), "color dialog", currentColor);
				
				if(color == null){
					return;
				}
				
				currentColor = color;
				button.setBackground(currentColor);  
				fireEditingStopped(); 
			}
		});
	}
	
	/**
	 * Return latest color of a button
	 */
	@Override
	public Color getCellEditorValue() {
		return currentColor;
	}

	/**
	 * Set the color of the button and returns it as the cell editor
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentColor = (Color)value;
        button.setBackground(currentColor);
        return button;
	}

	/**
	 * Set the color of the button and returns it as the cell renderer
	 */
	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object value, boolean arg2, boolean arg3, int arg4,
			int arg5) {    
		currentColor = (Color)value;   
		button.setBackground(currentColor);
		return button;
	}
}
