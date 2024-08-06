/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.javatools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;

/**
 *
 * @author Vlada
 */
public class SymbolicLinkCreator {

    public static void main(String[] args) throws IOException {
        gui();
    }

    private static void gui() throws IOException {
        while (true) {
            String source = JOptionPane.showInputDialog("Full path of the source file?");
            File sourceF = new File(source);
            if (!sourceF.exists()) {
                throw new IllegalArgumentException("File " + source + " does not exist");
            }
            System.err.println(source);
            String link = JOptionPane.showInputDialog("Folder name to new symbolic link?");
            System.err.println(link);
            File linkF = new File(link, sourceF.getName());
            Files.createSymbolicLink(linkF.toPath(), sourceF.toPath());
        }
    }
}
