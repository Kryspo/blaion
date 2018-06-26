package l2jpdt_npcs.LevelChooseNpc;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.Experience;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class LevelChooseNpc extends Quest
{
	// CONFIGS
	private static final int NPC_ID = 50018;
	private static final int ITEM_CONSUME = Config.ITEM_CONSUME; // Item Consume id
	private static final int ITEM_CONSUME_EACH_LEVEL = Config.ITEMS_EACH_LEVEL;
	private static final int MAX_LEVELING_TO_LEVEL = Config.MAX_LEVELING;
	private static final int MIN_LEVELING_TO_LEVEL = Config.MIN_DELEVELING;
	
	// ETC
	private static String ITEM_NAME = ItemTable.getInstance().createDummyItem(ITEM_CONSUME).getItemName();
	
	public LevelChooseNpc()
	{
		super(NPC_ID, LevelChooseNpc.class.getSimpleName(), "ai.npc");
		FirstTalkNpcs(NPC_ID);
		TalkNpcs(NPC_ID);
		StartNpcs(NPC_ID);
		if (Config.USE_LEVEL_MANAGER)
		{
			_log.info("L2jPDT: Level Choose NPC is enabled, NPC loaded.");
		}
		else
		{
			_log.info("L2jPDT: Level Choose NPC is disabled, NPC will not work.");
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		// TODO: Can be switch :-)
		if (event.startsWith("dlvl"))
		{
			DelevelMe(event, npc, player, event);
		}
		if (event.startsWith("lvlup"))
		{
			LevelMe(event, npc, player, event);
		}
		return "";
	}
	
	private void LevelMe(String event, L2Npc npc, L2PcInstance player, String command)
	{
		if (player.getInventory().getItemByItemId(ITEM_CONSUME) == null)
		{
			player.sendMessage("You don't have enough items!");
			return;
		}
		if (player.getInventory().getItemByItemId(ITEM_CONSUME).getCount() < (ITEM_CONSUME_EACH_LEVEL))
		{
			player.sendMessage("You don't have enough items!");
			return;
		}
		if (player.getLevel() == MAX_LEVELING_TO_LEVEL)
		{
			if (player.getLevel() == 85)
			{
				player.sendMessage("You reached maximum level.");
				return;
			}
			else
			{
				player.sendMessage("You reached maximum allowed level. Its " + MAX_LEVELING_TO_LEVEL + " Lv.");
			}
			return;
		}
		if (player.getInventory().getItemByItemId(ITEM_CONSUME).getCount() >= ITEM_CONSUME_EACH_LEVEL)
		{
			
			if ((player.getLevel() >= 1) && (player.getLevel() <= Experience.MAX_LEVEL))
			{
				long pXp = player.getExp();
				long tXp = Experience.LEVEL[player.getLevel() + 1];
				
				if (pXp < tXp)
				{
					player.addExpAndSp(tXp - pXp, 0);
					player.broadcastStatusUpdate();
					player.broadcastUserInfo();
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new ExBrExtraUserInfo(player));
					player.destroyItemByItemId("Level Manager", ITEM_CONSUME, ITEM_CONSUME_EACH_LEVEL, player, true);
				}
			}
		}
	}
	
	private void DelevelMe(String event, L2Npc npc, L2PcInstance player, String command)
	{
		if (player.getInventory().getItemByItemId(ITEM_CONSUME) == null)
		{
			player.sendMessage("You don't have enough items!");
			return;
		}
		if (player.getInventory().getItemByItemId(ITEM_CONSUME).getCount() < (ITEM_CONSUME_EACH_LEVEL))
		{
			player.sendMessage("You don't have enough items!");
			return;
		}
		if (player.getLevel() == MIN_LEVELING_TO_LEVEL)
		{
			if (player.getLevel() == 1)
			{
				player.sendMessage("You cant be lower level.");
				return;
			}
			else
			{
				player.sendMessage("You deleveled to minimum allowed level. Its " + MIN_LEVELING_TO_LEVEL + " Lv.");
			}
			return;
		}
		
		if (player.getInventory().getItemByItemId(ITEM_CONSUME).getCount() >= ITEM_CONSUME_EACH_LEVEL)
		{
			
			if ((player.getLevel() >= 1))
			{
				long pXp = player.getExp();
				long tXp = Experience.LEVEL[player.getLevel() - 1];
				
				if (pXp > tXp)
				{
					player.removeExpAndSp(pXp - tXp, 0);
					player.broadcastStatusUpdate();
					player.broadcastUserInfo();
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new ExBrExtraUserInfo(player));
					player.destroyItemByItemId("Level Manager", ITEM_CONSUME, ITEM_CONSUME_EACH_LEVEL, player, true);
				}
			}
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		final int npcId = npc.getNpcId();
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		if (npcId == NPC_ID)
		{
			if (Config.USE_LEVEL_MANAGER)
			{
				htmltext = "<html><title>Bless with %player% </title><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center><br>Hello %player%,<br1>You are welcome everytime<br1><br1>If you want to low your current level, you came to right place!<br>In exchange, i am going to take you <font color=LEVEL>%price% %itemname%</font> for <font color=LEVEL>each level</font> you decrease or level up.<br><br><table width=270><tr><td align=center><button value=\"Delevel me: " + Config.ITEMS_EACH_LEVEL + " " + ITEM_NAME + " cost \"action=\"bypass -h Quest LevelChooseNpc dlvl\" width=180 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table><br><table width=270><tr><td align=center><button value=\"Level Up: " + Config.ITEMS_EACH_LEVEL + " " + ITEM_NAME + " cost\"action=\"bypass -h Quest LevelChooseNpc lvlup\" width=180 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table><br><br><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center></body></html>";
				htmltext = htmltext.replaceAll("%player%", player.getName());
				htmltext = htmltext.replaceAll("%itemname%", ITEM_NAME);
				htmltext = htmltext.replaceAll("%price%", "" + ITEM_CONSUME_EACH_LEVEL + "");
			}
			else
			{
				htmltext = "<html><body><title>Level Manager  - Lady</title><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Hello, dear <font color=LEVEL>%player%</font><br>Im so sorry, but <font color=LEVEL>King of World of Aden</font> disabled my services.</center></body></html>";
				htmltext = htmltext.replace("%player%", String.valueOf(player.getName()));
			}
		}
		return htmltext;
	}
}