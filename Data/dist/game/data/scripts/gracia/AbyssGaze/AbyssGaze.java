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
package gracia.AbyssGaze;

import com.l2jserver.gameserver.instancemanager.gracia.SoIManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;

import ai.L2AttackableAIScript;

public class AbyssGaze extends L2AttackableAIScript
{
	// NPC
	private static final int ABYSS_GAZE = 32540;
	
	public AbyssGaze()
	{
		super(-1, AbyssGaze.class.getSimpleName(), "gracia");
		StartNpcs(ABYSS_GAZE);
		FirstTalkNpcs(ABYSS_GAZE);
		TalkNpcs(ABYSS_GAZE);
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
			case "request_permission":
				if ((SoIManager.getCurrentStage() == 2) || (SoIManager.getCurrentStage() == 5))
				{
					htmltext = "32540-2.htm";
				}
				else if ((SoIManager.getCurrentStage() == 3) && SoIManager.isSeedOpen())
				{
					htmltext = "32540-3.htm";
				}
				else
				{
					htmltext = "32540-1.htm";
				}
				
				break;
			case "enter_seed":
				if (SoIManager.getCurrentStage() == 3)
				{
					SoIManager.teleportInSeed(player);
					return null;
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
		
		if (npc.getNpcId() == ABYSS_GAZE)
		{
			return "32540.htm";
		}
		return "";
	}
}