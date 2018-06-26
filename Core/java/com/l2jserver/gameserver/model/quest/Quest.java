/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.quest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.ChampionData;
import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2ItemInstance;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.L2Trap;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TrapInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.quest.AiTask.SeeCreature;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.NpcQuestHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.StatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.scripting.ManagedScript;
import com.l2jserver.gameserver.scripting.ScriptManager;
import com.l2jserver.gameserver.skills.Stats;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.gameserver.templates.item.L2EtcItem;
import com.l2jserver.gameserver.templates.item.L2Item;
import com.l2jserver.gameserver.util.MinionList;
import com.l2jserver.util.Rnd;
import com.l2jserver.util.Util;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author Luis Arias
 */
public class Quest extends ManagedScript
{
	public static final Logger _log = Logger.getLogger(Quest.class.getName());
	
	private static final int RESET_HOUR = 6;
	private static final int RESET_MINUTES = 30;
	
	/** HashMap containing events from String value of the event */
	private static Map<String, Quest> _allEventsS = new FastMap<>();
	/** HashMap containing lists of timers from the name of the timer */
	private final Map<String, FastList<QuestTimer>> _allEventTimers = new FastMap<>();
	// private final Map<String, List<QuestTimer>> _allEventTimers = new FastMap<>();
	
	private final ReentrantReadWriteLock _rwLock = new ReentrantReadWriteLock();
	
	private final int _questId;
	private final String _name;
	private final String _descr;
	private final byte _initialState = State.CREATED;
	protected boolean _onEnterWorld = false;
	// NOTE: questItemIds will be overridden by child classes. Ideally, it should be
	// protected instead of public. However, quest scripts written in Jython will
	// have trouble with protected, as Jython only knows private and public...
	// In fact, protected will typically be considered private thus breaking the scripts.
	// Leave this as public as a workaround.
	public int[] questItemIds = null;
	
	private final Set<Integer> _questInvolvedNpcs = new HashSet<>();
	
	private static final String DEFAULT_NO_QUEST_MSG = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
	private static final String DEFAULT_ALREADY_COMPLETED_MSG = "<html><body>This quest has already been completed.</body></html>";
	
	public static enum QuestSound
	{
		ITEMSOUND_QUEST_ACCEPT(new PlaySound("ItemSound.quest_accept")),
		ITEMSOUND_QUEST_MIDDLE(new PlaySound("ItemSound.quest_middle")),
		ITEMSOUND_QUEST_FINISH(new PlaySound("ItemSound.quest_finish")),
		ITEMSOUND_QUEST_ITEMGET(new PlaySound("ItemSound.quest_itemget")),
		// Newbie Guide tutorial (incl. some quests), Mutated Kaneus quests, Quest 192
		ITEMSOUND_QUEST_TUTORIAL(new PlaySound("ItemSound.quest_tutorial")),
		// Quests 107, 363, 364
		ITEMSOUND_QUEST_GIVEUP(new PlaySound("ItemSound.quest_giveup")),
		// Quests 212, 217, 224, 226, 416
		ITEMSOUND_QUEST_BEFORE_BATTLE(new PlaySound("ItemSound.quest_before_battle")),
		// Quests 211, 258, 266, 330
		ITEMSOUND_QUEST_JACKPOT(new PlaySound("ItemSound.quest_jackpot")),
		// Quests 508, 509 and 510
		ITEMSOUND_QUEST_FANFARE_1(new PlaySound("ItemSound.quest_fanfare_1")),
		// Played only after class transfer via Test Server Helpers (Id 31756 and 31757)
		ITEMSOUND_QUEST_FANFARE_2(new PlaySound("ItemSound.quest_fanfare_2")),
		// Quest 336
		ITEMSOUND_QUEST_FANFARE_MIDDLE(new PlaySound("ItemSound.quest_fanfare_middle")),
		// Quest 114
		ITEMSOUND_ARMOR_WOOD(new PlaySound("ItemSound.armor_wood_3")),
		// Quest 21
		ITEMSOUND_ARMOR_CLOTH(new PlaySound("ItemSound.item_drop_equip_armor_cloth")),
		AMDSOUND_ED_CHIMES(new PlaySound("AmdSound.ed_chimes_05")),
		HORROR_01(new PlaySound("horror_01")), // played when spawned monster sees player
		// Quest 22
		AMBSOUND_HORROR_01(new PlaySound("AmbSound.dd_horror_01")),
		AMBSOUND_HORROR_03(new PlaySound("AmbSound.d_horror_03")),
		AMBSOUND_HORROR_15(new PlaySound("AmbSound.d_horror_15")),
		// Quest 23
		ITEMSOUND_ARMOR_LEATHER(new PlaySound("ItemSound.itemdrop_armor_leather")),
		ITEMSOUND_WEAPON_SPEAR(new PlaySound("ItemSound.itemdrop_weapon_spear")),
		AMBSOUND_MT_CREAK(new PlaySound("AmbSound.mt_creak01")),
		AMBSOUND_EG_DRON(new PlaySound("AmbSound.eg_dron_02")),
		SKILLSOUND_HORROR_02(new PlaySound("SkillSound5.horror_02")),
		CHRSOUND_MHFIGHTER_CRY(new PlaySound("ChrSound.MHFighter_cry")),
		// Quest 24
		AMDSOUND_WIND_LOOT(new PlaySound("AmdSound.d_wind_loot_02")),
		INTERFACESOUND_CHARSTAT_OPEN(new PlaySound("InterfaceSound.charstat_open_01")),
		// Quest 25
		AMDSOUND_HORROR_02(new PlaySound("AmdSound.dd_horror_02")),
		CHRSOUND_FDELF_CRY(new PlaySound("ChrSound.FDElf_Cry")),
		// Quest 115
		AMBSOUND_WINGFLAP(new PlaySound("AmbSound.t_wingflap_04")),
		AMBSOUND_THUNDER(new PlaySound("AmbSound.thunder_02")),
		// Quest 120
		AMBSOUND_DRONE(new PlaySound("AmbSound.ed_drone_02")),
		AMBSOUND_CRYSTAL_LOOP(new PlaySound("AmbSound.cd_crystal_loop")),
		AMBSOUND_PERCUSSION_01(new PlaySound("AmbSound.dt_percussion_01")),
		AMBSOUND_PERCUSSION_02(new PlaySound("AmbSound.ac_percussion_02")),
		// Quest 648 and treasure chests
		ITEMSOUND_BROKEN_KEY(new PlaySound("ItemSound2.broken_key")),
		// Quest 184
		ITEMSOUND_SIREN(new PlaySound("ItemSound3.sys_siren")),
		// Quest 648
		ITEMSOUND_ENCHANT_SUCCESS(new PlaySound("ItemSound3.sys_enchant_success")),
		ITEMSOUND_ENCHANT_FAILED(new PlaySound("ItemSound3.sys_enchant_failed")),
		// Best farm mobs
		ITEMSOUND_SOW_SUCCESS(new PlaySound("ItemSound3.sys_sow_success")),
		// Quest 25
		SKILLSOUND_HORROR_1(new PlaySound("SkillSound5.horror_01")),
		// Quests 21 and 23
		SKILLSOUND_HORROR_2(new PlaySound("SkillSound5.horror_02")),
		// Quest 22
		SKILLSOUND_ANTARAS_FEAR(new PlaySound("SkillSound3.antaras_fear")),
		// Quest 505
		SKILLSOUND_JEWEL_CELEBRATE(new PlaySound("SkillSound2.jewel.celebrate")),
		// Quest 373
		SKILLSOUND_LIQUID_MIX(new PlaySound("SkillSound5.liquid_mix_01")),
		SKILLSOUND_LIQUID_SUCCESS(new PlaySound("SkillSound5.liquid_success_01")),
		SKILLSOUND_LIQUID_FAIL(new PlaySound("SkillSound5.liquid_fail_01")),
		// Quest 111
		ETCSOUND_ELROKI_SONG_FULL(new PlaySound("EtcSound.elcroki_song_full")),
		ETCSOUND_ELROKI_SONG_1ST(new PlaySound("EtcSound.elcroki_song_1st")),
		ETCSOUND_ELROKI_SONG_2ND(new PlaySound("EtcSound.elcroki_song_2nd")),
		ETCSOUND_ELROKI_SONG_3RD(new PlaySound("EtcSound.elcroki_song_3rd")),
		// Long duration AI sounds
		BS01_A(new PlaySound("BS01_A")),
		BS02_A(new PlaySound("BS02_A")),
		BS03_A(new PlaySound("BS03_A")),
		BS04_A(new PlaySound("BS04_A")),
		BS06_A(new PlaySound("BS06_A")),
		BS07_A(new PlaySound("BS07_A")),
		BS08_A(new PlaySound("BS08_A")),
		BS01_D(new PlaySound("BS01_D")),
		BS02_D(new PlaySound("BS02_D")),
		BS05_D(new PlaySound("BS05_D")),
		BS07_D(new PlaySound("BS07_D")),
		
