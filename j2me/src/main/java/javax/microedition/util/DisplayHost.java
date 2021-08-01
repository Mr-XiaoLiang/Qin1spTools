package javax.microedition.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.overlay.OverlayView;

/**
 * @author lollipop
 * @date 2021/7/28 22:58
 */
public interface DisplayHost {

    public Activity getActivity();

    public boolean isVisible();

    public Displayable getCurrent();

    public void setCurrent(Displayable displayable);

    public ViewGroup getRootView();

    public OverlayView getOverlayView();

}
