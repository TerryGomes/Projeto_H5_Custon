package l2f.gameserver.utils;

public class DynamicConfig extends DynamicObject
{
	private final String configName;

	public DynamicConfig(Object object, String configName)
	{
		super(object);
		this.configName = configName;
	}

	public String getConfigName()
	{
		return configName;
	}

	@Override
	protected void onError(String type)
	{
		DynamicObject.LOG.error("Trying to get \"" + type + "\" from Dynamic Object \"" + getObject() + "\" from Config \"" + configName + '\"');
		Thread.dumpStack();
	}

	@Override
	public String toString()
	{
		return "DynamicConfig{object=" + getObject() + ", configName='" + configName + '\'' + '}';
	}
}
