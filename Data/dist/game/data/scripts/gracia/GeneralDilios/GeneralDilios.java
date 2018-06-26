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
package gracia.GeneralDilios;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;

import ai.L2AttackableAIScript;

/**
 * Dilios AI
 * @author JIV, Sephiroth, Apocalipce
 */
public final class GeneralDilios extends L2AttackableAIScript
{
	// NPC
	private static final int GENERAL_ID = 32549;
	private static final int GUARD_ID = 32619;
	
	private L2Npc _general = null;
	private final Set<L2Spawn> _guards = Collections.newSetFromMap(new ConcurrentHashMap<L2Spawn, Boolean>());
	
	private static final String[] DILIOS_TEXT =
	{
		"Messenger, inform the patrons of the Keucereus Alliance Base! We're gathering brave adventurers to attack Tiat's Mounted Troop that's rooted in the Seed of Destruction.",
		"Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Destruction is currently secured under the flag of the Keucereus Alliance!",
		"Messenger, inform the patrons of the Keucereus Alliance Base! Tiat's Mounted Troop is currently trying to retake Seed of Destruction! Commit all the available reinforcements into Seed of Destruction!",
		"Messenger, inform the brothers in Kucereus' clan outpost! Brave adventurers who have challenged the Seed of Infinity are currently infiltrating the Hall of Erosion through the defensively weak Hall of Suffering!",
		"Messenger, inform the patrons of the Keucereus Alliance Base! The Seed of Infinity is currently secured under the flag of the Keucereus Alliance!",
		"Messenger, inform the patrons of the Keucereus Alliance Base! The resurrected Undead in the Seed of Infinity are pouring into the Hall of Suffering and the Hall of Erosion!",
		"Messenger, inform the brothers in Kucereus' clan outpost! Ekimus is about to be revived by the resurrected Undead in Seed of Infinity. Send all reinforcements to the Heart and the Hall of Suffering!",
	};
	
	public GeneralDilios()
	{
		super(-1, GeneralDilios.class.getSimpleName(), "gracia");
		SpawnNpcs(GENERAL_ID, GUARD_ID);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "command_":
				int value8 = Integer.parseInt(event.substring(8));
				if (value8 < 6)
				{
					_general.broadcastPacket(new CreatureSay(_general.getObjectId(), 1, npc.getName(), "Stabbing three times!"));
					startQuestTimer("guard_animation_0", 3400, null, null);
				}
				else
				{
					value8 = -1;
					_general.broadcastPacket(new CreatureSay(_general.getObjectId(), 1, npc.getName(), DILIOS_TEXT[getRandom(DILIOS_TEXT.length)]));
				}
				startQuestTimer("command_" + (value8 + 1), 60000, null, null);
				break;
			case "guard_animation_":
				int value16 = Integer.parseInt(event.substring(16));
				for (L2Spawn guard : _guards)
				{
					guard.getLastSpawn().broadcastPacket(new SocialAction(npc, 4));
				}
				if (value16 < 2)
				{
					startQuestTimer("guard_animation_" + (value16 + 1), 1500, null, null);
				}
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case GENERAL_ID:
				startQuestTimer("command_0", 60000, null, null);
				_general = npc;
				break;
			case GUARD_ID:
				_guards.add(npc.getSpawn());
				break;
		}
		return super.onSpawn(npc);
	}
}
