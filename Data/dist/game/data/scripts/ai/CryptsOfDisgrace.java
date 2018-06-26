package ai;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.util.Rnd;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class CryptsOfDisgrace extends L2AttackableAIScript
{
	private static final Map<Integer, Integer> MOBSPAWNS5 = new ConcurrentHashMap<>();
	private static final Map<Integer, Integer> MOBSPAWNS15 = new ConcurrentHashMap<>();
	
	//@formatter:off
	public static final int[] MOBS =
	{
		22703, 22704, 22705, 22706, 22707
	};
	
	static
	{
		MOBSPAWNS5.put(Integer.valueOf(22705), Integer.valueOf(22707));
		MOBSPAWNS15.put(Integer.valueOf(22703), Integer.valueOf(22703));
		MOBSPAWNS15.put(Integer.valueOf(22704), Integer.valueOf(22704));
	}
	
	private static final int[][] MobSpawns =
	{
		{ 18464, -28681, 255110, -2160, 10 },
		{ 18464, -26114, 254708, -2139, 10 },
		{ 18463, -28457, 256584, -1926, 10 },
		{ 18463, -26482, 257663, -1925, 10 },
		{ 18464, -26453, 256745, -1930, 10 },
		{ 18463, -27362, 256282, -1935, 10 },
		{ 18464, -25441, 256441, -2147, 10 }
	};
	//@formatter:on
	
	public CryptsOfDisgrace()
	{
		super(-1, CryptsOfDisgrace.class.getSimpleName(), "ai");
		
		KillNpcs(MOBS);
		
		for (int[] loc : MobSpawns)
		{
			addSpawn(loc[0], loc[1], loc[2], loc[3], loc[4]);
		}
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		int npcId = npc.getNpcId();
		L2Attackable newNpc = null;
		if (MOBSPAWNS15.containsKey(Integer.valueOf(npcId)))
		{
			if (Rnd.get(100) < 15)
			{
				newNpc = (L2Attackable) addSpawn(MOBSPAWNS15.get(Integer.valueOf(npcId)).intValue(), npc);
				newNpc.setRunning();
				newNpc.addDamageHate(player, 0, 999);
				newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
			}
		}
		else if ((MOBSPAWNS5.containsKey(Integer.valueOf(npcId))) && (Rnd.get(100) < 5))
		{
			newNpc = (L2Attackable) addSpawn(MOBSPAWNS5.get(Integer.valueOf(npcId)).intValue(), npc);
			newNpc.setRunning();
			newNpc.addDamageHate(player, 0, 999);
			newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
		return super.onKill(npc, player, isPet);
	}
	
	public static L2Npc addSpawn(int npcId, int X, int Y, int Z, int head)
	{
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(head);
				spawn.setXLocation(X);
				spawn.setYLocation(Y);
				spawn.setZLocation(Z);
				spawn.setAmount(spawn.getAmount() + 1);
				return spawn.doSpawn();
			}
		}
		catch (Exception e)
		{
			_log.info("Could not spawn NPC #" + npcId + "; error: " + e.getMessage());
		}
		return null;
	}
}