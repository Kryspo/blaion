/*
 * Copyright (C) 2004-2018 L2J Server
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
package com.l2jserver.gameserver.model.entity;

/**
 * @author Pavel (L2jPrivateDevelopersTeam)
 */
public class MonsterRushTemplates
{
	// Fake Players (Defenders)
	// These fake players help players to defeat monsters
	public static int DEFEND_FAKE_1 = 47501;
	public static int DEFEND_FAKE_2 = 47502;
	public static int DEFEND_FAKE_3 = 47503;
	public static int DEFEND_FAKE_4 = 47504;
	public static int DEFEND_FAKE_5 = 47505;
	public static int DEFEND_FAKE_6 = 47506;
	public static int DEFEND_FAKE_7 = 47507;
	public static int DEFEND_FAKE_8 = 47508;
	public static int DEFEND_FAKE_9 = 47509;
	public static int DEFEND_FAKE_10 = 47510;
	// Fake Players (Attackers)
	// These fake players attack to Dion Village
	public static int ATTACKERS_FAKE_1 = 47601;
	public static int ATTACKERS_FAKE_2 = 47602;
	public static int ATTACKERS_FAKE_3 = 47603;
	public static int ATTACKERS_FAKE_4 = 47604;
	public static int ATTACKERS_FAKE_5 = 47605;
	public static int ATTACKERS_FAKE_6 = 47606;
	public static int ATTACKERS_FAKE_7 = 47607;
	public static int ATTACKERS_FAKE_8 = 47608;
	public static int ATTACKERS_FAKE_9 = 47609;
	public static int ATTACKERS_FAKE_10 = 47610;
	
	// Monsters
	public static int LORD = 40030;
	public static int LORD_X = 18864;
	public static int LORD_Y = 145216;
	public static int LORD_Z = -3132;
	//@formatter:off
	public static int[] _miniIntervals =
	{
		80000,160000,240000,360000
	};
	
	public static int[] _CoordsX =
	{
		19097,19626,17885
	};
	public static int[] _CoordsY =
	{
		144139,145667,146286
	};
	public static int[] _CoordsZ =
	{
		-3088,-3096,-3096
	};
	public static int[][] _wave1Mons_fakes =
	{
		{50000,50001,ATTACKERS_FAKE_1},
		{1,1,1}
	};
	public static int[][] _wave2Mons_fakes =
	{
		{50002,50003,50004,ATTACKERS_FAKE_1,ATTACKERS_FAKE_2},
		{1,1,1,1,1}
	};
	public static int[][] _wave3Mons_fakes =
	{
		{50005,50006,49999,ATTACKERS_FAKE_1,ATTACKERS_FAKE_2,ATTACKERS_FAKE_3},
		{1,1,1,1,1,1}
	};
}
