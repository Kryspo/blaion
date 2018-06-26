/*
 * Copyright (C) 2004-2015 L2J Server
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
package com.l2jserver.gameserver.instancemanager.gracia;

import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.ServerVariables;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

public class SoIManager
{
	protected static final Logger _log = Logger.getLogger(SoIManager.class.getName());
	
	private static final long SOI_OPEN_TIME = Config.HOURS_OPEN_SEEDS * 60 * 1000;
	
	private static final Location[] openSeedTeleportLocs =
	{
		new Location(-179537, 209551, -15504),
		new Location(-179779, 212540, -15520),
		new Location(-177028, 211135, -15520),
		new Location(-176355, 208043, -15520),
		new Location(-179284, 205990, -15520),
		new Location(-182268, 208218, -15520),
		new Location(-182069, 211140, -15520),
		new Location(-176036, 210002, -11948),
		new Location(-176039, 208203, -11949),
		new Location(-183288, 208205, -11939),
		new Location(-183290, 210004, -11939),
		new Location(-187776, 205696, -9536),
		new Location(-186327, 208286, -9536),
		new Location(-184429, 211155, -9536),
		new Location(-182811, 213871, -9504),
		new Location(-180921, 216789, -9536),
		new Location(-177264, 217760, -9536),
		new Location(-173727, 218169, -9536)
	};
	
	protected SoIManager()
	{
		checkStageAndSpawn();
		if (isSeedOpen())
		{
			openSeed(getOpenedTime());
		}
		_log.info("Seed of Infinity: Stage Level: " + getCurrentStage());
		_log.info("Seed of Infinity: Cohemenes Kills: " + getCohemenesCount());
		_log.info("Seed of Infinity: Ekimus Kills: " + getEkimusCount());
		_log.info("Seed of Infinity: HoE Defense Count:" + getHoEDefCount());
	}
	
	public static int getCurrentStage()
	{
		return ServerVariables.getInt("SoI_stage", 1);
	}
	
	public static long getOpenedTime()
	{
		if (getCurrentStage() != 3)
		{
			return 0;
		}
		return (ServerVariables.getLong("SoI_opened", 0) * 1000L) - System.currentTimeMillis();
	}
	
	public static void setCurrentStage(int stage)
	{
		if (getCurrentStage() == stage)
		{
			return;
		}
		if (stage == 3)
		{
			openSeed(SOI_OPEN_TIME);
		}
		else if (isSeedOpen())
		{
			closeSeed();
		}
		ServerVariables.set("SoI_stage", stage);
		setCohemenesCount(0);
		setEkimusCount(0);
		setHoEDefCount(0);
		checkStageAndSpawn();
		_log.info("Seed of Infinity: Set to stage " + stage);
	}
	
	public static boolean isSeedOpen()
	{
		return getOpenedTime() > 0;
	}
	
	public static void openSeed(long time)
	{
		if (time <= 0)
		{
			return;
		}
		ServerVariables.set("SoI_opened", (System.currentTimeMillis() + time) / 1000L);
		_log.info("Seed of Infinity: Opening the seed for " + Util.formatTime((int) time / 1000));
		spawnOpenedSeed();
		DoorTable.getInstance().getDoor(14240102).openMe();
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			closeSeed();
			setCurrentStage(4);
		}, time);
	}
	
	public static void closeSeed()
	{
		_log.info("Seed of Infinity: Closing the seed.");
		ServerVariables.unset("SoI_opened");
		
		final Quest script = QuestManager.getInstance().getQuest("EnergySeeds");
		if (script != null)
		{
			script.notifyEvent("SoiSeedStop", null, null);
		}
		DoorTable.getInstance().getDoor(14240102).closeMe();
	}
	
	public static void checkStageAndSpawn()
	{
		_log.info("Seed of Infinity: 60 Secs to spawn NPCs");
		ThreadPoolManager.getInstance().scheduleGeneral(new EnergySeedsSpawns(), 60000);
	}
	
	public static void notifyCohemenesKill()
	{
		if (getCurrentStage() == 1)
		{
			if (getCohemenesCount() < Config.COHEMENES_KILLS)
			{
				setCohemenesCount(getCohemenesCount() + 1);
			}
			else
			{
				setCurrentStage(2);
			}
		}
	}
	
	public static void notifyEkimusKill()
	{
		if (getCurrentStage() == 2)
		{
			if (getEkimusCount() < Config.SOI_EKIMUS_KILL_COUNT)
			{
				setEkimusCount(getEkimusCount() + 1);
			}
			else
			{
				setCurrentStage(3);
			}
		}
	}
	
	public static void notifyHoEDefSuccess()
	{
		if (getCurrentStage() == 4)
		{
			if (getHoEDefCount() < Config.HALL_OF_EROSION_DEFENSE_TO_5_STAGE)
			{
				setHoEDefCount(getHoEDefCount() + 1);
			}
			else
			{
				setCurrentStage(5);
			}
		}
	}
	
	public static void setCohemenesCount(int i)
	{
		ServerVariables.set("SoI_CohemenesCount", i);
	}
	
	public static void setEkimusCount(int i)
	{
		ServerVariables.set("SoI_EkimusCount", i);
	}
	
	public static void setHoEDefCount(int i)
	{
		ServerVariables.set("SoI_hoedefkillcount", i);
	}
	
	public static int getCohemenesCount()
	{
		return ServerVariables.getInt("SoI_CohemenesCount", 0);
	}
	
	public static int getEkimusCount()
	{
		return ServerVariables.getInt("SoI_EkimusCount", 0);
	}
	
	public static int getHoEDefCount()
	{
		return ServerVariables.getInt("SoI_hoedefkillcount", 0);
	}
	
	private static void spawnOpenedSeed()
	{
		QuestManager.getInstance().getQuest("EnergySeeds").notifyEvent("SoiSeedSpawn", null, null);
	}
	
	public static void teleportInSeed(L2PcInstance player)
	{
		player.teleToLocation(openSeedTeleportLocs[Rnd.get(openSeedTeleportLocs.length)], false);
	}
	
	public static final SoIManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final SoIManager _instance = new SoIManager();
	}
}