/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl.auxiliary;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import vm.datatools.DataTypeConvertor;

/**
 *
 * @author Vlada
 */
public class LogScaleZAxisFormatter extends DecimalFormat {

    public LogScaleZAxisFormatter() {
        super("0");
    }
    
    

    @Override
    public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
        float f = DataTypeConvertor.doubleToPreciseFloat(number);
        f = vm.mathtools.Tools.correctPossiblyCorruptedFloat(f);
        return new StringBuffer("1e" + f);
    }

}
