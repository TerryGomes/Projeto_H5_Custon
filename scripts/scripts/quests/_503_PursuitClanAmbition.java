package quests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Config;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.World;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.pledge.Clan;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

public class _503_PursuitClanAmbition extends Quest implements ScriptFile
{
	// Items

	// first part
	private final int G_Let_Martien = 3866;
	private final int Th_Wyrm_Eggs = 3842;
	private final int Drake_Eggs = 3841;
	private final int Bl_Wyrm_Eggs = 3840;
	private final int Mi_Drake_Eggs = 3839;
	private final int Brooch = 3843;
	private final int Bl_Anvil_Coin = 3871;

	// second Part
	private final int G_Let_Balthazar = 3867;
	private final int Recipe_Spiteful_Soul_Energy = 14854;
	private final int Spiteful_Soul_Energy = 14855;
	private final int Spiteful_Soul_Vengeance = 14856;

	// third part
	private final int G_Let_Rodemai = 3868;
	private final int Imp_Keys = 3847;
	private final int Scepter_Judgement = 3869;

	// the final item
	private final int Proof_Aspiration = 3870;

	private final int[] EggList = new int[]
	{
		Mi_Drake_Eggs,
		Bl_Wyrm_Eggs,
		Drake_Eggs,
		Th_Wyrm_Eggs
	};

	// NPCs
	private final int Gustaf = 30760;
	private final int Martien = 30645;
	private final int Athrea = 30758;
	private final int Kalis = 30759;
	private final int Fritz = 30761;
	private final int Lutz = 30762;
	private final int Kurtz = 30763;
	private final int Kusto = 30512;
	private final int Balthazar = 30764;
	private final int Rodemai = 30868;
	private final int Coffer = 30765;
	private final int Cleo = 30766;

	// MOBS
	private final int ThunderWyrm1 = 20282;
	private final int ThunderWyrm2 = 20243;
	private final int Drake1 = 20137;
	private final int Drake2 = 20285;
	private final int BlitzWyrm = 27178;
	private final int SpitefulSoulLeader = 20974;
	private final int GraveGuard = 20668;
	private final int GraveKeymaster = 27179;
	private final int ImperialGravekeeper = 27181;

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	public _503_PursuitClanAmbition()
	{
		super(PARTY_ALL);

		addStartNpc(Gustaf);

		addTalkId(Martien);
		addTalkId(Athrea);
		addTalkId(Kalis);
		addTalkId(Fritz);
		addTalkId(Lutz);
		addTalkId(Kurtz);
		addTalkId(Kusto);
		addTalkId(Balthazar);
		addTalkId(Rodemai);
		addTalkId(Coffer);
		addTalkId(Cleo);

		addKillId(ThunderWyrm1, ThunderWyrm2, Drake1, Drake2, BlitzWyrm, SpitefulSoulLeader, GraveGuard, GraveKeymaster, ImperialGravekeeper);

		addAttackId(ImperialGravekeeper);

		for (int i = 3839; i <= 3848; i++)
		{
			addQuestItem(i);
		}

		for (int i = 3866; i <= 3869; i++)
		{
			addQuestItem(i);
		}

		addQuestItem(Recipe_Spiteful_Soul_Energy, Spiteful_Soul_Energy, Spiteful_Soul_Vengeance);
	}

	public void suscribe_members(QuestState st)
	{
		int clan = st.getPlayer().getClan().getClanId();
		Connection con = null;
		PreparedStatement offline = null, insertion = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT obj_Id FROM characters WHERE clanid=? AND online=0");
			insertion = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)");
			offline.setInt(1, clan);
			rs = offline.executeQuery();
			while (rs.next())
			{
				int char_id = rs.getInt("obj_Id");
				try
				{
					insertion.setInt(1, char_id);
					insertion.setString(2, getName());
					insertion.setString(3, "<state>");
					insertion.setString(4, "Started");
					insertion.executeUpdate();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(insertion);
			DbUtils.closeQuietly(con, offline, rs);
		}
	}

