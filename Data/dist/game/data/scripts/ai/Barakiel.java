package ai;

import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Barakiel extends L2AttackableAIScript
{
	private static final int BARAKIEL = 25325;
	
	public Barakiel()
	{
		super(-1, Barakiel.class.getSimpleName(), "ai");
		registerMobs(BARAKIEL);
	}
	
	public String onAttack(L2NpcInstance npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (npc.getNpcId() == BARAKIEL)
		{
			if ((npc.getX() < 89800) || (npc.getX() > 93200) || (npc.getY() < -87038))
			{
				npc.teleToLocation(91008, -85904, -2736);
				npc.getStatus().setCurrentHp(npc.getMaxHp());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
}
