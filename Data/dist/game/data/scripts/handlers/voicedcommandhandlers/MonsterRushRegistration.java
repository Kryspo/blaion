package handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.MonsterRush;

/**
 * Monster Rush Event voiced reg
 * @author L2jPrivateDevelopersTeam
 */
public class MonsterRushRegistration implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"mrjoin",
		"mrleave"
	};
	
	@Override
	public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String target)
	{
		switch (command)
		{
			case "mrjoin":
				if ((activeChar.isAlikeDead() || activeChar._inEventCTF || activeChar._inEventDM || activeChar.isInStoreMode() || activeChar.isRooted() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isFestivalParticipant() || activeChar.isInsideZone(L2Character.ZONE_PVP)))
				{
					activeChar.sendMessage("You cannot register to event because you registred in another event or you are in PVP or combat");
				}
				else
				{
					Announcements.getInstance().announceToAll("Monster Rush: player " + activeChar.getName() + " with " + activeChar.getLevel() + " level registred to event.");
					MonsterRush.doReg(activeChar);
				}
				break;
			case "mrleave":
				Announcements.getInstance().announceToAll("Monster Rush: player " + activeChar.getName() + " unregistred from event.");
				MonsterRush.doUnReg(activeChar);
				break;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
