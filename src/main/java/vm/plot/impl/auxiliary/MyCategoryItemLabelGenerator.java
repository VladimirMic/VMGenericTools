package vm.plot.impl.auxiliary;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;
import vm.datatools.DataTypeConvertor;

/**
 *
 * @author Vlada
 * @param <T>
 */
public class MyCategoryItemLabelGenerator<T> implements CategoryItemLabelGenerator {

    private final Map<Integer, List<Float>> labels;
    private final TreeMap<Integer, NumberFormat> nfs;

    public MyCategoryItemLabelGenerator(Map<Integer, List<Float>> labels, TreeMap<Integer, NumberFormat> nfs) {
        super();
        this.labels = labels;
        this.nfs = nfs;
    }

    @Override
    public String generateRowLabel(CategoryDataset cd, int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String generateColumnLabel(CategoryDataset cd, int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String generateLabel(CategoryDataset cd, int firstIdx, int secondIdx) {
        if (labels == null || !labels.containsKey(firstIdx)) {
            return null;
        }
        List<Float> list = labels.get(firstIdx);
        if (list == null) {
            return null;
        }
        Float v = list.get(secondIdx);
        if (v == null) {
            return null;
        }
        if (nfs.containsKey(firstIdx)) {
            return nfs.get(firstIdx).format(v);
        }
        if (v == v.intValue()) {
            return Integer.toString(v.intValue());
        }
        return v.toString();
    }
}
