package quests.Q00288_HandleWithCare;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.quest.State;
import com.l2jserver.util.Rnd;

public class Q00288_HandleWithCare extends Quest
{
	private static final String qn = "288_HandleWithCare";
	private static final int _batracos = 32740;
	private static final int _ankumi = 32741;
	private static final int _seer = 18863;
	private static final int _pass = 15496;
	private static final int RATE = 3;
	private static final int _scale1 = 15497;
	private static final int _scale2 = 15498;
	private static final int _crystal = 9557;
	
	private static final int[][] _reward1 =
	{
		{
			959,
			1
		},
		{
			960,
			1
		},
		{
			960,
			2
		},
		{
			960,
			3
		},
		{
			9557,
			1
		}
	};
	
	private static final int[][] _reward2 =
	{
		{
			959,
			1
		},
		{
			960,
			1
		},
		{
			960,
			2
		},
		{
			960,
			3
		}
	};
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = event;
		QuestState st = player.getQuestState(qn);
		
		if (st == null)
		{
			return htmltext;
		}
		L2Attackable newNpc = null;
		
		if (npc.getNpcId() == _ankumi)
		{
			if (event.equalsIgnoreCase("32741-03.htm"))
			{
				st.setState(State.STARTED);
				st.set("cond", "1");
				st.playSound("ItemSound.quest_accept");
			}
		}
		else if (event.equalsIgnoreCase("teleport"))
		{
			if (st.getQuestItemsCount(_pass) >= 1)
			{
				st.takeItems(_pass, 1);
				player.teleToLocation(95737, 85685, -3730);
				startQuestTimer("time_to_spawn", 5000, npc, player);
			}
			else if (st.getQuestItemsCount(_pass) == 0)
			{
				htmltext = "32740-01.htm";
			}
		}
		else if (event.equalsIgnoreCase("time_to_spawn"))
		{
			newNpc = (L2Attackable) addSpawn(_seer, 96970, 85682, -3724, 0, false, 300000);
			newNpc.addDamageHate(player, 0, 999);
			newNpc.setRunning();
			newNpc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return htmltext;
		}
		int a = st.getRandom(5);
		int b = st.getRandom(4);
		
		if (npc.getNpcId() == _ankumi)
		{
			switch (st.getState())
			{
				case State.CREATED:
					if (player.getLevel() >= 82)
					{
						htmltext = "32741-01.htm";
					}
					else
					{
						htmltext = "32741-00.htm";
					}
					break;
				case State.STARTED:
					if (st.getInt("cond") == 1)
					{
						htmltext = "32741-03.htm";
					}
					else if (st.getInt("cond") == 2)
					{
						htmltext = "32741-05.htm";
						st.unset("cond");
						st.giveItems(_reward1[a][0], _reward1[a][1] * RATE);
						st.takeItems(_scale2, 1);
						st.playSound("ItemSound.quest_finish");
						st.exitQuest(true);
					}
					else if (st.getInt("cond") == 3)
					{
						htmltext = "32741-04.htm";
						st.unset("cond");
						st.giveItems(_crystal, 1 * RATE);
						st.takeItems(_scale1, 1);
						st.giveItems(_reward2[b][0], _reward2[b][1] * RATE);
						st.playSound("ItemSound.quest_finish");
						st.exitQuest(true);
					}
					break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		int npcId = npc.getNpcId();
		QuestState st = player.getQuestState(qn);
		if (st == null)
		{
			return "";
		}
		if (npcId == _seer)
		{
			if (Rnd.get(100) < 20)
			{
				st.set("cond", "3");
				st.playSound("ItemSound.quest_finish");
				st.giveItems(_scale1, 1);
			}
			else
			{
				st.set("cond", "2");
				st.playSound("ItemSound.quest_finish");
				st.giveItems(_scale2, 1);
			}
		}
		return "";
	}
	
	public Q00288_HandleWithCare()
	{
		super(288, Q00288_HandleWithCare.class.getSimpleName(), "Handle With Care");
		
		StartNpcs(_ankumi);
		TalkNpcs(_batracos);
		TalkNpcs(_ankumi);
		KillNpcs(_seer);
	}
}