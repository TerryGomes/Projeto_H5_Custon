package ai;

import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.ai.DefaultAI;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.utils.Location;

/**
 * @author PaInKiLlEr
  * - AI for MC (32433).
  * - Screams in the chat.
  * - AI is tested and works.
 */
public class MCManager extends DefaultAI
{
	public MCManager(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtSpawn()
	{
		NpcInstance actor = getActor();
		if (actor == null)
		{
			return;
		}

		ThreadPoolManager.getInstance().schedule(new ScheduleStart(1, actor), 30000);
		super.onEvtSpawn();
	}

	private class ScheduleStart implements Runnable
	{
		private int _taskId;
		private NpcInstance _actor;

		public ScheduleStart(int taskId, NpcInstance actor)
		{
			_taskId = taskId;
			_actor = actor;
		}

		@Override
		public void run()
		{
			switch (_taskId)
			{
			case 1:
				// _actor.say(NPC_STRING.UGH_I_HAVE_BUTTERFILIES_IN_MY_STOMATCH_THE_SHOW_STARTS_SOON, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(2, _actor), 1000);
				break;
			case 2:
				// _actor.say(NPC_STRING.THANK_YOU_ALL_FOR_COMING_HERE_TONIGHT, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 6000);
				break;
			case 3:
				// _actor.say(NPC_STRING.IT_IS_AN_HONOR_TO_HAVE_THE_SPECIAL_SHOW_TODAY, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(4, _actor), 4000);
				break;
			case 4:
				// _actor.say(NPC_STRING.FANTASY_ISLE_IS_FULLY_COMITTED_TO_YOUR_HAPPINESS, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(5, _actor), 5000);
				break;
			case 5:
				// _actor.say(NPC_STRING.NOW_ID_LIKE_TO_INTRODUCE_THE_MOST_BEAUTIFUL_SINGER_IN_ADEN_PLEASE_WELCOME_LEYLA_MIRA, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(6, _actor), 3000);
				addTaskMove(new Location(-56511, -56647, -2008), true);
				doTask();
				break;
			case 6:
				// _actor.say(NPC_STRING.HERE_SHE_COMES, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 220000);
				break;
			case 7:
				// _actor.say(NPC_STRING.THANK_YOU_VERY_MUCH_LEYLA, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(8, _actor), 12000);
				addTaskMove(new Location(-56698, -56430, -2008), true);
				doTask();
				break;
			case 8:
				// _actor.say(NPC_STRING.JUST_BACK_FROM_THEIR_WORLD_TOUR_PUT_YOUR_HANDS_TOGETHER_FOR_THE_FANTAST_ISLE_CIRCUS, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(9, _actor), 3000);
				addTaskMove(new Location(-56511, -56647, -2008), true);
				doTask();
				break;
			case 9:
				// _actor.say(NPC_STRING.COME_ON_EVERYONE, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 102000);
				break;
			case 10:
				// _actor.say(NPC_STRING.DID_YOU_lIKE_IT_THAT_WAS_SO_AMAZING, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(11, _actor), 5000);
				addTaskMove(new Location(-56698, -56430, -2008), true);
				doTask();
				break;
			case 11:
				// _actor.say(NPC_STRING.NOW_WE_ALSO_INVITED_INDIVIDUALS_WITH_SPECIAL_TALENTS, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
				break;
			case 12:
				// _actor.say(NPC_STRING.LETS_WELCOME_THE_FIRST_PERSON_HERE, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 3000);
				break;
			case 13:
				// _actor.say(NPC_STRING.OH, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(14, _actor), 2000);
				break;
			case 14:
				// _actor.say(NPC_STRING.OKAY_NOW_HERE_CONES_THE_NEXT_PERSON_COME_ON_UP_PLEASE, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(15, _actor), 1000);
				break;
			case 15:
				// _actor.say(NPC_STRING.OH_IT_LOOKS_LIKE_SOMETHING_GREAT_IS_GOING_TO_HAPPEN_RIGHT, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(16, _actor), 2000);
				break;
			case 16:
				// _actor.say(NPC_STRING.OH_MY, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(17, _actor), 2000);
				break;
			case 17:
				// _actor.say(NPC_STRING.THATS_GREAT_NOW_HERE_CONES_THE_LAST_PERSON, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(18, _actor), 3000);
				break;
			case 18:
				// _actor.say(NPC_STRING.NOW_THIS_IS_THE_END_OF_TODAY_SHOW, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(19, _actor), 5000);
				break;
			case 19:
				// _actor.say(NPC_STRING.HOW_WAS_IT_I_HOPE_YOU_ALL_ENJOYED_IT, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(20, _actor), 10000);
				addTaskMove(new Location(-56698, -56340, -2008), true);
				doTask();
				break;
			case 20:
				// _actor.say(NPC_STRING.PLEASE_REMEMBER_THAT_FANTASY_ISLE_IS_ALWAYS_PLANNING_A_LOT_OF_GREAT_SHOWS_FOR_YOU, CHAT_TYPES.ALL);
				ThreadPoolManager.getInstance().schedule(new ScheduleStart(21, _actor), 10000);
				break;
			case 21:
				// _actor.say(NPC_STRING.WELL_I_WISH_I_COULD_CONTINUE_ALL_NIGHT_LONG_BUT_THIS_IS_IT_FOR_TODAY_THANK_YOU, CHAT_TYPES.ALL);
				break;
			}
		}
	}
}