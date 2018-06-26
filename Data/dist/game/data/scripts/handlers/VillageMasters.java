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

import village_master.Alliance.Alliance;
import village_master.Clan.Clan;
import village_master.DarkElvenChange1.DarkElvenChange1;
import village_master.DarkElvenChange2.DarkElvenChange2;
import village_master.DwarvenOccupationChange.DwarvenOccupationChange;
import village_master.ElvenHumanBuffers2.ElvenHumanBuffers2;
import village_master.ElvenHumanFighters1.ElvenHumanFighters1;
import village_master.ElvenHumanFighters2.ElvenHumanFighters2;
import village_master.ElvenHumanMystics1.ElvenHumanMystics1;
import village_master.ElvenHumanMystics2.ElvenHumanMystics2;
import village_master.FirstClassTransferTalk.FirstClassTransferTalk;
import village_master.KamaelChange1.KamaelChange1;
import village_master.KamaelChange2.KamaelChange2;
import village_master.OrcOccupationChange1.OrcOccupationChange1;
import village_master.OrcOccupationChange2.OrcOccupationChange2;
import village_master.SubclassCertification.SubclassCertification;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class VillageMasters
{
	private static final Logger _log = Logger.getLogger(VillageMasters.class.getName());
	
	private static final Class<?>[] VILLAGE_MASTERS =
	{
		Alliance.class,
		Clan.class,
		DarkElvenChange1.class,
		DarkElvenChange2.class,
		DwarvenOccupationChange.class,
		ElvenHumanBuffers2.class,
		ElvenHumanFighters1.class,
		ElvenHumanFighters2.class,
		ElvenHumanMystics1.class,
		ElvenHumanMystics2.class,
		FirstClassTransferTalk.class,
		KamaelChange1.class,
		KamaelChange2.class,
		OrcOccupationChange1.class,
		OrcOccupationChange2.class,
		SubclassCertification.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public VillageMasters()
	{
		for (Class<?> file : VILLAGE_MASTERS)
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
		new VillageMasters();
	}
}
