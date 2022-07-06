package l2mv.gameserver.fandc.facebook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.commons.threading.RunnableImpl;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.listener.game.OnConfigsReloaded;
import l2mv.gameserver.utils.Log;

public class ActionsExtractingManager implements OnConfigsReloaded
{
	private static final Logger LOG = LoggerFactory.getLogger(ActionsExtractingManager.class);

	private final List<ActionsExtractor> _extractors;
	private ScheduledFuture<?> _extractionThread;

	private ActionsExtractingManager()
	{
		_extractors = new ArrayList<ActionsExtractor>(FacebookActionType.values().length + 1);
	}

	public void load()
	{
		checkExtractionThreadStatus();
	}

	public void onActionExtracted(FacebookAction action)
	{
		Log.logFacebook("Extracted " + action);
		final boolean foundOwner = ActiveTasksHandler.getInstance().checkTaskCompleted(action);
		if (!foundOwner)
		{
			ActionsAwaitingOwner.getInstance().addNewExtractedAction(action);
		}
	}

	public void addExtractor(ActionsExtractor extractor)
	{
		_extractors.add(extractor);
	}

	public void addExtractor(ActionsExtractor extractor, boolean extractImmediately)
	{
		_extractors.add(extractor);
		if (extractImmediately && ConfigHolder.getBool("AllowFacebookRewardSystem"))
		{
			extractSpecific(extractor);
		}
	}

	public ActionsExtractor getExtractor(String extractorName)
	{
		for (ActionsExtractor extractor : _extractors)
		{
			if (extractor.getClass().getSimpleName().equalsIgnoreCase(extractorName))
			{
				return extractor;
			}
		}

		return null;
	}

	private void extractAll()
	{
		final String token = ConfigHolder.getString("FacebookToken");
		for (ActionsExtractor extractor : _extractors)
		{
			try
			{
				extractor.extractData(token);
			}
			catch (IOException e)
			{
				ActionsExtractingManager.LOG.error("IOException while parsing " + extractor, e);
			}
		}
	}

	private void extractSpecific(ActionsExtractor extractor)
	{
		try
		{
			extractor.extractData(ConfigHolder.getString("FacebookToken"));
		}
		catch (IOException e)
		{
			ActionsExtractingManager.LOG.error("IOException while parsing " + extractor, e);
		}
	}

	public static void onActionDisappeared(FacebookAction removedAction, boolean completed)
	{
		if (completed)
		{
			CompletedTasksHistory.getInstance().removeCompletedTask(removedAction, true);
			removedAction.getExecutor().addNegativePoint(removedAction.getActionType(), true);
		}
		else
		{
			ActionsAwaitingOwner.getInstance().removeAction(removedAction);
		}
		Log.logFacebook("Action Disappeared: " + removedAction + ". Completed: " + completed);
	}

	private void checkExtractionThreadStatus()
	{
		if (ConfigHolder.getBool("AllowFacebookRewardSystem"))
		{
			if (_extractionThread == null)
			{
				final long delay = ConfigHolder.getLong("FacebookExtractionDelay");
				_extractionThread = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new ExtractionThread(), 0L, delay);
			}
		}
		else if (_extractionThread != null)
		{
			_extractionThread.cancel(false);
		}
	}

	@Override
	public void onConfigsReloaded()
	{
		checkExtractionThreadStatus();
	}

	public static ActionsExtractingManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		private static final ActionsExtractingManager INSTANCE = new ActionsExtractingManager();
	}

	private static class ExtractionThread extends RunnableImpl
	{
		@Override
		public void runImpl()
		{
			ActionsExtractingManager.getInstance().extractAll();
		}
	}
}
