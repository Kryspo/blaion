package ai;

import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Hallate extends L2AttackableAIScript
{
	private static final int HALLATE = 25220;
	
	public Hallate()
	{
		super(-1, Hallate.class.getSimpleName(), "ai");
		AttackNpcs(HALLATE);
	}
	
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == HALLATE)
		{
			if ((npc.getZ() > -1650) || (npc.getZ() < -2150))
			{
				npc.teleToLocation(113548, 17061, -2125);
				npc.getStatus().setCurrentHp(npc.getMaxHp());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
}
