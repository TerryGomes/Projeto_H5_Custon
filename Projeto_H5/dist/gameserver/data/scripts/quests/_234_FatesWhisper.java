package quests;

import java.util.List;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.instances.NpcInstance;
import l2f.gameserver.model.items.Inventory;
import l2f.gameserver.model.items.ItemInstance;
import l2f.gameserver.model.quest.Quest;
import l2f.gameserver.model.quest.QuestState;
import l2f.gameserver.scripts.Functions;
import l2f.gameserver.scripts.ScriptFile;

public class _234_FatesWhisper extends Quest implements ScriptFile
{
	// items
	private final static int PIPETTE_KNIFE = 4665;
	private final static int REIRIAS_SOUL_ORB = 4666;
	private final static int KERNONS_INFERNIUM_SCEPTER = 4667;
	private final static int GOLCONDAS_INFERNIUM_SCEPTER = 4668;
	private final static int HALLATES_INFERNIUM_SCEPTER = 4669;
	private final static int REORINS_HAMMER = 4670;
	private final static int REORINS_MOLD = 4671;
	private final static int INFERNIUM_VARNISH = 4672;
	private final static int RED_PIPETTE_KNIFE = 4673;
	private final static int STAR_OF_DESTINY = 5011;
	private final static int CRYSTAL_B = 1460;
	private final static int BLOODED_FABRIC = 4295;
	private final static int WhiteCloth = 14362;
	private final static int BloodStainedCloth = 14361;
	// Weapon B
	private final static int Damaskus = 79;
	private final static int Lance = 97;
	private final static int Samurai = 2626;
	private final static int Staff = 210;
	private final static int BOP = 287;
	private final static int Battle = 175;
	private final static int Demons = 234;
	private final static int Bellion = 268;
	private final static int Glory = 171;
	private final static int WizTear = 7889;
	private final static int GuardianSword = 7883;
	// Weapon A
	private final static int Tallum = 80;
	private final static int Infernal = 7884;
	private final static int Carnage = 288;
	private final static int Halberd = 98;
	private final static int Elemental = 150;
	private final static int Dasparion = 212;
	private final static int Spiritual = 7894;
	private final static int Bloody = 235;
	private final static int Blood = 269;
	private final static int Meteor = 2504;
	private final static int Destroyer = 7899;
	private final static int Keshanberk = 5233;
	// NPCs
	private final static int REORIN = 31002;
	private final static int CLIFF = 30182;
	private final static int FERRIS = 30847;
	private final static int ZENKIN = 30178;
	private final static int KASPAR = 30833;
	private final static int CABRIOCOFFER = 31027;
	private final static int CHEST_KERNON = 31028;
	private final static int CHEST_GOLKONDA = 31029;
	private final static int CHEST_HALLATE = 31030;
	// MOBs
	private final static int SHILLEN_MESSAGER = 25035;
	private final static int DEATH_LORD = 25220;
	private final static int KERNON = 25054;
	private final static int LONGHORN = 25126;
	private final static int BAIUM = 29020;
	private final static int GUARDIAN_ANGEL = 20859;
	private final static int SEAL_ANGEL = 20860;