		TUTORIAL_VOICE_001A_2000(new PlaySound("tutorial_voice_001a")),
		TUTORIAL_VOICE_001B_2000(new PlaySound("tutorial_voice_001b")),
		TUTORIAL_VOICE_001C_2000(new PlaySound("tutorial_voice_001c")),
		TUTORIAL_VOICE_001D_2000(new PlaySound("tutorial_voice_001d")),
		TUTORIAL_VOICE_001E_2000(new PlaySound("tutorial_voice_001e")),
		TUTORIAL_VOICE_001F_2000(new PlaySound("tutorial_voice_001f")),
		TUTORIAL_VOICE_001G_2000(new PlaySound("tutorial_voice_001g")),
		TUTORIAL_VOICE_001H_2000(new PlaySound("tutorial_voice_001h")),
		TUTORIAL_VOICE_001I_2000(new PlaySound("tutorial_voice_001i")),
		TUTORIAL_VOICE_001K_2000(new PlaySound("tutorial_voice_001k")),
		TUTORIAL_VOICE_002_1000(new PlaySound("tutorial_voice_002")),
		TUTORIAL_VOICE_003_2000(new PlaySound("tutorial_voice_003")),
		TUTORIAL_VOICE_004_5000(new PlaySound("tutorial_voice_004")),
		TUTORIAL_VOICE_005_5000(new PlaySound("tutorial_voice_005")),
		TUTORIAL_VOICE_006_1000(new PlaySound("tutorial_voice_006")),
		TUTORIAL_VOICE_006_3500(new PlaySound("tutorial_voice_006")),
		TUTORIAL_VOICE_007_3500(new PlaySound("tutorial_voice_007")),
		TUTORIAL_VOICE_008_1000(new PlaySound("tutorial_voice_008")),
		TUTORIAL_VOICE_009A(new PlaySound("tutorial_voice_009a")),
		TUTORIAL_VOICE_009B(new PlaySound("tutorial_voice_009b")),
		TUTORIAL_VOICE_009C(new PlaySound("tutorial_voice_009c")),
		TUTORIAL_VOICE_010A(new PlaySound("tutorial_voice_010a")),
		TUTORIAL_VOICE_010B(new PlaySound("tutorial_voice_010b")),
		TUTORIAL_VOICE_010C(new PlaySound("tutorial_voice_010c")),
		TUTORIAL_VOICE_010D(new PlaySound("tutorial_voice_010d")),
		TUTORIAL_VOICE_010E(new PlaySound("tutorial_voice_010e")),
		TUTORIAL_VOICE_010F(new PlaySound("tutorial_voice_010f")),
		TUTORIAL_VOICE_010G(new PlaySound("tutorial_voice_010g")),
		TUTORIAL_VOICE_011_1000(new PlaySound("tutorial_voice_011")),
		TUTORIAL_VOICE_012_1000(new PlaySound("tutorial_voice_012")),
		TUTORIAL_VOICE_013_1000(new PlaySound("tutorial_voice_013")),
		TUTORIAL_VOICE_014_1000(new PlaySound("tutorial_voice_014")),
		TUTORIAL_VOICE_016_1000(new PlaySound("tutorial_voice_016")),
		TUTORIAL_VOICE_017_1000(new PlaySound("tutorial_voice_017")),
		TUTORIAL_VOICE_018_1000(new PlaySound("tutorial_voice_018")),
		TUTORIAL_VOICE_019_1000(new PlaySound("tutorial_voice_019")),
		TUTORIAL_VOICE_020_1000(new PlaySound("tutorial_voice_020")),
		TUTORIAL_VOICE_021_1000(new PlaySound("tutorial_voice_021")),
		TUTORIAL_VOICE_023_1000(new PlaySound("tutorial_voice_023")),
		TUTORIAL_VOICE_024_1000(new PlaySound("tutorial_voice_024")),
		TUTORIAL_VOICE_025_1000(new PlaySound("tutorial_voice_025")),
		TUTORIAL_VOICE_026_1000(new PlaySound("tutorial_voice_026")),
		TUTORIAL_VOICE_027_1000(new PlaySound("tutorial_voice_027")),
		TUTORIAL_VOICE_028_1000(new PlaySound("tutorial_voice_028")),
		TUTORIAL_VOICE_030_1000(new PlaySound("tutorial_voice_030"));
		
		private final PlaySound _playSound;
		
		private static Map<String, PlaySound> soundPackets = new HashMap<>();
		
		private QuestSound(PlaySound playSound)
		{
			_playSound = playSound;
		}
		
		/**
		 * Get a {@link PlaySound} packet by its name.
		 * @param soundName : the name of the sound to look for
		 * @return the {@link PlaySound} packet with the specified sound or {@code null} if one was not found
		 */
		public static PlaySound getSound(String soundName)
		{
			if (soundPackets.containsKey(soundName))
			{
				return soundPackets.get(soundName);
			}
			
			for (QuestSound qs : QuestSound.values())
			{
				if (qs._playSound.getSoundName().equals(soundName))
				{
					soundPackets.put(soundName, qs._playSound); // cache in map to avoid looping repeatedly
					return qs._playSound;
				}
			}
			
			_log.info("Missing QuestSound enum for sound: " + soundName);
			soundPackets.put(soundName, new PlaySound(soundName));
			return soundPackets.get(soundName);
		}
		
		/**
		 * @return the name of the sound of this QuestSound object
		 */
		public String getSoundName()
		{
			return _playSound.getSoundName();
		}
		
		/**
		 * @return the {@link PlaySound} packet of this QuestSound object
		 */
		public PlaySound getPacket()
		{
			return _playSound;
		}
	}
	
	public static void playSound(L2PcInstance player, QuestSound sound)
	{
		player.sendPacket(sound.getPacket());
	}
	
	/**
	 * Return collection view of the values contains in the allEventS
	 * @return Collection<Quest>
	 */
	public static Collection<Quest> findAllEvents()
	{
		return _allEventsS.values();
	}
	
	/**
	 * (Constructor)Add values to class variables and put the quest in HashMaps.
	 * @param questId : int pointing out the ID of the quest
	 * @param name : String corresponding to the name of the quest
	 * @param descr : String for the description of the quest
	 */
	public Quest(int questId, String name, String descr)
	{
		_questId = questId;
		_name = name;
		_descr = descr;
		
		if (questId != 0)
		{
			QuestManager.getInstance().addQuest(Quest.this);
		}
		else
		{
			_allEventsS.put(name, this);
		}
		init_LoadGlobalData();
	}
	
	/**
	 * The function init_LoadGlobalData is, by default, called by the constructor of all quests. Children of this class can implement this function in order to define what variables to load and what structures to save them in. By default, nothing is loaded.
	 */
	protected void init_LoadGlobalData()
	{
		
	}
	
	/**
	 * The function saveGlobalData is, by default, called at shutdown, for all quests, by the QuestManager. Children of this class can implement this function in order to convert their structures into <var, value> tuples and make calls to save them to the database, if needed. By default, nothing is
	 * saved.
	 */
	public void saveGlobalData()
	{
		
	}
	
	public static enum TrapAction
	{
		TRAP_TRIGGERED,
		TRAP_DETECTED,
		TRAP_DISARMED
	}
	
	public static enum QuestEventType
	{
		ON_FIRST_TALK(false), // control the first dialog shown by NPCs when they are clicked (some quests must override the default npc action)
		QUEST_START(true), // onTalk action from start npcs
		ON_TALK(true), // onTalk action from npcs participating in a quest
		ON_ATTACK(true), // onAttack action triggered when a mob gets attacked by someone
		ON_KILL(true), // onKill action triggered when a mob gets killed.
		ON_SPAWN(true), // onSpawn action triggered when an NPC is spawned or respawned.
		ON_SKILL_SEE(true), // NPC or Mob saw a person casting a skill (regardless what the target is).
		ON_FACTION_CALL(true), // NPC or Mob saw a person casting a skill (regardless what the target is).
		ON_AGGRO_RANGE_ENTER(true), // a person came within the Npc/Mob's range
		ON_SPELL_FINISHED(true), // on spell finished action when npc finish casting skill
		ON_SKILL_LEARN(false), // control the AcquireSkill dialog from quest script
		ON_ENTER_ZONE(true), // on zone enter
		ON_EXIT_ZONE(true), // on zone exit
		ON_ITEM_USE(true), // on Item use
		ON_TRAP_ACTION(true), // on zone exit
		ON_SEE_CREATURE(true), // onSeeCreature action, triggered when NPC's known list include the character
		ON_MOVE_FINISHED(true), // onMoveFinished action, triggered when NPC stops after moving
		ON_ROUTE_FINISHED(true), // onRouteFinished action, triggered when NPC, controlled by Walking Manager, arrives to last node
		ON_EVENT_RECEIVED(true), // onEventReceived action, triggered when NPC receiving an event, sent by other NPC
		ON_NODE_ARRIVED(true); // onNodeArrived action, triggered when NPC, controlled by Walking Manager, arrives to next node
		// control whether this event type is allowed for the same npc template in multiple quests
		// or if the npc must be registered in at most one quest for the specified event
		private boolean _allowMultipleRegistration;
		
		QuestEventType(boolean allowMultipleRegistration)
		{
			_allowMultipleRegistration = allowMultipleRegistration;
		}
		
		public boolean isMultipleRegistrationAllowed()
		{
			return _allowMultipleRegistration;
		}
		
	}
	
	/**
	 * Return ID of the quest
	 * @return int
	 */
	public int getQuestIntId()
	{
		return _questId;
	}
	
	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @return QuestState : QuestState created
	 */
	public QuestState newQuestState(L2PcInstance player)
	{
		QuestState qs = new QuestState(this, player, getInitialState());
		return qs;
	}
	
	/**
	 * Return initial state of the quest
	 * @return State
	 */
	public byte getInitialState()
	{
		return _initialState;
	}
	
	/**
	 * Return name of the quest
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * Return description of the quest
	 * @return String
	 */
	public String getDescr()
	{
		return _descr;
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already
	 * @param name: name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time: time in ms for when to fire the timer
	 * @param npc: npc associated with this timer (can be null)
	 * @param player: player associated with this timer (can be null)
	 */
	public void startQuestTimer(String name, long time, L2Npc npc, L2PcInstance player)
	{
		startQuestTimer(name, time, npc, player, false);
	}
	
	/**
	 * Add a timer to the quest, if it doesn't exist already. If the timer is repeatable, it will auto-fire automatically, at a fixed rate, until explicitly canceled.
	 * @param name: name of the timer (also passed back as "event" in onAdvEvent)
	 * @param time: time in ms for when to fire the timer
	 * @param npc: npc associated with this timer (can be null)
	 * @param player: player associated with this timer (can be null)
	 * @param repeatable: indicates if the timer is repeatable or one-time.
	 */
	public void startQuestTimer(String name, long time, L2Npc npc, L2PcInstance player, boolean repeating)
	{
		// Add quest timer if timer doesn't already exist
		FastList<QuestTimer> timers = getQuestTimers(name);
		// no timer exists with the same name, at all
		if (timers == null)
		{
			timers = new FastList<>();
			timers.add(new QuestTimer(this, name, time, npc, player, repeating));
			_allEventTimers.put(name, timers);
		}
		// a timer with this name exists, but may not be for the same set of npc and player
		else
		{
			// if there exists a timer with this name, allow the timer only if the [npc, player] set is unique
			// nulls act as wildcards
			if (getQuestTimer(name, npc, player) == null)
			{
				try
				{
					_rwLock.writeLock().lock();
					timers.add(new QuestTimer(this, name, time, npc, player, repeating));
				}
				finally
				{
					_rwLock.writeLock().unlock();
				}
			}
		}
		
		if (Config.DEBUG_TIMERS)
		{
			_log.info("startQuestTimer --> " + name + "  -  " + time + " ms -- " + getClass().getSimpleName());
		}
	}
	
	public QuestTimer getQuestTimer(String name, L2Npc npc, L2PcInstance player)
	{
		FastList<QuestTimer> qt = getQuestTimers(name);
		
		if ((qt == null) || qt.isEmpty())
		{
			return null;
		}
		try
		{
			_rwLock.readLock().lock();
			for (QuestTimer timer : qt)
			{
				if (timer != null)
				{
					if (timer.isMatch(this, name, npc, player))
					{
						return timer;
					}
				}
			}
			
		}
		finally
		{
			_rwLock.readLock().unlock();
		}
		return null;
	}
	
	private FastList<QuestTimer> getQuestTimers(String name)
	{
		return _allEventTimers.get(name);
	}
	
	public void cancelQuestTimers(String name)
	{
		FastList<QuestTimer> timers = getQuestTimers(name);
		if (timers == null)
		{
			return;
		}
		try
		{
			_rwLock.writeLock().lock();
			for (QuestTimer timer : timers)
			{
				if (timer != null)
				{
					timer.cancel();
				}
			}
		}
		finally
		{
			_rwLock.writeLock().unlock();
		}
		if (Config.DEBUG_TIMERS)
		{
			_log.info("cancelQuestTimer --> " + name);
		}
	}
	
	public void cancelQuestTimer(String name, L2Npc npc, L2PcInstance player)
	{
		QuestTimer timer = getQuestTimer(name, npc, player);
		if (timer != null)
		{
			timer.cancel();
		}
		if (Config.DEBUG_TIMERS)
		{
			_log.info("cancelQuestTimer --> " + name);
		}
	}
	
	public void removeQuestTimer(QuestTimer timer)
	{
		if (timer == null)
		{
			return;
		}
		FastList<QuestTimer> timers = getQuestTimers(timer.getName());
		if (timers == null)
		{
			return;
		}
		try
		{
			_rwLock.writeLock().lock();
			timers.remove(timer);
		}
		finally
		{
			_rwLock.writeLock().unlock();
		}
	}
	
	// these are methods to call from java
	public final boolean notifyAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onAttack(npc, attacker, damage, isPet, skill);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(attacker, res);
	}
	
