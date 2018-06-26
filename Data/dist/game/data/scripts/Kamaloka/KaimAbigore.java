package Kamaloka;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 * custom - in future we will rework to retail with new improvements
 */
public class KaimAbigore extends L2AttackableAIScript
{
	private static final int KAIM = 18566;
	private static final int GUARD = 18567;
	
	boolean _isAlreadyStarted = false;
	boolean _isAlreadySpawned = false;
	int _isLockSpawned = 0;
	
	public KaimAbigore()
	{
		super(-1, KaimAbigore.class.getSimpleName(), "Kamaloka");
		AttackNpcs(KAIM);
		KillNpcs(KAIM, GUARD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "time_to_skill":
				npc.setTarget(player);
				npc.doCast(SkillTable.getInstance().getInfo(5260, 5));
				_isAlreadyStarted = false;
				break;
			case "time_to_spawn":
				addSpawn(GUARD, player.getX() + 100, player.getY() + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				addSpawn(GUARD, player.getX() - 100, player.getY() - 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				addSpawn(GUARD, player.getX(), player.getY() - 80, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
				_isAlreadySpawned = false;
				_isLockSpawned = 3;
				break;
		}
		return "";
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == KAIM)
		{
			if (_isAlreadyStarted == false)
			{
				startQuestTimer("time_to_skill", 45000, npc, player);
				_isAlreadyStarted = true;
			}
			else if (_isAlreadyStarted == true)
			{
				return "";
			}
			if (_isAlreadySpawned == false)
			{
				if (_isLockSpawned == 0)
				{
					startQuestTimer("time_to_spawn", 60000, npc, player);
					_isAlreadySpawned = true;
				}
				if (_isLockSpawned == 3)
				{
					return "";
				}
			}
			else if (_isAlreadySpawned == true)
			{
				return "";
			}
		}
		return "";
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARD:
				_isLockSpawned = 1;
				break;
			case KAIM:
				cancelQuestTimer("time_to_spawn", npc, player);
				cancelQuestTimer("time_to_skill", npc, player);
				break;
		}
		return "";
	}
}