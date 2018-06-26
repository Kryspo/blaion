package Kamaloka;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class PowderKeg extends L2AttackableAIScript
{
	private static final int POWDERK = 18622;
	
	public PowderKeg()
	{
		super(-1, PowderKeg.class.getSimpleName(), "Kamaloka");
		AttackNpcs(POWDERK);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("Boom"))
		{
			npc.setTarget(npc);
			npc.doCast(SkillTable.getInstance().getInfo(5714, 1));
			npc.setCurrentHp(0);
			npc.decayMe();
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		if (npc.getNpcId() == POWDERK)
		{
			startQuestTimer("Boom", 2000, npc, player);
		}
		return super.onAttack(npc, player, damage, isPet);
	}
}