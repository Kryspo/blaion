package ai;

import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Kernon extends L2AttackableAIScript
{
	private static final int KERNON = 25054;
	
	public Kernon()
	{
		super(-1, Kernon.class.getSimpleName(), "ai");
		AttackNpcs(KERNON);
	}
	
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == KERNON)
		{
			if ((npc.getZ() > 4300) || (npc.getZ() < 3900))
			{
				npc.teleToLocation(113420, 16424, 3969);
				npc.getStatus().setCurrentHp(npc.getMaxHp());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
}
