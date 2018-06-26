/*
 * Copyright (C) 2004-2014 L2J DataPack
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
package ai;

import com.l2jserver.gameserver.GameTimeController;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExChangeNpcState;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

/**
 * Sel Mahum Training Ground AI for squads and chefs.
 * @author GKR
 */
public final class SelMahumSquad extends L2AttackableAIScript
{
	// NPC's
	private static final int CHEF = 18908;
	private static final int FIRE = 18927;
	private static final int STOVE = 18933;
	
	private static final int OHS_Weapon = 15280;
	private static final int THS_Weapon = 15281;
	
	// Sel Mahum Squad Leaders
	private static final int[] SQUAD_LEADERS =
	{
		22786,
		22787,
		22788
	};
	
	// Others Npcs
	private static final int[] OTHERS =
	{
		CHEF,
		FIRE,
		STOVE
	};
	
	private static final String[] CHEF_FSTRINGS =
	{
		"I brought the food",
		"Come and eat."
	};
	
	private static final int FIRE_EFFECT_BURN = 1;
	private static final int FIRE_EFFECT_NONE = 2;
	
	private static final int MAHUM_EFFECT_EAT = 1;
	private static final int MAHUM_EFFECT_SLEEP = 2;
	private static final int MAHUM_EFFECT_NONE = 3;
	
	public SelMahumSquad()
	{
		super(-1, SelMahumSquad.class.getSimpleName(), "ai");
		
		AttackNpcs(CHEF);
		AttackNpcs(SQUAD_LEADERS);
		addEventReceivedId(CHEF, FIRE, STOVE);
		addEventReceivedId(SQUAD_LEADERS);
		FactionCallNpcs(SQUAD_LEADERS);
		KillNpcs(CHEF);
		MoveFinishedNpcs(SQUAD_LEADERS);
		NodeArrivedNpcs(CHEF);
		SkillSeeNpcs(STOVE);
		SpawnNpcs(CHEF, FIRE);
		SpawnNpcs(SQUAD_LEADERS);
		SpawnNpcs(22786, 22787, 22788);
		SpellFinishedNpcs(CHEF);
		
		// Send event to monsters, that was spawned through SpawnTable at server start (it is impossible to track first spawn)
		for (int npcId : SQUAD_LEADERS)
		{
			for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
			{
				onSpawn(npcSpawn.getLastSpawn());
			}
		}
		for (int npcId : OTHERS)
		{
			for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
			{
				onSpawn(npcSpawn.getLastSpawn());
			}
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "CHEF_DISABLE_REWARD": // 2019005
			{
				npc.getVariables().set("REWARD_TIME_GONE", 1); // i_ai6 = 1
				break;
			}
			case "CHEF_HEAL_PLAYER": // 2019003
			{
				healPlayer(npc, player);
				break;
			}
			case "CHEF_REMOVE_INVUL": // 2019006
			{
				if (npc.isMonster())
				{
					npc.setIsInvul(false);
					npc.getVariables().remove("INVUL_REMOVE_TIMER_STARTED"); // i_ai5 = 0
					if ((player != null) && !player.isDead() && npc.getKnownList().knowsThePlayer(player))
					{
						attackPlayer((L2MonsterInstance) npc, player);
					}
				}
				break;
			}
			case "CHEF_SET_INVUL":
			{
				if (!npc.isDead())
				{
					npc.setIsInvul(true);
				}
				break;
			}
			case "START_FIRE":
			{
				startQuestTimer("START_FIRE", 30000 + getRandom(5000), npc, null);
				npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), FIRE_EFFECT_NONE));
				
