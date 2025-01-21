/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import vm.colour.StandardColours.COLOUR_NAME;
import vm.datatools.DataTypeConvertor;
import vm.datatools.Tools;

/**
 *
 * @author au734419
 */
public class BoxPlotXYPlotter extends BoxPlotPlotter {

    protected final boolean isHorizontal;

    protected BoxPlotXYPlotter(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    public BoxPlotXYPlotter() {
        this(false);
    }

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String[] tracesNames, COLOUR_NAME[] tracesColours, Object[] xValues, List<Float>[][] values) {
        // notice that DefaultBoxAndWhiskerXYDataset contains a single only one series in the dataset. For that reason, implemented as category dataset with fixed step
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        if (tracesNames == null) {
            tracesNames = new String[]{""};
        }
        if (tracesNames.length != values.length) {
            String s = Integer.toString(tracesNames.length);
            throw new IllegalArgumentException("Number of traces descriptions does not match the values or is null when more traces are specified: " + s + ", " + values.length);
        }
        Float[] groupNumbers = DataTypeConvertor.objectsToObjectFloats(xValues);
        float xStep = (float) vm.mathtools.Tools.gcd(groupNumbers); // achtung
        if (Float.isNaN(xStep)) {
            xStep = 1;
        }
        for (int traceID = 0; traceID < values.length; traceID++) {
            List<Float>[] valuesForGroups = values[traceID];
            if (xValues.length != valuesForGroups.length) {
                throw new IllegalArgumentException("Number of groups descriptions does not match the values" + tracesNames.length + ", " + valuesForGroups.length);
            }
            Float previousKey = null;
            Integer iValue;
            String keyString;
            for (int groupId = 0; groupId < valuesForGroups.length; groupId++) {
                List<Float> valuesForGroupAndTrace = valuesForGroups[groupId];
                Float groupName = Float.valueOf(groupNumbers[groupId].toString());
                while (previousKey != null && groupName > previousKey + xStep * 1.5f) {// othewise damages the floating point numbers
                    previousKey += xStep;
                    iValue = Tools.parseInteger(previousKey);
                    keyString = iValue == null ? previousKey.toString() : iValue.toString();
                    dataset.add(new BoxPlotPlotter.DummyBoxAndWhiskerItem(), tracesNames[traceID], keyString);
                }
                // check if it is an integer (if float than ok
                iValue = Tools.parseInteger(groupName);
                keyString = iValue == null ? groupName.toString() : iValue.toString();
                if (valuesForGroupAndTrace != null) {
                    dataset.add(valuesForGroupAndTrace, tracesNames[traceID], keyString);
                }
                previousKey = Float.valueOf(groupNumbers[groupId].toString());// othewise damages the floating point numbers
            }
        }
        JFreeChart chart;
        if (isHorizontal) {
            String stepString = ((int) xStep) == xStep ? Integer.toString((int) xStep) : Float.toString(xStep);
            chart = ChartFactory.createBoxAndWhiskerChart(mainTitle, yAxisLabel + " (width: " + stepString + ")", xAxisLabel, dataset, true);
        } else {
            chart = ChartFactory.createBoxAndWhiskerChart(mainTitle, xAxisLabel, yAxisLabel, dataset, true);
        }
        return setAppearence(chart, tracesNames, tracesColours, xValues);
    }

    @Override
    public String getSimpleName() {
        return "BoxPlotNumerical";
    }

    @Override
    protected JFreeChart setAppearence(JFreeChart chart, String[] tracesNames, COLOUR_NAME[] tracesColours, Object[] groupsNames) {
        if (isHorizontal) {
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            String label = plot.getDomainAxis().getLabel();
            plot.setDomainAxis(new MyCategoryAxis(label, getNumberOrderOfShownXLabel(groupsNames.length)));
            plot.setOrientation(PlotOrientation.HORIZONTAL);
            plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        }
        return super.setAppearence(chart, tracesNames, tracesColours, groupsNames);
    }

    @Override
    public void storePlotPDF(String path, JFreeChart plot) {
        int width;
        if (isHorizontal) {
            width = IMPLICIT_WIDTH;
        } else {
            width = precomputeSuitablePlotWidth(IMPLICIT_HEIGHT, lastTracesCount, lastGroupCount);
        }
        storePlotPDF(path, plot, width, IMPLICIT_HEIGHT);
    }

    @Override
    public void storePlotPNG(String path, JFreeChart plot) {
        int width;
        if (isHorizontal) {
            width = IMPLICIT_WIDTH;
        } else {
            width = precomputeSuitablePlotWidth(IMPLICIT_HEIGHT, lastTracesCount, lastGroupCount);
        }
        storePlotPNG(path, plot, width, IMPLICIT_HEIGHT);
    }

    protected int getNumberOrderOfShownXLabel(int numberOfXLabels) {
        return 1;
    }

    public static Map<Float, List<Float>> quantiseMapToBoxPlotValues(Map<Float, Float> xToYMap) {
        Set<Float> keySet = xToYMap.keySet();
        Float[] groupNumbers = keySet.toArray(Float[]::new);
        float xStep = (float) vm.mathtools.Tools.gcd(groupNumbers);
        Map<Float, List<Float>> ret = new TreeMap<>();
        for (Map.Entry<Float, Float> entry : xToYMap.entrySet()) {
            float key = vm.mathtools.Tools.round(entry.getKey(), xStep, false);
            if (!ret.containsKey(key)) {
                ret.put(key, new ArrayList<>());
            }
            List<Float> list = ret.get(key);
            list.add(entry.getValue());
        }
        return ret;
    }

}
