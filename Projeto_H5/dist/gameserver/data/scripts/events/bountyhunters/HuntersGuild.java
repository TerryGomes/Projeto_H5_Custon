package events.bountyhunters;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.data.xml.holder.NpcHolder;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.model.instances.ChestInstance;
import l2f.gameserver.model.instances.DeadManInstance;
import l2f.gameserver.model.instances.FestivalMonsterInstance;
import l2f.gameserver.model.instances.MinionInstance;
import l2f.gameserver.model.instances.MonsterInstance;
import l2f.gameserver.model.instances.RaidBossInstance;
import l2f.gameserver.model.instances.TamedBeastInstance;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;
import l2f.gameserver.templates.npc.NpcTemplate;
import npc.model.QueenAntLarvaInstance;

public class HuntersGuild extends Functions implements ScriptFile, IVoicedCommandHandler, OnDeathListener
{
	private static final String[] _commandList = new String[]
	{
		"gettask",
		"declinetask"
	};
	private static final Logger _log = LoggerFactory.getLogger(HuntersGuild.class);

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
		{
			return;
		}
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		_log.info("Loaded Event: Bounty Hunters Guild");
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	private static boolean checkTarget(NpcTemplate npc)
	{
		if (!npc.isInstanceOf(MonsterInstance.class) || (npc.rewardExp == 0) || npc.isInstanceOf(RaidBossInstance.class) || npc.isInstanceOf(QueenAntLarvaInstance.class))
		{
			return false;
		}
		if (npc.isInstanceOf(npc.model.SquashInstance.class) || npc.isInstanceOf(MinionInstance.class) || npc.isInstanceOf(FestivalMonsterInstance.class) || npc.isInstanceOf(TamedBeastInstance.class))
		{
			return false;
		}
		if (npc.isInstanceOf(DeadManInstance.class) || npc.isInstanceOf(ChestInstance.class) || npc.title.contains("Quest Monster") || (GameObjectsStorage.getByNpcId(npc.getNpcId()) == null))
		{
			return false;
		}
		return true;
	}

	public void getTask(Player player, int id)
	{
		if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
		{
			return;
		}
		NpcTemplate target;
		double mod = 1.;
		if (id == 0)
		{
			List<NpcTemplate> monsters = NpcHolder.getInstance().getAllOfLevel(player.getLevel());
			if (monsters == null || monsters.isEmpty())
			{
				show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
				return;
			}
			List<NpcTemplate> targets = new ArrayList<NpcTemplate>();
			for (NpcTemplate npc : monsters)
			{
				if (checkTarget(npc))
				{
					targets.add(npc);
				}
			}
			if (targets.isEmpty())
			{
				show(new CustomMessage("scripts.events.bountyhunters.NoTargets", player), player);
				return;
			}
			target = targets.get(Rnd.get(targets.size()));
		}
		else
		{
			target = NpcHolder.getInstance().getTemplate(id);
			if (target == null || !checkTarget(target))
			{
				show(new CustomMessage("scripts.events.bountyhunters.WrongTarget", player), player);
				return;
			}
			if (player.getLevel() - target.level > 5)
			{
				show(new CustomMessage("scripts.events.bountyhunters.TooEasy", player), player);
				return;
			}
			mod = 0.5 * (10 + target.level - player.getLevel()) / 10.;
		}

		int mobcount = target.level + Rnd.get(25, 50);
		player.setVar("bhMonstersId", String.valueOf(target.getNpcId()), -1);
		player.setVar("bhMonstersNeeded", String.valueOf(mobcount), -1);
		player.setVar("bhMonstersKilled", "0", -1);

		int fails = player.getVar("bhfails") == null ? 0 : Integer.parseInt(player.getVar("bhfails")) * 5;
		int success = player.getVar("bhsuccess") == null ? 0 : Integer.parseInt(player.getVar("bhsuccess")) * 5;

		double reputation = Math.min(Math.max((100 + success - fails) / 100., .25), 2.) * mod;

		long adenarewardvalue = Math.round((target.level * Math.max(Math.log(target.level), 1) * 10 + Math.max((target.level - 60) * 33, 0) + Math.max((target.level - 65) * 50, 0)) * target.rateHp * mobcount
					* Config.RATE_DROP_ADENA * player.getRateAdena() * reputation * .15);
		if (Rnd.chance(30)) // Адена, 30% случаев
		{
			player.setVar("bhRewardId", "57", -1);
			player.setVar("bhRewardCount", String.valueOf(adenarewardvalue), -1);
		}
		else
		{ // Кристаллы, 70% случаев
			int crystal = 0;
			if (target.level <= 39)
			{
				crystal = 1458; // D
			}
			else if (target.level <= 51)
			{
				crystal = 1459; // C
			}
			else if (target.level <= 60)
			{
				crystal = 1460; // B
			}
			else if (target.level <= 75)
			{
				crystal = 1461; // A
			}
			else
			{
				crystal = 1462; // S
			}
			player.setVar("bhRewardId", String.valueOf(crystal), -1);
			player.setVar("bhRewardCount", String.valueOf(adenarewardvalue / ItemHolder.getInstance().getTemplate(crystal).getReferencePrice()), -1);
		}
		show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", player).addNumber(mobcount).addString(target.name), player);
	}

