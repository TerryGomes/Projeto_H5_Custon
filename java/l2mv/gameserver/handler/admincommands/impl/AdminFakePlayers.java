package l2mv.gameserver.handler.admincommands.impl;

import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.xml.holder.FakePlayerNpcsHolder;
import l2mv.gameserver.data.xml.holder.NpcHolder;
import l2mv.gameserver.data.xml.parser.FakePlayerNpcsParser;
import l2mv.gameserver.handler.admincommands.IAdminCommandHandler;
import l2mv.gameserver.idfactory.IdFactory;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.instances.FakePcInstance;
import l2mv.gameserver.network.FakeGameClient;
import l2mv.gameserver.tables.FakePlayersTable;
import l2mv.gameserver.templates.npc.FakePlayerTemplate;
import l2mv.gameserver.utils.Util;

public class AdminFakePlayers implements IAdminCommandHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(AdminFakePlayers.class);

	private static enum Commands
	{
		admin_create_fake_players,
		admin_setup_fake_player,
		admin_clone
	}

	@Override
	public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if (!activeChar.getPlayerAccess().CanReload)
		{
			return false;
		}

		switch (command)
		{
		case admin_create_fake_players:
		{
			int count = Integer.parseInt(wordList[1]);
			for (int i = 0; i < count; i++)
			{
				new FakeGameClient(null);
			}
			break;
		}
		case admin_setup_fake_player:
		{
			final StringTokenizer st = new StringTokenizer(fullString);
			st.nextToken();
			Player target = (activeChar.getTarget() instanceof Player ? activeChar.getTarget().getPlayer() : null);
			if (target == null && st.hasMoreTokens())
			{
				target = GameObjectsStorage.getPlayer(st.nextToken());
			}
			if (target == null)
			{
				activeChar.sendMessage("You must write the name of the player you want to copy or target him");
				return false;
			}
			final String title = Util.getAllTokens(st);
			final int id = getNewFakeTemplateId();
			final FakePlayerTemplate newTemplate = new FakePlayerTemplate(id, target.getName(), title, target);
			try
			{
				FakePlayerNpcsParser.getInstance().saveNewTemplate(newTemplate);
				activeChar.sendMessage("New fake player template saved succesfully");
			}
			catch (IOException | DocumentException e)
			{
				LOG.error("Error while setting up Fake Player Template", e);
				return false;
			}
			break;
		}
		// Synerge - Clones the targeted player and spawns a fakeplayer on the gm's position with the target's template. Its on real time, will be erased on restart
		case admin_clone:
		{
			try
			{
				Player target = (activeChar.getTarget() instanceof Player ? activeChar.getTarget().getPlayer() : null);
				if (target == null)
				{
					activeChar.sendMessage("You must target the player you want to create a fake player clone");
					return false;
				}

				final StringTokenizer st = new StringTokenizer(fullString);
				st.nextToken();
				final String name = st.nextToken();
				final String title = Util.getAllTokens(st);

				if (CharacterDAO.getInstance().getObjectIdByName(name) > 0 || FakePlayersTable.getActiveFakePlayers().contains(name) || FakePlayersTable.isRealTimeFakePlayerExist(name))
				{
					activeChar.sendMessage("The choosen fake player name already exists. Choose another one");
					return false;
				}

				final int id = getNewFakeTemplateId();
				final FakePlayerTemplate newTemplate = new FakePlayerTemplate(id, name, title, target);

				final FakePcInstance fakePc = new FakePcInstance(IdFactory.getInstance().getNextId(), NpcHolder.getInstance().getTemplate(90200));

				fakePc.setFakeTemplate(newTemplate);
				fakePc.setReflection(activeChar.getReflection());
				fakePc.spawnMe(activeChar.getLoc());

				activeChar.sendMessage("New fake player clone created succesfully");
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //clone [name] [title]");
				return false;
			}
			break;
		}
		}

		return true;
	}

	private static int getNewFakeTemplateId()
	{
		final Set<Integer> ids = FakePlayerNpcsHolder.getInstance().getTemplatesForIterate().keySet();
		int lastId = -1;
		for (Integer id : ids)
		{
			if (id > lastId)
			{
				lastId = id;
			}
		}
		return ++lastId;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
