package Kamaloka;

import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;
import gnu.trove.map.hash.TIntObjectHashMap;
import javolution.util.FastMap;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class BladeOtis extends L2AttackableAIScript
{
	private static final int BLADEO = 18562;
	private static final int GUARD = 18563;
	private final TIntObjectHashMap<Integer> _guardSpawns = new TIntObjectHashMap<Integer>();
	private final FastMap<L2Npc, L2Npc> _guardMaster = new FastMap<L2Npc, L2Npc>();
	
	public BladeOtis()
	{
		super(-1, BladeOtis.class.getSimpleName(), "Kamaloka");
		AttackNpcs(BLADEO);
		KillNpcs(BLADEO, GUARD);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if ((_guardSpawns.get(npc.getObjectId()) != null) && (_guardSpawns.get(npc.getObjectId()) >= 6))
		{
			return null;
		}
		if (event.equalsIgnoreCase("time_to_spawn"))
		{
			L2Npc guard = addSpawn(GUARD, player.getX() + Rnd.get(-20, 50), player.getY() + Rnd.get(-20, 50), npc.getZ(), 0, false, 0, false, npc.getInstanceId());
			if (_guardSpawns.get(npc.getObjectId()) != null)
			{
				_guardSpawns.put(npc.getObjectId(), _guardSpawns.get(npc.getObjectId()) + 1);
			}
			else
			{
				_guardSpawns.put(npc.getObjectId(), 1);
			}
			_guardMaster.put(guard, npc);
			guard.setTarget(player);
			((L2Attackable) npc).addDamageHate(player, 0, 999);
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet)
	{
		if (npc.getNpcId() == BLADEO)
		{
			if (npc.getStatus().getCurrentHp() < (npc.getMaxHp() * 0.5))
			{
				if ((_guardSpawns.get(npc.getObjectId()) == null) || (_guardSpawns.get(npc.getObjectId()) == 0))
				{
					startQuestTimer("time_to_spawn", 1, npc, player);
				}
				else if (_guardSpawns.get(npc.getObjectId()) < 6)
				{
					startQuestTimer("time_to_spawn", 10000, npc, player);
				}
			}
		}
		return super.onAttack(npc, player, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case GUARD:
				if (_guardMaster.get(npc) != null)
				{
					L2Npc master = _guardMaster.get(npc);
					if ((_guardSpawns.get(master.getObjectId()) != null) && (_guardSpawns.get(master.getObjectId()) > 0))
					{
						_guardSpawns.put(master.getObjectId(), _guardSpawns.get(master.getObjectId()) - 1);
					}
					_guardMaster.remove(npc);
				}
				break;
			case BLADEO:
				if (_guardSpawns.contains(npc.getObjectId()))
				{
					_guardSpawns.remove(npc.getObjectId());
				}
				for (L2Npc i : _guardMaster.keySet())
				{
					if ((_guardMaster.get(i) != null) && (npc == _guardMaster.get(i)))
					{
						i.decayMe();
						_guardMaster.remove(i);
					}
				}
				cancelQuestTimer("time_to_spawn", npc, killer);
				break;
		}
		return super.onKill(npc, killer, isPet);
	}
}