package l2f.gameserver.handler.voicecommands.impl.BotReport;

import l2f.gameserver.model.Player;

/**
 * Captcha Event that contains
 */
public class CaptchaEvent
{
	private final String _actorName;
	private final String _targetName;
	private final String _correctCaptcha;
	private final long _startDate;

	public CaptchaEvent(Player actor, Player target, String correctCaptcha, long startDate)
	{
		_actorName = actor.getName();
		_targetName = target.getName();
		_correctCaptcha = correctCaptcha;
		_startDate = startDate;
	}

	public String getActorName()
	{
		return _actorName;
	}

	public String getTargetName()
	{
		return _targetName;
	}

	public String getCorrectCaptcha()
	{
		return _correctCaptcha;
	}

	public long getStartDate()
	{
		return _startDate;
	}
}