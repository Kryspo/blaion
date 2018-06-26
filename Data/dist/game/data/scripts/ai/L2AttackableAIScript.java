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
package ai;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;

import java.util.List;

import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Skill;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2RiftInvaderInstance;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.templates.chars.L2NpcTemplate;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.gameserver.util.Util;

/**
 * Overarching Superclass for all mob AI
 * @author Fulminus
 */
public class L2AttackableAIScript extends Quest
{
	
	/**
	 * This is used to register all monsters contained in mobs for a particular script<BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method register ID for all QuestEventTypes<BR>
	 * Do not use for group_template AIs</B></FONT><BR>
	 * @param mobs
	 * @see #registerMobs(int[], QuestEventType...)
	 */
	public void registerMobs(int[] mobs)
	{
		for (int id : mobs)
		{
			NpcEventId(id, QuestEventType.ON_ATTACK);
			NpcEventId(id, QuestEventType.ON_KILL);
			NpcEventId(id, QuestEventType.ON_SPAWN);
			NpcEventId(id, QuestEventType.ON_SPELL_FINISHED);
			NpcEventId(id, QuestEventType.ON_SKILL_SEE);
			NpcEventId(id, QuestEventType.ON_FACTION_CALL);
			NpcEventId(id, QuestEventType.ON_AGGRO_RANGE_ENTER);
		}
	}
	
	public void registerMonsters(int... mobs)
	{
		AttackNpcs(mobs);
		KillNpcs(mobs);
		SpawnNpcs(mobs);
		SpellFinishedNpcs(mobs);
		SkillSeeNpcs(mobs);
		AggroRangeEnterNpcs(mobs);
		FactionCallNpcs(mobs);
	}
	
	public void registerMobs(int mobs)
	{
		NpcEventId(mobs, QuestEventType.ON_ATTACK);
		NpcEventId(mobs, QuestEventType.ON_KILL);
		NpcEventId(mobs, QuestEventType.ON_SPAWN);
		NpcEventId(mobs, QuestEventType.ON_SPELL_FINISHED);
		NpcEventId(mobs, QuestEventType.ON_SKILL_SEE);
		NpcEventId(mobs, QuestEventType.ON_FACTION_CALL);
		NpcEventId(mobs, QuestEventType.ON_AGGRO_RANGE_ENTER);
	}
	
	/**
	 * This is used to register all monsters contained in mobs for a particular script event types defined in types.
	 * @param mobs
	 * @param types
	 */
	public void registerMobs(int[] mobs, QuestEventType... types)
	{
		for (int id : mobs)
		{
			for (QuestEventType type : types)
			{
				NpcEventId(id, type);
			}
		}
	}
	
	public L2AttackableAIScript(int questId, String name, String descr)
	{
		super(questId, name, descr);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		return null;
	}
	
	@Override
	public String onSpellFinished(L2Npc npc, L2PcInstance player, L2Skill skill)
	{
		return null;
	}
	
