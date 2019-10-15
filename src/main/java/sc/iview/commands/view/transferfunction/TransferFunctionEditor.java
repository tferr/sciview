package sc.iview.commands.view.transferfunction;

import graphics.scenery.volumes.TransferFunction;
import graphics.scenery.volumes.Volume;
import org.scijava.command.Command;
import org.scijava.command.InteractiveCommand;
import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import sc.iview.SciView;

import javax.swing.*;

import static sc.iview.commands.MenuWeights.VIEW;
import static sc.iview.commands.MenuWeights.VIEW_SET_TRANSFER_FUNCTION;

@Plugin(type = Command.class, menuRoot = "SciView", //
        menu = {@Menu(label = "View", weight = VIEW), //
                @Menu(label = "Transfer Function Editor", weight = VIEW_SET_TRANSFER_FUNCTION)})
public class TransferFunctionEditor implements Command {

    @Parameter
    private LogService logService;

    @Parameter
    private SciView sciView;

    @Parameter
    private ObjectService objectService;

//    @Parameter(label = "Target Volume")
//    private Volume volume;

    /**
     * Just open a separate Panel for the TF editor
     */
    @Override
    public void run() {
        JFrame frame = new JFrame();
        frame.setTitle("TF Editor");
        frame.setSize(640,480);
        TransferFunctionDrawPanel panel = new TransferFunctionDrawPanel(new TransferFunction1D(), objectService);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

}