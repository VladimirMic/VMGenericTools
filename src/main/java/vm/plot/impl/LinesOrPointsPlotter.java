/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import vm.colour.StandardColours;
import static vm.colour.StandardColours.BOX_BLACK;
import vm.colour.StandardColours.COLOUR_NAME;
import static vm.colour.StandardColours.LIGHT_BOX_BLACK;
import static vm.colour.StandardColours.getColor;
import vm.datatools.DataTypeConvertor;
import vm.datatools.Tools;
import vm.plot.AbstractPlotter;
import vm.plot.impl.auxiliary.MyBarPainter;
import vm.plot.impl.auxiliary.MyBarRenderer;
import vm.plot.impl.auxiliary.MyStandardXYItemLabelGenerator;
import vm.plot.impl.auxiliary.MyXYLineAndShapeColourfulRenderer;

/**
 *
 * @author au734419
 */
public class LinesOrPointsPlotter extends AbstractPlotter {
//plot.mapDatasetToRangeAxis(1, 1)

    private final boolean linesVisible;
    private boolean isTimeSeries;
    protected boolean colouredLabelledPointsOrBars;
    protected boolean showPointLabels;
    protected boolean logarithmicScaleOfColours;

    protected final TreeMap<Integer, Map<Float, Float>> seriesToXToLabels = new TreeMap<>();
    protected final TreeMap<Integer, NumberFormat> nfs = new TreeMap<>();

    public LinesOrPointsPlotter() {
        this(true);
    }

    public LinesOrPointsPlotter(boolean linesVisible) {
        this.linesVisible = linesVisible;
        colouredLabelledPointsOrBars = true;
        logarithmicScaleOfColours = false;
        showPointLabels = true;
    }

    public boolean isLogarithmicScaleOfColours() {
        return logarithmicScaleOfColours;
    }

    public void setLogarithmicScaleOfColours(boolean logarithmicScaleOfColours) {
        this.logarithmicScaleOfColours = logarithmicScaleOfColours;
    }

    public boolean isShowPointLabels() {
        return showPointLabels;
    }

    public void setShowPointLabels(boolean showPointLabels) {
        this.showPointLabels = showPointLabels;
    }

