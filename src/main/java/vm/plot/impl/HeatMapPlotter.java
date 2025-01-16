/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import vm.datatools.DataTypeConvertor;
import vm.mathtools.Tools;
import vm.plot.AbstractPlotter;

/**
 *
 * @author Vlada
 */
public class HeatMapPlotter extends AbstractPlotter {

    public static final Integer IMPLICIT_WIDTH_FOR_HEAT_MAP_PLOT = (int) (IMPLICIT_WIDTH * 1.5);

    public static final Integer IMPLICIT_HEIGHT_FOR_HEAT_MAP_PLOT = (int) (IMPLICIT_HEIGHT * 1.5);
    public static final Integer LEGEND_IMPLICIT_COLOUR_COUNT = 20;
    private int legendCount = LEGEND_IMPLICIT_COLOUR_COUNT;

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object... data) {
        String traceName = (String) data[0];
        float[][] values = (float[][]) data[1];
        Map<Object, Integer> yHeaders = (Map<Object, Integer>) data[2];
        Map<Object, Integer> xHeaders = (Map<Object, Integer>) data[3];
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, traceName, values, yHeaders, xHeaders);
    }

    /**
     * see @createPlot with float arrays
     *
     * @param mainTitle
     * @param xAxisLabel
     * @param yAxisLabel
     * @param traceName
     * @param values
     * @param yHeaders
     * @param xHeaders
     * @return
     */
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, int[][] values, Map<Object, Integer> yHeaders, Map<Object, Integer> xHeaders) {
        float[][] valuesFloat = DataTypeConvertor.intsArrayToFloats(values);
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, traceName, valuesFloat, yHeaders, xHeaders);
    }

    /**
     *
     * @param mainTitle title of the plot (up)
     * @param xAxisLabel xAxisLabel (can be null to hide)
     * @param yAxisLabel yAxisLabel (can be null to hide)
     * @param traceName the name of the showed data for a legend. Can be null to
     * hide.
     * @param values values to show
     * @param yHeaders mapping of tick labels shown to the second index (y)
     * in @values
     * @param xHeaders mapping of tick labels shown to the first index (x) in
     * @values
     * @return
     */
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, float[][] values, Map<Object, Integer> yHeaders, Map<Object, Integer> xHeaders) {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        int size = yHeaders.size() * xHeaders.size();
        double[] xValues = new double[size];
        double[] yValues = new double[size];
        double[] zValues = new double[size];
        double[] extremes = new double[6];
        extremes[0] = Double.MAX_VALUE;
        extremes[2] = Double.MAX_VALUE;
        extremes[4] = Double.MAX_VALUE;
        int counter = 0;
        for (Map.Entry<Object, Integer> y : yHeaders.entrySet()) {
            double yValue = Double.parseDouble(y.getKey().toString());
            int yIdx = y.getValue();
            extremes[2] = Math.min(extremes[2], yValue);
            extremes[3] = Math.max(extremes[3], yValue);
            for (Map.Entry<Object, Integer> x : xHeaders.entrySet()) {
                double xValue = Double.parseDouble(x.getKey().toString());
                int xIdx = x.getValue();
                extremes[0] = Math.min(extremes[0], xValue);
                extremes[1] = Math.max(extremes[1], xValue);
                double zValue = values[xIdx][yIdx];
                xValues[counter] = xValue;
                yValues[counter] = yValue;
                zValues[counter] = zValue;
                extremes[4] = Math.min(extremes[4], zValue);
                extremes[5] = Math.max(extremes[5], zValue);
                counter++;
            }
        }
//        extremes[1] = 60;
//        extremes[3] = 1000000;
        double[][] valuesArray = new double[][]{xValues, yValues, zValues};
        dataset.addSeries(traceName, valuesArray);
        JFreeChart ret = datasetToChart(dataset, xAxisLabel, yAxisLabel, extremes);
        return ret;
    }

    private JFreeChart datasetToChart(XYZDataset dataset, String xAxisLabel, String yAxisLabel, double[] extremes) {
        // x-axis for time
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setLowerBound(extremes[0]);
        xAxis.setUpperBound(extremes[1]);
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);

        // visible y-axis with symbols
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setLowerBound(extremes[2]);
        yAxis.setUpperBound(extremes[3]);

        // another invisible y-axis for scaling
        // (this is not necessary if your y-values are suitable) 
        NumberAxis valueAxis1 = new NumberAxis("Marker");
        valueAxis1.setLowerMargin(0);
        valueAxis1.setUpperMargin(0);
        valueAxis1.setVisible(false);

        // create a paint-scale and a legend showing it
        double minZ = extremes[4];
        double maxZ = extremes[5];
        if (minZ == maxZ) {
            maxZ += 1;
        }
        LookupPaintScale paintScale = new LookupPaintScale(minZ, maxZ, Color.black);

        PaintScaleLegend psl = new PaintScaleLegend(paintScale, new NumberAxis());
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
        psl.setMargin(50.0, 20.0, 80.0, 0.0);

        double stepDouble = setAxisUnits(null, (NumberAxis) psl.getAxis(), legendCount, false); // todo - integers?
        float step = (float) stepDouble;
        minZ = vm.mathtools.Tools.round((float) minZ, step, true) - step;
        for (int i = 0; minZ <= maxZ; i++) {
            int idx = i % COLOURS.length;
            minZ += step;
            paintScale.add(minZ, COLOURS[idx]);
            minZ += step;
            paintScale.add(minZ, LIGHT_COLOURS[idx]);
        }

        // finally a renderer and a plot
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, new XYBlockRenderer());
        XYBlockRenderer renderer = ((XYBlockRenderer) plot.getRenderer());
        renderer.setPaintScale(paintScale);
        renderer.setSeriesStroke(0, new BasicStroke(30));
        JFreeChart chart = new JFreeChart(null, null, plot, false);
        chart.addSubtitle(psl);
        setAppearence(chart, plot, xAxis, yAxis, (NumberAxis) psl.getAxis());
        return chart;
    }

    @Override
    public String getSimpleName() {
        return "HeatMap";
    }

    public int[][] transformTo2DHistogramHeatMap(List<float[]> valuesToPlot, Map<Object, Integer> emptyMapToStoreYTickLabels, Map<Object, Integer> emptyMapToStoreXTickLabels) {
        List<Float> xValues = new ArrayList<>();
        List<Float> yValues = new ArrayList<>();
        for (float[] fs : valuesToPlot) {
            xValues.add(fs[0]);
            yValues.add(fs[1]);
        }
        return transformTo2DHistogramHeatMap(xValues, yValues, emptyMapToStoreYTickLabels, emptyMapToStoreXTickLabels);
    }

    /**
     * coordinates in two separated lists, so the values xValues[i] matches
     * values yValues[i] to make a point
     *
     * @param xValues
     * @param yValues
     * @param emptyMapToStoreXTickLabels to be used as the param in createPlot
     * @param emptyMapToStoreYTickLabels to be used as the param in createPlot
     * @return
     */
    public int[][] transformTo2DHistogramHeatMap(List<Float> xValues, List<Float> yValues, Map<Object, Integer> emptyMapToStoreYTickLabels, Map<Object, Integer> emptyMapToStoreXTickLabels) {
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("Diff lists: " + xValues.size() + "; " + yValues.size());
        }
        float[] xCountStepShownMin = initHeatMapHeaders(xValues, emptyMapToStoreXTickLabels, true);
        float[] yCountStepShownMin = initHeatMapHeaders(yValues, emptyMapToStoreYTickLabels, false);
        int[][] ret = new int[(int) xCountStepShownMin[0]][(int) yCountStepShownMin[0]];
        for (int idx = 0; idx < xValues.size(); idx++) {
            Float x = xValues.get(idx);
            x = Tools.round(x, xCountStepShownMin[1], false);
            Float y = yValues.get(idx);
            y = Tools.round(y, yCountStepShownMin[1], false);
            int yInt = (int) ((y - yCountStepShownMin[2]) / yCountStepShownMin[1]);
            int xInt = (int) ((x - xCountStepShownMin[2]) / xCountStepShownMin[1]);
//            if (xInt < 0 || yInt < 0) {
//                String s = "";
//            }
            ret[xInt][yInt]++;
        }
        return ret;

    }

    private float[] initHeatMapHeaders(List<Float> valuesOnTheAxis, Map<Object, Integer> axisHeadersToFill, boolean isXAxis) {
        float max = (float) Tools.getMax(valuesOnTheAxis);
        float min = (float) Tools.getMin(valuesOnTheAxis);
        float step;
        if (isXAxis) {
            step = vm.mathtools.Tools.computeBasicXIntervalForHistogram(min, max);
        } else {
            step = vm.mathtools.Tools.computeBasicYIntervalForHistogram(min, max);
        }
        min = Tools.round(min - step, step, false);
        max = Tools.round(max + step, step, false);
        int counter = 0;
        float y = min;
        while (y <= max) {
            axisHeadersToFill.put(y, counter);
            y = Tools.round(y + step, step, false);
            counter++;
        }
        return new float[]{counter, step, min};
    }

    private JFreeChart setAppearence(JFreeChart chart, XYPlot plot, NumberAxis xAxis, NumberAxis yAxis, NumberAxis zAxis) {
        setLabelsOfAxis(xAxis);
        setLabelsOfAxis(yAxis);
        setLabelsOfAxis(zAxis);
        setChartColor(chart, plot);
        return chart;
    }

    @Override
    public void storePlotPDF(String path, JFreeChart plot, int width, int height) {
        try {
            super.storePlotPDF(path, plot, IMPLICIT_WIDTH_FOR_HEAT_MAP_PLOT, IMPLICIT_HEIGHT_FOR_HEAT_MAP_PLOT);
        } catch (Throwable e) {// ignore - just at attempt.
            Logger.getLogger(HeatMapPlotter.class.getName()).log(Level.SEVERE, "For some reason, the library cannot store heatmaps in a vector format. Storing png instead.");
            storePlotPNG(path, plot);
        }
    }

    @Override
    public void storePlotPNG(String path, JFreeChart plot) {
        storePlotPNG(path, plot, IMPLICIT_WIDTH_FOR_HEAT_MAP_PLOT, IMPLICIT_HEIGHT_FOR_HEAT_MAP_PLOT);
    }

    public void setLegendCount(int legendCount) {
        this.legendCount = legendCount;
    }

}
