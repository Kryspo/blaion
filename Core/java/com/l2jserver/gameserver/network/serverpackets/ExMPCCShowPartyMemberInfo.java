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

import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Format: ch d[Sdd]
 * @author chris_00
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket
{
	private static final String _S__FE_4A_EXMPCCSHOWPARTYMEMBERINFO = "[S] FE:4b ExMPCCShowPartyMemberInfo";
	private final L2Party _party;
	
	public ExMPCCShowPartyMemberInfo(L2Party party)
	{
		_party = party;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x4b);
		writeD(_party.getMemberCount()); // Number of Members
		for (L2PcInstance pc : _party.getPartyMembers())
		{
			writeS(pc.getName()); // Membername
			writeD(pc.getObjectId()); // ObjId
			writeD(pc.getClassId().getId()); // Classid
		}
	}
	
	@Override
	public String getType()
	{
		return _S__FE_4A_EXMPCCSHOWPARTYMEMBERINFO;
	}
	
}
