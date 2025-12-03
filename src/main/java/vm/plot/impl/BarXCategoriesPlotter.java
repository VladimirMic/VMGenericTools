package vm.plot.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.data.xy.CategoryTableXYDataset;
import org.jfree.data.xy.XYSeries;
import vm.colour.StandardColours;
import static vm.colour.StandardColours.BOX_BLACK;
import static vm.colour.StandardColours.LIGHT_BOX_BLACK;
import static vm.colour.StandardColours.getColor;
import vm.plot.AbstractPlotter;
import static vm.plot.AbstractPlotter.FONT_VALUES_LABELSS;
import static vm.plot.AbstractPlotter.SERIES_STROKE;
import vm.plot.impl.auxiliary.MyBarPainter;
import vm.plot.impl.auxiliary.MyBarRenderer;

/**
 *
 * @author au734419
 */
public class BarXCategoriesPlotter extends AbstractPlotter {

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object... data) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object[] tracesNames, StandardColours.COLOUR_NAME[] tracesColours, Object[] groupsNames, Map<String, Float>[] map) {
        CategoryTableXYDataset dataset = new CategoryTableXYDataset();
        float minY = Float.MAX_VALUE;
        for (int i = 0; i < groupsNames.length; i++) {
            for (int j = 0; j < tracesNames.length; j++) {
                String tracesName = tracesNames[j].toString();
                Float v = map[i].get(tracesName);
                dataset.add(i * (tracesNames.length + 2) + j, v, tracesName);
                minY = Math.min(minY, v);
            }
        }
        updateMinRecall(minY);
        JFreeChart chart = ChartFactory.createXYBarChart(mainTitle, xAxisLabel, false, yAxisLabel, dataset);
        return setAppearence(chart, tracesColours, groupsNames, xAxisLabel, yAxisLabel);
    }

    @Override
    public String getSimpleName() {
        return "BarXCategoriesPlot";
    }

    @Override
    protected void storeCsvRawData(String path, JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();
        CategoryTableXYDataset dataset = (CategoryTableXYDataset) plot.getDataset();
//        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
//            for (int sIdx = 0; sIdx < series.size(); sIdx++) {
//                XYSeries cast = (XYSeries) series.get(sIdx);
//                List<Float> xValues = new ArrayList<>();
//                List<Float> yValues = new ArrayList<>();
//                List<Float> labels = new ArrayList<>();
//                List<Float> labelsMap = null;
//                int itemCount = cast.getItemCount();
//                for (int i = 0; i < itemCount; i++) {
//                    float x = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getX(i).floatValue());
//                    float y = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getY(i).floatValue());
//                    xValues.add(x);
//                    yValues.add(y);
//                    if (labelsMap != null) {
//                        Float label = labelsMap.get(i);
//                        labels.add(label);
//                    }
//                }
//                w.write("Trace;");
//                String traceName = dataset.getSeriesKey(sIdx).toString();
//                if (traceName.isBlank()) {
//                    traceName = plot.getRangeAxis().getLabel();
//                }
//                w.write(traceName + ";X:");
//                for (Float xValue : xValues) {
//                    w.write(";" + xValue);
//                }
//                w.newLine();
//                w.write("Trace;");
//                w.write(traceName + ";Y:");
//                for (Float yValue : yValues) {
//                    w.write(";" + yValue);
//                }
//                w.newLine();
//                w.write("Labels:;");
//                w.write(traceName + ";of X:");
//                for (Float label : labels) {
//                    w.write(";" + label);
//                }
//                w.newLine();
//            }
//            w.flush();
//        } catch (IOException ex) {
//            Logger.getLogger(HeatMapPlotter.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    protected JFreeChart setAppearence(JFreeChart chart, StandardColours.COLOUR_NAME[] tracesColours, Object[] groupsNames, String xAxisLabel, String yAxisLabel) {
        XYPlot plot = (XYPlot) chart.getPlot();
        // chart colours
        setChartColorAndTitleFont(chart, plot);

        // x axis settings
        ValueAxis xAxis = plot.getDomainAxis();
        setTicksOfXNumericAxis(xAxis);
        setLabelsOfAxis(xAxis);
        // y axis settings
        if (logY) {
            LogAxis yAxis = new LogAxis();
            setLabelsOfAxis(yAxis);
            yAxis.setAutoRange(true);
            yAxis.setSmallestValue(minMaxY[0]);
            plot.setRangeAxis(yAxis);
        } else {
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            setLabelsOfAxis(yAxis);
            boolean onlyIntegerYValues = false; // create method
            setTicksOfYNumericAxis(yAxis, onlyIntegerYValues);
        }
        //legend        
        setLegendFont(chart.getLegend());
//        if (traces.length == 1) {
//            String traceName = traces[0].getKey().toString().toLowerCase();
//            if (chart.getLegend() != null && (traceName.equals(yAxisLabel.toLowerCase()) || traceName.equals("") || traceName.equals(xAxisLabel.toLowerCase()))) {
//                chart.removeLegend();
//            }
//        }
        // set traces strokes
        XYBarRenderer barRenderer = new MyBarRenderer(false, null, false);
        plot.setRenderer(barRenderer);
        XYItemRenderer renderer = barRenderer;

        AffineTransform resize = new AffineTransform();
        resize.scale(1000, 1000);
        barRenderer.setDrawBarOutline(true); // border of the columns
        int barCount = groupsNames.length; //?
        Logger.getLogger(LinesOrPointsPlotter.class.getName()).log(Level.INFO, "Creating bars-plot with X axis named {0} and {1} bars", new Object[]{xAxisLabel, barCount});
        barRenderer.setMargin(0);
        barRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
        barRenderer.setBarPainter(new MyBarPainter(0, 0, 0));
        for (int i = 0; i < groupsNames.length; i++) {
            setColours(renderer, barRenderer, tracesColours, i);
        }
        if (renderer != null) {
            renderer.setDefaultItemLabelFont(FONT_VALUES_LABELSS);
//            for (Map.Entry<Integer, List<Float>> entry : pointsToLabels.entrySet()) {
//                int seriesIdx = entry.getKey();
//                List<Float> labels = entry.getValue();
//                NumberFormat nf = nfs.get(seriesIdx);
//            }
        }
        plot.setBackgroundAlpha(0);
        plot.setRenderer(renderer);
        return chart;
    }

    private void setColours(XYItemRenderer renderer, XYBarRenderer barRenderer, StandardColours.COLOUR_NAME[] tracesColours, int i) {
        Color darkColor = tracesColours == null ? StandardColours.COLOURS[i % StandardColours.COLOURS.length] : getColor(tracesColours[i], false);
        Color lightColor = tracesColours == null ? StandardColours.LIGHT_COLOURS[i % StandardColours.LIGHT_COLOURS.length] : getColor(tracesColours[i], true);
        if (renderer != null) {
            renderer.setSeriesOutlinePaint(i, darkColor);
            renderer.setSeriesStroke(i, new BasicStroke(SERIES_STROKE));
            if (barRenderer == null) {
                renderer.setSeriesPaint(i, darkColor);
            } else {
                Paint gradientPaint = new GradientPaint(0.0f, 0.0f, darkColor, Float.MAX_VALUE, Float.MAX_VALUE, lightColor); // seems to be an issue but must be like this!
                barRenderer.setSeriesPaint(i, gradientPaint);
                barRenderer.setSeriesOutlinePaint(i, darkColor);
            }
        }
    }

}
