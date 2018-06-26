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

import java.util.Calendar;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.util.Rnd;

public class RegionBBSManager extends BaseBBSManager
{
	public int getPage()
	{
		return 10;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		
		String[] commands = command.split(" ");
		
		int page = 1;
		
		if (commands.length > 1)
		{
			page = Integer.parseInt(commands[1]);
		}
		
		if (page < 0) // SIGN IN
		{
			page = 1;
			if (activeChar.getLevel() < Config.SIGN_IN_MIN_LEVEL)
			{
				activeChar.sendMessage("You must be over " + Config.SIGN_IN_MIN_LEVEL + " level.");
				showOnlinePlayer(activeChar, page);
				return;
			}
			
			long nextTime = activeChar.getVariables().getLong("signIn", 0);
			
			if (nextTime > System.currentTimeMillis())
			{
				activeChar.sendMessage("You already Sign In today , come later please!");
				showOnlinePlayer(activeChar, page);
				return;
			}
			
			if (!Config.SIGN_IN_REWARDS.isEmpty())
			{
				activeChar.addItem("SignIn", Config.SIGN_IN_REWARDS.get(Rnd.get(Config.SIGN_IN_REWARDS.size())), null, true);
			}
			
			Calendar c = Calendar.getInstance();
			if (c.get(Calendar.HOUR_OF_DAY) >= 6)
			{
				c.add(Calendar.DATE, 1);
			}
			c.set(Calendar.HOUR_OF_DAY, 6);
			activeChar.getVariables().set("signIn", c.getTimeInMillis());
			if (Config.SIGN_ANNOUNCEMENT.length() > 0)
			{
				Broadcast.announceToOnlinePlayers(Config.SIGN_ANNOUNCEMENT.replace("%player%", activeChar.getName()), true);
			}
		}
		
		showOnlinePlayer(activeChar, page);
	}
	
	private void showOnlinePlayer(L2PcInstance activeChar, int page)
	{
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/online.htm");
		int start = 0, end = 0, pages = 0;
		Object[] players = L2World.getInstance().getPlayers().stream().filter((player) ->
		{
			return !player.isDummy();
		}).toArray();
		pages = (players.length % getPage()) == 0 ? players.length / getPage() : (players.length / getPage()) + 1;
		start = (page - 1) * getPage();
		end = page == pages ? players.length - 1 : (page * getPage()) - 1;
		
		html = html.replace("%count%", Integer.toString(L2World.getInstance().getAllPlayersCount() + Config.FAKE_PLAYERS + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS)));
		StringBuilder sb = new StringBuilder(players.length * 32);
		StringBuilder pagehtml = new StringBuilder();
		int uptime, h, m, s = 0;
		L2PcInstance player;
		for (int i = start; i <= end; i++)
		{
			player = (L2PcInstance) players[i];
			uptime = (int) player.getUptime() / 1000;
			h = uptime / 3600;
			m = (uptime - (h * 3600)) / 60;
			s = ((uptime - (h * 3600)) - (m * 60));
			sb.append("<table height=32 cellspacing=0 width=777 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
			sb.append("<tr><td fixwidth=5></td><td fixwidth=80>").append(player.getName()).append("</td>");
			sb.append("<td fixwidth=32 align=center>Lv.").append(player.getLevel()).append("</td>");
			sb.append("<td fixwidth=80 align=center>").append(player.getTemplate().className).append("</td>");
			sb.append("<td fixwidth=32 align=center>").append(player.getAppearance().getSex() ? "female" : "male").append("</td>");
			sb.append("<td fixwidth=80 align=center>").append(h + "<font color=LEVEL>H:</font> " + m + "<font color=LEVEL>M:</font> " + s + "<font color=LEVEL>S</font> ").append("</td>");
			sb.append("<td fixwidth=50 align=center>").append(player.isHero() ? "<font color=5CACEE>" + "Hero" + "</font>" : player.isNoble() ? "<font color=C0FF3E>" + "Noble" + "</font>" : "<font color=FF0000>---</font>").append("</td>");
			sb.append("<td fixwidth=60 align=center>").append(player.isPremiumAccount() ? "" + "<font color=00FF00>Yes</font>" + "</font>" : "<font color=FF0000>---</font>").append("</td>");
			sb.append("<td fixwidth=120 align=center>").append(player.getClan() == null ? "<font color=FF0000>---</font>" : player.getClan().getName()).append("</td>");
			sb.append("</tr></table>");
		}
		
		if (page > 1)
		{
			pagehtml.append("<td>").append("<button value=\"Prev\" action=\"bypass _bbsloc " + (page - 1) + "\" width=75 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		}
		
		if (end < (players.length - 1))
		{
			pagehtml.append("<td>").append("<button value=\"Next\" action=\"bypass _bbsloc " + (page + 1) + "\" width=75 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		}
		
		html = html.replace("%list%", sb.toString());
		html = html.replace("%pages%", pagehtml.toString());
		separateAndSend(html, activeChar);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// Do nothing for now
	}
	
	/**
	 * Gets the single instance of RegionBBSManager.
	 * @return single instance of RegionBBSManager
	 */
	public static RegionBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RegionBBSManager _instance = new RegionBBSManager();
	}
}