/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.javatools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

/**
 *
 * @author au734419
 */
public class SVGtoPDF {

    public static final Transcoder transcoder = new PDFTranscoder();

    public static boolean transformToPdf(File f) {
        try {
            TranscoderInput transcoderInput = new TranscoderInput(new FileInputStream(f));
            String path = f.getAbsolutePath();
            if (path.toLowerCase().endsWith(".svg")) {
                path = path.substring(0, path.length() - 4);
            }
            path += ".pdf";
            TranscoderOutput transcoderOutput = new TranscoderOutput(new FileOutputStream(new File(path)));
            transcoder.transcode(transcoderInput, transcoderOutput);
            return true;
        } catch (FileNotFoundException | TranscoderException ex) {
            Logger.getLogger(SVGtoPDF.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
