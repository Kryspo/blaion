/*
  * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.zone.type.L2BossZone;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.templates.StatsSet;

import ai.Baium;
import ai.Antharas.Antharas;
import ai.Valakas.Valakas;

/**
 * @author L2jprivateDevelopersTeam
 */
public class AdminGB implements IAdminCommandHandler
{
	private static final int SAILREN = 29065; // Sailren
	private static final int BELETH = 29118; // Beleth
	private static final int ANTHARAS = 29068; // Antharas
	private static final int ANTHARAS_ZONE = 70050; // Antharas Nest
	private static final int VALAKAS = 29028; // Valakas
	private static final int VALAKAS_ZONE = 12010; // Valakas Nest
	private static final int BAIUM = 29020; // Baium
	private static final int BAIUM_ZONE = 12002; // Baium Nest
	private static final int QUEENANT = 29001; // Queen Ant
	private static final int ORFEN = 29014; // Orfen
	private static final int CORE = 29006; // Core
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_grandboss",
		"admin_grandboss_skip",
		"admin_grandboss_respawn",
		"admin_grandboss_minions",
		"admin_grandboss_abort",
		"admin_beleth_start", // this command start Beleth Boss and teleport you to room before Beleth Chamber
		"admin_beleth_reset", // Reset GB status
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_beleth_start":
			{
				GrandBossManager.getInstance().setBossStatus(BELETH, 1);
				activeChar.teleToLocation(16342, 209557, -9352, true);
				AdminHelpPage.showHelpPage(activeChar, "gb_menu.htm");
				break;
			}
			case "admin_beleth_reset":
			{
				GrandBossManager.getInstance().setBossStatus(BELETH, 0);
				AdminHelpPage.showHelpPage(activeChar, "gb_menu.htm");
				break;
			}
			case "admin_grandboss":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					manageHtml(activeChar, grandBossId);
				}
				else
				{
					NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
					html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/admin/grandbosses/grandboss.htm"));
					activeChar.sendPacket(html);
				}
				break;
			}
			
			case "admin_grandboss_skip":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("SKIP_WAITING", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case VALAKAS:
						{
							valakasAi().notifyEvent("SKIP_WAITING", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						default:
						{
							activeChar.sendMessage("Wrong ID!");
							break;
						}
					}
				}
				else
				{
					activeChar.sendMessage("Usage: //grandboss_skip Id");
				}
				break;
			}
			case "admin_grandboss_respawn":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("RESPAWN_ANTHARAS", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case VALAKAS:
						{
							valakasAi().notifyEvent("RESPAWN_VALAKAS", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("RESPAWN_BAIUM", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case BELETH:
						{
							GrandBossManager.getInstance().setBossStatus(BELETH, 0);
							activeChar.sendMessage("Beleth Grand Boss status is - Alive");
							manageHtml(activeChar, grandBossId);
							break;
						}
						case SAILREN:
						{
							GrandBossManager.getInstance().setBossStatus(SAILREN, 0);
							activeChar.sendMessage("Sailren Grand Boss status is - Alive");
							manageHtml(activeChar, grandBossId);
							break;
						}
						default:
						{
							activeChar.sendMessage("Wrong ID!");
						}
					}
				}
				else
				{
					activeChar.sendMessage("Usage: //grandboss_respawn Id");
				}
				break;
			}
			case "admin_grandboss_minions":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("DESPAWN_MINIONS", null, activeChar);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("DESPAWN_MINIONS", null, activeChar);
							break;
						}
						default:
						{
							activeChar.sendMessage("Wrong ID!");
						}
					}
				}
				else
				{
					activeChar.sendMessage("Usage: //grandboss_minions Id");
				}
				break;
			}
			case "admin_grandboss_abort":
			{
				if (st.hasMoreTokens())
				{
					final int grandBossId = Integer.parseInt(st.nextToken());
					
					switch (grandBossId)
					{
						case ANTHARAS:
						{
							antharasAi().notifyEvent("ABORT_FIGHT", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case VALAKAS:
						{
							valakasAi().notifyEvent("ABORT_FIGHT", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						case BAIUM:
						{
							baiumAi().notifyEvent("ABORT_FIGHT", null, activeChar);
							manageHtml(activeChar, grandBossId);
							break;
						}
						default:
						{
							activeChar.sendMessage("Wrong ID!");
						}
					}
				}
				else
				{
					activeChar.sendMessage("Usage: //grandboss_abort Id");
				}
			}
				break;
		}
		return true;
	}
	
	private void manageHtml(L2PcInstance activeChar, int grandBossId)
	{
		if (Arrays.asList(BELETH, SAILREN, ANTHARAS, VALAKAS, BAIUM, QUEENANT, ORFEN, CORE).contains(grandBossId))
		{
			final int bossStatus = GrandBossManager.getInstance().getBossStatus(grandBossId);
			L2BossZone bossZone = null;
			String textColor = null;
			String text = null;
			String htmlPatch = null;
			int deadStatus = 0;
			
			switch (grandBossId)
			{
				case BELETH:
				{
					htmlPatch = "data/html/admin/grandbosses/grandboss_beleth.htm";
					break;
				}
				case SAILREN:
				{
					htmlPatch = "data/html/admin/grandbosses/grandboss_sailren.htm";
					break;
				}
				case ANTHARAS:
				{
					bossZone = ZoneManager.getInstance().getZoneById(ANTHARAS_ZONE, L2BossZone.class);
					htmlPatch = "data/html/admin/grandbosses/grandboss_antharas.htm";
					break;
				}
				case VALAKAS:
				{
					bossZone = ZoneManager.getInstance().getZoneById(VALAKAS_ZONE, L2BossZone.class);
					htmlPatch = "data/html/admin/grandbosses/grandboss_valakas.htm";
					break;
				}
				case BAIUM:
				{
					bossZone = ZoneManager.getInstance().getZoneById(BAIUM_ZONE, L2BossZone.class);
					htmlPatch = "data/html/admin/grandbosses/grandboss_baium.htm";
					break;
				}
				case QUEENANT:
				{
					htmlPatch = "data/html/admin/grandbosses/grandboss_queenant.htm";
					break;
				}
				case ORFEN:
				{
					htmlPatch = "data/html/admin/grandbosses/grandboss_orfen.htm";
					break;
				}
				case CORE:
				{
					htmlPatch = "data/html/admin/grandbosses/grandboss_core.htm";
					break;
				}
			}
			
			if (Arrays.asList(ANTHARAS, VALAKAS, BAIUM, BELETH, SAILREN).contains(grandBossId))
			{
				deadStatus = 3;
				switch (bossStatus)
				{
					case 0:
					{
						textColor = "00FF00"; // Green
						text = "Alive";
						break;
					}
					case 1:
					{
						textColor = "FFFF00"; // Yellow
						text = "Waiting";
						break;
					}
					case 2:
					{
						textColor = "FF9900"; // Orange
						text = "In Fight";
						break;
					}
					case 3:
					{
						textColor = "FF0000"; // Red
						text = "Dead";
						break;
					}
				}
			}
			else
			{
				deadStatus = 1;
				switch (bossStatus)
				{
					case 0:
					{
						textColor = "00FF00"; // Green
						text = "Alive";
						break;
					}
					case 1:
					{
						textColor = "FF0000"; // Red
						text = "Dead";
						break;
					}
				}
			}
			
			final StatsSet info = GrandBossManager.getInstance().getStatsSet(grandBossId);
			final String bossRespawn = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(info.getLong("respawn_time"));
			
			NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
			html.setHtml(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), htmlPatch));
			html.replace("%bossStatus%", text);
			html.replace("%bossColor%", textColor);
			html.replace("%respawnTime%", bossStatus == deadStatus ? bossRespawn : "Already respawned!");
			html.replace("%playersInside%", bossZone != null ? String.valueOf(bossZone.getPlayersInside().size()) : "Zone not found!");
			activeChar.sendPacket(html);
		}
		else
		{
			activeChar.sendMessage("Wrong ID!");
		}
	}
	
	private Quest antharasAi()
	{
		return QuestManager.getInstance().getQuest(Antharas.class.getSimpleName());
	}
	
	private Quest valakasAi()
	{
		return QuestManager.getInstance().getQuest(Valakas.class.getSimpleName());
	}
	
	private Quest baiumAi()
	{
		return QuestManager.getInstance().getQuest(Baium.class.getSimpleName());
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
