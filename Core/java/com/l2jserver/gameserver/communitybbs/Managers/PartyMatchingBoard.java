/*
 * Copyright (C) 2004-2013 L2J Server
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.l2jserver.gameserver.communitybbs.BB.MatchFilter;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

import javolution.util.FastSet;

public class PartyMatchingBoard
{
	L2PcInstance player;
	int changeColor = 0;
	
	private final Set<L2PcInstance> members = new FastSet<L2PcInstance>().shared();
	private final Map<Integer, MatchFilter> filters = new HashMap<>();
	
	private static final PartyMatchingBoard instance = new PartyMatchingBoard();
	
	public static PartyMatchingBoard getInstance()
	{
		return instance;
	}
	
	private PartyMatchingBoard()
	{
	}
	
	private void addPartyMatchingMember(StringBuilder partyMatchingList, L2PcInstance member)
	{
		changeColor++;
		partyMatchingList.append("<table width=740 height=35 " + ((changeColor % 2) == 1 ? "" : "bgcolor=171612") + "><tr>");
		partyMatchingList.append("<td fixwidth=245 align=center>" + member.getName() + "</td>");
		partyMatchingList.append("<td fixwidth=50 align=center>" + member.getLevel() + "</td>");
		partyMatchingList.append("<td fixwidth=10></td>");
		
		if (member.getClan() != null)
		{
			partyMatchingList.append("<td fixwidth=235 align=center>" + member.getClan().getName() + "</td>");
		}
		else
		{
			partyMatchingList.append("<td fixwidth=235></td>");
		}
		
		partyMatchingList.append("<td fixwidth=14 height=18><img src=\"" + ClassId.getClassIcon(member.getClassIdPM()) + "\" width=12 height=12></td>");
		partyMatchingList.append("<td fixwidth=125>" + member.getTemplate().className + "</td>");
		
		if (member.isSubClassActive())
		{
			partyMatchingList.append("<td fixwidth=75 align=center>No</td>");
		}
		else
		{
			partyMatchingList.append("<td fixwidth=75 align=center>Yes</td>");
		}
		
		partyMatchingList.append("<td fixwidth=5></td>");
		partyMatchingList.append("<td fixwidth=80 align=center><button action=\"bypass -h _bbspartymatching invite " + member.getObjectId() + " \" value=\"Invite\" width=80 height=27 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
		partyMatchingList.append("</tr></table>");
		partyMatchingList.append("<img src=\"L2UI.SquareGray\" width=755 height=1>");
	}
	
	public boolean isInMatchList(L2PcInstance activeChar)
	{
		return members.contains(activeChar);
	}
	
	public void regOnMatchList(L2PcInstance activeChar)
	{
		members.add(activeChar);
		activeChar.sendMessage("You are now on the Party Matching list.");
	}
	
	public boolean removeFromMatchList(L2PcInstance activeChar)
	{
		activeChar.sendMessage("You've left the party matching list.");
		return members.remove(activeChar);
	}
	
	public String loadPartyMatchingMembersList(L2PcInstance activeChar)
	{
		StringBuilder builder = new StringBuilder();
		final MatchFilter filter = filters.get(activeChar.getObjectId());
		if (filter == null)
		{
			members.stream().filter((player) ->
			{
				return player != null;
			}).forEach((player) ->
			{
				addPartyMatchingMember(builder, player);
			});
		}
		else
		{
			members.stream().filter((player) ->
			{
				return (player != null) && filter.match(player);
			}).forEach((player) ->
			{
				addPartyMatchingMember(builder, player);
			});
		}
		return builder.toString();
	}
	
	public void addNewFilter(L2PcInstance activeChar, MatchFilter filter)
	{
		filters.put(activeChar.getObjectId(), filter);
	}
	
	public void removeFilter(L2PcInstance activeChar)
	{
		filters.remove(activeChar.getObjectId());
	}
	
	public void removeFilterAndMatch(L2PcInstance activeChar)
	{
		members.remove(activeChar);
		filters.remove(activeChar.getObjectId());
	}
}