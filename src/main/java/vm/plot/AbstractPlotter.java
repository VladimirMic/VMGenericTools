/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.CompactNumberFormat;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import vm.datatools.DataTypeConvertor;
import vm.javatools.SVGtoPDF;
import vm.mathtools.Tools;

/**
 *
 * @author au734419
 */
public abstract class AbstractPlotter {

    public static final Logger LOG = Logger.getLogger(AbstractPlotter.class.getName());

    protected static final Integer FONT_SIZE_AXIS_LABEL = 28;
    protected static final Integer FONT_SIZE_AXIS_TICKS = FONT_SIZE_AXIS_LABEL;
    protected static final Integer FONT_SIZE_VALUES_LABELS = 20;

    protected static final Integer X_TICKS_IMPLICIT_COUNT_FOR_SHORT_DESC = 12;
    protected static final Integer X_TICKS_IMPLICIT_COUNT_FOR_LONG_DESC = 8;
    // 9 is too much // possibly create map for different lengths of descriptions. 8 for length 5

    public static final Integer Y_TICKS_IMPLICIT_NUMBER = 14;

    public static final Float SERIES_STROKE = 2f;
    public static final Float GRID_STROKE = 0.6f;

    public static final Integer IMPLICIT_WIDTH = 600;
    public static final Integer IMPLICIT_HEIGHT = 600;

    public static final Font FONT_AXIS_TITLE = new Font("Arial", Font.PLAIN, FONT_SIZE_AXIS_LABEL);
    public static final Font FONT_AXIS_MARKERS = new Font("Arial", Font.PLAIN, FONT_SIZE_AXIS_TICKS);
    public static final Font FONT_VALUES_LABELSS = new Font("Arial", Font.PLAIN, FONT_SIZE_VALUES_LABELS);

