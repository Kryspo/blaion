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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.l2jserver.Config;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.L2CommandChannel;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2QuestGuardInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import quests.Q00697_DefendtheHallofErosion.Q00697_DefendtheHallofErosion;

public class HallOfErosionDefence extends Quest
{
	protected class HEDWorld extends InstanceWorld
	{
		public List<L2Attackable> npcList = new ArrayList<>();
		public List<L2Npc> alivetumor = new ArrayList<>();
		public List<L2Npc> deadTumors = new ArrayList<>();
		protected L2Npc deadTumor;
		public long startTime = 0;
		public ScheduledFuture<?> finishTask = null;
		
		public synchronized void addTag(int value)
		{
			tag += value;
		}
		
		public HEDWorld()
		{
			tag = -1;
		}
	}
	
	public int tumorKillCount = 0;
	protected boolean conquestEnded = false;
	private boolean soulwagonSpawned = false;
	private static int seedKills = 0;
	private long tumorRespawnTime;
	
	public HallOfErosionDefence()
	{
		super(-1, HallOfErosionDefence.class.getSimpleName(), "gracia");
		
		StartNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.DESTROYED_TUMOR_2);
		TalkNpcs(CentralSoI.EKIMUS_MOUTH, CentralSoI.DESTROYED_TUMOR_2);
		SpawnNpcs(CentralSoI.HOED_NOT_MOVE);
		SpawnNpcs(CentralSoI.SEED);
		AggroRangeEnterNpcs(CentralSoI.WARD_OF_DEATH_2);
		KillNpcs(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, CentralSoI.SEED, CentralSoI.SOUL_COFIN_2);
		
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
		
		L2CommandChannel channel = party.getCommandChannel();
		if (channel == null)
		{
			player.sendPacket(SystemMessageId.NOT_IN_COMMAND_CHANNEL_CANT_ENTER);
			return false;
		}
		
		if (channel.getChannelLeader() != player)
		{
			player.sendPacket(SystemMessageId.ONLY_PARTY_LEADER_CAN_ENTER);
			return false;
		}
		
