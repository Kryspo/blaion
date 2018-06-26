/*
 * Copyright (C) 2004-2018 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2jpdt_npcs.PremiumServiceNPC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import ai.L2AttackableAIScript;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.Announcements;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.Earthquake;
import com.l2jserver.gameserver.network.serverpackets.ExBrPremiumState;
import com.l2jserver.gameserver.network.serverpackets.ExRedSky;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;

/**
 * @author L2jPDT
 */
public class PremiumServiceNPC extends L2AttackableAIScript
{
	// NPCS
	private static final int NPC_ID_NICOL = 50017;
	// ITEMS
	private static final int BLUE_EVA = Config.COST_ITEM_ID;
	private static final int ITEM_NEED_FOR_1_MONTH = Config.COST_1_MONTHS;
	private static final int ITEM_NEED_FOR_2_MONTH = Config.COST_2_MONTHS;
	private static final int ITEM_NEED_FOR_3_MONTH = Config.COST_3_MONTHS;
	// OTHERS
	private static final int MIN_LEVEL = Config.MIN_LEVEL_FOR_PREMIUM;
	// UPDATE DB
	private static final String UPDATE_PREMIUMSERVICE = "UPDATE account_premium SET premium_service=?,enddate=? WHERE account_name=?";
	
	public PremiumServiceNPC()
	{
		super(50017, PremiumServiceNPC.class.getSimpleName(), "l2jpdt_npcs");
		StartNpcs(NPC_ID_NICOL);
		FirstTalkNpcs(NPC_ID_NICOL);
		TalkNpcs(NPC_ID_NICOL);
		if (Config.USE_PREMIUMSERVICE)
		{
			_log.info("Blaion: Premium Service is enabled, NPC loaded.");
		}
		else
		{
			_log.info("Blaion: Premium Service is disabled, NPC will not work.");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = "";
		if (Config.USE_PREMIUMSERVICE)
		{
			htmltext = "<html><body><title>Premium Service - Nicol</title><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Hello, dear <font color=LEVEL>%player%</font><br>We must protect our world, so <font color=LEVEL>lets be a premium.</font><br>Premium account cost <font color=0099FF>Blaion Coin</font> you can buy it, also can dropped from monsters.<br1>You are now level <font color=LEVEL>%level%</font>, premium accounts can be applied from level <font color=LEVEL>" + MIN_LEVEL + "</font>.<br1><button value=\"Give me 1 month PREMIUM (1000 BC)\" action=\"bypass -h Quest PremiumServiceNPC add_premium_1\" width=264 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/><br1><button value=\"Give me 2 months PREMIUM (1800 BC)\" action=\"bypass -h Quest PremiumServiceNPC add_premium_2\" width=264 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/><br1><button value=\"Give me 3 months PREMIUM (2500 BC)\" action=\"bypass -h Quest PremiumServiceNPC add_premium_3\" width=264 height=24 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"/><br1><br><br> Your Status:<br>Are you Premium user? <font color=LEVEL>%premiumacc%</font><br1>Use <font color=LEVEL>.premium</font> for more info about your acc.</center></body></html>";
			htmltext = htmltext.replace("%player%", String.valueOf(player.getName()));
			htmltext = htmltext.replace("%level%", String.valueOf(player.getLevel()));
			if (player.getPremiumService() == 0)
			{
				htmltext = htmltext.replace("%premiumacc%", String.valueOf("No"));
			}
			else
			{
				htmltext = htmltext.replace("%premiumacc%", String.valueOf("Yes"));
			}
		}
		else
		{
			htmltext = "<html><body><title>Premium Service - Nicol</title><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Hello, dear <font color=LEVEL>%player%</font><br>Im so sorry, but <font color=LEVEL>King of World of Aden</font> disabled Premium Services.</center></body></html>";
			htmltext = htmltext.replace("%player%", String.valueOf(player.getName()));
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "add_premium_1":
				if (player.getLevel() < MIN_LEVEL)
				{
					player.sendMessage("Your level is too low. Minimum level for use my service is: " + MIN_LEVEL);
				}
				else
				{
					if (player.getPremiumService() == 1)
					{
						player.sendMessage("You already a Premium Player.");
						player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
						player.broadcastPacket(new ExRedSky(10));
						player.broadcastPacket(new SocialAction(player, 11));
					}
					else
					{
						if (player.getInventory().getItemByItemId(BLUE_EVA) == null)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_1_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() < ITEM_NEED_FOR_1_MONTH)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_1_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() >= ITEM_NEED_FOR_1_MONTH)
						{
							player.destroyItemByItemId("ShopBBS", BLUE_EVA, ITEM_NEED_FOR_1_MONTH, player, true);
							player.getInventory().updateDatabase();
							addPremiumServices(1, player.getAccountName());
							player.sendMessage("You become to Premium Player for one month. Restart you character, please.");
							player.sendPacket(new ExBrPremiumState(player.getObjectId(), 1));
							player.updateOnlineStatus();
							player.broadcastPacket(new SocialAction(player, 3));
							Announcements.getInstance().announceToAll(player.getName() + " become to Premium Player.");
							ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1).addString(player.getName() + ", for activate Premium Account reconnect your character.");
							confirm.addTime(15000);
							player.sendPacket(confirm);
						}
					}
				}
				break;
			case "add_premium_2":
				if (player.getLevel() < MIN_LEVEL)
				{
					player.sendMessage("Your level is too low. Minimum level for use my service is: " + MIN_LEVEL);
				}
				else
				{
					if (player.getPremiumService() == 1)
					{
						player.sendMessage("You already a Premium Player.");
						player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
						player.broadcastPacket(new ExRedSky(10));
						player.broadcastPacket(new SocialAction(player, 11));
					}
					else
					{
						if (player.getInventory().getItemByItemId(BLUE_EVA) == null)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_2_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() < ITEM_NEED_FOR_2_MONTH)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_2_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() >= ITEM_NEED_FOR_2_MONTH)
						{
							player.destroyItemByItemId("ShopBBS", BLUE_EVA, ITEM_NEED_FOR_2_MONTH, player, true);
							player.getInventory().updateDatabase();
							addPremiumServices(2, player.getAccountName());
							player.sendMessage("You become to Premium Player for one month. Restart you character, please.");
							player.sendPacket(new ExBrPremiumState(player.getObjectId(), 1));
							player.updateOnlineStatus();
							player.broadcastPacket(new SocialAction(player, 3));
							Announcements.getInstance().announceToAll(player.getName() + " become to Premium Player.");
							ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1).addString(player.getName() + ", for activate Premium Account reconnect your character.");
							confirm.addTime(15000);
							player.sendPacket(confirm);
						}
					}
				}
				break;
			case "add_premium_3":
				if (player.getLevel() < MIN_LEVEL)
				{
					player.sendMessage("Your level is too low. Minimum level for use my service is: " + MIN_LEVEL);
				}
				else
				{
					if (player.getPremiumService() == 1)
					{
						player.sendMessage("You already a Premium Player.");
						player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
						player.broadcastPacket(new ExRedSky(10));
						player.broadcastPacket(new SocialAction(player, 11));
					}
					else
					{
						if (player.getInventory().getItemByItemId(BLUE_EVA) == null)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_3_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() < ITEM_NEED_FOR_3_MONTH)
						{
							player.sendMessage("You need " + ITEM_NEED_FOR_3_MONTH + " Blaion Coin.");
							player.broadcastPacket(new Earthquake(player.getX(), player.getY(), player.getZ(), 20, 10));
							player.broadcastPacket(new ExRedSky(10));
							player.broadcastPacket(new SocialAction(player, 11));
						}
						else if (player.getInventory().getItemByItemId(BLUE_EVA).getCount() >= ITEM_NEED_FOR_3_MONTH)
						{
							player.destroyItemByItemId("ShopBBS", BLUE_EVA, ITEM_NEED_FOR_3_MONTH, player, true);
							player.getInventory().updateDatabase();
							addPremiumServices(3, player.getAccountName());
							player.sendMessage("You become to Premium Player for one month. Restart you character, please.");
							player.sendPacket(new ExBrPremiumState(player.getObjectId(), 1));
							player.updateOnlineStatus();
							player.broadcastPacket(new SocialAction(player, 3));
							Announcements.getInstance().announceToAll(player.getName() + " become to Premium Player.");
							ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1).addString(player.getName() + ", for activate Premium Account reconnect your character.");
							confirm.addTime(15000);
							player.sendPacket(confirm);
						}
					}
				}
				break;
		}
		return htmltext;
	}
	
	private void addPremiumServices(int Months, String AccName)
	{
		Connection con = null;
		try
		{
			Calendar finishtime = Calendar.getInstance();
			finishtime.setTimeInMillis(System.currentTimeMillis());
			finishtime.set(Calendar.SECOND, 0);
			finishtime.add(Calendar.MONTH, Months);
			
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_PREMIUMSERVICE);
			statement.setInt(1, 1);
			statement.setLong(2, finishtime.getTimeInMillis());
			statement.setString(3, AccName);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.info("PremiumService:  Could not increase data");
		}
		finally
		{
			try
			{
				if (con != null)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
}