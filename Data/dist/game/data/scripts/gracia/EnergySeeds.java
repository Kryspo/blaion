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
package gracia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.instancemanager.gracia.SoDManager;
import com.l2jserver.gameserver.instancemanager.gracia.SoIManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.gameserver.util.Util;

import ai.L2AttackableAIScript;
import quests.Q692_HowtoOpposeEvil.Q692_HowtoOpposeEvil;

public class EnergySeeds extends L2AttackableAIScript
{
	private static final int RESPAWN = 480000;
	private static final int RANDOM_RESPAWN_OFFSET = 180000;
	
	private static final Map<Integer, ESSpawn> SPAWNS = new HashMap<>();
	protected static final Map<L2Npc, Integer> _spawnedNpcs = new ConcurrentHashMap<>();
	
	private static List<L2Npc> soiList = new ArrayList<>();
	private static List<L2Npc> soiclosemouthList = new ArrayList<>();
	private static List<L2Npc> soimouthList = new ArrayList<>();
	private static List<L2Npc> soiabyssgaze1List = new ArrayList<>();
	private static List<L2Npc> soiabyssgaze2List = new ArrayList<>();
	
	// ZONES
	private static final int SOD_ZONE = 201035;
	private static final int SOI_ZONE = 201034;
	
	// NPC
	private static final int TEMPORARY_TELEPORTER = 32602;
	private static final int STABILITED_SEED_OF_GENERATION = 32542;
	private static final int CENTRAL_TUMOR = 32547;
	
	private static final int ENERGY_SEED_WATER = 18678;
	private static final int ENERGY_SEED_FIRE = 18679;
	private static final int ENERGY_SEED_WIND = 18680;
	private static final int ENERGY_SEED_EARTH = 18681;
	private static final int ENERGY_SEED_DIVINITY = 18682;
	private static final int ENERGY_SEED_DARKNESS = 18683;
	
	private static final int ABYSS_GAZE_BLUE = 32539;
	private static final int ABYSS_GAZE_NORMAL = 32540;
	private static final int MOUTH_OF_EKIMUS = 32537;
	private static final int MOUTH_OF_EKIMUS_CLOSED = 32538;
	
	// MONSTERS
	private static final int BGURENT = 22746;
	private static final int BRAKIAN = 22747;
	private static final int GROIKAN = 22748;
	private static final int TREYKAN = 22749;
	private static final int TURTLELIAN = 22754;
	private static final int KRAJIAN = 22755;
	private static final int TARDYON = 22756;
	private static final int KANIBI = 22760;
	private static final int KIRIONA = 22761;
	private static final int KAIONA = 22762;
	
	// ITEMS
	private static final int FIRE_ENERGY_COMPRESSION = 14015;
	private static final int WATER_ENERGY_COMPRESSION = 14016;
	private static final int WIND_ENERGY_COMPRESSION = 14017;
	private static final int EARTH_ENERGY_COMPRESSION = 14018;
	private static final int DARKNESS_ENERGY_COMPRESSION = 14019;
	private static final int SACRED_ENERGY_COMPRESSION = 14020;
	// QUEST ITEMS
	private static final int CONCENTRATED_SPIRIT_ENERGY = 15535; // SOA
	private static final int TIAT_CHARM = 13867;// SOD
	private static final int POTION_OF_SOUL = 13798;// SOI
	
	// @formatter:off
	private static final int[] SEED_IDS =
	{
		ENERGY_SEED_WATER,
		ENERGY_SEED_FIRE,
		ENERGY_SEED_WIND,
		ENERGY_SEED_EARTH,
		ENERGY_SEED_DIVINITY,
		ENERGY_SEED_DARKNESS
	};
	
	private static final int[][] ANNIHILATION_SUPRISE_MOB_IDS =
	{
		{ 
			BGURENT,BRAKIAN,GROIKAN,TREYKAN
		},
		{ 
			TURTLELIAN, KRAJIAN, TARDYON
		},
		{ 
			KANIBI, KIRIONA, KAIONA
		}
	};
	
	private static final int[] SEED_OF_DESTRUCTION_DOORS =
	{
		12240003, 12240004, 12240005, 12240006, 12240007, 12240008, 
		12240009, 12240010, 12240011, 12240012, 12240013, 12240014, 
		12240015, 12240016, 12240017, 12240018, 12240019, 12240020, 
		12240021, 12240022, 12240023, 12240024, 12240025, 12240026, 
		12240027, 12240028, 12240029, 12240030, 12240031
	};
	
	private static final int[] SOD_EXIT_POINT =
	{
		-248717,250260,4337
	};
	
	private static final int[] SOI_EXIT_POINT =
	{
		-183285,205996,-12896
	};
	
