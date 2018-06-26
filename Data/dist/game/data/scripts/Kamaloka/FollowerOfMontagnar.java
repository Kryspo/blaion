package Kamaloka;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class FollowerOfMontagnar extends L2AttackableAIScript
{
	private static final int FOFMONTAGNAR = 18569;
	
	public FollowerOfMontagnar()
	{
		super(-1, FollowerOfMontagnar.class.getSimpleName(), "Kamaloka");
		AggroRangeEnterNpcs(FOFMONTAGNAR);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == FOFMONTAGNAR)
		{
			npc.setIsInvul(true);
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
}