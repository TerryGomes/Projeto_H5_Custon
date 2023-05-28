package l2mv.gameserver.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.quest.Quest;

public class Scripts
{
	private static final Logger _log = LoggerFactory.getLogger(Scripts.class);

	private static final Scripts _instance = new Scripts();

	public static Scripts getInstance()
	{
		return _instance;
	}

	public static final Map<Integer, List<ScriptClassAndMethod>> dialogAppends = new HashMap<Integer, List<ScriptClassAndMethod>>();
	public static final Map<String, ScriptClassAndMethod> onAction = new HashMap<String, ScriptClassAndMethod>();
	public static final Map<String, ScriptClassAndMethod> onActionShift = new HashMap<String, ScriptClassAndMethod>();

	private final Map<String, Class<?>> _classes = new TreeMap<String, Class<?>>();

	private Scripts()
	{
		load();
	}

	/**
	 * Loading all Script files from ./../libs/l2f-scripts.jar
	 */
	private void load()
	{
		_log.info("Scripts: Loading...");

		List<Class<?>> jarClasses = new ArrayList<Class<?>>();
		List<Class<?>> classes = new ArrayList<Class<?>>();

		boolean result = false;

		File f = new File("./../libs/scripts.jar");
		if (f.exists())
		{
			_log.info("Loading Server Scripts");
			try (JarInputStream stream = new JarInputStream(new FileInputStream(f)))
			{
				JarEntry entry = null;
				while ((entry = stream.getNextJarEntry()) != null)
				{
					// Đ’Đ»ĐľĐ¶ĐµĐ˝Đ˝Ń‹Đµ ĐşĐ»Đ°Ń�Ń�
					if (entry.getName().contains(ClassUtils.INNER_CLASS_SEPARATOR) || !entry.getName().endsWith(".class"))
					{
						continue;
					}

					String name = entry.getName().replace(".class", "").replace("/", ".");

					Class<?> clazz;// = Class.forName(name);
					clazz = getClass().getClassLoader().loadClass(name);
					if (Modifier.isAbstract(clazz.getModifiers()))
					{
						continue;
					}
					jarClasses.add(clazz);
				}
				result = true;
			}
			catch (ClassNotFoundException | IOException e)
			{
				_log.error("Fail to load l2f-scripts.jar!", e);
				jarClasses.clear();
			}
		}

		classes.addAll(jarClasses);
		if (!result)
		{
			_log.error("Scripts: Failed loading scripts!");
			Runtime.getRuntime().exit(0);
			return;
		}

		_log.info("Scripts: Loaded " + classes.size() + " classes.");

		Class<?> clazz;
		for (Class<?> aClass : classes)
		{
			clazz = aClass;
			_classes.put(clazz.getName(), clazz);
		}
	}

	/**
	 * Creating new Instance of every Class<?> from _classes
	 */
	public void init()
	{
//		if ((!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("127.0.0.1")) && (!Config.EXTERNAL_HOSTNAME.equalsIgnoreCase("178.33.90.147")))
//		{
//			return;
//		}
		for (Class<?> clazz : _classes.values())
		{
			addHandlers(clazz);

			if (Config.DONTLOADQUEST)
			{
				if (ClassUtils.isAssignable(clazz, Quest.class))
				{
					continue;
				}
			}

			if (ClassUtils.isAssignable(clazz, ScriptFile.class))
			{
				try
				{
					((ScriptFile) clazz.newInstance()).onLoad();
				}
				catch (IllegalAccessException | InstantiationException e)
				{
					_log.error("Scripts: Failed running " + clazz.getName() + ".onLoad()", e);
				}
			}
		}
	}

	/**
	 * Reloading Scripts from ./../libs/l2f-scripts.jar
	 * @return
	 */
	@Deprecated
	public boolean reload()
	{
		_log.info("Unable to reload Scripts");

		return false;
	}

	/**
	 * Shutting down every class instance in _classes
	 */
	public void shutdown()
	{
		for (Class<?> clazz : _classes.values())
		{
			if (ClassUtils.isAssignable(clazz, Quest.class))
			{
				continue;
			}

			if (ClassUtils.isAssignable(clazz, ScriptFile.class))
			{
				try
				{
					((ScriptFile) clazz.newInstance()).onShutdown();
				}
				catch (IllegalAccessException | InstantiationException e)
				{
					_log.error("Scripts: Failed running " + clazz.getName() + ".onShutdown()", e);
				}
			}
		}
	}

	/**
	 * Adding Handlers like DialogAppend, OnAction and OnActionShif if they exists in Class
	 * @param clazz Class to look for Handlers
	 */
	private static void addHandlers(Class<?> clazz)
	{
		try
		{
			for (Method method : clazz.getMethods())
			{
				if (method.getName().contains("DialogAppend_"))
				{
					Integer id = Integer.parseInt(method.getName().substring(13));
					List<ScriptClassAndMethod> handlers = dialogAppends.get(id);
					if (handlers == null)
					{
						handlers = new ArrayList<>();
						dialogAppends.put(id, handlers);
					}
					handlers.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
				}
				else if (method.getName().contains("OnAction_"))
				{
					String name = method.getName().substring(9);
					onAction.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
				}
				else if (method.getName().contains("OnActionShift_"))
				{
					String name = method.getName().substring(14);
					onActionShift.put(name, new ScriptClassAndMethod(clazz.getName(), method.getName()));
				}
			}
		}
		catch (NumberFormatException | SecurityException e)
		{
			_log.error("Exception while adding Handlers ", e);
		}
	}

