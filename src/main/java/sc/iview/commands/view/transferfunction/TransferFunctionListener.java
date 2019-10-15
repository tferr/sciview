package sc.iview.commands.view.transferfunction;

import java.util.EventListener;

/**
 * listen if a certain transfer function was altered.
 * @author michael
 *
 */
public interface TransferFunctionListener extends EventListener {

	/**
	 * Triggered by function point changes. 
	 * @param transferFunction the transfer function which point has changed
	 */
	public void functionPointChanged(final TransferFunction1D transferFunction );
	

	/**
	 * called if the sampler (pre integration or normal classifier) was altered. 
	 * The shader then needs to recompile.
	 * @param transferFunction1D the transfer function which point has classifier
	 */
	public void classifierChanged(final TransferFunction1D transferFunction1D);
}