	private static final int[][] SOI_MIDDLE_SEEDS =
	{
		{ENERGY_SEED_WATER,-187638,205392,-9529,0,480,1},
		{ENERGY_SEED_DARKNESS,-188101,205699,-9537,0,480,1},
		{ENERGY_SEED_FIRE,-187772,206175,-9526,0,480,1},
		{ENERGY_SEED_DIVINITY,-187476,205709,-9559,0,480,1},
		{ENERGY_SEED_FIRE,-186050,207995,-9531,0,480,1},
		{ENERGY_SEED_WIND,-186044,208588,-9533,0,480,1},
		{ENERGY_SEED_EARTH,-186591,208589,-9532,0,480,1},
		{ENERGY_SEED_WATER,-186599,207999,-9536,0,480,1},
		{ENERGY_SEED_WATER,-184759,210865,-9535,0,480,1},
		{ENERGY_SEED_FIRE,-184211,210865,-9534,0,480,1},
		{ENERGY_SEED_EARTH,-184756,211451,-9534,0,480,1},
		{ENERGY_SEED_DIVINITY,-184220,211499,-9532,0,480,1},
		{ENERGY_SEED_FIRE,-182534,213608,-9533,0,480,1},
		{ENERGY_SEED_WIND,-182518,214193,-9533,0,480,1},
		{ENERGY_SEED_DARKNESS,-183063,214198,-9533,0,480,1},
		{ENERGY_SEED_WATER,-183070,213607,-9533,0,480,1},
		{ENERGY_SEED_WATER,-181200,216482,-9538,0,480,1},
		{ENERGY_SEED_FIRE,-180655,216483,-9536,0,480,1},
		{ENERGY_SEED_DIVINITY,-180653,217068,-9532,0,480,1},
		{ENERGY_SEED_EARTH,-181203,217070,-9533,0,480,1},
		{ENERGY_SEED_WATER,-177531,217535,-9533,0,480,1},
		{ENERGY_SEED_FIRE,-176860,217587,-9529,0,480,1},
		{ENERGY_SEED_DARKNESS,-176926,218172,-9537,0,480,1},
		{ENERGY_SEED_EARTH,-177509,218147,-9529,0,480,1},
		{ENERGY_SEED_WIND,-173094,218073,-9556,0,480,1},
		{ENERGY_SEED_FIRE,-173833,218766,-9555,0,480,1},
		{ENERGY_SEED_WATER,-174296,217729,-9554,0,480,1},
		{ENERGY_SEED_EARTH,-173316,217530,-9555,0,480,1},
		{ENERGY_SEED_WATER,-179381,211049,-12786,0,480,1},
		{ENERGY_SEED_FIRE,-179680,211343,-12781,0,480,1},
		{ENERGY_SEED_WIND,-179935,211032,-12793,0,480,1},
		{ENERGY_SEED_DIVINITY,-179663,210831,-12789,0,480,1},
		{ENERGY_SEED_FIRE,-180336,211106,-12015,0,480,1},
		{ENERGY_SEED_EARTH,-180340,212228,-12018,0,480,1},
		{ENERGY_SEED_WIND,-181535,212193,-12036,0,480,1},
		{ENERGY_SEED_DARKNESS,-181507,211062,-12022,0,480,1},
		{ENERGY_SEED_EARTH,-182732,210369,-11931,0,480,1},
		{ENERGY_SEED_WATER,-182597,210577,-11944,0,480,1},
		{ENERGY_SEED_FIRE,-183806,209503,-11926,0,480,1},
		{ENERGY_SEED_DIVINITY,-182987,209357,-11924,0,480,1},
		{ENERGY_SEED_EARTH,-183026,208807,-11924,0,480,1},
		{ENERGY_SEED_WIND,-183814,208634,-11926,0,480,1},
		{ENERGY_SEED_DARKNESS,-183358,207535,-11926,0,480,1},
		{ENERGY_SEED_FIRE,-182666,207913,-11926,0,480,1},
		{ENERGY_SEED_FIRE,-181471,207142,-12026,0,480,1},
		{ENERGY_SEED_DIVINITY,-181565,206024,-12034,0,480,1},
		{ENERGY_SEED_WATER,-180190,206064,-12016,0,480,1},
		{ENERGY_SEED_EARTH,-180409,207224,-12016,0,480,1},
		{ENERGY_SEED_EARTH,-178914,207242,-12017,0,480,1},
		{ENERGY_SEED_FIRE,-179088,206126,-12018,0,480,1},
		{ENERGY_SEED_WIND,-177889,205872,-12040,0,480,1},
		{ENERGY_SEED_DARKNESS,-177954,207216,-12020,0,480,1},
		{ENERGY_SEED_DIVINITY,-176648,207886,-11927,0,480,1},
		{ENERGY_SEED_WATER,-175711,207706,-11948,0,480,1},
		{ENERGY_SEED_FIRE,-175508,208532,-11927,0,480,1},
		{ENERGY_SEED_DARKNESS,-176270,208731,-11924,0,480,1},
		{ENERGY_SEED_WIND,-176335,209415,-11924,0,480,1},
		{ENERGY_SEED_EARTH,-175503,209561,-11925,0,480,1},
		{ENERGY_SEED_DARKNESS,-175739,210510,-11944,0,480,1},
		{ENERGY_SEED_FIRE,-176550,210282,-11933,0,480,1},
		{ENERGY_SEED_FIRE,-177945,211022,-12024,0,480,1},
		{ENERGY_SEED_WIND,-177736,212120,-12031,0,480,1},
		{ENERGY_SEED_WATER,-178846,212402,-12018,0,480,1},
		{ENERGY_SEED_DIVINITY,-178867,211039,-12015,0,480,1},
		{ENERGY_SEED_WATER,-180053,206969,-15506,0,480,1},
		{ENERGY_SEED_FIRE,-180294,206535,-15507,0,480,1},
		{ENERGY_SEED_WIND,-180112,205799,-15502,0,480,1},
		{ENERGY_SEED_EARTH,-179111,205646,-15513,0,480,1},
		{ENERGY_SEED_DIVINITY,-178725,206281,-15510,0,480,1},
		{ENERGY_SEED_DARKNESS,-178964,206894,-15509,0,480,1},
		{ENERGY_SEED_DARKNESS,-177503,207795,-15505,0,480,1},
		{ENERGY_SEED_WATER,-176984,207217,-15502,0,480,1},
		{ENERGY_SEED_FIRE,-176416,207201,-15501,0,480,1},
		{ENERGY_SEED_WIND,-175937,207745,-15496,0,480,1},
		{ENERGY_SEED_EARTH,-176109,208489,-15505,0,480,1},
		{ENERGY_SEED_DIVINITY,-176851,208739,-15513,0,480,1},
		{ENERGY_SEED_DIVINITY,-177001,210471,-15505,0,480,1},
		{ENERGY_SEED_DARKNESS,-176442,210489,-15506,0,480,1},
		{ENERGY_SEED_WATER,-175932,211228,-15508,0,480,1},
		{ENERGY_SEED_FIRE,-176404,211988,-15508,0,480,1},
		{ENERGY_SEED_WIND,-177045,212000,-15506,0,480,1},
		{ENERGY_SEED_EARTH,-177512,211467,-15509,0,480,1},
		{ENERGY_SEED_EARTH,-178948,212336,-15504,0,480,1},
		{ENERGY_SEED_DIVINITY,-178783,213093,-15501,0,480,1},
		{ENERGY_SEED_DARKNESS,-179283,213641,-15497,0,480,1},
		{ENERGY_SEED_WATER,-180081,213455,-15507,0,480,1},
		{ENERGY_SEED_FIRE,-180391,212881,-15514,0,480,1},
		{ENERGY_SEED_WIND,-180143,212307,-15509,0,480,1},
		{ENERGY_SEED_WIND,-181572,211397,-15503,0,480,1},
		{ENERGY_SEED_EARTH,-182013,212002,-15504,0,480,1},
		{ENERGY_SEED_DIVINITY,-182870,211897,-15502,0,480,1},
		{ENERGY_SEED_DARKNESS,-183238,211211,-15511,0,480,1},
		{ENERGY_SEED_WATER,-182902,210620,-15506,0,480,1},
		{ENERGY_SEED_FIRE,-182269,210427,-15509,0,480,1},
		{ENERGY_SEED_FIRE,-182019,208741,-15502,0,480,1},
		{ENERGY_SEED_WIND,-182640,208738,-15503,0,480,1},
		{ENERGY_SEED_EARTH,-183145,208180,-15499,0,480,1},
		{ENERGY_SEED_DIVINITY,-183012,207450,-15495,0,480,1},
		{ENERGY_SEED_DARKNESS,-182279,207179,-15504,0,480,1},
		{ENERGY_SEED_WATER,-181645,207654,-15508,0,480,1},
		{ENERGY_SEED_DIVINITY,-179790,208689,-15502,0,480,1},
		{ENERGY_SEED_FIRE,-179285,208714,-15502,0,480,1},
		{ENERGY_SEED_WIND,-178606,208915,-15502,0,480,1},
		{ENERGY_SEED_DARKNESS,-178360,209580,-15502,0,480,1},
		{ENERGY_SEED_WATER,-178524,210196,-15502,0,480,1},
		{ENERGY_SEED_FIRE,-178967,210642,-15502,0,480,1},
		{ENERGY_SEED_DIVINITY,-179564,210792,-15502,0,480,1},
		{ENERGY_SEED_WIND,-180130,210598,-15502,0,480,1},
		{ENERGY_SEED_WATER,-180568,210139,-15502,0,480,1},
		{ENERGY_SEED_DARKNESS,-180692,209540,-15502,0,480,1},
		{ENERGY_SEED_EARTH,-180518,208941,-15502,0,480,1},
		{ENERGY_SEED_DIVINITY,-179801,208651,-15502,0,480,1},
		{ENERGY_SEED_WATER,-179538,209983,-15502,0,480,1},
		{ENERGY_SEED_FIRE,-179535,209146,-15502,0,480,1},
		{ENERGY_SEED_WIND,-179964,209581,-15502,0,480,1},
		{ENERGY_SEED_EARTH,-179099,209573,-15502,0,480,1},
		{STABILITED_SEED_OF_GENERATION,-179779,212540,-15520,49151,0,1},
		{STABILITED_SEED_OF_GENERATION,-177028,211135,-15520,36863,0,1},
		{STABILITED_SEED_OF_GENERATION,-176355,208043,-15520,28672,0,1},
		{STABILITED_SEED_OF_GENERATION,-179284,205990,-15520,16384,0,1},
		{STABILITED_SEED_OF_GENERATION,-182268,208218,-15520,4096,0,1},
		{STABILITED_SEED_OF_GENERATION,-182069,211140,-15520,61439,0,1},
		{STABILITED_SEED_OF_GENERATION,-179659,211061,-12784,49151,0,1},
		{STABILITED_SEED_OF_GENERATION,-178418,211653,-12029,49151,0,1},
		{STABILITED_SEED_OF_GENERATION,-178417,206558,-12032,16384,0,1},
		{STABILITED_SEED_OF_GENERATION,-180911,206551,-12028,16384,0,1},
		{STABILITED_SEED_OF_GENERATION,-180911,211652,-12028,49151,0,1},
		{STABILITED_SEED_OF_GENERATION,-176036,210002,-11948,36863,0,1},
		{STABILITED_SEED_OF_GENERATION,-176039,208203,-11948,28672,0,1},
		{STABILITED_SEED_OF_GENERATION,-183288,208205,-11939,4096,0,1},
		{STABILITED_SEED_OF_GENERATION,-183290,210004,-11939,61439,0,1},
		{STABILITED_SEED_OF_GENERATION,-186327,208286,-9536,-16096,0,1},
		{STABILITED_SEED_OF_GENERATION,-184429,211155,-9536,-27768,0,1},
		{STABILITED_SEED_OF_GENERATION,-182811,213871,-9504,-16376,0,1},
		{STABILITED_SEED_OF_GENERATION,-180921,216789,-9536,-28008,0,1},
		{STABILITED_SEED_OF_GENERATION,-177264,217760,-9536,0,0,1},
		{STABILITED_SEED_OF_GENERATION,-173727,218169,-9536,-16384,0,1},
		{STABILITED_SEED_OF_GENERATION,-187776,205696,-9536,0,0,1},
		{CENTRAL_TUMOR,-179538,211313,-15488,16384,0,1}
	};
	
