package utils;

import java.awt.*;

public class ColorHelper {
    public static Color getColor(int index) {
        if(index == 1) {
            return Color.RED;
        }
        if(index == 2) {
            return Color.BLUE;
        }
        if(index == 3) {
            return Color.YELLOW;
        }
        return Color.GREEN;
    }
}
