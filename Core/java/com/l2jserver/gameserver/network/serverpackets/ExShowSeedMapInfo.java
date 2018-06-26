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
package com.l2jserver.gameserver.network.serverpackets;

import com.l2jserver.gameserver.instancemanager.gracia.SoDManager;
import com.l2jserver.gameserver.instancemanager.gracia.SoIManager;
import com.l2jserver.gameserver.model.Location;

public class ExShowSeedMapInfo extends L2GameServerPacket
{
	private static final String _S__FE_A1_EXSHOWSEEDMAPINFO = "[S] FE:A1 ExShowSeedMapInfo";
	
	private static final Location[] ENTRANCES =
	{
		new Location(-246857, 251960, 4331, 1),
		new Location(-213770, 210760, 4400, 2),
	};
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE); // Id
		writeH(0xa1); // SubId
		writeD(ENTRANCES.length);
		for (Location loc : ENTRANCES)
		{
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
			switch (loc.getHeading())
			{
				case 1: // Seed of Destruction
					writeD(2770 + SoDManager.getInstance().getSoDState());
					break;
				case 2: // Seed of Immortality
					writeD(SoIManager.getCurrentStage() + 2765);
					break;
			}
		}
	}
	
	@Override
	public String getType()
	{
		return _S__FE_A1_EXSHOWSEEDMAPINFO;
	}
}
