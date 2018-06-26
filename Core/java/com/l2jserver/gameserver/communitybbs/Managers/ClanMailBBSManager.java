/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.communitybbs.Managers;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class ClanMailBBSManager extends BaseBBSManager
{
	private static ClanMailBBSManager instance = new ClanMailBBSManager();
	private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY");
	
	public static ClanMailBBSManager getInstance()
	{
		return instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		String[] events = command.split(" ");
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/clanMails_list.htm");
		html = html.replace("%button%", activeChar.isClanLeader() ? "<button action=\"bypass _maillist_0_1_0_ writenew\" value=\"New mail\" width=80 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">" : "");
		if (events.length == 1)
		{
			StringBuilder builder = new StringBuilder();
			if (activeChar.getClan() != null)
			{
				activeChar.getClan().getMails().forEach((title, content) ->
				{
					builder.append("<table width=730 height=22 cellspacing=0 cellpadding=-1>");
					builder.append("<tr>");
					builder.append("<td fixwidth=10></td>");
					builder.append("<td fixwidth=200><font name hs=12 color=LEVEL>").append(title).append("</font></td>");
					builder.append("<td fixwidth=100>").append(content[1]).append("</td>");
					builder.append("<td fixwidth=60>").append("<button action=\"bypass _maillist_0_1_0_ read " + title + "\" value=\"Read\" width=60 height=27 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").append("</td>");
					if (activeChar.isClanLeader())
					{
						builder.append("<td fixwidth=60>").append("<button action=\"bypass _maillist_0_1_0_ del " + title + "\" value=\"Del\" width=60 height=27 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").append("</td>");
					}
					builder.append("</tr>");
					builder.append("</table>");
					builder.append("<img src=\"L2UI.SquareGray\" width=755 height=1>");
				});
			}
			html = html.replace("%list%", builder.toString());
		}
		else if (events[1].equals("writenew"))
		{
			if (activeChar.getClan() != null)
			{
				if (!activeChar.getClan().canWriteNewMail())
				{
					html = html.replace("%list%", "Clan Mails is too much!! You must delete one of them");
				}
				else
				{
					html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/clanMails_new.htm");
				}
			}
		}
		else if (events[1].equals("send"))
		{
			if ((activeChar.getClan() != null) && (events.length == 4))
			{
				String title = events[2];
				String content = events[3];
				activeChar.getClan().writeNewMail(title, content, formatter.format(new Date()));
			}
		}
		else if (events[1].equals("read"))
		{
			if ((activeChar.getClan() != null) && (events.length == 3))
			{
				String title = events[2];
				String[] content = activeChar.getClan().getMails().get(title);
				html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/clanMail.htm");
				html = html.replace("%title%", title);
				html = html.replace("%content%", content[0]);
				html = html.replace("%date%", content[1]);
			}
		}
		else if (events[1].equals("del"))
		{
			if ((activeChar.getClan() != null) && (events.length == 3))
			{
				String title = events[2];
				activeChar.getClan().getMails().remove(title);
				L2DatabaseFactory.simpleExcuter("DELETE FROM clan_mail where title = ? and clan_id = ?", title, activeChar.getClan().getClanId());
			}
		}
		separateAndSend(html, activeChar);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub
	}
}