	/**
	 * Calling Method in Script file with Player caller as NULL, arguments as NULL, variables as NULL
	 * @param className Class to call
	 * @param methodName Method to call
	 * @return Object returned from method
	 */
	public Object callScripts(String className, String methodName)
	{
		return callScripts(null, className, methodName, null, null);
	}

	/**
	 * Calling Method in Script file with arguments, Player caller as NULL, variables as NULL
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param args Arguments that method takes
	 * @return Object returned from method
	 */
	public Object callScripts(String className, String methodName, Object[] args)
	{
		return callScripts(null, className, methodName, args, null);
	}

	/**
	 * Calling Method in Script file with variables, empty Arguments, Player caller as NULL
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param variables Additional arguments that may be called in Script file
	 * @return Object returned from method
	 */
	public Object callScripts(String className, String methodName, Map<String, Object> variables)
	{
		return callScripts(null, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
	}

	/**
	 * Calling Method in Script file with variables, argument and Player caller as NULL
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param args Arguments that method takes
	 * @param variables Additional arguments that may be called in Script file
	 * @return Object returned from method
	 */
	public Object callScripts(String className, String methodName, Object[] args, Map<String, Object> variables)
	{
		return callScripts(null, className, methodName, args, variables);
	}

	/**
	 * Calling Method in Script file with variables, argument and Player caller as NULL
	 * @param caller Player calling class(can be used later with getSelf())
	 * @param className Class to call
	 * @param methodName Method to call
	 * @return Object returned from method
	 */
	public Object callScripts(Player caller, String className, String methodName)
	{
		return callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, null);
	}

	/**
	 * Calling Method in Script file with variables, argument and Player caller as NULL
	 * @param caller Player calling class(can be used later with getSelf())
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param args Arguments that method takes
	 * @return Object returned from method
	 */
	public Object callScripts(Player caller, String className, String methodName, Object[] args)
	{
		return callScripts(caller, className, methodName, args, null);
	}

	/**
	 * Calling Method in Script file with variables, argument and Player caller as NULL
	 * @param caller Player calling class(can be used later with getSelf())
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param variables Additional arguments that may be called in Script file
	 * @return Object returned from method
	 */
	public Object callScripts(Player caller, String className, String methodName, Map<String, Object> variables)
	{
		return callScripts(caller, className, methodName, ArrayUtils.EMPTY_OBJECT_ARRAY, variables);
	}

	/**
	 * Calling Method in Script file with variables, argument and Player caller as NULL
	 * @param caller Player calling class(can be used later with getSelf())
	 * @param className Class to call
	 * @param methodName Method to call
	 * @param args Arguments that method takes
	 * @param variables Additional arguments that may be called in Script file
	 * @return Object returned from method
	 */
	public Object callScripts(Player caller, String className, String methodName, Object[] args, Map<String, Object> variables)
	{
		Object o;
		Class<?> clazz;

		clazz = _classes.get(className);
		if (clazz == null)
		{
			_log.error("Script class " + className + " not found!");
			return null;
		}

		try
		{
			o = clazz.newInstance();
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			_log.error("Scripts: Failed creating instance of " + clazz.getName(), e);
			return null;
		}

		if (variables != null && !variables.isEmpty())
		{
			for (Map.Entry<String, Object> param : variables.entrySet())
			{
				try
				{
					FieldUtils.writeField(o, param.getKey(), param.getValue());
				}
				catch (IllegalAccessException e)
				{
					_log.error("Scripts: Failed setting fields for " + clazz.getName(), e);
				}
			}
		}

		if (caller != null)
		{
			try
			{
				Field field = null;
				if ((field = FieldUtils.getField(clazz, "self")) != null)
				{
					FieldUtils.writeField(field, o, caller.getRef());
				}
			}
			catch (IllegalAccessException e)
			{
				_log.error("Scripts: Failed setting field for " + clazz.getName(), e);
			}
		}

		Object ret = null;
		try
		{
			Class<?>[] parameterTypes = new Class<?>[args.length];
			for (int i = 0; i < args.length; i++)
			{
				parameterTypes[i] = args[i] != null ? args[i].getClass() : null;
			}

			ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
		}
		catch (NoSuchMethodException nsme)
		{
			_log.error("Scripts: No such method " + clazz.getName() + "." + methodName + "()!", nsme);
		}
		catch (InvocationTargetException ite)
		{
			_log.error("Scripts: Error while calling " + clazz.getName() + "." + methodName + "()", ite.getTargetException());
		}
		catch (IllegalAccessException e)
		{
			_log.error("Scripts: Failed calling " + clazz.getName() + "." + methodName + "()", e);
		}

		return ret;
	}

	/**
	 * @return All script classes in Map<Class Name, Class<?>>
	 */
	public Map<String, Class<?>> getClasses()
	{
		return Collections.unmodifiableMap(_classes);
	}

	public static class ScriptClassAndMethod
	{
		public final String className;
		public final String methodName;

		public ScriptClassAndMethod(String className, String methodName)
		{
			this.className = className;
			this.methodName = methodName;
		}
	}
}