/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.awt.geom.Point2D;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateTickUnitType;
import vm.colour.StandardColours.COLOUR_NAME;
import vm.datatools.DataTypeConvertor;

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

    public void setLabels(int seriesIdx, List<Float> coloursList, String coloursAxisNameOrNull) {
        pointsToLabels.put(seriesIdx, coloursList);
        coloursLabel = coloursAxisNameOrNull;
    }
}
