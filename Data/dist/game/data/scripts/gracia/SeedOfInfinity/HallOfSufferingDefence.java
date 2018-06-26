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
package gracia.SeedOfInfinity;

import java.util.Calendar;
import java.util.Map;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.templates.skills.L2SkillType;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import javolution.util.FastList;
import javolution.util.FastMap;
import quests.Q00695_DefendtheHallofSuffering.Q00695_DefendtheHallofSuffering;

public class HallOfSufferingDefence extends Quest
{
	private class DHSWorld extends InstanceWorld
	{
		public Map<L2Npc, Boolean> npcList = new FastMap<>();
		public FastList<L2Npc> tumor = new FastList<>();
		public int tumorIndex = 300;
		public int tumorcount = 0;
		public L2Npc klodekus = null;
		public L2Npc klanikus = null;
		public boolean isBossesAttacked = false;
		public long[] storeTime =
		{
			0,
			0
		};
		
		public synchronized void TumorIndex(int value)
		{
			tumorIndex += value;
		}
		
		public synchronized void TumorCount(int value)
		{
			tumorcount += value;
		}
		
		protected void calcRewardItemId()
		{
			Long finishDiff = storeTime[1] - storeTime[0];
			if (finishDiff < 1260000)
			{
				tag = CentralSoI.JEWEL_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1380000)
			{
				tag = CentralSoI.MOTHER_OF_PEARL_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1500000)
			{
				tag = CentralSoI.GOLD_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1620000)
			{
				tag = CentralSoI.SILVER_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1740000)
			{
				tag = CentralSoI.BRONZE_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1860000)
			{
				tag = CentralSoI.NON_ORNAMENTED_DUEL_SUPPLIES;
			}
			else if (finishDiff < 1980000)
			{
				tag = CentralSoI.WEAK_LOOKING_DUEL_SUPPLIES;
			}
			else if (finishDiff < 2100000)
			{
				tag = CentralSoI.SAD_LOOKING_DUEL_SUPPLIES;
			}
			else if (finishDiff < 2220000)
			{
				tag = CentralSoI.POOR_LOOKING_DUEL_SUPPLIES;
			}
			else
			{
				tag = CentralSoI.WORTHLESS_DUEL_SUPPLIES;
			}
		}
		
		public DHSWorld()
		{
			tag = -1;
		}
	}
	
	protected int tumorIndex = 300;
	private boolean doCountCoffinNotifications = false;
	
	protected class teleCoord
	{
		int instanceId;
		int x;
		int y;
		int z;
	}
	
	public HallOfSufferingDefence()
	{
		super(-1, HallOfSufferingDefence.class.getSimpleName(), "gracia");
		
		StartNpcs(CentralSoI.TEPIOS, CentralSoI.EKIMUS_MOUTH);
		StartNpcs(CentralSoI.TEPIOS, CentralSoI.EKIMUS_MOUTH);
		KillNpcs(CentralSoI.TUMOR_OF_DEATH_ALIVE_79, CentralSoI.KLODEKUS, CentralSoI.KLANIKUS);
		AttackNpcs(CentralSoI.KLODEKUS, CentralSoI.KLANIKUS);
		
		for (int mobId : CentralSoI.TUMOR_MOBIDS)
		{
			SkillSeeNpcs(mobId);
			KillNpcs(mobId);
		}
	}
	
