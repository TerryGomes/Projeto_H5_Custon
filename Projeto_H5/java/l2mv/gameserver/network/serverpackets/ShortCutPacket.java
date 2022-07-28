package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.skills.TimeStamp;

/**
 * @author VISTALL
 * @date 7:48/29.03.2011
 */
public abstract class ShortCutPacket extends L2GameServerPacket
{
	public static ShortcutInfo convert(Player player, ShortCut shortCut)
	{
		ShortcutInfo shortcutInfo = null;
		int page = shortCut.getSlot() + shortCut.getPage() * 12;
		switch (shortCut.getType())
		{
		case ShortCut.TYPE_ITEM:
			int reuseGroup = -1, currentReuse = 0, reuse = 0, augmentationId = 0;
			ItemInstance item = player.getInventory().getItemByObjectId(shortCut.getId());
			if (item != null)
			{
				augmentationId = item.getAugmentationId();
				reuseGroup = item.getTemplate().getDisplayReuseGroup();
				if (item.getTemplate().getReuseDelay() > 0)
				{
					TimeStamp timeStamp = player.getSharedGroupReuse(item.getTemplate().getReuseGroup());
					if (timeStamp != null)
					{
						currentReuse = (int) (timeStamp.getReuseCurrent() / 1000L);
						reuse = (int) (timeStamp.getReuseBasic() / 1000L);
					}
				}
			}
			shortcutInfo = new ItemShortcutInfo(shortCut.getType(), page, shortCut.getId(), reuseGroup, currentReuse, reuse, augmentationId, shortCut.getCharacterType());
			break;
		case ShortCut.TYPE_SKILL:
			shortcutInfo = new SkillShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getLevel(), shortCut.getCharacterType());
			break;
		default:
			shortcutInfo = new ShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getCharacterType());
			break;
		}
		return shortcutInfo;
	}

	protected static class ItemShortcutInfo extends ShortcutInfo
	{
		private int _reuseGroup;
		private int _currentReuse;
		private int _basicReuse;
		private int _augmentationId;

		public ItemShortcutInfo(int type, int page, int id, int reuseGroup, int currentReuse, int basicReuse, int augmentationId, int characterType)
		{
			super(type, page, id, characterType);
			this._reuseGroup = reuseGroup;
			this._currentReuse = currentReuse;
			this._basicReuse = basicReuse;
			this._augmentationId = augmentationId;
		}

		@Override
		protected void write0(ShortCutPacket p)
		{
			p.writeD(this._id);
			p.writeD(this._characterType);
			p.writeD(this._reuseGroup);
			p.writeD(this._currentReuse);
			p.writeD(this._basicReuse);
			p.writeD(this._augmentationId);
		}
	}

	protected static class SkillShortcutInfo extends ShortcutInfo
	{
		private final int _level;

		public SkillShortcutInfo(int type, int page, int id, int level, int characterType)
		{
			super(type, page, id, characterType);
			this._level = level;
		}

		public int getLevel()
		{
			return this._level;
		}

		@Override
		protected void write0(ShortCutPacket p)
		{
			p.writeD(this._id);
			p.writeD(this._level);
			p.writeC(0x00);
			p.writeD(this._characterType);
		}
	}

	protected static class ShortcutInfo
	{
		protected final int _type;
		protected final int _page;
		protected final int _id;
		protected final int _characterType;

		public ShortcutInfo(int type, int page, int id, int characterType)
		{
			this._type = type;
			this._page = page;
			this._id = id;
			this._characterType = characterType;
		}

		protected void write(ShortCutPacket p)
		{
			p.writeD(this._type);
			p.writeD(this._page);
			this.write0(p);
		}

		protected void write0(ShortCutPacket p)
		{
			p.writeD(this._id);
			p.writeD(this._characterType);
		}
	}
}
