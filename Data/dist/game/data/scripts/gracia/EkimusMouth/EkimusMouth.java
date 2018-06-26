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
package gracia.EkimusMouth;

import com.l2jserver.gameserver.instancemanager.gracia.SoIManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;

import ai.L2AttackableAIScript;

public class EkimusMouth extends L2AttackableAIScript
{
	// NPC
	private static final int EKIMUS_MOUNT = 32537;
	
	public EkimusMouth()
	{
		super(-1, EkimusMouth.class.getSimpleName(), "gracia");
		StartNpcs(EKIMUS_MOUNT);
		FirstTalkNpcs(EKIMUS_MOUNT);
		TalkNpcs(EKIMUS_MOUNT);
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
			case "hos_enter":
				if (SoIManager.getCurrentStage() == 1)
				{
					htmltext = "32537-1.htm";
				}
				else if (SoIManager.getCurrentStage() == 4)
				{
					htmltext = "32537-2.htm";
				}
				break;
			case "hoe_enter":
				if (SoIManager.getCurrentStage() == 1)
				{
					htmltext = "32537-3.htm";
				}
				else if (SoIManager.getCurrentStage() == 4)
				{
					htmltext = "32537-4.htm";
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
		
		if (npc.getNpcId() == EKIMUS_MOUNT)
		{
			return "32537.htm";
		}
		return "";
	}
}