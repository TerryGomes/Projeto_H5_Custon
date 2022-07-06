package l2mv.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Config;
import l2mv.gameserver.dao.AccountBonusDAO;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.xml.holder.PremiumHolder;
import l2mv.gameserver.data.xml.holder.SkillAcquireHolder;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.instancemanager.QuestManager;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SkillLearn;
import l2mv.gameserver.model.actor.instances.player.Bonus;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.model.base.AcquireType;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.premium.PremiumAccount;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.network.GameClient;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.loginservercon.gspackets.BonusRequest;
import l2mv.gameserver.network.serverpackets.CharacterCreateFail;
import l2mv.gameserver.network.serverpackets.CharacterCreateSuccess;
import l2mv.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.PlayerTemplate;
import l2mv.gameserver.templates.item.CreateItem;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Util;

public class CharacterCreate extends L2GameClientPacket
{
	private static final Logger LOG = LoggerFactory.getLogger(CharacterCreate.class);
	// cSdddddddddddd
	private String _name;
	private int _sex;
	private int _classId;
	private int _hairStyle;
	private int _hairColor;
	private int _face;

	@Override
	protected void readImpl()
	{
		_name = readS();
		readD(); // race
		_sex = readD();
		_classId = readD();
		readD(); // int
		readD(); // str
		readD(); // con
		readD(); // men
		readD(); // dex
		readD(); // wit
		_hairStyle = readD();
		_hairColor = readD();
		_face = readD();
	}

	@Override
	protected void runImpl()
	{
		for (ClassId cid : ClassId.VALUES)
		{
			if (cid.getId() == _classId && cid.getLevel() != 1)
			{
				return;
			}
		}
		if (CharacterDAO.getInstance().accountCharNumber(getClient().getLogin()) >= 8)
		{
			sendPacket(CharacterCreateFail.REASON_TOO_MANY_CHARACTERS);
			return;
		}
		if (!checkName(_name) || _name.length() > 16)
		{
			sendPacket(CharacterCreateFail.REASON_16_ENG_CHARS);
			return;
		}
		else if (CharacterDAO.getInstance().getObjectIdByName(_name) > 0 || Util.contains(Config.FORBIDDEN_CHAR_NAMES, _name))
		{
			sendPacket(CharacterCreateFail.REASON_NAME_ALREADY_EXISTS);
			return;
		}

		if ((_face > 2) || (_face < 0))
		{
			return;
		}
		if ((_hairStyle < 0) || ((_sex == 0) && (_hairStyle > 4)) || ((_sex != 0) && (_hairStyle > 6)))
		{
			return;
		}
		if ((_hairColor > 3) || (_hairColor < 0))
		{
			return;
		}

		Player newChar = Player.create(_classId, _sex, getClient().getLogin(), _name, _hairStyle, _hairColor, _face);
		if (newChar == null)
		{
			return;
		}

		sendPacket(CharacterCreateSuccess.STATIC);

		initNewChar(getClient(), newChar);
	}

	private void initNewChar(GameClient client, Player newChar)
	{
		PlayerTemplate template = newChar.getTemplate();

		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			Player.restoreCharSubClasses(newChar, con);
		}
		catch (SQLException e)
		{
			LOG.error("Error while restoring Subclasses on initNewChar ", e);
		}

		if (Config.STARTING_ADENA > 0)
		{
			newChar.addAdena(Config.STARTING_ADENA, "Starting Adena");
		}

		if (Config.STARTING_LVL != 0)
		{
			newChar.addExpAndSp(Experience.LEVEL[Config.STARTING_LVL] - newChar.getExp(), 0, 0, 0, false, false);
		}

		if (Config.SPAWN_CHAR)
		{
			newChar.teleToLocation(Config.SPAWN_X, Config.SPAWN_Y, Config.SPAWN_Z);
		}
		else
		{
			newChar.setLoc(template.spawnLoc);
		}

		if (Config.CHAR_TITLE)
		{
			newChar.setTitle(Config.ADD_CHAR_TITLE);
		}
		else
		{
			newChar.setTitle("");
		}

