package l2mv.gameserver.network.loginservercon.lspackets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import l2mv.gameserver.Config;
import l2mv.gameserver.dao.CharacterDAO;
import l2mv.gameserver.data.StringHolder;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.loginservercon.ReceivablePacket;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.IStaticPacket;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.utils.Language;

public class OnWrongAccountPassword extends ReceivablePacket
{
	private static final ChatType CHAT_TYPE_FOR_GM = ChatType.TELL;
	private static final int SPAM_MSG_COUNT_TO_GM = 10;

	private String accountName;
	private String passwordWrote;

	@Override
	public void readImpl()
	{
		this.accountName = this.readS();
		this.passwordWrote = this.readS();
	}

	@Override
	protected void runImpl()
	{
		final List<Integer> charIds = CharacterDAO.getCharIdsFromAccount(this.accountName);
		for (Integer charId : charIds)
		{
			if (Config.gmlist.containsKey(charId))
			{
				final Player onlineGM = GameObjectsStorage.getPlayer(charId);
				if (onlineGM != null)
				{
					final IStaticPacket packet = new Say2(0, OnWrongAccountPassword.CHAT_TYPE_FOR_GM, "WARNING", "Someone is trying to enter your Account and wrote Wrong Password(" + this.passwordWrote + ")!");
					final List<IStaticPacket> spamList = new ArrayList<IStaticPacket>(10);
					for (int i = 0; i < SPAM_MSG_COUNT_TO_GM; ++i)
					{
						spamList.add(packet);
					}
					onlineGM.sendPacket(spamList);
				}
				else
				{
					final String charName = CharacterDAO.getNameByObjectId(charId);
					final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
					Functions.sendSystemMail(charName, "WARNING", "Someone tried to enter your Account(at " + dateFormat.format(new Date()) + ") and wrote Wrong Password(" + this.passwordWrote + ")!", Collections.emptyMap());
				}
			}
			else
			{
				final Player onlinePlayer = GameObjectsStorage.getPlayer(charId);
				if (onlinePlayer != null)
				{
					if (!Config.NORMAL_PLAYER_RECIEVE_MSG_ON_WRONG_ACCOUNT_PASS)
					{
						return;
					}
					final IStaticPacket packet = new Say2(0, Config.NORMAL_PLAYER_MSG_TYPE_ON_WRONG_ACCOUNT, StringHolder.getNotNull(onlinePlayer, "Security.SomeoneEnteringAccountPMOnlineTitle", new Object[0]), StringHolder.getNotNull(onlinePlayer, "Security.SomeoneEnteringAccountPMOnlineBody", this.passwordWrote));
					onlinePlayer.sendPacket(packet);
				}
				else
				{
					if (!Config.NORMAL_PLAYER_MAIL_ON_WRONG_ACCOUNT_WHILE_OFFLINE)
					{
						continue;
					}
					final String charName = CharacterDAO.getNameByObjectId(charId);
					final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
					final Language lang = CharacterDAO.getLanguage(charId);
					final String title = StringHolder.getNotNull(lang, "Security.SomeoneEnteringAccountMailOfflineTitle", new Object[0]);
					final String body = StringHolder.getNotNull(lang, "Security.SomeoneEnteringAccountMailOfflineBody", dateFormat.format(new Date()), this.passwordWrote);
					Functions.sendSystemMail(charName, title, body, Collections.emptyMap());
				}
			}
		}
	}
}
