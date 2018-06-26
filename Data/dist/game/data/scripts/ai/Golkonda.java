package ai;

import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Golkonda extends L2AttackableAIScript
{
	private static final int GOLKONDA = 25126;
	
	public Golkonda()
	{
		super(-1, Golkonda.class.getSimpleName(), "ai");
		AttackNpcs(GOLKONDA);
	}
	
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == GOLKONDA)
		{
			if ((npc.getZ() > 7500) || (npc.getZ() < 6900))
			{
				npc.teleToLocation(116313, 15896, 6999);
				npc.getStatus().setCurrentHp(npc.getMaxHp());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
}
