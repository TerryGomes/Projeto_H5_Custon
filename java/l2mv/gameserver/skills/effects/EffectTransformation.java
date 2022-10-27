package l2mv.gameserver.skills.effects;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.skills.skillclasses.Transformation;
import l2mv.gameserver.stats.Env;

public final class EffectTransformation extends Effect
{
	private final boolean isFlyingTransform;

	public EffectTransformation(Env env, EffectTemplate template)
	{
		super(env, template);
		int id = (int) template._value;
		isFlyingTransform = template.getParam().getBool("isFlyingTransform", id == 8 || id == 9 || id == 260); // TODO сделать через параметр
	}

	public EffectTransformation(Effect effect)
	{
		super(effect);
		final int id = (int) getTemplate()._value;
		isFlyingTransform = getTemplate().getParam().getBool("isFlyingTransform", id == 8 || id == 9 || id == 260);
	}

	@Override
	public boolean checkCondition()
	{
		if (!_effected.isPlayer() || (isFlyingTransform && _effected.getX() > -166168))
		{
			return false;
		}
		return super.checkCondition();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		Player player = (Player) _effected;
		player.setTransformationTemplate(getSkill().getNpcId());
		if (getSkill() instanceof Transformation)
		{
			player.setTransformationName(((Transformation) getSkill()).transformationName);
		}

		int id = (int) calc();
		if (isFlyingTransform)
		{
			boolean isVisible = player.isVisible();
			if (player.getPet() != null)
			{
				player.getPet().unSummon();
			}
			player.decayMe();
			player.setFlying(true);
			player.setLoc(player.getLoc().changeZ(300)); // Немного поднимаем чара над землей

			player.setTransformation(id);
			if (isVisible)
			{
				player.spawnMe();
			}
		}
		else
		{
			player.setTransformation(id);
		}
	}

	@Override
	public void onExit()
	{
		super.onExit();

		if (_effected.isPlayer())
		{
			Player player = (Player) _effected;

			if (getSkill() instanceof Transformation)
			{
				player.setTransformationName(null);
			}

			if (isFlyingTransform)
			{
				boolean isVisible = player.isVisible();
				player.decayMe();
				player.setFlying(false);
				player.setLoc(player.getLoc().correctGeoZ());
				player.setTransformation(0);
				if (isVisible)
				{
					player.spawnMe();
				}
			}
			else
			{
				player.setTransformation(0);
			}
		}
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}
}