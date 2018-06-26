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

import gracia.EnergySeeds;
import gracia.Enira;
import gracia.Lindvior;
import gracia.Maguen;
import gracia.StarStones;
import gracia.AbyssGaze.AbyssGaze;
import gracia.DestroyedTumors.DestroyedTumors;
import gracia.EkimusMouth.EkimusMouth;
import gracia.FortuneTelling.FortuneTelling;
import gracia.GeneralDilios.GeneralDilios;
import gracia.Lekon.Lekon;
import gracia.Nemo.Nemo;
import gracia.Nottingale.Nottingale;
import gracia.SecretArea.SecretArea;
import gracia.SeedOfAnnihilation.SeedOfAnnihilation;
import gracia.SeedOfDestruction.Stage1;
import gracia.SeedOfInfinity.CentralSoI;
import gracia.SeedOfInfinity.HallOfErosionAttack;
import gracia.SeedOfInfinity.HallOfErosionDefence;
import gracia.SeedOfInfinity.HallOfSufferingAttack;
import gracia.SeedOfInfinity.HallOfSufferingDefence;
import gracia.SeedOfInfinity.HeartInfinityAttack;
import gracia.SeedOfInfinity.HeartInfinityDefence;
import gracia.Seyo.Seyo;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class GraciaContinent
{
	private static final Logger _log = Logger.getLogger(GraciaContinent.class.getName());
	
	private static final Class<?>[] GRACIA =
	{
		EnergySeeds.class,
		Enira.class,
		Lindvior.class,
		Maguen.class,
		StarStones.class,
		AbyssGaze.class,
		DestroyedTumors.class,
		EkimusMouth.class,
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		SecretArea.class,
		SeedOfAnnihilation.class,
		Stage1.class,
		CentralSoI.class,
		HallOfSufferingAttack.class,
		HallOfSufferingDefence.class,
		HallOfErosionAttack.class,
		HallOfErosionDefence.class,
		HeartInfinityAttack.class,
		HeartInfinityDefence.class,
		Seyo.class,
	};
	
	boolean ENABLED_LOADING_INFO = false;
	
	public GraciaContinent()
	{
		for (Class<?> file : GRACIA)
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
		new GraciaContinent();
	}
}
