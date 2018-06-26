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
package com.l2jserver.gameserver.datatables;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jserver.gameserver.model.L2DropData;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.templates.item.L2Item;

/**
 * @author Michael
 */
public class ItemDropIndex
{
	private final Map<Integer, ArrayList<DropInfo>> allItemDropIndex = new HashMap<>();
	private final Map<Integer, String> itemNameMap = new HashMap<>();
	private final DecimalFormat amountFormat = new DecimalFormat("#,###");
	private final DecimalFormat chanceFormat = new DecimalFormat("0.00##");
	private final Map<Integer, String> cache = new HashMap<>();
	private int cacheSize = 0;
	private final int PAGE = 20;
	private final int MAX_SIZE = 1024 * 1024 * 20;// 20Mb
	
	private static final ItemDropIndex instance = new ItemDropIndex();
	
	private ItemDropIndex()
	{
	}
	
	public void addDropInfo(int itemId, DropInfo drop)
	{
		ArrayList<DropInfo> list;
		if ((list = getDrop(itemId)) == null)
		{
			list = new ArrayList<>();
			allItemDropIndex.put(itemId, list);
		}
		
		if (!list.contains(drop))
		{
			list.add(drop);
		}
	}
	
	public void buildIndex()
	{
		allItemDropIndex.clear();
		itemNameMap.clear();
		cache.clear();
		
		NpcTable.getInstance().getByFilter(npc -> (npc != null) && (npc.getAllDropData() != null) && !npc.getAllDropData().isEmpty()).forEach(npc ->
		{
			npc.getDropData().forEach(dropGroup ->
			{
				boolean isSweep = dropGroup.isSweep();
				dropGroup.getAllDrops().forEach(dropData ->
				{
					addDropInfo(dropData.getItemId(), new DropInfo(npc.getNpcId(), npc.getType(), Math.min(L2DropData.MAX_CHANCE, dropData.getChance()), isSweep, dropData.getMinDrop(), dropData.getMaxDrop()));
				});
			});
		});
		
		for (L2Item item : ItemTable.getInstance().getAllNotNullTemplates())
		{
			itemNameMap.put(item.getItemId(), item.getName());
		}
		
		allItemDropIndex.values().forEach((list) ->
		{
			list.sort((o1, o2) -> NpcTable.getInstance().getTemplate(o1.getNpcId()).getLevel() - NpcTable.getInstance().getTemplate(o2.getNpcId()).getLevel());
		});
		
	}
	
	public static ItemDropIndex getInstance()
	{
		return instance;
	}
	
	public ArrayList<DropInfo> getDrop(int itemId)
	{
		return allItemDropIndex.get(itemId);
	}
	
	public String searchItem(String key)
	{
		final StringBuilder sb = new StringBuilder();
		String name;
		int id;
		int count = 0;
		for (Entry<Integer, String> entry : itemNameMap.entrySet())
		{
			name = entry.getValue();
			id = entry.getKey();
			if (name.contains(key) && allItemDropIndex.containsKey(id))
			{
				sb.append("<table width=700 background=\"L2UI_CT1.Windows_DF_TooltipBG\">");
				sb.append("<tr><td fixwidth=5></td><td fixwidth=34 background=\"").append(ItemTable.getInstance().getTemplate(id).getIcon()).append("\"></td><td fixwidth=200>").append(name).append("</td><td width=450 align=right><button value=\"Search\" action=\"bypass _bbssearchDrop ").append(id).append(" 1\" width=75 height=30 back=\"\" fore=\"L2UI_CT1.Button_DF\">").append("</td></tr>");
				sb.append("</table><br><br1>");
				if (++count >= PAGE)
				{
					return sb.toString();
				}
			}
		}
		
		return sb.toString();
	}
	
