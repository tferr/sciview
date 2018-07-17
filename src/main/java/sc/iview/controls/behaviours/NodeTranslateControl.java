package sc.iview.controls.behaviours;

import cleargl.GLVector;
import org.scijava.ui.behaviour.DragBehaviour;
import sc.iview.SciView;

import static sun.misc.GThreadHelper.unlock;

public class NodeTranslateControl implements DragBehaviour {

    protected SciView sciView;
    private boolean firstEntered;
    private int lastX;
    private int lastY;

    public NodeTranslateControl( SciView sciView ) {
        this.sciView = sciView;
    }

    /**
     * This function is called upon mouse down and initialises the camera control
     * with the current window size.
     *
     * @param[x] x position in window
     * @param[y] y position in window
     */
    @Override public void init( int x, int y ) {
        if (firstEntered) {
            lastX = x;
            lastY = y;
            firstEntered = false;
        }
    }

    @Override public void drag( int x, int y ) {

        if( sciView.getActiveNode() == null || sciView.getActiveNode().getLock().tryLock() != true) {
            return;
        }

        float dragScale = 0.0001f;

        float[] translationVector = new float[]{ ( x - lastX ) * dragScale, ( y - lastY ) * dragScale, 0};
        sciView.getCamera().getRotation().rotateVector( translationVector, 0, translationVector, 0 );

        // TODO use an affine transform of the camera to figure out what plane to translate the node along
        sciView.getActiveNode().setPosition( sciView.getActiveNode().getPosition().plus( new GLVector( translationVector ) ) );

        sciView.getActiveNode().getLock().unlock();
    }

    @Override public void end( int x, int y ) {
        firstEntered = true;
    }
}
