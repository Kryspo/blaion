package com.l2jserver.gameserver.communitybbs;

import java.util.StringTokenizer;

import com.l2jserver.Config;
import com.l2jserver.gameserver.communitybbs.BB.MatchFilter;
import com.l2jserver.gameserver.communitybbs.Managers.BuffBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ClanBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ClanMailBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.DropSearchBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.EnchantBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.FriendBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.MessageBoxBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.PartyMatchingBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.PartyMatchingBoard;
import com.l2jserver.gameserver.communitybbs.Managers.RegionBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.ServiceBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.StatisticsBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.TeleportBBSManager;
import com.l2jserver.gameserver.communitybbs.Managers.TopBBSManager;
import com.l2jserver.gameserver.datatables.MultiSell;
import com.l2jserver.gameserver.enums.MatchClassType;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.RequestJoinParty;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jserver.gameserver.network.serverpackets.ShowBoard;
import com.l2jserver.gameserver.util.Util;

public class CommunityBoard
{
	protected CommunityBoard()
	{
	}
	
	public static CommunityBoard getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public void handleCommands(L2GameClient client, String command)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.inObserverMode())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isAlikeDead())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInSiege())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInsideZone(L2Character.ZONE_PVP))
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInCombat())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isDead())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isCastingNow())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isAttackingNow())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInJail())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isFlying())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		if (activeChar.isInDuel())
		{
			activeChar.sendMessage("You cant use Community Board for now.");
			return;
		}
		
		if (command.startsWith("_bbsclan"))
		{
			ClanBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_maillist_0_1_0_"))
		{
			ClanMailBBSManager.getInstance().parsecmd(command, activeChar);
		}
		// second table in cb
		else if ((command.startsWith("_bbsgetfav")) || (command.startsWith("_bbssearch")))
		{
			DropSearchBBSManager.getInstance().parsecmd(command, activeChar);
		}
		// third table in cb
		else if (command.startsWith("_bbslink"))
		{
			PartyMatchingBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_bbspartymatching"))
		{
			String[] value = command.split(" ");
			String type = value[1];
			if (type.equals("on"))
			{
				if (activeChar.isInParty())
				{
					activeChar.sendMessage("You cant use this while you're in party!");
				}
				else if (PartyMatchingBoard.getInstance().isInMatchList(activeChar))
				{
					activeChar.sendMessage("You're alredy in the Party Matching list!");
				}
				else
				{
					PartyMatchingBoard.getInstance().regOnMatchList(activeChar);
				}
			}
			else if (type.equals("off"))
			{
				if (activeChar.isInParty())
				{
					activeChar.sendMessage("You cant use this while you're in party!");
				}
				else if (!PartyMatchingBoard.getInstance().isInMatchList(activeChar))
				{
					activeChar.sendMessage("You've alredy left from the Party Matching list!");
				}
				else
				{
					PartyMatchingBoard.getInstance().removeFromMatchList(activeChar);
				}
			}
			else if (type.equals("filter"))
			{
				if (value.length == 3)
				{
					PartyMatchingBoard.getInstance().removeFilter(activeChar);
					activeChar.sendMessage("Filter removed!");
				}
				
				else if (value.length < 5)
				{
					activeChar.sendMessage("You must input all info!");
				}
				
				else if (Util.isDigit(value[2]) && Util.isDigit(value[3]))
				{
					int minLv = Integer.parseInt(value[2]);
					int maxLv = Integer.parseInt(value[3]);
					MatchClassType ct = MatchClassType.getByName(value[4]);
					PartyMatchingBoard.getInstance().addNewFilter(activeChar, new MatchFilter(minLv, maxLv, ct));
					activeChar.sendMessage("new Filter create success!");
				}
			}
			else if ("invite".equals(type))
			{
				L2PcInstance target = L2World.getInstance().getPlayer(Integer.parseInt(value[2]));
				if (target != null)
				{
					RequestJoinParty.sendInivte(activeChar, target);
				}
			}
			PartyMatchingBBSManager.getInstance().refreshBroad(activeChar);
		}
		else if (command.startsWith("_bbsmsg") || command.startsWith("_bbsmemo"))
		{
			if (command.startsWith("_bbsmemo"))
			{
				command = "_bbsmsg all 1";
			}
			MessageBoxBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_bbstop"))
		{
			TopBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_bbshome"))
		{
			TopBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_bbsloc"))
		{
			RegionBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else if (command.startsWith("_bbsstat;"))
		{
			if (Config.ALLOW_COMMUNITY_STATS)
			{
				StatisticsBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else
			{
				activeChar.sendMessage("You cant see stats!");
				return;
			}
		}
		else if (command.startsWith("_bbsteleport;"))
		{
			if (Config.ALLOW_COMMUNITY_TELEPORT)
			{
				TeleportBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else
			{
				activeChar.sendMessage("You cant use this service!");
				return;
			}
		}
		else if (command.startsWith("_bbs_buff"))
		{
			if (Config.ALLOW_COMMUNITY_BUFF)
			{
				BuffBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else
			{
				activeChar.sendMessage("You cant use this service!");
				return;
			}
		}
		else if (command.startsWith("_bbsservice"))
		{
			if (Config.ALLOW_COMMUNITY_SERVICES)
			{
				ServiceBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else
			{
				activeChar.sendMessage("You cant use this service!");
				return;
			}
		}
		else if (command.startsWith("_bbsechant"))
		{
			if (Config.ALLOW_COMMUNITY_ENCHANT)
			{
				EnchantBBSManager.getInstance().parsecmd(command, activeChar);
			}
			else
			{
				activeChar.sendMessage("You cant use this service!");
				return;
			}
		}
		else if (command.startsWith("_bbsmultisell;"))
		{
			if (Config.ALLOW_COMMUNITY_MULTISELL)
			{
				if (activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isInSiege() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isInJail() || activeChar.isFlying() || (activeChar.getKarma() > 0) || activeChar.isInDuel())
				{
					activeChar.sendMessage("You cant use this service!");
					return;
				}
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				TopBBSManager.getInstance().parsecmd("_bbstop;" + st.nextToken(), activeChar);
				int multisell = Integer.parseInt(st.nextToken());
				MultiSell.getInstance().separateAndSend(multisell, activeChar, null, false);
			}
			else
			{
				activeChar.sendMessage("You cant use this service");
				return;
			}
		}
		else if (command.startsWith("_bbsAugment;add"))
		{
			if (Config.ALLOW_COMMUNITY_MULTISELL)
			{
				TopBBSManager.getInstance().parsecmd(command, activeChar);
				activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
				activeChar.sendPacket(new ExShowVariationMakeWindow());
				activeChar.cancelActiveTrade();
				TopBBSManager.getInstance().parsecmd(command, activeChar);
				return;
			}
			activeChar.sendMessage("You cant use this service!");
			return;
		}
		else if (command.startsWith("_bbsAugment;remove"))
		{
			if (Config.ALLOW_COMMUNITY_MULTISELL)
			{
				TopBBSManager.getInstance().parsecmd(command, activeChar);
				activeChar.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
				activeChar.sendPacket(new ExShowVariationCancelWindow());
				activeChar.cancelActiveTrade();
				TopBBSManager.getInstance().parsecmd(command, activeChar);
				return;
			}
			activeChar.sendMessage("You cant use this service!");
			return;
		}
		else if (command.startsWith("bbs_add_fav"))
		{
			activeChar.sendMessage("Contact developers for missing text");
		}
		else if (command.startsWith("_friendlist_0_"))
		{
			FriendBBSManager.getInstance().parsecmd(command, activeChar);
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
		
	}
	
	public void handleWriteCommands(L2GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		if (url.equals("Region"))
		{
			RegionBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
		}
		else if (url.equals("Notice"))
		{
			ClanBBSManager.getInstance().parsewrite(arg1, arg2, arg3, arg4, arg5, activeChar);
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>the command: " + url + " is not implemented yet</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
		
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityBoard _instance = new CommunityBoard();
	}
}