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
	 * The abstract base class for all particles.
	 * 
	 * <p>
	 * You should not instantiate this class directly -- instead use one of the subclasses.
	 * </p>
	 */
	public abstract class AbstractParticle {
		
		
//		public final Vector curr;
		public final int[] curr = new int[2];
		
//		public final Vector prev;
		public final int[] prev = new int[2];
		
		
		public boolean isColliding;
		
		public final Interval interval;

		private final int[] forces = new int[2];
		
		private final int[] temp = new int[2];
		
		public AbstractParticle next = null;
		
		public int kfr;
		public int mass;
		public int invMass;
		public boolean fixed;
		public boolean visible;
		public int friction;
		public boolean collidable;
		private final Collision collision;
		
		private static final int[] nv = new int[2];
		private static final int[] vel = new int[2];
		
		public AbstractParticle (
				float x, 
				float y, 
				boolean isFixed, 
				float mass, 
				float elasticity,
				float friction) {
		
			interval = new Interval(0,0);
			
//			x*=APEngine.scale;
//			y*=APEngine.scale;
//			
//			mass*=APEngine.scale;
			
			curr[0] = FP.fromFloat(x);
			curr[1] = FP.fromFloat(y);
//			curr = Vector.getNew(FP.fromFloat(x),FP.fromFloat(y));
			prev[0] = FP.fromFloat(x);
			prev[1] = FP.fromFloat(y);
//			prev = Vector.getNew(FP.fromFloat(x),FP.fromFloat(y));
//			temp = Vector.getNew(0,0);
			
//			forces = Vector.getNew(0,0);
			collision = new Collision(new int[2], new int[2]);
			isColliding = false;
			
			setMass(FP.fromFloat(mass));
			setElasticity(FP.fromFloat(elasticity));
			setFriction(FP.fromFloat(friction));
			setFixed(isFixed);
			
			setCollidable(true);
			setVisible(true);
		}

		public abstract void drawParticle( Canvas c );
	
		/**
		 * The mass of the particle. Valid values are greater than zero. By default, all particles
		 * have a mass of 1. 
		 * 
		 * <p>
		 * The mass property has no relation to the size of the particle. However it can be
		 * easily simulated when creating particles. A simple example would be to set the 
		 * mass and the size of a particle to same value when you instantiate it.
		 * </p>
		 * @throws flash.errors.Error flash.errors.Error if the mass is set less than zero. 
		 */
		public final void setMass(int m) {
			if (m <= 0) throw new Error("mass may not be set <= 0"); 
			mass = m;
			invMass = FP.div(FP.ONE,mass);
		}	
	
		
		/**
		 * The elasticity of the particle. Standard values are between 0 and 1. 
		 * The higher the value, the greater the elasticity.
		 * 
		 * <p>
		 * During collisions the elasticity values are combined. If one particle's
		 * elasticity is set to 0.4 and the other is set to 0.4 then the collision will
		 * be have a total elasticity of 0.8. The result will be the same if one particle
		 * has an elasticity of 0 and the other 0.8.
		 * </p>
		 * 
		 * <p>
		 * Setting the elasticity to greater than 1 (of a single particle, or in a combined
		 * collision) will cause particles to bounce with energy greater than naturally 
		 * possible. Setting the elasticity to a value less than zero is allowed but may cause 
		 * unexpected results.
		 * </p>
		 */ 
		public final void setElasticity(int k) {
			kfr = k;
		}
		

		/**
		 * The visibility of the particle. This is only implemented for the default painting
		 * methods of the particles. When you create your painting methods in subclassed or
		 * composite particles, you should add a check for this property.
		 */		
		public final void setVisible(boolean v) {
			visible = v;
		}
		
				
		/**
		 * The surface friction of the particle. Values must be in the range of 0 to 1.
		 * 
		 * <p>
		 * 0 is no friction (slippery), 1 is full friction (sticky).
		 * </p>
		 * 
		 * <p>
		 * During collisions, the friction values are summed, but are clamped between 1 and 0.
		 * For example, If two particles have 0.7 as their surface friction, then the resulting
		 * friction between the two particles will be 1 (full friction).
		 * </p>
		 * 
		 * <p>
		 * Note: In the current release, only dynamic friction is calculated. Static friction
		 * is planned for a later release.
		 * </p>
		 * 
		 * @throws flash.errors.Error flash.errors.Error if the friction is set less than zero or greater than 1
		 */	
		public final void setFriction(int f) {
			if (f < 0 || f > FP.ONE) throw new Error("Legal friction must be >= 0 and <=1");
			friction = f;
		}
		
		/**
		 * The fixed state of the particle. If the particle is fixed, it does not move
		 * in response to forces or collisions. Fixed particles are good for surfaces.
		 */
		public void setFixed(boolean f) {
			if(f) {
				setMass(FP.HALF_VALUE);
			}
			fixed = f;
		}
		
		/**
		 * The position of the particle. Getting the position of the particle is useful
		 * for drawing it or testing it for some custom purpose. 
		 * 
		 * <p>
		 * When you get the <code>position</code> of a particle you are given a copy of the current
		 * location. Because of this you cannot change the position of a particle by
		 * altering the <code>x</code> and <code>y</code> components of the Vector you have retrieved from the position property.
		 * You have to do something instead like: <code> position = new Vector(100,100)</code>, or
		 * you can use the <code>px</code> and <code>py</code> properties instead.
		 * </p>
		 * 
		 * <p>
		 * You can alter the position of a particle three ways: change its position, set
		 * its velocity, or apply a force to it. Setting the position of a non-fixed particle
		 * is not the same as setting its fixed property to true. A particle held in place by 
		 * its position will behave as if it's attached there by a 0 length sprint constraint. 
		 * </p>
		 */
//		public final void setPosition(Vector p) {	
//			curr.setTo(p.x,p.y);
//			prev.setTo(p.x,p.y);
//		}
		
		public final void setPosition(int x, int y) {
			curr[0] = x;
			curr[1] = y;
			prev[0] = x;
			prev[1] = y;
		}

	
		/**
		 * The x position of this particle
		 */
		public final void setpx(int x) {
			curr[0] = x;
			prev[0] = x;
//			curr.x = x;
//			prev.x = x;	
		}


		/**
		 * The y position of this particle
		 */
		public final void setpy(int y) {
			curr[1] = y;
			prev[1] = y;
//			curr.y = y;
//			prev.y = y;	
		}


		/**
		 * The velocity of the particle. If you need to change the motion of a particle, 
		 * you should either use this property, or one of the addForce methods. Generally,
		 * the addForce methods are best for slowly altering the motion. The velocity property
		 * is good for instantaneously setting the velocity, e.g., for projectiles.
		 * 
		 */
//		public final Vector getVelocity() {
//			return curr.minus(prev);
//		}
		
		protected void supply_getVelocity(int[] result) {
			Vector.supply_minus(curr,prev,result);
		}
		
		protected final void setVelocity(int[] v) {
			prev[0] = curr[0] - v[0];
			prev[1] = curr[1] - v[1];
//			prev.setTo(curr.x - v.x, curr.y - v.y);    	
		}
		
//		public final void pool_setVelocity(Vector v) {
//			//pool_candidate
//			//pool_draw
//			prev = curr.pool_minus(v);	
//		}
		
		/**
		 * Determines if the particle can collide with other particles or constraints.
		 * The default state is true.
		 */
		public final void setCollidable(boolean b) {
			collidable = b;
		}
		
		/**
		 * Adds a force to the particle. The mass of the particle is taken into 
		 * account when using this method, so it is useful for adding forces 
		 * that simulate effects like wind. Particles with larger masses will
		 * not be affected as greatly as those with smaller masses. Note that the
		 * size (not to be confused with mass) of the particle has no effect 
		 * on its physical behavior.
		 * 
		 * @param force A Vector representing the force added.
		 */ 
		public final void addForce(int[] force) {
//			forces.plusEquals(f.multEquals(invMass));
			Vector.supply_mult(force, invMass, force);
			Vector.supply_plus(forces, force, forces);
		}
		
		
		/**
		 * Adds a 'massless' force to the particle. The mass of the particle is 
		 * not taken into account when using this method, so it is useful for
		 * adding forces that simulate effects like gravity. Particles with 
		 * larger masses will be affected the same as those with smaller masses.
		 *
		 * @param force A Vector representing the force added.
		 */ 	
		public final void addMasslessForce(int[] force) {
//			forces.plusEquals(f);
			Vector.supply_plus(forces, force, forces);
		}
		
		public void update(int dt2) {
			if (fixed) return;
			
			// global forces
			addForce(APEngine.force);
			addMasslessForce(APEngine.masslessForce);
	
			// integrate
			Vector.setTo(curr,temp);
//			temp.setTo(curr.x,curr.y);
//			
			supply_getVelocity(nv);
//			nv.supply_plus(forces.multEquals(dt2),nv);
//			forces.multEquals(dt2);
			Vector.supply_mult(forces, dt2, forces);
			Vector.supply_plus(nv,forces,nv);
			
//			curr.plusEquals(nv.multEquals(APEngine.damping));
			Vector.supply_mult(nv,APEngine.damping,nv);
			Vector.supply_plus(curr, nv, curr);
			

			Vector.setTo(temp,prev);
			
			// clear the forces
//			forces.setTo(0,0);
			forces[0] = 0;
			forces[1] = 0;
		}
			
		public final Collision getComponents(int[] collisionNormal) {
			supply_getVelocity(vel);

//			int vdotn = collisionNormal.dot(vel);
			int vdotn = Vector.dot(collisionNormal, vel);
			
//			collisionNormal.supply_mult(vdotn,collision.vn);
			Vector.supply_mult(collisionNormal, vdotn, collision.vn);
			
//			vel.supply_minus(collision.vn,collision.vt);
			Vector.supply_minus(vel, collision.vn, collision.vt);

			return collision;
		}
	
		public void resolveCollision(int[] mtd, int[] vel, int[] n, int d, int o) {	
//			curr.plusEquals(mtd);
			Vector.supply_plus(curr, mtd, curr);

			switch (APEngine.collisionResponseMode) {
				
				case APEngine.STANDARD:
					setVelocity(vel);
					break;
				
				case APEngine.SELECTIVE:
					if (!isColliding) setVelocity(vel);
					isColliding = true;
					break;
					
				case APEngine.SIMPLE:
					break;
			}
		}
//	
//		public abstract Interval getProjection(Vector axis);

		public void remove(AbstractParticle p, AbstractParticle previous) {
			if(p == this) {
				if(next != null) {
					previous.next = next;
				}
			}
			else if(next != null){
				next.remove(p,this);
			}
		}
	}	
