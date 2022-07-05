package events.Trivia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.commons.dbutils.DbUtils;
import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2f.gameserver.instancemanager.ServerVariables;
import l2f.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.actor.listener.CharListenerList;
import l2f.gameserver.network.serverpackets.Say2;
import l2f.gameserver.network.serverpackets.components.ChatType;
import l2f.gameserver.network.serverpackets.components.CustomMessage;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class TriviaEvent extends Functions implements ScriptFile, IVoicedCommandHandler, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(TriviaEvent.class);

	private final String[] _commandList = new String[]
	{
		"toff",
		"ton",
		"thelp",
		"ttop",
		"t",
		"to"
	};
	private final ArrayList<String> questions = new ArrayList<String>();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	static ScheduledFuture<?> _taskTriviaStart;
	private static ArrayList<RewardList> _items = new ArrayList<RewardList>();
	static ScheduledFuture<?> _taskStartQuestion;
	static ScheduledFuture<?> _taskStopQuestion;
	long _timeStopTrivia = 0;
	private static boolean status = false;
	private static boolean _questionStatus = false;
	private static int index;
	private static String question;
	private static String answer;
	private final static String GET_LIST_FASTERS = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='triviafirst' ORDER BY `value` DESC LIMIT 0,10";
	private final static String GET_LIST_TOP = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='triviatop' ORDER BY `value` DESC LIMIT 0,10";;
	private static TriviaEvent instance;
	private static boolean DEBUG_TRIVIA = true;

	public static TriviaEvent getInstance()
	{
		if (instance == null)
		{
			instance = new TriviaEvent();
		}
		return instance;
	}

	/**
	 * Загружаем базу вопросов.
	 */
	public void loadQuestions()
	{
		final File file = new File(Config.DATAPACK_ROOT + "/config/events/TriviaQuestions.txt");
		try
		{
			final BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null)
			{
				questions.add(str);
			}
			br.close();
			_log.info("Trivia Event: Questions loaded");
		}
		catch (final Exception e)
		{
			_log.info("Trivia Event: Error parsing questions file. " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Сохраняем вопросы обратно в файл.
	 */
	public void saveQuestions()
	{
		if (!Config.TRIVIA_REMOVE_QUESTION)
		{
			return;
		}
		final File file = new File(Config.DATAPACK_ROOT + "/config/events/TriviaQuestions.txt");
		try
		{
			final BufferedWriter br = new BufferedWriter(new FileWriter(file));
			for (final String str : questions)
			{
				br.write(str + "\r\n");
			}
			br.close();
			_log.info("Trivia Event: Questions saved");
		}
		catch (final Exception e)
		{
			_log.info("Trivia Event: Error save questions file. " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Готовим вопрос, вытягиваем рандомно любой вопрос с ответом.
	 */
	public void parseQuestion()
	{
		index = Rnd.get(questions.size());
		final String str = questions.get(index);
		final StringTokenizer st = new StringTokenizer(str, "|");
		question = st.nextToken();
		answer = st.nextToken();
	}

	public void checkAnswer(String chat, Player player)
	{
		if (chat.equalsIgnoreCase(answer) && isQuestionStatus())
		{
			if (!playerList.contains(player))
			{
				playerList.add(player);
			}
			_log.info("Trivia: Player - " + player.getName() + " has given correct answer. Added to the list.");
		}
	}

	/**
	 * Анонс вопроса викторины.
	 *
	 * @param text
	 */
	public void announseTrivia(String text)
	{
		final Say2 cs = new Say2(0, ChatType.TELL, "Trivia", text);
		for (final Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getVar("trivia") == "on")
			{
				player.sendPacket(cs);
			}
		}
	}

	public int playersInTrivia()
	{
		int playersCount = 0;
		for (final Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getVar("trivia") == "on")
			{
				playersCount++;
			}
		}
		return playersCount;
	}

	public void checkPlayers()
	{
		final Say2 cs = new Say2(0, ChatType.TELL, "Trivia", " To cancel participation in trivia event just type .toff for more info type .thelp");
		for (final Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getVar("trivia") == null)
			{
				player.sendPacket(cs);
				player.setVar("trivia", "on", -1);
			}
		}
	}

	public void triviaSay(Player player, String text)
	{
		final Say2 cs = new Say2(0, ChatType.TELL, "Trivia", text);
		if (player.getVar("trivia") == "on")
		{
			player.sendPacket(cs);
		}
	}

	/**
	 * Подсчет правильно ответивших
	 */
	public void winners()
	{
		if (!isStatus())
		{
			_log.info("Tried to declare a winner, but the quiz was turned off", "Trivia");
			return;
		}
		if (isQuestionStatus())
		{
			_log.info("Tried to declare a winner when acted question.", "Trivia");
			return;
		}
		if (ServerVariables.getString("triviaq") == null)
		{
			ServerVariables.set("triviaq", 0);
		}
		if (ServerVariables.getString("triviaa") == null)
		{
			ServerVariables.set("triviaa", 0);
		}
		if (playerList.size() > 0)
		{
			announseTrivia("-----------------------------------------");
			announseTrivia("Total players answered correctly: " + playerList.size() + " from " + playersInTrivia());
			announseTrivia("First: " + playerList.get(0).getName());
			announseTrivia("The answer was: " + answer);
			announseTrivia("Next question in " + Config.TRIVIA_TIME_PAUSE + " sec.");
			announseTrivia("-----------------------------------------");
			ServerVariables.set("triviaq", ServerVariables.getInt("triviaq") + 1);
			ServerVariables.set("triviaa", ServerVariables.getInt("triviaa") + 1);
			if (Config.TRIVIA_REMOVE_QUESTION)
			{
				questions.remove(index);
			}
			_log.info("" + playerList.get(0).getName() + "|" + playerList.size() + "|" + question + "|" + answer, "Trivia");
		}
		else
		{
			if (Config.TRIVIA_REMOVE_QUESTION_NO_ANSWER)
			{
				announseTrivia("The answer is: " + answer + " ! Nobody got it...");
				announseTrivia("Next question in " + Config.TRIVIA_TIME_PAUSE + " sec.");
			}
			else
			{
				announseTrivia("Nobody answered correctly!");
				announseTrivia("Next question in " + Config.TRIVIA_TIME_PAUSE + " sec.");
			}
			ServerVariables.set("triviaq", ServerVariables.getInt("triviaq") + 1);
			if (Config.TRIVIA_REMOVE_QUESTION && Config.TRIVIA_REMOVE_QUESTION_NO_ANSWER)
			{
				questions.remove(index);
			}
		}
	}

	/**
	 * Считам через сколько стартуем викторину, создаем пул.
	 */
	public void Start()
	{
		if (_taskTriviaStart != null)
		{
			_taskTriviaStart.cancel(true);
		}
		final Calendar _timeStartTrivia = Calendar.getInstance();
		_timeStartTrivia.set(Calendar.HOUR_OF_DAY, Config.TRIVIA_START_TIME_HOUR);
		_timeStartTrivia.set(Calendar.MINUTE, Config.TRIVIA_START_TIME_MIN);
		_timeStartTrivia.set(Calendar.SECOND, 0);
		_timeStartTrivia.set(Calendar.MILLISECOND, 0);
		final Calendar _timeStopTrivia = Calendar.getInstance();
		_timeStopTrivia.setTimeInMillis(_timeStartTrivia.getTimeInMillis());
		_timeStopTrivia.add(Calendar.HOUR_OF_DAY, Config.TRIVIA_WORK_TIME);
		final long currentTime = System.currentTimeMillis();
		// Если время виторины еще не наступило
		if (_timeStartTrivia.getTimeInMillis() >= currentTime)
		{
			_taskTriviaStart = ThreadPoolManager.getInstance().schedule(new TriviaStart(_timeStopTrivia.getTimeInMillis()), _timeStartTrivia.getTimeInMillis() - currentTime);
		}
		else if (currentTime > _timeStartTrivia.getTimeInMillis() && currentTime < _timeStopTrivia.getTimeInMillis())
		{
			_taskTriviaStart = ThreadPoolManager.getInstance().schedule(new TriviaStart(_timeStopTrivia.getTimeInMillis()), 1000);
		}
		else
		{
			_timeStartTrivia.add(Calendar.HOUR_OF_DAY, 24);
			_timeStopTrivia.add(Calendar.HOUR_OF_DAY, 24);
			_taskTriviaStart = ThreadPoolManager.getInstance().schedule(new TriviaStart(_timeStopTrivia.getTimeInMillis()), _timeStartTrivia.getTimeInMillis() - currentTime);
		}
		if (DEBUG_TRIVIA)
		{
			_log.info("Loaded Event: Trivia - Next Start: " + _timeStartTrivia.getTime());
		}
	}

	/**
	 * Функция продолжения таймера викторины, нужна при ручной остановке викторины. Назначает старт викторины на след день
	 */
	public void Continue()
	{
		if (_taskTriviaStart != null)
		{
			_taskTriviaStart.cancel(true);
		}
		final Calendar _timeStartTrivia = Calendar.getInstance();
		_timeStartTrivia.set(Calendar.HOUR_OF_DAY, Config.TRIVIA_START_TIME_HOUR);
		_timeStartTrivia.set(Calendar.MINUTE, Config.TRIVIA_START_TIME_MIN);
		_timeStartTrivia.set(Calendar.SECOND, 0);
		_timeStartTrivia.set(Calendar.MILLISECOND, 0);
		final Calendar _timeStopTrivia = Calendar.getInstance();
		_timeStopTrivia.setTimeInMillis(_timeStartTrivia.getTimeInMillis());
		_timeStopTrivia.add(Calendar.HOUR_OF_DAY, Config.TRIVIA_WORK_TIME);
		_timeStartTrivia.add(Calendar.HOUR_OF_DAY, 24);
		_timeStopTrivia.add(Calendar.HOUR_OF_DAY, 24);
		final long currentTime = System.currentTimeMillis();
		_taskTriviaStart = ThreadPoolManager.getInstance().schedule(new TriviaStart(_timeStopTrivia.getTimeInMillis()), _timeStartTrivia.getTimeInMillis() - currentTime);
		if (DEBUG_TRIVIA)
		{
			_log.info("Continue Trivia: " + _timeStartTrivia.getTime() + "| Stop Trivia: " + _timeStopTrivia.getTime());
		}
	}

	/**
	 * Запуск викторины в ручную!! запускается на время указанное в настройках.
	 */
	public void ForseStart(String[] var)
	{
		final Player player = getSelf();
		if (isRunned())
		{
			_log.info("Trivia: start task already running!");
			player.sendMessage("Trivia already running....");
			return;
		}
		if (var.length != 1)
		{
			show(new CustomMessage("common.Error", player), player);
			return;
		}
		Integer hours;
		try
		{
			hours = Integer.valueOf(var[0]);
			;
		}
		catch (final Exception e)
		{
			show(new CustomMessage("common.Error", player), player);
			return;
		}
		if (hours >= 12 || hours <= 0)
		{
			hours = 1;
		}
		if (_taskTriviaStart != null)
		{
			_taskTriviaStart.cancel(true);
		}
		final Calendar _timeStartTrivia = Calendar.getInstance();
		final Calendar _timeStopTrivia = Calendar.getInstance();
		_timeStopTrivia.setTimeInMillis(_timeStartTrivia.getTimeInMillis());
		_timeStopTrivia.add(Calendar.HOUR_OF_DAY, hours);
		_log.info("Trivia has Started");
		_taskTriviaStart = ThreadPoolManager.getInstance().schedule(new TriviaStart(_timeStopTrivia.getTimeInMillis()), 1000);
		if (DEBUG_TRIVIA)
		{
			_log.info("Start Trivia: " + _timeStartTrivia.getTime());
		}
		_log.info("Stop Trivia: " + _timeStopTrivia.getTime());
	}

	/**
	 * Стартуем викторину
	 *
	 * @author Sevil
	 */
	public class TriviaStart implements Runnable
	{
		public TriviaStart(long timeStopTrivia)
		{
			_timeStopTrivia = timeStopTrivia;
		}

		@Override
		public void run()
		{
			try
			{
				if (_taskStartQuestion != null)
				{
					_taskStartQuestion.cancel(true);
				}
				_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopTrivia), 5000);
				announceToAll("Trivia Event has been Started! For More info type .thelp", ChatType.BATTLEFIELD);
				loadQuestions();
				setStatus(true);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void announceToAll(String text, ChatType type)
	{
		final Say2 cs = new Say2(0, type, "Trivia", text);
		for (final Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			player.sendPacket(cs);
		}
	}

	/**
	 * Задаем вопрос, ждем время, запускаем стоп вопроса.
	 *
	 * @author Sevil
	 */
	public class startQuestion implements Runnable
	{
		long _timeStopTrivia = 0;

		public startQuestion(long timeStopTrivia)
		{
			_timeStopTrivia = timeStopTrivia;
		}

		@Override
		public void run()
		{
			final long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime > _timeStopTrivia)
			{
				_log.info("Trivia time off...", "Trivia");
				playerList.clear();
				setStatus(false);
				setQuestionStatus(false);
				announseTrivia("No more... Trivia event has finished.");
				announceToAll("Trivia Event Finished. Congratz to all winners.", ChatType.BATTLEFIELD);
				return;
			}
			if (!playerList.isEmpty())
			{
				_log.info("What the hell?? why, when I ask the question, answered correctly list is not empty!??", "Trivia");
				playerList.clear();
				return;
			}
			if (!isStatus())
			{
				_log.info("What the hell?? Why should I be asking the question of when a quiz is not running??", "Trivia");
				return;
			}
			if (!isQuestionStatus())
			{
				parseQuestion();
				checkPlayers();
				announseTrivia(question);
				if (_taskStopQuestion != null)
				{
					_taskStopQuestion.cancel(true);
				}
				_taskStopQuestion = ThreadPoolManager.getInstance().schedule(new stopQuestion(_timeStopTrivia), Config.TRIVIA_TIME_ANSER * 1000);
				setQuestionStatus(true);
			}
			else
			{
				_log.info("What the hell?? WHY status question true?? when must be false!!!!", "Trivia");
			}
		}
	}

	/**
	 * Стоп вопроса: подсчитываем правильные ответы, и кто дал правильный ответ быстрее всех. запускаем следующий вопрос.
	 *
	 * @author Sevil
	 */
	public class stopQuestion implements Runnable
	{
		long _timeStopTrivia = 0;

		public stopQuestion(long timeStopTriva)
		{
			_timeStopTrivia = timeStopTriva;
		}

		@Override
		public void run()
		{
			if (!isStatus())
			{
				_log.info("What the devil?? Why should I consider the winners and give out rewards when the quiz is not running??", "Trivia");
				return;
			}
			setQuestionStatus(false);
			winners();
			rewarding();
			playerList.clear();
			if (_taskStartQuestion != null)
			{
				_taskStartQuestion.cancel(true);
			}
			_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopTrivia), Config.TRIVIA_TIME_PAUSE * 1000);
		}
	}

	/**
	 * Останавливаем эвент.
	 */
	public void stop()
	{
		playerList.clear();
		if (_taskStartQuestion != null)
		{
			_taskStartQuestion.cancel(true);
		}
		if (_taskStopQuestion != null)
		{
			_taskStopQuestion.cancel(true);
		}
		setQuestionStatus(false);
		_log.info("Trivia has been Stoped.", "Trivia");
		if (isStatus())
		{
			announceToAll("Trivia Event has been Stoped!", ChatType.BATTLEFIELD);
		}
		setStatus(false);
	}

	/**
	 * Формируем окно справки. вызывается если игрок не разу не учавствовал в викторине или командой .thelp
	 *
	 * @param player
	 */
	public void help(Player player)
	{
		int top;
		int first;
		int vq;
		int va;
		String vstatus;
		if (player.getVar("triviatop") == null)
		{
			top = 0;
		}
		else
		{
			top = Integer.parseInt(player.getVar("triviatop"));
		}
		if (player.getVar("triviafirst") == null)
		{
			first = 0;
		}
		else
		{
			first = Integer.parseInt(player.getVar("triviafirst"));
		}
		if (ServerVariables.getString("triviaq", "0") == "0")
		{
			ServerVariables.set("triviaq", 0);
			vq = 0;
		}
		else
		{
			vq = Integer.parseInt(ServerVariables.getString("triviaq"));
		}
		if (ServerVariables.getString("triviaa", "0") == "0")
		{
			ServerVariables.set("triviaa", 0);
			va = 0;
		}
		else
		{
			va = Integer.parseInt(ServerVariables.getString("triviaa"));
		}
		if (player.getVar("trivia") == "on")
		{
			vstatus = "You <font color=\"#00FF00\">participate</font> in Trivia Event<br>";
		}
		else
		{
			vstatus = "You <font color=\"#FF0000\">do not participate</font> in Trivia Event<br>";
		}
		final StringBuffer help = new StringBuffer("<html><body>");
		help.append("<center>Trivia Help<br></center>");
		help.append(vstatus);
		// help.append("Time of event: " + Config.TRIVIA_START_TIME_HOUR + ":" + Config.TRIVIA_START_TIME_MIN + "<br>");
		help.append("Duration : " + Config.TRIVIA_WORK_TIME + " hours.<br>");
		help.append("Time to answer: " + Config.TRIVIA_TIME_ANSER + " sec.<br>");
		help.append("Time between questions: " + (Config.TRIVIA_TIME_ANSER + Config.TRIVIA_TIME_PAUSE) + " sec.<br>");
		help.append("Question No. " + vq + "<br>");
		help.append("Correctly answered : " + va + "<br>");
		help.append("You answered correctly on: " + top + " questions.<br>");
		help.append("You was first " + first + " times.<br>");
		help.append("<center>Trivia commands:<br></center>");
		help.append("<font color=\"LEVEL\">To answer just type it at any chat.</font><br>");
		help.append("<font color=\"LEVEL\">.ton</font> - Register to Trivia.<br>");
		help.append("<font color=\"LEVEL\">.toff</font> - Unregister from Trivia.<br>");
		help.append("<font color=\"LEVEL\">.ttop</font> - shows the Top Trivia Players.<br>");
		help.append("<font color=\"LEVEL\">.thelp</font> - Show this page..<br>");
		help.append("<font color=\"LEVEL\">.t</font> - Show the current question.<br>");
		help.append("</body></html>");
		show(help.toString(), player);
	}

	/**
	 * выводит топ
	 *
	 * @param player
	 */
	public void top(Player player)
	{
		final StringBuffer top = new StringBuffer("<html><body>");
		top.append("<center>Top Fastest");
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>");
		final List<Scores> fasters = getList(true);
		if (fasters.size() != 0)
		{
			top.append("<table width=300 border=0 bgcolor=\"000000\">");
			int index = 1;
			for (final Scores faster : fasters)
			{
				top.append("<tr>");
				top.append("<td><center>" + index + "<center></td>");
				top.append("<td><center>" + faster.getName() + "<center></td>");
				top.append("<td><center>" + faster.getScore() + "<center></td>");
				top.append("</tr>");
				index++;
			}
			top.append("<tr><td><br></td><td></td></tr>");
			top.append("</table>");
		}
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		top.append("</center>");
		top.append("<center>Overall Top");
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3>");
		final List<Scores> top10 = getList(false);
		if (top10.size() != 0)
		{
			top.append("<table width=300 border=0 bgcolor=\"000000\">");
			int index = 1;
			for (final Scores top1 : top10)
			{
				top.append("<tr>");
				top.append("<td><center>" + index + "<center></td>");
				top.append("<td><center>" + top1.getName() + "<center></td>");
				top.append("<td><center>" + top1.getScore() + "<center></td>");
				top.append("</tr>");
				index++;
			}
			top.append("<tr><td><br></td><td></td></tr>");
			top.append("</table>");
		}
		top.append("<img src=\"L2UI.SquareWhite\" width=270 height=1> <img src=\"L2UI.SquareBlank\" width=1 height=3>");
		top.append("</center>");
		top.append("</body></html>");
		show(top.toString(), player);
	}

	public void setQuestionStatus(boolean b)
	{
		_questionStatus = b;
	}

	public boolean isQuestionStatus()
	{
		return _questionStatus;
	}

	@Override
	public void onLoad()
	{
		CharListenerList.addGlobal(this);
		executeTask("events.Trivia.TriviaEvent", "preLoad", new Object[0], 20000);
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		_log.info("Loaded Event: Trivia");
	}

	@Override
	public void onReload()
	{
		stop();
	}

	@Override
	public void onShutdown()
	{
		stop();
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if (command.equals("ton"))
		{
			player.setVar("trivia", "on", -1);
			player.sendMessage(new CustomMessage("events.trivia.you_participate", player));
			player.sendMessage(new CustomMessage("events.trivia.message_questions", player));
		}
		if (command.equals("toff"))
		{
			player.setVar("trivia", "off", -1);
			player.sendMessage(new CustomMessage("events.trivia.refuse", player));
			player.sendMessage(new CustomMessage("events.trivia.next_time", player));
		}
		if (command.equals("thelp"))
		{
			help(player);
		}
		if (command.equals("ttop"))
		{
			top(player);
		}
		if (command.equals("t"))
		{
			triviaSay(player, question);
		}
		if (command.equals("to") && player.isGM())
		{
			triviaSay(player, answer);
		}
		return true;
	}

	/**
	 * выдача награды, начисление очков.
	 */
	private void rewarding()
	{
		if (!isStatus())
		{
			_log.info("Tried to present awards, but the quiz was turned off");
			return;
		}
		if (isQuestionStatus())
		{
			_log.info("Tried to present awards when acted question.");
			return;
		}
		parseReward();
		int schet;
		int first;
		for (final Player player : playerList)
		{
			if (player.getVar("triviatop") == null)
			{
				schet = 0;
			}
			else
			{
				schet = Integer.parseInt(player.getVar("triviatop"));
			}
			if (player.getVar("triviafirst") == null)
			{
				first = 0;
			}
			else
			{
				first = Integer.parseInt(player.getVar("triviafirst"));
			}
			if (player == playerList.get(0))
			{
				giveItemByChance(player, true);
				player.setVar("triviafirst", "" + (first + 1) + "", -1);
			}
			else
			{
				giveItemByChance(player, false);
			}
			player.setVar("triviatop", "" + (schet + 1) + "", -1);
		}
	}

	/**
	 * парсим конфиг наград
	 */
	private void parseReward()
	{
		_items.clear();
		final StringTokenizer st = new StringTokenizer(Config.TRIVIA_REWARD_FIRST, ";");
		final StringTokenizer str = new StringTokenizer(Config.TRIVIA_REWARD_REST, ";");
		while (st.hasMoreTokens())
		{
			final String str1 = st.nextToken();
			final StringTokenizer str2 = new StringTokenizer(str1, ",");
			final int itemId = Integer.parseInt(str2.nextToken());
			final int count = Integer.parseInt(str2.nextToken());
			final int chance = Integer.parseInt(str2.nextToken());
			final boolean first = true;
			final RewardList item = new RewardList();
			item.setProductId(itemId);
			item.setCount(count);
			item.setChance(chance);
			item.setFirst(first);
			_items.add(item);
		}
		while (str.hasMoreTokens())
		{
			final String str1 = str.nextToken();
			final StringTokenizer str2 = new StringTokenizer(str1, ",");
			final int itemId = Integer.parseInt(str2.nextToken());
			final int count = Integer.parseInt(str2.nextToken());
			final int chance = Integer.parseInt(str2.nextToken());
			final boolean first = false;
			final RewardList item = new RewardList();
			item.setProductId(itemId);
			item.setCount(count);
			item.setChance(chance);
			item.setFirst(first);
			_items.add(item);
		}
	}

	/**
	 * Выдаем приз на каторую укажет шанс + определяем выдавать приз для первого или для остальных
	 *
	 * @param player
	 * @param first
	 * @return
	 */
	private boolean giveItemByChance(Player player, boolean first)
	{
		int chancesumm = 0;
		int productId = 0;
		final int chance = Rnd.get(0, 100);
		int count = 0;
		for (final RewardList items : _items)
		{
			chancesumm = chancesumm + items.getChance();
			if (first == items.getFirst() && chancesumm > chance)
			{
				productId = items.getProductId();
				count = items.getCount();
				addItem(player, productId, count, true, "Trivia");
				if (DEBUG_TRIVIA)
				{
					_log.info("Player: " + player.getName() + " recived " + productId + ":" + count + " with chance: " + items.getChance() + ":" + items.getFirst() + "", "Trivia");
				}
				return true;
			}
		}
		return true;
	}

	private class RewardList
	{
		public int _productId;
		public int _count;
		public int _chance;
		public boolean _first;

		private void setProductId(int productId)
		{
			_productId = productId;
		}

		private void setChance(int chance)
		{
			_chance = chance;
		}

		private void setCount(int count)
		{
			_count = count;
		}

		private void setFirst(boolean first)
		{
			_first = first;
		}

		private int getProductId()
		{
			return _productId;
		}

		private int getChance()
		{
			return _chance;
		}

		private int getCount()
		{
			return _count;
		}

		private boolean getFirst()
		{
			return _first;
		}
	}

	private boolean isStatus()
	{
		return status;
	}

	public boolean isRunned()
	{
		return status;
	}

	private void setStatus(boolean status)
	{
		TriviaEvent.status = status;
	}

	/**
	 * Возвращаем имя чара по его obj_Id
	 *
	 * @param char_id
	 * @return
	 */
	private String getName(int char_id)
	{
		String name = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT char_name FROM characters WHERE obj_Id=?");
			statement.setInt(1, char_id);
			rset = statement.executeQuery();
			rset.next();
			name = rset.getString("char_name");
			// return name;
		}
		catch (final SQLException e)
		{
			_log.info("AAA! DANGER, I can not find a player with such obj_Id:s" + e.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return name;
	}

	/**
	 * Возвращаем лист имен.
	 *
	 * @param first
	 * @return
	 */
	private List<Scores> getList(final boolean first)
	{
		final List<Scores> names = new ArrayList<Scores>();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		String GET_LIST = null;
		if (first)
		{
			GET_LIST = GET_LIST_FASTERS;
		}
		else
		{
			GET_LIST = GET_LIST_TOP;
		}
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement(GET_LIST);
			rset = statement.executeQuery();
			while (rset.next())
			{
				final String name = getName(rset.getInt("obj_id"));
				final int score = rset.getInt("value");
				final Scores scores = new Scores();
				scores.setName(name);
				scores.setScore(score);
				names.add(scores);
			}
			return names;
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return names;
	}

	private class Scores
	{
		public String _name;
		public int _score;

		private void setName(String name)
		{
			_name = name;
		}

		private void setScore(int score)
		{
			_score = score;
		}

		private String getName()
		{
			return _name;
		}

		private int getScore()
		{
			return _score;
		}
	}

	public static void preLoad()
	{
		if (Config.TRIVIA_ENABLED)
		{
			executeTask("events.Trivia.TriviaEvent", "Start", new Object[0], 5000);
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		final Say2 cs = new Say2(0, ChatType.BATTLEFIELD, "Trivia", "Trivia Event is Active! To participate, type the command .ton! For reference .thelp!");
		if (isStatus())
		{
			player.sendPacket(cs);
		}
	}

	public long getTriviaEndTime()
	{
		return _timeStopTrivia;
	}
}
