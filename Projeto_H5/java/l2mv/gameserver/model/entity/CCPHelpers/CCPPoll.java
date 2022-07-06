package l2mv.gameserver.model.entity.CCPHelpers;

import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.poll.Poll;
import l2mv.gameserver.model.entity.poll.PollAnswer;
import l2mv.gameserver.model.entity.poll.PollEngine;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;

public class CCPPoll
{
	public static boolean bypass(Player activeChar, String[] vars)
	{
		String second = vars.length > 1 ? vars[1] : "";
		String fileName = "";

		Poll activePoll = PollEngine.getInstance().getActivePoll();

		if (activePoll == null)
		{
			fileName = "pollEmpty.htm";
		}
		else if (vars[0].equals("poll_vote"))
		{
			int answerId = Integer.parseInt(second.trim());
			activePoll.addVote(activeChar, answerId);
			return true;
		}
		else if (!vars[0].equals("poll_change") && activeChar.getHwidGamer().getPollAnswer() >= 0)
		{
			fileName = "pollResults.htm";
		}
		else
		{
			fileName = "pollVote.htm";
		}

		String html = HtmCache.getInstance().getNotNull("command/" + fileName, activeChar);

		if (html.contains("%question%"))
		{
			html = html.replace("%question%", activePoll.getQuestion());
		}

		if (html.contains("%endTime%"))
		{
			html = html.replace("%endTime%", activePoll.getPollEndDate());
		}

		if (html.contains("%answers%"))
		{
			html = fillAnswers(html, activeChar);
		}

		if (html.contains("%results%"))
		{
			html = fillResults(html, activeChar);
		}

		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(html);
		activeChar.sendPacket(msg);

		return false;
	}

	private static String fillAnswers(String html, Player activeChar)
	{
		PollAnswer[] answers = PollEngine.getInstance().getPoll().getAnswers();
		StringBuilder resultsBuilder = new StringBuilder("<table width=280><tr><td>");

		for (int i = 0; i < answers.length; i++)
		{
			PollAnswer answer = answers[i];
			resultsBuilder.append("<table width=280 bgcolor=").append(getColor(i)).append("><tr><td width=200>");
			resultsBuilder.append(answer.getAnswer());
			resultsBuilder.append("</td><td width=80>");
			resultsBuilder.append(getButton("Vote!", "user_poll poll_vote " + answer.getId()));
			resultsBuilder.append("</td></tr></table>");
		}

		resultsBuilder.append("</td></tr></table>");

		return html.replace("%answers%", resultsBuilder.toString());
	}

	private static String fillResults(String html, Player activeChar)
	{
		Poll currentPoll = PollEngine.getInstance().getPoll();
		int answersCount = currentPoll.getAnswers().length;
		PollAnswer[] answersToSort = new PollAnswer[answersCount];

		for (int i = 0; i < answersCount; i++)
		{
			answersToSort[i] = currentPoll.getAnswers()[i];
		}
		answersToSort = PollEngine.getInstance().sortAnswers(answersToSort);

		StringBuilder resultsBuilder = new StringBuilder("<table width=280><tr><td>");

		for (int i = 0; i < answersToSort.length; i++)
		{
			PollAnswer answer = answersToSort[i];
			resultsBuilder.append("<table width=280 bgcolor=");
			resultsBuilder.append(activeChar.getHwidGamer().getPollAnswer() == answer.getId() ? "7d805a" : getColor(i));
			resultsBuilder.append("><tr><td width=200>");
			resultsBuilder.append(answer.getAnswer());
			resultsBuilder.append("</td><td width=80><center>");
			resultsBuilder.append(PollEngine.getInstance().getAnswerProcentage(answer)).append('%');
			resultsBuilder.append("</center></td></tr></table>");
		}

		resultsBuilder.append("</td></tr></table>");

		return html.replace("%results%", resultsBuilder.toString());
	}

	private static String getColor(int index)
	{
		return (index % 2 == 0 ? "313a37" : "3a3a31");
	}

	private static String getButton(String buttonText, String bypass)
	{
		return "<button value=\"" + buttonText + "\" action=\"bypass -h " + bypass + "\" width=" + (buttonText.length() > 18 ? 170 : 80) + " height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
	}
}
