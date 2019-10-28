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
package sc.iview;

import graphics.scenery.SceneryBase;
import io.scif.SCIFIOService;

import net.imagej.ImageJ;
import net.imagej.ImageJService;

import org.scijava.Context;
import org.scijava.display.DisplayService;
import org.scijava.log.LogService;
import org.scijava.script.ScriptService;
import org.scijava.service.SciJavaService;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;

import cleargl.GLVector;
import picocli.CommandLine;
import sun.font.Script;

import java.io.IOException;

/**
 * Entry point for testing SciView functionality.
 * 
 * @author Kyle Harrington
 */
//@CommandLine.Command(name = "sciview", mixinStandardHelpOptions = true, version = "sciview 0.2.0-beta-2-SNAPSHOT",
//         description = "SciView is an ImageJ-based tool  for visualization and interaction with ND data.")
public class Main implements Runnable {

    @CommandLine.Option(names = {"-i", "--input"}, description = "The file to view.")
    private String source = null;

    @Override
    public void run() {
        SceneryBase.xinitThreads();

        System.setProperty( "scijava.log.level:sc.iview", "debug" );
        Context context = new Context( ImageJService.class, SciJavaService.class, SCIFIOService.class, ThreadService.class, DefaultSciViewService.class, UIService.class, DisplayService.class, ScriptService.class, LogService.class, SciViewService.class );

        UIService ui = context.service( UIService.class );
        //if( !ui.isVisible() ) ui.showUI();

        SciViewService sciViewService = context.service( SciViewService.class );
        SciView sciView = sciViewService.getOrCreateActiveSciView();

        sciView.addSphere();

        if( source != null ) {
            try {
                sciView.open(source);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main( String... args ) {
        (new Main()).run();
    }
}
