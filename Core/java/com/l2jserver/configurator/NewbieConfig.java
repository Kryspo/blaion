package com.l2jserver.configurator;

import static com.l2jserver.Config.DISABLE_TUTORIAL;
import static com.l2jserver.Config.NEWBIE_CHAR_TITLE;
import static com.l2jserver.Config.SPAWN_CHAR;
import static com.l2jserver.Config.SPAWN_X;
import static com.l2jserver.Config.SPAWN_Y;
import static com.l2jserver.Config.SPAWN_Z;
import static com.l2jserver.Config.STARTING_ADENA;
import static com.l2jserver.Config.STARTING_BUFF_MAGIC_FIGHT;
import static com.l2jserver.Config.STARTING_BUFF_MAGIC_MAGE;
import static com.l2jserver.Config.STARTING_COMMON_ITEMS;
import static com.l2jserver.Config.STARTING_COMMON_SKILLS;
import static com.l2jserver.Config.STARTING_LEVEL;
import static com.l2jserver.Config.STARTING_SP;

/**
 * @author L2jPDT
 */
public class NewbieConfig extends AbstractConfig
{
	public final String file = "./config/Playable/Newbies.properties";
	
	@Override
	protected String getFilePath()
	{
		return file;
	}
	
	@Override
	public void load()
	{
		NEWBIE_CHAR_TITLE = getParser().getString("NewbieCharTitle", "Im newbie");
		STARTING_COMMON_SKILLS = getParser().getHashMap("CommonSkills", "2013,10", ",", ";");
		STARTING_COMMON_ITEMS = getParser().getHashMap("CommonItems", "57,1000", ",", ";");
		STARTING_BUFF_MAGIC_FIGHT = getParser().getHashMap("StartingBuffMagicFight", "1499,1;1500,1;1501,1;1502,1;1503,1;1504,1;1356,1", ",", ";");
		STARTING_BUFF_MAGIC_MAGE = getParser().getHashMap("StartingBuffMagicMage", "1499,1;1500,1;1501,1;1502,1;1503,1;1504,1;1355,1", ",", ";");
		STARTING_ADENA = getParser().getLong("StartingAdena", 0);
		STARTING_LEVEL = getParser().getByte("StartingLevel", (byte) 1);
		STARTING_SP = getParser().getInt("StartingSP", 0);
		DISABLE_TUTORIAL = getParser().getBoolean("DisableTutorial", false);
		
		SPAWN_CHAR = getParser().getBoolean("CustomSpawn", false);
		SPAWN_X = getParser().getInt("SpawnX", 0);
		SPAWN_Y = getParser().getInt("SpawnY", 0);
		SPAWN_Z = getParser().getInt("SpawnZ", 0);
	}
}
