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

import freya.FreyasSteward.FreyasSteward;
import freya.IceQueensCastle.IceQueensCastle;
import freya.IceQueensCastleNormalBattle.IceQueensCastleNormalBattle;
import freya.IceQueensCastleUltimateBattle.IceQueensCastleUltimateBattle;
import freya.Jinia.Jinia;
import freya.JiniaGuildHideout1.JiniaGuildHideout1;
import freya.JiniaGuildHideout2.JiniaGuildHideout2;
import freya.JiniaGuildHideout3.JiniaGuildHideout3;
import freya.JiniaGuildHideout4.JiniaGuildHideout4;
import freya.MithrilMine.MithrilMine;
import freya.Rafforty.Rafforty;
import freya.Sirra.Sirra;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Freya
{
	private static final Logger _log = Logger.getLogger(Freya.class.getName());
	
	private static final Class<?>[] FREYA =
	{
		FreyasSteward.class,
		IceQueensCastle.class,
		IceQueensCastleNormalBattle.class,
		IceQueensCastleUltimateBattle.class,
		Jinia.class,
		JiniaGuildHideout1.class,
		JiniaGuildHideout2.class,
		JiniaGuildHideout3.class,
		JiniaGuildHideout4.class,
		MithrilMine.class,
		Rafforty.class,
		Sirra.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public Freya()
	{
		for (Class<?> file : FREYA)
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
		new Freya();
	}
}
