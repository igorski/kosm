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

	
	public final class CollisionDetector {
		
		public static final int[] collisionNormal = new int[2];
		public static final int[] mult_temp = new int[2];
		public static final int[] d = new int[2];
		public static final int[] dTemp = new int[2];
		public static final int[] vertex = new int[2];
		
		/**
		 * Tests the collision between two objects. If there is a collision it is passed off
		 * to the CollisionResolver class to resolve the collision.
		 */	
		public static final void test(AbstractParticle objA, AbstractParticle objB) {
			
			if (objA.fixed && objB.fixed) return;
		
			//MvdA TODO faster or not?
			if (objA instanceof CircleParticle) {
				if(objB instanceof CircleParticle) {
					testCirclevsCircle((CircleParticle)objA, (CircleParticle)objB);
				}
				else {
					testOBBvsCircle((RectangleParticle)objB, (CircleParticle)objA);
				}
			}
			else {
				if(objB instanceof CircleParticle) {
					testOBBvsCircle((RectangleParticle)objA, (CircleParticle)objB);
				}
				else {
					testOBBvsOBB((RectangleParticle)objA, (RectangleParticle)objB);
				}
			}

//			// circle to circle
//			if (objA instanceof CircleParticle && objB instanceof CircleParticle) {
//				testCirclevsCircle((CircleParticle)objA, (CircleParticle)objB);
//	
//			// rectangle to circle - two ways
//			} else if (objA instanceof RectangleParticle && objB instanceof CircleParticle) {
//				testOBBvsCircle((RectangleParticle)objA, (CircleParticle)objB);
//				
//			} else if (objA instanceof CircleParticle && objB instanceof RectangleParticle)  {
//				testOBBvsCircle((RectangleParticle)objB, (CircleParticle)objA);		
//			}			// rectangle to rectangle
//			else if (objA instanceof RectangleParticle && objB instanceof RectangleParticle) {
//				testOBBvsOBB((RectangleParticle)objA, (RectangleParticle)objB);
//			}
		}
	
		/**
		 * Tests the collision between two RectangleParticles (aka OBBs). If there is a collision it
		 * determines its axis and depth, and then passes it off to the CollisionResolver for handling.
		 */
		private static final void testOBBvsOBB(RectangleParticle ra, RectangleParticle rb) {
			int collisionDepth = FP.MAX_VALUE;
			
//			for (int i = 0; i < 2; i++) {
		
//				final Vector axisA = ra.axes[i];
				final int[] axisA = ra.axes0;
				int depthA = testIntervals(ra.getProjection(axisA), rb.getProjection(axisA));
			    if (depthA == 0) {
			    	return;
			    }
				
			    final int[] axisB = rb.axes0;
			    int depthB = testIntervals(ra.getProjection(axisB), rb.getProjection(axisB));
			    if (depthB == 0) {
			    	return;
			    }
			    
			    int absA = FP.abs(depthA);
			    int absB = FP.abs(depthB);
			    
			    if (absA < FP.abs(collisionDepth) || absB < FP.abs(collisionDepth)) {
			    	if(absA < absB) {
			    		Vector.setTo(axisA,collisionNormal);
//			    		collisionNormal.setTo(axisA[0],axisA[1]);
			    		collisionDepth = depthA;
			    	}
			    	else {
			    		Vector.setTo(axisB,collisionNormal);
//			    		collisionNormal.setTo(axisB[0],axisB[1]);
			    		collisionDepth = depthB;
			    	}
			    }
			    
			    //REPEAT
				final int[] axisC = ra.axes1;
				int depthC = testIntervals(ra.getProjection(axisC), rb.getProjection(axisC));
			    if (depthC == 0) {
			    	return;
			    }
				
			    final int[] axisD = rb.axes0;
			    int depthD = testIntervals(ra.getProjection(axisD), rb.getProjection(axisD));
			    if (depthD == 0) {
			    	return;
			    }
			    
			    int absC = FP.abs(depthC);
			    int absD = FP.abs(depthD);
			    
			    if (absC < FP.abs(collisionDepth) || absD < FP.abs(collisionDepth)) {
			    	if(absC < absD) {
			    		Vector.setTo(axisC,collisionNormal);
//			    		collisionNormal.setTo(axisC[0],axisC[1]);
			    		collisionDepth = depthC;
			    	}
			    	else {
			    		Vector.setTo(axisD,collisionNormal);
//			    		collisionNormal.setTo(axisD[0],axisD[1]);
			    		collisionDepth = depthD;
			    	}
			    }
//			}
			CollisionResolver.resolveParticleParticle(ra, rb, collisionNormal, collisionDepth);
		}		
	
		/**
		 * Tests the collision between a RectangleParticle (aka an OBB) and a CircleParticle. 
		 * If there is a collision it determines its axis and depth, and then passes it off 
		 * to the CollisionResolver for handling.
		 */
		private static final void testOBBvsCircle(RectangleParticle ra, CircleParticle ca) {
			int collisionDepth = FP.MAX_VALUE;

			int depth1;
			int depth2;
			
//			Vector boxAxis = ra.axes[0];
			final int[] boxAxis0 = ra.axes0;
			int depth = testIntervals(ra.getProjection(boxAxis0), ca.getProjection(boxAxis0));
			if (depth == 0) {
				return;
			}
	
			if (FP.abs(depth) < FP.abs(collisionDepth)) {
				Vector.setTo(boxAxis0,collisionNormal);
//				collisionNormal.setTo(boxAxis0[0],boxAxis0[1]);
				collisionDepth = depth;
			}
			depth1 = depth;
				
			final int[] boxAxis1 = ra.axes1;
			depth = testIntervals(ra.getProjection(boxAxis1), ca.getProjection(boxAxis1));
			if (depth == 0) {
				return;
			}
	
			if (FP.abs(depth) < FP.abs(collisionDepth)) {
				Vector.setTo(boxAxis1,collisionNormal);
//				collisionNormal.setTo(boxAxis1[0],boxAxis1[1]);
				collisionDepth = depth;
			}
			depth2 = depth;
			
			// determine if the circle's center is in a vertex region
			int r = ca.radius;
			if (FP.abs(depth1) < r && FP.abs(depth2) < r) {
	
				closestVertexOnOBB(ca.curr,ra);
	
				// get the distance from the closest vertex on rect to circle center
//				vertex.supply_minus(ca.curr,collisionNormal);
				Vector.supply_minus(vertex, ca.curr, collisionNormal);
				
				int mag = Vector.magnitude(collisionNormal);
				collisionDepth = r - mag;
	
				if (collisionDepth > 0) {
					// there is a collision in one of the vertex regions
//					collisionNormal.divEquals(mag);
					Vector.supply_div(collisionNormal,mag,collisionNormal);
				} else {
					// ra is in vertex region, but is not colliding
					return;
				}
			}
			CollisionResolver.resolveParticleParticle(ra, ca, collisionNormal, collisionDepth);
		}
	
		/**
		 * Tests the collision between two CircleParticles. If there is a collision it 
		 * determines its axis and depth, and then passes it off to the CollisionResolver
		 * for handling.
		 */	
		private static final void testCirclevsCircle(CircleParticle ca, CircleParticle cb) {
			int depthX = testIntervals(ca.getIntervalX(), cb.getIntervalX());
			if (depthX == 0) return;
			
			int depthY = testIntervals(ca.getIntervalY(), cb.getIntervalY());
			if (depthY == 0) return;
			Vector.supply_minus(ca.curr,cb.curr,collisionNormal);
//			ca.curr.supply_minus(cb.curr,collisionNormal);
			
			
			int mag = Vector.magnitude(collisionNormal);
			int collisionDepth = (ca.radius + cb.radius) - mag;
			
			if (collisionDepth > 0) {
//				collisionNormal.divEquals(mag);
				Vector.supply_div(collisionNormal, mag, collisionNormal);
				
				CollisionResolver.resolveParticleParticle(ca, cb, collisionNormal, collisionDepth);
			}
		}
	
		/**
		 * Returns 0 if intervals do not overlap. Returns smallest depth if they do.
		 */
		private static final int testIntervals(Interval intervalA, Interval intervalB) {	
			if (intervalA.max < intervalB.min) return 0;
			if (intervalB.max < intervalA.min) return 0;
			
			int lenA = intervalB.max - intervalA.min;
			int lenB = intervalB.min - intervalA.max;
			
			return (FP.abs(lenA) < FP.abs(lenB)) ? lenA : lenB;
		}
		
		/**
		 * Returns the location of the closest vertex on r to point p
		 */
		private static final void closestVertexOnOBB(int[] p, RectangleParticle r) {
	
//			p.supply_minus(r.curr,d);
			Vector.supply_minus(p,r.curr,d);
			
			Vector.setTo(r.curr,vertex);
	
//			for (int i = 0; i < 2; i++) {
				dTemp[0] = d[0];
				dTemp[1] = d[1];
				int dist = Vector.dot(dTemp,r.axes0);
	
				if (dist >= 0) dist = r.extents[0];
				else if (dist < 0) dist = -r.extents[0];
	
//				vertex.plusEquals(Vector.supply_mult(r.axes0,dist, mult_temp));
				
				Vector.supply_mult(r.axes0,dist,mult_temp);
				Vector.supply_plus(vertex,mult_temp,vertex);
				
				dTemp[0] = d[0];
				dTemp[1] = d[1];
				dist = Vector.dot(dTemp,r.axes1);
				
				if (dist >= 0) dist = r.extents[1];
				else if (dist < 0) dist = -r.extents[1];
	
//				vertex.plusEquals(Vector.supply_mult(r.axes1,dist, mult_temp));	
				
				Vector.supply_mult(r.axes1,dist, mult_temp);
				Vector.supply_plus(vertex, mult_temp, vertex);
//			}
//			return vertex;
		}
	}