	public final boolean notifyDeath(L2Character killer, L2Character victim, QuestState qs)
	{
		String res = null;
		try
		{
			res = onDeath(killer, victim, qs);
		}
		catch (Exception e)
		{
			return showError(qs.getPlayer(), e);
		}
		return showResult(qs.getPlayer(), res);
	}
	
	public final boolean notifySpellFinished(L2Npc instance, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onSpellFinished(instance, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	/**
	 * Notify quest script when something happens with a trap
	 * @param trap: the trap instance which triggers the notification
	 * @param trigger: the character which makes effect on the trap
	 * @param action: 0: trap casting its skill. 1: trigger detects the trap. 2: trigger removes the trap
	 */
	public final boolean notifyTrapAction(L2Trap trap, L2Character trigger, TrapAction action)
	{
		String res = null;
		try
		{
			res = onTrapAction(trap, trigger, action);
		}
		catch (Exception e)
		{
			if (trigger.getActingPlayer() != null)
			{
				return showError(trigger.getActingPlayer(), e);
			}
			_log.log(Level.WARNING, "Exception on onTrapAction() in notifyTrapAction(): " + e.getMessage(), e);
			return true;
		}
		if (trigger.getActingPlayer() != null)
		{
			return showResult(trigger.getActingPlayer(), res);
		}
		return false;
	}
	
	public final boolean notifySpawn(L2Npc npc)
	{
		try
		{
			onSpawn(npc);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onSpawn() in notifySpawn(): " + e.getMessage(), e);
			return true;
		}
		return false;
	}
	
	public final boolean notifyEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAdvEvent(event, npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	public final boolean notifyEnterWorld(L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onEnterWorld(player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	public final boolean notifyKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		String res = null;
		try
		{
			res = onKill(npc, killer, isPet);
		}
		catch (Exception e)
		{
			return showError(killer, e);
		}
		return showResult(killer, res);
	}
	
	public final boolean notifyTalk(L2Npc npc, QuestState qs)
	{
		String res = null;
		try
		{
			res = onTalk(npc, qs.getPlayer());
		}
		catch (Exception e)
		{
			return showError(qs.getPlayer(), e);
		}
		qs.getPlayer().setLastQuestNpcObject(npc.getObjectId());
		return showResult(qs.getPlayer(), res);
	}
	
	// override the default NPC dialogs when a quest defines this for the given NPC
	public final boolean notifyFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		// if the quest returns text to display, display it.
		if ((res != null) && (res.length() > 0))
		{
			return showResult(player, res);
			// else tell the player that
		}
		player.sendPacket(ActionFailed.STATIC_PACKET);
		// note: if the default html for this npc needs to be shown, onFirstTalk should
		// call npc.showChatWindow(player) and then return null.
		return true;
	}
	
	public final boolean notifyAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onAcquireSkillList(npc, player);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	public final boolean notifyAcquireSkillInfo(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkillInfo(npc, player, skill);
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	public final boolean notifyAcquireSkill(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		String res = null;
		try
		{
			res = onAcquireSkill(npc, player, skill);
			if (res == "true")
			{
				return true;
			}
			else if (res == "false")
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	public class TmpOnSkillSee implements Runnable
	{
		private final L2Npc _npc;
		private final L2PcInstance _caster;
		private final L2Skill _skill;
		private final L2Object[] _targets;
		private final boolean _isPet;
		
		public TmpOnSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
		{
			_npc = npc;
			_caster = caster;
			_skill = skill;
			_targets = targets;
			_isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onSkillSee(_npc, _caster, _skill, _targets, _isPet);
			}
			catch (Exception e)
			{
				showError(_caster, e);
			}
			showResult(_caster, res);
			
		}
	}
	
	public final boolean notifySkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		ThreadPoolManager.getInstance().executeAi(new TmpOnSkillSee(npc, caster, skill, targets, isPet));
		return true;
	}
	
	public final boolean notifyFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		String res = null;
		try
		{
			res = onFactionCall(npc, caller, attacker, isPet);
		}
		catch (Exception e)
		{
			return showError(attacker, e);
		}
		return showResult(attacker, res);
	}
	
	public class TmpOnAggroEnter implements Runnable
	{
		private final L2Npc _npc;
		private final L2PcInstance _pc;
		private final boolean _isPet;
		
		public TmpOnAggroEnter(L2Npc npc, L2PcInstance pc, boolean isPet)
		{
			_npc = npc;
			_pc = pc;
			_isPet = isPet;
		}
		
		@Override
		public void run()
		{
			String res = null;
			try
			{
				res = onAggroRangeEnter(_npc, _pc, _isPet);
			}
			catch (Exception e)
			{
				showError(_pc, e);
			}
			showResult(_pc, res);
			
		}
	}
	
	public final boolean notifyAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		ThreadPoolManager.getInstance().executeAi(new TmpOnAggroEnter(npc, player, isPet));
		return true;
	}
	
	public final boolean notifyEnterZone(L2Character character, L2ZoneType zone)
	{
		L2PcInstance player = character.getActingPlayer();
		String res = null;
		try
		{
			res = onEnterZone(character, zone);
		}
		catch (Exception e)
		{
			if (player != null)
			{
				return showError(player, e);
			}
		}
		if (player != null)
		{
			return showResult(player, res);
		}
		return true;
	}
	
	public final boolean notifyExitZone(L2Character character, L2ZoneType zone)
	{
		L2PcInstance player = character.getActingPlayer();
		String res = null;
		try
		{
			res = onExitZone(character, zone);
		}
		catch (Exception e)
		{
			if (player != null)
			{
				return showError(player, e);
			}
		}
		if (player != null)
		{
			return showResult(player, res);
		}
		return true;
	}
	
	// these are methods that java calls to invoke scripts
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		return null;
	}
	
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		return onAttack(npc, attacker, damage, isPet);
	}
	
	public String onDeath(L2Character killer, L2Character victim, QuestState qs)
	{
		if (killer instanceof L2Npc)
		{
			return onAdvEvent("", (L2Npc) killer, qs.getPlayer());
		}
		return onAdvEvent("", null, qs.getPlayer());
	}
	
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// if not overridden by a subclass, then default to the returned value of the simpler (and older) onEvent override
		// if the player has a state, use it as parameter in the next call, else return null
		QuestState qs = player.getQuestState(getName());
		if (qs != null)
		{
			return onEvent(event, qs);
		}
		
