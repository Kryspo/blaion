/*
 * Copyright (C) 2004-2017 L2J Server
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

import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.quest.Quest;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class EnergySeedsSpawns implements Runnable
{
	protected static final Logger _log = Logger.getLogger(SoIManager.class.getName());
	
	@Override
	public void run()
	{
		final Quest script = QuestManager.getInstance().getQuest("EnergySeeds");
		if (script != null)
		{
			script.notifyEvent("SoiCloseMouthStop", null, null);
			script.notifyEvent("SoiMouthStop", null, null);
			
			script.notifyEvent("SoiAbyssGaze2Stop", null, null);
			script.notifyEvent("SoiAbyssGaze1Stop", null, null);
			switch (SoIManager.getCurrentStage())
			{
				case 1:
				case 4:
					script.notifyEvent("SoiMouthSpawn", null, null);
					script.notifyEvent("SoiAbyssGaze2Spawn", null, null);
					break;
				case 5:
					script.notifyEvent("SoiCloseMouthSpawn", null, null);
					script.notifyEvent("SoiAbyssGaze2Spawn", null, null);
					break;
				default:
					script.notifyEvent("SoiCloseMouthSpawn", null, null);
					script.notifyEvent("SoiAbyssGaze1Spawn", null, null);
					break;
			}
		}
	}
	
}
