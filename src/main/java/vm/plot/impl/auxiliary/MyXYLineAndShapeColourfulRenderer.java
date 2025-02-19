package vm.plot.impl.auxiliary;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import vm.colour.StandardColours;

/**
 *
 * @author au734419
 */
public class MyXYLineAndShapeColourfulRenderer extends XYLineAndShapeRenderer implements ColourfulRendererInterface {

    private final TreeMap<Integer, List<Float>> pointsToLabels;
    private final TreeMap<Integer, LookupPaintScale> rainboxPaintScale;
    private final boolean logarithmic;

    public MyXYLineAndShapeColourfulRenderer() {
        this(null);
    }

    public MyXYLineAndShapeColourfulRenderer(TreeMap<Integer, List<Float>> pointsToLabels) {
        this(pointsToLabels, false);
    }

    public MyXYLineAndShapeColourfulRenderer(TreeMap<Integer, List<Float>> pointsToLabels, boolean logarithmicScale) {
        this.pointsToLabels = new TreeMap<>(pointsToLabels);
        rainboxPaintScale = new TreeMap<>();
        if (pointsToLabels != null) {
            Set<Integer> series = pointsToLabels.keySet();
            for (Integer s : series) {
                List<Float> labels = pointsToLabels.get(s);
                LookupPaintScale lookupPaintScale = StandardColours.createContinuousPaintScale(labels, logarithmicScale);
                rainboxPaintScale.put(s, lookupPaintScale);
            }
        }
        this.logarithmic = logarithmicScale;
    }

    @Override
    public Paint getItemPaint(int seriesIdx, int pointIdx) {
        if (pointsToLabels.isEmpty()) {
            return super.getItemPaint(seriesIdx, pointIdx);
        }
        List<Float> pointsToColourValue = pointsToLabels.get(seriesIdx);
        LookupPaintScale scale = rainboxPaintScale.get(seriesIdx);
        if (pointsToColourValue == null || pointsToColourValue.isEmpty() || scale == null) {
            return StandardColours.BOX_BLACK;
        }
        Float colourValue1 = pointsToColourValue.get(pointIdx);
        if (colourValue1 != null && !Float.isNaN(colourValue1)) {
            if (logarithmic && (scale.getLowerBound() > colourValue1 || scale.getUpperBound() < colourValue1)) {
                colourValue1 = (float) Math.log10(colourValue1);
            }
            return scale.getPaint(colourValue1);
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
