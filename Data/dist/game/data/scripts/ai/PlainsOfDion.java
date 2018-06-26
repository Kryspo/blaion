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

import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.util.Util;

/**
 * AI for mobs in Plains of Dion (near Floran Village).
 * @author Gladicek
 */
public final class PlainsOfDion extends L2AttackableAIScript
{
	private static final int DELU_LIZARDMEN[] =
	{
		21104, // Delu Lizardman Supplier
		21105, // Delu Lizardman Special Agent
		21107, // Delu Lizardman Commander
	};
	
	public PlainsOfDion()
	{
		super(-1, PlainsOfDion.class.getSimpleName(), "ai");
		AttackNpcs(DELU_LIZARDMEN);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			int i = getRandom(5);
			if (i < 2)
			{
				switch (getRandom(3))
				{
					case 1:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), player.getName() + "! How dare you interrupt our fight! Hey guys, help!"));
						break;
					case 2:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), player.getName() + "! Hey! We're having a duel here!"));
						break;
					case 3:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "The duel is over! Attack!"));
						break;
					case 4:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Foul! Kill the coward!"));
						break;
					case 5:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "How dare you interrupt a sacred duel! You must be taught a lesson!"));
						break;
				}
			}
			else
			{
				switch (getRandom(3))
				{
					case 1:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Die, you coward!"));
						break;
					case 2:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Kill the coward!"));
						break;
					case 3:
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "What are you looking at?"));
						break;
				}
			}
			
			for (L2Character obj : npc.getKnownList().getKnownCharactersInRadius(npc.getFactionRange()))
			{
				if (obj.isMonster() && Util.contains(DELU_LIZARDMEN, ((L2MonsterInstance) obj).getNpcId()) && !obj.isAttackingNow() && !obj.isDead() && GeoData.getInstance().canSeeTarget(npc, obj))
				{
					final L2MonsterInstance monster = (L2MonsterInstance) obj;
					attackPlayer(monster, player);
					switch (getRandom(3))
					{
						case 1:
							npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Die, you coward!"));
							break;
						case 2:
							npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Kill the coward!"));
							break;
						case 3:
							npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "What are you looking at?"));
							break;
					}
				}
			}
			npc.setScriptValue(1);
		}
		return super.onAttack(npc, player, damage, isSummon);
	}
}