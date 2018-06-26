package village_master.ElvenHumanMystics1;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.Race;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

import ai.L2AttackableAIScript;

public class ElvenHumanMystics1 extends L2AttackableAIScript
{
	// NPCs
	private static int[] NPCS =
	{
		30070,
		30289,
		30037,
		32153,
		32147
	};
	
	// Items
	private static int MARK_OF_FAITH = 1201;
	private static int ETERNITY_DIAMOND = 1230;
	private static int LEAF_OF_ORACLE = 1235;
	private static int BEAD_OF_SEASON = 1292;
	
	// Rewards
	private static int SHADOW_WEAPON_COUPON_DGRADE = 8869;
	
	private static int[][] CLASSES =
	{
		{
			26,
			25,
			15,
			16,
			17,
			18,
			ETERNITY_DIAMOND
		},
		{
			29,
			25,
			19,
			20,
			21,
			22,
			LEAF_OF_ORACLE
		},
		{
			11,
			10,
			23,
			24,
			25,
			26,
			BEAD_OF_SEASON
		},
		{
			15,
			10,
			27,
			28,
			29,
			30,
			MARK_OF_FAITH
		}
	};
	
	public ElvenHumanMystics1()
	{
		super(-1, ElvenHumanMystics1.class.getSimpleName(), "village_master");
		StartNpcs(NPCS);
		TalkNpcs(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			return getNoQuestMsg(player);
		}
		
		if (Util.isDigit(event))
		{
			int i = Integer.valueOf(event);
			final ClassId cid = player.getClassId();
			if (((cid.getRace() == Race.Elf) || (cid.getRace() == Race.Human)) && (cid.getId() == CLASSES[i][1]))
			{
				int suffix;
				final boolean item = st.hasQuestItems(CLASSES[i][6]);
				if (player.getLevel() < 20)
				{
					suffix = (!item) ? CLASSES[i][2] : CLASSES[i][3];
				}
				else
				{
					if (!item)
					{
						suffix = CLASSES[i][4];
					}
					else
					{
						suffix = CLASSES[i][5];
						st.giveItems(SHADOW_WEAPON_COUPON_DGRADE, 15);
						st.takeItems(CLASSES[i][6], -1);
						player.setClassId(CLASSES[i][0]);
						player.setBaseClass(CLASSES[i][0]);
						playSound(player, QuestSound.ITEMSOUND_QUEST_FANFARE_2);
						player.broadcastUserInfo();
						st.exitQuest(false);
					}
				}
				event = npc.getNpcId() + "-" + suffix + ".htm";
			}
		}
		return event;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = getNoQuestMsg(player);
		QuestState st = player.getQuestState(getName());
		if (st == null)
		{
			st = newQuestState(player);
		}
		if (player.isSubClassActive())
		{
			return htmltext;
		}
		
		final ClassId cid = player.getClassId();
		
		if ((cid.getRace() == Race.Elf) || (cid.getRace() == Race.Human))
		{
			switch (cid)
			{
				case elvenMage:
				{
					htmltext = npc.getNpcId() + "-01.htm";
					break;
				}
				case mage:
				{
					htmltext = npc.getNpcId() + "-08.htm";
					break;
				}
				default:
				{
					if (cid.level() == 1)
					{
						return npc.getNpcId() + "-31.htm";
					}
					else if (cid.level() >= 2)
					{
						return npc.getNpcId() + "-32.htm";
					}
				}
			}
		}
		else
		{
			htmltext = npc.getNpcId() + "-33.htm";
		}
		return htmltext;
	}
}