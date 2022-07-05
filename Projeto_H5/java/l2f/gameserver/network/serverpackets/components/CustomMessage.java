package l2f.gameserver.network.serverpackets.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.data.StringHolder;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.templates.item.ItemTemplate;

public class CustomMessage
{
	private static final Logger _log = LoggerFactory.getLogger(CustomMessage.class);

	private String _text;
	private int mark = 0;

	public CustomMessage(String text)
	{
		_text = text;
	}

	public CustomMessage(String address, Player player, Object... args)
	{
		_text = StringHolder.getInstance().getNotNull(player, address);
		add(args);
	}

	public CustomMessage addNumber(long number)
	{
		_text = _text.replace("{" + mark + "}", String.valueOf(number));
		mark++;
		return this;
	}

	public CustomMessage add(Object... args)
	{
		for (Object arg : args)
		{
			if (arg instanceof String)
			{
				addString((String) arg);
			}
			else if (arg instanceof Integer)
			{
				addNumber((Integer) arg);
			}
			else if (arg instanceof Long)
			{
				addNumber((Long) arg);
			}
			else if (arg instanceof ItemTemplate)
			{
				addItemName((ItemTemplate) arg);
			}
			else if (arg instanceof ItemInstance)
			{
				addItemName((ItemInstance) arg);
			}
			else if (arg instanceof Creature)
			{
				addCharName((Creature) arg);
			}
			else if (arg instanceof Skill)
			{
				this.addSkillName((Skill) arg);
			}
			else
			{
				_log.warn("unknown CustomMessage arg type: " + arg);
				Thread.dumpStack();
			}
		}

		return this;
	}

	public CustomMessage addString(String str)
	{
		_text = _text.replace("{" + mark + "}", str);
		mark++;
		return this;
	}

	public CustomMessage addSkillName(Skill skill)
	{
		_text = _text.replace("{" + mark + "}", skill.getName());
		mark++;
		return this;
	}

	public CustomMessage addSkillName(int skillId, int skillLevel)
	{
		return addSkillName(SkillTable.getInstance().getInfo(skillId, skillLevel));
	}

	public CustomMessage addItemName(ItemTemplate item)
	{
		_text = _text.replace("{" + mark + "}", item.getName());
		mark++;
		return this;
	}

	public CustomMessage addItemName(int itemId)
	{
		return addItemName(ItemHolder.getInstance().getTemplate(itemId));
	}

	public CustomMessage addItemName(ItemInstance item)
	{
		return addItemName(item.getTemplate());
	}

	public CustomMessage addCharName(Creature cha)
	{
		_text = _text.replace("{" + mark + "}", cha.getName());
		mark++;
		return this;
	}

	@Override
	public String toString()
	{
		return _text;
	}
}