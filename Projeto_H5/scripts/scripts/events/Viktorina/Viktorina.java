package events.Viktorina;

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

import events.EventsConfig;
import l2mv.commons.dbutils.DbUtils;
import l2mv.commons.util.Rnd;
import l2mv.gameserver.Announcements;
import l2mv.gameserver.Config;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.database.DatabaseFactory;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2mv.gameserver.instancemanager.ServerVariables;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.SystemMessage;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;

public class Viktorina extends Functions implements ScriptFile, IVoicedCommandHandler, OnPlayerEnterListener
{
	private static final Logger _log = LoggerFactory.getLogger(Viktorina.class);
	private String[] _commandList = new String[]
	{
		"ololo",
		"voffolo",
		"vonololo",
		"vhelplolo",
		"vtoplolo",
		"vlolo",
		"vololo"
	};
	private ArrayList<String> questions = new ArrayList<String>();
	private static ArrayList<Player> playerList = new ArrayList<Player>();
	static ScheduledFuture<?> _taskViktorinaStart;
	private static ArrayList<RewardList> _items = new ArrayList<RewardList>();
	static ScheduledFuture<?> _taskStartQuestion;
	static ScheduledFuture<?> _taskStopQuestion;
	long _timeStopViktorina = 0;
	private static boolean status = false;
	private static boolean _questionStatus = false;
	private static int index;
	private static String question;
	private static String answer;
	private final static String GET_LIST_FASTERS = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='viktorinafirst' ORDER BY `value` DESC LIMIT 0,10";
	private final static String GET_LIST_TOP = "SELECT `obj_id`,`value` FROM `character_variables` WHERE `name`='viktorinaschet' ORDER BY `value` DESC LIMIT 0,10";
	private static Viktorina instance;
	private static boolean DEBUG_VIKROINA = true;
	// Перменные ниже, перенес в конфиг.
	/*
	 * private static boolean VIKTORINA_ENABLED = false;// false;
	 * private static boolean VIKTORINA_REMOVE_QUESTION = false;//false;;
	 * private static boolean VIKTORINA_REMOVE_QUESTION_NO_ANSWER = false;//= false;
	 * private static int VIKTORINA_START_TIME_HOUR;// 16;
	 * private static int VIKTORINA_START_TIME_MIN;// 16;
	 * private static int VIKTORINA_WORK_TIME;//2;
	 * private static int VIKTORINA_TIME_ANSER;//1;
	 * private static int VIKTORINA_TIME_PAUSE;//1;
	 */

	// private static String REWARD_FERST = ScriptConfig.get("Victorina_Reward_Ferst");//"57,1,100;57,2,100;";
	// private static String REWARD_OTHER = ScriptConfig.get("Victorina_Reward_Other");//"57,1,100;57,2,100;";

	public static Viktorina getInstance()
	{
		if (instance == null)
		{
			instance = new Viktorina();
		}
		return instance;
	}

