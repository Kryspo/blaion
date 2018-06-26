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
package quests.Q00423_TakeYourBestShot;

import java.util.Arrays;
import java.util.List;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;

import quests.Q00249_PoisonedPlainsOfTheLizardmen.Q00249_PoisonedPlainsOfTheLizardmen;

/**
 * Take Your Best Shot (423)
 * @author Gnacik
 * @version 2010-06-26 Based on official server Franz
 */
public class Q00423_TakeYourBestShot extends Quest
{
	// NPCs
	private static final int BATRACOS = 32740;
	private static final int JOHNNY = 32744;
	
	// Monster
	private static final int TANTA_GUARD = 18862;
	private static final List<Integer> MONSTERS = Arrays.asList(22768, 22769, 22770, 22771, 22772, 22773, 22774);
	
	// Item
	private static final int SEER_UGOROS_PASS = 15496;
	
	// Misc
	private static final int MIN_LEVEL = 82;
	private static final int SPAWN_CHANCE = 500; // Spawn chance x/1000
	
	public Q00423_TakeYourBestShot()
	{
		super(423, Q00423_TakeYourBestShot.class.getSimpleName(), "Take Your Best Shot!");
		StartNpcs(JOHNNY, BATRACOS);
		TalkNpcs(JOHNNY, BATRACOS);
		FirstTalkNpcs(BATRACOS);
		
		KillNpcs(TANTA_GUARD);
		KillNpcs(MONSTERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		String htmltext = event;
		switch (event)
		{
			case "32740.html":
			case "32740-01.html":
			case "32744-02.html":
			case "32744-03.htm":
				break;
			case "32744-04.htm":
				st.startQuest();
				break;
			case "32744-quit.html":
				st.exitQuest(true);
				break;
			default:
				htmltext = null;
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.isInsideRadius(96782, 85918, 100, true))
		{
			return "32740-ugoros.html";
		}
		return "32740.html";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		final QuestState st = getQuestState(player, false);
		if (st == null)
		{
			return null;
		}
		
		if (MONSTERS.contains(npc.getNpcId()) && (getRandom(1000) <= SPAWN_CHANCE))
		{
			L2Npc guard = addSpawn(TANTA_GUARD, npc, false);
			attackPlayer((L2Attackable) guard, player);
		}
		else if ((npc.getNpcId() == TANTA_GUARD) && (st.getInt("cond") == 1))
		{
			st.set("cond", "2");
		}
		return null;
	}
	
	private void attackPlayer(L2Attackable npc, L2PcInstance player)
	{
		npc.setIsRunning(true);
		npc.addDamageHate(player, 0, 999);
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState st = player.getQuestState(getName());
		String htmltext = getNoQuestMsg(player);
		if (st == null)
		{
			return htmltext;
		}
		
		switch (npc.getNpcId())
		{
			case JOHNNY:
				switch (st.getState())
				{
					case State.CREATED:
						final QuestState _prev = player.getQuestState(Q00249_PoisonedPlainsOfTheLizardmen.class.getSimpleName());
						if ((_prev != null) && _prev.isCompleted() && (player.getLevel() >= MIN_LEVEL))
						{
							htmltext = (st.hasQuestItems(SEER_UGOROS_PASS)) ? "32744-07.htm" : "32744-01.htm";
						}
						else
						{
							htmltext = "32744-00.htm";
						}
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "32744-05.html";
						}
						else if (st.isCond(2))
						{
							htmltext = "32744-06.html";
						}
						break;
				}
				break;
			case BATRACOS:
				switch (st.getState())
				{
					case State.CREATED:
						htmltext = (st.hasQuestItems(SEER_UGOROS_PASS)) ? "32740-05.html" : "32740-00.html";
						break;
					case State.STARTED:
						if (st.isCond(1))
						{
							htmltext = "32740-02.html";
						}
						else if (st.isCond(2))
						{
							st.giveItems(SEER_UGOROS_PASS, 1);
							st.exitQuest(true, true);
							htmltext = "32740-04.html";
						}
						break;
				}
		}
		return htmltext;
	}
}