		if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && Config.SERVICES_RATE_CREATE_PA != 0 && newChar.getBonus() == null)
		{
			newChar.getBonus().setBonusExpire((int) (System.currentTimeMillis() / 1000L * (60 * 60 * 24 * Config.SERVICES_RATE_CREATE_PA)));
			newChar.stopBonusTask();
			newChar.startBonusTask();
		}

		// Synerge - Support to gifting a premium service to the player when creating a new char
		if (Config.SERVICES_RATE_TYPE != Bonus.NO_BONUS && Config.ALT_NEW_CHAR_PREMIUM_ID != 0 && newChar.getBonus() == null)
		{
			PremiumAccount premium = PremiumHolder.getInstance().getPremium(Config.ALT_NEW_CHAR_PREMIUM_ID);
			int current = (int) (System.currentTimeMillis() / 1000L);
			int newBonusTime = current + premium.getTime();
			if (Config.PREMIUM_ACCOUNT_TYPE == 1)
			{
				AuthServerCommunication.getInstance().sendPacket(new BonusRequest(newChar.getAccountName(), premium.getId(), newBonusTime));
			}
			else
			{
				AccountBonusDAO.getInstance().insert(newChar.getAccountName(), premium.getId(), newBonusTime);
			}

			newChar.getNetConnection().setBonus(premium.getId());
			newChar.getNetConnection().setBonusExpire(newBonusTime);
		}

		for (CreateItem i : template.getItems())
		{
			ItemInstance item = ItemFunctions.createItem(i.getItemId());
			newChar.getInventory().addItem(item, "New Char Item");

			if (i.getShortcut() - 1 > -1) // tutorial book
			{
				newChar.registerShortCut(new ShortCut(Math.min(i.getShortcut() - 1, 11), 0, ShortCut.TYPE_ITEM, item.getObjectId(), -1, 1));
			}

			if (i.isEquipable() && item.isEquipable() && (newChar.getActiveWeaponItem() == null || item.getTemplate().getType2() != ItemTemplate.TYPE2_WEAPON))
			{
				newChar.getInventory().equipItem(item);
			}
		}

		ClassId nclassId = ClassId.VALUES[_classId];
		if (Config.ALLOW_START_ITEMS)
		{
			if (nclassId.isMage())
			{
				for (int i = 0; i < Config.START_ITEMS_MAGE.length; i++)
				{
					ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_MAGE[i]);
					item.setCount(Config.START_ITEMS_MAGE_COUNT[i]);
					newChar.getInventory().addItem(item, "New Char Item");
					if (item.isEquipable())
					{
						newChar.getInventory().equipItem(item);
					}
				}
			}
			else
			{
				for (int i = 0; i < Config.START_ITEMS_FITHER.length; i++)
				{
					ItemInstance item = ItemFunctions.createItem(Config.START_ITEMS_FITHER[i]);
					item.setCount(Config.START_ITEMS_FITHER_COUNT[i]);
					newChar.getInventory().addItem(item, "New Char Item");
					if (item.isEquipable())
					{
						newChar.getInventory().equipItem(item);
					}
				}
			}
		}
		// Adventurer's Scroll of Escape
		ItemInstance item = ItemFunctions.createItem(10650);
		item.setCount(5);
		newChar.getInventory().addItem(item, "New Char Item");

		// Scroll of Escape: Town Of Giran
		item = ItemFunctions.createItem(7126);
		item.setCount(10);
		newChar.getInventory().addItem(item, "New Char Item");

		for (SkillLearn skill : SkillAcquireHolder.getInstance().getAvailableSkills(newChar, AcquireType.NORMAL))
		{
			newChar.addSkill(SkillTable.getInstance().getInfo(skill.getId(), skill.getLevel()), true);
		}

		if (newChar.getSkillLevel(1001) > 0) // Soul Cry
		{
			newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1001, 1, 1));
		}
		if (newChar.getSkillLevel(1177) > 0) // Wind Strike
		{
			newChar.registerShortCut(new ShortCut(1, 0, ShortCut.TYPE_SKILL, 1177, 1, 1));
		}
		if (newChar.getSkillLevel(1216) > 0) // Self Heal
		{
			newChar.registerShortCut(new ShortCut(2, 0, ShortCut.TYPE_SKILL, 1216, 1, 1));
		}

		// add attack, take, sit shortcut
		newChar.registerShortCut(new ShortCut(0, 0, ShortCut.TYPE_ACTION, 2, -1, 1));
		newChar.registerShortCut(new ShortCut(3, 0, ShortCut.TYPE_ACTION, 5, -1, 1));
		newChar.registerShortCut(new ShortCut(10, 0, ShortCut.TYPE_ACTION, 0, -1, 1));
		// I understood a panel display. NC Soft 10-11 panel made (by VISTALL)
		// fly transform
		newChar.registerShortCut(new ShortCut(0, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 911, 1, 1));
		newChar.registerShortCut(new ShortCut(3, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 884, 1, 1));
		newChar.registerShortCut(new ShortCut(4, ShortCut.PAGE_FLY_TRANSFORM, ShortCut.TYPE_SKILL, 885, 1, 1));
		// air ship
		newChar.registerShortCut(new ShortCut(0, ShortCut.PAGE_AIRSHIP, ShortCut.TYPE_ACTION, 70, 0, 1));

		startTutorialQuest(newChar);

		newChar.setCurrentHpMp(newChar.getMaxHp(), newChar.getMaxMp());
		newChar.setCurrentCp(0); // retail
		newChar.setOnlineStatus(false);

		newChar.store(false);
		newChar.getInventory().store();
		newChar.deleteMe();

		client.setCharSelection(CharacterSelectionInfo.loadCharacterSelectInfo(client.getLogin()));
	}

	private static final String[] ALLOWED_LETTERS =
	{
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
		"9",
		"0",
		"q",
		"w",
		"e",
		"r",
		"t",
		"y",
		"u",
		"i",
		"o",
		"p",
		"a",
		"s",
		"d",
		"f",
		"g",
		"h",
		"j",
		"k",
		"l",
		"z",
		"x",
		"c",
		"v",
		"b",
		"n",
		"m"
	};

	public static boolean checkName(String name)
	{
		char[] chars = name.toCharArray();
		for (char c : chars)
		{
			String letter = String.valueOf(c);
			boolean foundLetter = false;
			for (String allowed : ALLOWED_LETTERS)
			{
				if (letter.equalsIgnoreCase(allowed))
				{
					foundLetter = true;
				}
			}
			if (!foundLetter)
			{
				return false;
			}
		}
		return true;
	}

	public static void startTutorialQuest(Player player)
	{
		Quest q = QuestManager.getQuest(255);
		if (q != null)
		{
			q.newQuestState(player, Quest.CREATED);
		}
	}
}