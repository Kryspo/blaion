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

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.network.serverpackets.FriendPacket;

/**
 * @author Administrator
 */
public class FriendBBSManager extends BaseBBSManager
{
	private static final FriendBBSManager instance = new FriendBBSManager();
	
	public static FriendBBSManager getInstance()
	{
		return instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		String[] events = command.split(" ");
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/friendlist.htm");
		
		if (events.length == 1)
		{
			StringBuilder builder = new StringBuilder();
			
			activeChar.getFriendList().forEach((playerId) ->
			{
				L2PcInstance friend;
				if ((friend = L2World.getInstance().getPlayer(playerId)) != null)
				{
					String OnOff;
					String color;
					if (activeChar.isOnline())
					{
						OnOff = "ONLINE";
						color = "00CC00";
					}
					else
					{
						OnOff = "OFFLINE";
						color = "D70000";
					}
					builder.append("<table width=580 height=22 cellspacing=0 cellpadding=-1>");
					builder.append("<tr>");
					builder.append("<td fixwidth=5></td>");
					builder.append("<td fixwidth=150>").append(friend.getName()).append("</td>");
					builder.append("<td fixwidth=40>").append(friend.getLevel()).append("</td>");
					builder.append("<td fixwidth=14 height=18><img src=\"" + ClassId.getClassIcon(friend.getClassIdPM()) + "\" width=12 height=12></td>");
					builder.append("<td fixwidth=125>").append(friend.getTemplate().className).append("</td>");
					builder.append("<td fixwidth=80>").append("<font color=" + color + ">" + OnOff).append("</font></td>");
					builder.append("<td fixwidth=80>").append("<button value=\"Invite\" action=\"bypass -h _bbspartymatching invite " + friend.getObjectId() + "\" width=80 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").append("</td>");
					builder.append("<td fixwidth=80>").append("<button value=\"Unfriend\" action=\"bypass -h _friendlist_0_ unfriend " + friend.getObjectId() + "\" width=80 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").append("</td>");
					builder.append("</tr>");
					builder.append("</table>");
					builder.append("<img src=\"L2UI.SquareGray\" width=755 height=1>");
				}
			});
			html = html.replace("%friendlist%", builder.toString());
		}
		else if (events[1].equals("unfriend"))
		{
			int id = Integer.parseInt(events[2]);
			activeChar.getFriendList().remove(Integer.valueOf(id));
			activeChar.sendPacket(new FriendPacket(false, id));
			
			L2PcInstance player = L2World.getInstance().getPlayer(id);
			if (player != null)
			{
				player.getFriendList().remove(Integer.valueOf(activeChar.getObjectId()));
				player.sendPacket(new FriendPacket(false, activeChar.getObjectId()));
			}
			StringBuilder builder = new StringBuilder();
			
			activeChar.getFriendList().forEach((playerId) ->
			{
				L2PcInstance friend;
				if ((friend = L2World.getInstance().getPlayer(playerId)) != null)
				{
					builder.append("<table width=500 height=22 cellspacing=0 cellpadding=-1>");
					builder.append("<tr>");
					builder.append("<td fixwidth=5></td>");
					builder.append("<td fixwidth=150>").append(friend.getName()).append("</td>");
					builder.append("<td fixwidth=40>").append(friend.getLevel()).append("</td>");
					builder.append("<td fixwidth=14 height=18><img src=\"" + ClassId.getClassIcon(friend.getClassIdPM()) + "\" width=12 height=12></td>");
					builder.append("<td fixwidth=125>").append(friend.getTemplate().className).append("</td>");
					builder.append("<td fixwidth=80>").append("<button value=\"Invite\" action=\"bypass -h _bbspartymatching invite " + friend.getObjectId() + "\" width=80 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").append("</td>");
					builder.append("<td fixwidth=80>").append("<button value=\"Unfriend\" action=\"bypass -h _friendlist_0_ unfriend " + friend.getObjectId() + "\" width=80 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">").append("</td>");
					builder.append("</tr>");
					builder.append("</table>");
					builder.append("<img src=\"L2UI.SquareGray\" width=755 height=1>");
				}
			});
			html = html.replace("%friendlist%", builder.toString());
			L2DatabaseFactory.simpleExcuter("DELETE FROM character_friends WHERE (charId=? AND friendId=?) OR (charId=? AND friendId=?)", activeChar.getObjectId(), id, id, activeChar.getObjectId());
		}
		separateAndSend(html, activeChar);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub
		
	}
	
}
