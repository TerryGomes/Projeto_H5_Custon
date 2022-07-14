package quests;

import org.apache.commons.lang3.ArrayUtils;

import l2mv.gameserver.model.Effect;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.entity.Reflection;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.EventTrigger;
import l2mv.gameserver.network.serverpackets.ExStartScenePlayer;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Location;

/**
 * @author pchayka
 *         <p/>
 *         TODO: спавн минионов
 *         TODO: включение и отключение свечения у Контроллеров Границ
 */
public class _10295_SevenSignsSolinasTomb extends Quest implements ScriptFile
{
	private static final int ErisEvilThoughts = 32792;
	private static final int ElcardiaInzone1 = 32787;
	private static final int TeleportControlDevice = 32820;
	private static final int PowerfulDeviceStaff = 32838;
	private static final int PowerfulDeviceBook = 32839;
	private static final int PowerfulDeviceSword = 32840;
	private static final int PowerfulDeviceShield = 32841;
	private static final int AltarofHallowsStaff = 32857;
	private static final int AltarofHallowsSword = 32858;
	private static final int AltarofHallowsBook = 32859;
	private static final int AltarofHallowsShield = 32860;

	private static final int TeleportControlDevice2 = 32837;
	private static final int TeleportControlDevice3 = 32842;
	private static final int TomboftheSaintess = 32843;

	private static final int ScrollofAbstinence = 17228;
	private static final int ShieldofSacrifice = 17229;
	private static final int SwordofHolySpirit = 17230;
	private static final int StaffofBlessing = 17231;

	private static final int Solina = 32793;

	private static final int[] SolinaGuardians =
	{
		18952,
		18953,
		18954,
		18955
	};
	private static final int[] TombGuardians =
	{
		18956,
		18957,
		18958,
		18959
	};

	static
	{
		Location[] minions1 =
		{
			new Location(55672, -252120, -6760),
			new Location(55752, -252120, -6760),
			new Location(55656, -252216, -6760),
			new Location(55736, -252216, -6760)
		};
		Location[] minions2 =
		{
			new Location(55672, -252728, -6760),
			new Location(55752, -252840, -6760),
			new Location(55768, -252840, -6760),
			new Location(55752, -252712, -6760)
		};
		Location[] minions3 =
		{
			new Location(56504, -252840, -6760),
			new Location(56504, -252728, -6760),
			new Location(56392, -252728, -6760),
			new Location(56408, -252840, -6760)
		};
		Location[] minions4 =
		{
			new Location(56520, -252232, -6760),
			new Location(56520, -252104, -6760),
			new Location(56424, -252104, -6760),
			new Location(56440, -252216, -6760)
		};
	}

