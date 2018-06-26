/*
 * Copyright (C) 2004-2013 L2J DataPack
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
package hellbound;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.DoorTable;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.serverpackets.BelethCamera;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.DoorStatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.StaticObject;
import com.l2jserver.gameserver.skills.SkillHolder;
import com.l2jserver.gameserver.templates.StatsSet;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.gameserver.templates.skills.L2SkillType;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;

/**
 * Beleth's AI.
 * @author Treat
 */
public class Beleth extends L2AttackableAIScript
{
	// NPCs
	private static final int REAL_BELETH = 29118;
	private static final int FAKE_BELETH = 29119;
	private static final int STONE_COFFIN = 32470;
	private static final int ELF = 29128;
	private static final int WHIRPOOL = 29125;
	
	protected static L2Npc _CAMERA;
	protected static L2Npc _CAMERA2;
	protected static L2Npc _CAMERA3;
	protected static L2Npc _CAMERA4;
	protected static L2Npc _BELETH;
	protected static L2Npc _PRIEST;
	protected static L2Npc _WHIRPOOL;
	
	// OTHERs
	private static L2PcInstance BELETH_KILLER;
	protected static int _ALLOW_OBJECT_ID = 0;
	private static int KILLED = 0;
	final static List<L2Npc> _MINIONS = new CopyOnWriteArrayList<>();
	protected static ScheduledFuture<?> SPAWN_TIMER = null;
	
	// ZONEs
	protected static final L2ZoneType _ZONE = ZoneManager.getInstance().getZoneById(12018);
	
	// SKILLs
	protected static SkillHolder BLEED = new SkillHolder(5495, 1);
	protected static SkillHolder FIREBALL = new SkillHolder(5496, 1);
	protected static SkillHolder HORN_OF_RISING = new SkillHolder(5497, 1);
	protected static SkillHolder LIGHTENING = new SkillHolder(5499, 1);
	
	// Configs
	private static final long WAIT_TIME_BELETH = 300000; // 5 minutes
	
	// VARIABLEs
	protected static boolean DEBUG = false;
	protected static boolean MOVIE = false;
	protected static boolean ATTACKED = false;
	
