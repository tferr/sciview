package sc.iview.commands.view.transferfunction;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Panel to display the names of the volumes and show the visibility of the partial volume
 * @author michael
 *
 */
public class VolumeLegend extends JPanel {

	/**
	 * default id
	 */
	private static final long serialVersionUID = 1L;

	private final VolumeDataManager dataManager;
	
	private final Map<Integer,JCheckBox> idCheckboxMap = new HashMap<Integer, JCheckBox>(); 
	
	/**
	 * Constructor
	 * @param m
	 */
	public VolumeLegend(final VolumeDataManager m){
		this.dataManager = m;
		initLegend();
		initListener();
	}
	
	/**
	 * Init data listener
	 */
	private void initListener() {
		dataManager.addVolumeDataManagerListener(new VolumeDataManagerAdapter() {
			
			/**
			 * repaint if data is added
			 */
			@Override
			public void addedData(Integer id) {
				updateLegend(id);
				repaint();
			}
		});
	}

	/**
	 * Initializes the UI
	 */
	private void initLegend() {
		setBorder(BorderFactory.createTitledBorder("Volume data Legend"));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	/**
	 * Updates UI, add Entries if they are not already present
	 * @param id
	 */
	private void updateLegend(final Integer id) {
			if(idCheckboxMap.containsKey(id)){
				return;
			}

			Color volumeColor = getColorOfVolume(id);
			VolumeDataBlock data = dataManager.getVolume(id);
			final JCheckBox tmp = new JCheckBox("Volume: "+(id+1)+" ("+data.name+")");
			tmp.setForeground(volumeColor);
			tmp.setSelected(true);
			tmp.addItemListener(new ItemListener() {
				
				/**
				 * Dis- or enables partial volume if selection of the check box changes 
				 */
				@Override
				public void itemStateChanged(ItemEvent e) {
				 	dataManager.enableVolume(id, tmp.isSelected());
					
				}
			});
			idCheckboxMap.put(id, tmp);
			add(tmp);
	}
}
