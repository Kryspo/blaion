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
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import quests.Q00697_DefendtheHallofErosion.Q00697_DefendtheHallofErosion;

public class HeartInfinityDefence extends Quest
{
	private class HIDWorld extends InstanceWorld
	{
		public List<L2Npc> npcList = new ArrayList<>();
		public List<L2Npc> deadTumors = new ArrayList<>();
		protected L2Npc deadTumor;
		public long startTime = 0;
		protected ScheduledFuture<?> finishTask = null, timerTask = null, wagonSpawnTask = null;
		
		public HIDWorld()
		{
		}
	}
	
	L2Npc preEkimus = null;
	protected int coffinsCreated = 0;
	protected long tumorRespawnTime = 0;
	protected long wagonRespawnTime = 0;
	protected boolean conquestBegun = false;
	protected boolean conquestEnded = false;
	
	public HeartInfinityDefence()
	{
		super(-1, HeartInfinityDefence.class.getSimpleName(), "gracia");
		
		StartNpcs(CentralSoI.GATEKEEPER_OF_ABYSS_YELLOW, CentralSoI.DESTROYED_TUMOR_2, CentralSoI.DESTROYED_TUMOR_3_START);
		TalkNpcs(CentralSoI.GATEKEEPER_OF_ABYSS_YELLOW, CentralSoI.DESTROYED_TUMOR_2, CentralSoI.DESTROYED_TUMOR_3_START);
		for (int spawns : CentralSoI.HID_NOT_MOVE)
		{
			SpawnNpcs(spawns);
		}
		SpawnNpcs(CentralSoI.SOUL_DEVOURER_3);
		AggroRangeEnterNpcs(CentralSoI.WARD_OF_DEATH_2);
		KillNpcs(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, CentralSoI.SOUL_COFIN_2);
		EnterZoneId(CentralSoI.EKIMUS_ZONE);
		
		tumorRespawnTime = 180 * 1000;
		wagonRespawnTime = 60 * 1000;
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
		if ((party.getCommandChannel().getMembers().size() < Config.HEART_DEFENCE_MIN_PLAYERS) || (party.getCommandChannel().getMembers().size() > Config.HEART_DEFENCE_MAX_PLAYERS))// 18 27
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
			
			Long reentertime = InstanceManager.getInstance().getInstanceTime(partyMember.getObjectId(), CentralSoI.INSTANCE_ID_HEART_INFINITY_DEFENCE);
			if (System.currentTimeMillis() < reentertime)
			{
				SystemMessage sm = SystemMessage.getSystemMessage(2100);
				sm.addPcName(partyMember);
				party.getCommandChannel().broadcastPacket(sm);
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
			if (!(world instanceof HIDWorld))
			{
				player.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_ANOTHER_INSTANT_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
				return;
			}
			teleportPlayer(player, coords, world.getInstanceId());
			return;
		}
		
		if (checkConditions(player))
		{
			world = new HIDWorld();
			world.setInstanceId(InstanceManager.getInstance().createDynamicInstance(template));
			world.setTemplateId(CentralSoI.INSTANCE_ID_HEART_INFINITY_DEFENCE);
			world.setStatus(0);
			InstanceManager.getInstance().addWorld(world);
			_log.info("Heart Infinity Defence started " + template + " Instance: " + world.getInstanceId() + " created by player: " + player.getName());
			
			if ((player.getParty() == null) || (player.getParty().getCommandChannel() == null))
			{
				teleportPlayer(player, coords, world.getInstanceId());
				world.addAllowed(player.getObjectId());
			}
			else
			{
				for (L2PcInstance partyMember : player.getParty().getCommandChannel().getMembers())
				{
					teleportPlayer(partyMember, coords, world.getInstanceId());
					world.addAllowed(partyMember.getObjectId());
					if (partyMember.getQuestState(HeartInfinityDefence.class.getSimpleName()) == null)
					{
						newQuestState(partyMember);
					}
				}
			}
			((HIDWorld) world).startTime = System.currentTimeMillis();
			((HIDWorld) world).finishTask = ThreadPoolManager.getInstance().scheduleGeneral(new FinishTask((HIDWorld) world), 30 * 60000);
			((HIDWorld) world).timerTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new TimerTask((HIDWorld) world), 298 * 1000, 5 * 60 * 1000);
			conquestBegins((HIDWorld) world);
		}
	}
	
	private void conquestBegins(final HIDWorld world)
	{
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			broadCastPacket(world, new ExShowScreenMessage("You can hear the undead of Ekimus rushing toward you. It has now begun!", 8000));
			for (int[] spawn1 : CentralSoI.ROOMS_MOBS_HID)
			{
				for (int i1 = 0; i1 < spawn1[6]; i1++)
				{
					L2Npc npc1 = addSpawn(spawn1[0], spawn1[1], spawn1[2], spawn1[3], spawn1[4], false, 0, false, world.getInstanceId());
					npc1.getSpawn().setRespawnDelay(spawn1[5]);
					npc1.getSpawn().setAmount(1);
					if (spawn1[5] > 0)
					{
						npc1.getSpawn().startRespawn();
					}
					else
					{
						npc1.getSpawn().stopRespawn();
					}
				}
			}
			
			for (int[] spawn2 : CentralSoI.ROOMS_TUMORS_HID)
			{
				for (int i2 = 0; i2 < spawn2[6]; i2++)
				{
					L2Npc npc2 = addSpawn(spawn2[0], spawn2[1], spawn2[2], spawn2[3], spawn2[4], false, 0, false, world.getInstanceId());
					world.deadTumors.add(npc2);
				}
			}
			
			InstanceManager.getInstance().getInstance(world.getInstanceId()).getDoor(14240102).openMe();
			preEkimus = addSpawn(CentralSoI.PRE_EKIMUS, -179534, 208510, -15496, 16342, false, 0, false, world.getInstanceId());
			
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				if (!conquestEnded)
				{
					if (!world.deadTumors.isEmpty())
					{
						for (L2Npc npc : world.deadTumors)
						{
							if (npc != null)
							{
								spawnCoffin(npc, world);
							}
						}
					}
				}
			}, 60000);
			
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				if (!conquestEnded)
				{
					if (!world.deadTumors.isEmpty())
					{
						for (L2Npc npc3 : world.deadTumors)
						{
							if (npc3 != null)
							{
								npc3.deleteMe();
							}
						}
						world.deadTumors.clear();
					}
					
					for (int[] spawn : CentralSoI.ROOMS_ALIVE_TUMORS_HID)
					{
						for (int i = 0; i < spawn[6]; i++)
						{
							L2Npc npc4 = addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, world.getInstanceId());
							npc4.setCurrentHp(npc4.getMaxHp() * .5);
							world.deadTumors.add(npc4);
						}
					}
					broadCastPacket(world, new ExShowScreenMessage("Tumor has completely revived. Ekimus started to regain his energy and looking for his prey...", 8000));
				}
			}, tumorRespawnTime);
			
			world.wagonSpawnTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> addSpawn(CentralSoI.SOUL_DEVOURER_3, -179544, 207400, -15496, 0, false, 0, false, world.getInstanceId()), 1000, wagonRespawnTime);
		}, 20000);
	}
	
	void spawnCoffin(L2Npc npc, HIDWorld world)
	{
		spawnNpc(CentralSoI.SOUL_COFIN, npc.getLocationXYZ(), 0, world.getInstanceId());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof HIDWorld)
		{
			HIDWorld world = (HIDWorld) tmpworld;
			
			switch (event)
			{
				case "warpechmus":
					broadCastPacket(world, new ExShowScreenMessage(player.getParty().getPartyLeaderName() + " party has moved to a different location through the crack in the tumor!", 8000));
					for (L2PcInstance partyMember : player.getParty().getPartyMembers())
					{
						if (partyMember.isInsideRadius(player, 800, true, false))
						{
							partyMember.teleToLocation(-179548, 209584, -15504, true);
						}
					}
					break;
				case "reenterechmus":
					player.destroyItemByItemId("SOI", CentralSoI.TEARS_OF_A_FREED_SOUL, 3, player, true);
					for (L2PcInstance partyMember : player.getParty().getPartyMembers())
					{
						if (partyMember.isInsideRadius(player, 400, true, false))
						{
							partyMember.teleToLocation(-179548, 209584, -15504, true);
						}
					}
					break;
				case "warp":
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
						for (L2PcInstance partyMember : player.getParty().getPartyMembers())
						{
							if (partyMember.isInsideRadius(player, 500, true, false))
							{
								partyMember.teleToLocation(loc, true);
							}
						}
					}
					break;
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(HeartInfinityDefence.class.getSimpleName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (npcId == CentralSoI.GATEKEEPER_OF_ABYSS_YELLOW)
		{
			enterInstance(player, CentralSoI.HEART_INFINITY_DEFENCE_XML, CentralSoI.ENTER_TELEPORT_HID);
		}
		return "";
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HIDWorld)
		{
			final HIDWorld world = (HIDWorld) tmpworld;
			switch (npc.getNpcId())
			{
				case CentralSoI.WARD_OF_DEATH_1:
					for (int i = 0; i < Rnd.get(1, 4); i++)
					{
						spawnNpc(CentralSoI.GROUP_SPAWN_LIST_HIA_HID[Rnd.get(CentralSoI.GROUP_SPAWN_LIST_HIA_HID.length)], npc.getLocationXYZ(), 0, world.getInstanceId());
					}
					npc.doDie(npc);
					break;
				case CentralSoI.WARD_OF_DEATH_2:
					for (int i = 0; i < Rnd.get(1, 4); i++)
					{
						spawnNpc(CentralSoI.GROUP_SPAWN_LIST_HIA_HID[Rnd.get(CentralSoI.GROUP_SPAWN_LIST_HIA_HID.length)], npc.getLocationXYZ(), 0, world.getInstanceId());
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
		if (Util.contains(CentralSoI.HID_NOT_MOVE, npc.getNpcId()))
		{
			npc.setIsNoRndWalk(true);
			npc.setIsImmobilized(true);
		}
		
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HIDWorld)
		{
			if (npc.getNpcId() == CentralSoI.SOUL_DEVOURER_3)
			{
				((L2MonsterInstance) npc).setPassive(true);
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
		if (tmpworld instanceof HIDWorld)
		{
			final HIDWorld world = (HIDWorld) tmpworld;
			final Location loc = npc.getLocationXYZ();
			switch (npc.getNpcId())
			{
				case CentralSoI.TUMOR_OF_DEATH_ALIVE_81:
					npc.dropItem(player, CentralSoI.TEARS_OF_A_FREED_SOUL, Rnd.get(2, 5));
					npc.deleteMe();
					world.deadTumor = spawnNpc(CentralSoI.DESTROYED_TUMOR_2, loc, 0, world.getInstanceId());
					world.deadTumors.add(world.deadTumor);
					wagonRespawnTime += 10000;
					broadCastPacket(world, new ExShowScreenMessage("Tumor inside has been destroyed! Ekimus calls out his prey has slowed down!", 8000));
					
					ThreadPoolManager.getInstance().scheduleGeneral(() ->
					{
						world.deadTumor.deleteMe();
						L2Npc alivetumor = spawnNpc(CentralSoI.TUMOR_OF_DEATH_ALIVE_81, loc, 0, world.getInstanceId());
						alivetumor.setCurrentHp(alivetumor.getMaxHp() * .25);
						world.npcList.add(alivetumor);
						wagonRespawnTime -= 10000;
						broadCastPacket(world, new ExShowScreenMessage("Tumor inside has completely revived. Ekimus started to regain his energy and looking for his prey...", 8000));
					}, tumorRespawnTime);
					break;
				case CentralSoI.SOUL_COFIN_2:
					tumorRespawnTime += 5 * 1000;
					break;
			}
		}
		return "";
	}
	
	protected void notifyWagonArrived(L2Npc npc, HIDWorld world)
	{
		coffinsCreated++;
		if (coffinsCreated == 20)
		{
			conquestConclusion(world);
		}
		else
		{
			CreatureSay cs = new CreatureSay(preEkimus.getObjectId(), Say2.SHOUT, preEkimus.getName(), "Bring more, more souls...!");
			preEkimus.broadcastPacket(cs);
			ExShowScreenMessage message = new ExShowScreenMessage("The Soul Coffin has Awakened Ekimus!", 8000);
			broadCastPacket(world, message);
		}
	}
	
	private class TimerTask implements Runnable
	{
		private final HIDWorld _world;
		
		TimerTask(HIDWorld world)
		{
			_world = world;
		}
		
		@Override
		public void run()
		{
			long time = ((_world.startTime + (25 * 60 * 1000L)) - System.currentTimeMillis()) / 60000;
			if (time == 0)
			{
				conquestConclusion(_world);
			}
			else
			{
				if (time == 15)
				{
					for (int[] spawn : CentralSoI.ROOMS_BOSSES_HID)
					{
						for (int i = 0; i < spawn[6]; i++)
						{
							addSpawn(spawn[0], spawn[1], spawn[2], spawn[3], spawn[4], false, 0, false, _world.getInstanceId());
						}
					}
				}
			}
		}
	}
	
	class FinishTask implements Runnable
	{
		private final HIDWorld _world;
		
		FinishTask(HIDWorld world)
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
	
	protected void conquestConclusion(HIDWorld world)
	{
		if (world.timerTask != null)
		{
			world.timerTask.cancel(false);
		}
		
		if (world.finishTask != null)
		{
			world.finishTask.cancel(false);
		}
		
		if (world.wagonSpawnTask != null)
		{
			world.wagonSpawnTask.cancel(false);
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
	
	@Override
	public final String onEnterZone(L2Character character, L2ZoneType zone)
	{
		if (character instanceof L2Attackable)
		{
			final L2Attackable npc = (L2Attackable) character;
			InstanceWorld tmpworld = InstanceManager.getInstance().getWorld(npc.getInstanceId());
			if (tmpworld instanceof HIDWorld)
			{
				HIDWorld world = (HIDWorld) tmpworld;
				if (npc.getNpcId() == CentralSoI.SOUL_DEVOURER_3)
				{
					notifyWagonArrived(npc, world);
					npc.deleteMe();
				}
			}
		}
		return null;
	}
	
	protected void broadCastPacket(HIDWorld world, L2GameServerPacket packet)
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