	@Override
	public void onDeath(Creature cha, Creature killer)
	{
		if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
		{
			return;
		}
		if (cha.isMonster() && !cha.isRaid() && killer != null && killer.getPlayer() != null && killer.getPlayer().getVar("bhMonstersId") != null
					&& Integer.parseInt(killer.getPlayer().getVar("bhMonstersId")) == cha.getNpcId())
		{
			int count = Integer.parseInt(killer.getPlayer().getVar("bhMonstersKilled")) + 1;
			killer.getPlayer().setVar("bhMonstersKilled", String.valueOf(count), -1);
			int needed = Integer.parseInt(killer.getPlayer().getVar("bhMonstersNeeded"));
			if (count >= needed)
			{
				doReward(killer.getPlayer());
			}
			else
			{
				sendMessage(new CustomMessage("scripts.events.bountyhunters.NotifyKill", killer.getPlayer()).addNumber(needed - count), killer.getPlayer());
			}
		}
	}

	private static void doReward(Player player)
	{
		if (!Config.EVENT_BOUNTY_HUNTERS_ENABLED)
		{
			return;
		}
		int rewardid = Integer.parseInt(player.getVar("bhRewardId"));
		long rewardcount = Long.parseLong(player.getVar("bhRewardCount"));
		player.unsetVar("bhMonstersId");
		player.unsetVar("bhMonstersNeeded");
		player.unsetVar("bhMonstersKilled");
		player.unsetVar("bhRewardId");
		player.unsetVar("bhRewardCount");
		if (player.getVar("bhsuccess") != null)
		{
			player.setVar("bhsuccess", String.valueOf(Integer.parseInt(player.getVar("bhsuccess")) + 1), -1);
		}
		else
		{
			player.setVar("bhsuccess", "1", -1);
		}
		addItem(player, rewardid, rewardcount, "HuntersGuild");
		show(new CustomMessage("scripts.events.bountyhunters.TaskCompleted", player).addNumber(rewardcount).addItemName(rewardid), player);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar == null || !Config.EVENT_BOUNTY_HUNTERS_ENABLED)
		{
			return false;
		}
		if (activeChar.getLevel() < 20)
		{
			sendMessage(new CustomMessage("scripts.events.bountyhunters.TooLowLevel", activeChar), activeChar);
			return true;
		}
		if (command.equalsIgnoreCase("gettask"))
		{
			if (activeChar.getVar("bhMonstersId") != null)
			{
				int mobid = Integer.parseInt(activeChar.getVar("bhMonstersId"));
				int mobcount = Integer.parseInt(activeChar.getVar("bhMonstersNeeded")) - Integer.parseInt(activeChar.getVar("bhMonstersKilled"));
				show(new CustomMessage("scripts.events.bountyhunters.TaskGiven", activeChar).addNumber(mobcount).addString(NpcHolder.getInstance().getTemplate(mobid).name), activeChar);
				return true;
			}
			int id = 0;
			if (target != null && target.trim().matches("[\\d]{1,9}"))
			{
				id = Integer.parseInt(target);
			}
			getTask(activeChar, id);
			return true;
		}
		if (command.equalsIgnoreCase("declinetask"))
		{
			if (activeChar.getVar("bhMonstersId") == null)
			{
				sendMessage(new CustomMessage("scripts.events.bountyhunters.NoTask", activeChar), activeChar);
				return true;
			}
			activeChar.unsetVar("bhMonstersId");
			activeChar.unsetVar("bhMonstersNeeded");
			activeChar.unsetVar("bhMonstersKilled");
			activeChar.unsetVar("bhRewardId");
			activeChar.unsetVar("bhRewardCount");
			if (activeChar.getVar("bhfails") != null)
			{
				activeChar.setVar("bhfails", String.valueOf(Integer.parseInt(activeChar.getVar("bhfails")) + 1), -1);
			}
			else
			{
				activeChar.setVar("bhfails", "1", -1);
			}
			show(new CustomMessage("scripts.events.bountyhunters.TaskCanceled", activeChar), activeChar);
			return true;
		}
		return false;
	}
}