package imageprocessor.utils;

import imageprocessor.model.ColorAmount;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kgurgul on 2015-11-15.
 */
public class Utils {

    public static List<ColorAmount> convertMapToColorObjects(Map<Integer, Integer> colorMap) {
        return colorMap.entrySet().stream().map(entry -> new ColorAmount(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
