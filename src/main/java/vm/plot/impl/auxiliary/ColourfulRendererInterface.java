/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package vm.plot.impl.auxiliary;

import org.jfree.chart.renderer.LookupPaintScale;

/**
 *
 * @author Vlada
 */
public interface ColourfulRendererInterface {

    public int getNumberOfColourScales();

    public LookupPaintScale getColourScale(int seriesIndex);
}
