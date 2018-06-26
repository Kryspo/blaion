/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.l2jserver.gameserver.model.base.ClassId;

/**
 * @author Administrator
 */
public enum MatchClassType
{
	HEALER(Arrays.asList(ClassId.cardinal, ClassId.evaSaint, ClassId.shillienSaint)),
	KNIGHT(Arrays.asList(ClassId.shillienTemplar, ClassId.phoenixKnight, ClassId.hellKnight, ClassId.evaTemplar)),
	WARRIOR(Arrays.asList(ClassId.doombringer, ClassId.fortuneSeeker, ClassId.maestro, ClassId.grandKhauatari, ClassId.titan, ClassId.duelist, ClassId.dreadnought)),
	WIZARD(Arrays.asList(ClassId.archmage, ClassId.soultaker, ClassId.mysticMuse, ClassId.stormScreamer)),
	SUMMONER(Arrays.asList(ClassId.arcanaLord, ClassId.elementalMaster, ClassId.spectralMaster)),
	BUFFER_F(Arrays.asList(ClassId.swordMuse, ClassId.spectralDancer, ClassId.judicator)),
	BUFFER_M(Arrays.asList(ClassId.dominator, ClassId.doomcryer)),
	ARCHER(Arrays.asList(ClassId.trickster, ClassId.sagittarius, ClassId.moonlightSentinel, ClassId.ghostSentinel)),
	ASSASSIN(Arrays.asList(ClassId.maleSoulhound, ClassId.femaleSoulhound, ClassId.ghostHunter, ClassId.adventurer, ClassId.windRider)),
	ALL(Arrays.asList(ClassId.values()));
	Collection<ClassId> classId = new ArrayList<>();
	
	MatchClassType(Collection<ClassId> classId)
	{
		this.classId.addAll(classId);
	}
	
	public Collection<ClassId> getClassList()
	{
		return classId;
	}
	
	public static MatchClassType getByName(String name)
	{
		for (MatchClassType t : values())
		{
			if (t.name().equals(name))
			{
				return t;
			}
		}
		return null;
	}
}
