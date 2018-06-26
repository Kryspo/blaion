package freya.IceQueensCastle;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.InstanceManager.InstanceWorld;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage2;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.util.Rnd;

import javolution.util.FastList;
import quests.Q10285_MeetingSirra.Q10285_MeetingSirra;

public class IceQueensCastle extends Quest
{
	private static final int INSTANCEID = 137;
	
	private static final int JINIA2 = 32781;
	private static final int FREYA = 18847;
	private static final int JINIA_GUARD_1 = 18848;
	private static final int JINIA_GUARD_2 = 18849;
	private static final int JINIA_GUARD_3 = 18926;
	private static final int ICE_KNIGHT = 22767;
	private static final int FREYA_CONTROLLER = 18930;
	
	private static final int[] ENTRY_POINT =
	{
		114000,
		-112357,
		-11200
	};
	
	private class FDWorld extends InstanceWorld
	{
		public L2Attackable _freya = null;
		public L2Attackable _jinia_guard1 = null;
		public L2Attackable _jinia_guard2 = null;
		public L2Attackable _jinia_guard3 = null;
		public L2Attackable _jinia_guard4 = null;
		public L2Attackable _jinia_guard5 = null;
		public L2Attackable _jinia_guard6 = null;
		public L2Attackable _freya_guard1 = null;
		public L2Attackable _freya_guard2 = null;
		public L2Attackable _freya_guard3 = null;
		public L2Attackable _freya_guard4 = null;
		public L2Attackable _freya_guard5 = null;
		public L2Attackable _freya_controller = null;
		
		public FDWorld()
		{
		}
	}
	
	protected class teleCoord
	{
		int instanceId;
		int x;
		int y;
		int z;
	}
	
	private void teleportplayer(L2PcInstance player, teleCoord teleto)
	{
		player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
		player.setInstanceId(teleto.instanceId);
		player.teleToLocation(teleto.x, teleto.y, teleto.z);
		return;
	}
	
