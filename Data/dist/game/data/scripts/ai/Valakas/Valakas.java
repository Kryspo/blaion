package ai.Valakas;

import java.util.List;

import com.l2jserver.Config;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.SpecialCamera;
import com.l2jserver.gameserver.skills.SkillHolder;
import com.l2jserver.gameserver.templates.StatsSet;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;

/**
 * Valakas AI BY L2jPrivateDevelopersTeam
 */
public final class Valakas extends L2AttackableAIScript
{
	// NPC
	private static final int VALAKAS = 29028;
	private static final int PUSTBON = 29029;
	private static final int VOLCANO = 31385;
	private static final int CUBE = 31759;
	
	// Config
	private static int MAX_PLAYERS = Config.VALAKAS_MAXIMUM_PLAYERS;
	private static int MAXIMUM_MINIONS = Config.VALAKAS_MAXIMUM_MINIONS;
	private static int INTERVAL_SPAWN = Config.VALAKAS_INTERVAL_OF_SPAWN;
	private static int RANDOM_SPAWN = Config.VALAKAS_RANDOM_SPAWN;
	private static long WAIT_TIME_BEFORE_SPAWN = Config.VALAKAS_WAIT_TIME;
	
	// Locations
	private static final Location REST_ROOM = new Location(-105200, -253104, -15264);
	private static final Location ENTRANCE = new Location(204187, -111864, 34);
	private static final Location VALAKAS_SPAWN = new Location(212852, -114842, -1632);
	private static final Location OUT_LOC = new Location(150037, -57720, -2976);
	private static final Location[] CUBE_LOC =
	{
		new Location(214880, -116144, -1644),
		new Location(213696, -116592, -1644),
		new Location(212112, -116688, -1644),
		new Location(211184, -115472, -1664),
		new Location(210336, -114592, -1644),
		new Location(211360, -113904, -1644),
		new Location(213152, -112352, -1644),
		new Location(214032, -113232, -1644),
		new Location(214752, -114592, -1644),
		new Location(209824, -115568, -1421),
		new Location(210528, -112192, -1403),
		new Location(213120, -111136, -1408),
		new Location(215184, -111504, -1392),
		new Location(215456, -117328, -1392),
		new Location(213200, -118160, -1424),
	};
	// Skills
	// VALAKAS
	private static final SkillHolder VALAKAS_REGEN1 = new SkillHolder(4691, 1);
	private static final SkillHolder VALAKAS_REGEN2 = new SkillHolder(4691, 2);
	private static final SkillHolder VALAKAS_REGEN3 = new SkillHolder(4691, 3);
	private static final SkillHolder VALAKAS_REGEN4 = new SkillHolder(4691, 4);
	private static final SkillHolder VALAKAS_REGEN5 = new SkillHolder(4691, 5);
	private static final SkillHolder VALAKAS_LAVA_SKIN = new SkillHolder(4680, 1);
	private static final SkillHolder VALAKAS_DRAGON_BREATH = new SkillHolder(4683, 1);
	private static final SkillHolder VALAKAS_STUN = new SkillHolder(4688, 1);
	private static final SkillHolder VALAKAS_FEAR = new SkillHolder(4689, 1);
	private static final SkillHolder VALAKAS_METEOR_STORM = new SkillHolder(4690, 1);
	// PUSTBON
	private static final SkillHolder LAVASAURUS_FIRTBORN_HEAL = new SkillHolder(6657, 1);
	
	// Status
	private static final int ALIVE = 0;
	private static final int WAITING = 1;
	private static final int IN_FIGHT = 2;
	private static final int DEAD = 3;
	// Zone
	private static final L2BossZone _zone = ZoneManager.getInstance().getZoneById(12010, L2BossZone.class); // Valakas Nest zone
	// Misc
	private L2GrandBossInstance _valakas = null;
	private static long _lastAttack = 0;
	private static int _minionCount = 0;
	