	/**
	 * Save the Soul of Reiria
	 * The soul of Reiria, Maestro Reorin's dearly departed wife, is being held hostage by demons.
	 * He asks that you free her by slaying Messenger Cabrio and recovering the soul orb.
	 * He was last seen lurking in the northern part of the cemetery.
	 */
	private final static int COND1 = 1;
	/**
	 * Infernium Scepter
	 * In order to restore his honor, Maestro Reorin wishes to recover his masterpiece from the demons.
	 * He asks that you recover his three infernium scepters from Death Lord Hallate, his aide Kernon and Longhorn Golkonda, who can be found in the Tower of Insolence.
	 */
	private final static int COND2 = 2;
	/**
	 * Infernium Varnish
	 * Maestro Reorin asks that you go to Warehouse Freightman Cliff in the Town of Oren and obtain infernium varnish so he may continue his enhancement of the infernium scepters.
	 */
	private final static int COND3 = 3;
	/**
	 * Specially Made Hammer
	 * Maestro Reorin asks that you recover his hammer from Head Blacksmith Ferris in the Town of Aden, who confiscated it after expelling the maestro from the guild.
	 */
	private final static int COND4 = 4;
	/**
	 * Maestro-Level Mold
	 * Go to Trader Zenkin in the Town of Oren and recover the maestro-level mold that he bought from Reorin.
	 */
	private final static int COND5 = 5;
	/**
	 * To Hardin's Academy
	 * Trader Zenkin informs you that Maestro Reorin's mold has already been sold to Magister Kaspar of Hardin's Academy.
	 * Try to obtain the mold from him.
	 */
	private final static int COND6 = 6;
	/**
	 * Baium's Blood
	 * Magister Kaspar offers to give you Reorin's mold in exchange for a sample of the blood of Baium.
	 * Baium is the emperor of the ancient empire who is sealed in a room atop the Tower of Insolence.
	 * He is said to have turned into a beast-like creature. Magister Hanellin of the Town of Aden will tell you how to enter his room.
	 * When you attack him, you must use a pipette knife and stab him with it.
	 */
	private final static int COND7 = 7;
	/**
	 * Blood of Angel
	 * Magister Caspa gave 30 pieces of white cloth to have them stain with the blood of Platinum Faction and angels in the Tower of Insolence.
	 * Let's bring them to Caspa by staining them with the blood of angels.
	 */
	private final static int COND8 = 8;
	/**
	 * To Caspa
	 * All 30 pieces of white cloth have been stained with the blood of angel as Caspa requested. Let's go back to Caspa.
	 */
	private final static int COND9 = 9;
	/**
	 * Holding a mold in a hand
	 * The Maestro Reorin's mold has been acquired! Please go back to Reorin now.
	 */
	private final static int COND10 = 10;
	/**
	 * B Grade Crystal
	 * Many B-grade Crystals are required in order to process molten infernium. Obtain 984 crystals and deliver them to Maestro Reorin.
	 */
	private final static int COND11 = 11;
	/**
	 * Equipment Fusion
	 * In order to neutralize a hazardous energy emited from raw Infernium, you will need to mix it with a B-Grade item in to create a new alloy.
	 * Bring a B-grade weapon to Maestro Reorin.
	 */
	private final static int COND12 = 12;

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

