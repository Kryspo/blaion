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
package gracia.Seyo;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;

/**
 * Seyo AI.
 * @author St3eT
 */
public final class Seyo extends L2AttackableAIScript
{
	// NPC
	private static final int SEYO = 32737;
	// Item
	private static final int STONE_FRAGMENT = 15486; // Spirit Stone Fragment
	// Misc
	private static final String[] TEXT =
	{
		"No one else? Don't worry~ I don't bite. Haha~!",
		"OK~ Master of luck? That's you? Haha~! Well, anyone can come after all.",
		"Shedding blood is a given on the battlefield. At least it's safe here.",
		"OK~ Who's next? It all depends on your fate and luck, right? At least come and take a look.",
		"There was someone who won 10,000 from me. A warrior shouldn't just be good at fighting, right? You've gotta be good in everything."
	};
	
	public Seyo()
	{
		super(-1, Seyo.class.getSimpleName(), "gracia");
		StartNpcs(SEYO);
		TalkNpcs(SEYO);
		FirstTalkNpcs(SEYO);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (npc.getNpcId() == SEYO)
		{
			return "32737.html";
		}
		return "";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (npc == null)
		{
			return htmltext;
		}
		switch (event)
		{
			case "TRICKERY_TIMER":
			{
				if (npc.isScriptValue(1))
				{
					npc.setScriptValue(0);
					broadcastNpcSay(npc, Say2.ALL, TEXT[Rnd.get(TEXT.length)]);
				}
				break;
			}
			case "give1":
			{
				if (npc.isScriptValue(1))
				{
					htmltext = "32737-04.html";
				}
				else if (!hasQuestItems(player, STONE_FRAGMENT))
				{
					htmltext = "32737-01.html";
				}
				else
				{
					npc.setScriptValue(1);
					takeItems(player, STONE_FRAGMENT, 1);
					if (Rnd.get(100) == 0)
					{
						giveItems(player, STONE_FRAGMENT, 100);
						broadcastNpcSay(npc, Say2.ALL, "Amazing. " + player.getName() + " took 100 of these soul stone fragments. What a complete swindler.");
					}
					else
					{
						broadcastNpcSay(npc, Say2.ALL, "Hmm? Hey, did you give " + player.getName() + " something? But it was just 1. Haha.");
					}
					startQuestTimer("TRICKERY_TIMER", 5000, npc, null);
				}
				break;
			}
			case "give5":
			{
				if (npc.isScriptValue(1))
				{
					htmltext = "32737-04.html";
				}
				else if (getQuestItemsCount(player, STONE_FRAGMENT) < 5)
				{
					htmltext = "32737-02.html";
				}
				else
				{
					npc.setScriptValue(1);
					takeItems(player, STONE_FRAGMENT, 5);
					final int chance = Rnd.get(100);
					if (chance < 20)
					{
						broadcastNpcSay(npc, Say2.ALL, "Ahem~! " + player.getName() + " has no luck at all. Try praying.");
					}
					else if (chance < 80)
					{
						giveItems(player, STONE_FRAGMENT, 1);
						broadcastNpcSay(npc, Say2.ALL, "It's better than losing it all, right? Or does this feel worse?");
					}
					else
					{
						final int itemCount = Rnd.get(10, 16);
						giveItems(player, STONE_FRAGMENT, itemCount);
						broadcastNpcSay(npc, Say2.ALL, player.getName() + " pulled one with " + String.valueOf(itemCount) + " digits. Lucky~ Not bad~");
					}
					startQuestTimer("TRICKERY_TIMER", 5000, npc, null);
				}
				break;
			}
			case "give20":
			{
				if (npc.isScriptValue(1))
				{
					htmltext = "32737-04.html";
				}
				else if (getQuestItemsCount(player, STONE_FRAGMENT) < 20)
				{
					htmltext = "32737-03.html";
				}
				else
				{
					npc.setScriptValue(1);
					takeItems(player, STONE_FRAGMENT, 20);
					final int chance = Rnd.get(10000);
					if (chance == 0)
					{
						giveItems(player, STONE_FRAGMENT, 10000);
						broadcastNpcSay(npc, Say2.ALL, "Ah... It's over. What kind of guy is that? Damn... Fine, you " + player.getName() + ", take it and get outta here.");
					}
					else if (chance < 10)
					{
						giveItems(player, STONE_FRAGMENT, 1);
						broadcastNpcSay(npc, Say2.ALL, "You don't feel bad, right? Are you sad? But don't cry");
					}
					else
					{
						giveItems(player, STONE_FRAGMENT, Rnd.get(1, 100));
						broadcastNpcSay(npc, Say2.ALL, "A big piece is made up of little pieces. So here's a little piece");
					}
					startQuestTimer("TRICKERY_TIMER", 5000, npc, null);
				}
				break;
			}
		}
		return htmltext;
	}
}