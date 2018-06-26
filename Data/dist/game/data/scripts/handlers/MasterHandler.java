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
package handlers;

import java.util.logging.Logger;

import com.l2jserver.gameserver.ThreadPoolManager;

/**
 * @author L2jPrivateDevelopersTeam
 */
public class MasterHandler
{
	private static Logger _log = Logger.getLogger(MasterHandler.class.getName());
	
	public static void main(String[] args)
	{
		_log.config("Loading handlers in 5 seconds.");
		ThreadPoolManager.getInstance().scheduleGeneral(new StartLoadingAllHandlers(), 5000);
	}
}