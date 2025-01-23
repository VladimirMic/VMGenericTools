/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl.auxiliary;

import java.text.NumberFormat;
import java.util.Map;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author au734419
 * @param <T>
 */
public class MyStandardXYItemLabelGenerator<T> extends StandardXYItemLabelGenerator {

    private final Map<Float, T> xValueToLabel;
    private final NumberFormat nf;

    public MyStandardXYItemLabelGenerator(Map<Float, T> xValueToLabel, NumberFormat nf) {
        super();
        this.xValueToLabel = xValueToLabel;
        this.nf = nf;
    }

    @Override
    public String generateLabel(XYDataset dataset, int series, int item) {
        double x = dataset.getXValue(series, item);
        Object v = xValueToLabel.get((float) x);
        if (v == null) {
            return null;
        }
        return nf.format(v);
    }
}