	public _234_FatesWhisper()
	{
		super(true);

		addStartNpc(REORIN);
		addTalkId(CLIFF);
		addTalkId(FERRIS);
		addTalkId(ZENKIN);
		addTalkId(KASPAR);
		addTalkId(CABRIOCOFFER);
		addTalkId(CHEST_KERNON);
		addTalkId(CHEST_GOLKONDA);
		addTalkId(CHEST_HALLATE);

		addKillId(SHILLEN_MESSAGER);
		addKillId(DEATH_LORD);
		addKillId(KERNON);
		addKillId(LONGHORN);
		addKillId(GUARDIAN_ANGEL, SEAL_ANGEL);
		addAttackId(BAIUM);

		addQuestItem(new int[]
		{
			REIRIAS_SOUL_ORB,
			HALLATES_INFERNIUM_SCEPTER,
			KERNONS_INFERNIUM_SCEPTER,
			GOLCONDAS_INFERNIUM_SCEPTER,
			INFERNIUM_VARNISH,
			REORINS_HAMMER,
			REORINS_MOLD,
			PIPETTE_KNIFE,
			RED_PIPETTE_KNIFE
		});
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		int oldweapon = 0;
		int newweapon = 0;
		if (event.equalsIgnoreCase("31002-03.htm"))
		{
			st.setCond(COND1);
			st.setState(STARTED);
		}
		else if (event.equalsIgnoreCase("31002-05b.htm"))
		{
			st.takeItems(REIRIAS_SOUL_ORB, -1);
			st.setCond(COND2);
		}
		else if (event.equalsIgnoreCase("31030-02.htm"))
		{
			st.giveItems(HALLATES_INFERNIUM_SCEPTER, 1, false);
		}
		else if (event.equalsIgnoreCase("31028-02.htm"))
		{
			st.giveItems(KERNONS_INFERNIUM_SCEPTER, 1, false);
		}
		else if (event.equalsIgnoreCase("31029-02.htm"))
		{
			st.giveItems(GOLCONDAS_INFERNIUM_SCEPTER, 1, false);
		}
		else if (event.equalsIgnoreCase("31002-06a.htm"))
		{
			st.takeItems(HALLATES_INFERNIUM_SCEPTER, -1);
			st.takeItems(KERNONS_INFERNIUM_SCEPTER, -1);
			st.takeItems(GOLCONDAS_INFERNIUM_SCEPTER, -1);
			st.setCond(COND3);
		}
		else if (event.equalsIgnoreCase("30182-01c.htm"))
		{
			st.takeItems(INFERNIUM_VARNISH, -1);
			st.giveItems(INFERNIUM_VARNISH, 1, false);
		}
		else if (event.equalsIgnoreCase("31002-07a.htm"))
		{
			st.setCond(COND4);
		}
		else if (event.equalsIgnoreCase("31002-08a.htm"))
		{
			st.takeItems(REORINS_HAMMER, -1);
			st.setCond(COND5);
		}
		else if (event.equalsIgnoreCase("30178-01a.htm"))
		{
			st.setCond(COND6);
		}
		else if (event.equalsIgnoreCase("30833-01a.htm"))
		{
			return Config.ALT_ALLOW_SUBCLASS_WITHOUT_BAIUM ? "30833-01a.htm" : "30833-01n.htm";
		}
		else if (event.equalsIgnoreCase("30833-01b.htm"))
		{
			st.setCond(COND7);
			st.giveItems(PIPETTE_KNIFE, 1, false);
		}
		else if (event.equalsIgnoreCase("30833-01c.htm"))
		{
			st.setCond(COND8);
			st.giveItems(WhiteCloth, 30, false);
		}
		else if (event.equalsIgnoreCase("Damaskus.htm"))
		{
			oldweapon = Damaskus;
		}
		else if (event.equalsIgnoreCase("Samurai.htm"))
		{
			oldweapon = Samurai;
		}
		else if (event.equalsIgnoreCase("BOP.htm"))
		{
			oldweapon = BOP;
		}
		else if (event.equalsIgnoreCase("Lance.htm"))
		{
			oldweapon = Lance;
		}
		else if (event.equalsIgnoreCase("Battle.htm"))
		{
			oldweapon = Battle;
		}
		else if (event.equalsIgnoreCase("Staff.htm"))
		{
			oldweapon = Staff;
		}
		else if (event.equalsIgnoreCase("Demons.htm"))
		{
			oldweapon = Demons;
		}
		else if (event.equalsIgnoreCase("Bellion.htm"))
		{
			oldweapon = Bellion;
		}
		else if (event.equalsIgnoreCase("Glory.htm"))
		{
			oldweapon = Glory;
		}
		else if (event.equalsIgnoreCase("WizTear.htm"))
		{
			oldweapon = WizTear;
		}
		else if (event.equalsIgnoreCase("GuardianSword.htm"))
		{
			oldweapon = GuardianSword;
		}
		else if (event.equalsIgnoreCase("Tallum"))
		{
			newweapon = Tallum;
		}
		else if (event.equalsIgnoreCase("Infernal"))
		{
			newweapon = Infernal;
		}
		else if (event.equalsIgnoreCase("Carnage"))
		{
			newweapon = Carnage;
		}
		else if (event.equalsIgnoreCase("Halberd"))
		{
			newweapon = Halberd;
		}
		else if (event.equalsIgnoreCase("Elemental"))
		{
			newweapon = Elemental;
		}
		else if (event.equalsIgnoreCase("Dasparion"))
		{
			newweapon = Dasparion;
		}
		else if (event.equalsIgnoreCase("Spiritual"))
		{
			newweapon = Spiritual;
		}
		else if (event.equalsIgnoreCase("Bloody"))
		{
			newweapon = Bloody;
		}
		else if (event.equalsIgnoreCase("Blood"))
		{
			newweapon = Blood;
		}
		else if (event.equalsIgnoreCase("Meteor"))
		{
			newweapon = Meteor;
		}
		else if (event.equalsIgnoreCase("Destroyer"))
		{
			newweapon = Destroyer;
		}
		else if (event.equalsIgnoreCase("Keshanberk"))
		{
			newweapon = Keshanberk;
		}
		else if (event.equalsIgnoreCase("CABRIOCOFFER_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CABRIOCOFFER);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		else if (event.equalsIgnoreCase("CHEST_HALLATE_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_HALLATE);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		else if (event.equalsIgnoreCase("CHEST_KERNON_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_KERNON);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		else if (event.equalsIgnoreCase("CHEST_GOLKONDA_Fail"))
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_GOLKONDA);
			if (isQuest != null)
			{
				isQuest.deleteMe();
			}
		}
		if (oldweapon != 0)
		{
			if (st.getQuestItemsCount(oldweapon) >= 1)
			{
				if (st.getQuestItemsCount(CRYSTAL_B) >= 984)
				{
					st.set("oldweapon", String.valueOf(oldweapon));
					st.takeItems(CRYSTAL_B, 984);
					st.setCond(COND12);
				}
				else
				{
					htmltext = "cheeter.htm";
				}
			}
			else
			{
				htmltext = "noweapon.htm";
			}
		}
		if (newweapon != 0)
		{
			List<ItemInstance> olditem = st.getPlayer().getInventory().getItemsByItemId(st.getInt("oldweapon"));
			ItemInstance itemtotake = null;
			for (ItemInstance i : olditem)
			{
				if (!i.isAugmented() && i.getEnchantLevel() == 0)
				{
					itemtotake = i;
					break;
				}
			}
			if (st.getPlayer().getInventory().destroyItem(itemtotake, 1L, "_234_FatesWhisper"))
			{
				st.giveItems(newweapon, 1, false);
				st.giveItems(STAR_OF_DESTINY, 1, false);
				st.unset("cond");
				st.unset("oldweapon");
				st.playSound(SOUND_FINISH);
				htmltext = "make.htm";
				st.exitCurrentQuest(false);
			}
			else
			{
				htmltext = "noweapon.htm";
			}
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		if (st.getState() == COMPLETED)
		{
			return "completed";
		}
		int npcId = npc.getNpcId();
		String htmltext = "noquest";
		int cond = st.getCond();
		if (npcId == REORIN)
		{
			if (cond == 0)
			{
				if (st.getPlayer().getLevel() >= 75)
				{
					htmltext = "31002-02.htm";
				}
				else
				{
					htmltext = "31002-01.htm";
					st.exitCurrentQuest(true);
				}
			}
			else if (cond == COND1 && st.getQuestItemsCount(REIRIAS_SOUL_ORB) >= 1)
			{
				htmltext = "31002-05.htm";
			}
			else if (cond == COND2 && st.getQuestItemsCount(HALLATES_INFERNIUM_SCEPTER) >= 1 && st.getQuestItemsCount(KERNONS_INFERNIUM_SCEPTER) >= 1 && st.getQuestItemsCount(GOLCONDAS_INFERNIUM_SCEPTER) >= 1)
			{
				htmltext = "31002-06.htm";
			}
			else if (cond == COND3 && st.getQuestItemsCount(INFERNIUM_VARNISH) >= 1)
			{
				htmltext = "31002-07.htm";
			}
			else if (cond == COND4 && st.getQuestItemsCount(REORINS_HAMMER) >= 1)
			{
				htmltext = "31002-08.htm";
			}
			else if (cond == COND10 && st.getQuestItemsCount(REORINS_MOLD) >= 1)
			{
				st.takeItems(REORINS_MOLD, -1);
				st.setCond(COND11);
				htmltext = "31002-09.htm";
			}
			else if (cond == COND11 && st.getQuestItemsCount(CRYSTAL_B) >= 984)
			{
				htmltext = "31002-10.htm";
			}
			else if (cond == COND12)
			{
				htmltext = "a-grade.htm";
			}
		}
		else if (npcId == CABRIOCOFFER && cond == COND1 && st.getQuestItemsCount(REIRIAS_SOUL_ORB) == 0)
		{
			st.giveItems(REIRIAS_SOUL_ORB, 1, false);
			htmltext = "31027-01.htm";
		}
		else if (npcId == CHEST_HALLATE && cond == COND2 && st.getQuestItemsCount(HALLATES_INFERNIUM_SCEPTER) == 0)
		{
			htmltext = "31030-01.htm";
		}
		else if (npcId == CHEST_KERNON && cond == COND2 && st.getQuestItemsCount(KERNONS_INFERNIUM_SCEPTER) == 0)
		{
			htmltext = "31028-01.htm";
		}
		else if (npcId == CHEST_GOLKONDA && cond == COND2 && st.getQuestItemsCount(GOLCONDAS_INFERNIUM_SCEPTER) == 0)
		{
			htmltext = "31029-01.htm";
		}
		else if (npcId == CLIFF && cond == COND3 && st.getQuestItemsCount(INFERNIUM_VARNISH) == 0)
		{
			htmltext = "30182-01.htm";
		}
		else if (npcId == FERRIS && cond == COND4 && st.getQuestItemsCount(REORINS_HAMMER) == 0)
		{
			st.giveItems(REORINS_HAMMER, 1, false);
			htmltext = "30847-01.htm";
		}
		else if (npcId == ZENKIN && st.getQuestItemsCount(REORINS_MOLD) == 0 && cond == COND5)
		{
			htmltext = "30178-01.htm";
		}
		else if (npcId == KASPAR)
		{
			if (cond == COND6)
			{
				htmltext = "30833-01.htm";
			}
			else if ((cond == COND7 || cond == COND9) && st.getQuestItemsCount(RED_PIPETTE_KNIFE) == 1)
			{
				st.setCond(COND10);
				st.takeItems(RED_PIPETTE_KNIFE, -1);
				st.giveItems(REORINS_MOLD, 1, false);
				htmltext = "30833-03.htm";
			}
			else if (cond == COND9 || cond == COND8)
			{
				if (st.getQuestItemsCount(BLOODED_FABRIC) >= 30)
				{
					st.setCond(COND10);
					st.takeItems(BLOODED_FABRIC, 30);
					st.giveItems(REORINS_MOLD, 1, false);
					htmltext = "30833-03.htm";
				}
				else if (st.getQuestItemsCount(BloodStainedCloth, BLOODED_FABRIC) >= 30)
				{
					st.setCond(COND10);
					long items1 = st.takeItems(BloodStainedCloth, -1);
					if (items1 < 30)
					{
						st.takeItems(BLOODED_FABRIC, 30 - items1);
					}
					st.takeAllItems(WhiteCloth);
					st.giveItems(REORINS_MOLD, 1, false);
					htmltext = "30833-03.htm";
				}
			}
		}

		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();
		int cond = st.getCond();
		if (cond == COND1 && npcId == SHILLEN_MESSAGER)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CABRIOCOFFER);
			if (isQuest == null)
			{
				st.addSpawn(CABRIOCOFFER);
				st.playSound(SOUND_MIDDLE);
				st.startQuestTimer("CABRIOCOFFER_Fail", 120000);
			}
		}
		if (cond == COND2 && npcId == DEATH_LORD)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_HALLATE);
			if (isQuest == null)
			{
				st.addSpawn(CHEST_HALLATE);
				st.playSound(SOUND_MIDDLE);
				st.startQuestTimer("CHEST_HALLATE_Fail", 120000);
			}
		}
		if (cond == COND2 && npcId == KERNON)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_KERNON);
			if (isQuest == null)
			{
				st.addSpawn(CHEST_KERNON);
				st.playSound(SOUND_MIDDLE);
				st.startQuestTimer("CHEST_KERNON_Fail", 120000);
			}
		}
		if (cond == COND2 && npcId == LONGHORN)
		{
			NpcInstance isQuest = GameObjectsStorage.getByNpcId(CHEST_GOLKONDA);
			if (isQuest == null)
			{
				st.addSpawn(CHEST_GOLKONDA);
				st.playSound(SOUND_MIDDLE);
				st.startQuestTimer("CHEST_GOLKONDA_Fail", 120000);
			}
		}
		if (cond == COND8 && (npcId == GUARDIAN_ANGEL || npcId == SEAL_ANGEL))
		{
			long count = st.getQuestItemsCount(BloodStainedCloth);
			if (st.getQuestItemsCount(WhiteCloth) > 0 && count < 30 && Rnd.chance(33))
			{
				st.giveItems(BloodStainedCloth, 1);
				if (count >= 29)
				{
					st.takeItems(WhiteCloth, -1);
					st.setCond(COND9);
				}
				else
				{
					st.takeItems(WhiteCloth, 1);
				}
			}
		}
		return null;
	}

	@Override
	public String onAttack(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		if ((cond == COND7 || cond == COND9) && npc.getNpcId() == BAIUM && st.getQuestItemsCount(PIPETTE_KNIFE) >= 1 && st.getQuestItemsCount(RED_PIPETTE_KNIFE) == 0 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == PIPETTE_KNIFE)
		{
			if (Rnd.chance(50))
			{
				Functions.npcSay(npc, "Who dares to try steal my blood?");
			}
			st.takeItems(PIPETTE_KNIFE, -1);
			st.giveItems(RED_PIPETTE_KNIFE, 1, false);
			st.playSound(SOUND_ITEMGET);
		}
		return null;
	}
}