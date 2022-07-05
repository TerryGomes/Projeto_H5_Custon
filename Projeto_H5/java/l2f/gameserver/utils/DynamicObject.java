package l2f.gameserver.utils;

import java.io.File;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.annotations.NotNull;

public class DynamicObject
{
	protected static final Logger LOG;
	private final Object object;

	public DynamicObject(Object object)
	{
		super();
		this.object = object;
	}

	public Object getObject()
	{
		return object;
	}

	public int getInt()
	{
		if (object instanceof Number)
		{
			final Number tmp = (Number) object;
			return tmp.intValue();
		}
		onError("Integer");
		throw new AssertionError();
	}

	public long getLong()
	{
		if (object instanceof Number)
		{
			final Number tmp = (Number) object;
			return tmp.longValue();
		}
		onError("Long");
		throw new AssertionError();
	}

	public double getDouble()
	{
		if (object instanceof Number)
		{
			final Number tmp = (Number) object;
			return tmp.doubleValue();
		}
		onError("Double");
		throw new AssertionError();
	}

	public boolean getBool()
	{
		if (object instanceof Boolean)
		{
			return (boolean) object;
		}
		onError("Boolean");
		throw new AssertionError();
	}

	public String getString()
	{
		if (object instanceof String)
		{
			return (String) object;
		}
		return object.toString();
	}

	public File getFilePath()
	{
		if (object instanceof File)
		{
			return (File) object;
		}
		if (object instanceof String)
		{
			return new File((String) object);
		}
		onError("File");
		throw new AssertionError();
	}

	@NotNull
	public Pattern getPattern()
	{
		if (object instanceof Pattern)
		{
			final Pattern pattern = (Pattern) object;
			if (pattern == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/utils/DynamicObject", "getPattern"));
			}
			return pattern;
		}
		else
		{
			if (!(object instanceof String))
			{
				onError("Pattern");
				throw new AssertionError();
			}
			final Pattern compile = Pattern.compile((String) object);
			if (compile == null)
			{
				throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "l2f/gameserver/utils/DynamicObject", "getPattern"));
			}
			return compile;
		}
	}

	public Location getLocation()
	{
		if (object instanceof Location)
		{
			return (Location) object;
		}
		if (object instanceof int[])
		{
			final int[] arrayValue = (int[]) object;
			if (arrayValue.length == 3)
			{
				return new Location(arrayValue[0], arrayValue[1], arrayValue[2]);
			}
			if (arrayValue.length == 4)
			{
				return new Location(arrayValue[0], arrayValue[1], arrayValue[2], arrayValue[3]);
			}
		}
		onError("Location");
		throw new AssertionError();
	}

	public <E> E getClassType(Class<E> eClass)
	{
		if (eClass.isAssignableFrom(object.getClass()))
		{
			return (E) object;
		}
		onError(eClass.toString());
		throw new AssertionError();
	}

	public int[][] getMultiIntArray()
	{
		if (object instanceof int[][])
		{
			return (int[][]) object;
		}
		onError("int[][]");
		throw new AssertionError();
	}

	public long[][] getMultiLongArray()
	{
		if (object instanceof long[][])
		{
			return (long[][]) object;
		}
		onError("long[][]");
		throw new AssertionError();
	}

	public double[][] getMultiDoubleArray()
	{
		if (object instanceof double[][])
		{
			return (double[][]) object;
		}
		onError("double[][]");
		throw new AssertionError();
	}

	public String[][] getMultiStringArray()
	{
		if (object instanceof String[][])
		{
			return (String[][]) object;
		}
		onError("String[][]");
		throw new AssertionError();
	}

	public int[] getIntArray()
	{
		if (object instanceof int[])
		{
			return (int[]) object;
		}
		onError("int[]");
		throw new AssertionError();
	}

	public long[] getLongArray()
	{
		if (object instanceof long[])
		{
			return (long[]) object;
		}
		onError("Long[]");
		throw new AssertionError();
	}

	public double[] getDoubleArray()
	{
		if (object instanceof double[])
		{
			return (double[]) object;
		}
		onError("double[]");
		throw new AssertionError();
	}

	public boolean[] getBooleanArray()
	{
		if (object instanceof boolean[])
		{
			return (boolean[]) object;
		}
		onError("boolean[]");
		throw new AssertionError();
	}

	public String[] getStringArray()
	{
		if (object instanceof String[])
		{
			return (String[]) object;
		}
		onError("String[]");
		throw new AssertionError();
	}

	protected void onError(String type)
	{
		throw new AssertionError("Trying to get \"" + type + "\" from Dynamic Object \"" + object + '\"');
	}

	@Override
	public String toString()
	{
		return "DynamicObject{object=" + object + '}';
	}

	static
	{
		LOG = LoggerFactory.getLogger(DynamicObject.class);
	}
}
