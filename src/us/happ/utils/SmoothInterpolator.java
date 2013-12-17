package us.happ.utils;

import android.view.animation.Interpolator;

//Smooth interpolation. Formula from Prixing
// http://cyrilmottier.com/2012/05/22/the-making-of-prixing-fly-in-app-menu-part-1/
public class SmoothInterpolator implements Interpolator {

	@Override
	public float getInterpolation(float input) {
		return (float) (Math.pow(input - 1, 5) + 1);
	}
}