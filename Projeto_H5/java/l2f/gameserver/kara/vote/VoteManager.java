package l2f.gameserver.kara.vote;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.tables.SkillTable;
import l2f.gameserver.utils.DocumentParser;

/**
 * @author Kara`
 */
public class VoteManager extends DocumentParser
{
	public final Map<Site, SiteTemplate> _holder = new HashMap<>();
	public final Map<String, Map<String, Long>> _storagedData = new HashMap<>();

	public VoteManager()
	{
		load();
	}

	@Override
	protected void parseDocument()
	{
		for (Node n = getCurrentDocument().getFirstChild(); n != null; n = n.getNextSibling())
		{
			if (n.getNodeName().equals("list"))
			{
				for (Node list = n.getFirstChild(); list != null; list = list.getNextSibling())
				{
					if ("site".equals(list.getNodeName()))
					{
						SiteTemplate template = new SiteTemplate();

						if (list.getAttributes().getNamedItem("api") != null)
						{
							template.setAPI(list.getAttributes().getNamedItem("api").getNodeValue());
						}

						if (list.getAttributes().getNamedItem("id") != null)
						{
							template.setServerId(list.getAttributes().getNamedItem("id").getNodeValue());
						}

						if (list.getAttributes().getNamedItem("user") != null)
						{
							template.setUser(list.getAttributes().getNamedItem("user").getNodeValue());
						}

						for (Node node = list.getFirstChild(); node != null; node = node.getNextSibling())
						{
							if ("settings".equals(node.getNodeName()))
							{
								for (Node settings = node.getFirstChild(); settings != null; settings = settings.getNextSibling())
								{
									if ("set".equals(settings.getNodeName()))
									{
										if (settings.getAttributes().getNamedItem("hoursToVoteAgain") != null)
										{
											template.setHourToVote(Integer.parseInt(settings.getAttributes().getNamedItem("hoursToVoteAgain").getNodeValue()));
										}
									}
								}
							}
							if ("rewards".equals(node.getNodeName()))
							{
								for (Node rew = node.getFirstChild(); rew != null; rew = rew.getNextSibling())
								{
									if ("reward".equals(rew.getNodeName()))
									{
										VoteReward reward = new VoteReward(Integer.parseInt(rew.getAttributes().getNamedItem("itemId").getNodeValue()), Integer.parseInt(rew.getAttributes().getNamedItem("count").getNodeValue()));

										if (rew.getAttributes().getNamedItem("chance") != null)
										{
											reward.setChance(Integer.parseInt(rew.getAttributes().getNamedItem("chance").getNodeValue()));
										}
										if (rew.getAttributes().getNamedItem("enchant") != null)
										{
											reward.setChance(Integer.parseInt(rew.getAttributes().getNamedItem("enchant").getNodeValue()));
										}

										template.addReward(reward);
									}

									if ("buffs".equals(node.getNodeName()))
									{
										for (Node bf = rew.getFirstChild(); bf != null; bf = bf.getNextSibling())
										{
											if ("buff".equals(bf.getNodeName()))
											{
												VoteBuff buff = new VoteBuff(SkillTable.getInstance().getInfo(Integer.parseInt(bf.getAttributes().getNamedItem("id").getNodeValue()), Integer.parseInt(bf.getAttributes().getNamedItem("level").getNodeValue())));

												if (bf.getAttributes().getNamedItem("chance") != null)
												{
													buff.setChance(Integer.parseInt(bf.getAttributes().getNamedItem("chance").getNodeValue()));
												}

												template.addBuff(buff);
											}
										}
									}
								}
							}
						}
						_holder.put(Site.valueOf(list.getAttributes().getNamedItem("name").getNodeValue().toString().toUpperCase()), template);
						System.out.println("[VoteManager] Loaded site template: " + list.getAttributes().getNamedItem("name").getNodeValue());
					}
				}
			}
		}
	}

	@Override
	public void load()
	{
		_holder.clear();
		_storagedData.clear();
		loadVote();
		parseFile(new File("config/voteReward.xml"));
	}

	public SiteTemplate getSite(Site site)
	{
		return _holder.get(site);
	}

	public boolean canGetReward(String HWID, String site)
	{
		if (_storagedData.containsKey(HWID))
		{
			if (_storagedData.get(HWID).containsKey(site))
			{
				return (_storagedData.get(HWID).get(site) < System.currentTimeMillis());
			}
		}
		return true;
	}

	public void loadVote()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement("SELECT * FROM last_vote_kara");
			ResultSet rs = ps.executeQuery();

			while (rs.next())
			{
				String hwid = rs.getString("hwid");
				String site = rs.getString("site");
				long timeStamp = rs.getLong("lastReward");

				addVotedPlayer(hwid, site, timeStamp);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void storeVote()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement("DELETE FROM last_vote_kara");
			ps.execute();
			ps.close();

			ps = con.prepareStatement("INSERT INTO last_vote_kara VALUES(?,?,?)");

			for (Entry<String, Map<String, Long>> i : _storagedData.entrySet())
			{
				for (Entry<String, Long> sub : i.getValue().entrySet())
				{
					ps.setString(1, i.getKey());
					ps.setString(2, sub.getKey());
					ps.setLong(3, sub.getValue());
					ps.execute();
					ps.clearParameters();
				}
			}

			ps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addVotedPlayer(String HWID, String site, long timestamp)
	{
		if (!_storagedData.containsKey(HWID))
		{
			_storagedData.put(HWID, new HashMap<>());
		}

		_storagedData.get(HWID).put(site, timestamp);
	}

	public static VoteManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final VoteManager _instance = new VoteManager();
	}
}