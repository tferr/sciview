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
package sc.iview.commands.demo.celltrackingchallenge;

import graphics.scenery.volumes.Volume;
import ij.IJ;
import ij.ImagePlus;
import io.scif.services.DatasetIOService;
import net.imagej.mesh.Mesh;
import net.imagej.ops.OpService;
import net.imagej.ops.geom.geom3d.mesh.BitTypeVertexInterpolator;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayRandomAccess;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelRegion;
import net.imglib2.roi.labeling.LabelRegions;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;
import org.joml.Vector3f;
import org.scijava.command.Command;
import org.scijava.command.CommandService;
import org.scijava.command.InteractiveCommand;
import org.scijava.log.LogService;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import sc.iview.SciView;
import sc.iview.process.MeshConverter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static sc.iview.commands.MenuWeights.DEMO;
import static sc.iview.commands.MenuWeights.DEMO_VOLUME_RENDER;

/**
 * Convert a mastodon track file into a directory of tiffs for comparison with Vlado's measure
 *
 * @author Kyle Harrington
 */
@Plugin(type = Command.class, label = "Convert Tracks To Tiffs", menuRoot = "SciView", //
        menu = { @Menu(label = "Demo", weight = DEMO), //
                 @Menu(label = "Cell Tracking Challenge"), //
                 @Menu(label = "Convert Tracks To Tiffs", weight = DEMO_VOLUME_RENDER) })
public class TracksAsTiffDirectory implements Command {

    @Parameter
    private LogService log;

    @Parameter
    private OpService ops;

    @Override
    public void run() {
        String baseDir = "/home/kharrington/Data/CellTrackingChallenge/VladoUlrikBT/";

        String filename = baseDir + "with_reorganized_tree.txtExportedTracks.txt";
        String outDirectory = baseDir + "track_tiff_dir";

        long[] outputDims = new long[]{700, 660, 113};
        int numTimesteps = 600;

        // End hard coded


        // ArrayImg<UnsignedShortType, ShortArray> output = ArrayImgs.unsignedShorts(outputDims[0], outputDims[1], outputDims[2], numTimesteps);// Make a single output image
        RandomAccess<UnsignedShortType> outputRA;// = output.randomAccess();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader(filename) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<Object[]> trackNodes = new ArrayList<Object[]>();

        long[] pos = new long[4];

        for( String line : reader.lines().collect(Collectors.toList()) ){

            // Skip comments
            if( line.startsWith("#") ) {
                continue;
            }

            if( line != null && line.length() > 1 ) {
                String[] parts = line.split("\t");
                //System.out.println(Arrays.toString(parts));
                int time = Integer.parseInt(parts[0]);
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                int trackId = Integer.parseInt(parts[4]);
                int parentTrackId = Integer.parseInt(parts[5]);
                String spotLabel = parts[6];

                Object[] r = new Object[]{time, x, y, z, trackId, parentTrackId, spotLabel};
                trackNodes.add(r);

//                pos[0] = Math.round(x);
//                pos[1] = Math.round(y);
//                pos[2] = Math.round(z);
//                pos[3] = time;
//
//                outputRA.setPosition(pos);
//                outputRA.get().set(trackId);
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Now save tiffs

        if( !new File(outDirectory).exists() ) {
            new File(outDirectory).mkdir();
        }

        for( int t = 0; t < numTimesteps; t++ ) {
            Img<UnsignedShortType> outFrame = ArrayImgs.unsignedShorts(outputDims[0], outputDims[1], outputDims[2]);
            outputRA = outFrame.randomAccess();

            for( Object[] trackNode : trackNodes ) {
                int time = (int) trackNode[0];
                double x = (double) trackNode[1];
                double y = (double) trackNode[2];
                double z = (double) trackNode[3];
                int trackId = (int) trackNode[4];
                int parentTrackId = (int) trackNode[5];
                String spotLabel = (String) trackNode[6];

                if( time == t ) {
                    pos[0] = Math.round(x);
                    pos[1] = Math.round(y);
                    pos[2] = Math.round(z);
                    pos[3] = time;

                    outputRA.setPosition(pos);
                    outputRA.get().set(trackId);
                }
            }

            ImagePlus imp = ImageJFunctions.wrap(outFrame, "timestep_" + t);
            //ImagePlus imp = ImageJFunctions.wrap(Views.hyperSlice(output, 3, t), "timestep_" + t);
            IJ.saveAsTiff(imp, outDirectory + "/output_" + String.format("%05d", t) + ".tif" );
        }
    }

    public static void main(String... args) throws Exception {
        SciView sv = SciView.create();

        CommandService command = sv.getScijavaContext().getService(CommandService.class);

        HashMap<String, Object> argmap = new HashMap<String, Object>();

        command.run(TracksAsTiffDirectory.class, true, argmap);
    }
}
