package vm.plot.impl.auxiliary;

import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.ui.GradientPaintTransformer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.category.CategoryDataset;
import vm.colour.StandardColours;

/**
 *
 * @author au734419
 */
public class MyCategoryItemRenderer extends BarRenderer implements ColourfulRendererInterface {

    private final TreeMap<Integer, List<Float>> pointsToLabels;
    private final TreeMap<Integer, LookupPaintScale> rainboxPaintScale;
    private final boolean logarithmic;
    private final boolean colouredLabelledPointsOrBars;

    public MyCategoryItemRenderer(boolean colouredLabelledPointsOrBars, TreeMap<Integer, List<Float>> coloursValues, boolean logarithmic) {
        this.colouredLabelledPointsOrBars = colouredLabelledPointsOrBars;
        this.logarithmic = logarithmic;
        this.pointsToLabels = new TreeMap<>();
        if (coloursValues != null) {
            this.pointsToLabels.putAll(coloursValues);
        }
        rainboxPaintScale = new TreeMap<>();
        if (coloursValues != null) {
            Set<Integer> series = coloursValues.keySet();
            for (Integer sery : series) {
                List<Float> labels = coloursValues.get(sery);
                LookupPaintScale lookupPaintScale = StandardColours.createContinuousPaintScale(labels, logarithmic);
                rainboxPaintScale.put(sery, lookupPaintScale);
            }
        }
    }

    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea,
            CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset,
            int row, int column, int pass) {

        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        double value = dataValue.doubleValue();

        PlotOrientation orientation = plot.getOrientation();
        double barW0 = calculateBarW0(plot, orientation, dataArea, domainAxis,
                state, row, column);
        double[] barL0L1 = calculateBarL0L1(value);
        if (barL0L1 == null) {
            return;  // the bar is not visible
        }

        RectangleEdge edge = plot.getRangeAxisEdge();
        double transL0 = rangeAxis.valueToJava2D(barL0L1[0], dataArea, edge);
        double transL1 = rangeAxis.valueToJava2D(barL0L1[1], dataArea, edge);
        double barL0 = Math.min(transL0, transL1);
        double barLength = Math.max(Math.abs(transL1 - transL0),
                getMinimumBarLength());

        // draw the bar...
        Rectangle2D bar = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            bar = new Rectangle2D.Double(barL0, barW0, barLength,
                    state.getBarWidth());
        } else {
            bar = new Rectangle2D.Double(barW0, barL0, state.getBarWidth(),
                    barLength);
        }
        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);

        // draw the outline...
        if (isDrawBarOutline()
                && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }

        CategoryItemLabelGenerator generator
                = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar,
                    (value < 0.0));
        }

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }

    }

    @Override
    public Paint getItemPaint(int seriesIdx, int pointIdx) {
        if (!colouredLabelledPointsOrBars) {
            return super.getItemPaint(seriesIdx, pointIdx);
        }
        if (pointsToLabels.isEmpty()) {
            return StandardColours.BOX_BLACK;
        }
        List<Float> pointsToColourValue = pointsToLabels.get(seriesIdx);
        LookupPaintScale scale = rainboxPaintScale.get(seriesIdx);
        if (pointsToColourValue.isEmpty() || scale == null) {
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
    public int getNumberOfColourScales() {
        return rainboxPaintScale.size();
    }

    @Override
    public LookupPaintScale getColourScale(int seriesIndex) {
        return rainboxPaintScale.get(seriesIndex);
    }

}
