/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.javatools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.JOptionPane;

/**
 *
 * @author Vlada
 */
public class SymbolicLinkCreator {

    public static void main(String[] args) throws IOException {
        /* JOptionPane Java user input example */
        String source = JOptionPane.showInputDialog("Full path of the source file?");
        File sourceF = new File(source);
        if (!sourceF.exists()) {
            throw new IllegalArgumentException("File " + source + " does not exist");
        }
        String link = JOptionPane.showInputDialog("Full path to new symbolic link?");
        File linkF = new File(link);
        Files.createSymbolicLink(sourceF.toPath(), linkF.toPath());
    }
}
