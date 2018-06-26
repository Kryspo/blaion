package l2jpdt_npcs.AutomaticTeleporterNPC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.util.Rnd;

import javolution.text.TextBuilder;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class AutomaticTeleporterNPC extends Quest
{
	// CONFIGS
	public static final boolean USE_TELEPORTER = true;
	private static final int NPC_ID = 50020;
	
	private static final int ITEM_CONSUME = 57; // Item Consume id for text only
	private static final String ITEM_COUNT = "10000";
	// ETC
	private static String ITEM_NAME = ItemTable.getInstance().createDummyItem(ITEM_CONSUME).getItemName();
	
	public class AutomaticTeleport
	{
		public int TpId = 0;
		public String TpName = "";
		public int PlayerId = 0;
		public int xC = 0;
		public int yC = 0;
		public int zC = 0;
	}
	
	public AutomaticTeleporterNPC()
	{
		super(NPC_ID, AutomaticTeleporterNPC.class.getSimpleName(), "ai.npc");
		FirstTalkNpcs(NPC_ID);
		TalkNpcs(NPC_ID);
		StartNpcs(NPC_ID);
		if (USE_TELEPORTER)
		{
			_log.info("L2jPDT: Teleporter NPC is enabled, NPC loaded.");
		}
		else
		{
			_log.info("L2jPDT: Teleporte NPC is disabled, NPC will not work.");
		}
	}
	
	private void showAgain(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(5);
		html.setFile(activeChar.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/againTeleport.htm");
		html.replace("%player%", activeChar.getName());
		activeChar.sendPacket(html);
		addSpawn(NPC_ID, activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), true, 20000, false, 0);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (USE_TELEPORTER)
		{
			if (event.startsWith("startingloc"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/startingloc.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("gracia"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/gracia.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("toi"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/toi.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("freya"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/freya.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("other"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/other.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("kingdoms"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/kingdoms.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("necropolis"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/necropolis.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("catacombs"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/catacombs.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("primeval"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/primeval.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("hellbound"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/hellbound.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			
			//
			//
			else if (event.startsWith("towns"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC//towns.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			//
			//
			else if (event.startsWith("aden"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/aden.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("giran"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/giran.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("goddard"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/goddard.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("rune"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/rune.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("schuttgart"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/schuttgart.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("oren"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/oren.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("dion"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/dion.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("gludio"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/gludio.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("gludin"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/gludin.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			else if (event.startsWith("hunters"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/kingdoms/hunters.htm");
				player.sendPacket(html);
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
			}
			//
			//
			//
			//
			else if (event.startsWith("back"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/index.htm");
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%player%", player.getName());
				player.sendPacket(html);
			}
			else if (event.startsWith("showRegions"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/telmenu/showRegions.htm");
				html.replace("%player%", player.getName());
				html.replace("%itemname%", ITEM_NAME);
				html.replace("%itemcount%", ITEM_COUNT);
				player.sendPacket(html);
			}
			else if (event.startsWith("info1"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/info1.htm");
				html.replace("%player%", player.getName());
				player.sendPacket(html);
			}
			else if (event.startsWith("info2"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(5);
				html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/info2.htm");
				html.replace("%player%", player.getName());
				player.sendPacket(html);
			}
			else if (event.startsWith("savedLocation"))
			{
				AutomaticTeleport tp;
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement st = con.prepareStatement("SELECT * FROM comteleport WHERE charId=?;");
					st.setLong(1, player.getObjectId());
					ResultSet rs = st.executeQuery();
					TextBuilder html = new TextBuilder();
					html.append("<table width=220>");
					while (rs.next())
					{
						
						tp = new AutomaticTeleport();
						tp.TpId = rs.getInt("TpId");
						tp.TpName = rs.getString("name");
						tp.PlayerId = rs.getInt("charId");
						tp.xC = rs.getInt("xPos");
						tp.yC = rs.getInt("yPos");
						tp.zC = rs.getInt("zPos");
						html.append("<tr>");
						html.append("<td>");
						html.append("<button value=\"" + tp.TpName + "\" action=\"bypass -h _bbsteleport;teleport; " + tp.xC + " " + tp.yC + " " + tp.zC + " " + 100000 + "\" width=120 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
						html.append("</td>");
						html.append("<td>");
						html.append("<button value=\"Delete\" action=\"bypass -h _bbsteleport;delete;" + tp.TpId + "\" width=100 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
						html.append("</td>");
						html.append("</tr>");
					}
					html.append("</table>");
					
					final NpcHtmlMessage ShowHTML = new NpcHtmlMessage(5);
					ShowHTML.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/savedLocation.htm");
					ShowHTML.replace("%player%", player.getName());
					ShowHTML.replace("%tp%", html.toString());
					player.sendPacket(ShowHTML);
				}
				catch (Exception e)
				{
					_log.info("" + e);
				}
			}
			else if (event.startsWith("autoTeleport"))
			{
				// 20-30
				if ((player.getLevel() >= 18) && (player.getLevel() <= 22))
				{
					player.teleToLocation(-56235, 106668, -3773);
					player.sendMessage("You has been teleported to: Ruins of Agony");
					showAgain(player);
				}
				else if ((player.getLevel() >= 23) && (player.getLevel() <= 25))
				{
					player.teleToLocation(-20043, 137688, -3896);
					player.sendMessage("You has been teleported to: Ruins of Despair");
					showAgain(player);
				}
				else if ((player.getLevel() >= 26) && (player.getLevel() <= 30))
				{
					player.teleToLocation(-17161, 206581, -3666);
					player.sendMessage("You has been teleported to: WasteLands");
					showAgain(player);
				}
				// 30-40
				else if ((player.getLevel() >= 31) && (player.getLevel() <= 34))
				{
					player.teleToLocation(-26111, 173692, -4152);
					player.sendMessage("You has been teleported to: Ant Nest");
					showAgain(player);
				}
				else if ((player.getLevel() >= 35) && (player.getLevel() <= 40))
				{
					if (Rnd.get(100) < 33)
					{
						player.teleToLocation(17144, 170156, -3502);
						player.sendMessage("You has been teleported to: Floran Village");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(69385, 118854, -3465);
						player.sendMessage("You has been teleported to: Death Pass");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(17723, 117302, -12073);
						player.sendMessage("You has been teleported to: Cruma Tower");
						showAgain(player);
					}
					else
					{
						player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
						showAgain(player);
					}
				}
				// 40-50
				else if ((player.getLevel() >= 41) && (player.getLevel() <= 44))
				{
					player.teleToLocation(62425, 30856, -3779);
					player.sendMessage("You has been teleported to: Sea of Spores");
					showAgain(player);
				}
				else if ((player.getLevel() >= 45) && (player.getLevel() <= 59))
				{
					if (Rnd.get(100) < 33)
					{
						player.teleToLocation(44220, 207412, -3759);
						player.sendMessage("You have been teleported to: Devils Isle");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(73228, 118098, -3726);
						player.sendMessage("You have been teleported to: Dragon Valley");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(178299, 20322, -3252);
						player.sendMessage("You have been teleported to: The Cemetery");
						showAgain(player);
					}
					else
					{
						player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
						showAgain(player);
					}
				}
				// 50-57
				else if ((player.getLevel() >= 50) && (player.getLevel() <= 53))
				{
					player.teleToLocation(150447, 85907, -2753);
					player.sendMessage("You has been teleported to: Forest of Mirrors");
					showAgain(player);
				}
				else if ((player.getLevel() >= 54) && (player.getLevel() <= 57))
				{
					player.teleToLocation(131335, 114451, -3718);
					player.sendMessage("You has been teleported to: Antharas Lair Entrance");
					showAgain(player);
				}
				// 58-64
				else if ((player.getLevel() >= 58) && (player.getLevel() <= 64))
				{
					if (player.isMageClass())
					{
						player.teleToLocation(67992, -72012, -3748);
						player.sendMessage("You has been teleported to: Valley of Saint");
						showAgain(player);
					}
					else
					{
						if (Rnd.get(100) < 33)
						{
							player.teleToLocation(134103, 114443, -3727);
							player.sendMessage("You has been teleported to: Antharas Lair inside");
							showAgain(player);
						}
						else if (Rnd.get(100) < 33)
						{
							player.teleToLocation(115001, 15883, -5102);
							player.sendMessage("You has been teleported to: Tower of Insolence - 1st floor");
							showAgain(player);
						}
						else if (Rnd.get(100) < 33)
						{
							player.teleToLocation(67992, -72012, -3748);
							player.sendMessage("You has been teleported to: Valley of Saint");
							showAgain(player);
						}
						else if (Rnd.get(100) < 33)
						{
							player.teleToLocation(185374, 20284, -3272);
							player.sendMessage("You has been teleported to: The Forbidden Gateway");
							showAgain(player);
						}
						else
						{
							player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
							showAgain(player);
						}
					}
				}
				// 65-69
				else if ((player.getLevel() >= 65) && (player.getLevel() <= 69))
				{
					if (Rnd.get(100) < 33)
					{
						player.teleToLocation(117088, 15233, 926);
						player.sendMessage("You has been teleported to: Tower of Insolence - 5st floor");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(164370, -48389, -3554);
						player.sendMessage("You has been teleported to: Wall of Agros");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(156898, -8298, -3670);
						player.sendMessage("You has been teleported to: Blazing Swamp");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(69361, -50177, -3283);
						player.sendMessage("You has been teleported to: Swamp of Scream");
						showAgain(player);
					}
					else
					{
						player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
						showAgain(player);
					}
				}
				// 70-72
				else if ((player.getLevel() >= 70) && (player.getLevel() <= 72))
				{
					player.teleToLocation(149516, -112593, -2066);
					player.sendMessage("You has been teleported to: Hot Springs");
					showAgain(player);
				}
				// 73-79
				else if ((player.getLevel() >= 73) && (player.getLevel() <= 79))
				{
					if (Rnd.get(100) < 33)
					{
						player.teleToLocation(117356, 18462, 4977);
						player.sendMessage("You has been teleported to: Tower of insolence - 9st floor");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						// TODO: Alliance with Ketry and Varka
						player.teleToLocation(125394, -40886, -3687);
						player.sendMessage("You has been teleported to: Varka Silenos Outpost");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						// TODO: Alliance with Ketry and Varka
						player.teleToLocation(146980, -67098, -3635);
						player.sendMessage("You has been teleported to: Ketra Orc Outpost");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(181391, -78685, -2731);
						player.sendMessage("You has been teleported to: Imperial Tomb");
						showAgain(player);
					}
					else
					{
						player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
						showAgain(player);
					}
				}
				else if ((player.getLevel() >= 80) && (player.getLevel() <= 86))
				{
					// TODO: all archers
					/*
					 * if (player.getClass().a) { player.teleToLocation(87252, 85514, -3056); player.sendMessage("You has been teleported to: Plains of lizardmens"); }
					 */
					
					if (Rnd.get(100) < 33)
					{
						player.teleToLocation(174528, 52683, -4369);
						player.sendMessage("You has been teleported to: Giants Cave");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(118829, -80566, -2692);
						player.sendMessage("You has been teleported to: Monastery of Silence - 2nd floor");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(189429, -79369, -6994);
						player.sendMessage("You has been teleported to: Imperial Tomb Inside");
						showAgain(player);
					}
					else if (Rnd.get(100) < 33)
					{
						player.teleToLocation(171385, -116124, -2120);
						player.sendMessage("You has been teleported to: Forge of the Gods");
						showAgain(player);
					}
					else
					{
						player.sendMessage("Global teleporter: Ehhmm.... I cant find place for you... try again...");
						showAgain(player);
					}
				}
			}
			
		}
		else
		{
			player.sendMessage("Automatic Teleporter: Services is disabled for now.");
		}
		_log.info("Automatic Teleport NPC: used command: " + event);
		return "";
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (player.getQuestState(getName()) == null)
		{
			newQuestState(player);
		}
		if (USE_TELEPORTER)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(5);
			html.setFile(player.getHtmlPrefix(), "data/scripts/l2jpdt_npcs/AutomaticTeleporterNPC/index.htm");
			html.replace("%itemname%", ITEM_NAME);
			html.replace("%player%", player.getName());
			player.sendPacket(html);
		}
		else
		{
			player.sendMessage("Automatic Teleporter: Services is disabled for now.");
		}
		return "";
	}
}