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

import java.util.Map;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

import ai.L2AttackableAIScript;
import javolution.util.FastMap;

/**
 * Manages Sin Wardens disappearing and chat
 * @author GKR
 */
public class SinWardens extends L2AttackableAIScript
{
	private static final int[] SIN_WARDEN_MINIONS =
	{
		22424,
		22425,
		22426,
		22427,
		22428,
		22429,
		22430,
		22432,
		22433,
		22434,
		22435,
		22436,
		22437,
		22438
	};
	
	private final Map<Integer, Integer> killedMinionsCount = new FastMap<Integer, Integer>();
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.isMinion())
		{
			L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead())
			{
				int killedCount = killedMinionsCount.containsKey(master.getObjectId()) ? killedMinionsCount.get(master.getObjectId()) : 0;
				killedCount++;
				
				if ((killedCount) == 5)
				{
					master.broadcastPacket(new CreatureSay(master.getObjectId(), 1, master.getName(), "We might need new slaves... I'll be back soon, so wait!"));
					master.doDie(killer);
					killedMinionsCount.remove(master.getObjectId());
				}
				else
				{
					killedMinionsCount.put(master.getObjectId(), killedCount);
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public SinWardens()
	{
		super(-1, SinWardens.class.getSimpleName(), "hellbound");
		KillNpcs(SIN_WARDEN_MINIONS);
	}
}
