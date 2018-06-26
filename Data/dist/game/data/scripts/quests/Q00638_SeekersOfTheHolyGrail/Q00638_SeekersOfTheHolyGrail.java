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
package quests.Q00638_SeekersOfTheHolyGrail;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.util.Rnd;

/**
 * @author RobíkBobík
 */
public final class Q00638_SeekersOfTheHolyGrail extends Quest
{
	private static final int DROP_CHANCE = 30;
	private static final int INNOCENTIN = 31328;
	private static final int[] MOBS =
	{
		22138,
		22139,
		22140,
		22142,
		22143,
		22144,
		22145,
		22146,
		22147,
		22148,
		22149,
		22150,
		22151,
		22152,
		22153,
		22154,
		22154,
		22155,
		22156,
		22157,
		22158,
		22159,
		22160,
		22161,
		22161,
		22162,
		22163,
		22164,
		22165,
		22166,
		22167,
		22168,
		22169,
		22170,
		22171,
		22172,
		22173,
		22174,
		22175
	};
	// Items
	private static final int TOTEM = 8068;
	private static final int ANTEROOMKEY = 8273;
	private static final int CHAPELKEY = 8274;
	private static final int KEYOFDARKNESS = 8275;
	// Mobs/raids that drop keys
	private static final int RitualOffering = 22149;
	private static final int ZombieWorker = 22140;
	private static final int TriolsBeliever = 22143;
	private static final int TriolsLayperson = 22142;
	private static final int TriolsPriest2 = 22151;
	private static final int TriolsPriest3 = 22146;
	
	public Q00638_SeekersOfTheHolyGrail()
	{
		super(638, Q00638_SeekersOfTheHolyGrail.class.getSimpleName(), "Seekers of the Holy Grail");
		
		StartNpcs(INNOCENTIN);
		TalkNpcs(INNOCENTIN);
		for (int npcId : MOBS)
		{
			KillNpcs(npcId);
		}
		questItemIds = new int[]
		{
			TOTEM
		};
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		
		if (event.equalsIgnoreCase("31328-02.htm"))
		{
			st.set("cond", "1");
			st.setState(State.STARTED);
			st.playSound("ItemSound.quest_accept");
		}
		else if (event.equalsIgnoreCase("31328-06.htm"))
		{
			st.playSound("ItemSound.quest_finish");
			st.exitQuest(true);
		}
		return htmltext;
	}
	
	@Override
	public final String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = Quest.getNoQuestMsg(player);
		final QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return htmltext;
		}
		
		final int level = player.getLevel();
		final byte id = st.getState();
		
		if (level >= 73)
		{
			if (id == State.CREATED)
			{
				htmltext = "31328-01.htm";
			}
			else if ((id == State.STARTED) && (st.getQuestItemsCount(TOTEM) >= 2000))
			{
				int rr = st.getRandom(3);
				
				if (rr == 0)
				{
					st.takeItems(TOTEM, 2000);
					st.giveReward(959, st.getRandom(4) + 3);
					st.playSound("ItemSound.quest_middle");
				}
				if (rr == 1)
				{
					st.takeItems(TOTEM, 2000);
					st.giveReward(57, 3576000);
					st.playSound("ItemSound.quest_middle");
				}
				if (rr == 2)
				{
					st.takeItems(TOTEM, 2000);
					st.giveReward(960, st.getRandom(4) + 3);
					st.playSound("ItemSound.quest_middle");
				}
				htmltext = "31328-03.htm";
			}
			else
			{
				htmltext = "31328-04.htm";
			}
		}
		else
		{
			htmltext = "31328-00.htm";
		}
		return htmltext;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		L2PcInstance partyMember = this.getRandomPartyMember(player, "1");
		if (partyMember == null)
		{
			return null;
		}
		QuestState st = partyMember.getQuestState(getName());
		if (st == null)
		{
			return null;
		}
		final int npcId = npc.getNpcId();
		if (st.getInt("cond") == 1)
		{
			st.dropQuestItems(TOTEM, 1, 1, 0, true, DROP_CHANCE, true);
			if ((npcId == RitualOffering) || (npcId == ZombieWorker))
			{
				st.giveItems(ANTEROOMKEY, 6);
			}
			else if ((npcId == TriolsBeliever) || (npcId == TriolsLayperson))
			{
				st.giveItems(CHAPELKEY, 1);
			}
			else if (((npcId == TriolsPriest2) || (npcId == TriolsPriest3)) && (Rnd.get(100) < 10))
			{
				st.giveItems(KEYOFDARKNESS, 1);
			}
		}
		
		return null;
	}
}