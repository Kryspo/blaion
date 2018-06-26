package com.l2jserver.gameserver;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.util.Rnd;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class AutoAnnounceOnline
{
	static int _fakeOnlinePlayer = Config.FAKE_PLAYERS;
	
	protected static void StartAnnounce()
	{
		int OnlinePlayers = L2World.getInstance().getAllPlayersCount();
		
		if (OnlinePlayers >= 1)
		{
			if (Config.CRITICAL_ONLINE_ANNOUNCE)
			{
				Announcements.getInstance().announceToAll(OnlinePlayers + _fakeOnlinePlayer + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS) + " players are online. Have a nice day.", true);
			}
			else
			{
				Announcements.getInstance().announceToAll(OnlinePlayers + _fakeOnlinePlayer + Rnd.get(Config.MIN_RND_FAKE_PPLS, Config.MAX_RND_FAKE_PPLS) + " players are online. Have a nice day.");
			}
		}
	}
	
	public static void getInstance()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> StartAnnounce(), 0, Config.ANNOUNCE_ONLINE_PLAYERS_DELAY * 1000);
	}
}