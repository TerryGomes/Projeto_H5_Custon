package l2mv.gameserver.network.telnet.commands;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import l2mv.gameserver.GameServer;
import l2mv.gameserver.Shutdown;
import l2mv.gameserver.network.telnet.TelnetCommand;
import l2mv.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetServer implements TelnetCommandHolder
{
	private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

	public TelnetServer()
	{
		this._commands.add(new TelnetCommand("version", "ver")
		{
			@Override
			public String getUsage()
			{
				return "version";
			}

			@Override
			public String handle(String[] args)
			{
				return "Rev." + GameServer.getInstance().getVersion().getRevisionNumber() + " Builded : " + GameServer.getInstance().getVersion().getBuildDate() + "\n";
			}
		});

		this._commands.add(new TelnetCommand("uptime")
		{
			@Override
			public String getUsage()
			{
				return "uptime";
			}

			@Override
			public String handle(String[] args)
			{
				return DurationFormatUtils.formatDurationHMS(ManagementFactory.getRuntimeMXBean().getUptime()) + "\n";
			}
		});

		this._commands.add(new TelnetCommand("restart")
		{
			@Override
			public String getUsage()
			{
				return "restart <seconds>|now>";
			}

			@Override
			public String handle(String[] args)
			{
				if (args.length == 0)
				{
					return null;
				}

				StringBuilder sb = new StringBuilder();

				if (NumberUtils.isNumber(args[0]))
				{
					final int val = NumberUtils.toInt(args[0]);
					Shutdown.getInstance().schedule(val, Shutdown.ShutdownMode.RESTART, false);
					sb.append("Server will restart in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
					sb.append("Type \"abort\" to abort restart!\n");
				}
				else if (args[0].equalsIgnoreCase("now"))
				{
					sb.append("Server will restart now!\n");
					Shutdown.getInstance().schedule(0, Shutdown.ShutdownMode.RESTART, false);
				}
				else
				{
					String[] hhmm = args[0].split(":");

					Calendar date = Calendar.getInstance();
					Calendar now = Calendar.getInstance();

					date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hhmm[0]));
					date.set(Calendar.MINUTE, hhmm.length > 1 ? Integer.parseInt(hhmm[1]) : 0);
					date.set(Calendar.SECOND, 0);
					date.set(Calendar.MILLISECOND, 0);
					if (date.before(now))
					{
						date.roll(Calendar.DAY_OF_MONTH, true);
					}

					final int seconds = (int) (date.getTimeInMillis() / 1000L - now.getTimeInMillis() / 1000L);
					Shutdown.getInstance().schedule(seconds, Shutdown.ShutdownMode.RESTART, false);
					sb.append("Server will restart in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
					sb.append("Type \"abort\" to abort restart!\n");
				}

				return sb.toString();
			}
		});

		this._commands.add(new TelnetCommand("shutdown")
		{
			@Override
			public String getUsage()
			{
				return "shutdown <seconds>|now|<hh:mm>";
			}

			@Override
			public String handle(String[] args)
			{
				if (args.length == 0)
				{
					return null;
				}

				StringBuilder sb = new StringBuilder();

				if (NumberUtils.isNumber(args[0]))
				{
					final int val = NumberUtils.toInt(args[0]);
					Shutdown.getInstance().schedule(val, Shutdown.ShutdownMode.SHUTDOWN, false);
					sb.append("Server will shutdown in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
					sb.append("Type \"abort\" to abort shutdown!\n");
				}
				else if (args[0].equalsIgnoreCase("now"))
				{
					sb.append("Server will shutdown now!\n");
					Shutdown.getInstance().schedule(0, Shutdown.ShutdownMode.SHUTDOWN, false);
				}
				else
				{
					String[] hhmm = args[0].split(":");

					Calendar date = Calendar.getInstance();
					Calendar now = Calendar.getInstance();

					date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hhmm[0]));
					date.set(Calendar.MINUTE, hhmm.length > 1 ? Integer.parseInt(hhmm[1]) : 0);
					date.set(Calendar.SECOND, 0);
					date.set(Calendar.MILLISECOND, 0);
					if (date.before(now))
					{
						date.roll(Calendar.DAY_OF_MONTH, true);
					}

					final int seconds = (int) (date.getTimeInMillis() / 1000L - now.getTimeInMillis() / 1000L);
					Shutdown.getInstance().schedule(seconds, Shutdown.ShutdownMode.SHUTDOWN, false);
					sb.append("Server will shutdown in ").append(Shutdown.getInstance().getSeconds()).append(" seconds!\n");
					sb.append("Type \"abort\" to abort shutdown!\n");
				}

				return sb.toString();
			}
		});

		this._commands.add(new TelnetCommand("abort")
		{

			@Override
			public String getUsage()
			{
				return "abort";
			}

			@Override
			public String handle(String[] args)
			{
				Shutdown.getInstance().cancel();
				return "Aborted.\n";
			}

		});
	}

	@Override
	public Set<TelnetCommand> getCommands()
	{
		return this._commands;
	}
}
