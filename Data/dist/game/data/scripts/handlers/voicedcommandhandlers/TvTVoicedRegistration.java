package handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;

/**
 * TvT Register via voiced command
 * @author L2jPrivateDevelopersTeam
 */
public class TvTVoicedRegistration implements IVoicedCommandHandler
{
	private static final String[] _voicedCommands =
	{
		"tvtjoin",
		"tvtleave"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		switch (command)
		{
			case "tvtjoin":
				TvTEvent.onBypass("tvt_event_participation", activeChar, true);
				break;
			case "tvtleave":
				TvTEvent.onBypass("tvt_event_remove_participation", activeChar, true);
				break;
		}
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _voicedCommands;
	}
}
