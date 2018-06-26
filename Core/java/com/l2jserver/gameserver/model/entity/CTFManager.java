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
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.entity;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class CTFManager
{
	protected static final Logger _log = Logger.getLogger(CTFManager.class.getName());
	private CTFStartTask _task;
	
	protected CTFManager()
	{
		if (Config.CTF_EVENT_ENABLED)
		{
			CTF.loadData();
			
			this.scheduleEventStart();
			_log.info("Capture The Flag Engine: Started.");
		}
		else
		{
			_log.info("Capture The Flag Engine: Engine is disabled.");
		}
	}
	
	public static CTFManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void scheduleEventStart()
	{
		try
		{
			final Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : Config.CTF_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
				
			}
			_task = new CTFStartTask(nextStartTime.getTimeInMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
		catch (Exception e)
		{
			_log.warning("CTFEventEngine[CTFManager.scheduleEventStart()]: Error figuring out a start time. Check CTFEventInterval in config file.");
		}
	}
	
	public void startEvent()
	{
		CTF.autoEvent();
		this.scheduleEventStart();
		
	}
	
	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}
	
	class CTFStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public CTFStartTask(long startTime)
		{
			_startTime = startTime;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		@Override
		public void run()
		{
			final int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			// start
			if (delay <= 0)
			{
				if (CTF.isInactive())
				{
					startEvent();
				}
				return;
			}
			
			int nextMsg = 0;
			if (delay > 3600)
			{
				nextMsg = delay - 3600;
			}
			else if (delay > 1800)
			{
				nextMsg = delay - 1800;
			}
			else if (delay > 900)
			{
				nextMsg = delay - 900;
			}
			else if (delay > 600)
			{
				nextMsg = delay - 600;
			}
			else if (delay > 300)
			{
				nextMsg = delay - 300;
			}
			else if (delay > 60)
			{
				nextMsg = delay - 60;
			}
			else if (delay > 5)
			{
				nextMsg = delay - 5;
			}
			else if (delay > 0)
			{
				nextMsg = delay;
			}
			
			nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			
		}
	}
	
	private static class SingletonHolder
	{
		protected static final CTFManager _instance = new CTFManager();
	}
}
