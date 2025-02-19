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
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.GradientPaintTransformType;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.StandardGradientPaintTransformer;
import org.jfree.data.xy.XYDataItem;
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
import vm.plot.impl.auxiliary.ColourfulRendererInterface;
import vm.plot.impl.auxiliary.LogScaleZAxisFormatter;

/**
 *
 * @author au734419
 */
public class LinesOrPointsPlotter extends AbstractPlotter {

    private final boolean linesVisible;
    private boolean isTimeSeries;
    protected boolean colouredLabelledPointsOrBars;
    protected boolean showPointLabels;
    protected boolean logarithmicScaleOfColours;

    protected final TreeMap<Integer, List<Float>> pointsToLabels = new TreeMap<>();
    protected String coloursLabel = null;
    protected final TreeMap<Integer, NumberFormat> nfs = new TreeMap<>();

    public LinesOrPointsPlotter(boolean linesVisible, boolean colouredLabelledPointsOrBars) {
        this.linesVisible = linesVisible;
        this.colouredLabelledPointsOrBars = colouredLabelledPointsOrBars;
        logarithmicScaleOfColours = false;
        showPointLabels = true;
    }

    public LinesOrPointsPlotter(boolean linesVisible) {
        this(linesVisible, false);
    }

    public LinesOrPointsPlotter() {
        this(true, false);
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

    public void setLabelsAndPointColours(int seriesIdx, List<Float> labelsAndcoloursList, String coloursAxisNameOrNull) {
        if (pointsToLabels.containsKey(seriesIdx)) {
            pointsToLabels.remove(pointsToLabels);
        }
        pointsToLabels.put(seriesIdx, labelsAndcoloursList);
        coloursLabel = coloursAxisNameOrNull;
    }

    public void setNumberFormatForTraceLabel(int seriesIdx, NumberFormat nf) {
        if (nfs.containsKey(seriesIdx)) {
            nfs.remove(seriesIdx);
        }
        nfs.put(seriesIdx, nf);
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
            tracesXValues = DataTypeConvertor.datesArrayToFloats((Date[][]) data[2]);
            isTimeSeries = true;
        } else if (data[2] instanceof Date[]) {
            float[] tmp2 = DataTypeConvertor.datesArrayToFloats((Date[]) data[2]);
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
        if (!pointsToLabels.isEmpty()) {
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
        boolean deleteSeriesToXToLabels = false;
        if (renderer instanceof XYLineAndShapeRenderer) {
            deleteSeriesToXToLabels = checkPointLabelsForColours(traces, yAxisLabel);
            lineAndShapeRenderer = new MyXYLineAndShapeColourfulRenderer(pointsToLabels, logarithmicScaleOfColours);
            plot.setRenderer(lineAndShapeRenderer);
            renderer = lineAndShapeRenderer;
        }
        if (renderer instanceof XYBarRenderer) {
            deleteSeriesToXToLabels = checkPointLabelsForColours(traces, yAxisLabel);
            barRenderer = new MyBarRenderer(colouredLabelledPointsOrBars, pointsToLabels, logarithmicScaleOfColours);
            plot.setRenderer(barRenderer);
            renderer = barRenderer;
        }

        // legend of colours
        if (colouredLabelledPointsOrBars) {
            ColourfulRendererInterface cRend = (ColourfulRendererInterface) renderer;
            int numberOfColourScales = cRend.getNumberOfColourScales();
            for (int i = 0; i < numberOfColourScales; i++) {
                List<Float> labels = pointsToLabels.get(i);
                TreeSet<Float> colourValues = new TreeSet<>(labels);
                LookupPaintScale paintScale = cRend.getColourScale(i);
                NumberAxis zAxis = logarithmicScaleOfColours ? new NumberAxis(coloursLabel) : new NumberAxis(coloursLabel);
                setLabelsOfAxis(zAxis);
                if (logarithmicScaleOfColours && colourValues.contains(0f)) {
                    colourValues.remove(0f);
                }
                if (colourValues.isEmpty()) {
                    continue;
                }
                double minZ = colourValues.first();
                double maxZ = colourValues.last();

                if (logarithmicScaleOfColours) {
                    minZ = Math.log10(minZ);
                    maxZ = Math.log10(maxZ);
                    zAxis.setNumberFormatOverride(new LogScaleZAxisFormatter());
                }
                zAxis.setLowerMargin(0);
                zAxis.setUpperMargin(0);
                zAxis.setLowerBound(minZ);
                zAxis.setUpperBound(maxZ);
                PaintScaleLegend psl = new PaintScaleLegend(paintScale, zAxis);
                psl.setPosition(RectangleEdge.RIGHT);
                psl.setAxisLocation(AxisLocation.TOP_OR_RIGHT);
                psl.setMargin(25.0, 20.0, 25.0, 0.0);
                double debug = setAxisUnits(null, psl.getAxis(), StandardColours.RAINBOW_COLOURS.length - 2, false); // todo - integers?
                chart.addSubtitle(psl);
            }
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
            for (Map.Entry<Integer, List<Float>> entry : pointsToLabels.entrySet()) {
                int seriesIdx = entry.getKey();
                List<Float> labels = entry.getValue();
                NumberFormat nf = nfs.get(seriesIdx);
                if (showPointLabels) {
                    MyStandardXYItemLabelGenerator<Float> generator = new MyStandardXYItemLabelGenerator(labels, nf);
                    renderer.setSeriesItemLabelGenerator(seriesIdx, generator);
                    renderer.setSeriesItemLabelsVisible(seriesIdx, true);
                }
            }
        }
        plot.setBackgroundAlpha(0);
        plot.setRenderer(renderer);
        if (deleteSeriesToXToLabels) {
            pointsToLabels.clear();
        }
        return chart;
    }

    public boolean isColouredLabelledPointsOrBars() {
        return colouredLabelledPointsOrBars;
    }

    public void setColouredLabelledPointsOrBars(boolean colouredLabelledPointsOrBars) {
        this.colouredLabelledPointsOrBars = colouredLabelledPointsOrBars;
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
                Paint gradientPaint = new GradientPaint(0.0f, 0.0f, darkColor, Float.MAX_VALUE, Float.MAX_VALUE, lightColor); // seems to be an issue but must be like this!
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
                List<Float> labelsMap = null;
                if (pointsToLabels.containsKey(sIdx)) {
                    labelsMap = pointsToLabels.get(sIdx);
                }
                int itemCount = cast.getItemCount();
                for (int i = 0; i < itemCount; i++) {
                    float x = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getX(i).floatValue());
                    float y = vm.mathtools.Tools.correctPossiblyCorruptedFloat(cast.getY(i).floatValue());
                    xValues.add(x);
                    yValues.add(y);
                    if (labelsMap != null) {
                        Float label = labelsMap.get(i);
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

    private boolean checkPointLabelsForColours(XYSeries[] traces, String yAxisLabel) {
        boolean ret = pointsToLabels.isEmpty() && colouredLabelledPointsOrBars;
        if (ret) {
            coloursLabel = yAxisLabel;
            for (int i = 0; i < traces.length; i++) {
                XYSeries trace = traces[i];
                List<Float> listOfColours = new ArrayList<>();
                int itemCount = trace.getItemCount();
                for (int j = 0; j < itemCount; j++) {
                    XYDataItem dataItem = trace.getDataItem(j);
                    listOfColours.add(dataItem.getY().floatValue());
                }
                pointsToLabels.put(i, listOfColours);
            }
        }
        return ret;
    }

}
