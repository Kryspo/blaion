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
package gracia.DestroyedTumors;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

public class DestroyedTumors extends Quest
{
	// NPC
	private static final int DESTROYED_TUMORS = 32535;
	private static final int DESTROYED_TUMORS_2 = 32536;
	
	// ITEMS
	private static final int TEAR_OF_A_FREED_SOUL = 13797;
	
	private long warpTimer = 0;
	
	public DestroyedTumors()
	{
		super(-1, DestroyedTumors.class.getSimpleName(), "gracia");
		StartNpcs(DESTROYED_TUMORS, DESTROYED_TUMORS_2);
		FirstTalkNpcs(DESTROYED_TUMORS, DESTROYED_TUMORS_2);
		TalkNpcs(DESTROYED_TUMORS, DESTROYED_TUMORS_2);
		warpTimer = System.currentTimeMillis();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if ((world != null) && (world.templateId == 119))
		{
			switch (event)
			{
				case "examine_tumor":
					if ((player.getParty() == null) || (player.getParty().getLeader() != player))
					{
						htmltext = "32535-2.htm";
					}
					else
					{
						htmltext = "32535-1.htm";
					}
					break;
				case "showcheckpage":
					if (player.getInventory().getItemByItemId(TEAR_OF_A_FREED_SOUL) == null)
					{
						htmltext = "32535-6.htm";
					}
					else if ((warpTimer + 60000) > System.currentTimeMillis())
					{
						htmltext = "32535-4.htm";
					}
					else if (world.tag <= 0)
					{
						htmltext = "32535-3.htm";
					}
					else
					{
						htmltext = "32535-5a.htm";
					}
					break;
			}
		}
		else if ((world != null) && (world.templateId == 120))
		{
			switch (event)
			{
				case "examine_tumor":
					if ((player.getParty() == null) || (player.getParty().getLeader() != player))
					{
						htmltext = "32535-2.htm";
					}
					else
					{
						htmltext = "32535-1.htm";
					}
					break;
				case "showcheckpage":
					if (player.getInventory().getItemByItemId(TEAR_OF_A_FREED_SOUL) == null)
					{
						htmltext = "32535-6.htm";
					}
					else if ((warpTimer + 60000) > System.currentTimeMillis())
					{
						htmltext = "32535-4.htm";
					}
					else if (world.tag <= 0)
					{
						htmltext = "32535-3.htm";
					}
					else
					{
						htmltext = "32535-5b.htm";
					}
					break;
			}
		}
		else if ((world != null) && (world.templateId == 121))
		{
			switch (event)
			{
				case "examine_tumor":
					if (npc.getNpcId() == DESTROYED_TUMORS_2)
					{
						if ((player.getParty() == null) || (player.getParty().getLeader() != player))
						{
							htmltext = "32536-2.htm";
						}
						else
						{
							htmltext = "32536-1.htm";
						}
					}
					if (npc.getNpcId() == DESTROYED_TUMORS)
					{
						if ((player.getParty() == null) || (player.getParty().getLeader() != player))
						{
							htmltext = "32535-2.htm";
						}
						else
						{
							htmltext = "32535-7.htm";
						}
					}
					break;
				case "showcheckpage":
					if (player.getInventory().getItemByItemId(TEAR_OF_A_FREED_SOUL) == null)
					{
						htmltext = "32535-6.htm";
					}
					else if ((warpTimer + 60000) > System.currentTimeMillis())
					{
						htmltext = "32535-4.htm";
					}
					else if (world.tag <= 0)
					{
						htmltext = "32535-3.htm";
					}
					else
					{
						htmltext = "32535-5.htm";
					}
					break;
				case "reenter":
					if ((player.getInventory().getItemByItemId(TEAR_OF_A_FREED_SOUL) == null) || (player.getInventory().getItemByItemId(TEAR_OF_A_FREED_SOUL).getCount() < 3))
					{
						htmltext = "32535-6.htm";
					}
					else
					{
						htmltext = "32535-8.htm";
					}
					break;
			}
		}
		else if ((world != null) && (world.templateId == 122))
		{
			if (event.equalsIgnoreCase("examine_tumor"))
			{
				if (npc.getNpcId() == DESTROYED_TUMORS)
				{
					htmltext = "32535-4.htm";
				}
			}
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		switch (npc.getNpcId())
		{
			case DESTROYED_TUMORS:
				return "32535.htm";
			case DESTROYED_TUMORS_2:
				return "32536.htm";
		}
		return "";
	}
}