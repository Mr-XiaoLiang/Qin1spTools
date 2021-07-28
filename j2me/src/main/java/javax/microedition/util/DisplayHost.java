package javax.microedition.util;

import android.app.Activity;

import javax.microedition.lcdui.Displayable;

/**
 * @author lollipop
 * @date 2021/7/28 22:58
 */
public interface DisplayHost {

    public Activity getActivity();

    public boolean isVisible();

    public Displayable getCurrent();

    public void setCurrent(Displayable displayable);

}
