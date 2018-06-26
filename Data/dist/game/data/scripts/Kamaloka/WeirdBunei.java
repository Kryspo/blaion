package Kamaloka;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class WeirdBunei extends L2AttackableAIScript
{
	private static final int WEIRD = 18564;
	boolean _isAlreadyStarted = false;
	
	public WeirdBunei()
	{
		super(-1, WeirdBunei.class.getSimpleName(), "Kamaloka");
		AttackNpcs(WEIRD);
		KillNpcs(WEIRD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_skill"))
		{
			if (_isAlreadyStarted)
			{
				_isAlreadyStarted = false;
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(5625, 1));
			}
		}
		
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == WEIRD)
		{
			if (!_isAlreadyStarted)
			{
				startQuestTimer("time_to_skill", 30000, npc, player);
				_isAlreadyStarted = true;
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == WEIRD)
		{
			cancelQuestTimer("time_to_skill", npc, player);
		}
		
		return super.onKill(npc, player, isPet);
	}
}