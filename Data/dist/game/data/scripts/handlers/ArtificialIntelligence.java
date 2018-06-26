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

import ai.Baium;
import ai.Barakiel;
import ai.BeastFarm;
import ai.Chests;
import ai.Core;
import ai.CryptsOfDisgrace;
import ai.DenOfEvil;
import ai.DrChaos;
import ai.FairyTrees;
import ai.FeedableBeasts;
import ai.FieldOfWhispersSilence;
import ai.FleeNpc;
import ai.FlyingNpcs;
import ai.FrozenLabyrinth;
import ai.GargosPailaka;
import ai.GiantScouts;
import ai.Golkonda;
import ai.Gordon;
import ai.GraveRobbers;
import ai.Hallate;
import ai.HolyBrazier;
import ai.HotSpringsBuffs;
import ai.KarulBugbear;
import ai.Kernon;
import ai.MithrilMinesLocation;
import ai.Monastery;
import ai.Orfen;
import ai.PavelArchaic;
import ai.PlainsOfDion;
import ai.PlainsOfLizardman;
import ai.PolymorphingAngel;
import ai.PolymorphingOnAttack;
import ai.QueenAnt;
import ai.QueenShyeed;
import ai.RagnaOrcFrightened;
import ai.RagnaOrcs;
import ai.SearchingMaster;
import ai.SeeThroughSilentMove;
import ai.SelMahumDrill;
import ai.SelMahumSquad;
import ai.SilentValley;
import ai.StakatoNestMonsters;
import ai.SummonMinions;
import ai.VanHalter;
import ai.WarriorFishingBlock;
import ai.Antharas.Antharas;
import ai.Valakas.Valakas;
import ai.Zaken.Zaken;
import ai.fantasy_isle.HandysBlockCheckerEvent;
import ai.fantasy_isle.MC_Show;
import ai.npc.PcBangPoint.PcBangPoint;
import ai.npc.PriestOfBlessing.PriestOfBlessing;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class ArtificialIntelligence
{
	private static final Logger _log = Logger.getLogger(ArtificialIntelligence.class.getName());
	
	private static final Class<?>[] AI =
	{
		// Need to add in signle AI
		FlyingNpcs.class,
		// L2AttackableAIScript.class, // loaded by script.cfg
		Antharas.class,
		Baium.class,
		Barakiel.class,
		BeastFarm.class,
		Core.class,
		CryptsOfDisgrace.class,
		DenOfEvil.class,
		DrChaos.class,
		FairyTrees.class,
		FeedableBeasts.class,
		FieldOfWhispersSilence.class,
		FleeNpc.class,
		FrozenLabyrinth.class,
		GargosPailaka.class,
		GiantScouts.class,
		Golkonda.class,
		Gordon.class,
		GraveRobbers.class,
		Hallate.class,
		HolyBrazier.class,
		HotSpringsBuffs.class,
		Chests.class,
		KarulBugbear.class,
		Kernon.class,
		MithrilMinesLocation.class,
		Monastery.class,
		Orfen.class,
		PavelArchaic.class,
		PlainsOfDion.class,
		PlainsOfLizardman.class, // move to correct folder
		PolymorphingAngel.class,
		PolymorphingOnAttack.class,
		QueenAnt.class,
		QueenShyeed.class,
		RagnaOrcFrightened.class,
		RagnaOrcs.class,
		SearchingMaster.class,
		SeeThroughSilentMove.class,
		SelMahumDrill.class,
		SelMahumSquad.class,
		SilentValley.class,
		StakatoNestMonsters.class,
		SummonMinions.class,
		HandysBlockCheckerEvent.class,
		MC_Show.class,
		PriestOfBlessing.class,
		PcBangPoint.class,
		VanHalter.class,
		Valakas.class,
		Zaken.class,
		WarriorFishingBlock.class,
	};
	boolean ENABLED_LOADING_INFO = false;
	
	public ArtificialIntelligence()
	{
		for (Class<?> file : AI)
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
		new ArtificialIntelligence();
	}
}
