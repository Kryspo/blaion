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
package ai;

import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;

public class SeeThroughSilentMove extends L2AttackableAIScript
{
	private static final int[] MOBIDS =
	{
		18001,
		18002,
		22199,
		22215,
		22216,
		22217,
		22327,
		22746,
		22747,
		22748,
		22749,
		22750,
		22751,
		22752,
		22753,
		22754,
		22755,
		22756,
		22757,
		22758,
		22759,
		22760,
		22761,
		22762,
		22763,
		22764,
		22765,
		22794,
		22795,
		22796,
		22797,
		22798,
		22799,
		22800,
		29009,
		29010,
		29011,
		29012,
		29013
	};
	
	public SeeThroughSilentMove()
	{
		super(-1, SeeThroughSilentMove.class.getSimpleName(), "ai");
		for (int npcId : MOBIDS)
		{
			for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(npcId))
			{
				final L2Npc npc = spawn.getLastSpawn();
				if ((npc != null) && npc.isAttackable())
				{
					((L2Attackable) npc).setSeeThroughSilentMove(true);
				}
			}
		}
		SpawnNpcs(MOBIDS);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc instanceof L2Attackable)
		{
			((L2Attackable) npc).setSeeThroughSilentMove(true);
		}
		return super.onSpawn(npc);
	}
}