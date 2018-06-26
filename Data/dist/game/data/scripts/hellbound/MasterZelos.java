/*
 * Copyright (C) 2004-2018 L2J Server
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
package hellbound;

import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class MasterZelos extends L2AttackableAIScript
{
	// DOORs
	private static final int DOOR_1 = 19260053;
	private static final int DOOR_2 = 19260054;
	
	// NPCs
	private static final int MASTER_ZELOS = 22377;
	
	public MasterZelos()
	{
		super(-1, MasterZelos.class.getSimpleName(), "hellbound");
		registerMobs(MASTER_ZELOS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "open_doors":
				DoorTable.getInstance().getDoor(DOOR_1).openMe();
				DoorTable.getInstance().getDoor(DOOR_2).openMe();
				startQuestTimer("close_doors", 600000, npc, null, false);
				break;
			case "close_doors":
				DoorTable.getInstance().getDoor(DOOR_1).closeMe();
				DoorTable.getInstance().getDoor(DOOR_2).closeMe();
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case MASTER_ZELOS:
				startQuestTimer("open_doors", 5000, npc, null, false);
				break;
		}
		return super.onKill(npc, killer, isPet);
	}
}
