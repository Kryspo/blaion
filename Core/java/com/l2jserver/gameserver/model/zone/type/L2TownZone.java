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
package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.Config;
import com.l2jserver.gameserver.datatables.MapRegionTable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.L2SpawnZone;

/**
 * A Town zone
 * @author L2jPrivateDevelopersTeam
 */
public class L2TownZone extends L2SpawnZone
{
	private int _townId;
	private int _taxById;
	private boolean _isPeaceZone;
	private boolean _isTWZone = false;
	
	public L2TownZone(int id)
	{
		super(id);
		
		_taxById = 0;
		
		// Default not peace zone
		_isPeaceZone = false;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("townId"))
		{
			_townId = Integer.parseInt(value);
		}
		else if (name.equals("taxById"))
		{
			_taxById = Integer.parseInt(value);
		}
		else if (name.equals("isPeaceZone"))
		{
			_isPeaceZone = Boolean.parseBoolean(value);
		}
		else
		{
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			// PVP possible during siege, now for siege participants only
			// Could also check if this town is in siege, or if any siege is going on
			if ((((L2PcInstance) character).getSiegeState() != 0) && (Config.ZONE_TOWN == 1))
			{
				return;
			}
			
			int _nearestTown = MapRegionTable.getInstance().getClosestTownNumber(character);
			String msg;
			switch (_nearestTown)
			{
				case 0:
					msg = "Talking Island";
					break;
				case 1:
					msg = "Elven Village";
					break;
				case 2:
					msg = "Dark Eleven Village";
					break;
				case 3:
					msg = "Orc Village";
					break;
				case 4:
					msg = "Dwarven Village";
					break;
				case 5:
					msg = "Town of Gludio";
					break;
				case 6:
					msg = "Gludin Village";
					break;
				case 7:
					msg = "Town of Dion";
					break;
				case 8:
					msg = "Town of Giran";
					break;
				case 9:
					msg = "Town of Oren";
					break;
				case 10:
					msg = "Town of Aden";
					break;
				case 11:
					msg = "Hunters Village";
					break;
				case 12:
					msg = "Giran Harbor";
					break;
				case 13:
					msg = "Town of Heine";
					break;
				case 14:
					msg = "Town of Rune";
					break;
				case 15:
					msg = "Town of Goddard";
					break;
				case 16:
					msg = "Town of Schuttgart";
					break;
				case 17:
					msg = "Floran Village";
					break;
				case 18:
					msg = "Primeval Isle";
					break;
				case 19:
					msg = "Kamael Village";
					break;
				case 20:
					msg = "Near south of Wastelands Camp";
					break;
				case 21:
					msg = "Near Fantasy Isle";
					break;
				case 22:
					msg = "Near the Neutral Zone";
					break;
				case 23:
					msg = "Near the Coliseum";
					break;
				case 24:
					msg = "GM Consultation Service";
					break;
				case 25:
					msg = "Dimensional Gap";
					break;
				case 26:
					msg = "Cemetery of the Empire";
					break;
				case 27:
					msg = "Inside the Steel Citadel";
					break;
				case 28:
					msg = "Steel Citadel Resistance";
					break;
				case 29:
					msg = "Inside Kamaloka";
					break;
				case 30:
					msg = "Inside Nia Kamaloka";
					break;
				case 31:
					msg = "Rim Kamaloka";
					break;
				case 32:
					msg = "Kecereus Alliance base";
					break;
				case 33:
					msg = "Inside Seed of Infinity";
					break;
				case 34:
					msg = "Outside Seed of Infinity";
					break;
				case 35:
					msg = "Areal Cleft";
					break;
				default:
					msg = "Town of Aden";
			}
			character.sendMessage("You entered: " + msg + " area.");
		}
		
		if (_isTWZone)
		{
			character.setInTownWarEvent(true);
			character.sendMessage("You entered a Town War event zone.");
		}
		
		if (_isPeaceZone && (Config.ZONE_TOWN != 2))
		{
			character.setInsideZone(L2Character.ZONE_PEACE, true);
		}
		
