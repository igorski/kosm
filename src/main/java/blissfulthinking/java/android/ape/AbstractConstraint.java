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
import android.graphics.Canvas;

	/**
	 * The abstract base class for all constraints. 
	 * 
	 * <p>
	 * You should not instantiate this class directly -- instead use one of the subclasses.
	 * </p>
	 */
	public abstract class AbstractConstraint {

		public boolean visible;
		public int stiffness;

		public AbstractConstraint (float stiffness) {
			visible = true;
			this.stiffness = FP.fromFloat(stiffness);
		}
			
		/**
		 * The stiffness of the constraint. Higher values result in result in 
		 * stiffer constraints. Values should be greater than 0 and less than or 
		 * equal to 1. Depending on the situation, setting constraints to very high 
		 * values may result in instability or unwanted energy.
		 */  	
		public final void setStiffness(int s) {
			stiffness = s;
		}
		
		
		/**
		 * The visibility of the constraint. This is only implemented for the default painting
		 * methods of the constraints. When you create your painting methods in subclassed constraints 
		 * or composites you should add a check for this property.
		 */				
		public final void setVisible(boolean v) {
			visible = v;
		}
		
		public abstract void resolve();
		
		public abstract void drawConstraint(Canvas c);
		
	}
