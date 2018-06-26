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

import l2jpdt_npcs.AutomaticTeleporterNPC.AutomaticTeleporterNPC;
import l2jpdt_npcs.GmShopNPC.GmShopNPC;
import l2jpdt_npcs.LevelChooseNpc.LevelChooseNpc;
import l2jpdt_npcs.PremiumServiceNPC.PremiumServiceNPC;
import l2jpdt_npcs.SchemeBuffer.NpcBuffer;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class L2jPDT_NPCS
{
	private static final Logger _log = Logger.getLogger(L2jPDT_NPCS.class.getName());
	
	private static final Class<?>[] CUSTOM =
	{
		PremiumServiceNPC.class,
		LevelChooseNpc.class,
		GmShopNPC.class,
		AutomaticTeleporterNPC.class,
		NpcBuffer.class,
	};
	
	boolean ENABLED_LOADING_INFO = false;
	
	public L2jPDT_NPCS()
	{
		for (Class<?> file : CUSTOM)
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
		new L2jPDT_NPCS();
	}
}
