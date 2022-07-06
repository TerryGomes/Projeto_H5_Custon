package services.community;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import l2mv.gameserver.Config;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.listener.actor.player.OnAnswerListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Party;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.SubClass;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.Experience;
import l2mv.gameserver.network.serverpackets.ConfirmDlg;
import l2mv.gameserver.network.serverpackets.JoinParty;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.Functions;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.utils.Util;

public class CommunityPartyMatching extends Functions implements ScriptFile, ICommunityBoardHandler
{
	private static final int CHECKED_COUNT = 9; // last checked + 1
	private static final int MAX_PER_PAGE = 14;

	@Override
	public String[] getBypassCommands()
	{
		return new String[]
		{
			"_partymatching"
		};
	}

	@Override
	public void onBypassCommand(Player player, String bypass)
	{
		// Bypass: bbslink_class_sort_asc_charpage_char_classpage sometimes _invClassId

		StringTokenizer st = new StringTokenizer(bypass, "_");
		String mainStringToken = st.nextToken(); // bbslink
		if (mainStringToken.equals("partymatching"))
		{
			if (!st.hasMoreTokens())
			{
				showMainPage(player, 0, 0, 0, 0, 0);
			}
			else
			{
				int classesSortType = Integer.parseInt(st.nextToken());
				int sortType = Integer.parseInt(st.nextToken());
				int asc = Integer.parseInt(st.nextToken());
				int page = Integer.parseInt(st.nextToken());
				int charObjId = Integer.parseInt(st.nextToken());
				showMainPage(player, classesSortType, sortType, asc, page, charObjId);

				if (st.hasMoreTokens())
				{
					int nextNumber = Integer.parseInt(st.nextToken());

					if (nextNumber == -1) // Show/Hide on list
					{
						player.setPartyMatchingVisible();
						if (player.isPartyMatchingVisible())
						{
							player.sendMessage("You are now visible on Party Matching list!");
						}
						else
						{
							player.sendMessage("You are NO LONGER visible on Party Matching list!");
						}
						showMainPage(player, classesSortType, sortType, asc, page, charObjId);
					}
					else // Invite to party
					{
						Player invited = GameObjectsStorage.getPlayer(charObjId);
						if (invited != null && player != invited && invited.getParty() == null)
						{
							String partyMsg = canJoinParty(invited);
							if (partyMsg.isEmpty())
							{
								ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 60000).addString("Do you want to join " + player.getName() + "'s party?");
								invited.ask(packet, new InviteAnswer(invited, player));
								player.sendMessage("Invitation has been sent!");
							}
							else
							{
								player.sendMessage(partyMsg);
							}
						}
					}
				}
			}
		}
	}

	private void showMainPage(Player player, int classesSortType, int sortType, int asc, int page, int charObjId)
	{
		String html = HtmCache.getInstance().getNotNull(Config.BBS_HOME_DIR + "bbs_partymatching.htm", player);
		html = html.replace("%characters%", getCharacters(player, sortType, asc, classesSortType, page, charObjId));
		html = html.replace("%visible%", player.isPartyMatchingVisible() ? "Hide from list" : "Show on list");
		html = replace(html, classesSortType, sortType, asc, page, charObjId);

		for (int i = 0; i < CHECKED_COUNT; i++)
		{
			html = html.replace("%checked" + i + "%", getChecked(i, classesSortType));
		}

		ShowBoard.separateAndSend(html, player);
	}

	private String replace(String text, int classesSortType, int sortType, int asc, int page, int charObjId)
	{
		text = text.replace("%class%", String.valueOf(classesSortType));
		text = text.replace("%sort%", String.valueOf(sortType));
		text = text.replace("%asc%", String.valueOf(asc));
		text = text.replace("%asc2%", String.valueOf(asc == 0 ? 1 : 0));
		text = text.replace("%page%", String.valueOf(page));
		text = text.replace("%char%", String.valueOf(charObjId));
		return text;
	}

	private String getCharacters(Player visitor, int charSort, int asc, int classSort, int page, int charToView)
	{
		String html = "";
		List<Player> allPlayers = getPlayerList(visitor, charSort, asc, classSort);
		int badCharacters = 0;
		boolean isThereNextPage = true;

		for (int i = MAX_PER_PAGE * page; i < (MAX_PER_PAGE + badCharacters + page * MAX_PER_PAGE); i++)
		{
			if (allPlayers.size() <= i)
			{
				isThereNextPage = false;
				break;
			}
			Player player = allPlayers.get(i);

			if (!isClassTestPassed(player, classSort))
			{
				badCharacters++;
				continue;
			}

			html += "<table bgcolor=" + getLineColor(i) + " width=760 border=0 cellpadding=0 cellspacing=0><tr>";
			html += "<td width=180><center><font color=" + getTextColor(i) + ">" + player.getName() + "</font></center></td>";
			html += "<td width=130><center><font color=" + getTextColor(i) + ">" + Util.getFullClassName(player.getClassId()) + "</font></center></td>";
			html += "<td width=75><center><font color=" + getTextColor(i) + ">" + player.getLevel() + "</font></center></td>";
			html += "<td width=75><center><font color=" + getTextColor(i) + ">" + (player.getBaseClassId() == player.getActiveClassId() ? "Yes" : "No") + "</font></center></td>";
			html += "<td width=180><center><font color=" + getTextColor(i) + ">" + (player.getClan() != null ? player.getClan().getName() : "<br>") + "</font></center></td>";
			if (!player.equals(visitor) || player.getParty() != null)
			{
				html += "<td width=120><center><button value=\"Invite\" action=\"bypass _partymatching_%class%_%sort%_%asc%_%page%_" + player.getObjectId() + "_0\" width=70 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"><center></td>";
			}
			else
			{
				html += "<td width=120><br></td>";
			}

			html += "</tr></table>";
		}
		html += "<center><table><tr>";
		if (page > 0)
		{
			html += "<td><button value=\"Prev\" action=\"bypass _partymatching_%class%_%sort%_%asc%_" + (page - 1) + "_%char%\" width=80 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>";
		}
		if (isThereNextPage)
		{
			html += "<td><button value=\"Next\" action=\"bypass _partymatching_%class%_%sort%_%asc%_" + (page + 1) + "_%char%\" width=80 height=18 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>";
		}
		html += "</tr></table></center>";
		return html;
	}

	private boolean containsClass(ClassId[] group, int clazz)
	{
		for (ClassId classInGroup : group)
		{
			if (clazz == classInGroup.getId())
			{
				return true;
			}
		}
		return false;
	}

	private boolean isClassTestPassed(Player player, int classSortType)
	{

		for (ClassId clazz : getNeededClasses(classSortType))
		{
			for (SubClass sub : player.getSubClasses().values())
			{
				if (clazz.getId() == sub.getClassId())
				{
					return true;
				}
			}
		}
		return false;
	}

	private List<Player> getPlayerList(Player player, int sortType, int asc, int classSortType)
	{

		List<Player> allPlayers = new ArrayList<>();
		if (classSortType == 8) // Party
		{
			if (player.getParty() == null)
			{
				allPlayers.add(player);
			}
			else
			{
				for (Player member : player.getParty().getMembers())
				{
					allPlayers.add(member);
				}
			}
		}
		else
		{
			for (Player singlePlayer : GameObjectsStorage.getAllPlayersForIterate())
			{
				String party_checker = canJoinParty(singlePlayer);
				if (party_checker.isEmpty())
				{
					if (!isClassTestPassed(singlePlayer, classSortType))
					{
						continue;
					}
					else
					{
						allPlayers.add(singlePlayer);
					}
				}
			}
		}

		Collections.sort(allPlayers, new CharComparator(sortType, classSortType, asc));
		return allPlayers;
	}

	private class CharComparator implements Comparator<Player>
	{
		int _type;
		int _classType;
		int _asc;

		private CharComparator(int sortType, int classType, int asc)
		{
			_type = sortType;
			_classType = classType;
			_asc = asc;
		}

		@Override
		public int compare(Player o1, Player o2)
		{
			if (_asc == 1)
			{
				Player temp = o1;
				o1 = o2;
				o2 = temp;
			}
			switch (_type)
			{
			case 0:
				return o1.getName().compareTo(o2.getName());
			case 1:
				return ((Integer) getMaxLevel(o2, _classType)).compareTo((getMaxLevel(o1, _classType)));
			case 2:
				return ((Integer) getUnlocksSize(o2, _classType)).compareTo((getUnlocksSize(o1, _classType)));
			default:
				break;
			}
			return 0;
		}
	}

	private int getMaxLevel(Player player, int classSortType)
	{
		ClassId[] group = getNeededClasses(classSortType);
		int maxLevel = 0;

		for (SubClass sub : player.getSubClasses().values())
		{
			if (!containsClass(group, sub.getClassId()))
			{
				continue;
			}
			int level = Experience.getLevel(sub.getExp());
			if (level > maxLevel)
			{
				maxLevel = level;
			}
		}
		return maxLevel;
	}

	private int getUnlocksSize(Player player, int classSortType)
	{
		return player.getSubClasses().size();
	}

	private static String canJoinParty(Player player)
	{
		String name = player.getName();
		if (player.isGM())
		{
			return "Don't invite GMs...";
		}
		if (player.getParty() != null)
		{
			return name + " has already found a party.";
		}
		if (player.isInOfflineMode())
		{
			return name + " is offline.";
		}
		if (player.isInOlympiadMode())
		{
			return name + " is currently fighting in the Olympiad.";
		}
		if (player.isInObserverMode())
		{
			return name + " is currently observing an Olympiad Match.";
		}
		if (player.getCursedWeaponEquippedId() != 0)
		{
			return name + " cannot join the party because he is holding a cursed weapon.";
		}
		if (!player.isPartyMatchingVisible())
		{
			return name + " doesn't want to join any party.";
		}
		if (player.getPrivateStoreType() > 0)
		{
			return name + " cannot join the party because he is currently having a private store.";
		}
		return "";
	}

	private ClassId[] getNeededClasses(int type)
	{
		switch (type)
		{
		case 0: // All
			return ClassId.values();
		case 1: // Buffers
			ClassId[] classes =
			{
				ClassId.inspector,
				ClassId.judicator,
				ClassId.oracle,
				ClassId.orcShaman,
				ClassId.prophet,
				ClassId.warcryer,
				ClassId.overlord,
				ClassId.shillienElder,
				ClassId.shillienSaint,
				ClassId.hierophant,
				ClassId.evaSaint,
				ClassId.shillienSaint,
				ClassId.dominator,
				ClassId.doomcryer
			};
			return classes;
		case 2: // BD
			return new ClassId[]
			{
				ClassId.bladedancer,
				ClassId.spectralDancer
			};
		case 3: // SWS
			return new ClassId[]
			{
				ClassId.swordSinger,
				ClassId.swordMuse
			};
		case 4: // Healers
			return new ClassId[]
			{
				ClassId.bishop,
				ClassId.shillienElder,
				ClassId.cardinal,
				ClassId.evaSaint,
				ClassId.shillienSaint
			};
		case 5: // Tanks
			return new ClassId[]
			{
				ClassId.knight,
				ClassId.darkAvenger,
				ClassId.paladin,
				ClassId.palusKnight,
				ClassId.shillienKnight,
				ClassId.shillienTemplar,
				ClassId.phoenixKnight,
				ClassId.hellKnight,
				ClassId.evaTemplar,
				ClassId.shillienTemplar
			};
		case 6: // Mage DD
			return new ClassId[]
			{
				ClassId.elvenMage,
				ClassId.mage,
				ClassId.orcShaman,
				ClassId.darkMage,
				ClassId.wizard,
				ClassId.warcryer,
				ClassId.overlord,
				ClassId.spellsinger,
				ClassId.spellhowler,
				ClassId.necromancer,
				ClassId.sorceror,
				ClassId.archmage,
				ClassId.soultaker,
				ClassId.arcanaLord,
				ClassId.mysticMuse,
				ClassId.elementalMaster,
				ClassId.stormScreamer,
				ClassId.spectralMaster,
				ClassId.dominator,
				ClassId.doomcryer
			};
		case 7: // Fighter DD
			return new ClassId[]
			{
				ClassId.inspector,
				ClassId.judicator,
				ClassId.abyssWalker,
				ClassId.swordSinger,
				ClassId.swordMuse,
				ClassId.assassin,
				ClassId.berserker,
				ClassId.bountyHunter,
				ClassId.artisan,
				ClassId.arbalester,
				ClassId.darkFighter,
				ClassId.destroyer,
				ClassId.doombringer,
				ClassId.elvenFighter,
				ClassId.darkFighter,
				ClassId.dreadnought,
				ClassId.warlord,
				ClassId.warsmith,
				ClassId.warrior,
				ClassId.femaleSoldier,
				ClassId.bladedancer,
				ClassId.spectralDancer,
				ClassId.femaleSoulbreaker,
				ClassId.femaleSoulhound,
				ClassId.maleSoldier,
				ClassId.maleSoulbreaker,
				ClassId.maleSoulhound,
				ClassId.maestro,
				ClassId.hawkeye,
				ClassId.treasureHunter,
				ClassId.titan,
				ClassId.trickster,
				ClassId.trooper,
				ClassId.tyrant,
				ClassId.gladiator,
				ClassId.duelist,
				ClassId.phantomRanger,
				ClassId.plainsWalker,
				ClassId.rogue,
				ClassId.silverRanger,
				ClassId.orcRaider,
				ClassId.orcFighter,
				ClassId.orcMonk,
				ClassId.dreadnought,
				ClassId.duelist,
				ClassId.adventurer,
				ClassId.sagittarius,
				ClassId.windRider,
				ClassId.moonlightSentinel,
				ClassId.ghostHunter,
				ClassId.ghostSentinel,
				ClassId.titan,
				ClassId.grandKhauatari,
				ClassId.fortuneSeeker
			};
		}
		return ClassId.values();
	}

	private String getChecked(int i, int classSortType)
	{
		if (classSortType == i)
		{
			return "L2UI.Checkbox_checked";
		}

		return "L2UI.CheckBox";
	}

	private String getLineColor(int i)
	{
		if (i % 2 == 0)
		{
			return "18191e";
		}

		return "22181a";
	}

	private String getTextColor(int i)
	{
		if (i % 2 == 0)
		{
			return "8f3d3f";
		}

		return "327b39";
	}

	public static class InviteAnswer implements OnAnswerListener
	{
		private final Player _invited;
		private final Player _inviter;

		public InviteAnswer(Player invited, Player inviter)
		{
			_invited = invited;
			_inviter = inviter;
		}

		@Override
		public void sayYes()
		{
			String inviteMsg = canJoinParty(_invited);
			if (!inviteMsg.isEmpty())
			{
				_inviter.sendMessage(inviteMsg);
				return;
			}
			// Joining Party
			Party party = _inviter.getParty();
			if (party == null)
			{
				_inviter.setParty(party = new Party(_inviter, 0));
			}
			_invited.joinParty(party);
			_invited.sendPacket(JoinParty.SUCCESS);
			_inviter.sendPacket(JoinParty.SUCCESS);
		}

		@Override
		public void sayNo()
		{
			_inviter.sendMessage(_invited.getName() + " declined your party request!");
		}
	}

	@Override
	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
	}

	@Override
	public void onLoad()
	{
		CommunityBoardManager.getInstance().registerHandler(this);
	}

	@Override
	public void onReload()
	{
		CommunityBoardManager.getInstance().removeHandler(this);
	}

	@Override
	public void onShutdown()
	{

	}
}
