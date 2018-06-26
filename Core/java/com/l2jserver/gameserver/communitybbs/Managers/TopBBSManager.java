/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://l2jpsproject.eu/>.
 */
package com.l2jserver.gameserver.communitybbs.Managers;

import java.util.StringTokenizer;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;
import com.l2jserver.util.Rnd;

public class TopBBSManager extends BaseBBSManager
{
	private TopBBSManager()
	{
	}
	
	public int onlineplayers = 0;
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		if (command.equals("_bbstop"))
		{
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/index.htm");
			adminReply.replace("%playername%", activeChar.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			onlineplayers = L2World.getInstance().getAllPlayersCount();
			adminReply.replace("%online%", String.valueOf(onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		else if (command.equals("_bbshome"))
		{
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/index.htm");
			adminReply.replace("%playername%", activeChar.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			onlineplayers = L2World.getInstance().getAllPlayersCount();
			adminReply.replace("%online%", String.valueOf(onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		else if (command.startsWith("_bbstop2;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			String idp = st.nextToken();
			if ("settings".equals(idp))
			{
				String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/settings.htm");
				// html = PcSettingBBSManager.getInstance().replaceButton(html, activeChar);
				separateAndSend(html, activeChar);
				return;
			}
			sendHtm(activeChar, "data/html/CommunityBoard/" + idp + ".htm");
		}
		else if (command.startsWith("_bbstop;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			int idp = Integer.parseInt(st.nextToken());
			
			NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
			adminReply.setFile(activeChar.getLang(), "data/html/CommunityBoard/" + idp + ".htm");
			adminReply.replace("%playername%", activeChar.getName());
			adminReply.replace("%servername%", Config.SERVER_NAME);
			onlineplayers = L2World.getInstance().getAllPlayersCount();
			adminReply.replace("%online%", String.valueOf(onlineplayers + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
			separateAndSend(adminReply.getHtm(), activeChar);
		}
		else if (command.startsWith("_bbsAugment;add"))
		{
			sendHtm(activeChar, "data/html/CommunityBoard/7.htm");
		}
		else if (command.startsWith("_bbsAugment;remove"))
		{
			sendHtm(activeChar, "data/html/CommunityBoard/7.htm");
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	private boolean sendHtm(L2PcInstance player, String path)
	{
		String oriPath = path;
		if ((player.getLang() != null) && !player.getLang().equalsIgnoreCase("en"))
		{
			if (path.contains("html/"))
			{
				path = path.replace("html/", "html-" + player.getLang() + "/");
			}
		}
		String content = HtmCache.getInstance().getHtm(path);
		if ((content == null) && !oriPath.equals(path))
		{
			content = HtmCache.getInstance().getHtm(oriPath);
		}
		if (content == null)
		{
			return false;
		}
		
		separateAndSend(content, player);
		return true;
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	}
	
	public static TopBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final TopBBSManager _instance = new TopBBSManager();
	}
}