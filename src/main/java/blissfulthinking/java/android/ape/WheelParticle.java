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
import java.util.ArrayList;

import blissfulthinking.java.android.apeforandroid.FP;
	
	/**
	 * A particle that simulates the behavior of a wheel 
	 */ 
	public class WheelParticle extends CircleParticle  {
	
		private final RimParticle rp;
		private final Vector tan;	
		private final Vector normSlip;
		
		//MvdA TODO make this fast
		private final ArrayList _edgePositions = new ArrayList();
		private final ArrayList _edgeParticles = new ArrayList();
		private int _traction;
		
	
		/**
		 * @param x The initial x position.
		 * @param y The initial y position.
		 * @param radius The radius of this particle.
		 * @param angle The rotation of this particle in radians.
		 * @param fixed Determines if the particle is fixed or not. Fixed particles
		 * are not affected by forces or collisions and are good to use as surfaces.
		 * Non-fixed particles move freely in response to collision and forces.
		 * @param mass The mass of the particle
		 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
		 * @param friction The surface friction of the particle. 
		 * <p>
		 * Note that WheelParticles can be fixed but still have their rotation property 
		 * changed.
		 * </p>
		 */
		public WheelParticle(
		float x, 
		float y, 
		float radius, 
		boolean fixed, 
		float mass, 
		float elasticity,
		float friction,
		float traction) {
	
			super(x,y,radius,fixed, mass, elasticity, friction);
			tan = Vector.getNew(0,0);
			normSlip = Vector.getNew(0,0);
			rp = new RimParticle(radius,FP.TWO); 	
			
			setTraction(FP.fromFloat(traction));
			
			initEdgePositions();
			initEdgeParticles();
		}	
	
		
		/**
		 * The angular velocity of the WheelParticle. You can alter this value to make the 
		 * WheelParticle spin.
		 */
		public final int getAngularVelocity() {
			return rp.getAngularVelocity();
		}
		
		public final void setAngularVelocity(int a) {
			rp.setAngularVelocity(a);
		}
	
		/**
		 * The amount of traction during a collision. This property controls how much traction is 
		 * applied when the WheelParticle is in contact with another particle. If the value is set
		 * to 0, there will be no traction and the WheelParticle will behave as if the 
		 * surface was totally slippery, like ice. Acceptable values are between 0 and 1. 
		 * 
		 * <p>
		 * Note that the friction property behaves differently than traction. If the surface 
		 * friction is set high during a collision, the WheelParticle will move slowly as if
		 * the surface was covered in glue.
		 * </p>
		 */		
		public final int getTraction() {
			return FP.ONE-_traction;
		}
			
		public final void setTraction(int t) {
			_traction = FP.ONE - t;
		}
		
		/**
		 * An Array of 4 contact particles on the rim of the wheel.  The edge particles
		 * are positioned relatively at 12, 3, 6, and 9 o'clock positions. You can attach other
		 * particles or constraints to these particles. Note this is a one-way effect, meaning the
		 * WheelParticle's motion will move objects attached to the edge particles, but the reverse
		 * is not true. 
		 * 
		 * <p>
		 * In order to access one of the 4 edge particles, you can use array notation 
		 * e.g., <code>myWheelParticle.edgeParticles[0]</code>
		 * </p>
		 */			
		public ArrayList initEdgeParticles() {
			
			if (_edgePositions.size() == 0) initEdgePositions();
			
			if (_edgeParticles.size() == 0) {
				CircleParticle cp1 = new CircleParticle(0.0f,0.0f,1.0f,false,1.0f,0.3f,0.0f);
				cp1.setCollidable(false);
				cp1.setVisible(false);
				APEngine.addParticle(cp1);
				
				CircleParticle cp2 = new CircleParticle(0.0f,0.0f,1.0f,false,1.0f,0.3f,0.0f);
				cp2.setCollidable(false);
				cp2.setVisible(false);
				APEngine.addParticle(cp2);
	
				CircleParticle cp3 = new CircleParticle(0.0f,0.0f,1.0f,false,1.0f,0.3f,0.0f);
				cp3.setCollidable(false);
				cp3.setVisible(false);
				APEngine.addParticle(cp3);
	
				CircleParticle cp4 = new CircleParticle(0.0f,0.0f,1.0f,false,1.0f,0.3f,0.0f);
				cp4.setCollidable(false);
				cp4.setVisible(false);
				APEngine.addParticle(cp4);
			
				_edgeParticles.add(cp1);
				_edgeParticles.add(cp2);
				_edgeParticles.add(cp3);
				_edgeParticles.add(cp4);
				
				updateEdgeParticles();
			}
			return _edgeParticles;
		}
	
		/**
		 * An Array of 4 <code>Vector</code> objects storing the location of the 4
		 * edge positions of this WheelParticle. The edge positions
		 * are located relatively at the 12, 3, 6, and 9 o'clock positions.
		 */
		public ArrayList initEdgePositions() {
					
			if (_edgePositions.size() == 0) {
				_edgePositions.add(Vector.getNew(0,0));
				_edgePositions.add(Vector.getNew(0,0));
				_edgePositions.add(Vector.getNew(0,0));
				_edgePositions.add(Vector.getNew(0,0));
						
				updateEdgePositions();
			}
			return _edgePositions;
		}
			
		@Override
		public void update(int dt) {
			super.update(dt);
			rp.update(dt);
			
			if (_edgePositions != null) updateEdgePositions();
			if (_edgeParticles != null) updateEdgeParticles();
		}
	
		@Override
		public void resolveCollision(
				int[] mtd, 
				int[] velocity, 
				int[] normal,
				int depth,
				int order) {
					
			super.resolveCollision(mtd, velocity, normal, depth, order);
			
			Vector tmp1 = Vector.getNew(0,0);
			//TODO int[]
//			resolve(normal.supply_mult(sign(FP.mul(depth,order)),tmp1));
			Vector.release(tmp1);
		}
		
	
		/**
		 * simulates torque/wheel-ground interaction - n is the surface normal
		 * Origins of this code thanks to Raigan Burns, Metanet software
		 */
		private void resolve(int[] n) {
	
			tan.setTo(-rp.curr.y, rp.curr.x);
	
			//MvdA TODO this should be checked to work
			//TODO int[]
//			tan.supply_normalize(tan);
	
			// velocity of the wheel's surface 
			Vector wheelSurfaceVelocity = Vector.getNew(0,0);
			//TODO int[]
//			tan.supply_mult(rp.getSpeed(),wheelSurfaceVelocity);
			
			// the velocity of the wheel's surface relative to the ground
			//MvdA TODO not tested yet
			final Vector combinedVelocity = Vector.getNew(0,0);
			//TODO int[]
//			supply_getVelocity(combinedVelocity);
			//TODO int[]
//			combinedVelocity.plusEquals(wheelSurfaceVelocity);
		
			// the wheel's comb velocity projected onto the contact normal
			//TODO int[]
//			int cp = combinedVelocity.cross(n);
	
			Vector.release(wheelSurfaceVelocity);
			Vector.release(combinedVelocity);
			
			// set the wheel's spinspeed to track the ground
			//TODO int[]
//			tan.multEquals(cp);
			Vector temp = Vector.getNew(0,0);
			//TODO int[]
//			rp.curr.supply_minus(tan,temp); 
			rp.prev.setTo(temp.x,temp.y);
			Vector.release(temp);
	
			// some of the wheel's torque is removed and converted into linear displacement
			int  slipSpeed = FP.mul((FP.ONE - _traction),rp.getSpeed());
			//TODO int[]
//			normSlip.setTo(FP.mul(slipSpeed,n.y), FP.mul(slipSpeed,n.x));
			//TODO int[]
//			curr.plusEquals(normSlip);
			rp.setSpeed(FP.mul(rp.getSpeed(),_traction));
		}
		
		/**
		 *
		 */	
		private void updateEdgePositions() {
			
			int px = curr[0];
			int py = curr[1];
			int rx = rp.curr.x;
			int ry = rp.curr.y;
			
			((Vector)_edgePositions.get(0)).setTo( rx + px,  ry + py);
			((Vector)_edgePositions.get(1)).setTo(-ry + px,  rx + py);
			((Vector)_edgePositions.get(2)).setTo(-rx + px, -ry + py);
			((Vector)_edgePositions.get(3)).setTo( ry + px, -rx + py);
		}

		private void updateEdgeParticles() {
			for (int i = 0; i < 4; i++) {
				((CircleParticle)_edgeParticles.get(i)).setpx( ((Vector)_edgePositions.get(i)).x ); 
				((CircleParticle)_edgeParticles.get(i)).setpy( ((Vector)_edgePositions.get(i)).y ); 
			}	
		}
		
		/**
		 * Returns 1 if the value is >= 0. Returns -1 if the value is < 0.
		 */	
		private int sign(int val) {
			if (val < 0) return -FP.ONE;
			return FP.ONE;
		}
	}


