package vm.plot.impl.auxiliary;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import vm.colour.StandardColours;

/**
 *
 * @author au734419
 */
public class MyXYLineAndShapeColourfulRenderer extends XYLineAndShapeRenderer {

    private final TreeMap<Integer, Map<Float, Float>> seriesToXToLabels;
    private final TreeMap<Integer, LookupPaintScale> rainboxPaintScale;

    public MyXYLineAndShapeColourfulRenderer() {
        this(null);
    }

    public MyXYLineAndShapeColourfulRenderer(TreeMap<Integer, Map<Float, Float>> seriesToXToLabels) {
        this.seriesToXToLabels = seriesToXToLabels;
        rainboxPaintScale = new TreeMap<>();
        if (seriesToXToLabels != null) {
            Set<Integer> series = seriesToXToLabels.keySet();
            for (Integer sery : series) {
                Map<Float, Float> labels = seriesToXToLabels.get(sery);
                float min = (float) vm.mathtools.Tools.getMin(labels.values());
                float max = (float) vm.mathtools.Tools.getMax(labels.values());
                LookupPaintScale lookupPaintScale = StandardColours.createRainboxPaintScale(min, max);
                rainboxPaintScale.put(sery, lookupPaintScale);
            }
        }
    }

    @Override
    public Paint getItemPaint(int seriesIdx, int pointIdx) {
        if (seriesToXToLabels.isEmpty()) {
            return StandardColours.BOX_BLACK;
        }
        Map<Float, Float> xValueToColourValue = seriesToXToLabels.get(seriesIdx);
        LookupPaintScale scale = rainboxPaintScale.get(seriesIdx);
        if (xValueToColourValue.isEmpty() || scale == null) {
            return StandardColours.BOX_BLACK;
        }
        XYDataset dataset = getPlot().getDataset();
        double x = dataset.getXValue(seriesIdx, pointIdx);
        Float colourValue = xValueToColourValue.get((float) x);
        return scale.getPaint(colourValue);
    }

    @Override
    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(StandardColours.BOX_BLACK);
        g2.draw(shape);
    }

}
