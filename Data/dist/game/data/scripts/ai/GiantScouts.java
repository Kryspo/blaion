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
package ai;

import java.util.Collection;

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class GiantScouts extends L2AttackableAIScript
{
	// NPC
	final private static int SCOUTS[] =
	{
		22668, // Gamlin (Scout)
		22669, // Leogul (Scout)
	};
	
	public GiantScouts()
	{
		super(-1, GiantScouts.class.getSimpleName(), "ai");
		AggroRangeEnterNpcs(SCOUTS);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2Character target = isPet ? player.getPet() : player;
		
		if (GeoData.getInstance().canSeeTarget(npc, target))
		{
			if (!npc.isInCombat() && (npc.getTarget() == null))
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.SHOUT, npc.getName(), "Oh Giants, an intruder has been discovered."));
			}
			
			npc.setTarget(target);
			npc.setRunning();
			((L2Attackable) npc).addDamageHate(target, 0, 999);
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
			
			// Notify clan
			Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
			for (L2Object obj : objs)
			{
				if (obj != null)
				{
					if (obj instanceof L2MonsterInstance)
					{
						L2MonsterInstance monster = (L2MonsterInstance) obj;
						if (((npc.getClan() != null) && (monster.getClan() != null)) && monster.getClan().equals(npc.getClan()) && GeoData.getInstance().canSeeTarget(npc, monster))
						{
							monster.setTarget(target);
							monster.setRunning();
							monster.addDamageHate(target, 0, 999);
							monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
						}
					}
					
				}
			}
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
}