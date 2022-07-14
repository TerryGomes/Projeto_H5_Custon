package npc.model;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.math.NumberUtils;

import l2mv.gameserver.ThreadPoolManager;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.Skill;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.network.serverpackets.components.CustomMessage;
import l2mv.gameserver.tables.SkillTable;
import l2mv.gameserver.templates.npc.NpcTemplate;
import l2mv.gameserver.utils.Util;

public final class DPSTestInstance extends NpcInstance
{
	private static final DecimalFormat FORMAT = new DecimalFormat("0.000");
	Map<Integer, DPSTest> _dpsMap = new HashMap<Integer, DPSTest>();

	public DPSTestInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void showChatWindow(Player player, int val, Object... arg)
	{
		final DPSTest dpsTest = _dpsMap.get(player.getObjectId());
		final long damage = dpsTest == null ? 0 : dpsTest.getDamage();
		final long petDamage = dpsTest == null ? 0 : dpsTest.getPetDamage();
		final int hitCount = dpsTest == null ? 0 : dpsTest.getHitCount();
		final int petHitCount = dpsTest == null ? 0 : dpsTest.getPetHitCount();
		final int timeInterval = dpsTest == null ? 0 : dpsTest.getTimeInterval();
		final int seconds = Math.max(1, timeInterval / 1000); // 1 to avoid divide by zero.
		Map<Integer, Integer> skillsUsed;
		if (dpsTest != null)
		{
			skillsUsed = dpsTest.getSkillsUsed();
		}
		else
		{
			skillsUsed = Collections.emptyMap();
		}
		final NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
		final StringBuilder sb = new StringBuilder();
		sb.append("<title>DPS Test Dummy</title>").append("This is a traning dummy to check your abilities. Select a time interval to test your skills.<br>")
					// .append("<center><button value=\"Reset\" action=\"bypass -h npc_%objectId%_reset\" width=\"75\" height=\"26\" back=\"L2UI_ct1.button_df\"
					// fore=\"L2UI_ct1.button_df\"></center>")
					.append("<a action=\"bypass -h npc_%objectId%_start 10\">[ 10 sec.]</a>").append("<a action=\"bypass -h npc_%objectId%_start 30\">[ 30 sec.]</a>").append("<a action=\"bypass -h npc_%objectId%_start 60\">[ 60 sec.]</a>").append("<a action=\"bypass -h npc_%objectId%_start 90\">[ 90 sec.]</a>").append("<a action=\"bypass -h npc_%objectId%_start 120\">[ 120 sec.]</a>").append("<br><br>").append("<center><font color=LEVEL>DPS Test Results:</font></center>")
					.append("<br><font color=55FF55>Total Damage:</font> " + (damage + petDamage)).append("<br1><font color=55FF55>Total DPS:</font> " + (damage + petDamage) / seconds + " DPS.").append("<br1><font color=55FF55>Hits Done:</font> " + (hitCount + petHitCount) + " hits.").append("<br1><font color=55FF55>Time Wasted:</font> " + FORMAT.format((double) timeInterval / 1000) + " seconds.").append("<br>").append("<center><font color=LEVEL>Statistics:</font></center>")
					.append("<br1><font color=55FF55>[Your Info]:</font>").append("<br1><font color=AAFFAA>Damage: <font>" + damage).append("<br1><font color=AAFFAA>DPS: <font>" + damage / seconds).append("<br1><font color=AAFFAA>Hits: <font>" + hitCount).append("<br1><font color=AAFFAA>Hits Per Second <font>" + FORMAT.format((double) hitCount / seconds)).append("<br1><font color=AAFFAA>Avg. DMG Per Hit: <font>" + (hitCount == 0 ? 0 : damage / hitCount))
					.append("<br><font color=55FF55>[Pet Info]:</font>").append("<br1><font color=AAFFAA>Damage: <font>" + petDamage).append("<br1><font color=AAFFAA>DPS: <font>" + petDamage / seconds).append("<br1><font color=AAFFAA>Hits: <font>" + petHitCount).append("<br1><font color=AAFFAA>Hits Per Second <font>" + FORMAT.format((double) petHitCount / seconds)).append("<br1><font color=AAFFAA>Avg. DMG Per Hit: <font>" + (petHitCount == 0 ? 0 : petDamage / petHitCount))
					.append("<br><font color=55FF55>[Skills Used]:</font>").append("<table width=280>");
		for (final Entry<Integer, Integer> entry : skillsUsed.entrySet())
		{
			sb.append("<tr><td width=200>").append(SkillTable.getInstance().getInfo(entry.getKey(), 1).getName()).append("</td><td>").append(entry.getValue()).append(" times. </td></tr>");
		}
		sb.append("</table>");
		msg.setHtml(sb.toString());
		player.sendPacket(msg);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!canBypassCheck(player, this))
		{
			return;
		}
		if (command.startsWith("reset"))
		{
			_dpsMap.remove(player.getObjectId());
			// Original Message: DPS Information reset. Time interval set to unlimited. The counter will begin on the first attack.
			player.sendMessage(new CustomMessage("scripts.npc.model.DPSTestInstance.message1", player));
		}
		if (command.startsWith("start"))
		{
			int timeInSecs = NumberUtils.toInt(command.substring(6));
			timeInSecs = Util.constrain(timeInSecs, 1, 120); // Limit the time from 1 to 120 secs.
			_dpsMap.put(player.getObjectId(), new DPSTest(timeInSecs));
			// Original Message: DPS Test timer set to " + timeInSecs + " seconds. The counter will begin on the first attack.
			player.sendMessage(new CustomMessage("scripts.npc.model.DPSTestInstance.message2", player).addNumber(timeInSecs));
		}
		// showChatWindow(player, 0);
	}

	@Override
	protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp)
	{
		if (attacker.isPlayable())
		{
			final DPSTest dpsTest = _dpsMap.get(attacker.getPlayer().getObjectId());
			if (dpsTest == null)
			{
				return;// dpsTest = new DPSTest(0);
			}
			dpsTest.addDamage((int) damage, skill, !attacker.isPlayer());
			_dpsMap.put(attacker.getPlayer().getObjectId(), dpsTest);
		}
		super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
	}

	@Override
	public boolean isAttackable(Creature attacker)
	{
		return true;
	}

	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		if (!attacker.isPlayable())
		{
			return false;
		}
		final DPSTest dpsTest = _dpsMap.get(attacker.getPlayer().getObjectId());
		return dpsTest != null && !dpsTest.isBlocked();
	}

	@Override
	public boolean isInvul()
	{
		return false;
	}

	@Override
	public boolean isImmortal()
	{
		return true;
	}

	private static class DPSTest
	{
		private long startTime;
		private long endTime;
		private long damage;
		private int hitCount;
		private long petDamage;
		private int petHitCount;
		private boolean isBlocked;
		private int testTime;
		Map<Integer, Integer> skillsUsed; // skillId, timesUsed.

		public DPSTest(int testTimeInSecs)
		{
			damage = 0;
			hitCount = 0;
			skillsUsed = new TreeMap<Integer, Integer>(Collections.reverseOrder());
			isBlocked = false;
			testTime = testTimeInSecs * 1000;
		}

		public void addDamage(int dmg, Skill skill, boolean isPet)
		{
			if (isBlocked)
			{
				return;
			}
			if (startTime == 0)
			{
				startTime = System.currentTimeMillis();
			}
			if (testTime > 0)
			{
				synchronized (this) // Synch cause dem players like to whack they keyboard. Prevent double-timer launch.
				{
					if (testTime > 0)
					{
						ThreadPoolManager.getInstance().schedule(new Runnable()
						{
							@Override
							public void run()
							{
								isBlocked = true;
							}
						}, testTime);
						testTime = 0; // Meh, I have to mark somehow if the times has been started to prevent it from starting again.
					}
				}
			}
			if (isPet)
			{
				petDamage += dmg;
				petHitCount++;
			}
			else
			{
				damage += dmg;
				hitCount++;
			}
			if (skill != null)
			{
				final int timesUsed = skillsUsed.get(skill.getId()) == null ? 0 : skillsUsed.get(skill.getId());
				skillsUsed.put(skill.getId(), timesUsed + 1);
			}
			endTime = System.currentTimeMillis();
		}

		public int getTimeInterval()
		{
			return (int) (endTime - startTime);
		}

		public long getDamage()
		{
			return damage;
		}

		public long getPetDamage()
		{
			return petDamage;
		}

		public int getHitCount()
		{
			return hitCount;
		}

		public int getPetHitCount()
		{
			return petHitCount;
		}

		public boolean isBlocked()
		{
			return isBlocked;
		}

		public Map<Integer, Integer> getSkillsUsed()
		{
			return skillsUsed;
		}
	}
}
