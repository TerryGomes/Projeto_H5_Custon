package l2f.gameserver.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.annotations.Nullable;

/**
 * Class helping to Invoke the Methods<p/>
 * You can invoke the method immediately by using {@link #invokeMethod(Class, String, Object, Object...)}<p/>
 * or by keeping the {@link Method} class(create by {@link #createMethodInfo(Class, String, Object, Object...)} and then invoking it with {@link #invokeMethod(Method)}
 */
public class MethodInvoker
{
	private static final Logger LOG = LoggerFactory.getLogger(MethodInvoker.class);

	private MethodInvoker()
	{
	}

	/**
	 * Class containing information required to Invoke the Specified Method.<p/>
	 * It contains classToInvoke, methodName, classObject and parameters.<p/>
	 * Create it by {@link #createMethodInfo(Class, String, Object, Object...)}.<p/>
	 * You can then invoke the Method by {@link #invokeMethod(Method)}
	 */
	public static class Method
	{
		private final Class<?> classToInvoke;
		private final String methodName;
		private final Object classObject;
		private final Object[] parameters;

		private Method(Class<?> classToInvoke, String methodName, @Nullable Object classObject, Object... parameters)
		{
			this.classToInvoke = classToInvoke;
			this.methodName = methodName;
			this.classObject = classObject;
			this.parameters = new Object[parameters.length];
			System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
		}

		private Class<?> getClassToInvoke()
		{
			return classToInvoke;
		}

		private String getMethodName()
		{
			return methodName;
		}

		private Object getClassObject()
		{
			return classObject;
		}

		private Object[] getParameters()
		{
			return parameters;
		}
	}

	/**
	 * Creating new Object of the Method which contains information required to invoke a Method.
	 * <p/>
	 * Method can be later invoked by {@link #invokeMethod(Method)}
	 * <ul>
	 *  <li>Method to Invoke needs to be public.</li>
	 *  <li>ClassObject can be null, but then Invoking Method needs to be Static</li>
	 *  <li>Parameters can be empty</li>
	 *  <li>If Method Parameter is extended primitive(like Integer), primitive type can be used as parameter value</li>
	 * </ul>
	 * @param classToInvoke Class which contains the Method. Needs to be Public
	 * @param methodName Name of the Method to Invoke
	 * @param classObject Object of the Class which will run the method
	 * @param parameters Parameters that method is accepting.
	 * @return Object of the Method.
	 */
	public static Method createMethodInfo(Class<?> classToInvoke, String methodName, @Nullable Object classObject, Object... parameters)
	{
		return new Method(classToInvoke, methodName, classObject, parameters);
	}

	/**
	 * Invoking method of the Class. All required information should be in <code>method</code>.
	 * <p>
	 * You can create Method object by {@link #createMethodInfo(Class, String, Object, Object...)}
	 *
	 * @param method Object which contains information required to invoke method
	 * @return Return Value of the method
	 */
	public static Object invokeMethod(Method method)
	{
		return invokeMethod(method.getClassToInvoke(), method.getMethodName(), method.getClassObject(), method.getParameters());
	}

	/**
	 * Invoking the method by the Class, Method Name, Class object and parameters.
	 * <ul>
	 *  <li>Method needs to be public.</li>
	 *  <li>ClassObject can be null, but then Method needs to be Static</li>
	 *  <li>Parameters can be empty</li>
	 *  <li>If Method Parameter is extended primitive(like Integer), primitive type can be used as parameter value</li>
	 * </ul>
	 *
	 * @param classToInvoke Class which contains the Method. Needs to be Public
	 * @param methodName Name of the Method to Invoke
	 * @param classObject Object of the Class which will run the method
	 * @param parameters Parameters that method is accepting.
	 * @return Return value of the Method
	 */
	@SuppressWarnings("OverloadedVarargsMethod")
	public static Object invokeMethod(Class<?> classToInvoke, String methodName, @Nullable Object classObject, Object... parameters)
	{
		Class<?>[] parameterClasses = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++)
		{
			parameterClasses[i] = parameters.getClass();
		}
		try
		{
			if (classObject == null)
			{
				return MethodUtils.invokeStaticMethod(classToInvoke, methodName, parameters, parameterClasses);
			}
			else
			{
				return MethodUtils.invokeMethod(classObject, methodName, parameters, parameterClasses);
			}
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
		{
			LOG.error("Error while invoking the method!", e);
			return null;
		}
	}
}
