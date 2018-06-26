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
package freya.IceQueensCastleNormalBattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2QuestGuardInstance;
import com.l2jserver.gameserver.model.actor.instance.L2RaidBossInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.gameserver.model.variables.NpcVariables;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.ExChangeClientEffectInfo;
import com.l2jserver.gameserver.network.serverpackets.ExChangeNpcState;
import com.l2jserver.gameserver.network.serverpackets.ExSendUIEvent;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.ExStartScenePlayer;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.OnEventTrigger;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.skills.SkillHolder;
import com.l2jserver.gameserver.taskmanager.DecayTaskManager;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;
import quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;

/**
 * Ice Queen's Castle (Normal Battle) instance zone.
 */
public final class IceQueensCastleNormalBattle extends L2AttackableAIScript
{
	protected class IQCNBWorld extends InstanceWorld
	{
		protected List<L2PcInstance> playersInside = new ArrayList<>();
		protected List<L2Npc> knightStatues = new ArrayList<>();
		protected List<L2Attackable> spawnedMobs = new CopyOnWriteArrayList<>();
		protected L2NpcInstance controller = null;
		protected L2GrandBossInstance freya = null;
		protected L2QuestGuardInstance supp_Jinia = null;
		protected L2QuestGuardInstance supp_Kegor = null;
		protected boolean isSupportActive = false;
		protected boolean canSpawnMobs = true;
	}
	
	// Npcs
	private static final int FREYA_THRONE = 29177; // First freya
	private static final int FREYA_SPELLING = 29178; // Second freya
	private static final int FREYA_STAND = 29179; // Last freya
	private static final int INVISIBLE_NPC = 18919;
	private static final int KNIGHT = 18855; // Archery Knight
	private static final int GLACIER = 18853; // Glacier
	private static final int BREATH = 18854; // Archer's Breath
	private static final int GLAKIAS = 25699; // Glakias (Archery Knight Captain)
	private static final int SIRRA = 32762; // Sirra
	private static final int JINIA = 32781; // Jinia
	private static final int SUPP_JINIA = 18850; // Jinia
	private static final int SUPP_KEGOR = 18851; // Kegor
	// Skills
	private static final SkillHolder BLIZZARD = new SkillHolder(6274, 1); // Eternal Blizzard
	private static final SkillHolder BLIZZARD_BREATH = new SkillHolder(6299, 1); // Breath of Ice Palace - Ice Storm
	private static final SkillHolder SUICIDE_BREATH = new SkillHolder(6300, 1); // Self-Destruction
	private static final int JINIA_SUPPORT = 6288; // Jinia's Prayer
	private static final int KEGOR_SUPPORT = 6289; // Kegor's Courage
	private static final SkillHolder ICE_STONE = new SkillHolder(6301, 1); // Cold Mana's Fragment
	private static final SkillHolder CANCEL = new SkillHolder(4618, 1); // NPC Cancel PC Target
	private static final SkillHolder POWER_STRIKE = new SkillHolder(6293, 1); // Power Strike
	private static final SkillHolder POINT_TARGET = new SkillHolder(6295, 1); // Point Target
	private static final SkillHolder CYLINDER_THROW = new SkillHolder(6297, 1); // Cylinder Throw
	private static final SkillHolder SelfRangeBuff = new SkillHolder(6294, 1); // Leader's Roar
	private static final SkillHolder LEADER_RUSH = new SkillHolder(6296, 1); // Rush
	private static final SkillHolder ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	private static final SkillHolder ICE_BALL = new SkillHolder(6278, 1); // Ice Ball
	private static final SkillHolder SUMMON_ELEMENTAL = new SkillHolder(6277, 1); // Summon Spirits
	private static final SkillHolder SELF_NOVA = new SkillHolder(6279, 1); // Attack Nearby Range
	private static final SkillHolder REFLECT_MAGIC = new SkillHolder(6282, 1); // Reflect Magic
	// Locations
	private static final Location FREYA_SPAWN = new Location(114720, -117085, -11088, 15956);
	private static final Location FREYA_SPELLING_SPAWN = new Location(114723, -117502, -10672, 15956);
	private static final Location FREYA_CORPSE = new Location(114767, -114795, -11200, 0);
	private static final Location MIDDLE_POINT = new Location(114730, -114805, -11200);
	private static final Location GLAKIAS_SPAWN = new Location(114707, -114799, -11199, 15956);
	private static final Location SUPP_JINIA_SPAWN = new Location(114751, -114781, -11205);
	private static final Location SUPP_KEGOR_SPAWN = new Location(114659, -114796, -11205);
	private static final Location BATTLE_PORT = new Location(114694, -113700, -11200);
	private static final Location CONTROLLER_LOC = new Location(114394, -112383, -11200);
	private static final Location[] ENTER_LOC =
	{
		new Location(114185, -112435, -11210),
		new Location(114183, -112280, -11210),
		new Location(114024, -112435, -11210),
		new Location(114024, -112278, -11210),
		new Location(113865, -112435, -11210),
		new Location(113865, -112276, -11210),
	
	};
	private static final Location[] STATUES_LOC =
	{
		new Location(113845, -116091, -11168, 8264),
		new Location(113381, -115622, -11168, 8264),
		new Location(113380, -113978, -11168, -8224),
		new Location(113845, -113518, -11168, -8224),
		new Location(115591, -113516, -11168, -24504),
		new Location(116053, -113981, -11168, -24504),
		new Location(116061, -115611, -11168, 24804),
		new Location(115597, -116080, -11168, 24804),
		new Location(112942, -115480, -10960, 52),
		new Location(112940, -115146, -10960, 52),
		new Location(112945, -114453, -10960, 52),
		new Location(112945, -114123, -10960, 52),
		new Location(116497, -114117, -10960, 32724),
		new Location(116499, -114454, -10960, 32724),
		new Location(116501, -115145, -10960, 32724),
		new Location(116502, -115473, -10960, 32724),
	};
	private static Location[] KNIGHTS_LOC =
	{
		new Location(114502, -115315, -11205, 15451),
		new Location(114937, -115323, -11205, 18106),
		new Location(114722, -115185, -11205, 16437),
	};
	// Misc
	private static final int MAX_PLAYERS = Config.MAX_FREYA_PLAYERS;
	private static final int MIN_PLAYERS = Config.MIN_FREYA_PLAYERS;
	private static final int MIN_LEVEL = Config.MIN_LEVEL_PLAYERS;
	private static final int TEMPLATE_ID = 139; // Ice Queen's Castle
	private static final int DOOR_ID = 23140101;
	
