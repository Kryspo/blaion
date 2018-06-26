package handlers.admincommandhandlers;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.MonsterRush;

/**
 * @author L2jPrivateDevelopers
 */
public class AdminMonsterRush implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_mrush_start",
		"admin_mrush_teleport",
		"admin_mrush_abort",
		"admin_mrush_test"
	};
	
	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar)
	{
		switch (command)
		{
			case "admin_mrush_start":
				MonsterRush.startRegister();
				break;
			case "admin_mrush_teleport":
				MonsterRush.startEvent();
				break;
			case "admin_mrush_abort":
				MonsterRush.abortEvent();
				break;
			case "admin_mrush_test":
				activeChar.sendMessage("Im in running event? " + activeChar.getIsInMonsterRush());
				activeChar.sendMessage("Running registration? " + MonsterRush.isParticipating());
				activeChar.sendMessage("Event Status: " + MonsterRush._status);
				break;
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