    public static final Stroke DASHED_STROKE = new BasicStroke(GRID_STROKE, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3, 3}, 0);
    public static final Stroke FULL_STROKE = new BasicStroke(GRID_STROKE);

    private int xTicksForShort = X_TICKS_IMPLICIT_COUNT_FOR_SHORT_DESC;

    protected boolean logY = false;
    protected boolean includeZeroForYAxis = false;
    protected boolean verticalXLabels = false;
    protected boolean forceHorizontalXLabels = false;

    private Boolean includeZeroForXAxis = null;

    protected DateFormat dateFormat = null;
    protected DateTickUnitType timeTickType = null;
    protected Integer timeUnitInterval = null;
    protected Integer yTicksCount = Y_TICKS_IMPLICIT_NUMBER;
    protected boolean xThousandDelimit = true;
    protected boolean yThousandDelimit = true;
    private float[] yBounds = null;
    private float[] xBounds = null;
    private Double yStep = null;
    protected Double xStep = null;
    protected boolean showLegend = true;

    public void setxThousandDelimit(boolean xThousandDelimit) {
        this.xThousandDelimit = xThousandDelimit;
    }

    public void setxTicksMaxCountForShort(int xTicksForShort) {
        this.xTicksForShort = xTicksForShort;
    }

    public void setyThousandDelimit(boolean yThousandDelimit) {
        this.yThousandDelimit = yThousandDelimit;
    }

    public void setIncludeZeroForXAxis(Boolean includeZeroForXAxis) {
        this.includeZeroForXAxis = includeZeroForXAxis;
    }

    public Integer getyTicksCount() {
        return yTicksCount;
    }

    public void setyTicksCount(Integer yTicksCount) {
        this.yTicksCount = yTicksCount;
    }

    public void setLogY(boolean logY) {
        this.logY = logY;
    }

    public void setVerticalXLabels(boolean verticalXLabels) {
        this.verticalXLabels = verticalXLabels;
    }

    public boolean isForceHorizontalXLabels() {
        return forceHorizontalXLabels;
    }

    public void setForceHorizontalXLabels(boolean forceHorizontalXLabels) {
        this.forceHorizontalXLabels = forceHorizontalXLabels;
        verticalXLabels = false;
    }

    public void setIncludeZeroForYAxis(boolean includeZeroForYAxis) {
        this.includeZeroForYAxis = includeZeroForYAxis;
    }

    public abstract JFreeChart createPlot(String mainTitle, String xAxisLabel, String yAxisLabel, Object... data);

    public abstract String getSimpleName();

    public void setYBounds(float min, float max) {
        yBounds = new float[]{min, max};
    }

    public void setYStep(Double yStep) {
        this.yStep = yStep;
    }

    public void setXStep(Double xStep) {
        this.xStep = xStep;
    }

    protected double setAxisUnits(Double step, ValueAxis axis, Integer axisImplicitTicksNumber, boolean forceIntegerStep) {
        if (step == null && axisImplicitTicksNumber == null) {
            throw new IllegalArgumentException("At least one param, step and axisImplicitTicksNumber must be non-null");
        }
        if (step == null) {
            double diff = Math.abs(axis.getUpperBound() - axis.getLowerBound());
            if (Double.isInfinite(diff)) {
                Logger.getLogger(AbstractPlotter.class.getName()).log(Level.SEVERE, "Infinite diff on axis: {0}, {1}", new Object[]{axis.getUpperBound(), axis.getLowerBound()});
            }
            float division = (float) (diff / axisImplicitTicksNumber);
            step = Tools.getNiceStepForAxis(division);
            LOG.log(Level.INFO, "The step for the axis is set to {0}", step);
        }
        if (forceIntegerStep) {
            step = Math.ceil(step);
        }
        if (step == 0) {
            step = Math.abs(axis.getUpperBound() - axis.getLowerBound());
        }
        TickUnits tickUnits = new TickUnits();
        NumberTickUnit tickUnitNumber = new NumberTickUnit(step);
        tickUnits.add(tickUnitNumber);
        axis.setStandardTickUnits(tickUnits);
        if (axis instanceof NumberAxis) {
            NumberAxis a = (NumberAxis) axis;
            a.setTickUnit(tickUnitNumber);
        }
        if (axis instanceof LogAxis) {
            LogAxis a = (LogAxis) axis;
            a.setTickUnit(tickUnitNumber);
        }
        return step;
    }

    private int getMaxTickLabelLength(double lb, double ubShown, Double xStep, NumberFormat nf) {
        double ubShownCopy = ubShown;
        int ret = 0;
        while (ubShownCopy > lb) {
            int length = nf.format(ubShownCopy).length();
            ret = Math.max(ret, length);
            ubShownCopy -= xStep;
        }
        LOG.log(Level.INFO, "Max tickLength: {0} for ubShown {1} and step {2}", new Object[]{ret, ubShown, xStep});
        return ret;
    }

    public void storePlotPDF(File file, JFreeChart plot) {
        storePlotPDF(file.getAbsolutePath(), plot);
    }

    public void storePlotPNG(File file, JFreeChart plot) {
        storePlotPNG(file.getAbsolutePath(), plot);
    }

    public void storePlotPDF(String path, JFreeChart plot) {
        storePlotPDF(path, plot, IMPLICIT_WIDTH, IMPLICIT_HEIGHT);
    }

    public final void storePlotPDF(String path, JFreeChart plot, int width, int height) {
        setMarginsForPaintScaleLegend(plot, height);
        if (!path.endsWith(".svg")) {
            path += ".svg";
        }
        SVGGraphics2D g2 = new SVGGraphics2D(width, height);
        Rectangle r = new Rectangle(0, 0, width, height);
        plot.draw(g2, r);
        File f = new File(path);
        f.getParentFile().mkdirs();
        try {
            LOG.log(Level.INFO, "Storing plot {0}", f);
            SVGUtils.writeToSVG(f, g2.getSVGElement());
            LOG.log(Level.INFO, "Transforming svg to pdf");
            boolean transformToPdf = SVGtoPDF.transformToPdf(f);
            if (transformToPdf) {
                f.delete();
            }
        } catch (IOException ex) {
            Logger.getLogger(AbstractPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String pathToCSV = deriveCSVPath(path);
        if ((lastStoredPlotPath == null || !pathToCSV.equals(lastStoredPlotPath))
                && (lastStoredPlot == null || lastStoredPlot != plot)) {
            Logger.getLogger(AbstractPlotter.class.getName()).log(Level.INFO, "Storing raw plot data to {0}", path);
            storeCsvRawData(pathToCSV, plot);
            lastStoredPlotPath = pathToCSV;
            lastStoredPlot = plot;
        }
    }
    private String lastStoredPlotPath = null;
    private JFreeChart lastStoredPlot = null;

    public void storePlotPNG(String path, JFreeChart plot) {
        storePlotPNG(path, plot, IMPLICIT_WIDTH, IMPLICIT_HEIGHT);
    }

    public void storePlotPNG(File file, JFreeChart plot, int width, int height) {
        storePlotPNG(file.getAbsolutePath(), plot, width, height);
    }

    public void storePlotPDF(File file, JFreeChart plot, int width, int height) {
        storePlotPDF(file.getAbsolutePath(), plot, width, height);
    }

    public final void storePlotPNG(String path, JFreeChart plot, int width, int height) {
        setMarginsForPaintScaleLegend(plot, height);
        if (!path.endsWith(".png")) {
            path += ".png";
        }
        File file = new File(path);
        File parentFile = file.getParentFile();
        String name = file.getName();
        file = new File(parentFile, "png");
        file.mkdirs();
        file = new File(file, name);
        try {
            LOG.log(Level.INFO, "Storing plot to {0}", path);
            ChartUtils.saveChartAsPNG(file, plot, width, height);
        } catch (IOException ex) {
            Logger.getLogger(AbstractPlotter.class.getName()).log(Level.SEVERE, null, ex);
        }
        String pathToCSV = deriveCSVPath(path);
        if ((lastStoredPlotPath == null || !pathToCSV.equals(lastStoredPlotPath))
                && (lastStoredPlot == null || lastStoredPlot != plot)) {
            Logger.getLogger(AbstractPlotter.class.getName()).log(Level.INFO, "Storing raw plat data to {0}", path);
            storeCsvRawData(pathToCSV, plot);
            lastStoredPlotPath = pathToCSV;
            lastStoredPlot = plot;
        }
    }

    private String deriveCSVPath(String path) {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent.getName().equals("png")) {
            parent = parent.getParentFile();
        }
        parent = new File(parent, "csv");
        parent.mkdirs();
        String name = file.getName();
        if (name.toLowerCase().endsWith(".pdf") || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".svg")) {
            name = name.substring(0, name.length() - 4);
        }
        File ret = new File(parent, name + ".csv");
        return ret.getAbsolutePath();
    }

    protected void printXCSVHeader(float xMin, float xStep, double xMax, BufferedWriter w) throws IOException {
        float x = xMin;
        while (x <= xMax) {
            w.write(";" + Float.toString(x));
            x = Tools.correctPossiblyCorruptedFloat(x + xStep);
        }
        w.newLine();
    }

    protected abstract void storeCsvRawData(String path, JFreeChart plot);

    public void setXBounds(float min, float max) {
        xBounds = new float[]{min, max};
    }

    protected void setTicksOfXNumericAxis(ValueAxis xAxis) {
        if (xBounds != null) {
            xAxis.setUpperBound(xBounds[1]);
            if (!includeZeroForYAxis) {
                xAxis.setLowerBound(xBounds[0]);
            } else {
                xAxis.setLowerBound(xBounds[0]);
            }
        }
        Boolean includeZeroForXAxisLocal = includeZeroForXAxis;
        try {
            boolean coversZero = xAxis.getLowerBound() <= 0 && xAxis.getUpperBound() >= 0;
            if (coversZero) {
                includeZeroForXAxisLocal = true;
            } else if (includeZeroForXAxisLocal == null && xBounds == null) {
                LOG.log(Level.WARNING, "Asking for involving zero to x axis");
                Object[] options = new String[]{"Yes", "No"};
                String question = "Do you want to involve ZERO to the X axis for all the plots being produced?";
                int add = JOptionPane.showOptionDialog(null, question, "Involve zero to the axis?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, JOptionPane.NO_OPTION);
                includeZeroForXAxisLocal = add == 0;
            } else if (xBounds != null) {
                includeZeroForXAxisLocal = false;
            }
        } catch (Throwable e) {
            includeZeroForXAxisLocal = true;
        }
        if (xAxis instanceof NumberAxis) {
            setTicksForXNumberAxis((NumberAxis) xAxis, includeZeroForXAxisLocal);
        }
        if (xAxis instanceof DateAxis) {
            setTicksForDateAxis((DateAxis) xAxis);
        }
    }

    private void setTicksForDateAxis(DateAxis xAxis) {
        xAxis.setDateFormatOverride(dateFormat);
        xAxis.setTickMarkPosition(DateTickMarkPosition.START);
        xAxis.setTickUnit(new DateTickUnit(timeTickType, timeUnitInterval, dateFormat));
        xAxis.setVerticalTickLabels(verticalXLabels);
    }

    protected void setRotationOfYAxisNumbersFont(NumberAxis axis, double step, NumberFormat nf) {
        // intentional
    }

    protected void setRotationOfValueAxisFont(ValueAxis axis, double step, NumberFormat nf) {
        int maxLength = 0;
        Double lowerBound = axis.getLowerBound();
        double upperBound = axis.getUpperBound();
        float curr = Tools.round(lowerBound.floatValue(), Float.parseFloat(Double.toString(step)), false);
        int counter = 0;
        while (curr < upperBound && counter < 10000) {
            maxLength = Math.max(maxLength, nf.format(curr).length());
            curr += step;
            counter++;
        }
        if (maxLength >= 3) {
            axis.setVerticalTickLabels(true);
        }
    }

    private void setTicksForXNumberAxis(NumberAxis xAxis, Boolean includeZeroForXAxisLocal) {
        xAxis.setAutoRangeIncludesZero(includeZeroForXAxisLocal);
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        nf.setGroupingUsed(xThousandDelimit);
        Double xStepNew = setAxisUnits(xStep, xAxis, xTicksForShort, false);
        if (xStepNew >= 120) {
            NumberFormat nfBig = new CompactNumberFormat(
                    "#,##0.##",
                    DecimalFormatSymbols.getInstance(Locale.US),
                    new String[]{"", "", "", "0K", "00K", "000K", "0M", "00M", "000M", "0B", "00B", "000B", "0T", "00T", "000T"});
            nf = nfBig;
            try {
                setDecimalsForShortExpressionOfYTicks(nf, xStepNew, xAxis);
            } catch (ParseException ex) {
                Logger.getLogger(AbstractPlotter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        xAxis.setNumberFormatOverride(nf);

        double ubShown = calculateHighestVisibleTickValue(xAxis);
        double lb = xAxis.getLowerBound();
        int maxTickLength = getMaxTickLabelLength(lb, ubShown, xStepNew, nf);
        if (maxTickLength >= 4) {
            setAxisUnits(null, xAxis, X_TICKS_IMPLICIT_COUNT_FOR_LONG_DESC, false);
        }
        setRotationOfValueAxisFont(xAxis, xStepNew, nf);
    }

    protected void setRotationOfXAxisCategoriesFont(CategoryAxis xAxis, Object[] groupsNames, int tracesPerGroup) {
        int maxLength = 0;
        if (groupsNames != null) {
            for (Object groupName : groupsNames) {
                maxLength = Math.max(maxLength, groupName.toString().length());
            }
        }
        if (forceHorizontalXLabels) {
            xAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
        } else if (verticalXLabels) {
            xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        } else if (maxLength >= 3 * tracesPerGroup) {
            xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }
    }

    protected void setSpacingOfCategoriesAndTraces(CategoryPlot plot, BoxAndWhiskerRenderer renderer, CategoryAxis xAxis, int tracesPerGroupCount, int groupCount) {
        if (groupCount == 1) {
            renderer.setItemMargin(0.3);
            xAxis.setCategoryMargin(0);
        }
        xAxis.setLowerMargin(0);
        xAxis.setUpperMargin(0);
//        int tracesTotalCount = tracesPerGroupCount * groupCount;
//        double edgeMarging = 1f / (4 * tracesTotalCount);
//        xAxis.setLowerMargin(edgeMarging);
//        xAxis.setUpperMargin(edgeMarging);
    }

    private float minRecall = 0.9f;

    public void updateMinRecall(float minRecall) {
        minRecall = Tools.round(minRecall, 0.1f, true);
        this.minRecall = Math.min(minRecall, this.minRecall);
    }

    protected void setTicksOfYNumericAxis(NumberAxis yAxis, boolean forceIntegers) {
        String label = yAxis.getLabel();
        if (label == null) {
            label = "NULL";
        } else {
            label = label.toLowerCase().trim();
        }
        if (includeZeroForYAxis) {
            yAxis.setLowerBound(0);
        }
        if ("".equals(label)) {
            yAxis.setTickLabelsVisible(false);
            yAxis.setTickMarksVisible(false);
            return;
        }
        if (label.equals("recall") || label.equals("precision") || label.equals("accuracy")) {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            yAxis.setNumberFormatOverride(nf);
            yAxis.setUpperBound(1);
            if (!includeZeroForYAxis) {
                yAxis.setLowerBound(minRecall);
            }
            setAxisUnits(yStep, yAxis, yTicksCount, forceIntegers);
            return;
        }
        if (yBounds != null) {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            yAxis.setNumberFormatOverride(nf);
            yAxis.setUpperBound(yBounds[1]);
            if (!includeZeroForYAxis) {
                yAxis.setLowerBound(yBounds[0]);
            }
            setAxisUnits(yStep, yAxis, yTicksCount, forceIntegers);
            return;
        }

        if (label.equals("frr") || label.equals("false reject rate")) {
            yAxis.setUpperBound(1 - minRecall);
        }
        double yStep = setAxisUnits(null, yAxis, yTicksCount, forceIntegers);
        if (yAxis.getUpperBound() >= 1000) {
            NumberFormat nfBig = new CompactNumberFormat(
                    "#,##0.##",
                    DecimalFormatSymbols.getInstance(Locale.US),
                    new String[]{"", "", "", "0K", "00K", "000K", "0M", "00M", "000M", "0B", "00B", "000B", "0T", "00T", "000T"});
            try {
                setDecimalsForShortExpressionOfYTicks(nfBig, yStep, yAxis);
            } catch (ParseException ex) {
                Logger.getLogger(AbstractPlotter.class.getName()).log(Level.SEVERE, null, ex);
            }
            yAxis.setNumberFormatOverride(nfBig);
        } else {
            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            yAxis.setNumberFormatOverride(nf);
        }
        setRotationOfYAxisNumbersFont(yAxis, yStep, yAxis.getNumberFormatOverride());
    }

    protected void setLabelsOfAxis(Axis axis) {
        axis.setTickLabelFont(FONT_AXIS_MARKERS);
        axis.setLabelFont(FONT_AXIS_TITLE);
        axis.setAxisLineVisible(false); // doubles lines next to axes
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    protected void setLegendFont(LegendTitle legend) {
        if (legend != null) {
            legend.setItemFont(FONT_AXIS_MARKERS);
            legend.setItemLabelPadding(new RectangleInsets(2, 2, 2, 15));
        }
    }

    protected void setChartColorAndTitleFont(JFreeChart chart, Plot plot) {
        TextTitle title = chart.getTitle();
        if (title != null) {
            title.setFont(FONT_AXIS_TITLE);
        }
        chart.setBackgroundPaint(Color.WHITE);
        plot.setBackgroundAlpha(0);
        if (plot instanceof XYPlot) {
            XYPlot xyPlot = (XYPlot) plot;
            xyPlot.setDomainGridlineStroke(DASHED_STROKE);
            xyPlot.setDomainGridlinePaint(Color.GRAY);
            xyPlot.setRangeGridlineStroke(DASHED_STROKE);
            xyPlot.setRangeGridlinePaint(Color.GRAY);
        } else if (plot instanceof CategoryPlot) {
            CategoryPlot catPlot = (CategoryPlot) plot;
            catPlot.setRangeGridlinesVisible(true);
            catPlot.setRangeGridlineStroke(DASHED_STROKE);
            catPlot.setRangeGridlinePaint(Color.GRAY);
        } else {
            System.out.println("!!!");
            System.out.println("!!!");
            System.out.println("!!!");
            System.out.println("!!!");
        }
    }

    protected double calculateHighestVisibleTickValue(NumberAxis axis) {
        double unit = axis.getTickUnit().getSize();
        double index = Math.floor(axis.getRange().getUpperBound() / unit);
        return index * unit;
    }

    private void setDecimalsForShortExpressionOfYTicks(NumberFormat nfBig, Double step, NumberAxis axis) throws ParseException {
        double max = calculateHighestVisibleTickValue(axis);
        double lb = axis.getLowerBound();
        int decimalsOfNext = 0;
        boolean ok;
        if (step < Tools.MIN_ROUNDING_TOVALUE) {
            return;
        }
        do {
            ok = true;
            double currDouble = max;
            String prev = "";
            String currString;
            while (currDouble > lb) {
                currString = nfBig.format(currDouble);
                double check = nfBig.parse(currString).doubleValue();
                float f = DataTypeConvertor.doubleToPreciseFloat(check);
                f = Tools.round(f, step.floatValue(), false);
                f = vm.mathtools.Tools.correctPossiblyCorruptedFloat(f);
                check = DataTypeConvertor.floatToPreciseDouble(f); // problem with floats
                if (currString.equals(prev) || check != currDouble) {
                    ok = false;
                    decimalsOfNext++;
                    if (decimalsOfNext == 20) {
                        decimalsOfNext = 1;
                        nfBig.setMaximumFractionDigits(decimalsOfNext);
                        ok = true;
                    } else {
                        nfBig.setMaximumFractionDigits(decimalsOfNext);
                        nfBig.setMinimumFractionDigits(decimalsOfNext);
                        break;
                    }
                }
                f = (float) (currDouble - step);
                f = Tools.round(f, step.floatValue(), false);
                f = vm.mathtools.Tools.correctPossiblyCorruptedFloat(f);
                currDouble = DataTypeConvertor.floatToPreciseDouble(f);// problem with floats
                prev = currString;
            }
        } while (!ok);
        nfBig.setMinimumFractionDigits(0);
        LOG.log(Level.INFO, "yStep: {0}, decimals: {1}", new Object[]{step, decimalsOfNext});
    }

    protected float[] minMaxY;

    protected void setMinAndMaxYValues(float[][] tracesYValues) {
        minMaxY = new float[]{Float.MAX_VALUE, -Float.MAX_VALUE};
        for (float[] r : tracesYValues) {
            for (float f : r) {
                if (f > 0) {
                    minMaxY[0] = Math.min(minMaxY[0], f);
                }
                minMaxY[1] = Math.max(minMaxY[1], f);
            }
        }
        if (minMaxY[0] > 0) {
            minMaxY[0] *= 0.9;
        } else {
            minMaxY[0] *= 1.1;
        }
        if (minMaxY[1] > 0) {
            minMaxY[1] *= 0.9;
        } else {
            minMaxY[1] *= 1.1;
        }
    }

    private void setMarginsForPaintScaleLegend(JFreeChart plot, int height) {
        if (showLegend) {
            List subtitles = plot.getSubtitles();
            if (subtitles != null && !subtitles.isEmpty()) {
                double margin = (0.05 * height) / 2;
                for (Object subtitle : subtitles) {
                    if (subtitle instanceof PaintScaleLegend) {
                        PaintScaleLegend psl = (PaintScaleLegend) subtitle;
                        psl.setMargin(margin, 0, margin, 0);
                    }
                }
            }
        } else {
            plot.removeLegend();
        }
    }
}
