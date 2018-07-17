/*-
 * #%L
 * Scenery-backed 3D visualization package for ImageJ.
 * %%
 * Copyright (C) 2016 - 2018 SciView developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package sc.iview.editor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.scijava.widget.UIComponent;

import sc.iview.SciView;

/**
 * Interactive UI for visualizing and editing the scene graph.
 *
 * @author Curtis Rueden
 */
public class SceneEditor implements UIComponent<JPanel> {

    private final SciView sciView;
    private JPanel panel;

    public SceneEditor( final SciView sciView ) {
        this.sciView = sciView;
        sciView.getScijavaContext().inject( this );
    }

    /** Creates and displays a window containing the scene editor. */
    public void show() {
        final JFrame frame = new JFrame( "Scene Editor" );
        frame.setContentPane( getComponent() );
        // FIXME: Why doesn't the frame disappear when closed?
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }

    @Override
    public JPanel getComponent() {
        if( panel == null ) initPanel();
        return panel;
    }

    @Override
    public Class<JPanel> getComponentType() {
        return JPanel.class;
    }

    /** Initializes {@link #panel}. */
    private synchronized void initPanel() {
        if( panel != null ) return;
        final JPanel p = new JPanel();
        p.setLayout(new MigLayout("", "[grow]", "[grow]"));

        p.add( new JButton("Hello world") );
        p.add( new JButton( sciView.getName() ) );

        panel = p;
    }
}
