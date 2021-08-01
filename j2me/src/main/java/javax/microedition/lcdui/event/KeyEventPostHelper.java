package javax.microedition.lcdui.event;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;

/**
 * @author lollipop
 * @date 2021/8/1 17:09
 */
public class KeyEventPostHelper {

    public static boolean postKeyPressed(Displayable canvas, int keyCode) {
        if (canvas instanceof Canvas) {
            ((Canvas)canvas).postKeyPressed(keyCode);
            return true;
        }
        return false;
    }

    public static boolean postKeyReleased(Displayable canvas, int keyCode) {
        if (canvas instanceof Canvas) {
            ((Canvas)canvas).postKeyReleased(keyCode);
            return true;
        }
        return false;
    }

    public static boolean postKeyRepeated(Displayable canvas, int keyCode) {
        if (canvas instanceof Canvas) {
            ((Canvas)canvas).postKeyRepeated(keyCode);
            return true;
        }
        return false;
    }

}
