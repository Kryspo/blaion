package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class FieldOfWhispersSilence extends L2AttackableAIScript
{
	private static final int BRAZIER_OF_PURITY = 18806;
	private static final int GUARDIAN_SPIRITS_OF_MAGIC_FORCE = 22659;
	
	public FieldOfWhispersSilence()
	{
		super(-1, FieldOfWhispersSilence.class.getSimpleName(), "ai");
		AggroRangeEnterNpcs(BRAZIER_OF_PURITY);
		AggroRangeEnterNpcs(GUARDIAN_SPIRITS_OF_MAGIC_FORCE);
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		switch (npc.getNpcId())
		{
			case BRAZIER_OF_PURITY:
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "The purification field is being attacked. Guardian Spirits! Protect the Magic Force!!"));
				break;
			case GUARDIAN_SPIRITS_OF_MAGIC_FORCE:
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), 1, npc.getName(), "Even the Magic Force binds you, you will never be forgiven..."));
				break;
		}
		
		return null;
	}
}