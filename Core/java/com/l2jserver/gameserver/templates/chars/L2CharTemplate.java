/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.templates.chars;

import com.l2jserver.gameserver.templates.StatsSet;

/**
 * This class ...
 * @version $Revision: 1.2.4.6 $ $Date: 2005/04/02 15:57:51 $
 */
public class L2CharTemplate
{
	// BaseStats
	public final int baseSTR;
	public final int baseCON;
	public final int baseDEX;
	public final int baseINT;
	public final int baseWIT;
	public final int baseMEN;
	public float baseHpMax;
	public final float baseCpMax;
	public final float baseMpMax;
	
	/** HP Regen base */
	public final float baseHpReg;
	
	/** MP Regen base */
	public final float baseMpReg;
	
	public final int basePAtk;
	public final int baseMAtk;
	public final int basePDef;
	public final int baseMDef;
	public final int basePAtkSpd;
	public final int baseMAtkSpd;
	public final float baseMReuseRate;
	public final int baseShldDef;
	public final int baseAtkRange;
	public final int baseShldRate;
	public final int baseCritRate;
	public final int baseMCritRate;
	public final int baseWalkSpd;
	public final int baseRunSpd;
	
	// SpecialStats
	public final int baseBreath;
	public final int baseAggression;
	public final int baseBleed;
	public final int basePoison;
	public final int baseStun;
	public final int baseRoot;
	public final int baseMovement;
	public final int baseConfusion;
	public final int baseSleep;
	public final double baseAggressionVuln;
	public final double baseBleedVuln;
	public final double basePoisonVuln;
	public final double baseStunVuln;
	public final double baseRootVuln;
	public final double baseMovementVuln;
	public final double baseSleepVuln;
	public int baseFire;
	public int baseWind;
	public int baseWater;
	public int baseEarth;
	public int baseHoly;
	public int baseDark;
	public double baseFireRes;
	public double baseWindRes;
	public double baseWaterRes;
	public double baseEarthRes;
	public double baseHolyRes;
	public double baseDarkRes;
	public final double baseCritVuln;
	
	// C4 Stats
	public final int baseMpConsumeRate;
	public final int baseHpConsumeRate;
	
	public final int collisionRadius;
	
	public final int collisionHeight;
	
	public final double fCollisionRadius;
	public final double fCollisionHeight;
	
	public L2CharTemplate(StatsSet set)
	{
		// Base stats
		baseSTR = set.getInt("baseSTR");
		baseCON = set.getInt("baseCON");
		baseDEX = set.getInt("baseDEX");
		baseINT = set.getInt("baseINT");
		baseWIT = set.getInt("baseWIT");
		baseMEN = set.getInt("baseMEN");
		baseHpMax = set.getFloat("baseHpMax");
		baseCpMax = set.getFloat("baseCpMax");
		baseMpMax = set.getFloat("baseMpMax");
		baseHpReg = set.getFloat("baseHpReg");
		baseMpReg = set.getFloat("baseMpReg");
		basePAtk = set.getInt("basePAtk");
		baseMAtk = set.getInt("baseMAtk");
		basePDef = set.getInt("basePDef");
		baseMDef = set.getInt("baseMDef");
		basePAtkSpd = set.getInt("basePAtkSpd");
		baseMAtkSpd = set.getInt("baseMAtkSpd");
		baseMReuseRate = set.getFloat("baseMReuseDelay", 1.f);
		baseShldDef = set.getInt("baseShldDef");
		baseAtkRange = set.getInt("baseAtkRange");
		baseShldRate = set.getInt("baseShldRate");
		baseCritRate = set.getInt("baseCritRate");
		baseMCritRate = set.getInt("baseMCritRate", 80); // CT2: The magic critical rate has been increased to 10 times.
		baseWalkSpd = set.getInt("baseWalkSpd");
		baseRunSpd = set.getInt("baseRunSpd");
		
		// SpecialStats
		baseBreath = set.getInt("baseBreath", 100);
		baseAggression = set.getInt("baseAggression", 0);
		baseBleed = set.getInt("baseBleed", 0);
		basePoison = set.getInt("basePoison", 0);
		baseStun = set.getInt("baseStun", 0);
		baseRoot = set.getInt("baseRoot", 0);
		baseMovement = set.getInt("baseMovement", 0);
		baseConfusion = set.getInt("baseConfusion", 0);
		baseSleep = set.getInt("baseSleep", 0);
		baseFire = set.getInt("baseFire", 0);
		baseWind = set.getInt("baseWind", 0);
		baseWater = set.getInt("baseWater", 0);
		baseEarth = set.getInt("baseEarth", 0);
		baseHoly = set.getInt("baseHoly", 0);
		baseDark = set.getInt("baseDark", 0);
		baseAggressionVuln = set.getInt("baseAggressionVuln", 0);
		baseBleedVuln = set.getInt("baseBleedVuln", 0);
		basePoisonVuln = set.getInt("basePoisonVuln", 0);
		baseStunVuln = set.getInt("baseStunVuln", 0);
		baseRootVuln = set.getInt("baseRootVuln", 0);
		baseMovementVuln = set.getInt("baseMovementVuln", 0);
		baseSleepVuln = set.getInt("baseSleepVuln", 0);
		baseFireRes = set.getInt("baseFireRes", 0);
		baseWindRes = set.getInt("baseWindRes", 0);
		baseWaterRes = set.getInt("baseWaterRes", 0);
		baseEarthRes = set.getInt("baseEarthRes", 0);
		baseHolyRes = set.getInt("baseHolyRes", 0);
		baseDarkRes = set.getInt("baseDarkRes", 0);
		baseCritVuln = set.getInt("baseCritVuln", 1);
		
		// C4 Stats
		baseMpConsumeRate = set.getInt("baseMpConsumeRate", 0);
		baseHpConsumeRate = set.getInt("baseHpConsumeRate", 0);
		
		// Geometry
		fCollisionHeight = set.getDouble("collision_height");
		fCollisionRadius = set.getDouble("collision_radius");
		collisionRadius = (int) fCollisionRadius;
		collisionHeight = (int) fCollisionHeight;
	}
	
	/**
	 * @return the basePAtk
	 */
	public int getBasePAtk()
	{
		return basePAtk;
	}
	
	/**
	 * @return the baseMAtk
	 */
	public int getBaseMAtk()
	{
		return baseMAtk;
	}
	
	/**
	 * @return the basePDef
	 */
	public int getBasePDef()
	{
		return basePDef;
	}
	
	/**
	 * @return the baseMDef
	 */
	public int getBaseMDef()
	{
		return baseMDef;
	}
}
