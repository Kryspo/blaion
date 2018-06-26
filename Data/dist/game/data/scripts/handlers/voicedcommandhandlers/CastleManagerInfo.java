package handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SiegeInfo;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class CastleManagerInfo implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"castlemanager",
		"siege_gludio",
		"siege_dion",
		"siege_giran",
		"siege_oren",
		"siege_aden",
		"siege_innadril",
		"siege_goddard",
		"siege_rune",
		"siege_schuttgart"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		switch (command)
		{
			case "castlemanager":
				String htmFile = "data/html/mods/CastleManager.htm";
				NpcHtmlMessage msg = new NpcHtmlMessage(5);
				msg.setFile(activeChar.getHtmlPrefix(), htmFile);
				activeChar.sendPacket(msg);
				break;
			case "siege_gludio":
				Castle gludio = CastleManager.getInstance().getCastleById(1);
				if (gludio != null)
				{
					activeChar.sendPacket(new SiegeInfo(gludio));
				}
				break;
			case "siege_dion":
				Castle dion = CastleManager.getInstance().getCastleById(2);
				if (dion != null)
				{
					activeChar.sendPacket(new SiegeInfo(dion));
				}
				break;
			case "siege_giran":
				Castle giran = CastleManager.getInstance().getCastleById(3);
				if (giran != null)
				{
					activeChar.sendPacket(new SiegeInfo(giran));
				}
				break;
			case "siege_oren":
				Castle oren = CastleManager.getInstance().getCastleById(4);
				if (oren != null)
				{
					activeChar.sendPacket(new SiegeInfo(oren));
				}
				break;
			case "siege_aden":
				Castle aden = CastleManager.getInstance().getCastleById(5);
				if (aden != null)
				{
					activeChar.sendPacket(new SiegeInfo(aden));
				}
				break;
			case "siege_innadril":
				Castle innadril = CastleManager.getInstance().getCastleById(6);
				if (innadril != null)
				{
					activeChar.sendPacket(new SiegeInfo(innadril));
				}
				break;
			case "siege_goddard":
				Castle goddard = CastleManager.getInstance().getCastleById(7);
				if (goddard != null)
				{
					activeChar.sendPacket(new SiegeInfo(goddard));
				}
				break;
			case "siege_rune":
				Castle rune = CastleManager.getInstance().getCastleById(8);
				if (rune != null)
				{
					activeChar.sendPacket(new SiegeInfo(rune));
				}
				break;
			case "siege_schuttgart":
				Castle schuttgart = CastleManager.getInstance().getCastleById(9);
				if (schuttgart != null)
				{
					activeChar.sendPacket(new SiegeInfo(schuttgart));
				}
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