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
public class MyXYLineAndShapeColourfulRenderer extends XYLineAndShapeRenderer implements ColourfulRendererInterface {

    private final TreeMap<Integer, Map<Float, Float>> seriesToXToLabels;
    private final TreeMap<Integer, LookupPaintScale> rainboxPaintScale;
    private final boolean logarithmic;

    public MyXYLineAndShapeColourfulRenderer() {
        this(null);
    }

    public MyXYLineAndShapeColourfulRenderer(TreeMap<Integer, Map<Float, Float>> seriesToXToLabels) {
        this(seriesToXToLabels, false);
    }

    public MyXYLineAndShapeColourfulRenderer(TreeMap<Integer, Map<Float, Float>> seriesToXToLabels, boolean logarithmicScale) {
        this.seriesToXToLabels = new TreeMap<>(seriesToXToLabels);
        rainboxPaintScale = new TreeMap<>();
        if (seriesToXToLabels != null) {
            Set<Integer> series = seriesToXToLabels.keySet();
            for (Integer s : series) {
                Map<Float, Float> labels = seriesToXToLabels.get(s);
                LookupPaintScale lookupPaintScale = StandardColours.createContinuousPaintScale(labels.values(), logarithmicScale);
                rainboxPaintScale.put(s, lookupPaintScale);
            }
        }
        this.logarithmic = logarithmicScale;
    }

    @Override
    public Paint getItemPaint(int seriesIdx, int pointIdx) {
        if (seriesToXToLabels.isEmpty()) {
            return super.getItemPaint(seriesIdx, pointIdx);
        }
        Map<Float, Float> xValueToColourValue = seriesToXToLabels.get(seriesIdx);
        LookupPaintScale scale = rainboxPaintScale.get(seriesIdx);
        if (xValueToColourValue == null || xValueToColourValue.isEmpty() || scale == null) {
            return StandardColours.BOX_BLACK;
        }
        XYDataset dataset = getPlot().getDataset();
        double x = dataset.getXValue(seriesIdx, pointIdx);
        Float colourValue1 = xValueToColourValue.get((float) x);
        if (logarithmic && x != 0) {
            x = Math.log10(x);
        }
        Float colourValue2 = xValueToColourValue.get((float) x);
        if (colourValue2 == null && colourValue1 != null) {
            if (logarithmic && (scale.getLowerBound() > colourValue1 || scale.getUpperBound() < colourValue1)) {
                colourValue1 = (float) Math.log10(colourValue1);
            }
            return scale.getPaint(colourValue1);
        }
        if (colourValue2 != null) {
            return scale.getPaint(colourValue2);
        }
        return scale.getDefaultPaint();
    }

    @Override
    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(getItemPaint(series, item));
        g2.draw(shape);
    }

    @Override
    public int getNumberOfColourScales() {
        return rainboxPaintScale.size();
    }

    @Override
    public LookupPaintScale getColourScale(int seriesIndex) {
        return rainboxPaintScale.get(seriesIndex);
    }

}