	private boolean checkConditions(L2PcInstance player)
	{
		L2Party party = player.getParty();
		if (party == null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(2101));
			return false;
		}
		if (party.getLeader() != player)
		{
			player.sendPacket(SystemMessage.getSystemMessage(2185));
			return false;
		}
		for (L2PcInstance partyMember : party.getMembers())
		{
			if ((partyMember.getLevel() < 75) || (partyMember.getLevel() > 82))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2097);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (!Util.checkIfInRange(1000, player, partyMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2096);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CentralSoI.INSTANCE_ID_HALL_OF_SUFFERING_DEFENCE);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2100);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			QuestState st = partyMember.getQuestState(Q00695_DefendtheHallofSuffering.class.getSimpleName());
			if ((st == null) || (st.getInt("cond") != 1))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	private void teleportplayer(L2PcInstance player, teleCoord teleto)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(teleto.instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
		return;
	}
	
	protected int enterInstance(L2PcInstance player, String template, teleCoord teleto)
	{
		int instanceId = 0;
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if (world != null)
		{
			if (!(world instanceof DHSWorld))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return 0;
			}
			teleto.instanceId = world.getInstanceId();
			teleportplayer(player, teleto);
			return instanceId;
		}
		
		if (!checkConditions(player))
		{
			return 0;
		}
		
		instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		world = new DHSWorld();
		world.setTemplateId(CentralSoI.INSTANCE_ID_HALL_OF_SUFFERING_DEFENCE);
		world.setInstanceId(instanceId);
		world.setStatus(0);
		((DHSWorld) world).storeTime[0] = System.currentTimeMillis();
		InstanceManager.getInstance().addWorld(world);
		runTumors((DHSWorld) world);
		teleto.instanceId = instanceId;
		
		if (player.getParty() == null)
		{
			teleportplayer(player, teleto);
			world.addAllowed(player.getObjectId());
		}
		else
		{
			for (L2PcInstance partyMember : player.getParty().getMembers())
			{
				teleportplayer(partyMember, teleto);
				world.addAllowed(partyMember.getObjectId());
			}
		}
		return instanceId;
	}
	
	protected void exitInstance(L2PcInstance player, teleCoord tele)
	{
		player.setInstanceId(0);
		player.teleToLocation(tele.x, tele.y, tele.z);
		L2Summon pet = player.getSummon();
		if (pet != null)
		{
			pet.setInstanceId(0);
			pet.teleToLocation(tele.x, tele.y, tele.z);
		}
	}
	
	protected boolean checkKillProgress(L2Npc mob, DHSWorld world)
	{
		if (world.npcList.containsKey(mob))
		{
			world.npcList.put(mob, true);
		}
		for (boolean isDead : world.npcList.values())
		{
			if (!isDead)
			{
				return false;
			}
		}
		return true;
	}
	
	protected int[][] getRoomSpawns(int room)
	{
		switch (room)
		{
			case 0:
				return CentralSoI.ROOM_1_MOBS_HOSD;
			case 1:
				return CentralSoI.ROOM_2_MOBS_HOSD;
			case 2:
				return CentralSoI.ROOM_3_MOBS_HOSD;
			case 3:
				return CentralSoI.ROOM_4_MOBS_HOSD;
			case 4:
				return CentralSoI.ROOM_5_MOBS_HOSD;
		}
		return new int[][] {};
	}
	
	protected void runTumors(DHSWorld world)
	{
		for (int[] mob : getRoomSpawns(world.getStatus()))
		{
			L2Npc npc = addSpawn(mob[0], mob[1], mob[2], mob[3], 0, false, 0, false, world.getInstanceId());
			world.npcList.put(npc, false);
		}
		L2Npc mob = addSpawn(CentralSoI.TUMOR_OF_DEATH_ALIVE_79, CentralSoI.TUMOR_SPAWNS_HOSD[world.getStatus()][0], CentralSoI.TUMOR_SPAWNS_HOSD[world.getStatus()][1], CentralSoI.TUMOR_SPAWNS_HOSD[world.getStatus()][2], 0, false, 0, false, world.getInstanceId());
		mob.disableCoreAI(true);
		mob.setIsImmobilized(true);
		mob.setCurrentHp(mob.getMaxHp() * 0.5);
		world.npcList.put(mob, false);
		world.incStatus();
	}
	
	protected void runTwins(DHSWorld world)
	{
		world.incStatus();
		world.klodekus = addSpawn(CentralSoI.TWIN_SPAWNS_HOSD[0][0], CentralSoI.TWIN_SPAWNS_HOSD[0][1], CentralSoI.TWIN_SPAWNS_HOSD[0][2], CentralSoI.TWIN_SPAWNS_HOSD[0][3], 0, false, 0, false, world.getInstanceId());
		world.klanikus = addSpawn(CentralSoI.TWIN_SPAWNS_HOSD[1][0], CentralSoI.TWIN_SPAWNS_HOSD[1][1], CentralSoI.TWIN_SPAWNS_HOSD[1][2], CentralSoI.TWIN_SPAWNS_HOSD[1][3], 0, false, 0, false, world.getInstanceId());
		world.klanikus.setIsMortal(false);
		world.klodekus.setIsMortal(false);
	}
	
	protected void bossSimpleDie(L2Npc boss)
	{
		synchronized (this)
		{
			if (boss.isDead())
			{
				return;
			}
			boss.setCurrentHp(0);
			boss.setIsDead(true);
		}
		boss.setTarget(null);
		boss.stopMove(null);
		boss.getStatus().stopHpMpRegeneration();
		boss.stopAllEffectsExceptThoseThatLastThroughDeath();
		boss.broadcastStatusUpdate();
		boss.getAI().notifyEvent(CtrlEvent.EVT_DEAD);
		
		if (boss.getWorldRegion() != null)
		{
			boss.getWorldRegion().onDeath(boss);
		}
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		if ((skill.getSkillType() == L2SkillType.BALANCE_LIFE) || (skill.getSkillType() == L2SkillType.HEAL) || (skill.getSkillType() == L2SkillType.HEAL_PERCENT) || (skill.getSkillType() == L2SkillType.HEAL_STATIC))
		{
			int hate = 2 * skill.getAggroPoints();
			if (hate < 2)
			{
				hate = 1000;
			}
			((L2Attackable) npc).addDamageHate(caster, 0, hate);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof DHSWorld)
		{
			DHSWorld world = (DHSWorld) tmpworld;
			if (event.equalsIgnoreCase("spawnBossGuards"))
			{
				L2Npc mob = addSpawn(CentralSoI.TWIN_MOBIDS[Rnd.get(CentralSoI.TWIN_MOBIDS.length)], CentralSoI.TWIN_SPAWNS_HOSD[0][1], CentralSoI.TWIN_SPAWNS_HOSD[0][2], CentralSoI.TWIN_SPAWNS_HOSD[0][3], 0, false, 0, false, npc.getInstanceId());
				((L2Attackable) mob).addDamageHate(((L2Attackable) npc).getMostHated(), 0, 1);
				if (Rnd.get(100) < 33)
				{
					mob = addSpawn(CentralSoI.TWIN_MOBIDS[Rnd.get(CentralSoI.TWIN_MOBIDS.length)], CentralSoI.TWIN_SPAWNS_HOSD[1][1], CentralSoI.TWIN_SPAWNS_HOSD[1][2], CentralSoI.TWIN_SPAWNS_HOSD[1][3], 0, false, 0, false, npc.getInstanceId());
					((L2Attackable) mob).addDamageHate(((L2Attackable) npc).getMostHated(), 0, 1);
				}
				startQuestTimer("spawnBossGuards", CentralSoI.BOSS_MINION_SPAWN_TIME, npc, null);
			}
			else if (event.equalsIgnoreCase("ressurectTwin"))
			{
				L2Skill skill = SkillTable.getInstance().getInfo(5824, 1);
				L2Npc aliveTwin = (world.klanikus == npc ? world.klodekus : world.klanikus);
				npc.doRevive();
				npc.doCast(skill);
				npc.setCurrentHp(aliveTwin.getCurrentHp());
				
				L2Character hated = ((L2MonsterInstance) aliveTwin).getMostHated();
				if (hated != null)
				{
					npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, hated, 1000);
				}
			}
		}
		return "";
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof DHSWorld)
		{
			if (!((DHSWorld) tmpworld).isBossesAttacked)
			{
				((DHSWorld) tmpworld).isBossesAttacked = true;
				Calendar reenter = Calendar.getInstance();
				reenter.set(Calendar.MINUTE, 30);
				
				if (reenter.get(Calendar.HOUR_OF_DAY) >= 6)
				{
					reenter.add(Calendar.DATE, 1);
				}
				reenter.set(Calendar.HOUR_OF_DAY, 6);
				
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED);
				sm.addString(InstanceManager.getInstance().getInstanceIdName(tmpworld.getTemplateId()));
				
				for (int objectId : tmpworld.getAllowed())
				{
					L2PcInstance player = L2World.getInstance().getPlayer(objectId);
					if ((player != null) && player.isOnline())
					{
						InstanceManager.getInstance().setInstanceTime(objectId, tmpworld.getTemplateId(), reenter.getTimeInMillis());
						player.sendPacket(sm);
					}
				}
				startQuestTimer("spawnBossGuards", CentralSoI.BOSS_MINION_SPAWN_TIME, npc, null);
			}
			else if (damage >= npc.getCurrentHp())
			{
				if (((DHSWorld) tmpworld).klanikus.isDead())
				{
					((DHSWorld) tmpworld).klanikus.setIsDead(false);
					((DHSWorld) tmpworld).klanikus.doDie(attacker);
					((DHSWorld) tmpworld).klodekus.doDie(attacker);
				}
				else if (((DHSWorld) tmpworld).klodekus.isDead())
				{
					((DHSWorld) tmpworld).klodekus.setIsDead(false);
					((DHSWorld) tmpworld).klodekus.doDie(attacker);
					((DHSWorld) tmpworld).klanikus.doDie(attacker);
				}
				else
				{
					bossSimpleDie(npc);
					startQuestTimer("ressurectTwin", CentralSoI.BOSS_RESSURECT_TIME, npc, null);
				}
			}
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof DHSWorld)
		{
			Location loc = npc.getLocationXYZ();
			DHSWorld world = (DHSWorld) tmpworld;
			if (world.getStatus() < 5)
			{
				if (checkKillProgress(npc, world))
				{
					runTumors(world);
				}
			}
			else if (world.getStatus() == 5)
			{
				if (checkKillProgress(npc, world))
				{
					runTwins(world);
				}
			}
			else if ((world.getStatus() == 6) && ((npc.getNpcId() == CentralSoI.KLODEKUS) || (npc.getNpcId() == CentralSoI.KLANIKUS)))
			{
				if (world.klanikus.isDead() && world.klodekus.isDead())
				{
					world.incStatus();
					world.storeTime[1] = System.currentTimeMillis();
					world.calcRewardItemId();
					world.klanikus = null;
					world.klodekus = null;
					this.cancelQuestTimers("ressurectTwin");
					this.cancelQuestTimers("spawnBossGuards");
					addSpawn(CentralSoI.TEPIOS, CentralSoI.TEPIOS_SPAWN_HOSD[0], CentralSoI.TEPIOS_SPAWN_HOSD[1], CentralSoI.TEPIOS_SPAWN_HOSD[2], 0, false, 0, false, world.getInstanceId());
					doCountCoffinNotifications = false;
				}
			}
			
			if (npc.getNpcId() == CentralSoI.TUMOR_OF_DEATH_ALIVE_79)
			{
				world.TumorIndex(-50);
				world.TumorCount(1);
				doCountCoffinNotifications = true;
				notifyCoffinActivity(npc, world);
				if (world.tumorcount == 5)
				{
					npc.deleteMe();
					L2Npc deadTumor = spawnNpc(CentralSoI.TUMOR_OF_DEATH_ALIVE_80, loc, 0, world.getInstanceId());
					world.tumor.add(deadTumor);
				}
			}
		}
		return "";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(HallOfSufferingDefence.class.getSimpleName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		if (npcId == CentralSoI.EKIMUS_MOUTH)
		{
			teleCoord tele = new teleCoord();
			tele.x = -174701;
			tele.y = 218109;
			tele.z = -9592;
			enterInstance(player, CentralSoI.HALL_OF_SUFFERING_DEFENCE_XML, tele);
			return null;
		}
		return "";
	}
	
	public void notifyCoffinActivity(L2Npc npc, DHSWorld world)
	{
		if (!doCountCoffinNotifications)
		{
			return;
		}
		
		if (world.tumorIndex == 200)
		{
			broadCastPacket(world, new ExShowScreenMessage("The area near the Tumor is full of ominous energy!", 8000));
		}
		else if (world.tumorIndex == 100)
		{
			broadCastPacket(world, new ExShowScreenMessage("You can feel the surging energy of death from the Tumor", 8000));
		}
		if (world.tumorIndex <= 0)
		{
			Location loc = npc.getLocationXYZ();
			
			for (L2Npc npcs : world.tumor)
			{
				if (npcs != null)
				{
					npcs.deleteMe();
				}
			}
			L2Npc aliveTumor = spawnNpc(CentralSoI.TUMOR_OF_DEATH_ALIVE_79, loc, 0, world.getInstanceId());
			world.npcList.put(aliveTumor, false);
			doCountCoffinNotifications = false;
		}
	}
	
	protected void broadCastPacket(DHSWorld world, L2GameServerPacket packet)
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
}