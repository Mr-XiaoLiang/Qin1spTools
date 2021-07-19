/*
 * Copyright (c) 2003 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Nokia Corporation - initial contribution.
 *
 * Contributors:
 *
 * Description:
 *
 */

package javax.microedition.m3g;

public class World extends Group {
	//------------------------------------------------------------------
	// Instance data
	//------------------------------------------------------------------

	private Camera activeCamera;
	private Background background;

	//------------------------------------------------------------------
	// Constructor(s)
	//------------------------------------------------------------------

	public World() {
		super(_ctor(Interface.getHandle()));
	}

	/**
	 */
	World(long handle) {
		super(handle);
		background = (Background) getInstance(_getBackground(handle));
		activeCamera = (Camera) getInstance(_getActiveCamera(handle));
	}

	//------------------------------------------------------------------
	// Public methods
	//------------------------------------------------------------------

	public void setBackground(Background background) {
		_setBackground(handle, background != null ? background.handle : 0);
		this.background = background;
	}

	public Background getBackground() {
		return background;
	}

	public void setActiveCamera(Camera camera) {
		_setActiveCamera(handle, camera != null ? camera.handle : 0);
		this.activeCamera = camera;
	}

	public Camera getActiveCamera() {
		return activeCamera;
	}

	// Native methods
	private static native long _ctor(long hInterface);

	private static native void _setActiveCamera(long handle, long hCamera);

	private static native void _setBackground(long handle, long hBackground);

	private static native long _getActiveCamera(long handle);

	private static native long _getBackground(long handle);
}