	private static final int[][] SOI_WORLD_CLOSEMOUTHS =
	{
		{MOUTH_OF_EKIMUS_CLOSED,-182996,206510,-12896,40959,0,1},
		{MOUTH_OF_EKIMUS_CLOSED,-183002,205944,-12896,24576,0,1},
		{MOUTH_OF_EKIMUS_CLOSED,-183589,206510,-12896,57343,0,1},
		{MOUTH_OF_EKIMUS_CLOSED,-183586,205947,-12896,8192,0,1}
	};
	
	private static final int[][] SOI_WORLD_MOUTHS =
	{
		{MOUTH_OF_EKIMUS,-182996,206510,-12896,40959,0,1},
		{MOUTH_OF_EKIMUS,-183002,205944,-12896,24576,0,1},
		{MOUTH_OF_EKIMUS,-183589,206510,-12896,57343,0,1},
		{MOUTH_OF_EKIMUS,-183586,205947,-12896,8192,0,1}
	};
	
	
	private static final int[][] SOI_WORLD_ABYSSGAZE_BLUE =
	{
		{ABYSS_GAZE_BLUE,-183295,205861,-12896,16384,0,1}
	};
	
	private static final int[][] SOI_WORLD_ABYSSGAZE_NORMAL =
	{
		{ABYSS_GAZE_NORMAL,-183295,205861,-12896,16384,0,1}
	};
	
	private enum GraciaSeeds
	{
		INFINITY, DESTRUCTION, ANNIHILATION_BISTAKON, ANNIHILATION_REPTILIKON, ANNIHILATION_COKRAKON
	}
	//@formatter:on
	
	public EnergySeeds()
	{
		super(-1, EnergySeeds.class.getSimpleName(), "gracia");
		registerMobs(SEED_IDS);
		FirstTalkNpcs(SEED_IDS);
		FirstTalkNpcs(TEMPORARY_TELEPORTER);
		EnterZoneId(SOD_ZONE);
		EnterZoneId(SOI_ZONE);
		addSpawnsToList();
		startAI();
	}
	
