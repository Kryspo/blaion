package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * @author L2jPrivateDevelopersTeam
 */
public final class GraveRobbers extends L2AttackableAIScript
{
	// GRAVE ROBBER SUMMONER
	private static final int GRAVE_ROBBER_SUMMONER = 22678;
	private static final int GRAVE_ROBBER_SUMMONER_SERVITOR_1 = 22683;
	private static final int GRAVE_ROBBER_SUMMONER_SERVITOR_2 = 22684;
	// GRAVE ROBBER MEGICIAN
	private static final int GRAVE_ROBBER_MEGICIAN = 22679;
	private static final int GRAVE_ROBBER_MEGICIAN_SERVITOR_1 = 22685;
	private static final int GRAVE_ROBBER_MEGICIAN_SERVITOR_2 = 22686;
	
	public GraveRobbers()
	{
		super(-1, GraveRobbers.class.getSimpleName(), "ai");
		SpawnNpcs(GRAVE_ROBBER_SUMMONER, GRAVE_ROBBER_SUMMONER_SERVITOR_1, GRAVE_ROBBER_SUMMONER_SERVITOR_2);
		SpawnNpcs(GRAVE_ROBBER_MEGICIAN, GRAVE_ROBBER_MEGICIAN_SERVITOR_1, GRAVE_ROBBER_MEGICIAN_SERVITOR_2);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case GRAVE_ROBBER_SUMMONER:
				if (getRandom(2) == 0)
				{
					addSpawn(GRAVE_ROBBER_SUMMONER_SERVITOR_1, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				else
				{
					addSpawn(GRAVE_ROBBER_SUMMONER_SERVITOR_2, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				break;
			case GRAVE_ROBBER_MEGICIAN:
				if (getRandom(2) == 0)
				{
					addSpawn(GRAVE_ROBBER_MEGICIAN_SERVITOR_1, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				else
				{
					addSpawn(GRAVE_ROBBER_MEGICIAN_SERVITOR_2, npc.getX() + 50, npc.getY() + 50, npc.getZ(), 0, true, 0, false);
				}
				break;
		}
		return super.onSpawn(npc);
	}
}