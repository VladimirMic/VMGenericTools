/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateTickUnitType;
import vm.colour.StandardColours.COLOUR_NAME;

/**
 *
 * @author au734419
 */
public class TimeSeriesLinesPlotter extends LinesOrPointsPlotter {

    public TimeSeriesLinesPlotter(DateFormat dateFormat, DateTickUnitType timeTickType, int timeUnitInterval, boolean linesVisible) {
        super(linesVisible);
        this.dateFormat = dateFormat;
        this.timeTickType = timeTickType;
        this.timeUnitInterval = timeUnitInterval;
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, COLOUR_NAME traceColour, Date[] tracesXValues, float[] tracesYValues) {
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, null, traceColour, tracesXValues, tracesYValues);
    }

    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, COLOUR_NAME traceColour, Date[][] tracesXValues, float[][] tracesYValues) {
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, null, traceColour, tracesXValues, tracesYValues);
    }

    public void setLabels(int seriesIdx, List<Float> coloursList, String coloursAxisNameOrNull) {
        pointsToLabels.put(seriesIdx, coloursList);
        coloursLabel = coloursAxisNameOrNull;
    }

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, COLOUR_NAME traceColour, Map xToYMap) {
        transformMapToArrays(xToYMap);
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, traceColour, dates, yValues);
    }

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String[] tracesName, COLOUR_NAME[] traceColours, Map[] xToYMaps) {
        Date[][] xValues = new Date[xToYMaps.length][];
        float[][] yValuesArray = new float[xToYMaps.length][];
        int i = 0;
        for (Map<Date, Float> xToYMap : xToYMaps) {
            TimeSeriesLinesPlotter.transformMapToArrays(xToYMap);
            xValues[i] = dates;
            yValuesArray[i] = yValues;
            i++;
        }
        return createPlot(mainTitle, xAxisLabel, yAxisLabel, tracesName, traceColours, xValues, yValuesArray);
    }

    private static Date[] dates;
    private static float[] yValues;

    private static void transformMapToArrays(Map<Date, Float> xToYMap) {
        dates = new Date[xToYMap.size()];
        yValues = new float[xToYMap.size()];
        int i = 0;
        for (Map.Entry entry : xToYMap.entrySet()) {
            dates[i] = (Date) entry.getKey();
            yValues[i] = (float) entry.getValue();
            i++;
        }
    }

}
