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
package com.l2jserver.configurator;

import java.util.Calendar;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author L2jPDT
 */
public class RateEventConfig extends AbstractConfig
{
	public final String file = "./config/Custom/RatesByDay.properties";
	private static boolean loaded = false;
	
	@Override
	protected String getFilePath()
	{
		return file;
	}
	
	@Override
	public void load()
	{
		Config.ENABLED_BOOST_EXP = getParser().getBoolean("EnabledBoostExp", false);
		Config.RATES_XP_OF_DAY = getParser().getDoubleArray("ExpRateOfDay", "1.0,1.0,1.0,1.0,1.0,1.0,1.0", ",");
		Config.RATES_SP_OF_DAY = getParser().getDoubleArray("SpRateOfDay", "1.0,1.0,1.0,1.0,1.0,1.0,1.0", ",");
		Config.RATES_XP_OF_DAY_PREMIUM = getParser().getDoubleArray("ExpRateOfDayPremium", "1.0,1.0,1.0,1.0,1.0,1.0,1.0", ",");
		Config.RATES_SP_OF_DAY_PREMIUM = getParser().getDoubleArray("SpRateOfDayPremium", "1.0,1.0,1.0,1.0,1.0,1.0,1.0", ",");
		setTodyRate();
		if (!loaded && Config.ENABLED_BOOST_EXP)
		{
			calculateDate();
			loaded = true;
		}
	}
	
	protected static void setTodyRate()
	{
		Config.INT_RATE_XP_OF_TODAY = Config.RATES_XP_OF_DAY[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
		Config.INT_RATE_SP_OF_TODAY = Config.RATES_SP_OF_DAY[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
		Config.INT_RATE_XP_OF_TODAY_PREMIUM = Config.RATES_XP_OF_DAY_PREMIUM[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
		Config.INT_RATE_SP_OF_TODAY_PREMIUM = Config.RATES_SP_OF_DAY_PREMIUM[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
	}
	
	protected static class CalculateDate implements Runnable
	{
		@Override
		public void run()
		{
			setTodyRate();
		}
	}
	
	protected static void calculateDate()
	{
		long now = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 24);
		ThreadPoolManager.getInstance().scheduleAiAtFixedRate(new CalculateDate(), c.getTimeInMillis() - now, 24 * 60 * 60 * 1000L);
	}
}
