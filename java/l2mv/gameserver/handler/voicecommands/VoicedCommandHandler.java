package l2mv.gameserver.handler.voicecommands;

import java.util.HashMap;
import java.util.Map;

import l2mv.commons.data.xml.AbstractHolder;
import l2mv.gameserver.Config;
import l2mv.gameserver.handler.voicecommands.impl.ACP;
import l2mv.gameserver.handler.voicecommands.impl.AchievementsVoice;
import l2mv.gameserver.handler.voicecommands.impl.AntiGrief;
import l2mv.gameserver.handler.voicecommands.impl.Atod;
import l2mv.gameserver.handler.voicecommands.impl.Away;
import l2mv.gameserver.handler.voicecommands.impl.BuffStoreVoice;
import l2mv.gameserver.handler.voicecommands.impl.CWHPrivileges;
import l2mv.gameserver.handler.voicecommands.impl.Cfg;
import l2mv.gameserver.handler.voicecommands.impl.CombineTalismans;
import l2mv.gameserver.handler.voicecommands.impl.Debug;
//import l2mv.gameserver.handler.voicecommands.impl.Donate;
import l2mv.gameserver.handler.voicecommands.impl.DressMe;
import l2mv.gameserver.handler.voicecommands.impl.FacebookVoice;
import l2mv.gameserver.handler.voicecommands.impl.FindParty;
import l2mv.gameserver.handler.voicecommands.impl.Hellbound;
import l2mv.gameserver.handler.voicecommands.impl.Help;
import l2mv.gameserver.handler.voicecommands.impl.ItemLogsVoice;
import l2mv.gameserver.handler.voicecommands.impl.LockPc;
import l2mv.gameserver.handler.voicecommands.impl.NpcSpawn;
import l2mv.gameserver.handler.voicecommands.impl.Offline;
import l2mv.gameserver.handler.voicecommands.impl.Online;
import l2mv.gameserver.handler.voicecommands.impl.Password;
import l2mv.gameserver.handler.voicecommands.impl.Ping;
import l2mv.gameserver.handler.voicecommands.impl.PollVoice;
import l2mv.gameserver.handler.voicecommands.impl.Relocate;
import l2mv.gameserver.handler.voicecommands.impl.Repair;
import l2mv.gameserver.handler.voicecommands.impl.ReportBot;
import l2mv.gameserver.handler.voicecommands.impl.RuVotePanel;
import l2mv.gameserver.handler.voicecommands.impl.Security;
import l2mv.gameserver.handler.voicecommands.impl.ServerInfo;
import l2mv.gameserver.handler.voicecommands.impl.SiegeVoice;
import l2mv.gameserver.handler.voicecommands.impl.StreamVoice;
import l2mv.gameserver.handler.voicecommands.impl.Teleport;
import l2mv.gameserver.handler.voicecommands.impl.TournamentVoice;
import l2mv.gameserver.handler.voicecommands.impl.VoiceGmEvent;
import l2mv.gameserver.handler.voicecommands.impl.VoteReward;
import l2mv.gameserver.handler.voicecommands.impl.Wedding;
import l2mv.gameserver.handler.voicecommands.impl.WhoAmI;
import l2mv.gameserver.handler.voicecommands.impl.res;
import l2mv.gameserver.handler.voicecommands.impl.BotReport.ReportCommand;
import l2mv.gameserver.masteriopack.rankpvpsystem.VoicedCommandHandlerPvpInfo;

public class VoicedCommandHandler extends AbstractHolder
{
	private static final VoicedCommandHandler _instance = new VoicedCommandHandler();

	public static VoicedCommandHandler getInstance()
	{
		return _instance;
	}

	private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<String, IVoicedCommandHandler>();

	private VoicedCommandHandler()
	{
		registerVoicedCommandHandler(new Away());
		registerVoicedCommandHandler(new Atod());
		registerVoicedCommandHandler(new AntiGrief());
		registerVoicedCommandHandler(new CombineTalismans());
		registerVoicedCommandHandler(new Cfg());
		registerVoicedCommandHandler(new Help());
		registerVoicedCommandHandler(new Online());
		registerVoicedCommandHandler(new Hellbound());
		registerVoicedCommandHandler(new Teleport());
		registerVoicedCommandHandler(new PollVoice());
		registerVoicedCommandHandler(new CWHPrivileges());
		registerVoicedCommandHandler(new Offline());
		registerVoicedCommandHandler(new Password());
		registerVoicedCommandHandler(new Relocate());
		registerVoicedCommandHandler(new Repair());
		registerVoicedCommandHandler(new ServerInfo());
		registerVoicedCommandHandler(new Wedding());
		registerVoicedCommandHandler(new WhoAmI());
		registerVoicedCommandHandler(new Debug());
		registerVoicedCommandHandler(new Security());
		registerVoicedCommandHandler(new ReportBot());
		registerVoicedCommandHandler(new res());
		registerVoicedCommandHandler(new FindParty());
		registerVoicedCommandHandler(new Ping());
		registerVoicedCommandHandler(new LockPc());
		registerVoicedCommandHandler(new NpcSpawn());
		// registerVoicedCommandHandler(new Donate());

		if (Config.ENABLE_ACHIEVEMENTS)
		{
			registerVoicedCommandHandler(new AchievementsVoice());
		}

		// Synerge
		registerVoicedCommandHandler(new ReportCommand());
		registerVoicedCommandHandler(new SiegeVoice());
		registerVoicedCommandHandler(new BuffStoreVoice());
		registerVoicedCommandHandler(new VoiceGmEvent());
		registerVoicedCommandHandler(new ACP());
		registerVoicedCommandHandler(new ItemLogsVoice());
		registerVoicedCommandHandler(new StreamVoice());
		registerVoicedCommandHandler(new FacebookVoice());
		registerVoicedCommandHandler(new DressMe());
		registerVoicedCommandHandler(new VoteReward());
		registerVoicedCommandHandler(new RuVotePanel());
		registerVoicedCommandHandler(new TournamentVoice());
		registerVoicedCommandHandler(new VoicedCommandHandlerPvpInfo());
	}

	public void registerVoicedCommandHandler(IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		for (String element : ids)
		{
			_datatable.put(element, handler);
		}
	}

	public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand)
	{
		String command = voicedCommand;
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}

		return _datatable.get(command);
	}

	@Override
	public int size()
	{
		return _datatable.size();
	}

	@Override
	public void clear()
	{
		_datatable.clear();
	}
}