		return null;
	}
	
	public String onEvent(String event, QuestState qs)
	{
		return null;
	}
	
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		return null;
	}
	
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		return null;
	}
	
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	public String onAcquireSkillList(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	public String onAcquireSkillInfo(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	public String onAcquireSkill(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		return null;
	}
	
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	public String onTrapAction(L2Trap trap, L2Character trigger, TrapAction action)
	{
		return null;
	}
	
	public String onSpawn(L2Npc npc)
	{
		return null;
	}
	
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		return null;
	}
	
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		return null;
	}
	
	public String onEnterWorld(L2PcInstance player)
	{
		return null;
	}
	
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		return null;
	}
	
	public String onExitZone(L2Character character, L2ZoneType zone)
	{
		return null;
	}
	
	/**
	 * Show message error to player who has an access level greater than 0
	 * @param player : L2PcInstance
	 * @param t : Throwable
	 * @return boolean
	 */
	public boolean showError(L2PcInstance player, Throwable t)
	{
		_log.log(Level.WARNING, getScriptFile().getAbsolutePath(), t);
		if (t.getMessage() == null)
		{
			t.printStackTrace();
		}
		if ((player != null) && player.getAccessLevel().isGm())
		{
			String res = "<html><body><title>Script error</title>" + Util.getStackTrace(t) + "</body></html>";
			return showResult(player, res);
		}
		return false;
	}
	
	/**
	 * Show a message to player.<BR>
	 * <BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :<BR>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
	 * <LI><U>"res" starts with "<html>" :</U> the message hold in "res" is shown in a dialog box</LI>
	 * <LI><U>otherwise :</U> the message held in "res" is shown in chat box</LI>
	 * @param qs : QuestState
	 * @param res : String pointing out the message to show at the player
	 * @return boolean
	 */
	public boolean showResult(L2PcInstance player, String res)
	{
		if ((res == null) || res.isEmpty() || (player == null))
		{
			return true;
		}
		
		if (res.endsWith(".htm") || res.endsWith(".html"))
		{
			showHtmlFile(player, res);
		}
		else if (res.startsWith("<html>"))
		{
			NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
			npcReply.setHtml(res);
			npcReply.replace("%playername%", player.getName());
			player.sendPacket(npcReply);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
		{
			player.sendMessage(res);
		}
		return false;
	}
	
	/**
	 * Add quests to the L2PCInstance of the player.<BR>
	 * <BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap _quest of L2PcInstance
	 * @param player : Player who is entering the world
	 */
	public final static void playerEnter(L2PcInstance player)
	{
		
		Connection con = null;
		try
		{
			// Get list of quests owned by the player from database
			con = L2DatabaseFactory.getInstance().getConnection();
			
			PreparedStatement invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE charId=? and name=?");
			PreparedStatement invalidQuestDataVar = con.prepareStatement("delete FROM character_quests WHERE charId=? and name=? and var=?");
			
			PreparedStatement statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE charId=? AND var=?");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, "<state>");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				
				// Get ID of the quest and ID of its state
				String questId = rs.getString("name");
				String statename = rs.getString("value");
				
				// Search quest associated with the ID
				Quest q = QuestManager.getInstance().getQuest(questId);
				if (q == null)
				{
					_log.finer("Unknown quest " + questId + " for player " + player.getName());
					if (Config.AUTODELETE_INVALID_QUEST_DATA)
					{
						invalidQuestData.setInt(1, player.getObjectId());
						invalidQuestData.setString(2, questId);
						invalidQuestData.executeUpdate();
					}
					continue;
				}
				
				// Create a new QuestState for the player that will be added to the player's list of quests
				new QuestState(q, player, State.getStateId(statename));
			}
			rs.close();
			invalidQuestData.close();
			statement.close();
			
			// Get list of quests owned by the player from the DB in order to add variables used in the quest.
			statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE charId=? AND var<>?");
			statement.setInt(1, player.getObjectId());
			statement.setString(2, "<state>");
			rs = statement.executeQuery();
			while (rs.next())
			{
				String questId = rs.getString("name");
				String var = rs.getString("var");
				String value = rs.getString("value");
				// Get the QuestState saved in the loop before
				QuestState qs = player.getQuestState(questId);
				if (qs == null)
				{
					_log.finer("Lost variable " + var + " in quest " + questId + " for player " + player.getName());
					if (Config.AUTODELETE_INVALID_QUEST_DATA)
					{
						invalidQuestDataVar.setInt(1, player.getObjectId());
						invalidQuestDataVar.setString(2, questId);
						invalidQuestDataVar.setString(3, var);
						invalidQuestDataVar.executeUpdate();
					}
					continue;
				}
				// Add parameter to the quest
				qs.setInternal(var, value);
			}
			rs.close();
			invalidQuestDataVar.close();
			statement.close();
			
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not insert char quest:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		// events
		for (String name : _allEventsS.keySet())
		{
			player.processQuestEvent(name, "enter");
		}
	}
	
	/**
	 * Insert (or Update) in the database variables that need to stay persistant for this quest after a reboot. This function is for storage of values that do not related to a specific player but are global for all characters. For example, if we need to disable a quest-gatekeeper until a certain
	 * time (as is done with some grand-boss gatekeepers), we can save that time in the DB.
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public final void saveGlobalQuestVar(String var, String value)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("REPLACE INTO quest_global_data (quest_name,var,value) VALUES (?,?,?)");
			statement.setString(1, getName());
			statement.setString(2, var);
			statement.setString(3, value);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not insert global quest variable:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Read from the database a previously saved variable for this quest. Due to performance considerations, this function should best be used only when the quest is first loaded. Subclasses of this class can define structures into which these loaded values can be saved. However, on-demand usage of
	 * this function throughout the script is not prohibited, only not recommended. Values read from this function were entered by calls to "saveGlobalQuestVar"
	 * @param var : String designating the name of the variable for the quest
	 * @return String : String representing the loaded value for the passed var, or an empty string if the var was invalid
	 */
	public final String loadGlobalQuestVar(String var)
	{
		String result = "";
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("SELECT value FROM quest_global_data WHERE quest_name = ? AND var = ?");
			statement.setString(1, getName());
			statement.setString(2, var);
			ResultSet rs = statement.executeQuery();
			if (rs.first())
			{
				result = rs.getString(1);
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not load global quest variable:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		return result;
	}
	
	/**
	 * Permanently delete from the database a global quest variable that was previously saved for this quest.
	 * @param var : String designating the name of the variable for the quest
	 */
	public final void deleteGlobalQuestVar(String var)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("DELETE FROM quest_global_data WHERE quest_name = ? AND var = ?");
			statement.setString(1, getName());
			statement.setString(2, var);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete global quest variable:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Permanently delete from the database all global quest variables that was previously saved for this quest.
	 */
	public final void deleteAllGlobalQuestVars()
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("DELETE FROM quest_global_data WHERE quest_name = ?");
			statement.setString(1, getName());
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete global quest variables:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Insert in the database the quest for the player.
	 * @param qs : QuestState pointing out the state of the quest
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public static void createQuestVarInDb(QuestState qs, String var, String value)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("INSERT INTO character_quests (charId,name,var,value) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE value=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuestName());
			statement.setString(3, var);
			statement.setString(4, value);
			statement.setString(5, value);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not insert char quest:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Update the value of the variable "var" for the quest.<BR>
	 * <BR>
	 * <U><I>Actions :</I></U><BR>
	 * The selection of the right record is made with :
	 * <LI>charId = qs.getPlayer().getObjectID()</LI>
	 * <LI>name = qs.getQuest().getName()</LI>
	 * <LI>var = var</LI> <BR>
	 * <BR>
	 * The modification made is :
	 * <LI>value = parameter value</LI>
	 * @param qs : Quest State
	 * @param var : String designating the name of the variable for quest
	 * @param value : String designating the value of the variable for quest
	 */
	public static void updateQuestVarInDb(QuestState qs, String var, String value)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("UPDATE character_quests SET value=? WHERE charId=? AND name=? AND var = ?");
			statement.setString(1, value);
			statement.setInt(2, qs.getPlayer().getObjectId());
			statement.setString(3, qs.getQuestName());
			statement.setString(4, var);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not update char quest:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Delete a variable of player's quest from the database.
	 * @param qs : object QuestState pointing out the player's quest
	 * @param var : String designating the variable characterizing the quest
	 */
	public static void deleteQuestVarInDb(QuestState qs, String var)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("DELETE FROM character_quests WHERE charId=? AND name=? AND var=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuestName());
			statement.setString(3, var);
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete char quest:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Delete the player's quest from database.
	 * @param qs : QuestState pointing out the player's quest
	 */
	public static void deleteQuestInDb(QuestState qs)
	{
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement;
			statement = con.prepareStatement("DELETE FROM character_quests WHERE charId=? AND name=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
			statement.setString(2, qs.getQuestName());
			statement.executeUpdate();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "could not delete char quest:", e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	/**
	 * Create a record in database for quest.<BR>
	 * <BR>
	 * <U><I>Actions :</I></U><BR>
	 * Use fucntion createQuestVarInDb() with following parameters :<BR>
	 * <LI>QuestState : parameter sq that puts in fields of database :
	 * <UL type="square">
	 * <LI>charId : ID of the player</LI>
	 * <LI>name : name of the quest</LI>
	 * </UL>
	 * </LI>
	 * <LI>var : string "&lt;state&gt;" as the name of the variable for the quest</LI>
	 * <LI>val : string corresponding at the ID of the state (in fact, initial state)</LI>
	 * @param qs : QuestState
	 */
	public static void createQuestInDb(QuestState qs)
	{
		createQuestVarInDb(qs, "<state>", State.getStateName(qs.getState()));
	}
	
	/**
	 * Update informations regarding quest in database.<BR>
	 * <U><I>Actions :</I></U><BR>
	 * <LI>Get ID state of the quest recorded in object qs</LI>
	 * <LI>Test if quest is completed. If true, add a star (*) before the ID state</LI>
	 * <LI>Save in database the ID state (with or without the star) for the variable called "&lt;state&gt;" of the quest</LI>
	 * @param qs : QuestState
	 */
	public static void updateQuestInDb(QuestState qs)
	{
		String val = State.getStateName(qs.getState());
		updateQuestVarInDb(qs, "<state>", val);
	}
	
	/**
	 * Return default html page "You are either not on a quest that involves this NPC.."
	 * @param player
	 * @return
	 */
	public static String getNoQuestMsg(L2PcInstance player)
	{
		final String result = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/noquest.htm");
		if ((result != null) && (result.length() > 0))
		{
			return result;
		}
		
		return DEFAULT_NO_QUEST_MSG;
	}
	
	/**
	 * Return default html page "This quest has already been completed."
	 * @param player
	 * @return
	 */
	public static String getAlreadyCompletedMsg(L2PcInstance player)
	{
		final String result = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/alreadycompleted.htm");
		if ((result != null) && (result.length() > 0))
		{
			return result;
		}
		
		return DEFAULT_ALREADY_COMPLETED_MSG;
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for the specified Event type.<BR>
	 * <BR>
	 * @param npcId : id of the NPC to register
	 * @param eventType : type of event being registered
	 * @return L2NpcTemplate : Npc Template corresponding to the npcId, or null if the id is invalid
	 */
	public L2NpcTemplate NpcEventId(int npcId, QuestEventType eventType)
	{
		try
		{
			L2NpcTemplate t = NpcTable.getInstance().getTemplate(npcId);
			if (t != null)
			{
				t.addQuestEvent(eventType, this);
			}
			return t;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addEventId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	private boolean _isCustom = false;
	
	/**
	 * If a quest is set as custom, it will display it's name in the NPC Quest List.<br>
	 * Retail quests are unhardcoded to display the name using a client string.
	 * @param val if {@code true} the quest script will be set as custom quest.
	 */
	public void setIsCustom(boolean val)
	{
		_isCustom = val;
	}
	
	/**
	 * @return {@code true} if the quest script is a custom quest, {@code false} otherwise.
	 */
	public boolean isCustomQuest()
	{
		return _isCustom;
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for the specified Event type.
	 * @param eventType type of event being registered
	 * @param npcIds NPC Ids to register
	 */
	public void EventId(QuestEventType eventType, Collection<Integer> npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(eventType, npcId);
		}
	}
	
	/**
	 * Add this quest to the list of quests that the passed mob will respond to for the specified Event type.
	 * @param eventType type of event being registered
	 * @param npcId NPC Id to register
	 */
	public void addEventId(QuestEventType eventType, int npcId)
	{
		try
		{
			final L2NpcTemplate t = NpcTable.getInstance().getTemplate(npcId);
			if (t != null)
			{
				t.addQuestEvent(eventType, this);
				_questInvolvedNpcs.add(npcId);
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addEventId(): " + e.getMessage(), e);
		}
	}
	
	public void EventId(QuestEventType eventType, int... npcIds)
	{
		for (int npcId : npcIds)
		{
			addEventId(eventType, npcId);
		}
	}
	
	// returns a random party member's L2PcInstance for the passed player's party
	// returns the passed player if he has no party.
	public L2PcInstance getRandomPartyMember(L2PcInstance player)
	{
		// NPE prevention. If the player is null, there is nothing to return
		if (player == null)
		{
			return null;
		}
		if ((player.getParty() == null) || (player.getParty().getPartyMembers().isEmpty()))
		{
			return player;
		}
		L2Party party = player.getParty();
		return party.getPartyMembers().get(Rnd.get(party.getPartyMembers().size()));
	}
	
	/**
	 * Auxilary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param player: the instance of a player whose party is to be searched
	 * @param value: the value of the "cond" variable that must be matched
	 * @return L2PcInstance: L2PcInstance for a random party member that matches the specified condition, or null if no match.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, String value)
	{
		return getRandomPartyMember(player, "cond", value);
	}
	
	/**
	 * Get a random party member from the specified player's party.<br>
	 * If the player is not in a party, only the player himself is checked.<br>
	 * The lucky member is chosen by standard loot roll rules -<br>
	 * each member rolls a random number, the one with the highest roll wins.
	 * @param player the player whose party to check
	 * @param npc the NPC used for distance and other checks (if {@link #checkPartyMember(L2PcInstance, L2Npc)} is overridden)
	 * @return the random party member or {@code null}
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, L2Npc npc)
	{
		if ((player == null) || !checkDistanceToTarget(player, npc))
		{
			return null;
		}
		final L2Party party = player.getParty();
		L2PcInstance luckyPlayer = null;
		if (party == null)
		{
			if (checkPartyMember(player, npc))
			{
				luckyPlayer = player;
			}
		}
		else
		{
			int highestRoll = 0;
			
			for (L2PcInstance member : party.getMembers())
			{
				final int rnd = getRandom(1000);
				
				if ((rnd > highestRoll) && checkPartyMember(member, npc))
				{
					highestRoll = rnd;
					luckyPlayer = member;
				}
			}
		}
		if ((luckyPlayer != null) && checkDistanceToTarget(luckyPlayer, npc))
		{
			return luckyPlayer;
		}
		return null;
	}
	
	public boolean checkPartyMember(L2PcInstance player, L2Npc npc)
	{
		return true;
	}
	
	/**
	 * Auxilary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param player: the instance of a player whose party is to be searched
	 * @param var/value: a tuple specifying a quest condition that must be satisfied for a party member to be considered.
	 * @return L2PcInstance: L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, any random party member is returned (i.e. no condition is applied). The party member must be within 1500 distance from the target of the reference
	 *         player, or if no target exists, 1500 distance from the player itself.
	 */
	public L2PcInstance getRandomPartyMember(L2PcInstance player, String var, String value)
	{
		// if no valid player instance is passed, there is nothing to check...
		if (player == null)
		{
			return null;
		}
		
		// for null var condition, return any random party member.
		if (var == null)
		{
			return getRandomPartyMember(player);
		}
		
		// normal cases...if the player is not in a party, check the player's state
		QuestState temp = null;
		L2Party party = player.getParty();
		// if this player is not in a party, just check if this player instance matches the conditions itself
		if ((party == null) || (party.getPartyMembers().isEmpty()))
		{
			temp = player.getQuestState(getName());
			if ((temp != null) && (temp.get(var) != null) && (temp.get(var)).equalsIgnoreCase(value))
			{
				return player; // match
			}
			
			return null; // no match
		}
		
		// if the player is in a party, gather a list of all matching party members (possibly
		// including this player)
		FastList<L2PcInstance> candidates = new FastList<>();
		
		// get the target for enforcing distance limitations.
		L2Object target = player.getTarget();
		if (target == null)
		{
			target = player;
		}
		
		for (L2PcInstance partyMember : party.getPartyMembers())
		{
			if (partyMember == null)
			{
				continue;
			}
			temp = partyMember.getQuestState(getName());
			if ((temp != null) && (temp.get(var) != null) && (temp.get(var)).equalsIgnoreCase(value) && partyMember.isInsideRadius(target, 1500, true, false))
			{
				candidates.add(partyMember);
			}
		}
		// if there was no match, return null...
		if (candidates.isEmpty())
		{
			return null;
		}
		
		// if a match was found from the party, return one of them at random.
		return candidates.get(Rnd.get(candidates.size()));
	}
	
	/**
	 * Auxilary function for party quests. Note: This function is only here because of how commonly it may be used by quest developers. For any variations on this function, the quest script can always handle things on its own
	 * @param player: the instance of a player whose party is to be searched
	 * @param state: the state in which the party member's queststate must be in order to be considered.
	 * @return L2PcInstance: L2PcInstance for a random party member that matches the specified condition, or null if no match. If the var is null, any random party member is returned (i.e. no condition is applied).
	 */
	public L2PcInstance getRandomPartyMemberState(L2PcInstance player, byte state)
	{
		// if no valid player instance is passed, there is nothing to check...
		if (player == null)
		{
			return null;
		}
		
		// normal cases...if the player is not in a partym check the player's state
		QuestState temp = null;
		L2Party party = player.getParty();
		// if this player is not in a party, just check if this player instance matches the conditions itself
		if ((party == null) || (party.getPartyMembers().isEmpty()))
		{
			temp = player.getQuestState(getName());
			if ((temp != null) && (temp.getState() == state))
			{
				return player; // match
			}
			
			return null; // no match
		}
		
		// if the player is in a party, gather a list of all matching party members (possibly
		// including this player)
		FastList<L2PcInstance> candidates = new FastList<>();
		
		// get the target for enforcing distance limitations.
		L2Object target = player.getTarget();
		if (target == null)
		{
			target = player;
		}
		
		for (L2PcInstance partyMember : party.getPartyMembers())
		{
			if (partyMember == null)
			{
				continue;
			}
			temp = partyMember.getQuestState(getName());
			if ((temp != null) && (temp.getState() == state) && partyMember.isInsideRadius(target, 1500, true, false))
			{
				candidates.add(partyMember);
			}
		}
		// if there was no match, return null...
		if (candidates.isEmpty())
		{
			return null;
		}
		
		// if a match was found from the party, return one of them at random.
		return candidates.get(Rnd.get(candidates.size()));
	}
	
	/**
	 * Get a random party member from the player's party who has this quest at the specified quest progress.<br>
	 * If the player is not in a party, only the player himself is checked.
	 * @param player the player whose random party member state to get
	 * @param condition the quest progress step the random member should be at (-1 = check only if quest is started)
	 * @param playerChance how many times more chance does the player get compared to other party members (3 - 3x more chance).<br>
	 *            On retail servers, the killer usually gets 2-3x more chance than other party members
	 * @param target the NPC to use for the distance check (can be null)
	 * @return the {@link QuestState} object of the random party member or {@code null} if none matched the condition
	 */
	public QuestState getRandomPartyMemberState(L2PcInstance player, int condition, int playerChance, L2Npc target)
	{
		if ((player == null) || (playerChance < 1))
		{
			return null;
		}
		
		QuestState qs = player.getQuestState(getName());
		if (!player.isInParty())
		{
			if (!checkPartyMemberConditions(qs, condition, target))
			{
				return null;
			}
			if (!checkDistanceToTarget(player, target))
			{
				return null;
			}
			return qs;
		}
		
		final List<QuestState> candidates = new ArrayList<>();
		if (checkPartyMemberConditions(qs, condition, target) && (playerChance > 0))
		{
			for (int i = 0; i < playerChance; i++)
			{
				candidates.add(qs);
			}
		}
		
		for (L2PcInstance member : player.getParty().getMembers())
		{
			if (member == player)
			{
				continue;
			}
			
			qs = member.getQuestState(getName());
			if (checkPartyMemberConditions(qs, condition, target))
			{
				candidates.add(qs);
			}
		}
		
		if (candidates.isEmpty())
		{
			return null;
		}
		
		qs = candidates.get(getRandom(candidates.size()));
		if (!checkDistanceToTarget(qs.getPlayer(), target))
		{
			return null;
		}
		return qs;
	}
	
	private boolean checkPartyMemberConditions(QuestState qs, int condition, L2Npc npc)
	{
		return ((qs != null) && ((condition == -1) ? qs.isStarted() : qs.isCond(condition)) && checkPartyMember(qs, npc));
	}
	
	private static boolean checkDistanceToTarget(L2PcInstance player, L2Npc target)
	{
		return ((target == null) || com.l2jserver.gameserver.util.Util.checkIfInRange(1500, player, target, true));
	}
	
	public boolean checkPartyMember(QuestState qs, L2Npc npc)
	{
		return true;
	}
	
	/**
	 * Show HTML file to client
	 * @param fileName
	 * @return String : message sent to client
	 */
	public String showHtmlFile(L2PcInstance player, String fileName)
	{
		boolean questwindow = true;
		if (fileName.endsWith(".html"))
		{
			questwindow = false;
		}
		int questId = getQuestIntId();
		// Create handler to file linked to the quest
		String content = getHtm(player.getHtmlPrefix(), fileName);
		
		if (player.getTarget() != null)
		{
			content = content.replaceAll("%objectId%", String.valueOf(player.getTarget().getObjectId()));
		}
		
		// Send message to client if message not empty
		if (content != null)
		{
			if (questwindow && (questId > 0) && (questId < 20000) && (questId != 999))
			{
				NpcQuestHtmlMessage npcReply = new NpcQuestHtmlMessage(5, questId);
				npcReply.setHtml(content);
				npcReply.replace("%playername%", player.getName());
				player.sendPacket(npcReply);
			}
			else
			{
				NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
				npcReply.setHtml(content);
				npcReply.replace("%playername%", player.getName());
				player.sendPacket(npcReply);
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		return content;
	}
	
	/**
	 * Return HTML file contents
	 * @param player
	 * @param fileName
	 * @return
	 */
	public String getHtm(String prefix, String fileName)
	{
		String content = HtmCache.getInstance().getHtm(prefix, "data/scripts/" + getDescr().toLowerCase() + "/" + getName() + "/" + fileName);
		
		if (content == null)
		{
			content = HtmCache.getInstance().getHtm(prefix, "data/scripts/quests/Q" + getName() + "/" + fileName);
			if (content == null)
			{
				content = HtmCache.getInstance().getHtmForce(prefix, "data/scripts/quests/" + getName() + "/" + fileName);
			}
		}
		
		return content;
	}
	
	/**
	 * Add a temporary (quest) spawn Return instance of newly spawned npc
	 */
	public L2Npc addSpawn(int npcId, L2Character cha)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0, false);
	}
	
	/**
	 * Add a temporary (quest) spawn Return instance of newly spawned npc with summon animation
	 */
	public L2Npc addSpawn(int npcId, L2Character cha, boolean isSummonSpawn)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0, isSummonSpawn);
	}
	
	public L2Npc addSpawn(int npcId, L2Character cha, boolean isSummonSpawn, long despawnDelay)
	{
		return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0, isSummonSpawn);
	}
	
	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffSet, long despawnDelay)
	{
		return addSpawn(npcId, x, y, z, heading, randomOffSet, despawnDelay, false);
	}
	
	public L2Npc addSpawn(int npcId, Location loc, boolean randomOffset, long despawnDelay, boolean isSummonSpawn)
	{
		return addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffset, despawnDelay, isSummonSpawn, 0);
	}
	
	public static L2Npc addSpawn(int npcId, Location pos, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instanceId)
	{
		return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, isSummonSpawn, instanceId);
	}
	
	public static L2Npc addSpawn(L2Npc summoner, int npcId, Location pos, boolean randomOffset, long despawnDelay)
	{
		return addSpawn(summoner, npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, false, 0);
	}
	
	public L2Npc addSpawn(int npcId, Location loc, boolean randomOffSet, long despawnDelay)
	{
		return addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffSet, despawnDelay, false, 0);
	}
	
	public static L2Npc addSpawn(int npcId, Location pos)
	{
		return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), false, 0, false, 0);
	}
	
	public static L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instanceId)
	{
		return addSpawn(null, npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, instanceId);
	}
	
	public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn)
	{
		return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, 0);
	}
	
	/**
	 * Add a temporary spawn of the specified NPC.
	 * @param summoner the NPC that requires this spawn
	 * @param npcId the ID of the NPC to spawn
	 * @param x the X coordinate of the spawn location
	 * @param y the Y coordinate of the spawn location
	 * @param z the Z coordinate (height) of the spawn location
	 * @param heading the heading of the NPC
	 * @param randomOffset if {@code true}, adds +/- 50~100 to X/Y coordinates of the spawn location
	 * @param despawnDelay time in milliseconds till the NPC is despawned (0 - only despawned on server shutdown)
	 * @param isSummonSpawn if {@code true}, displays a summon animation on NPC spawn
	 * @param instanceId the ID of the instance to spawn the NPC in (0 - the open world)
	 * @return the {@link L2Npc} object of the newly spawned NPC or {@code null} if the NPC doesn't exist
	 * @see #addSpawn(int, IPositionable, boolean, long, boolean, int)
	 * @see #addSpawn(int, int, int, int, int, boolean, long)
	 * @see #addSpawn(int, int, int, int, int, boolean, long, boolean)
	 */
	public static L2Npc addSpawn(L2Npc summoner, int npcId, int x, int y, int z, int heading, boolean randomOffset, long despawnDelay, boolean isSummonSpawn, int instanceId)
	{
		try
		{
			if ((x == 0) && (y == 0))
			{
				_log.info("addSpawn(): invalid spawn coordinates for NPC #" + npcId + "!");
				return null;
			}
			
			if (randomOffset)
			{
				int offset = Rnd.get(50, 100);
				if (Rnd.nextBoolean())
				{
					offset *= -1;
				}
				x += offset;
				
				offset = Rnd.get(50, 100);
				if (Rnd.nextBoolean())
				{
					offset *= -1;
				}
				y += offset;
			}
			
			final L2Spawn spawn = new L2Spawn(npcId);
			spawn.setInstanceId(instanceId);
			spawn.setHeading(heading);
			spawn.setXLocation(x);
			spawn.setYLocation(y);
			spawn.setZLocation(z);
			spawn.stopRespawn();
			
			final L2Npc npc = spawn.spawnOne(isSummonSpawn);
			if (despawnDelay > 0)
			{
				npc.scheduleDespawn(despawnDelay);
			}
			
			if (summoner != null)
			{
				summoner.addSummonedNpc(npc);
			}
			return npc;
		}
		catch (Exception e)
		{
			_log.info("Could not spawn NPC #" + npcId + "; error: " + e.getMessage());
		}
		
		return null;
	}
	
	public L2Trap addTrap(int trapId, int x, int y, int z, int heading, L2Skill skill, int instanceId)
	{
		L2NpcTemplate TrapTemplate = NpcTable.getInstance().getTemplate(trapId);
		L2Trap trap = new L2TrapInstance(IdFactory.getInstance().getNextId(), TrapTemplate, instanceId, -1, skill);
		trap.setCurrentHp(trap.getMaxHp());
		trap.setCurrentMp(trap.getMaxMp());
		trap.setIsInvul(true);
		trap.setHeading(heading);
		// L2World.getInstance().storeObject(trap);
		trap.spawnMe(x, y, z);
		
		return trap;
	}
	
	public L2Npc addMinion(L2MonsterInstance master, int minionId)
	{
		return MinionList.spawnMinion(master, minionId);
	}
	
	public int[] getRegisteredItemIds()
	{
		return questItemIds;
	}
	
	@Override
	public String getScriptName()
	{
		return getName();
	}
	
	@Override
	public void setActive(boolean status)
	{
		// TODO implement me
	}
	
	@Override
	public boolean reload()
	{
		unload();
		return super.reload();
	}
	
	@Override
	public boolean unload()
	{
		return unload(true);
	}
	
	public boolean unload(boolean removeFromList)
	{
		saveGlobalData();
		// cancel all pending timers before reloading.
		// if timers ought to be restarted, the quest can take care of it
		// with its code (example: save global data indicating what timer must
		// be restarted).
		for (FastList<QuestTimer> timers : _allEventTimers.values())
		{
			for (QuestTimer timer : timers)
			{
				timer.cancel();
			}
		}
		_allEventTimers.clear();
		if (removeFromList)
		{
			return QuestManager.getInstance().removeQuest(this);
		}
		return true;
	}
	
	@Override
	public ScriptManager<?> getScriptManager()
	{
		return QuestManager.getInstance();
	}
	
	public void setOnEnterWorld(boolean val)
	{
		_onEnterWorld = val;
	}
	
	public boolean getOnEnterWorld()
	{
		return _onEnterWorld;
	}
	
	public static int getRandom(int max)
	{
		return Rnd.get(max);
	}
	
	public L2PcInstance getRandomPartyMember(L2PcInstance player, int cond)
	{
		return getRandomPartyMember(player, "cond", String.valueOf(cond));
	}
	
	public void teleportPlayer(L2PcInstance player, Location loc, int instanceId)
	{
		teleportPlayer(player, loc, instanceId, true);
	}
	
	public void teleportPlayer(L2PcInstance player, Location loc, int instanceId, boolean allowRandomOffset)
	{
		loc.setInstanceId(instanceId);
		player.teleToLocation(loc, allowRandomOffset);
	}
	
	public static boolean hasQuestItems(L2PcInstance player, int itemId)
	{
		return player.getInventory().getItemByItemId(itemId) != null;
	}
	
	public boolean hasQuestItems(L2PcInstance player, int... itemIds)
	{
		final PcInventory inv = player.getInventory();
		for (int itemId : itemIds)
		{
			if (inv.getItemByItemId(itemId) == null)
			{
				return false;
			}
		}
		return true;
	}
	
	public L2DoorInstance getDoor(int doorId, int instanceId)
	{
		L2DoorInstance door = null;
		if (instanceId <= 0)
		{
			door = DoorTable.getInstance().getDoor(doorId);
		}
		else
		{
			final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
			if (inst != null)
			{
				door = inst.getDoor(doorId);
			}
		}
		return door;
	}
	
	public void openDoor(int doorId, int instanceId)
	{
		final L2DoorInstance door = getDoor(doorId, instanceId);
		if (door == null)
		{
			_log.log(Level.WARNING, getClass().getSimpleName() + ": called openDoor(" + doorId + ", " + instanceId + "); but door wasnt found!", new NullPointerException());
		}
		else if (!door.getOpen())
		{
			door.openMe();
		}
	}
	
	/**
	 * Closes the door if presents on the instance and its open
	 * @param doorId
	 * @param instanceId
	 */
	public void closeDoor(int doorId, int instanceId)
	{
		final L2DoorInstance door = getDoor(doorId, instanceId);
		if (door == null)
		{
			_log.log(Level.WARNING, getClass().getSimpleName() + ": called closeDoor(" + doorId + ", " + instanceId + "); but door wasnt found!", new NullPointerException());
		}
		else if (door.getOpen())
		{
			door.closeMe();
		}
	}
	
	/**
	 * @return the reset hour for a daily quest, could be overridden on a script.
	 */
	public int getResetHour()
	{
		return RESET_HOUR;
	}
	
	/**
	 * @return the reset minutes for a daily quest, could be overridden on a script.
	 */
	public int getResetMinutes()
	{
		return RESET_MINUTES;
	}
	
	/**
	 * Get a random integer from {@code min} (inclusive) to {@code max} (inclusive).<br>
	 * Use this method instead of importing {@link com.l2jserver.util.Rnd} utility.
	 * @param min the minimum value for randomization
	 * @param max the maximum value for randomization
	 * @return a random integer number from {@code min} to {@code max}
	 */
	public static int getRandom(int min, int max)
	{
		return Rnd.get(min, max);
	}
	
	/**
	 * @param item
	 * @param player
	 * @return
	 */
	public final boolean notifyItemTalk(L2ItemInstance item, L2PcInstance player)
	{
		String res = null;
		try
		{
			res = onItemTalk(item, player);
			if (res != null)
			{
				if (res.equalsIgnoreCase("true"))
				{
					return true;
				}
				else if (res.equalsIgnoreCase("false"))
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			return showError(player, e);
		}
		return showResult(player, res);
	}
	
	/**
	 * @param item
	 * @param player
	 * @return
	 */
	public String onItemTalk(L2ItemInstance item, L2PcInstance player)
	{
		return null;
	}
	
	/**
	 * @param npc the NPC that sees the creature
	 * @param creature the creature seen by the NPC
	 * @param isSummon
	 */
	public final void notifySeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		ThreadPoolManager.getInstance().executeAi(new SeeCreature(this, npc, creature, isSummon));
	}
	
	/**
	 * This function is called whenever a NPC "sees" a creature.
	 * @param npc the NPC who sees the creature
	 * @param creature the creature seen by the NPC
	 * @param isSummon this parameter if it's {@code false} it denotes that the character seen by the NPC was indeed the player, else it specifies that the character was the player's summon
	 * @return
	 */
	public String onSeeCreature(L2Npc npc, L2Character creature, boolean isSummon)
	{
		return null;
	}
	
	/**
	 * Take an amount of a specified item from player's inventory.
	 * @param player the player whose item to take
	 * @param itemId the Id of the item to take
	 * @param amount the amount to take
	 * @return {@code true} if any items were taken, {@code false} otherwise
	 */
	public static boolean takeItems(L2PcInstance player, int itemId, long amount)
	{
		// Get object item from player's inventory list
		L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
		if (item == null)
		{
			return false;
		}
		
		// Tests on count value in order not to have negative value
		if ((amount < 0) || (amount > item.getCount()))
		{
			amount = item.getCount();
		}
		
		// Destroy the quantity of items wanted
		if (item.isEquipped())
		{
			L2ItemInstance[] unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(item.getItem().getBodyPart());
			InventoryUpdate iu = new InventoryUpdate();
			for (L2ItemInstance itm : unequiped)
			{
				iu.addModifiedItem(itm);
			}
			player.sendPacket(iu);
			player.broadcastUserInfo();
		}
		return player.destroyItemByItemId("Quest", itemId, amount, player, true);
	}
	
	/**
	 * Take an amount of all specified items from player's inventory.
	 * @param player the player whose items to take
	 * @param amount the amount to take of each item
	 * @param itemIds a list or an array of Ids of the items to take
	 * @return {@code true} if all items were taken, {@code false} otherwise
	 */
	public boolean takeItems(L2PcInstance player, int amount, int... itemIds)
	{
		boolean check = true;
		if (itemIds != null)
		{
			for (int item : itemIds)
			{
				check &= takeItems(player, item, amount);
			}
		}
		return check;
	}
	
	/**
	 * Give item/reward to the player
	 * @param player
	 * @param itemId
	 * @param count
	 */
	public static void giveItems(L2PcInstance player, int itemId, long count)
	{
		giveItems(player, itemId, count, 0);
	}
	
	/**
	 * Gives an item to the player
	 * @param player
	 * @param item
	 * @param limit the maximum amount of items the player can have. Won't give more if this limit is reached.
	 * @return <code>true</code> if at least one item was given to the player, <code>false</code> otherwise
	 */
	protected static boolean giveItems(L2PcInstance player, ItemHolder item, long limit)
	{
		long maxToGive = limit - player.getInventory().getInventoryItemCount(item.getId(), -1);
		if (maxToGive <= 0)
		{
			return false;
		}
		giveItems(player, item.getId(), Math.min(maxToGive, item.getCount()));
		return true;
	}
	
	protected static boolean giveItems(L2PcInstance player, ItemHolder item, long limit, boolean playSound)
	{
		boolean drop = giveItems(player, item, limit);
		if (drop && playSound)
		{
			playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
		}
		return drop;
	}
	
	/**
	 * @param player
	 * @param itemId
	 * @param count
	 * @param enchantlevel
	 */
	public static void giveItems(L2PcInstance player, int itemId, long count, int enchantlevel)
	{
		if (count <= 0)
		{
			return;
		}
		
		// If item for reward is adena (Id=57), modify count with rate for quest reward if rates available
		if ((itemId == PcInventory.ADENA_ID) && (enchantlevel == 0))
		{
			count = (long) (count * Config.RATE_QUEST_REWARD_ADENA);
		}
		
		// Add items to player's inventory
		L2ItemInstance item = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
		if (item == null)
		{
			return;
		}
		
		// set enchant level for item if that item is not adena
		if ((enchantlevel > 0) && (itemId != PcInventory.ADENA_ID))
		{
			item.setEnchantLevel(enchantlevel);
		}
		
		sendItemGetMessage(player, item, count);
	}
	
	/**
	 * Send the system message and the status update packets to the player.
	 * @param player the player that has got the item
	 * @param item the item obtain by the player
	 * @param count the item count
	 */
	private static void sendItemGetMessage(L2PcInstance player, L2ItemInstance item, long count)
	{
		// If item for reward is gold, send message of gold reward to client
		if (item.getItemId() == PcInventory.ADENA_ID)
		{
			SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S1_ADENA);
			smsg.addItemNumber(count);
			player.sendPacket(smsg);
		}
		// Otherwise, send message of object reward to client
		else
		{
			if (count > 1)
			{
				SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
				smsg.addItemName(item);
				smsg.addItemNumber(count);
				player.sendPacket(smsg);
			}
			else
			{
				SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
				smsg.addItemName(item);
				player.sendPacket(smsg);
			}
		}
		// send packets
		StatusUpdate su = new StatusUpdate(player);
		su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
		player.sendPacket(su);
	}
	
	/**
	 * Get a random boolean.
	 * @return {@code true} or {@code false} randomly
	 */
	public static boolean getRandomBoolean()
	{
		return Rnd.nextBoolean();
	}
	
	/**
	 * @param npc
	 */
	public final void notifyRouteFinished(L2Npc npc)
	{
		try
		{
			onRouteFinished(npc);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onRouteFinished() in notifyRouteFinished(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * @param npc
	 */
	public final void notifyMoveFinished(L2Npc npc)
	{
		try
		{
			onMoveFinished(npc);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onMoveFinished() in notifyMoveFinished(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * @param npc
	 */
	public final void notifyNodeArrived(L2Npc npc)
	{
		try
		{
			onNodeArrived(npc);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onNodeArrived() in notifyNodeArrived(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * This function is called whenever a walker NPC (controlled by WalkingManager) arrive to last node
	 * @param npc registered NPC
	 */
	public void onRouteFinished(L2Npc npc)
	{
		
	}
	
	/**
	 * This function is called whenever a NPC finishes moving
	 * @param npc registered NPC
	 */
	public void onMoveFinished(L2Npc npc)
	{
		
	}
	
	/**
	 * This function is called whenever a walker NPC (controlled by WalkingManager) arrive a walking node
	 * @param npc registered NPC
	 */
	public void onNodeArrived(L2Npc npc)
	{
		
	}
	
	public String onEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		return null;
	}
	
	/**
	 * @param eventName - name of event
	 * @param sender - NPC, who sent event
	 * @param receiver - NPC, who received event
	 * @param reference - L2Object to pass, if needed
	 */
	public final void notifyEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		try
		{
			onEventReceived(eventName, sender, receiver, reference);
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on onEventReceived() in notifyEventReceived(): " + e.getMessage(), e);
		}
	}
	
	/**
	 * Register onEventReceived trigger for NPC
	 * @param npcIds
	 */
	public void addEventReceivedId(int... npcIds)
	{
		EventId(QuestEventType.ON_EVENT_RECEIVED, npcIds);
	}
	
	/**
	 * Register onEventReceived trigger for NPC
	 * @param npcIds
	 */
	public void addEventReceivedId(Collection<Integer> npcIds)
	{
		EventId(QuestEventType.ON_EVENT_RECEIVED, npcIds);
	}
	
	/**
	 * Get the specified player's {@link QuestState} object for this quest.<br>
	 * If the player does not have it and initIfNode is {@code true},<br>
	 * create a new QuestState object and return it, otherwise return {@code null}.
	 * @param player the player whose QuestState to get
	 * @param initIfNone if true and the player does not have a QuestState for this quest,<br>
	 *            create a new QuestState
	 * @return the QuestState object for this quest or null if it doesn't exist
	 */
	public QuestState getQuestState(L2PcInstance player, boolean initIfNone)
	{
		final QuestState qs = player.getQuestState(_name);
		if ((qs != null) || !initIfNone)
		{
			return qs;
		}
		return newQuestState(player);
	}
	
	public static void rewardItems(L2PcInstance player, int itemId, long count)
	{
		if (count <= 0)
		{
			return;
		}
		
		final L2Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item == null)
		{
			return;
		}
		
		try
		{
			if (itemId == 57)
			{
				count *= Config.RATE_QUEST_REWARD_ADENA;
			}
			else if (Config.RATE_QUEST_REWARD_USE_MULTIPLIERS)
			{
				if (item instanceof L2EtcItem)
				{
					switch (((L2EtcItem) item).getItemType())
					{
						case POTION:
							count *= Config.RATE_QUEST_REWARD_POTION;
							break;
						case SCRL_ENCHANT_WP:
						case SCRL_ENCHANT_AM:
						case SCROLL:
							count *= Config.RATE_QUEST_REWARD_SCROLL;
							break;
						case RECIPE:
							count *= Config.RATE_QUEST_REWARD_RECIPE;
							break;
						case MATERIAL:
							count *= Config.RATE_QUEST_REWARD_MATERIAL;
							break;
						default:
							count *= Config.RATE_QUEST_REWARD;
					}
				}
			}
			else
			{
				count *= Config.RATE_QUEST_REWARD;
			}
		}
		catch (Exception e)
		{
			count = Long.MAX_VALUE;
		}
		
		// Add items to player's inventory
		final L2ItemInstance itemInstance = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
		if (itemInstance == null)
		{
			return;
		}
		
		sendItemGetMessage(player, itemInstance, count);
	}
	
	public L2Npc spawnNpc(int npcId, Location loc, int heading, int instId)
	{
		L2NpcTemplate npcTemplate = NpcTable.getInstance().getTemplate(npcId);
		Instance inst = InstanceManager.getInstance().getInstance(instId);
		try
		{
			L2Spawn npcSpawn = new L2Spawn(npcTemplate);
			npcSpawn.setXLocation(loc.getX());
			npcSpawn.setYLocation(loc.getY());
			npcSpawn.setZLocation(loc.getZ());
			npcSpawn.setHeading(loc.getHeading());
			npcSpawn.setAmount(1);
			npcSpawn.setInstanceId(instId);
			SpawnTable.getInstance().addNewSpawn(npcSpawn, false);
			L2Npc npc = npcSpawn.spawnOne(false);
			inst.addNpc(npc);
			return npc;
		}
		catch (Exception ignored)
		{
			_log.info("Could not spawnNpc(int, int, int)");
		}
		return null;
	}
	
	/**
	 * Executes a procedure for each player, depending on the parameters.
	 * @param player the player were the procedure will be executed
	 * @param npc the related Npc
	 * @param isSummon {@code true} if the event that call this method was originated by the player's summon
	 * @param includeParty if {@code true} #actionForEachPlayer(L2PcInstance, L2Npc, boolean) will be called with the player's party members
	 * @param includeCommandChannel if {@code true} {@link #actionForEachPlayer(L2PcInstance, L2Npc, boolean)} will be called with the player's command channel members
	 * @see #actionForEachPlayer(L2PcInstance, L2Npc, boolean)
	 */
	public final void executeForEachPlayer(L2PcInstance player, final L2Npc npc, final boolean isSummon, boolean includeParty, boolean includeCommandChannel)
	{
		if ((includeParty || includeCommandChannel) && player.isInParty())
		{
			if (includeCommandChannel && player.getParty().isInCommandChannel())
			{
				player.getParty().getCommandChannel().forEachMember(member ->
				{
					actionForEachPlayer(member, npc, isSummon);
					return true;
				});
			}
			else if (includeParty)
			{
				player.getParty().forEachMember(member ->
				{
					actionForEachPlayer(member, npc, isSummon);
					return true;
				});
			}
		}
		else
		{
			actionForEachPlayer(player, npc, isSummon);
		}
	}
	
	/**
	 * Overridable method called from {@link #executeForEachPlayer(L2PcInstance, L2Npc, boolean, boolean, boolean)}
	 * @param player the player where the action will be run
	 * @param npc the Npc related to this action
	 * @param isSummon {@code true} if the event that call this method was originated by the player's summon
	 */
	public void actionForEachPlayer(L2PcInstance player, L2Npc npc, boolean isSummon)
	{
		// To be overridden in quest scripts.
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param player the player whose inventory to check for quest items
	 * @param itemIds a list of item Ids to check for
	 * @return {@code true} if at least one items exist in player's inventory, {@code false} otherwise
	 */
	public boolean hasAtLeastOneQuestItem(L2PcInstance player, int... itemIds)
	{
		final PcInventory inv = player.getInventory();
		for (int itemId : itemIds)
		{
			if (inv.getItemByItemId(itemId) != null)
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean giveItemRandomly(L2PcInstance player, L2Npc npc, int itemId, long amountToGive, long limit, double dropChance, boolean playSound)
	{
		return giveItemRandomly(player, npc, itemId, amountToGive, amountToGive, limit, dropChance, playSound);
	}
	
	public static boolean giveItemRandomly(L2PcInstance player, int itemId, long amountToGive, long limit, double dropChance, boolean playSound)
	{
		return giveItemRandomly(player, null, itemId, amountToGive, amountToGive, limit, dropChance, playSound);
	}
	
	public static boolean giveItemRandomly(L2PcInstance player, L2Npc npc, int itemId, long minAmount, long maxAmount, long limit, double dropChance, boolean playSound)
	{
		final long currentCount = getQuestItemsCount(player, itemId);
		
		if ((limit > 0) && (currentCount >= limit))
		{
			return true;
		}
		
		minAmount *= Config.RATE_QUEST_DROP;
		maxAmount *= Config.RATE_QUEST_DROP;
		dropChance *= Config.RATE_QUEST_DROP;
		if ((npc != null) && Config.CHAMPION_ENABLE && npc.isChampion())
		{
			dropChance *= ChampionData.getInstance().getRewardMultipler((L2Attackable) npc);
			if ((itemId == 57) || (itemId == 5575))
			{
				minAmount *= ChampionData.getInstance().getAdenaMultipler((L2Attackable) npc);
				maxAmount *= ChampionData.getInstance().getAdenaMultipler((L2Attackable) npc);
			}
			else
			{
				minAmount *= ChampionData.getInstance().getRewardMultipler((L2Attackable) npc);
				maxAmount *= ChampionData.getInstance().getRewardMultipler((L2Attackable) npc);
			}
			
		}
		
		long amountToGive = ((minAmount == maxAmount) ? minAmount : Rnd.get(minAmount, maxAmount));
		final double random = Rnd.nextDouble();
		// Inventory slot check (almost useless for non-stacking items)
		if ((dropChance >= random) && (amountToGive > 0) && player.getInventory().validateCapacityByItemId(itemId))
		{
			if ((limit > 0) && ((currentCount + amountToGive) > limit))
			{
				amountToGive = limit - currentCount;
			}
			
			// Give the item to player
			L2ItemInstance item = player.addItem("Quest", itemId, amountToGive, npc, true);
			if (item != null)
			{
				// limit reached (if there is no limit, this block doesn't execute)
				if ((currentCount + amountToGive) == limit)
				{
					if (playSound)
					{
						playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
					}
					return true;
				}
				
				if (playSound)
				{
					playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
				}
				// if there is no limit, return true every time an item is given
				if (limit <= 0)
				{
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Get the amount of an item in player's inventory.
	 * @param player the player whose inventory to check
	 * @param itemId the Id of the item whose amount to get
	 * @return the amount of the specified item in player's inventory
	 */
	public static long getQuestItemsCount(L2PcInstance player, int itemId)
	{
		return player.getInventory().getInventoryItemCount(itemId, -1);
	}
	
	/**
	 * Get the total amount of all specified items in player's inventory.
	 * @param player the player whose inventory to check
	 * @param itemIds a list of IDs of items whose amount to get
	 * @return the summary amount of all listed items in player's inventory
	 */
	public long getQuestItemsCount(L2PcInstance player, int... itemIds)
	{
		long count = 0;
		for (L2ItemInstance item : player.getInventory().getItems())
		{
			if (item == null)
			{
				continue;
			}
			
			for (int itemId : itemIds)
			{
				if (item.getItemId() == itemId)
				{
					if ((count + item.getCount()) > Long.MAX_VALUE)
					{
						return Long.MAX_VALUE;
					}
					count += item.getCount();
				}
			}
		}
		return count;
	}
	
	public static void addExpAndSp(L2PcInstance player, long exp, int sp)
	{
		player.addExpAndSp((long) player.calcStat(Stats.EXPSP_RATE, exp * Config.RATE_QUEST_REWARD_XP, null, null), (int) player.calcStat(Stats.EXPSP_RATE, sp * Config.RATE_QUEST_REWARD_SP, null, null));
	}
	
	public static void giveAdena(L2PcInstance player, long count, boolean applyRates)
	{
		if (applyRates)
		{
			rewardItems(player, Inventory.ADENA_ID, count);
		}
		else
		{
			giveItems(player, Inventory.ADENA_ID, count);
		}
	}
	
	/**
	 * Give item/reward to the player
	 * @param player
	 * @param holder
	 */
	protected static void giveItems(L2PcInstance player, ItemHolder holder)
	{
		giveItems(player, holder.getId(), holder.getCount());
	}
	
	/**
	 * Check if the player has the specified item in his inventory.
	 * @param player the player whose inventory to check for the specified item
	 * @param item the {@link ItemHolder} object containing the ID and count of the item to check
	 * @return {@code true} if the player has the required count of the item
	 */
	protected static boolean hasItem(L2PcInstance player, ItemHolder item)
	{
		return hasItem(player, item, true);
	}
	
	/**
	 * Check if the player has the required count of the specified item in his inventory.
	 * @param player the player whose inventory to check for the specified item
	 * @param item the {@link ItemHolder} object containing the ID and count of the item to check
	 * @param checkCount if {@code true}, check if each item is at least of the count specified in the ItemHolder,<br>
	 *            otherwise check only if the player has the item at all
	 * @return {@code true} if the player has the item
	 */
	protected static boolean hasItem(L2PcInstance player, ItemHolder item, boolean checkCount)
	{
		if (item == null)
		{
			return false;
		}
		if (checkCount)
		{
			return (getQuestItemsCount(player, item.getId()) >= item.getCount());
		}
		return hasQuestItems(player, item.getId());
	}
	
	/**
	 * Take a set amount of a specified item from player's inventory.
	 * @param player the player whose item to take
	 * @param holder the {@link ItemHolder} object containing the ID and count of the item to take
	 * @return {@code true} if the item was taken, {@code false} otherwise
	 */
	protected static boolean takeItem(L2PcInstance player, ItemHolder holder)
	{
		if (holder == null)
		{
			return false;
		}
		return takeItems(player, holder.getId(), holder.getCount());
	}
	
	/**
	 * Take a set amount of all specified items from player's inventory.
	 * @param player the player whose items to take
	 * @param itemList the list of {@link ItemHolder} objects containing the IDs and counts of the items to take
	 * @return {@code true} if all items were taken, {@code false} otherwise
	 */
	protected static boolean takeAllItems(L2PcInstance player, ItemHolder... itemList)
	{
		if ((itemList == null) || (itemList.length == 0))
		{
			return false;
		}
		// first check if the player has all items to avoid taking half the items from the list
		if (!hasAllItems(player, true, itemList))
		{
			return false;
		}
		for (ItemHolder item : itemList)
		{
			// this should never be false, but just in case
			if (!takeItem(player, item))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check if the player has all the specified items in his inventory and, if necessary, if their count is also as required.
	 * @param player the player whose inventory to check for the specified item
	 * @param checkCount if {@code true}, check if each item is at least of the count specified in the ItemHolder,<br>
	 *            otherwise check only if the player has the item at all
	 * @param itemList a list of {@link ItemHolder} objects containing the IDs of the items to check
	 * @return {@code true} if the player has all the items from the list
	 */
	protected static boolean hasAllItems(L2PcInstance player, boolean checkCount, ItemHolder... itemList)
	{
		if ((itemList == null) || (itemList.length == 0))
		{
			return false;
		}
		for (ItemHolder item : itemList)
		{
			if (!hasItem(player, item, checkCount))
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Give a reward to player using multipliers.
	 * @param player the player to whom to give the item
	 * @param holder
	 */
	public static void rewardItems(L2PcInstance player, ItemHolder holder)
	{
		rewardItems(player, holder.getId(), holder.getCount());
	}
	
	/**
	 * Instantly cast a skill upon the given target.
	 * @param npc the caster NPC
	 * @param target the target of the cast
	 * @param skill the skill to cast
	 */
	protected void castSkill(L2Npc npc, L2Playable target, L2Skill skill)
	{
		npc.setTarget(target);
		npc.doCast(skill);
	}
	
	// TODO: move to Until
	public static boolean calcChance(int max)
	{
		return Rnd.get(100) < max;
	}
	
	public static boolean calcChanceHigh(int max)
	{
		return Rnd.get(1000000) < max;
	}
	
	public void registerQuestItems(int... items)
	{
		questItemIds = items;
	}
	
	public void StartNpcs(int... npcIds)
	{
		EventId(QuestEventType.QUEST_START, npcIds);
	}
	
	public void FirstTalkNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_FIRST_TALK, npcIds);
	}
	
	public void AttackNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_ATTACK, npcIds);
	}
	
	public void KillNpcs(Collection<Integer> killIds)
	{
		EventId(QuestEventType.ON_KILL, killIds);
	}
	
	public void KillNpcs(int... killIds)
	{
		EventId(QuestEventType.ON_KILL, killIds);
	}
	
	public void TalkNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_TALK, npcIds);
	}
	
	public void TalkNpcs(Collection<Integer> npcIds)
	{
		EventId(QuestEventType.ON_TALK, npcIds);
	}
	
	public void SpellFinishedNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_SPELL_FINISHED, npcIds);
	}
	
	public void SkillSeeNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_SKILL_SEE, npcIds);
	}
	
	public void AggroRangeEnterNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_AGGRO_RANGE_ENTER, npcIds);
	}
	
	public void FactionCallNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_FACTION_CALL, npcIds);
	}
	
	public void SpawnNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_SPAWN, npcIds);
	}
	
	public void SeeCreatureNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_SEE_CREATURE, npcIds);
	}
	
	public L2NpcTemplate addAcquireSkillId(int npcId)
	{
		return NpcEventId(npcId, Quest.QuestEventType.ON_SKILL_LEARN);
	}
	
	public L2NpcTemplate addTrapActionId(int trapId)
	{
		return NpcEventId(trapId, Quest.QuestEventType.ON_TRAP_ACTION);
	}
	
	public void RouteFinishedNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_ROUTE_FINISHED, npcIds);
	}
	
	public void RouteFinishedNpcs(Collection<Integer> npcIds)
	{
		EventId(QuestEventType.ON_ROUTE_FINISHED, npcIds);
	}
	
	public void MoveFinishedNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_MOVE_FINISHED, npcIds);
	}
	
	public void MoveFinishedNpcs(Collection<Integer> npcIds)
	{
		EventId(QuestEventType.ON_MOVE_FINISHED, npcIds);
	}
	
	public void NodeArrivedNpcs(int... npcIds)
	{
		EventId(QuestEventType.ON_NODE_ARRIVED, npcIds);
	}
	
	public void NodeArrivedNpcs(Collection<Integer> npcIds)
	{
		EventId(QuestEventType.ON_NODE_ARRIVED, npcIds);
	}
	
	public L2ZoneType EnterZoneId(int zoneId)
	{
		try
		{
			L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
			if (zone != null)
			{
				zone.addQuestEvent(Quest.QuestEventType.ON_ENTER_ZONE, this);
			}
			return zone;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addEnterZoneId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	public L2ZoneType ExitZoneId(int zoneId)
	{
		try
		{
			L2ZoneType zone = ZoneManager.getInstance().getZoneById(zoneId);
			if (zone != null)
			{
				zone.addQuestEvent(Quest.QuestEventType.ON_EXIT_ZONE, this);
			}
			return zone;
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on addExitZoneId(): " + e.getMessage(), e);
			return null;
		}
	}
	
	public Set<Integer> getQuestInvolvedNpcs()
	{
		return _questInvolvedNpcs;
	}
	
	public Map<String, FastList<QuestTimer>> getQuestTimers()
	{
		return _allEventTimers;
	}
}
