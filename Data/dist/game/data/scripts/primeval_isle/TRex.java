package primeval_isle;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;
import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class TRex extends L2AttackableAIScript
{
	//@formatter:off
	private final int[] TREX =
	{
		22215,22216,22217
	};
	
	private final TIntObjectHashMap<int[]> SKILLS_HP = new TIntObjectHashMap<int[]>();
	
	public TRex()
	{
		super(-1, TRex.class.getSimpleName(), "primeval_isle");
		// skillId:[minHp,maxHp]
		SKILLS_HP.put(3626, new int[]{65,100});
		SKILLS_HP.put(3627, new int[]{25,65});
		SKILLS_HP.put(3628, new int[]{0,25});
		SkillSeeNpcs(TREX);
		//@formatter:on
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance player, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		// if not npc in targets: return
		boolean b = false;
		for (L2Object trg : targets)
		{
			if (trg == npc)
			{
				b = true;
			}
		}
		if (!b)
		{
			return super.onSkillSee(npc, player, skill, targets, isPet);
		}
		if ((skill.getId() >= 3626) && (skill.getId() <= 3628))
		{
			int minHp = ((SKILLS_HP.get(skill.getId())[0] * npc.getMaxHp()) / 100);
			int maxHp = ((SKILLS_HP.get(skill.getId())[1] * npc.getMaxHp()) / 100);
			if ((npc.getCurrentHp() < minHp) || (npc.getCurrentHp() > maxHp))
			{
				npc.stopSkillEffects(skill.getId());
			}
		}
		return super.onSkillSee(npc, player, skill, targets, isPet);
	}
}
