/*
 * Copyright (C) 2004-2015 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers;

import java.util.logging.Logger;

import teleports.CrumaTower.CrumaTower;
import teleports.ElrokiTeleporters.ElrokiTeleporters;
import teleports.GatekeeperSpirit.GatekeeperSpirit;
import teleports.HuntingGroundsTeleport.HuntingGroundsTeleport;
import teleports.Klein.Klein;
import teleports.MithrilMines.MithrilMines;
import teleports.NewbieTravelToken.NewbieTravelToken;
import teleports.NoblesseTeleport.NoblesseTeleport;
import teleports.OracleTeleport.OracleTeleport;
import teleports.PaganTeleporters.PaganTeleporters;
import teleports.StakatoNest.StakatoNest;
import teleports.SteelCitadelTeleport.SteelCitadelTeleport;
import teleports.StrongholdsTeleports.StrongholdsTeleports;
import teleports.Survivor.Survivor;
import teleports.TeleportToFantasy.TeleportToFantasy;
import teleports.TeleportToRaceTrack.TeleportToRaceTrack;
import teleports.TeleportToUndergroundColiseum.TeleportToUndergroundColiseum;
import teleports.TeleportWithCharm.TeleportWithCharm;
import teleports.ToIVortex.ToIVortex;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Teleports
{
	private static final Logger _log = Logger.getLogger(Teleports.class.getName());
	
	private static final Class<?>[] TELEPORTS =
	{
		CrumaTower.class,
		ElrokiTeleporters.class,
		GatekeeperSpirit.class,
		HuntingGroundsTeleport.class,
		Klein.class,
		MithrilMines.class,
		NewbieTravelToken.class,
		NoblesseTeleport.class,
		OracleTeleport.class,
		PaganTeleporters.class,
		StakatoNest.class,
		SteelCitadelTeleport.class,
		StrongholdsTeleports.class,
		Survivor.class,
		TeleportToFantasy.class,
		TeleportToRaceTrack.class,
		TeleportToUndergroundColiseum.class,
		TeleportWithCharm.class,
		ToIVortex.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public Teleports()
	{
		for (Class<?> file : TELEPORTS)
		{
			try
			{
				file.newInstance();
				if (ENABLED_LOADING_INFO)
				{
					_log.info("Script Loader: loading " + file.getSimpleName());
				}
			}
			catch (Exception e)
			{
				_log.info("### ERRORS ### Script Loader: Failed loading " + file.getSimpleName());
				_log.info("### DESCRIPTION ###");
				_log.info("" + e);
			}
		}
	}
	
	// for loading from script.cfg
	public static void main(String[] args)
	{
		new Teleports();
	}
}
