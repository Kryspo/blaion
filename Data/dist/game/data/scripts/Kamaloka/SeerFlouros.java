package Kamaloka;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class SeerFlouros extends L2AttackableAIScript
{
	private static L2Npc SeerFlouros;
	private static L2Npc Follower;
	private static final int duration = 300000;
	
	private static final int SEER_FLOUROS = 18559;
	private static final int FOLLOWER = 18560;
	
	private static long _LastAttack = 0;
	private static boolean successDespawn = false;
	private static boolean minion = false;
	
	public SeerFlouros()
	{
		super(-1, SeerFlouros.class.getSimpleName(), "Kamaloka");
		
		registerMobs(new int[]
		{
			SEER_FLOUROS,
			FOLLOWER
		});
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equalsIgnoreCase("despawn"))
		{
			if (!successDespawn && (SeerFlouros != null) && ((_LastAttack + 300000) < System.currentTimeMillis()))
			{
				cancelQuestTimer("despawn", npc, null);
				SeerFlouros.deleteMe();
				if ((SeerFlouros != null) && (InstanceManager.getInstance().getInstance(SeerFlouros.getInstanceId()) != null))
				{
					InstanceManager.getInstance().getInstance(SeerFlouros.getInstanceId()).setDuration(duration);
				}
				successDespawn = true;
				if (Follower != null)
				{
					Follower.deleteMe();
				}
			}
		}
		else if (event.equalsIgnoreCase("respMinion") && (SeerFlouros != null))
		{
			Follower = addSpawn(FOLLOWER, SeerFlouros.getX(), SeerFlouros.getY(), SeerFlouros.getZ(), SeerFlouros.getHeading(), false, 0);
			Follower = addSpawn(FOLLOWER, SeerFlouros.getX(), SeerFlouros.getY(), SeerFlouros.getZ(), SeerFlouros.getHeading(), false, 0);
			L2Attackable target = (L2Attackable) SeerFlouros;
			Follower.setRunning();
			((L2Attackable) Follower).addDamageHate(target.getMostHated(), 0, 999);
			Follower.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == SEER_FLOUROS)
		{
			_LastAttack = System.currentTimeMillis();
			startQuestTimer("despawn", 60000, npc, null, true);
			SeerFlouros = npc;
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!minion)
		{
			Follower = addSpawn(FOLLOWER, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0);
			minion = true;
		}
		_LastAttack = System.currentTimeMillis();
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == SEER_FLOUROS)
		{
			cancelQuestTimer("despawn", npc, null);
			if (Follower != null)
			{
				Follower.deleteMe();
			}
		}
		else if ((npc.getNpcId() == FOLLOWER) && (SeerFlouros != null))
		{
			startQuestTimer("respMinion", 30000, npc, null);
		}
		return null;
	}
}
