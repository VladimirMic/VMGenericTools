/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import vm.colour.StandardColours;
import vm.colour.StandardColours.COLOUR_NAME;
import static vm.colour.StandardColours.getColor;
import vm.datatools.DataTypeConvertor;
import vm.plot.AbstractPlotter;
import vm.plot.impl.auxiliary.MyBoxAndWhiskerRenderer;

/**
 *
 * @author au734419
 */
public class BoxPlotXValuesPlotter extends AbstractPlotter {

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object... data) {
        if (this instanceof BoxPlotXCategoryPlotter && data.length >= 3
                && (data[0] == null || data[0] instanceof String)
                && (data[1] == null || data[1] instanceof COLOUR_NAME)
                && data[2] instanceof Map) {
            Map map = (Map) data[2];
            Iterator<Map.Entry> it = map.entrySet().iterator();
            BoxPlotXCategoryPlotter xy = (BoxPlotXCategoryPlotter) this;
            if (it.hasNext()) {
                Object v = it.next().getValue();
                if (!(v instanceof Collection)) {
                    Map quantisedMap = xy.quantiseMapToBoxPlotValues(map);
                    return xy.createPlot(mainTitle, xAxisLabel, yAxisLabel, (String) data[0], (COLOUR_NAME) data[1], quantisedMap);
                }
            }
            return xy.createPlot(mainTitle, xAxisLabel, yAxisLabel, (String) data[0], (COLOUR_NAME) data[1], (Map) data[2]);
        } else {
            String[] tracesNames = (String[]) data[0];
            COLOUR_NAME[] tracesColours = (COLOUR_NAME[]) data[1];
            Object[] groupsNames = (Object[]) data[2];
            List<Float>[][] values = (List<Float>[][]) data[3];
            return createPlot(mainTitle, xAxisLabel, yAxisLabel, tracesNames, tracesColours, groupsNames, values);
        }
    }

    // never tested - check!
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, COLOUR_NAME traceColour, Object[] groupsNames, List<Float>[] values) {
        String[] tracesNames = DataTypeConvertor.objectToSingularArray(traceName);
        COLOUR_NAME[] tracesColours = DataTypeConvertor.objectToSingularArray(traceColour);
        List<Float>[][] valuesArray = DataTypeConvertor.objectToSingularArray(values);
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, tracesNames, tracesColours, groupsNames, valuesArray);
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String[] tracesNames, COLOUR_NAME[] tracesColours, Object[] groupsNames, List<Float>[][] values) {
        try {
            groupsNames = vm.mathtools.Tools.correctPossiblyCorruptedFloats(DataTypeConvertor.objectsToObjectFloats(groupsNames));
        } catch (java.lang.Exception e) {
// ignore
        }
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        if (tracesNames == null) {
            tracesNames = new String[]{""};
        }
        if (tracesNames.length != values.length) {
            String s = Integer.toString(tracesNames.length);
            throw new IllegalArgumentException("Number of traces descriptions does not match the values or is null when more traces are specified: " + s + ", " + values.length);
        }
        if (tracesNames.length != values.length) {
            throw new IllegalArgumentException("Number of traces descriptions does not match the values" + tracesNames.length + ", " + values.length);
        }
        for (int traceID = 0; traceID < values.length; traceID++) {
            List<Float>[] valuesForGroups = values[traceID];
            boolean error = groupsNames == null && valuesForGroups.length != 1;
            error = error || (groupsNames != null && groupsNames.length != valuesForGroups.length);
            if (error) {
                throw new IllegalArgumentException("Number of groups descriptions does not match the values" + tracesNames.length + ", " + valuesForGroups.length);
            }
            for (int groupId = 0; groupId < valuesForGroups.length; groupId++) {
                List<Float> valuesForGroupAndTrace = valuesForGroups[groupId];
                String groupName = groupsNames == null ? "" : groupsNames[groupId].toString();
                if (valuesForGroupAndTrace != null && !valuesForGroupAndTrace.isEmpty()) {
                    dataset.add(valuesForGroupAndTrace, tracesNames[traceID], groupName);
                    BoxAndWhiskerItem item = dataset.getItem(traceID, groupId);
                    float mean = item.getMean().floatValue();
                    LOG.log(Level.INFO, "Mean {3} for {0} in group {1}: {2}", new Object[]{tracesNames[traceID], groupName, mean, yAxisLabel});
                } else {
                    List<Float> atrapa = new ArrayList<>();
                    atrapa.add(0f);
                    dataset.add(atrapa, tracesNames[traceID], groupName);
                }
            }
        }
        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(mainTitle, xAxisLabel, yAxisLabel, dataset, true);
        return setAppearence(chart, tracesNames, tracesColours, groupsNames);
    }

    // never tested - check!
    public <T> JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, COLOUR_NAME traceColour, Map<T, List<Float>> xToYValues) {
        T[] xValues = DataTypeConvertor.collectionToArray(xToYValues.keySet());
        List<Float>[] retArray = new List[xValues.length];
        for (int i = 0; i < xValues.length; i++) {
            T key = xValues[i];
            retArray[i] = xToYValues.get(key);
        }
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, traceName, traceColour, xValues, retArray);
    }

    protected int precomputeSuitablePlotWidth(int height, int tracesCount, int groupsCount) {
        int tracesTotalCount = tracesCount * groupsCount;
        double usedWidth = 28 * tracesTotalCount + 30 * (tracesTotalCount - 1);
        if (groupsCount > 1) {
            usedWidth += 45 * groupsCount + 160;
        } else {
            usedWidth += 70;
        }
        float ratio = height / 500f;
        return (int) (ratio * usedWidth);
    }

    @Override
    public void storePlotPDF(String path, JFreeChart plot) {
        int width = precomputeSuitablePlotWidth(IMPLICIT_HEIGHT, lastTracesCount, lastGroupCount);
        storePlotPDF(path, plot, width, IMPLICIT_HEIGHT);
    }

    @Override
    public void storePlotPNG(String path, JFreeChart plot) {
        int width = precomputeSuitablePlotWidth(IMPLICIT_HEIGHT, lastTracesCount, lastGroupCount);
        storePlotPNG(path, plot, width, IMPLICIT_HEIGHT);
    }

    protected int lastTracesCount;
    protected int lastGroupCount;

    protected JFreeChart setAppearence(JFreeChart chart, String[] tracesNames, COLOUR_NAME[] tracesColours, Object[] groupsNames) {
        lastTracesCount = tracesNames.length;
        lastGroupCount = groupsNames == null ? 1 : groupsNames.length;

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        // chart colours
        setChartColorAndTitleFont(chart, plot);

        // y axis settings
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset) plot.getDataset();
        double axisBound = yAxis.getUpperBound();
        double dataBound = dataset.getRangeUpperBound(true);
        double range = yAxis.getUpperBound() - yAxis.getLowerBound();
        if (axisBound < dataBound) {
            yAxis.setUpperBound(dataBound + 0.03 * range);
        }
        axisBound = yAxis.getLowerBound();
        dataBound = dataset.getRangeLowerBound(true);
        if (axisBound > dataBound) {
            yAxis.setLowerBound(dataBound + 0.03 * range);
        }
        setLabelsOfAxis(yAxis);
        setTicksOfYNumericAxis(yAxis, false); // todo integers

        BoxAndWhiskerRenderer renderer = new MyBoxAndWhiskerRenderer();
        renderer.setMaxOutlierVisible(true);
        renderer.setMinOutlierVisible(true);
        // x axis settings
        CategoryAxis xAxis = plot.getDomainAxis();
        setLabelsOfAxis(xAxis);
        setRotationOfXAxisCategoriesFont(xAxis, groupsNames, tracesNames.length);
        if (groupsNames == null || groupsNames.length <= 1) {
            xAxis.setTickLabelsVisible(false);
            xAxis.setAxisLineVisible(false);
            xAxis.setTickMarksVisible(false);
            xAxis.setTickMarkInsideLength(0);
            xAxis.setTickMarkOutsideLength(0);
            xAxis.setTickMarksVisible(false);
        }
        int length = groupsNames == null ? 1 : groupsNames.length;
        setSpacingOfCategoriesAndTraces(plot, renderer, xAxis, tracesNames.length, length);
        //legend        
        setLegendFont(chart.getLegend());
        if (tracesNames.length == 1 && (tracesNames[0] == null
                || tracesNames[0].isBlank()
                || tracesNames[0].equals(chart.getTitle().getText())
                || tracesNames[0].equals(yAxis.getLabel())
                || tracesNames[0].equals(xAxis.getLabel()))) {
            chart.removeLegend();
        }

        // set traces strokes
        for (int i = 0; i < tracesNames.length; i++) {
            renderer.setSeriesStroke(i, new BasicStroke(SERIES_STROKE));
            Color darkColor = tracesColours == null ? StandardColours.COLOURS[i % StandardColours.COLOURS.length] : getColor(tracesColours[i], false);
            Color lightColor = tracesColours == null ? StandardColours.LIGHT_COLOURS[i % StandardColours.LIGHT_COLOURS.length] : getColor(tracesColours[i], true);
//            if (tracesNames.length > 1) {
            renderer.setSeriesPaint(i, lightColor);
            renderer.setSeriesOutlinePaint(i, darkColor);
//            } else {
//                renderer.setSeriesPaint(i, LIGHT_BOX_BLACK);
//                renderer.setSeriesOutlinePaint(i, BOX_BLACK);
//            }
            renderer.setSeriesOutlineStroke(i, new BasicStroke(3));
            renderer.setSeriesStroke(i, new BasicStroke(3));
        }
        renderer.setUseOutlinePaintForWhiskers(true);
        renderer.setMaxOutlierVisible(false);
        renderer.setMinOutlierVisible(false);
        plot.setBackgroundAlpha(0);
        plot.setRenderer(renderer);
        return chart;
    }

    @Override
    public String getSimpleName() {
        return "BoxPlotCat";
    }

    @Override
    protected void storeCsvRawData(String path, JFreeChart plot) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public static class DummyBoxAndWhiskerItem extends BoxAndWhiskerItem {

        public DummyBoxAndWhiskerItem() {
            super(null, null, null, null, null, null, null, null, Collections.EMPTY_LIST);
        }

    }
}
