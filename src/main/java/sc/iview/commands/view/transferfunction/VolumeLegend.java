package sc.iview.commands.view.transferfunction;

import graphics.scenery.Node;
import graphics.scenery.volumes.Volume;
import org.scijava.event.EventHandler;
import org.scijava.object.ObjectService;
import sc.iview.event.NodeAddedEvent;

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

	private final ObjectService objectService;

	private final Map<Volume,JCheckBox> idCheckboxMap = new HashMap<>();
	
	/**
	 * Constructor
	 * @param objectService
	 */
	public VolumeLegend(final ObjectService objectService){
		this.objectService = objectService;
		initLegend();
		initListener();
	}
	
	/**
	 * Init data listener
	 */
	private void initListener() {
		objectService.eventService().subscribe(this);
	}

	@EventHandler
    protected void onNodeAdded(NodeAddedEvent event) {
		if( event.getNode() instanceof Volume ) {
			updateLegend((Volume) event.getNode());
			repaint();
		}
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
	 * @param volume
	 */
	private void updateLegend(final Volume volume) {
			if(idCheckboxMap.containsKey(volume)){
				return;
			}

			Color volumeColor = Color.RED;// TODO: keep some list like the previous version did

			final JCheckBox tmp = new JCheckBox("Volume "+volume.getName()+": ");
			tmp.setForeground(volumeColor);
			tmp.setSelected(true);
			idCheckboxMap.put(volume, tmp);
			add(tmp);
	}
}
