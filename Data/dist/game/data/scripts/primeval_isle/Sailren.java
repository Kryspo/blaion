package primeval_isle;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.SkillTable;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.L2CharPosition;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.SpecialCamera;
import com.l2jserver.gameserver.templates.StatsSet;
import com.l2jserver.util.Rnd;

import ai.L2AttackableAIScript;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class Sailren extends L2AttackableAIScript
{
	private static final int SAILREN = 29065;
	private static final int VELOCIRAPTOR = 22218;
	private static final int PTEROSAUR = 22199;
	private static final int TYRANNOSAURUS = 22217;
	private static final int STATUE = 32109;
	
	private static final byte DORMANT = 0;
	private static final byte WAITING = 1;
	private static final byte FIGHTING = 2;
	private static final byte DEAD = 3;
	
	private static final String qn = "sailren";
	
	private static long _LastAction = 0;
	
	public Sailren()
	{
		super(-1, qn, "primeval_isle");
		int[] mob =
		{
			SAILREN,
			VELOCIRAPTOR,
			PTEROSAUR,
			TYRANNOSAURUS
		};
		registerMobs(mob);
		TalkNpcs(STATUE);
		StartNpcs(STATUE);
		StatsSet info = GrandBossManager.getInstance().getStatsSet(SAILREN);
		int status = GrandBossManager.getInstance().getBossStatus(SAILREN);
		if (status == DEAD)
		{
			long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
			if (temp > 0)
			{
				startQuestTimer("sailren_unlock", temp, null, null);
			}
			else
			{
				GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
			}
		}
		else
		{
			GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		long temp = 0;
		switch (event)
		{
			case "waiting":
				GrandBossManager.getInstance().setBossStatus(SAILREN, FIGHTING);
				L2Npc mob1 = addSpawn(VELOCIRAPTOR, 27852, -5536, -1983, 44732, false, 0);
				startQuestTimer("start", 0, mob1, null);
				break;
			case "waiting2":
				L2Npc mob2 = addSpawn(PTEROSAUR, 27852, -5536, -1983, 44732, false, 0);
				startQuestTimer("start", 0, mob2, null);
				break;
			case "waiting3":
				L2Npc mob3 = addSpawn(TYRANNOSAURUS, 27852, -5536, -1983, 44732, false, 0);
				startQuestTimer("start", 0, mob3, null);
				break;
			case "waiting_boss":
				L2GrandBossInstance sailren = (L2GrandBossInstance) addSpawn(SAILREN, 27734, -6938, -1982, 44732, false, 0);
				GrandBossManager.getInstance().addBoss(sailren);
				startQuestTimer("start2", 0, sailren, null);
				break;
			case "start":
				npc.setRunning();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(27628, -6109, -1982, 44732));
				startQuestTimer("mob_has_arrived", 200, npc, null, true);
				break;
			case "start2":
				npc.setRunning();
				npc.setIsInvul(true);
				npc.setIsParalyzed(true);
				npc.setIsImmobilized(true);
				startQuestTimer("camera_1", 2000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 0, 32, 2000, 11000, 0, 0, 1, 0));
				break;
			case "action_1":
				npc.broadcastPacket(new SocialAction(npc, 2));
				startQuestTimer("camera_6", 2500, npc, null);
				break;
			case "camera_1":
				npc.setTarget(npc);
				npc.setIsParalyzed(false);
				npc.doCast(SkillTable.getInstance().getInfo(5118, 1));
				npc.setIsParalyzed(true);
				startQuestTimer("camera_2", 4000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 90, 24, 4000, 11000, 0, 0, 1, 0));
				break;
			case "camera_2":
				npc.setTarget(npc);
				npc.setIsParalyzed(false);
				npc.doCast(SkillTable.getInstance().getInfo(5118, 1));
				npc.setIsParalyzed(true);
				startQuestTimer("camera_3", 4000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 160, 16, 4000, 11000, 0, 0, 1, 0));
				break;
			case "camera_3":
				npc.setTarget(npc);
				npc.setIsParalyzed(false);
				npc.doCast(SkillTable.getInstance().getInfo(5118, 1));
				npc.setIsParalyzed(true);
				startQuestTimer("camera_4", 4000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 250, 8, 4000, 11000, 0, 0, 1, 0));
				break;
			case "camera_4":
				npc.setTarget(npc);
				npc.setIsParalyzed(false);
				npc.doCast(SkillTable.getInstance().getInfo(5118, 1));
				npc.setIsParalyzed(true);
				startQuestTimer("camera_5", 4000, npc, null);
				npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 300, 340, 0, 4000, 11000, 0, 0, 1, 0));
				break;
			case "camera_5":
				npc.broadcastPacket(new SocialAction(npc, 2));
				startQuestTimer("camera_6", 5000, npc, null);
				break;
			case "camera_6":
				npc.setIsInvul(false);
				npc.setIsParalyzed(false);
				npc.setIsImmobilized(false);
				_LastAction = System.currentTimeMillis();
				startQuestTimer("sailren_despawn", 30000, npc, null, true);
				break;
			case "sailren_despawn":
				temp = (System.currentTimeMillis() - _LastAction);
				if (temp > 600000)
				{
					npc.deleteMe();
					GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
					cancelQuestTimer("sailren_despawn", npc, null);
					
				}
				break;
			case "mob_has_arrived":
				int dx = Math.abs(npc.getX() - 27628);
				int dy = Math.abs(npc.getY() + 6109);
				if ((dx <= 10) && (dy <= 10))
				{
					npc.setIsImmobilized(true);
					startQuestTimer("action_1", 500, npc, null);
					npc.getSpawn().setXLocation(27628);
					npc.getSpawn().setYLocation(-6109);
					npc.getSpawn().setZLocation(-1982);
					npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
					cancelQuestTimer("mob_has_arrived", npc, null);
				}
				break;
			case "spawn_cubes":
				addSpawn(32107, 27734, -6838, -1982, 0, false, 600000);
				break;
			case "sailren_unlock":
				GrandBossManager.getInstance().setBossStatus(SAILREN, DORMANT);
				break;
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		
		if (st == null)
		{
			newQuestState(player);
		}
		
		if (npc.getNpcId() == STATUE)
		{
			if ((GrandBossManager.getInstance().getBossStatus(SAILREN) == DORMANT) || (GrandBossManager.getInstance().getBossStatus(SAILREN) == WAITING))
			{
				if (player.isFlying())
				{
					htmltext = "<html><body>Stone Statue of Shilen:<br>You can't be teleported when you're flying</body></html>";
				}
				else if (player.getQuestState("sailren").hasQuestItems(8784))
				{
					player.getQuestState("sailren").takeItems(8784, 1);
					player.teleToLocation(27734 + Rnd.get(-80, 80), -6938 + Rnd.get(-80, 80), -1982);
					htmltext = "";
					if (GrandBossManager.getInstance().getBossStatus(SAILREN) == DORMANT)
					{
						startQuestTimer("waiting", 60000, npc, null);
						GrandBossManager.getInstance().setBossStatus(SAILREN, WAITING);
					}
				}
				else
				{
					htmltext = "<html><body>Stone Statue of Shilen:<br>You haven't got needed item to enter</body></html>";
				}
			}
			else if (GrandBossManager.getInstance().getBossStatus(SAILREN) == FIGHTING)
			{
				htmltext = "<html><body>Stone Statue of Shilen:<br><font color=\"LEVEL\">Sailren Lair is now full. </font></body></html>";
			}
			else
			{
				htmltext = "<html><body>Stone Statue of Shilen:<br><font color=\"LEVEL\">You can't enter now.</font></body></html>";
			}
		}
		
		if (npc.getNpcId() == 32107)
		{
			player.teleToLocation(23534 + Rnd.get(10), -8536 + Rnd.get(10), -1356);
			return null;
		}
		
		return htmltext;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		_LastAction = System.currentTimeMillis();
		if (npc.isInvul() && (npc.getNpcId() == SAILREN))
		{
			return null;
		}
		if (((npc.getNpcId() == VELOCIRAPTOR) || (npc.getNpcId() == PTEROSAUR) || (npc.getNpcId() == TYRANNOSAURUS)) && (GrandBossManager.getInstance().getBossStatus(SAILREN) == FIGHTING))
		{
			if (getQuestTimer("mob_has_arrived", npc, null) != null)
			{
				getQuestTimer("mob_has_arrived", npc, null).cancel();
				npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()));
				startQuestTimer("camera_6", 0, npc, null);
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if ((npc == null) || (killer == null))
		{
			return null;
		}
		if (GrandBossManager.getInstance().getBossStatus(SAILREN) == FIGHTING)
		{
			switch (npc.getNpcId())
			{
				case SAILREN:
					cancelQuestTimer("sailren_despawn", npc, null);
					startQuestTimer("spawn_cubes", 5000, npc, null);
					GrandBossManager.getInstance().setBossStatus(SAILREN, DEAD);
					long respawnTime = (12 + Rnd.get(24));
					startQuestTimer("sailren_unlock", respawnTime, null, null);
					// also save the respawn time so that the info is maintained past reboots
					StatsSet info = GrandBossManager.getInstance().getStatsSet(SAILREN);
					info.set("respawn_time", System.currentTimeMillis() + respawnTime);
					GrandBossManager.getInstance().setStatsSet(SAILREN, info);
					break;
				case VELOCIRAPTOR:
					cancelQuestTimer("sailren_despawn", npc, null);
					startQuestTimer("waiting2", 15000, npc, null);
					break;
				case PTEROSAUR:
					cancelQuestTimer("sailren_despawn", npc, null);
					startQuestTimer("waiting3", 15000, npc, null);
					break;
				case TYRANNOSAURUS:
					cancelQuestTimer("sailren_despawn", npc, null);
					startQuestTimer("waiting_boss", 15000, npc, null);
					break;
			}
		}
		return super.onKill(npc, killer, isPet);
	}
}