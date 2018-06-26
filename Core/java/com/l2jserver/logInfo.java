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
package com.l2jserver;

import java.util.logging.Logger;

public class logInfo
{
	private static final Logger _log = Logger.getLogger(logInfo.class.getName());
	
	public static void info()
	{
		_log.info("-------------------------------------------------------------------------------");
		_log.info("                        Private Development Team                               ");
		_log.info("                             Client: Freya                                     ");
		_log.info("                               2016-2018                                       ");
		_log.info("                 http://l2jprivate-developers-team.eu                          ");
		_log.info("-------------------------------------------------------------------------------");
	}
}