	public Beleth()
	{
		super(-1, Beleth.class.getSimpleName(), "hellbound");
		EnterZoneId(_ZONE.getId());
		registerMobs(REAL_BELETH);
		registerMobs(FAKE_BELETH);
		StartNpcs(STONE_COFFIN);
		TalkNpcs(STONE_COFFIN);
		FirstTalkNpcs(ELF);
		StatsSet info = GrandBossManager.getInstance().getStatsSet(REAL_BELETH);
		int status = GrandBossManager.getInstance().getBossStatus(REAL_BELETH);
		if (status == 3)
		{
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new unlock(), temp);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(REAL_BELETH, 0);
			}
		}
		else if (status != 0)
		{
			GrandBossManager.getInstance().setBossStatus(REAL_BELETH, 0);
		}
		DoorTable.getInstance().getDoor(20240001).openMe();
	}
	
	protected static L2Npc spawn(int npcId, Location loc)
	{
		try
		{
			L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
			if (template != null)
			{
				L2Spawn spawn = new L2Spawn(template);
				spawn.setInstanceId(loc.getInstanceId());
				spawn.setLocation(loc);
				spawn.setAmount(spawn.getAmount() + 1);
				return spawn.doSpawn();
			}
		}
		catch (Exception ignored)
		{
		}
		return null;
	}
	
	public static void startSpawnTask()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(1), DEBUG ? 10000 : WAIT_TIME_BELETH);
	}
	
	protected static class unlock implements Runnable
	{
		@Override
		public void run()
		{
			GrandBossManager.getInstance().setBossStatus(REAL_BELETH, 0);
			DoorTable.getInstance().getDoor(20240001).openMe();
		}
	}
	
	private static class Cast implements Runnable
	{
		SkillHolder _skill;
		L2Npc _npc;
		
		public Cast(SkillHolder skill, L2Npc npc)
		{
			_skill = skill;
			_npc = npc;
		}
		
		@Override
		public void run()
		{
			if ((_npc != null) && !_npc.isDead() && !_npc.isCastingNow())
			{
				_npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
				_npc.stopMove(null);
				_npc.doCast(_skill.getSkill());
			}
		}
	}
	
	private static class Spawn implements Runnable
	{
		private int _taskId = 0;
		
		public Spawn(int taskId)
		{
			_taskId = taskId;
		}
		
		@Override
		public void run()
		{
			try
			{
				switch (_taskId)
				{
					case 1:
						MOVIE = true;
						
						_CAMERA = addSpawn(29120, new Location(16323, 213142, -9357));
						_CAMERA2 = addSpawn(29121, new Location(16323, 210741, -9357));
						_CAMERA3 = addSpawn(29122, new Location(16323, 213170, -9357));
						_CAMERA4 = addSpawn(29123, new Location(16323, 214917, -9356));
						broadcastPacket(QuestSound.BS07_A.getPacket());
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(2), 300);
						break;
					case 2:
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(3), 4900);
						break;
					case 3:
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(4), 4900);
						break;
					case 4:
						broadcastPacket(new BelethCamera(_CAMERA2.getObjectId(), 2200, 130, 0, 0, 1500, -20, 15, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(5), 1400);
						break;
					case 5:
						broadcastPacket(new BelethCamera(_CAMERA2.getObjectId(), 2300, 100, 0, 2000, 4500, 0, 10, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(6), 2500);
						break;
					case 6:
						final L2DoorInstance door = DoorTable.getInstance().getDoor(20240001);
						door.closeMe();
						
						broadcastPacket(new StaticObject(door, false));
						broadcastPacket(new DoorStatusUpdate(door));
						
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(7), 1700);
						break;
					case 7:
						broadcastPacket(new BelethCamera(_CAMERA4.getObjectId(), 1500, 210, 0, 0, 1500, 0, 0, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA4.getObjectId(), 900, 255, 0, 5000, 6500, 0, 10, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(8), 6000);
						break;
					case 8:
						addSpawn(_WHIRPOOL, WHIRPOOL, 16323, 214917, -9356, 0, false, 0, false, 0);
						broadcastPacket(new BelethCamera(_CAMERA4.getObjectId(), 900, 255, 0, 0, 1500, 0, 10, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(9), 1000);
						break;
					case 9:
						broadcastPacket(new BelethCamera(_CAMERA4.getObjectId(), 1000, 255, 0, 7000, 17000, 0, 25, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(10), 3000);
						break;
					case 10:
						_BELETH = addSpawn(REAL_BELETH, 16321, 214211, -9352, 49369, false, 0, false, 0);
						_BELETH.disableAllSkills();
						_BELETH.setIsInvul(true);
						_BELETH.setIsImmobilized(true);
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(11), 200);
						break;
					case 11:
						broadcastPacket(new SocialAction(_BELETH, 1));
						for (int i = 0; i < 6; i++)
						{
							int x = (int) ((150 * Math.cos(i * 1.046666667)) + 16323);
							int y = (int) ((150 * Math.sin(i * 1.046666667)) + 213059);
							L2Npc minion = spawn(FAKE_BELETH, new Location(x, y, -9357, 49152, _BELETH.getInstanceId()));
							minion.setShowSummonAnimation(true);
							minion.decayMe();
							_MINIONS.add(minion);
						}
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(12), 6800);
						break;
					case 12:
						broadcastPacket(new BelethCamera(_BELETH.getObjectId(), 0, 270, -5, 0, 4000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(13), 3500);
						break;
					case 13:
						broadcastPacket(new BelethCamera(_BELETH.getObjectId(), 800, 270, 10, 3000, 6000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(14), 5000);
						break;
					case 14:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 100, 270, 15, 0, 5000, 0, 0, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 100, 270, 15, 0, 5000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(15), 100);
						break;
					case 15:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 100, 270, 15, 3000, 6000, 0, 5, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(16), 1400);
						break;
					case 16:
						_BELETH.teleToLocation(16323, 213059, -9357, 49152, false);
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(17), 200);
						break;
					case 17:
						broadcastPacket(new MagicSkillUse(_BELETH, _BELETH, 5532, 1, 2000, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(18), 2000);
						break;
					case 18:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 700, 270, 20, 1500, 8000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(19), 6900);
						break;
					case 19:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 40, 260, 0, 0, 4000, 0, 0, 1, 0));
						for (L2Npc fakeBeleth : _MINIONS)
						{
							fakeBeleth.spawnMe();
							fakeBeleth.disableAllSkills();
							fakeBeleth.setIsInvul(true);
							fakeBeleth.setIsImmobilized(true);
						}
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(20), 3000);
						break;
					case 20:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 40, 280, 0, 0, 4000, 5, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(21), 3000);
						break;
					case 21:
						broadcastPacket(new BelethCamera(_CAMERA3.getObjectId(), 5, 250, 5, 0, 13000, 20, 15, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(22), 1000);
						break;
					case 22:
						broadcastPacket(new SocialAction(_BELETH, 3));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(23), 4000);
						break;
					case 23:
						broadcastPacket(new MagicSkillUse(_BELETH, _BELETH, 5533, 1, 2000, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(24), 6800);
						break;
					case 24:
						_BELETH.deleteMe();
						_BELETH = null;
						for (L2Npc fakeBeleth : _MINIONS)
						{
							fakeBeleth.deleteMe();
						}
						_MINIONS.clear();
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(25), 1000);
						break;
					case 25:
						_CAMERA.deleteMe();
						_CAMERA2.deleteMe();
						_CAMERA3.deleteMe();
						_CAMERA4.deleteMe();
						MOVIE = false;
						for (L2Character c : _ZONE.getCharactersInside())
						{
							c.enableAllSkills();
							c.setIsInvul(false);
							c.setIsImmobilized(false);
						}
						broadcastPacket(new ExShowScreenMessage("Get ready for the fight! 60 seconds to start.", 5000));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(26), 60000);
						break;
					case 26:
						if (SPAWN_TIMER != null)
						{
							SPAWN_TIMER.cancel(false);
							setSpawnTimer(0);
						}
						L2Npc npc;
						_MINIONS.clear();
						int a = 0;
						for (int i = 0; i < 16; i++)
						{
							a++;
							
							int x = (int) ((650 * Math.cos(i * 0.39)) + 16323);
							int y = (int) ((650 * Math.sin(i * 0.39)) + 213170);
							
							npc = addSpawn(FAKE_BELETH, new Location(x, y, -9357, 49152));
							_MINIONS.add(npc);
							
							if (a >= 2)
							{
								npc.setIsOverloaded(true);
								a = 0;
							}
						}
						
						int[] xm = new int[16];
						int[] ym = new int[16];
						for (int i = 0; i < 4; i++)
						{
							xm[i] = (int) ((1700 * Math.cos((i * 1.57) + 0.78)) + 16323);
							ym[i] = (int) ((1700 * Math.sin((i * 1.57) + 0.78)) + 213170);
							
							npc = addSpawn(FAKE_BELETH, new Location(xm[i], ym[i], -9357, 49152));
							npc.setIsImmobilized(true);
							
							_MINIONS.add(npc);
						}
						
						xm[4] = (xm[0] + xm[1]) / 2;
						ym[4] = (ym[0] + ym[1]) / 2;
						npc = addSpawn(FAKE_BELETH, new Location(xm[4], ym[4], -9357, 49152));
						npc.setIsImmobilized(true);
						_MINIONS.add(npc);
						xm[5] = (xm[1] + xm[2]) / 2;
						ym[5] = (ym[1] + ym[2]) / 2;
						npc = addSpawn(FAKE_BELETH, new Location(xm[5], ym[5], -9357, 49152));
						npc.setIsImmobilized(true);
						_MINIONS.add(npc);
						xm[6] = (xm[2] + xm[3]) / 2;
						ym[6] = (ym[2] + ym[3]) / 2;
						npc = addSpawn(FAKE_BELETH, new Location(xm[6], ym[6], -9357, 49152));
						npc.setIsImmobilized(true);
						_MINIONS.add(npc);
						xm[7] = (xm[3] + xm[0]) / 2;
						ym[7] = (ym[3] + ym[0]) / 2;
						npc = addSpawn(FAKE_BELETH, new Location(xm[7], ym[7], -9357, 49152));
						npc.setIsImmobilized(true);
						_MINIONS.add(npc);
						
						xm[8] = (xm[0] + xm[4]) / 2;
						ym[8] = (ym[0] + ym[4]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[8], ym[8], -9357, 49152)));
						xm[9] = (xm[4] + xm[1]) / 2;
						ym[9] = (ym[4] + ym[1]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[9], ym[9], -9357, 49152)));
						xm[10] = (xm[1] + xm[5]) / 2;
						ym[10] = (ym[1] + ym[5]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[10], ym[10], -9357, 49152)));
						xm[11] = (xm[5] + xm[2]) / 2;
						ym[11] = (ym[5] + ym[2]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[11], ym[11], -9357, 49152)));
						xm[12] = (xm[2] + xm[6]) / 2;
						ym[12] = (ym[2] + ym[6]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[12], ym[12], -9357, 49152)));
						xm[13] = (xm[6] + xm[3]) / 2;
						ym[13] = (ym[6] + ym[3]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[13], ym[13], -9357, 49152)));
						xm[14] = (xm[3] + xm[7]) / 2;
						ym[14] = (ym[3] + ym[7]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[14], ym[14], -9357, 49152)));
						xm[15] = (xm[7] + xm[0]) / 2;
						ym[15] = (ym[7] + ym[0]) / 2;
						_MINIONS.add(addSpawn(FAKE_BELETH, new Location(xm[15], ym[15], -9357, 49152)));
						
						_ALLOW_OBJECT_ID = _MINIONS.get(getRandom(_MINIONS.size())).getObjectId();
						break;
					case 27:
						_BELETH.doDie(null);
						_CAMERA = spawn(29122, new Location(16323, 213170, -9357, 0, 0));
						broadcastPacket(QuestSound.BS07_D.getPacket());
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 400, 290, 25, 0, 10000, 0, 0, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 400, 290, 25, 0, 10000, 0, 0, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 400, 110, 25, 4000, 10000, 0, 0, 1, 0));
						broadcastPacket(new SocialAction(_BELETH, 5));
						
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(28), 4000);
						break;
					case 28:
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 400, 295, 25, 4000, 5000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(29), 4500);
						break;
					case 29:
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 400, 295, 10, 4000, 11000, 0, 25, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(30), 9000);
						break;
					case 30:
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 250, 90, 25, 0, 1000, 0, 0, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA.getObjectId(), 250, 90, 25, 0, 10000, 0, 0, 1, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(31), 2000);
						break;
					case 31:
						_BELETH.deleteMe();
						_CAMERA2 = spawn(29121, new Location(14056, 213170, -9357, 0, 0));
						ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(32), 3500);
						break;
					case 32:
						broadcastPacket(new BelethCamera(_CAMERA2.getObjectId(), 800, 180, 0, 0, 4000, 0, 10, 1, 0));
						broadcastPacket(new BelethCamera(_CAMERA2.getObjectId(), 800, 180, 0, 0, 4000, 0, 10, 1, 0));
						
						L2DoorInstance door2 = DoorTable.getInstance().getDoor(20240002);
						door2.openMe();
						
						broadcastPacket(new StaticObject(door2, false));
						broadcastPacket(new DoorStatusUpdate(door2));
						
						DoorTable.getInstance().getDoor(20240003).openMe();
						
						_CAMERA.deleteMe();
						_CAMERA2.deleteMe();
						
						for (L2Character c : _ZONE.getCharactersInside())
						{
							c.enableAllSkills();
							c.setIsInvul(false);
							c.setIsImmobilized(false);
						}
						MOVIE = false;
						
						break;
					case 333:
						_BELETH = addSpawn(REAL_BELETH, 16323, 213170, -9357, 49152, false, 0, true, 0);
						break;
					case 334:
						_BELETH.deleteMe();
						break;
					
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if ((character instanceof L2PcInstance) && !character.isDead() && !character.isTeleporting() && ((L2PcInstance) character).isOnline() && (GrandBossManager.getInstance().getBossStatus(REAL_BELETH) == 1))
		{
			startSpawnTask();
			GrandBossManager.getInstance().setBossStatus(REAL_BELETH, 2);
		}
		return super.onEnterZone(character, zone);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance player, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		if ((npc != null) && !npc.isDead() && ((npc.getNpcId() == REAL_BELETH) || (npc.getNpcId() == FAKE_BELETH)) && !npc.isCastingNow() && (skill.getSkillType() == L2SkillType.HEAL) && (Rnd.get(100) < 80))
		{
			npc.setTarget(player);
			npc.stopMove(null);
			npc.doCast(HORN_OF_RISING.getSkill());
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc == null)
		{
			return super.onAttack(npc, attacker, damage, isSummon);
		}
		
		if ((npc.getNpcId() == REAL_BELETH) || (npc.getNpcId() == FAKE_BELETH))
		{
			if ((npc.getObjectId() == _ALLOW_OBJECT_ID) && !ATTACKED)
			{
				ATTACKED = true;
				L2Npc fakeBeleth = _MINIONS.get(getRandom(_MINIONS.size()));
				while (fakeBeleth.getObjectId() == _ALLOW_OBJECT_ID)
				{
					fakeBeleth = _MINIONS.get(getRandom(_MINIONS.size()));
				}
				broadcastPacket(new CreatureSay(fakeBeleth.getObjectId(), 0, fakeBeleth.getName(), "Ha Ha, look at my power!"));
			}
			if (getRandom(100) < 40)
			{
				return null;
			}
			final double distance = Math.sqrt(npc.getPlanDistanceSq(attacker.getX(), attacker.getY()));
			if ((distance > 500) || (getRandom(100) < 80))
			{
				for (L2Npc beleth : _MINIONS)
				{
					if ((beleth != null) && !beleth.isDead() && Util.checkIfInRange(900, beleth, attacker, false) && !beleth.isCastingNow())
					{
						beleth.setTarget(attacker);
						npc.stopMove(null);
						beleth.doCast(FIREBALL.getSkill());
					}
				}
				if ((_BELETH != null) && !_BELETH.isDead() && Util.checkIfInRange(900, _BELETH, attacker, false) && !_BELETH.isCastingNow())
				{
					_BELETH.setTarget(attacker);
					npc.stopMove(null);
					_BELETH.doCast(FIREBALL.getSkill());
				}
			}
			else if (!npc.isDead() && !npc.isCastingNow())
			{
				if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty())
				{
					npc.stopMove(null);
					npc.doCast(LIGHTENING.getSkill());
					return null;
				}
				((L2Attackable) npc).clearAggroList();
			}
		}
		return null;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if ((npc != null) && !npc.isDead() && ((npc.getNpcId() == REAL_BELETH) || (npc.getNpcId() == FAKE_BELETH)) && !npc.isCastingNow())
		{
			if ((player != null) && !player.isDead())
			{
				final double distance2 = Math.sqrt(npc.getPlanDistanceSq(player.getX(), player.getY()));
				if ((distance2 > 890) && !npc.isMovementDisabled())
				{
					npc.setTarget(player);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
					int speed = npc.isRunning() ? npc.getRunSpeed() : npc.getWalkSpeed();
					int time = (int) (((distance2 - 890) / speed) * 1000);
					ThreadPoolManager.getInstance().scheduleGeneral(new Cast(FIREBALL, npc), time);
					
				}
				else if (distance2 < 890)
				{
					npc.setTarget(player);
					npc.stopMove(null);
					npc.doCast(FIREBALL.getSkill());
				}
				return null;
			}
			if (getRandom(100) < 40)
			{
				if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty())
				{
					npc.stopMove(null);
					npc.doCast(LIGHTENING.getSkill());
					return null;
				}
			}
			for (L2PcInstance plr : npc.getKnownList().getKnownPlayersInRadius(950))
			{
				npc.setTarget(plr);
				npc.stopMove(null);
				npc.doCast(FIREBALL.getSkill());
				return null;
			}
			((L2Attackable) npc).clearAggroList();
		}
		return null;
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if ((npc != null) && !npc.isDead() && ((npc.getNpcId() == REAL_BELETH) || (npc.getNpcId() == FAKE_BELETH)) && !npc.isCastingNow() && !MOVIE)
		{
			if (getRandom(100) < 40)
			{
				if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty())
				{
					npc.stopMove(null);
					npc.doCast(BLEED.getSkill());
					return null;
				}
			}
			npc.setTarget(player);
			npc.stopMove(null);
			npc.doCast(FIREBALL.getSkill());
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.getSpawn().setRespawnDelay(0);
		npc.setRandomAnimationEnabled(false);
		npc.setRunning();
		if (!MOVIE && !npc.getKnownList().getKnownPlayersInRadius(300).isEmpty() && (getRandom(100) < 60))
		{
			npc.stopMove(null);
			npc.doCast(BLEED.getSkill());
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.getNpcId() == REAL_BELETH)
		{
			npc.getSpawn().setRespawnDelay(0);
			setBelethKiller(1, killer);
			GrandBossManager.getInstance().setBossStatus(REAL_BELETH, 3);
			// Calculate Min and Max respawn times randomly.
			final long respawnTime = (Config.INTERVAL_OF_BELETH_SPAWN + getRandom(-Config.RANDOM_OF_BELETH_SPAWN, Config.RANDOM_OF_BELETH_SPAWN)) * 3600000;
			StatsSet info = GrandBossManager.getInstance().getStatsSet(REAL_BELETH);
			info.set("respawn_time", System.currentTimeMillis() + respawnTime);
			GrandBossManager.getInstance().setStatsSet(REAL_BELETH, info);
			
			ThreadPoolManager.getInstance().scheduleGeneral(new unlock(), respawnTime);
			deleteAll();
			npc.deleteMe();
			
			MOVIE = true;
			
			_BELETH = addSpawn(REAL_BELETH, 16323, 213170, -9357, 49152, false, 0, false, 0);
			_BELETH.setIsInvul(true);
			_BELETH.setIsImmobilized(true);
			_BELETH.disableAllSkills();
			
			ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(334), 12000);
			
			addSpawn(ELF, 16330, 213130, -9352, 0, false, 0, false, 0);
			
			addSpawn(STONE_COFFIN, 12470, 215607, -9381, 49152, false, 0, false, 0);
			
			ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(27), 1000);
		}
		else if (npc.getNpcId() == FAKE_BELETH)
		{
			_MINIONS.remove(npc);
			KILLED++;
		}
		
		if (KILLED >= 5)
		{
			deleteAll();
			setSpawnTimer(1);
		}
		
		return null;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final String html;
		if ((BELETH_KILLER != null) && (player.getObjectId() == BELETH_KILLER.getObjectId()))
		{
			player.addItem("Kill Beleth", 10314, 1, null, true);// giveItems(10314, 1, 0)
			setBelethKiller(0, player);
			html = "32470a.htm";
		}
		else
		{
			html = "32470b.htm";
		}
		return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/default/" + html);
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	private static void setBelethKiller(int event, L2PcInstance killer)
	{
		if (event == 0)
		{
			BELETH_KILLER = null;
		}
		else if (event == 1)
		{
			if (killer.getParty() != null)
			{
				if (killer.getParty().getCommandChannel() != null)
				{
					BELETH_KILLER = killer.getParty().getCommandChannel().getChannelLeader();
				}
				else
				{
					BELETH_KILLER = killer.getParty().getLeader();
				}
			}
			else
			{
				BELETH_KILLER = killer;
			}
		}
	}
	
	protected static void setSpawnTimer(int event)
	{
		switch (event)
		{
			case 0:
				SPAWN_TIMER = null;
				break;
			case 1:
				SPAWN_TIMER = ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(333), 60000);
				break;
			case 2:
				SPAWN_TIMER = ThreadPoolManager.getInstance().scheduleGeneral(new Spawn(26), 60000);
				break;
			default:
				break;
		}
	}
	
	private static void deleteAll()
	{
		if ((_MINIONS != null) && !_MINIONS.isEmpty())
		{
			for (L2Npc npc : _MINIONS)
			{
				if ((npc == null) || npc.isDead())
				{
					continue;
				}
				npc.abortCast();
				npc.setTarget(null);
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
				npc.deleteMe();
			}
			_MINIONS.clear();
		}
		_ALLOW_OBJECT_ID = 0;
		ATTACKED = false;
	}
	
	protected static void broadcastPacket(L2GameServerPacket mov)
	{
		if (_ZONE != null)
		{
			for (L2Character characters : _ZONE.getCharactersInside())
			{
				if (characters instanceof L2PcInstance)
				{
					characters.sendPacket(mov);
				}
			}
		}
	}
}