	public void offlineMemberExit(QuestState st)
	{
		int clan = st.getPlayer().getClan().getClanId();

		Connection con = null;
		PreparedStatement offline = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("DELETE FROM character_quests WHERE name=? AND char_id IN (SELECT obj_id FROM characters WHERE clanId=? AND online=0)");
			offline.setString(1, getName());
			offline.setInt(2, clan);
			offline.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, offline);
		}
	}

	public Player getLeader(QuestState st)
	{
		Player player = st.getPlayer();
		if (player == null)
		{
			return null;
		}
		Clan clan = player.getClan();
		if (clan == null)
		{
			return null;
		}
		return clan.getLeader().getPlayer();
	}

	// returns leaders quest cond, if he is offline will read out of database :)
	public int getLeaderVar(QuestState st, String var)
	{
		final boolean cond = "cond".equalsIgnoreCase(var);
		try
		{
			Player leader = getLeader(st);
			if (leader != null)
			{
				if (cond)
				{
					return leader.getQuestState(getName()).getCond();
				}
				else
				{
					return leader.getQuestState(getName()).getInt(var);
				}
			}
		}
		catch (Exception e)
		{
			return -1;
		}

		Clan clan = st.getPlayer().getClan();

		if (clan == null)
		{
			return -1;
		}

		int leaderId = clan.getLeaderId();

		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("SELECT value FROM character_quests WHERE char_id=? AND var=? AND name=?");
			offline.setInt(1, leaderId);
			offline.setString(2, var);
			offline.setString(3, getName());
			int val = -1;
			rs = offline.executeQuery();
			if (rs.next())
			{
				val = rs.getInt("value");
				if (cond && (val & 0x80000000) != 0)
				{
					val &= 0x7fffffff;
					for (int i = 1; i < 32; i++)
					{
						val = (val >> 1);
						if (val == 0)
						{
							return i;
						}
					}
				}
			}
			return val;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
		finally
		{
			DbUtils.closeQuietly(con, offline, rs);
		}
	}

	// set's leaders quest cond, if he is offline will read out of database :)
	// for now, if the leader is not logged in, this assumes that the variable
	// has already been inserted once (initialized, in some sense).
	public void setLeaderVar(QuestState st, String var, String value)
	{
		Clan clan = st.getPlayer().getClan();
		if (clan == null)
		{
			return;
		}
		Player leader = clan.getLeader().getPlayer();
		if (leader != null)
		{
			if ("cond".equalsIgnoreCase(var))
			{
				leader.getQuestState(getName()).setCond(Integer.parseInt(value));
			}
			else
			{
				leader.getQuestState(getName()).set(var, value);
			}
		}
		else // для прямой записи в базу cond не корректируем, при загрузке сконвертится само.
		{
			int leaderId = st.getPlayer().getClan().getLeaderId();

			Connection con = null;
			PreparedStatement offline = null;
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				offline = con.prepareStatement("UPDATE character_quests SET value=? WHERE char_id=? AND var=? AND name=?");
				offline.setString(1, value);
				offline.setInt(2, leaderId);
				offline.setString(3, var);
				offline.setString(4, getName());
				offline.executeUpdate();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				DbUtils.closeQuietly(con, offline);
			}
		}
	}

	public boolean checkEggs(QuestState st)
	{
		int count = 0;
		for (int item : EggList)
		{
			if (st.getQuestItemsCount(item) > 9)
			{
				count += 1;
			}
		}
		return count > 3;
	}

	public void giveItem(int item, long maxcount, QuestState st)
	{
		Player player = st.getPlayer();
		if (player == null)
		{
			return;
		}
		Player leader = getLeader(st);
		if ((leader == null) || (player.getDistance(leader) > Config.ALT_PARTY_DISTRIBUTION_RANGE))
		{
			return;
		}
		QuestState qs = leader.getQuestState(getClass());
		if (qs == null)
		{
			return;
		}
		long count = qs.getQuestItemsCount(item);
		if (count < maxcount)
		{
			qs.giveItems(item, 1);
			if (count == maxcount - 1)
			{
				qs.playSound(SOUND_MIDDLE);
			}
			else
			{
				qs.playSound(SOUND_ITEMGET);
			}
		}
	}

	public String exit503(boolean completed, QuestState st)
	{
		if (completed)
		{
			st.giveItems(Proof_Aspiration, 1);
			st.addExpAndSp(0, 250000);
			st.unset("cond");
			st.unset("Fritz");
			st.unset("Lutz");
			st.unset("Kurtz");
			st.unset("ImpGraveKeeper");
			st.exitCurrentQuest(false);
		}
		else
		{
			st.exitCurrentQuest(true);
		}
		st.takeItems(Scepter_Judgement, -1);
		try
		{
			List<Player> members = st.getPlayer().getClan().getOnlineMembers(0);
			for (Player player : members)
			{
				if (player == null)
				{
					continue;
				}
				QuestState qs = player.getQuestState(getName());
				if (qs != null)
				{
					qs.exitCurrentQuest(true);
				}
			}
			offlineMemberExit(st);
		}
		catch (Exception e)
		{
			return "You dont have any members in your Clan, so you can't finish the Pursuit of Aspiration";
		}
		return "Congratulations, you have finished the Pursuit of Clan Ambition";
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		// Events Gustaf
		if (event.equalsIgnoreCase("30760-08.htm"))
		{
			st.giveItems(G_Let_Martien, 1);
			st.setCond(1);
			st.set("Fritz", "1");
			st.set("Lutz", "1");
			st.set("Kurtz", "1");
			st.set("ImpGraveKeeper", "1");
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("30760-12.htm"))
		{
			st.giveItems(G_Let_Balthazar, 1);
			st.setCond(4);
		}
		else if (event.equalsIgnoreCase("30760-16.htm"))
		{
			st.giveItems(G_Let_Rodemai, 1);
			st.setCond(7);
		}
		else if (event.equalsIgnoreCase("30760-20.htm"))
		{
			exit503(true, st);
		}
		else if (event.equalsIgnoreCase("30760-22.htm"))
		{
			st.setCond(13);
		}
		else if (event.equalsIgnoreCase("30760-23.htm"))
		{
			exit503(true, st);
		}
		else if (event.equalsIgnoreCase("30645-03.htm"))
		{
			st.takeItems(G_Let_Martien, -1);
			st.setCond(2);
			suscribe_members(st);
			List<Player> members = st.getPlayer().getClan().getOnlineMembers(st.getPlayer().getObjectId());
			for (Player player : members)
			{
				newQuestState(player, STARTED);
			}
		}
		// Events Kurtz
		else if (event.equalsIgnoreCase("30763-03.htm"))
		{
			if (st.getInt("Kurtz") == 1)
			{
				htmltext = "30763-02.htm";
				st.giveItems(Mi_Drake_Eggs, 6);
				st.giveItems(Brooch, 1);
				st.set("Kurtz", "2");
			}
		}
		// Events Lutz
		else if (event.equalsIgnoreCase("30762-03.htm"))
		{
			int lutz = st.getInt("Lutz");
			if (lutz == 1)
			{
				htmltext = "30762-02.htm";
				st.giveItems(Mi_Drake_Eggs, 4);
				st.giveItems(Bl_Wyrm_Eggs, 3);
				st.set("Lutz", "2");
			}
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
		}
		// Events Fritz
		else if (event.equalsIgnoreCase("30761-03.htm"))
		{
			int fritz = st.getInt("Fritz");
			if (fritz == 1)
			{
				htmltext = "30761-02.htm";
				st.giveItems(Bl_Wyrm_Eggs, 3);
				st.set("Fritz", "2");
			}
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
			st.addSpawn(BlitzWyrm, npc.getLoc().x, npc.getLoc().y, npc.getLoc().z, Location.getRandomHeading(), 300, 120000);
		}
		// Events Kusto
		else if (event.equalsIgnoreCase("30512-03.htm"))
		{
			st.takeItems(Brooch, -1);
			st.giveItems(Bl_Anvil_Coin, 1);
			st.set("Kurtz", "3");
		}
		// Events Balthazar
		else if (event.equalsIgnoreCase("30764-03.htm"))
		{
			st.takeItems(G_Let_Balthazar, -1);
			st.setCond(5);
			st.set("Kurtz", "3");
		}
		else if (event.equalsIgnoreCase("30764-05.htm"))
		{
			st.takeItems(G_Let_Balthazar, -1);
			st.setCond(5);
		}
		else if (event.equalsIgnoreCase("30764-06.htm"))
		{
			st.takeItems(Bl_Anvil_Coin, -1);
			st.set("Kurtz", "4");
			st.giveItems(Recipe_Spiteful_Soul_Energy, 1);
		}
		// Events Rodemai
		else if (event.equalsIgnoreCase("30868-04.htm"))
		{
			st.takeItems(G_Let_Rodemai, -1);
			st.setCond(8);
		}
		else if (event.equalsIgnoreCase("30868-06a.htm"))
		{
			st.setCond(10);
		}
		else if (event.equalsIgnoreCase("30868-10.htm"))
		{
			st.setCond(12);
		}
		else if (event.equalsIgnoreCase("30766-04.htm"))
		{
			st.setCond(9);

			NpcInstance n = st.findTemplate(Cleo);
			if (n != null)
			{
				Functions.npcSay(n, "Blood and Honour");
			}

			n = st.findTemplate(Kalis);
			if (n != null)
			{
				Functions.npcSay(n, "Ambition and Power");
			}

			n = st.findTemplate(Athrea);
			if (n != null)
			{
				Functions.npcSay(n, "War and Death");
			}
		}
		else if (event.equalsIgnoreCase("30766-08.htm"))
		{
			st.takeItems(Scepter_Judgement, -1);
			exit503(false, st);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int id = st.getState();

		String htmltext = "noquest";
		boolean isLeader = st.getPlayer().isClanLeader();
		if (id == CREATED && npcId == Gustaf)
		{
			if (st.getPlayer().getClan() != null) // has Clan
			{
				if (isLeader) // check if player is clan leader
				{
					int clanLevel = st.getPlayer().getClan().getLevel();
					if (st.getQuestItemsCount(Proof_Aspiration) > 0) // if he has the proof
					// already, tell him
					// what to do now
					{
						htmltext = "30760-03.htm";
						st.exitCurrentQuest(true);
					}
					else if (clanLevel > 3)
					{ // if clanLevel > 3 you can take this quest,
						// because repeatable
						htmltext = "30760-04.htm";
					}
					else
					// if clanLevel < 4 you cant take it
					{
						htmltext = "30760-02.htm";
						st.exitCurrentQuest(true);
					}
				}
				else
				// player isnt a leader
				{
					htmltext = "30760-04t.htm";
					st.exitCurrentQuest(true);
				}
			}
			else
			// no Clan
			{
				htmltext = "30760-01.htm";
				st.exitCurrentQuest(true);
			}
			return htmltext;
		}
		else if (st.getPlayer().getClan() != null && st.getPlayer().getClan().getLevel() == 5)
		{ // player has level 5 clan already
			return "completed";
		}
		else
		// ######## Leader Area ######
		if (isLeader)
		{
			if (st.getCond() == 0)
			{
				st.setCond(1);
			}
			if (st.get("Kurtz") == null)
			{
				st.set("Kurtz", "1");
			}
			if (st.get("Lutz") == null)
			{
				st.set("Lutz", "1");
			}
			if (st.get("Fritz") == null)
			{
				st.set("Fritz", "1");
			}
			int cond = st.getCond();
			int kurtz = st.getInt("Kurtz");
			int lutz = st.getInt("Lutz");
			int fritz = st.getInt("Fritz");

			if (npcId == Gustaf)
			{
				switch (cond)
				{
				case 1:
					htmltext = "30760-09.htm";
					break;
				case 2:
					htmltext = "30760-10.htm";
					break;
				case 3:
					htmltext = "30760-11.htm";
					break;
				case 4:
					htmltext = "30760-13.htm";
					break;
				case 5:
					htmltext = "30760-14.htm";
					break;
				case 6:
					htmltext = "30760-15.htm";
					break;
				case 7:
					htmltext = "30760-17.htm";
					break;
				case 12:
					htmltext = "30760-19.htm";
					break;
				case 13:
					htmltext = "30760-24.htm";
					break;
				default:
					htmltext = "30760-18.htm";
					break;
				}
			}
			else if (npcId == Martien)
			{
				switch (cond)
				{
				case 1:
					htmltext = "30645-02.htm";
					break;
				case 2:
					if (checkEggs(st) && kurtz > 1 && lutz > 1 && fritz > 1)
					{
						htmltext = "30645-05.htm";
						st.setCond(3);
						for (int item : EggList)
						{
							st.takeItems(item, -1);
						}
					}
					else
					{
						htmltext = "30645-04.htm";
					}
					break;
				case 3:
					htmltext = "30645-07.htm";
					break;
				default:
					htmltext = "30645-08.htm";
					break;
				}
			}
			else if (npcId == Lutz && cond == 2)
			{
				htmltext = "30762-01.htm";
			}
			else if (npcId == Kurtz && cond == 2)
			{
				htmltext = "30763-01.htm";
			}
			else if (npcId == Fritz && cond == 2)
			{
				htmltext = "30761-01.htm";
			}
			else
			{
				switch (npcId)
				{
				case Kusto:
					if (kurtz == 1)
					{
						htmltext = "30512-01.htm";
					}
					else if (kurtz == 2)
					{
						htmltext = "30512-02.htm";
					}
					else
					{
						htmltext = "30512-04.htm";
					}
					break;
				case Balthazar:
					switch (cond)
					{
					case 4:
						if (kurtz > 2)
						{
							htmltext = "30764-04.htm";
						}
						else
						{
							htmltext = "30764-02.htm";
						}
						break;
					case 5:
						if (st.getQuestItemsCount(Spiteful_Soul_Energy) > 9)
						{
							htmltext = "30764-08.htm";
							st.takeItems(Spiteful_Soul_Energy, -1);
							st.takeItems(Brooch, -1);
							st.setCond(6);
						}
						else
						{
							htmltext = "30764-07.htm";
						}
						break;
					case 6:
						htmltext = "30764-09.htm";
						break;
					default:
						break;
					}
					break;
				case Rodemai:
					switch (cond)
					{
					case 7:
						htmltext = "30868-02.htm";
						break;
					case 8:
						htmltext = "30868-05.htm";
						break;
					case 9:
						htmltext = "30868-06.htm";
						break;
					case 10:
						htmltext = "30868-08.htm";
						break;
					case 11:
						htmltext = "30868-09.htm";
						break;
					case 12:
						htmltext = "30868-11.htm";
						break;
					default:
						break;
					}
					break;
				case Cleo:
					switch (cond)
					{
					case 8:
						htmltext = "30766-02.htm";
						break;
					case 9:
						htmltext = "30766-05.htm";
						break;
					case 10:
						htmltext = "30766-06.htm";
						break;
					case 11:
					case 12:
					case 13:
						htmltext = "30766-07.htm";
						break;
					default:
						break;
					}
					break;
				case Coffer:
					if (st.getCond() == 10)
					{
						if (st.getQuestItemsCount(Imp_Keys) < 6)
						{
							htmltext = "30765-03a.htm";
						}
						else if (st.getInt("ImpGraveKeeper") == 3)
						{
							htmltext = "30765-02.htm";
							st.setCond(11);
							st.takeItems(Imp_Keys, 6);
							st.giveItems(Scepter_Judgement, 1);
						}
						else
						{
							htmltext = "<html><head><body>(You and your Clan didn't kill the Imperial Gravekeeper by your own, do it try again.)</body></html>";
						}
					}
					else
					{
						htmltext = "<html><head><body>(You already have the Scepter of Judgement.)</body></html>";
					}
					break;
				case Kalis:
					htmltext = "30759-01.htm";
					break;
				case Athrea:
					htmltext = "30758-01.htm";
					break;
				default:
					break;
				}
			}
			return htmltext;
		}
		// ######## Member Area ######
		else
		{
			int cond = getLeaderVar(st, "cond");
			if (npcId == Martien && (cond == 1 || cond == 2 || cond == 3))
			{
				htmltext = "30645-01.htm";
			}
			else if (npcId == Rodemai)
			{
				if (cond == 9 || cond == 10)
				{
					htmltext = "30868-07.htm";
				}
				else if (cond == 7)
				{
					htmltext = "30868-01.htm";
				}
			}
			else if (npcId == Balthazar && cond == 4)
			{
				htmltext = "30764-01.htm";
			}
			else if (npcId == Cleo && cond == 8)
			{
				htmltext = "30766-01.htm";
			}
			else if (npcId == Kusto && cond > 2 && cond < 6)
			{
				htmltext = "30512-01a.htm";
			}
			else if (npcId == Coffer && cond == 10)
			{
				htmltext = "30765-01.htm";
			}
			else if (npcId == Gustaf)
			{
				switch (cond)
				{
				case 3:
					htmltext = "30760-11t.htm";
					break;
				case 4:
					htmltext = "30760-15t.htm";
					break;
				case 12:
					htmltext = "30760-19t.htm";
					break;
				case 13:
					htmltext = "30766-24t.htm";
					break;
				default:
					break;
				}
			}
			return htmltext;
		}
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		if (npc.getMaxHp() / 2 > npc.getCurrentHp())
		{
			if (Rnd.chance(4))
			{
				int ImpGraveKepperStat = getLeaderVar(st, "ImpGraveKeeper");
				if (ImpGraveKepperStat == 1)
				{
					for (int i = 1; i <= 4; i++)
					{
						st.addSpawn(27180, 120000);
					}
					setLeaderVar(st, "ImpGraveKeeper", "2");
				}
				else
				{
					List<Player> players = World.getAroundPlayers(npc, 900, 200);
					if (players.size() > 0)
					{
						Player player = players.get(Rnd.get(players.size()));
						if (player != null)
						{
							player.teleToLocation(185462, 20342, -3250);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = getLeaderVar(st, "cond");
		switch (cond)
		{
		case 2:
			switch (npcId)
			{
			case ThunderWyrm1:
				if (Rnd.chance(20))
				{
					giveItem(Th_Wyrm_Eggs, 10, st);
				}
				break;

			case ThunderWyrm2:
				if (Rnd.chance(15))
				{
					giveItem(Th_Wyrm_Eggs, 10, st);
				}
				break;
			case Drake1:
				if (Rnd.chance(20))
				{
					giveItem(Drake_Eggs, 10, st);
				}
				break;
			case Drake2:
				if (Rnd.chance(25))
				{
					giveItem(Drake_Eggs, 10, st);
				}
				break;
			case BlitzWyrm:
				giveItem(Bl_Wyrm_Eggs, 10, st);
				break;
			}
			break;
		case 5:
			if (npcId == SpitefulSoulLeader && Rnd.chance(25))
			{
				if (Rnd.chance(50))
				{
					if (getLeaderVar(st, "Kurtz") < 4)
					{
						return null;
					}
					giveItem(Spiteful_Soul_Vengeance, 40, st);
				}
				else
				{
					giveItem(Spiteful_Soul_Energy, 10, st);
				}
			}
			break;
		case 10:
			switch (npcId)
			{
			case GraveGuard:
				if (Rnd.chance(15))
				{
					st.addSpawn(GraveKeymaster, 120000);
				}
				break;
			case GraveKeymaster:
				if (Rnd.chance(80))
				{
					giveItem(Imp_Keys, 6, st);
				}
				break;
			case ImperialGravekeeper:
				NpcInstance spawnedNpc = st.addSpawn(Coffer, 120000);
				Functions.npcSay(spawnedNpc, "Curse of the gods on the one that defiles the property of the empire!");
				setLeaderVar(st, "ImpGraveKeeper", "3");
				break;
			}
			break;
		}
		return null;
	}
}