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
package sc.iview.commands.edit;

import static sc.iview.commands.MenuWeights.EDIT;
import static sc.iview.commands.MenuWeights.EDIT_ADD_BOX;

import org.scijava.command.Command;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;
import org.scijava.util.Colors;

import sc.iview.SciView;
import sc.iview.vector.ClearGLVector3;
import sc.iview.vector.Vector3;

@Plugin(type = Command.class, menuRoot = "SciView", //
        menu = { @Menu(label = "Scene", weight = EDIT), //
                 @Menu(label = "Add Box...", weight = EDIT_ADD_BOX) })
public class AddBox implements Command {

    @Parameter
    private DisplayService displayService;

    @Parameter
    private SciView sciView;

    @Parameter
    private float size = 1.0f;

    @Parameter
    private ColorRGB color = Colors.BLUE;

    @Parameter
    private boolean inside;

    @Override
    public void run() {
        final Vector3 pos = new ClearGLVector3( 0, 0, 0 );
        final Vector3 vSize = new ClearGLVector3( size, size, size );
        sciView.addBox( pos, vSize, color, inside );
    }
}
