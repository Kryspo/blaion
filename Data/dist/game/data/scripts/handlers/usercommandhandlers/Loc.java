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
package handlers.usercommandhandlers;

import com.l2jserver.gameserver.datatables.MapRegionTable;
import com.l2jserver.gameserver.handler.IUserCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	/**
	 * @see com.l2jserver.gameserver.handler.IUserCommandHandler#useUserCommand(int, com.l2jserver.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public boolean useUserCommand(int id, L2PcInstance activeChar)
	{
		int _nearestTown = MapRegionTable.getInstance().getClosestTownNumber(activeChar);
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
		activeChar.sendMessage("Your location is X: " + activeChar.getX() + "  Y: " + activeChar.getY() + "  Z: " + activeChar.getZ() + ". Nearest area is: " + msg);
		return true;
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}