package l2f.gameserver.network.clientpackets;

import l2f.gameserver.instancemanager.QuestManager;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.quest.Quest;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	// format: cd
	int event = 0;

	/**
	 * Пакет от клиента, если вы в туториале подергали мышкой как надо - клиент пришлет его со значением 1 ну или нужным ивентом
	 */
	@Override
	protected void readImpl()
	{
		event = readD();
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if (player == null)
		{
			return;
		}

		Quest tutorial = QuestManager.getQuest(255);
		if (tutorial != null)
		{
			player.processQuestEvent(tutorial.getName(), "CE" + event, null);
		}
	}
}