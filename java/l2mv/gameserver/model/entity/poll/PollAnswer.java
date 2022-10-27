package l2mv.gameserver.model.entity.poll;

public class PollAnswer
{
	private int _id;
	private String _answer;
	private int _votes;

	public PollAnswer(String answer)
	{
		_id = PollEngine.getInstance().getPoll().getNewAnswerId();
		_answer = answer;
		_votes = 0;
	}

	protected PollAnswer(int id, String answer, int votes)
	{
		_id = id;
		_answer = answer;
		_votes = votes;
	}

	public int getId()
	{
		return _id;
	}

	public String getAnswer()
	{
		return _answer;
	}

	public int getVotes()
	{
		return _votes;
	}

	public void setVotes(int votes)
	{
		_votes = votes;
	}

	public void increaseVotes()
	{
		_votes++;
	}

	public void decreaseVotes()
	{
		if (_votes > 0)
		{
			_votes--;
		}

	}
}
