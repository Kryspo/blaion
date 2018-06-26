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
package events.HideAndSeek;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.templates.StatsSet;
import com.l2jserver.util.Rnd;

import javolution.util.FastList;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class HideAndSeek extends Quest
{
	protected static final int REG_NPC = Config.HAS_REGISTER_NPC;
	protected static final int[] REG_NPC_COORDS =
	{
		// X,Y,Z, heading
		//@formatter:off
		Config.HAS_NPC_X,Config.HAS_NPC_Y,Config.HAS_NPC_Z,1//82698,148638,-3473,1
		//@formatter:on
	};
	protected static int MIN_LEVEL = Config.HAS_MIN_LEVEL;
	protected static int MAX_LEVEL = Config.HAS_MAX_LEVEL;
	protected static int MIN_PLAYERS = Config.HAS_MIN_PLAYERS;
	protected static int MAX_PLAYERS = Config.HAS_MAX_PLAYERS;
	
	public static boolean _canRegister = false;
	public static boolean _active = false;
	
	protected static int HIDE_NPC = 0;
	public static final Engine _event = new Engine();
	private static int lastNpc = 0;
	private static int nextNpc = 0;
	
	private static final List<StatsSet> hideNpcs = new FastList<StatsSet>();
	protected static final FastList<L2PcInstance> _players = new FastList<L2PcInstance>();
	
	public HideAndSeek()
	{
		super(-1, HideAndSeek.class.getSimpleName(), "events");
		StartNpcs(REG_NPC);
		FirstTalkNpcs(REG_NPC);
		TalkNpcs(REG_NPC);
		loadHolders();
		for (final StatsSet holder : hideNpcs)
		{
			if (holder != null)
			{
				FirstTalkNpcs(holder.getInt("npc"));
			}
			else
			{
				_log.config("Hide and Seek Engine: Null holder!");
			}
		}
		if (Config.HAS_ENABLED)
		{
			scheduleEventStart();
			_log.info("Hide and Seek Engine: Started.");
		}
		else
		{
			_log.info("Hide and Seek Engine: Engine is disabled.");
		}
	}
	
	@Override
	public String onFirstTalk(L2Npc npc, L2PcInstance player)
	{
		if (!_active)
		{
			return null;
		}
		if ((npc == null) || (player == null))
		{
			return null;
		}
		
		String htmltext = "";
		final int id = npc.getNpcId();
		
		if (id == REG_NPC)
		{
			QuestState st = player.getQuestState(getName());
			if (st == null)
			{
				final Quest q = QuestManager.getInstance().getQuest(getName());
				st = q.newQuestState(player);
			}
			htmltext = "registration_main.htm";
		}
		else if (id == HIDE_NPC)
		{
			if (_players.contains(player))
			{
				_event.endGame(player);
				htmltext = "winner.htm";
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (!_active || !_canRegister)
		{
			return null;
		}
		if ((npc == null) || (player == null))
		{
			return null;
		}
		
		String htmltext = "";
		
		if (event.equalsIgnoreCase("register"))
		{
			if (addPlayer(player))
			{
				htmltext = "registered.htm";
			}
		}
		else if (event.equalsIgnoreCase("unregister"))
		{
			if (removePlayer(player))
			{
				htmltext = "unregistered.htm";
			}
		}
		return htmltext;
	}
	
	private static boolean addPlayer(L2PcInstance player)
	{
		final int level = player.getLevel();
		if (player.isInOlympiadMode())
		{
			player.sendPacket(message("Cannot join while in olympiad"));
		}
		else if (Config.HAS_PK_PLAYER_CAN_JOIN && ((player.getKarma() > 0) || player.isCursedWeaponEquipped()))
		{
			player.sendPacket(message("Cannot join while holding karma or cursed weapon"));
		}
		else if (TvTEvent.isPlayerParticipant(player.getObjectId()))
		{
			player.sendPacket(message("Cannot join while in TvT Event"));
		}
		else if (_players.contains(player))
		{
			player.sendPacket(message("You are alredy in the waiting list for the event!"));
		}
		else if ((HideAndSeek.MIN_LEVEL != -1) && (level < HideAndSeek.MIN_LEVEL))
		{
			player.sendPacket(message("Your level is too low, min level is " + HideAndSeek.MIN_LEVEL));
		}
		else if ((HideAndSeek.MAX_LEVEL != -1) && (level > HideAndSeek.MAX_LEVEL))
		{
			player.sendPacket(message("Your level is too high, max level is " + HideAndSeek.MAX_LEVEL));
		}
		else if ((HideAndSeek.MAX_PLAYERS != -1) && (_players.size() == HideAndSeek.MAX_PLAYERS))
		{
			player.sendPacket(message("Max amount of player for event reached, you cannot join."));
		}
		else
		{
			_players.add(player);
			player.sendPacket(message("You have been added for the Hide and Seek Event!"));
			return true;
		}
		return false;
	}
	
	private static boolean removePlayer(L2PcInstance player)
	{
		if (!_players.contains(player))
		{
			return false;
		}
		_players.remove(player);
		player.sendPacket(message("You have been removed from the event!"));
		return true;
	}
	
	private static final CreatureSay message(String message)
	{
		final CreatureSay say = new CreatureSay(0, Say2.TELL, "Hide & Seek", message);
		return say;
	}
	
	protected static final void clue(String message)
	{
		final CreatureSay say = new CreatureSay(0, Say2.PARTY, "Hide & Seek", message);
		for (final L2PcInstance plr : _players)
		{
			if (plr != null)
			{
				plr.sendPacket(say);
			}
		}
	}
	
	private static final void loadHolders()
	{
		Connection con = null;
		try
		{
			read();
			
			con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM hide_and_seek");
			final ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				final StatsSet set = new StatsSet();
				
				set.set("npc", rset.getInt("npc"));
				
				set.set("x", rset.getInt("x"));
				set.set("y", rset.getInt("y"));
				set.set("z", rset.getInt("z"));
				
				set.set("0_clue", rset.getString("first_clue"));
				set.set("1_clue", rset.getString("second_clue"));
				set.set("2_clue", rset.getString("third_clue"));
				
				set.set("rewards", rset.getString("rewards"));
				
				hideNpcs.add(set);
			}
			rset.close();
			statement.close();
		}
		catch (final Exception e)
		{
			System.out.println("Couldnt Load Hided npc stats!");
			e.printStackTrace();
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
	}
	
	protected static StatsSet getNextNpc()
	{
		if (nextNpc == hideNpcs.size())
		{
			nextNpc = 0;
		}
		StatsSet holder = hideNpcs.get(nextNpc);
		while (holder.getInt("npc") == lastNpc)
		{
			nextNpc++;
			if (nextNpc == hideNpcs.size())
			{
				nextNpc = 0;
			}
			holder = hideNpcs.get(nextNpc);
		}
		nextNpc++;
		lastNpc = holder.getInt("npc");
		save();
		return holder;
	}
	
	protected static StatsSet getRandomNpc()
	{
		final int size = hideNpcs.size();
		int pos = Rnd.get(size);
		StatsSet holder = hideNpcs.get(pos);
		// Keep getting a new holder while hodler.getNpc() equals lastNpc
		while (holder.getInt("npc") == lastNpc)
		{
			pos = Rnd.get(size);
			holder = hideNpcs.get(pos);
		}
		lastNpc = holder.getInt("npc");
		save();
		return holder;
	}
	
	private static final void read()
	{
		try
		{
			final File file = new File(Config.DATAPACK_ROOT, "data/scripts/events/HideAndSeek/npc_save.properties");
			final FileInputStream reader = new FileInputStream(file);
			final Properties prop = new Properties();
			prop.load(reader);
			lastNpc = Integer.parseInt(prop.getProperty("lastNpc", "0"));
			nextNpc = Integer.parseInt(prop.getProperty("nextNpc", "0"));
		}
		catch (final IOException ioe)
		{
			System.out.println("Hide and Seek: Error while reading last and next npc from npc_save!");
			ioe.printStackTrace();
		}
	}
	
	private static final void save()
	{
		try
		{
			final File file = new File(Config.DATAPACK_ROOT, "data/scripts/events/HideAndSeek/npc_save.properties");
			final FileInputStream reader = new FileInputStream(file);
			final Properties prop = new Properties();
			prop.load(reader);
			prop.setProperty("lastNpc", String.valueOf(lastNpc));
			prop.setProperty("nextNpc", String.valueOf(nextNpc));
			prop.store(new FileOutputStream(file), "");
		}
		catch (final IOException ioe)
		{
			System.out.println("Hide and Seek: Error while saving last and next npc to npc_save!");
			ioe.printStackTrace();
		}
	}
	
	private static void scheduleEventStart()
	{
		try
		{
			final Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (final String timeOfDay : Config.HAS_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				final String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			final long delay = nextStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
			ThreadPoolManager.getInstance().scheduleGeneral(new ScheduledEventStart(), delay);
		}
		catch (final Exception e)
		{
			_log.warning("Couldn't Initialize Hide and Seek Event");
			e.printStackTrace();
		}
	}
	
	static class ScheduledEventStart implements Runnable
	{
		@Override
		public void run()
		{
			if (_active)
			{
				return;
			}
			
			_canRegister = true;
			_active = true;
			StatsSet holder = null;
			
			holder = Config.HAS_SEQUENCE_NPC ? getNextNpc() : getRandomNpc();
			
			if (holder == null)
			{
				System.out.println("Hide and Seek: Stopped due null InfoHolder");
				return;
			}
			HIDE_NPC = holder.getInt("npc");
			_event.setHolder(holder);
			_event.launchGame();
		}
	}
}
