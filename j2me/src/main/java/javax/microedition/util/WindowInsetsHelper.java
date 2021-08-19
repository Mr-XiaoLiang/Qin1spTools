package javax.microedition.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewParent;

/**
 * @author lollipop
 * @date 2021/8/18 22:29
 */
public class WindowInsetsHelper {

    public static Rect getOffset(View target, int left, int top, int right, int bottom) {
        View root = getRoot(target);
        int[] rootLocation = new int[2];
        root.getLocationInWindow(rootLocation);
        int[] targetLocation = new int[2];
        target.getLocationInWindow(targetLocation);
        int offsetX = targetLocation[0] - rootLocation[0];
        int offsetY = targetLocation[1] - rootLocation[1];
        int rootRight = root.getWidth() + rootLocation[0];
        int rootBottom = root.getHeight() + rootLocation[1];
        int targetRight = target.getWidth() + targetLocation[0];
        int targetBottom = target.getHeight() + targetLocation[1];
        return new Rect(
                left - offsetX,
                top - offsetY,
                right - (rootRight - targetRight),
                bottom - (rootBottom - targetBottom)
        );
    }

    private static View getRoot(View target) {
        View view = target;
        ViewParent parent = target.getParent();
        while (parent != null) {
            if (parent instanceof View) {
                view = (View) parent;
            } else {
                break;
            }
            parent = view.getParent();
        }
        return view;
    }

}
