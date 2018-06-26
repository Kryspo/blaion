package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * @author L2jPrivateDevelopersTeam
 */
public final class RagnaOrcs extends L2AttackableAIScript
{
	// RAGNA ORC COMMANDERS AND MINIONS
	private static final int RAGNA_ORC_COMMANDER = 22694;
	private static final int RAGNA_ORC_COMMANDER_MINION_1_HEALER = 22695;
	private static final int RAGNA_ORC_COMMANDER_MINION_2_HERO = 22693;
	private static final int RAGNA_ORC_COMMANDER_MINION_3_SEER = 22697;
	// RAGNA ORC HEROES AND MINIONS
	private static final int RAGNA_ORC_HERO = 22693;
	private static final int RAGNA_ORC_HERO_HEALER = 22695;
	private static final int RAGNA_ORC_HERO_WARRIOR = 22692;
	// RAGNA ORC SEER AND MINIONS
	private static final int RAGNA_ORC_SEER = 22697;
	private static final int RAGNA_ORC_SEER_SHAMAN = 22696;
	private static final int RAGNA_ORC_SEER_WARRIOR = 22692;
	
	public RagnaOrcs()
	{
		super(-1, RagnaOrcs.class.getSimpleName(), "ai");
		SpawnNpcs(RAGNA_ORC_COMMANDER, RAGNA_ORC_COMMANDER_MINION_1_HEALER, RAGNA_ORC_COMMANDER_MINION_2_HERO, RAGNA_ORC_COMMANDER_MINION_3_SEER);
		SpawnNpcs(RAGNA_ORC_HERO, RAGNA_ORC_HERO_HEALER, RAGNA_ORC_HERO_WARRIOR);
		SpawnNpcs(RAGNA_ORC_SEER, RAGNA_ORC_SEER_SHAMAN, RAGNA_ORC_SEER_WARRIOR);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case RAGNA_ORC_COMMANDER:
				addSpawn(RAGNA_ORC_COMMANDER_MINION_1_HEALER, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				if (getRandom(100) < 50)
				{
					addSpawn(RAGNA_ORC_COMMANDER_MINION_2_HERO, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				else
				{
					addSpawn(RAGNA_ORC_COMMANDER_MINION_3_SEER, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				break;
			case RAGNA_ORC_HERO:
				if (getRandom(100) < 70)
				{
					addSpawn(RAGNA_ORC_HERO_HEALER, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				else
				{
					addSpawn(RAGNA_ORC_HERO_WARRIOR, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				break;
			case RAGNA_ORC_SEER:
				if (getRandom(100) < 50)
				{
					addSpawn(RAGNA_ORC_SEER_SHAMAN, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				else
				{
					addSpawn(RAGNA_ORC_SEER_WARRIOR, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				break;
		}
		return super.onSpawn(npc);
	}
}