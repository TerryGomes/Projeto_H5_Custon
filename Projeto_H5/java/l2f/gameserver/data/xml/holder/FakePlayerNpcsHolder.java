package l2f.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.templates.npc.FakePlayerTemplate;

public final class FakePlayerNpcsHolder extends AbstractHolder
{
	private final Map<Integer, FakePlayerTemplate> _templates;

	private FakePlayerNpcsHolder()
	{
		_templates = new HashMap<Integer, FakePlayerTemplate>();
	}

	public void addTemplate(FakePlayerTemplate template)
	{
		_templates.put(template.getTemplateId(), template);
	}

	public FakePlayerTemplate getTemplate(int templateId)
	{
		return _templates.get(templateId);
	}

	public Map<Integer, FakePlayerTemplate> getTemplatesForIterate()
	{
		return _templates;
	}

	public boolean containsTemplate(int templateId)
	{
		return _templates.containsKey(templateId);
	}

	@Override
	public int size()
	{
		return _templates.size();
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}

	@Override
	public String toString()
	{
		return "FakePlayerNpcsHolder{templates=" + _templates + '}';
	}

	public static FakePlayerNpcsHolder getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final FakePlayerNpcsHolder _instance = new FakePlayerNpcsHolder();
	}
}
