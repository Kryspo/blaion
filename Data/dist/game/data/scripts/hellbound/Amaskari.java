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

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.instancemanager.HellboundManager;
import com.l2jserver.gameserver.model.L2Effect;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.skills.SkillHolder;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;

/**
 * Manages Amaskari's and minions' chat and some skill usage.
 * @author GKR
 */
public class Amaskari extends L2AttackableAIScript
{
	private static final int AMASKARI = 22449;
	private static final int AMASKARI_PRISONER = 22450;
	
	private static final int BUFF_ID = 4632;
	private static SkillHolder[] BUFF =
	{
		new SkillHolder(BUFF_ID, 1),
		new SkillHolder(BUFF_ID, 2),
		new SkillHolder(BUFF_ID, 3)
	};
	// private static SkillHolder INVINCIBILITY = new SkillHolder(5417, 1);
	
	private static final String[] AMASKARI_NPCSTRING_ID =
	{
		"I'll make everyone feel the same suffering as me!",
		"Ha-ha yes, die slowly writhing in pain and agony!",
		"More... need more... severe pain...",
		"Something is burning inside my body!"
	};
	
	private static final String[] MINIONS_NPCSTRING_ID =
	{
		"Ahh! My life is being drained out!",
		"Thank you for saving me.",
		"It... will... kill... everyone...",
		"Eeek... I feel sick...yow...!"
	};
	
	public Amaskari()
	{
		super(-1, Amaskari.class.getSimpleName(), "hellbound");
		KillNpcs(AMASKARI, AMASKARI_PRISONER);
		AttackNpcs(AMASKARI);
		SpawnNpcs(AMASKARI_PRISONER);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("stop_toggle"))
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), AMASKARI_NPCSTRING_ID[2]));
			((L2MonsterInstance) npc).clearAggroList();
			((L2MonsterInstance) npc).getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
			npc.setIsInvul(false);
			// npc.doCast(INVINCIBILITY.getSkill())
		}
		else if (event.equalsIgnoreCase("onspawn_msg") && (npc != null) && !npc.isDead())
		{
			if (Rnd.get(100) > 20)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), MINIONS_NPCSTRING_ID[2]));
			}
			else if (Rnd.get(100) > 40)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), MINIONS_NPCSTRING_ID[3]));
			}
			startQuestTimer("onspawn_msg", (Rnd.get(8) + 1) * 30000, npc, null);
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		if ((npc.getNpcId() == AMASKARI) && (Rnd.get(1000) < 25))
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), AMASKARI_NPCSTRING_ID[0]));
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead() && (Rnd.get(10) == 0))
				{
					minion.broadcastPacket(new CreatureSay(minion.getObjectId(), 1, minion.getName(), MINIONS_NPCSTRING_ID[0]));
					minion.setCurrentHp(minion.getCurrentHp() - (minion.getCurrentHp() / 5));
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isPet, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == AMASKARI_PRISONER)
		{
			final L2MonsterInstance master = ((L2MonsterInstance) npc).getLeader();
			if ((master != null) && !master.isDead())
			{
				master.broadcastPacket(new CreatureSay(master.getObjectId(), Say2.ALL, master.getName(), AMASKARI_NPCSTRING_ID[1]));
				final L2Effect e = master.getFirstEffect(BUFF_ID);
				if ((e != null) && (e.getAbnormalLvl() == 3) && master.isInvul())
				{
					master.setCurrentHp(master.getCurrentHp() + (master.getCurrentHp() / 5));
				}
				else
				{
					master.clearAggroList();
					master.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
					if (e == null)
					{
						master.doCast(BUFF[0].getSkill());
					}
					else if (e.getAbnormalLvl() < 3)
					{
						master.doCast(BUFF[e.getAbnormalLvl()].getSkill());
					}
					else
					{
						master.broadcastPacket(new CreatureSay(master.getObjectId(), Say2.ALL, master.getName(), AMASKARI_NPCSTRING_ID[3]));
						// master.doCast(INVINCIBILITY.getSkill())
						master.setIsInvul(true);
						startQuestTimer("stop_toggle", 10000, master, null);
					}
				}
			}
		}
		else if (npc.getNpcId() == AMASKARI)
		{
			for (L2MonsterInstance minion : ((L2MonsterInstance) npc).getMinionList().getSpawnedMinions())
			{
				if ((minion != null) && !minion.isDead())
				{
					if (Rnd.get(1000) > 300)
					{
						minion.broadcastPacket(new CreatureSay(minion.getObjectId(), Say2.ALL, minion.getName(), MINIONS_NPCSTRING_ID[1]));
					}
					
					HellboundManager.getInstance().updateTrust(30, true);
					minion.deleteMe();
				}
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		if (!npc.isTeleporting())
		{
			startQuestTimer("onspawn_msg", (Rnd.get(3) + 1) * 30000, npc, null);
		}
		return super.onSpawn(npc);
	}
}
