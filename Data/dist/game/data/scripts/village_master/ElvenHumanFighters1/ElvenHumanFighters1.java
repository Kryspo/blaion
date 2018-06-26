package village_master.ElvenHumanFighters1;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.Race;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

import ai.L2AttackableAIScript;

public class ElvenHumanFighters1 extends L2AttackableAIScript
{
	// NPCs
	private static int[] NPCS =
	{
		30066,
		30288,
		30373,
		32154
	};
	
	// Items
	private static int MEDALLION_OF_WARRIOR = 1145;
	private static int SWORD_OF_RITUAL = 1161;
	private static int BEZIQUES_RECOMMENDATION = 1190;
	private static int ELVEN_KNIGHT_BROOCH = 1204;
	private static int REORIA_RECOMMENDATION = 1217;
	
	// Rewards
	private static int SHADOW_WEAPON_COUPON_DGRADE = 8869;
	
	private static int[][] CLASSES =
	{
		{
			19,
			18,
			18,
			19,
			20,
			21,
			ELVEN_KNIGHT_BROOCH
		},
		{
			22,
			18,
			22,
			23,
			24,
			25,
			REORIA_RECOMMENDATION
		},
		{
			1,
			0,
			26,
			27,
			28,
			29,
			MEDALLION_OF_WARRIOR
		},
		{
			4,
			0,
			30,
			31,
			32,
			33,
			SWORD_OF_RITUAL
		},
		{
			7,
			0,
			34,
			35,
			36,
			37,
			BEZIQUES_RECOMMENDATION
		}
	};
	
	public ElvenHumanFighters1()
	{
		super(-1, ElvenHumanFighters1.class.getSimpleName(), "village_master");
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
				case elvenFighter:
				{
					htmltext = npc.getNpcId() + "-01.htm";
					break;
				}
				case fighter:
				{
					htmltext = npc.getNpcId() + "-08.htm";
					break;
				}
				default:
				{
					if (cid.level() == 1)
					{
						return npc.getNpcId() + "-38.htm";
					}
					else if (cid.level() >= 2)
					{
						return npc.getNpcId() + "-39.htm";
					}
				}
			}
		}
		else
		{
			htmltext = npc.getNpcId() + "-40.htm";
		}
		return htmltext;
	}
}