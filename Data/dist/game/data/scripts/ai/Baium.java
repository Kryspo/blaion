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
package ai;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_FOLLOW;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.L2Effect;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2DecoyInstance;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestTimer;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.Earthquake;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;
import com.l2jserver.gameserver.network.serverpackets.MoveToPawn;
import com.l2jserver.gameserver.network.serverpackets.PlaySound;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.skills.SkillHolder;
import com.l2jserver.gameserver.templates.StatsSet;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

import javolution.util.FastList;

public class Baium extends L2AttackableAIScript
{
	private L2Character _target;
	private L2Skill _skill;
	private static final int STONE_BAIUM = 29025;
	private static final int ANGELIC_VORTEX = 31862;
	private static final int LIVE_BAIUM = 29020;
	private static final int ARCHANGEL = 29021;
	
	// Baium status tracking
	private static final byte ASLEEP = 0; // baium is in the stone version, waiting to be woken up. Entry is unlocked
	private static final byte AWAKE = 1; // baium is awake and fighting. Entry is locked.
	private static final byte DEAD = 2; // baium has been killed and has not yet spawned. Entry is locked
	
	// ETC Skills
	private static final SkillHolder BAIUM_PRESENT = new SkillHolder(4136, 1); // Baium's Gift
	private static final SkillHolder ANTI_STRIDER = new SkillHolder(4258, 1); // Hinder Strider
	
	//@formatter:off
	// fixed archangel spawnloc
	private final static int ANGEL_LOCATION[][] =
	{
		{114239,17168,10080,63544},
		{115780,15564,10080,13620},
		{114880,16236,10080,5400},
		{115168,17200,10080,0},
		{115792,16608,10080,0},
	};
	//@formatter:on
	
	private long _LastAttackVsBaiumTime = 0;
	private final List<L2Npc> _Minions = new ArrayList<L2Npc>(5);
	private L2BossZone _Zone;
	
