/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vm.colour;

import java.awt.Color;
import java.awt.Paint;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.renderer.LookupPaintScale;
import vm.datatools.Tools;

/**
 *
 * @author au734419
 */
public class StandardColours {

    public static final Color BOX_BLACK = Color.BLACK;
    public static final Color LIGHT_BOX_BLACK = new Color(128, 128, 128);

    public static final Color[] COLOURS = new Color[]{
        new Color(31, 119, 180),
        new Color(214, 39, 40),
        new Color(44, 160, 44),
        new Color(255, 127, 14),
        new Color(148, 103, 189),
        new Color(140, 86, 75),
        new Color(227, 119, 194),
        new Color(127, 127, 127),
        new Color(188, 189, 34),
        new Color(23, 190, 207)
    };

    public static final Color[] LIGHT_COLOURS = new Color[]{
        new Color(143, 187, 217),
        new Color(234, 147, 147),
        new Color(149, 207, 149),
        new Color(255, 191, 134),
        new Color(201, 179, 222),
        new Color(197, 170, 165),
        new Color(241, 187, 224),
        new Color(191, 191, 191),
        new Color(221, 222, 144),
        new Color(139, 222, 231)
    };

    public static enum COLOUR_NAME {
        C1_BLUE,
        C2_RED,
        C3_GREEN,
        C4_ORANGE,
        C5_VIOLET,
        C6_BROWN,
        C7_PURPLE,
        C8_GREY,
        C9_LIME,
        C10_CYAN,
        CX_BLACK
    }

    public static final Color[] RAINBOW_COLOURS = new Color[]{
        new Color(00, 43, 170), // first blue
        new Color(00, 00, 255),
        new Color(25, 00, 213),
        new Color(50, 00, 172),
        new Color(75, 00, 130),
        new Color(129, 43, 166),
        new Color(184, 87, 202),
        new Color(208, 58, 135),
        new Color(231, 29, 67),// first from physics
        new Color(255, 0, 0),
        new Color(255, 55, 0),
        new Color(255, 110, 0),
        new Color(255, 165, 0),
        new Color(255, 195, 0),
        new Color(255, 225, 0),
        new Color(255, 255, 0),
        new Color(170, 213, 0),
        new Color(85, 170, 0),
        new Color(00, 128, 0),
        new Color(00, 85, 85)
    };

    public static final Color getColor(COLOUR_NAME name, boolean light) {
        int idx = Arrays.binarySearch(COLOUR_NAME.values(), name);
        if (name == COLOUR_NAME.CX_BLACK) {
            if (!light) {
                return BOX_BLACK;
            } else {
                return LIGHT_BOX_BLACK;
            }
        }
        if (!light) {
            return StandardColours.COLOURS[idx];
        }
        return StandardColours.LIGHT_COLOURS[idx];
    }

    private static final SortedMap<float[], LookupPaintScale> cache = new TreeMap<>(new Tools.FloatArraySameLengthsComparator());

    public static Color getRainboxRelativeColour(float minValue, float maxValue, float value, boolean logarithmic) {
        LookupPaintScale paintScale = createRainboxPaintScale(minValue, maxValue, logarithmic);
        if (logarithmic) {
            value = getLogarithm(minValue, maxValue, value);
        }
        Paint paint = paintScale.getPaint(value);
        return (Color) paint;
    }

    public static LookupPaintScale createRainboxPaintScale(float minValue, float maxValue, boolean logarithmic) {
        if (logarithmic) {
            minValue = getLogarithm(minValue, maxValue, minValue);
            maxValue = getLogarithm(minValue, maxValue, minValue);
        }
        float[] key = new float[]{minValue, maxValue};
        LookupPaintScale paintScale;
        if (cache.containsKey(key)) {
            paintScale = cache.get(key);
        } else {
            if (minValue < maxValue) {
                paintScale = new LookupPaintScale(minValue, maxValue, Color.black);
                float interval = maxValue - minValue;
                float step = interval / (StandardColours.RAINBOW_COLOURS.length - 2);
                int i = 0;
                while (minValue <= maxValue) {
                    paintScale.add(minValue, StandardColours.RAINBOW_COLOURS[i]);
                    i = (i + 1) % StandardColours.RAINBOW_COLOURS.length;
                    minValue += step;
                }
            } else {
                paintScale = new LookupPaintScale(0, 1000, Color.black);
            }
            cache.put(key, paintScale);
        }
        return paintScale;
    }

    private static float getLogarithm(float min, float max, float value) {
        if (min > 0 && max > 0) {
            return (float) Math.log(value);
        }
        Logger.getLogger(StandardColours.class.getName()).log(Level.WARNING, "Min max values do not allow logarithmic scale!{0}, {1}", new Object[]{min, max});
        return value;
    }

}
