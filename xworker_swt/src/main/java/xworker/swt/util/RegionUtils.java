package xworker.swt.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Region;

public class RegionUtils {

    /**
     * 把一个Path添加到Region中。
     *
     * @param region
     * @param path
     */
    public static void addPathToRegion(Region region, Path path) {
        PathData pathData = path.getPathData();
        float[] points = pathData.points;
        byte[] types = pathData.types;

        int start = 0, end = 0;
        for (byte type : types) {
            switch (type) {
                case SWT.PATH_MOVE_TO: {
                    if (start != end) {
                        int n = 0;
                        int[] temp = new int[end - start];
                        for (int k = start; k < end; k++) {
                            temp[n++] = Math.round(points[k]);
                        }
                        region.add(temp);
                    }
                    start = end;
                    end += 2;
                    break;
                }
                case SWT.PATH_LINE_TO: {
                    end += 2;
                    break;
                }
                case SWT.PATH_CLOSE: {
                    if (start != end) {
                        int n = 0;
                        int[] temp = new int[end - start];
                        for (int k = start; k < end; k++) {
                            temp[n++] = Math.round(points[k]);
                        }
                        region.add(temp);
                    }
                    start = end;
                    break;
                }
                case SWT.PATH_CUBIC_TO:{
                    end += 6;
                    break;
                }
                case SWT.PATH_QUAD_TO:{
                    end += 6;
                    break;
                }
            }
        }
    }

}
