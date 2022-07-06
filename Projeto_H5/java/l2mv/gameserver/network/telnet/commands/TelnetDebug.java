package l2mv.gameserver.network.telnet.commands;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import l2mv.gameserver.model.GameObject;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.loginservercon.AuthServerCommunication;
import l2mv.gameserver.network.telnet.TelnetCommand;
import l2mv.gameserver.network.telnet.TelnetCommandHolder;

public class TelnetDebug implements TelnetCommandHolder
{
	private Set<TelnetCommand> _commands = new LinkedHashSet<TelnetCommand>();

	public TelnetDebug()
	{
		_commands.add(new TelnetCommand("dumpnpc", "dnpc")
		{
			@Override
			public String getUsage()
			{
				return "dumpnpc";
			}

			@Override
			public String handle(String[] args)
			{
				StringBuilder sb = new StringBuilder();

				int total = 0;
				int maxId = 0, maxCount = 0;

				TIntObjectHashMap<List<NpcInstance>> npcStats = new TIntObjectHashMap<List<NpcInstance>>();

				for (GameObject obj : GameObjectsStorage.getAllObjects())
				{
					if (obj.isCreature())
					{
						if (obj.isNpc())
						{
							List<NpcInstance> list;
							NpcInstance npc = (NpcInstance) obj;
							int id = npc.getNpcId();

							if ((list = npcStats.get(id)) == null)
							{
								npcStats.put(id, list = new ArrayList<NpcInstance>());
							}

							list.add(npc);

							if (list.size() > maxCount)
							{
								maxId = id;
								maxCount = list.size();
							}

							total++;
						}
					}
				}

				sb.append("Total NPCs: ").append(total).append("\n");
				sb.append("Maximum NPC ID: ").append(maxId).append(" count : ").append(maxCount).append("\n");

				TIntObjectIterator<List<NpcInstance>> itr = npcStats.iterator();

				while (itr.hasNext())
				{
					itr.advance();
					int id = itr.key();
					List<NpcInstance> list = itr.value();
					sb.append("=== ID: ").append(id).append(" ").append(" Count: ").append(list.size()).append(" ===").append("\n");

					for (NpcInstance npc : list)
					{
						try
						{
							sb.append("AI: ");

							if (npc.hasAI())
							{
								sb.append(npc.getAI().getClass().getName());
							}
							else
							{
								sb.append("none");
							}

							sb.append(", ");

							if (npc.getReflectionId() > 0)
							{
								sb.append("ref: ").append(npc.getReflectionId());
								sb.append(" - ").append(npc.getReflection().getName());
							}

							sb.append("loc: ").append(npc.getLoc());
							sb.append(", ");
							sb.append("spawned: ");
							sb.append(npc.isVisible());
							sb.append("\n");
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}

				try
				{
					new File("stats").mkdir();
					FileUtils.writeStringToFile(new File("stats/NpcStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), sb.toString());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

				return "NPC stats saved.\n";
			}

		});

		_commands.add(new TelnetCommand("asrestart")
		{
			@Override
			public String getUsage()
			{
				return "asrestart";
			}

			@Override
			public String handle(String[] args)
			{
				AuthServerCommunication.getInstance().restart();

				return "Restarted.\n";
			}

		});
	}

	@Override
	public Set<TelnetCommand> getCommands()
	{
		return _commands;
	}
}