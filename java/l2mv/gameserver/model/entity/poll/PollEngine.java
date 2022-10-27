package l2mv.gameserver.model.entity.poll;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.components.ChatType;

public class PollEngine
{
	private static final Logger _log = LoggerFactory.getLogger(PollEngine.class);
	private static PollEngine _instance;

	private Poll _poll;
	private boolean _isActive = false;
	private ScheduledFuture<?> _endPollThread = null;

	public PollEngine()
	{
		if (!Config.ENABLE_POLL_SYSTEM)
		{
			return;
		}

		loadPoll();
		startAnnounceThread();
	}

	public void addNewPollQuestion(String question)
	{
		_poll = new Poll(question);
	}

	public Poll getPoll()
	{
		return _poll;
	}

	public Poll getActivePoll()
	{
		if ((_poll == null) || !isActive())
		{
			return null;
		}
		return _poll;
	}

	public void startPoll(boolean announce, boolean firstTime)
	{
		if (getPoll().getEndTime() < System.currentTimeMillis())
		{
			if (firstTime)
			{
				getPoll().setEndTime(System.currentTimeMillis() + getPoll().getEndTime());
			}
			else
			{
				return;
			}
		}

		_isActive = true;
		if (announce)
		{
			announcePoll(true);
		}
		startThread();
	}

	public void deleteCurrentPoll()
	{
		_poll = null;
		deleteAllPlayerVotes();
		savePoll();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getHwidGamer() != null)
			{
				player.getHwidGamer().setPollAnswer(-1, false);
			}
		}
	}

	public void stopPoll(boolean announce)
	{
		_isActive = false;
		if (announce)
		{
			announcePoll(false);
		}
		deleteAllPlayerVotes();
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getHwidGamer() != null)
			{
				player.getHwidGamer().setPollAnswer(-1, false);
			}
		}
	}

	public boolean isActive()
	{
		return _isActive;
	}

	private void announcePoll(boolean active)
	{
		if (active)
		{
			Announcements.getInstance().announceToAll("New poll has been opened! Use .poll to Vote!");
		}
		else
		{
			Announcements.getInstance().announceToAll("Voting on the poll is now finished!");

			sortAnswers(getPoll().getAnswers());

			for (PollAnswer answer : getPoll().getAnswers())
			{
				Announcements.getInstance().announceToAll(getAnswerProcentage(answer) + "% players voted on \"" + answer.getAnswer() + "\"");
			}
		}
	}

	protected void startThread()
	{
		if (_endPollThread != null)
		{
			_endPollThread.cancel(false);
			_endPollThread = null;
		}
		_endPollThread = ThreadPoolManager.getInstance().schedule(new Runnable()
		{

			@Override
			public void run()
			{
				if (getPoll() != null)
				{
					stopPoll(true);
				}
			}
		}, getPoll().getEndTime() - System.currentTimeMillis());
	}

	private void startAnnounceThread()
	{
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable()
		{

			@Override
			public void run()
			{
				if (getActivePoll() != null)
				{
					Say2 say = new Say2(0, ChatType.ANNOUNCEMENT, "", "You didn't vote on the poll yet! Write .poll to vote!");
					for (Player onlinePlayer : GameObjectsStorage.getAllPlayersForIterate())
					{
						if (!onlinePlayer.isOnline() || onlinePlayer.isInOfflineMode() || (onlinePlayer.getHwidGamer().getPollAnswer() >= 0))
						{
							continue;
						}
						onlinePlayer.sendPacket(say);
					}
				}
			}
		}, Config.ANNOUNCE_POLL_EVERY_X_MIN * 60000, Config.ANNOUNCE_POLL_EVERY_X_MIN * 60000);

	}

	public int getAnswerProcentage(PollAnswer choosenAnswer)
	{
		if (choosenAnswer.getVotes() == 0)
		{
			return 0;
		}

		int totalVotes = 0;
		for (PollAnswer singleAnswer : getPoll().getAnswers())
		{
			totalVotes += singleAnswer.getVotes();
		}
		return (int) (((double) choosenAnswer.getVotes() / totalVotes) * 100);
	}

	public PollAnswer[] sortAnswers(PollAnswer[] answers)
	{
		Arrays.sort(answers, new AnswersComparator());
		return answers;
	}

	private void loadPoll()
	{
		String question = null;
		List<PollAnswer> answers = new ArrayList<PollAnswer>();
		long endTime = 0;

		ResultSet rset = null;
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("SELECT * FROM poll"))
			{
				statement.execute();
				rset = statement.getResultSet();

				while (rset.next())
				{

					question = rset.getString("question");
					endTime = rset.getLong("end_time") * 1000;

					int answerId = rset.getInt("answer_id");
					String answerText = rset.getString("answer_text");
					int answerVotes = rset.getInt("answer_votes");

					PollAnswer answer = new PollAnswer(answerId, answerText, answerVotes);
					answers.add(answer);

				}

				if (question != null)
				{
					_poll = new Poll(question, answers, endTime);
					startPoll(true, false);
				}

				rset.close();
			}
		}
		catch (Exception e)
		{
			_log.error("error in loadPoll:", e);
		}
	}

	public void savePoll()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM poll"))
			{
				// Deleting everything from poll
				statement.execute();
			}

			if (getPoll() == null)
			{
				return;
			}
			// inserting data
			try (PreparedStatement statement = con.prepareStatement("INSERT INTO poll VALUES (?,?,?,?,?)"))
			{
				for (PollAnswer answer : getPoll().getAnswers())
				{
					statement.setString(1, getPoll().getQuestion());
					statement.setInt(2, answer.getId());
					statement.setString(3, answer.getAnswer());
					statement.setInt(4, answer.getVotes());
					statement.setLong(5, getPoll().getEndTime() / 1000);
					statement.execute();
				}
			}
		}
		catch (Exception e)
		{
			_log.error("could not save Poll:", e);
		}
	}

	private void deleteAllPlayerVotes()
	{
		try (Connection con = DatabaseFactory.getInstance().getConnection())
		{
			// updating data
			try (PreparedStatement statement = con.prepareStatement("UPDATE hwid SET poll_answer=-1"))
			{
				statement.execute();
			}
		}
		catch (Exception e)
		{
			_log.error("could not deleteAllPlayerVotes:", e);
		}
	}

	private static class AnswersComparator implements Comparator<PollAnswer>, Serializable
	{
		private static final long serialVersionUID = 3963758588280527442L;

		@Override
		public int compare(PollAnswer o1, PollAnswer o2)
		{
			int votes1 = o1.getVotes();
			int votes2 = o2.getVotes();

			return Integer.compare(votes2, votes1);
		}
	}

	public static PollEngine getInstance()
	{
		if (_instance == null)
		{
			_instance = new PollEngine();
		}
		return _instance;
	}
}