	//@formatter:off
	private static int _decoration = 0;
	private static final int[] _eventTriggers =
	{
		23140202, 23140204, 23140206, 23140208, 23140212, 23140214, 23140216 };
	
	private static final int[] _zoneSkill =
	{
		// ID, LVL
		6437, 7
	};
	//@formatter:on
	
	public IceQueensCastleNormalBattle()
	{
		super(11111, IceQueensCastleNormalBattle.class.getSimpleName(), "freya");
		StartNpcs(SIRRA, SUPP_KEGOR, SUPP_JINIA);
		FirstTalkNpcs(SUPP_KEGOR, SUPP_JINIA);
		TalkNpcs(SIRRA, JINIA, SUPP_KEGOR);
		AttackNpcs(FREYA_THRONE, FREYA_STAND, GLAKIAS, GLACIER, BREATH, KNIGHT);
		KillNpcs(GLAKIAS, FREYA_STAND, KNIGHT, GLACIER, BREATH);
		SpawnNpcs(GLAKIAS, FREYA_STAND, KNIGHT, GLACIER, BREATH);
		SpellFinishedNpcs(GLACIER, BREATH);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.equals("enter"))
		{
			enterInstance(player, new IQCNBWorld(), "IceQueensCastleNormalBattle.xml", TEMPLATE_ID);
		}
		else
		{
			final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			
			if ((tmpworld != null) && (tmpworld instanceof IQCNBWorld))
			{
				final IQCNBWorld world = (IQCNBWorld) tmpworld;
				switch (event)
				{
					case "openDoor":
					{
						if (npc.isScriptValue(0))
						{
							npc.setScriptValue(1);
							openDoor(DOOR_ID, world.getInstanceId());
							world.controller = (L2NpcInstance) addSpawn(INVISIBLE_NPC, CONTROLLER_LOC, false, 0, true, world.getInstanceId());
							for (Location loc : STATUES_LOC)
							{
								if (loc.getZ() == -11168)
								{
									final L2Npc statue = addSpawn(INVISIBLE_NPC, loc, false, 0, false, world.getInstanceId());
									world.knightStatues.add(statue);
								}
							}
							
							for (L2PcInstance players : world.playersInside)
							{
								if ((players != null) && (players.getInstanceId() == world.getInstanceId()))
								{
									final QuestState qs = players.getQuestState(Q10286_ReunionWithSirra.class.getSimpleName());
									if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(5))
									{
										qs.setCond(6, true);
									}
								}
							}
							startQuestTimer("STAGE_1_MOVIE", 60000, world.controller, null);
						}
						break;
					}
					case "STOP_ALL_BEFORE_ENTER_SUPPORT":
					{
						startQuestTimer("STOP_JINIA_KEGOR_ANIMATION", 1000, world.controller, null);
						world.isSupportActive = true;
						world.freya.setIsInvul(true);
						world.freya.disableCoreAI(true);
						world.freya.abortAttack();
						for (L2PcInstance players : world.playersInside)
						{
							if (!players.isDead())
							{
								players.setIsInvul(true);
								players.abortAttack();
							}
						}
						manageMovie(world, ExStartScenePlayer.SCENE_BOSS_KEGOR_INTRUSION);
						startQuestTimer("SPAWN_SUPPORT", 27000, world.controller, null);
						startQuestTimer("CANCEL_STOP_JINIA_KEGOR_ANIMATION", 27000, world.controller, null);
						break;
					}
					case "STOP_JINIA_KEGOR_ANIMATION":
					{
						for (int objId : world.getAllowed())
						{
							L2PcInstance pls = L2World.getInstance().getPlayer(objId);
							if ((pls != null) && pls.isOnline() && (pls.getInstanceId() == world.getInstanceId()))
							{
								pls.abortAttack();
								pls.abortCast();
								pls.disableAllSkills();
								pls.setTarget(null);
								pls.stopMove(null);
								pls.setIsImmobilized(true);
								pls.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
							}
						}
						
						for (L2Attackable mobs : world.spawnedMobs)
						{
							if (mobs != null)
							{
								mobs.abortAttack();
								mobs.abortCast();
								mobs.setTarget(null);
								mobs.stopMove(null);
								mobs.setIsInvul(true);
								mobs.setIsImmobilized(true);
								mobs.disableAllSkills();
							}
						}
						break;
					}
					case "CANCEL_STOP_JINIA_KEGOR_ANIMATION":
					{
						for (int objId : world.getAllowed())
						{
							L2PcInstance pls = L2World.getInstance().getPlayer(objId);
							if ((pls != null) && pls.isOnline() && (pls.getInstanceId() == world.getInstanceId()))
							{
								pls.enableAllSkills();
								pls.setIsImmobilized(false);
								pls.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
							}
						}
						
						for (L2Attackable mobs : world.spawnedMobs)
						{
							if (mobs != null)
							{
								mobs.setIsInvul(false);
								mobs.setIsImmobilized(false);
								mobs.enableAllSkills();
							}
						}
						break;
					}
					case "STOP_ALL":
					{
						for (int objId : world.getAllowed())
						{
							L2PcInstance pls = L2World.getInstance().getPlayer(objId);
							if ((pls != null) && pls.isOnline() && (pls.getInstanceId() == world.getInstanceId()))
							{
								pls.abortAttack();
								pls.abortCast();
								pls.setTarget(null);
								pls.stopMove(null);
							}
						}
						
						for (L2Attackable mobs : world.spawnedMobs)
						{
							if (mobs != null)
							{
								mobs.abortAttack();
								mobs.abortCast();
								mobs.setTarget(null);
								mobs.stopMove(null);
							}
						}
						break;
					}
					case "portInside":
					{
						teleportPlayer(player, BATTLE_PORT, world.getInstanceId());
						break;
					}
					case "killFreya":
					{
						for (L2PcInstance players : world.playersInside)
						{
							if ((players != null) && (players.getInstanceId() == world.getInstanceId()))
							{
								final QuestState qs = players.getQuestState(Q10286_ReunionWithSirra.class.getSimpleName());
								if ((qs != null) && (qs.getState() == State.STARTED) && qs.isCond(6))
								{
									qs.setMemoState(10);
									qs.setCond(7, true);
								}
							}
						}
						
						world.supp_Kegor.deleteMe();
						world.freya.decayMe();
						manageMovie(world, ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_B);
						cancelQuestTimer("FINISH_WORLD", world.controller, null);
						startQuestTimer("FINISH_WORLD", 60000, world.controller, null);
						broadCastPacket(world, new ExShowScreenMessage("You'll teleport from the instance in 60 seconds.", 6000));
						break;
					}
					case "18851-01.html":
					{
						return event;
					}
					case "STAGE_1_MOVIE":
					{
						closeDoor(DOOR_ID, world.getInstanceId());
						world.setStatus(1);
						manageMovie(world, ExStartScenePlayer.SCENE_BOSS_FREYA_OPENING);
						startQuestTimer("STAGE_1_START", 53500, world.controller, null);
						break;
					}
					case "STAGE_1_START":
					{
						world.freya = (L2GrandBossInstance) addSpawn(FREYA_THRONE, FREYA_SPAWN, false, 0, true, world.getInstanceId());
						world.freya.setIsMortal(false);
						broadCastPacket(world, new ExShowScreenMessage("Begin stage 1", 6000));
						startQuestTimer("CAST_BLIZZARD", 50000, world.controller, null);
						startQuestTimer("STAGE_1_SPAWN", 2000, world.freya, null);
						startQuestTimer("MANAGE_ZONE_SKILL", 9000, world.controller, null, true);
						break;
					}
					case "STAGE_1_SPAWN":
					{
						notifyEvent("START_SPAWN", world.controller, null);
						break;
					}
					case "STAGE_1_FINISH":
					{
						if (world.freya != null)
						{
							world.freya.deleteMe();
							world.freya = null;
						}
						manageDespawnMinions(world);
						manageMovie(world, ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_A);
						startQuestTimer("STAGE_1_PAUSE", 24100 - 1000, world.controller, null);
						cancelQuestTimer("MANAGE_ZONE_SKILL", world.controller, null);
						break;
					}
					case "STAGE_1_PAUSE":
					{
						world.freya = (L2GrandBossInstance) addSpawn(FREYA_SPELLING, FREYA_SPELLING_SPAWN, false, 0, true, world.getInstanceId());
						world.freya.setIsInvul(true);
						world.freya.disableCoreAI(true);
						for (int objId : world.allowed)
						{
							L2PcInstance plr = L2World.getInstance().getPlayer(objId);
							ExSendUIEvent time_packet = new ExSendUIEvent(plr, false, false, 60, 0, "Time remaining until next battle");
							plr.sendPacket(time_packet);
						}
						world.setStatus(2);
						startQuestTimer("STAGE_2_START", 60000, world.controller, null);
						break;
					}
					case "STAGE_2_START":
					{
						world.canSpawnMobs = true;
						notifyEvent("START_SPAWN", world.controller, null);
						broadCastPacket(world, new ExShowScreenMessage("Begin stage 2", 6000));
						break;
					}
					case "STAGE_2_MOVIE":
					{
						manageMovie(world, ExStartScenePlayer.SCENE_ICE_HEAVYKNIGHT_SPAWN);
						startQuestTimer("STAGE_2_GLAKIAS", 7000, world.controller, null);
						break;
					}
					case "STAGE_2_GLAKIAS":
					{
						for (Location loc : STATUES_LOC)
						{
							if (loc.getZ() == -10960)
							{
								final L2Npc statue = addSpawn(INVISIBLE_NPC, loc, false, 0, false, world.getInstanceId());
								world.knightStatues.add(statue);
								startQuestTimer("SPAWN_KNIGHT", 5000, statue, null);
							}
						}
						final L2RaidBossInstance glakias = (L2RaidBossInstance) addSpawn(GLAKIAS, GLAKIAS_SPAWN, false, 0, true, world.getInstanceId());
						startQuestTimer("LEADER_DELAY", 5000, glakias, null);
						startQuestTimer("MANAGE_ZONE_SKILL", 9000, world.controller, null, true);
						break;
					}
					case "STAGE_3_MOVIE":
					{
						ExChangeNpcState as = new ExChangeNpcState(_decoration, 2);
						Broadcast.toPlayersInInstance(as, world.instanceId);
						for (int emitter : _eventTriggers)
						{
							OnEventTrigger et = new OnEventTrigger(emitter, false);
							Broadcast.toPlayersInInstance(et, world.instanceId);
						}
						manageMovie(world, ExStartScenePlayer.SCENE_BOSS_FREYA_PHASE_B);
						startQuestTimer("STAGE_3_START", 21500, world.controller, null);
						break;
					}
					case "STAGE_3_START":
					{
						world.setStatus(4);
						world.freya.deleteMe();
						world.canSpawnMobs = true;
						world.freya = (L2GrandBossInstance) addSpawn(FREYA_STAND, FREYA_SPAWN, false, 0, true, world.getInstanceId());
						world.controller.getVariables().set("FREYA_MOVE", 0);
						world.playersInside.stream().filter(p -> (p != null) && (p.getInstanceId() == world.getInstanceId())).forEach(p -> p.broadcastPacket(ExChangeClientEffectInfo.STATIC_FREYA_DESTROYED));
						notifyEvent("START_SPAWN", world.controller, null);
						startQuestTimer("START_MOVE", 10000, world.controller, null);
						startQuestTimer("CAST_BLIZZARD", 50000, world.controller, null);
						broadCastPacket(world, new ExShowScreenMessage("Begin stage 3", 6000));
						startQuestTimer("MANAGE_ZONE_SKILL", 9000, world.controller, null, true);
						break;
					}
					case "START_MOVE":
					{
						if (npc.getVariables().getInt("FREYA_MOVE") == 0)
						{
							world.controller.getVariables().set("FREYA_MOVE", 1);
							world.freya.setIsRunning(true);
							world.freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114730, -114805, -11200, 15956));
						}
						break;
					}
					case "CAST_BLIZZARD":
					{
						if (!world.freya.isInvul())
						{
							world.freya.doCast(BLIZZARD.getSkill());
							broadCastPacket(world, new ExShowScreenMessage("Magic power so strong that it could make you lose your mind can be felt from somewhere!!", 6000));
						}
						
						for (L2Attackable minion : world.spawnedMobs)
						{
							if ((minion != null) && !minion.isDead() && !minion.isInCombat())
							{
								manageRandomAttack(world, minion);
							}
						}
						startQuestTimer("CAST_BLIZZARD", getRandom(55, 60) * 1000, world.controller, null);
						break;
					}
					case "SPAWN_SUPPORT":
					{
						startQuestTimer("STOP_ALL", 1000, world.controller, null);
						startQuestTimer("SPAWN_SUPPORT_STOP_ALL", 1000, world.controller, null);
						world.playersInside.add(player);
						break;
					}
					case "SPAWN_SUPPORT_STOP_ALL":
					{
						for (L2PcInstance players : world.playersInside)
						{
							if ((players != null) && (players.getInstanceId() == world.getInstanceId()))
							{
								players.setIsInvul(false);
							}
						}
						world.freya.setIsInvul(false);
						world.freya.disableCoreAI(false);
						broadCastPacket(world, new ExShowScreenMessage("Begin stage 4", 6000));
						world.supp_Jinia = (L2QuestGuardInstance) addSpawn(SUPP_JINIA, SUPP_JINIA_SPAWN, false, 0, true, world.getInstanceId());
						world.supp_Jinia.setIsImmobilized(false);
						world.supp_Jinia.setIsRunning(true);
						world.supp_Jinia.setIsInvul(true);
						world.supp_Jinia.setCanReturnToSpawnPoint(false);
						world.supp_Kegor = (L2QuestGuardInstance) addSpawn(SUPP_KEGOR, SUPP_KEGOR_SPAWN, false, 0, true, world.getInstanceId());
						world.supp_Kegor.setIsImmobilized(false);
						world.supp_Kegor.setIsRunning(true);
						world.supp_Kegor.setIsInvul(true);
						world.supp_Kegor.setCanReturnToSpawnPoint(false);
						startQuestTimer("ATTACK_FREYA", 5000, world.supp_Jinia, null);
						startQuestTimer("ATTACK_FREYA", 5000, world.supp_Kegor, null);
						startQuestTimer("GIVE_SUPPORT", 1000, world.controller, null);
						break;
					}
					case "GIVE_SUPPORT":
					{
						if (world.isSupportActive)
						{
							L2Skill skill1 = SkillTable.getInstance().getInfo(JINIA_SUPPORT, 1);
							L2Skill skill2 = SkillTable.getInstance().getInfo(KEGOR_SUPPORT, 1);
							for (int objId : world.allowed)
							{
								L2PcInstance players = L2World.getInstance().getPlayer(objId);
								if (players != null)
								{
									skill1.getEffects(world.supp_Jinia, players);
									skill2.getEffects(world.supp_Kegor, players);
									startQuestTimer("GIVE_SUPPORT", 25000, world.controller, null);
								}
							}
						}
						break;
					}
					case "FINISH_STAGE":
					{
						if (world.supp_Jinia != null)
						{
							world.supp_Jinia.deleteMe();
							world.supp_Jinia = null;
						}
						
						if (world.freya != null)
						{
							world.freya.teleToLocation(FREYA_CORPSE);
						}
						
						if (world.supp_Kegor != null)
						{
							world.supp_Kegor.deleteMe();
							world.supp_Kegor = null;
						}
						world.supp_Kegor = (L2QuestGuardInstance) addSpawn(SUPP_KEGOR, SUPP_KEGOR_SPAWN, false, 0, true, world.getInstanceId());
						break;
					}
					case "START_SPAWN":
					{
						for (L2Npc statues : world.knightStatues)
						{
							notifyEvent("SPAWN_KNIGHT", statues, null);
						}
						
						for (Location loc : KNIGHTS_LOC)
						{
							final L2Attackable knight = (L2Attackable) addSpawn(KNIGHT, loc, false, 0, false, world.getInstanceId());
							knight.disableCoreAI(true);
							knight.setDisplayEffect(1);
							knight.getSpawn().setLocation(loc);
							manageTargetToAttack(world, knight);
							world.spawnedMobs.add(knight);
							startQuestTimer("ICE_RUPTURE", getRandom(2, 5) * 1000, knight, null);
						}
						
						int maxGlaciers = world.getStatus() + 2;
						for (int i = 0; i < maxGlaciers; i++)
						{
							notifyEvent("SPAWN_GLACIER", world.controller, null);
						}
						break;
					}
					case "SPAWN_KNIGHT":
					{
						if (world.canSpawnMobs)
						{
							final Location loc = new Location(MIDDLE_POINT.getX() + getRandom(-1000, 1000), MIDDLE_POINT.getY() + getRandom(-1000, 1000), MIDDLE_POINT.getZ());
							final L2Attackable knight = (L2Attackable) addSpawn(KNIGHT, npc.getLocationXYZ(), false, 0, false, world.getInstanceId());
							knight.getVariables().set("SPAWNED_NPC", npc);
							knight.disableCoreAI(true);
							knight.setIsImmobilized(true);
							knight.setDisplayEffect(1);
							knight.getSpawn().setLocation(loc);
							manageTargetToAttack(world, knight);
							world.spawnedMobs.add(knight);
							startQuestTimer("ICE_RUPTURE", getRandom(5, 10) * 1000, knight, null);
						}
						break;
					}
					case "SPAWN_GLACIER":
					{
						if (world.canSpawnMobs)
						{
							final Location loc = new Location(MIDDLE_POINT.getX() + getRandom(-1000, 1000), MIDDLE_POINT.getY() + getRandom(-1000, 1000), MIDDLE_POINT.getZ());
							final L2Attackable glacier = (L2Attackable) addSpawn(GLACIER, loc, false, 0, false, world.getInstanceId());
							glacier.setDisplayEffect(1);
							glacier.disableCoreAI(true);
							glacier.setIsImmobilized(true);
							world.spawnedMobs.add(glacier);
							startQuestTimer("CHANGE_STATE", 1400, glacier, null);
						}
						break;
					}
					case "ICE_RUPTURE":
					{
						if (npc.isCoreAIDisabled())
						{
							npc.disableCoreAI(false);
							npc.setIsImmobilized(false);
							npc.setDisplayEffect(2);
							manageRandomAttack(world, (L2Attackable) npc);
						}
						break;
					}
					case "FIND_TARGET":
					{
						manageRandomAttack(world, (L2Attackable) npc);
						break;
					}
					case "CHANGE_STATE":
					{
						npc.setDisplayEffect(2);
						startQuestTimer("CAST_SKILL", 20000, npc, null);
						break;
					}
					case "CAST_SKILL":
					{
						if (npc.isScriptValue(0) && !npc.isDead())
						{
							npc.setTarget(npc);
							npc.doCast(ICE_STONE.getSkill());
							npc.setScriptValue(1);
						}
						break;
					}
					case "SUICIDE":
					{
						npc.setDisplayEffect(3);
						npc.setIsMortal(true);
						npc.doDie(null);
						break;
					}
					case "BLIZZARD":
					{
						npc.getVariables().set("SUICIDE_COUNT", npc.getVariables().getInt("SUICIDE_COUNT") + 1);
						
						if (npc.getVariables().getInt("SUICIDE_ON") == 0)
						{
							if (npc.getVariables().getInt("SUICIDE_COUNT") == 2)
							{
								startQuestTimer("ELEMENTAL_SUICIDE", 20000, npc, null);
							}
							else
							{
								if (npc.checkDoCastConditions(BLIZZARD_BREATH.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(npc);
									npc.doCast(BLIZZARD_BREATH.getSkill());
								}
								startQuestTimer("BLIZZARD", 20000, npc, null);
							}
						}
						break;
					}
					case "ELEMENTAL_SUICIDE":
					{
						npc.setTarget(npc);
						npc.doCast(SUICIDE_BREATH.getSkill());
						break;
					}
					case "ELEMENTAL_KILLED":
					{
						if (npc.getVariables().getInt("SUICIDE_ON") == 1)
						{
							npc.setTarget(npc);
							npc.doCast(SUICIDE_BREATH.getSkill());
						}
						break;
					}
					case "ATTACK_FREYA":
					{
						if ((world.freya != null) && !world.freya.isDead())
						{
							world.supp_Jinia.setTarget(world.freya);
							world.supp_Jinia.addDamageHate(world.freya, 0, 9999);
							world.supp_Jinia.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, world.freya);
							startQuestTimer("ATTACK_FREYA", 10000, world.supp_Jinia, null);
							world.supp_Kegor.setTarget(world.freya);
							world.supp_Kegor.addDamageHate(world.freya, 0, 9999);
							world.supp_Kegor.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, world.freya);
							startQuestTimer("ATTACK_FREYA", 10000, world.supp_Kegor, null);
						}
						// if not moving, Freya in world doesn't exist.
						else
						{
							world.supp_Jinia.setIsImmobilized(true);
							world.supp_Kegor.setIsImmobilized(true);
						}
						break;
					}
					case "FINISH_WORLD":
					{
						if (world.freya != null)
						{
							world.freya.decayMe();
						}
						
						for (L2PcInstance players : world.playersInside)
						{
							if ((players != null))
							{
								players.broadcastPacket(ExChangeClientEffectInfo.STATIC_FREYA_DEFAULT);
							}
						}
						InstanceManager.getInstance().destroyInstance(world.getInstanceId());
						break;
					}
					case "LEADER_RANGEBUFF":
					{
						if (npc.checkDoCastConditions(SelfRangeBuff.getSkill()) && !npc.isCastingNow())
						{
							npc.setTarget(npc);
							npc.doCast(SelfRangeBuff.getSkill());
						}
						else
						{
							startQuestTimer("LEADER_RANGEBUFF", 30000, npc, null);
						}
						break;
					}
					case "LEADER_RANDOMIZE":
					{
						final L2Attackable mob = (L2Attackable) npc;
						mob.clearAggroList();
						
						for (L2Character characters : npc.getKnownList().getKnownPlayersInRadius(1000))
						{
							if ((characters != null))
							{
								mob.addDamageHate(characters, 0, getRandom(10000, 20000));
							}
						}
						startQuestTimer("LEADER_RANDOMIZE", 25000, npc, null);
						break;
					}
					case "LEADER_DASH":
					{
						final L2Character mostHated = ((L2Attackable) npc).getMostHated();
						if (getRandomBoolean() && !npc.isCastingNow() && (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance(mostHated.getLocationXYZ(), true, false) < 1000))
						{
							npc.setTarget(mostHated);
							npc.doCast(LEADER_RUSH.getSkill());
						}
						startQuestTimer("LEADER_DASH", 10000, npc, null);
						break;
					}
					case "LEADER_DESTROY":
					{
						if (npc.getVariables().getInt("OFF_SHOUT") == 0)
						{
							broadCastPacket(world, new ExShowScreenMessage("The space feels like its gradually starting to shake.", 6000));
						}
						break;
					}
					case "LEADER_DELAY":
					{
						if (npc.getVariables().getInt("DELAY_VAL") == 0)
						{
							npc.getVariables().set("DELAY_VAL", 1);
						}
						break;
					}
					case "MANAGE_ZONE_SKILL":
					{
						manageZoneSkill(world);
						break;
					}
				}
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if (tmpworld instanceof IQCNBWorld)
		{
			final IQCNBWorld world = (IQCNBWorld) tmpworld;
			
			if (npc.getNpcId() == SUPP_JINIA)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return null;
			}
			else if (npc.getNpcId() == SUPP_KEGOR)
			{
				if (world.isSupportActive)
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return null;
				}
				return "18851.html";
			}
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if (tmpworld instanceof IQCNBWorld)
		{
			final IQCNBWorld world = (IQCNBWorld) tmpworld;
			switch (npc.getNpcId())
			{
				case FREYA_THRONE:
				{
					if ((world.controller.getVariables().getInt("FREYA_MOVE") == 0) && (world.getStatus() == 1))
					{
						world.controller.getVariables().set("FREYA_MOVE", 1);
						broadCastPacket(world, new ExShowScreenMessage("Freya has started to move!", 6000));
						world.freya.setIsRunning(true);
						world.freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114730, -114805, -11200, 15956));
					}
					
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.02))
					{
						notifyEvent("STAGE_1_FINISH", world.controller, null);
						cancelQuestTimer("CAST_BLIZZARD", world.controller, null);
					}
					else
					{
						if ((attacker.getMountType() == 1) && !attacker.isAffected(ANTI_STRIDER.getSkillId()) && !npc.isCastingNow())
						{
							if (!npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
							{
								npc.setTarget(attacker);
								npc.doCast(ANTI_STRIDER.getSkill());
							}
						}
						
						final L2Character mostHated = ((L2Attackable) npc).getMostHated();
						final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance(mostHated.getLocationXYZ(), true, false) <= 800);
						
						if (getRandom(10000) < 3333)
						{
							if (getRandomBoolean())
							{
								if ((npc.calculateDistance(attacker.getLocationXYZ(), true, false) <= 800) && npc.checkDoCastConditions(ICE_BALL.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(attacker);
									npc.doCast(ICE_BALL.getSkill());
								}
							}
							else
							{
								if (canReachMostHated && npc.checkDoCastConditions(ICE_BALL.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(mostHated);
									npc.doCast(ICE_BALL.getSkill());
								}
							}
						}
						else if (getRandom(10000) < 800)
						{
							if (getRandomBoolean())
							{
								if ((npc.calculateDistance(attacker.getLocationXYZ(), true, false) <= 800) && npc.checkDoCastConditions(SUMMON_ELEMENTAL.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(attacker);
									npc.doCast(SUMMON_ELEMENTAL.getSkill());
								}
							}
							else
							{
								if (canReachMostHated && npc.checkDoCastConditions(SUMMON_ELEMENTAL.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(mostHated);
									npc.doCast(SUMMON_ELEMENTAL.getSkill());
								}
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (!npc.isAffected(SELF_NOVA.getSkillId()) && npc.checkDoCastConditions(SELF_NOVA.getSkill()) && !npc.isCastingNow())
							{
								npc.setTarget(npc);
								npc.doCast(SELF_NOVA.getSkill());
							}
						}
					}
					break;
				}
				case FREYA_STAND:
				{
					if (world.controller.getVariables().getInt("FREYA_MOVE") == 0)
					{
						world.controller.getVariables().set("FREYA_MOVE", 1);
						world.freya.setIsRunning(true);
						world.freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114730, -114805, -11200, 15956));
					}
					
					if ((npc.getCurrentHp() < (npc.getMaxHp() * 0.2)) && !world.isSupportActive)
					{
						startQuestTimer("STOP_ALL_BEFORE_ENTER_SUPPORT", 1000, world.controller, null);
					}
					
					if ((attacker.getMountType() == 1) && !attacker.isAffected(ANTI_STRIDER.getSkillId()) && !npc.isCastingNow())
					{
						if (!npc.isSkillDisabled(ANTI_STRIDER.getSkill()))
						{
							npc.setTarget(attacker);
							npc.doCast(ANTI_STRIDER.getSkill());
						}
					}
					
					final L2Character mostHated = ((L2Attackable) npc).getMostHated();
					final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance(mostHated.getLocationXYZ(), true, false) <= 800);
					
					if (getRandom(10000) < 3333)
					{
						if (getRandomBoolean())
						{
							if ((npc.calculateDistance(attacker.getLocationXYZ(), true, false) <= 800) && npc.checkDoCastConditions(ICE_BALL.getSkill()) && !npc.isCastingNow())
							{
								npc.setTarget(attacker);
								npc.doCast(ICE_BALL.getSkill());
							}
						}
						else
						{
							if (canReachMostHated && npc.checkDoCastConditions(ICE_BALL.getSkill()) && !npc.isCastingNow())
							{
								npc.setTarget(mostHated);
								npc.doCast(ICE_BALL.getSkill());
							}
						}
					}
					else if (getRandom(10000) < 1333)
					{
						if (getRandomBoolean())
						{
							if ((npc.calculateDistance(attacker.getLocationXYZ(), true, false) <= 800) && npc.checkDoCastConditions(SUMMON_ELEMENTAL.getSkill()) && !npc.isCastingNow())
							{
								npc.setTarget(attacker);
								npc.doCast(SUMMON_ELEMENTAL.getSkill());
							}
						}
						else
						{
							if (canReachMostHated && npc.checkDoCastConditions(SUMMON_ELEMENTAL.getSkill()) && !npc.isCastingNow())
							{
								npc.setTarget(mostHated);
								npc.doCast(SUMMON_ELEMENTAL.getSkill());
							}
						}
					}
					else if (getRandom(10000) < 1500)
					{
						if (!npc.isAffected(SELF_NOVA.getSkillId()) && npc.checkDoCastConditions(SELF_NOVA.getSkill()) && !npc.isCastingNow())
						{
							npc.setTarget(npc);
							npc.doCast(SELF_NOVA.getSkill());
						}
					}
					else if (getRandom(10000) < 1333)
					{
						if (!npc.isAffected(REFLECT_MAGIC.getSkillId()) && npc.checkDoCastConditions(REFLECT_MAGIC.getSkill()) && !npc.isCastingNow())
						{
							npc.setTarget(npc);
							npc.doCast(REFLECT_MAGIC.getSkill());
						}
					}
					break;
				}
				case GLACIER:
				{
					if (npc.isScriptValue(0) && (npc.getCurrentHp() < (npc.getMaxHp() * 0.5)))
					{
						npc.setTarget(attacker);
						npc.doCast(ICE_STONE.getSkill());
						npc.setScriptValue(1);
					}
					break;
				}
				case BREATH:
				{
					if ((npc.getCurrentHp() < (npc.getMaxHp() / 20)) && (npc.getVariables().getInt("SUICIDE_ON", 0) == 0))
					{
						npc.getVariables().set("SUICIDE_ON", 1);
						startQuestTimer("ELEMENTAL_KILLED", 1000, npc, null);
					}
					break;
				}
				case KNIGHT:
				{
					if (npc.isCoreAIDisabled())
					{
						manageRandomAttack(world, (L2Attackable) npc);
						npc.disableCoreAI(false);
						npc.setIsImmobilized(false);
						npc.setDisplayEffect(2);
						cancelQuestTimer("ICE_RUPTURE", npc, null);
					}
					break;
				}
				case GLAKIAS:
				{
					if (npc.getCurrentHp() < (npc.getMaxHp() * 0.02))
					{
						if (npc.getVariables().getInt("OFF_SHOUT") == 0)
						{
							npc.getVariables().set("OFF_SHOUT", 1);
							npc.getVariables().set("DELAY_VAL", 2);
							npc.setTarget(attacker);
							npc.doCast(CANCEL.getSkill());
						}
						else if (npc.getVariables().getInt("OFF_SHOUT") == 1)
						{
							npc.setTarget(attacker);
							npc.doCast(CANCEL.getSkill());
						}
					}
					else if ((npc.getVariables().getInt("OFF_SHOUT") == 0) && (npc.getVariables().getInt("DELAY_VAL") == 1))
					{
						final L2Character mostHated = ((L2Attackable) npc).getMostHated();
						final boolean canReachMostHated = (mostHated != null) && !mostHated.isDead() && (npc.calculateDistance(mostHated.getLocationXYZ(), true, false) < 1000);
						
						if (npc.getVariables().getInt("TIMER_ON") == 0)
						{
							npc.getVariables().set("TIMER_ON", 1);
							startQuestTimer("LEADER_RANGEBUFF", getRandom(5, 30) * 1000, npc, null);
							startQuestTimer("LEADER_RANDOMIZE", 25000, npc, null);
							startQuestTimer("LEADER_DASH", 5000, npc, null);
							startQuestTimer("LEADER_DESTROY", 60000, npc, null);
						}
						
						if (getRandom(10000) < 2500)
						{
							if (getRandom(10000) < 2500)
							{
								if (npc.checkDoCastConditions(POWER_STRIKE.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(attacker);
									npc.doCast(POWER_STRIKE.getSkill());
								}
							}
							else if (npc.checkDoCastConditions(POWER_STRIKE.getSkill()) && !npc.isCastingNow() && canReachMostHated)
							{
								npc.setTarget(((L2Attackable) npc).getMostHated());
								npc.doCast(POWER_STRIKE.getSkill());
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (getRandomBoolean())
							{
								if (npc.checkDoCastConditions(POINT_TARGET.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(attacker);
									npc.doCast(POINT_TARGET.getSkill());
								}
							}
							else if (npc.checkDoCastConditions(POINT_TARGET.getSkill()) && !npc.isCastingNow() && canReachMostHated)
							{
								npc.setTarget(((L2Attackable) npc).getMostHated());
								npc.doCast(POINT_TARGET.getSkill());
							}
						}
						else if (getRandom(10000) < 1500)
						{
							if (getRandomBoolean())
							{
								if (npc.checkDoCastConditions(CYLINDER_THROW.getSkill()) && !npc.isCastingNow())
								{
									npc.setTarget(attacker);
									npc.doCast(CYLINDER_THROW.getSkill());
								}
							}
							else if (npc.checkDoCastConditions(CYLINDER_THROW.getSkill()) && !npc.isCastingNow() && canReachMostHated)
							{
								npc.setTarget(((L2Attackable) npc).getMostHated());
								npc.doCast(CYLINDER_THROW.getSkill());
							}
						}
					}
					break;
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if (tmpworld instanceof IQCNBWorld)
		{
			final IQCNBWorld world = (IQCNBWorld) tmpworld;
			
			switch (npc.getNpcId())
			{
				case GLACIER:
				{
					if (skill == ICE_STONE.getSkill())
					{
						if (getRandom(100) < 75)
						{
							manageBreathSpawn(player, npc, world);
						}
						notifyEvent("SUICIDE", npc, null);
					}
					break;
				}
				case BREATH:
				{
					if (skill == SUICIDE_BREATH.getSkill())
					{
						npc.doDie(null);
					}
					break;
				}
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		
		if (tmpworld instanceof IQCNBWorld)
		{
			final IQCNBWorld world = (IQCNBWorld) tmpworld;
			switch (npc.getNpcId())
			{
				case GLAKIAS:
				{
					manageDespawnMinions(world);
					for (int objId : world.allowed)
					{
						L2PcInstance plr = L2World.getInstance().getPlayer(objId);
						ExSendUIEvent time_packet = new ExSendUIEvent(plr, false, false, 60, 0, "Time remaining until next battle");
						plr.sendPacket(time_packet);
					}
					startQuestTimer("STAGE_3_MOVIE", 60000, world.controller, null);
					cancelQuestTimer("MANAGE_ZONE_SKILL", world.controller, null);
					break;
				}
				case FREYA_STAND:
				{
					world.isSupportActive = false;
					manageMovie(world, ExStartScenePlayer.SCENE_BOSS_FREYA_ENDING_A);
					manageDespawnMinions(world);
					// TODO:finishInstance(world, 300000);
					DecayTaskManager.getInstance().cancelDecayTask(world.freya);
					cancelQuestTimer("ATTACK_FREYA", world.supp_Jinia, null);
					cancelQuestTimer("ATTACK_FREYA", world.supp_Kegor, null);
					cancelQuestTimer("GIVE_SUPPORT", world.controller, null);
					cancelQuestTimer("CAST_BLIZZARD", world.controller, null);
					cancelQuestTimer("MANAGE_ZONE_SKILL", world.controller, null);
					startQuestTimer("FINISH_STAGE", 16000, world.controller, null);
					startQuestTimer("FINISH_WORLD", 300000, world.controller, null);
					break;
				}
				case KNIGHT:
				{
					final L2Npc spawnedBy = npc.getVariables().getObject("SPAWNED_NPC", L2Npc.class);
					final NpcVariables var = world.controller.getVariables();
					int knightCount = var.getInt("KNIGHT_COUNT");
					
					if ((var.getInt("FREYA_MOVE") == 0) && (world.getStatus() == 1))
					{
						var.set("FREYA_MOVE", 1);
						broadCastPacket(world, new ExShowScreenMessage("Freya has started to move!", 6000));
						world.freya.setIsRunning(true);
						world.freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114730, -114805, -11200, 15956));
					}
					
					if ((knightCount < 10) && (world.getStatus() == 2))
					{
						knightCount++;
						var.set("KNIGHT_COUNT", knightCount);
						
						if (knightCount == 10)
						{
							notifyEvent("STAGE_2_MOVIE", world.controller, null);
							world.setStatus(3);
						}
					}
					
					if (spawnedBy != null)
					{
						startQuestTimer("SPAWN_KNIGHT", getRandom(50, 60) * 1000, spawnedBy, null);
					}
					world.spawnedMobs.remove(npc);
					break;
				}
				case GLACIER:
				{
					startQuestTimer("SPAWN_GLACIER", getRandom(30, 60) * 1000, world.controller, null);
					npc.setDisplayEffect(3);
					world.spawnedMobs.remove(npc);
					
					manageBreathSpawn(killer, npc, world);
					break;
				}
				case BREATH:
				{
					world.spawnedMobs.remove(npc);
					break;
				}
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	protected int enterInstance(L2PcInstance player, InstanceWorld instance, String template, int templateId)
	{
		int instanceId = 0;
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if (world != null)
		{
			if (!(world instanceof IQCNBWorld))
			{
				player.sendPacket(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER);
				return 0;
			}
			managePlayerEnter(player, (IQCNBWorld) world);
			player.setInstanceId(instanceId);
			return world.instanceId;
		}
		else
		{
			if (!checkConditions(player))
			{
				return 0;
			}
			
			instanceId = InstanceManager.getInstance().createDynamicInstance(template);
			world = new IQCNBWorld();
			world.templateId = TEMPLATE_ID;
			world.instanceId = instanceId;
			world.status = 0;
			player.setInstanceId(instanceId);
			InstanceManager.getInstance().addWorld(world);
			_log.info("Freya Normal " + template + " Instance: " + instanceId + " created by player: " + player.getName());
			
			if ((player.getParty() == null) || (player.getParty().getCommandChannel() == null))
			{
				managePlayerEnter(player, (IQCNBWorld) world);
			}
			else
			{
				for (L2PcInstance partyMember : player.getParty().getCommandChannel().getMembers())
				{
					managePlayerEnter(partyMember, (IQCNBWorld) world);
				}
			}
			return instanceId;
		}
	}
	
	private void managePlayerEnter(L2PcInstance player, IQCNBWorld world)
	{
		world.playersInside.add(player);
		world.allowed.add(player.getObjectId());
		teleportPlayer(player, ENTER_LOC[getRandom(ENTER_LOC.length)], world.getInstanceId(), false);
	}
	
	protected boolean checkConditions(L2PcInstance player)
	{
		final L2Party party = player.getParty();
		final L2CommandChannel channel = party != null ? party.getCommandChannel() : null;
		
		if (player.isGM())
		{
			return true;
		}
		
		if (party == null)
		{
			player.sendPacket(SystemMessageId.NOT_IN_PARTY_CANT_ENTER);
			return false;
		}
		else if (channel == null)
		{
			player.sendPacket(SystemMessageId.NOT_IN_COMMAND_CHANNEL_CANT_ENTER);
			return false;
		}
		else if (player != channel.getChannelLeader())
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		else if ((channel.getMemberCount() < MIN_PLAYERS) || (channel.getMemberCount() > MAX_PLAYERS))
		{
			player.sendPacket(SystemMessageId.PARTY_EXCEEDED_THE_LIMIT_CANT_ENTER);
			return false;
		}
		for (L2PcInstance channelMember : channel.getMembers())
		{
			if (channelMember.getLevel() < MIN_LEVEL)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
				sm.addPcName(channelMember);
				party.broadcastPacket(sm);
				return false;
			}
			else if (!Util.checkIfInRange(1000, player, channelMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED);
				sm.addPcName(channelMember);
				party.broadcastPacket(sm);
				return false;
			}
			else if (System.currentTimeMillis() < InstanceManager.getInstance().getInstanceTime(channelMember.getObjectId(), TEMPLATE_ID))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_RE_ENTER_YET);
				sm.addPcName(channelMember);
				party.broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	private void manageRandomAttack(IQCNBWorld world, L2Attackable mob)
	{
		final List<L2PcInstance> players = new ArrayList<>();
		for (L2PcInstance player : world.playersInside)
		{
			if ((player != null) && !player.isDead() && GeoData.getInstance().canSeeTarget(mob, player) && (player.getInstanceId() == world.getInstanceId()) && !player.isInvisible())
			{
				players.add(player);
			}
		}
		
		Collections.shuffle(players);
		final L2PcInstance target = (!players.isEmpty()) ? players.get(0) : null;
		if (target != null)
		{
			mob.addDamageHate(target, 0, 999);
			mob.setIsRunning(true);
			mob.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
		}
		else
		{
			startQuestTimer("FIND_TARGET", 10000, mob, null);
		}
	}
	
	private void manageDespawnMinions(IQCNBWorld world)
	{
		world.canSpawnMobs = false;
		for (L2Attackable mobs : world.spawnedMobs)
		{
			if ((mobs != null) && !mobs.isDead())
			{
				mobs.doDie(null);
			}
		}
	}
	
	private void manageMovie(IQCNBWorld world, int movie)
	{
		stopAll(world);
		for (L2PcInstance players : world.playersInside)
		{
			if ((players != null) && (players.getInstanceId() == world.getInstanceId()))
			{
				players.showQuestMovie(movie);
			}
		}
	}
	
	private void manageBreathSpawn(L2Character killer, L2Npc npc, IQCNBWorld world)
	{
		final L2Attackable breath = (L2Attackable) addSpawn(BREATH, npc.getLocationXYZ(), false, 0, true, world.getInstanceId());
		if (killer != null)
		{
			breath.setIsRunning(true);
			breath.addDamageHate(killer, 0, 999);
			breath.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, killer);
		}
		else
		{
			manageRandomAttack(world, breath);
		}
		
		startQuestTimer("BLIZZARD", 20000, breath, null);
		world.spawnedMobs.add(breath);
	}
	
	private void manageZoneSkill(IQCNBWorld world)
	{
		if (world != null)
		{
			// if (world.spawnedMobs.stream().anyMatch(mob -> mob.getId() == GLACIER))
			int skillLvl = Rnd.get(1, _zoneSkill[1]);
			L2Skill skill = SkillTable.getInstance().getInfo(_zoneSkill[0], skillLvl);
			for (L2PcInstance player : world.playersInside)
			{
				if ((player != null) && !player.isDead() && !player.isAffected(_zoneSkill[0]) && (player.getInstanceId() == world.getInstanceId()))
				{
					skill.getEffects(player, player);
				}
			}
		}
	}
	
	private void manageTargetToAttack(IQCNBWorld world, L2Attackable knight)
	{
		for (L2PcInstance player : world.playersInside)
		{
			if ((player != null) && !player.isDead() && GeoData.getInstance().canSeeTarget(knight, player) && (player.getInstanceId() == world.getInstanceId()))
			{
				knight.addDamageHate(player, 0, 500);
				knight.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
				return;
			}
		}
	}
	
	protected void broadCastPacket(IQCNBWorld world, L2GameServerPacket packet)
	{
		for (int objId : world.getAllowed())
		{
			L2PcInstance player = L2World.getInstance().getPlayer(objId);
			if ((player != null) && player.isOnline() && (player.getInstanceId() == world.getInstanceId()))
			{
				player.sendPacket(packet);
			}
		}
	}
	
	protected void stopAll(IQCNBWorld world)
	{
		if (world == null)
		{
			return;
		}
		
		for (L2Npc mob : InstanceManager.getInstance().getInstance(world.instanceId).getNpcs())
		{
			if ((mob != null) && !mob.isDead())
			{
				mob.abortAttack();
				mob.abortCast();
				if (mob instanceof L2Attackable)
				{
					((L2Attackable) mob).clearAggroList();
				}
			}
		}
		
		for (int objId : world.allowed)
		{
			L2PcInstance player = L2World.getInstance().getPlayer(objId);
			player.abortAttack();
			player.abortCast();
		}
	}
}
