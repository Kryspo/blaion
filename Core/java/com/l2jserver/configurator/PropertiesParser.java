/*
 * Copyright (C) 2004-2014 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.configurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simplifies loading of property files and adds logging if a non existing property is requested.
 * @author Nos
 */
public final class PropertiesParser
{
	private static final Logger _log = Logger.getLogger(PropertiesParser.class.getName());
	
	private final Properties _properties = new Properties();
	private final File _file;
	
	public PropertiesParser(String name)
	{
		this(new File(name));
	}
	
	public PropertiesParser(File file)
	{
		_file = file;
		try (FileInputStream fileInputStream = new FileInputStream(file))
		{
			try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.defaultCharset()))
			{
				_properties.load(inputStreamReader);
			}
		}
		catch (Exception e)
		{
			_log.warning("[" + _file.getName() + "] There was an error loading config reason: " + e.getMessage());
		}
	}
	
	public boolean containskey(String key)
	{
		return _properties.containsKey(key);
	}
	
	private String getValue(String key)
	{
		String value = _properties.getProperty(key);
		return value != null ? value.trim() : null;
	}
	
	public boolean getBoolean(String key, boolean defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		if (value.equalsIgnoreCase("true"))
		{
			return true;
		}
		else if (value.equalsIgnoreCase("false"))
		{
			return false;
		}
		else
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"boolean\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public byte getByte(String key, byte defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Byte.parseByte(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"byte\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public short getShort(String key, short defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Short.parseShort(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"short\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public int getInt(String key, int defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"int\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public long getLong(String key, long defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Long.parseLong(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"long\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public float getFloat(String key, float defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Float.parseFloat(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"float\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public double getDouble(String key, double defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"double\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public String getString(String key, String defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		return value;
	}
	
	public <T extends Enum<T>> T getEnum(String key, Class<T> clazz, T defaultValue)
	{
		String value = getValue(key);
		if (value == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			return defaultValue;
		}
		
		try
		{
			return Enum.valueOf(clazz, value);
		}
		catch (IllegalArgumentException e)
		{
			_log.warning("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be enum value of \"" + clazz.getSimpleName() + "\" using default value: " + defaultValue);
			return defaultValue;
		}
	}
	
	public int[] getIntArray(String key, String defaultValue, String split)
	{
		int[] value;
		String line = getValue(key);
		if (line == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			line = defaultValue;
		}
		String[] ary = line.split(split);
		value = new int[ary.length];
		for (int i = 0; i < ary.length; i++)
		{
			value[i] = Integer.parseInt(ary[i]);
		}
		
		return value;
	}
	
	public double[] getDoubleArray(String key, String defaultValue, String split)
	{
		double[] value;
		String line = getValue(key);
		if (line == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			line = defaultValue;
		}
		String[] ary = line.split(split);
		value = new double[ary.length];
		for (int i = 0; i < ary.length; i++)
		{
			value[i] = Double.parseDouble(ary[i]);
		}
		return value;
	}
	
	public Map<Integer, Integer> getHashMap(String key, String defaultValue, String kvsplit, String entrysplit)
	{
		Map<Integer, Integer> map = new HashMap<>();
		String line = getValue(key);
		if (line == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			line = defaultValue;
		}
		String[] ary = line.split(entrysplit);
		String[] key_value;
		for (String kv : ary)
		{
			key_value = kv.split(kvsplit);
			map.put(Integer.parseInt(key_value[0]), Integer.parseInt(key_value[1]));
		}
		return map;
	}
	
	/**
	 * @param key
	 * @param defaultValue
	 * @param split1
	 * @param split2
	 * @return
	 */
	public int[][] getIntIntArray(String key, String defaultValue, String split1, String split2)
	{
		String line = getValue(key);
		if (line == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			line = defaultValue;
		}
		String[] tmpArray = line.split(split1);
		int[][] array = new int[tmpArray.length][];
		String[] tmpArray2;
		for (int i = 0; i < tmpArray.length; i++)
		{
			tmpArray2 = tmpArray[i].split(split2);
			int[] array1 = new int[tmpArray2.length];
			for (int j = 0; j < tmpArray2.length; j++)
			{
				array1[j] = Integer.parseInt(tmpArray2[j]);
			}
			array[i] = array1;
		}
		return array;
	}
	
	public double[] autoFillArray(String key, String defaultValue, int maxLength)
	{
		String line = getValue(key);
		if (line == null)
		{
			_log.warning("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
			line = defaultValue;
		}
		String[] values = line.split(";");
		double[] result = new double[maxLength];
		for (String s : values)
		{
			int min;
			int max;
			double val;
			
			String[] vals = s.split("-");
			String[] mM = vals[0].split(",");
			
			min = Integer.parseInt(mM[0]);
			max = Integer.parseInt(mM[1]);
			val = Double.parseDouble(vals[1]);
			
			for (int i = min; i <= max; i++)
			{
				result[i] = val;
			}
		}
		return result;
	}
}
