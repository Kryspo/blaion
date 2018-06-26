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

import hellbound.Amaskari;
import hellbound.Beleth;
import hellbound.BodyDestroyer;
import hellbound.Chimeras;
import hellbound.DemonPrince;
import hellbound.Engine;
import hellbound.Epidos;
import hellbound.HellboundCore;
import hellbound.Hellenark;
import hellbound.Keltas;
import hellbound.MasterZelos;
import hellbound.NaiaLock;
import hellbound.OutpostCaptain;
import hellbound.RandomSpawn;
import hellbound.Ranku;
import hellbound.Remnants;
import hellbound.Sandstorms;
import hellbound.SinWardens;
import hellbound.Slaves;
import hellbound.AnomicFoundry.AnomicFoundry;
import hellbound.BaseTower.BaseTower;
import hellbound.Bernarde.Bernarde;
import hellbound.Budenka.Budenka;
import hellbound.Buron.Buron;
import hellbound.DemonPrinceFloor.DemonPrinceFloor;
import hellbound.Falk.Falk;
import hellbound.HellboundTown.HellboundTown;
import hellbound.Hude.Hude;
import hellbound.Jude.Jude;
import hellbound.Kanaf.Kanaf;
import hellbound.Kief.Kief;
import hellbound.Natives.Natives;
import hellbound.Quarry.Quarry;
import hellbound.RankuFloor.RankuFloor;
import hellbound.Shadai.Shadai;
import hellbound.Solomon.Solomon;
import hellbound.TowerOfInfinitum.TowerOfInfinitum;
import hellbound.TowerOfNaia.TowerOfNaia;
import hellbound.TullyWorkshop.TullyWorkshop;
import hellbound.Warpgate.Warpgate;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Hellbound
{
	private static final Logger _log = Logger.getLogger(Hellbound.class.getName());
	
	private static final Class<?>[] HELLBOUND =
	{
		Amaskari.class,
		Beleth.class,
		BodyDestroyer.class,
		DemonPrince.class,
		Engine.class,
		Epidos.class,
		HellboundCore.class,
		Hellenark.class,
		Chimeras.class,
		Keltas.class,
		MasterZelos.class,
		NaiaLock.class,
		OutpostCaptain.class,
		RandomSpawn.class,
		Ranku.class,
		Remnants.class,
		Sandstorms.class,
		SinWardens.class,
		Slaves.class,
		AnomicFoundry.class,
		BaseTower.class,
		Bernarde.class,
		Budenka.class,
		Buron.class,
		DemonPrinceFloor.class,
		Falk.class,
		HellboundTown.class,
		Hude.class,
		Jude.class,
		Kanaf.class,
		Kief.class,
		Natives.class,
		Quarry.class,
		RankuFloor.class,
		Shadai.class,
		Solomon.class,
		TowerOfInfinitum.class,
		TowerOfNaia.class,
		TullyWorkshop.class,
		Warpgate.class,
	
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public Hellbound()
	{
		for (Class<?> file : HELLBOUND)
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
		new Hellbound();
	}
}
