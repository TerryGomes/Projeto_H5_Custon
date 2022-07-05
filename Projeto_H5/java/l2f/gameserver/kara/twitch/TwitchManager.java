/*
 * Copyright (C) 2004-2018 L2J Server
 * This file is part of L2J Server.
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2f.gameserver.kara.twitch;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;

import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.data.xml.holder.ItemHolder;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.NpcHtmlMessage;
import l2f.gameserver.utils.ItemFunctions;

/**
 * @author Kara`
 */
public class TwitchManager
{
	public TwitchManager()
	{
		init();
	}

	private void init()
	{
		/** Register voice comamnd handler */
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new IVoicedCommandHandler()
		{
			private final String[] VOICE_COMMAND =
			{
				Config.TWITCH_VOICE
			};

			@Override
			public boolean useVoicedCommand(String command, Player activeChar, String params)
			{
				if (command.equals(VOICE_COMMAND[0]))
				{
					NpcHtmlMessage html = new NpcHtmlMessage(0);

					String htm = "twitch.htm";

					if (activeChar.getTwitch() != null)
					{
						htm = "twitch - Already.htm";
					}

					html.setFile("mods/" + htm);
					html.replace("%minutes%", Config.TWITCH_MIN_MINUTE);
					html.replace("%rewards%", buildReward());
					activeChar.sendPacket(html);
				}
				return false;
			}

			private String buildReward()
			{
				StringBuilder sb = new StringBuilder();

				for (int[] i : Config.TWITCH_REWARD)
				{
					sb.append("<font color=\"d9b330\">" + i[1] + " " + ItemHolder.getInstance().getTemplate(i[0]).getName() + "</font><br1>");
				}

				return sb.toString();
			}

			@Override
			public String[] getVoicedCommandList()
			{
				return VOICE_COMMAND;
			}
		});

		/** Initialize threadpool */
		ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> GameObjectsStorage.getAllPlayers().stream().filter(player -> player.getTwitch() != null).forEach(player ->
		{
			final Twitch twitch = new Twitch();
			twitch.setClientId(Config.TWITCH_TOKEN);

			twitch.streams().get(player.getTwitch(), new StreamResponseHandler()
			{
				@Override
				public void onFailure(Throwable arg0)
				{

				}

				@Override
				public void onFailure(int arg0, String arg1, String arg2)
				{

				}

				@Override
				public void onSuccess(Stream stream)
				{
					if (stream == null)
					{
						return;
					}

					if ((Config.TWITCH_MIN_VIEWERS > 0) && (stream.getViewers() < Config.TWITCH_MIN_VIEWERS))
					{
						player.sendMessage("Your stream need " + (Config.TWITCH_MIN_VIEWERS - stream.getViewers()) + " more.");
					}
					else if (!stream.getGame().equalsIgnoreCase(Config.TWITCH_GAME_NAME))
					{
						player.sendMessage("Your stream game is not " + Config.TWITCH_GAME_NAME);
					}
					else if (!stream.getChannel().getStatus().contains(Config.TWITCH_SERVER_NAME))
					{
						player.sendMessage("Your stream does not contain" + Config.TWITCH_SERVER_NAME + " in title.");
					}
					else if ((stream.getCreatedAt().getTime() - (Config.TWITCH_MIN_MINUTE * 60 * 1000)) > System.currentTimeMillis())
					{
						for (int[] i : Config.TWITCH_REWARD)
						{
							ItemFunctions.addItem(player, i[0], i[1], true, "");
						}
					}
				}
			});
		}), Config.TWITCH_REWARD_EVERY * 60 * 1000, Config.TWITCH_REWARD_EVERY * 60 * 1000);
	}

	public static TwitchManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final TwitchManager _instance = new TwitchManager();
	}
}