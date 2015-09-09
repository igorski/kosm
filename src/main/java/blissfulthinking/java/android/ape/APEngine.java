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
//package org.cove.ape {
package blissfulthinking.java.android.ape;
	
import blissfulthinking.java.android.apeforandroid.FP;

import java.util.ArrayList;


/**
	 * The main engine class. All particles and constraints should be added and removed
	 * through this class.
	 * 
	 */
	public final class APEngine {
		public static final int STANDARD = 100;
		public static final int SELECTIVE = 200;
		public static final int SIMPLE = 300;
		
//		public static final float scale = 2.0f;
		
		public static final int[] force = new int[2];
		
		public static AbstractParticle particles = null;
		
		public static final int[] masslessForce = new int[2];
			
		public static int timeStep;
//		private static final ArrayList<AbstractParticle> particles = new ArrayList<AbstractParticle>();
		
		private static final ArrayList<AbstractConstraint> constraints = new ArrayList<AbstractConstraint>();
		
		public static int damping;

		public static int collisionResponseMode = STANDARD;
		
		public final void setMasslessForce(int x, int y) {
//			masslessForce.setTo(x,y);
			masslessForce[0] = x;
			masslessForce[1] = y;
		}
		
		/**
		 * Initializes the engine. You must call this method prior to adding
		 * any particles or constraints.
		 * 
		 * @param dt The delta time value for the engine. This parameter can be used -- in 
		 * conjunction with speed at which <code>APEngine.step()</code> is called -- to change the speed
		 * of the simulation. Typical values are 1/3 or 1/4. Lower values result in slower,
		 * but more accurate simulations, and higher ones result in faster, less accurate ones.
		 * Note that this only applies to the forces added to particles. If you do not add any
		 * forces, the <code>dt</code> value won't matter.
		 */
		public static final void init(int dt) {
			timeStep = FP.mul(dt, dt);

			damping = FP.ONE;
		}


		/**
		 * The global damping. Values should be between 0 and 1. Higher numbers
		 * result in less damping. A value of 1 is no damping. A value of 0 will
		 * not allow any particles to move. The default is 1.
		 * 
		 * <p>
		 * Damping will slow down your simulation and make it more stable. If you find
		 * that your sim is "blowing up', try applying more damping. 
		 * </p>
		 * 
		 * @param d The damping value. Values should be >=0 and <=1.
		 */
		public static final void setDamping(int d) {
			damping = d;
		}
				
		/**
		 * The collision response mode for the engine. The engine has three different possible
		 * settings for the collisionResponseMode property. Valid values are APEngine.STANDARD, 
		 * APEngine.SELECTIVE, and APEngine.SIMPLE. Those settings go in order from slower and
		 * more accurate to faster and less accurate. In all cases it's worth experimenting to
		 * see what mode suits your sim best.  
		 *
		 * <ul>
		 * <li>
		 * <b>APEngine.STANDARD</b>&mdash;Particles are moved out of collision and then velocity is 
		 * applied. Momentum is conserved and the mass of the particles is properly calculated. This
		 * is the default and most physically accurate setting.<br/><br/>
		 * </li>
		 * 
		 * <li>
		 * <b>APEngine.SELECTIVE</b>&mdash;Similar to the APEngine.STANDARD setting, except only 
		 * previously non-colliding particles have their velocity set. In otherwords, if there are 
		 * multiple collisions on a particle, only the first collision on that particle causes a 
		 * change in its velocity. Both this and the APEngine.SIMPLE setting may give better results
		 * than APEngine.STANDARD when using a large number of colliding particles.<br/><br/>
		 * </li>
		 * 
		 * <li>
		 * <b>APEngine.SIMPLE</b>&mdash;Particles do not have their velocity set after colliding. This
		 * is faster than the other two modes but is the least accurate. Mass is not calculated, and 
		 * there is no conservation of momentum. <br/><br/>
		 * </li>
		 * </ul>
		 */	
		public static final void setCollisionResponseMode(int m) {
			collisionResponseMode = m;	
		}
		
			
		/**
		 * Adds a force to all particles in the system. The mass of the particle is taken into 
		 * account when using this method, so it is useful for adding forces that simulate effects
		 * like wind. Particles with larger masses will not be affected as greatly as those with
		 * smaller masses. Note that the size (not to be confused with mass) of the particle has
		 * no effect on its physical behavior.
		 * 
		 * @param f A Vector represeting the force added.
		 */ 
		public static final void addForce(int x, int y) {
//			force.plusEquals(vx,vy);
			force[0] = force[0] + x;
			force[1] = force[1] + y;
		}
		
		/**
		 * Adds a 'massless' force to all particles in the system. The mass of the particle is 
		 * not taken into account when using this method, so it is useful for adding forces that
		 * simulate effects like gravity. Particles with larger masses will be affected the same
		 * as those with smaller masses. Note that the size (not to be confused with mass) of 
		 * the particle has no effect on its physical behavior.
		 * 
		 * @param f A Vector represeting the force added.
		 */ 	
//		public final void addMasslessForce(int x, int y) {
////			masslessForce.plusEquals(vx,vy);
//			masslessForce[0] = masslessForce[0] + x;
//			masslessForce[1] = masslessForce[1] + y;
//		}
		
		/**
		 * Adds a particle to the engine.
		 * 
		 * @param p The particle to be added.
		 */
		public static final void addParticle(AbstractParticle p) {
			if(particles != null) {
				p.next = particles;
			}
			particles = p;
//			particles.add(p); // adds to the end of the list http://java.sun.com/j2se/1.4.2/docs/api/java/util/ArrayList.html		
		}
		
		
		/**
		 * Removes a particle to the engine.
		 * 
		 * @param p The particle to be removed.
		 */
		public static final void removeParticle(AbstractParticle p) {
			if(particles != null) {
				particles.remove(p,null);
			}
//			int ppos = particles.indexOf(p);
//			if (ppos == -1) return;
//			particles.remove(ppos);
		}
		
		
		/**
		 * Adds a constraint to the engine.
		 * 
		 * @param c The constraint to be added.
		 */
		public static final void addConstraint(AbstractConstraint c) {
			constraints.add(c); // adds to the end of the list http://java.sun.com/j2se/1.4.2/docs/api/java/util/ArrayList.html
		}


		/**
		 * Removes a constraint from the engine.
		 * 
		 * @param c The constraint to be removed.
		 */
		public static final void removeConstraint(AbstractConstraint c) {
			int cpos = constraints.indexOf(c);
			if (cpos == -1) return;
			constraints.remove(cpos);
		}
	
	//MvdA Removed this since it wasn't used and using this method's result impacts performance
//		/**
//		 * Returns an array of every item added to the engine. This includes all particles and
//		 * constraints.
//		 */
//		public static ArrayList<Object> getAll() {
//			ArrayList a = (ArrayList)particles.clone(); // I have added this line
//			a.addAll(constraints);
//			return a;
//		}
	
//		/**
//		 * Returns an array of every particle added to the engine.
//		 */
//		public static final ArrayList<AbstractParticle> getAllParticles() {
//			return particles;
//		}	
		
		public static final AbstractParticle getFirstParticle() {
			return particles;
		}
		
//		/**
//		 * Returns an array of every custom particle added to the engine. A custom
//		 * particle is defined as one that is not an instance of the included particle
//		 * classes. If you create subclasses of any of the included particle classes, and
//		 * add them to the engine using <code>addParticle(...)</code> then they will be
//		 * returned by this method. This way you can keep a list of particles you have
//		 * created, if you need to distinguish them from the built in particles.
//		 */	
//		public static ArrayList getCustomParticles() {
//			ArrayList customParticles = new ArrayList();
//			for (int i = 0; i < particles.size(); i++) {
//				AbstractParticle p = (AbstractParticle)particles.get(i);
//				if (isCustomParticle(p)) customParticles.add(p);
//			}
//			return customParticles;
//		}
//		
//		
//		/**
//		 * Returns an array of particles added to the engine whose type is one of the built-in
//		 * particle types in the APE. This includes the CircleParticle, WheelParticle, and
//		 * RectangleParticle.
//		 */			
//		public static ArrayList getAPEParticles() {
//			ArrayList apeParticles = new ArrayList();
//			for (int i = 0; i < particles.size(); i++) {
//				AbstractParticle p = (AbstractParticle)particles.get(i);
//				if (! isCustomParticle(p)) apeParticles.add(p);
//			}
//			return apeParticles;
//		}
			
		/**
		 * Returns an array of all the constraints added to the engine
		 */						
		public static final ArrayList<AbstractConstraint> getAllConstraints() {
			return constraints;
		}	
	

		/**
		 * The main step function of the engine. This method should be called
		 * continously to advance the simulation. The faster this method is 
		 * called, the faster the simulation will run. Usually you would call
		 * this in your main program loop. 
		 */			
		public static final void step() {
			integrate();
			satisfyConstraints();
			checkCollisions();
		}
		
//		private static boolean isCustomParticle(AbstractParticle p) {
//			boolean isWP = false;
//			boolean isCP = false;
//			boolean isRP = false;
//			//TG TODO this needs to be tested : p.getClass().getName()
//			String className = p.getClass().getName();
//			if (className == "org.cove.ape::WheelParticle") isWP=true;
//			if (className == "org.cove.ape::CircleParticle") isCP=true;
//			if (className == "org.cove.ape::RectangleParticle") isRP=true;
//
//			if (! (isWP || isCP || isRP)) return true;
//			return false;		
//		}

		private static final void integrate() {
//			int size = particles.size();
			if(particles == null) {
				return;
			}
			AbstractParticle current = particles;
			while(current != null) {
				if (current instanceof RectangleParticle) {  
					((RectangleParticle)current).update(timeStep);
				}
				else if (current instanceof CircleParticle) {
					((CircleParticle)current).update(timeStep);
				}
				current = current.next;
			}
//			for (int i = 0; i < size; i++) {
//				if (particles.get(i) instanceof RectangleParticle)  
//					((RectangleParticle)particles.get(i)).update(timeStep);
//				else if (particles.get(i) instanceof CircleParticle) 
//					((CircleParticle)particles.get(i)).update(timeStep);
//			}
		}
	
		private static final void satisfyConstraints() {
			int size = constraints.size();
			for (int n = 0; n < size; n++) {
				constraints.get(n).resolve();
			}
		}
	
		/**
		 * Checks all collisions between particles and constraints. The following rules apply: 
		 * Particles vs Particles are tested unless either collidable property is set to false.
		 * Particles vs Constraints are not tested by default unless collidable is true.
		 * is called on a SpringConstraint. AngularConstraints are not tested for collision,
		 * but their component SpringConstraints are -- with the previous rule in effect. If
		 * a Particle is attached to a SpringConstraint it is never tested against that 
		 * SpringConstraint for collision
		 */
		private static final void checkCollisions() {
			if(particles == null) {
				return;
			}
//			int size0 = particles.size();
			int size1 = constraints.size();

			AbstractParticle current0 = particles;
			AbstractParticle current1;
			while(current0 != null) {
				
				current1 = current0.next;
				while(current1 != null) {
//					if (current0.collidable && current1.collidable) {
						CollisionDetector.test(current0,current1);
						current1 = current1.next;
//					}
				}
				
				for (int n = 0; n < size1; n++) {
					//MvdA TODO was dead code
//					if (constraints.get(n) instanceof AngularConstraint) continue;
					SpringConstraint c = (SpringConstraint)constraints.get(n);
					if (current0.collidable && c.collidable && ! c.isConnectedTo(current0)) {
						CollisionDetector.test(current0,c.getCollisionRect());
					}
				}
				current0.isColliding = false;	
				
				current0 = current0.next;
			}
//			for (int j = 0; j < size0; j++) {
//				
//				AbstractParticle pa = particles.get(j);
//				
//				for (int i = j + 1; i < size0; i++) {
//					AbstractParticle pb = particles.get(i);
//					if (pa.collidable && pb.collidable) {
//						CollisionDetector.test(pa, pb);
//					}
//				}
//				
//				for (int n = 0; n < size1; n++) {
//					//MvdA TODO was dead code
////					if (constraints.get(n) instanceof AngularConstraint) continue;
//					SpringConstraint c = (SpringConstraint)constraints.get(n);
//					if (pa.collidable && c.collidable && ! c.isConnectedTo(pa)) {
//						CollisionDetector.test(pa,c.getCollisionRect());
//					}
//				}
//				pa.isColliding = false;	
//			}
		}
	}	
