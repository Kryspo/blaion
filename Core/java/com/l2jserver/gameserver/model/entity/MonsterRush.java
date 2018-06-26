package com.l2jserver.gameserver.model.entity;

import com.l2jserver.Config;
import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.util.Rnd;

import javolution.util.FastSet;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class MonsterRush
{
	public static boolean isAdminSummoning = true;
	
	public enum Status
	{
		INACTIVE,
		STARTED,
		REGISTER,
		TELEPORT,
		REWARDING
	}
	
	public static FastSet<L2PcInstance> _participants = new FastSet<L2PcInstance>();
	public static FastSet<L2Npc> _monsters = new FastSet<L2Npc>();
	public static Status _status = Status.INACTIVE;
	
	protected static L2Npc _lord, _defender_1, _defender_2, _defender_3, _defender_4, _defender_5, _defender_6, _defender_7, _defender_8, _defender_9, _defender_10;
	
	public static int _wave = 1;
	
	public static int getParticipatingPlayers()
	{
		return _participants.size();
	}
	
	protected static void MonstersWave(final int _waveNum)
	{
		if (_status == Status.INACTIVE)
		{
			return;
		}
		
		switch (_waveNum)
		{
			case 2:
				Announcements.getInstance().announceToAll("Monster Rush: First wave has ended. Second wave is coming soon.");
				break;
			case 3:
				Announcements.getInstance().announceToAll("Monster Rush: Second wave has ended. Last wave is coming soon.");
				break;
		}
		
		L2Npc mobas = null;
		int[][] wave = MonsterRushTemplates._wave1Mons_fakes;
		
		switch (_waveNum)
		{
			case 2:
				wave = MonsterRushTemplates._wave2Mons_fakes;
				break;
			case 3:
				wave = MonsterRushTemplates._wave3Mons_fakes;
				break;
		}
		
		for (int i = 0; i <= (wave[0].length - 1); i++)
		{
			for (int a = 1; a <= wave[1][i]; a++)
			{
				for (int r = 0; r <= (MonsterRushTemplates._CoordsX.length - 1); r++)
				{
					mobas = addSpawn(wave[0][i], MonsterRushTemplates._CoordsX[r] + Rnd.get(-100, 100), MonsterRushTemplates._CoordsY[r] + Rnd.get(-100, 100), MonsterRushTemplates._CoordsZ[r]);
					mobas.getKnownList().addKnownObject(_lord);
					_monsters.add(mobas);
				}
			}
		}
		
		for (int i = 0; i <= (MonsterRushTemplates._miniIntervals.length - 1); i++)
		{
			ThreadPoolManager.getInstance().scheduleGeneral(() -> miniWave(_waveNum), MonsterRushTemplates._miniIntervals[i]);
		}
		
	}
	
	public static void endEvent()
	{
		_status = Status.INACTIVE;
		for (L2Npc monster : _monsters)
		{
			monster.onDecay();
			monster.deleteMe();
		}
		_monsters.clear();
		if (L2World.getInstance().findObject(_defender_1.getObjectId()) != null)
		{
			_defender_1.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_2.getObjectId()) != null)
		{
			_defender_2.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_3.getObjectId()) != null)
		{
			_defender_3.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_4.getObjectId()) != null)
		{
			_defender_4.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_5.getObjectId()) != null)
		{
			_defender_5.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_6.getObjectId()) != null)
		{
			_defender_6.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_7.getObjectId()) != null)
		{
			_defender_7.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_8.getObjectId()) != null)
		{
			_defender_8.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_9.getObjectId()) != null)
		{
			_defender_9.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_10.getObjectId()) != null)
		{
			_defender_10.deleteMe();
		}
		
		for (L2PcInstance player : _participants)
		{
			player.setTeam(0);
		}
		
		if (L2World.getInstance().findObject(_lord.getObjectId()) == null)
		{
			Announcements.getInstance().announceToAll("Monster Rush: Lord was not protected!");
			Announcements.getInstance().announceToAll("Monster Rush: Teleporting players back to town.");
			Announcements.getInstance().announceToAll("Monster Rush: Event has ended.");
			_lord.deleteMe();
			synchronized (_participants)
			{
				for (L2PcInstance player : _participants)
				{
					player.setIsInMonsterRush(false);
					_participants.clear();
				}
			}
		}
		else if (L2World.getInstance().findObject(_lord.getObjectId()) != null)
		{
			Announcements.getInstance().announceToAll("Monster Rush: Lord was protected!");
			Announcements.getInstance().announceToAll("Monster Rush: Teleporting players back to town.");
			Announcements.getInstance().announceToAll("Monster Rush: Event has ended.");
			_lord.deleteMe();
			synchronized (_participants)
			{
				for (L2PcInstance player : _participants)
				{
					_lord.broadcastPacket(new CreatureSay(_lord.getObjectId(), 1, _lord.getName(), "Thanks you for help."));
					_lord.broadcastPacket(new CreatureSay(_lord.getObjectId(), 1, _lord.getName(), "For your courage I give you a reward, check your inventory."));
					
					player.addItem("MonsterRush Event", Config.MRUSH_REWARD_ITEM, Config.MRUSH_REWARD_AMOUNT, player, true);
					Announcements.getInstance().announceToAll("Monster Rush: Players rewarded.");
					player.getInventory().updateDatabase();
					player.setIsInMonsterRush(false);
					_participants.clear();
				}
			}
		}
	}
	
	public static void cancelEvent()
	{
		_status = Status.INACTIVE;
	}
	
	public static void abortEvent()
	{
		_status = Status.INACTIVE;
		synchronized (_participants)
		{
			for (L2PcInstance player : _participants)
			{
				player.teleToLocation(MonsterRushTemplates.LORD_X, MonsterRushTemplates.LORD_Y, MonsterRushTemplates.LORD_Z, true);
				player.setTeam(0);
				player.setIsInMonsterRush(false);
			}
		}
		for (L2Npc monster : _monsters)
		{
			monster.onDecay();
			monster.deleteMe();
		}
		_monsters.clear();
		if (L2World.getInstance().findObject(_lord.getObjectId()) != null)
		{
			_lord.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_1.getObjectId()) != null)
		{
			_defender_1.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_2.getObjectId()) != null)
		{
			_defender_2.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_3.getObjectId()) != null)
		{
			_defender_3.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_4.getObjectId()) != null)
		{
			_defender_4.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_5.getObjectId()) != null)
		{
			_defender_5.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_6.getObjectId()) != null)
		{
			_defender_6.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_7.getObjectId()) != null)
		{
			_defender_7.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_8.getObjectId()) != null)
		{
			_defender_8.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_9.getObjectId()) != null)
		{
			_defender_9.deleteMe();
		}
		if (L2World.getInstance().findObject(_defender_10.getObjectId()) != null)
		{
			_defender_10.deleteMe();
		}
		_participants.clear();
	}
	
	protected static void miniWave(int _waveNum)
	{
		if (_status == Status.INACTIVE)
		{
			return;
		}
		
		int[][] wave = MonsterRushTemplates._wave1Mons_fakes;
		
		switch (_waveNum)
		{
			case 2:
				wave = MonsterRushTemplates._wave2Mons_fakes;
				break;
			case 3:
				wave = MonsterRushTemplates._wave3Mons_fakes;
				break;
		}
		
		L2Npc mobas = null;
		for (int i = 0; i <= (wave[0].length - 1); i++)
		{
			for (int a = 1; a <= Math.round(wave[1][i] * 0.65); a++)
			{
				for (int r = 0; r <= (MonsterRushTemplates._CoordsX.length - 1); r++)
				{
					mobas = addSpawn(wave[0][i], MonsterRushTemplates._CoordsX[r] + Rnd.get(-100, 100), MonsterRushTemplates._CoordsY[r] + Rnd.get(-100, 100), MonsterRushTemplates._CoordsZ[r]);
					_monsters.add(mobas);
				}
			}
		}
	}
	
	public static L2Npc addSpawn(int npcId, int x, int y, int z)
	{
		L2Npc result = null;
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setInstanceId(0);
				spawn.setHeading(1);
				spawn.setLocx(x);
				spawn.setLocy(y);
				spawn.setLocz(z);
				spawn.stopRespawn();
				result = spawn.spawnOne(true);
				
				return result;
			}
		}
		catch (Exception e1)
		{
		}
		
		return null;
	}
	
	public static void doUnReg(L2PcInstance player)
	{
		if (_status == Status.REGISTER)
		{
			if (_participants.contains(player))
			{
				_participants.remove(player);
				player.sendMessage("Monster Rush: You have succesfully unregistered from Monster Rush event.");
			}
			else
			{
				player.sendMessage("Monster Rush: You aren't registered in this event.");
			}
		}
		else
		{
			player.sendMessage("Monster Rush: Event is inactive.");
		}
	}
	
	public static void doReg(L2PcInstance player)
	{
		if (_status == Status.REGISTER)
		{
			if (!_participants.contains(player))
			{
				_participants.add(player);
				player.sendMessage("Monster Rush: You have succesfully registered to Monster Rush event.");
				Announcements.getInstance().announceToAll("Monster Rush: player " + player.getName() + " with " + player.getLevel() + " level registred to event.");
			}
			else
			{
				player.sendMessage("Monster Rush: You have already registered for this event.");
			}
		}
		else
		{
			player.sendMessage("Monster Rush: You cannot register now.");
		}
	}
	
	public static void startRegister()
	{
		_status = Status.REGISTER;
		_participants.clear();
		Announcements.getInstance().announceToAll("Monster Rush: Registration is open.");
		Announcements.getInstance().announceToAll("Monster Rush: For registration write .mrjoin command.");
		showRegWin();
	}
	
	public static void showRegWin()
	{
		for (L2PcInstance activeChar : L2World.getInstance().getPlayers())
		{
			if ((activeChar.isAlikeDead() || activeChar._inEventCTF || activeChar._inEventDM || activeChar.isInStoreMode() || activeChar.isRooted() || activeChar.isInCombat() || activeChar.isInOlympiadMode() || activeChar.isFestivalParticipant() || activeChar.isInsideZone(L2Character.ZONE_PVP)))
			{
				activeChar.sendMessage("You cannot register to event because you registred in another event or you are in PVP or combat");
			}
			else
			{
				try
				{
					ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1).addString(activeChar.getName() + ", do you want to register to Monster Rush?");
					confirm.addTime(15000);
					confirm.addRequesterId(activeChar.getObjectId());
					activeChar.sendPacket(confirm);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void startEvent()
	{
		if (getParticipatingPlayers() < Config.MINIMUM_PLAYERS_FOR_START_MONSTER_RUSH)
		{
			Announcements.getInstance().announceToAll("Monster Rush: Event cancelled. Anyone not registered");
			cancelEvent();
		}
		else
		{
			_status = Status.STARTED;
			Announcements.getInstance().announceToAll("Monster Rush: Registration is over. Teleporting players to town center in 20 seconds.");
			ThreadPoolManager.getInstance().scheduleGeneral(() -> beginTeleport(), 20000);
		}
	}
	
	public static void SpawnDefender1()
	{
		_defender_1 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_1, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_2 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_2, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
	}
	
	public static void SpawnDefender2()
	{
		_defender_3 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_3, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_4 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_4, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_5 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_5, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
	}
	
	public static void SpawnDefender3()
	{
		_defender_6 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_6, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_7 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_7, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_8 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_8, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_9 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_9, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
		_defender_10 = addSpawn(MonsterRushTemplates.DEFEND_FAKE_10, MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z);
	}
	
	/**
	 * Is Monster Rush Event inactive?<br>
	 * <br>
	 * @return boolean: true if event is inactive(waiting for next event cycle), otherwise false<br>
	 */
	public static boolean isInactive()
	{
		boolean isInactive;
		
		synchronized (_status)
		{
			isInactive = _status == Status.INACTIVE;
		}
		
		return isInactive;
	}
	
	/**
	 * Is Monster Rush Event in participation?<br>
	 * <br>
	 * @return boolean: true if event is in participation progress, otherwise false<br>
	 */
	public static boolean isParticipating()
	{
		boolean isParticipating;
		
		synchronized (_status)
		{
			isParticipating = _status == Status.REGISTER;
		}
		
		return isParticipating;
	}
	
	/**
	 * Is Monster Rush Event started?<br>
	 * <br>
	 * @return boolean: true if event is started, otherwise false<br>
	 */
	public static boolean isStarted()
	{
		boolean isStarted;
		
		synchronized (_status)
		{
			isStarted = _status == Status.STARTED;
		}
		
		return isStarted;
	}
	
	/**
	 * Send a SystemMessage to all participated players<br>
	 * 1. Send the message to all players of team number one<br>
	 * 2. Send the message to all players of team number two<br>
	 * <br>
	 * @param message as String<br>
	 */
	public static void sysMsgToAllParticipants(String message)
	{
		for (final L2PcInstance playerInstance : _participants)
		{
			if (playerInstance != null)
			{
				playerInstance.sendMessage(message);
			}
		}
	}
	
	protected static void beginTeleport()
	{
		_status = Status.TELEPORT;
		_lord = addSpawn(MonsterRushTemplates.LORD, MonsterRushTemplates.LORD_X, MonsterRushTemplates.LORD_Y, MonsterRushTemplates.LORD_Z);
		_lord.broadcastPacket(new CreatureSay(_lord.getObjectId(), 1, _lord.getName(), "For brave! For Dion Village!"));
		// Monster Waves timers
		ThreadPoolManager.getInstance().scheduleGeneral(() -> MonstersWave(1), 60000 * 3);// 3,6,9,12
		ThreadPoolManager.getInstance().scheduleGeneral(() -> MonstersWave(2), 60000 * 6);
		ThreadPoolManager.getInstance().scheduleGeneral(() -> MonstersWave(3), 60000 * 9);
		ThreadPoolManager.getInstance().scheduleGeneral(() -> endEvent(), 60000 * 12);
		// Defender Spawns timers
		ThreadPoolManager.getInstance().scheduleGeneral(() -> SpawnDefender1(), 60000 * 3);
		ThreadPoolManager.getInstance().scheduleGeneral(() -> SpawnDefender2(), 60000 * 6);
		ThreadPoolManager.getInstance().scheduleGeneral(() -> SpawnDefender3(), 60000 * 9);
		synchronized (_participants)
		{
			for (L2PcInstance player : _participants)
			{
				if (player.isInOlympiadMode() || TvTEvent.isPlayerParticipant(player.getObjectId()) || TvTRoundEvent.isPlayerParticipant(player.getObjectId()) || player._inEventCTF)
				{
					_participants.remove(player);
					return;
				}
				player.teleToLocation(MonsterRushTemplates.LORD_X + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Y + Rnd.get(-100, 100), MonsterRushTemplates.LORD_Z, true);
				player.setTeam(2);
				player.setIsInMonsterRush(true);
			}
			Announcements.getInstance().announceToAll("Monster Rush: Teleportation done. Every monster wave will approach town in 3 minutes! End of event in 12 minutes");
		}
		
	}
	
	public static void endByLordDeath()
	{
		endEvent();
	}
}