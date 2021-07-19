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

public class Appearance extends Object3D {
	//------------------------------------------------------------------
	// Instance data
	//------------------------------------------------------------------

	private CompositingMode compositingMode;
	private Fog fog;
	private Material material;
	private PolygonMode polygonMode;
	private Texture2D[] textures;

	//------------------------------------------------------------------
	// Constructor(s)
	//------------------------------------------------------------------

	public Appearance() {
		super(_ctor(Interface.getHandle()));
	}

	/**
	 */
	Appearance(long handle) {
		super(handle);

		compositingMode = (CompositingMode) getInstance(_getCompositingMode(handle));
		fog = (Fog) getInstance(_getFog(handle));
		material = (Material) getInstance(_getMaterial(handle));
		polygonMode = (PolygonMode) getInstance(_getPolygonMode(handle));

		textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];

		for (int i = 0; i < Defs.NUM_TEXTURE_UNITS; ++i) {
			textures[i] = (Texture2D) getInstance(_getTexture(handle, i));
		}
	}

	//------------------------------------------------------------------
	// Public methods
	//------------------------------------------------------------------

	public void setCompositingMode(CompositingMode compositingMode) {
		_setCompositingMode(handle,
				compositingMode != null
						? compositingMode.handle
						: 0);
		this.compositingMode = compositingMode;
	}

	public CompositingMode getCompositingMode() {
		return compositingMode;
	}

	public void setFog(Fog fog) {
		_setFog(handle, fog != null ? fog.handle : 0);
		this.fog = fog;
	}

	public Fog getFog() {
		return fog;
	}

	public void setPolygonMode(PolygonMode polygonMode) {
		_setPolygonMode(handle, polygonMode != null ? polygonMode.handle : 0);
		this.polygonMode = polygonMode;
	}

	public PolygonMode getPolygonMode() {
		return polygonMode;
	}

	public void setLayer(int index) {
		_setLayer(handle, index);
	}

	public int getLayer() {
		return _getLayer(handle);
	}

	public void setMaterial(Material material) {
		_setMaterial(handle, material != null ? material.handle : 0);
		this.material = material;
	}

	public Material getMaterial() {
		return material;
	}

	public void setTexture(int unit, Texture2D texture) {
		_setTexture(handle, unit, texture != null ? texture.handle : 0);

		if (textures == null) {
			textures = new Texture2D[Defs.NUM_TEXTURE_UNITS];
		}
		textures[unit] = texture;
	}

	public Texture2D getTexture(int unit) {
		return (Texture2D) getInstance(_getTexture(handle, unit));
	}

	//------------------------------------------------------------------
	// Private methods
	//------------------------------------------------------------------

	private static native long _ctor(long hInterface);

	private static native long _getCompositingMode(long hApp);

	private static native long _getFog(long hApp);

	private static native int _getLayer(long hApp);

	private static native long _getMaterial(long hApp);

	private static native long _getPolygonMode(long hApp);

	private static native long _getTexture(long hApp, int unit);

	private static native void _setCompositingMode(long hApp, long hMode);

	private static native void _setFog(long hApp, long hFog);

	private static native void _setLayer(long hApp, int layer);

	private static native void _setMaterial(long hApp, long hMaterial);

	private static native void _setPolygonMode(long hApp, long hMode);

	private static native void _setTexture(long hApp,
										   int unit,
										   long hTexture);
}