	public Valakas()
	{
		super(-1, Valakas.class.getSimpleName(), "ai");
		StartNpcs(VOLCANO, CUBE);
		FirstTalkNpcs(VOLCANO, CUBE);
		TalkNpcs(VOLCANO, CUBE);
		KillNpcs(VALAKAS);
		AttackNpcs(VALAKAS, PUSTBON);
		SpawnNpcs(VALAKAS, PUSTBON);
		registerMobs(PUSTBON);
		
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(VALAKAS);
		final int curr_hp = info.getInt("currentHP");
		final int curr_mp = info.getInt("currentMP");
		final int loc_x = info.getInt("loc_x");
		final int loc_y = info.getInt("loc_y");
		final int loc_z = info.getInt("loc_z");
		final int heading = info.getInt("heading");
		final long respawnTime = info.getLong("respawn_time");
		
		switch (getStatus())
		{
			case ALIVE:
			{
				_valakas = (L2GrandBossInstance) addSpawn(VALAKAS, REST_ROOM, false, 0);
				_valakas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_valakas);
				break;
			}
			case WAITING:
			{
				_valakas = (L2GrandBossInstance) addSpawn(VALAKAS, REST_ROOM, false, 0);
				_valakas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_valakas);
				startQuestTimer("SPAWN_VALAKAS", WAIT_TIME_BEFORE_SPAWN, null, null);
				break;
			}
			case IN_FIGHT:
			{
				_valakas = (L2GrandBossInstance) addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0);
				_valakas.setCurrentHpMp(curr_hp, curr_mp);
				addBoss(_valakas);
				_lastAttack = System.currentTimeMillis();
				startQuestTimer("CHECK_ATTACK", 60000, _valakas, null);
				startQuestTimer("SPAWN_MINION", 300000, _valakas, null);
				startQuestTimer("DO_SKILLS", 60000, _valakas, null);
				break;
			}
			case DEAD:
			{
				final long remain = respawnTime - System.currentTimeMillis();
				if (remain > 0)
				{
					startQuestTimer("CLEAR_STATUS", remain, null, null);
				}
				else
				{
					setStatus(ALIVE);
					_valakas = (L2GrandBossInstance) addSpawn(VALAKAS, REST_ROOM, false, 0);
					addBoss(_valakas);
				}
				break;
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		switch (npc.getNpcId())
		{
			case VOLCANO:
				htmltext = "31385.html";
				break;
			case CUBE:
				htmltext = "31759.html";
				break;
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "enter":
			{
				String htmltext = null;
				if (getStatus() == DEAD)
				{
					htmltext = "31385-01.html";
				}
				else if (getStatus() == IN_FIGHT)
				{
					htmltext = "31385-03.html";
				}
				else if (_zone.getPlayersInside().size() >= MAX_PLAYERS)
				{
					htmltext = "31385-04.html";
				}
				else
				{
					if (player.isInParty())
					{
						final L2Party party = player.getParty();
						final boolean isInCC = party.isInCommandChannel();
						final boolean isPartyLeader = (isInCC) ? party.getCommandChannel().isLeader(player) : party.isLeader(player);
						final List<L2PcInstance> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();
						
						if (isPartyLeader)
						{
							if (members.size() > (MAX_PLAYERS - _zone.getPlayersInside().size()))
							{
								htmltext = "31385-04.html";
							}
							else
							{
								for (L2PcInstance member : members)
								{
									if (member.isInsideRadius(npc, 1000, true, false))
									{
										member.teleToLocation(ENTRANCE);
									}
								}
								if (getStatus() != WAITING)
								{
									setStatus(WAITING);
									startQuestTimer("SPAWN_VALAKAS", WAIT_TIME_BEFORE_SPAWN, null, null);
								}
							}
						}
						else
						{
							if (getStatus() != WAITING)
							{
								setStatus(WAITING);
								startQuestTimer("SPAWN_VALAKAS", WAIT_TIME_BEFORE_SPAWN, null, null);
							}
							player.teleToLocation(ENTRANCE);
						}
					}
					else
					{
						if (getStatus() != WAITING)
						{
							setStatus(WAITING);
							startQuestTimer("SPAWN_VALAKAS", WAIT_TIME_BEFORE_SPAWN, null, null);
						}
						player.teleToLocation(ENTRANCE);
					}
				}
				return htmltext;
			}
			case "teleportOut":
			{
				final Location loc = new Location(OUT_LOC.getX() + getRandom(500), OUT_LOC.getY() + getRandom(500), OUT_LOC.getZ());
				player.teleToLocation(loc);
				break;
			}
			case "SPAWN_VALAKAS":
			{
				_valakas.teleToLocation(VALAKAS_SPAWN);
				setStatus(IN_FIGHT);
				_lastAttack = System.currentTimeMillis();
				_zone.broadcastPacket(new PlaySound("BS03_A"));
				startQuestTimer("CAMERA_1", 100, _valakas, null);
				break;
			}
			case "CAMERA_1":
			{
				_zone.broadcastPacket(new SocialAction(_valakas, 3));
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1800, 180, -1, 1500, 15000, 0, 0, 1, 0));
				startQuestTimer("CAMERA_2", 1500, npc, null);
				break;
			}
			case "CAMERA_2":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 180, -5, 3000, 15000, 0, -5, 1, 0));
				startQuestTimer("CAMERA_3", 5000, npc, null);
				break;
			}
			case "CAMERA_3":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500, 180, -8, 600, 15000, 0, 60, 1, 0));
				startQuestTimer("CAMERA_4", 2900, npc, null);
				break;
			}
			case "CAMERA_4":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 800, 180, -8, 2700, 15000, 0, 30, 1, 0));
				startQuestTimer("CAMERA_5", 2700, npc, null);
				break;
			}
			case "CAMERA_5":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 250, 70, 0, 15000, 30, 80, 1, 0));
				startQuestTimer("CAMERA_6", 1, npc, null);
				break;
			}
			case "CAMERA_6":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 250, 70, 2500, 15000, 30, 80, 1, 0));
				startQuestTimer("CAMERA_7", 3200, npc, null);
				break;
			}
			case "CAMERA_7":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 150, 30, 0, 15000, -10, 60, 1, 0));
				startQuestTimer("CAMERA_8", 1400, npc, null);
				break;
			}
			case "CAMERA_8":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 150, 20, 2900, 15000, -10, 30, 1, 0));
				startQuestTimer("CAMERA_9", 6700, npc, null);
				break;
			}
			case "CAMERA_9":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 750, 170, -10, 3400, 15000, 10, -15, 1, 0));
				startQuestTimer("START_MOVE", 5700, npc, null);
				break;
			}
			case "CAMERA_10":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 210, -5, 3000, 15000, -13, 0, 1, 0));
				startQuestTimer("CAMERA_11", 3500, npc, null);
				break;
			}
			case "CAMERA_11":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 200, -8, 3000, 15000, 0, 15, 1, 0));
				startQuestTimer("CAMERA_12", 4500, npc, null);
				break;
			}
			case "CAMERA_12":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1000, 190, 0, 500, 15000, 0, 10, 1, 0));
				startQuestTimer("CAMERA_13", 500, npc, null);
				break;
			}
			case "CAMERA_13":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 120, 0, 2500, 15000, 12, 40, 1, 0));
				startQuestTimer("CAMERA_14", 4600, npc, null);
				break;
			}
			case "CAMERA_14":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 20, 0, 700, 15000, 10, 10, 1, 0));
				startQuestTimer("CAMERA_15", 750, npc, null);
				break;
			}
			case "CAMERA_15":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 10, 0, 1000, 15000, 20, 70, 1, 0));
				startQuestTimer("CAMERA_16", 2500, npc, null);
				break;
			}
			case "CAMERA_16":
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 10, 0, 800, 15000, 20, -20, 1, 0));
				break;
			}
			case "START_MOVE":
			{
				npc.doCast(VALAKAS_REGEN1.getSkill());
				startQuestTimer("CHECK_ATTACK", 60000, _valakas, null);
				startQuestTimer("SPAWN_MINION", 60000, _valakas, null);
				startQuestTimer("DO_SKILLS", 60000, _valakas, null);
				
				for (L2PcInstance players : npc.getKnownList().getKnownPlayersInRadius(4000))
				{
					if (players.isHero())
					{
						players.broadcastPacket(new ExShowScreenMessage(player.getName() + " you cannot hope to defeat me with your meager strenght", 4000));
						break;
					}
				}
				break;
			}
			case "SET_REGEN":
			{
				if ((((npc.getCurrentHp() / npc.getMaxHp()) * 100) < 75) && (getRandom(150) == 0) && (!npc.isAffected(VALAKAS_LAVA_SKIN.getSkillId())))
				{
					npc.doCast(VALAKAS_REGEN5.getSkill());
				}
				if (npc.getCurrentHp() < (npc.getMaxHp() * 0.2))
				{
					npc.doCast(VALAKAS_REGEN5.getSkill());
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.4))
				{
					npc.doCast(VALAKAS_REGEN4.getSkill());
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.6))
				{
					npc.doCast(VALAKAS_REGEN3.getSkill());
				}
				else if (npc.getCurrentHp() < (npc.getMaxHp() * 0.8))
				{
					npc.doCast(VALAKAS_REGEN2.getSkill());
				}
				else
				{
					npc.doCast(VALAKAS_REGEN1.getSkill());
				}
				startQuestTimer("SET_REGEN", 60000, npc, null);
				break;
			}
			case "CHECK_ATTACK":
			{
				if ((npc != null) && ((_lastAttack + 900000) < System.currentTimeMillis()))
				{
					setStatus(ALIVE);
					for (L2Character charInside : _zone.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								if (charInside.getNpcId() == VALAKAS)
								{
									charInside.teleToLocation(REST_ROOM);
								}
								else
								{
									charInside.deleteMe();
								}
							}
							else if (charInside.isPlayer())
							{
								final Location loc = new Location(OUT_LOC.getX() + getRandom(500), OUT_LOC.getY() + getRandom(500), OUT_LOC.getZ());
								charInside.teleToLocation(loc);
							}
						}
					}
					cancelQuestTimer("CHECK_ATTACK", npc, null);
				}
				else
				{
					startQuestTimer("CHECK_ATTACK", 60000, _valakas, null);
				}
				break;
			}
			case "SPAWN_MINION":
			{
				if (_minionCount == MAXIMUM_MINIONS)
				{
					cancelQuestTimer("SPAWN_MINION", npc, null);
				}
				
				if (_minionCount < MAXIMUM_MINIONS)
				{
					addSpawn(PUSTBON, _valakas.getX() + Rnd.get(0, 1500), _valakas.getY() + Rnd.get(0, 1500), -1636, 0, false, 0, false, _valakas.getInstanceId());
					_minionCount++;
					_log.info("VALAKAS MINIONS COUNTING: " + _minionCount);
					startQuestTimer("SPAWN_MINION", 10000, npc, null);
				}
				break;
			}
			case "CLEAR_STATUS":
			{
				_valakas = (L2GrandBossInstance) addSpawn(VALAKAS, REST_ROOM, false, 0);
				addBoss(_valakas);
				setStatus(ALIVE);
				break;
			}
			case "CLEAR_ZONE":
			{
				for (L2Character charInside : _zone.getCharactersInside())
				{
					if (charInside != null)
					{
						if (charInside.isNpc())
						{
							charInside.deleteMe();
						}
						else if (charInside.isPlayer())
						{
							final Location loc = new Location(OUT_LOC.getX() + getRandom(500), OUT_LOC.getY() + getRandom(500), OUT_LOC.getZ());
							charInside.teleToLocation(loc);
						}
					}
				}
				break;
			}
			case "SKIP_WAITING":
			{
				if (getStatus() == WAITING)
				{
					cancelQuestTimer("SPAWN_VALAKAS", null, null);
					notifyEvent("SPAWN_VALAKAS", null, null);
					player.sendMessage(getClass().getSimpleName() + ": Skipping waiting time ...");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You cant skip waiting time right now!");
				}
				break;
			}
			case "RESPAWN_VALAKAS":
			{
				if (getStatus() == DEAD)
				{
					setRespawn(0);
					cancelQuestTimer("CLEAR_STATUS", null, null);
					notifyEvent("CLEAR_STATUS", null, null);
					player.sendMessage(getClass().getSimpleName() + ": Valakas has been respawned.");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You cant respawn valakas while valakas is alive!");
				}
				break;
			}
			case "HEAL_VAL":
			{
				if (npc.getNpcId() == PUSTBON)
				{
					npc.setTarget(_valakas);
					npc.doCast(LAVASAURUS_FIRTBORN_HEAL.getSkill());
					startQuestTimer("DO_SKILLS", 30000 + getRandom(1000, 7000), npc, null);
				}
				break;
			}
			case "DO_SKILLS":
			{
				startQuestTimer("DO_SKILLS", 15000 + getRandom(1000, 7000), npc, null);
				final long chance = getRandom(100);
				
				final int hpRatio = (int) ((npc.getCurrentHp() / npc.getMaxHp()) * 100);
				if ((hpRatio < 75) && !npc.isAffected(VALAKAS_LAVA_SKIN.getSkillId()))
				{
					npc.doCast(VALAKAS_LAVA_SKIN.getSkill());
				}
				
				// Valakas will use mass spells if he feels surrounded.
				if (Util.getPlayersCountInRadius(1200, npc, false, false) >= 20)
				{
					if (chance < 40)
					{
						npc.doCast(VALAKAS_DRAGON_BREATH.getSkill()); // Valakas Dragon Breath
					}
					else if (chance < 40)
					{
						npc.doCast(VALAKAS_STUN.getSkill()); // Valakas Stun
					}
					else if (chance < 40)
					{
						npc.doCast(VALAKAS_FEAR.getSkill()); // Valakas Fear
					}
					else
					{
						npc.doCast(VALAKAS_METEOR_STORM.getSkill()); // Valakas Meteor Storm
					}
					
				}
				
				if (hpRatio > 50)
				{
					if (chance < 60)
					{
						npc.doCast(VALAKAS_DRAGON_BREATH.getSkill()); // Valakas Dragon Breath
					}
					else
					{
						npc.doCast(VALAKAS_FEAR.getSkill()); // Valakas Fear
					}
					
				}
				
				if (hpRatio < 50)
				{
					if (chance < 60)
					{
						npc.doCast(VALAKAS_DRAGON_BREATH.getSkill()); // Valakas Dragon Breath
					}
					if (chance < 60)
					{
						npc.doCast(VALAKAS_FEAR.getSkill()); // Valakas Fear
					}
					else
					{
						npc.doCast(VALAKAS_METEOR_STORM.getSkill()); // Valakas Meteor Storm
					}
				}
				break;
			}
			case "ABORT_FIGHT":
			
			{
				if (getStatus() == IN_FIGHT)
				{
					setStatus(ALIVE);
					cancelQuestTimer("CHECK_ATTACK", _valakas, null);
					cancelQuestTimer("SPAWN_MINION", _valakas, null);
					cancelQuestTimer("DO_SKILLS", _valakas, null);
					for (L2Character charInside : _zone.getCharactersInside())
					{
						if (charInside != null)
						{
							if (charInside.isNpc())
							{
								if (charInside.getNpcId() == VALAKAS)
								{
									charInside.teleToLocation(REST_ROOM);
								}
								else
								{
									charInside.deleteMe();
								}
							}
							else if (charInside.isPlayer() && !charInside.isGM())
							{
								final Location loc = new Location(OUT_LOC.getX() + getRandom(500), OUT_LOC.getY() + getRandom(500), OUT_LOC.getZ());
								charInside.teleToLocation(loc);
							}
						}
					}
					player.sendMessage(getClass().getSimpleName() + ": Fight has been aborted!");
				}
				else
				{
					player.sendMessage(getClass().getSimpleName() + ": You cant abort fight right now!");
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
		
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		_lastAttack = System.currentTimeMillis();
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (_zone.isCharacterInZone(killer))
		{
			if (npc.getNpcId() == VALAKAS)
			{
				_zone.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2000, 130, -1, 0, 15000, 0, 0, 1, 0));
				_zone.broadcastPacket(new PlaySound("B03_D"));
				startQuestTimer("CAMERA_10", 500, npc, null);
				long respawnTime = (INTERVAL_SPAWN + getRandom(-RANDOM_SPAWN, RANDOM_SPAWN)) * 3600000;
				setRespawn(respawnTime);
				for (Location loc : CUBE_LOC)
				{
					addSpawn(CUBE, loc, false, 900000);
				}
				startQuestTimer("CLEAR_STATUS", respawnTime, null, null);
				cancelQuestTimer("SET_REGEN", npc, null);
				cancelQuestTimer("CHECK_ATTACK", npc, null);
				cancelQuestTimer("SPAWN_MINION", npc, null);
				cancelQuestTimer("DO_SKILLS", npc, null);
				startQuestTimer("CLEAR_ZONE", 900000, null, null);
				setStatus(DEAD);
			}
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getNpcId())
		{
			case VALAKAS:
				cancelQuestTimer("SET_REGEN", npc, null);
				startQuestTimer("SET_REGEN", 60000, npc, null);
				((L2Attackable) npc).setOnKillDelay(0);
				npc.setRandomAnimationEnabled(false);
				break;
			case PUSTBON:
				startQuestTimer("HEAL_VAL", 60000, npc, null);
				npc.setHideName(true);
				break;
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return super.onSpellFinished(npc, player, skill);
	}
	
	private int getStatus()
	{
		return GrandBossManager.getInstance().getBossStatus(VALAKAS);
	}
	
	private void addBoss(L2GrandBossInstance grandboss)
	{
		GrandBossManager.getInstance().addBoss(grandboss);
	}
	
	private void setStatus(int status)
	{
		GrandBossManager.getInstance().setBossStatus(VALAKAS, status);
	}
	
	private void setRespawn(long respawnTime)
	{
		GrandBossManager.getInstance().getStatsSet(VALAKAS).set("respawn_time", (System.currentTimeMillis() + respawnTime));
	}
}