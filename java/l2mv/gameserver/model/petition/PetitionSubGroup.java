package l2mv.gameserver.model.petition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2mv.gameserver.handler.petition.IPetitionHandler;
import l2mv.gameserver.scripts.Scripts;

public class PetitionSubGroup extends PetitionGroup
{
	private static final Logger LOG = LoggerFactory.getLogger(PetitionSubGroup.class);
	private final IPetitionHandler _handler;

	public PetitionSubGroup(int id, String handler)
	{
		super(id);

		Class<?> clazz = Scripts.getInstance().getClasses().get("handler.petition." + handler);

		try
		{
			_handler = (IPetitionHandler) clazz.newInstance();
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			LOG.error("Error while creating PetitionSubGroup: ", e);
			throw new Error(e);
		}
	}

	public IPetitionHandler getHandler()
	{
		return _handler;
	}
}
