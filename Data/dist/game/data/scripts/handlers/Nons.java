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

import ai.Nons.NoAttacking;
import ai.Nons.NoChampion;
import ai.Nons.NoLethalable;
import ai.Nons.NoMoving;
import ai.Nons.NoRandomAnimation;
import ai.Nons.NoRandomWalk;
import ai.Nons.NoTalking;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Nons
{
	private static final Logger _log = Logger.getLogger(Nons.class.getName());
	
	private static final Class<?>[] NONS =
	{
		NoAttacking.class,
		NoChampion.class,
		NoLethalable.class,
		NoMoving.class,
		NoRandomAnimation.class,
		NoRandomWalk.class,
		NoTalking.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public Nons()
	{
		for (Class<?> file : NONS)
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
		new Nons();
	}
}
