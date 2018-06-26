package hellbound;

import java.util.ArrayList;

import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Hellenark extends L2AttackableAIScript
{
	// NPC
	private static final int HELLENARK = 22326;
	private static final int NAIA_FAILAN = 18484;
	// DEBUFF
	private static final int NAIA_SPROUT = 5765;
	
	private int status;
	public ArrayList<L2Npc> spawnnaia = new ArrayList<L2Npc>();
	
	//@formatter:off
	private static final int NAIA_LOCK[][] =
	{
		{-24542, 245792, -3133, 19078},
		{-23839, 246056, -3133, 17772},
		{-23713, 244358, -3133, 53369},
		{-23224, 244524, -3133, 57472},
		{-24709, 245186, -3133, 63974},
		{-24394, 244379, -3133, 5923}
	};
	//@formatter:on
	
	public Hellenark()
	{
		super(-1, Hellenark.class.getSimpleName(), "hellbound");
		status = 0;
		spawnnaia = new ArrayList<>();
		AttackNpcs(HELLENARK);
		TalkNpcs(NAIA_FAILAN);
		FirstTalkNpcs(NAIA_FAILAN);
		StartNpcs(NAIA_FAILAN);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance player, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getNpcId() == HELLENARK)
		{
			if (status == 0)
			{
				startQuestTimer("spawn", 20000, npc, null, false);
			}
			status = 1;
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == NAIA_FAILAN)
		{
			npc.setIsImmobilized(true);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "spawn":
				if (status == 1)
				{
					status = 3;
				}
				startQuestTimer("check", 30000, npc, null, false);
				for (int i = 0; i < 6; i++)
				{
					L2Npc mob = addSpawn(NAIA_FAILAN, NAIA_LOCK[i][0], NAIA_LOCK[i][1], NAIA_LOCK[i][2], NAIA_LOCK[i][3], false, 0);
					spawnnaia.add(mob);
					mob.setIsInvul(true);
					mob.setIsImmobilized(true);
					mob.setIsOverloaded(true);
				}
				startQuestTimer("cast", 5000, npc, null, false);
				break;
			case "check":
				if (status == 1)
				{
					startQuestTimer("check", 180000, npc, null, false);
				}
				if (status == 3)
				{
					startQuestTimer("desp", 180000, npc, null, false);
				}
				status = 3;
				break;
			case "desp":
				cancelQuestTimers("cast");
				for (L2Npc spawned_naia : spawnnaia)
				{
					spawned_naia.deleteMe();
				}
				status = 0;
				break;
			case "cast":
				for (L2Npc spawned_naia : spawnnaia)
				{
					spawned_naia.doCast(SkillTable.getInstance().getInfo(NAIA_SPROUT, 1));
				}
				startQuestTimer("cast", 5000, npc, null, false);
				break;
		}
		return event;
	}
}
