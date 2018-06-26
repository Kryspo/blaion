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

import isle_of_prayer.DarkWaterDragon;
import isle_of_prayer.EvasGiftBoxes;
import isle_of_prayer.PrisonGuards;
import isle_of_prayer.CrystalCaverns.CrystalCaverns;
import isle_of_prayer.DarkCloudMansion.DarkCloudMansion;
import isle_of_prayer.IOPRace.IOPRace;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class IsleOfPrayer
{
	private static final Logger _log = Logger.getLogger(IsleOfPrayer.class.getName());
	
	private static final Class<?>[] ISLE_OF_PRAYER =
	{
		DarkWaterDragon.class,
		EvasGiftBoxes.class,
		PrisonGuards.class,
		CrystalCaverns.class,
		DarkCloudMansion.class,
		IOPRace.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public IsleOfPrayer()
	{
		for (Class<?> file : ISLE_OF_PRAYER)
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
		new IsleOfPrayer();
	}
}
