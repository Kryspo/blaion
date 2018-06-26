/* This program is free software: you can redistribute it and/or modify it under
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
package ai;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class SelMahumDrill extends L2AttackableAIScript
{
	//@formatter:off
	// Sel Mahum Drill Sergeant, Sel Mahum Training Officer, Sel Mahum Drill Sergeant respectively
	private static final int[] MAHUM_CHIEFS =
	{
		22775,22776,22778
	};
	
	// Sel Mahum Recruit, Sel Mahum Recruit, Sel Mahum Soldier, Sel Mahum Recruit, Sel Mahum Soldier respectively
	private static final int[] MAHUM_SOLDIERS =
	{
		22780,22782,22783,22784,22785
	};
	
	private static final int[] CHIEF_SOCIAL_ACTIONS =
	{
		1,4,5,7
	};
	private static final Actions[] SOLDIER_SOCIAL_ACTIONS =
	{
		Actions.SCE_TRAINING_ACTION_A,
		Actions.SCE_TRAINING_ACTION_B,
		Actions.SCE_TRAINING_ACTION_C,
		Actions.SCE_TRAINING_ACTION_D
	};
	
	private static final String[] CHIEF_FSTRINGS =
	{
		"How dare you attack my recruits!!",
		"Who is disrupting the order?!"
	};
	
	private static final String[] SOLDIER_FSTRINGS =
	{
		"The drillmaster is dead!",
		"Line up the ranks!!"
	};
	
	// Chiefs event broadcast range
	private static final int TRAINING_RANGE = 650;
	
	// Script AI parameters naming
	private static final int SOCIAL_ACTION_ALT_BEHAVIOR = 1;
	private static final int SOCIAL_ACTION_NEXT_INDEX = 2;
	private static final int SOCIAL_ACTION_REMAINED_COUNT = 3;
	private static final int BUSY_STATE = 4;
	//@formatter:on
	
	private enum Actions
	{
		SCE_TRAINING_ACTION_A(4, -1, 2, 2333),
		SCE_TRAINING_ACTION_B(1, -1, 2, 4333),
		SCE_TRAINING_ACTION_C(6, 5, 4, 1000),
		SCE_TRAINING_ACTION_D(7, -1, 2, 1000);
		
		private final int _socialActionId;
		private final int _altSocialActionId;
		private final int _repeatCount;
		private final int _repeatInterval;
		
		private Actions(int socialActionId, int altSocialActionId, int repeatCount, int repeatInterval)
		{
			_socialActionId = socialActionId;
			_altSocialActionId = altSocialActionId;
			_repeatCount = repeatCount;
			_repeatInterval = repeatInterval;
		}
		
		protected int getSocialActionId()
		{
			return _socialActionId;
		}
		
		protected int getAltSocialActionId()
		{
			return _altSocialActionId;
		}
		
		protected int getRepeatCount()
		{
			return _repeatCount;
		}
		
		protected int getRepeatInterval()
		{
			return _repeatInterval;
		}
	}
	
	public SelMahumDrill()
	{
		super(-1, SelMahumDrill.class.getSimpleName(), "ai");
		addEventReceivedId(MAHUM_CHIEFS);
		addEventReceivedId(MAHUM_SOLDIERS);
		SpawnNpcs(MAHUM_CHIEFS);
		SpawnNpcs(MAHUM_SOLDIERS);
		KillNpcs(MAHUM_CHIEFS);
		AttackNpcs(MAHUM_SOLDIERS);
		
		// Send event to monsters, that was spawned through SpawnTable at server start (it is impossible to track first spawn)
		for (int npcId : MAHUM_CHIEFS)
		{
			for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
			{
				onSpawn(npcSpawn.getLastSpawn());
			}
		}
		for (int npcId : MAHUM_SOLDIERS)
		{
			for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
			{
				onSpawn(npcSpawn.getLastSpawn());
			}
		}
		
		// Start global return home timer
		startQuestTimer("DO_RETURN_HOME", 120000, null, null, true);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "DO_SOCIAL_ACTION":
				if ((npc != null) && !npc.isDead())
				{
					if ((npc.getNpcId() == 22775) || (npc.getNpcId() == 22776) || (npc.getNpcId() == 22778))
					{
						if (npc.isScriptValue(BUSY_STATE, 0) && (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) && (npc.getX() == npc.getSpawn().getLocx()) && (npc.getY() == npc.getSpawn().getLocy()))
						{
							int idx = Rnd.get(6);
							if (idx <= (CHIEF_SOCIAL_ACTIONS.length - 1))
							{
								npc.broadcastPacket(new SocialAction(npc, CHIEF_SOCIAL_ACTIONS[idx]));
								npc.setScriptValue(SOCIAL_ACTION_NEXT_INDEX, idx);
								npc.broadcastEvent("DO_SOCIAL_ACTION_2", TRAINING_RANGE, null);
							}
						}
						
						startQuestTimer("DO_SOCIAL_ACTION", 15000, npc, null);
					}
					if ((npc.getNpcId() == 22780) || (npc.getNpcId() == 22782) || (npc.getNpcId() == 22783) || (npc.getNpcId() == 22784) || (npc.getNpcId() == 22785))
					{
						handleSocialAction(npc, SOLDIER_SOCIAL_ACTIONS[npc.getScriptValue(SOCIAL_ACTION_NEXT_INDEX)], false);
					}
				}
				break;
			case "RESET_BUSY_STATE":
				if (npc != null)
				{
					npc.setScriptValue(BUSY_STATE, 0);
					npc.disableCoreAI(false);
				}
				break;
			case "DO_RETURN_HOME":
				for (int npcId : MAHUM_SOLDIERS)
				{
					for (L2Spawn npcSpawn : SpawnTable.getInstance().getSpawns(npcId))
					{
						L2Npc soldier = npcSpawn.getLastSpawn();
						if ((soldier != null) && !soldier.isDead() && ((soldier.getX() != soldier.getSpawn().getLocx()) || (soldier.getY() != soldier.getSpawn().getLocy())) && ((soldier.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) || (soldier.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)))
						{
							npcSpawn.getLastSpawn().setHeading(npcSpawn.getHeading());
							npcSpawn.getLastSpawn().teleToLocation(npcSpawn.getLocx(), npcSpawn.getLocy(), npcSpawn.getLocz());
						}
					}
				}
				break;
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (Rnd.get(10) < 1)
		{
			npc.broadcastEvent("ATTACKED", 1000, null);
		}
		
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onEventReceived(String eventName, L2Npc sender, L2Npc receiver, L2Object reference)
	{
		if ((receiver != null) && !receiver.isDead())
		{
			if (Util.contains(MAHUM_SOLDIERS, receiver.getNpcId()))
			{
				switch (eventName)
				{
					case "DO_SOCIAL_ACTION_2":
						int actionIndex = sender.getScriptValue(SOCIAL_ACTION_NEXT_INDEX);
						receiver.setScriptValue(SOCIAL_ACTION_NEXT_INDEX, actionIndex);
						handleSocialAction(receiver, SOLDIER_SOCIAL_ACTIONS[actionIndex], true);
						break;
					case "CHIEF_DIED":
						if (Rnd.get(4) < 1)
						{
							receiver.broadcastPacket(new CreatureSay(receiver.getObjectId(), Say2.ALL, receiver.getName(), SOLDIER_FSTRINGS[Rnd.get(2)]));
						}
						
						if (receiver.isAttackable())
						{
							((L2Attackable) receiver).clearAggroList();
						}
						receiver.disableCoreAI(true);
						receiver.setScriptValue(BUSY_STATE, 1);
						receiver.setIsRunning(true);
						receiver.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location((receiver.getX() + Rnd.get(-800, 800)), (receiver.getY() + Rnd.get(-800, 800)), receiver.getZ(), receiver.getHeading()));
						startQuestTimer("RESET_BUSY_STATE", 5000, receiver, null);
						break;
				}
			}
			if (Util.contains(MAHUM_CHIEFS, receiver.getNpcId()))
			{
				switch (eventName)
				{
					case "ATTACKED":
						receiver.broadcastPacket(new CreatureSay(receiver.getObjectId(), Say2.ALL, receiver.getName(), CHIEF_FSTRINGS[Rnd.get(2)]));
						break;
				}
			}
		}
		return super.onEventReceived(eventName, sender, receiver, reference);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		if (Util.contains(MAHUM_CHIEFS, npc.getNpcId()))
		{
			npc.broadcastEvent("CHIEF_DIED", TRAINING_RANGE, null);
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		if (Util.contains(MAHUM_CHIEFS, npc.getNpcId()))
		{
			npc.disableCoreAI(false);
			npc.setIsVisible(true);
			npc.setIsNoRndWalk(true);
			npc.setRandomAnimationEnabled(false);
			startQuestTimer("DO_SOCIAL_ACTION", 15000, npc, null);
		}
		if (Util.contains(MAHUM_SOLDIERS, npc.getNpcId()))
		{
			npc.disableCoreAI(false);
			npc.setIsVisible(true);
			npc.setIsNoRndWalk(true);
			npc.setRandomAnimationEnabled(false);
			if ((Rnd.get(18) < 1))
			{
				npc.setScriptValue(SOCIAL_ACTION_ALT_BEHAVIOR, 1);
			}
		}
		return super.onSpawn(npc);
	}
	
	private void handleSocialAction(L2Npc npc, Actions action, boolean firstCall)
	{
		if (!npc.isScriptValue(BUSY_STATE, 0) || (npc.getAI().getIntention() != CtrlIntention.AI_INTENTION_ACTIVE) || (npc.getX() != npc.getSpawn().getLocx()) || (npc.getY() != npc.getSpawn().getLocy()))
		{
			return;
		}
		
		int socialActionId = npc.isScriptValue(SOCIAL_ACTION_ALT_BEHAVIOR, 0) ? action.getSocialActionId() : action.getAltSocialActionId();
		
		if (socialActionId < 0)
		{
			return;
		}
		
		if (firstCall)
		{
			npc.setScriptValue(SOCIAL_ACTION_REMAINED_COUNT, action.getRepeatCount());
		}
		
		npc.broadcastPacket(new SocialAction(npc, socialActionId));
		
		int remainedCount = npc.getScriptValue(SOCIAL_ACTION_REMAINED_COUNT);
		if (remainedCount > 0)
		{
			npc.setScriptValue(SOCIAL_ACTION_REMAINED_COUNT, (remainedCount - 1));
			startQuestTimer("DO_SOCIAL_ACTION", action.getRepeatInterval(), npc, null);
		}
	}
}