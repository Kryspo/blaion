package Kamaloka;

import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class VenomousStorace extends L2AttackableAIScript
{
	private static final int VENOMOUS = 18571;
	private static final int GUARD = 18572;
	
	boolean _isAlreadySpawned = false;
	int _isLockSpawned = 0;
	
	public VenomousStorace()
	{
		super(-1, VenomousStorace.class.getSimpleName(), "Kamaloka");
		AttackNpcs(VENOMOUS);
		KillNpcs(GUARD, VENOMOUS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("time_to_spawn"))
		{
			addSpawn(GUARD, player.getX() + 100, player.getY() + 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			addSpawn(GUARD, player.getX() - 100, player.getY() - 50, npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			_isAlreadySpawned = false;
			_isLockSpawned = 2;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		final int npcId = npc.getNpcId();
		if (npcId == VENOMOUS)
		{
			switch (_isLockSpawned)
			{
				case 0:
					if (!_isAlreadySpawned)
					{
						startQuestTimer("time_to_spawn", 20000, npc, player);
						_isAlreadySpawned = true;
					}
					break;
				case 2:
				default:
					return null;
			}
		}
		
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARD:
				_isLockSpawned = 1;
				break;
			case VENOMOUS:
				cancelQuestTimer("time_to_spawn", npc, player);
				break;
		}
		
		return super.onKill(npc, player, isPet);
	}
}