	/**
	 * Загружаем базу вопросов.
	 */
	public void loadQuestions()
	{
		File file = new File(Config.DATAPACK_ROOT + "/data/other/events/Viktorina/questions.txt");

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null)
			{
				questions.add(str);
			}
			br.close();
			_log.info("Viktorina Event: Questions loaded");
		}
		catch (Exception e)
		{
			_log.info("Viktorina Event: Error parsing questions file. " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Сохраняем вопросы обратно в файл.
	 */
	public void saveQuestions()
	{
		if (!Config.VIKTORINA_REMOVE_QUESTION)
		{
			return;
		}
		File file = new File(Config.DATAPACK_ROOT + "/data/other/events/Viktorina/questions.txt");

		try
		{
			BufferedWriter br = new BufferedWriter(new FileWriter(file));
			for (String str : questions)
			{
				br.write(str + "\r\n");
			}
			br.close();
			_log.info("Viktorina Event: Questions saved");
		}
		catch (Exception e)
		{
			_log.info("Viktorina Event: Error save questions file. " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Готовим вопрос, вытягиваем рандомно любой вопрос с ответом.
	 */
	public void parseQuestion()
	{
		index = Rnd.get(questions.size());
		String str = questions.get(index);
		StringTokenizer st = new StringTokenizer(str, "|");
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
			_log.info("Viktorina: player - " + player.getName() + " gave the correct answer. Was added to the list.");
		}
	}

	/**
	 * Анонс вопроса викторины.
	 * @param text
	 */
	public void announseViktorina(String text)
	{
		Say2 cs = new Say2(0, ChatType.TELL, "quiz", text);
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getVar("viktorina") == "on")
			{
				player.sendPacket(cs);
			}
		}
	}

	public void checkPlayers()
	{
		Say2 cs = new Say2(0, ChatType.TELL, "Quiz ", " to refuse to participate in a quiz type .voff, a reference type .vhelp");
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
		{
			if (player.getVar("viktorina") == null)
			{
				player.sendPacket(cs);
				player.setVar("viktorina", "on", -1);
			}
		}
	}

	public void viktorinaSay(Player player, String text)
	{
		Say2 cs = new Say2(0, ChatType.TELL, "Quiz", text);
		if (player.getVar("viktorina") == "on")
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
			_log.info("Tried to declare a winner, but the quiz was off", "Viktorina");
			return;
		}
		if (isQuestionStatus())
		{
			_log.info("Tried to declare a winner, when acted question.", "Viktorina");
			return;
		}
		if (ServerVariables.getString("viktorinaq") == null)
		{
			ServerVariables.set("viktorinaq", 0);
		}
		if (ServerVariables.getString("viktorinaa") == null)
		{
			ServerVariables.set("viktorinaa", 0);
		}
		if (playerList.size() > 0)
		{
			announseViktorina(" correct answers: " + playerList.size() + ", The first answer: " + playerList.get(0).getName() + ", The correct answer: " + answer + "");
			ServerVariables.set("viktorinaq", ServerVariables.getInt("viktorinaq") + 1);
			ServerVariables.set("viktorinaa", ServerVariables.getInt("viktorinaa") + 1);
			if (Config.VIKTORINA_REMOVE_QUESTION)
			{
				questions.remove(index);
			}
			_log.info("" + playerList.get(0).getName() + "|" + playerList.size() + "|" + question + "|" + answer, "Viktorina");
		}
		else
		{
			if (Config.VIKTORINA_REMOVE_QUESTION_NO_ANSWER)
			{
				announseViktorina(" the correct answer had been received, the correct answer was:" + answer + "");
			}
			if (!Config.VIKTORINA_REMOVE_QUESTION_NO_ANSWER)
			{
				announseViktorina(" the correct answer was received");
			}
			ServerVariables.set("viktorinaq", ServerVariables.getInt("viktorinaq") + 1);
			if (Config.VIKTORINA_REMOVE_QUESTION && Config.VIKTORINA_REMOVE_QUESTION_NO_ANSWER)
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
		if (_taskViktorinaStart != null)
		{
			_taskViktorinaStart.cancel(true);
		}
		Calendar _timeStartViktorina = Calendar.getInstance();
		_timeStartViktorina.set(Calendar.HOUR_OF_DAY, Config.VIKTORINA_START_TIME_HOUR);
		_timeStartViktorina.set(Calendar.MINUTE, Config.VIKTORINA_START_TIME_MIN);
		_timeStartViktorina.set(Calendar.SECOND, 0);
		_timeStartViktorina.set(Calendar.MILLISECOND, 0);
		Calendar _timeStopViktorina = Calendar.getInstance();
		_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
		_timeStopViktorina.add(Calendar.HOUR_OF_DAY, Config.VIKTORINA_WORK_TIME);
		long currentTime = System.currentTimeMillis();
		// Если время виторины еще не наступило
		if (_timeStartViktorina.getTimeInMillis() >= currentTime)
		{
			_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
		}
		// Если как раз идет время викторины - стартуем викторину
		else if (currentTime > _timeStartViktorina.getTimeInMillis() && currentTime < _timeStopViktorina.getTimeInMillis())
		{
			_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), 1000);
		}
		// сегодня олим уже не должен запускаться, значит нада стартовать викторину
		// на след день, прибавляем 24 часа
		else
		{
			_timeStartViktorina.add(Calendar.HOUR_OF_DAY, 24);
			_timeStopViktorina.add(Calendar.HOUR_OF_DAY, 24);
			_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
		}

		if (DEBUG_VIKROINA)
		{
			_log.info("Start Viktorina: " + _timeStartViktorina.getTime());
		}
		_log.info("Stop Viktorina: " + _timeStopViktorina.getTime());

	}

	/**
	 * Функция продолжения таймера викторины, нужна при ручной остановке викторины.
	 * Назначает старт викторины на след день
	 */
	public void Continue()
	{
		if (_taskViktorinaStart != null)
		{
			_taskViktorinaStart.cancel(true);
		}
		Calendar _timeStartViktorina = Calendar.getInstance();
		_timeStartViktorina.set(Calendar.HOUR_OF_DAY, Config.VIKTORINA_START_TIME_HOUR);
		_timeStartViktorina.set(Calendar.MINUTE, Config.VIKTORINA_START_TIME_MIN);
		_timeStartViktorina.set(Calendar.SECOND, 0);
		_timeStartViktorina.set(Calendar.MILLISECOND, 0);
		Calendar _timeStopViktorina = Calendar.getInstance();
		_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
		_timeStopViktorina.add(Calendar.HOUR_OF_DAY, Config.VIKTORINA_WORK_TIME);
		_timeStartViktorina.add(Calendar.HOUR_OF_DAY, 24);
		_timeStopViktorina.add(Calendar.HOUR_OF_DAY, 24);
		long currentTime = System.currentTimeMillis();
		_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), _timeStartViktorina.getTimeInMillis() - currentTime);
		if (DEBUG_VIKROINA)
		{
			_log.info("Continue Viktorina: " + _timeStartViktorina.getTime() + "|Stop Viktorina: " + _timeStopViktorina.getTime());
		}

	}

	/**
	 * Запуск викторины в ручную!!
	 * запускается на время указанное в настройках.
	 */
	public void ForseStart()
	{
		if (_taskViktorinaStart != null)
		{
			_taskViktorinaStart.cancel(true);
		}
		Calendar _timeStartViktorina = Calendar.getInstance();
		Calendar _timeStopViktorina = Calendar.getInstance();
		_timeStopViktorina.setTimeInMillis(_timeStartViktorina.getTimeInMillis());
		_timeStopViktorina.add(Calendar.HOUR_OF_DAY, Config.VIKTORINA_WORK_TIME);
		_log.info("Viktorina Started");
		_taskViktorinaStart = ThreadPoolManager.getInstance().schedule(new ViktorinaStart(_timeStopViktorina.getTimeInMillis()), 1000);
		if (DEBUG_VIKROINA)
		{
			_log.info("Start Viktorina: " + _timeStartViktorina.getTime());
		}
		_log.info("Stop Viktorina: " + _timeStopViktorina.getTime());

	}

	/**
	 * Стартуем викторину
	 * @author Sevil
	 *
	 */
	public class ViktorinaStart implements Runnable
	{

		public ViktorinaStart(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		@Override
		public void run()
		{
			try
			{
				// if(isStatus())
				// {
				// if (DEBUG_VIKROINA)
				// _log.info("Viktoryna is already starter, WTF ??? \n" + Util.dumpStack());
				// return;
				// }
				if (_taskStartQuestion != null)
				{
					_taskStartQuestion.cancel(true);
				}
				_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopViktorina), 5000);
				Announcements.getInstance().announceToAll("Quiz started!");
				Announcements.getInstance().announceToAll("For help, typе .vhelp");
				loadQuestions();
				setStatus(true);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Задаем вопрос, ждем время, запускаем стоп вопроса.
	 * @author Sevil
	 *
	 */
	public class startQuestion implements Runnable
	{
		long _timeStopViktorina = 0;

		public startQuestion(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		@Override
		public void run()
		{
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime > _timeStopViktorina)
			{
				_log.info("Viktorina time off...", "Viktorina");
				playerList.clear();
				setStatus(false);
				setQuestionStatus(false);
				announseViktorina("Opening hours of the quiz is up, all the participants have fun!");
				Announcements.getInstance().announceToAll("The quiz is over.!");
				return;
			}
			if (!playerList.isEmpty())
			{
				_log.info("Wtf? why, when I ask the question, list the correct answers is not empty!?!?", "Viktorina");
				playerList.clear();
				return;
			}
			if (!isStatus())
			{
				_log.info("Wtf? Why do I have to ask the question, when a quiz is not running???", "Viktorina");
				return;
			}
			if (!isQuestionStatus())
			{
				parseQuestion();
				checkPlayers();
				announseViktorina(question);
				if (_taskStopQuestion != null)
				{
					_taskStopQuestion.cancel(true);
				}
				_taskStopQuestion = ThreadPoolManager.getInstance().schedule(new stopQuestion(_timeStopViktorina), Config.VIKTORINA_TIME_ANSER * 1000);
				setQuestionStatus(true);
			}
			else
			{
				_log.info("Wtf?? Why is the status question true?? when should be false!!!!", "Viktorina");
			}
		}
	}

	/**
	 * Стоп вопроса: подсчитываем правильные ответы, и кто дал правильный ответ быстрее всех.
	 * запускаем следующий вопрос.
	 * @author Sevil
	 *
	 */
	public class stopQuestion implements Runnable
	{
		long _timeStopViktorina = 0;

		public stopQuestion(long timeStopViktorina)
		{
			_timeStopViktorina = timeStopViktorina;
		}

		@Override
		public void run()
		{
			if (!isStatus())
			{
				_log.info("Wtf? Why should I consider the winners and give out rewards when the quiz is not running???", "Viktorina");
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
			_taskStartQuestion = ThreadPoolManager.getInstance().schedule(new startQuestion(_timeStopViktorina), Config.VIKTORINA_TIME_PAUSE * 1000);
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
		_log.info("Viktorina Stoped.", "Viktorina");
		if (isStatus())
		{
			Announcements.getInstance().announceToAll("Quiz stopped!");
		}
		setStatus(false);
		Continue();
	}

	/**
	 * Формируем окно справки. вызывается если игрок не разу не учавствовал в викторине
	 * или командой .vhelp
	 * @param player
	 */
	public void help(Player player)
	{
		int schet;
		int first;
		int vq;
		int va;
		String vstatus;
		if (player.getVar("viktorinaschet") == null)
		{
			schet = 0;
		}
		else
		{
			schet = Integer.parseInt(player.getVar("viktorinaschet"));
		}

		if (player.getVar("viktorinafirst") == null)
		{
			first = 0;
		}
		else
		{
			first = Integer.parseInt(player.getVar("viktorinafirst"));
		}

		if (ServerVariables.getString("viktorinaq", "0") == "0")
		{
			ServerVariables.set("viktorinaq", 0);
			vq = 0;
		}
		else
		{
			vq = Integer.parseInt(ServerVariables.getString("viktorinaq"));
		}

		if (ServerVariables.getString("viktorinaa", "0") == "0")
		{
			ServerVariables.set("viktorinaa", 0);
			va = 0;
		}
		else
		{
			va = Integer.parseInt(ServerVariables.getString("viktorinaa"));
		}

		if (player.getVar("viktorina") == "on")
		{
			vstatus = "<font color=\"#00FF00\">You are participating in \"Quiz\"</font><br>";
		}
		else
		{
			vstatus = "<font color=\"#FF0000\">You are not in \"Quiz\"</font><br>";
		}

		StringBuffer help = new StringBuffer("<html><body>");
		help.append("<center>Some help to the Quiz<br></center>");
		help.append(vstatus);
		help.append("Start time quiz: " + Config.VIKTORINA_START_TIME_HOUR + ":" + Config.VIKTORINA_START_TIME_MIN + "<br>");
		help.append("Maximum life quiz " + Config.VIKTORINA_WORK_TIME + " h.<br>");
		help.append("The time during which it is possible to answer: " + Config.VIKTORINA_TIME_ANSER + " sec.<br>");
		help.append("Time between questions: " + (Config.VIKTORINA_TIME_ANSER + Config.VIKTORINA_TIME_PAUSE) + " sec.<br>");
		help.append("Issues have already been set: " + vq + ".<br>");
		help.append("The correct answer to the : " + va + ".<br>");
		help.append("You correctly answered : " + schet + ", в " + first + " вы были первым.<br>");
		help.append("<br>");
		help.append("<center>Quiz team:<br></center>");
		help.append("<font color=\"LEVEL\">Answer</font> - enter into any kind of chat.<br>");
		help.append("<font color=\"LEVEL\">.von</font> - command to enable Vitokrina<br>");
		help.append("<font color=\"LEVEL\">.voff</font> - command to disable Vitokrina<br>");
		help.append("<font color=\"LEVEL\">.vtop</font> - command to view the results.<br>");
		help.append("<font color=\"LEVEL\">.vhelp</font> - command for this page.<br>");
		help.append("<font color=\"LEVEL\">.v</font> - shows the current issue.<br>");
		help.append("</body></html>");
		show(help.toString(), player);
	}

	/**
	 * выводит топ
	 * @param player
	 */
	public void top(Player player)
	{
		StringBuffer top = new StringBuffer("<html><body>");
		top.append("<center>Top the fastest");
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

		top.append("<center>Overall top");
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
		executeTask("events.Viktorina.Viktorina", "preLoad", new Object[0], 20000);
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
		_log.info("Loaded Event: Viktorina");
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

		if (command.equals("o"))
		{
			if (args.equalsIgnoreCase(answer) && isQuestionStatus())
			{
				if (!playerList.contains(player))
				{
					playerList.add(player);
					// _log.info("preprepls " + playerList + "");
				}
			}
			if (!isQuestionStatus())
			{
				viktorinaSay(player, "Возможно вопрос не был задан,или же время ответа истекло");
			}
		}
		if (command.equals("von"))
		{
			player.setVar("viktorina", "on", -1);
			player.sendMessage("You take part in the Quiz!");
			player.sendMessage("Wait receipts you issue a PM!");
		}
		if (command.equals("voff"))
		{
			player.setVar("viktorina", "off", -1);
			player.sendMessage("Refused to participate in the Quiz!");
			player.sendMessage("Until next time!");
		}
		if (command.equals("vhelp"))
		{
			help(player);
		}
		if (command.equals("vtop"))
		{
			top(player);
		}
		if (command.equals("v"))
		{
			viktorinaSay(player, question);
		}
		if (command.equals("vo") && player.isGM())
		{
			viktorinaSay(player, answer);
		}
		return true;
	}

	/**
	 *выдача награды, начисление очков.
	 */
	private void rewarding()
	{
		if (!isStatus())
		{
			_log.info("Tried to present awards, but the quiz was off");
			return;
		}
		if (isQuestionStatus())
		{
			_log.info("Tried to present awards, when acted question.");
			return;
		}

		parseReward();
		int schet;
		int first;
		for (Player player : playerList)
		{
			if (player.getVar("viktorinaschet") == null)
			{
				schet = 0;
			}
			else
			{
				schet = Integer.parseInt(player.getVar("viktorinaschet"));
			}
			if (player.getVar("viktorinafirst") == null)
			{
				first = 0;
			}
			else
			{
				first = Integer.parseInt(player.getVar("viktorinafirst"));
			}
			if (player == playerList.get(0))
			{
				giveItemByChance(player, true);
				player.setVar("viktorinafirst", "" + (first + 1) + "", -1);
			}
			else
			{
				giveItemByChance(player, false);
			}
			player.setVar("viktorinaschet", "" + (schet + 1) + "", -1);
		}
	}

	/**
	 * парсим конфиг наград
	 */
	private void parseReward()
	{
		_items.clear();
		StringTokenizer st = new StringTokenizer(EventsConfig.get("Victorina_Reward_Ferst"), ";");
		StringTokenizer str = new StringTokenizer(EventsConfig.get("Victorina_Reward_Other"), ";");
		while (st.hasMoreTokens())
		{
			String str1 = st.nextToken();
			StringTokenizer str2 = new StringTokenizer(str1, ",");
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
			String str1 = str.nextToken();
			StringTokenizer str2 = new StringTokenizer(str1, ",");
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
	 * @param player
	 * @param first
	 * @return
	 */
	private boolean giveItemByChance(Player player, boolean first)
	{
		int chancesumm = 0;
		int productId = 0;
		int chance = Rnd.get(0, 100);
		int count = 0;
		for (RewardList items : _items)
		{
			chancesumm = chancesumm + items.getChance();
			if (first == items.getFirst() && chancesumm > chance)
			{
				productId = items.getProductId();
				count = items.getCount();
				addItem(player, productId, count, false, "Viktorina");
				if (count > 1)
				{
					player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S2_S1S).addItemName(productId).addNumber(count));
				}
				else
				{
					player.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1).addItemName(productId));
				}
				if (DEBUG_VIKROINA)
				{
					_log.info("Player: " + player.getName() + " recived " + productId + ":" + count + " with a chance to: " + items.getChance() + ":" + items.getFirst() + "", "Viktorina");
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

	public static boolean isRunned()
	{
		return status;
	}

	@SuppressWarnings("static-access")
	private void setStatus(boolean status)
	{
		this.status = status;
	}

	/**
	 * Возвращаем имя чара по его obj_Id
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
			_log.info("AAA! HAZARD, I can not find a player with such a obj_Id:" + e.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return name;
	}

	/**
	 * Возвращаем лист имен.
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
				final String name = (getName(rset.getInt("obj_id")));
				final int score = rset.getInt("value");
				Scores scores = new Scores();
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

		if (Config.VIKTORINA_ENABLED)
		{
			executeTask("events.Viktorina.Viktorina", "Start", new Object[0], 5000);
		}
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		Say2 cs = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "Quiz", "Active event Quiz! To participate, type the command .von! for the record .vhelp!");
		if (isStatus())
		{
			player.sendPacket(cs);
		}
	}
}
