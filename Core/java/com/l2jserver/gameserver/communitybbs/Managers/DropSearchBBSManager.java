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

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ItemDropIndex;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

// NOT DONE !!!!
public class DropSearchBBSManager extends BaseBBSManager
{
	private static DropSearchBBSManager instance = new DropSearchBBSManager();
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/search.htm");
		
		if (html == null)
		{
			html = "<html><body><br><br><center>404 :File not found: 'data/html/CommunityBoard/home.htm'</center></body></html>";
		}
		
		else if (command.startsWith("_bbssearchItem"))
		{
			if (command.length() > 15)
			{
				String keyword = command.substring(15);
				String list = ItemDropIndex.getInstance().searchItem(keyword);
				html = html.replace("%list%", list);
			}
		}
		else if (command.startsWith("_bbssearchDrop"))
		{
			int id = Integer.parseInt(command.split(" ")[1]);
			int page = Integer.parseInt(command.split(" ")[2]);
			String list = ItemDropIndex.getInstance().buildDropSearchPage(id, page);
			html = html.replace("%list%", list);
			
		}
		else if (command.startsWith("_bbssearchLocation"))
		{
			ItemDropIndex.getInstance().showNpcLocation(Integer.parseInt(command.split(" ")[1]), activeChar);
		}
		separateAndSend(html, activeChar);
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub
	}
	
	public static DropSearchBBSManager getInstance()
	{
		return instance;
	}
}
