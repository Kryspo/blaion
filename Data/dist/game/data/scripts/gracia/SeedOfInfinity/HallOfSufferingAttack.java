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
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.templates.skills.L2SkillType;
import com.l2jserver.gameserver.util.Util;

import javolution.util.FastMap;
import quests.Q00694_BreakThroughTheHallOfSuffering.Q00694_BreakThroughTheHallOfSuffering;

public class HallOfSufferingAttack extends Quest
{
	private class HSWorld extends InstanceWorld
	{
		public Map<L2Npc, Boolean> npcList = new FastMap<>();
		public L2Npc klodekus = null;
		public L2Npc klanikus = null;
		public boolean isBossesAttacked = false;
		public long[] storeTime =
		{
			0,
			0
		};
		
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
		
		public HSWorld()
		{
			tag = -1;
		}
	}
	
	public HallOfSufferingAttack()
	{
		super(-1, HallOfSufferingAttack.class.getSimpleName(), "gracia");
		
		StartNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.TEPIOS);
		TalkNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.TEPIOS);
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
			player.sendPacket(SystemMessageId.NOT_IN_PARTY_CANT_ENTER);
			return false;
		}
		if (party.getLeader() != player)
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		for (L2PcInstance partyMember : party.getMembers())
		{
			if (partyMember.getLevel() < 75)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			if (!Util.checkIfInRange(1000, player, partyMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_LOCATION_THAT_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CentralSoI.INSTANCE_ID_HALL_OF_SUFFERING_ATTACK);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_MAY_NOT_REENTER_YET);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
			
			QuestState st = partyMember.getQuestState(Q00694_BreakThroughTheHallOfSuffering.class.getSimpleName());
			if ((st == null) || (st.getInt("cond") != 1))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_QUEST_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				party.broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	private void teleportPlayer(L2PcInstance player, int[] coords, int instanceId)
	{
		player.setInstanceId(instanceId);
		player.teleToLocation(coords[0], coords[1], coords[2]);
	}
	
	protected int enterInstance(L2PcInstance player, String template, int[] coords)
	{
		int instanceId = 0;
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if (world != null)
		{
			if (!(world instanceof HSWorld))
			{
				player.sendPacket(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER);
				return 0;
			}
			teleportPlayer(player, coords, world.getInstanceId());
			return world.getInstanceId();
		}
		
		if (!checkConditions(player))
		{
			return 0;
		}
		
		instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		world = new HSWorld();
		world.setTemplateId(CentralSoI.INSTANCE_ID_HALL_OF_SUFFERING_ATTACK);
		world.setInstanceId(instanceId);
		world.setStatus(0);
		((HSWorld) world).storeTime[0] = System.currentTimeMillis();
		InstanceManager.getInstance().addWorld(world);
		runTumors((HSWorld) world);
		
		if (player.getParty() == null)
		{
			teleportPlayer(player, coords, instanceId);
			world.addAllowed(player.getObjectId());
		}
		else
		{
			for (L2PcInstance partyMember : player.getParty().getMembers())
			{
				teleportPlayer(partyMember, coords, instanceId);
				world.addAllowed(partyMember.getObjectId());
				if (partyMember.getQuestState(HallOfSufferingAttack.class.getSimpleName()) == null)
				{
					newQuestState(partyMember);
				}
			}
		}
		return instanceId;
	}
	
	protected boolean checkKillProgress(L2Npc mob, HSWorld world)
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
				return CentralSoI.ROOM_1_MOBS_HOSA;
			case 1:
				return CentralSoI.ROOM_2_MOBS_HOSA;
			case 2:
				return CentralSoI.ROOM_3_MOBS_HOSA;
			case 3:
				return CentralSoI.ROOM_4_MOBS_HOSA;
			case 4:
				return CentralSoI.ROOM_5_MOBS_HOSA;
		}
		return new int[][] {};
	}
	
	protected void runTumors(HSWorld world)
	{
		for (int[] mob : getRoomSpawns(world.getStatus()))
		{
			L2Npc npc = addSpawn(mob[0], mob[1], mob[2], mob[3], 0, false, 0, false, world.getInstanceId());
			world.npcList.put(npc, false);
		}
		L2Npc mob = addSpawn(CentralSoI.TUMOR_OF_DEATH_ALIVE_79, CentralSoI.TUMOR_SPAWNS_HOSA[world.getStatus()][0], CentralSoI.TUMOR_SPAWNS_HOSA[world.getStatus()][1], CentralSoI.TUMOR_SPAWNS_HOSA[world.getStatus()][2], 0, false, 0, false, world.getInstanceId());
		mob.disableCoreAI(true);
		mob.setIsImmobilized(true);
		mob.setCurrentHp(mob.getMaxHp() * 0.5);
		world.npcList.put(mob, false);
		world.incStatus();
	}
	
	protected void runTwins(HSWorld world)
	{
		world.incStatus();
		world.klodekus = addSpawn(CentralSoI.TWIN_SPAWNS_HOSA[0][0], CentralSoI.TWIN_SPAWNS_HOSA[0][1], CentralSoI.TWIN_SPAWNS_HOSA[0][2], CentralSoI.TWIN_SPAWNS_HOSA[0][3], 0, false, 0, false, world.getInstanceId());
		world.klanikus = addSpawn(CentralSoI.TWIN_SPAWNS_HOSA[1][0], CentralSoI.TWIN_SPAWNS_HOSA[1][1], CentralSoI.TWIN_SPAWNS_HOSA[1][2], CentralSoI.TWIN_SPAWNS_HOSA[1][3], 0, false, 0, false, world.getInstanceId());
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
		if (tmpworld instanceof HSWorld)
		{
			HSWorld world = (HSWorld) tmpworld;
			if (event.equalsIgnoreCase("spawnBossGuards"))
			{
				if (!world.klanikus.isInCombat() && !world.klodekus.isInCombat())
				{
					world.isBossesAttacked = false;
					return "";
				}
				L2Npc mob = addSpawn(CentralSoI.TWIN_MOBIDS[getRandom(CentralSoI.TWIN_MOBIDS.length)], CentralSoI.TWIN_SPAWNS_HOSA[0][1], CentralSoI.TWIN_SPAWNS_HOSA[0][2], CentralSoI.TWIN_SPAWNS_HOSA[0][3], 0, false, 0, false, npc.getInstanceId());
				((L2Attackable) mob).addDamageHate(((L2Attackable) npc).getMostHated(), 0, 1);
				if (getRandom(100) < 33)
				{
					mob = addSpawn(CentralSoI.TWIN_MOBIDS[getRandom(CentralSoI.TWIN_MOBIDS.length)], CentralSoI.TWIN_SPAWNS_HOSA[1][1], CentralSoI.TWIN_SPAWNS_HOSA[1][2], CentralSoI.TWIN_SPAWNS_HOSA[1][3], 0, false, 0, false, npc.getInstanceId());
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
		final InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HSWorld)
		{
			final HSWorld world = (HSWorld) tmpworld;
			if (!world.isBossesAttacked)
			{
				world.isBossesAttacked = true;
				Calendar reenter = Calendar.getInstance();
				reenter.add(Calendar.HOUR, CentralSoI.INSTANCE_PENALTY);
				
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED);
				sm.addInstanceName(tmpworld.getTemplateId());
				
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
				if (world.klanikus.isDead())
				{
					world.klanikus.setIsDead(false);
					world.klanikus.doDie(attacker);
					world.klodekus.doDie(attacker);
				}
				else if (((HSWorld) tmpworld).klodekus.isDead())
				{
					world.klodekus.setIsDead(false);
					world.klodekus.doDie(attacker);
					world.klanikus.doDie(attacker);
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
		if (tmpworld instanceof HSWorld)
		{
			HSWorld world = (HSWorld) tmpworld;
			
			if (npc.getNpcId() == CentralSoI.TUMOR_OF_DEATH_ALIVE_79)
			{
				npc.deleteMe();
				addSpawn(CentralSoI.DESTROYED_TUMOR_1, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 0, false, npc.getInstanceId());
			}
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
					addSpawn(CentralSoI.TEPIOS, CentralSoI.TEPIOS_SPAWN_HOSA[0], CentralSoI.TEPIOS_SPAWN_HOSA[1], CentralSoI.TEPIOS_SPAWN_HOSA[2], 0, false, 0, false, world.getInstanceId());
				}
			}
		}
		return "";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(HallOfSufferingAttack.class.getSimpleName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (npcId == CentralSoI.EKIMUS_MOUTH)
		{
			enterInstance(player, CentralSoI.HALL_OF_SUFFERING_ATTACK_XML, CentralSoI.ENTER_TELEPORT_HOSA);
			return null;
		}
		return "";
	}
}