				if (GameTimeController.getInstance().isNight())
				{
					npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), FIRE_EFFECT_BURN));
					npc.broadcastEvent("SCE_CAMPFIRE_START", 600, null);
				}
				else
				{
					npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), FIRE_EFFECT_NONE));
					npc.broadcastEvent("SCE_CAMPFIRE_END", 600, null);
				}
				break;
			}
			case "FIRE_ARRIVED":
			{
				// myself.i_quest0 = 1;
				npc.setIsRunning(false);
				npc.setTarget(npc);
				
				switch (getRandom(2))
				{
					case 1:
						// npc.doCast(SkillTable.getInstance().getInfo(6331, 1));
						npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), MAHUM_EFFECT_SLEEP));
						npc.setRandomAnimationEnabled(false);
						startQuestTimer("REMOVE_EFFECT", 30000, npc, null);
						break;
					case 2:
						// npc.doCast(SkillTable.getInstance().getInfo(6332, 1));
						npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), MAHUM_EFFECT_EAT));
						npc.setIsNoRndWalk(true);
						npc.setRandomAnimationEnabled(false);
						startQuestTimer("REMOVE_EFFECT", 30000, npc, null);
						break;
				}
				break;
			}
			case "NOTIFY_DINNER":
			{
				npc.broadcastEvent("SCE_DINNER_EAT", 600, null);
				break;
			}
			case "REMOVE_EFFECT":
			{
				npc.setIsRunning(true);
				npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), MAHUM_EFFECT_NONE));
				npc.setIsNoRndWalk(false);
				npc.setRandomAnimationEnabled(false);
				break;
			}
			case "RESET_FULL_BOTTLE_PRIZE":
			{
				npc.getVariables().remove("FULL_BARREL_REWARDING_PLAYER");
				break;
			}
			case "RETURN_FROM_FIRE":
			{
				if (npc.isMonster() && !npc.isDead())
				{
					((L2MonsterInstance) npc).returnHome();
					npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), MAHUM_EFFECT_NONE));
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, L2Skill skill)
	{
		if ((npc.getNpcId() == CHEF) && (npc.getVariables().getInt("BUSY_STATE") == 0)) // i_ai2 == 0
		{
			if (npc.getVariables().getInt("INVUL_REMOVE_TIMER_STARTED") == 0) // i_ai5 == 0
			{
				startQuestTimer("CHEF_REMOVE_INVUL", 180000, npc, attacker); // 2019004
				startQuestTimer("CHEF_DISABLE_REWARD", 60000, npc, null); // 2019005
				npc.getVariables().set("INVUL_REMOVE_TIMER_STARTED", 1);
			}
			startQuestTimer("CHEF_HEAL_PLAYER", 1000, npc, attacker); // 2019003
			startQuestTimer("CHEF_SET_INVUL", 60000, npc, null); // 2019006
			npc.getVariables().set("BUSY_STATE", 1);
		}
		if (Util.contains(SQUAD_LEADERS, npc.getNpcId()))
		{
			handlePreAttackMotion(npc);
		}
		return super.onAttack(npc, attacker, damage, isSummon, skill);
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isSummon)
	{
		handlePreAttackMotion(npc);
		return super.onFactionCall(npc, caller, attacker, isSummon);
	}
	
	@Override
	public String onEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		switch (eventName)
		{
			case "SCE_DINNER_CHECK":
			{
				if (receiver.getNpcId() == FIRE)
				{
					receiver.broadcastPacket(new ExChangeNpcState(receiver.getObjectId(), FIRE_EFFECT_BURN));
					final L2Npc stove = addSpawn(STOVE, receiver.getX(), receiver.getY(), receiver.getZ() + 100, 0, false, 60000);
					stove.setSummoner(receiver);
					startQuestTimer("NOTIFY_DINNER", 2000, receiver, null); // @SCE_DINNER_EAT
					sender.broadcastPacket(new CreatureSay(sender.getObjectId(), 1, sender.getName(), CHEF_FSTRINGS[getRandom(2)]));
				}
				break;
			}
			case "SCE_CAMPFIRE_START":
			{
				if (!receiver.isNoRndWalk() && !receiver.isDead() && (receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && Util.contains(SQUAD_LEADERS, receiver.getNpcId()))
				{
					receiver.setIsNoRndWalk(true); // Moving to fire - i_ai0 = 1
					receiver.setIsRunning(true);
					final Location loc = sender.getPointInRange(100, 200);
					loc.setHeading(receiver.getHeading());
					receiver.stopMove(null);
					receiver.getVariables().set("DESTINATION_X", loc.getX());
					receiver.getVariables().set("DESTINATION_Y", loc.getY());
					receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(sender.getX() + Rnd.get(100, 500), sender.getY() + Rnd.get(100, 500), sender.getZ(), sender.getHeading())); // TODO: TEST
				}
				break;
			}
			case "SCE_CAMPFIRE_END":
			{
				if ((receiver.getNpcId() == STOVE) && (receiver.getSummoner() == sender))
				{
					receiver.deleteMe();
				}
				else if ((receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && Util.contains(SQUAD_LEADERS, receiver.getNpcId()))
				{
					receiver.setIsNoRndWalk(false);
					receiver.getVariables().remove("BUSY_STATE");
					receiver.setRHandId(THS_Weapon);
					startQuestTimer("RETURN_FROM_FIRE", 3000, receiver, null);
				}
				break;
			}
			case "SCE_DINNER_EAT":
			{
				if (!receiver.isDead() && (receiver.getAI().getIntention() != CtrlIntention.AI_INTENTION_ATTACK) && (receiver.getVariables().getInt("BUSY_STATE", 0) == 0) && Util.contains(SQUAD_LEADERS, receiver.getNpcId()))
				{
					if (receiver.isNoRndWalk()) // i_ai0 == 1
					{
						receiver.setRHandId(THS_Weapon);
					}
					receiver.setIsNoRndWalk(true); // Moving to fire - i_ai0 = 1
					receiver.getVariables().set("BUSY_STATE", 1); // Eating - i_ai3 = 1
					receiver.setIsRunning(true);
					receiver.broadcastPacket(new CreatureSay(receiver.getObjectId(), 1, receiver.getName(), (getRandom(3) < 1) ? "Looks delicious" : "Lets go eat."));
					final Location loc = sender.getPointInRange(100, 200);
					loc.setHeading(receiver.getHeading());
					receiver.stopMove(null);
					receiver.getVariables().set("DESTINATION_X", loc.getX());
					receiver.getVariables().set("DESTINATION_Y", loc.getY());
					receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(sender.getX() + Rnd.get(100, 500), sender.getY() + Rnd.get(100, 500), sender.getZ(), sender.getHeading())); // TODO: TEST
				}
				break;
			}
			case "SCE_SOUP_FAILURE":
			{
				if (Util.contains(SQUAD_LEADERS, receiver.getNpcId()))
				{
					receiver.getVariables().set("FULL_BARREL_REWARDING_PLAYER", reference.getObjectId());
					startQuestTimer("RESET_FULL_BOTTLE_PRIZE", 180000, receiver, null);
				}
				break;
			}
		}
		return super.onEventReceived(eventName, sender, receiver, reference);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (npc.isMonster() && (npc.getVariables().getInt("REWARD_TIME_GONE") == 0))
		{
			((L2MonsterInstance) npc).dropItem(killer, 15492, 1);
		}
		cancelQuestTimer("CHEF_REMOVE_INVUL", npc, null);
		cancelQuestTimer("CHEF_DISABLE_REWARD", npc, null);
		cancelQuestTimer("CHEF_HEAL_PLAYER", npc, null);
		cancelQuestTimer("CHEF_SET_INVUL", npc, null);
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public void onMoveFinished(L2Npc npc)
	{
		// Npc moves to fire
		if (npc.isNoRndWalk())
		{
			npc.setRHandId(OHS_Weapon);
			startQuestTimer("FIRE_ARRIVED", 3000, npc, null);
		}
	}
	
	@Override
	public void onNodeArrived(L2Npc npc)
	{
		npc.broadcastEvent("SCE_DINNER_CHECK", 500, null);
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isSummon)
	{
		if ((npc.getNpcId() == STOVE) && (skill.getId() == 9075) && Util.contains(targets, npc))
		{
			// npc.doCast(SkillTable.getInstance().getInfo(6688, 1));
			npc.broadcastEvent("SCE_SOUP_FAILURE", 600, caster);
		}
		return super.onSkillSee(npc, caster, skill, targets, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (npc.getNpcId() == CHEF)
		{
			npc.setIsInvul(false);
			npc.setIsNoRndWalk(true);
			npc.setRandomAnimationEnabled(false);
		}
		
		if (npc.getNpcId() == FIRE)
		{
			startQuestTimer("START_FIRE", 1000, npc, null);
			npc.setIsNoRndWalk(true);
			npc.setRandomAnimationEnabled(false);
			npc.isMonster();
			npc.setIsInvul(true);
		}
		
		if (Util.contains(SQUAD_LEADERS, npc.getNpcId()))
		{
			npc.broadcastPacket(new ExChangeNpcState(npc.getObjectId(), MAHUM_EFFECT_NONE));
			npc.setRandomAnimationEnabled(false);
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if ((skill != null) && (skill.getId() == 6330))
		{
			healPlayer(npc, player);
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	private void healPlayer(L2Npc npc, L2PcInstance player)
	{
		if ((player != null) && !player.isDead() && (npc.getVariables().getInt("INVUL_REMOVE_TIMER_STARTED") != 1) && ((npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ATTACK) || (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_CAST)))
		{
			npc.setTarget(player);
			// npc.doCast(SkillTable.getInstance().getInfo(6330, 1));
		}
		else
		{
			cancelQuestTimer("CHEF_SET_INVUL", npc, null);
			npc.getVariables().remove("BUSY_STATE"); // i_ai2 = 0
			npc.getVariables().remove("INVUL_REMOVE_TIMER_STARTED"); // i_ai5 = 0
			npc.setIsRunning(false);
		}
	}
	
	private void handlePreAttackMotion(L2Npc attacked)
	{
		cancelQuestTimer("REMOVE_EFFECT", attacked, null);
		attacked.getVariables().remove("BUSY_STATE");
		attacked.setIsNoRndWalk(false); // i_ai0 == 0
		attacked.broadcastPacket(new ExChangeNpcState(attacked.getObjectId(), MAHUM_EFFECT_NONE));
		if (attacked.getRightHandItem() == OHS_Weapon)
		{
			attacked.setRHandId(THS_Weapon);
		}
	}
}