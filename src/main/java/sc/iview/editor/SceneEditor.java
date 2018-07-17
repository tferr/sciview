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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.miginfocom.swing.MigLayout;

import org.scijava.swing.checkboxtree.CheckBoxNodeData;
import org.scijava.swing.checkboxtree.CheckBoxNodeEditor;
import org.scijava.swing.checkboxtree.CheckBoxNodeRenderer;
import org.scijava.widget.UIComponent;

import sc.iview.SciView;

import graphics.scenery.Node;

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
        p.setLayout( new MigLayout( "", "[grow]", "[grow]" ) );

        p.add( createTree() );

        panel = p;
    }

    private JTree createTree() {
        final DefaultMutableTreeNode root = new DefaultMutableTreeNode( sciView );

        for( final Node sceneNode : sciView.getSceneNodes() ) {
            addNode( root, sceneNode );
        }

        final DefaultTreeModel treeModel = new DefaultTreeModel( root );
        final JTree tree = new JTree( treeModel );

        final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer( renderer );

        final CheckBoxNodeEditor editor = new CheckBoxNodeEditor( tree );
        tree.setCellEditor( editor );
        tree.setEditable( true );

        // listen for changes in the selection
        tree.addTreeSelectionListener( e -> {
            System.out.println( System.currentTimeMillis() + ": selection changed" );
        } );

        // listen for changes in the model (including check box toggles)
        treeModel.addTreeModelListener( new TreeModelListener() {

            @Override
            public void treeNodesChanged( final TreeModelEvent e ) {
                System.out.println( System.currentTimeMillis() + ": nodes changed" );
            }

            @Override
            public void treeNodesInserted( final TreeModelEvent e ) {
                System.out.println( System.currentTimeMillis() + ": nodes inserted" );
            }

            @Override
            public void treeNodesRemoved( final TreeModelEvent e ) {
                System.out.println( System.currentTimeMillis() + ": nodes removed" );
            }

            @Override
            public void treeStructureChanged( final TreeModelEvent e ) {
                System.out.println( System.currentTimeMillis() + ": structure changed" );
            }
        } );

        return tree;
    }

    private static DefaultMutableTreeNode addNode( final DefaultMutableTreeNode parent, final Node sceneNode ) {
        final String text = sceneNode.getName();
        final boolean checked = sceneNode.getVisible();
        final CheckBoxNodeData data = new CheckBoxNodeData( text, checked );
        final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode( data );
        parent.add( treeNode );
        return treeNode;
    }
}