	public _10295_SevenSignsSolinasTomb()
	{
		super(false);
		addStartNpc(ErisEvilThoughts);
		addTalkId(ElcardiaInzone1, TeleportControlDevice, PowerfulDeviceStaff, PowerfulDeviceBook, PowerfulDeviceSword, PowerfulDeviceShield);
		addTalkId(AltarofHallowsStaff, AltarofHallowsSword, AltarofHallowsBook, AltarofHallowsShield);
		addTalkId(TeleportControlDevice2, TomboftheSaintess, TeleportControlDevice3, Solina);
		addQuestItem(ScrollofAbstinence, ShieldofSacrifice, SwordofHolySpirit, StaffofBlessing);
		addKillId(SolinaGuardians);
		addKillId(TombGuardians);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		String htmltext = event;
		if (event.equalsIgnoreCase("eris_q10295_5.htm"))
		{
			st.setCond(1);
			st.setState(STARTED);
			st.playSound(SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("teleport_in"))
		{
			player.teleToLocation(new Location(45512, -249832, -6760));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("teleport_out"))
		{
			player.teleToLocation(new Location(120664, -86968, -3392));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("use_staff"))
		{
			if (st.getQuestItemsCount(StaffofBlessing) > 0)
			{
				st.takeAllItems(StaffofBlessing);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18953);
				return null;
			}
			else
			{
				htmltext = "powerful_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("use_book"))
		{
			if (st.getQuestItemsCount(ScrollofAbstinence) > 0)
			{
				st.takeAllItems(ScrollofAbstinence);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18954);
				return null;
			}
			else
			{
				htmltext = "powerful_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("use_sword"))
		{
			if (st.getQuestItemsCount(SwordofHolySpirit) > 0)
			{
				st.takeAllItems(SwordofHolySpirit);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18955);
				return null;
			}
			else
			{
				htmltext = "powerful_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("use_shield"))
		{
			if (st.getQuestItemsCount(ShieldofSacrifice) > 0)
			{
				st.takeAllItems(ShieldofSacrifice);
				// TODO: remove glow from NPC
				removeInvincibility(player, 18952);
				return null;
			}
			else
			{
				htmltext = "powerful_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("altarstaff_q10295_2.htm"))
		{
			if (st.getQuestItemsCount(StaffofBlessing) == 0)
			{
				st.giveItems(StaffofBlessing, 1);
			}
			else
			{
				htmltext = "atlar_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("altarbook_q10295_2.htm"))
		{
			if (st.getQuestItemsCount(ScrollofAbstinence) == 0)
			{
				st.giveItems(ScrollofAbstinence, 1);
			}
			else
			{
				htmltext = "atlar_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("altarsword_q10295_2.htm"))
		{
			if (st.getQuestItemsCount(SwordofHolySpirit) == 0)
			{
				st.giveItems(SwordofHolySpirit, 1);
			}
			else
			{
				htmltext = "atlar_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("altarshield_q10295_2.htm"))
		{
			if (st.getQuestItemsCount(ShieldofSacrifice) == 0)
			{
				st.giveItems(ShieldofSacrifice, 1);
			}
			else
			{
				htmltext = "atlar_q10295_0.htm";
			}
		}
		else if (event.equalsIgnoreCase("teleport_solina"))
		{
			player.teleToLocation(new Location(56033, -252944, -6760));
			teleportElcardia(player);
			return null;
		}
		else if (event.equalsIgnoreCase("tombsaintess_q10295_2.htm"))
		{
			if (!player.getReflection().getDoor(21100101).isOpen())
			{
				activateTombGuards(player);
			}
			else
			{
				htmltext = "tombsaintess_q10295_3.htm";
			}
		}
		else if (event.equalsIgnoreCase("teleport_realtomb"))
		{
			player.teleToLocation(new Location(56081, -250391, -6760));
			teleportElcardia(player);
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_ELYSS_NARRATION);
			return null;
		}
		else if (event.equalsIgnoreCase("solina_q10295_4.htm"))
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("solina_q10295_8.htm"))
		{
			st.setCond(3);
			st.playSound(SOUND_MIDDLE);
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		String htmltext = "noquest";
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		Player player = st.getPlayer();
		if (player.getBaseClassId() != player.getActiveClassId())
		{
			return "no_subclass_allowed.htm";
		}
		switch (npcId)
		{
		case ErisEvilThoughts:
			switch (cond)
			{
			case 0:
			{
				QuestState qs = player.getQuestState(_10294_SevenSignsMonasteryofSilence.class);
				if (player.getLevel() >= 81 && qs != null && qs.isCompleted())
				{
					htmltext = "eris_q10295_1.htm";
				}
				else
				{
					htmltext = "eris_q10295_0a.htm";
					st.exitCurrentQuest(true);
				}
				break;
			}
			case 1:
				htmltext = "eris_q10295_6.htm";
				break;
			case 2:
				htmltext = "eris_q10295_7.htm";
				break;
			case 3:
				if (player.getLevel() >= 81)
				{
					htmltext = "eris_q10295_8.htm";
					st.addExpAndSp(125000000, 12500000);
					st.setState(COMPLETED);
					st.playSound(SOUND_FINISH);
					st.exitCurrentQuest(false);
				}
				else
				{
					htmltext = "eris_q10295_0.htm";
				}
				break;
			default:
				break;
			}
			break;
		case ElcardiaInzone1:
			htmltext = "elcardia_q10295_1.htm";
			break;
		case TeleportControlDevice:
			if (!checkGuardians(player, SolinaGuardians))
			{
				htmltext = "teleport_device_q10295_1.htm";
			}
			else
			{
				htmltext = "teleport_device_q10295_2.htm";
			}
			break;
		case PowerfulDeviceStaff:
			htmltext = "powerfulstaff_q10295_1.htm";
			break;
		case PowerfulDeviceBook:
			htmltext = "powerfulbook_q10295_1.htm";
			break;
		case PowerfulDeviceSword:
			htmltext = "powerfulsword_q10295_1.htm";
			break;
		case PowerfulDeviceShield:
			htmltext = "powerfulsheild_q10295_1.htm";
			break;
		case AltarofHallowsStaff:
			htmltext = "altarstaff_q10295_1.htm";
			break;
		case AltarofHallowsSword:
			htmltext = "altarsword_q10295_1.htm";
			break;
		case AltarofHallowsBook:
			htmltext = "altarbook_q10295_1.htm";
			break;
		case AltarofHallowsShield:
			htmltext = "altarshield_q10295_1.htm";
			break;
		case TeleportControlDevice2:
			htmltext = "teleportdevice2_q10295_1.htm";
			break;
		case TomboftheSaintess:
			htmltext = "tombsaintess_q10295_1.htm";
			break;
		case TeleportControlDevice3:
			htmltext = "teleportdevice3_q10295_1.htm";
			break;
		case Solina:
			switch (cond)
			{
			case 1:
				htmltext = "solina_q10295_1.htm";
				break;
			case 2:
				htmltext = "solina_q10295_4.htm";
				break;
			case 3:
				htmltext = "solina_q10295_8.htm";
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		Player player = st.getPlayer();
		if (ArrayUtils.contains(SolinaGuardians, npcId) && checkGuardians(player, SolinaGuardians))
		{
			player.showQuestMovie(ExStartScenePlayer.SCENE_SSQ2_SOLINA_TOMB_CLOSING);
			player.broadcastPacket(new EventTrigger(21100100, false));
			player.broadcastPacket(new EventTrigger(21100102, true));
		}
		if (ArrayUtils.contains(TombGuardians, npcId))
		{
			if (checkGuardians(player, TombGuardians))
			{
				player.getReflection().openDoor(21100018);
			}
			switch (npcId)
			{
			case 18956:
				player.getReflection().despawnByGroup("tombguards3");
				break;
			case 18957:
				player.getReflection().despawnByGroup("tombguards2");
				break;
			case 18958:
				player.getReflection().despawnByGroup("tombguards1");
				break;
			case 18959:
				player.getReflection().despawnByGroup("tombguards4");
				break;
			}
		}
		return null;
	}

	private void teleportElcardia(Player player)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == ElcardiaInzone1)
			{
				n.teleToLocation(Location.findPointToStay(player, 100));
			}
		}
	}

	private void removeInvincibility(Player player, int mobId)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (n.getNpcId() == mobId)
			{
				for (Effect e : n.getEffectList().getAllEffects())
				{
					if (e.getSkill().getId() == 6371)
					{
						e.exit();
					}
				}
			}
		}
	}

	private boolean checkGuardians(Player player, int[] npcIds)
	{
		for (NpcInstance n : player.getReflection().getNpcs())
		{
			if (ArrayUtils.contains(npcIds, n.getNpcId()) && !n.isDead())
			{
				return false;
			}
		}
		return true;
	}

	private void activateTombGuards(Player player)
	{
		Reflection r = player.getReflection();
		if (r == null || r.isDefault())
		{
			return;
		}

		r.openDoor(21100101);
		r.openDoor(21100102);
		r.openDoor(21100103);
		r.openDoor(21100104);
		r.spawnByGroup("tombguards1");
		r.spawnByGroup("tombguards2");
		r.spawnByGroup("tombguards3");
		r.spawnByGroup("tombguards4");
	}

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}
}