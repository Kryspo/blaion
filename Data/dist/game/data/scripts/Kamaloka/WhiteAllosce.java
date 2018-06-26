package Kamaloka;

import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class WhiteAllosce extends L2AttackableAIScript
{
	private static final int ALLOSCE = 18577;
	private static final int GUARD = 18578;
	boolean _isLock = false;
	
	public WhiteAllosce()
	{
		super(-1, WhiteAllosce.class.getSimpleName(), "Kamaloka");
		AttackNpcs(ALLOSCE);
		KillNpcs(ALLOSCE);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_spawn"))
		{
			addSpawn(GUARD, player.getX() + 100, player.getY() + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			_isLock = false;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == ALLOSCE)
		{
			if (!_isLock)
			{
				startQuestTimer("time_to_spawn", 40000, npc, player);
				_isLock = true;
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == ALLOSCE)
		{
			cancelQuestTimer("time_to_spawn", npc, player);
		}
		return super.onKill(npc, player, isPet);
	}
}