package quests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import l2mv.gameserver.Config;
import l2mv.gameserver.ConfigHolder;
import l2mv.gameserver.data.htm.HtmCache;
import l2mv.gameserver.data.xml.holder.ItemHolder;
import l2mv.gameserver.handler.bbs.CommunityBoardManager;
import l2mv.gameserver.handler.bbs.ICommunityBoardHandler;
import l2mv.gameserver.hwid.HwidGamer;
import l2mv.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2mv.gameserver.model.GameObjectsStorage;
import l2mv.gameserver.model.Player;
import l2mv.gameserver.model.actor.instances.player.ShortCut;
import l2mv.gameserver.model.actor.listener.CharListenerList;
import l2mv.gameserver.model.base.ClassId;
import l2mv.gameserver.model.base.Race;
import l2mv.gameserver.model.entity.ChangeLogManager;
import l2mv.gameserver.model.entity.CCPHelpers.CCPSecondaryPassword;
import l2mv.gameserver.model.entity.forum.ForumBoard;
import l2mv.gameserver.model.entity.forum.ForumBoardType;
import l2mv.gameserver.model.entity.forum.ForumHandler;
import l2mv.gameserver.model.entity.forum.ForumPost;
import l2mv.gameserver.model.instances.NpcInstance;
import l2mv.gameserver.model.instances.SchemeBufferInstance;
import l2mv.gameserver.model.items.ItemInstance;
import l2mv.gameserver.model.mail.Mail;
import l2mv.gameserver.model.quest.Quest;
import l2mv.gameserver.model.quest.QuestState;
import l2mv.gameserver.network.serverpackets.ExShowScreenMessage;
import l2mv.gameserver.network.serverpackets.MagicSkillUse;
import l2mv.gameserver.network.serverpackets.RadarControl;
import l2mv.gameserver.network.serverpackets.Say2;
import l2mv.gameserver.network.serverpackets.ShortCutRegister;
import l2mv.gameserver.network.serverpackets.ShowBoard;
import l2mv.gameserver.network.serverpackets.SystemMessage2;
import l2mv.gameserver.network.serverpackets.TutorialShowHtml;
import l2mv.gameserver.network.serverpackets.components.ChatType;
import l2mv.gameserver.network.serverpackets.components.SystemMsg;
import l2mv.gameserver.scripts.ScriptFile;
import l2mv.gameserver.taskmanager.GlobalPvPZoneTaskManager;
import l2mv.gameserver.templates.item.ItemTemplate;
import l2mv.gameserver.templates.item.ItemTemplate.Grade;
import l2mv.gameserver.templates.item.WeaponTemplate;
import l2mv.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2mv.gameserver.utils.ItemFunctions;
import l2mv.gameserver.utils.Util;

public class _255_Tutorial extends Quest implements ScriptFile, OnPlayerEnterListener
{
	// table for Quest Timer ( Ex == -2 ) [raceId, voice, html]
	public final String[][] QTEXMTWO =
	{
		{
			"0",
			"tutorial_voice_001a",
			"tutorial_human_fighter001.htm"
		},
		{
			"10",
			"tutorial_voice_001b",
			"tutorial_human_mage001.htm"
		},
		{
			"18",
			"tutorial_voice_001c",
			"tutorial_elven_fighter001.htm"
		},
		{
			"25",
			"tutorial_voice_001d",
			"tutorial_elven_mage001.htm"
		},
		{
			"31",
			"tutorial_voice_001e",
			"tutorial_delf_fighter001.htm"
		},
		{
			"38",
			"tutorial_voice_001f",
			"tutorial_delf_mage001.htm"
		},
		{
			"44",
			"tutorial_voice_001g",
			"tutorial_orc_fighter001.htm"
		},
		{
			"49",
			"tutorial_voice_001h",
			"tutorial_orc_mage001.htm"
		},
		{
			"53",
			"tutorial_voice_001i",
			"tutorial_dwarven_fighter001.htm"
		},
		{
			"123",
			"tutorial_voice_001k",
			"tutorial_kamael_male001.htm"
		},
		{
			"124",
			"tutorial_voice_001j",
			"tutorial_kamael_female001.htm"
		}
	};

	// table for Client Event Enable (8) [raceId, html, x, y, z]
	public final String[][] CEEa =
	{
		{
			"0",
			"tutorial_human_fighter007.htm",
			"-71424",
			"258336",
			"-3109"
		},
		{
			"10",
			"tutorial_human_mage007.htm",
			"-91036",
			"248044",
			"-3568"
		},
		{
			"18",
			"tutorial_elf007.htm",
			"46112",
			"41200",
			"-3504"
		},
		{
			"25",
			"tutorial_elf007.htm",
			"46112",
			"41200",
			"-3504"
		},
		{
			"31",
			"tutorial_delf007.htm",
			"28384",
			"11056",
			"-4233"
		},
		{
			"38",
			"tutorial_delf007.htm",
			"28384",
			"11056",
			"-4233"
		},
		{
			"44",
			"tutorial_orc007.htm",
			"-56736",
			"-113680",
			"-672"
		},
		{
			"49",
			"tutorial_orc007.htm",
			"-56736",
			"-113680",
			"-672"
		},
		{
			"53",
			"tutorial_dwarven_fighter007.htm",
			"108567",
			"-173994",
			"-406"
		},
		{
			"123",
			"tutorial_kamael007.htm",
			"-125872",
			"38016",
			"1251"
		},
		{
			"124",
			"tutorial_kamael007.htm",
			"-125872",
			"38016",
			"1251"
		}
	};

