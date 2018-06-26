package Kamaloka;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.skills.SkillHolder;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public final class CrimsonHatuOtis extends L2AttackableAIScript
{
	// Npc
	private static final int CRIMSON_HATU_OTIS = 18558;
	// Skills
	private static SkillHolder BOSS_SPINING_SLASH = new SkillHolder(4737, 1);
	private static SkillHolder BOSS_HASTE = new SkillHolder(4175, 1);
	
	public CrimsonHatuOtis()
	{
		super(-1, CrimsonHatuOtis.class.getSimpleName(), "Kamaloka");
		AttackNpcs(CRIMSON_HATU_OTIS);
		KillNpcs(CRIMSON_HATU_OTIS);
	}
	
	@Override
	public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SKILL":
			{
				if (npc.isDead())
				{
					cancelQuestTimer("SKILL", npc, null);
					return null;
				}
				npc.setTarget(player);
				npc.doCast(BOSS_SPINING_SLASH.getSkill());
				startQuestTimer("SKILL", 60000, npc, null);
				break;
			}
			case "BUFF":
			{
				if (npc.isScriptValue(2))
				{
					npc.setTarget(npc);
					npc.doCast(BOSS_HASTE.getSkill());
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			startQuestTimer("SKILL", 5000, npc, null);
		}
		else if (npc.isScriptValue(1) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.3)))
		{
			npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "I ve had it up to here with you. I will take care of you!"));
			npc.setScriptValue(2);
			startQuestTimer("BUFF", 1000, npc, null);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		cancelQuestTimer("SKILL", npc, null);
		cancelQuestTimer("BUFF", npc, null);
		return super.onKill(npc, player, isSummon);
	}
	
	public static void main(String[] args)
	{
		new CrimsonHatuOtis();
	}
}