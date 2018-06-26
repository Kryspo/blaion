package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.skills.SkillHolder;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class HolyBrazier extends L2AttackableAIScript
{
	// NPC
	public static final int HOLY_BRAZIERS = 32027;
	public static final int GUARDIANS_OF_THE_GRAIL = 22133;
	
	// SKILLS
	private static final SkillHolder AV_TELEPORT = new SkillHolder(4671, 1);
	
	public static L2Npc holybrazier_12, holybrazier_11, holybrazier_10, holybrazier_9, holybrazier_8, holybrazier_7, holybrazier_6, holybrazier_5, holybrazier_4, holybrazier_3, holybrazier_2, holybrazier_1 = null;
	public static L2Npc holybrazier_12_guard, holybrazier_11_guard, holybrazier_10_guard, holybrazier_9_guard, holybrazier_8_guard, holybrazier_7_guard, holybrazier_6_guard, holybrazier_5_guard, holybrazier_4_guard, holybrazier_3_guard, holybrazier_2_guard, holybrazier_1_guard = null;
	
	public HolyBrazier()
	{
		super(-1, HolyBrazier.class.getSimpleName(), "ai");
		
		AggroRangeEnterNpcs(GUARDIANS_OF_THE_GRAIL);
		registerMobs(HOLY_BRAZIERS);
		registerMobs(GUARDIANS_OF_THE_GRAIL);
		
		startQuestTimer("monatery_start_12", 41000, null, null, false);
		startQuestTimer("monatery_start_11", 40000, null, null, false);
		startQuestTimer("monatery_start_10", 39000, null, null, false);
		startQuestTimer("monatery_start_9", 38000, null, null, false);
		startQuestTimer("monatery_start_8", 37000, null, null, false);
		startQuestTimer("monatery_start_7", 36000, null, null, false);
		startQuestTimer("monatery_start_6", 35000, null, null, false);
		startQuestTimer("monatery_start_5", 34000, null, null, false);
		startQuestTimer("monatery_start_4", 33000, null, null, false);
		startQuestTimer("monatery_start_3", 32000, null, null, false);
		startQuestTimer("monatery_start_2", 31000, null, null, false);
		startQuestTimer("monatery_start_1", 30000, null, null, false);
		
		_log.info("MoS AI: Spawning Holy Braziers...");
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "av_teleport":
				npc.setTarget(player);
				npc.doCast(AV_TELEPORT.getSkill());
				break;
			case "monatery_start_12":
				holybrazier_12 = addSpawn(HOLY_BRAZIERS, 111217, -80955, -1637, 0, false, 0, false);
				holybrazier_12_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_12.getX(), holybrazier_12.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 12 location");
				break;
			case "monatery_start_12_only_guard":
				holybrazier_12_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_12.getX(), holybrazier_12.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 12 location");
				break;
			case "monatery_start_11":
				holybrazier_11 = addSpawn(HOLY_BRAZIERS, 111824, -80955, -1637, 0, false, 0, false);
				holybrazier_11_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_11.getX(), holybrazier_11.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 11 location");
				break;
			case "monatery_start_11_only_guard":
				holybrazier_11_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_11.getX(), holybrazier_11.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 11 location");
				break;
			case "monatery_start_10":
				holybrazier_10 = addSpawn(HOLY_BRAZIERS, 112491, -80955, -1637, 0, false, 0, false);
				holybrazier_10_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_10.getX(), holybrazier_10.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 10 location");
				break;
			case "monatery_start_10_only_guard":
				holybrazier_10_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_10.getX(), holybrazier_10.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 10 location");
				break;
			case "monatery_start_9":
				holybrazier_9 = addSpawn(HOLY_BRAZIERS, 113108, -80955, -1637, 0, false, 0, false);
				holybrazier_9_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_9.getX(), holybrazier_9.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 9 location");
				break;
			case "monatery_start_9_only_guard":
				holybrazier_9_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_9.getX(), holybrazier_9.getY(), -1637, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 9 location");
				break;
			case "monatery_start_8":
				holybrazier_8 = addSpawn(HOLY_BRAZIERS, 109630, -74921, -1120, 0, false, 0, false);
				holybrazier_8_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_8.getX(), holybrazier_8.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 8 location");
				break;
			case "monatery_start_8_only_guard":
				holybrazier_8_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_8.getX(), holybrazier_8.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 8 location");
				break;
			case "monatery_start_7":
				holybrazier_7 = addSpawn(HOLY_BRAZIERS, 109630, -75541, -1120, 0, false, 0, false);
				holybrazier_7_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_7.getX(), holybrazier_7.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 7 location");
				break;
			case "monatery_start_7_only_guard":
				holybrazier_7_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_7.getX(), holybrazier_7.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 7 location");
				break;
			case "monatery_start_6":
				holybrazier_6 = addSpawn(HOLY_BRAZIERS, 109630, -76189, -1120, 0, false, 0, false);
				holybrazier_6_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_6.getX(), holybrazier_6.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 6 location");
				break;
			case "monatery_start_6_only_guard":
				holybrazier_6_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_6.getX(), holybrazier_6.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 6 location");
				break;
			case "monatery_start_5":
				holybrazier_5 = addSpawn(HOLY_BRAZIERS, 109630, -76789, -1120, 0, false, 0, false);
				holybrazier_5_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_5.getX(), holybrazier_5.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 5 location");
				break;
			case "monatery_start_5_only_guard":
				holybrazier_5_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_5.getX(), holybrazier_5.getY(), -1120, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 5 location");
				break;
			case "monatery_start_4":
				holybrazier_4 = addSpawn(HOLY_BRAZIERS, 114416, -72070, -597, 0, false, 0, false);
				holybrazier_4_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_4.getX(), holybrazier_4.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 4 location");
				break;
			case "monatery_start_4_only_guard":
				holybrazier_4_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_4.getX(), holybrazier_4.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 4 location");
				break;
			case "monatery_start_3":
				holybrazier_3 = addSpawn(HOLY_BRAZIERS, 113767, -72070, -597, 0, false, 0, false);
				holybrazier_3_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_3.getX(), holybrazier_3.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 3 location");
				break;
			case "monatery_start_3_only_guard":
				holybrazier_3_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_3.getX(), holybrazier_3.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 3 location");
				break;
			case "monatery_start_2":
				holybrazier_2 = addSpawn(HOLY_BRAZIERS, 113117, -72070, -597, 0, false, 0, false);
				holybrazier_2_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_2.getX(), holybrazier_2.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 2 location");
				break;
			case "monatery_start_2_only_guard":
				holybrazier_2_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_2.getX(), holybrazier_2.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 2 location");
				break;
			case "monatery_start_1":
				holybrazier_1 = addSpawn(HOLY_BRAZIERS, 112489, -72070, -597, 0, false, 0, false);
				holybrazier_1_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_1.getX(), holybrazier_1.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 1 location");
				break;
			case "monatery_start_1_only_guard":
				holybrazier_1_guard = addSpawn(GUARDIANS_OF_THE_GRAIL, 50 + holybrazier_1.getX(), holybrazier_1.getY(), -597, 0, false, 0, false);
				_log.info("MoS AI: Spawned Guard on Holy Brazier 1 location");
				break;
		}
		return event;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc.getNpcId() == GUARDIANS_OF_THE_GRAIL)
		{
			if (holybrazier_12_guard.isDead())
			{
				holybrazier_12_guard = null;
				startQuestTimer("monatery_start_12_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_11_guard.isDead())
			{
				holybrazier_11_guard = null;
				startQuestTimer("monatery_start_11_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_10_guard.isDead())
			{
				holybrazier_10_guard = null;
				startQuestTimer("monatery_start_10_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_9_guard.isDead())
			{
				holybrazier_9_guard = null;
				startQuestTimer("monatery_start_9_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_8_guard.isDead())
			{
				holybrazier_8_guard = null;
				startQuestTimer("monatery_start_8_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_7_guard.isDead())
			{
				holybrazier_7_guard = null;
				startQuestTimer("monatery_start_7_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_6_guard.isDead())
			{
				holybrazier_6_guard = null;
				startQuestTimer("monatery_start_6_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_5_guard.isDead())
			{
				holybrazier_5_guard = null;
				startQuestTimer("monatery_start_5_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_4_guard.isDead())
			{
				holybrazier_4_guard = null;
				startQuestTimer("monatery_start_4_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_3_guard.isDead())
			{
				holybrazier_3_guard = null;
				startQuestTimer("monatery_start_3_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_2_guard.isDead())
			{
				holybrazier_2_guard = null;
				startQuestTimer("monatery_start_2_only_guard", 1000, npc, null, false);
			}
			else if (holybrazier_1_guard.isDead())
			{
				holybrazier_1_guard = null;
				startQuestTimer("monatery_start_1_only_guard", 1000, npc, null, false);
			}
		}
		else if (npc.getNpcId() == HOLY_BRAZIERS)
		{
			if (holybrazier_1.isDead())
			{
				if (holybrazier_1_guard != null)
				{
					holybrazier_1_guard.deleteMe();
					holybrazier_1_guard = null;
				}
				holybrazier_1 = null;
				startQuestTimer("monatery_start_1", 1000, npc, null, false);
			}
			else if (holybrazier_2.isDead())
			{
				if (holybrazier_2_guard != null)
				{
					holybrazier_2_guard.deleteMe();
					holybrazier_2_guard = null;
				}
				holybrazier_2 = null;
				startQuestTimer("monatery_start_2", 1000, npc, null, false);
			}
			else if (holybrazier_3.isDead())
			{
				if (holybrazier_3_guard != null)
				{
					holybrazier_3_guard.deleteMe();
					holybrazier_3_guard = null;
				}
				holybrazier_3 = null;
				startQuestTimer("monatery_start_3", 1000, npc, null, false);
			}
			else if (holybrazier_4.isDead())
			{
				if (holybrazier_4_guard != null)
				{
					holybrazier_4_guard.deleteMe();
					holybrazier_4_guard = null;
				}
				holybrazier_4 = null;
				startQuestTimer("monatery_start_4", 1000, npc, null, false);
			}
			else if (holybrazier_5.isDead())
			{
				if (holybrazier_5_guard != null)
				{
					holybrazier_5_guard.deleteMe();
					holybrazier_5_guard = null;
				}
				holybrazier_5 = null;
				startQuestTimer("monatery_start_5", 1000, npc, null, false);
			}
			else if (holybrazier_4.isDead())
			{
				if (holybrazier_4_guard != null)
				{
					holybrazier_4_guard.deleteMe();
					holybrazier_4_guard = null;
				}
				holybrazier_4 = null;
				startQuestTimer("monatery_start_4", 1000, npc, null, false);
			}
			else if (holybrazier_7.isDead())
			{
				if (holybrazier_7_guard != null)
				{
					holybrazier_7_guard.deleteMe();
					holybrazier_7_guard = null;
				}
				holybrazier_7 = null;
				startQuestTimer("monatery_start_7", 1000, npc, null, false);
			}
			else if (holybrazier_8.isDead())
			{
				if (holybrazier_8_guard != null)
				{
					holybrazier_8_guard.deleteMe();
					holybrazier_8_guard = null;
				}
				holybrazier_8 = null;
				startQuestTimer("monatery_start_8", 1000, npc, null, false);
			}
			else if (holybrazier_9.isDead())
			{
				if (holybrazier_9_guard != null)
				{
					holybrazier_9_guard.deleteMe();
					holybrazier_9_guard = null;
				}
				holybrazier_9 = null;
				startQuestTimer("monatery_start_9", 1000, npc, null, false);
			}
			else if (holybrazier_10.isDead())
			{
				if (holybrazier_10_guard != null)
				{
					holybrazier_10_guard.deleteMe();
					holybrazier_10_guard = null;
				}
				holybrazier_10 = null;
				startQuestTimer("monatery_start_10", 1000, npc, null, false);
			}
			else if (holybrazier_11.isDead())
			{
				if (holybrazier_11_guard != null)
				{
					holybrazier_11_guard.deleteMe();
					holybrazier_11_guard = null;
				}
				holybrazier_11 = null;
				startQuestTimer("monatery_start_11", 1000, npc, null, false);
			}
			else if (holybrazier_12.isDead())
			{
				if (holybrazier_12_guard != null)
				{
					holybrazier_12_guard.deleteMe();
					holybrazier_12_guard = null;
				}
				holybrazier_12 = null;
				startQuestTimer("monatery_start_12", 1000, npc, null, false);
			}
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case HOLY_BRAZIERS:
				npc.setIsImmobilized(true);
				break;
			case GUARDIANS_OF_THE_GRAIL:
				npc.setIsNoRndWalk(true);
				break;
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if ((npc.getNpcId() == GUARDIANS_OF_THE_GRAIL) && !npc.isInCombat() && (npc.getTarget() == null))
		{
			npc.setIsNoRndWalk(true);
		}
		else
		{
			startQuestTimer("av_teleport", 1000, npc, player, false);
		}
		return super.onAggroRangeEnter(npc, player, isPet);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if ((npc.getNpcId() == GUARDIANS_OF_THE_GRAIL) && !npc.isInCombat() && (npc.getTarget() == null))
		{
			startQuestTimer("av_teleport", 1000, npc, null, false);
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
}
