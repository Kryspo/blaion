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
package plains_of_the_lizardmen;

import java.util.concurrent.ScheduledFuture;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;
import quests.Q00288_HandleWithCare.Q00288_HandleWithCare;
import quests.Q00423_TakeYourBestShot.Q00423_TakeYourBestShot;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class SeerUgoros extends L2AttackableAIScript
{
	// OTHERS
	protected ScheduledFuture<?> _thinkTask = null;
	protected static L2Npc _ugoros = null;
	protected static L2Npc _weed = null;
	protected static boolean _weed_attack = false;
	private static boolean _weed_killed_by_player = false;
	private static boolean _killed_one_weed = false;
	protected static L2PcInstance _player = null;
	
	// ITEMS
	private static final int UGOROS_PASS = 15496;
	private static final int MID_SCALE = 15498;
	private static final int HIGH_SCALE = 15497;
	
	// ZONE
	private static final int ZONE = 70307;
	
	// NPC
	private static final int BATRACOS = 32740;
	
	// MONSTERS
	private static final int WEED = 18867;
	private static final int SEER_UGOROS = 18863;
	
	// STATUS
	private static final byte ALIVE = 0;
	private static final byte FIGHTING = 1;
	private static final byte DEAD = 2;
	public static byte STATE = DEAD;
	
	// SKILLS
	private static final L2Skill _ugoros_skill = SkillTable.getInstance().getInfo(6426, 1);
	
	public SeerUgoros()
	{
		super(-1, SeerUgoros.class.getSimpleName(), "plains_of_the_lizardmen");
		StartNpcs(BATRACOS);
		TalkNpcs(BATRACOS);
		AttackNpcs(WEED);
		KillNpcs(SEER_UGOROS);
		startQuestTimer("ugoros_respawn", 60000, null, null);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "ugoros_respawn":
				if (_ugoros == null)
				{
					_ugoros = addSpawn(SEER_UGOROS, 96804, 85604, -3720, 34360, false, 0);
					broadcastInRegion(_ugoros, "Listen, oh Tantas! I have returned! The Prophet Yugoros of the Black Abyss is with me, so do not be afraid!");
					STATE = ALIVE;
					startQuestTimer("ugoros_shout", 120000, null, null);
				}
				break;
			case "ugoros_shout":
				if (STATE == FIGHTING)
				{
					L2ZoneType _zone = ZoneManager.getInstance().getZoneById(ZONE);
					if (_player == null)
					{
						STATE = ALIVE;
					}
					else if (!_zone.isCharacterInZone(_player))
					{
						STATE = ALIVE;
						_player = null;
					}
				}
				else if (STATE == ALIVE)
				{
					broadcastInRegion(_ugoros, "Listen, oh Tantas! The Black Abyss is famished! Find some fresh offerings!");
				}
				
				startQuestTimer("ugoros_shout", 120000, null, null);
				break;
			case "ugoros_attack":
				if (_player != null)
				{
					changeAttackTarget(_player);
					_ugoros.broadcastPacket(new CreatureSay(_ugoros.getObjectId(), 1, _ugoros.getName(), "Welcome, " + _player.getName() + " Let us see if you have brought a worthy offering for the Black Abyss!"));
					if (_thinkTask != null)
					{
						_thinkTask.cancel(true);
					}
					
					_thinkTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ThinkTask(), 1000, 3000);
				}
				break;
			case "weed_check":
				if ((_weed_attack == true) && (_ugoros != null) && (_weed != null))
				{
					if (_weed.isDead() && !_weed_killed_by_player)
					{
						_killed_one_weed = true;
						_weed = null;
						_weed_attack = false;
						_ugoros.getStatus().setCurrentHp(_ugoros.getStatus().getCurrentHp() + (_ugoros.getMaxHp() * 0.2));
						_ugoros.broadcastPacket(new CreatureSay(_ugoros.getObjectId(), 1, _ugoros.getName(), "What a formidable foe! But I have the Abyss Weed given to me by the Black Abyss! Let me see..."));
					}
					else
					{
						startQuestTimer("weed_check", 2000, null, null);
					}
				}
				else
				{
					_weed = null;
					_weed_attack = false;
				}
				break;
			case "ugoros_expel":
				if (_player != null)
				{
					_player.teleToLocation(94701, 83053, -3580);
					_player = null;
				}
				break;
			case "teleportInside":
				if ((player != null) && (STATE == ALIVE))
				{
					if (player.getInventory().getItemByItemId(UGOROS_PASS) != null)
					{
						STATE = FIGHTING;
						_player = player;
						_killed_one_weed = false;
						player.teleToLocation(95984, 85692, -3720);
						player.destroyItemByItemId("SeerUgoros", UGOROS_PASS, 1, npc, true);
						startQuestTimer("ugoros_attack", 2000, null, null);
						QuestState st = player.getQuestState(Q00288_HandleWithCare.class.getSimpleName());
						if (st != null)
						{
							st.set("drop", "1");
						}
					}
					else
					{
						QuestState st = player.getQuestState(Q00423_TakeYourBestShot.class.getSimpleName());
						if (st == null)
						{
							return "<html><body>Gatekeeper Batracos:<br>You look too inexperienced to make a journey to see Tanta Seer Ugoros. If you can convince Chief Investigator Johnny that you should go, then I will let you pass. Johnny has been everywhere and done everything. He may not be of my people but he has my respect, and anyone who has his will in turn have mine as well.<br></body></html>";
						}
						
						return "<html><body>Gatekeeper Batracos:<br>Tanta Seer Ugoros is hard to find. You'll just have to keep looking.<br></body></html>";
					}
				}
				else
				{
					return "<html><body>Gatekeeper Batracos:<br>Tanta Seer Ugoros is hard to find. You'll just have to keep looking.<br></body></html>";
				}
				break;
			case "teleport_back":
				
				if (player != null)
				{
					player.teleToLocation(94792, 83542, -3424);
					_player = null;
				}
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		if (npc.isDead())
		{
			return null;
		}
		
		if (npc.getNpcId() == WEED)
		{
			if ((_ugoros != null) && (_weed != null) && npc.equals(_weed))
			{
				_weed = null;
				_weed_attack = false;
				_weed_killed_by_player = true;
				_ugoros.broadcastPacket(new CreatureSay(_ugoros.getObjectId(), 1, _ugoros.getName(), "What a formidable foe! But I have the Abyss Weed given to me by the Black Abyss! Let me see..."));
				
				if (_thinkTask != null)
				{
					_thinkTask.cancel(true);
				}
				
				_thinkTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ThinkTask(), 500, 3000);
			}
			npc.doDie(attacker);
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isSummon)
	{
		if (npc.getNpcId() == SEER_UGOROS)
		{
			if (_thinkTask != null)
			{
				_thinkTask.cancel(true);
				_thinkTask = null;
			}
			STATE = DEAD;
			broadcastInRegion(_ugoros, "Ah... How could I lose... Oh, Black Abyss, receive me...");
			_ugoros = null;
			addSpawn(BATRACOS, 96782, 85918, -3720, 34360, false, 50000);
			startQuestTimer("ugoros_expel", 50000, null, null);
			startQuestTimer("ugoros_respawn", 60000, null, null);
			QuestState st = player.getQuestState(Q00288_HandleWithCare.class.getSimpleName());
			if ((st != null) && (st.getInt("cond") == 1) && (st.getInt("drop") == 1))
			{
				if (_killed_one_weed)
				{
					player.addItem("SeerUgoros", MID_SCALE, 1, npc, true);
					st.set("cond", "2");
				}
				else
				{
					player.addItem("SeerUgoros", HIGH_SCALE, 1, npc, true);
					st.set("cond", "3");
				}
				st.unset("drop");
			}
		}
		return null;
	}
	
	private void broadcastInRegion(L2Npc npc, String npcString)
	{
		if (npc == null)
		{
			return;
		}
		
		CreatureSay cs = new CreatureSay(npc.getObjectId(), 1, npc.getName(), npcString);
		for (L2PcInstance player : L2World.getInstance().getAllPlayersArray())
		{
			if (Util.checkIfInRange(6000, npc, player, false))
			{
				player.sendPacket(cs);
			}
		}
	}
	
	protected class ThinkTask implements Runnable
	{
		@Override
		public void run()
		{
			L2ZoneType _zone = ZoneManager.getInstance().getZoneById(ZONE);
			
			if ((STATE == FIGHTING) && (_player != null) && _zone.isCharacterInZone(_player) && !_player.isDead())
			{
				if (_weed_attack && (_weed != null))
				{
				}
				else if (Rnd.get(10) < 6)
				{
					_weed = null;
					for (L2Character _char : _ugoros.getKnownList().getKnownCharactersInRadius(2000))
					{
						if ((_char instanceof L2Attackable) && !_char.isDead() && (((L2Attackable) _char).getNpcId() == WEED))
						{
							_weed_attack = true;
							_weed = (L2Attackable) _char;
							changeAttackTarget(_weed);
							startQuestTimer("weed_check", 1000, null, null);
							break;
						}
					}
					if (_weed == null)
					{
						changeAttackTarget(_player);
					}
				}
				else
				{
					changeAttackTarget(_player);
				}
			}
			else
			{
				STATE = ALIVE;
				_player = null;
				if (_thinkTask != null)
				{
					_thinkTask.cancel(true);
					_thinkTask = null;
				}
			}
		}
	}
	
	protected void changeAttackTarget(L2Character _attack)
	{
		((L2Attackable) _ugoros).getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		((L2Attackable) _ugoros).clearAggroList();
		((L2Attackable) _ugoros).setTarget(_attack);
		
		if (_attack instanceof L2Attackable)
		{
			_weed_killed_by_player = false;
			_ugoros.disableSkill(_ugoros_skill, 100000);
			((L2Attackable) _ugoros).setIsRunning(true);
			((L2Attackable) _ugoros).addDamageHate(_attack, 0, Integer.MAX_VALUE);
		}
		else
		{
			_ugoros.enableSkill(_ugoros_skill);
			((L2Attackable) _ugoros).addDamageHate(_attack, 0, 99);
			((L2Attackable) _ugoros).setIsRunning(false);
		}
		((L2Attackable) _ugoros).getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, _attack);
	}
}