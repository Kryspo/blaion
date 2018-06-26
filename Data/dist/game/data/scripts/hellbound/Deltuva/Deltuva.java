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
package hellbound.Deltuva;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;

import quests.Q00132_MatrasCuriosity.Q00132_MatrasCuriosity;

/**
 * @author GKR
 */
public class Deltuva extends Quest
{
	private static final int DELTUVA = 32313;
	
	public Deltuva()
	{
		super(-1, Deltuva.class.getSimpleName(), "hellbound");
		StartNpcs(DELTUVA);
		TalkNpcs(DELTUVA);
		FirstTalkNpcs(DELTUVA);
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
		switch (event)
		{
			case "teleport":
				final QuestState hostQuest = player.getQuestState(Q00132_MatrasCuriosity.class.getSimpleName());
				if ((hostQuest == null) || !hostQuest.isCompleted())
				{
					return "32313-02.htm";
				}
				else
				{
					player.teleToLocation(17934, 283189, -9701);
				}
				break;
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
		
		if (npc.getNpcId() == DELTUVA)
		{
			return "32313.htm";
		}
		return "";
	}
}