		if ((party.getCommandChannel().getMembers().size() < Config.EROSION_DEFENCE_MIN_PLAYERS) || (party.getCommandChannel().getMembers().size() > Config.EROSION_DEFENCE_MAX_PLAYERS))// 18 27
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
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
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CentralSoI.INSTANCE_ID_HALL_OF_EROSION_DEFENCE);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2100);
				sm.addPcName(partyMember);
				party.getCommandChannel().broadcastPacket(sm);
				return false;
			}
			QuestState st = partyMember.getQuestState(Q00697_DefendtheHallofErosion.class.getSimpleName());
			if ((st == null) || (st.getInt("cond") != 1))
			{
				SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED);
				sm.addPcName(partyMember);
				player.getParty().getCommandChannel().broadcastPacket(sm);
				return false;
			}
		}
		return true;
	}
	
	protected void enterInstance(L2PcInstance player, String template, int[] coords)
	{
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		if (world != null)
		{
			if (!(world instanceof HEDWorld))
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
				return;
			}
			teleportPlayer(player, coords, world.getInstanceId());
			return;
		}
		
		if (checkConditions(player))
		{
			world = new HEDWorld();
			world.setInstanceId(InstanceManager.getInstance().createDynamicInstance(template));
			world.setTemplateId(CentralSoI.INSTANCE_ID_HALL_OF_EROSION_DEFENCE);
			world.setStatus(0);
			((HEDWorld) world).startTime = System.currentTimeMillis();
			InstanceManager.getInstance().addWorld(world);
			_log.info("Hall Of Erosion Defence started " + template + " Instance: " + world.getInstanceId() + " created by player: " + player.getName());
			if (player.isInParty())
			{
				for (L2PcInstance partyMember : player.getParty().isInCommandChannel() ? player.getParty().getCommandChannel().getMembers() : player.getParty().getMembers())
				{
					teleportPlayer(partyMember, coords, world.getInstanceId());
					world.addAllowed(partyMember.getObjectId());
				}
			}
			else
			{
				teleportPlayer(player, coords, world.getInstanceId());
				world.addAllowed(player.getObjectId());
			}
			((HEDWorld) world).finishTask = ThreadPoolManager.getInstance().scheduleGeneral(new FinishTask((HEDWorld) world), 20 * 60000);
			runTumors((HEDWorld) world);
		}
	}
	
	protected void runTumors(final HEDWorld world)
	{
		for (int[] spawn : CentralSoI.ROOMS_MOBS_HOED)
		{
			for (int i = 0; i < spawn[6]; i++)
			{
				world.npcList = new ArrayList<>();
				L2Attackable npc = (L2Attackable) addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
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
		
		for (int[] spawn : CentralSoI.TUMOR_DEAD_SPAWN_HOED)
		{
			for (int i = 0; i < spawn[6]; i++)
			{
				L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
				world.deadTumors.add(npc);
				ThreadPoolManager.getInstance().scheduleGeneral(new RegenerationCoffinSpawn(npc, world), 1000);
			}
		}
		
		for (int[] spawn : CentralSoI.SEEDS_SPAWN_HOED)
		{
			for (int i = 0; i < spawn[6]; i++)
			{
				addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
			}
		}
		
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (!conquestEnded)
			{
				stopDeadTumors(world);
				for (int[] spawn : CentralSoI.TUMOR_ALIVE_SPAWN_HOED)
				{
					for (int i = 0; i < spawn[6]; i++)
					{
						L2Npc npc = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
						world.alivetumor.add(npc);
					}
				}
				broadCastPacket(world, new ExShowScreenMessage("Tumors has completely revived. Undeads are swarming toward Seed of Life...", 8000));
			}
		}, 180 * 1000);
		broadCastPacket(world, new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. It has now begun!", 8000));
	}
	
	protected void stopDeadTumors(HEDWorld world)
	{
		if (!world.deadTumors.isEmpty())
		{
			for (L2Npc npc : world.deadTumors)
			{
				if (npc != null)
				{
					npc.deleteMe();
				}
			}
		}
		world.deadTumors.clear();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof HEDWorld)
		{
			HEDWorld world = (HEDWorld) tmpworld;
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
		if (npc.getNpcId() == CentralSoI.EKIMUS_MOUTH)
		{
			enterInstance(player, CentralSoI.HALL_OF_EROSION_DEFENCE_XML, CentralSoI.ENTER_TELEPORT_HOED);
			return "";
		}
		return "";
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEDWorld)
		{
			final HEDWorld world = (HEDWorld) tmpworld;
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
	public final String onSpawn(L2Npc npc)
	{
		if (Util.contains(CentralSoI.HOED_NOT_MOVE, npc.getNpcId()))
		{
			npc.setIsNoRndWalk(true);
			npc.setIsImmobilized(true);
		}
		
		if (npc.getNpcId() == CentralSoI.SEED)
		{
			((L2QuestGuardInstance) npc).setPassive(true);
		}
		
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEDWorld)
		{
			HEDWorld world = (HEDWorld) tmpworld;
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
		if (tmpworld instanceof HEDWorld)
		{
			final HEDWorld world = (HEDWorld) tmpworld;
			if (npc.getNpcId() == CentralSoI.TUMOR_OF_DEATH_ALIVE_81)
			{
				npc.dropItem(player, CentralSoI.TEARS_OF_A_FREED_SOUL, Rnd.get(2, 5));
				npc.deleteMe();
				notifyTumorDeath(npc, world);
				world.deadTumor = spawnNpc(CentralSoI.DESTROYED_TUMOR_2, npc.getLocationXYZ(), 0, world.getInstanceId());
				world.deadTumors.add(world.deadTumor);
				broadCastPacket(world, new ExShowScreenMessage("Tumor inside has been destroyed! Undead starts losing their energy and run away!", 8000));
				ThreadPoolManager.getInstance().scheduleGeneral(() ->
				{
					world.deadTumor.deleteMe();
					L2Npc tumor = spawnNpc(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, world.deadTumor.getLocationXYZ(), 0, world.getInstanceId());
					world.alivetumor.add(tumor);
					broadCastPacket(world, new ExShowScreenMessage("Tumor inside has completely revived. Undeads are swarming toward Seed of Life...", 8000));
				}, tumorRespawnTime);
			}
			if (npc.getNpcId() == CentralSoI.SOUL_COFIN_2)
			{
				tumorRespawnTime += 5 * 1000;
			}
		}
		return super.onKill(npc, player, isSummon);
	}
	
	public String onKillByMob2(L2Npc npc, L2Npc killer)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HEDWorld)
		{
			HEDWorld world = (HEDWorld) tmpworld;
			seedKills++;
			if (seedKills >= 1)
			{
				conquestConclusion(world);
			}
		}
		return null;
	}
	
	private void notifyTumorDeath(L2Npc npc, HEDWorld world)
	{
		tumorKillCount++;
		if ((tumorKillCount == 4) && !soulwagonSpawned)
		{
			soulwagonSpawned = true;
			L2Npc soul = spawnNpc(25636, npc.getLocationXYZ(), 0, world.getInstanceId());
			CreatureSay cs = new CreatureSay(soul.getObjectId(), 1, soul.getName(), "Ha Ha Ha");
			soul.broadcastPacket(cs);
		}
	}
	
	private class RegenerationCoffinSpawn implements Runnable
	{
		private final L2Npc _npc;
		private final HEDWorld _world;
		
		public RegenerationCoffinSpawn(L2Npc npc, HEDWorld world)
		{
			_npc = npc;
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
				L2Npc worm = spawnNpc(CentralSoI.SOUL_COFIN, _npc.getLocationXYZ(), 0, _world.getInstanceId());
				_world.deadTumors.add(worm);
			}
		}
	}
	
	class FinishTask implements Runnable
	{
		private final HEDWorld _world;
		
		FinishTask(HEDWorld world)
		{
			_world = world;
		}
		
		@Override
		public void run()
		{
			if (_world != null)
			{
				conquestEnded = true;
				final Instance inst = InstanceManager.getInstance().getInstance(_world.getInstanceId());
				if (inst != null)
				{
					for (int objId : _world.getAllowed())
					{
						L2PcInstance player = L2World.getInstance().getPlayer(objId);
						QuestState st = player.getQuestState(Q00697_DefendtheHallofErosion.class.getSimpleName());
						if ((st != null) && (st.getInt("cond") == 1))
						{
							st.set("defenceDone", 1);
						}
					}
					broadCastPacket(_world, new ExShowScreenMessage("Congratulations! You have succeeded! The instance will shortly expire.", 8000));
					inst.removeNpcs();
					if (inst.getPlayers().isEmpty())
					{
						inst.setDuration(CentralSoI.EXIT_TIME);
					}
					else
					{
						inst.setDuration(CentralSoI.EXIT_TIME);
						inst.setEmptyDestroyTime(CentralSoI.EXIT_TIME);
					}
				}
			}
		}
	}
	
	private void conquestConclusion(HEDWorld world)
	{
		if (world.finishTask != null)
		{
			world.finishTask.cancel(false);
			world.finishTask = null;
		}
		broadCastPacket(world, new ExShowScreenMessage("You have failed... The instance will shortly expire.", 8000));
		
		conquestEnded = true;
		final Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
		if (inst != null)
		{
			inst.removeNpcs();
			if (inst.getPlayers().isEmpty())
			{
				inst.setDuration(CentralSoI.EXIT_TIME);
			}
			else
			{
				inst.setDuration(CentralSoI.EXIT_TIME);
				inst.setEmptyDestroyTime(CentralSoI.EXIT_TIME);
			}
		}
	}
	
	protected void broadCastPacket(HEDWorld world, L2GameServerPacket packet)
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