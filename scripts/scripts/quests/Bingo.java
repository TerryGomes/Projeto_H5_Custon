package quests;

import java.util.ArrayList;
import java.util.List;

import l2mv.commons.util.Rnd;

public class Bingo
{
	protected final static String template = "%msg%<br><br>%choices%<br><br>%board%";
	protected final static String template_final = "%msg%<br><br>%board%";
	protected final static String template_board = "For your information, below is your current selection.<br><table border=\"1\" border color=\"white\" width=100><tr><td align=\"center\">%cell1%</td><td align=\"center\">%cell2%</td><td align=\"center\">%cell3%</td></tr><tr><td align=\"center\">%cell4%</td><td align=\"center\">%cell5%</td><td align=\"center\">%cell6%</td></tr><tr><td align=\"center\">%cell7%</td><td align=\"center\">%cell8%</td><td align=\"center\">%cell9%</td></tr></table>";
	protected final static String msg_again = "You have already selected that number. Choose your %choicenum% number again.";
	protected final static String msg_begin = "I've arranged 9 numbers on the panel.<br>Now, select your %choicenum% number.";
	protected final static String msg_next = "Now, choose your %choicenum% number.";
	protected final static String msg_0lines = "You are spectacularly unlucky! The red-colored numbers on the panel below are the ones you chose. As you can see, they didn't create even a single line. Did you know that it is harder not to create a single line than creating all 3 lines?";
	protected final static String msg_3lines = "You've created 3 lines! The red colored numbers on the bingo panel below are the numbers you chose. Congratulations!";
	protected final static String msg_lose = "Hmm... You didn't make 3 lines. Why don't you try again? The red-colored numbers on the panel are the ones you chose.";
	protected final static String[] nums =
	{
		"first",
		"second",
		"third",
		"fourth",
		"fifth",
		"final"
	};
	protected int lines;
	private final String _template_choice;
	private final List<Integer> board = new ArrayList<Integer>();
	private final List<Integer> guesses = new ArrayList<Integer>();

	public Bingo(String template_choice)
	{
		_template_choice = template_choice;
		while (board.size() < 9)
		{
			int num = Rnd.get(1, 9);
			if (!board.contains(num))
			{
				board.add(num);
			}
		}
	}

	public String Select(String s)
	{
		try
		{
			return Select(Integer.valueOf(s));
		}
		catch (Exception E)
		{
			return null;
		}
	}

	public String Select(int choise)
	{
		if (choise < 1 || choise > 9)
		{
			return null;
		}
		if (guesses.contains(choise))
		{
			return getDialog(msg_again);
		}
		guesses.add(choise);
		if (guesses.size() == 6)
		{
			return getFinal();
		}
		return getDialog("");
	}

	protected String getBoard()
	{
		if (guesses.size() == 0)
		{
			return "";
		}
		String result = template_board;
		for (int i = 1; i <= 9; i++)
		{
			String cell = "%cell" + String.valueOf(i) + "%";
			int num = board.get(i - 1);
			if (guesses.contains(num))
			{
				result = result.replaceFirst(cell, "<font color=\"" + (guesses.size() == 6 ? "ff0000" : "ffff00") + "\">" + String.valueOf(num) + "</font>");
			}
			else
			{
				result = result.replaceFirst(cell, "?");
			}
		}
		return result;
	}

	public String getDialog(String _msg)
	{
		String result = template;
		if (guesses.size() == 0)
		{
			result = result.replaceFirst("%msg%", msg_begin);
		}
		else
		{
			result = result.replaceFirst("%msg%", _msg.equalsIgnoreCase("") ? msg_next : _msg);
		}
		result = result.replaceFirst("%choicenum%", nums[guesses.size()]);
		StringBuilder choices = new StringBuilder();
		for (int i = 1; i <= 9; i++)
		{
			if (!guesses.contains(i))
			{
				choices.append(_template_choice.replaceAll("%n%", String.valueOf(i)));
			}
		}
		result = result.replaceFirst("%choices%", choices.toString());
		result = result.replaceFirst("%board%", getBoard());
		return result;
	}

	protected String getFinal()
	{
		String result = template_final.replaceFirst("%board%", getBoard());
		calcLines();
		if (lines == 3)
		{
			result = result.replaceFirst("%msg%", msg_3lines);
		}
		else if (lines == 0)
		{
			result = result.replaceFirst("%msg%", msg_0lines);
		}
		else
		{
			result = result.replaceFirst("%msg%", msg_lose);
		}
		return result;
	}

	public int calcLines()
	{
		lines = 0;
		// Horisontal
		lines += checkLine(0, 1, 2) ? 1 : 0;
		lines += checkLine(3, 4, 5) ? 1 : 0;
		lines += checkLine(6, 7, 8) ? 1 : 0;
		// Vertical
		lines += checkLine(0, 3, 6) ? 1 : 0;
		lines += checkLine(1, 4, 7) ? 1 : 0;
		lines += checkLine(2, 5, 8) ? 1 : 0;
		// Diagonal
		lines += checkLine(0, 4, 8) ? 1 : 0;
		lines += checkLine(2, 4, 6) ? 1 : 0;
		return lines;
	}

	public boolean checkLine(int idx1, int idx2, int idx3)
	{
		return guesses.contains(board.get(idx1)) && guesses.contains(board.get(idx2)) && guesses.contains(board.get(idx3));
	}
}