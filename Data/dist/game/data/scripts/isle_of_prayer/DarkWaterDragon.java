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
package isle_of_prayer;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.datatables.NpcTable;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import ai.L2AttackableAIScript;
import javolution.util.FastMap;
import javolution.util.FastSet;

public class DarkWaterDragon extends L2AttackableAIScript
{
	private static final int DRAGON = 22267;
	private static final int SHADE1 = 22268;
	private static final int SHADE2 = 22269;
	private static final int FAFURION = 18482;
	private static final int DETRACTOR1 = 22270;
	private static final int DETRACTOR2 = 22271;
	private static FastSet<Integer> secondSpawn = new FastSet<Integer>(); // Used to track if second Shades were already spawned
	private static FastSet<Integer> myTrackingSet = new FastSet<Integer>(); // Used to track instances of npcs
	private static FastMap<Integer, L2PcInstance> _idmap = new FastMap<Integer, L2PcInstance>().shared(); // Used to track instances of npcs
	
	public DarkWaterDragon()
	{
		super(-1, DarkWaterDragon.class.getSimpleName(), "isle_of_prayer");
		int[] mobs =
		{
			DRAGON,
			SHADE1,
			SHADE2,
			FAFURION,
			DETRACTOR1,
			DETRACTOR2
		};
		registerMobs(mobs);
		myTrackingSet.clear();
		secondSpawn.clear();
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (npc != null)
		{
			switch (event)
			{
				case "first_spawn":
					startQuestTimer("1", 40000, npc, null, true);
					break;
				case "second_spawn":
					startQuestTimer("2", 40000, npc, null, true);
					break;
				case "third_spawn":
					startQuestTimer("3", 40000, npc, null, true);
					break;
				case "fourth_spawn":
					startQuestTimer("4", 40000, npc, null, true);
					break;
				case "1":
					addSpawn(DETRACTOR1, (npc.getX() + 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
					break;
				case "2":
					addSpawn(DETRACTOR2, (npc.getX() + 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
					break;
				case "3":
					addSpawn(DETRACTOR1, (npc.getX() - 100), (npc.getY() + 100), npc.getZ(), 0, false, 40000);
					break;
				case "4":
					addSpawn(DETRACTOR2, (npc.getX() - 100), (npc.getY() - 100), npc.getZ(), 0, false, 40000);
					break;
				case "fafurion_despawn":
					cancelQuestTimer("fafurion_poison", npc, null);
					cancelQuestTimer("1", npc, null);
					cancelQuestTimer("2", npc, null);
					cancelQuestTimer("3", npc, null);
					cancelQuestTimer("4", npc, null);
					
					myTrackingSet.remove(npc.getObjectId());
					player = _idmap.remove(npc.getObjectId());
					if (player != null)
					{
						((L2Attackable) npc).doItemDrop(NpcTable.getInstance().getTemplate(18485), player);
					}
					
					npc.deleteMe();
					break;
				case "fafurion_poison":
					if (npc.getCurrentHp() <= 500)
					{
						cancelQuestTimer("fafurion_despawn", npc, null);
						cancelQuestTimer("first_spawn", npc, null);
						cancelQuestTimer("second_spawn", npc, null);
						cancelQuestTimer("third_spawn", npc, null);
						cancelQuestTimer("fourth_spawn", npc, null);
						cancelQuestTimer("1", npc, null);
						cancelQuestTimer("2", npc, null);
						cancelQuestTimer("3", npc, null);
						cancelQuestTimer("4", npc, null);
						myTrackingSet.remove(npc.getObjectId());
						_idmap.remove(npc.getObjectId());
					}
					npc.reduceCurrentHp(500, npc, null); // poison kills Fafurion if he is not healed
					break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isPet)
	{
		int npcId = npc.getNpcId();
		int npcObjId = npc.getObjectId();
		if (npcId == DRAGON)
		{
			if (!myTrackingSet.contains(npcObjId)) // this allows to handle multiple instances of npc
			{
				myTrackingSet.add(npcObjId);
				// Spawn first 5 shades on first attack on Dark Water Dragon
				L2Character originalAttacker = isPet ? attacker.getPet() : attacker;
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			}
			else if ((npc.getCurrentHp() < (npc.getMaxHp() / 2.0)) && !(secondSpawn.contains(npcObjId)))
			{
				secondSpawn.add(npcObjId);
				// Spawn second 5 shades on half hp of on Dark Water Dragon
				L2Character originalAttacker = isPet ? attacker.getPet() : attacker;
				spawnShade(originalAttacker, SHADE2, npc.getX() + 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() + 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 100, npc.getY() + 100, npc.getZ());
				spawnShade(originalAttacker, SHADE1, npc.getX() - 100, npc.getY() - 100, npc.getZ());
				spawnShade(originalAttacker, SHADE2, npc.getX() - 150, npc.getY() + 150, npc.getZ());
			}
		}
		return super.onAttack(npc, attacker, damage, isPet);
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isPet)
	{
		int npcId = npc.getNpcId();
		int npcObjId = npc.getObjectId();
		if (npcId == DRAGON)
		{
			myTrackingSet.remove(npcObjId);
			secondSpawn.remove(npcObjId);
			L2Attackable faf = (L2Attackable) addSpawn(FAFURION, npc.getX(), npc.getY(), npc.getZ(), 0, false, 0); // spawns Fafurion Kindred when Dard Water Dragon is dead
			_idmap.put(faf.getObjectId(), killer);
		}
		else if (npcId == FAFURION)
		{
			cancelQuestTimer("fafurion_poison", npc, null);
			cancelQuestTimer("fafurion_despawn", npc, null);
			cancelQuestTimer("first_spawn", npc, null);
			cancelQuestTimer("second_spawn", npc, null);
			cancelQuestTimer("third_spawn", npc, null);
			cancelQuestTimer("fourth_spawn", npc, null);
			cancelQuestTimer("1", npc, null);
			cancelQuestTimer("2", npc, null);
			cancelQuestTimer("3", npc, null);
			cancelQuestTimer("4", npc, null);
			myTrackingSet.remove(npcObjId);
			_idmap.remove(npcObjId);
		}
		return super.onKill(npc, killer, isPet);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		int npcId = npc.getNpcId();
		int npcObjId = npc.getObjectId();
		if (npcId == FAFURION)
		{
			if (!myTrackingSet.contains(npcObjId))
			{
				myTrackingSet.add(npcObjId);
				// Spawn 4 Detractors on spawn of Fafurion
				int x = npc.getX();
				int y = npc.getY();
				addSpawn(DETRACTOR2, x + 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x + 100, y - 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR2, x - 100, y + 100, npc.getZ(), 0, false, 40000);
				addSpawn(DETRACTOR1, x - 100, y - 100, npc.getZ(), 0, false, 40000);
				startQuestTimer("first_spawn", 2000, npc, null); // timer to delay timer "1"
				startQuestTimer("second_spawn", 4000, npc, null); // timer to delay timer "2"
				startQuestTimer("third_spawn", 8000, npc, null); // timer to delay timer "3"
				startQuestTimer("fourth_spawn", 10000, npc, null); // timer to delay timer "4"
				startQuestTimer("fafurion_poison", 3000, npc, null, true); // Every three seconds reduces Fafurions hp like it is poisoned
				startQuestTimer("fafurion_despawn", 120000, npc, null); // Fafurion Kindred disappears after two minutes
			}
		}
		return super.onSpawn(npc);
	}
	
	public void spawnShade(L2Character attacker, int npcId, int x, int y, int z)
	{
		final L2Npc shade = addSpawn(npcId, x, y, z, 0, false, 0);
		shade.setRunning();
		((L2Attackable) shade).addDamageHate(attacker, 0, 999);
		shade.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
	}
}