	public Baium()
	{
		super(-1, Baium.class.getSimpleName(), "ai");
		registerMobs(LIVE_BAIUM);
		StartNpcs(STONE_BAIUM, ANGELIC_VORTEX);
		TalkNpcs(STONE_BAIUM, ANGELIC_VORTEX);
		_Zone = GrandBossManager.getInstance().getZone(113100, 14500, 10077);
		StatsSet info = GrandBossManager.getInstance().getStatsSet(LIVE_BAIUM);
		int status = GrandBossManager.getInstance().getBossStatus(LIVE_BAIUM);
		if (status == DEAD)
		{
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				startQuestTimer("baium_unlock", temp, null, null);
			}
			else
			{
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
				GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP);
			}
		}
		else if (status == AWAKE)
		{
			int loc_x = info.getInt("loc_x");
			int loc_y = info.getInt("loc_y");
			int loc_z = info.getInt("loc_z");
			int heading = info.getInt("heading");
			final int hp = info.getInt("currentHP");
			final int mp = info.getInt("currentMP");
			L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(LIVE_BAIUM, loc_x, loc_y, loc_z, heading, false, 0);
			GrandBossManager.getInstance().addBoss(baium);
			final L2Npc _baium = baium;
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				try
				{
					_baium.setCurrentHpMp(hp, mp);
					_baium.setIsInvul(true);
					_baium.setIsImmobilized(true);
					_baium.setRunning();
					_baium.broadcastPacket(new SocialAction(_baium, 2));
					startQuestTimer("baium_wakeup", 15000, _baium, null);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}, 100L);
		}
		else
		{
			addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case "exit_toi":
			{
				player.teleToLocation(108784, 16000, -4928);
				break;
			}
			case "baium_unlock":
			{
				GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP);
				addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0);
				break;
			}
			case "skill_range":
			{
				if (!npc.isCastingNow())
				{
					callSkillAI(npc);
				}
				break;
			}
			case "clean_player":
			{
				_target = getRandomTarget(npc);
				break;
			}
			case "baium_wakeup":
			{
				if (npc.getNpcId() == LIVE_BAIUM)
				{
					npc.broadcastPacket(new SocialAction(npc, 1));
					npc.broadcastPacket(new Earthquake(npc.getX(), npc.getY(), npc.getZ(), 40, 5));
					// start monitoring baium's inactivity
					_LastAttackVsBaiumTime = System.currentTimeMillis();
					startQuestTimer("baium_despawn", 60000, npc, null, true);
					startQuestTimer("skill_range", 500, npc, null, true);
					final L2Npc baium = npc;
					ThreadPoolManager.getInstance().scheduleGeneral(() ->
					{
						try
						{
							baium.setIsInvul(false);
							baium.setIsImmobilized(false);
							for (L2Npc minion : _Minions)
							{
								minion.setShowSummonAnimation(false);
							}
						}
						catch (Exception e)
						{
							_log.log(Level.WARNING, "", e);
						}
					}, 11100L);
					// TODO: NEed test
					if ((player != null) && player.isInsideRadius(npc, 16000, true, false))
					{
						npc.broadcastPacket(new SocialAction(npc, 1));
						broadcastNpcSay(npc, Say2.ALL, player.getName() + ", How dare you wake me! Now you shall die!");
						npc.setTarget(player);
						npc.doCast(BAIUM_PRESENT.getSkill());
					}
					for (L2PcInstance players : _Zone.getPlayersInside())
					{
						if (players.isHero())
						{
							_Zone.broadcastPacket(new ExShowScreenMessage("Not event the gods themselves could touch me, but you " + players.getName() + " dare challenge me ignorant mortal!", 4000));
							break;
						}
					}
					for (int[] element : ANGEL_LOCATION)
					{
						L2Npc angel = addSpawn(ARCHANGEL, element[0], element[1], element[2], element[3], false, 0, true);
						angel.setIsInvul(true);
						_Minions.add(angel);
					}
				}
				break;
			}
			case "baium_despawn":
			{
				if (npc.getNpcId() == LIVE_BAIUM)
				{
					// just in case the zone reference has been lost (somehow...), restore the reference
					if (_Zone == null)
					{
						_Zone = GrandBossManager.getInstance().getZone(113100, 14500, 10077);
					}
					if ((_LastAttackVsBaiumTime + 1800000) < System.currentTimeMillis())
					{
						npc.deleteMe(); // despawn the live-baium
						for (L2Npc minion : _Minions)
						{
							if (minion != null)
							{
								minion.getSpawn().stopRespawn();
								minion.deleteMe();
							}
						}
						_Minions.clear();
						addSpawn(STONE_BAIUM, 116033, 17447, 10104, 40188, false, 0); // spawn stone-baium
						GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, ASLEEP); // mark that Baium is not awake any more
						_Zone.oustAllPlayers();
						cancelQuestTimer("baium_despawn", npc, null);
					}
					else if (((_LastAttackVsBaiumTime + 300000) < System.currentTimeMillis()) && (npc.getCurrentHp() < ((npc.getMaxHp() * 3) / 4.0)))
					{
						npc.setIsCastingNow(false); // just in case
						npc.setTarget(npc);
						npc.doCast(SkillTable.getInstance().getInfo(4135, 1));
						npc.setIsCastingNow(true);
					}
					else if (!_Zone.isInsideZone(npc))
					{
						npc.teleToLocation(116033, 17447, 10104);
					}
				}
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		int npcId = npc.getNpcId();
		String htmltext = "";
		if (_Zone == null)
		{
			_Zone = GrandBossManager.getInstance().getZone(113100, 14500, 10077);
		}
		if (_Zone == null)
		{
			return "<html><body>Angelic Vortex:<br>You may not enter while admin disabled this zone</body></html>";
		}
		if ((npcId == STONE_BAIUM) && (GrandBossManager.getInstance().getBossStatus(LIVE_BAIUM) == ASLEEP))
		{
			if (_Zone.isPlayerAllowed(player))
			{
				// once Baium is awaken, no more people may enter until he dies, the server reboots, or
				// 30 minutes pass with no attacks made against Baium.
				GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, AWAKE);
				npc.deleteMe();
				L2GrandBossInstance baium = (L2GrandBossInstance) addSpawn(LIVE_BAIUM, npc, true);
				GrandBossManager.getInstance().addBoss(baium);
				final L2Npc _baium = baium;
				ThreadPoolManager.getInstance().scheduleGeneral(() ->
				{
					try
					{
						_baium.setIsInvul(true);
						_baium.setRunning();
						_baium.broadcastPacket(new SocialAction(_baium, 2));
						startQuestTimer("baium_wakeup", 15000, _baium, null);
						_baium.setShowSummonAnimation(false);
					}
					catch (Throwable e)
					{
						_log.log(Level.WARNING, "", e);
					}
				}, 100L);
			}
			else
			{
				htmltext = "Conditions are not right to wake up Baium";
			}
		}
		else if (npcId == ANGELIC_VORTEX)
		{
			if (player.isFlying())
			{
				// print "Player "+player.getName()+" attempted to enter Baium's lair while flying!";
				return "<html><body>Angelic Vortex:<br>You may not enter while flying a wyvern</body></html>";
			}
			
			if ((GrandBossManager.getInstance().getBossStatus(LIVE_BAIUM) == ASLEEP) && (player.getQuestState("Baium").getQuestItemsCount(4295) > 0)) // bloody fabric
			{
				player.getQuestState("Baium").takeItems(4295, 1);
				// allow entry for the player for the next 30 secs (more than enough time for the TP to happen)
				// Note: this just means 30secs to get in, no limits on how long it takes before we get out.
				_Zone.allowPlayerEntry(player, 30);
				player.teleToLocation(113100, 14500, 10077);
			}
			else
			{
				npc.showChatWindow(player, 1);
			}
		}
		return htmltext;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		if (npc.isInvul())
		{
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			return null;
		}
		else if ((npc.getNpcId() == LIVE_BAIUM) && !npc.isInvul())
		{
			if (!npc.isCastingNow())
			{
				callSkillAI(npc);
			}
		}
		return super.onSpellFinished(npc, player, skill);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		npc.disableCoreAI(true);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if (!_Zone.isInsideZone(attacker))
		{
			attacker.reduceCurrentHp(attacker.getCurrentHp(), attacker, false, false, null);
			return super.onAttack(npc, attacker, damage, isPet);
		}
		if (npc.isInvul())
		{
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			return super.onAttack(npc, attacker, damage, isPet);
		}
		else if ((npc.getNpcId() == LIVE_BAIUM) && !npc.isInvul())
		{
			if (attacker.getMountType() == 1)
			{
				int sk_4258 = 0;
				L2Effect[] effects = attacker.getAllEffects();
				if ((effects != null) && (effects.length != 0))
				{
					for (L2Effect e : effects)
					{
						if (e.getSkill().getId() == 4258)
						{
							sk_4258 = 1;
						}
					}
				}
				if (sk_4258 == 0)
				{
					npc.setTarget(attacker);
					npc.doCast(ANTI_STRIDER.getSkill());
				}
			}
			// update a variable with the last action against baium
			_LastAttackVsBaiumTime = System.currentTimeMillis();
			if (!npc.isCastingNow())
			{
				callSkillAI(npc);
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		cancelQuestTimer("baium_despawn", npc, null);
		npc.broadcastPacket(new PlaySound(1, "BS01_D", 1, npc.getObjectId(), npc.getX(), npc.getY(), npc.getZ()));
		// spawn the "Teleportation Cubic" for 15 minutes (to allow players to exit the lair)
		addSpawn(29055, 115203, 16620, 10078, 0, false, 900000); //// should we teleport everyone out if the cubic despawns??
		// "lock" baium for 5 days and 1 to 8 hours [i.e. 432,000,000 + 1*3,600,000 + random-less-than(8*3,600,000) millisecs]
		long respawnTime = (long) Config.INTERVAL_OF_BAIUM_SPAWN + Rnd.get(Config.RANDOM_OF_BAIUM_SPAWN);
		GrandBossManager.getInstance().setBossStatus(LIVE_BAIUM, DEAD);
		startQuestTimer("baium_unlock", respawnTime, null, null);
		// also save the respawn time so that the info is maintained past reboots
		StatsSet info = GrandBossManager.getInstance().getStatsSet(LIVE_BAIUM);
		info.set("respawn_time", (System.currentTimeMillis()) + respawnTime);
		GrandBossManager.getInstance().setStatsSet(LIVE_BAIUM, info);
		for (L2Npc minion : _Minions)
		{
			if (minion != null)
			{
				minion.getSpawn().stopRespawn();
				minion.deleteMe();
			}
		}
		_Minions.clear();
		if (getQuestTimer("skill_range", npc, null) != null)
		{
			getQuestTimer("skill_range", npc, null).cancel();
		}
		return super.onKill(npc, killer, isPet);
	}
	
	public L2Character getRandomTarget(L2Npc npc)
	{
		FastList<L2Character> result = FastList.newInstance();
		Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
		{
			for (L2Object obj : objs)
			{
				if ((obj instanceof L2Playable) || (obj instanceof L2DecoyInstance))
				{
					if (obj instanceof L2PcInstance)
					{
						if (((L2PcInstance) obj).getAppearance().getInvisible())
						{
							continue;
						}
					}
					
					if (((((L2Character) obj).getZ() < (npc.getZ() - 100)) && (((L2Character) obj).getZ() > (npc.getZ() + 100))) || !(GeoData.getInstance().canSeeTarget(((L2Character) obj).getX(), ((L2Character) obj).getY(), ((L2Character) obj).getZ(), npc.getX(), npc.getY(), npc.getZ())))
					{
						continue;
					}
				}
				if ((obj instanceof L2Playable) || (obj instanceof L2DecoyInstance))
				{
					if (Util.checkIfInRange(9000, npc, obj, true) && !((L2Character) obj).isDead())
					{
						result.add((L2Character) obj);
					}
				}
			}
		}
		if (result.isEmpty())
		{
			for (L2Npc minion : _Minions)
			{
				if (minion != null)
				{
					result.add(minion);
				}
			}
		}
		
		if (result.isEmpty())
		{
			FastList.recycle(result);
			return null;
		}
		
		Object[] characters = result.toArray();
		QuestTimer timer = getQuestTimer("clean_player", npc, null);
		if (timer != null)
		{
			timer.cancel();
		}
		startQuestTimer("clean_player", 20000, npc, null);
		L2Character target = (L2Character) characters[Rnd.get(characters.length)];
		FastList.recycle(result);
		return target;
		
	}
	
	public synchronized void callSkillAI(L2Npc npc)
	{
		if (npc.isInvul() || npc.isCastingNow())
		{
			return;
		}
		
		if ((_target == null) || _target.isDead() || !(_Zone.isInsideZone(_target)))
		{
			_target = getRandomTarget(npc);
			if (_target != null)
			{
				_skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
			}
		}
		
		L2Character target = _target;
		L2Skill skill = _skill;
		if (skill == null)
		{
			skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
		}
		if ((target == null) || target.isDead() || !(_Zone.isInsideZone(target)))
		{
			npc.setIsCastingNow(false);
			return;
		}
		
		if (Util.checkIfInRange(skill.getCastRange(), npc, target, true))
		{
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			npc.setTarget(target);
			npc.setIsCastingNow(true);
			_target = null;
			_skill = null;
			if (getDist(skill.getCastRange()) > 0)
			{
				npc.broadcastPacket(new MoveToPawn(npc, target, getDist(skill.getCastRange())));
			}
			try
			{
				Thread.sleep(1000);
				npc.stopMove(null);
				npc.doCast(skill);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			npc.getAI().setIntention(AI_INTENTION_FOLLOW, target, null);
			npc.setIsCastingNow(false);
		}
	}
	
	public int getRandomSkill(L2Npc npc)
	{
		int skill;
		if (npc.getCurrentHp() > ((npc.getMaxHp() * 3) / 4.0))
		{
			if (Rnd.get(100) < 10)
			{
				skill = 4128;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4129;
			}
			else
			{
				skill = 4127;
			}
		}
		else if (npc.getCurrentHp() > ((npc.getMaxHp() * 2) / 4.0))
		{
			if (Rnd.get(100) < 10)
			{
				skill = 4131;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4128;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4129;
			}
			else
			{
				skill = 4127;
			}
		}
		else if (npc.getCurrentHp() > ((npc.getMaxHp() * 1) / 4.0))
		{
			if (Rnd.get(100) < 10)
			{
				skill = 4130;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4131;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4128;
			}
			else if (Rnd.get(100) < 10)
			{
				skill = 4129;
			}
			else
			{
				skill = 4127;
			}
		}
		else if (Rnd.get(100) < 10)
		{
			skill = 4130;
		}
		else if (Rnd.get(100) < 10)
		{
			skill = 4131;
		}
		else if (Rnd.get(100) < 10)
		{
			skill = 4128;
		}
		else if (Rnd.get(100) < 10)
		{
			skill = 4129;
		}
		else
		{
			skill = 4127;
		}
		return skill;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (npc.isInvul())
		{
			npc.getAI().setIntention(AI_INTENTION_IDLE);
			return null;
		}
		npc.setTarget(caster);
		return super.onSkillSee(npc, caster, skill, targets, isPet);
	}
	
	public int getDist(int range)
	{
		int dist = 0;
		switch (range)
		{
			case -1:
				break;
			case 100:
				dist = 85;
				break;
			default:
				dist = range - 85;
				break;
		}
		return dist;
	}
}