	protected boolean isSeedActive(GraciaSeeds seed)
	{
		switch (seed)
		{
			case INFINITY:
				return false;
			case DESTRUCTION:
				return SoDManager.getInstance().getSoDState() == 2;
			case ANNIHILATION_BISTAKON:
			case ANNIHILATION_REPTILIKON:
			case ANNIHILATION_COKRAKON:
				return true;
		}
		return true;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		if (!Util.contains(targets, npc) || (skill.getId() != 5780))
		{
			return super.onSkillSee(npc, caster, skill, targets, isSummon);
		}
		
		npc.deleteMe();
		
		if (_spawnedNpcs.containsKey(npc) && SPAWNS.containsKey(_spawnedNpcs.get(npc)))
		{
			ESSpawn spawn = SPAWNS.get(_spawnedNpcs.get(npc));
			spawn.scheduleRespawn(RESPAWN + getRandom(RANDOM_RESPAWN_OFFSET));
			_spawnedNpcs.remove(npc);
			if (isSeedActive(spawn._seedId))
			{
				int itemId = 0;
				
				switch (npc.getNpcId())
				{
					case ENERGY_SEED_FIRE: // Fire
						itemId = FIRE_ENERGY_COMPRESSION;
						break;
					case ENERGY_SEED_WATER: // Water
						itemId = WATER_ENERGY_COMPRESSION;
						break;
					case ENERGY_SEED_WIND: // Wind
						itemId = WIND_ENERGY_COMPRESSION;
						break;
					case ENERGY_SEED_EARTH: // Earth
						itemId = EARTH_ENERGY_COMPRESSION;
						break;
					case ENERGY_SEED_DARKNESS: // Darkness
						itemId = DARKNESS_ENERGY_COMPRESSION;
						break;
					case ENERGY_SEED_DIVINITY: // Divinity
						itemId = SACRED_ENERGY_COMPRESSION;
						break;
					default:
						return super.onSkillSee(npc, caster, skill, targets, isSummon);
				}
				if (getRandom(100) < 33)
				{
					caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED);
					caster.addItem("EnergySeed", itemId, getRandom(Config.HOW_TO_OPPOSE_EVIL_RATE + 1, 2 * Config.HOW_TO_OPPOSE_EVIL_RATE), null, true);
				}
				else
				{
					caster.sendPacket(SystemMessageId.THE_COLLECTION_HAS_SUCCEEDED);
					caster.addItem("EnergySeed", itemId, getRandom(1, Config.HOW_TO_OPPOSE_EVIL_RATE), null, true);
				}
				seedCollectEvent(caster, npc, spawn._seedId);
			}
		}
		
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "SoiAbyssGaze1Stop":
				if (!soiabyssgaze1List.isEmpty())
				{
					for (L2Npc a : soiabyssgaze1List)
					{
						if (a != null)
						{
							a.deleteMe();
						}
					}
				}
				soiabyssgaze1List.clear();
				break;
			case "SoiAbyssGaze2Stop":
				if (!soiabyssgaze2List.isEmpty())
				{
					for (L2Npc b : soiabyssgaze2List)
					{
						if (b != null)
						{
							b.deleteMe();
						}
					}
				}
				soiabyssgaze2List.clear();
				break;
			case "SoiAbyssGaze1Spawn":
				for (int[] spawn : SOI_WORLD_ABYSSGAZE_NORMAL)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc c = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
						soiabyssgaze1List.add(c);
					}
				}
				break;
			case "SoiAbyssGaze2Spawn":
				for (int[] spawn : SOI_WORLD_ABYSSGAZE_BLUE)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc d = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
						soiabyssgaze2List.add(d);
					}
				}
				break;
			case "SoiCloseMouthStop":
				if (!soiclosemouthList.isEmpty())
				{
					for (L2Npc e : soiclosemouthList)
					{
						if (e != null)
						{
							e.deleteMe();
						}
					}
				}
				soiclosemouthList.clear();
				break;
			case "SoiMouthStop":
				if (!soimouthList.isEmpty())
				{
					for (L2Npc f : soimouthList)
					{
						if (f != null)
						{
							f.deleteMe();
						}
					}
				}
				soimouthList.clear();
				break;
			case "SoiMouthSpawn":
				for (int[] spawn : SOI_WORLD_MOUTHS)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc g = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
						soimouthList.add(g);
					}
				}
				break;
			case "SoiCloseMouthSpawn":
				for (int[] spawn : SOI_WORLD_CLOSEMOUTHS)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc h = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
						soiclosemouthList.add(h);
					}
				}
				break;
			case "SoiSeedStop":
				if (!soiList.isEmpty())
				{
					for (L2Npc ch : soiList)
					{
						if (ch != null)
						{
							ch.deleteMe();
						}
					}
				}
				soiList.clear();
				break;
			case "SoiSeedSpawn":
				for (int[] spawn : SOI_MIDDLE_SEEDS)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc k = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
						k.getSpawn().setRespawnDelay(spawn[5]);
						k.getSpawn().setAmount(1);
						if (spawn[5] > 0)
						{
							k.getSpawn().startRespawn();
						}
						else
						{
							k.getSpawn().stopRespawn();
						}
						soiList.add(k);
					}
				}
				break;
			case "StartSoDAi":
				for (int dootIdStart : SEED_OF_DESTRUCTION_DOORS)
				{
					L2DoorInstance doorInstance = DoorTable.getInstance().getDoor(dootIdStart);
					if (doorInstance != null)
					{
						doorInstance.openMe();
					}
				}
				startAI(GraciaSeeds.DESTRUCTION);
				break;
			case "StopSoDAi":
				for (int doorIdStop : SEED_OF_DESTRUCTION_DOORS)
				{
					L2DoorInstance doorInstance = DoorTable.getInstance().getDoor(doorIdStop);
					if (doorInstance != null)
					{
						doorInstance.closeMe();
					}
				}
				for (L2PcInstance players : ZoneManager.getInstance().getZoneById(SOD_ZONE).getPlayersInside())
				{
					if (players != null)
					{
						players.teleToLocation(SOD_EXIT_POINT[0], SOD_EXIT_POINT[1], SOD_EXIT_POINT[2]);
					}
				}
				stopAI(GraciaSeeds.DESTRUCTION);
				break;
			case "DeSpawnTask":
				if (npc.isInCombat())
				{
					startQuestTimer("DeSpawnTask", 30000, npc, null);
				}
				else
				{
					npc.deleteMe();
				}
				break;
		}
		return event;
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (npc.getNpcId() == TEMPORARY_TELEPORTER)
		{
			player.teleToLocation(SOD_EXIT_POINT[0], SOD_EXIT_POINT[1], SOD_EXIT_POINT[2]);
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (_spawnedNpcs.containsKey(npc) && SPAWNS.containsKey(_spawnedNpcs.get(npc)))
		{
			SPAWNS.get(_spawnedNpcs.get(npc)).scheduleRespawn(RESPAWN + getRandom(RANDOM_RESPAWN_OFFSET));
			_spawnedNpcs.remove(npc);
		}
		return super.onKill(npc, player, isSummon);
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character.getInstanceId() != 0)
		{
			return super.onEnterZone(character, zone);
		}
		if (character instanceof L2PcInstance)
		{
			switch (zone.getId())
			{
				case SOD_ZONE:
					if (!isSeedActive(GraciaSeeds.DESTRUCTION) && !character.isGM())
					{
						character.teleToLocation(SOD_EXIT_POINT[0], SOD_EXIT_POINT[1], SOD_EXIT_POINT[2]);
					}
					break;
				case SOI_ZONE:
					if ((SoIManager.getCurrentStage() != 3) && !SoIManager.isSeedOpen())
					{
						character.teleToLocation(SOI_EXIT_POINT[0], SOI_EXIT_POINT[1], SOI_EXIT_POINT[2]);
					}
					break;
			}
		}
		return super.onEnterZone(character, zone);
	}
	
	public void startAI()
	{
		// spawn all NPCs
		for (ESSpawn spawn : SPAWNS.values())
		{
			if (isSeedActive(spawn._seedId))
			{
				spawn.scheduleRespawn(0);
			}
		}
	}
	
	public void startAI(GraciaSeeds type)
	{
		// spawn all NPCs
		for (ESSpawn spawn : SPAWNS.values())
		{
			if (spawn._seedId == type)
			{
				spawn.scheduleRespawn(0);
			}
		}
	}
	
	public void stopAI(GraciaSeeds type)
	{
		for (L2Npc seed : _spawnedNpcs.keySet())
		{
			if (type == SPAWNS.get(_spawnedNpcs.get(seed))._seedId)
			{
				seed.deleteMe();
			}
		}
	}
	
	public void seedCollectEvent(L2PcInstance player, L2Npc seedEnergy, GraciaSeeds seedType)
	{
		if (player == null)
		{
			return;
		}
		QuestState st = player.getQuestState(Q692_HowtoOpposeEvil.class.getSimpleName());
		switch (seedType)
		{
			case INFINITY:
				if ((st != null) && st.isCond(3))
				{
					handleQuestDrop(player, POTION_OF_SOUL);
				}
				break;
			case DESTRUCTION:
				if ((st != null) && st.isCond(3))
				{
					handleQuestDrop(player, TIAT_CHARM);
				}
				break;
			case ANNIHILATION_BISTAKON:
				if ((st != null) && st.isCond(3))
				{
					handleQuestDrop(player, CONCENTRATED_SPIRIT_ENERGY);
				}
				if (getRandom(100) < 50)
				{
					L2MonsterInstance mob = spawnSupriseMob(seedEnergy, ANNIHILATION_SUPRISE_MOB_IDS[0][getRandom(ANNIHILATION_SUPRISE_MOB_IDS[0].length)]);
					mob.setRunning();
					mob.addDamageHate(player, 0, 999);
					mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
				break;
			case ANNIHILATION_REPTILIKON:
				if ((st != null) && st.isCond(3))
				{
					handleQuestDrop(player, CONCENTRATED_SPIRIT_ENERGY);
				}
				if (getRandom(100) < 50)
				{
					L2MonsterInstance mob = spawnSupriseMob(seedEnergy, ANNIHILATION_SUPRISE_MOB_IDS[1][getRandom(ANNIHILATION_SUPRISE_MOB_IDS[1].length)]);
					mob.setRunning();
					mob.addDamageHate(player, 0, 999);
					mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
				break;
			case ANNIHILATION_COKRAKON:
				if ((st != null) && st.isCond(3))
				{
					handleQuestDrop(player, CONCENTRATED_SPIRIT_ENERGY);
				}
				if (getRandom(100) < 50)
				{
					L2MonsterInstance mob = spawnSupriseMob(seedEnergy, ANNIHILATION_SUPRISE_MOB_IDS[2][getRandom(ANNIHILATION_SUPRISE_MOB_IDS[2].length)]);
					mob.setRunning();
					mob.addDamageHate(player, 0, 999);
					mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				}
				break;
		}
	}
	
	private L2MonsterInstance spawnSupriseMob(L2Npc energy, int npcId)
	{
		L2NpcTemplate supriseMobTemplate = NpcTable.getInstance().getTemplate(npcId);
		L2MonsterInstance monster = new L2MonsterInstance(npcId, supriseMobTemplate);
		monster.setCurrentHpMp(monster.getMaxHp(), monster.getMaxMp());
		monster.setHeading(energy.getHeading());
		monster.setInstanceId(energy.getInstanceId());
		monster.setShowSummonAnimation(true);
		monster.spawnMe(energy.getX(), energy.getY(), energy.getZ());
		startQuestTimer("DeSpawnTask", 30000, monster, null);
		return monster;
	}
	
	private void handleQuestDrop(L2PcInstance player, int itemId)
	{
		double chance = Config.HOW_TO_OPPOSE_EVIL_CHANCE * Config.RATE_QUEST_DROP;
		int numItems = (int) (chance / 100);
		chance = chance % 100;
		if (getRandom(100) < chance)
		{
			numItems++;
		}
		if (numItems > 0)
		{
			giveItems(player, itemId, numItems);
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
	}
	
	private void addSpawnsToList()
	{
		// Seed of Destruction
		// Temporary Teleporters
		//@formatter:off
		SPAWNS.put(1, new ESSpawn(1, GraciaSeeds.DESTRUCTION, new Location(-245790,220320,-12104), new int[]{TEMPORARY_TELEPORTER}));
		SPAWNS.put(2, new ESSpawn(2, GraciaSeeds.DESTRUCTION, new Location(-249770,207300,-11952), new int[]{TEMPORARY_TELEPORTER}));
		//Energy Seeds
		SPAWNS.put(3, new ESSpawn(3, GraciaSeeds.DESTRUCTION, new Location(-248360,219272,-12448), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(4, new ESSpawn(4, GraciaSeeds.DESTRUCTION, new Location(-249448,219256,-12448), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(5, new ESSpawn(5, GraciaSeeds.DESTRUCTION, new Location(-249432,220872,-12448), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(6, new ESSpawn(6, GraciaSeeds.DESTRUCTION, new Location(-248360,220888,-12448), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(7, new ESSpawn(7, GraciaSeeds.DESTRUCTION, new Location(-250088,219256,-12448), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(8, new ESSpawn(8, GraciaSeeds.DESTRUCTION, new Location(-250600,219272,-12448), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(9, new ESSpawn(9, GraciaSeeds.DESTRUCTION, new Location(-250584,220904,-12448), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(10, new ESSpawn(10, GraciaSeeds.DESTRUCTION, new Location(-250072,220888,-12448), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(11, new ESSpawn(11, GraciaSeeds.DESTRUCTION, new Location(-253096,217704,-12296), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(12, new ESSpawn(12, GraciaSeeds.DESTRUCTION, new Location(-253112,217048,-12288), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(13, new ESSpawn(13, GraciaSeeds.DESTRUCTION, new Location(-251448,217032,-12288), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(14, new ESSpawn(14, GraciaSeeds.DESTRUCTION, new Location(-251416,217672,-12296), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(15, new ESSpawn(15, GraciaSeeds.DESTRUCTION, new Location(-251416,217672,-12296), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(16, new ESSpawn(16, GraciaSeeds.DESTRUCTION, new Location(-251416,217016,-12280), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(17, new ESSpawn(17, GraciaSeeds.DESTRUCTION, new Location(-249752,217016,-12280), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(18, new ESSpawn(18, GraciaSeeds.DESTRUCTION, new Location(-249736,217688,-12296), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(19, new ESSpawn(19, GraciaSeeds.DESTRUCTION, new Location(-252472,215208,-12120), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(20, new ESSpawn(20, GraciaSeeds.DESTRUCTION, new Location(-252552,216760,-12248), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(21, new ESSpawn(21, GraciaSeeds.DESTRUCTION, new Location(-253160,216744,-12248), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(22, new ESSpawn(22, GraciaSeeds.DESTRUCTION, new Location(-253128,215160,-12096), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(23, new ESSpawn(23, GraciaSeeds.DESTRUCTION, new Location(-250392,215208,-12120), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(24, new ESSpawn(24, GraciaSeeds.DESTRUCTION, new Location(-250264,216744,-12248), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(25, new ESSpawn(25, GraciaSeeds.DESTRUCTION, new Location(-249720,216744,-12248), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(26, new ESSpawn(26, GraciaSeeds.DESTRUCTION, new Location(-249752,215128,-12096), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(27, new ESSpawn(27, GraciaSeeds.DESTRUCTION, new Location(-250280,216760,-12248), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(28, new ESSpawn(28, GraciaSeeds.DESTRUCTION, new Location(-250344,216152,-12248), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(29, new ESSpawn(29, GraciaSeeds.DESTRUCTION, new Location(-252504,216152,-12248), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(30, new ESSpawn(30, GraciaSeeds.DESTRUCTION, new Location(-252520,216792,-12248), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(31, new ESSpawn(31, GraciaSeeds.DESTRUCTION, new Location(-242520,217272,-12384), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(32, new ESSpawn(32, GraciaSeeds.DESTRUCTION, new Location(-241432,217288,-12384), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(33, new ESSpawn(33, GraciaSeeds.DESTRUCTION, new Location(-241432,218936,-12384), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(34, new ESSpawn(34, GraciaSeeds.DESTRUCTION, new Location(-242536,218936,-12384), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(35, new ESSpawn(35, GraciaSeeds.DESTRUCTION, new Location(-240808,217272,-12384), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(36, new ESSpawn(36, GraciaSeeds.DESTRUCTION, new Location(-240280,217272,-12384), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(37, new ESSpawn(37, GraciaSeeds.DESTRUCTION, new Location(-240280,218952,-12384), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(38, new ESSpawn(38, GraciaSeeds.DESTRUCTION, new Location(-240792,218936,-12384), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(39, new ESSpawn(39, GraciaSeeds.DESTRUCTION, new Location(-239576,217240,-12640), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(40, new ESSpawn(40, GraciaSeeds.DESTRUCTION, new Location(-239560,216168,-12640), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(41, new ESSpawn(41, GraciaSeeds.DESTRUCTION, new Location(-237896,216152,-12640), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(42, new ESSpawn(42, GraciaSeeds.DESTRUCTION, new Location(-237912,217256,-12640), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(43, new ESSpawn(43, GraciaSeeds.DESTRUCTION, new Location(-237896,215528,-12640), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(44, new ESSpawn(44, GraciaSeeds.DESTRUCTION, new Location(-239560,215528,-12640), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(45, new ESSpawn(45, GraciaSeeds.DESTRUCTION, new Location(-239560,214984,-12640), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(46, new ESSpawn(46, GraciaSeeds.DESTRUCTION, new Location(-237896,215000,-12640), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(47, new ESSpawn(47, GraciaSeeds.DESTRUCTION, new Location(-237896,213640,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(48, new ESSpawn(48, GraciaSeeds.DESTRUCTION, new Location(-239560,213640,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(49, new ESSpawn(49, GraciaSeeds.DESTRUCTION, new Location(-239544,212552,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(50, new ESSpawn(50, GraciaSeeds.DESTRUCTION, new Location(-237912,212552,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(51, new ESSpawn(51, GraciaSeeds.DESTRUCTION, new Location(-237912,211912,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(52, new ESSpawn(52, GraciaSeeds.DESTRUCTION, new Location(-237912,211400,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(53, new ESSpawn(53, GraciaSeeds.DESTRUCTION, new Location(-239560,211400,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(54, new ESSpawn(54, GraciaSeeds.DESTRUCTION, new Location(-239560,211912,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(55, new ESSpawn(55, GraciaSeeds.DESTRUCTION, new Location(-241960,214536,-12512), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(56, new ESSpawn(56, GraciaSeeds.DESTRUCTION, new Location(-241976,213448,-12512), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(57, new ESSpawn(57, GraciaSeeds.DESTRUCTION, new Location(-243624,213448,-12512), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(58, new ESSpawn(58, GraciaSeeds.DESTRUCTION, new Location(-243624,214520,-12512), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(59, new ESSpawn(59, GraciaSeeds.DESTRUCTION, new Location(-241976,212808,-12504), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(60, new ESSpawn(60, GraciaSeeds.DESTRUCTION, new Location(-241960,212280,-12504), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(61, new ESSpawn(61, GraciaSeeds.DESTRUCTION, new Location(-243624,212264,-12504), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(62, new ESSpawn(62, GraciaSeeds.DESTRUCTION, new Location(-243624,212792,-12504), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(63, new ESSpawn(63, GraciaSeeds.DESTRUCTION, new Location(-243640,210920,-12640), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(64, new ESSpawn(64, GraciaSeeds.DESTRUCTION, new Location(-243624,209832,-12640), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(65, new ESSpawn(65, GraciaSeeds.DESTRUCTION, new Location(-241976,209832,-12640), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(66, new ESSpawn(66, GraciaSeeds.DESTRUCTION, new Location(-241976,210920,-12640), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(67, new ESSpawn(67, GraciaSeeds.DESTRUCTION, new Location(-241976,209192,-12640), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(68, new ESSpawn(68, GraciaSeeds.DESTRUCTION, new Location(-241976,208664,-12640), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(69, new ESSpawn(69, GraciaSeeds.DESTRUCTION, new Location(-243624,208664,-12640), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(70, new ESSpawn(70, GraciaSeeds.DESTRUCTION, new Location(-243624,209192,-12640), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(71, new ESSpawn(71, GraciaSeeds.DESTRUCTION, new Location(-241256,208664,-12896), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(72, new ESSpawn(72, GraciaSeeds.DESTRUCTION, new Location(-240168,208648,-12896), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(73, new ESSpawn(73, GraciaSeeds.DESTRUCTION, new Location(-240168,207000,-12896), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(74, new ESSpawn(74, GraciaSeeds.DESTRUCTION, new Location(-241256,207000,-12896), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(75, new ESSpawn(75, GraciaSeeds.DESTRUCTION, new Location(-239528,208648,-12896), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(76, new ESSpawn(76, GraciaSeeds.DESTRUCTION, new Location(-238984,208664,-12896), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(77, new ESSpawn(77, GraciaSeeds.DESTRUCTION, new Location(-239000,207000,-12896), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(78, new ESSpawn(78, GraciaSeeds.DESTRUCTION, new Location(-239512,207000,-12896), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE}));
		SPAWNS.put(79, new ESSpawn(79, GraciaSeeds.DESTRUCTION, new Location(-245064,213144,-12384), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(80, new ESSpawn(80, GraciaSeeds.DESTRUCTION, new Location(-245064,212072,-12384), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(81, new ESSpawn(81, GraciaSeeds.DESTRUCTION, new Location(-246696,212072,-12384), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(82, new ESSpawn(82, GraciaSeeds.DESTRUCTION, new Location(-246696,213160,-12384), new int[]{ENERGY_SEED_WIND,ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(83, new ESSpawn(83, GraciaSeeds.DESTRUCTION, new Location(-245064,211416,-12384), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(84, new ESSpawn(84, GraciaSeeds.DESTRUCTION, new Location(-245048,210904,-12384), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(85, new ESSpawn(85, GraciaSeeds.DESTRUCTION, new Location(-246712,210888,-12384), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(86, new ESSpawn(86, GraciaSeeds.DESTRUCTION, new Location(-246712,211416,-12384), new int[]{ENERGY_SEED_DARKNESS,ENERGY_SEED_WATER}));
		SPAWNS.put(87, new ESSpawn(87, GraciaSeeds.DESTRUCTION, new Location(-245048,209544,-12512), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(88, new ESSpawn(88, GraciaSeeds.DESTRUCTION, new Location(-245064,208456,-12512), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(89, new ESSpawn(89, GraciaSeeds.DESTRUCTION, new Location(-246696,208456,-12512), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(90, new ESSpawn(90, GraciaSeeds.DESTRUCTION, new Location(-246712,209544,-12512), new int[]{ENERGY_SEED_FIRE,ENERGY_SEED_WIND,ENERGY_SEED_EARTH}));
		SPAWNS.put(91, new ESSpawn(91, GraciaSeeds.DESTRUCTION, new Location(-245048,207816,-12512), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(92, new ESSpawn(92, GraciaSeeds.DESTRUCTION, new Location(-245048,207288,-12512), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(93, new ESSpawn(93, GraciaSeeds.DESTRUCTION, new Location(-246696,207304,-12512), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(94, new ESSpawn(94, GraciaSeeds.DESTRUCTION, new Location(-246712,207816,-12512), new int[]{ENERGY_SEED_DIVINITY,ENERGY_SEED_DARKNESS}));
		SPAWNS.put(95, new ESSpawn(95, GraciaSeeds.DESTRUCTION, new Location(-244328,207272,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(96, new ESSpawn(96, GraciaSeeds.DESTRUCTION, new Location(-243256,207256,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(97, new ESSpawn(97, GraciaSeeds.DESTRUCTION, new Location(-243256,205624,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(98, new ESSpawn(98, GraciaSeeds.DESTRUCTION, new Location(-244328,205608,-12768), new int[]{ENERGY_SEED_WATER,ENERGY_SEED_FIRE,ENERGY_SEED_WIND}));
		SPAWNS.put(99, new ESSpawn(99, GraciaSeeds.DESTRUCTION, new Location(-242616,207272,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(100, new ESSpawn(100, GraciaSeeds.DESTRUCTION, new Location(-242104,207272,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(101, new ESSpawn(101, GraciaSeeds.DESTRUCTION, new Location(-242088,205624,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		SPAWNS.put(102, new ESSpawn(102, GraciaSeeds.DESTRUCTION, new Location(-242600,205608,-12768), new int[]{ENERGY_SEED_EARTH,ENERGY_SEED_DIVINITY}));
		// Seed of Annihilation
		SPAWNS.put(103, new ESSpawn(103, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184519,183007,-10456), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(104, new ESSpawn(104, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184873,181445,-10488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(105, new ESSpawn(105, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184009,180962,-10488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(106, new ESSpawn(106, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-185321,181641,-10448), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(107, new ESSpawn(107, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184035,182775,-10512), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(108, new ESSpawn(108, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-185433,181935,-10424), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(109, new ESSpawn(109, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183309,183007,-10560), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(110, new ESSpawn(110, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184929,181886,-10488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(111, new ESSpawn(111, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184009,180392,-10424), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(112, new ESSpawn(112, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183793,183239,-10488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(113, new ESSpawn(113, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184245,180848,-10464), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(114, new ESSpawn(114, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-182704,183761,-10528), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(115, new ESSpawn(115, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184705,181886,-10504), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(116, new ESSpawn(116, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184304,181076,-10488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(117, new ESSpawn(117, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183596,180430,-10424), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(118, new ESSpawn(118, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184422,181038,-10480), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(119, new ESSpawn(119, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184929,181543,-10496), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(120, new ESSpawn(120, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184398,182891,-10472), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(121, new ESSpawn(121, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-177606,182848,-10584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(122, new ESSpawn(122, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178104,183224,-10560), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(123, new ESSpawn(123, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-177274,182284,-10600), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(124, new ESSpawn(124, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-177772,183224,-10560), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(125, new ESSpawn(125, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-181532,180364,-10504), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(126, new ESSpawn(126, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-181802,180276,-10496), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(127, new ESSpawn(127, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178429,180444,-10512), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(128, new ESSpawn(128, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-177606,182190,-10600), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(129, new ESSpawn(129, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-177357,181908,-10576), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(130, new ESSpawn(130, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178747,179534,-10408), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(131, new ESSpawn(131, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178429,179534,-10392), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(132, new ESSpawn(132, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178853,180094,-10472), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(133, new ESSpawn(133, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-181937,179660,-10416), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(134, new ESSpawn(134, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-180992,179572,-10416), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(135, new ESSpawn(135, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-185552,179252,-10368), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(136, new ESSpawn(136, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184572,178913,-10400), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(137, new ESSpawn(137, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184768,178348,-10312), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(138, new ESSpawn(138, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-184572,178574,-10352), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(139, new ESSpawn(139, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-185062,178913,-10384), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(140, new ESSpawn(140, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-181397,179484,-10416), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(141, new ESSpawn(141, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-181667,179044,-10408), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(142, new ESSpawn(142, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-185258,177896,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(143, new ESSpawn(143, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183506,176570,-10280), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(144, new ESSpawn(144, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183719,176804,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(145, new ESSpawn(145, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183648,177116,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(146, new ESSpawn(146, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183932,176492,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(147, new ESSpawn(147, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183861,176570,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(148, new ESSpawn(148, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-183790,175946,-10240), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(149, new ESSpawn(149, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178641,179604,-10416), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(150, new ESSpawn(150, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-178959,179814,-10432), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(151, new ESSpawn(151, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-176367,178456,-10376), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(152, new ESSpawn(152, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-175845,177172,-10264), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(153, new ESSpawn(153, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-175323,177600,-10248), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(154, new ESSpawn(154, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-174975,177172,-10216), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(155, new ESSpawn(155, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-176019,178242,-10352), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(156, new ESSpawn(156, GraciaSeeds.ANNIHILATION_BISTAKON, new Location(-174801,178456,-10264), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(157, new ESSpawn(157, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185648,183384,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(158, new ESSpawn(158, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-186740,180908,-15528), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(159, new ESSpawn(159, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185297,184658,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(160, new ESSpawn(160, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185697,181601,-15488), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(161, new ESSpawn(161, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-186684,182744,-15536), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(162, new ESSpawn(162, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184908,183384,-15616), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(163, new ESSpawn(163, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184994,185572,-15784), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(164, new ESSpawn(164, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185796,182616,-15608), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(165, new ESSpawn(165, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184970,184385,-15648), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(166, new ESSpawn(166, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185995,180809,-15512), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(167, new ESSpawn(167, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185352,182872,-15632), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(168, new ESSpawn(168, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185624,184294,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(169, new ESSpawn(169, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184486,185774,-15816), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(170, new ESSpawn(170, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-186496,184112,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(171, new ESSpawn(171, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184232,185976,-15816), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(172, new ESSpawn(172, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184994,185673,-15792), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(173, new ESSpawn(173, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185733,184203,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(174, new ESSpawn(174, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185079,184294,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(175, new ESSpawn(175, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184803,180710,-15528), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(176, new ESSpawn(176, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-186293,180413,-15528), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(177, new ESSpawn(177, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185352,182936,-15632), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(178, new ESSpawn(178, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184356,180611,-15496), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(179, new ESSpawn(179, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185375,186784,-15816), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(180, new ESSpawn(180, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184867,186784,-15816), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(181, new ESSpawn(181, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180553,180454,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(182, new ESSpawn(182, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180422,180454,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(183, new ESSpawn(183, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-181863,181138,-15120), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(184, new ESSpawn(184, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-181732,180454,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(185, new ESSpawn(185, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180684,180397,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(186, new ESSpawn(186, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-182256,180682,-15112), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(187, new ESSpawn(187, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185492,179492,-15392), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(188, new ESSpawn(188, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185894,178538,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(189, new ESSpawn(189, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-186028,178856,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(190, new ESSpawn(190, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185224,179068,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(191, new ESSpawn(191, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185492,178538,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(192, new ESSpawn(192, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185894,178538,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(193, new ESSpawn(193, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180619,178855,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(194, new ESSpawn(194, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180255,177892,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(195, new ESSpawn(195, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-185804,176472,-15336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(196, new ESSpawn(196, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184580,176370,-15320), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(197, new ESSpawn(197, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184308,176166,-15320), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(198, new ESSpawn(198, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-183764,177186,-15304), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(199, new ESSpawn(199, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180801,177571,-15144), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(200, new ESSpawn(200, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184716,176064,-15320), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(201, new ESSpawn(201, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-184444,175452,-15296), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(202, new ESSpawn(202, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180164,177464,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(203, new ESSpawn(203, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-180164,178213,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(204, new ESSpawn(204, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-179982,178320,-15152), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(205, new ESSpawn(205, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-176925,177757,-15824), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(206, new ESSpawn(206, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-176164,179282,-15720), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(207, new ESSpawn(207, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175692,177613,-15800), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(208, new ESSpawn(208, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175418,178117,-15824), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(209, new ESSpawn(209, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-176103,177829,-15824), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(210, new ESSpawn(210, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175966,177325,-15792), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(211, new ESSpawn(211, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174778,179732,-15664), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(212, new ESSpawn(212, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175692,178261,-15824), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(213, new ESSpawn(213, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-176038,179192,-15736), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(214, new ESSpawn(214, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175660,179462,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(215, new ESSpawn(215, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175912,179732,-15664), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(216, new ESSpawn(216, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175156,180182,-15680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(217, new ESSpawn(217, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174240,182059,-15664), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(218, new ESSpawn(218, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-175590,181478,-15640), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(219, new ESSpawn(219, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174510,181561,-15616), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(220, new ESSpawn(220, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174240,182391,-15688), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(221, new ESSpawn(221, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174105,182806,-15672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(222, new ESSpawn(222, GraciaSeeds.ANNIHILATION_REPTILIKON, new Location(-174645,182806,-15712), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(223, new ESSpawn(223, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-214962,182403,-10992), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(224, new ESSpawn(224, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-215019,182493,-11000), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(225, new ESSpawn(225, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-211374,180793,-11672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(226, new ESSpawn(226, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-211198,180661,-11680), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(227, new ESSpawn(227, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-213097,178936,-12720), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(228, new ESSpawn(228, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-213517,178936,-12712), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(229, new ESSpawn(229, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-214105,179191,-12720), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(230, new ESSpawn(230, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-213769,179446,-12720), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(231, new ESSpawn(231, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-214021,179344,-12720), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(232, new ESSpawn(232, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-210582,180595,-11672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(233, new ESSpawn(233, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-210934,180661,-11696), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(234, new ESSpawn(234, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207058,178460,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(235, new ESSpawn(235, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207454,179151,-11368), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(236, new ESSpawn(236, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207422,181365,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(237, new ESSpawn(237, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207358,180627,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(238, new ESSpawn(238, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207230,180996,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(239, new ESSpawn(239, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-208515,184160,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(240, new ESSpawn(240, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207613,184000,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(241, new ESSpawn(241, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-208597,183760,-11352), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(242, new ESSpawn(242, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206710,176142,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(243, new ESSpawn(243, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206361,178136,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(244, new ESSpawn(244, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206178,178630,-12672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(245, new ESSpawn(245, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-205738,178715,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(246, new ESSpawn(246, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206442,178205,-12648), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(247, new ESSpawn(247, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206585,178874,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(248, new ESSpawn(248, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206073,179366,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(249, new ESSpawn(249, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206009,178628,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(250, new ESSpawn(250, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206155,181301,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(251, new ESSpawn(251, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206595,181641,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(252, new ESSpawn(252, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206507,181641,-12656), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(253, new ESSpawn(253, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206507,181471,-12640), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(254, new ESSpawn(254, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206974,175972,-12672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(255, new ESSpawn(255, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206304,175130,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(256, new ESSpawn(256, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206886,175802,-12672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(257, new ESSpawn(257, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207238,175972,-12672), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(258, new ESSpawn(258, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206386,174857,-11328), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(259, new ESSpawn(259, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-206386,175039,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(260, new ESSpawn(260, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-205976,174584,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(261, new ESSpawn(261, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-207367,184320,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(262, new ESSpawn(262, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219002,180419,-12608), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(263, new ESSpawn(263, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218853,182790,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(264, new ESSpawn(264, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218853,183343,-12600), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(265, new ESSpawn(265, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218358,186247,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(266, new ESSpawn(266, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218358,186083,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(267, new ESSpawn(267, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-217574,185796,-11352), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(268, new ESSpawn(268, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219178,181051,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(269, new ESSpawn(269, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-220171,180313,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(270, new ESSpawn(270, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219293,183738,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(271, new ESSpawn(271, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219381,182553,-12584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(272, new ESSpawn(272, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219600,183024,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(273, new ESSpawn(273, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219940,182680,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(274, new ESSpawn(274, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219260,183884,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(275, new ESSpawn(275, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219855,183540,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(276, new ESSpawn(276, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218946,186575,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(277, new ESSpawn(277, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219882,180103,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(278, new ESSpawn(278, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219266,179787,-12584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(279, new ESSpawn(279, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219201,178337,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(280, new ESSpawn(280, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219716,179875,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(281, new ESSpawn(281, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219716,180021,-11328), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(282, new ESSpawn(282, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219989,179437,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(283, new ESSpawn(283, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219078,178298,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(284, new ESSpawn(284, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218684,178954,-11328), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(285, new ESSpawn(285, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219089,178456,-11328), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(286, new ESSpawn(286, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-220266,177623,-12608), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(287, new ESSpawn(287, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219201,178025,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(288, new ESSpawn(288, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219142,177044,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(289, new ESSpawn(289, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219690,177895,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(290, new ESSpawn(290, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219754,177623,-12584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(291, new ESSpawn(291, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218791,177830,-12584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(292, new ESSpawn(292, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218904,176219,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(293, new ESSpawn(293, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218768,176384,-12584), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(294, new ESSpawn(294, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218774,177626,-11320), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(295, new ESSpawn(295, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218774,177792,-11328), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(296, new ESSpawn(296, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219880,175901,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(297, new ESSpawn(297, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219210,176054,-12592), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(298, new ESSpawn(298, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219850,175991,-12608), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(299, new ESSpawn(299, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-219079,175021,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(300, new ESSpawn(300, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218812,174229,-11344), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		SPAWNS.put(301, new ESSpawn(301, GraciaSeeds.ANNIHILATION_COKRAKON, new Location(-218723,174669,-11336), new int[]{ ENERGY_SEED_WATER, ENERGY_SEED_FIRE, ENERGY_SEED_WIND, ENERGY_SEED_EARTH, ENERGY_SEED_DIVINITY, ENERGY_SEED_DARKNESS }));
		//@formatter:on
	}
	
	private class ESSpawn
	{
		protected final int _spawnId;
		protected final GraciaSeeds _seedId;
		protected final int[] _npcIds;
		protected final Location _loc;
		
		public ESSpawn(int spawnId, GraciaSeeds seedId, Location loc, int[] npcIds)
		{
			_spawnId = spawnId;
			_seedId = seedId;
			_loc = loc;
			_npcIds = npcIds;
		}
		
		public void scheduleRespawn(long waitTime)
		{
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				// if the AI is inactive, do not spawn the NPC
				if (isSeedActive(_seedId))
				{
					// get a random NPC that should spawn at this location
					Integer spawnId = _spawnId; // the map uses "Integer", not "int"
					_spawnedNpcs.put(addSpawn(_npcIds[getRandom(_npcIds.length)], _loc, false, 0), spawnId);
				}
			}, waitTime);
		}
	}
	
	public static L2Npc addSpawn(int npcId, int X, int Y, int Z, int head)
	{
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(head);
				spawn.setXLocation(X);
				spawn.setYLocation(Y);
				spawn.setZLocation(Z);
				spawn.setAmount(spawn.getAmount() + 1);
				return spawn.doSpawn();
			}
		}
		catch (Exception e)
		{
			_log.info("Could not spawn NPC #" + npcId + "; error: " + e.getMessage());
		}
		return null;
	}
}