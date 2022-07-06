package l2mv.gameserver.ai;

/**
 * This class contains an enum of each possibles evenements that can happen on an AI character.
 */

public enum CtrlEvent
{
	/** Something has changed, usually a previous step has being completed
	 * or maybe was completed, the AI must thing on next action
	 */
	EVT_THINK,
	/** The actor was attacked. This event comes each time a physical or magical
	 * attack was done on the actor. NPC may start attack in responce, or ignore
	 * this event if they already attack someone, or change target and so on.
	 */
	EVT_ATTACKED, EVT_CLAN_ATTACKED,
	/** Increase/decrease aggression towards a target, or reduce global aggression if target is null */
	EVT_AGGRESSION,
	/** An event that previous action was completed. The action may be an attempt
	 * to physically/magically hit an enemy, or an action that discarded
	 * attack attempt has finished. */
	EVT_READY_TO_ACT,
	/** The actor arrived to assigned location, or it's a time to modify
	 * movement destination (follow, interact, random move and others intentions). */
	EVT_ARRIVED, EVT_ARRIVED_TARGET,
	/** The actor cannot move anymore. */
	EVT_ARRIVED_BLOCKED,
	/** Forgets an object (if it's used as attack target, follow target and so on */
	EVT_FORGET_OBJECT,
	/** The character is dead */
	EVT_DEAD,
	/** The character looks like dead */
	EVT_FAKE_DEATH,
	/** The character finish casting **/
	EVT_FINISH_CASTING, EVT_SEE_SPELL, EVT_SPAWN, EVT_DESPAWN, EVT_TIMER, EVT_TELEPORTED
}