	@Override
	public String onSkillSee(L2Npc npc, L2PcInstance caster, L2Skill skill, L2Object[] targets, boolean isPet)
	{
		if (caster == null)
		{
			return null;
		}
		if (!(npc instanceof L2Attackable))
		{
			return null;
		}
		
		L2Attackable attackable = (L2Attackable) npc;
		
		int skillAggroPoints = skill.getAggroPoints();
		
		if (caster.getPet() != null)
		{
			if ((targets.length == 1) && Util.contains(targets, caster.getPet()))
			{
				skillAggroPoints = 0;
			}
		}
		
		if (skillAggroPoints > 0)
		{
			if (attackable.hasAI() && (attackable.getAI().getIntention() == AI_INTENTION_ATTACK))
			{
				L2Object npcTarget = attackable.getTarget();
				for (L2Object skillTarget : targets)
				{
					if ((npcTarget == skillTarget) || (npc == skillTarget))
					{
						L2Character originalCaster = isPet ? caster.getPet() : caster;
						attackable.addDamageHate(originalCaster, 0, (skillAggroPoints * 150) / (attackable.getLevel() + 7));
					}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public String onFactionCall(L2Npc npc, L2Npc caller, L2PcInstance attacker, boolean isPet)
	{
		if (attacker == null)
		{
			return null;
		}
		
		L2Character originalAttackTarget = (isPet ? attacker.getPet() : attacker);
		if (attacker.isInParty() && attacker.getParty().isInDimensionalRift())
		{
			byte riftType = attacker.getParty().getDimensionalRift().getType();
			byte riftRoom = attacker.getParty().getDimensionalRift().getCurrentRoom();
			
			if ((caller instanceof L2RiftInvaderInstance) && !DimensionalRiftManager.getInstance().getRoom(riftType, riftRoom).checkIfInZone(npc.getX(), npc.getY(), npc.getZ()))
			{
				return null;
			}
		}
		
		// By default, when a faction member calls for help, attack the caller's attacker.
		// Notify the AI with EVT_AGGRESSION
		npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, originalAttackTarget, 1);
		
		return null;
	}
	
	@Override
	public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isPet)
	{
		if (player == null)
		{
			return null;
		}
		
		L2Character target = isPet ? player.getPet() : player;
		
		((L2Attackable) npc).addDamageHate(target, 0, 1);
		
		// Set the intention to the L2Attackable to AI_INTENTION_ACTIVE
		if (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)
		{
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
		}
		return null;
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		return null;
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		if ((attacker != null) && (npc instanceof L2Attackable))
		{
			L2Attackable attackable = (L2Attackable) npc;
			
			L2Character originalAttacker = isPet ? attacker.getPet() : attacker;
			attackable.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, originalAttacker);
			attackable.addDamageHate(originalAttacker, damage, (damage * 100) / (attackable.getLevel() + 7));
		}
		return null;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		if (npc instanceof L2MonsterInstance)
		{
			final L2MonsterInstance mob = (L2MonsterInstance) npc;
			if (mob.getLeader() != null)
			{
				mob.getLeader().getMinionList().onMinionDie(mob, -1);
			}
			
			if (mob.hasMinions())
			{
				mob.getMinionList().onMasterDie(false);
			}
		}
		return null;
	}
	
	public static void main(String[] args)
	{
		L2AttackableAIScript ai = new L2AttackableAIScript(-1, "L2AttackableAIScript", "L2AttackableAIScript");
		// register all mobs here...
		for (int level = 1; level < 100; level++)
		{
			final List<L2NpcTemplate> templates = NpcTable.getInstance().getAllOfLevel(level);
			for (L2NpcTemplate t : templates)
			{
				try
				{
					if (L2Attackable.class.isAssignableFrom(Class.forName("com.l2jserver.gameserver.model.actor.instance." + t.type + "Instance")))
					{
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_ATTACK);
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_KILL);
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_SPAWN);
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_SKILL_SEE);
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_FACTION_CALL);
						ai.NpcEventId(t.npcId, Quest.QuestEventType.ON_AGGRO_RANGE_ENTER);
					}
				}
				catch (ClassNotFoundException ex)
				{
					_log.info("Class not found " + t.type + "Instance");
				}
			}
		}
	}
	
	/**
	 * Monster is running and attacking the playable.
	 * @param npc
	 * @param playable
	 */
	protected void attackPlayer(L2Attackable npc, L2Playable playable)
	{
		npc.setIsRunning(true);
		npc.addDamageHate(playable, 0, 999);
		npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, playable);
	}
	
	protected void broadcastNpcSay(L2Npc npc, int type, String stringId, int radius)
	{
		Broadcast.toKnownPlayersInRadius(npc, new CreatureSay(npc.getObjectId(), type, npc.getName(), stringId), radius);
	}
	
	protected void broadcastNpcSay(L2Npc npc, int type, String stringId)
	{
		Broadcast.toKnownPlayers(npc, new CreatureSay(npc.getObjectId(), type, npc.getName(), stringId));
	}
	
	protected void broadcastNpcSay(L2Npc npc, int type, String stringId, String... parameters)
	{
		final NpcSay say = new NpcSay(npc.getObjectId(), type, npc.getTemplate().getIdTemplate(), stringId);
		if (parameters != null)
		{
			for (String parameter : parameters)
			{
				say.addStringParameter(parameter);
			}
		}
		Broadcast.toKnownPlayers(npc, say);
	}
	
	public static long getQuestItemsCount(L2PcInstance player, int itemId)
	{
		return player.getInventory().getInventoryItemCount(itemId, -1);
	}
	
	/**
	 * Check for multiple items in player's inventory.
	 * @param player the player whose inventory to check for quest items
	 * @param itemIds a list of item IDs to check for
	 * @return {@code true} if at least one items exist in player's inventory, {@code false} otherwise
	 */
	@Override
	public boolean hasAtLeastOneQuestItem(L2PcInstance player, int... itemIds)
	{
		final PcInventory inv = player.getInventory();
		for (int itemId : itemIds)
		{
			if (inv.getItemByItemId(itemId) != null)
			{
				return true;
			}
		}
		return false;
	}
}