    public void setLabels(int seriesIdx, Map<Float, Float> mapOfXValuesToLabels, NumberFormat nf, boolean colourfulPoints) {
        seriesToXToLabels.put(seriesIdx, mapOfXValuesToLabels);
        nfs.put(seriesIdx, nf);
        colouredLabelledPointsOrBars = colourfulPoints;
    }

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object... data) {
        Object[] tracesNames = new Object[]{""};
        if (data[0] != null) {
            if (data[0] instanceof Object[]) {
                tracesNames = (Object[]) data[0];
            } else {
                tracesNames = (Object[]) DataTypeConvertor.objectToSingularArray(data[0]);
            }
        }
        COLOUR_NAME[] tracesColours = null;
        if (data[1] != null) {
            if (data[1] instanceof COLOUR_NAME[]) {
                tracesColours = (COLOUR_NAME[]) data[1];
            } else {
                tracesColours = (COLOUR_NAME[]) DataTypeConvertor.objectToSingularArray(data[1]);
            }
        }
        float[][] tracesXValues;
        if (data[2] == null) {
            float[] yValues = (float[]) data[3];
            float[] xValues = new float[yValues.length];
            data[2] = new float[yValues.length];
            for (int i = 0; i < yValues.length; i++) {
                xValues[i] = i;
            }
            data[2] = xValues;
        }
        if (data[2] instanceof float[][]) {
            tracesXValues = (float[][]) data[2];
            isTimeSeries = false;
        } else if (data[2] instanceof Date[][]) {
            long[][] tmp = DataTypeConvertor.datesArrayToLongs((Date[][]) data[2]);
            tracesXValues = DataTypeConvertor.longsArrayToFloats(tmp);
            isTimeSeries = true;
        } else if (data[2] instanceof Date[]) {
            long[] tmp = DataTypeConvertor.datesArrayToLongs((Date[]) data[2]);
            float[] tmp2 = DataTypeConvertor.longsArrayToFloats(tmp);
            tracesXValues = DataTypeConvertor.objectToSingularArray(tmp2);
            isTimeSeries = true;
        } else { // assumes float[]
            tracesXValues = (float[][]) DataTypeConvertor.objectToSingularArray(data[2]);
            isTimeSeries = false;
        }
        float[][] tracesYValues;
        if (data[3] instanceof float[][]) {
            tracesYValues = (float[][]) data[3];
        } else {
            tracesYValues = (float[][]) DataTypeConvertor.objectToSingularArray(data[3]);
        }
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, tracesNames, tracesColours, tracesXValues, tracesYValues);
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, COLOUR_NAME traceColour, float[] tracesXValues, float[] tracesYValues) {
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, null, traceColour, tracesXValues, tracesYValues);
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, COLOUR_NAME traceColour, Map<Float, Float> xToYMap) {
        float[] xValues = new float[xToYMap.size()];
        float[] yValues = new float[xToYMap.size()];
        Iterator<Map.Entry<Float, Float>> it = xToYMap.entrySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            Map.Entry<Float, Float> entry = it.next();
            xValues[i] = entry.getKey();
            yValues[i] = entry.getValue();
        }
        xValues = vm.mathtools.Tools.correctPossiblyCorruptedFloats(xValues);
        yValues = vm.mathtools.Tools.correctPossiblyCorruptedFloats(yValues);
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, traceName, traceColour, xValues, yValues);
    }

    protected JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object[] tracesNames, COLOUR_NAME[] tracesColours, float[][] tracesXValues, float[][] tracesYValues) {
        XYSeries[] traces = transformCoordinatesIntoTraces(tracesNames, tracesXValues, tracesYValues);
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries trace : traces) {
            dataset.addSeries(trace);
        }
        JFreeChart chart;
        if (isTimeSeries) {
            chart = ChartFactory.createTimeSeriesChart(mainTitle, xAxisLabel, yAxisLabel, dataset);
        } else {
            chart = ChartFactory.createXYLineChart(mainTitle, xAxisLabel, yAxisLabel, dataset);
        }
        if (logY) {
            setMinAndMaxYValues(tracesYValues);
        }
        return setAppearence(chart, traces, tracesColours, xAxisLabel, yAxisLabel);
    }

    protected XYSeries[] transformCoordinatesIntoTraces(Object[] tracesNames, float[][] tracesXValues, float[][] tracesYValues) {
        if (tracesNames == null) {
            tracesNames = new String[]{""};
        }
        if (tracesNames.length != tracesXValues.length || tracesNames.length != tracesYValues.length) {
            throw new IllegalArgumentException("Inconsistent number of traces in data. Names count: " + tracesNames.length + ", x count: " + tracesXValues.length + ", y count: " + tracesYValues.length);
        }
        XYSeries[] ret = new XYSeries[tracesNames.length];
        for (int i = 0; i < tracesNames.length; i++) {
            ret[i] = new XYSeries(tracesNames[i].toString());
            if (tracesXValues[i].length != tracesYValues[i].length) {
                throw new IllegalArgumentException("Inconsistent number of point in x and y coordinates. Trace: " + i + ", X coords: " + tracesXValues[i].length + ", y coords: " + tracesYValues[i].length);
            }
            int[] idxs = permutationOfIndexesToMakeXIncreasing(tracesXValues[i]);
            for (int idx : idxs) {
                float x = tracesXValues[i][idx];
                float y = tracesYValues[i][idx];
                x = vm.mathtools.Tools.correctPossiblyCorruptedFloat(x);
                y = vm.mathtools.Tools.correctPossiblyCorruptedFloat(y);
                ret[i].add(DataTypeConvertor.floatToPreciseDouble(x), DataTypeConvertor.floatToPreciseDouble(y));
            }
        }
        return ret;
    }

    private int[] permutationOfIndexesToMakeXIncreasing(float[] traceXValues) {
        TreeSet<AbstractMap.Entry<Integer, Float>> set = new TreeSet<>(new Tools.MapByFloatValueComparator<>());
        for (int i = 0; i < traceXValues.length; i++) {
            AbstractMap.Entry<Integer, Float> entry = new AbstractMap.SimpleEntry<>(i, traceXValues[i]);
            set.add(entry);
        }
        int[] ret = new int[traceXValues.length];
        Iterator<Map.Entry<Integer, Float>> it = set.iterator();
        for (int i = 0; i < ret.length; i++) {
            ret[i] = it.next().getKey();
        }
        return ret;
    }

    protected JFreeChart setAppearence(JFreeChart chart, XYSeries[] traces, COLOUR_NAME[] tracesColours, String xAxisLabel, String yAxisLabel) {
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
            boolean onlyIntegerYValues = isOnlyIntegerYValues(traces);
            setTicksOfYNumericAxis(yAxis, onlyIntegerYValues);
        }
        if (!seriesToXToLabels.isEmpty()) {
            plot.getRangeAxis().setUpperMargin(0.08);
            xAxis.setLowerMargin(0.06);
            xAxis.setUpperMargin(0.06);
        }
        //legend        
        setLegendFont(chart.getLegend());
        if (traces.length == 1) {
            String traceName = traces[0].getKey().toString().toLowerCase();
            if (chart.getLegend() != null && (traceName.equals(yAxisLabel.toLowerCase()) || traceName.equals("") || traceName.equals(xAxisLabel.toLowerCase()))) {
                chart.removeLegend();
            }
        }

        // set traces strokes
        XYItemRenderer renderer = plot.getRenderer();
        XYLineAndShapeRenderer lineAndShapeRenderer = null;
        XYBarRenderer barRenderer = null;
        if (renderer instanceof XYLineAndShapeRenderer) {
            lineAndShapeRenderer = new MyXYLineAndShapeColourfulRenderer(seriesToXToLabels, logarithmicScaleOfColours);
            plot.setRenderer(lineAndShapeRenderer);
            renderer = lineAndShapeRenderer;
        }
        if (renderer instanceof XYBarRenderer) {
            barRenderer = new MyBarRenderer(seriesToXToLabels, logarithmicScaleOfColours);
            plot.setRenderer(barRenderer);
            renderer = barRenderer;
        }
        AffineTransform resize = new AffineTransform();
        resize.scale(1000, 1000);
        if (barRenderer != null) {
            barRenderer.setDrawBarOutline(true); // border of the columns
            int barCount = traces[0].getItemCount();
            Logger.getLogger(LinesOrPointsPlotter.class.getName()).log(Level.INFO, "Creating bars-plot with X axis named {0} and {1} bars", new Object[]{xAxisLabel, barCount});
            barRenderer.setMargin(0);
            barRenderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL));
            barRenderer.setBarPainter(new MyBarPainter(0, 0, 0));
        }
        for (int i = 0; i < traces.length; i++) {
            if (lineAndShapeRenderer != null) {
                if (!linesVisible) {
                    lineAndShapeRenderer.setSeriesLinesVisible(i, false);
                    lineAndShapeRenderer.setSeriesItemLabelsVisible(i, true);
                }
                lineAndShapeRenderer.setSeriesShapesVisible(i, true);
            }
            setColours(renderer, traces, barRenderer, tracesColours, i);
        }
        if (renderer != null) {
            renderer.setDefaultItemLabelFont(FONT_VALUES_LABELSS);
            for (Map.Entry<Integer, Map<Float, Float>> entry : seriesToXToLabels.entrySet()) {
                int seriesIdx = entry.getKey();
                Map<Float, Float> labelsMap = entry.getValue();
                NumberFormat nf = nfs.get(seriesIdx);
                if (showPointLabels) {
                    MyStandardXYItemLabelGenerator<Float> generator = new MyStandardXYItemLabelGenerator(labelsMap, nf);
                    renderer.setSeriesItemLabelGenerator(seriesIdx, generator);
                    renderer.setSeriesItemLabelsVisible(seriesIdx, true);
                }
            }
        }
        plot.setBackgroundAlpha(0);
        plot.setRenderer(renderer);
        return chart;
    }

    private void setColours(XYItemRenderer renderer, XYSeries[] traces, XYBarRenderer barRenderer, COLOUR_NAME[] tracesColours, int i) {
        Color darkColor = tracesColours == null ? StandardColours.COLOURS[i % StandardColours.COLOURS.length] : getColor(tracesColours[i], false);
        Color lightColor = tracesColours == null ? StandardColours.LIGHT_COLOURS[i % StandardColours.LIGHT_COLOURS.length] : getColor(tracesColours[i], true);
        renderer.setSeriesOutlinePaint(i, darkColor);
        if (traces.length == 1 && barRenderer == null && tracesColours == null) {
            darkColor = BOX_BLACK;
            lightColor = LIGHT_BOX_BLACK;
        }
        if (renderer != null) {
            renderer.setSeriesStroke(i, new BasicStroke(SERIES_STROKE));
            if (barRenderer == null) {
                renderer.setSeriesPaint(i, darkColor);
            } else {
                Paint gradientPaint = new GradientPaint(0.0f, 0.0f, lightColor, Float.MAX_VALUE, Float.MAX_VALUE, lightColor); // seems to be an issue but must be like this!
                barRenderer.setSeriesPaint(i, gradientPaint);
                barRenderer.setSeriesOutlinePaint(i, darkColor);
            }
        }
    }

    @Override
    public String getSimpleName() {
        return "PlotXYLines";
    }

    private boolean isOnlyIntegerYValues(XYSeries[] traces) {
        for (XYSeries trace : traces) {
            int itemCount = trace.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                Number y = trace.getY(i);
                if (y.floatValue() != y.intValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void storeCsvRawData(String path, JFreeChart chart) {
        XYPlot plot = (XYPlot) chart.getPlot();
        XYSeriesCollection dataset = (XYSeriesCollection) plot.getDataset();
        List series = dataset.getSeries();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            for (int sIdx = 0; sIdx < series.size(); sIdx++) {
                XYSeries cast = (XYSeries) series.get(sIdx);
                List<Float> xValues = new ArrayList<>();
                List<Float> yValues = new ArrayList<>();
                List<Float> labels = new ArrayList<>();
                Map<Float, Float> labelsMap = null;
                if (seriesToXToLabels.containsKey(sIdx)) {
                    labelsMap = seriesToXToLabels.get(sIdx);
                }
                int itemCount = cast.getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    float x = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getX(i).floatValue());
                    float y = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getY(i).floatValue());
                    xValues.add(x);
                    yValues.add(y);
                    if (labelsMap != null && labelsMap.containsKey(x)) {
                        Float label = labelsMap.get(x);
                        labels.add(label);
                    }
                }
                w.write("Trace;");
                String traceName = dataset.getSeriesKey(sIdx).toString();
                if (traceName.isBlank()) {
                    traceName = plot.getRangeAxis().getLabel();
                }
                w.write(traceName + ";X:");
                for (Float xValue : xValues) {
                    w.write(";" + xValue);
                }
                w.newLine();
                w.write("Trace;");
                w.write(traceName + ";Y:");
                for (Float yValue : yValues) {
                    w.write(";" + yValue);
                }
                w.newLine();
                w.write("Labels:;");
                w.write(traceName + ";of X:");
                for (Float label : labels) {
                    w.write(";" + label);
                }
                w.newLine();
            }
            w.flush();
        } catch (IOException ex) {
            Logger.getLogger(HeatMapPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
