/*
 * Copyright (C) 2004-2015 L2J DataPack
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

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * @author L2jPrivateDevelopersTeam
 */
public final class WarriorFishingBlock extends L2AttackableAIScript
{
	// Monsters
	private static final int[] MONSTERS =
	{
		18319, // Caught Frog
		18320, // Caught Undine
		18321, // Caught Rakul
		18322, // Caught Sea Giant
		18323, // Caught Sea Horse Soldier
		18324, // Caught Homunculus
		18325, // Caught Flava
		18326, // Caught Gigantic Eye
	};
	private static final String[] NPC_STRINGS_ON_ATTACK =
	{
		"Do you know what a frog tastes like?",
		"I will show you the power of a frog!",
		"I will swallow at a mouthful!"
	};
	private static final String[] NPC_STRINGS_ON_KILL =
	{
		"Ugh, no chance. How could this elder pass away like this!",
		"Croak! Croak! A frog is dying!",
		"A frog tastes bad! Yuck!"
	};
	// Misc
	private static final int CHANCE_TO_SHOUT_ON_ATTACK = 33;
	private static final int DESPAWN_TIME = 50; // 50 seconds to despawn
	
	public WarriorFishingBlock()
	{
		super(-1, WarriorFishingBlock.class.getSimpleName(), "ai");
		AttackNpcs(MONSTERS);
		KillNpcs(MONSTERS);
		SpawnNpcs(MONSTERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SPAWN":
			{
				final L2Object obj = npc.getTarget();
				if ((obj == null) || !obj.isPlayer())
				{
					npc.decayMe();
				}
				else
				{
					final L2PcInstance target = obj.getActingPlayer();
					switch (getRandom(3))
					{
						case 1:
							npc.sendPacket(new CreatureSay(npc.getNpcId(), 1, npc.getName(), "Croak, Croak! Food like in this place?!"));
							break;
						case 2:
							npc.sendPacket(new CreatureSay(npc.getNpcId(), 1, npc.getName(), "How lucky I am!"));
							break;
						case 3:
							npc.sendPacket(new CreatureSay(npc.getNpcId(), 1, npc.getName(), "HPray that you caught a wrong fish!"));
							break;
					}
					((L2Attackable) npc).addDamageHate(target, 0, 2000);
					npc.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, target);
					npc.addAttackerToAttackByList(target);
					
					startQuestTimer("DESPAWN", DESPAWN_TIME * 1000, npc, target);
				}
				break;
			}
			case "DESPAWN":
			{
				npc.decayMe();
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (getRandom(100) < CHANCE_TO_SHOUT_ON_ATTACK)
		{
			broadcastNpcSay(npc, Say2.ALL, NPC_STRINGS_ON_ATTACK[getRandom(NPC_STRINGS_ON_ATTACK.length)]);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		broadcastNpcSay(npc, Say2.ALL, NPC_STRINGS_ON_KILL[getRandom(NPC_STRINGS_ON_KILL.length)]);
		cancelQuestTimer("DESPAWN", npc, killer);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		startQuestTimer("SPAWN", 2000, npc, null);
		return super.onSpawn(npc);
	}
}