	public String buildDropSearchPage(int itemid, int page)
	{
		String html = getCache(itemid, page);
		if (html != null)
		{
			return html;
		}
		
		ArrayList<DropInfo> drop = getDrop(itemid);
		if (drop == null)
		{
			return "Sorry , there is no dropdata for this Item!!";
		}
		
		DropInfo[] dropArray = drop.toArray(new DropInfo[0]);
		int pages = (dropArray.length % PAGE) == 0 ? dropArray.length / PAGE : (dropArray.length / PAGE) + 1;
		int start = (page - 1) * PAGE;
		int end = ((start + PAGE) - 1);
		end = Math.min(end, dropArray.length - 1);
		StringBuilder sb = new StringBuilder();
		DropInfo dp;
		boolean color = false;
		for (int index = start; index <= end; index++)
		{
			dp = dropArray[index];
			sb.append("<table height=32 cellspacing=0 width=700 bgcolor=" + (color ? "080808" : "525252") + ">");
			sb.append("<tr>");
			sb.append("<td width=15></td>");
			sb.append("<td width=45>lv").append(NpcTable.getInstance().getTemplate(dp.getNpcId()).getLevel()).append("</td>");
			sb.append("<td fixwidth=150>").append("&@").append(dp.getNpcId()).append(";").append("</td>");
			sb.append("<td fixwidth=160 align=center>").append(amountFormat.format(dp.getMin())).append(" - ").append(amountFormat.format(dp.getMax())).append("</td>");
			sb.append("<td fixwidth=50>").append(chanceFormat.format(dp.getChance() / 1000)).append("%").append("</td>");
			sb.append("<td fixwidth=50>").append(dp.isSweep() ? "<font color=7EC0EE>Sweep</font>" : "--").append("</td>");
			sb.append("<td width=100 align=right><button value=\"Show Location\" action=\"bypass _bbssearchLocation ").append(dp.getNpcId()).append("\" width=100 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			color = !color;
		}
		
		if (pages > 1)
		{
			sb.append("<table>");
			
			sb.append("<tr>");
			for (int i = 1; i <= pages; i++)
			{
				sb.append("<td>").append("<a action=\"bypass _bbssearchDrop ").append(itemid).append(" ").append(i).append("\">P" + i + "</a></td>");
				
				if ((i % 18) == 0)
				{
					sb.append("</tr>");
					sb.append("<tr>");
				}
			}
			sb.append("</tr>");
			sb.append("</table>");
		}
		updateCache(itemid, page, sb.toString());
		return sb.toString();
	}
	
	public void updateCache(int itemId, int page, String html)
	{
		int hashCode = (itemId * 10000) + page;
		cacheSize += html.getBytes().length;
		if (cacheSize > MAX_SIZE)
		{
			cacheSize = 0;
			cache.clear();
		}
		cacheSize += html.getBytes().length;
		cache.put(hashCode, html);
	}
	
	public String getCache(int itemId, int page)
	{
		int hashCode = (itemId * 10000) + page;
		return cache.get(hashCode);
	}
	
	public void showNpcLocation(int npcId, L2PcInstance player)
	{
		L2Spawn spawn = SpawnTable.getInstance().getFirstSpawn(npcId);
		if (spawn == null)
		{
			// player.sendPacket(new ExShowScreenMessage("No spawn data!! Maybe it's RaidBoss or only spawn in Instance World", 5000, 1, ExShowScreenMessage.BOTTOM_CENTER));
			player.sendMessage("No spawn data! Maybe it's RaidBoss or only spawn in Instance World");
		}
		else
		{
			int x = spawn.getLocx();
			int y = spawn.getLocy();
			int z = spawn.getLocz();
			player.getRadar().addMarker(x, y, z);
		}
	}
	
	/**
	 * @author Michael. build index of NpcDropList
	 */
	public static class DropInfo
	{
		private final int npcId;
		private final String npcType;
		private final double chance;
		private final boolean isSweep;
		private final long min;
		private final long max;
		
		/**
		 * @param npcId
		 * @param npcType
		 * @param chance
		 * @param isSweep
		 * @param min
		 * @param max
		 */
		public DropInfo(int npcId, String npcType, double chance, boolean isSweep, long min, long max)
		{
			super();
			this.npcId = npcId;
			this.npcType = npcType;
			this.chance = chance;
			this.isSweep = isSweep;
			this.min = min;
			this.max = max;
		}
		
		/**
		 * @return the npcId
		 */
		public int getNpcId()
		{
			return npcId;
		}
		
		/**
		 * @return the npcType
		 */
		public String getNpcType()
		{
			return npcType;
		}
		
		/**
		 * @return the chance
		 */
		public double getChance()
		{
			return chance;
		}
		
		/**
		 * @return the isSweep
		 */
		public boolean isSweep()
		{
			return isSweep;
		}
		
		/**
		 * @return the min
		 */
		public long getMin()
		{
			return min;
		}
		
		/**
		 * @return the max
		 */
		public long getMax()
		{
			return max;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(chance);
			result = (prime * result) + (int) (temp ^ (temp >>> 32));
			result = (prime * result) + (isSweep ? 1231 : 1237);
			result = (prime * result) + (int) (max ^ (max >>> 32));
			result = (prime * result) + (int) (min ^ (min >>> 32));
			result = (prime * result) + npcId;
			result = (prime * result) + ((npcType == null) ? 0 : npcType.hashCode());
			return result;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
			{
				return true;
			}
			if (obj == null)
			{
				return false;
			}
			if (getClass() != obj.getClass())
			{
				return false;
			}
			DropInfo other = (DropInfo) obj;
			if (Double.doubleToLongBits(chance) != Double.doubleToLongBits(other.chance))
			{
				return false;
			}
			if (isSweep != other.isSweep)
			{
				return false;
			}
			if (max != other.max)
			{
				return false;
			}
			if (min != other.min)
			{
				return false;
			}
			if (npcId != other.npcId)
			{
				return false;
			}
			if (npcType == null)
			{
				if (other.npcType != null)
				{
					return false;
				}
			}
			else if (!npcType.equals(other.npcType))
			{
				return false;
			}
			return true;
		}
		
		@Override
		public String toString()
		{
			return "[" + NpcTable.getInstance().getTemplate(getNpcId()).getName() + " " + getChance() + "  " + isSweep() + " " + getMin() + " - " + getMax() + " " + getChance() + "]";
		}
	}
	
}
