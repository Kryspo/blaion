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
package hellbound;

import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.instancemanager.HellboundManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;

/**
 * @author DS
 */
public class OutpostCaptain extends Quest
{
	private static final int CAPTAIN = 18466;
	private static final int[] DEFENDERS =
	{
		22357,
		22358
	};
	
	public OutpostCaptain()
	{
		super(-1, OutpostCaptain.class.getSimpleName(), "hellbound");
		KillNpcs(CAPTAIN);
		SpawnNpcs(CAPTAIN);
		SpawnNpcs(DEFENDERS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("level_up"))
		{
			HellboundManager.getInstance().setLevel(9);
		}
		return null;
	}
	
	@Override
	public final String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (HellboundManager.getInstance().getLevel() == 8)
		{
			startQuestTimer("level_up", 3000, npc, null);
		}
		
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		npc.setIsNoRndWalk(true);
		
		switch (npc.getNpcId())
		{
			case CAPTAIN:
				L2DoorInstance door = DoorTable.getInstance().getDoor(20250001);
				if (door != null)
				{
					door.closeMe();
				}
				break;
		}
		return super.onSpawn(npc);
	}
}