	// table for Question Mark Clicked (9 & 11) learning skills [raceId, html, x, y, z]
	public final String[][] QMCa =
	{
		{
			"0",
			"tutorial_fighter017.htm",
			"-83165",
			"242711",
			"-3720"
		},
		{
			"10",
			"tutorial_mage017.htm",
			"-85247",
			"244718",
			"-3720"
		},
		{
			"18",
			"tutorial_fighter017.htm",
			"45610",
			"52206",
			"-2792"
		},
		{
			"25",
			"tutorial_mage017.htm",
			"45610",
			"52206",
			"-2792"
		},
		{
			"31",
			"tutorial_fighter017.htm",
			"10344",
			"14445",
			"-4242"
		},
		{
			"38",
			"tutorial_mage017.htm",
			"10344",
			"14445",
			"-4242"
		},
		{
			"44",
			"tutorial_fighter017.htm",
			"-46324",
			"-114384",
			"-200"
		},
		{
			"49",
			"tutorial_fighter017.htm",
			"-46305",
			"-112763",
			"-200"
		},
		{
			"53",
			"tutorial_fighter017.htm",
			"115447",
			"-182672",
			"-1440"
		},
		{
			"123",
			"tutorial_fighter017.htm",
			"-118132",
			"42788",
			"723"
		},
		{
			"124",
			"tutorial_fighter017.htm",
			"-118132",
			"42788",
			"723"
		}
	};

	// table for Question Mark Clicked (24) newbie lvl [raceId, html]
	public final Map<Integer, String> QMCb = new HashMap<Integer, String>();

	// table for Question Mark Clicked (35) 1st class transfer [raceId, html]
	public final Map<Integer, String> QMCc = new HashMap<Integer, String>();

	// table for Tutorial Close Link (26) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLa = new HashMap<Integer, String>();

	// table for Tutorial Close Link (23) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLb = new HashMap<Integer, String>();

	// table for Tutorial Close Link (24) 2nd class transfer [raceId, html]
	public final Map<Integer, String> TCLc = new HashMap<Integer, String>();

	// private static TutorialShowListener _tutorialShowListener;

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

	public _255_Tutorial()
	{
		super(false);

		CharListenerList.addGlobal(this);

		// _tutorialShowListener = new TutorialShowListener();

		QMCb.put(0, "tutorial_human009.htm");
		QMCb.put(10, "tutorial_human009.htm");
		QMCb.put(18, "tutorial_elf009.htm");
		QMCb.put(25, "tutorial_elf009.htm");
		QMCb.put(31, "tutorial_delf009.htm");
		QMCb.put(38, "tutorial_delf009.htm");
		QMCb.put(44, "tutorial_orc009.htm");
		QMCb.put(49, "tutorial_orc009.htm");
		QMCb.put(53, "tutorial_dwarven009.htm");
		QMCb.put(123, "tutorial_kamael009.htm");
		QMCb.put(124, "tutorial_kamael009.htm");

		QMCc.put(0, "tutorial_21.htm");
		QMCc.put(10, "tutorial_21a.htm");
		QMCc.put(18, "tutorial_21b.htm");
		QMCc.put(25, "tutorial_21c.htm");
		QMCc.put(31, "tutorial_21g.htm");
		QMCc.put(38, "tutorial_21h.htm");
		QMCc.put(44, "tutorial_21d.htm");
		QMCc.put(49, "tutorial_21e.htm");
		QMCc.put(53, "tutorial_21f.htm");

		TCLa.put(1, "tutorial_22w.htm");
		TCLa.put(4, "tutorial_22.htm");
		TCLa.put(7, "tutorial_22b.htm");
		TCLa.put(11, "tutorial_22c.htm");
		TCLa.put(15, "tutorial_22d.htm");
		TCLa.put(19, "tutorial_22e.htm");
		TCLa.put(22, "tutorial_22f.htm");
		TCLa.put(26, "tutorial_22g.htm");
		TCLa.put(29, "tutorial_22h.htm");
		TCLa.put(32, "tutorial_22n.htm");
		TCLa.put(35, "tutorial_22o.htm");
		TCLa.put(39, "tutorial_22p.htm");
		TCLa.put(42, "tutorial_22q.htm");
		TCLa.put(45, "tutorial_22i.htm");
		TCLa.put(47, "tutorial_22j.htm");
		TCLa.put(50, "tutorial_22k.htm");
		TCLa.put(54, "tutorial_22l.htm");
		TCLa.put(56, "tutorial_22m.htm");

		TCLb.put(4, "tutorial_22aa.htm");
		TCLb.put(7, "tutorial_22ba.htm");
		TCLb.put(11, "tutorial_22ca.htm");
		TCLb.put(15, "tutorial_22da.htm");
		TCLb.put(19, "tutorial_22ea.htm");
		TCLb.put(22, "tutorial_22fa.htm");
		TCLb.put(26, "tutorial_22ga.htm");
		TCLb.put(32, "tutorial_22na.htm");
		TCLb.put(35, "tutorial_22oa.htm");
		TCLb.put(39, "tutorial_22pa.htm");
		TCLb.put(50, "tutorial_22ka.htm");

		TCLc.put(4, "tutorial_22ab.htm");
		TCLc.put(7, "tutorial_22bb.htm");
		TCLc.put(11, "tutorial_22cb.htm");
		TCLc.put(15, "tutorial_22db.htm");
		TCLc.put(19, "tutorial_22eb.htm");
		TCLc.put(22, "tutorial_22fb.htm");
		TCLc.put(26, "tutorial_22gb.htm");
		TCLc.put(32, "tutorial_22nb.htm");
		TCLc.put(35, "tutorial_22ob.htm");
		TCLc.put(39, "tutorial_22pb.htm");
		TCLc.put(50, "tutorial_22kb.htm");
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		Player player = st.getPlayer();
		if (player == null)
		{
			return null;
		}

		String html = "";

		int classId = player.getClassId().getId();
		int Ex = st.getInt("Ex");

		if (event.equals("CheckPass"))
		{
			if (!Config.ENABLE_SECONDARY_PASSWORD)
			{
				return null;
			}

			String text = HtmCache.getInstance().getNotNull("enterworldSecondary.htm", player);
			st.showTutorialHTML(text);
			// player.setIsInvul(true);
			player.block();

			return null;
		}
		else if (event.equals("ProposePass"))
		{
			if (!Config.ENABLE_SECONDARY_PASSWORD)
			{
				return null;
			}

			String text = HtmCache.getInstance().getNotNull("enterworldNoSecondary.htm", player);
			st.showTutorialHTML(text);
			player.block();
			// player.startAbnormalEffect(AbnormalEffect.FIREROOT_STUN);

			player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "System", "Secondary Password is MANDATORY!"));
			player.sendPacket(new Say2(0, ChatType.COMMANDCHANNEL_ALL, "System", "If you cannot see Secondary Password Setup window, use ALT+L."));