		character.setInsideZone(L2Character.ZONE_TOWN, true);
		
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (_isPeaceZone)
		{
			character.setInsideZone(L2Character.ZONE_PEACE, false);
		}
		
		if (_isTWZone)
		{
			character.setInTownWarEvent(false);
			character.sendMessage("You left a Town War event zone.");
		}
		
		character.setInsideZone(L2Character.ZONE_TOWN, false);
		
		int _nearestTown = MapRegionTable.getInstance().getClosestTownNumber(character);
		String msg;
		switch (_nearestTown)
		{
			case 0:
				msg = "Talking Island";
				break;
			case 1:
				msg = "Elven Village";
				break;
			case 2:
				msg = "Dark Eleven Village";
				break;
			case 3:
				msg = "Orc Village";
				break;
			case 4:
				msg = "Dwarven Village";
				break;
			case 5:
				msg = "Town of Gludio";
				break;
			case 6:
				msg = "Gludin Village";
				break;
			case 7:
				msg = "Town of Dion";
				break;
			case 8:
				msg = "Town of Giran";
				break;
			case 9:
				msg = "Town of Oren";
				break;
			case 10:
				msg = "Town of Aden";
				break;
			case 11:
				msg = "Hunters Village";
				break;
			case 12:
				msg = "Giran Harbor";
				break;
			case 13:
				msg = "Town of Heine";
				break;
			case 14:
				msg = "Town of Rune";
				break;
			case 15:
				msg = "Town of Goddard";
				break;
			case 16:
				msg = "Town of Schuttgart";
				break;
			case 17:
				msg = "Floran Village";
				break;
			case 18:
				msg = "Primeval Isle";
				break;
			case 19:
				msg = "Kamael Village";
				break;
			case 20:
				msg = "Near south of Wastelands Camp";
				break;
			case 21:
				msg = "Near Fantasy Isle";
				break;
			case 22:
				msg = "Near the Neutral Zone";
				break;
			case 23:
				msg = "Near the Coliseum";
				break;
			case 24:
				msg = "GM Consultation Service";
				break;
			case 25:
				msg = "Dimensional Gap";
				break;
			case 26:
				msg = "Cemetery of the Empire";
				break;
			case 27:
				msg = "Inside the Steel Citadel";
				break;
			case 28:
				msg = "Steel Citadel Resistance";
				break;
			case 29:
				msg = "Inside Kamaloka";
				break;
			case 30:
				msg = "Inside Nia Kamaloka";
				break;
			case 31:
				msg = "Rim Kamaloka";
				break;
			case 32:
				msg = "Kecereus Alliance base";
				break;
			case 33:
				msg = "Inside Seed of Infinity";
				break;
			case 34:
				msg = "Outside Seed of Infinity";
				break;
			case 35:
				msg = "Areal Cleft";
				break;
			default:
				msg = "Town of Aden";
		}
		character.sendMessage("You left: " + msg + " area.");
	}
	
	public void onUpdate(L2Character character)
	{
		if (_isTWZone)
		{
			character.setInTownWarEvent(true);
			character.sendMessage("You entered a Town War event zone.");
		}
		else
		{
			character.setInTownWarEvent(false);
			character.sendMessage("You left a Town War event zone.");
		}
	}
	
	public void updateForCharactersInside()
	{
		for (L2Character character : _characterList.values())
		{
			if (character != null)
			{
				onEnter(character);
			}
			onUpdate(character);
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	/**
	 * Returns this zones town id (if any)
	 * @return
	 */
	public int getTownId()
	{
		return _townId;
	}
	
	/**
	 * Returns this town zones castle id
	 * @return
	 */
	public final int getTaxById()
	{
		return _taxById;
	}
	
	public final boolean isPeaceZone()
	{
		return _isPeaceZone;
	}
	
	public final void setIsTWZone(boolean value)
	{
		_isTWZone = value;
	}
}
