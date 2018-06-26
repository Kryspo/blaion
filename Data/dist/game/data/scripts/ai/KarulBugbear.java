package ai;

import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.util.Rnd;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class KarulBugbear extends L2AttackableAIScript
{
	private static final int KARUL_BUGBEAR = 20600;
	
	public KarulBugbear()
	{
		super(-1, KarulBugbear.class.getSimpleName(), "ai");
		AttackNpcs(KARUL_BUGBEAR);
		SpellFinishedNpcs(KARUL_BUGBEAR);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		if (npc.getCurrentHp() == npc.getMaxHp())
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), attacker.getName() + " Watch your back!"));
		}
		return "";
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if (Rnd.chance(75))
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "How dare you turn your back on me!"));
		}
		else if (Rnd.chance(10))
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Your rear is practically unguarded!"));
		}
		return "";
	}
}