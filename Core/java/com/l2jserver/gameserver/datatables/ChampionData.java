package com.l2jserver.gameserver.datatables;

import com.l2jserver.Config;
import com.l2jserver.gameserver.model.actor.L2Attackable;

public class ChampionData
{
	// private static final Logger _log = Logger.getLogger(ChampionData.class.getName());
	
	public boolean isPassive(L2Attackable champion)
	{
		// TODO: Implement it for both champions.
		return Config.CHAMPION_PASSIVE;
	}
	
	public String getTitle(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_TITLE_HARD : Config.CHAMPION_TITLE_EASY;
	}
	
	public int getChance(boolean isHard)
	{
		return isHard ? Config.CHAMPION_FREQUENCY_HARD : Config.CHAMPION_FREQUENCY_EASY;
	}
	
	public int getMinLv(boolean isHard)
	{
		return isHard ? Config.CHAMPION_MIN_LVL_HARD : Config.CHAMPION_MIN_LVL_EASY;
	}
	
	public int getMaxLv(boolean isHard)
	{
		return isHard ? Config.CHAMPION_MAX_LVL_HARD : Config.CHAMPION_MAX_LVL_EASY;
	}
	
	public int getHpMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_HP_HARD : Config.CHAMPION_HP_EASY;
	}
	
	public int getRewardMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_REWARDS_HARD : Config.CHAMPION_REWARDS_EASY;
	}
	
	public float getAdenaMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_ADENAS_REWARD_HARD : Config.CHAMPION_ADENAS_REWARD_EASY;
	}
	
	public float getHpRegMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_HP_REGEN_HARD : Config.CHAMPION_HP_REGEN_EASY;
	}
	
	public float getAttackMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_ATK_HARD : Config.CHAMPION_ATK_EASY;
	}
	
	public float getAttacSpdkMultipler(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_SPD_ATK_HARD : Config.CHAMPION_SPD_ATK_EASY;
	}
	
	public int getLowerLvChance(L2Attackable champion)
	{
		// TODO: Implement it for both champions.
		return Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
	}
	
	public int getHigherLvChance(L2Attackable champion)
	{
		// TODO: Implement it for both champions.
		return Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
	}
	
	public int getRewardId(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_REWARD_ID_HARD : Config.CHAMPION_REWARD_ID_EASY;
	}
	
	public int getRewardCount(L2Attackable champion)
	{
		return champion.isHardChampion() ? Config.CHAMPION_REWARD_QTY_HARD : Config.CHAMPION_REWARD_QTY_EASY;
	}
	
	public boolean isEnabledVitality(L2Attackable champion)
	{
		// TODO: Implement it for both champions.
		return Config.CHAMPION_ENABLE_VITALITY;
	}
	
	public boolean inInstanceEnabled()
	{
		return Config.CHAMPION_ENABLE_IN_INSTANCES;
	}
	
	/**
	 * Gets the single instance of ChampionData.
	 * @return single instance of ChampionData
	 */
	
	public static ChampionData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ChampionData _instance = new ChampionData();
	}
}