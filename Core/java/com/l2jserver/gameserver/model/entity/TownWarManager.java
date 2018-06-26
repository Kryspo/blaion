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
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.TownManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class TownWarManager
{
	protected static final Logger _log = Logger.getLogger(TownWarManager.class.getName());
	private TownWarStartTask _task;
	private static final int ALL_TOWNS_INT = 17;
	protected boolean isInactive = false;
	protected boolean isStarting = false;
	protected boolean isStarted = false;
	
	private TownWarManager()
	{
		if (Config.TW_AUTO_EVENT)
		{
			isInactive = true;
			this.scheduleEventStart();
			_log.info("Town War Engine: Started.");
		}
		else
		{
			_log.info("Town War Engine: Engine is Disabled.");
		}
	}
	
	public static TownWarManager getInstance()
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
			for (String timeOfDay : Config.TW_INTERVAL)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			_task = new TownWarStartTask(nextStartTime.getTimeInMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
		catch (Exception e)
		{
			_log.warning("Town War Engine: Error figuring out a start time. Check TownWarEventInterval in Config file.");
		}
	}
	
	public void starting()
	{
		isInactive = false;
		isStarting = true;
		_task.setStartTime(System.currentTimeMillis() + (60000L * Config.TW_TIME_BEFORE_START));
		ThreadPoolManager.getInstance().executeTask(_task);
	}
	
	public void startEvent()
	{
		isStarting = false;
		isStarted = true;
		if (Config.TW_ALL_TOWNS)
		{
			for (int i = 1; i <= ALL_TOWNS_INT; i++)
			{
				TownManager.getTown(i).setIsTWZone(true);
				TownManager.getTown(i).updateForCharactersInside();
			}
			TownManager.getTown(20).setIsTWZone(true);
			TownManager.getTown(20).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: All towns are war zone.");
		}
		else
		{
			TownManager.getTown(Config.TW_TOWN_ID).setIsTWZone(true);
			TownManager.getTown(Config.TW_TOWN_ID).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: " + Config.TW_TOWN_NAME + " is a war zone.");
		}
		
		_task.setStartTime(System.currentTimeMillis() + (60000L * Config.TW_RUNNING_TIME));
		ThreadPoolManager.getInstance().executeTask(_task);
	}
	
	public void endEvent()
	{
		isStarted = false;
		isInactive = true;
		if (Config.TW_ALL_TOWNS)
		{
			for (int i = 1; i <= ALL_TOWNS_INT; i++)
			{
				TownManager.getTown(i).setIsTWZone(false);
				TownManager.getTown(i).updateForCharactersInside();
			}
			TownManager.getTown(20).setIsTWZone(false);
			TownManager.getTown(20).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: All towns are returned normal.");
		}
		else
		{
			TownManager.getTown(Config.TW_TOWN_ID).setIsTWZone(false);
			TownManager.getTown(Config.TW_TOWN_ID).updateForCharactersInside();
			Announcements.getInstance().announceToAll("Town War: " + Config.TW_TOWN_NAME + " is returned normal.");
		}
		
		this.scheduleEventStart();
	}
	
	class TownWarStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();
		
		public TownWarStartTask(long startTime)
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
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			if (delay > 0)
			{
				this.announce(delay);
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
			else
			{
				if (TownWarManager.this.isInactive)
				{
					TownWarManager.this.starting();
				}
				else if (TownWarManager.this.isStarting)
				{
					TownWarManager.this.startEvent();
				}
				else
				{
					TownWarManager.this.endEvent();
				}
			}
			
			if (delay > 0)
			{
				nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			}
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (TownWarManager.this.isStarting)
				{
					Announcements.getInstance().announceToAll("Town War Event: " + (time / 60 / 60) + " hour(s) until event starts!");
				}
				else if (TownWarManager.this.isStarted)
				{
					for (L2PcInstance onlinePlayer : pls)
					{
						if ((onlinePlayer != null) && onlinePlayer.isOnline())
						{
							onlinePlayer.sendMessage("Town War Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
						}
					}
				}
			}
			else if (time >= 60)
			{
				if (TownWarManager.this.isStarting)
				{
					Announcements.getInstance().announceToAll("Town War Event: " + (time / 60) + " minute(s) until event starts!");
				}
				else if (TownWarManager.this.isStarted)
				{
					for (L2PcInstance onlinePlayer : pls)
					{
						if ((onlinePlayer != null) && onlinePlayer.isOnline())
						{
							onlinePlayer.sendMessage("Town War Event: " + (time / 60) + " minute(s) until the event is finished!");
						}
					}
				}
			}
			else
			{
				if (TownWarManager.this.isStarting)
				{
					Announcements.getInstance().announceToAll("Town War Event: " + time + " second(s) until event starts!");
				}
				else if (TownWarManager.this.isStarted)
				{
					for (L2PcInstance onlinePlayer : pls)
					{
						if ((onlinePlayer != null) && onlinePlayer.isOnline())
						{
							onlinePlayer.sendMessage("Town War Event: " + time + " second(s) until the event is finished!");
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final TownWarManager _instance = new TownWarManager();
	}
}
