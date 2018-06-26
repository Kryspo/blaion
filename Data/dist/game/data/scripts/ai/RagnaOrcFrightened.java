package ai;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.skills.SkillHolder;

/**
 * @author L2jPrivateDevelopersTeam
 */
public final class RagnaOrcFrightened extends L2AttackableAIScript
{
	// NPC ID
	private static final int MOB_ID = 18807;
	// Chances
	private static final int ADENA = 10000;
	private static final int CHANCE = 1000;
	private static final int ADENA2 = 1000000;
	private static final int CHANCE2 = 10;
	// Skill
	private static final SkillHolder SKILL = new SkillHolder(6234, 1);
	
	public RagnaOrcFrightened()
	{
		super(-1, RagnaOrcFrightened.class.getSimpleName(), "ai");
		AttackNpcs(MOB_ID);
		KillNpcs(MOB_ID);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isScriptValue(0))
		{
			npc.setScriptValue(1);
			startQuestTimer("say", (getRandom(5) + 3) * 1000, npc, null, true);
		}
		else if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.2)) && npc.isScriptValue(1))
		{
			startQuestTimer("reward", 10000, npc, attacker);
			broadcastNpcSay(npc, Say2.ALL, "Wait... Wait! Stop! Save me, and I'll give you 10,000,000 adena!!");
			npc.setScriptValue(2);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		final String msg = getRandomBoolean() ? "Ugh... A curse upon you...!" : "I really... didn't want... to fight...";
		broadcastNpcSay(npc, Say2.ALL, msg);
		cancelQuestTimer("say", npc, null);
		cancelQuestTimer("reward", npc, player);
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "say":
			{
				if (npc.isDead() || !npc.isScriptValue(1))
				{
					cancelQuestTimer("say", npc, null);
					return null;
				}
				final String msg = getRandomBoolean() ? "I... don't want to fight..." : "Is this really necessary...?";
				broadcastNpcSay(npc, Say2.ALL, msg);
				break;
			}
			case "reward":
			{
				if (!npc.isDead() && npc.isScriptValue(2))
				{
					if (getRandom(100000) < CHANCE2)
					{
						final String msg = getRandomBoolean() ? "Th... Thanks... I could have become good friends with you..." : "I'll give you 10,000,000 adena, like I promised! I might be an orc who keeps my promises!";
						broadcastNpcSay(npc, Say2.ALL, msg);
						npc.setScriptValue(3);
						npc.doCast(SKILL.getSkill());
						for (int i = 0; i < 10; i++)
						{
							npc.dropItem(player, Inventory.ADENA_ID, ADENA2);
						}
					}
					else if (getRandom(100000) < CHANCE)
					{
						final String msg = getRandomBoolean() ? "Th... Thanks... I could have become good friends with you..." : "Sorry but this is all I have.. Give me a break!";
						broadcastNpcSay(npc, Say2.ALL, msg);
						npc.setScriptValue(3);
						npc.doCast(SKILL.getSkill());
						for (int i = 0; i < 10; i++)
						{
							((L2Attackable) npc).dropItem(player, Inventory.ADENA_ID, ADENA);
						}
					}
					else
					{
						final String msg = getRandomBoolean() ? "Thanks, but that thing about 10,000,000 adena was a lie! See ya!!" : "You're pretty dumb to believe me!";
						broadcastNpcSay(npc, Say2.ALL, msg);
					}
					startQuestTimer("despawn", 1000, npc, null);
				}
				break;
			}
			case "despawn":
			{
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location((npc.getX() + getRandom(-800, 800)), (npc.getY() + getRandom(-800, 800)), npc.getZ(), npc.getHeading()));
				npc.deleteMe();
				break;
			}
		}
		return null;
	}
}