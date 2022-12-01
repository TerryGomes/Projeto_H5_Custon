package l2mv.gameserver.handler.voicecommands.impl;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.text.StrBuilder;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2mv.gameserver.model.Creature;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.base.Element;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
import l2mv.gameserver.stats.Formulas;
import l2mv.gameserver.stats.Stats;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2mv.gameserver.utils.Strings;

public class WhoAmI implements IVoicedCommandHandler
{
	private static final String[] _commandList = new String[]
	{
		"stats"
	};

	@Override
	public String[] getVoicedCommandList()
	{
		return _commandList;
	}

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		// Synerge - Disabled for normal players
		if (!player.isGM())
		{
			return false;
		}

		Player playerToShow = player.isGM() && player.getTarget() != null && player.getTarget().isPlayer() ? player.getTarget().getPlayer() : player;
		Creature target = null;

		// TODO [G1ta0] add reflective
		// TODO [G1ta0] may want to show the stats according to the purpose
		double hpRegen = Formulas.calcHpRegen(playerToShow);
		double cpRegen = Formulas.calcCpRegen(playerToShow);
		double mpRegen = Formulas.calcMpRegen(playerToShow);
		double hpDrain = playerToShow.calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0., target, null);
		double mpDrain = playerToShow.calcStat(Stats.ABSORB_DAMAGEMP_PERCENT, 0., target, null);
		double hpGain = playerToShow.calcStat(Stats.HEAL_EFFECTIVNESS, 100., target, null);
		double mpGain = playerToShow.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., target, null);
		double critPerc = 2 * playerToShow.calcStat(Stats.CRITICAL_DAMAGE, target, null);
		double critStatic = playerToShow.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, null);
		double mCritRate = playerToShow.calcStat(Stats.MCRITICAL_RATE, target, null);
		double blowRate = playerToShow.calcStat(Stats.FATALBLOW_RATE, target, null);

		ItemInstance shld = playerToShow.getSecondaryWeaponInstance();
		boolean shield = shld != null && shld.getItemType() == WeaponType.NONE;

		double shieldDef = shield ? playerToShow.calcStat(Stats.SHIELD_DEFENCE, player.getTemplate().baseShldDef, target, null) : 0.;
		double shieldRate = shield ? playerToShow.calcStat(Stats.SHIELD_RATE, target, null) : 0.;

		double xpRate = Config.RATE_XP * playerToShow.getBonus().getRateXp();
		double spRate = Config.RATE_SP * playerToShow.getBonus().getRateSp();
		double dropRate = Config.RATE_DROP_ITEMS * playerToShow.getBonus().getDropItems();
		double adenaRate = Config.RATE_DROP_ADENA * playerToShow.getBonus().getDropAdena();
		double spoilRate = Config.RATE_DROP_SPOIL * playerToShow.getBonus().getDropSpoil();
		double fireResist = playerToShow.calcStat(Element.FIRE.getDefence(), 0., target, null);
		double windResist = playerToShow.calcStat(Element.WIND.getDefence(), 0., target, null);
		double waterResist = playerToShow.calcStat(Element.WATER.getDefence(), 0., target, null);
		double earthResist = playerToShow.calcStat(Element.EARTH.getDefence(), 0., target, null);
		double holyResist = playerToShow.calcStat(Element.HOLY.getDefence(), 0., target, null);
		double unholyResist = playerToShow.calcStat(Element.UNHOLY.getDefence(), 0., target, null);

		double bleedPower = playerToShow.calcStat(Stats.BLEED_POWER, target, null);
		double bleedResist = playerToShow.calcStat(Stats.BLEED_RESIST, target, null);
		double poisonPower = playerToShow.calcStat(Stats.POISON_POWER, target, null);
		double poisonResist = playerToShow.calcStat(Stats.POISON_RESIST, target, null);
		double stunPower = playerToShow.calcStat(Stats.STUN_POWER, target, null);
		double stunResist = playerToShow.calcStat(Stats.STUN_RESIST, target, null);
		double rootPower = playerToShow.calcStat(Stats.ROOT_POWER, target, null);
		double rootResist = playerToShow.calcStat(Stats.ROOT_RESIST, target, null);
		double sleepPower = playerToShow.calcStat(Stats.SLEEP_POWER, target, null);
		double sleepResist = playerToShow.calcStat(Stats.SLEEP_RESIST, target, null);
		double paralyzePower = playerToShow.calcStat(Stats.PARALYZE_POWER, target, null);
		double paralyzeResist = playerToShow.calcStat(Stats.PARALYZE_RESIST, target, null);
		double mentalPower = playerToShow.calcStat(Stats.MENTAL_POWER, target, null);
		double mentalResist = playerToShow.calcStat(Stats.MENTAL_RESIST, target, null);
		double debuffPower = playerToShow.calcStat(Stats.DEBUFF_POWER, target, null);
		double debuffResist = playerToShow.calcStat(Stats.DEBUFF_RESIST, target, null);
		double cancelPower = playerToShow.calcStat(Stats.CANCEL_POWER, target, null);
		double cancelResist = playerToShow.calcStat(Stats.CANCEL_RESIST, target, null);

		double swordResist = 100. - playerToShow.calcStat(Stats.SWORD_WPN_VULNERABILITY, target, null);
		double dualResist = 100. - playerToShow.calcStat(Stats.DUAL_WPN_VULNERABILITY, target, null);
		double bluntResist = 100. - playerToShow.calcStat(Stats.BLUNT_WPN_VULNERABILITY, target, null);
		double daggerResist = 100. - playerToShow.calcStat(Stats.DAGGER_WPN_VULNERABILITY, target, null);
		double bowResist = 100. - playerToShow.calcStat(Stats.BOW_WPN_VULNERABILITY, target, null);
		double crossbowResist = 100. - playerToShow.calcStat(Stats.CROSSBOW_WPN_VULNERABILITY, target, null);
		double poleResist = 100. - playerToShow.calcStat(Stats.POLE_WPN_VULNERABILITY, target, null);
		double fistResist = 100. - playerToShow.calcStat(Stats.FIST_WPN_VULNERABILITY, target, null);

		double critChanceResist = 100. - playerToShow.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, target, null);
		double critDamResistStatic = playerToShow.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, target, null);
		double critDamResist = 100. - 100 * (playerToShow.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, 1., target, null) - critDamResistStatic);

		String dialog = HtmCache.getInstance().getNotNull(player.isGM() ? "command/whoamiGM.htm" : "command/whoami.htm", player);

		NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
		df.setMaximumFractionDigits(1);
		df.setMinimumFractionDigits(1);

		StrBuilder sb = new StrBuilder(dialog);
		sb.replaceFirst("%hpRegen%", df.format(hpRegen));
		sb.replaceFirst("%cpRegen%", df.format(cpRegen));
		sb.replaceFirst("%mpRegen%", df.format(mpRegen));
		sb.replaceFirst("%hpDrain%", df.format(hpDrain));
		sb.replaceFirst("%mpDrain%", df.format(mpDrain));
		sb.replaceFirst("%hpGain%", df.format(hpGain));
		sb.replaceFirst("%mpGain%", df.format(mpGain));
		sb.replaceFirst("%critPerc%", df.format(critPerc));
		sb.replaceFirst("%critStatic%", df.format(critStatic));
		sb.replaceFirst("%mCritRate%", df.format(mCritRate));
		sb.replaceFirst("%blowRate%", df.format(blowRate));
		sb.replaceFirst("%shieldDef%", df.format(shieldDef));
		sb.replaceFirst("%shieldRate%", df.format(shieldRate));
		if (Config.show_rates)
		{
			sb.replaceFirst("%xpRate%", df.format(xpRate));
			sb.replaceFirst("%spRate%", df.format(spRate));
			sb.replaceFirst("%dropRate%", df.format(dropRate));
			sb.replaceFirst("%adenaRate%", df.format(adenaRate));
			sb.replaceFirst("%spoilRate%", df.format(spoilRate));
		}
		sb.replaceFirst("%fireResist%", df.format(fireResist));
		sb.replaceFirst("%windResist%", df.format(windResist));
		sb.replaceFirst("%waterResist%", df.format(waterResist));
		sb.replaceFirst("%earthResist%", df.format(earthResist));
		sb.replaceFirst("%holyResist%", df.format(holyResist));
		sb.replaceFirst("%darkResist%", df.format(unholyResist));
		sb.replaceFirst("%bleedPower%", df.format(bleedPower));
		sb.replaceFirst("%bleedResist%", df.format(bleedResist));
		sb.replaceFirst("%poisonPower%", df.format(poisonPower));
		sb.replaceFirst("%poisonResist%", df.format(poisonResist));
		sb.replaceFirst("%stunPower%", df.format(stunPower));
		sb.replaceFirst("%stunResist%", df.format(stunResist));
		sb.replaceFirst("%rootPower%", df.format(rootPower));
		sb.replaceFirst("%rootResist%", df.format(rootResist));
		sb.replaceFirst("%sleepPower%", df.format(sleepPower));
		sb.replaceFirst("%sleepResist%", df.format(sleepResist));
		sb.replaceFirst("%paralyzePower%", df.format(paralyzePower));
		sb.replaceFirst("%paralyzeResist%", df.format(paralyzeResist));
		sb.replaceFirst("%mentalPower%", df.format(mentalPower));
		sb.replaceFirst("%mentalResist%", df.format(mentalResist));
		sb.replaceFirst("%debuffPower%", df.format(debuffPower));
		sb.replaceFirst("%debuffResist%", df.format(debuffResist));
		sb.replaceFirst("%cancelPower%", df.format(cancelPower));
		sb.replaceFirst("%cancelResist%", df.format(cancelResist));
		sb.replaceFirst("%swordResist%", df.format(swordResist));
		sb.replaceFirst("%dualResist%", df.format(dualResist));
		sb.replaceFirst("%bluntResist%", df.format(bluntResist));
		sb.replaceFirst("%daggerResist%", df.format(daggerResist));
		sb.replaceFirst("%bowResist%", df.format(bowResist));
		sb.replaceFirst("%crossbowResist%", df.format(crossbowResist));
		sb.replaceFirst("%fistResist%", df.format(fistResist));
		sb.replaceFirst("%poleResist%", df.format(poleResist));
		sb.replaceFirst("%critChanceResist%", df.format(critChanceResist));
		sb.replaceFirst("%critDamResist%", df.format(critDamResist));

		NpcHtmlMessage msg = new NpcHtmlMessage(0);
		msg.setHtml(Strings.bbParse(sb.toString()));
		player.sendPacket(msg);

		return true;
	}
}
