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

	
	// NEED TO EXCLUDE VELOCITY CALCS BASED ON collisionResponseMode
	public final class CollisionResolver {
		
		public static final int[] tmp1 = new int[2];
		public static final int[] tmp2 = new int[2];
		public static final int[] mtd = new int[2];
		public static final int[] mtdA = new int[2];
		public static final int[] mtdB = new int[2];
		public static final int[] vnA = new int[2];
		public static final int[] vnB = new int[2];
		
		public static final void resolveParticleParticle(
				AbstractParticle pa, 
				AbstractParticle pb, 
				int[] normal, 
				int depth) {
			
//			normal.supply_mult(depth,mtd);
			Vector.supply_mult(normal,depth,mtd);
			
			int te = pa.kfr + pb.kfr;
			
			// the total friction in a collision is combined but clamped to [0,1]
			int tf = FP.ONE - (pa.friction + pb.friction);
			if (tf > FP.ONE) {
				tf = FP.ONE;
			}
			else if (tf < 0) {
				tf = 0;
			}
		
			// get the total mass
			int ma = pa.mass;
			int mb = pb.mass;
			int tm = pa.mass + pb.mass;
			//MvdA have assigned a large mass to fixed particles in setFixed(boolean)
			
			// get the collision components, vn and vt
			Collision ca = pa.getComponents(normal);
			Collision cb = pb.getComponents(normal);
		 
		 	// calculate the coefficient of restitution based on the mass
//			
//			Vector.supply_mult(cb.vn,FP.mul((te + FP.ONE),mb), tmp1);
//			Vector.supply_mult(ca.vn,ma - FP.mul(te,mb), tmp2);
//			Vector.supply_plus(tmp1,tmp2,vnA);	
//			Vector.supply_div(tmp1,tm,vnA);
//			
//			
//			Vector.supply_mult(ca.vn,FP.mul((te + FP.ONE),ma), tmp1);
//			Vector.supply_mult(cb.vn,mb - FP.mul(te,ma), tmp2);
//			Vector.supply_plus(tmp1,tmp2,vnB);	
//			Vector.supply_div(tmp1,tm,vnB);
//			
//			(cb.vn.supply_mult(FP.mul((te + FP.ONE),mb), tmp1).supply_plus(ca.vn.supply_mult(ma - FP.mul(te,mb), tmp2),vnA)).divEquals(tm);		
//			(ca.vn.supply_mult(FP.mul((te + FP.ONE),ma), tmp1).supply_plus(cb.vn.supply_mult(mb - FP.mul(te,ma), tmp2),vnB)).divEquals(tm);
//			
			Vector.supply_mult(cb.vn,FP.mul((te + FP.ONE),mb), tmp1);
			Vector.supply_mult(ca.vn,FP.mul(ma - te,mb), tmp2);
			Vector.supply_plus(tmp1,tmp2,vnA);	
			Vector.supply_div(tmp1,tm,vnA);
			
			
			Vector.supply_mult(ca.vn,FP.mul((te + FP.ONE),ma), tmp1);
			Vector.supply_mult(cb.vn,FP.mul(mb - te,ma), tmp2);
			Vector.supply_plus(tmp1,tmp2,vnB);	
			Vector.supply_div(tmp1,tm,vnB);
			
			
			
			
//			(cb.vn.supply_mult(FP.mul((te + FP.ONE),mb), tmp1).supply_plus(ca.vn.supply_mult(ma - FP.mul(te,mb), tmp2),vnA)).divEquals(tm);		
//			(ca.vn.supply_mult(FP.mul((te + FP.ONE),ma), tmp1).supply_plus(cb.vn.supply_mult(mb - FP.mul(te,ma), tmp2),vnB)).divEquals(tm);
//			(cb.vn.supply_mult(FP.mul((te + FP.ONE),mb), tmp1).supply_plus(ca.vn.supply_mult(FP.mul(ma - te,mb), tmp2),vnA)).divEquals(tm);		
//			(ca.vn.supply_mult(FP.mul((te + FP.ONE),ma), tmp1).supply_plus(cb.vn.supply_mult(FP.mul(mb - te,ma), tmp2),vnB)).divEquals(tm);
			
//			ca.vt.multEquals(tf);
//			cb.vt.multEquals(tf);
//			Vector.supply_mult(ca.vt,tf,ca.vt);
//			Vector.supply_mult(cb.vt,tf,cb.vt);
			
			
			// scale the mtd by the ratio of the masses. heavier particles move less
//			mtd.supply_mult(FP.div(mb,tm),mtdA);
			Vector.supply_mult(mtd,FP.div(mb,tm),mtdA);

//			//TODO test
//			mtd.supply_mult(-FP.div(ma,tm),mtdB);
//			mtd.supply_mult(FP.div(-ma,tm),mtdB);
			Vector.supply_mult(mtd,-FP.div(ma,tm),mtdB);
			
			
			// TODO: Igor hacking away to get fixed AudioParticles to sound upon collision
			//if (! pa.fixed) {
				Vector.supply_plus(vnA,ca.vt,vnA);
				pa.resolveCollision(mtdA, vnA, normal, depth, -FP.ONE);
			//}
			//if (! pb.fixed) {
				Vector.supply_plus(vnB,cb.vt,vnB);
				pb.resolveCollision(mtdB, vnB, normal, depth, FP.ONE);
			//}
//			if (! pa.fixed) pa.resolveCollision(mtdA, vnA.plusEquals(ca.vt), normal, depth, -FP.ONE);
//			if (! pb.fixed) pb.resolveCollision(mtdB, vnB.plusEquals(cb.vt), normal, depth, FP.ONE);
		}
	}

