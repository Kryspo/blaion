/*
 * Copyright (C) 2004-2014 L2J Server
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
package com.l2jserver.gameserver.communitybbs.Managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.CharNameTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class MessageBoxBBSManager extends BaseBBSManager
{
	// private final Logger log = Logger.getLogger("BBS");
	private static MessageBoxBBSManager instance = new MessageBoxBBSManager();
	private final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY");
	
	private MessageBoxBBSManager()
	{
	}
	
	private String showTopicList(L2PcInstance activeChar, int page, String type, String operation)
	{
		StringBuilder builder = new StringBuilder(768);
		String sql = "SELECT * FROM bbs_article order by number DESC Limit ?,12";
		String sql1 = "SELECT count(author) FROM bbs_article";
		if (type.equals("my"))
		{
			sql = "SELECT * FROM bbs_article WHERE author=? order by number";
		}
		else if (!type.equals("all"))
		{
			sql = "SELECT * FROM bbs_article WHERE type=? order by number DESC Limit ?,12";
			sql1 = "SELECT count(author) FROM bbs_article WHERE type=?";
		}
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			PreparedStatement stmt1 = con.prepareStatement(sql1))
		{
			int start = (page - 1) * 12;
			if (type.equals("my"))
			{
				stmt.setInt(1, activeChar.getObjectId());
			}
			else if (type.equals("all"))
			{
				stmt.setInt(1, start);
			}
			else
			{
				stmt.setString(1, type);
				stmt.setInt(2, start);
				stmt1.setString(1, type);
			}
			
			try (ResultSet rset = stmt.executeQuery())
			{
				Article aritcle;
				while (rset.next())
				{
					aritcle = new Article(rset);
					drawTopic(builder, aritcle, page, type, operation);
				}
			}
			
			int count = 0;
			try (ResultSet rset = stmt1.executeQuery())
			{
				if (rset.next())
				{
					count = rset.getInt("count(author)");
				}
			}
			
			int pages = (count % 12) == 0 ? count / 12 : (count / 12) + 1;
			
			builder.append("<br>");
			builder.append("<table>");
			if ((page > 1) && (page < pages))
			{
				builder.append("<tr>");
				builder.append("<td>").append("<button action=\"bypass _bbsmsg " + type + " " + (page - 1) + "\" value=\"Prev\" width=40 height=25 back=\"L2UI_CT1.Button_DF_Msn_down\" fore=\"L2UI_CT1.Button_DF_Msn\">").append("</td>");
				builder.append("<td>").append("<button action=\"bypass _bbsmsg " + type + " " + (page + 1) + "\" value=\"Next\" width=40 height=25 back=\"L2UI_CT1.Button_DF_Msn_down\" fore=\"L2UI_CT1.Button_DF_Msn\">").append("</td>");
				builder.append("</tr>");
			}
			else if ((page == pages) && (pages > 1)) // last page
			{
				builder.append("<tr>");
				builder.append("<td>").append("<button action=\"bypass _bbsmsg " + type + " " + (page - 1) + "\" value=\"Prev\" width=40 height=25 back=\"L2UI_CT1.Button_DF_Msn_down\" fore=\"L2UI_CT1.Button_DF_Msn\">").append("</td>");
				builder.append("</tr>");
			}
			else if ((page == 1) && (pages > 1)) // first page and there are more than one page
			{
				builder.append("<tr>");
				builder.append("<td>").append("<button action=\"bypass _bbsmsg " + type + " " + (page + 1) + "\" value=\"Next\" width=40 height=25 back=\"L2UI_CT1.Button_DF_Msn_down\" fore=\"L2UI_CT1.Button_DF_Msn\">").append("</td>");
				builder.append("</tr>");
			}
			builder.append("</table>");
		}
		catch (SQLException se)
		{
			se.printStackTrace();
		}
		return builder.toString();
	}
	
	public static MessageBoxBBSManager getInstance()
	{
		return instance;
	}
	
	@Override
	public void parsecmd(String command, L2PcInstance activeChar)
	{
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/message_box.htm");
		String[] event = command.split(" ");
		if (event[1].equals("all"))
		{
			int page = Integer.parseInt(event[2]);
			html = html.replace("%list%", showTopicList(activeChar, page, "all", "view"));
		}
		else if (event[1].equals("my"))
		{
			html = html.replace("%list%", showTopicList(activeChar, 0, "my", "delete"));
		}
		else if (event[1].equals("delete"))
		{
			int num = Integer.parseInt(event[2]);
			L2DatabaseFactory.simpleExcuter("DELETE FROM bbs_article WHERE number = ?", num);
			html = html.replace("%list%", showTopicList(activeChar, 0, "my", "delete"));
		}
		else if (event[1].equals("view"))
		{
			int num = Integer.parseInt(event[2]);
			String type = event[3];
			int page = Integer.parseInt(event[4]);
			html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/message.htm");
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM bbs_article WHERE number = ?"))
			{
				stmt.setInt(1, num);
				try (ResultSet rset = stmt.executeQuery())
				{
					if (rset.next())
					{
						html = html.replace("%title%", rset.getString("title"));
						html = html.replace("%content%", rset.getString("content"));
						html = html.replace("%date%", rset.getString("date"));
						html = html.replace("%author%", CharNameTable.getInstance().getNameById(rset.getInt("author")) == null ? "NONE" : CharNameTable.getInstance().getNameById(rset.getInt("author")));
						html = html.replace("%type%", type);
						html = html.replace("%page%", String.valueOf(page));
					}
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		else if (event[1].equals("new"))
		{
			String type = event[2];
			String[] topic = command.split(" / ");
			
			if (topic.length != 3)
			{
				html = "<html><body><br><br><center><font name=\"hs12\" color=\"LEVEL\">Please fill in complete information!</font></center></body></html>";
				separateAndSend(html, activeChar);
				return;
			}
			
			String subject = topic[1];
			String content = topic[2];
			
			if ((content.length() < 255) && (subject.length() < 30))
			{
				content = content.replaceAll("[\\r][\\n]", "<br1>");
				
				if (L2DatabaseFactory.simpleExcuter("INSERT INTO bbs_article (author,type,title,content,date) values(?,?,?,?,?)", activeChar.getObjectId(), type, subject, content, formatter.format(new Date())) > 0)
				{
					html = "<html><body><br><br><center><font name=\"hs12\" color=\"LEVEL\">Post Success!</font></center></body></html>";
				}
				else
				{
					html = "<html><body><br><br><center><font name=\"hs12\" color=\"LEVEL\">Post Failure!</font></center></body></html>";
				}
			}
			else
			{
				html = "<html><body><br><br><center><font name=\"hs12\" color=\"LEVEL\">Charactes is to long!</font></center></body></html>";
			}
		}
		else
		{
			int page = Integer.parseInt(event[2]);
			html = html.replace("%list%", showTopicList(activeChar, page, event[1], "view"));
		}
		separateAndSend(html, activeChar);
	}
	
	private String drawAricleType(Article article)
	{
		String type = article.getType();
		if (type.equals("Sell"))
		{
			type = "[<font color=00FF00>" + type + "</font>]";
		}
		else if (type.equals("Buy"))
		{
			type = "[<font color=1C86EE>" + type + "</font>]";
		}
		else if (type.equals("Other"))
		{
			type = "[<font color=FF3030>" + type + "</font>]";
		}
		else if (type.equals("Invite"))
		{
			type = "[<font color=9F79EE>" + type + "</font>]";
		}
		return type;
	}
	
	private String drawTitle(Article article)
	{
		String title = article.getTitle();
		if (title.length() > 30)
		{
			title = title.substring(0, 29);
			title += "...";
		}
		
		return title;
	}
	
	/**
	 * @param builder
	 * @param article
	 * @param page
	 * @param type
	 * @param operation
	 */
	private void drawTopic(StringBuilder builder, Article article, int page, String type, String operation)
	{
		builder.append("<table width=730 height=22 cellspacing=0 cellpadding=-1>");
		builder.append("<tr>");
		builder.append("<td fixwidth=10></td>");
		builder.append("<td fixwidth=100>").append(drawAricleType(article)).append("</td>");
		builder.append("<td fixwidth=340>").append(drawTitle(article)).append("</td>");
		builder.append("<td fixwidth=140>").append(CharNameTable.getInstance().getNameById(article.getAuthor())).append("</td>");
		builder.append("<td fixwidth=100>").append(article.getDate()).append("</td>");
		builder.append("<td fixwidth=60>").append("<a action=\"bypass -h _bbsmsg " + operation + " " + article.getNum() + " " + type + " " + page + "\">" + operation + "</a>").append("</td>");
		builder.append("</tr>");
		builder.append("</table>");
		builder.append("<img src=\"L2UI.SquareGray\" width=755 height=1>");
	}
	
	@Override
	public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		// TODO Auto-generated method stub
	}
	
	class Article
	{
		int num;
		int author;
		String type;
		String title;
		String date;
		
		/**
		 * @return the date
		 */
		public String getDate()
		{
			return date;
		}
		
		Article(ResultSet rset) throws SQLException
		{
			num = rset.getInt("number");
			author = rset.getInt("author");
			type = rset.getString("type");
			title = rset.getString("title");
			date = rset.getString("date");
		}
		
		/**
		 * @param num
		 * @param author
		 * @param type
		 * @param title
		 * @param date
		 */
		public Article(int num, int author, String type, String title, String date)
		{
			super();
			this.num = num;
			this.author = author;
			this.type = type;
			this.title = title;
			this.date = date;
		}
		
		/**
		 * @return the num
		 */
		public int getNum()
		{
			return num;
		}
		
		/**
		 * @return the author
		 */
		public int getAuthor()
		{
			return author;
		}
		
		/**
		 * @return the type
		 */
		public String getType()
		{
			return type;
		}
		
		/**
		 * @return the title
		 */
		public String getTitle()
		{
			return title;
		}
	}
}
