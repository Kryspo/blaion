package com.l2jserver.gameserver.network.serverpackets;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class ExPCCafePointInfo extends L2GameServerPacket
{
	private static final String _S__FE_31_EXPCCAFEPOINTINFO = "[S] FE:32 ExPCCafePointInfo";
	private final int _points;
	private final int _mAddPoint;
	private int _mPeriodType;
	private final int _remainTime;
	private int _pointType = 0;
	
	public ExPCCafePointInfo()
	{
		_points = 0;
		_mAddPoint = 0;
		_remainTime = 0;
		_mPeriodType = 0;
		_pointType = 0;
	}
	
	public ExPCCafePointInfo(final int points, final int modify_points, final boolean mod, final boolean _double, final int hours_left)
	{
		_points = points;
		_mAddPoint = modify_points;
		_remainTime = hours_left;
		if (mod && _double)
		{
			_mPeriodType = 1;
			_pointType = 0;
		}
		else if (mod)
		{
			_mPeriodType = 1;
			_pointType = 1;
		}
		else
		{
			_mPeriodType = 2;
			_pointType = 2;
		}
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x32);
		writeD(_points); // num points
		writeD(_mAddPoint); // points inc display
		writeC(_mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
		writeD(_remainTime); // period hours left
		writeC(_pointType); // points inc display color(0=yellow,1=cyan-blue,2=red,all other black)
	}
	
	@Override
	public String getType()
	{
		return _S__FE_31_EXPCCAFEPOINTINFO;
	}
}