	public IceQueensCastle()
	{
		super(-1, IceQueensCastle.class.getSimpleName(), "freya");
		StartNpcs(JINIA2);
		TalkNpcs(JINIA2);
		AggroRangeEnterNpcs(FREYA_CONTROLLER);
		AttackNpcs(JINIA_GUARD_1, JINIA_GUARD_2, JINIA_GUARD_3);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		
		if (npc.getNpcId() == JINIA2)
		{
			teleCoord tele = new teleCoord();
			tele.x = ENTRY_POINT[0];
			tele.y = ENTRY_POINT[1];
			tele.z = ENTRY_POINT[2];
			
			if (enterInstance(player, "IceQueensCastle.xml", tele) <= 0)
			{
				return "32781-10.htm";
			}
		}
		return "";
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof FDWorld)
		{
			FDWorld world = (FDWorld) tmpworld;
			switch (event)
			{
				case "check_guards":
					if (((world._freya_guard1 == null) || world._freya_guard1.isDead()) && (getQuestTimer("spawn_ice_guard1", null, player) == null))
					{
						startQuestTimer("spawn_ice_guard1", 30000, null, player);
					}
					if (((world._freya_guard2 == null) || world._freya_guard2.isDead()) && (getQuestTimer("spawn_ice_guard2", null, player) == null))
					{
						startQuestTimer("spawn_ice_guard2", 30000, null, player);
					}
					if (((world._freya_guard3 == null) || world._freya_guard3.isDead()) && (getQuestTimer("spawn_ice_guard3", null, player) == null))
					{
						startQuestTimer("spawn_ice_guard3", 30000, null, player);
					}
					if (((world._freya_guard4 == null) || world._freya_guard4.isDead()) && (getQuestTimer("spawn_ice_guard4", null, player) == null))
					{
						startQuestTimer("spawn_ice_guard4", 30000, null, player);
					}
					if (((world._freya_guard5 == null) || world._freya_guard5.isDead()) && (getQuestTimer("spawn_ice_guard5", null, player) == null))
					{
						startQuestTimer("spawn_ice_guard5", 30000, null, player);
					}
					
					if (((world._jinia_guard1 == null) || world._jinia_guard1.isDead()) && (getQuestTimer("spawn_guard1", null, player) == null))
					{
						startQuestTimer("spawn_guard1", 60000, null, player);
					}
					else
					{
						world._jinia_guard1.stopHating(player);
					}
					if (((world._jinia_guard2 == null) || world._jinia_guard2.isDead()) && (getQuestTimer("spawn_guard2", null, player) == null))
					{
						startQuestTimer("spawn_guard2", 45000, null, player);
					}
					else
					{
						world._jinia_guard2.stopHating(player);
					}
					if (((world._jinia_guard3 == null) || world._jinia_guard3.isDead()) && (getQuestTimer("spawn_guard3", null, player) == null))
					{
						startQuestTimer("spawn_guard3", 45000, null, player);
					}
					else
					{
						world._jinia_guard3.stopHating(player);
					}
					if (((world._jinia_guard4 == null) || world._jinia_guard4.isDead()) && (getQuestTimer("spawn_guard4", null, player) == null))
					{
						startQuestTimer("spawn_guard4", 60000, null, player);
					}
					else
					{
						world._jinia_guard4.stopHating(player);
					}
					if (((world._jinia_guard5 == null) || world._jinia_guard5.isDead()) && (getQuestTimer("spawn_guard5", null, player) == null))
					{
						startQuestTimer("spawn_guard5", 45000, null, player);
					}
					else
					{
						world._jinia_guard5.stopHating(player);
					}
					if (((world._jinia_guard6 == null) || world._jinia_guard6.isDead()) && (getQuestTimer("spawn_guard6", null, player) == null))
					{
						startQuestTimer("spawn_guard6", 45000, null, player);
					}
					else
					{
						world._jinia_guard6.stopHating(player);
					}
					
					break;
				case "spawn_ice_guard1":
					world._freya_guard1 = (L2Attackable) addSpawn(ICE_KNIGHT, 114713, -115109, -11198, 16456, false, 0, false, world.instanceId);
					L2Character target = getRandomTargetFreya(world);
					world._freya_guard1.addDamageHate(target, 9999, 9999);
					world._freya_guard1.setRunning();
					world._freya_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
					break;
				case "spawn_ice_guard2":
					world._freya_guard2 = (L2Attackable) addSpawn(ICE_KNIGHT, 114008, -115080, -11198, 3568, false, 0, false, world.instanceId);
					L2Character target1 = getRandomTargetFreya(world);
					world._freya_guard2.addDamageHate(target1, 9999, 9999);
					world._freya_guard2.setRunning();
					world._freya_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target1);
					break;
				case "spawn_ice_guard3":
					world._freya_guard3 = (L2Attackable) addSpawn(ICE_KNIGHT, 114422, -115508, -11198, 12400, false, 0, false, world.instanceId);
					L2Character target2 = getRandomTargetFreya(world);
					world._freya_guard3.addDamageHate(target2, 9999, 9999);
					world._freya_guard3.setRunning();
					world._freya_guard3.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target2);
					break;
				case "spawn_ice_guard4":
					world._freya_guard4 = (L2Attackable) addSpawn(ICE_KNIGHT, 115023, -115508, -11198, 20016, false, 0, false, world.instanceId);
					L2Character target3 = getRandomTargetFreya(world);
					world._freya_guard4.addDamageHate(target3, 9999, 9999);
					world._freya_guard4.setRunning();
					world._freya_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target3);
					break;
				case "spawn_ice_guard5":
					world._freya_guard5 = (L2Attackable) addSpawn(ICE_KNIGHT, 115459, -115079, -11198, 27936, false, 0, false, world.instanceId);
					L2Character target4 = getRandomTargetFreya(world);
					world._freya_guard5.addDamageHate(target4, 9999, 9999);
					world._freya_guard5.setRunning();
					world._freya_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target4);
					break;
				case "spawn_guard1":
					world._jinia_guard1 = (L2Attackable) addSpawn(JINIA_GUARD_1, 114861, -113615, -11198, -21832, false, 0, false, world.instanceId);
					world._jinia_guard1.setRunning();
					L2Character target5 = getRandomTargetGuard(world);
					world._jinia_guard1.addDamageHate(target5, 9999, 9999);
					world._jinia_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target5);
					break;
				case "spawn_guard2":
					world._jinia_guard2 = (L2Attackable) addSpawn(JINIA_GUARD_2, 114950, -113647, -11198, -20880, false, 0, false, world.instanceId);
					world._jinia_guard2.setRunning();
					L2Character target6 = getRandomTargetGuard(world);
					world._jinia_guard2.addDamageHate(target6, 9999, 9999);
					world._jinia_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target6);
					break;
				case "spawn_guard3":
					world._jinia_guard3 = (L2Attackable) addSpawn(JINIA_GUARD_3, 115041, -113694, -11198, -22440, false, 0, false, world.instanceId);
					world._jinia_guard3.setRunning();
					L2Character target7 = getRandomTargetGuard(world);
					world._jinia_guard3.addDamageHate(target7, 9999, 9999);
					world._jinia_guard3.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target7);
					break;
				case "spawn_guard4":
					world._jinia_guard4 = (L2Attackable) addSpawn(JINIA_GUARD_1, 114633, -113619, -11198, -12224, false, 0, false, world.instanceId);
					world._jinia_guard4.setRunning();
					L2Character target8 = getRandomTargetGuard(world);
					world._jinia_guard4.addDamageHate(target8, 9999, 9999);
					world._jinia_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target8);
					break;
				case "spawn_guard5":
					world._jinia_guard5 = (L2Attackable) addSpawn(JINIA_GUARD_2, 114540, -113654, -11198, -12880, false, 0, false, world.instanceId);
					world._jinia_guard5.setRunning();
					L2Character target9 = getRandomTargetGuard(world);
					world._jinia_guard5.addDamageHate(target9, 9999, 9999);
					world._jinia_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target9);
					break;
				case "spawn_guard6":
					world._jinia_guard6 = (L2Attackable) addSpawn(JINIA_GUARD_3, 114446, -113698, -11198, -11264, false, 0, false, world.instanceId);
					world._jinia_guard6.setRunning();
					L2Character target10 = getRandomTargetGuard(world);
					world._jinia_guard6.addDamageHate(target10, 9999, 9999);
					world._jinia_guard6.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target10);
					break;
				case "call_freya_skill":
					// call freya skill
					L2Object target11 = world._freya.getTarget();
					if ((target11 != null) && (player != null) && (target11.getObjectId() == player.getObjectId()) && !world._freya.isCastingNow())
					{
						if (Rnd.get(100) < 40)
						{
							world._freya.doCast(SkillTable.getInstance().getInfo(6278, 1));
						}
					}
					break;
				case "go_guards":
					CreatureSay ns = new CreatureSay(world._jinia_guard1.getObjectId(), 0, world._jinia_guard1.getName(), player.getAppearance().getVisibleName() + ". May the protection of the gods be upon you!");
					player.sendPacket(ns);
					
					world._jinia_guard1.setRunning();
					world._jinia_guard2.setRunning();
					world._jinia_guard3.setRunning();
					world._jinia_guard4.setRunning();
					world._jinia_guard5.setRunning();
					world._jinia_guard6.setRunning();
					world._jinia_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114673, -113374, -11200, 0));
					world._jinia_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114745, -113383, -11200, 0));
					world._jinia_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114711, -113382, -11200, 0));
					world._jinia_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114662, -113382, -11200, 0));
					
					startQuestTimer("go_fight", 3000, null, player);
					break;
				case "go_fight":
					world._jinia_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114673, -114324, -11200, 0));
					world._jinia_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114745, -114324, -11200, 0));
					world._jinia_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114711, -114324, -11200, 0));
					world._jinia_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114662, -114324, -11200, 0));
					world._jinia_guard3.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(115041, -114324, -11200, 0));
					world._jinia_guard6.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114446, -114324, -11200, 0));
					
					world._freya_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114713, -114920, -11200, 0));
					world._freya_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114008, -114920, -11200, 0));
					world._freya_guard3.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114422, -114920, -11200, 0));
					world._freya_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(115023, -114920, -11200, 0));
					world._freya_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(115459, -114920, -11200, 0));
					world._freya.setRunning();
					world._freya.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(114722, -114798, -11205, 15956));
					startQuestTimer("freya", 17000, null, player);
					startQuestTimer("go_fight2", 7000, null, player);
					break;
				case "go_fight2":
					world._jinia_guard1.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					world._jinia_guard4.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					world._jinia_guard2.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					world._jinia_guard5.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					world._jinia_guard3.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					world._jinia_guard6.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getRandomTargetGuard(world));
					break;
				case "freya":
					L2Character target12 = getRandomTargetFreya(world);
					world._freya.addDamageHate(target12, 9999, 9999);
					world._freya.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target12);
					break;
				case "end_inst":
					cancelQuestTimer("spawn_guard1", null, player);
					cancelQuestTimer("spawn_guard2", null, player);
					cancelQuestTimer("spawn_guard3", null, player);
					cancelQuestTimer("spawn_guard4", null, player);
					cancelQuestTimer("spawn_guard5", null, player);
					cancelQuestTimer("spawn_guard6", null, player);
					cancelQuestTimer("check_guards", null, player);
					cancelQuestTimer("spawn_ice_guard1", null, player);
					cancelQuestTimer("spawn_ice_guard2", null, player);
					cancelQuestTimer("spawn_ice_guard3", null, player);
					cancelQuestTimer("spawn_ice_guard4", null, player);
					cancelQuestTimer("spawn_ice_guard5", null, player);
					cancelQuestTimer("call_freya_skill", null, player);
					world._freya.abortAttack();
					world._freya.abortCast();
					world._freya.setTarget(player);
					world._freya.doCast(SkillTable.getInstance().getInfo(6275, 1));
					startQuestTimer("movie", 7000, null, player);
					break;
				case "movie":
					player.sendPacket(new ExShowScreenMessage2(1801111, 3000, ExShowScreenMessage2.ScreenMessageAlign.MIDDLE_CENTER, true, false, -1, true));
					startQuestTimer("movie2", 3000, null, player);
					
					QuestState st = player.getQuestState(Q10285_MeetingSirra.class.getSimpleName());
					if (st != null)
					{
						// st.set("cond", "10");
						// st.playSound("ItemSound.quest_middle");
						// st.set("progress", "3");
						
						st.setMemoState(3);
						st.setCond(10, true);
					}
					break;
				case "movie2":
					player.showQuestMovie(21);
					player.setInstanceId(0);
					player.teleToLocation(113851, -108987, -837);
					InstanceManager.getInstance().destroyInstance(world.instanceId);
					break;
			}
		}
		return null;
	}
	
	protected int enterInstance(L2PcInstance player, String template, teleCoord teleto)
	{
		int instanceId = 0;
		// check for existing instances for this player
		InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
		// existing instance
		if (world != null)
		{
			if (!(world instanceof FDWorld))
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ALREADY_ENTERED_ANOTHER_INSTANCE_CANT_ENTER));
				return 0;
			}
			teleto.instanceId = world.instanceId;
			teleportplayer(player, teleto);
			return instanceId;
		}
		// New instance
		else
		{
			if (player.getLevel() < 82)
			{
				return 0;
			}
			instanceId = InstanceManager.getInstance().createDynamicInstance(template);
			world = new FDWorld();
			
			world.instanceId = instanceId;
			world.templateId = INSTANCEID;
			world.status = 0;
			
			world.allowed.add(player.getObjectId());
			
			InstanceManager.getInstance().addWorld(world);
			_log.info("Freya started " + template + " Instance: " + instanceId + " created by player: " + player.getName());
			teleto.instanceId = instanceId;
			teleportplayer(player, teleto);
			world.allowed.add(player.getObjectId());
			spawnFirst((FDWorld) world);
			
			return instanceId;
		}
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		InstanceWorld tmpworld = InstanceManager.getInstance().getPlayerWorld(player);
		if (tmpworld instanceof FDWorld)
		{
			FDWorld world = (FDWorld) tmpworld;
			if (npc.getNpcId() == FREYA_CONTROLLER)
			{
				world._jinia_guard1.setIsImmobilized(false);
				world._jinia_guard2.setIsImmobilized(false);
				world._jinia_guard3.setIsImmobilized(false);
				world._jinia_guard4.setIsImmobilized(false);
				world._jinia_guard5.setIsImmobilized(false);
				world._jinia_guard6.setIsImmobilized(false);
				world._freya.setIsImmobilized(false);
				world._freya_guard1.setIsImmobilized(false);
				world._freya_guard2.setIsImmobilized(false);
				world._freya_guard3.setIsImmobilized(false);
				world._freya_guard4.setIsImmobilized(false);
				world._freya_guard5.setIsImmobilized(false);
				
				startQuestTimer("go_guards", 300, npc, player);
				startQuestTimer("end_inst", 120000, npc, player);
				startQuestTimer("check_guards", 1000, null, player, true);
				startQuestTimer("call_freya_skill", 7000, null, player, true);
				world._freya_controller.deleteMe();
				world._freya_controller = null;
			}
		}
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet, L2Skill skill)
	{
		switch (npc.getNpcId())
		{
			case JINIA_GUARD_1:
				npc.setCurrentHp(npc.getCurrentHp() + damage);
				((L2Attackable) npc).stopHating(attacker);
				break;
			case JINIA_GUARD_2:
				npc.setCurrentHp(npc.getCurrentHp() + damage);
				((L2Attackable) npc).stopHating(attacker);
				break;
			case JINIA_GUARD_3:
				npc.setCurrentHp(npc.getCurrentHp() + damage);
				((L2Attackable) npc).stopHating(attacker);
				break;
		}
		return onAttack(npc, attacker, damage, isPet);
	}
	
	private void spawnFirst(FDWorld world)
	{
		world._freya = (L2Attackable) addSpawn(FREYA, 114722, -114798, -11205, 15956, false, 0, false, world.instanceId);
		world._freya.teleToLocation(114720, -117085, -11088, 15956, false);
		world._freya.setIsImmobilized(true);
		
		world._jinia_guard1 = (L2Attackable) addSpawn(JINIA_GUARD_1, 114861, -113615, -11198, -21832, false, 0, false, world.instanceId);
		world._jinia_guard1.setIsImmobilized(true);
		world._jinia_guard2 = (L2Attackable) addSpawn(JINIA_GUARD_2, 114950, -113647, -11198, -20880, false, 0, false, world.instanceId);
		world._jinia_guard2.setIsImmobilized(true);
		world._jinia_guard3 = (L2Attackable) addSpawn(JINIA_GUARD_3, 115041, -113694, -11198, -22440, false, 0, false, world.instanceId);
		world._jinia_guard3.setIsImmobilized(true);
		world._jinia_guard4 = (L2Attackable) addSpawn(JINIA_GUARD_1, 114633, -113619, -11198, -12224, false, 0, false, world.instanceId);
		world._jinia_guard4.setIsImmobilized(true);
		world._jinia_guard5 = (L2Attackable) addSpawn(JINIA_GUARD_2, 114540, -113654, -11198, -12880, false, 0, false, world.instanceId);
		world._jinia_guard5.setIsImmobilized(true);
		world._jinia_guard6 = (L2Attackable) addSpawn(JINIA_GUARD_3, 114446, -113698, -11198, -11264, false, 0, false, world.instanceId);
		world._jinia_guard6.setIsImmobilized(true);
		
		world._freya_guard1 = (L2Attackable) addSpawn(ICE_KNIGHT, 114713, -115109, -11198, 16456, false, 0, false, world.instanceId);
		world._freya_guard1.setIsImmobilized(true);
		world._freya_guard1.setRunning();
		world._freya_guard2 = (L2Attackable) addSpawn(ICE_KNIGHT, 114008, -115080, -11198, 3568, false, 0, false, world.instanceId);
		world._freya_guard2.setIsImmobilized(true);
		world._freya_guard2.setRunning();
		world._freya_guard3 = (L2Attackable) addSpawn(ICE_KNIGHT, 114422, -115508, -11198, 12400, false, 0, false, world.instanceId);
		world._freya_guard3.setIsImmobilized(true);
		world._freya_guard3.setRunning();
		world._freya_guard4 = (L2Attackable) addSpawn(ICE_KNIGHT, 115023, -115508, -11198, 20016, false, 0, false, world.instanceId);
		world._freya_guard4.setIsImmobilized(true);
		world._freya_guard4.setRunning();
		world._freya_guard5 = (L2Attackable) addSpawn(ICE_KNIGHT, 115459, -115079, -11198, 27936, false, 0, false, world.instanceId);
		world._freya_guard5.setIsImmobilized(true);
		world._freya_guard5.setRunning();
		
		world._freya_controller = (L2Attackable) addSpawn(FREYA_CONTROLLER, 114713, -113578, -11200, 27936, false, 0, false, world.instanceId);
		world._freya_controller.setIsImmobilized(true);
		
		InstanceManager.getInstance().getInstance(world.instanceId).getDoor(23140101).openMe();
	}
	
	private L2Npc getRandomTargetFreya(FDWorld world)
	{
		FastList<L2Npc> npcList = new FastList<L2Npc>();
		L2Npc victim = null;
		victim = world._jinia_guard1;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._jinia_guard2;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._jinia_guard3;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._jinia_guard4;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._jinia_guard5;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._jinia_guard6;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		if (npcList.size() > 0)
		{
			return npcList.get(Rnd.get(npcList.size() - 1));
		}
		else
		{
			return null;
		}
	}
	
	private L2Npc getRandomTargetGuard(FDWorld world)
	{
		FastList<L2Npc> npcList = new FastList<L2Npc>();
		L2Npc victim = null;
		victim = world._freya_guard1;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._freya_guard2;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._freya_guard3;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._freya_guard4;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		victim = world._freya_guard5;
		if ((victim != null) && !victim.isDead())
		{
			npcList.add(victim);
		}
		if (npcList.size() > 0)
		{
			return npcList.get(Rnd.get(npcList.size() - 1));
		}
		return null;
	}
}