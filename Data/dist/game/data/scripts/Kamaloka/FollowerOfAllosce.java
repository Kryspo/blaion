package Kamaloka;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class FollowerOfAllosce extends L2AttackableAIScript
{
	private static final int FOFALLOSCE = 18578;
	
	public FollowerOfAllosce()
	{
		super(-1, FollowerOfAllosce.class.getSimpleName(), "Kamaloka");
		AggroRangeEnterNpcs(FOFALLOSCE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_skill"))
		{
			npc.setTarget(player);
			npc.doCast(SkillTable.getInstance().getInfo(5624, 1));
			startQuestTimer("time_to_skill", 30000, npc, player);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == FOFALLOSCE)
		{
			npc.setIsInvul(true);
			startQuestTimer("time_to_skill", 30000, npc, player);
			npc.setTarget(player);
			npc.doCast(SkillTable.getInstance().getInfo(5624, 1));
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
}