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
package quests.Q10286_ReunionWithSirra;

import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

import quests.Q10285_MeetingSirra.Q10285_MeetingSirra;

/**
 * Reunion with Sirra (10286)
 * @author Adry_85
 */
public final class Q10286_ReunionWithSirra extends Quest
{
	// NPCs
	private static final int RAFFORTY = 32020;
	private static final int JINIA = 32760;
	private static final int SIRRA = 32762;
	private static final int JINIA2 = 32781;
	// Item
	private static final int BLACK_FROZEN_CORE = 15470;
	// Misc
	private static final int MIN_LEVEL = 82;
	
	public Q10286_ReunionWithSirra()
	{
		super(10286, Q10286_ReunionWithSirra.class.getSimpleName(), "Reunion with Sirra");
		StartNpcs(RAFFORTY);
		TalkNpcs(RAFFORTY, JINIA, SIRRA, JINIA2);
		registerQuestItems(BLACK_FROZEN_CORE);
	}
	
	protected class teleCoord
	{
		int instanceId;
		int x;
		int y;
		int z;
	}
	
	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		player.setInstanceId(0);
		player.teleToLocation(tele.x, tele.y, tele.z);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		String htmltext = null;
		switch (event)
		{
			case "32020-02.htm":
			{
				st.startQuest();
				st.setMemoState(1);
				htmltext = event;
				break;
			}
			case "32020-03.html":
			case "32760-02.html":
			case "32760-03.html":
			case "32760-04.html":
			{
				if (st.isMemoState(1))
				{
					htmltext = event;
				}
				break;
			}
			case "32760-05.html":
			{
				if (st.isMemoState(1))
				{
					addSpawn(SIRRA, -23905, -8790, -5384, 56238, false, 0, false, npc.getInstanceId());
					if (npc.getNpcId() == SIRRA)
					{
						npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "You advanced bravely but got such a tiny result. Hohoho."));
					}
					st.setCond(3, true);
					st.set("ex", 1);
					htmltext = event;
				}
				break;
			}
			case "32760-07.html":
			{
				if (st.isMemoState(1) && (st.getInt("ex") == 2))
				{
					st.unset("ex");
					st.setMemoState(2);
					htmltext = event;
				}
				break;
			}
			case "32760-08.html":
			{
				if (st.isMemoState(2))
				{
					st.setCond(5, true);
					final InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
					world.allowed.remove(world.allowed.indexOf(player.getObjectId()));
					final teleCoord tele = new teleCoord();
					tele.instanceId = 0;
					tele.x = 113793;
					tele.y = -109342;
					tele.z = -845;
					exitInstance(player, tele);
					htmltext = event;
				}
				break;
			}
			case "32762-02.html":
			case "32762-03.html":
			{
				if (st.isMemoState(1) && (st.getInt("ex") == 1))
				{
					htmltext = event;
				}
				break;
			}
			case "32762-04.html":
			{
				if (st.isMemoState(1) && (st.getInt("ex") == 1))
				{
					if (!st.hasQuestItems(BLACK_FROZEN_CORE))
					{
						st.giveItems(BLACK_FROZEN_CORE, 5);
					}
					st.set("ex", 2);
					st.setCond(4, true);
					htmltext = event;
				}
				break;
			}
			case "32781-02.html":
			case "32781-03.html":
			{
				if (st.isMemoState(2))
				{
					htmltext = event;
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		switch (st.getState())
		{
			case State.COMPLETED:
			{
				if (npc.getNpcId() == RAFFORTY)
				{
					htmltext = "32020-05.html";
				}
				break;
			}
			case State.CREATED:
			{
				if (npc.getNpcId() == RAFFORTY)
				{
					st = player.getQuestState(Q10285_MeetingSirra.class.getSimpleName());
					htmltext = ((player.getLevel() >= MIN_LEVEL) && (st != null) && (st.isCompleted())) ? "32020-01.htm" : "32020-04.htm";
				}
				break;
			}
			case State.STARTED:
			{
				switch (npc.getNpcId())
				{
					case RAFFORTY:
					{
						if (st.isMemoState(1))
						{
							htmltext = (player.getLevel() >= MIN_LEVEL) ? "32020-06.html" : "32020-08.html";
						}
						else if (st.isMemoState(2))
						{
							htmltext = "32020-07.html";
						}
						break;
					}
					case JINIA:
					{
						if (st.isMemoState(1))
						{
							final int state = st.getInt("ex");
							switch (state)
							{
								case 0:
								{
									htmltext = "32760-01.html";
									break;
								}
								case 1:
								{
									htmltext = "32760-05.html";
									break;
								}
								case 2:
								{
									htmltext = "32760-06.html";
									break;
								}
							}
						}
						break;
					}
					case SIRRA:
					{
						if (st.isMemoState(1))
						{
							final int state = st.getInt("ex");
							if (state == 1)
							{
								htmltext = "32762-01.html";
							}
							else if (state == 2)
							{
								htmltext = "32762-05.html";
							}
						}
						break;
					}
					case JINIA2:
					{
						if (st.isMemoState(2))
						{
							htmltext = "32781-02.html";
						}
						else if (st.isMemoState(10))
						{
							st.addExpAndSp(2152200, 181070);
							st.exitQuest(false, true);
							htmltext = "32781-01.html";
						}
						break;
					}
				}
				break;
			}
		}
		return htmltext;
	}
}
