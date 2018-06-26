/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package ai.Nons;

import com.l2jserver.gameserver.model.actor.L2Npc;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class NoAttacking extends L2AttackableAIScript
{
	// @formatter:off
	private static final int[] NON_ATTACKING_NPCS =
	{
		// Fairy Trees
		27185, 27186, 27187, 27188,
		// Treasure of the festival
		18811
	};
	// @formatter:on
	
	public NoAttacking()
	{
		super(-1, NoAttacking.class.getSimpleName(), "ai/Nons");
		SpawnNpcs(NON_ATTACKING_NPCS);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.disableCoreAI(true);
		npc.setIsImmobilized(true);
		return super.onSpawn(npc);
	}
}
