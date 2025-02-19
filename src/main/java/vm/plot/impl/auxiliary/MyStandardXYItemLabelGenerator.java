package vm.plot.impl.auxiliary;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;

/**
 *
 * @author au734419
 * @param <T>
 */
public class MyStandardXYItemLabelGenerator<T> extends StandardXYItemLabelGenerator {

    private final List<T> xValueToLabel;
    private final NumberFormat nf;

    public MyStandardXYItemLabelGenerator(List<T> labels, NumberFormat nf) {
        super();
        this.xValueToLabel = labels;
        this.nf = nf;
    }

    @Override
    public String generateLabel(XYDataset dataset, int series, int item) {
        Object v = xValueToLabel.get(item);
        if (v == null) {
            return null;
        }
        if (nf == null) {
            return v.toString();
        }
        return nf.format(v);
    }
}
