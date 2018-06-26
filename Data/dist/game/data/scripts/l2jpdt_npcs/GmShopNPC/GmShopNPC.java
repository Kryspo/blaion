package l2jpdt_npcs.GmShopNPC;

import java.util.StringTokenizer;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.MultiSell;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class GmShopNPC extends Quest
{
	// CONFIGS
	private static final int NPC_ID = 50019;
	private static final int ITEM_CONSUME = Config.MAIN_PAYMENT_ITEM; // Item Consume id for text only
	public static final boolean USE_GM_SHOP = Config.USE_GM_SHOP;
	
	// ETC
	private static String ITEM_NAME = ItemTable.getInstance().createDummyItem(ITEM_CONSUME).getItemName();
	
	public GmShopNPC()
	{
		super(NPC_ID, GmShopNPC.class.getSimpleName(), "ai.npc");
		FirstTalkNpcs(NPC_ID);
		TalkNpcs(NPC_ID);
		StartNpcs(NPC_ID);
		if (USE_GM_SHOP)
		{
			_log.info("L2jPDT: Gm Shop NPC is enabled, NPC loaded.");
		}
		else
		{
			_log.info("L2jPDT: Gm Shop NPC is disabled, NPC will not work.");
		}
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (USE_GM_SHOP)
		{
			if (event.startsWith("back"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/index.htm");
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%player%", player.getName());
				player.sendPacket(html);
			}
			else if (event.startsWith("_multisell;"))
			{
				if (player.isDead() || player.isAlikeDead() || player.isInSiege() || player.isCastingNow() || player.isInCombat() || player.isAttackingNow() || player.isInOlympiadMode() || player.isInJail() || player.isFlying() || (player.getKarma() > 0) || player.isInDuel())
				{
					player.sendMessage("You cant use this service!");
				}
				else
				{
					StringTokenizer st = new StringTokenizer(event, ";");
					st.nextToken();
					int multisell = Integer.parseInt(st.nextToken());
					MultiSell.getInstance().separateAndSend(multisell, player, null, false);
				}
			}
			else if (event.startsWith("_bbsAugment;add"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/index.htm");
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%player%", player.getName());
				player.sendPacket(html);
				player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
				player.sendPacket(new ExShowVariationMakeWindow());
				player.cancelActiveTrade();
			}
			else if (event.startsWith("_bbsAugment;remove"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/index.htm");
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%player%", player.getName());
				player.sendPacket(html);
				player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
				player.sendPacket(new ExShowVariationCancelWindow());
				player.cancelActiveTrade();
			}
			else if (event.startsWith("showArmors"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/armors.htm");
				html.replace("%adena%", "" + player.getAdena());
				html.replace("%level%", "" + player.getLevel());
				if (player.getLevel() < 20)
				{
					html.replace("%grade%", "No Grade");
				}
				else if ((player.getLevel() >= 20) && (player.getLevel() < 40))
				{
					html.replace("%grade%", "D Grade");
				}
				else if ((player.getLevel() >= 40) && (player.getLevel() < 52))
				{
					html.replace("%grade%", "C Grade");
				}
				else if ((player.getLevel() >= 52) && (player.getLevel() < 61))
				{
					html.replace("%grade%", "B Grade");
				}
				else if ((player.getLevel() >= 61) && (player.getLevel() < 76))
				{
					html.replace("%grade%", "A Grade");
				}
				else if ((player.getLevel() >= 76) && (player.getLevel() < 80))
				{
					html.replace("%grade%", "S Grade");
				}
				else if ((player.getLevel() >= 80) && (player.getLevel() < 84))
				{
					html.replace("%grade%", "S80 Grade");
				}
				else if (player.getLevel() >= 84)
				{
					html.replace("%grade%", "S84 Grade");
				}
				player.sendPacket(html);
			}
			else if (event.startsWith("showWeapons"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/weapons.htm");
				html.replace("%adena%", "" + player.getAdena());
				html.replace("%level%", "" + player.getLevel());
				if (player.getLevel() < 20)
				{
					html.replace("%grade%", "No Grade");
				}
				else if ((player.getLevel() >= 20) && (player.getLevel() < 40))
				{
					html.replace("%grade%", "D Grade");
				}
				else if ((player.getLevel() >= 40) && (player.getLevel() < 52))
				{
					html.replace("%grade%", "C Grade");
				}
				else if ((player.getLevel() >= 52) && (player.getLevel() < 61))
				{
					html.replace("%grade%", "B Grade");
				}
				else if ((player.getLevel() >= 61) && (player.getLevel() < 76))
				{
					html.replace("%grade%", "A Grade");
				}
				else if ((player.getLevel() >= 76) && (player.getLevel() < 80))
				{
					html.replace("%grade%", "S Grade");
				}
				else if ((player.getLevel() >= 80) && (player.getLevel() < 84))
				{
					html.replace("%grade%", "S80 Grade");
				}
				else if (player.getLevel() >= 84)
				{
					html.replace("%grade%", "S84 Grade");
				}
				player.sendPacket(html);
			}
			else if (event.startsWith("showJewelry"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/jewels.htm");
				html.replace("%adena%", "" + player.getAdena());
				html.replace("%level%", "" + player.getLevel());
				if (player.getLevel() < 20)
				{
					html.replace("%grade%", "No Grade");
				}
				else if ((player.getLevel() >= 20) && (player.getLevel() < 40))
				{
					html.replace("%grade%", "D Grade");
				}
				else if ((player.getLevel() >= 40) && (player.getLevel() < 52))
				{
					html.replace("%grade%", "C Grade");
				}
				else if ((player.getLevel() >= 52) && (player.getLevel() < 61))
				{
					html.replace("%grade%", "B Grade");
				}
				else if ((player.getLevel() >= 61) && (player.getLevel() < 76))
				{
					html.replace("%grade%", "A Grade");
				}
				else if ((player.getLevel() >= 76) && (player.getLevel() < 80))
				{
					html.replace("%grade%", "S Grade");
				}
				else if ((player.getLevel() >= 80) && (player.getLevel() < 84))
				{
					html.replace("%grade%", "S80 Grade");
				}
				else if (player.getLevel() >= 84)
				{
					html.replace("%grade%", "S84 Grade");
				}
				player.sendPacket(html);
			}
			else if (event.startsWith("showOthers"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/others.htm");
				html.replace("%adena%", "" + player.getAdena());
				html.replace("%player%", player.getName());
				player.sendPacket(html);
			}
		}
		else
		{
			player.sendMessage("GM Shop: Services is disabled for now.");
		}
		return "";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		if (USE_GM_SHOP)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(5);
			html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/GmShopNPC/index.htm");
			html.replace("%itemname%", ITEM_NAME);
			html.replace("%player%", player.getName());
			player.sendPacket(html);
		}
		else
		{
			player.sendMessage("GM Shop: Services is disabled for now.");
		}
		return "";
	}
}