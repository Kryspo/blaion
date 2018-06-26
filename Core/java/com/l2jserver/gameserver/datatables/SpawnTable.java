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
package com.l2jserver.gameserver.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.instancemanager.DayNightSpawnManager;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;

import javolution.util.FastMap;
import javolution.util.FastSet;

/**
 * This class ...
 * @author Nightmare
 * @version $Revision: 1.5.2.6.2.7 $ $Date: 2005/03/27 15:29:18 $
 */
public class SpawnTable
{
	private static Logger _log = Logger.getLogger(SpawnTable.class.getName());
	
	// private final FastSet<L2Spawn> _spawntable = new FastSet<L2Spawn>().shared();
	private static final Map<Integer, Set<L2Spawn>> _spawnTable = new FastMap<Integer, Set<L2Spawn>>().shared();
	private int _npcSpawnCount;
	private int _customSpawnCount;
	
	public static SpawnTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private SpawnTable()
	{
		if (!Config.ALT_DEV_NO_SPAWNS)
		{
			fillSpawnTable();
		}
	}
	
	/*
	 * public FastSet<L2Spawn> getSpawnTable() { return _spawntable; }
	 */
	
	public Map<Integer, Set<L2Spawn>> getSpawnTable()
	{
		return _spawnTable;
	}
	
