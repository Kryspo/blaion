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
package com.l2jserver.gameserver.communitybbs.BB;

import com.l2jserver.gameserver.enums.MatchClassType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Administrator
 */
public class MatchFilter
{
	private int minLevel;
	private int maxLevel;
	private MatchClassType nclass;
	
	/**
	 * @return the nclass
	 */
	public MatchClassType getNclass()
	{
		return nclass;
	}
	
	/**
	 * @param nclass the nclass to set
	 */
	public void setNclass(MatchClassType nclass)
	{
		this.nclass = nclass;
	}
	
	/**
	 * @param minLevel the minLevel to set
	 */
	public void setMinLevel(int minLevel)
	{
		this.minLevel = minLevel;
	}
	
	/**
	 * @param maxLevel the maxLevel to set
	 */
	public void setMaxLevel(int maxLevel)
	{
		this.maxLevel = maxLevel;
	}
	
	/**
	 * @param minLevel
	 * @param maxLevel
	 * @param nclass
	 */
	public MatchFilter(int minLevel, int maxLevel, MatchClassType nclass)
	{
		super();
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.nclass = nclass;
	}
	
	/**
	 * @return the minLevel
	 */
	public int getMinLevel()
	{
		return minLevel;
	}
	
	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * @return the needClass
	 */
	public MatchClassType getNeedClass()
	{
		return nclass;
	}
	
	public boolean match(L2PcInstance player)
	{
		return (player.getLevel() >= getMinLevel()) && (player.getLevel() <= getMaxLevel()) && ((getNeedClass() == MatchClassType.ALL) || (getNeedClass().getClassList().stream().filter((cid) ->
		{
			return (cid == player.getClassId()) || cid.childOf(player.getClassId());
		}).count() > 0));
	}
}
