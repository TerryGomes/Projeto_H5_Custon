package l2f.gameserver.model.actor.listener;

import l2f.commons.listener.Listener;
import l2f.commons.listener.ListenerList;
import l2f.gameserver.ai.CtrlEvent;
import l2f.gameserver.ai.CtrlIntention;
import l2f.gameserver.listener.actor.OnAttackHitListener;
import l2f.gameserver.listener.actor.OnAttackListener;
import l2f.gameserver.listener.actor.OnCharEnterLeaveZoneListener;
import l2f.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2f.gameserver.listener.actor.OnDeathListener;
import l2f.gameserver.listener.actor.OnDeleteListener;
import l2f.gameserver.listener.actor.OnKillListener;
import l2f.gameserver.listener.actor.OnMagicHitListener;
import l2f.gameserver.listener.actor.OnMagicUseListener;
import l2f.gameserver.listener.actor.OnStatusUpdateBroadcastListener;
import l2f.gameserver.listener.actor.ai.OnAiEventListener;
import l2f.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.model.Zone;

public class CharListenerList extends ListenerList<Creature>
{
	final static ListenerList<Creature> global = new ListenerList<Creature>();

	protected final Creature actor;

	public CharListenerList(Creature actor)
	{
		this.actor = actor;
	}

	public Creature getActor()
	{
		return actor;
	}

	public final static boolean addGlobal(Listener<Creature> listener)
	{
		return global.add(listener);
	}

	public final static boolean removeGlobal(Listener<Creature> listener)
	{
		return global.remove(listener);
	}

	public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1)
	{
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnAiIntentionListener.class.isInstance(listener))
				{
					((OnAiIntentionListener) listener).onAiIntention(getActor(), intention, arg0, arg1);
				}
			}
		}
	}

	public void onAiEvent(CtrlEvent evt, Object[] args)
	{
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnAiEventListener.class.isInstance(listener))
				{
					((OnAiEventListener) listener).onAiEvent(getActor(), evt, args);
				}
			}
		}
	}

	public void onAttack(Creature target)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnAttackListener.class.isInstance(listener))
				{
					((OnAttackListener) listener).onAttack(getActor(), target);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnAttackListener.class.isInstance(listener))
				{
					((OnAttackListener) listener).onAttack(getActor(), target);
				}
			}
		}
	}

	public void onAttackHit(Creature attacker)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnAttackHitListener.class.isInstance(listener))
				{
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnAttackHitListener.class.isInstance(listener))
				{
					((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
				}
			}
		}
	}

	public void onMagicUse(Skill skill, Creature target, boolean alt)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnMagicUseListener.class.isInstance(listener))
				{
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnMagicUseListener.class.isInstance(listener))
				{
					((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
				}
			}
		}
	}

	public void onMagicHit(Skill skill, Creature caster)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnMagicHitListener.class.isInstance(listener))
				{
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnMagicHitListener.class.isInstance(listener))
				{
					((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
				}
			}
		}
	}

	public void onDeath(Creature killer)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnDeathListener.class.isInstance(listener))
				{
					((OnDeathListener) listener).onDeath(getActor(), killer);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnDeathListener.class.isInstance(listener))
				{
					((OnDeathListener) listener).onDeath(getActor(), killer);
				}
			}
		}
	}

	public void onKill(Creature victim)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
				{
					((OnKillListener) listener).onKill(getActor(), victim);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnKillListener.class.isInstance(listener) && !((OnKillListener) listener).ignorePetOrSummon())
				{
					((OnKillListener) listener).onKill(getActor(), victim);
				}
			}
		}
	}

	public void onKillIgnorePetOrSummon(Creature victim)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
				{
					((OnKillListener) listener).onKill(getActor(), victim);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnKillListener.class.isInstance(listener) && ((OnKillListener) listener).ignorePetOrSummon())
				{
					((OnKillListener) listener).onKill(getActor(), victim);
				}
			}
		}
	}

	public void onCurrentHpDamage(double damage, Creature attacker, Skill skill)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnCurrentHpDamageListener.class.isInstance(listener))
				{
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);
				}
			}
		}

		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnCurrentHpDamageListener.class.isInstance(listener))
				{
					((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);
				}
			}
		}
	}

	public void onDeleted()
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnDeleteListener.class.isInstance(listener))
				{
					((OnDeleteListener) listener).onDelete(getActor());
				}
			}
		}
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnDeleteListener.class.isInstance(listener))
				{
					((OnDeleteListener) listener).onDelete(getActor());
				}
			}
		}
	}

	public void onZoneEnter(Zone zone)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnCharEnterLeaveZoneListener.class.isInstance(listener))
				{
					((OnCharEnterLeaveZoneListener) listener).onEnter(getActor(), zone);
				}
			}
		}
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnCharEnterLeaveZoneListener.class.isInstance(listener))
				{
					((OnCharEnterLeaveZoneListener) listener).onEnter(getActor(), zone);
				}
			}
		}
	}

	public void onZoneLeave(Zone zone)
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnCharEnterLeaveZoneListener.class.isInstance(listener))
				{
					((OnCharEnterLeaveZoneListener) listener).onLeave(getActor(), zone);
				}
			}
		}
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnCharEnterLeaveZoneListener.class.isInstance(listener))
				{
					((OnCharEnterLeaveZoneListener) listener).onLeave(getActor(), zone);
				}
			}
		}
	}

	public void onStatucUpdateBroadcasted()
	{
		if (!global.getListeners().isEmpty())
		{
			for (Listener<Creature> listener : global.getListeners())
			{
				if (OnStatusUpdateBroadcastListener.class.isInstance(listener))
				{
					((OnStatusUpdateBroadcastListener) listener).onStatusUpdate(getActor());
				}
			}
		}
		if (!getListeners().isEmpty())
		{
			for (Listener<Creature> listener : getListeners())
			{
				if (OnStatusUpdateBroadcastListener.class.isInstance(listener))
				{
					((OnStatusUpdateBroadcastListener) listener).onStatusUpdate(getActor());
				}
			}
		}
	}
}
