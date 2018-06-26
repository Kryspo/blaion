package ai;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class GargosPailaka extends L2AttackableAIScript
{
	private static final int GARGOS = 18607;
	
	boolean _isStarted = false;
	
	public GargosPailaka()
	{
		super(-1, GargosPailaka.class.getSimpleName(), "ai");
		AttackNpcs(GARGOS);
		KillNpcs(GARGOS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("TimeToFire"))
		{
			_isStarted = false;
			player.sendMessage("Oooo... Ooo...");
			npc.doCast(SkillTable.getInstance().getInfo(5705, 1));
		}
		return onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == GARGOS)
		{
			if (_isStarted == false)
			{
				startQuestTimer("TimeToFire", 60000, npc, player);
				_isStarted = true;
			}
		}
		return onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (npc.getNpcId() == GARGOS)
		{
			cancelQuestTimer("TimeToFire", npc, player);
		}
		return onKill(npc, player, isPet);
		
	}
}