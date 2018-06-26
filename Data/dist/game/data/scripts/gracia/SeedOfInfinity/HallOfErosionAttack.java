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

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.instancemanager.gracia.SoIManager;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import javolution.util.FastList;
import quests.Q00696_ConquertheHallofErosion.Q00696_ConquertheHallofErosion;

public class HallOfErosionAttack extends Quest
{
	protected class HEAWorld extends InstanceWorld
	{
		public FastList<L2Npc> npcList = new FastList<>();
		public FastList<L2Npc> deadTumors = new FastList<>();
		public int tumorCount = 0;
		public L2Npc cohemenes = null;
		protected L2Npc deadTumor;
		public boolean isBossAttacked = false;
		public long startTime = 0;
		
		public synchronized void addTumorCount(int value)
		{
			tumorCount += value;
		}
		
		public synchronized void addTag(int value)
		{
			tag += value;
		}
		
		public HEAWorld()
		{
			tag = -1;
		}
	}
	
	protected boolean conquestEnded = false;
	private long tumorRespawnTime;
	
	public HallOfErosionAttack()
	{
		super(-1, HallOfErosionAttack.class.getSimpleName(), "gracia");
		
		StartNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.DESTROYED_TUMOR_2);
		TalkNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.DESTROYED_TUMOR_2);
		SpawnNpcs(CentralSoI.COHEMENES);
		for (int id : CentralSoI.HOEA_NOT_MOVE)
		{
			SpawnNpcs(id);
		}
		AggroRangeEnterNpcs(CentralSoI.WARD_OF_DEATH_2, CentralSoI.SYMBOL_OF_COHEMENES);
		AttackNpcs(CentralSoI.COHEMENES);
		KillNpcs(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, CentralSoI.COHEMENES, CentralSoI.SOUL_COFIN_2);
		
		tumorRespawnTime = 180 * 1000;
	}
	
	private void teleportPlayer(L2PcInstance player, int[] coords, int instanceId)
	{
		CentralSoI.removeBuffs(player);
		player.setInstanceId(instanceId);
		player.teleToLocation(coords[0], coords[1], coords[2]);
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
		if ((party.getCommandChannel() == null) || (party.getCommandChannel().getChannelLeader() != player))
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		if ((party.getCommandChannel().getMembers().size() < Config.EROSION_ATTACK_MIN_PLAYERS) || (party.getCommandChannel().getMembers().size() > Config.EROSION_ATTACK_MAX_PLAYERS))// 18 27
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LEVEL_REQUIREMENT_NOT_SUFFICIENT);
			party.getCommandChannel().broadcastPacket(sm);
			return false;
		}
		for (L2PcInstance partyMember : party.getCommandChannel().getMembers())
		{
			if ((partyMember.getLevel() < 75) || (partyMember.getLevel() > 85))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2097);
				sm.addPcName(partyMember);
				party.getCommandChannel().broadcastPacket(sm);
				return false;
			}
			
			if (!Util.checkIfInRange(1000, player, partyMember, true))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2096);
				sm.addPcName(partyMember);
				party.getCommandChannel().broadcastPacket(sm);
				return false;
			}
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CentralSoI.INSTANCE_ID_HALL_OF_EROSION_ATTACK);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2100);
				sm.addPcName(partyMember);
				party.getCommandChannel().broadcastPacket(sm);
				return false;
			}
			
			QuestState st = partyMember.getQuestState(Q00696_ConquertheHallofErosion.class.getSimpleName());
			if ((st == null) || (st.getInt("cond") != 1))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_QUEST_REQUIREMENT_NOT_SUFFICIENT);
				sm.addPcName(partyMember);
				player.getParty().getCommandChannel().broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	protected int enterInstance(L2PcInstance player, String template, int[] coords)
	{
		int instanceId = 0;
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		
		if (world != null)
		{
			if (!(world instanceof HEAWorld))
			{
				player.sendPacket(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER);
				return 0;
			}
			teleportPlayer(player, coords, world.getInstanceId());
			return instanceId;
		}
		
		if (!checkConditions(player))
		{
			return 0;
		}
		
		instanceId = InstanceManager.getInstance().createDynamicInstance(template);
		world = new HEAWorld();
		world.setTemplateId(CentralSoI.INSTANCE_ID_HALL_OF_EROSION_ATTACK);
		world.setInstanceId(instanceId);
		world.setStatus(0);
		((HEAWorld) world).startTime = System.currentTimeMillis();
		InstanceManager.getInstance().addWorld(world);
		
		if ((player.getParty() == null) || (player.getParty().getCommandChannel() == null))
		{
			teleportPlayer(player, coords, instanceId);
			world.addAllowed(player.getObjectId());
		}
		else
		{
			for (L2PcInstance partyMember : player.getParty().getCommandChannel().getMembers())
			{
				teleportPlayer(partyMember, coords, instanceId);
				world.addAllowed(partyMember.getObjectId());
				if (partyMember.getQuestState(getName()) == null)
				{
					newQuestState(partyMember);
				}
			}
		}
		runTumors((HEAWorld) world);
		
		return instanceId;
	}
	
	protected void runTumors(HEAWorld world)
	{
		for (int[] spawn : CentralSoI.ROOMS_MOBS_HOEA)
		{
			for (int i = 0; i < spawn[6]; i++)
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
				npc.getSpawn().setRespawnDelay(spawn[5]);
				npc.getSpawn().setAmount(1);
				if (spawn[5] > 0)
				{
					npc.getSpawn().startRespawn();
				}
				else
				{
					npc.getSpawn().stopRespawn();
				}
			}
		}
		
		for (int[] spawn : CentralSoI.ROOMS_TUMORS_HOEA)
		{
			for (int i = 0; i < spawn[6]; i++)
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
				npc.getSpawn().setRespawnDelay(spawn[5]);
				npc.getSpawn().setAmount(1);
				if (spawn[5] > 0)
				{
					npc.getSpawn().startRespawn();
				}
				else
				{
					npc.getSpawn().stopRespawn();
				}
				world.npcList.add(npc);
			}
		}
		broadCastPacket(world, new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. It has now begun!", 8000));
	}
	
	protected void stopTumors(HEAWorld world)
	{
		if (!world.npcList.isEmpty())
		{
			for (L2Npc npc : world.npcList)
			{
				if (npc != null)
				{
					npc.deleteMe();
				}
			}
		}
		world.npcList.clear();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof HEAWorld)
		{
			HEAWorld world = (HEAWorld) tmpworld;
			
			if (event.startsWith("warp"))
			{
				L2Npc victim = null;
				victim = world.deadTumor;
				if (victim != null)
				{
					world.deadTumors.add(victim);
				}
				
				player.destroyItemByItemId("SOI", CentralSoI.TEARS_OF_A_FREED_SOUL, 1, player, true);
				Location loc = world.deadTumors.get(Rnd.get(world.deadTumors.size())).getLocationXYZ();
				if (loc != null)
				{
					broadCastPacket(world, new ExShowScreenMessage(player.getParty().getPartyLeaderName() + " party has moved to a different location through the crack in the tumor!", 8000));
					for (L2PcInstance partyMember : player.getParty().getMembers())
					{
						if (partyMember.isInsideRadius(player, 500, true, false))
						{
							partyMember.teleToLocation(loc, true);
						}
					}
				}
			}
		}
		return "";
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (npcId == CentralSoI.EKIMUS_MOUTH)
		{
			enterInstance(player, CentralSoI.HALL_OF_EROSION_ATTACK_XML, CentralSoI.ENTER_TELEPORT_HOEA);
			return "";
		}
		return "";
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEAWorld)
		{
			final HEAWorld world = (HEAWorld) tmpworld;
			
			switch (npc.getNpcId())
			{
				case CentralSoI.WARD_OF_DEATH_2:
					for (int i = 0; i < Rnd.get(1, 4); i++)
					{
						spawnNpc(CentralSoI.GROUP_SPAWN_LIST_HOEA[Rnd.get(CentralSoI.GROUP_SPAWN_LIST_HOEA.length)], npc.getLocationXYZ(), 0, world.getInstanceId());
					}
					npc.doDie(npc);
					break;
				case CentralSoI.SYMBOL_OF_COHEMENES:
					for (int i = 0; i < Rnd.get(1, 4); i++)
					{
						spawnNpc(CentralSoI.GROUP_SPAWN_LIST_HOEA[Rnd.get(CentralSoI.GROUP_SPAWN_LIST_HOEA.length)], npc.getLocationXYZ(), 0, world.getInstanceId());
					}
					npc.doDie(npc);
					break;
			}
		}
		return super.onAggroRangeEnter(npc, player, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEAWorld)
		{
			final HEAWorld world = (HEAWorld) tmpworld;
			if (!world.isBossAttacked)
			{
				world.isBossAttacked = true;
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
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public final String onSpawn(L2Npc npc)
	{
		if (Util.contains(CentralSoI.HOEA_NOT_MOVE, npc.getNpcId()))
		{
			npc.setIsNoRndWalk(true);
			npc.setIsImmobilized(true);
		}
		
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEAWorld)
		{
			HEAWorld world = (HEAWorld) tmpworld;
			if (npc.getNpcId() == CentralSoI.TUMOR_OF_DEATH_ALIVE_81)
			{
				world.addTumorCount(1);
				if ((world.tumorCount == 4) && (world.cohemenes != null))
				{
					world.cohemenes.deleteMe();
					broadCastPacket(world, new ExShowScreenMessage("You have failed... The instance will shortly expire.", 8000));
					finishInstance(world);
					conquestEnded = true;
					stopTumors(world);
				}
			}
			
			if (npc.getNpcId() == CentralSoI.DESTROYED_TUMOR_2)
			{
				world.addTag(1);
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEAWorld)
		{
			HEAWorld world = (HEAWorld) tmpworld;
			Location loc = npc.getLocationXYZ();
			if (npc.getNpcId() == CentralSoI.TUMOR_OF_DEATH_ALIVE_81)
			{
				world.addTumorCount(-1);
				((L2MonsterInstance) npc).dropItem(player, CentralSoI.TEARS_OF_A_FREED_SOUL, Rnd.get(2, 5));
				npc.deleteMe();
				world.deadTumor = spawnNpc(CentralSoI.DESTROYED_TUMOR_2, loc, 0, world.getInstanceId());
				world.deadTumors.add(world.deadTumor);
				ThreadPoolManager.getInstance().scheduleGeneral(new TumorRevival(world.deadTumor, world), tumorRespawnTime);
				ThreadPoolManager.getInstance().scheduleGeneral(new RegenerationCoffinSpawn(world.deadTumor, world), 20000);
				if (world.tumorCount >= 1)
				{
					broadCastPacket(world, new ExShowScreenMessage("The tumor inside has been destroyed! You must destroy all the tumors!", 8000));
				}
				
				if ((world.tumorCount == 0) && (world.cohemenes == null))
				{
					broadCastPacket(world, new ExShowScreenMessage("All the tumors there have been destroyed! Driven into a corner, Cohemenes appears!", 8000));
					int[] spawn = CentralSoI.COHEMENES_SPAWN_HOEA[Rnd.get(0, CentralSoI.COHEMENES_SPAWN_HOEA.length - 1)];
					L2Npc n = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
					n.broadcastPacket(new CreatureSay(n.getObjectId(), Say2.SHOUT, n.getName(), "C'mon, c'mon! Show your face, you little rats! Let me see what the doomed weaklings are scheming!"));
					world.cohemenes = n;
				}
			}
			if (npc.getNpcId() == CentralSoI.COHEMENES)
			{
				npc.broadcastPacket(new CreatureSay(npc.getObjectId(), Say2.SHOUT, npc.getName(), "Keu... I will leave for now... But don't think this is over... The Seed of Infinity can never die..."));
				for (int objId : world.getAllowed())
				{
					L2PcInstance pl = L2World.getInstance().getPlayer(objId);
					QuestState st = pl.getQuestState(Q00696_ConquertheHallofErosion.class.getSimpleName());
					if ((st != null) && (st.getInt("cond") == 1))
					{
						st.set("cohemenes", "1");
					}
				}
				broadCastPacket(world, new ExShowScreenMessage("Congratulations! You have succeeded! The instance will shortly expire.", 8000));
				world.cohemenes = null;
				conquestEnded = true;
				finishInstance(world);
				stopTumors(world);
				SoIManager.notifyCohemenesKill();
			}
			
			if (npc.getNpcId() == CentralSoI.SOUL_COFIN_2)
			{
				tumorRespawnTime += 10 * 1000;
			}
		}
		return "";
	}
	
	private static final void finishInstance(InstanceWorld world)
	{
		if (world instanceof HEAWorld)
		{
			Calendar reenter = Calendar.getInstance();
			reenter.set(Calendar.MINUTE, 30);
			
			if (reenter.get(Calendar.HOUR_OF_DAY) >= 6)
			{
				reenter.add(Calendar.DATE, 1);
			}
			reenter.set(Calendar.HOUR_OF_DAY, 6);
			
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.INSTANT_ZONE_S1_RESTRICTED);
			sm.addInstanceName(world.getTemplateId());
			
			for (int objectId : world.getAllowed())
			{
				L2PcInstance obj = L2World.getInstance().getPlayer(objectId);
				if ((obj != null) && obj.isOnline())
				{
					InstanceManager.getInstance().setInstanceTime(objectId, world.getTemplateId(), reenter.getTimeInMillis());
					obj.sendPacket(sm);
				}
			}
			Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
			inst.setDuration(CentralSoI.EXIT_TIME);
			inst.setEmptyDestroyTime(0);
		}
	}
	
	private class TumorRevival implements Runnable
	{
		private final L2Npc _deadTumor;
		private final HEAWorld _world;
		
		public TumorRevival(L2Npc deadTumor, HEAWorld world)
		{
			_deadTumor = world.deadTumor;
			_world = world;
		}
		
		@Override
		public void run()
		{
			if (conquestEnded)
			{
				return;
			}
			
			L2Npc tumor = spawnNpc(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, _deadTumor.getLocationXYZ(), 0, _world.getInstanceId());
			_world.npcList.add(tumor);
			_deadTumor.deleteMe();
			_world.addTag(-1);
		}
	}
	
	private class RegenerationCoffinSpawn implements Runnable
	{
		private final L2Npc _deadTumor;
		private final HEAWorld _world;
		
		public RegenerationCoffinSpawn(L2Npc deadTumor, HEAWorld world)
		{
			_deadTumor = world.deadTumor;
			_world = world;
		}
		
		@Override
		public void run()
		{
			if (conquestEnded)
			{
				return;
			}
			for (int i = 0; i < 4; i++)
			{
				L2Npc worm = spawnNpc(CentralSoI.SOUL_COFIN_1, _deadTumor.getLocationXYZ(), 0, _world.getInstanceId());
				_world.npcList.add(worm);
			}
		}
	}
	
	protected void broadCastPacket(HEAWorld world, L2GameServerPacket packet)
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