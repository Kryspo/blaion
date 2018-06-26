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
import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class MRManager
{
	protected static final Logger _log = Logger.getLogger(MRManager.class.getName());
	private MRStartTask _task;
	
	protected MRManager()
	{
		if (Config.MR_ENABLED)
		{
			this.scheduleEventStart();
			_log.info("Monster Rush Engine: Started.");
		}
		else
		{
			_log.info("Monster Rush Engine: Engine is disabled.");
		}
	}
	
	public static MRManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void scheduleEventStart()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : Config.MR_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				final String[] splitTimeOfDay = timeOfDay.split(":");
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
			_task = new MRStartTask(nextStartTime.getTimeInMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
		catch (Exception e)
		{
			_log.warning("Monster Rush Event Engine: Error figuring out a start time. Check MonsterRushEventInterval in config file.");
		}
	}
	
	public void startReg()
	{
		MonsterRush.startRegister();
		_task.setStartTime(System.currentTimeMillis() + (60000 * Config.MR_PARTICIPATION_TIME));
		ThreadPoolManager.getInstance().executeTask(_task);
	}
	
	public void startEvent()
	{
		MonsterRush.startEvent();
		_task.setStartTime(System.currentTimeMillis() + (60000 * Config.MR_RUNNING_TIME));
		ThreadPoolManager.getInstance().executeTask(_task);
	}
	
	public void endEvent()
	{
		MonsterRush.endByLordDeath();
		scheduleEventStart();
	}
	
	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}
	
	class MRStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public MRStartTask(long startTime)
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
			
			if (delay <= 0)
			{
				// start
				if (MonsterRush.isInactive())
				{
					startReg();
				}
				else if (MonsterRush.isParticipating())
				{
					startEvent();
				}
				else
				{
					endEvent();
				}
				
				return;
			}
			
			this.announce(delay);
			
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
			else
			{
				nextMsg = delay;
			}
			
			nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (MonsterRush.isParticipating())
				{
					Announcements.getInstance().announceToAll("Monster Rush Event: " + (time / 60 / 60) + " hour(s) until registration is closed!");
				}
				else if (MonsterRush.isStarted())
				{
					MonsterRush.sysMsgToAllParticipants("Monster Rush Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
				}
			}
			else if (time >= 60)
			{
				if (MonsterRush.isParticipating())
				{
					Announcements.getInstance().announceToAll("Monster Rush Event: " + (time / 60) + " minute(s) until registration is closed!");
				}
				else if (MonsterRush.isStarted())
				{
					MonsterRush.sysMsgToAllParticipants("Monster Rush Event: " + (time / 60) + " minute(s) until the event is finished!");
				}
			}
			else
			{
				if (MonsterRush.isParticipating())
				{
					Announcements.getInstance().announceToAll("Monster Rush Event: " + time + " second(s) until registration is closed!");
				}
				else if (MonsterRush.isStarted())
				{
					MonsterRush.sysMsgToAllParticipants("Monster Rush Event: " + time + " second(s) until the event is finished!");
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final MRManager _instance = new MRManager();
	}
}
