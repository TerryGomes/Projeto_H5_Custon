package l2mv.gameserver.network.serverpackets.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.item.ItemTemplate;

public class CustomMessage
{
	private static final Logger _log = LoggerFactory.getLogger(CustomMessage.class);

	private String _text;
	private int mark = 0;

	public CustomMessage(String text)
	{
		this._text = text;
	}

	public CustomMessage(String address, Player player, Object... args)
	{
		this._text = StringHolder.getInstance().getNotNull(player, address);
		this.add(args);
	}

	public CustomMessage addNumber(long number)
	{
		this._text = this._text.replace("{" + this.mark + "}", String.valueOf(number));
		this.mark++;
		return this;
	}

	public CustomMessage add(Object... args)
	{
		for (Object arg : args)
		{
			if (arg instanceof String)
			{
				this.addString((String) arg);
			}
			else if (arg instanceof Integer)
			{
				this.addNumber((Integer) arg);
			}
			else if (arg instanceof Long)
			{
				this.addNumber((Long) arg);
			}
			else if (arg instanceof ItemTemplate)
			{
				this.addItemName((ItemTemplate) arg);
			}
			else if (arg instanceof ItemInstance)
			{
				this.addItemName((ItemInstance) arg);
			}
			else if (arg instanceof Creature)
			{
				this.addCharName((Creature) arg);
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
		this._text = this._text.replace("{" + this.mark + "}", str);
		this.mark++;
		return this;
	}

	public CustomMessage addSkillName(Skill skill)
	{
		this._text = this._text.replace("{" + this.mark + "}", skill.getName());
		this.mark++;
		return this;
	}

	public CustomMessage addSkillName(int skillId, int skillLevel)
	{
		return this.addSkillName(SkillTable.getInstance().getInfo(skillId, skillLevel));
	}

	public CustomMessage addItemName(ItemTemplate item)
	{
		this._text = this._text.replace("{" + this.mark + "}", item.getName());
		this.mark++;
		return this;
	}

	public CustomMessage addItemName(int itemId)
	{
		return this.addItemName(ItemHolder.getInstance().getTemplate(itemId));
	}

	public CustomMessage addItemName(ItemInstance item)
	{
		return this.addItemName(item.getTemplate());
	}

	public CustomMessage addCharName(Creature cha)
	{
		this._text = this._text.replace("{" + this.mark + "}", cha.getName());
		this.mark++;
		return this;
	}

	@Override
	public String toString()
	{
		return this._text;
	}
}