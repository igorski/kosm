/*
APE (Actionscript Physics Engine) is an AS3 open source 2D physics engine
Copyright 2006, Alec Cove 

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

Contact: nl.blissfulthinking.java.android.ape@cove.org

Converted to Java by Theo Galanakis theo.galanakis@hotmail.com

Optimized for Android by Michiel van den Anker michiel.van.den.anker@gmail.com

*/
package blissfulthinking.java.android.ape;

import blissfulthinking.java.android.apeforandroid.FP;
import blissfulthinking.java.android.apeforandroid.Paints;
import android.graphics.Canvas;


/**
	 * A circle shaped particle. 	 
	 */
	public class CircleParticle extends AbstractParticle {
		public final int[] currTemp = new int[2];
		public int radius;
	
		
		/**
		 * @param x The initial x position of this particle.
		 * @param y The initial y position of this particle.
		 * @param radius The radius of this particle.
		 * @param fixed Determines if the particle is fixed or not. Fixed particles
		 * are not affected by forces or collisions and are good to use as surfaces.
		 * Non-fixed particles move freely in response to collision and forces.
		 * @param mass The mass of the particle.
		 * @param elasticity The elasticity of the particle. Higher values mean more elasticity or 'bounciness'.
		 * @param friction The surface friction of the particle.
		 */
		public CircleParticle (
				float x, 
				float y, 
				float radius, 
				boolean fixed,
				float mass, 
				float elasticity,
				float friction) {
			super(x, y, fixed, mass, elasticity, friction);
//			radius*=APEngine.scale;
			this.radius = FP.fromFloat(radius);
		}
		
		@Override
		public /*final*/ void drawParticle(Canvas c) {
//			c.drawCircle(FP.toFloat(curr.x)/APEngine.scale,FP.toFloat(curr.y)/APEngine.scale,FP.toFloat(radius)/APEngine.scale,Paints.circlePaint);
			c.drawCircle(FP.toFloat(curr[0]),FP.toFloat(curr[1]),FP.toFloat(radius), Paints.circlePaint);
		}	

		/**
		 * The radius of the particle.
		 */
		public final void setRadius(int r) {
			radius = r;
		}
			
		// TODO REVIEW FOR ANY POSSIBILITY OF PRECOMPUTING
////		@Override
//		public final Interval getProjection(Vector axis) {
//			int c = curr.dot(axis);
//			interval.min = c - radius;
//			interval.max = c + radius;
//			return interval;
//		}
		// TODO REVIEW FOR ANY POSSIBILITY OF PRECOMPUTING

		public final Interval getProjection(int[] axis) {
			currTemp[0] = curr[0];
			currTemp[1] = curr[1];
			int c = Vector.dot(currTemp,axis);
			interval.min = c - radius;
			interval.max = c + radius;
			return interval;
		}
		
		public final Interval getIntervalX() {
			interval.min = curr[0] - radius;
			interval.max = curr[0] + radius;
			return interval;
		}
			
		public final Interval getIntervalY() {
			interval.min = curr[1] - radius;
			interval.max = curr[1] + radius;
			return interval;
		}
	}
