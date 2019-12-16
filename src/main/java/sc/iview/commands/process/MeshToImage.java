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
package sc.iview.commands.process;

import static sc.iview.commands.MenuWeights.PROCESS;
import static sc.iview.commands.MenuWeights.PROCESS_MESH_TO_IMAGE;

import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import org.scijava.ui.UIService;
import sc.iview.SciView;
import sc.iview.ops.DefaultVoxelization3D;
import sc.iview.process.MeshConverter;

import graphics.scenery.Mesh;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Plugin(type = Command.class, menuRoot = "SciView", //
        menu = { @Menu(label = "Process", weight = PROCESS), //
                 @Menu(label = "Mesh To Image", weight = PROCESS_MESH_TO_IMAGE) })
public class MeshToImage implements Command {

    @Parameter
    private OpService ops;

    @Parameter
    private LogService log;

    @Parameter
    private SciView sciView;

    @Parameter
    private int width;

    @Parameter
    private int height;

    @Parameter
    private int depth;

    @Parameter(type = ItemIO.OUTPUT)
    private RandomAccessibleInterval<BitType> img;

    @Parameter
    private Mesh mesh;

    @Override
    public void run() {
        net.imagej.mesh.Mesh ijMesh = MeshConverter.toImageJ( mesh );

        //img = ops.geom().voxelization( ijMesh, width, height, depth );
        CommandService command = sciView.getScijavaContext().service(CommandService.class);
        HashMap<String, Object> argmap = new HashMap<String, Object>();
        argmap.put("width", width);
        argmap.put("height", height);
        argmap.put("depth", depth);
        argmap.put("mesh", ijMesh);
        command.run(DefaultVoxelization3D.class, true, argmap);
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        String filename = "/home/kharrington/Data/Voxelization/sphere25mm/Sphere_diameter_25mm.stl";
        //String filename = "/home/kharrington/Data/Voxelization/pipeBubble/pipebuble2.stl";

        SciView sv = SciView.createSciView();
        IOService io = sv.getScijavaContext().service(IOService.class);
        CommandService command = sv.getScijavaContext().service(CommandService.class);
        UIService ui = sv.getScijavaContext().service(UIService.class);

        Object data = io.open( filename );
        Mesh mesh = (Mesh) sv.addMesh((net.imagej.mesh.Mesh)data);
        HashMap<String, Object> argmap = new HashMap<String, Object>();
        argmap.put("mesh", mesh);
        argmap.put("width", 100);
        argmap.put("height", 100);
        argmap.put("depth", 100);

        Future<CommandModule> result = command.run(MeshToImage.class, true, argmap);
        RandomAccessibleInterval<BitType> img = (RandomAccessibleInterval<BitType>) result.get().getOutput("img");

        ui.showUI();
        ui.show(img);

        //org.joml.Intersectionf.testLineSegmentTriangle()
        // check testLineSegmentTriangle
    }
}
