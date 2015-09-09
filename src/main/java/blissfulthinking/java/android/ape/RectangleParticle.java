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
	 * A rectangular shaped particle. 
	 */ 
	public class RectangleParticle extends AbstractParticle {
		
		public final int[] currTemp = new int[2];
		public final Vector[] cornerPositions = new Vector[4];
		
		//MvdA TODO reestablish cornerParticle functionality
//		public final AbstractParticle[] cornerParticles = new AbstractParticle[4];
		
		public final int[] extents = new int[2];
//		
//		public final Vector[] axes = new Vector[2];
//		
		public final int[] axes0 = new int[2];
		public final int[] axes1 = new int[2];
		private int rotation;

		private boolean show;
		
		/**
		 * @param x The initial x position.
		 * @param y The initial y position.
		 * @param width The width of this particle.
		 * @param height The height of this particle.
		 * @param rotation The rotation of this particle in radians.
		 * @param fixed Determines if the particle is fixed or not. Fixed particles
		 * are not affected by forces or collisions and are good to use as surfaces.
		 * Non-fixed particles move freely in response to collision and forces.
		 * @param mass The mass of the particle
		 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
		 * @param friction The surface friction of the particle. 
		 * <p>
		 * Note that RectangleParticles can be fixed but still have their rotation property 
		 * changed.
		 * </p>
		 */
		public RectangleParticle (
				float x, 
				float y, 
				float width, 
				float height, 
				float rotation, 
				boolean fixed,
				float mass, 
				float elasticity,
				float friction,
				boolean show) {

			super(x, y, fixed, mass, elasticity, friction);
			
//			width*=APEngine.scale;
//			height*=APEngine.scale;
//			
			
			extents[0] = FP.div(FP.fromFloat(width),FP.TWO);
			extents[1] = FP.div(FP.fromFloat(height),FP.TWO);
			
//			axes[0] = Vector.getNew(0,0);
//			axes[1] = Vector.getNew(0,0);
			setRotation(FP.fromFloat(rotation));
			
			this.show  = show;
			
			initCornerPositions();
//			initCornerParticles();
		}
		
		@Override
		public void drawParticle(Canvas c) {
			if(!show) {
				return;
			}
//			System.out.println("DRAWING RECT PARTICLE");
			
//			for (int  j = 0; j < 4; j++) {
//					int i = j;
//					
//					float X1 = (float) (cornerPositions[i]).x;
//					float Y1 = (float) (cornerPositions[i]).y;
//					
//					// point back to first element 
//					if (j == 3) i = -1;
//						
//					float X2 = (float) cornerPositions[i+1].x;
//					float Y2 = (float) cornerPositions[i+1].y;
//
//					c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
//					
//			}
//			//MvdA TODO not sure if this is faster
//			float X1 = FP.toFloat(cornerPositions[0].x)/APEngine.scale;
//			float Y1 = FP.toFloat(cornerPositions[0].y)/APEngine.scale;
//			float X2 = FP.toFloat(cornerPositions[1].x)/APEngine.scale;
//			float Y2 = FP.toFloat(cornerPositions[1].y)/APEngine.scale;
//			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
//			
//			X1 = FP.toFloat(cornerPositions[1].x)/APEngine.scale;
//			Y1 = FP.toFloat(cornerPositions[1].y)/APEngine.scale;
//			X2 = FP.toFloat(cornerPositions[2].x)/APEngine.scale;
//			Y2 = FP.toFloat(cornerPositions[2].y)/APEngine.scale;
//			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
//			
//			X1 = FP.toFloat(cornerPositions[2].x)/APEngine.scale;
//			Y1 = FP.toFloat(cornerPositions[2].y)/APEngine.scale;
//			X2 = FP.toFloat(cornerPositions[3].x)/APEngine.scale;
//			Y2 = FP.toFloat(cornerPositions[3].y)/APEngine.scale;
//			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
//			
//			X1 = FP.toFloat(cornerPositions[3].x)/APEngine.scale;
//			Y1 = FP.toFloat(cornerPositions[3].y)/APEngine.scale;
//			X2 = FP.toFloat(cornerPositions[0].x)/APEngine.scale;
//			Y2 = FP.toFloat(cornerPositions[0].y)/APEngine.scale;
//			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
			//MvdA TODO not sure if this is faster
			float X1 = FP.toFloat(cornerPositions[0].x);
			float Y1 = FP.toFloat(cornerPositions[0].y);
			float X2 = FP.toFloat(cornerPositions[1].x);
			float Y2 = FP.toFloat(cornerPositions[1].y);
			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
			
			X1 = FP.toFloat(cornerPositions[1].x);
			Y1 = FP.toFloat(cornerPositions[1].y);
			X2 = FP.toFloat(cornerPositions[2].x);
			Y2 = FP.toFloat(cornerPositions[2].y);
			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
			
			X1 = FP.toFloat(cornerPositions[2].x);
			Y1 = FP.toFloat(cornerPositions[2].y);
			X2 = FP.toFloat(cornerPositions[3].x);
			Y2 = FP.toFloat(cornerPositions[3].y);
			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
			
			X1 = FP.toFloat(cornerPositions[3].x);
			Y1 = FP.toFloat(cornerPositions[3].y);
			X2 = FP.toFloat(cornerPositions[0].x);
			Y2 = FP.toFloat(cornerPositions[0].y);
			c.drawLine(X1,Y1,X2,Y2,Paints.rectanglePaint);
		 }
		
		
		/**
		 * The rotation of the RectangleParticle in radians. For drawing methods you may 
		 * want to use the <code>angle</code> property which gives the rotation in
		 * degrees from 0 to 360.
		 * 
		 * <p>
		 * Note that while the RectangleParticle can be rotated, it does not have angular
		 * velocity. In otherwords, during collisions, the rotation is not altered, 
		 * and the energy of the rotation is not applied to other colliding particles.
		 * A true rigid body is planned for a later release.
		 * </p>
		 */
		public final int getRotation() {
			return rotation;
		}
			
		public final void setRotation(int t) {
			rotation = t;
			setAxes(t);
		}
			
		/**
		 * An Array of 4 contact particles at the corners of the RectangleParticle. You can attach
		 * other particles or constraints to these particles. Note this is a one-way effect, meaning the
		 * RectangleParticle's motion will move objects attached to the corner particles, but the 
		 * reverse is not true. 
		 * 
		 * <p>
		 * In order to access one of the 4 corner particles, you can use array notation 
		 * e.g., <code>myRectangleParticle.cornerParticles[0]</code>
		 * </p>
		 */					
//		private final void initCornerParticles() {
//			
//			//MvdA TODO reinstate this 
//				CircleParticle cp1 = new CircleParticle(0.0,0.0,1.0,false,0.1,0.0,0.0);
//				cp1.setCollidable(false);
//				cp1.setVisible(false);
//				APEngine.addParticle(cp1);
//				
//				CircleParticle cp2 = new CircleParticle(0.0,0.0,1.0,false,0.1,0.0,0.0);
//				cp2.setCollidable(false);
//				cp2.setVisible(false);
//				APEngine.addParticle(cp2);
//	
//				CircleParticle cp3 = new CircleParticle(0.0,0.0,1.0,false,0.1,0.0,0.0);
//				cp3.setCollidable(false);
//				cp3.setVisible(false);
//				APEngine.addParticle(cp3);
//	
//				CircleParticle cp4 = new CircleParticle(0.0,0.0,1.0,false,0.1,0.0,0.0);
//				cp4.setCollidable(false);
//				cp4.setVisible(false);
//				APEngine.addParticle(cp4);
//				
////				cornerParticles[0] = cp1;
////				cornerParticles[1] = cp2;
////				cornerParticles[2] = cp3;
////				cornerParticles[3] = cp4;
//				
//				updateCornerParticles();
//		}
			
		/**
		 * An Array of <code>Vector</code> objects storing the location of the 4
		 * corners of this RectangleParticle. This method would usually be called
		 * in a painting method if the locations of the corners were needed. If the
		 * RectangleParticle is being drawn using its position and angle properties 
		 * then you don't need to access this property.
		 */
		private final void initCornerPositions() {
				cornerPositions[0] = Vector.getNew(0,0);
				cornerPositions[1] = Vector.getNew(0,0);
				cornerPositions[2] = Vector.getNew(0,0);
				cornerPositions[3] = Vector.getNew(0,0);
						
				updateCornerPositions();
		}
		
		@Override
		public final void update(int dt2) {
			super.update(dt2);
			updateCornerPositions();
		}

//		// TODO REVIEW FOR ANY POSSIBILITY OF PRECOMPUTING
//		@Override
//		public final Interval getProjection(Vector axis) {
//			
//			int radius =
//				FP.mul(extents[0],FP.abs(axis.dot(axes0)))+
//				FP.mul(extents[1],FP.abs(axis.dot(axes1)));
//			
//			int c = curr.dot(axis);
//			
//			interval.min = c - radius;
//			interval.max = c + radius;
//			return interval;
//		}
		// TODO REVIEW FOR ANY POSSIBILITY OF PRECOMPUTING
	
		public final Interval getProjection(int[] axis) {
			
			int radius =
				FP.mul(extents[0],FP.abs(Vector.dot(axis,axes0)))+
				FP.mul(extents[1],FP.abs(Vector.dot(axis,axes1)));
			
			currTemp[0] = curr[0];
			currTemp[1] = curr[1];
			int c = Vector.dot(currTemp,axis);
			
			interval.min = c - radius;
			interval.max = c + radius;
			return interval;
		}

		public final void updateCornerPositions() {
		
			int ae0_x = FP.mul(axes0[0],extents[0]);
			int ae0_y = FP.mul(axes0[1],extents[0]);
			int ae1_x = FP.mul(axes1[0],extents[1]);
			int ae1_y = FP.mul(axes1[1],extents[1]);
			
			
			int emx = ae0_x - ae1_x;
			int emy = ae0_y - ae1_y;
			int epx = ae0_x + ae1_x;
			int epy = ae0_y + ae1_y;
			
			//MvdA TODO optimized to reuse the old Vectors but could be notated faster I think
			Vector cornerPosition1 = cornerPositions[0];
			Vector cornerPosition2 = cornerPositions[1];
			Vector cornerPosition3 = cornerPositions[2];
			Vector cornerPosition4 = cornerPositions[3];
			
			cornerPosition1.x = curr[0] - epx;
			cornerPosition1.y = curr[1] - epy;
			cornerPositions[0] = cornerPosition1;
			
			cornerPosition2.x = curr[0] + emx;
			cornerPosition2.y = curr[1] + emy;
			cornerPositions[1] = cornerPosition2;
			
			cornerPosition3.x = curr[0] + epx;
			cornerPosition3.y = curr[1] + epy;
			cornerPositions[2] = cornerPosition3;
			
			cornerPosition4.x = curr[0] - emx;
			cornerPosition4.y = curr[1] - emy;
			cornerPositions[3] = cornerPosition4;
		}
		
//		private final void updateCornerParticles() {
//			//MvdA TODO reinstate this
////			for (int i = 0; i < 4; i++) {
////				cornerParticles[i].setpx(cornerPositions[i].x);
////				cornerParticles[i].setpy(cornerPositions[i].y);
////				getCornerParticles()[i].setpx(cornerPositions[i].x);
////				getCornerParticles()[i].setpy( cornerPositions[i].y);
////			}	
////			cornerParticles[0].setpx(cornerPositions[0].x);
////			cornerParticles[0].setpy(cornerPositions[0].y);
////			cornerParticles[1].setpx(cornerPositions[1].x);
////			cornerParticles[1].setpy(cornerPositions[1].y);
////			cornerParticles[2].setpx(cornerPositions[2].x);
////			cornerParticles[2].setpy(cornerPositions[2].y);
////			cornerParticles[3].setpx(cornerPositions[3].x);
////			cornerParticles[3].setpy(cornerPositions[3].y);
//		}
				
		private final void setAxes(int t) {
			int s = FP.sin(t);
			int c = FP.cos(t);
			
			axes0[0] = c;
			axes0[1] = s;
			axes1[0] = -s;
			axes1[1] = c;
		}

	}
