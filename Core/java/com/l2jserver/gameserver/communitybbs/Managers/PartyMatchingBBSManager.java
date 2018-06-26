package com.l2jserver.gameserver.communitybbs.Managers;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;

public class PartyMatchingBBSManager extends BaseBBSManager
{
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbslink"))
		{
			refreshBroad(activeChar);
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>The command: " + command + " is not implemented yet.</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	public void refreshBroad(L2PcInstance activeChar)
	{
		String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/Home.htm").replace("%partyMatchingMembers%", PartyMatchingBoard.getInstance().loadPartyMatchingMembersList(activeChar));
		
		if (content == null)
		{
			content = "<html><body><br><br><center>404 :File not found: 'data/html/CommunityBoard/Home.htm'</center></body></html>";
		}
		
		separateAndSend(content, activeChar);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
	
	public static PartyMatchingBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PartyMatchingBBSManager _instance = new PartyMatchingBBSManager();
	}
}