/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot.impl;

import java.text.NumberFormat;
import java.util.List;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import vm.colour.StandardColours.COLOUR_NAME;

/**
 *
 * @author au734419
 */
public class BoxPlotYXHorizontalPlotter extends BoxPlotXYPlotter {

    public BoxPlotYXHorizontalPlotter() {
        super(true);
    }

    @Override
    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String[] tracesNames, COLOUR_NAME[] tracesColours, Object[] yValues, List<Float>[][] valuesYX) {
        return super.createPlot(mainTitle, xAxisLabel, yAxisLabel, tracesNames, tracesColours, yValues, valuesYX);
    }

    @Override
    protected int getNumberOrderOfShownXLabel(int numberOfXLabels) {
        int preserveEachTh = 1;
        int countShown = numberOfXLabels;
        while (countShown > yTicksCount) {
            preserveEachTh++;
            countShown = (int) Math.ceil(numberOfXLabels / ((float) preserveEachTh));
        }
        return preserveEachTh;
    }

//    @Override
//    public JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, String traceName, COLOUR_NAME traceColour, Map<Float, List<Float>> yToXListsValies) {
//        return super.createPlot(mainTitle, xAxisLabel, yAxisLabel, traceName, traceColour, yToXListsValies);
//    }

    @Override
    protected void setRotationOfXAxisCategoriesFont(CategoryAxis xAxis, Object[] groupsNames, int tracesPerGroup) {
        // this is correct
    }

    @Override
    protected void setRotationOfYAxisNumbersFont(NumberAxis axis, double step, NumberFormat nf) {
        setRotationOfValueAxisFont(axis, step, nf);
    }

}
