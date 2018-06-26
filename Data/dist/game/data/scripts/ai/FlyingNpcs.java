package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class FlyingNpcs extends L2AttackableAIScript
{
	// @formatter:off
	private final static int[] FLYING_NPCS =
	{
		// Flying Monsters
		13148, 22602, 22603, 22604, 22605, 22606, 22607, 22608,
		22609, 22610, 22611, 22612, 22613, 22614, 22615, 25629,
		25630, 25633,
		
		// Flying Npcs
		18684, 18685, 18686, 18687, 18688, 18689, 18690, 18691, 18692,
		
		// Flying Raid Bosses
		25623, 25624, 25625, 25626,
	};
	// @formatter:on
	
	public FlyingNpcs()
	{
		super(-1, FlyingNpcs.class.getSimpleName(), "ai");
		SpawnNpcs(FLYING_NPCS);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.setIsFlying(true);
		return super.onSpawn(npc);
	}
}