	private void fillSpawnTable()
	{
		Connection con = null;
		
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT spawn_name, count, npc_templateid, locx, locy, locz, heading, respawn_delay, loc_id, periodOfDay FROM spawnlist");
			ResultSet rset = statement.executeQuery();
			
			L2Spawn spawnDat;
			L2NpcTemplate template1;
			
			while (rset.next())
			{
				template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
				if (template1 != null)
				{
					if (template1.type.equalsIgnoreCase("L2SiegeGuard"))
					{
						// Don't spawn
					}
					else if (template1.type.equalsIgnoreCase("L2RaidBoss"))
					{
						// Don't spawn raidboss
					}
					else if (!Config.ALLOW_CLASS_MASTERS && template1.type.equals("L2ClassMaster"))
					{
						// Dont' spawn class masters
					}
					else
					{
						spawnDat = new L2Spawn(template1);
						spawnDat.setAmount(rset.getInt("count"));
						spawnDat.setXLocation(rset.getInt("locx"));
						spawnDat.setYLocation(rset.getInt("locy"));
						spawnDat.setZLocation(rset.getInt("locz"));
						spawnDat.setHeading(rset.getInt("heading"));
						spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
						int loc_id = rset.getInt("loc_id");
						spawnDat.setLocation(loc_id);
						String spawnName = rset.getString("spawn_name");
						if ((spawnName != null) && !spawnName.isEmpty())
						{
							spawnDat.setName(spawnName);
						}
						switch (rset.getInt("periodOfDay"))
						{
							case 0: // default
								_npcSpawnCount += spawnDat.init();
								break;
							case 1: // Day
								DayNightSpawnManager.getInstance().addDayCreature(spawnDat);
								_npcSpawnCount++;
								break;
							case 2: // Night
								DayNightSpawnManager.getInstance().addNightCreature(spawnDat);
								_npcSpawnCount++;
								break;
						}
						
						// _spawntable.add(spawnDat);
						addSpawn(spawnDat);
					}
				}
				else
				{
					_log.warning("SpawnTable: Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
				}
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			// problem with initializing spawn, go to next one
			_log.log(Level.WARNING, "SpawnTable: Spawn could not be initialized: " + e.getMessage(), e);
		}
		finally
		{
			L2DatabaseFactory.close(con);
		}
		
		_log.info("SpawnTable: Loaded " + _spawnTable.size() + " Npc Spawn Locations.");
		
		if (Config.CUSTOM_SPAWNLIST_TABLE)
		{
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("SELECT count, npc_templateid, locx, locy, locz, heading, respawn_delay, loc_id, periodOfDay FROM custom_spawnlist");
				ResultSet rset = statement.executeQuery();
				
				L2Spawn spawnDat;
				L2NpcTemplate template1;
				
				while (rset.next())
				{
					template1 = NpcTable.getInstance().getTemplate(rset.getInt("npc_templateid"));
					if (template1 != null)
					{
						if (template1.type.equalsIgnoreCase("L2SiegeGuard"))
						{
							// Don't spawn
						}
						else if (template1.type.equalsIgnoreCase("L2RaidBoss"))
						{
							// Don't spawn raidboss
						}
						else if (!Config.ALLOW_CLASS_MASTERS && template1.type.equals("L2ClassMaster"))
						{
							// Dont' spawn class masters
						}
						else
						{
							spawnDat = new L2Spawn(template1);
							spawnDat.setAmount(rset.getInt("count"));
							spawnDat.setXLocation(rset.getInt("locx"));
							spawnDat.setYLocation(rset.getInt("locy"));
							spawnDat.setZLocation(rset.getInt("locz"));
							spawnDat.setHeading(rset.getInt("heading"));
							spawnDat.setRespawnDelay(rset.getInt("respawn_delay"));
							spawnDat.setCustom(true);
							int loc_id = rset.getInt("loc_id");
							spawnDat.setLocation(loc_id);
							
							switch (rset.getInt("periodOfDay"))
							{
								case 0: // default
									_customSpawnCount += spawnDat.init();
									break;
								case 1: // Day
									DayNightSpawnManager.getInstance().addDayCreature(spawnDat);
									_customSpawnCount++;
									break;
								case 2: // Night
									DayNightSpawnManager.getInstance().addNightCreature(spawnDat);
									_customSpawnCount++;
									break;
							}
							
							// _spawntable.add(spawnDat);
							addSpawn(spawnDat);
						}
					}
					else
					{
						_log.warning("CustomSpawnTable: Data missing in NPC table for ID: " + rset.getInt("npc_templateid") + ".");
					}
				}
				rset.close();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with initializing spawn, go to next one
				_log.log(Level.WARNING, "CustomSpawnTable: Spawn could not be initialized: " + e.getMessage(), e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
			_log.info("CustomSpawnTable: Loaded " + _customSpawnCount + " Npc Spawn Locations.");
			
		}
		
		if (Config.DEBUG)
		{
			_log.fine("SpawnTable: Spawning completed, total number of NPCs in the world: " + (_npcSpawnCount + _customSpawnCount));
		}
		
	}
	
	/**
	 * Add a spawn to the spawn set if present, otherwise add a spawn set and add the spawn to the newly created spawn set.
	 * @param spawn the NPC spawn to add
	 */
	private void addSpawn(L2Spawn spawn)
	{
		if (!_spawnTable.containsKey(spawn.getNpcId()))
		{
			_spawnTable.put(spawn.getNpcId(), new FastSet<L2Spawn>().shared());
		}
		_spawnTable.get(spawn.getNpcId()).add(spawn);
	}
	
	public void addNewSpawn(L2Spawn spawn, boolean storeInDb)
	{
		// _spawntable.add(spawn);
		addSpawn(spawn);
		if (storeInDb)
		{
			String spawnTable;
			if (spawn.isCustom() && Config.CUSTOM_SPAWNLIST_TABLE)
			{
				spawnTable = "custom_spawnlist";
			}
			else
			{
				spawnTable = "spawnlist";
			}
			
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO " + spawnTable + "(count,npc_templateid,locx,locy,locz,heading,respawn_delay,loc_id) values(?,?,?,?,?,?,?,?)");
				statement.setInt(1, spawn.getAmount());
				statement.setInt(2, spawn.getNpcId());
				statement.setInt(3, spawn.getLocx());
				statement.setInt(4, spawn.getLocy());
				statement.setInt(5, spawn.getLocz());
				statement.setInt(6, spawn.getHeading());
				statement.setInt(7, spawn.getRespawnDelay() / 1000);
				statement.setInt(8, spawn.getLocationId());
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with storing spawn
				_log.log(Level.WARNING, "SpawnTable: Could not store spawn in the DB:" + e.getMessage(), e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
		}
	}
	
	public void deleteSpawn(L2Spawn spawn, boolean updateDb)
	{
		
		/*
		 * if (!_spawntable.remove(spawn)) { return; }
		 */
		
		if (!removeSpawn(spawn))
		{
			return;
		}
		if (updateDb)
		{
			Connection con = null;
			try
			{
				con = L2DatabaseFactory.getInstance().getConnection();
				PreparedStatement statement = con.prepareStatement("DELETE FROM " + (spawn.isCustom() ? "custom_spawnlist" : "spawnlist") + " WHERE locx=? AND locy=? AND locz=? AND npc_templateid=? AND heading=?");
				statement.setInt(1, spawn.getLocx());
				statement.setInt(2, spawn.getLocy());
				statement.setInt(3, spawn.getLocz());
				statement.setInt(4, spawn.getNpcId());
				statement.setInt(5, spawn.getHeading());
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				// problem with deleting spawn
				_log.log(Level.WARNING, "SpawnTable: Spawn " + spawn + " could not be removed from DB: " + e.getMessage(), e);
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}
			
		}
	}
	
	// just wrapper
	public void reloadAll()
	{
		fillSpawnTable();
	}
	
	/**
	 * Get all the spawn of a NPC<BR>
	 * <BR>
	 * @param npcId : ID of the NPC to find.
	 * @return
	 */
	public void findNPCInstances(L2PcInstance activeChar, int npcId, int teleportIndex, boolean showposition)
	{
		int index = 0;
		for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(npcId))
		{
			index++;
			L2Npc npc = spawn.getLastSpawn();
			if (teleportIndex > -1)
			{
				if (teleportIndex == index)
				{
					if (showposition && (npc != null))
					{
						activeChar.teleToLocation(npc.getX(), npc.getY(), npc.getZ(), true);
					}
					else
					{
						activeChar.teleToLocation(spawn.getLocx(), spawn.getLocy(), spawn.getLocz(), true);
					}
				}
			}
			else
			{
				if (showposition && (npc != null))
				{
					activeChar.sendMessage(index + " - " + spawn.getTemplate().getName() + " (" + spawn + "): " + npc.getX() + " " + npc.getY() + " " + npc.getZ());
				}
				else
				{
					activeChar.sendMessage(index + " - " + spawn.getTemplate().getName() + " (" + spawn + "): " + spawn.getLocx() + " " + spawn.getLocy() + " " + spawn.getLocz());
				}
			}
		}
		
		if (index == 0)
		{
			activeChar.sendMessage(getClass().getSimpleName() + ": No current spawns found.");
		}
	}
	
	/**
	 * Remove a spawn from the spawn set, if the spawn set is empty, remove it as well.
	 * @param spawn the NPC spawn to remove
	 * @return {@code true} if the spawn was successfully removed, {@code false} otherwise
	 */
	private boolean removeSpawn(L2Spawn spawn)
	{
		if (_spawnTable.containsKey(spawn.getNpcId()))
		{
			final Set<L2Spawn> set = _spawnTable.get(spawn.getNpcId());
			boolean removed = set.remove(spawn);
			if (set.isEmpty())
			{
				_spawnTable.remove(spawn.getNpcId());
			}
			return removed;
		}
		return false;
	}
	
	/**
	 * Get the spawns for the NPC Id.
	 * @param npcId the NPC Id
	 * @return the spawn set for the given npcId
	 */
	public Set<L2Spawn> getSpawns(int npcId)
	{
		return _spawnTable.containsKey(npcId) ? _spawnTable.get(npcId) : Collections.<L2Spawn> emptySet();
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final SpawnTable _instance = new SpawnTable();
	}
	
	/**
	 * Get the first NPC spawn.
	 * @param npcId the NPC Id to search
	 * @return the first not null spawn, if any
	 */
	public L2Spawn getFirstSpawn(int npcId)
	{
		if (_spawnTable.containsKey(npcId))
		{
			for (L2Spawn spawn : _spawnTable.get(npcId))
			{
				if (spawn != null)
				{
					return spawn;
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds a spawn for the given NPC ID.
	 * @param npcId the NPC Id
	 * @return a spawn for the given NPC ID or {@code null}
	 */
	public L2Spawn findAny(int npcId)
	{
		return getSpawns(npcId).stream().findFirst().orElse(null);
	}
}
