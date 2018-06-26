package village_master.OrcOccupationChange1;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.Race;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.util.Util;

import ai.L2AttackableAIScript;

public class OrcOccupationChange1 extends L2AttackableAIScript
{
	// NPCs
	private static int[] NPCS =
	{
		30500,
		30505,
		30508,
		32150
	};
	
	// Items
	private static int MARK_OF_RAIDER = 1592;
	private static int KHAVATARI_TOTEM = 1615;
	private static int MASK_OF_MEDIUM = 1631;
	
	// Rewards
	private static int SHADOW_WEAPON_COUPON_DGRADE = 8869;
	
	private static int[][] CLASSES =
	{
		{
			45,
			44,
			9,
			10,
			11,
			12,
			MARK_OF_RAIDER
		},
		{
			47,
			44,
			13,
			14,
			15,
			16,
			KHAVATARI_TOTEM
		},
		{
			50,
			49,
			17,
			18,
			19,
			20,
			MASK_OF_MEDIUM
		}
	};
	
	public OrcOccupationChange1()
	{
		super(-1, OrcOccupationChange1.class.getSimpleName(), "village_master");
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
			if ((cid.getRace() == Race.Orc) && (cid.getId() == CLASSES[i][1]))
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
				String htm = suffix == 9 ? "09" : String.valueOf(suffix);
				event = npc.getNpcId() + "-" + htm + ".htm";
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
		
		if (cid.getRace() == Race.Orc)
		{
			switch (cid)
			{
				case orcFighter:
				{
					htmltext = npc.getNpcId() + "-01.htm";
					break;
				}
				case orcMage:
				{
					htmltext = npc.getNpcId() + "-06.htm";
					break;
				}
				default:
				{
					if (cid.level() == 1)
					{
						return npc.getNpcId() + "-21.htm";
					}
					else if (cid.level() >= 2)
					{
						return npc.getNpcId() + "-22.htm";
					}
				}
			}
		}
		else
		{
			htmltext = npc.getNpcId() + "-23.htm";
		}
		return htmltext;
	}
}