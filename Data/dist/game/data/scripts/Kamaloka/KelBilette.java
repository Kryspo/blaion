package Kamaloka;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class KelBilette extends L2AttackableAIScript
{
	private static final int KEL = 18573;
	private static final int GUARD = 18574;
	
	boolean _isAlreadyStarted = false;
	boolean _isAlreadySpawned = false;
	
	public KelBilette()
	{
		super(-1, KelBilette.class.getSimpleName(), "Kamaloka");
		AttackNpcs(KEL);
		KillNpcs(GUARD, KEL);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "time_to_skill":
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(4748, 6));
				_isAlreadyStarted = false;
				startQuestTimer("time_to_skill1", 10000, npc, player);
				break;
			case "time_to_skill1":
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(5203, 6));
				break;
			case "time_to_spawn":
				addSpawn(GUARD, player.getX() + 100, player.getY() + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == KEL)
		{
			if (_isAlreadyStarted)
			{
				return null;
			}
			
			startQuestTimer("time_to_skill", 30000, npc, player);
			_isAlreadyStarted = true;
			
			if (_isAlreadySpawned)
			{
				return null;
			}
			
			startQuestTimer("time_to_spawn", 10000, npc, player);
			_isAlreadySpawned = true;
		}
		
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARD:
				_isAlreadySpawned = true;
				break;
			case KEL:
				cancelQuestTimer("time_to_spawn", npc, player);
				cancelQuestTimer("time_to_skill", npc, player);
				break;
		}
		
		return super.onKill(npc, player, isPet);
	}
}