			return null;
		}
		else if (event.startsWith("TryPass"))
		{
			if (!Config.ENABLE_SECONDARY_PASSWORD)
			{
				return null;
			}

			String pass = null;
			boolean correct = false;
			try
			{
				pass = event.substring("TryPass ".length());
				pass = pass.trim();
				correct = CCPSecondaryPassword.tryPass(player, pass);
			}
			catch (IndexOutOfBoundsException e)
			{
				correct = false;
			}

			if (correct)
			{
				player.sendPacket(ShowBoard.CLOSE_STATIC);
				st.closeTutorial();

				// onEvent("UC", st, null);
				player.sendMessage("Your password is correct!");
				if (player.isBlocked())
				{
					final MagicSkillUse msu = new MagicSkillUse(player, player, 23312, 1, 0, 500);
					player.broadcastPacket(msu);
					player.broadcastCharInfo();
					player.unblock();
				}
				// player.setIsInvul(false);

				// Synerge - Show the premium htm and message
				if (Config.ENTER_WORLD_SHOW_HTML_PREMIUM_BUY)
				{
					/*
					 * if (player.getClan() == null && player.getNetConnection().getBonus() < 1)
					 * {
					 * player.sendPacket(new NpcHtmlMessage(5).setFile("advertise.htm").replace("%playername%", player.getName()));
					 * }
					 */
					if (player.getNetConnection() != null && player.getNetConnection().getBonus() < 1)
					{
						String msg = "You don't have Premium Account, you can buy it from Community Board.";
						player.sendPacket(new ExShowScreenMessage(msg, 10000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false));
						player.sendMessage(msg);
					}
				}

				// Merge and forum community
				if (ConfigHolder.getBool("EnableMergeCommunity"))
				{
					final ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_merge");
					if (handler != null)
					{
						handler.onBypassCommand(player, "_merge");
					}
				}
				else if (ConfigHolder.getBool("ForumOnEnterWorld"))
				{
					final ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler("_bbsforum");
					if (handler != null)
					{
						final ForumBoardType boardType = ForumBoardType.getTypeByIndex(ConfigHolder.getInt("ForumOnEnterWorldBoardId"));
						if (boardType == null)
						{
							handler.onBypassCommand(player, "_bbsforum");
						}
						else
						{
							final ForumBoard board = ForumHandler.getInstance().getBoardByIndex(boardType.getBoardIndex());
							final ForumPost post = board.getLastPost();
							if (post == null)
							{
								handler.onBypassCommand(player, "_bbsforum");
							}
							else
							{
								handler.onBypassCommand(player, "_bbsforum_post_" + post.getPostId());
							}
						}
					}
				}

				// Synerge - Force ClassMaster check after putting the password just in case
				if (!checkClassMaster(st))
				{
					// Synerge - If the player doesnt need to show the classmaster then we invite him to the global pvp event if its enabled
					if (GlobalPvPZoneTaskManager.getInstance().isGlobalPvpOn())
					{
						GlobalPvPZoneTaskManager.getInstance().sendMainHtmlToPlayer(player);
					}
				}

				return null;
			}
			else
			{
				// Send a mail to the character telling that his account got a wrong secondary password
				Mail mail = new Mail();
				mail.setSenderId(1);
				mail.setSenderName("System");
				mail.setReceiverId(player.getObjectId());
				mail.setReceiverName(player.getName());
				mail.setTopic("Wrong Secondary Password");
				mail.setBody("Someone wrote a wrong secondary password (" + pass + ") to enter to your character. This is a warning message, if you didnt entered this password then change it");
				mail.setType(Mail.SenderType.NEWS_INFORMER);
				mail.setUnread(true);
				mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
				mail.save();

				// Logout the character
				// player.stopAbnormalEffect(AbnormalEffect.FIREROOT_STUN);
				player.logout();

				return null;
			}
		}
		else if (event.equals("OpenClassMaster"))
		{
			checkClassMaster(st);
			return null;
		}
		else if (event.equals("ShowChangeLog"))
		{
			checkChangeLog(st);
		}
		else if (event.startsWith("ShowChangeLogPage"))
		{
			int page = Integer.parseInt(event.substring("ShowChangeLogPage".length()).trim());
			String change = ChangeLogManager.getInstance().getChangeLog(page);
			st.showTutorialHTML(change);
		}
		else if (event.startsWith("ChangeTo"))
		{
			StringTokenizer tokenizer = new StringTokenizer(event, ";");
			tokenizer.nextToken();
			int newClassId = Integer.parseInt(tokenizer.nextToken());
			long price = Long.parseLong(tokenizer.nextToken());

			if ((price < 0L) || !ClassId.VALUES[newClassId].equalsOrChildOf(ClassId.VALUES[player.getActiveClassId()]))// Somebody cheating
			{
				st.closeTutorial();
				return null;
			}

			final int jobLevel = player.getClassId().getLevel();
			if (!canChangeClass(player, jobLevel))
			{
				st.closeTutorial();
				return null;
			}

			ItemTemplate item = ItemHolder.getInstance().getTemplate(Config.CLASS_MASTERS_PRICE_ITEM);
			ItemInstance pay = player.getInventory().getItemByItemId(item.getItemId());
			if (pay != null && pay.getCount() >= price)
			{
				player.getInventory().destroyItem(pay, price, "_255_Tutorial");
				if (jobLevel == 3)
				{
					player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST);
				}
				else
				{
					player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
				}

				player.setClassId(newClassId, false, false);

				final MagicSkillUse msu = new MagicSkillUse(player, player, 5103, 1, 1, 1);
				player.broadcastPacket(msu);
				player.broadcastUserInfo(true);
				st.closeTutorial();

				// Synerge - Dont check tutorial events for characters above level 70 or in subclass
				if (player.getLevel() < 70 && player.getActiveClassId() == player.getBaseClassId())
				{
					// Synerge - Show a special tutorial htm for weapons after the first class transfer
					if (jobLevel == 1 && player.getVarInt("lvl") < 21)
					{
						player.setVar("lvl", "21");
						player.sendPacket(new TutorialShowHtml(HtmCache.getInstance().getNotNull("SpecialTutorial/Level21.htm", player)));
					}
					// Synerge - Show a special tutorial htm after the second class transfer
					else if (jobLevel == 2 && player.getVarInt("lvl") < 41)
					{
						player.setVar("lvl", "41");
						player.sendPacket(new TutorialShowHtml(HtmCache.getInstance().getNotNull("SpecialTutorial/Level41.htm", player)));
					}
				}
				else
				{
					onEvent("OpenClassMaster", st, null);
				}
				return null;
			}
			else if (Config.CLASS_MASTERS_PRICE_ITEM == ItemTemplate.ITEM_ID_ADENA)
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
			}
			else
			{
				player.sendPacket(new SystemMessage2(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA));
			}
			st.closeTutorial();
			return null;
		}
		else if (event.equals("CloseTutorial"))
		{
			st.closeTutorial();
			return null;
		}
		else if (event.equals("onTutorialClose"))
		{
			onTutorialClose(st);
			return null;
		}

		// Synerge - Removed completely the tutorial and replaced with a new one. Reused some functions
		if (!Config.ENABLE_SPECIAL_TUTORIAL)
		{
			if (event.startsWith("UC"))
			{
				int level = player.getLevel();
				if (level < 6 && st.getInt("onlyone") == 0)
				{
					int uc = st.getInt("ucMemo");
					switch (uc)
					{
					case 0:
						st.set("ucMemo", "0");
						st.startQuestTimer("QT", 10000);
						st.set("Ex", "-2");
						break;
					case 1:
						st.showQuestionMark(1);
						st.playTutorialVoice("tutorial_voice_006");
						st.playSound(SOUND_TUTORIAL);
						break;
					case 2:
						if (Ex == 2)
						{
							st.showQuestionMark(3);
							st.playSound(SOUND_TUTORIAL);
						}
						else if (st.getQuestItemsCount(6353) > 0)
						{
							st.showQuestionMark(5);
							st.playSound(SOUND_TUTORIAL);
						}
						break;
					case 3:
						st.showQuestionMark(12);
						st.playSound(SOUND_TUTORIAL);
						st.onTutorialClientEvent(0);
						break;
					default:
						break;
					}
				}
				else if ((level == 18 && player.getQuestState("_10276_MutatedKaneusGludio") == null) || (level == 28 && player.getQuestState("_10277_MutatedKaneusDion") == null))
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
				else if (level == 28 && player.getQuestState("_10278_MutatedKaneusHeine") == null)
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
				else if (level == 28 && player.getQuestState("_10279_MutatedKaneusOren") == null)
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
				else if (level == 28 && player.getQuestState("_10280_MutatedKaneusSchuttgart") == null)
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
				else if (level == 28 && player.getQuestState("_10281_MutatedKaneusRune") == null)
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
				else if (level == 79 && player.getQuestState("_192_SevenSignSeriesOfDoubt") == null)
				{
					st.showQuestionMark(36);
					st.playSound(SOUND_TUTORIAL);
				}
			}

			else if (event.startsWith("QT"))
			{
				switch (Ex)
				{
				case -2:
				{
					String voice = "";
					for (String[] element : QTEXMTWO)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							voice = element[1];
							html = element[2];
						}
					}
					st.playTutorialVoice(voice);
					st.set("Ex", "-3");
					st.cancelQuestTimer("QT");
					st.startQuestTimer("QT", 30000);
					break;
				}
				case -3:
					st.playTutorialVoice("tutorial_voice_002");
					st.set("Ex", "0");
					break;
				case -4:
					st.playTutorialVoice("tutorial_voice_008");
					st.set("Ex", "-5");
					break;
				default:
					break;
				}
			}

			// Tutorial close
			else if (event.startsWith("TE"))
			{
				st.cancelQuestTimer("TE");
				int event_id = 0;
				if (!event.equalsIgnoreCase("TE"))
				{
					event_id = Integer.valueOf(event.substring(2));
				}
				switch (event_id)
				{
				case 0:
					st.closeTutorial();
					break;
				case 1:
					st.closeTutorial();
					st.playTutorialVoice("tutorial_voice_006");
					st.showQuestionMark(1);
					st.playSound(SOUND_TUTORIAL);
					st.startQuestTimer("QT", 30000);
					st.set("Ex", "-4");
					break;
				case 2:
					st.playTutorialVoice("tutorial_voice_003");
					html = "tutorial_02.htm";
					st.onTutorialClientEvent(1);
					st.set("Ex", "-5");
					break;
				case 3:
					html = "tutorial_03.htm";
					st.onTutorialClientEvent(2);
					break;
				case 5:
					html = "tutorial_05.htm";
					st.onTutorialClientEvent(8);
					break;
				case 7:
					html = "tutorial_100.htm";
					st.onTutorialClientEvent(0);
					break;
				case 8:
					html = "tutorial_101.htm";
					st.onTutorialClientEvent(0);
					break;
				case 10:
					html = "tutorial_103.htm";
					st.onTutorialClientEvent(0);
					break;
				case 12:
					st.closeTutorial();
					break;
				default:
					if (event_id == 23 && TCLb.containsKey(classId))
					{
						html = TCLb.get(classId);
					}
					else if (event_id == 24 && TCLc.containsKey(classId))
					{
						html = TCLc.get(classId);
					}
					else if (event_id == 25)
					{
						html = "tutorial_22cc.htm";
					}
					else if (event_id == 26 && TCLa.containsKey(classId))
					{
						html = TCLa.get(classId);
					}
					else
					{
						switch (event_id)
						{
						case 27:
							html = "tutorial_29.htm";
							break;
						case 28:
							html = "tutorial_28.htm";
							break;
						case 49:
							st.closeTutorial();
							return null;
						case 50:
							CCPSecondaryPassword.startSecondaryPasswordSetup(player, "secondaryPassF");
							st.closeTutorial();
							return null;
						default:
							break;
						}
					}
					break;
				}
			}

			// Client Event
			else if (event.startsWith("CE"))
			{
				int event_id = Integer.valueOf(event.substring(2));
				if (event_id == 1 && player.getLevel() < 6)
				{
					st.playTutorialVoice("tutorial_voice_004");
					html = "tutorial_03.htm";
					st.playSound(SOUND_TUTORIAL);
					st.onTutorialClientEvent(2);
				}
				else if (event_id == 2 && player.getLevel() < 6)
				{
					st.playTutorialVoice("tutorial_voice_005");
					html = "tutorial_05.htm";
					st.playSound(SOUND_TUTORIAL);
					st.onTutorialClientEvent(8);
				}
				else if (event_id == 8 && player.getLevel() < 6)
				{
					int x = 0;
					int y = 0;
					int z = 0;
					for (String[] element : CEEa)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					}
					if (x != 0)
					{
						st.playSound(SOUND_TUTORIAL);
						st.addRadar(x, y, z);
						st.playTutorialVoice("tutorial_voice_007");
						st.set("ucMemo", "1");
						st.set("Ex", "-5");
					}
				}
				else if (event_id == 30 && player.getLevel() < 10 && st.getInt("Die") == 0)
				{
					st.playTutorialVoice("tutorial_voice_016");
					st.playSound(SOUND_TUTORIAL);
					st.set("Die", "1");
					st.showQuestionMark(8);
					st.onTutorialClientEvent(0);
				}
				else if (event_id == 800000 && player.getLevel() < 6 && st.getInt("sit") == 0)
				{
					st.playTutorialVoice("tutorial_voice_018");
					st.playSound(SOUND_TUTORIAL);
					st.set("sit", "1");
					st.onTutorialClientEvent(0);
					html = "tutorial_21z.htm";
				}
				else if (event_id == 40)
				{
					if (player.getLevel() == 5)
					{
						if (st.getInt("lvl") < 5 && !player.getClassId().isMage() || classId == 49)
						{
							st.playTutorialVoice("tutorial_voice_014");
							st.showQuestionMark(9);
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "5");
						}
					}
					if (player.getLevel() == 6)
					{
						if (st.getInt("lvl") < 6 && player.getClassId().level() == 0)
						{
							st.playTutorialVoice("tutorial_voice_020");
							st.playSound(SOUND_TUTORIAL);
							st.showQuestionMark(24);
						}
					}
					else if (player.getLevel() == 7)
					{
						if (st.getInt("lvl") < 7 && player.getClassId().isMage() && classId != 49 && player.getClassId().level() == 0)
						{
							st.playTutorialVoice("tutorial_voice_019");
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "7");
							st.showQuestionMark(11);
						}
					}
					else if (player.getLevel() == 15)
					{
						if (st.getInt("lvl") < 15)
						{
							// st.playTutorialVoice("tutorial_voice_???");
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "15");
							st.showQuestionMark(33);
						}
					}
					else if (player.getLevel() == 18)
					{
						if (st.getInt("lvl") < 18)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "18");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 19)
					{
						if (st.getInt("lvl") < 19 && player.getRace() != Race.kamael && player.getClassId().level() == 0)
						{
							switch (classId)
							{
							case 0:
							case 10:
							case 18:
							case 25:
							case 31:
							case 38:
							case 44:
							case 49:
							case 52:
								// st.playTutorialVoice("tutorial_voice_???");
								st.playSound(SOUND_TUTORIAL);
								st.set("lvl", "19");
								st.showQuestionMark(35);
							}
						}
					}
					else if (player.getLevel() == 28)
					{
						if (st.getInt("lvl") < 28)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "28");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 35)
					{
						if (st.getInt("lvl") < 35 && player.getRace() != Race.kamael && player.getClassId().level() == 1)
						{
							switch (classId)
							{
							case 1:
							case 4:
							case 7:
							case 11:
							case 15:
							case 19:
							case 22:
							case 26:
							case 29:
							case 32:
							case 35:
							case 39:
							case 42:
							case 45:
							case 47:
							case 50:
							case 54:
							case 56:
								// st.playTutorialVoice("tutorial_voice_???");
								st.playSound(SOUND_TUTORIAL);
								st.set("lvl", "35");
								st.showQuestionMark(34);
							}
						}
					}
					else if (player.getLevel() == 38)
					{
						if (st.getInt("lvl") < 38)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "38");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 48)
					{
						if (st.getInt("lvl") < 48)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "48");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 58)
					{
						if (st.getInt("lvl") < 58)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "58");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 68)
					{
						if (st.getInt("lvl") < 68)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "68");
							st.showQuestionMark(36);
						}
					}
					else if (player.getLevel() == 79)
					{
						if (st.getInt("lvl") < 79)
						{
							st.playSound(SOUND_TUTORIAL);
							st.set("lvl", "79");
							st.showQuestionMark(79);
						}
					}
				}
				else if (event_id == 45 && player.getLevel() < 10 && st.getInt("HP") == 0)
				{
					st.playTutorialVoice("tutorial_voice_017");
					st.playSound(SOUND_TUTORIAL);
					st.set("HP", "1");
					st.showQuestionMark(10);
					st.onTutorialClientEvent(800000);
				}
				else if (event_id == 57 && player.getLevel() < 6 && st.getInt("Adena") == 0)
				{
					st.playTutorialVoice("tutorial_voice_012");
					st.playSound(SOUND_TUTORIAL);
					st.set("Adena", "1");
					st.showQuestionMark(23);
				}
				else if (event_id == 6353 && player.getLevel() < 6 && st.getInt("Gemstone") == 0)
				{
					st.playTutorialVoice("tutorial_voice_013");
					st.playSound(SOUND_TUTORIAL);
					st.set("Gemstone", "1");
					st.showQuestionMark(5);
				}
				else if (event_id == 1048576 && player.getLevel() < 6)
				{
					st.showQuestionMark(5);
					st.playTutorialVoice("tutorial_voice_013");
					st.playSound(SOUND_TUTORIAL);
				}
			}

			// Question mark clicked
			else if (event.startsWith("QM"))
			{
				int MarkId = Integer.valueOf(event.substring(2));
				switch (MarkId)
				{
				case 1:
				{
					st.playTutorialVoice("tutorial_voice_007");
					st.set("Ex", "-5");
					int x = 0;
					int y = 0;
					int z = 0;
					for (String[] element : CEEa)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					}
					st.addRadar(x, y, z);
					break;
				}
				case 3:
					html = "tutorial_09.htm";
					st.onTutorialClientEvent(1048576);
					break;
				case 5:
				{
					int x = 0;
					int y = 0;
					int z = 0;
					for (String[] element : CEEa)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					}
					st.addRadar(x, y, z);
					html = "tutorial_11.htm";
					break;
				}
				case 7:
					html = "tutorial_15.htm";
					st.set("ucMemo", "3");
					break;
				case 8:
					html = "tutorial_18.htm";
					break;
				case 9:
				{
					int x = 0;
					int y = 0;
					int z = 0;
					for (String[] element : QMCa)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					}
					if (x != 0)
					{
						st.addRadar(x, y, z);
					}
					break;
				}
				case 10:
					html = "tutorial_19.htm";
					break;
				case 11:
				{
					int x = 0;
					int y = 0;
					int z = 0;
					for (String[] element : QMCa)
					{
						if (classId == Integer.valueOf(element[0]))
						{
							html = element[1];
							x = Integer.valueOf(element[2]);
							y = Integer.valueOf(element[3]);
							z = Integer.valueOf(element[4]);
						}
					}
					if (x != 0)
					{
						st.addRadar(x, y, z);
					}
					break;
				}
				case 12:
					html = "tutorial_15.htm";
					st.set("ucMemo", "4");
					break;
				case 23:
					html = "tutorial_24.htm";
					break;
				default:
					if (MarkId == 24 && QMCb.containsKey(classId))
					{
						html = QMCb.get(classId);
					}
					else
					{
						switch (MarkId)
						{
						case 26:
							if (player.getClassId().isMage() && classId != 49)
							{
								html = "tutorial_newbie004b.htm";
							}
							else
							{
								html = "tutorial_newbie004a.htm";
							}
							break;
						case 33:
							html = "tutorial_27.htm";
							break;
						case 34:
							html = "tutorial_28.htm";
							break;
						default:
							if (MarkId == 35 && QMCc.containsKey(classId))
							{
								html = QMCc.get(classId);
							}
							else if (MarkId == 36)
							{
								int lvl = player.getLevel();
								switch (lvl)
								{
								case 18:
									html = "tutorial_kama_18.htm";
									break;
								case 28:
									html = "tutorial_kama_28.htm";
									break;
								case 38:
									html = "tutorial_kama_38.htm";
									break;
								case 48:
									html = "tutorial_kama_48.htm";
									break;
								case 58:
									html = "tutorial_kama_58.htm";
									break;
								case 68:
									html = "tutorial_kama_68.htm";
									break;
								case 79:
									html = "tutorial_epic_quest.htm";
									break;
								default:
									break;
								}
							}
							break;
						}
					}
					break;
				}
			}

			if (html.isEmpty())
			{
				return null;
			}
			st.showTutorialPage(html);
		}
		// Synerge - Special tutorial that replaces the retial one
		else // Tutorial close
		if (event.startsWith("TE"))
		{
			st.cancelQuestTimer("TE");
			int event_id = 0;
			if (!event.equalsIgnoreCase("TE"))
			{
				try
				{
					event_id = Integer.valueOf(event.substring(2));
				}
				catch (Exception e)
				{
				}
			}
			if (event_id == 0)
			{
				st.closeTutorial();
			}
			else if (event_id == 50)// New Secondary Password
			{
				CCPSecondaryPassword.startSecondaryPasswordSetup(player, "secondaryPassF");
				st.closeTutorial();
				return null;
			}
		}
		// Client Event
		else if (event.startsWith("CE"))
		{
			// Dont check tutorial events for characters above level 70 or in subclass
			if (player.getLevel() >= 70 || player.getActiveClassId() != player.getBaseClassId())
			{
				return null;
			}

			int event_id = Integer.valueOf(event.substring(2));

			// Level up event
			switch (event_id)
			{
			case 40:
				// Synerge - On lvl 6 show a html for teleporting the player to other place
				if (player.getLevel() >= 6 && player.getVarInt("lvl") < 6 && st.getInt("firstexp") == 1)
				{
					if (player.getClassId().level() == 0)
					{
						// player.setVar("lvl", "6");
						st.set("firstexp", "2");
						st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level6.htm", player));
					}
				}
				// Synerge - Show a special tutorial htm for showing a npc in radar
				else if (player.getLevel() >= 32 && player.getVarInt("lvl") < 32)
				{
					player.setVar("lvl", "32");
					st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level32.htm", player));
				}
				// Synerge - Show a special tutorial htm for teleporting
				else if (player.getLevel() >= 52 && player.getVarInt("lvl") < 52)
				{
					player.setVar("lvl", "52");
					st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level52.htm", player));
				}
				break;
			case 41:
				// Synerge - When getting the first exp the tutorial should close
				if (st.getInt("firstexp") < 1)
				{
					st.set("firstexp", "1");
					st.closeTutorial();
				}
				// Synerge - Player should get another html after the html7, when he kills a monster
				else if (st.getInt("firstexp") == 3)
				{
					player.setVar("lvl", "6");
					st.set("firstexp", "4");
					st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level8.htm", player));
				}
				break;
			case 42:
				// Synerge - Shows the level41Ready htm when teleporting after lvl 40
				if (player.getVarInt("lvl") == 41)
				{
					player.setVar("lvl", "42");
					st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level41Ready.htm", player));
				}
				break;
			default:
				break;
			}
		}
		// Synerge - Shows the buffer on the community board and the level7.htm in the tutorial
		else if (event.startsWith("ShowBuffer") && st.getInt("firstexp") == 2)
		{
			st.set("firstexp", "3");

			SchemeBufferInstance.showWindow(player);

			st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level7.htm", player));
		}
		// Synerge - Gives the character a certain weapon id and equips it
		else if (event.startsWith("GetWeaponD ") && player.getVarInt("weapon") < 1)
		{
			StringTokenizer tokenizer = new StringTokenizer(event, " ");
			tokenizer.nextToken();
			final int itemId = Integer.parseInt(tokenizer.nextToken());

			final ItemInstance createditem = ItemFunctions.createItem(itemId);

			if (createditem == null || createditem.getCrystalType() != Grade.D)
			{
				player.sendMessage("Wrong weapon");
				st.closeTutorial();
				return null;
			}

			player.setVar("weapon", "1");
			player.getInventory().addItem(createditem, "SpecialTutorial");

			// Also give arrows if the weapon is a bow
			if (createditem.isWeapon() && ((WeaponTemplate) createditem.getTemplate()).getItemType() == WeaponType.BOW)
			{
				final ItemInstance arrows = ItemFunctions.createItem(1341);
				arrows.setCount(300);
				player.getInventory().addItem(arrows, "SpecialTutorial");
			}

			// Unequip the current player's weapon
			if (player.getActiveWeaponInstance() != null)
			{
				player.getInventory().unEquipItem(player.getActiveWeaponInstance());
			}

			// Equip the new item
			player.getInventory().equipItem(createditem);

			// Show the equip armor next
			if (player.getRace() == Race.kamael)
			{
				st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level21ArmorKamael.htm", player));
			}
			else
			{
				st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level21Armors.htm", player));
			}
		}
		// Synerge - Gives the character a certain armor ids and equips it
		else if (event.startsWith("GetArmorD ") && player.getVarInt("armor") < 1)
		{
			StringTokenizer tokenizer = new StringTokenizer(event, " ");
			tokenizer.nextToken();

			player.setVar("armor", "1");

			// We have to give and equip each item that is sent through the bypass
			while (tokenizer.hasMoreTokens())
			{
				final int itemId = Integer.parseInt(tokenizer.nextToken());
				final ItemInstance createditem = ItemFunctions.createItem(itemId);

				if (createditem == null || createditem.getCrystalType() != Grade.D)
				{
					player.sendMessage("Wrong Armor");
					st.closeTutorial();
					return null;
				}

				player.getInventory().addItem(createditem, "SpecialTutorial");

				// Unequip the current player's armor slot
				player.getInventory().unEquipItemInBodySlot(createditem.getBodyPart());

				// Equip the new item
				player.getInventory().equipItem(createditem);
			}

			// Show the soulshots html next
			st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level21Soulshots.htm", player));
		}
		// Synerge - Gives the character some shots
		else if (event.startsWith("GetShotsD ") && player.getVarInt("shots") < 1)
		{
			StringTokenizer tokenizer = new StringTokenizer(event, " ");
			tokenizer.nextToken();
			final int itemId = Integer.parseInt(tokenizer.nextToken());
			final int itemCount = Integer.parseInt(tokenizer.nextToken());

			final ItemInstance createditem = ItemFunctions.createItem(itemId);

			if (createditem == null || createditem.getCrystalType() != Grade.D)
			{
				player.sendMessage("Wrong shots");
				st.closeTutorial();
				return null;
			}

			createditem.setCount(itemCount);

			player.setVar("shots", "1");
			player.getInventory().addItem(createditem, "SpecialTutorial");

			// Add the soulshots to a new shortcut
			ShortCut shortCut = new ShortCut(11, 0, ShortCut.TYPE_ITEM, createditem.getObjectId(), -1, 1);
			player.sendPacket(new ShortCutRegister(player, shortCut));
			player.registerShortCut(shortCut);

			// Show the cruma html next
			st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/Level21Cruma.htm", player));
		}
		// Synerge - Allows to open htmls directly as a bypass. Support for link on tutorials? Should work outside this, but whatever
		else if (event.startsWith("Link "))
		{
			StringTokenizer tokenizer = new StringTokenizer(event, " ");
			tokenizer.nextToken();
			final String htm = tokenizer.nextToken();

			st.showTutorialHTML(HtmCache.getInstance().getNotNull("SpecialTutorial/" + htm, player));
		}
		// Synerge - Shows a certain npc in the map and radar
		else if (event.startsWith("ShowLocation "))
		{
			StringTokenizer tokenizer = new StringTokenizer(event, " ");
			tokenizer.nextToken();
			final int npcId = Integer.parseInt(tokenizer.nextToken());

			final NpcInstance npcLoc = GameObjectsStorage.getByNpcId(npcId);
			if (npcLoc != null)
			{
				player.sendPacket(new RadarControl(2, 2, 0, 0, 0));
				player.sendPacket(new RadarControl(0, 2, npcLoc.getLoc()));
			}

			st.closeTutorial();
		}

		return null;
	}

	private static boolean checkCanSeeTutorial(Player player)
	{
		return !player.containsQuickVar("watchingTutorial");
	}

	private static void addToTutorialQueue(Player player, String pageToCheck)
	{
		@SuppressWarnings("unchecked")
		Collection<String> tutorialsToSee = (List<String>) player.getQuickVarO("tutorialsToSee", new ArrayList<String>());
		tutorialsToSee.add(pageToCheck);
		if (!player.containsQuickVar("tutorialsToSee"))
		{
			player.addQuickVar("tutorialsToSee", tutorialsToSee);
		}
	}

	private static void onTutorialClose(QuestState st)
	{
		Player player = st.getPlayer();
		if (player.containsQuickVar("tutorialsToSee"))
		{
			@SuppressWarnings("unchecked")
			List<String> tutorialsToSee = (List<String>) player.getQuickVarO("tutorialsToSee", new ArrayList<String>());
			String tutorialToSee = tutorialsToSee.remove(0);
			if (tutorialsToSee.isEmpty())
			{
				player.deleteQuickVar("tutorialsToSee");
			}
			switch (tutorialToSee)
			{
			case "checkChangeLog":
				checkChangeLog(st);
				return;
			case "checkClassMaster":
				checkClassMaster(st);
				return;
			default:
			}
		}
	}

	private static void checkChangeLog(QuestState st)
	{
		Player player = st.getPlayer();
		if (!checkCanSeeTutorial(player))
		{
			addToTutorialQueue(player, "checkChangeLog");
		}
		else
		{
			int lastNotSeenChange = ChangeLogManager.getInstance().getNotSeenChangeLog(player);
			if (lastNotSeenChange >= 0)
			{
				String change = ChangeLogManager.getInstance().getChangeLog(lastNotSeenChange);
				st.showTutorialHTML(change);
				HwidGamer gamer = player.getHwidGamer();
				if (gamer != null)
				{
					gamer.setSeenChangeLog(ChangeLogManager.getInstance().getLatestChangeId(), true);
				}
			}
		}
	}

	/**
	 * If {@link #canChangeClass(l2mv.gameserver.model.Player, int) canChangeClass}, showing Tutorial Page with next Classes that player can advance to
	 * @param st
	 * @return
	 */
	private static boolean checkClassMaster(QuestState st)
	{
		Player player = st.getPlayer();

		if (!checkCanSeeTutorial(player))
		{
			addToTutorialQueue(player, "OpenClassMaster");
			return true;
		}

		ClassId classId = player.getClassId();
		int jobLevel = classId.getLevel();

		if (Config.ALLOW_CLASS_MASTERS_LIST.isEmpty() || !Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			jobLevel = 4;
		}

		if (canChangeClass(player, jobLevel))
		{
			StringBuilder html = new StringBuilder();
			html.append("<html noscrollbar><head><title>Ultimate New Era Newbie Tutorial</title></head>");
			html.append("<body>");
			html.append("<table border=0 cellpadding=0 cellspacing=0 width=292 height=310 background=\"l2ui_ct1.Windows_DF_TooltipBG\">");
			html.append("<tr><td align=center><br>");
			html.append("<table width=280><tr><td align=center valign=center>");
			html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
			html.append("<font name=hs12 color=3399FF>Ultimate New Era</font>");
			html.append("<img src=\"L2UI.squaregray\" width=285 height=1/>");
			html.append("</td></tr></table>");
			html.append("<br></td></tr><tr>");
			html.append("<td align=center height=150>");
			html.append("<table width=280>");
			html.append("<tr><td align=center><br><font color=LEVEL name=hs12>Welcome to Ultimate New Era! </font></td></tr>");
			html.append("</table>");
			html.append("<br1>");
			html.append("<table width=280><tr><td align=center>");
			html.append("<font color=00ff99>").append(player.getName()).append("</font> change your class for <font color=\"LEVEL\">").append(Util.formatAdena(Config.CLASS_MASTERS_PRICE_LIST[jobLevel])).append(" Adena</font>!<br1>");
			html.append("</td></tr></table>");
			html.append("<table width=280>");
			for (ClassId cid : ClassId.values())
			{
				if (cid != ClassId.inspector && cid.childOf(classId) && cid.level() == classId.level() + 1)
				{
					if (!player.getActiveClass().isBase() && cid.level() >= 2 && Config.ALT_ENABLE_MULTI_PROFA)
					{
						player.sendCustomMessage("There is no class to change for you.");
						return false;
					}
					String name = cid.name().substring(0, 1).toUpperCase() + cid.name().substring(1);
					html.append("<tr><td align=center><button value=\"").append(name).append("\" action=\"bypass -h ChangeTo;").append(cid.getId()).append(';').append(Config.CLASS_MASTERS_PRICE_LIST[jobLevel]).append("\" width=200 height=32 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"></td></tr>");
				}
			}
			html.append("<tr><td align=center><button value=\"Remaind me later\" action=\"bypass CloseTutorial\" width=200 height=28 back=\"L2UI_CT1.OlympiadWnd_DF_Back_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Back\"></td></tr>");
			html.append("</table>");
			html.append("</td></tr><tr>");
			html.append("<td align=center><table width=280><tr>");
			html.append("<td align=center valign=center>");
			html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
			html.append("<font name=hs12 color=3399FF>Ultimate New Era</font>");
			html.append("<img src=\"L2UI.squaregray\" width=285 height=1/> ");
			html.append("</td></tr></table><br></td></tr>");
			html.append("</table></body></html>");

			st.closeTutorial(); // Close the tutorial first so the other html can be shown
			st.showTutorialHTML(html.toString());
			return true;
		}

		return false;
	}

	/**
	 * Checking if player have got level >= 20, >= 40 or >= 76 and still didn't change class
	 * @param player to check
	 * @param jobLevel level of the class
	 * @return can change class
	 */
	private static boolean canChangeClass(Player player, int jobLevel)
	{
		int level = player.getLevel();

		if (!Config.ALLOW_CLASS_MASTERS_LIST.contains(jobLevel))
		{
			return false;
		}
		if ((level >= 20 && jobLevel == 1) || (level >= 40 && jobLevel == 2))
		{
			return true;
		}
		if (level >= 76 && jobLevel == 3)
		{
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerEnter(Player player)
	{
		/*
		 * if (player.getLevel() < 6)
		 * player.addListener(_tutorialShowListener);
		 */
	}

	/*
	 * public class TutorialShowListener implements OnCurrentHpDamageListener
	 * {
	 * @Override
	 * public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill)
	 * {
	 * Player player = actor.getPlayer();
	 * if (player.getCurrentHpPercents() < 25)
	 * {
	 * player.removeListener(_tutorialShowListener);
	 * Quest q = QuestManager.getQuest(255);
	 * if (q != null)
	 * player.processQuestEvent(q.getName(), "CE45", null);
	 * }
	 * else if (player.getLevel() > 5)
	 * player.removeListener(_tutorialShowListener);
	 * }
	 * }
	 */

	@Override
	public boolean isVisible()
	{
		return false;
	}
}