package l2mv.gameserver.network.serverpackets.components;

import java.util.NoSuchElementException;

import l2mv.gameserver.model.Player;
import l2mv.gameserver.network.serverpackets.L2GameServerPacket;
import l2mv.gameserver.network.serverpackets.SystemMessage2;

public enum SystemMsg implements IStaticPacket
{
	// Сервер будет отключен через $s1 сек. Пожалуйста, выйдите из игры.
	THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS__PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT(1),
	// Message: This character cannot make a report because another character from this account has already done so.
	THIS_CHARACTER_CANNOT_MAKE_A_REPORT_BECAUSE_ANOTHER_CHARACTER_FROM_THIS_ACCOUNT_HAS_ALREADY_DONE_SO(2472),
	// Message: You cannot report a character who has not acquired any Exp. after connecting.
	CANNOT_REPORT_CHARACTER_WITHOUT_GAINEXP(2379),
	// Message: You cannot report this person again at this time.
	CANNOT_REPORT_CHARACTER_AGAIN(2380),
	// Message: You cannot report this person again at this time.
	CANNOT_REPORT_CHARACTER_AGAIN_2(2381),
	// Message: You cannot report this person again at this time.
	CANNOT_REPORT_CHARACTER_AGAIN_3(2382),
	// Message: You cannot report this person again at this time.
	CANNOT_REPORT_CHARACTER_AGAIN_4(2383),
	// Message: $c1 was reported as a BOT.
	C1_REPORTED_AS_BOT(2371),
	// Олимпиада началась.
	THE_OLYMPIAD_GAME_HAS_STARTED(1641),
	// Олимпиада окончена.
	THE_OLYMPIAD_GAME_HAS_ENDED(1642),
	// $s1 период Олимпиады начался.
	OLYMPIAD_PERIOD_S1_HAS_STARTED(1639),
	// $s1 период Олимпиады закончился.
	OLYMPIAD_PERIOD_S1_HAS_ENDED(1640),
	// Джекпот лотереи $s1 составил $s2 аден. Количество победителей: $s3 чел.
	THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS(1112),
	// Джекпот лотереи $s1 составил $s2 аден. Первое место не занял никто. Данный джекпот будет разыгран в следующей лотерее.
	THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING(1113),
	// Message: This item cannot be discarded.
	THIS_ITEM_CANNOT_BE_DISCARDED(98),
	// Питомец призван и не может быть удален.
	THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_DELETED(557),
	// Питомец призван и не может быть отпущен.
	THE_PET_HAS_BEEN_SUMMONED_AND_CANNOT_BE_LET_GO(558),
	// Message: Hero weapons cannot be destroyed.
	HERO_WEAPONS_CANNOT_BE_DESTROYED(1845),
	// Вы не можете разбить на кристаллы этот предмет. Уровень Вашего умения слишком низок.
	CANNOT_CRYSTALLIZE_CRYSTALLIZATION_SKILL_LEVEL_TOO_LOW(562),
	// Message: Once an item is augmented, it cannot be augmented again.
	ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN(1970),
	// Message: The level of the hardener is too high to be used.
	THE_LEVEL_OF_THE_HARDENER_IS_TOO_HIGH_TO_BE_USED(1971),
	// Message: You cannot augment items while a private store or private workshop is in operation.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION(1972),
	// Message: You cannot augment items while frozen.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_FROZEN(1973),
	// Message: You cannot augment items while dead.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD(1974),
	// Message: You cannot augment items while engaged in trade activities.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES(1975),
	// Message: You cannot augment items while paralyzed.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED(1976),
	// Message: You cannot augment items while fishing.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING(1977),
	// Message: You cannot augment items while sitting down.
	YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN(1978),
	// Message: You have been disconnected from the server.
	YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER(0),
	// Message: The server will be coming down in $s1 second(s). Please find a safe place to log out.
	THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS(1),
	// Message: $s1 does not exist.
	S1_DOES_NOT_EXIST(2),
	// Message: $s1 is not currently logged in.
	S1_IS_NOT_CURRENTLY_LOGGED_IN(3),
	// Message: You cannot ask yourself to apply to a clan.
	YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN(4),
	// Message: $s1 already exists.
	S1_ALREADY_EXISTS(5),
	// Message: $s1 does not exist.
	S1_DOES_NOT_EXIST_(6),
	// Message: You are already a member of $s1.
	YOU_ARE_ALREADY_A_MEMBER_OF_S1(7),
	// Message: You are already a member of another clan.
	YOU_ARE_ALREADY_A_MEMBER_OF_ANOTHER_CLAN(8),
	// Message: $s1 is not a clan leader.
	S1_IS_NOT_A_CLAN_LEADER(9),
	// Message: $s1 is already a member of another clan.
	S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN(10),
	// Message: There are no applicants for this clan.
	THERE_ARE_NO_APPLICANTS_FOR_THIS_CLAN(11),
	// Message: Applicant information is incorrect.
	APPLICANT_INFORMATION_IS_INCORRECT(12),
	// Message: Unable to dissolve: your clan has requested to participate in a castle siege.
	UNABLE_TO_DISSOLVE_YOUR_CLAN_HAS_REQUESTED_TO_PARTICIPATE_IN_A_CASTLE_SIEGE(13),
	// Message: Unable to dissolve: your clan owns one or more castles or hideouts.
	UNABLE_TO_DISSOLVE_YOUR_CLAN_OWNS_ONE_OR_MORE_CASTLES_OR_HIDEOUTS(14),
	// Message: You are in siege.
	YOU_ARE_IN_SIEGE(15),
	// Message: You are not in siege.
	YOU_ARE_NOT_IN_SIEGE(16),
	// Message: The castle siege has begun.
	THE_CASTLE_SIEGE_HAS_BEGUN(17),
	// Message: The castle siege has ended.
	THE_CASTLE_SIEGE_HAS_ENDED(18),
	// Message: There is a new Lord of the castle!
	THERE_IS_A_NEW_LORD_OF_THE_CASTLE(19),
	// Message: The gate is being opened.
	THE_GATE_IS_BEING_OPENED(20),
	// Message: The gate is being destroyed.
	THE_GATE_IS_BEING_DESTROYED(21),
	// Message: Your target is out of range.
	YOUR_TARGET_IS_OUT_OF_RANGE(22),
	// Message: Not enough HP.
	NOT_ENOUGH_HP(23),
	// Message: Not enough MP.
	NOT_ENOUGH_MP(24),
	// Message: Rejuvenating HP.
	REJUVENATING_HP(25),
	// Message: Rejuvenating MP.
	REJUVENATING_MP(26),
	// Message: Your casting has been interrupted.
	YOUR_CASTING_HAS_BEEN_INTERRUPTED(27),
	// Message: You have obtained $s1 adena.
	YOU_HAVE_OBTAINED_S1_ADENA(28),
	// Message: You have obtained $s2 $s1.
	YOU_HAVE_OBTAINED_S2_S1(29),
	// Message: You have obtained $s1.
	YOU_HAVE_OBTAINED_S1(30),
	// Message: You cannot move while sitting.
	YOU_CANNOT_MOVE_WHILE_SITTING(31),
	// Message: You are unable to engage in combat. Please go to the nearest restart point.
	YOU_ARE_UNABLE_TO_ENGAGE_IN_COMBAT(32),
	// Message: You cannot move while casting.
	YOU_CANNOT_MOVE_WHILE_CASTING(33),
	// Message: Welcome to the World of Lineage II.
	WELCOME_TO_THE_WORLD_OF_LINEAGE_II(34),
	// Message: You carefully nock an arrow.
	YOU_CAREFULLY_NOCK_AN_ARROW(41),
	// Message:
	YOU_USE_S1(46),
	// Message: You have equipped your $s1.
	YOU_HAVE_EQUIPPED_YOUR_S1(49),
	// Message: You cannot use this on yourself.
	YOU_CANNOT_USE_THIS_ON_YOURSELF(51),
	// Message: You have earned $s1 adena.
	YOU_HAVE_EARNED_S1_ADENA(52),
	// Message: You have earned $s2 $s1(s).
	YOU_HAVE_EARNED_S2_S1S(53),
	// Message: You have earned $s1.
	YOU_HAVE_EARNED_S1(54),
	// Message: You have failed to pick up $s1 adena.
	YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA(55),
	// Message: You have failed to pick up $s1.
	YOU_HAVE_FAILED_TO_PICK_UP_S1(56),
	// Message: You have failed to pick up $s2 $s1(s).
	YOU_HAVE_FAILED_TO_PICK_UP_S2_S1S(57),
	// Message: You have failed to earn $s1 adena.
	YOU_HAVE_FAILED_TO_EARN_S1_ADENA(58),
	// Message: You have failed to earn $s1.
	YOU_HAVE_FAILED_TO_EARN_S1(59),
	// Message: You have failed to earn $s2 $s1(s).
	YOU_HAVE_FAILED_TO_EARN_S2_S1S(60),
	// Message: Nothing happened.
	NOTHING_HAPPENED(61),
	// Message: Your $s1 has been successfully enchanted.
	YOUR_S1_HAS_BEEN_SUCCESSFULLY_ENCHANTED(62),
	// Message: Your +$S1 $S2 has been successfully enchanted.
	YOUR_S1_S2_HAS_BEEN_SUCCESSFULLY_ENCHANTED(63),
	// Message: The enchantment has failed! Your $s1 has been crystallized.
	THE_ENCHANTMENT_HAS_FAILED__YOUR_S1_HAS_BEEN_CRYSTALLIZED(64),
	// Message: The enchantment has failed! Your +$s1 $s2 has been crystallized.
	THE_ENCHANTMENT_HAS_FAILED__YOUR_S1_S2_HAS_BEEN_CRYSTALLIZED(65),
	// Message: $c1 is inviting you to join a party. Do you accept?
	C1_IS_INVITING_YOU_TO_JOIN_A_PARTY(66),
	// Message: $s1 has invited you to join their clan, $s2. Do you wish to join?
	S1_HAS_INVITED_YOU_TO_JOIN_THEIR_CLAN_S2(67),
	// Message: Would you like to withdraw from clan $s1? If you leave, you will have to wait at least a day before joining another clan.
	WOULD_YOU_LIKE_TO_WITHDRAW_FROM_CLAN_S1_IF_YOU_LEAVE_YOU_WILL_HAVE_TO_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN(68),
	// Message: Would you like to dismiss $s1 from the clan? If you do so, you will have to wait at least a day before accepting a new member.
	WOULD_YOU_LIKE_TO_DISMISS_S1_FROM_THE_CLAN_IF_YOU_DO_SO_YOU_WILL_HAVE_TO_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER(69),
	// Message: Do you wish to disperse the clan, $s1?
	DO_YOU_WISH_TO_DISPERSE_THE_CLAN_S1(70),
	// Message: How much $s1(s) do you wish to discard?
	HOW_MUCH_S1S_DO_YOU_WISH_TO_DISCARD(71),
	// Message: How much $s1(s) do you wish to move?
	HOW_MUCH_S1S_DO_YOU_WISH_TO_MOVE(72),
	// Message: How much $s1(s) do you wish to destroy?
	HOW_MUCH_S1S_DO_YOU_WISH_TO_DESTROY(73),
	// Message: Do you wish to destroy your $s1?
	DO_YOU_WISH_TO_DESTROY_YOUR_S1(74),
	// Message: ID does not exist.
	ID_DOES_NOT_EXIST(75),
	// Message: Incorrect password.
	INCORRECT_PASSWORD(76),
	// Message: You cannot create another character. Please delete an existing character and try again.
	YOU_CANNOT_CREATE_ANOTHER_CHARACTER(77),
	// Message: When you delete a character, any items in his/her possession will also be deleted. Do you really wish to delete $s1?
	WHEN_YOU_DELETE_A_CHARACTER_ANY_ITEMS_IN_HISHER_POSSESSION_WILL_ALSO_BE_DELETED(78),
	// Message: This name already exists.
	THIS_NAME_ALREADY_EXISTS(79),
	// Message: Your title cannot exceed 16 characters in length.  Please try again.
	YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH(80),
	// Message: Please select your race.
	PLEASE_SELECT_YOUR_RACE(81),
	// Message: Please select your occupation.
	PLEASE_SELECT_YOUR_OCCUPATION(82),
	// Message: Please select your gender.
	PLEASE_SELECT_YOUR_GENDER(83),
	// Message: You may not attack in a peaceful zone.
	YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE(84),
	// Message: You may not attack this target in a peaceful zone.
	YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE(85),
	// Message:
	S1_HAS_WORN_OFF(92),
	// Message: This item cannot be moved.
	THIS_ITEM_CANNOT_BE_MOVED(97),
	// Message: This item cannot be traded or sold.
	THIS_ITEM_CANNOT_BE_TRADED_OR_SOLD(99),
	// Message: You cannot exit the game while in combat.
	YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT(101),
	// Message: You cannot restart while in combat.
	YOU_CANNOT_RESTART_WHILE_IN_COMBAT(102),
	// Message: $c1 has been invited to the party.
	C1_HAS_BEEN_INVITED_TO_THE_PARTY(105),
	// Message:
	YOU_HAVE_JOINED_S1S_PARTY(106),
	// Message:
	S1_HAS_JOINED_THE_PARTY(107),
	// Message:
	S1_HAS_LEFT_THE_PARTY(108),
	// Message: Invalid target.
	INVALID_TARGET(109),
	// Message: $s1’s effect can be felt.
	S1S_EFFECT_CAN_BE_FELT(110),
	// Message: Your shield defense has succeeded.
	YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED(111),
	// Message: $s1 cannot be used due to unsuitable terms.
	S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS(113),
	// Message: You have entered the shadow of the Mother Tree.
	YOU_HAVE_ENTERED_THE_SHADOW_OF_THE_MOTHER_TREE(114),
	// Message: You have left the shadow of the Mother Tree.
	YOU_HAVE_LEFT_THE_SHADOW_OF_THE_MOTHER_TREE(115),
	// Message: You have entered a peace zone.
	YOU_HAVE_ENTERED_A_PEACE_ZONE(116),
	// Message: You have left the peace zone.
	YOU_HAVE_LEFT_THE_PEACE_ZONE(117),
	// Message: You have requested a trade with $c1.
	YOU_HAVE_REQUESTED_A_TRADE_WITH_C1(118),
	// Message: $c1 has denied your request to trade.
	C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE(119),
	// Message: You begin trading with $c1.
	YOU_BEGIN_TRADING_WITH_C1(120),
	// $c1 подтверждает сделку.
	C1_HAS_CONFIRMED_THE_TRADE(121),
	// Message: You may no longer adjust items in the trade because the trade has been confirmed.
	YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED(122),
	// Message: Your trade was successful.
	YOUR_TRADE_WAS_SUCCESSFUL(123),
	// Message: $c1 has cancelled the trade.
	C1_HAS_CANCELLED_THE_TRADE(124),
	// Message: Do you wish to exit the game?
	DO_YOU_WISH_TO_EXIT_THE_GAME(125),
	// Message: Do you wish to exit to the character select screen?
	DO_YOU_WISH_TO_EXIT_TO_THE_CHARACTER_SELECT_SCREEN(126),
	// Message: You have been disconnected from the server. Please login again.
	YOU_HAVE_BEEN_DISCONNECTED_FROM_THE_SERVER_(127),
	// Message: Your character creation has failed.
	YOUR_CHARACTER_CREATION_HAS_FAILED(128),
	// Message: Your inventory is full.
	YOUR_INVENTORY_IS_FULL(129),
	// Message: Your warehouse is full.
	YOUR_WAREHOUSE_IS_FULL(130),
	// Message: $s1 has logged in.
	S1_HAS_LOGGED_IN(131),
	// Message: $s1 has been added to your friends list.
	S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST(132),
	// Message: $s1 has been removed from your friends list.
	S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST(133),
	// Message: Please check your friends list again.
	PLEASE_CHECK_YOUR_FRIENDS_LIST_AGAIN(134),
	// Message: $c1 did not reply to your invitation. Your invitation has been cancelled.
	C1_DID_NOT_REPLY_TO_YOUR_INVITATION(135),
	// Message: You have not replied to $c1's invitation. The offer has been cancelled.
	YOU_HAVE_NOT_REPLIED_TO_C1S_INVITATION(136),
	// Message: There are no more items in the shortcut.
	THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT(137),
	// Message: Designate shortcut.
	DESIGNATE_SHORTCUT(138),
	// Message: $c1 has resisted your $s2.
	C1_HAS_RESISTED_YOUR_S2(139),
	// Message: Your skill was deactivated due to lack of MP.
	YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP(140),
	// Message: Once a trade is confirmed, the items involved cannot be moved again. If you wish to make a change, cancel the trade and start again.
	ONCE_A_TRADE_IS_CONFIRMED_THE_ITEMS_INVOLVED_CANNOT_BE_MOVED_AGAIN(141),
	// Message: You are already trading with someone.
	YOU_ARE_ALREADY_TRADING_WITH_SOMEONE(142),
	// Message: $c1 is already trading with another person. Please try again later.
	C1_IS_ALREADY_TRADING_WITH_ANOTHER_PERSON(143),
	// Message: That is an incorrect target.
	THAT_IS_AN_INCORRECT_TARGET(144),
	// Message: That player is not online.
	THAT_PLAYER_IS_NOT_ONLINE(145),
	// Message: You cannot discard something that far away from you.
	YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU(151),
	// Message: You have invited the wrong target.
	YOU_HAVE_INVITED_THE_WRONG_TARGET(152),
	// Message: $c1 is on another task. Please try again later.
	C1_IS_ON_ANOTHER_TASK(153),
	// Message: Only the leader can give out invitations.
	ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS(154),
	// Message: The party is full.
	THE_PARTY_IS_FULL(155),
	// Message: Your attack has failed.
	YOUR_ATTACK_HAS_FAILED(158),
	// Message: $c1 is a member of another party and cannot be invited.
	C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED(160),
	// Message: That player is not currently online.
	THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE(161),
	// Message: You have moved too far away from the warehouse to perform that action.
	YOU_HAVE_MOVED_TOO_FAR_AWAY_FROM_THE_WAREHOUSE_TO_PERFORM_THAT_ACTION(162),
	// Message: You cannot destroy it because the number is incorrect.
	YOU_CANNOT_DESTROY_IT_BECAUSE_THE_NUMBER_IS_INCORRECT(163),
	// Message: Waiting for another reply.
	WAITING_FOR_ANOTHER_REPLY(164),
	// Message: You cannot add yourself to your own friend list.
	YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST(165),
	// Message: Friend list is not ready yet. Please register again later.
	FRIEND_LIST_IS_NOT_READY_YET(166),
	// Message: $c1 is already on your friend list.
	C1_IS_ALREADY_ON_YOUR_FRIEND_LIST(167),
	// Message: $c1 has sent a friend request.
	C1_HAS_SENT_A_FRIEND_REQUEST(168),
	// Message: Accept friendship 0/1 (1 to accept, 0 to deny)
	ACCEPT_FRIENDSHIP_01_1_TO_ACCEPT_0_TO_DENY(169),
	// Message: The user who requested to become friends is not found in the game.
	THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME(170),
	// Message: $c1 is not on your friend list.
	C1_IS_NOT_ON_YOUR_FRIEND_LIST(171),
	// Message: You lack the funds needed to pay for this transaction.
	YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION(172),
	// Message: You lack the funds needed to pay for this transaction.
	YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION_(173),
	// Message: That person's inventory is full.
	THAT_PERSONS_INVENTORY_IS_FULL(174),
	// Message: That skill has been de-activated as HP was fully recovered.
	THAT_SKILL_HAS_BEEN_DEACTIVATED_AS_HP_WAS_FULLY_RECOVERED(175),
	// Message: That person is in message refusal mode.
	THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE(176),
	// Message: Message refusal mode.
	MESSAGE_REFUSAL_MODE(177),
	// Message: Message acceptance mode.
	MESSAGE_ACCEPTANCE_MODE(178),
	// Message: You cannot discard those items here.
	YOU_CANNOT_DISCARD_THOSE_ITEMS_HERE(179),
	// Message: You have $s1 day(s) left until deletion. Do you wish to cancel this action?
	YOU_HAVE_S1_DAYS_LEFT_UNTIL_DELETION(180),
	// Message: Cannot see target.
	CANNOT_SEE_TARGET(181),
	// Message: Do you wish to stop the currently selected "$s1" quest?
	DO_YOU_WISH_TO_STOP_THE_CURRENTLY_SELECTED_S1_QUEST(182),
	// Message: There are too many users on the server. Please try again later.
	THERE_ARE_TOO_MANY_USERS_ON_THE_SERVER(183),
	// Message: Please try again later.
	PLEASE_TRY_AGAIN_LATER(184),
	// Message: You must first select a user to invite to your party.
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_PARTY(185),
	// Message: You must first select a user to invite to your clan.
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_CLAN(186),
	// Message: Select user to expel.
	SELECT_USER_TO_EXPEL(187),
	// Message: Please create your clan name.
	PLEASE_CREATE_YOUR_CLAN_NAME(188),
	// Message: Your clan has been created.
	YOUR_CLAN_HAS_BEEN_CREATED(189),
	// Message: You have failed to create a clan.
	YOU_HAVE_FAILED_TO_CREATE_A_CLAN(190),
	// Message: Clan member $s1 has been expelled.
	CLAN_MEMBER_S1_HAS_BEEN_EXPELLED(191),
	// Message: You have failed to expel $s1 from the clan.
	YOU_HAVE_FAILED_TO_EXPEL_S1_FROM_THE_CLAN(192),
	// Message: Clan has dispersed.
	CLAN_HAS_DISPERSED(193),
	// Message: You have failed to disperse the clan.
	YOU_HAVE_FAILED_TO_DISPERSE_THE_CLAN(194),
	// Message: Entered the clan.
	ENTERED_THE_CLAN(195),
	// Message: $s1 declined your clan invitation.
	S1_DECLINED_YOUR_CLAN_INVITATION(196),
	// Message: You have recently been dismissed from a clan. You are not allowed to join another clan for 24-hours.
	YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN(199),
	// Message: You have withdrawn from the party.
	YOU_HAVE_WITHDRAWN_FROM_THE_PARTY(200),
	// Message: $c1 was expelled from the party.
	C1_WAS_EXPELLED_FROM_THE_PARTY(201),
	// Message: You have been expelled from the party.
	YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY(202),
	// Message: The party has dispersed.
	THE_PARTY_HAS_DISPERSED(203),
	// Message: Incorrect name. Please try again.
	INCORRECT_NAME(204), INCORRECT_CHARACTER_NAME(205),
	// Message: Please enter the name of the clan you wish to declare war on.
	PLEASE_ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_DECLARE_WAR_ON(206),
	// Message: $s2 of the clan $s1 requests a declaration of war. Do you accept?
	S2_OF_THE_CLAN_S1_REQUESTS_A_DECLARATION_OF_WAR(207),
	// Message: Please include file type when entering file path.
	PLEASE_INCLUDE_FILE_TYPE_WHEN_ENTERING_FILE_PATH(208),
	// Message: The size of the image file is inappropriate. Please adjust to 16x12 pixels.
	THE_SIZE_OF_THE_IMAGE_FILE_IS_INAPPROPRIATE(209),
	// Message: Cannot find file. Please enter precise path.
	CANNOT_FIND_FILE(210),
	// Message: You can only register 16x12 pixel 256 color bmp files.
	YOU_CAN_ONLY_REGISTER_16X12_PIXEL_256_COLOR_BMP_FILES(211),
	// Message: You are not a clan member and cannot perform this action.
	YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION(212),
	// Message: Not working. Please try again later.
	NOT_WORKING(213),
	// Message: Your title has been changed.
	YOUR_TITLE_HAS_BEEN_CHANGED(214),
	// Message: War with the $s1 clan has begun.
	WAR_WITH_THE_S1_CLAN_HAS_BEGUN(215),
	// Message: War with the $s1 clan has ended.
	WAR_WITH_THE_S1_CLAN_HAS_ENDED(216),
	// Message: You have won the war over the $s1 clan!
	YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN(217),
	// Message: You have surrendered to the $s1 clan.
	YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN(218),
	// Message: Your clan leader has died. You have been defeated by the $s1 Clan.
	YOUR_CLAN_LEADER_HAS_DIED(219),
	// Message: You have $s1 minute(s) left until the clan war ends.
	YOU_HAVE_S1_MINUTES_LEFT_UNTIL_THE_CLAN_WAR_ENDS(220),
	// Message: The time limit for the clan war is up. War with the $s1 clan is over.
	THE_TIME_LIMIT_FOR_THE_CLAN_WAR_IS_UP(221),
	// Message: $s1 has joined the clan.
	S1_HAS_JOINED_THE_CLAN(222),
	// Message: $s1 has withdrawn from the clan.
	S1_HAS_WITHDRAWN_FROM_THE_CLAN(223),
	// Message: $s1 did not respond: Invitation to the clan has been cancelled.
	S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED(224),
	// Message: You didn't respond to $s1's invitation: joining has been cancelled.
	YOU_DIDNT_RESPOND_TO_S1S_INVITATION_JOINING_HAS_BEEN_CANCELLED(225),
	// Message: The $s1 clan did not respond: war proclamation has been refused.
	THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED(226),
	// Message: Clan war has been refused because you did not respond to $s1 clan's war proclamation.
	CLAN_WAR_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1_CLANS_WAR_PROCLAMATION(227),
	// Message: Request to end war has been denied.
	REQUEST_TO_END_WAR_HAS_BEEN_DENIED(228),
	// Message: You do not meet the criteria in order to create a clan.
	YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN(229),
	// Message: You must wait 10 days before creating a new clan.
	YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN(230),
	// Message: After a clan member is dismissed from a clan, the clan must wait at least a day before accepting a new member.
	AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER(231),
	// Message: After leaving or having been dismissed from a clan, you must wait at least a day before joining another clan.
	AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN(232),
	// Message: The Academy/Royal Guard/Order of Knights is full and cannot accept new members at this time.
	THE_ACADEMYROYAL_GUARDORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME(233),
	// Message: The target must be a clan member.
	THE_TARGET_MUST_BE_A_CLAN_MEMBER(234),
	// Message: Only the clan leader is enabled.
	ONLY_THE_CLAN_LEADER_IS_ENABLED(236),
	// Message: The clan leader could not be found.
	THE_CLAN_LEADER_COULD_NOT_BE_FOUND(237),
	// Message: Not joined in any clan.
	NOT_JOINED_IN_ANY_CLAN(238),
	// Message: A clan leader cannot withdraw from their own clan.
	A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN(239),
	// Message: You are currently involved in clan war.
	YOU_ARE_CURRENTLY_INVOLVED_IN_CLAN_WAR(240),
	// Message: Leader of the $s1 Clan is not logged in.
	LEADER_OF_THE_S1_CLAN_IS_NOT_LOGGED_IN(241),
	// Message: Select target.
	SELECT_TARGET(242),
	// Message: You cannot declare war on an allied clan.
	YOU_CANNOT_DECLARE_WAR_ON_AN_ALLIED_CLAN(243),
	// Message: You are not allowed to issue this challenge.
	YOU_ARE_NOT_ALLOWED_TO_ISSUE_THIS_CHALLENGE(244),
	// Message: It has not been 5 days since you refused a clan war. Do you wish to continue?
	IT_HAS_NOT_BEEN_5_DAYS_SINCE_YOU_REFUSED_A_CLAN_WAR(245),
	// Message: That clan is currently at war.
	THAT_CLAN_IS_CURRENTLY_AT_WAR(246),
	// Message: You have already been at war with the $s1 clan: 5 days must pass before you can challenge this clan again.
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_CHALLENGE_THIS_CLAN_AGAIN(247),
	// Message: You cannot proclaim war: the $s1 clan does not have enough members.
	YOU_CANNOT_PROCLAIM_WAR_THE_S1_CLAN_DOES_NOT_HAVE_ENOUGH_MEMBERS(248),
	// Message: Do you wish to surrender to clan $s1?
	DO_YOU_WISH_TO_SURRENDER_TO_CLAN_S1(249),
	// Message: You have personally surrendered to the $s1 clan. You are no longer participating in this clan war.
	YOU_HAVE_PERSONALLY_SURRENDERED_TO_THE_S1_CLAN(250),
	// Message: You cannot proclaim war: you are at war with another clan.
	YOU_CANNOT_PROCLAIM_WAR_YOU_ARE_AT_WAR_WITH_ANOTHER_CLAN(251),
	// Message: Enter the name of the clan you wish to surrender to.
	ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_SURRENDER_TO(252),
	// Message: Enter the name of the clan you wish to end the war with.
	ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_END_THE_WAR_WITH(253),
	// Message: A clan leader cannot personally surrender.
	A_CLAN_LEADER_CANNOT_PERSONALLY_SURRENDER(254),
	// Message: The $s1 Clan has requested to end war. Do you agree?
	THE_S1_CLAN_HAS_REQUESTED_TO_END_WAR(255),
	// Message: Enter Title
	ENTER_TITLE(256),
	// Message: Do you offer the $s1 clan a proposal to end the war?
	DO_YOU_OFFER_THE_S1_CLAN_A_PROPOSAL_TO_END_THE_WAR(257),
	// Message: You are not involved in a clan war.
	YOU_ARE_NOT_INVOLVED_IN_A_CLAN_WAR(258),
	// Message: Select clan members from list.
	SELECT_CLAN_MEMBERS_FROM_LIST(259),
	// Message: The clan reputation score was reduced because it hasn't been 5 days since refusing a clan war.
	THE_CLAN_REPUTATION_SCORE_WAS_REDUCED_BECAUSE_IT_HASNT_BEEN_5_DAYS_SINCE_REFUSING_A_CLAN_WAR(260),
	// Message: Clan name is invalid.
	CLAN_NAME_IS_INVALID(261),
	// Message: Clan name's length is incorrect.
	CLAN_NAMES_LENGTH_IS_INCORRECT(262),
	// Message: You have already requested the dissolution of your clan.
	YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN(263),
	// Message: You cannot dissolve a clan while engaged in a war.
	YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR(264),
	// Message: You cannot dissolve a clan during a siege or while protecting a castle.
	YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE(265),
	// Message: You cannot dissolve a clan while owning a clan hall or castle.
	YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE(266),
	// Message: There are no requests to disperse.
	THERE_ARE_NO_REQUESTS_TO_DISPERSE(267),
	// Message: That player already belongs to another clan.
	THAT_PLAYER_ALREADY_BELONGS_TO_ANOTHER_CLAN(268),
	// Message: You cannot dismiss yourself.
	YOU_CANNOT_DISMISS_YOURSELF(269),
	// Message: You have already surrendered.
	YOU_HAVE_ALREADY_SURRENDERED(270),
	// Message: A player can only be granted a title if the clan is level 3 or above.
	A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE(271),
	// Message: A clan crest can only be registered when the clan's skill level is 3 or above.
	A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLANS_SKILL_LEVEL_IS_3_OR_ABOVE(272),
	// Message: A clan war can only be declared when a clan's level is 3 or above.
	A_CLAN_WAR_CAN_ONLY_BE_DECLARED_WHEN_A_CLANS_LEVEL_IS_3_OR_ABOVE(273),
	// Message: Your clan's level has increased.
	YOUR_CLANS_LEVEL_HAS_INCREASED(274),
	// Message: The clan has failed to increase its level.
	THE_CLAN_HAS_FAILED_TO_INCREASE_ITS_LEVEL(275),
	// Message: You do not have the necessary materials or prerequisites to learn this skill.
	YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL(276),
	// Message: You have earned $s1.
	YOU_HAVE_EARNED_S1_SKILL(277),
	// Message: You do not have enough SP to learn this skill.
	YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL(278),
	// Message: You do not have enough adena.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA(279),
	// Message: You do not have any items to sell.
	YOU_DO_NOT_HAVE_ANY_ITEMS_TO_SELL(280),
	// Message: You do not have enough adena to pay the fee.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_PAY_THE_FEE(281),
	// Message:
	YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE(282),
	// Message: You have entered a combat zone.
	YOU_HAVE_ENTERED_A_COMBAT_ZONE(283),
	// Message: You have left a combat zone.
	YOU_HAVE_LEFT_A_COMBAT_ZONE(284),
	// Message: Clan $s1 has successfully engraved the holy artifact!
	CLAN_S1_HAS_SUCCESSFULLY_ENGRAVED_THE_HOLY_ARTIFACT(285),
	// Message: Your base is being attacked.
	YOUR_BASE_IS_BEING_ATTACKED(286),
	// Message: The opposing clan has started to engrave the holy artifact!
	THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT(287),
	// Message: The castle gate has been destroyed.
	THE_CASTLE_GATE_HAS_BEEN_DESTROYED(288),
	// Message: An outpost or headquarters cannot be built because one already exists.
	AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS(289),
	// Message: You cannot set up a base here.
	YOU_CANNOT_SET_UP_A_BASE_HERE(290),
	// Message: Clan $s1 is victorious over $s2's castle siege!
	CLAN_S1_IS_VICTORIOUS_OVER_S2S_CASTLE_SIEGE(291),
	// Message: $s1 has announced the next castle siege time.
	S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME(292),
	// Message: The registration term for $s1 has ended.
	THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED(293),
	// Message: You cannot summon the encampment because you are not a member of the siege clan involved in the castle / fortress / hideout siege.
	YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE(294),
	// Message: $s1's siege was canceled because there were no clans that participated.
	S1S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED(295),
	// Message:
	YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE(297),
	// Message: You have dropped $s1.
	YOU_HAVE_DROPPED_S1(298),
	// Message: $c1 has obtained $s3 $s2.
	C1_HAS_OBTAINED_S3_S2(299),
	// Message: $c1 has obtained $s2.
	C1_HAS_OBTAINED_S2(300),
	// Message: $s2 $s1 has disappeared.
	S2_S1_HAS_DISAPPEARED(301),
	// Message: $s1 has disappeared.
	S1_HAS_DISAPPEARED(302),
	// Message: Clan member $s1 has logged into game.
	CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME(304),
	// Message: The player declined to join your party.
	THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY(305),
	// Message: You have failed to delete the character.
	YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER(306),
	// Message: You cannot trade with a warehouse keeper.
	YOU_CANNOT_TRADE_WITH_A_WAREHOUSE_KEEPER(307),
	// Message: The player declined your clan invitation.
	THE_PLAYER_DECLINED_YOUR_CLAN_INVITATION(308),
	// Message: You have succeeded in expelling the clan member.
	YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN_MEMBER(309),
	// Message: You have failed to expel the clan member.
	YOU_HAVE_FAILED_TO_EXPEL_THE_CLAN_MEMBER(310),
	// Message: The clan war declaration has been accepted.
	THE_CLAN_WAR_DECLARATION_HAS_BEEN_ACCEPTED(311),
	// Message: The clan war declaration has been refused.
	THE_CLAN_WAR_DECLARATION_HAS_BEEN_REFUSED(312),
	// Message: The cease war request has been accepted.
	THE_CEASE_WAR_REQUEST_HAS_BEEN_ACCEPTED(313),
	// Message: You have failed to surrender.
	YOU_HAVE_FAILED_TO_SURRENDER(314),
	// Message: You have failed to personally surrender.
	YOU_HAVE_FAILED_TO_PERSONALLY_SURRENDER(315),
	// Message: You have failed to withdraw from the party.
	YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_PARTY(316),
	// Message: You have failed to expel the party member.
	YOU_HAVE_FAILED_TO_EXPEL_THE_PARTY_MEMBER(317),
	// Message: You have failed to disperse the party.
	YOU_HAVE_FAILED_TO_DISPERSE_THE_PARTY(318),
	// Message: This door cannot be unlocked.
	THIS_DOOR_CANNOT_BE_UNLOCKED(319),
	// Message: You have failed to unlock the door.
	YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR(320),
	// Message: It is not locked.
	IT_IS_NOT_LOCKED(321),
	// Message: Please decide on the sales price.
	PLEASE_DECIDE_ON_THE_SALES_PRICE(322),
	// Message: Your force has increased to level $s1.
	YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1(323),
	// Message: Your force has reached maximum capacity.
	YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY(324),
	// Message: The corpse has already disappeared.
	THE_CORPSE_HAS_ALREADY_DISAPPEARED(325),
	// Message: Select target from list.
	SELECT_TARGET_FROM_LIST(326),
	// Message: You cannot exceed 80 characters.
	YOU_CANNOT_EXCEED_80_CHARACTERS(327),
	// Message: Please input title using less than 128 characters.
	PLEASE_INPUT_TITLE_USING_LESS_THAN_128_CHARACTERS(328),
	// Message: Please input contents using less than 3000 characters.
	PLEASE_INPUT_CONTENTS_USING_LESS_THAN_3000_CHARACTERS(329),
	// Message: A one-line response may not exceed 128 characters.
	A_ONELINE_RESPONSE_MAY_NOT_EXCEED_128_CHARACTERS(330),
	// Message: You have acquired $s1 SP.
	YOU_HAVE_ACQUIRED_S1_SP(331),
	// Message: Do you want to be restored?
	DO_YOU_WANT_TO_BE_RESTORED(332),
	// Message: You have received $s1 damage by Core's barrier.
	YOU_HAVE_RECEIVED_S1_DAMAGE_BY_CORES_BARRIER(333),
	// Message: Please enter your private store display message.
	PLEASE_ENTER_YOUR_PRIVATE_STORE_DISPLAY_MESSAGE(334),
	// Message: $s1 has been aborted.
	S1_HAS_BEEN_ABORTED(335),
	// Message: You are attempting to crystallize $s1. Do you wish to continue?
	YOU_ARE_ATTEMPTING_TO_CRYSTALLIZE_S1(336),
	// Message: The soulshot you are attempting to use does not match the grade of your equipped weapon.
	THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON(337),
	// Message: You do not have enough soulshots for that.
	YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT(338),
	// Message: Cannot use soulshots.
	CANNOT_USE_SOULSHOTS(339),
	// Message: Your private store is now open for business.
	YOUR_PRIVATE_STORE_IS_NOW_OPEN_FOR_BUSINESS(340),
	// Message: You do not have enough materials to perform that action.
	YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION(341),
	// Message: Your soulshots are enabled.
	YOUR_SOULSHOTS_ARE_ENABLED(342),
	// Message: Sweeper failed, target not spoiled.
	SWEEPER_FAILED_TARGET_NOT_SPOILED(343),
	// Message: Your soulshots are disabled.
	YOUR_SOULSHOTS_ARE_DISABLED(344),
	// Message: Chat enabled.
	CHAT_ENABLED(345),
	// Message: Chat disabled.
	CHAT_DISABLED(346),
	// Message: Incorrect item count.
	INCORRECT_ITEM_COUNT(347),
	// Message: Incorrect item price.
	INCORRECT_ITEM_PRICE(348),
	// Message: Private store already closed.
	PRIVATE_STORE_ALREADY_CLOSED(349),
	// Message: Item out of stock.
	ITEM_OUT_OF_STOCK(350),
	// Message: Incorrect item count.
	INCORRECT_ITEM_COUNT_(351),
	// Message: Inappropriate enchant conditions.
	INAPPROPRIATE_ENCHANT_CONDITIONS(355),
	// Message: $s1 hour(s) until castle siege conclusion.
	S1_HOURS_UNTIL_CASTLE_SIEGE_CONCLUSION(358),
	// Message: It has already been spoiled.
	IT_HAS_ALREADY_BEEN_SPOILED(357),
	// Message: $s1 minute(s) until castle siege conclusion.
	S1_MINUTES_UNTIL_CASTLE_SIEGE_CONCLUSION(359),
	// Message: This castle siege will end in $s1 second(s)!
	THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECONDS(360),
	// Message: You have obtained a +$s1 $s2.
	YOU_HAVE_OBTAINED_A_S1_S2(369),
	// Message: $c1 has obtained +$s2$s3.
	C1_HAS_OBTAINED_S2S3(376),
	// Message: $S1 $S2 disappeared.
	S1_S2_DISAPPEARED(377),
	// Message: $c1 purchased $s2.
	C1_PURCHASED_S2(378),
	// Message: $c1 purchased +$s2$s3.
	C1_PURCHASED_S2S3(379),
	// Message: $c1 purchased $s3 $s2(s).
	C1_PURCHASED_S3_S2S(380),
	// Message: The game client encountered an error and was unable to connect to the petition server.
	THE_GAME_CLIENT_ENCOUNTERED_AN_ERROR_AND_WAS_UNABLE_TO_CONNECT_TO_THE_PETITION_SERVER(381),
	// Message: Currently there are no users that have checked out a GM ID.
	CURRENTLY_THERE_ARE_NO_USERS_THAT_HAVE_CHECKED_OUT_A_GM_ID(382),
	// Message: Request confirmed to end consultation at petition server.
	REQUEST_CONFIRMED_TO_END_CONSULTATION_AT_PETITION_SERVER(383),
	// Message: The client is not logged onto the game server.
	THE_CLIENT_IS_NOT_LOGGED_ONTO_THE_GAME_SERVER(384),
	// Message: Request confirmed to begin consultation at petition server.
	REQUEST_CONFIRMED_TO_BEGIN_CONSULTATION_AT_PETITION_SERVER(385),
	// Message: The body of your petition must be more than five characters in length.
	THE_BODY_OF_YOUR_PETITION_MUST_BE_MORE_THAN_FIVE_CHARACTERS_IN_LENGTH(386),
	// Message: This ends the GM petition consultation. \nPlease give us feedback on the petition service.
	THIS_ENDS_THE_GM_PETITION_CONSULTATION(387),
	// Message: Not under petition consultation.
	NOT_UNDER_PETITION_CONSULTATION(388),
	// Message: Your petition application has been accepted. \n - Receipt No. is $s1.
	YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED(389),
	// Message: You may only submit one petition (active) at a time.
	YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME(390),
	// Message: Receipt No. $s1: petition cancelled.
	RECEIPT_NO(391),
	// Message: Petition underway.
	PETITION_UNDERWAY(392),
	// Message: Failed to cancel petition. Please try again later.
	FAILED_TO_CANCEL_PETITION(393),
	// Message: Starting petition consultation with $c1.
	STARTING_PETITION_CONSULTATION_WITH_C1(394),
	// Message: Ending petition consultation with $c1.
	ENDING_PETITION_CONSULTATION_WITH_C1(395),
	// Message: Please login after changing your temporary password.
	PLEASE_LOGIN_AFTER_CHANGING_YOUR_TEMPORARY_PASSWORD(396),
	// Message: This is not a paid account.
	THIS_IS_NOT_A_PAID_ACCOUNT(397),
	// Message: There is no time left on this account.
	THERE_IS_NO_TIME_LEFT_ON_THIS_ACCOUNT(398),
	// Message: System error.
	SYSTEM_ERROR(399),
	// Message: You are attempting to drop $s1. Do you wish to continue?
	YOU_ARE_ATTEMPTING_TO_DROP_S1(400),
	// Message: You have too many ongoing quests.
	YOU_HAVE_TOO_MANY_ONGOING_QUESTS(401),
	// Message: You do not possess the correct ticket to board the boat.
	YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT(402),
	// Message: You have exceeded your out-of-pocket adena limit.
	YOU_HAVE_EXCEEDED_YOUR_OUTOFPOCKET_ADENA_LIMIT(403),
	// Message: Your Create Item level is too low to register this recipe.
	YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE(404),
	// Message: The total price of the product is too high.
	THE_TOTAL_PRICE_OF_THE_PRODUCT_IS_TOO_HIGH(405),
	// Message: Petition application accepted.
	PETITION_APPLICATION_ACCEPTED(406),
	// Message: Your petition is being processed.
	YOUR_PETITION_IS_BEING_PROCESSED(407),
	// Message: Set Period
	SET_PERIOD(408),
	// Message: Set Time-$s1: $s2: $s3
	SET_TIMES1_S2_S3(409),
	// Message: Registration Period
	REGISTRATION_PERIOD(410),
	// Message: Registration TIme-$s1: $s2: $s3
	REGISTRATION_TIMES1_S2_S3(411),
	// Message: Battle begins in $s1: $s2: $s4
	BATTLE_BEGINS_IN_S1_S2_S4(412),
	// Message: Battle ends in $s1: $s2: $s5
	BATTLE_ENDS_IN_S1_S2_S5(413),
	// Message: Standby
	STANDBY(414),
	// Message: Siege is underway
	SIEGE_IS_UNDERWAY(415),
	// Message: This item cannot be exchanged.
	THIS_ITEM_CANNOT_BE_EXCHANGED(416),
	// Message: $s1 has been disarmed.
	S1_HAS_BEEN_DISARMED(417),
	// Message: There is a significant difference between the item's price and its standard price. Please check again.
	THERE_IS_A_SIGNIFICANT_DIFFERENCE_BETWEEN_THE_ITEMS_PRICE_AND_ITS_STANDARD_PRICE(418),
	// Message: $s1 minute(s) of usage time lel2f.
	S1_MINUTES_OF_USAGE_TIME_LEFT(419),
	// Message: Time expired.
	TIME_EXPIRED(420),
	// Message: Another person has logged in with the same account.
	ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT(421),
	// Message: You have exceeded the weight limit.
	YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT(422),
	// Message: You have cancelled the enchanting process.
	YOU_HAVE_CANCELLED_THE_ENCHANTING_PROCESS(423),
	// Message: Does not fit strengthening conditions of the scroll.
	DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL(424),
	// Message: Your Create Item level is too low to register this recipe.
	YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE_(425),
	// Message: Your account has been reported for intentionally not paying the cyber cafй fees.
	YOUR_ACCOUNT_HAS_BEEN_REPORTED_FOR_INTENTIONALLY_NOT_PAYING_THE_CYBER_CAF_FEES(426),
	// Message: Please contact us.
	PLEASE_CONTACT_US(427),
	// Message: Use of your account has been limited due to alleged account thel2f. If you have nothing to do with account theft, please visit the Support Center on the NCsoft website
	// (http://us.ncsol2f.com/support).
	USE_OF_YOUR_ACCOUNT_HAS_BEEN_LIMITED_DUE_TO_ALLEGED_ACCOUNT_THEFT(428),
	// Message: In accordance with company policy, your account has been suspended for submitting a false report. Submitting a false report to the Support Center may harm other players.
	// For more information on account suspension, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	IN_ACCORDANCE_WITH_COMPANY_POLICY_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_SUBMITTING_A_FALSE_REPORT(429),
	// Message: 번역불필요 (Doesn't need to translate.)
	__DOESNT_NEED_TO_TRANSLATE(430),
	// Message: Your account has been suspended for violating the EULA, RoC and/or User Agreement. When a user violates the terms of the User Agreement, the company can impose a
	// restriction on the applicable user's account. For more information on account suspension, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_VIOLATING_THE_EULA_ROC_ANDOR_USER_AGREEMENT(431),
	// Message: Your account has been suspended for 7 days (retroactive to the day of disclosure), under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for dealing or
	// attempting to deal items or characters (accounts) within the game in exchange for cash or items of other games. Suspension of your account will automatically expire after 7 days.
	// For more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_7_DAYS_RETROACTIVE_TO_THE_DAY_OF_DISCLOSURE_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_AGREEMENT_FOR_DEALING_OR_ATTEMPTING_TO_DEAL_ITEMS_OR_CHARACTERS_ACCOUNTS_WITHIN_THE_GAME_IN_EXCHANGE_FOR_CASH_OR_ITEMS_OF_OTHER_GAMES(432),
	// Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for dealing or attempting to deal items or characters (accounts)
	// within the game in exchange for cash or items of other games. For more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_AGREEMENT_FOR_DEALING_OR_ATTEMPTING_TO_DEAL_ITEMS_OR_CHARACTERS_ACCOUNTS_WITHIN_THE_GAME_IN_EXCHANGE_FOR_CASH_OR_ITEMS_OF_OTHER_GAMES(433),
	// Message: Your account has been suspended, under Chapter 3, Section 14 of the Lineage II Service Use Agreement, for unethical behavior or fraud. For more information, please visit
	// the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_3_SECTION_14_OF_THE_LINEAGE_II_SERVICE_USE_AGREEMENT_FOR_UNETHICAL_BEHAVIOR_OR_FRAUD(434),
	// Message: Your account has been suspended for unethical behavior. For more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_UNETHICAL_BEHAVIOR(435),
	// Message: Your account has been suspended for abusing the game system or exploiting bug(s). Abusing bug(s) may cause critical situations as well as harm the game world's balance.
	// For more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_ABUSING_THE_GAME_SYSTEM_OR_EXPLOITING_BUGS(436),
	// Message: Your account has been suspended for using illegal software which has not been authorized by our company. For more information, please visit the Support Center on the
	// NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_USING_ILLEGAL_SOFTWARE_WHICH_HAS_NOT_BEEN_AUTHORIZED_BY_OUR_COMPANY(437),
	// Message: Your account has been suspended for impersonating an official Game Master or staff member. For more information, please visit the Support Center on the NCsoft website
	// (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_IMPERSONATING_AN_OFFICIAL_GAME_MASTER_OR_STAFF_MEMBER(438),
	// Message: In accordance with the company's User Agreement and Operational Policy this account has been suspended at the account holder's request. If you have any questions
	// regarding your account please contact support at http://us.ncsol2f.com/support.
	IN_ACCORDANCE_WITH_THE_COMPANYS_USER_AGREEMENT_AND_OPERATIONAL_POLICY_THIS_ACCOUNT_HAS_BEEN_SUSPENDED_AT_THE_ACCOUNT_HOLDERS_REQUEST(439),
	// Message: Because you are registered as a minor, your account has been suspended at the request of your parents or guardian. For more information, please visit the Support Center
	// on the NCsoft website (http://us.ncsol2f.com/support).
	BECAUSE_YOU_ARE_REGISTERED_AS_A_MINOR_YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_AT_THE_REQUEST_OF_YOUR_PARENTS_OR_GUARDIAN(440),
	// Message: Per our company's User Agreement, the use of this account has been suspended. If you have any questions regarding your account please contact support at
	// http://us.ncsol2f.com/support.
	PER_OUR_COMPANYS_USER_AGREEMENT_THE_USE_OF_THIS_ACCOUNT_HAS_BEEN_SUSPENDED(441),
	// Message: Your account has been suspended, under Chapter 2, Section 7 of the Lineage II Service Use Agreement, for misappropriating payment under another player's account. For
	// more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_UNDER_CHAPTER_2_SECTION_7_OF_THE_LINEAGE_II_SERVICE_USE_AGREEMENT_FOR_MISAPPROPRIATING_PAYMENT_UNDER_ANOTHER_PLAYERS_ACCOUNT(442),
	// Message: Ownership of this account needs to be verified. For more information, Please submit a ticket at the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	OWNERSHIP_OF_THIS_ACCOUNT_NEEDS_TO_BE_VERIFIED(443),
	// Message: Since we have received a withdrawal request from the holder of this account access to all applicable accounts has been automatically suspended.
	SINCE_WE_HAVE_RECEIVED_A_WITHDRAWAL_REQUEST_FROM_THE_HOLDER_OF_THIS_ACCOUNT_ACCESS_TO_ALL_APPLICABLE_ACCOUNTS_HAS_BEEN_AUTOMATICALLY_SUSPENDED(444),
	// Message: (Reference Number Regarding Membership Withdrawal Request: $s1)
	REFERENCE_NUMBER_REGARDING_MEMBERSHIP_WITHDRAWAL_REQUEST_S1(445),
	// Message: For more information, please visit the Support Center on the NCsoft website (http://us.ncsol2f.com/support).
	FOR_MORE_INFORMATION_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_NCSOFT_WEBSITE_HTTPUS(446),
	// Message: There is a system error. Please log in again later.
	THERE_IS_A_SYSTEM_ERROR(448),
	// Message: The password you have entered is incorrect.
	THE_PASSWORD_YOU_HAVE_ENTERED_IS_INCORRECT(449),
	// Message: Confirm your account information and log in again later.
	CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_LOG_IN_AGAIN_LATER(450),
	// Message: The password you have entered is incorrect.
	THE_PASSWORD_YOU_HAVE_ENTERED_IS_INCORRECT_(451),
	// Message: Please confirm your account information and try logging in again.
	PLEASE_CONFIRM_YOUR_ACCOUNT_INFORMATION_AND_TRY_LOGGING_IN_AGAIN(452),
	// Message: Your account information is incorrect.
	YOUR_ACCOUNT_INFORMATION_IS_INCORRECT(453),
	// Message: For more details, please contact our customer service center at http://us.ncsol2f.com/support.
	FOR_MORE_DETAILS_PLEASE_CONTACT_OUR_CUSTOMER_SERVICE_CENTER_AT_HTTPUS(454),
	// Message: Account is already in use. Unable to log in.
	ACCOUNT_IS_ALREADY_IN_USE(455),
	// Message: Lineage II game services may be used by individuals 15 years of age or older except for PvP servers, which may only be used by adults 18 years of age and older. (Korea
	// Only)
	LINEAGE_II_GAME_SERVICES_MAY_BE_USED_BY_INDIVIDUALS_15_YEARS_OF_AGE_OR_OLDER_EXCEPT_FOR_PVP_SERVERS_WHICH_MAY_ONLY_BE_USED_BY_ADULTS_18_YEARS_OF_AGE_AND_OLDER(456),
	// Message: We are currently undergoing game server maintenance. Please log in again later.
	WE_ARE_CURRENTLY_UNDERGOING_GAME_SERVER_MAINTENANCE(457),
	// Message: Your game time has expired. You can not login.
	YOUR_GAME_TIME_HAS_EXPIRED(458),
	// Message: To continue playing, please purchase Lineage II
	TO_CONTINUE_PLAYING_PLEASE_PURCHASE_LINEAGE_II(459),
	// Message: either directly from the PlayNC Store or from any leading games retailer.
	EITHER_DIRECTLY_FROM_THE_PLAYNC_STORE_OR_FROM_ANY_LEADING_GAMES_RETAILER(460),
	// Message: Access failed.
	ACCESS_FAILED(461),
	// Message: Please try again later.
	PLEASE_TRY_AGAIN_LATER_(462),
	// Message: This feature is only available to alliance leaders.
	THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS(464),
	// Message: You are not currently allied with any clans.
	YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS(465),
	// Message: You have exceeded the limit.
	YOU_HAVE_EXCEEDED_THE_LIMIT(466),
	// Message: You may not accept any clan within a day after expelling another clan.
	YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN(467),
	// Message: A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.
	A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION(468),
	// Message: You may not ally with a clan you are currently at war with. That would be diabolical and treacherous.
	YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH(469),
	// Message: Only the clan leader may apply for withdrawal from the alliance.
	ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE(470),
	// Message: Alliance leaders cannot withdraw.
	ALLIANCE_LEADERS_CANNOT_WITHDRAW(471),
	// Message: You cannot expel yourself from the clan.
	YOU_CANNOT_EXPEL_YOURSELF_FROM_THE_CLAN(472),
	// Message: Different alliance.
	DIFFERENT_ALLIANCE(473),
	// Message: That clan does not exist.
	THAT_CLAN_DOES_NOT_EXIST(474),
	// Message: Different alliance.
	DIFFERENT_ALLIANCE_(475),
	// Message: Please adjust the image size to 8x12.
	PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12(476),
	// Message: No response. Invitation to join an alliance has been cancelled.
	NO_RESPONSE(477),
	// Message: No response. Your entrance to the alliance has been cancelled.
	NO_RESPONSE_(478),
	// Message: $s1 has joined as a friend.
	S1_HAS_JOINED_AS_A_FRIEND(479),
	// Message: Please check your friends list.
	PLEASE_CHECK_YOUR_FRIENDS_LIST(480),
	// Message: $s1 has been deleted from your friends list.
	S1_HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST(481),
	// Message: You cannot add yourself to your own friend list.
	YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST_(482),
	// Message: This function is inaccessible right now. Please try again later.
	THIS_FUNCTION_IS_INACCESSIBLE_RIGHT_NOW(483),
	// Message: This player is already registered on your friends list.
	THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST(484),
	// Message: No new friend invitations may be accepted.
	NO_NEW_FRIEND_INVITATIONS_MAY_BE_ACCEPTED(485),
	// Message: The following user is not on your friends list.
	THE_FOLLOWING_USER_IS_NOT_ON_YOUR_FRIENDS_LIST(486),
	// Message: ======<Friends List>======
	FRIENDS_LIST(487),
	// Message: $s1 (Currently: Online)
	S1_CURRENTLY_ONLINE(488),
	// Message: $s1 (Currently: Offline)
	S1_CURRENTLY_OFFLINE(489),
	// Message: ========================
	__EQUALS__(490),
	// Message: =======<Alliance Information>=======
	ALLIANCE_INFORMATION(491),
	// Message: Alliance Name: $s1
	ALLIANCE_NAME_S1(492),
	// Message: Connection: $s1 / Total $s2
	CONNECTION_S1__TOTAL_S2(493),
	// Message: Alliance Leader: $s2 of $s1
	ALLIANCE_LEADER_S2_OF_S1(494),
	// Message: Affiliated clans: Total $s1 clan(s)
	AFFILIATED_CLANS_TOTAL_S1_CLANS(495),
	// Message: =====<Clan Information>=====
	CLAN_INFORMATION(496),
	// Message: Clan Name: $s1
	CLAN_NAME_S1(497),
	// Message: Clan Leader: $s1
	CLAN_LEADER__S1(498),
	// Message: Clan Level: $s1
	CLAN_LEVEL_S1(499),
	// Message: ------------------------
	__DASHES__(500),
	// Message: ========================
	SYSMSG_ID501(501),
	// Message: You already belong to another alliance.
	YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE(502),
	// Message: Your friend $s1 just logged in.
	YOUR_FRIEND_S1_JUST_LOGGED_IN(503),
	// Message: Only clan leaders may create alliances.
	ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES(504),
	// Message: You cannot create a new alliance within 1 day of dissolution.
	YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION(505),
	// Message: Incorrect alliance name. Please try again.
	INCORRECT_ALLIANCE_NAME(506),
	// Message: Incorrect length for an alliance name.
	INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME(507),
	// Message: That alliance name already exists.
	THAT_ALLIANCE_NAME_ALREADY_EXISTS(508),
	// Message: Unable to proceed. Clan ally is currently registered as an enemy for the siege.
	UNABLE_TO_PROCEED(509),
	// Message: You have invited someone to your alliance.
	YOU_HAVE_INVITED_SOMEONE_TO_YOUR_ALLIANCE(510),
	// Message: You must first select a user to invite.
	YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE(511),
	// Message: Do you really wish to withdraw from the alliance?
	DO_YOU_REALLY_WISH_TO_WITHDRAW_FROM_THE_ALLIANCE(512),
	// Message: Enter the name of the clan you wish to expel.
	ENTER_THE_NAME_OF_THE_CLAN_YOU_WISH_TO_EXPEL(513),
	// Message: Do you really wish to dissolve the alliance? You cannot create a new alliance for 1 day.
	DO_YOU_REALLY_WISH_TO_DISSOLVE_THE_ALLIANCE_YOU_CANNOT_CREATE_A_NEW_ALLIANCE_FOR_1_DAY(514),
	// Message: Enter a file name for the alliance crest.
	ENTER_A_FILE_NAME_FOR_THE_ALLIANCE_CREST(515),
	// Message: $s1 has invited you to be their friend.
	S1_HAS_INVITED_YOU_TO_BE_THEIR_FRIEND(516),
	// Message: You have accepted the alliance.
	YOU_HAVE_ACCEPTED_THE_ALLIANCE(517),
	// Message: You have failed to invite a clan into the alliance.
	YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE(518),
	// Message: You have withdrawn from the alliance.
	YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE(519),
	// Message: You have failed to withdraw from the alliance.
	YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE(520),
	// Message: You have succeeded in expelling the clan.
	YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN(521),
	// Message: You have failed to expel a clan.
	YOU_HAVE_FAILED_TO_EXPEL_A_CLAN(522),
	// Message: The alliance has been dissolved.
	THE_ALLIANCE_HAS_BEEN_DISSOLVED(523),
	// Message: You have failed to dissolve the alliance.
	YOU_HAVE_FAILED_TO_DISSOLVE_THE_ALLIANCE(524),
	// Message: That person has been successfully added to your Friend List
	THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST(525),
	// Message: You have failed to add a friend to your friends list.
	YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST(526),
	// Message: $s1 leader, $s2, has requested an alliance.
	S1_LEADER_S2_HAS_REQUESTED_AN_ALLIANCE(527),
	// Message: Unable to find file at target location.
	UNABLE_TO_FIND_FILE_AT_TARGET_LOCATION(528),
	// Message: You may only register an 8 x 12 pixel, 256-color BMP.
	YOU_MAY_ONLY_REGISTER_AN_8_X_12_PIXEL_256COLOR_BMP(529),
	// Message: Your Spiritshot does not match the weapon's grade.
	YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPONS_GRADE(530),
	// Message: You do not have enough Spiritshot for that.
	YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT(531),
	// Message: You may not use Spiritshots.
	YOU_MAY_NOT_USE_SPIRITSHOTS(532),
	// Message: Your spiritshot has been enabled.
	YOUR_SPIRITSHOT_HAS_BEEN_ENABLED(533),
	// Message: Your spiritshot has been disabled.
	YOUR_SPIRITSHOT_HAS_BEEN_DISABLED(534),
	// Message: Enter a name for your pet.
	ENTER_A_NAME_FOR_YOUR_PET(535),
	// Message: How much adena do you wish to transfer to your Inventory?
	HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_INVENTORY(536),
	// Message: How much will you transfer?
	HOW_MUCH_WILL_YOU_TRANSFER(537),
	// Message: You cannot summon during a trade or while using a private store.
	YOUR_SP_HAS_DECREASED_BY_S1(538),
	// Message: Your Experience has decreased by $s1.
	YOUR_EXPERIENCE_HAS_DECREASED_BY_S1(539),
	// Message: Clan leaders may not be deleted. Dissolve the clan first and try again.
	CLAN_LEADERS_MAY_NOT_BE_DELETED(540),
	// Message: You may not delete a clan member. Withdraw from the clan first and try again.
	YOU_MAY_NOT_DELETE_A_CLAN_MEMBER(541),
	// Message: The NPC server is currently down. Pets and servitors cannot be summoned at this time.
	THE_NPC_SERVER_IS_CURRENTLY_DOWN(542),
	// Message: You already have a pet.
	YOU_ALREADY_HAVE_A_PET(543),
	// Message: Your pet cannot carry this item.
	YOUR_PET_CANNOT_CARRY_THIS_ITEM(544),
	// Message: Your pet cannot carry any more items. Remove some, then try again.
	YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS(545),
	// Message: Your pet cannot carry any more items.
	YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_(546),
	// Message: Summoning your pet…
	SUMMONING_YOUR_PET(547),
	// Message: Your pet's name can be up to 8 characters in length.
	YOUR_PETS_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH(548),
	// Message: To create an alliance, your clan must be Level 5 or higher.
	TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER(549),
	// Message: As you are currently schedule for clan dissolution, no alliance can be created.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED(550),
	// Message: As you are currently schedule for clan dissolution, your clan level cannot be increased.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED(551),
	// Message: As you are currently schedule for clan dissolution, you cannot register or delete a Clan Crest.
	AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST(552),
	// Message: The opposing clan has applied for dispersion.
	THE_OPPOSING_CLAN_HAS_APPLIED_FOR_DISPERSION(553),
	// Message: You cannot disperse the clans in your alliance.
	YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE(554),
	// Message: You cannot move - you are too encumbered.
	YOU_CANNOT_MOVE__YOU_ARE_TOO_ENCUMBERED(555),
	// Message: You cannot move in this state.
	YOU_CANNOT_MOVE_IN_THIS_STATE(556),
	// Message: As your pet is currently out, its summoning item cannot be destroyed.
	AS_YOUR_PET_IS_CURRENTLY_OUT_ITS_SUMMONING_ITEM_CANNOT_BE_DESTROYED(557),
	// Message: As your pet is currently summoned, you cannot discard the summoning item.
	AS_YOUR_PET_IS_CURRENTLY_SUMMONED_YOU_CANNOT_DISCARD_THE_SUMMONING_ITEM(558),
	// Message: You have purchased $s2 from $c1.
	YOU_HAVE_PURCHASED_S2_FROM_C1(559),
	// Message: You have purchased +$s2 $s3 from $c1.
	YOU_HAVE_PURCHASED_S2_S3_FROM_C1(560),
	// Message: You have purchased $s3 $s2(s) from $c1.
	YOU_HAVE_PURCHASED_S3_S2S_FROM_C1(561),
	// Message: You may not crystallize this item. Your crystallization skill level is too low.
	YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM(562),
	// Message: Failed to disable attack target.
	FAILED_TO_DISABLE_ATTACK_TARGET(563),
	// Message: Failed to change attack target.
	FAILED_TO_CHANGE_ATTACK_TARGET(564),
	// Message: You don't have enough luck.
	YOU_DONT_HAVE_ENOUGH_LUCK(565),
	// Message: Your confusion spell failed.
	YOUR_CONFUSION_SPELL_FAILED(566),
	// Message: Your fear spell failed.
	YOUR_FEAR_SPELL_FAILED(567),
	// Message: Cubic Summoning failed.
	CUBIC_SUMMONING_FAILED(568),
	// Message: Caution -- this item's price greatly differs from non-player run shops. Do you wish to continue?
	CAUTION__THIS_ITEMS_PRICE_GREATLY_DIFFERS_FROM_NONPLAYER_RUN_SHOPS(569),
	// Message: How many $s1(s) do you want to purchase?
	HOW_MANY__S1S_DO_YOU_WANT_TO_PURCHASE(570),
	// Message: How many $s1(s) do you want to purchase?
	HOW_MANY__S1S_DO_YOU_WANT_TO_PURCHASE_(571),
	// Message: Do you accept $c1's party invitation? (Item Distribution: Finders Keepers.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_FINDERS_KEEPERS(572),
	// Message: Do you accept $c1's party invitation? (Item Distribution: Random.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_RANDOM(573),
	// Message: Pets and Servitors are not available at this time.
	PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME(574),
	// Message: How much adena do you wish to transfer to your pet?
	HOW_MUCH_ADENA_DO_YOU_WISH_TO_TRANSFER_TO_YOUR_PET(575),
	// Message: How much do you wish to transfer?
	HOW_MUCH_DO_YOU_WISH_TO_TRANSFER(576),
	// Message: Your Experience has decreased by $s1.
	YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE(577),
	// Message: You cannot summon during combat.
	YOU_CANNOT_SUMMON_DURING_COMBAT(578),
	// Message: A pet cannot be unsummoned during battle.
	A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE(579),
	// Message: You may not use multiple pets or servitors at the same time.
	YOU_MAY_NOT_USE_MULTIPLE_PETS_OR_SERVITORS_AT_THE_SAME_TIME(580),
	// Message: There is a space in the name.
	THERE_IS_A_SPACE_IN_THE_NAME(581),
	// Message: Inappropriate character name.
	INAPPROPRIATE_CHARACTER_NAME(582),
	// Message: Name includes forbidden words.
	NAME_INCLUDES_FORBIDDEN_WORDS(583),
	// Message: This is already in use by another pet.
	THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET(584),
	// Message: Select the purchasing price
	SELECT_THE_PURCHASING_PRICE(585),
	// Message: Pet items cannot be registered as shortcuts.
	PET_ITEMS_CANNOT_BE_REGISTERED_AS_SHORTCUTS(586),
	// Message: Irregular system speed.
	IRREGULAR_SYSTEM_SPEED(587),
	// Message: Your pet's inventory is full.
	YOUR_PETS_INVENTORY_IS_FULL(588),
	// Message: Dead pets cannot be returned to their summoning item.
	DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM(589),
	// Message: Your pet is dead and any attempt you make to give it something goes unrecognized.
	YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED(590),
	// Message: An invalid character is included in the pet's name.
	AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PETS_NAME(591),
	// Message: Do you wish to dismiss your pet? Dismissing your pet will cause the pet necklace to disappear.
	DO_YOU_WISH_TO_DISMISS_YOUR_PET_DISMISSING_YOUR_PET_WILL_CAUSE_THE_PET_NECKLACE_TO_DISAPPEAR(592),
	// Message: Starving, grumpy and fed up, your pet has lel2f.
	STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT(593),
	// Message: You may not restore a hungry pet.
	YOU_MAY_NOT_RESTORE_A_HUNGRY_PET(594),
	// Message: You may not equip a pet item.
	YOU_MAY_NOT_EQUIP_A_PET_ITEM(600),
	// Message:
	THERE_ARE_S1_PETITIONS_PENDING(601),
	// Message:
	THE_PETITION_SYSTEM_IS_CURRENTLY_UNAVAILABLE_PLEASE_TRY_AGAIN_LATER(602),
	// Message: That item cannot be discarded or exchanged.
	THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED(603),
	// Message: You may not call forth a pet or summoned creature from this location.
	YOU_MAY_NOT_CALL_FORTH_A_PET_OR_SUMMONED_CREATURE_FROM_THIS_LOCATION(604),
	// Message: You can only enter up 128 names in your friends list.
	YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST(605),
	// Message: The Friend's List of the person you are trying to add is full, so registration is not possible.
	THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE(606),
	// Message: You do not have any further skills to learn. Come back when you have reached Level $s1.
	YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN__COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1(607),
	// Message: $c1 has obtained $s3 $s2 by using sweeper.
	C1_HAS_OBTAINED_S3_S2_BY_USING_SWEEPER(608),
	// Message: $c1 has obtained $s2 by using sweeper.
	C1_HAS_OBTAINED_S2_BY_USING_SWEEPER(609),
	// Message: Your skill has been canceled due to lack of HP.
	YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP(610),
	// Message: You have succeeded in Confusing the enemy.
	YOU_HAVE_SUCCEEDED_IN_CONFUSING_THE_ENEMY(611),
	// Message: The Spoil condition has been activated.
	THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED(612),
	// Message: ======<Ignore List>======
	IGNORE_LIST(613),
	// Message: $c1 : $c2
	C1__C2(614),
	// Message: You have failed to register the user to your Ignore List.
	YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST(615),
	// Message: You have failed to delete the character.
	YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_(616),
	// Message: $s1 has been added to your Ignore List.
	S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST(617),
	// Message: $s1 has been removed from your Ignore List.
	S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST(618),
	// Message: $s1 has placed you on his/her Ignore List.
	S1_HAS_PLACED_YOU_ON_HISHER_IGNORE_LIST(619),
	// Message: $s1 has placed you on his/her Ignore List.
	S1_HAS_PLACED_YOU_ON_HISHER_IGNORE_LIST_(620),
	// Message: Game connection attempted through a restricted IP.
	GAME_CONNECTION_ATTEMPTED_THROUGH_A_RESTRICTED_IP(621),
	// Message: You may not make a declaration of war during an alliance battle.
	YOU_MAY_NOT_MAKE_A_DECLARATION_OF_WAR_DURING_AN_ALLIANCE_BATTLE(622),
	// Message: Your opponent has exceeded the number of simultaneous alliance battles allowed.
	YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED(623),
	// Message: Clan leader $s1 is not currently connected to the game server.
	CLAN_LEADER_S1_IS_NOT_CURRENTLY_CONNECTED_TO_THE_GAME_SERVER(624),
	// Message: Your request for an Alliance Battle truce has been denied.
	YOUR_REQUEST_FOR_AN_ALLIANCE_BATTLE_TRUCE_HAS_BEEN_DENIED(625),
	// Message: The $s1 clan did not respond: war proclamation has been refused.
	THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED_(626),
	// Message: Clan battle has been refused because you did not respond to $s1's war proclamation.
	CLAN_BATTLE_HAS_BEEN_REFUSED_BECAUSE_YOU_DID_NOT_RESPOND_TO_S1S_WAR_PROCLAMATION(627),
	// Message: You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.
	YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN(628),
	// Message: Your opponent has exceeded the number of simultaneous alliance battles allowed.
	YOUR_OPPONENT_HAS_EXCEEDED_THE_NUMBER_OF_SIMULTANEOUS_ALLIANCE_BATTLES_ALLOWED_(629),
	// Message: War with clan $s1 has begun.
	WAR_WITH_CLAN_S1_HAS_BEGUN(630),
	// Message: War with clan $s1 is over.
	WAR_WITH_CLAN_S1_IS_OVER(631),
	// Message: You have won the war over the $s1 clan!
	YOU_HAVE_WON_THE_WAR_OVER_THE_S1_CLAN_(632),
	// Message: You have surrendered to the $s1 clan.
	YOU_HAVE_SURRENDERED_TO_THE_S1_CLAN_(633),
	// Message: Your alliance leader has been slain. You have been defeated by the $s1 clan.
	YOUR_ALLIANCE_LEADER_HAS_BEEN_SLAIN(634),
	// Message: The time limit for the clan war has been exceeded. War with the $s1 clan is over.
	THE_TIME_LIMIT_FOR_THE_CLAN_WAR_HAS_BEEN_EXCEEDED(635),
	// Message: You are not involved in a clan war.
	YOU_ARE_NOT_INVOLVED_IN_A_CLAN_WAR_(636),
	// Message: A clan ally has registered itself to the opponent.
	A_CLAN_ALLY_HAS_REGISTERED_ITSELF_TO_THE_OPPONENT(637),
	// Message: You have already requested a Castle Siege.
	YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE(638),
	// Message: You are already registered to the attacker side and must cancel your registration before submitting your request.
	YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST(642),
	// Message: You have already registered to the defender side and must cancel your registration before submitting your request.
	YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST(643),
	// Message: You are not yet registered for the castle siege.
	YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE(644),
	// Message: Only clans of level 5 or higher may register for a castle siege.
	ONLY_CLANS_OF_LEVEL_5_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE(645),
	// Message: You do not have the authority to modify the castle defender list.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_CASTLE_DEFENDER_LIST(646),
	// Message: You do not have the authority to modify the siege time.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME(647),
	// Message: No more registrations may be accepted for the attacker side.
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE(648),
	// Message: No more registrations may be accepted for the defender side.
	NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE(649),
	// Message:
	YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION(650),
	// Message: You do not have the authority to position mercenaries.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES(653),
	// Message: You do not have the authority to cancel mercenary positioning.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING(654),
	// Message: Mercenaries cannot be positioned here.
	MERCENARIES_CANNOT_BE_POSITIONED_HERE(655),
	// Message: This mercenary cannot be positioned anymore.
	THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE(656),
	// Message: Positioning cannot be done here because the distance between mercenaries is too short.
	POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT(657),
	// Message: This is not a mercenary of a castle that you own and so you cannot cancel its positioning.
	THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_YOU_CANNOT_CANCEL_ITS_POSITIONING(658),
	// Message: This is not the time for siege registration and so registrations cannot be accepted or rejected.
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATIONS_CANNOT_BE_ACCEPTED_OR_REJECTED(659),
	// Message: This is not the time for siege registration and so registration and cancellation cannot be done.
	THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE(660),
	// Message: $s1 adena disappeared.
	S1_ADENA_DISAPPEARED(672),
	// Message: Only a clan leader whose clan is of level 2 or higher is allowed to participate in a clan hall auction.
	ONLY_A_CLAN_LEADER_WHOSE_CLAN_IS_OF_LEVEL_2_OR_HIGHER_IS_ALLOWED_TO_PARTICIPATE_IN_A_CLAN_HALL_AUCTION(673),
	// Message: It has not yet been seven days since canceling an auction.
	IT_HAS_NOT_YET_BEEN_SEVEN_DAYS_SINCE_CANCELING_AN_AUCTION(674),
	// Message: There are no clan halls up for auction.
	THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION(675),
	// Message: Since you have already submitted a bid, you are not allowed to participate in another auction at this time.
	SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME(676),
	// Message: Your bid price must be higher than the minimum price currently being bid.
	YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID(677),
	// Message:
	YOU_HAVE_SUBMITTED_A_BID_IN_THE_AUCTION_OF_S1(678),
	// Message: You have submitted a bid for the auction of $s1.
	YOU_HAVE_SUBMITTED_A_BID_FOR_THE_AUCTION_OF_S1(678),
	// Message: You have canceled your bid.
	YOU_HAVE_CANCELED_YOUR_BID(679),
	// Message: You do not meet the requirements to participate in an auction.
	YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION(680),
	// Message: The clan does not own a clan hall.
	THE_CLAN_DOES_NOT_OWN_A_CLAN_HALL(681),
	// Message: You are moving to another village. Do you want to continue?
	YOU_ARE_MOVING_TO_ANOTHER_VILLAGE(682),
	// Message: There are no priority rights on a sweeper.
	THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER(683),
	// Message: You cannot position mercenaries during a siege.
	YOU_CANNOT_POSITION_MERCENARIES_DURING_A_SIEGE(684),
	// Message: You cannot apply for clan war with a clan that belongs to the same alliance.
	YOU_CANNOT_APPLY_FOR_CLAN_WAR_WITH_A_CLAN_THAT_BELONGS_TO_THE_SAME_ALLIANCE(685),
	// Message: You have received $s1 damage from the fire of magic.
	YOU_HAVE_RECEIVED_S1_DAMAGE_FROM_THE_FIRE_OF_MAGIC(686),
	// Message: You cannot move while frozen. Please wait.
	YOU_CANNOT_MOVE_WHILE_FROZEN(687),
	// Message: Castle-owning clans are automatically registered on the defending side.
	CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE(688),
	// Message: A clan that owns a castle cannot participate in another siege.
	A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE(689),
	// Message: You cannot register as an attacker because you are in an alliance with the castle-owning clan.
	YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN(690),
	// Message: $s1 clan is already a member of $s2 alliance.
	S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE(691),
	// Message: The other party is frozen. Please wait a moment.
	THE_OTHER_PARTY_IS_FROZEN(692),
	// Message: No packages have arrived.
	NO_PACKAGES_HAVE_ARRIVED(694),
	// Message: You do not have enough required items.
	YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS(701),
	// Message:
	THERE_ARE_NOT_ANY_GMS_THAT_ARE_PROVIDING_CUSTOMER_SERVICE_CURRENTLY(702),
	// Message:
	_GM_LIST_(703),
	// Message:
	GM_S1(704),
	// Message: You cannot exclude yourself.
	YOU_CANNOT_EXCLUDE_YOURSELF(705),
	// Message: You can only enter up to 128 names in your block list.
	YOU_CAN_ONLY_ENTER_UP_TO_128_NAMES_IN_YOUR_BLOCK_LIST(706),
	// Message: You cannot teleport to a village that is in a siege.
	YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE(707),
	// Message: You do not have the right to use the castle warehouse.
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CASTLE_WAREHOUSE(708),
	// Message:
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE(709),
	// Message:
	ONLY_CLANS_OF_CLAN_LEVEL_1_OR_HIGHER_CAN_USE_A_CLAN_WAREHOUSE(710), THE_SIEGE_OF_S1_HAS_STARTED(711), // $s1: осада началась.
	THE_SIEGE_OF_S1_HAS_FINISHED(712), // $s1: осада завершена.
	// Message: If a base camp does not exist, resurrection is not possible.
	IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE(716),
	// Message: The guardian tower has been destroyed and resurrection is not possible.
	THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE(717),
	// Message: The castle gates cannot be opened and closed during a siege.
	THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE(718),
	// Message: You failed at mixing the item.
	YOU_FAILED_AT_MIXING_THE_ITEM(719),
	// Message: The purchase price is higher than the amount of money that you have and so you cannot open a personal store.
	THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE(720),
	// Message: You cannot create an alliance while participating in a siege.
	YOU_CANNOT_CREATE_AN_ALLIANCE_WHILE_PARTICIPATING_IN_A_SIEGE(721),
	// Message: You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.
	YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE(722),
	// Message: The opposing clan is participating in a siege battle.
	THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE(723),
	// Message: You cannot leave while participating in a siege battle.
	YOU_CANNOT_LEAVE_WHILE_PARTICIPATING_IN_A_SIEGE_BATTLE(724),
	// Message: You cannot banish a clan from an alliance while the clan is participating in a siege.
	YOU_CANNOT_BANISH_A_CLAN_FROM_AN_ALLIANCE_WHILE_THE_CLAN_IS_PARTICIPATING_IN_A_SIEGE(725),
	// Message: The frozen condition has started. Please wait a moment.
	THE_FROZEN_CONDITION_HAS_STARTED(726),
	// Message: The frozen condition was removed.
	THE_FROZEN_CONDITION_WAS_REMOVED(727),
	// Message: You cannot apply for dissolution again within seven days after a previous application for dissolution.
	YOU_CANNOT_APPLY_FOR_DISSOLUTION_AGAIN_WITHIN_SEVEN_DAYS_AFTER_A_PREVIOUS_APPLICATION_FOR_DISSOLUTION(728),
	// Message: That item cannot be discarded.
	THAT_ITEM_CANNOT_BE_DISCARDED(729),
	// Message: You have submitted $s1 petition(s). \n - You may submit $s2 more petition(s) today.
	YOU_HAVE_SUBMITTED_S1_PETITIONS(730),
	// Message: A petition has been received by the GM on behalf of $s1. The petition code is $s2.
	A_PETITION_HAS_BEEN_RECEIVED_BY_THE_GM_ON_BEHALF_OF_S1(731),
	// Message: $c1 has received a request for a consultation with the GM.
	C1_HAS_RECEIVED_A_REQUEST_FOR_A_CONSULTATION_WITH_THE_GM(732),
	// Message: We have received $s1 petitions from you today and that is the maximum that you can submit in one day. You cannot submit any more petitions.
	WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY(733),
	// Message: You have failed at submitting a petition on behalf of someone else. $c1 already submitted a petition.
	YOU_HAVE_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_SOMEONE_ELSE(734),
	// Message: You have failed at submitting a petition on behalf of $c1. The error number is $s2.
	YOU_HAVE_FAILED_AT_SUBMITTING_A_PETITION_ON_BEHALF_OF_C1(735),
	// Message: The petition was canceled. You may submit $s1 more petition(s) today.
	THE_PETITION_WAS_CANCELED(736),
	// Message: You have cancelled submitting a petition on behalf of $s1.
	YOU_HAVE_CANCELLED_SUBMITTING_A_PETITION_ON_BEHALF_OF_S1(737),
	// Message: You have not submitted a petition.
	YOU_HAVE_NOT_SUBMITTED_A_PETITION(738),
	// Message: You have failed at cancelling a petition on behalf of $c1. The error code is $s2.
	YOU_HAVE_FAILED_AT_CANCELLING_A_PETITION_ON_BEHALF_OF_C1(739),
	// Message: $c1 participated in a petition chat at the request of the GM.
	C1_PARTICIPATED_IN_A_PETITION_CHAT_AT_THE_REQUEST_OF_THE_GM(740),
	// Message: You have failed at adding $c1 to the petition chat. Petition has already been submitted.
	YOU_HAVE_FAILED_AT_ADDING_C1_TO_THE_PETITION_CHAT(741),
	// Message: You have failed at adding $c1 to the petition chat. The error code is $s2.
	YOU_HAVE_FAILED_AT_ADDING_C1_TO_THE_PETITION_CHAT_(742),
	// Message: $c1 left the petition chat.
	C1_LEFT_THE_PETITION_CHAT(743),
	// Message: You failed at removing $s1 from the petition chat. The error code is $s2.
	YOU_FAILED_AT_REMOVING_S1_FROM_THE_PETITION_CHAT(744),
	// Message: You are currently not in a petition chat.
	YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT(745),
	// Message: It is not currently a petition.
	IT_IS_NOT_CURRENTLY_A_PETITION(746),
	// Message: If you need help, please visit the Support Center on the PlayNC website (http://us.ncsol2f.com/support/).
	IF_YOU_NEED_HELP_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_PLAYNC_WEBSITE_HTTPUS(747),
	// Message: The distance is too far and so the casting has been stopped.
	THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_STOPPED(748),
	// Message: The effect of $s1 has been removed.
	THE_EFFECT_OF_S1_HAS_BEEN_REMOVED(749),
	// Message: There are no other skills to learn.
	THERE_ARE_NO_OTHER_SKILLS_TO_LEARN(750),
	// Message: You cannot position mercenaries here.
	YOU_CANNOT_POSITION_MERCENARIES_HERE(753),
	// Message: There are $s1 hours and $s2 minutes left in this week's usage time.
	THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THIS_WEEKS_USAGE_TIME(754),
	// Message: There are $s1 minutes left in this week's usage time.
	THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEKS_USAGE_TIME(755),
	// Message: This week's usage time has finished.
	THIS_WEEKS_USAGE_TIME_HAS_FINISHED(756),
	// Message: There are $s1 hours and $s2 minutes left in the fixed use time.
	THERE_ARE_S1_HOURS_AND_S2_MINUTES_LEFT_IN_THE_FIXED_USE_TIME(757),
	// Message: There are $s1 hour(s) $s2 minute(s) left in this week's play time.
	THERE_ARE_S1_HOURS_S2_MINUTES_LEFT_IN_THIS_WEEKS_PLAY_TIME(758),
	// Message: There are $s1 minutes left in this week's play time.
	THERE_ARE_S1_MINUTES_LEFT_IN_THIS_WEEKS_PLAY_TIME(759),
	// Message: $c1 cannot join the clan because one day has not yet passed since they left another clan.
	C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN(760),
	// Message: $s1 clan cannot join the alliance because one day has not yet passed since they left another alliance.
	S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE(761),
	// Message: $c1 rolled $s2 and $s3's eye came out.
	C1_ROLLED_S2_AND_S3S_EYE_CAME_OUT(762),
	// Message: You failed at sending the package because you are too far from the warehouse.
	YOU_FAILED_AT_SENDING_THE_PACKAGE_BECAUSE_YOU_ARE_TOO_FAR_FROM_THE_WAREHOUSE(763),
	// Message: You have been playing for an extended period of time. Please consider taking a break.
	YOU_HAVE_BEEN_PLAYING_FOR_AN_EXTENDED_PERIOD_OF_TIME_S1(764),
	// Message: GameGuard is already running. Please try running it again after rebooting.
	GAMEGUARD_IS_ALREADY_RUNNING(765),
	// Message: There is a GameGuard initialization error. Please try running it again after rebooting.
	THERE_IS_A_GAMEGUARD_INITIALIZATION_ERROR(766),
	// Message: The GameGuard file is damaged. Please reinstall GameGuard.
	THE_GAMEGUARD_FILE_IS_DAMAGED(767),
	// Message: A Windows system file is damaged. Please reinstall Internet Explorer.
	A_WINDOWS_SYSTEM_FILE_IS_DAMAGED(768),
	// Message: A hacking tool has been discovered. Please try playing again after closing unnecessary programs.
	A_HACKING_TOOL_HAS_BEEN_DISCOVERED(769),
	// Message: The GameGuard update was canceled. Please check your network connection status or firewall.
	THE_GAMEGUARD_UPDATE_WAS_CANCELED(770),
	// Message: The GameGuard update was canceled. Please try running it again after doing a virus scan or changing the settings in your PC management program.
	THE_GAMEGUARD_UPDATE_WAS_CANCELED_(771),
	// Message: There was a problem when running GameGuard.
	THERE_WAS_A_PROBLEM_WHEN_RUNNING_GAMEGUARD(772),
	// Message: The game or GameGuard files are damaged.
	THE_GAME_OR_GAMEGUARD_FILES_ARE_DAMAGED(773),
	// Message: Play time is no longer accumulating.
	PLAY_TIME_IS_NO_LONGER_ACCUMULATING(774),
	// Message: From here on, play time will be expended.
	FROM_HERE_ON_PLAY_TIME_WILL_BE_EXPENDED(775),
	// Message: The clan hall which was put up for auction has been awarded to $s1 clan.
	THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN(776),
	// Message: The clan hall which had been put up for auction was not sold and therefore has been re-listed.
	THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED(777),
	// Message: You may not log out from this location.
	YOU_MAY_NOT_LOG_OUT_FROM_THIS_LOCATION(778),
	// Message: You may not restart in this location.
	YOU_MAY_NOT_RESTART_IN_THIS_LOCATION(779),
	// Message: Observation is only possible during a siege.
	OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE(780),
	// Message: Observers cannot participate.
	OBSERVERS_CANNOT_PARTICIPATE(781),
	// Message: You may not observe a siege with a pet or servitor summoned.
	YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_PET_OR_SERVITOR_SUMMONED(782),
	// Message: Lottery ticket sales have been temporarily suspended.
	LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED(783),
	// Message: Tickets for the current lottery are no longer available.
	TICKETS_FOR_THE_CURRENT_LOTTERY_ARE_NO_LONGER_AVAILABLE(784),
	// Message: The results of lottery number $s1 have not yet been published.
	THE_RESULTS_OF_LOTTERY_NUMBER_S1_HAVE_NOT_YET_BEEN_PUBLISHED(785),
	// Message: Incorrect syntax.
	INCORRECT_SYNTAX(786),
	// Message: The tryouts are finished.
	THE_TRYOUTS_ARE_FINISHED(787),
	// Message: The finals are finished.
	THE_FINALS_ARE_FINISHED(788),
	// Message: The tryouts have begun.
	THE_TRYOUTS_HAVE_BEGUN(789),
	// Message: The finals have begun.
	THE_FINALS_HAVE_BEGUN(790),
	// Message: The final match is about to begin. Line up!
	THE_FINAL_MATCH_IS_ABOUT_TO_BEGIN(791),
	// Message: You are not authorized to do that.
	YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT(794),
	// Message: Only clan leaders are authorized to set rights.
	ONLY_CLAN_LEADERS_ARE_AUTHORIZED_TO_SET_RIGHTS(795),
	// Message: Your remaining observation time is $s1 minutes.
	YOUR_REMAINING_OBSERVATION_TIME_IS_S1_MINUTES(796),
	// Message: You may create up to 48 macros.
	YOU_MAY_CREATE_UP_TO_48_MACROS(797),
	// Message: Item registration is irreversible. Do you wish to continue?
	ITEM_REGISTRATION_IS_IRREVERSIBLE(798),
	// Message: The observation time has expired.
	THE_OBSERVATION_TIME_HAS_EXPIRED(799),
	// Message: You are too late. The registration period is over.
	YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER(800),
	// Message: The tryouts are about to begin. Line up!
	THE_TRYOUTS_ARE_ABOUT_TO_BEGIN(815),
	// Message: Tickets are now available for Monster Race $s1!
	TICKETS_ARE_NOW_AVAILABLE_FOR_MONSTER_RACE_S1(816),
	// Message: Now selling tickets for Monster Race $s1!
	NOW_SELLING_TICKETS_FOR_MONSTER_RACE_S1(817),
	// Message: Ticket sales for the Monster Race will end in $s1 minute(s).
	TICKET_SALES_FOR_THE_MONSTER_RACE_WILL_END_IN_S1_MINUTES(818),
	// Message: Tickets sales are closed for Monster Race $s1. Odds are posted.
	TICKETS_SALES_ARE_CLOSED_FOR_MONSTER_RACE_S1(819),
	// Message: Monster Race $s2 will begin in $s1 minute(s)!
	MONSTER_RACE_S2_WILL_BEGIN_IN_S1_MINUTES(820),
	// Message: Monster Race $s1 will begin in 30 seconds!
	MONSTER_RACE_S1_WILL_BEGIN_IN_30_SECONDS(821),
	// Message: Monster Race $s1 is about to begin! Countdown in five seconds!
	MONSTER_RACE_S1_IS_ABOUT_TO_BEGIN_COUNTDOWN_IN_FIVE_SECONDS(822),
	// Message: The race will begin in $s1 second(s)!
	THE_RACE_WILL_BEGIN_IN_S1_SECONDS(823),
	// Message: They're off!
	THEYRE_OFF(824),
	// Message: Monster Race $s1 is finished!
	MONSTER_RACE_S1_IS_FINISHED(825),
	// Message: First prize goes to the player in lane $s1. Second prize goes to the player in lane $s2.
	FIRST_PRIZE_GOES_TO_THE_PLAYER_IN_LANE_S1(826),
	// Message: You may not impose a block on a GM.
	YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM(827),
	// Message: Are you sure you wish to delete the $s1 macro?
	ARE_YOU_SURE_YOU_WISH_TO_DELETE_THE_S1_MACRO(828),
	// Message: You cannot recommend yourself.
	YOU_CANNOT_RECOMMEND_YOURSELF(829),
	// Message: You have recommended $c1. You have $s2 recommendations lel2f.
	YOU_HAVE_RECOMMENDED_C1(830),
	// Message: You have been recommended by $c1.
	YOU_HAVE_BEEN_RECOMMENDED_BY_C1(831),
	// Message: That character has already been recommended.
	THAT_CHARACTER_HAS_ALREADY_BEEN_RECOMMENDED(832),
	// Message: You are not authorized to make further recommendations at this time. You will receive more recommendation credits each day at 1 p.m.
	YOU_ARE_NOT_AUTHORIZED_TO_MAKE_FURTHER_RECOMMENDATIONS_AT_THIS_TIME(833),
	// Message: $c1 has rolled a $s2.
	C1_HAS_ROLLED_A_S2(834),
	// Message: You may not throw the dice at this time. Try again later.
	YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME(835),
	// Message: You have exceeded your inventory volume limit and cannot take this item.
	YOU_HAVE_EXCEEDED_YOUR_INVENTORY_VOLUME_LIMIT_AND_CANNOT_TAKE_THIS_ITEM(836),
	// Message: Macro descriptions may contain up to 32 characters.
	MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS(837),
	// Message: Enter the name of the macro.
	ENTER_THE_NAME_OF_THE_MACRO(838),
	// Message: That name is already assigned to another macro.
	THAT_NAME_IS_ALREADY_ASSIGNED_TO_ANOTHER_MACRO(839),
	// Message: That recipe is already registered.
	THAT_RECIPE_IS_ALREADY_REGISTERED(840),
	// Message: No further recipes may be registered.
	NO_FURTHER_RECIPES_MAY_BE_REGISTERED(841),
	// Message: You are not authorized to register a recipe.
	YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE(842),
	// Message: The siege of $s1 is finished.
	THE_SIEGE_OF_S1_IS_FINISHED(843),
	// Message: The siege to conquer $s1 has begun.
	THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN(844),
	// Message: The deadline to register for the siege of $s1 has passed.
	THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED(845),
	// Message: The siege of $s1 has been canceled due to lack of interest.
	THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST(846),
	// Message: A clan that owns a clan hall may not participate in a clan hall siege.
	A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE(847),
	// Message: $s1 has been deleted.
	S1_HAS_BEEN_DELETED(848),
	// Message: $s1 cannot be found.
	S1_CANNOT_BE_FOUND(849),
	// Message: $s1 already exists.
	S1_ALREADY_EXISTS_(850),
	// Message: $s1 has been added.
	S1_HAS_BEEN_ADDED(851),
	// Message: The recipe is incorrect.
	THE_RECIPE_IS_INCORRECT(852),
	// Message: You may not alter your recipe book while engaged in manufacturing.
	YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING(853),
	// Message: You are missing $s2 $s1 required to create that.
	YOU_ARE_MISSING_S2_S1_REQUIRED_TO_CREATE_THAT(854),
	// Message: $s1 clan has defeated $s2.
	S1_CLAN_HAS_DEFEATED_S2(855),
	// Message: The siege of $s1 has ended in a draw.
	THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW(856),
	// Message: $s1 clan has won in the preliminary match of $s2.
	S1_CLAN_HAS_WON_IN_THE_PRELIMINARY_MATCH_OF_S2(857),
	// Message: The preliminary match of $s1 has ended in a draw.
	THE_PRELIMINARY_MATCH_OF_S1_HAS_ENDED_IN_A_DRAW(858),
	// Message: Please register a recipe.
	PLEASE_REGISTER_A_RECIPE(859),
	// Message: You may not build your headquarters in close proximity to another headquarters.
	YOU_MAY_NOT_BUILD_YOUR_HEADQUARTERS_IN_CLOSE_PROXIMITY_TO_ANOTHER_HEADQUARTERS(860),
	// Message: You have exceeded the maximum number of memos.
	YOU_HAVE_EXCEEDED_THE_MAXIMUM_NUMBER_OF_MEMOS(861),
	// Message: Odds are not posted until ticket sales have closed.
	ODDS_ARE_NOT_POSTED_UNTIL_TICKET_SALES_HAVE_CLOSED(862),
	// Message: You feel the energy of fire.
	YOU_FEEL_THE_ENERGY_OF_FIRE(863),
	// Message: You feel the energy of water.
	YOU_FEEL_THE_ENERGY_OF_WATER(864),
	// Message: You feel the energy of wind.
	YOU_FEEL_THE_ENERGY_OF_WIND(865),
	// Message: You may no longer gather energy.
	YOU_MAY_NO_LONGER_GATHER_ENERGY(866),
	// Message: The energy is depleted.
	THE_ENERGY_IS_DEPLETED(867),
	// Message: The energy of fire has been delivered.
	THE_ENERGY_OF_FIRE_HAS_BEEN_DELIVERED(868),
	// Message: The energy of water has been delivered.
	THE_ENERGY_OF_WATER_HAS_BEEN_DELIVERED(869),
	// Message: The energy of wind has been delivered.
	THE_ENERGY_OF_WIND_HAS_BEEN_DELIVERED(870),
	// Message: The seed has been sown.
	THE_SEED_HAS_BEEN_SOWN(871),
	// Message: This seed may not be sown here.
	THIS_SEED_MAY_NOT_BE_SOWN_HERE(872),
	// Message: The symbol has been added.
	THE_SYMBOL_HAS_BEEN_ADDED(877),
	// Message: The symbol has been deleted.
	THE_SYMBOL_HAS_BEEN_DELETED(878),
	// Message: The manor system is currently under maintenance.
	THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE(879),
	// Message: The transaction is complete.
	THE_TRANSACTION_IS_COMPLETE(880),
	// Message: There is a discrepancy on the invoice.
	THERE_IS_A_DISCREPANCY_ON_THE_INVOICE(881),
	// Message: The seed quantity is incorrect.
	THE_SEED_QUANTITY_IS_INCORRECT(882),
	// Message: The seed information is incorrect.
	THE_SEED_INFORMATION_IS_INCORRECT(883),
	// Message:
	THE_MANOR_INFORMATION_HAS_BEEN_UPDATED(884),
	// Message: The number of crops is incorrect.
	THE_NUMBER_OF_CROPS_IS_INCORRECT(885),
	// Message: The crops are priced incorrectly.
	THE_CROPS_ARE_PRICED_INCORRECTLY(886),
	// Message: The type is incorrect.
	THE_TYPE_IS_INCORRECT(887),
	// Message: No crops can be purchased at this time.
	NO_CROPS_CAN_BE_PURCHASED_AT_THIS_TIME(888),
	// Message: The seed was successfully sown.
	THE_SEED_WAS_SUCCESSFULLY_SOWN(889),
	// Message: The seed was not sown.
	THE_SEED_WAS_NOT_SOWN(890),
	// Message: You are not authorized to harvest.
	YOU_ARE_NOT_AUTHORIZED_TO_HARVEST(891),
	// Message: The harvest has failed.
	THE_HARVEST_HAS_FAILED(892),
	// Message: The harvest failed because the seed was not sown.
	THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN(893),
	// Message: Up to $s1 recipes can be registered.
	UP_TO_S1_RECIPES_CAN_BE_REGISTERED(894),
	// Message: No recipes have been registered.
	NO_RECIPES_HAVE_BEEN_REGISTERED(895),
	// Message: Quest recipes can not be registered.
	QUEST_RECIPES_CAN_NOT_BE_REGISTERED(896),
	// Message: The fee to create the item is incorrect.
	THE_FEE_TO_CREATE_THE_ITEM_IS_INCORRECT(897),
	// Message: Only characters of level 10 or above are authorized to make recommendations.
	ONLY_CHARACTERS_OF_LEVEL_10_OR_ABOVE_ARE_AUTHORIZED_TO_MAKE_RECOMMENDATIONS(898),
	// Message: The symbol cannot be drawn.
	THE_SYMBOL_CANNOT_BE_DRAWN(899),
	// Message: No slot exists to draw the symbol.
	NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL(900),
	// Message: The symbol information cannot be found.
	THE_SYMBOL_INFORMATION_CANNOT_BE_FOUND(901),
	// Message: You don't possess the correct number of items.
	YOU_DONT_POSSESS_THE_CORRECT_NUMBER_OF_ITEMS(902),
	// Message: You may not submit a petition while frozen. Be patient.
	YOU_MAY_NOT_SUBMIT_A_PETITION_WHILE_FROZEN(903),
	// Message: Items cannot be discarded while in a private store.
	ITEMS_CANNOT_BE_DISCARDED_WHILE_IN_A_PRIVATE_STORE(904),
	// Message: The current score for the Humans is $s1.
	THE_CURRENT_SCORE_FOR_THE_HUMANS_IS_S1(905),
	// Message: The current score for the Elves is $s1.
	THE_CURRENT_SCORE_FOR_THE_ELVES_IS_S1(906),
	// Message: The current score for the Dark Elves is $s1.
	THE_CURRENT_SCORE_FOR_THE_DARK_ELVES_IS_S1(907),
	// Message: The current score for the Orcs is $s1.
	THE_CURRENT_SCORE_FOR_THE_ORCS_IS_S1(908),
	// Message: The current score for the Dwarves is $s1.
	THE_CURRENT_SCORE_FOR_THE_DWARVES_IS_S1(909),
	// Message: Current location : $s1, $s2, $s3 (Near Talking Island Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_TALKING_ISLAND_VILLAGE(910),
	// Message: Current location : $s1, $s2, $s3 (Near Gludin Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_GLUDIN_VILLAGE(911),
	// Message: Current location : $s1, $s2, $s3 (Near the Town of Gludio)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_GLUDIO(912),
	// Message: Current location : $s1, $s2, $s3 (Near the Neutral Zone)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_NEUTRAL_ZONE(913),
	// Message: Current location : $s1, $s2, $s3 (Near the Elven Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_ELVEN_VILLAGE(914),
	// Message: Current location : $s1, $s2, $s3 (Near the Dark Elf Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_DARK_ELF_VILLAGE(915),
	// Message: Current location : $s1, $s2, $s3 (Near the Town of Dion)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_DION(916),
	// Message: Current location : $s1, $s2, $s3 (Near the Floran Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_FLORAN_VILLAGE(917),
	// Message: Current location : $s1, $s2, $s3 (Near the Town of Giran)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_GIRAN(918),
	// Message: Current location : $s1, $s2, $s3 (Near Giran Harbor)
	CURRENT_LOCATION__S1_S2_S3_NEAR_GIRAN_HARBOR(919),
	// Message: Current location : $s1, $s2, $s3 (Near the Orc Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_ORC_VILLAGE(920),
	// Message: Current location : $s1, $s2, $s3 (Near the Dwarven Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_DWARVEN_VILLAGE(921),
	// Message: Current location : $s1, $s2, $s3 (Near the Town of Oren)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_TOWN_OF_OREN(922),
	// Message: Current location : $s1, $s2, $s3 (Near Hunters Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_HUNTERS_VILLAGE(923),
	// Message: Current location : $s1, $s2, $s3 (Near Aden Castle Town)
	CURRENT_LOCATION__S1_S2_S3_NEAR_ADEN_CASTLE_TOWN(924),
	// Message: Current location : $s1, $s2, $s3 (Near the Coliseum)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_COLISEUM(925),
	// Message: Current location : $s1, $s2, $s3 (Near Heine)
	CURRENT_LOCATION__S1_S2_S3_NEAR_HEINE(926),
	// Message: The current time is $s1:$s2.
	THE_CURRENT_TIME_IS_S1S2(927),
	// Message: The current time is $s1:$s2.
	THE_CURRENT_TIME_IS_S1S2_(928),
	// Message: No compensation was given for the farm products.
	NO_COMPENSATION_WAS_GIVEN_FOR_THE_FARM_PRODUCTS(929),
	// Message: Lottery tickets are not currently being sold.
	LOTTERY_TICKETS_ARE_NOT_CURRENTLY_BEING_SOLD(930),
	// Message: The winning lottery ticket numbers have not yet been announced.
	THE_WINNING_LOTTERY_TICKET_NUMBERS_HAVE_NOT_YET_BEEN_ANNOUNCED(931),
	// Message: You cannot chat while in observation mode.
	YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE(932),
	// Message: The seed pricing greatly differs from standard seed prices.
	THE_SEED_PRICING_GREATLY_DIFFERS_FROM_STANDARD_SEED_PRICES(933),
	// Message: It is a deleted recipe.
	IT_IS_A_DELETED_RECIPE(934),
	// Message: You do not have enough funds in the clan warehouse for the Manor to operate.
	YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE(935),
	// Message: Use $s1.
	USE_S1(936),
	// Message: Currently preparing for private workshop.
	CURRENTLY_PREPARING_FOR_PRIVATE_WORKSHOP(937),
	// Message: The community server is currently offline.
	THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE(938),
	// Message: You cannot exchange while blocking everything.
	YOU_CANNOT_EXCHANGE_WHILE_BLOCKING_EVERYTHING(939),
	// Message: $s1 is blocking everything.
	S1_IS_BLOCKING_EVERYTHING(940),
	// Message: Restart at Talking Island Village.
	RESTART_AT_TALKING_ISLAND_VILLAGE(941),
	// Message: Restart at Gludin Village.
	RESTART_AT_GLUDIN_VILLAGE(942),
	// Message: Restart at the Town of Gludin.
	RESTART_AT_THE_TOWN_OF_GLUDIN(943),
	// Message: Restart at the Neutral Zone.
	RESTART_AT_THE_NEUTRAL_ZONE(944),
	// Message: Restart at the Elven Village.
	RESTART_AT_THE_ELVEN_VILLAGE(945),
	// Message: Restart at the Dark Elf Village.
	RESTART_AT_THE_DARK_ELF_VILLAGE(946),
	// Message: Restart at the Town of Dion.
	RESTART_AT_THE_TOWN_OF_DION(947),
	// Message: Restart at Floran Village.
	RESTART_AT_FLORAN_VILLAGE(948),
	// Message: Restart at the Town of Giran.
	RESTART_AT_THE_TOWN_OF_GIRAN(949),
	// Message: Restart at Giran Harbor.
	RESTART_AT_GIRAN_HARBOR(950),
	// Message: Restart at the Orc Village.
	RESTART_AT_THE_ORC_VILLAGE(951),
	// Message: Restart at the Dwarven Village.
	RESTART_AT_THE_DWARVEN_VILLAGE(952),
	// Message: Restart at the Town of Oren.
	RESTART_AT_THE_TOWN_OF_OREN(953),
	// Message: Restart at Hunters Village.
	RESTART_AT_HUNTERS_VILLAGE(954),
	// Message: Restart at the Town of Aden.
	RESTART_AT_THE_TOWN_OF_ADEN(955),
	// Message: Restart at the Coliseum.
	RESTART_AT_THE_COLISEUM(956),
	// Message: Restart at Heine.
	RESTART_AT_HEINE(957),
	// Message: Items cannot be discarded or destroyed while operating a private store or workshop.
	ITEMS_CANNOT_BE_DISCARDED_OR_DESTROYED_WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP(958),
	// Message: $s1 (*$s2) manufactured successfully.
	S1_S2_MANUFACTURED_SUCCESSFULLY(959),
	// Message: You failed to manufacture $s1.
	YOU_FAILED_TO_MANUFACTURE_S1(960),
	// Message: You are now blocking everything.
	YOU_ARE_NOW_BLOCKING_EVERYTHING(961),
	// Message: You are no longer blocking everything.
	YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING(962),
	// Message: Please determine the manufacturing price.
	PLEASE_DETERMINE_THE_MANUFACTURING_PRICE(963),
	// Message: Chatting is prohibited for one minute.
	CHATTING_IS_PROHIBITED_FOR_ONE_MINUTE(964),
	// Message: The chatting prohibition has been removed.
	THE_CHATTING_PROHIBITION_HAS_BEEN_REMOVED(965),
	// Message: Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will increase even further.
	CHATTING_IS_CURRENTLY_PROHIBITED_(966),
	// Message: Do you accept $c1's party invitation? (Item Distribution: Random including spoil.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_RANDOM_INCLUDING_SPOIL(967),
	// Message: Do you accept $c1's party invitation? (Item Distribution: By Turn.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_BY_TURN(968),
	// Message: Do you accept $c1's party invitation? (Item Distribution: By Turn including spoil.)
	DO_YOU_ACCEPT_C1S_PARTY_INVITATION_ITEM_DISTRIBUTION_BY_TURN_INCLUDING_SPOIL(969),
	// Message: $s2's MP has been drained by $c1.
	S2S_MP_HAS_BEEN_DRAINED_BY_C1(970),
	// Message: Petitions cannot exceed 255 characters.
	PETITIONS_CANNOT_EXCEED_255_CHARACTERS(971),
	// Message: This pet cannot use this item.
	THIS_PET_CANNOT_USE_THIS_ITEM(972),
	// Message: Please input no more than the number you have.
	PLEASE_INPUT_NO_MORE_THAN_THE_NUMBER_YOU_HAVE(973),
	// Message: The soul crystal succeeded in absorbing a soul.
	THE_SOUL_CRYSTAL_SUCCEEDED_IN_ABSORBING_A_SOUL(974),
	// Message: The soul crystal was not able to absorb the soul.
	THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_THE_SOUL(975),
	// Message: The soul crystal broke because it was not able to endure the soul energy.
	THE_SOUL_CRYSTAL_BROKE_BECAUSE_IT_WAS_NOT_ABLE_TO_ENDURE_THE_SOUL_ENERGY(976),
	// Message: The soul crystal caused resonation and failed at absorbing a soul.
	THE_SOUL_CRYSTAL_CAUSED_RESONATION_AND_FAILED_AT_ABSORBING_A_SOUL(977),
	// Message: The soul crystal is refusing to absorb the soul.
	THE_SOUL_CRYSTAL_IS_REFUSING_TO_ABSORB_THE_SOUL(978),
	// Message: You have registered for a clan hall auction.
	YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION(1004),
	// Message: There is not enough adena in the clan hall warehouse.
	THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE(1005),
	// Message: Your bid has been successfully placed.
	YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED(1006),
	// Message: A hungry strider cannot be mounted or dismounted.
	A_HUNGRY_STRIDER_CANNOT_BE_MOUNTED_OR_DISMOUNTED(1008),
	// Message: A strider cannot be ridden when dead.
	A_STRIDER_CANNOT_BE_RIDDEN_WHEN_DEAD(1009),
	// Message: A dead strider cannot be ridden.
	A_DEAD_STRIDER_CANNOT_BE_RIDDEN(1010),
	// Message: A strider in battle cannot be ridden.
	A_STRIDER_IN_BATTLE_CANNOT_BE_RIDDEN(1011),
	// Message: A strider cannot be ridden while in battle.
	A_STRIDER_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE(1012),
	// Message: A strider can be ridden only when standing.
	A_STRIDER_CAN_BE_RIDDEN_ONLY_WHEN_STANDING(1013),
	// Message: Pet's critical hit!
	PETS_CRITICAL_HIT(1017),
	// Message: Summoned monster's critical hit!
	SUMMONED_MONSTERS_CRITICAL_HIT(1028),
	// Message: A summoned monster uses $s1.
	A_SUMMONED_MONSTER_USES_S1(1029),
	// Message: <Party Information>
	PARTY_INFORMATION(1030),
	// Message: Looting method: Finders keepers
	LOOTING_METHOD_FINDERS_KEEPERS(1031),
	// Message: Looting method: Random
	LOOTING_METHOD_RANDOM(1032),
	// Message: Looting method: Random including spoil
	LOOTING_METHOD_RANDOM_INCLUDING_SPOIL(1033),
	// Message: Looting method: By turn
	LOOTING_METHOD_BY_TURN(1034),
	// Message: Looting method: By turn including spoil
	LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL(1035),
	// Message: You have exceeded the quantity that can be inputted.
	YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED(1036),
	// Message:
	ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE(1039),
	// Message: Payment for your clan hall has not been made. Please make payment to your clan warehouse by $s1 tomorrow.
	PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW(1051),
	// Message: The clan hall fee is one week overdue; therefore the clan hall ownership has been revoked.
	THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED(1052),
	// Message: It is not possible to resurrect in battlefields where a siege war is taking place.
	IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE(1053),
	// Message: While operating a private store or workshop, you cannot discard, destroy, or trade an item.
	WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM(1065),
	// Message: $s1 HP has been restored.
	S1_HP_HAS_BEEN_RESTORED(1066),
	// Message: $s2 HP has been restored by $c1.
	S2_HP_HAS_BEEN_RESTORED_BY_C1(1067),
	// Message: $s1 MP has been restored.
	S1_MP_HAS_BEEN_RESTORED(1068),
	// Message: $s2 MP has been restored by $c1.
	S2_MP_HAS_BEEN_RESTORED_BY_C1(1069),
	// Message: The bid amount must be higher than the previous bid.
	THE_BID_AMOUNT_MUST_BE_HIGHER_THAN_THE_PREVIOUS_BID(1075),
	// Message: The game cannot be terminated at this time.
	THE_GAME_CANNOT_BE_TERMINATED_AT_THIS_TIME(1076),
	// Message: A GameGuard Execution error has occurred. Please send the *.erl file(s) located in the GameGuard folder to game@inca.co.kr.
	A_GAMEGUARD_EXECUTION_ERROR_HAS_OCCURRED(1077),
	// Message: When a user's keyboard input exceeds a certain cumulative score a chat ban will be applied. This is done to discourage spamming. Please avoid posting the same message
	// multiple times during a short period.
	WHEN_A_USERS_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED(1078),
	// Message:  The target is currently banned from chatting.
	THE_TARGET_IS_CURRENTLY_BANNED_FROM_CHATTING(1079),
	// Message: Being permanent, are you sure you wish to use the facelift potion - Type A?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_FACELIFT_POTION__TYPE_A(1080),
	// Message: Being permanent, are you sure you wish to use the hair dye potion - Type A?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_HAIR_DYE_POTION__TYPE_A(1081),
	// Message: Do you wish to use the hair style change potion – Type A? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_A_IT_IS_PERMANENT(1082),
	// Message: Facelift potion - Type A is being applied.
	FACELIFT_POTION__TYPE_A_IS_BEING_APPLIED(1083),
	// Message: Hair dye potion - Type A is being applied.
	HAIR_DYE_POTION__TYPE_A_IS_BEING_APPLIED(1084),
	// Message: The hair style change potion - Type A is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_A_IS_BEING_USED(1085),
	// Message: Your facial appearance has been changed.
	YOUR_FACIAL_APPEARANCE_HAS_BEEN_CHANGED(1086),
	// Message: Your hair color has been changed.
	YOUR_HAIR_COLOR_HAS_BEEN_CHANGED(1087),
	// Message: Your hair style has been changed.
	YOUR_HAIR_STYLE_HAS_BEEN_CHANGED(1088),
	// Message: $c1 has obtained a first anniversary commemorative item.
	C1_HAS_OBTAINED_A_FIRST_ANNIVERSARY_COMMEMORATIVE_ITEM(1089),
	// Message: Being permanent, are you sure you wish to use the facelift potion - Type B?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_FACELIFT_POTION__TYPE_B(1090),
	// Message: Being permanent, are you sure you wish to use the facelift potion - Type C?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_FACELIFT_POTION__TYPE_C(1091),
	// Message: Being permanent, are you sure you wish to use the hair dye potion - Type B?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_HAIR_DYE_POTION__TYPE_B(1092),
	// Message: Being permanent, are you sure you wish to use the hair dye potion - Type C?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_HAIR_DYE_POTION__TYPE_C(1093),
	// Message: Being permanent, are you sure you wish to use the hair dye potion - Type D?
	BEING_PERMANENT_ARE_YOU_SURE_YOU_WISH_TO_USE_THE_HAIR_DYE_POTION__TYPE_D(1094),
	// Message: Do you wish to use the hair style change potion – Type B? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_B_IT_IS_PERMANENT(1095),
	// Message: Do you wish to use the hair style change potion – Type C? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_C_IT_IS_PERMANENT(1096),
	// Message: Do you wish to use the hair style change potion – Type D? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_D_IT_IS_PERMANENT(1097),
	// Message: Do you wish to use the hair style change potion – Type E? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_E_IT_IS_PERMANENT(1098),
	// Message: Do you wish to use the hair style change potion – Type F? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_F_IT_IS_PERMANENT(1099),
	// Message: Do you wish to use the hair style change potion – Type G? It is permanent.
	DO_YOU_WISH_TO_USE_THE_HAIR_STYLE_CHANGE_POTION__TYPE_G_IT_IS_PERMANENT(1100),
	// Message: Facelift potion - Type B is being applied.
	FACELIFT_POTION__TYPE_B_IS_BEING_APPLIED(1101),
	// Message: Facelift potion - Type C is being applied.
	FACELIFT_POTION__TYPE_C_IS_BEING_APPLIED(1102),
	// Message: Hair dye potion - Type B is being applied.
	HAIR_DYE_POTION__TYPE_B_IS_BEING_APPLIED(1103),
	// Message: Hair dye potion - Type C is being applied.
	HAIR_DYE_POTION__TYPE_C_IS_BEING_APPLIED(1104),
	// Message: Hair dye potion - Type D is being applied.
	HAIR_DYE_POTION__TYPE_D_IS_BEING_APPLIED(1105),
	// Message: The hair style change potion - Type B is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_B_IS_BEING_USED(1106),
	// Message: The hair style change potion - Type C is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_C_IS_BEING_USED(1107),
	// Message: The hair style change potion - Type D is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_D_IS_BEING_USED(1108),
	// Message: The hair style change potion - Type E is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_E_IS_BEING_USED(1109),
	// Message: The hair style change potion - Type F is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_F_IS_BEING_USED(1110),
	// Message: The hair style change potion - Type G is being used.
	THE_HAIR_STYLE_CHANGE_POTION__TYPE_G_IS_BEING_USED(1111),
	// Message: The prize amount for the winner of Lottery #$s1 is $s2 adena. We have $s3 first prize winners.
	THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY_S1_IS_S2_ADENA(1112),
	// Message: The prize amount for Lucky Lottery #$s1 is $s2 adena. There was no first prize winner in this drawing, therefore the jackpot will be added to the next drawing.
	THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY_S1_IS_S2_ADENA(1113),
	// Message: Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.
	YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLANS_DISSOLUTION(1114),
	// Message: Individuals may not surrender during combat.
	INDIVIDUALS_MAY_NOT_SURRENDER_DURING_COMBAT(1115),
	// Message: You cannot leave a clan while engaged in combat.
	YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT(1116),
	// Message: A clan member may not be dismissed during combat.
	A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT(1117),
	// Message:
	A_PRIVATE_STORE_MAY_NOT_BE_OPENED_WHILE_USING_A_SKILL(1128),
	// Message:
	WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(1135),
	// Message: Since there was an account that used this IP and attempted to log in illegally, this account is not allowed to connect to the game server for $s1 minutes. Please use
	// another game server.
	SINCE_THERE_WAS_AN_ACCOUNT_THAT_USED_THIS_IP_AND_ATTEMPTED_TO_LOG_IN_ILLEGALLY_THIS_ACCOUNT_IS_NOT_ALLOWED_TO_CONNECT_TO_THE_GAME_SERVER_FOR_S1_MINUTES(1136),
	// Message: $c1 harvested $s3 $s2(s).
	C1_HARVESTED_S3_S2S(1137),
	// Message: $c1 harvested $s2.
	C1_HARVESTED_S2(1138),
	// Message: The weight and volume limit of your inventory cannot be exceeded.
	THE_WEIGHT_AND_VOLUME_LIMIT_OF_YOUR_INVENTORY_CANNOT_BE_EXCEEDED(1139),
	// Message: Would you like to open the gate?
	WOULD_YOU_LIKE_TO_OPEN_THE_GATE(1140),
	// Message: Would you like to close the gate?
	WOULD_YOU_LIKE_TO_CLOSE_THE_GATE(1141),
	// Message: Since $s1 already exists nearby, you cannot summon it again.
	SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN(1142),
	// Message: Since you do not have enough items to maintain the servitor's stay, the servitor has disappeared.
	SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_HAS_DISAPPEARED(1143),
	// Message: You don't have anybody to chat with in the game.
	YOU_DONT_HAVE_ANYBODY_TO_CHAT_WITH_IN_THE_GAME(1144),
	// Message: $s2 has been created for $c1 after the payment of $s3 adena was received.
	S2_HAS_BEEN_CREATED_FOR_C1_AFTER_THE_PAYMENT_OF_S3_ADENA_WAS_RECEIVED(1145),
	// Message: $c1 created $s2 after receiving $s3 adena.
	C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA(1146),
	// Message: $s2 $s3 have been created for $c1 at the price of $s4 adena.
	S2_S3_HAVE_BEEN_CREATED_FOR_C1_AT_THE_PRICE_OF_S4_ADENA(1147),
	// Message: $c1 created $s2 $s3 at the price of $s4 adena.
	C1_CREATED_S2_S3_AT_THE_PRICE_OF_S4_ADENA(1148),
	// Message: Your attempt to create $s2 for $c1 at the price of $s3 adena has failed.
	YOUR_ATTEMPT_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA_HAS_FAILED(1149),
	// Message: $c1 has failed to create $s2 at the price of $s3 adena.
	C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA(1150),
	// Message: $s2 is sold to $c1 for the price of $s3 adena.
	S2_IS_SOLD_TO_C1_FOR_THE_PRICE_OF_S3_ADENA(1151),
	// Message: $s2 $s3 have been sold to $c1 for $s4 adena.
	S2_S3_HAVE_BEEN_SOLD_TO_C1_FOR_S4_ADENA(1152),
	// Message: $s2 has been purchased from $c1 at the price of $s3 adena.
	S2_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S3_ADENA(1153),
	// Message: $s3 $s2 has been purchased from $c1 for $s4 adena.
	S3_S2_HAS_BEEN_PURCHASED_FROM_C1_FOR_S4_ADENA(1154),
	// Message: +$s2$s3 has been sold to $c1 at the price of $s4 adena.
	S2S3_HAS_BEEN_SOLD_TO_C1_AT_THE_PRICE_OF_S4_ADENA(1155),
	// Message: +$s2$s3 has been purchased from $c1 at the price of $s4 adena.
	S2S3_HAS_BEEN_PURCHASED_FROM_C1_AT_THE_PRICE_OF_S4_ADENA(1156),
	// Message: The preview state only lasts for 5 seconds. If you wish to continue, please click Confirm.
	THE_PREVIEW_STATE_ONLY_LASTS_FOR_5_SECONDS(1157),
	// Message: You cannot dismount from this elevation.
	YOU_CANNOT_DISMOUNT_FROM_THIS_ELEVATION(1158),
	// Message: The ferry from Talking Island will arrive at Gludin Harbor in approximately 10 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_ARRIVE_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1159),
	// Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 5 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1160),
	// Message: The ferry from Talking Island will be arriving at Gludin Harbor in approximately 1 minute.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1161),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 15 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_15_MINUTES(1162),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 10 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_10_MINUTES(1163),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 5 minutes.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_5_MINUTES(1164),
	// Message: The ferry from Giran Harbor will be arriving at Talking Island in approximately 1 minute.
	THE_FERRY_FROM_GIRAN_HARBOR_WILL_BE_ARRIVING_AT_TALKING_ISLAND_IN_APPROXIMATELY_1_MINUTE(1165),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 20 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_20_MINUTES(1166),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 15 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_15_MINUTES(1167),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 10 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1168),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 5 minutes.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1169),
	// Message: The ferry from Talking Island will be arriving at Giran Harbor in approximately 1 minute.
	THE_FERRY_FROM_TALKING_ISLAND_WILL_BE_ARRIVING_AT_GIRAN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1170),
	// Message: The Innadril pleasure boat will arrive in approximately 20 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_20_MINUTES(1171),
	// Message: The Innadril pleasure boat will arrive in approximately 15 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_15_MINUTES(1172),
	// Message: The Innadril pleasure boat will arrive in approximately 10 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_10_MINUTES(1173),
	// Message: The Innadril pleasure boat will arrive in approximately 5 minutes.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_5_MINUTES(1174),
	// Message: The Innadril pleasure boat will arrive in approximately 1 minute.
	THE_INNADRIL_PLEASURE_BOAT_WILL_ARRIVE_IN_APPROXIMATELY_1_MINUTE(1175),
	// Message: The SSQ Competition period is underway.
	THE_SSQ_COMPETITION_PERIOD_IS_UNDERWAY(1176),
	// Message: This is the seal validation period.
	THIS_IS_THE_SEAL_VALIDATION_PERIOD(1177),
	// Message: This seal permits the group that holds it to exclusive entry to the dungeons opened by the Seal of Avarice during the seal validation period. It also permits trading
	// with the Merchant of Mammon and permits meetings with Anakim or Lilith in the Disciple's Necropolis.
	THIS_SEAL_PERMITS_THE_GROUP_THAT_HOLDS_IT_TO_EXCLUSIVE_ENTRY_TO_THE_DUNGEONS_OPENED_BY_THE_SEAL_OF_AVARICE_DURING_THE_SEAL_VALIDATION_PERIOD(1178),
	// Message: This seal permits the group that holds it to enter the dungeon opened by the Seal of Gnosis, use the teleportation service offered by the priest in the village, and do
	// business with the Blacksmith of Mammon. The Orator of Revelations appears and casts good magic on the winners, and the Preacher of Doom appears and casts bad magic on the losers.
	THIS_SEAL_PERMITS_THE_GROUP_THAT_HOLDS_IT_TO_ENTER_THE_DUNGEON_OPENED_BY_THE_SEAL_OF_GNOSIS_USE_THE_TELEPORTATION_SERVICE_OFFERED_BY_THE_PRIEST_IN_THE_VILLAGE_AND_DO_BUSINESS_WITH_THE_BLACKSMITH_OF_MAMMON(1179),
	// Message: During the Seal Validation period, the cabal's maximum CP amount increases. In addition, the cabal possessing the seal will benefit from favorable changes in the cost to
	// upgrade castle defense mercenaries, castle gates and walls; basic P. Def. of castle gates and walls; and the limit imposed on the castle tax rate. The use of siege war weapons
	// will also be limited. If the Revolutionary Army of Dusk takes possession of this seal during the castle siege war, only the clan that owns the castle can come to its defense.
	DURING_THE_SEAL_VALIDATION_PERIOD_THE_CABALS_MAXIMUM_CP_AMOUNT_INCREASES(1180),
	// Message: Do you really wish to change the title?
	DO_YOU_REALLY_WISH_TO_CHANGE_THE_TITLE(1181),
	// Message: Are you sure you wish to delete the clan crest?
	ARE_YOU_SURE_YOU_WISH_TO_DELETE_THE_CLAN_CREST(1182),
	// Message: This is the initial period.
	THIS_IS_THE_INITIAL_PERIOD(1183),
	// Message: This is a period when server statistics are calculated.
	THIS_IS_A_PERIOD_WHEN_SERVER_STATISTICS_ARE_CALCULATED(1184),
	// Message: days left until deletion.
	DAYS_LEFT_UNTIL_DELETION(1185),
	// Message: To create a new account, please visit the PlayNC website (http://us.ncsol2f.com/support/).
	TO_CREATE_A_NEW_ACCOUNT_PLEASE_VISIT_THE_PLAYNC_WEBSITE_HTTPUS(1186),
	// Message: If you've forgotten your account information or password, please visit the Support Center on the PlayNC website (http://us.ncsol2f.com/support/).
	IF_YOUVE_FORGOTTEN_YOUR_ACCOUNT_INFORMATION_OR_PASSWORD_PLEASE_VISIT_THE_SUPPORT_CENTER_ON_THE_PLAYNC_WEBSITE_HTTPUS(1187),
	// Message: Your selected target can no longer receive a recommendation.
	YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION(1188),
	// Message: The temporary alliance of the Castle Attacker team is in effect. It will be dissolved when the Castle Lord is replaced.
	THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_IS_IN_EFFECT(1189),
	// Message: The temporary alliance of the Castle Attacker team has been dissolved.
	THE_TEMPORARY_ALLIANCE_OF_THE_CASTLE_ATTACKER_TEAM_HAS_BEEN_DISSOLVED(1190),
	// Message: A mercenary can be assigned to a position from the beginning of the Seal Validation period until the time when a siege starts.
	A_MERCENARY_CAN_BE_ASSIGNED_TO_A_POSITION_FROM_THE_BEGINNING_OF_THE_SEAL_VALIDATION_PERIOD_UNTIL_THE_TIME_WHEN_A_SIEGE_STARTS(1194),
	// Message: This mercenary cannot be assigned to a position by using the Seal of Strife.
	THIS_MERCENARY_CANNOT_BE_ASSIGNED_TO_A_POSITION_BY_USING_THE_SEAL_OF_STRIFE(1195),
	// Message: Your force has reached maximum capacity.
	YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY_(1196),
	// Message: Summoning a servitor costs $s2 $s1.
	SUMMONING_A_SERVITOR_COSTS_S2_S1(1197),
	// Message: The item has been successfully crystallized.
	THE_ITEM_HAS_BEEN_SUCCESSFULLY_CRYSTALLIZED(1198),
	// Message: =======<Clan War Target>=======
	CLAN_WAR_TARGET(1199),
	// Message: = $s1 ($s2 Alliance)
	_S1_S2_ALLIANCE(1200),
	// Message: Please select the quest you wish to abort.
	PLEASE_SELECT_THE_QUEST_YOU_WISH_TO_ABORT(1201),
	// Message: = $s1 (No alliance exists)
	_S1_NO_ALLIANCE_EXISTS(1202),
	// Message: There is no clan war in progress.
	THERE_IS_NO_CLAN_WAR_IN_PROGRESS(1203),
	// Message: The screenshot has been saved. ($s1 $s2x$s3)
	THE_SCREENSHOT_HAS_BEEN_SAVED(1204),
	// Message: Your mailbox is full. There is a 100 message limit.
	YOUR_MAILBOX_IS_FULL(1205),
	// Message: The memo box is full. There is a 100 memo limit.
	THE_MEMO_BOX_IS_FULL(1206),
	// Message: Please make an entry in the field.
	PLEASE_MAKE_AN_ENTRY_IN_THE_FIELD(1207),
	// Message: $c1 died and dropped $s3 $s2.
	C1_DIED_AND_DROPPED_S3_S2(1208),
	// Message: Congratulations. Your raid was successful.
	CONGRATULATIONS(1209),
	// Message: Seven Signs: The competition period has begun. Visit a Priest of Dawn or Priestess of Dusk to participate in the event.
	SEVEN_SIGNS_THE_COMPETITION_PERIOD_HAS_BEGUN(1210),
	// Message: Seven Signs: The competition period has ended. The next quest event will start in one week.
	SEVEN_SIGNS_THE_COMPETITION_PERIOD_HAS_ENDED(1211),
	// Message: Seven Signs: The Lords of Dawn have obtained the Seal of Avarice.
	SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_AVARICE(1212),
	// Message: Seven Signs: The Lords of Dawn have obtained the Seal of Gnosis.
	SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS(1213),
	// Message: Seven Signs: The Lords of Dawn have obtained the Seal of Strife.
	SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_OBTAINED_THE_SEAL_OF_STRIFE(1214),
	// Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Avarice.
	SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_AVARICE(1215),
	// Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Gnosis.
	SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_GNOSIS(1216),
	// Message: Seven Signs: The Revolutionaries of Dusk have obtained the Seal of Strife.
	SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_OBTAINED_THE_SEAL_OF_STRIFE(1217),
	// Message: Seven Signs: The Seal Validation period has begun.
	SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_BEGUN(1218),
	// Message: Seven Signs: The Seal Validation period has ended.
	SEVEN_SIGNS_THE_SEAL_VALIDATION_PERIOD_HAS_ENDED(1219),
	// Message: Are you sure you wish to summon it?
	ARE_YOU_SURE_YOU_WISH_TO_SUMMON_IT(1220),
	// Message: Do you really wish to return it?
	DO_YOU_REALLY_WISH_TO_RETURN_IT(1221),
	// Message: Current Location: $s1, $s2, $s3 (GM Consultation Area)
	CURRENT_LOCATION_S1_S2_S3_GM_CONSULTATION_AREA(1222),
	// Message: We depart for Talking Island in five minutes.
	WE_DEPART_FOR_TALKING_ISLAND_IN_FIVE_MINUTES(1223),
	// Message: We depart for Talking Island in one minute.
	WE_DEPART_FOR_TALKING_ISLAND_IN_ONE_MINUTE(1224),
	// Message: All aboard for Talking Island!
	ALL_ABOARD_FOR_TALKING_ISLAND(1225),
	// Message: We are now leaving for Talking Island.
	WE_ARE_NOW_LEAVING_FOR_TALKING_ISLAND(1226),
	// Message: You have $s1 unread messages.
	YOU_HAVE_S1_UNREAD_MESSAGES(1227),
	// Message: $c1 has blocked you. You cannot send mail to $c1.
	C1_HAS_BLOCKED_YOU(1228),
	// Message: No more messages may be sent at this time. Each account is allowed 10 messages per day.
	NO_MORE_MESSAGES_MAY_BE_SENT_AT_THIS_TIME(1229),
	// Message: You are limited to five recipients at a time.
	YOU_ARE_LIMITED_TO_FIVE_RECIPIENTS_AT_A_TIME(1230),
	// Message: You've sent mail.
	YOUVE_SENT_MAIL(1231),
	// Message: The message was not sent.
	THE_MESSAGE_WAS_NOT_SENT(1232),
	// Message: You've got mail.
	YOUVE_GOT_MAIL(1233),
	// Message: The mail has been stored in your temporary mailbox.
	THE_MAIL_HAS_BEEN_STORED_IN_YOUR_TEMPORARY_MAILBOX(1234),
	// Message: Do you wish to delete all your friends?
	DO_YOU_WISH_TO_DELETE_ALL_YOUR_FRIENDS(1235),
	// Message: Please enter security card number.
	PLEASE_ENTER_SECURITY_CARD_NUMBER(1236),
	// Message: Please enter the card number for number $s1.
	PLEASE_ENTER_THE_CARD_NUMBER_FOR_NUMBER_S1(1237),
	// Message: Your temporary mailbox is full. No more mail can be stored; you have reached the 10 message limit.
	YOUR_TEMPORARY_MAILBOX_IS_FULL(1238),
	// Message: The keyboard security module has failed to load. Please exit the game and try again.
	THE_KEYBOARD_SECURITY_MODULE_HAS_FAILED_TO_LOAD(1239),
	// Message: Seven Signs: The Revolutionaries of Dusk have won.
	SEVEN_SIGNS_THE_REVOLUTIONARIES_OF_DUSK_HAVE_WON(1240),
	// Message: Seven Signs: The Lords of Dawn have won.
	SEVEN_SIGNS_THE_LORDS_OF_DAWN_HAVE_WON(1241),
	// Message: Users who have not verified their age may not log in between the hours of 10:00 p.m. and 6:00 a.m.
	USERS_WHO_HAVE_NOT_VERIFIED_THEIR_AGE_MAY_NOT_LOG_IN_BETWEEN_THE_HOURS_OF_1000_P(1242),
	// Message: The security card number is invalid.
	THE_SECURITY_CARD_NUMBER_IS_INVALID(1243),
	// Message: Users who have not verified their age may not log in between the hours of 10:00 p.m. and 6:00 a.m. Logging off now.
	USERS_WHO_HAVE_NOT_VERIFIED_THEIR_AGE_MAY_NOT_LOG_IN_BETWEEN_THE_HOURS_OF_1000_P_(1244),
	// Message: You will be logged out in $s1 minutes.
	YOU_WILL_BE_LOGGED_OUT_IN_S1_MINUTES(1245),
	// Message: $c1 has died and dropped $s2 adena.
	C1_HAS_DIED_AND_DROPPED_S2_ADENA(1246),
	// Message: The corpse is too old. The skill cannot be used.
	THE_CORPSE_IS_TOO_OLD(1247),
	// Message: You are out of feed. Mount status canceled.
	YOU_ARE_OUT_OF_FEED(1248),
	// Message: You may only ride a wyvern while you're riding a strider.
	YOU_MAY_ONLY_RIDE_A_WYVERN_WHILE_YOURE_RIDING_A_STRIDER(1249),
	// Message: Do you really want to surrender? If you surrender during an alliance war, your Exp will drop the same as if you were to die once.
	DO_YOU_REALLY_WANT_TO_SURRENDER_IF_YOU_SURRENDER_DURING_AN_ALLIANCE_WAR_YOUR_EXP_WILL_DROP_THE_SAME_AS_IF_YOU_WERE_TO_DIE_ONCE(1250),
	// Message: Are you sure you want to dismiss the alliance? If you use the /allydismiss command, you will not be able to accept another clan to your alliance for one day.
	ARE_YOU_SURE_YOU_WANT_TO_DISMISS_THE_ALLIANCE_IF_YOU_USE_THE_ALLYDISMISS_COMMAND_YOU_WILL_NOT_BE_ABLE_TO_ACCEPT_ANOTHER_CLAN_TO_YOUR_ALLIANCE_FOR_ONE_DAY(1251),
	// Message: Are you sure you want to surrender? Exp penalty will be the same as death.
	ARE_YOU_SURE_YOU_WANT_TO_SURRENDER_EXP_PENALTY_WILL_BE_THE_SAME_AS_DEATH(1252),
	// Message: Are you sure you want to surrender? Exp penalty will be the same as death and you will not be allowed to participate in clan war.
	ARE_YOU_SURE_YOU_WANT_TO_SURRENDER_EXP_PENALTY_WILL_BE_THE_SAME_AS_DEATH_AND_YOU_WILL_NOT_BE_ALLOWED_TO_PARTICIPATE_IN_CLAN_WAR(1253),
	// Message: Thank you for submitting feedback.
	THANK_YOU_FOR_SUBMITTING_FEEDBACK(1254),
	// Message: GM consultation has begun.
	GM_CONSULTATION_HAS_BEGUN(1255),
	// Message: Please write the name after the command.
	PLEASE_WRITE_THE_NAME_AFTER_THE_COMMAND(1256),
	// Message: The special skill of a servitor or pet cannot be registered as a macro.
	THE_SPECIAL_SKILL_OF_A_SERVITOR_OR_PET_CANNOT_BE_REGISTERED_AS_A_MACRO(1257),
	// Message: $s1 has been crystallized.
	S1_HAS_BEEN_CRYSTALLIZED(1258),
	// Message: =======<Alliance Target>=======
	ALLIANCE_TARGET(1259),
	// Message: Seven Signs: Preparations have begun for the next quest event.
	SEVEN_SIGNS_PREPARATIONS_HAVE_BEGUN_FOR_THE_NEXT_QUEST_EVENT(1260),
	// Message: Seven Signs: The quest event period has begun. Speak with a Priest of Dawn or Dusk Priestess if you wish to participate in the event.
	SEVEN_SIGNS_THE_QUEST_EVENT_PERIOD_HAS_BEGUN(1261),
	// Message: Seven Signs: Quest event has ended. Results are being tallied.
	SEVEN_SIGNS_QUEST_EVENT_HAS_ENDED(1262),
	// Message: Seven Signs: This is the seal validation period. A new quest event period begins next Monday.
	SEVEN_SIGNS_THIS_IS_THE_SEAL_VALIDATION_PERIOD(1263),
	// Message: This soul stone cannot currently absorb souls. Absorption has failed.
	THIS_SOUL_STONE_CANNOT_CURRENTLY_ABSORB_SOULS(1264),
	// Message: You can't absorb souls without a soul stone.
	YOU_CANT_ABSORB_SOULS_WITHOUT_A_SOUL_STONE(1265),
	// Message: The exchange has ended.
	THE_EXCHANGE_HAS_ENDED(1266),
	// Message: Your contribution score has increased by $s1.
	YOUR_CONTRIBUTION_SCORE_HAS_INCREASED_BY_S1(1267),
	// Message: Do you wish to add $s1 as your sub class?
	DO_YOU_WISH_TO_ADD_S1_AS_YOUR_SUB_CLASS(1268),
	// Message: The new subclass has been added.
	THE_NEW_SUBCLASS_HAS_BEEN_ADDED(1269),
	// Message: You have successfully switched to your subclass.
	YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS(1270),
	// Message: Do you wish to participate? Until the next seal validation period, you will be a member of the Lords of Dawn.
	DO_YOU_WISH_TO_PARTICIPATE_UNTIL_THE_NEXT_SEAL_VALIDATION_PERIOD_YOU_WILL_BE_A_MEMBER_OF_THE_LORDS_OF_DAWN(1271),
	// Message: Do you wish to participate? Until the next seal validation period, you will be a member of the Revolutionaries of Dusk.
	DO_YOU_WISH_TO_PARTICIPATE_UNTIL_THE_NEXT_SEAL_VALIDATION_PERIOD_YOU_WILL_BE_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK(1272),
	// Message: You will participate in the Seven Signs as a member of the Lords of Dawn.
	YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN(1273),
	// Message: You will participate in the Seven Signs as a member of the Revolutionaries of Dusk.
	YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK(1274),
	// Message: You've chosen to fight for the Seal of Avarice during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD(1275),
	// Message: You've chosen to fight for the Seal of Gnosis during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD(1276),
	// Message: You've chosen to fight for the Seal of Strife during this quest event period.
	YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD(1277),
	// Message: The NPC server is not operating at this time.
	THE_NPC_SERVER_IS_NOT_OPERATING_AT_THIS_TIME(1278),
	// Message: Contribution level has exceeded the limit. You may not continue.
	CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT(1279),
	// Message: Magic Critical Hit!
	MAGIC_CRITICAL_HIT(1280),
	// Message: Your excellent shield defense was a success!
	YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS(1281),
	// Message: Subclasses may not be created or changed while a skill is in use.
	SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE(1295),
	// Message: You cannot open a Private Store here.
	YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE(1296),
	// Message: You cannot open a Private Workshop here.
	YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE(1297),
	// Message: Please confirm that you would like to exit the Monster Race Track.
	PLEASE_CONFIRM_THAT_YOU_WOULD_LIKE_TO_EXIT_THE_MONSTER_RACE_TRACK(1298),
	// Message: $c1's casting has been interrupted.
	C1S_CASTING_HAS_BEEN_INTERRUPTED(1299),
	// Message: You are no longer trying on equipment.
	YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT(1300),
	// Message: Only a Lord of Dawn may use this.
	ONLY_A_LORD_OF_DAWN_MAY_USE_THIS(1301),
	// Message: Only a Revolutionary of Dusk may use this.
	ONLY_A_REVOLUTIONARY_OF_DUSK_MAY_USE_THIS(1302),
	// Message: This may only be used during the quest event period.
	THIS_MAY_ONLY_BE_USED_DURING_THE_QUEST_EVENT_PERIOD(1303),
	// Message: The influence of the Seal of Strife has caused all defensive registrations to be canceled.
	THE_INFLUENCE_OF_THE_SEAL_OF_STRIFE_HAS_CAUSED_ALL_DEFENSIVE_REGISTRATIONS_TO_BE_CANCELED(1304),
	// Message: Seal Stones may only be transferred during the quest event period.
	SEAL_STONES_MAY_ONLY_BE_TRANSFERRED_DURING_THE_QUEST_EVENT_PERIOD(1305),
	// Message: You are no longer trying on equipment.
	YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_(1306),
	// Message: Only during the seal validation period may you settle your account.
	ONLY_DURING_THE_SEAL_VALIDATION_PERIOD_MAY_YOU_SETTLE_YOUR_ACCOUNT(1307),
	// Message: Congratulations - You've completed a class transfer!
	CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER(1308),
	// Message: To use this option, you must have the latest version of MSN Messenger installed on your computer.
	TO_USE_THIS_OPTION_YOU_MUST_HAVE_THE_LATEST_VERSION_OF_MSN_MESSENGER_INSTALLED_ON_YOUR_COMPUTER(1309),
	// Message: For full functionality, the latest version of MSN Messenger must be installed on your computer.
	FOR_FULL_FUNCTIONALITY_THE_LATEST_VERSION_OF_MSN_MESSENGER_MUST_BE_INSTALLED_ON_YOUR_COMPUTER(1310),
	// Message: Previous versions of MSN Messenger only provide the basic features for in-game MSN Messenger chat. Add/Delete Contacts and other MSN Messenger options are not available.
	PREVIOUS_VERSIONS_OF_MSN_MESSENGER_ONLY_PROVIDE_THE_BASIC_FEATURES_FOR_INGAME_MSN_MESSENGER_CHAT(1311),
	// Message: The latest version of MSN Messenger may be obtained from the MSN web site (http://messenger.msn.com).
	THE_LATEST_VERSION_OF_MSN_MESSENGER_MAY_BE_OBTAINED_FROM_THE_MSN_WEB_SITE_HTTPMESSENGER(1312),
	// Message: $s1, to better server our customers, all chat histories are stored and maintained by NCsol2f. If you do not agree to have your chat records stored, please close the chat
	// window now. For more information regarding this procedure, please visit our home page at www.PlayNC.com. Thank you!
	S1_TO_BETTER_SERVER_OUR_CUSTOMERS_ALL_CHAT_HISTORIES_ARE_STORED_AND_MAINTAINED_BY_NCSOFT(1313),
	// Message: Please enter the passport ID of the person you wish to add to your contact list.
	PLEASE_ENTER_THE_PASSPORT_ID_OF_THE_PERSON_YOU_WISH_TO_ADD_TO_YOUR_CONTACT_LIST(1314),
	// Message: Deleting a contact will remove that contact from MSN Messenger as well. The contact can still check your online status and will not be blocked from sending you a
	// message.
	DELETING_A_CONTACT_WILL_REMOVE_THAT_CONTACT_FROM_MSN_MESSENGER_AS_WELL(1315),
	// Message: The contact will be deleted and blocked from your contact list.
	THE_CONTACT_WILL_BE_DELETED_AND_BLOCKED_FROM_YOUR_CONTACT_LIST(1316),
	// Message: Would you like to delete this contact?
	WOULD_YOU_LIKE_TO_DELETE_THIS_CONTACT(1317),
	// Message: Please select the contact you want to block or unblock.
	PLEASE_SELECT_THE_CONTACT_YOU_WANT_TO_BLOCK_OR_UNBLOCK(1318),
	// Message: Please select the name of the contact you wish to change to another group.
	PLEASE_SELECT_THE_NAME_OF_THE_CONTACT_YOU_WISH_TO_CHANGE_TO_ANOTHER_GROUP(1319),
	// Message: After selecting the group you wish to move your contact to, press the OK button.
	AFTER_SELECTING_THE_GROUP_YOU_WISH_TO_MOVE_YOUR_CONTACT_TO_PRESS_THE_OK_BUTTON(1320),
	// Message: Enter the name of the group you wish to add.
	ENTER_THE_NAME_OF_THE_GROUP_YOU_WISH_TO_ADD(1321),
	// Message: Select the group and enter the new name.
	SELECT_THE_GROUP_AND_ENTER_THE_NEW_NAME(1322),
	// Message: Select the group you wish to delete and click the OK button.
	SELECT_THE_GROUP_YOU_WISH_TO_DELETE_AND_CLICK_THE_OK_BUTTON(1323),
	// Message: Signing in...
	SIGNING_IN(1324),
	// Message: You've logged into another computer and have been logged out of the .NET Messenger Service on this computer.
	YOUVE_LOGGED_INTO_ANOTHER_COMPUTER_AND_HAVE_BEEN_LOGGED_OUT_OF_THE_(1325),
	// Message: The following message could not be delivered:
	THE_FOLLOWING_MESSAGE_COULD_NOT_BE_DELIVERED(1327),
	// Message: Members of the Revolutionaries of Dusk will not be resurrected.
	MEMBERS_OF_THE_REVOLUTIONARIES_OF_DUSK_WILL_NOT_BE_RESURRECTED(1328),
	// Message: You are currently blocked from using the Private Store and Private Workshop.
	YOU_ARE_CURRENTLY_BLOCKED_FROM_USING_THE_PRIVATE_STORE_AND_PRIVATE_WORKSHOP(1329),
	// Message: You may not open a Private Store or Private Workshop for another $s1 minute(s).
	YOU_MAY_NOT_OPEN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_FOR_ANOTHER_S1_MINUTES(1330),
	// Message: You are no longer blocked from using Private Stores or Private Workshops.
	YOU_ARE_NO_LONGER_BLOCKED_FROM_USING_PRIVATE_STORES_OR_PRIVATE_WORKSHOPS(1331),
	// Message: Items may not be used after your character or pet dies.
	ITEMS_MAY_NOT_BE_USED_AFTER_YOUR_CHARACTER_OR_PET_DIES(1332),
	// Message: The replay file is not accessible. Please verify that the replay.ini file exists in your Lineage 2 directory. Please note that footage from previous major updates are
	// not accessible in newer updates.
	THE_REPLAY_FILE_IS_NOT_ACCESSIBLE(1333),
	// Message: Your recording has been stored in the Replay folder.
	YOUR_RECORDING_HAS_BEEN_STORED_IN_THE_REPLAY_FOLDER(1334),
	// Message: Your attempt to store this recording has failed.
	YOUR_ATTEMPT_TO_STORE_THIS_RECORDING_HAS_FAILED(1335),
	// Message: The replay file, $s1.$s2 has been corrupted, please check the file.
	THE_REPLAY_FILE_S1(1336),
	// Message: This will terminate the replay. Do you wish to continue?
	THIS_WILL_TERMINATE_THE_REPLAY(1337),
	// Message: You have exceeded the maximum amount that may be transferred at one time.
	YOU_HAVE_EXCEEDED_THE_MAXIMUM_AMOUNT_THAT_MAY_BE_TRANSFERRED_AT_ONE_TIME(1338),
	// Message: Once a macro is assigned to a shortcut, it cannot be run as part of a new macro.
	ONCE_A_MACRO_IS_ASSIGNED_TO_A_SHORTCUT_IT_CANNOT_BE_RUN_AS_PART_OF_A_NEW_MACRO(1339),
	// Message: This server cannot be accessed with the coupon you are using.
	THIS_SERVER_CANNOT_BE_ACCESSED_WITH_THE_COUPON_YOU_ARE_USING(1340),
	// Message: Incorrect name and/or email address.
	INCORRECT_NAME_ANDOR_EMAIL_ADDRESS(1341),
	// Message: You are already logged in.
	YOU_ARE_ALREADY_LOGGED_IN(1342),
	// Message: Incorrect email address and/or password. Your attempt to log into .NET Messenger Service has failed.
	INCORRECT_EMAIL_ADDRESS_ANDOR_PASSWORD(1343),
	// Message: Your request to log into the .NET Messenger Service has failed. Please verify that you are currently connected to the internet.
	YOUR_REQUEST_TO_LOG_INTO_THE_(1344),
	// Message: Click on the OK button after you have selected a contact name.
	CLICK_ON_THE_OK_BUTTON_AFTER_YOU_HAVE_SELECTED_A_CONTACT_NAME(1345),
	// Message: You are currently entering a chat message.
	YOU_ARE_CURRENTLY_ENTERING_A_CHAT_MESSAGE(1346),
	// Message: The Lineage II messenger could not carry out the task you requested.
	THE_LINEAGE_II_MESSENGER_COULD_NOT_CARRY_OUT_THE_TASK_YOU_REQUESTED(1347),
	// Message: $s1 has entered the chat room.
	S1_HAS_ENTERED_THE_CHAT_ROOM(1348),
	// Message: $s1 has left the chat room.
	S1_HAS_LEFT_THE_CHAT_ROOM(1349),
	// Message: Your status will be changed to indicate that you are "off-line." All chat windows currently open will be closed.
	YOUR_STATUS_WILL_BE_CHANGED_TO_INDICATE_THAT_YOU_ARE_OFFLINE(1350),
	// Message: Click the Delete button after selecting the contact you wish to remove.
	CLICK_THE_DELETE_BUTTON_AFTER_SELECTING_THE_CONTACT_YOU_WISH_TO_REMOVE(1351),
	// Message: You have been added to $s1 ($s2)'s contact list.
	YOU_HAVE_BEEN_ADDED_TO_S1_S2S_CONTACT_LIST(1352),
	// Message: You can set the option to show your status as always being off-line to all of your contacts.
	YOU_CAN_SET_THE_OPTION_TO_SHOW_YOUR_STATUS_AS_ALWAYS_BEING_OFFLINE_TO_ALL_OF_YOUR_CONTACTS(1353),
	// Message: You are not allowed to chat with a contact while a chatting block is imposed.
	YOU_ARE_NOT_ALLOWED_TO_CHAT_WITH_A_CONTACT_WHILE_A_CHATTING_BLOCK_IS_IMPOSED(1354),
	// Message: That contact is currently blocked from chatting.
	THAT_CONTACT_IS_CURRENTLY_BLOCKED_FROM_CHATTING(1355),
	// Message: That contact is not currently logged in.
	THAT_CONTACT_IS_NOT_CURRENTLY_LOGGED_IN(1356),
	// Message: You have been blocked from chatting with that contact.
	YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT(1357),
	// Message: You are being logged out...
	YOU_ARE_BEING_LOGGED_OUT(1358),
	// Message: $s1 has logged in.
	S1_HAS_LOGGED_IN_(1359),
	// Message: You have received a message from $s1.
	YOU_HAVE_RECEIVED_A_MESSAGE_FROM_S1(1360),
	// Message: Due to a system error, you have been logged out of the .NET Messenger Service.
	DUE_TO_A_SYSTEM_ERROR_YOU_HAVE_BEEN_LOGGED_OUT_OF_THE_(1361),
	// Message: Please select the contact you wish to delete. If you would like to delete a group, click the button next to My Status, and then use the Options menu.
	PLEASE_SELECT_THE_CONTACT_YOU_WISH_TO_DELETE(1362),
	// Message: Your request to participate to initiate an alliance war has been denied.
	YOUR_REQUEST_TO_PARTICIPATE_TO_INITIATE_AN_ALLIANCE_WAR_HAS_BEEN_DENIED(1363),
	// Message: The request for an alliance war has been rejected.
	THE_REQUEST_FOR_AN_ALLIANCE_WAR_HAS_BEEN_REJECTED(1364),
	// Message: $s2 of $s1 clan has surrendered as an individual.
	S2_OF_S1_CLAN_HAS_SURRENDERED_AS_AN_INDIVIDUAL(1365),
	// Message: In order to delete a group, you must not have any contacts listed under that group. Please transfer your contact(s) to another group before continuing with deletion.
	IN_ORDER_TO_DELETE_A_GROUP_YOU_MUST_NOT_HAVE_ANY_CONTACTS_LISTED_UNDER_THAT_GROUP(1366),
	// Message: Only members of the group are allowed to add records.
	ONLY_MEMBERS_OF_THE_GROUP_ARE_ALLOWED_TO_ADD_RECORDS(1367),
	// Message: You can not try those items on at the same time.
	YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME(1368),
	// Message: You've exceeded the maximum.
	YOUVE_EXCEEDED_THE_MAXIMUM(1369),
	// Message: Your message to $c1 did not reach its recipient. You cannot send mail to the GM staff.
	YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT(1370),
	// Message: It has been determined that you're not engaged in normal gameplay and a restriction has been imposed upon you. You may not move for $s1 minutes.
	IT_HAS_BEEN_DETERMINED_THAT_YOURE_NOT_ENGAGED_IN_NORMAL_GAMEPLAY_AND_A_RESTRICTION_HAS_BEEN_IMPOSED_UPON_YOU(1371),
	// Message: Your punishment will continue for $s1 minutes.
	YOUR_PUNISHMENT_WILL_CONTINUE_FOR_S1_MINUTES(1372),
	// Message: $c1 has picked up $s2 that was dropped by the Raid Boss.
	C1_HAS_PICKED_UP_S2_THAT_WAS_DROPPED_BY_THE_RAID_BOSS(1373),
	// Message: $c1 has picked up $s3 $s2(s) that were dropped by the Raid Boss.
	C1_HAS_PICKED_UP_S3_S2S_THAT_WERE_DROPPED_BY_THE_RAID_BOSS(1374),
	// Message: $c1 has picked up $s2 adena that was dropped by the Raid Boss.
	C1_HAS_PICKED_UP_S2_ADENA_THAT_WAS_DROPPED_BY_THE_RAID_BOSS(1375),
	// Message: $c1 has picked up $s2 that was dropped by another character.
	C1_HAS_PICKED_UP_S2_THAT_WAS_DROPPED_BY_ANOTHER_CHARACTER(1376),
	// Message: $c1 has picked up $s3 $s2(s) that were dropped by another character.
	C1_HAS_PICKED_UP_S3_S2S_THAT_WERE_DROPPED_BY_ANOTHER_CHARACTER(1377),
	// Message: $c1 has picked up +$s3 $s2 that was dropped by another character.
	C1_HAS_PICKED_UP_S3_S2_THAT_WAS_DROPPED_BY_ANOTHER_CHARACTER(1378),
	// Message: $c1 has obtained $s2 adena.
	C1_HAS_OBTAINED_S2_ADENA(1379),
	// Message: You can't summon a $s1 while on the battleground.
	YOU_CANT_SUMMON_A_S1_WHILE_ON_THE_BATTLEGROUND(1380),
	// Message: The party leader has obtained $s2 of $s1.
	THE_PARTY_LEADER_HAS_OBTAINED_S2_OF_S1(1381),
	// Message: To fulfill the quest, you must bring the chosen weapon. Are you sure you want to choose this weapon?
	TO_FULFILL_THE_QUEST_YOU_MUST_BRING_THE_CHOSEN_WEAPON(1382),
	// Message: Are you sure you want to exchange?
	ARE_YOU_SURE_YOU_WANT_TO_EXCHANGE(1383),
	// Message: $c1 has become the party leader.
	C1_HAS_BECOME_THE_PARTY_LEADER(1384),
	// Message: You are not allowed to dismount in this location.
	YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION(1385),
	// Message: You are no longer immobile.
	YOU_ARE_NO_LONGER_IMMOBILE(1386),
	// Message: Please select the item you would like to try on.
	PLEASE_SELECT_THE_ITEM_YOU_WOULD_LIKE_TO_TRY_ON(1387),
	// Message: You have created a party room.
	YOU_HAVE_CREATED_A_PARTY_ROOM(1388),
	// Message: The party room's information has been revised.
	THE_PARTY_ROOMS_INFORMATION_HAS_BEEN_REVISED(1389),
	// Message: You are not allowed to enter the party room.
	YOU_ARE_NOT_ALLOWED_TO_ENTER_THE_PARTY_ROOM(1390),
	// Message: You have exited the party room.
	YOU_HAVE_EXITED_THE_PARTY_ROOM(1391),
	// Message: $c1 has left the party room.
	C1_HAS_LEFT_THE_PARTY_ROOM(1392),
	// Message: You have been ousted from the party room.
	YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM(1393),
	// Message: $c1 has been kicked from the party room.
	C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM(1394),
	// Message: The party room has been disbanded.
	THE_PARTY_ROOM_HAS_BEEN_DISBANDED(1395),
	// Message: The list of party rooms can only be viewed by a person who is not part of a party.
	THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY(1396),
	// Message: The leader of the party room has changed.
	THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED(1397),
	// Message: We are recruiting party members.
	WE_ARE_RECRUITING_PARTY_MEMBERS(1398),
	// Message: Only the leader of the party can transfer party leadership to another player.
	ONLY_THE_LEADER_OF_THE_PARTY_CAN_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_PLAYER(1399),
	// Message: Please select the person you wish to make the party leader.
	PLEASE_SELECT_THE_PERSON_YOU_WISH_TO_MAKE_THE_PARTY_LEADER(1400),
	// Message: Slow down, you are already the party leader.
	SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER(1401),
	// Message: You may only transfer party leadership to another member of the party.
	YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY(1402),
	// Message: You have failed to transfer party leadership.
	YOU_HAVE_FAILED_TO_TRANSFER_PARTY_LEADERSHIP(1403),
	// Message: The owner of the private manufacturing store has changed the price for creating this item. Please check the new price before trying again.
	THE_OWNER_OF_THE_PRIVATE_MANUFACTURING_STORE_HAS_CHANGED_THE_PRICE_FOR_CREATING_THIS_ITEM(1404),
	// Message: $s1 CP has been restored.
	S1_CP_HAS_BEEN_RESTORED(1405),
	// Message: $s2 CP has been restored by $c1.
	S2_CP_HAS_BEEN_RESTORED_BY_C1(1406),
	// Message: You do not meet the requirements to enter that party room.
	YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM(1413),
	// Message: The width and length should be 100 or more grids and less than 5000 grids respectively.
	THE_WIDTH_AND_LENGTH_SHOULD_BE_100_OR_MORE_GRIDS_AND_LESS_THAN_5000_GRIDS_RESPECTIVELY(1414),
	// Message: The command file is not set.
	THE_COMMAND_FILE_IS_NOT_SET(1415),
	// Message: The party representative of Team 1 has not been selected.
	THE_PARTY_REPRESENTATIVE_OF_TEAM_1_HAS_NOT_BEEN_SELECTED(1416),
	// Message: The party representative of Team 2 has not been selected.
	THE_PARTY_REPRESENTATIVE_OF_TEAM_2_HAS_NOT_BEEN_SELECTED(1417),
	// Message: The name of Team 1 has not yet been chosen.
	THE_NAME_OF_TEAM_1_HAS_NOT_YET_BEEN_CHOSEN(1418),
	// Message: The name of Team 2 has not yet been chosen.
	THE_NAME_OF_TEAM_2_HAS_NOT_YET_BEEN_CHOSEN(1419),
	// Message: The name of Team 1 and the name of Team 2 are identical.
	THE_NAME_OF_TEAM_1_AND_THE_NAME_OF_TEAM_2_ARE_IDENTICAL(1420),
	// Message: The race setup file has not been designated.
	THE_RACE_SETUP_FILE_HAS_NOT_BEEN_DESIGNATED(1421),
	// Message: Race setup file error - BuffCnt is not specified.
	RACE_SETUP_FILE_ERROR__BUFFCNT_IS_NOT_SPECIFIED(1422),
	// Message: Race setup file error - BuffID$s1 is not specified.
	RACE_SETUP_FILE_ERROR__BUFFIDS1_IS_NOT_SPECIFIED(1423),
	// Message: Race setup file error - BuffLv$s1 is not specified.
	RACE_SETUP_FILE_ERROR__BUFFLVS1_IS_NOT_SPECIFIED(1424),
	// Message: Race setup file error - DefaultAllow is not specified.
	RACE_SETUP_FILE_ERROR__DEFAULTALLOW_IS_NOT_SPECIFIED(1425),
	// Message: Race setup file error - ExpSkillCnt is not specified.
	RACE_SETUP_FILE_ERROR__EXPSKILLCNT_IS_NOT_SPECIFIED(1426),
	// Message: Race setup file error - ExpSkillID$s1 is not specified.
	RACE_SETUP_FILE_ERROR__EXPSKILLIDS1_IS_NOT_SPECIFIED(1427),
	// Message: Race setup file error - ExpItemCnt is not specified.
	RACE_SETUP_FILE_ERROR__EXPITEMCNT_IS_NOT_SPECIFIED(1428),
	// Message: Race setup file error - ExpItemID$s1 is not specified.
	RACE_SETUP_FILE_ERROR__EXPITEMIDS1_IS_NOT_SPECIFIED(1429),
	// Message: Race setup file error - TeleportDelay is not specified.
	RACE_SETUP_FILE_ERROR___TELEPORTDELAY_IS_NOT_SPECIFIED(1430),
	// Message: The race will be stopped temporarily.
	THE_RACE_WILL_BE_STOPPED_TEMPORARILY(1431),
	// Message: Your opponent is currently in a petrified state.
	YOUR_OPPONENT_IS_CURRENTLY_IN_A_PETRIFIED_STATE(1432),
	// Message: The automatic use of $s1 has been activated.
	THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED(1433),
	// Message: The automatic use of $s1 has been deactivated.
	THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED(1434),
	// Message: Due to insufficient $s1, the automatic use function has been deactivated.
	DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_HAS_BEEN_DEACTIVATED(1435),
	// Message: Due to insufficient $s1, the automatic use function cannot be activated.
	DUE_TO_INSUFFICIENT_S1_THE_AUTOMATIC_USE_FUNCTION_CANNOT_BE_ACTIVATED(1436),
	// Message: Players are no longer allowed to play dice. Dice can no longer be purchased from a village store. However, you can still sell them to any village store.
	PLAYERS_ARE_NO_LONGER_ALLOWED_TO_PLAY_DICE(1437),
	// Message: There is no skill that enables enchant.
	THERE_IS_NO_SKILL_THAT_ENABLES_ENCHANT(1438),
	// Message: You do not have all of the items needed to enchant that skill.
	YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL(1439),
	// Message: Skill enchant was successful! $s1 has been enchanted.
	SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED(1440),
	// Message: Skill enchant failed. The skill will be initialized.
	SKILL_ENCHANT_FAILED(1441),
	// Message: Remaining Time: $s1 second(s)
	REMAINING_TIME_S1_SECONDS(1442),
	// Message: You do not have enough SP to enchant that skill.
	YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL(1443),
	// Message: You do not have enough experience (Exp) to enchant that skill.
	YOU_DO_NOT_HAVE_ENOUGH_EXPERIENCE_EXP_TO_ENCHANT_THAT_SKILL(1444),
	// Message: Your previous subclass will be removed and replaced with the new subclass at level 40. Do you wish to continue?
	YOUR_PREVIOUS_SUBCLASS_WILL_BE_REMOVED_AND_REPLACED_WITH_THE_NEW_SUBCLASS_AT_LEVEL_40(1445),
	// Message: The ferry from $s1 to $s2 has been delayed.
	THE_FERRY_FROM_S1_TO_S2_HAS_BEEN_DELAYED(1446),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING(1447),
	// Message: Only fishing skills may be used at this time.
	ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME(1448),
	// Message:
	SUCCEEDED_IN_GETTING_A_BITE(1449),
	// Message:REELING_FAILED_DAMAGE_S1
	TIME_IS_UP_SO_THAT_FISH_GOT_AWAY(1450),
	// Message:
	THE_FISH_GOT_AWAY(1451),
	// Message:
	BAITS_HAVE_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY(1452),
	// Message: You do not have a fishing pole equipped.
	YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED(1453),
	// Message: You must put bait on your hook before you can fish.
	YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH(1454),
	// Message: You cannot fish while under water.
	YOU_CANNOT_FISH_WHILE_UNDER_WATER(1455),
	// Message: You cannot fish while riding as a passenger of a boat - it's against the rules.
	YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT__ITS_AGAINST_THE_RULES(1456),
	// Message: You can't fish here.
	YOU_CANT_FISH_HERE(1457),
	// Message:
	CANCELS_FISHING(1458),
	// Message: You do not have enough bait.
	YOU_DO_NOT_HAVE_ENOUGH_BAIT(1459),
	// Message
	ENDS_FISHING(1460),
	// Message:
	STARTS_FISHING(1461),
	// Message: You may only use the Pumping skill while you are fishing.
	YOU_MAY_ONLY_USE_THE_PUMPING_SKILL_WHILE_YOU_ARE_FISHING(1462),
	// Message: You may only use the Reeling skill while you are fishing.
	YOU_MAY_ONLY_USE_THE_REELING_SKILL_WHILE_YOU_ARE_FISHING(1463),
	// Message:
	FISH_HAS_RESISTED(1464),
	// Message:
	PUMPING_IS_SUCCESSFUL_DAMAGE_S1(1465), PUMPING_FAILED_DAMAGE_S1(1466), REELING_IS_SUCCESSFUL_DAMAGE_S1(1467), REELING_FAILED_DAMAGE_S1(1468),
	// Message:
	SUCCEEDED_IN_FISHING(1469),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING_(1470),
	// Message: You cannot do that while fishing.
	YOU_CANNOT_DO_THAT_WHILE_FISHING_2(1471),
	// Message: You look oddly at the fishing pole in disbelief and realize that you can't attack anything with this.
	YOU_LOOK_ODDLY_AT_THE_FISHING_POLE_IN_DISBELIEF_AND_REALIZE_THAT_YOU_CANT_ATTACK_ANYTHING_WITH_THIS(1472),
	// Message: $s1 is not sufficient.
	S1_IS_NOT_SUFFICIENT(1473),
	// Message: $s1 is not available.
	S1_IS_NOT_AVAILABLE(1474),
	// Message: You pet has dropped $s1.
	YOU_PET_HAS_DROPPED_S1(1475),
	// Message: You pet has dropped +$s1$s2.
	YOU_PET_HAS_DROPPED_S1S2(1476),
	// Message: You pet has dropped $s2 of $s1.
	YOU_PET_HAS_DROPPED_S2_OF_S1(1477),
	// Message: You may only register a 64 x 64 pixel, 256-color BMP.
	YOU_MAY_ONLY_REGISTER_A_64_X_64_PIXEL_256COLOR_BMP(1478),
	// Message: That is the wrong grade of soulshot for that fishing pole.
	THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE(1479),
	// Message: Are you sure you wish to remove yourself from the Grand Olympiad waiting list?
	ARE_YOU_SURE_YOU_WISH_TO_REMOVE_YOURSELF_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST(1480),
	// Message: You have selected a class irrelevant individual match. Do you wish to participate?
	YOU_HAVE_SELECTED_A_CLASS_IRRELEVANT_INDIVIDUAL_MATCH(1481),
	// Message: You've selected to join a class specific game. Continue?
	YOUVE_SELECTED_TO_JOIN_A_CLASS_SPECIFIC_GAME(1482),
	// Message: Are you ready to become a Hero?
	ARE_YOU_READY_TO_BECOME_A_HERO(1483),
	// Message: Are you sure this is the Hero weapon you wish to use?
	ARE_YOU_SURE_THIS_IS_THE_HERO_WEAPON_YOU_WISH_TO_USE(1484),
	// Message: The ferry from Talking Island to Gludin Harbor has been delayed.
	THE_FERRY_FROM_TALKING_ISLAND_TO_GLUDIN_HARBOR_HAS_BEEN_DELAYED(1485),
	// Message: The ferry from Gludin Harbor to Talking Island has been delayed.
	THE_FERRY_FROM_GLUDIN_HARBOR_TO_TALKING_ISLAND_HAS_BEEN_DELAYED(1486),
	// Message: The ferry from Giran Harbor to Talking Island has been delayed.
	THE_FERRY_FROM_GIRAN_HARBOR_TO_TALKING_ISLAND_HAS_BEEN_DELAYED(1487),
	// Message: The ferry from Talking Island to Giran Harbor has been delayed.
	THE_FERRY_FROM_TALKING_ISLAND_TO_GIRAN_HARBOR_HAS_BEEN_DELAYED(1488),
	// Message: The Innadril cruise service has been delayed.
	THE_INNADRIL_CRUISE_SERVICE_HAS_BEEN_DELAYED(1489),
	// Message: Traded $s2 of $s1 crops.
	TRADED_S2_OF_S1_CROPS(1490),
	// Message: Failed in trading $s2 of $s1 crops.
	FAILED_IN_TRADING_S2_OF_S1_CROPS(1491),
	// Message: You will be moved to the Olympiad Stadium in $s1 second(s).
	YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECONDS(1492),
	// Message: Your opponent made haste with their tail between their legs; the match has been cancelled.
	YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED(1493),
	// Message: Your opponent does not meet the requirements to do battle; the match has been cancelled.
	YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED(1494),
	// Message: The match will start in $s1 second(s).
	THE_MATCH_WILL_START_IN_S1_SECONDS(1495),
	// Message: The match has started. Fight!
	THE_MATCH_HAS_STARTED(1496),
	// Message: Congratulations, $c1! You win the match!
	CONGRATULATIONS_C1_YOU_WIN_THE_MATCH(1497),
	// Message: There is no victor; the match ends in a tie.
	THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE(1498),
	// Message: You will be moved back to town in $s1 second(s).
	YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECONDS(1499),
	// Message: $c1 does not meet the participation requirements. A subclass character cannot participate in the Olympiad.
	C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_SUBCLASS_CHARACTER_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD(1500),
	// Message: $c1 does not meet the participation requirements. Only Noblesse characters can participate in the Olympiad.
	C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_NOBLESSE_CHARACTERS_CAN_PARTICIPATE_IN_THE_OLYMPIAD(1501),
	// Message: $c1 is already registered on the match waiting list.
	C1_IS_ALREADY_REGISTERED_ON_THE_MATCH_WAITING_LIST(1502),
	// Message: You have been registered for the Grand Olympiad waiting list for a class specific match.
	YOU_HAVE_BEEN_REGISTERED_FOR_THE_GRAND_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_SPECIFIC_MATCH(1503),
	// Message: You are currently registered for a 1v1 class irrelevant match.
	YOU_ARE_CURRENTLY_REGISTERED_FOR_A_1V1_CLASS_IRRELEVANT_MATCH(1504),
	// Message: You have been removed from the Grand Olympiad waiting list.
	YOU_HAVE_BEEN_REMOVED_FROM_THE_GRAND_OLYMPIAD_WAITING_LIST(1505),
	// Message: You are not currently registered for the Grand Olympiad.
	YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_GRAND_OLYMPIAD(1506),
	// Message: You cannot equip that item in a Grand Olympiad match.
	YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH(1507),
	// Message: You cannot use that item in a Grand Olympiad match.
	YOU_CANNOT_USE_THAT_ITEM_IN_A_GRAND_OLYMPIAD_MATCH(1508),
	// Message: You cannot use that skill in a Grand Olympiad match.
	YOU_CANNOT_USE_THAT_SKILL_IN_A_GRAND_OLYMPIAD_MATCH(1509),
	// Message: $c1 is making an attempt to resurrect you. If you choose this path, $s2 experience points will be returned to you. Do you want to be resurrected?
	C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU(1510),
	// Message: While a pet is being resurrected, it cannot help in resurrecting its master.
	WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER(1511),
	// Message: You cannot resurrect a pet while their owner is being resurrected.
	YOU_CANNOT_RESURRECT_A_PET_WHILE_THEIR_OWNER_IS_BEING_RESURRECTED(1512),
	// Message: Resurrection has already been proposed.
	RESURRECTION_HAS_ALREADY_BEEN_PROPOSED(1513),
	// Message: You cannot resurrect the owner of a pet while their pet is being resurrected.
	YOU_CANNOT_RESURRECT_THE_OWNER_OF_A_PET_WHILE_THEIR_PET_IS_BEING_RESURRECTED(1514),
	// Message: A pet cannot be resurrected while it's owner is in the process of resurrecting.
	A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING(1515),
	// Message: The target is unavailable for seeding.
	THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING(1516),
	// Message: The Blessed Enchant failed. The enchant value of the item became 0.
	THE_BLESSED_ENCHANT_FAILED(1517),
	// Message:
	YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM(1518),
	// Message: You should release your pet or servitor so that it does not fall off of the boat and drown!
	YOU_SHOULD_RELEASE_YOUR_PET_OR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN(1523),
	// Message: $c1's pet gained $s2.
	C1S_PET_GAINED_S2(1524),
	// Message: $c1's pet gained $s3 of $s2.
	C1S_PET_GAINED_S3_OF_S2(1525),
	// Message: $c1's pet gained +$s2$s3.
	C1S_PET_GAINED_S2S3(1526),
	// Message: Your pet was hungry so it ate $s1.
	YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1(1527),
	// Message: You've sent a petition to the GM staff.
	YOUVE_SENT_A_PETITION_TO_THE_GM_STAFF(1528),
	// Message: $c1 is inviting you to a Command Channel. Do you accept?
	C1_IS_INVITING_YOU_TO_A_COMMAND_CHANNEL(1529),
	// Message: Select a target or enter the name.
	SELECT_A_TARGET_OR_ENTER_THE_NAME(1530),
	// Message: Enter the name of the clan that you wish to declare war on.
	ENTER_THE_NAME_OF_THE_CLAN_THAT_YOU_WISH_TO_DECLARE_WAR_ON(1531),
	// Message: Enter the name of the clan that you wish to request a cease-fire with.
	ENTER_THE_NAME_OF_THE_CLAN_THAT_YOU_WISH_TO_REQUEST_A_CEASEFIRE_WITH(1532),
	// Message: Attention: $c1 has picked up $s2.
	ATTENTION_C1_HAS_PICKED_UP_S2(1533),
	// Message: Attention: $c1 has picked up +$s2$s3.
	ATTENTION_C1_HAS_PICKED_UP_S2S3(1534),
	// Message: Attention: $c1's pet has picked up $s2.
	ATTENTION_C1S_PET_HAS_PICKED_UP_S2(1535),
	// Message: Attention: $c1's pet has picked up +$s2$s3.
	ATTENTION_C1S_PET_HAS_PICKED_UP_S2S3(1536),
	// Message: Current Location: $s1, $s2, $s3 (near Rune Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_RUNE_VILLAGE(1537),
	// Message: Current Location: $s1, $s2, $s3 (near the Town of Goddard)
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GODDARD(1538),
	// Message: Cargo has arrived at Talking Island Village.
	CARGO_HAS_ARRIVED_AT_TALKING_ISLAND_VILLAGE(1539),
	// Message: Cargo has arrived at the Dark Elf Village.
	CARGO_HAS_ARRIVED_AT_THE_DARK_ELF_VILLAGE(1540),
	// Message: Cargo has arrived at Elven Village.
	CARGO_HAS_ARRIVED_AT_ELVEN_VILLAGE(1541),
	// Message: Cargo has arrived at Orc Village.
	CARGO_HAS_ARRIVED_AT_ORC_VILLAGE(1542),
	// Message: Cargo has arrived at Dwarven Village.
	CARGO_HAS_ARRIVED_AT_DWARVEN_VILLAGE(1543),
	// Message: Cargo has arrived at Aden Castle Town.
	CARGO_HAS_ARRIVED_AT_ADEN_CASTLE_TOWN(1544),
	// Message: Cargo has arrived at the Town of Oren.
	CARGO_HAS_ARRIVED_AT_THE_TOWN_OF_OREN(1545),
	// Message: Cargo has arrived at Hunters Village.
	CARGO_HAS_ARRIVED_AT_HUNTERS_VILLAGE(1546),
	// Message: Cargo has arrived at the Town of Dion.
	CARGO_HAS_ARRIVED_AT_THE_TOWN_OF_DION(1547),
	// Message: Cargo has arrived at Floran Village.
	CARGO_HAS_ARRIVED_AT_FLORAN_VILLAGE(1548),
	// Message: Cargo has arrived at Gludin Village.
	CARGO_HAS_ARRIVED_AT_GLUDIN_VILLAGE(1549),
	// Message: Cargo has arrived at the Town of Gludio.
	CARGO_HAS_ARRIVED_AT_THE_TOWN_OF_GLUDIO(1550),
	// Message: Cargo has arrived at Giran Castle Town.
	CARGO_HAS_ARRIVED_AT_GIRAN_CASTLE_TOWN(1551),
	// Message: Cargo has arrived at Heine.
	CARGO_HAS_ARRIVED_AT_HEINE(1552),
	// Message: Cargo has arrived at Rune Village.
	CARGO_HAS_ARRIVED_AT_RUNE_VILLAGE(1553),
	// Message: Cargo has arrived at the Town of Goddard.
	CARGO_HAS_ARRIVED_AT_THE_TOWN_OF_GODDARD(1554),
	// Message: Do you want to cancel character deletion?
	DO_YOU_WANT_TO_CANCEL_CHARACTER_DELETION(1555),
	// Message: Your clan notice has been saved.
	YOUR_CLAN_NOTICE_HAS_BEEN_SAVED(1556),
	// Message: Seed price should be more than $s1 and less than $s2.
	SEED_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1557),
	// Message: The seed quantity should be more than $s1 and less than $s2.
	THE_SEED_QUANTITY_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1558),
	// Message: Crop price should be more than $s1 and less than $s2.
	CROP_PRICE_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2(1559),
	// Message: The crop quantity should be more than $s1 and less than $s2 .
	THE_CROP_QUANTITY_SHOULD_BE_MORE_THAN_S1_AND_LESS_THAN_S2_(1560),
	// Message: $s1 has declared a Clan War.
	S1_HAS_DECLARED_A_CLAN_WAR(1561),
	// Message: A Clan War has been declared against the clan, $s1. If you are killed during the Clan War by members of the opposing clan, you will only lose a quarter of the normal
	// experience from death.
	A_CLAN_WAR_HAS_BEEN_DECLARED_AGAINST_THE_CLAN_S1(1562),
	// Message: The clan, $s1, cannot declare a clan war because their clan is level 2 or lower, and or they do not have enough members.
	THE_CLAN_S1_CANNOT_DECLARE_A_CLAN_WAR_BECAUSE_THEIR_CLAN_IS_LEVEL_2_OR_LOWER_AND_OR_THEY_DO_NOT_HAVE_ENOUGH_MEMBERS(1563),
	// Message: A clan war can only be declared if the clan is level 3 or above, and the number of clan members is fifteen or greater.
	A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_FIFTEEN_OR_GREATER(1564),
	// Message: A clan war cannot be declared against a clan that does not exist!
	A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST(1565),
	// Message: The clan, $s1, has decided to stop the war.
	THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR(1566),
	// Message: The war against $s1 Clan has been stopped.
	THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED(1567),
	// Message: The target for declaration is wrong.
	THE_TARGET_FOR_DECLARATION_IS_WRONG(1568),
	// Message: A declaration of Clan War against an allied clan can't be made.
	A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CANT_BE_MADE(1569),
	// Message: A declaration of war against more than 30 Clans can't be made at the same time.
	A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CANT_BE_MADE_AT_THE_SAME_TIME(1570),
	// Message: ======<Clans You've Declared War On>======
	CLANS_YOUVE_DECLARED_WAR_ON(1571),
	// Message: ======<Clans That Have Declared War On You>======
	CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU(1572),
	// Message: All is well. There are no clans that have declared war against your clan.
	ALL_IS_WELL(1573),
	// Message: Command Channels can only be formed by a party leader who is also the leader of a level 5 clan.
	COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN(1574),
	// Message: Your pet uses spiritshot.
	YOUR_PET_USES_SPIRITSHOT(1575),
	// Message: Your servitor uses spiritshot.
	YOUR_SERVITOR_USES_SPIRITSHOT(1576),
	// Message: Servitor uses the power of spirit.
	SERVITOR_USES_THE_POWER_OF_SPIRIT(1577),
	// Message: Items are not available for a private store or private manufacture.
	ITEMS_ARE_NOT_AVAILABLE_FOR_A_PRIVATE_STORE_OR_PRIVATE_MANUFACTURE(1578),
	// Message: $c1's pet gained $s2 adena.
	C1S_PET_GAINED_S2_ADENA(1579),
	// Message: The Command Channel has been formed.
	THE_COMMAND_CHANNEL_HAS_BEEN_FORMED(1580),
	// Message: The Command Channel has been disbanded.
	THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED(1581),
	// Message: You have joined the Command Channel.
	YOU_HAVE_JOINED_THE_COMMAND_CHANNEL(1582),
	// Message: You were dismissed from the Command Channel.
	YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL(1583),
	// Message: $c1's party has been dismissed from the Command Channel.
	C1S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL(1584),
	// Message: The Command Channel has been disbanded.
	THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED_(1585),
	// Message: You have quit the Command Channel.
	YOU_HAVE_QUIT_THE_COMMAND_CHANNEL(1586),
	// Message: $c1's party has left the Command Channel.
	C1S_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL(1587),
	// Message: The Command Channel is activated only when there are at least 5 parties participating.
	THE_COMMAND_CHANNEL_IS_ACTIVATED_ONLY_WHEN_THERE_ARE_AT_LEAST_5_PARTIES_PARTICIPATING(1588),
	// Message: Command Channel authority has been transferred to $c1.
	COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1(1589),
	// Message: ===<Guild Info (Total Parties: $s1)>===
	GUILD_INFO_TOTAL_PARTIES_S1(1590),
	// Message: No user has been invited to the Command Channel.
	NO_USER_HAS_BEEN_INVITED_TO_THE_COMMAND_CHANNEL(1591),
	// Message: You can no longer set up a Command Channel.
	YOU_CAN_NO_LONGER_SET_UP_A_COMMAND_CHANNEL(1592),
	// Message: You do not have authority to invite someone to the Command Channel.
	YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL(1593),
	// Message: $c1's party is already a member of the Command Channel.
	C1S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL(1594),
	// Message: $s1 has succeeded.
	S1_HAS_SUCCEEDED(1595),
	// Message: You were hit by $s1!
	YOU_WERE_HIT_BY_S1(1596),
	// Message: $s1 has failed.
	S1_HAS_FAILED(1597),
	// Message: Soulshots and spiritshots are not available for a dead pet or servitor. Sad, isn't it?
	SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_PET_OR_SERVITOR(1598),
	// Message: You cannot "observe" while you are in combat!
	YOU_CANNOT_OBSERVE_WHILE_YOU_ARE_IN_COMBAT(1599),
	// Message: Tomorrow's items will ALL be set to 0. Do you wish to continue?
	TOMORROWS_ITEMS_WILL_ALL_BE_SET_TO_0(1600),
	// Message: Tomorrow's items will all be set to the same value as today's items. Do you wish to continue?
	TOMORROWS_ITEMS_WILL_ALL_BE_SET_TO_THE_SAME_VALUE_AS_TODAYS_ITEMS(1601),
	// Message: Only a party leader can access the Command Channel.
	ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL(1602),
	// Message: Only the Command Channel creator can use the Raid Leader text.
	ONLY_THE_COMMAND_CHANNEL_CREATOR_CAN_USE_THE_RAID_LEADER_TEXT(1603),
	// Message: While dressed in formal wear, you can't use items that require all skills and casting operations.
	WHILE_DRESSED_IN_FORMAL_WEAR_YOU_CANT_USE_ITEMS_THAT_REQUIRE_ALL_SKILLS_AND_CASTING_OPERATIONS(1604),
	// Message: * Here, you can buy only seeds of $s1 Manor.
	_HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR(1605),
	// Message: Congratulations - You've completed your third-class transfer quest!
	CONGRATULATIONS__YOUVE_COMPLETED_YOUR_THIRDCLASS_TRANSFER_QUEST(1606),
	// Message: $s1 adena has been withdrawn to pay for purchasing fees.
	S1_ADENA_HAS_BEEN_WITHDRAWN_TO_PAY_FOR_PURCHASING_FEES(1607),
	// Message: Due to insufficient adena you cannot buy another castle.
	DUE_TO_INSUFFICIENT_ADENA_YOU_CANNOT_BUY_ANOTHER_CASTLE(1608),
	// Message: War has already been declared against that clan… but I'll make note that you really don't like them.
	WAR_HAS_ALREADY_BEEN_DECLARED_AGAINST_THAT_CLAN_BUT_ILL_MAKE_NOTE_THAT_YOU_REALLY_DONT_LIKE_THEM(1609),
	// Message: Fool! You cannot declare war against your own clan!
	FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN(1610),
	// Message: Party Leader: $c1
	PARTY_LEADER_C1(1611),
	// Message: =====<War List>=====
	WAR_LIST(1612),
	// Message: There is no clan listed on your War List.
	THERE_IS_NO_CLAN_LISTED_ON_YOUR_WAR_LIST(1613),
	// Message: You have joined a channel that was already open.
	YOU_HAVE_JOINED_A_CHANNEL_THAT_WAS_ALREADY_OPEN(1614),
	// Message: The number of remaining parties is $s1 until a channel is activated.
	THE_NUMBER_OF_REMAINING_PARTIES_IS_S1_UNTIL_A_CHANNEL_IS_ACTIVATED(1615),
	// Message: The Command Channel has been activated.
	THE_COMMAND_CHANNEL_HAS_BEEN_ACTIVATED(1616),
	// Message: You do not have the authority to use the Command Channel.
	YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL(1617),
	// Message: The ferry from Rune Harbor to Gludin Harbor has been delayed.
	THE_FERRY_FROM_RUNE_HARBOR_TO_GLUDIN_HARBOR_HAS_BEEN_DELAYED(1618),
	// Message: The ferry from Gludin Harbor to Rune Harbor has been delayed.
	THE_FERRY_FROM_GLUDIN_HARBOR_TO_RUNE_HARBOR_HAS_BEEN_DELAYED(1619),
	// Message: Welcome to Rune Harbor.
	WELCOME_TO_RUNE_HARBOR(1620),
	// Message: Departure for Gludin Harbor will take place in five minutes!
	DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_FIVE_MINUTES(1621),
	// Message: Departure for Gludin Harbor will take place in one minute!
	DEPARTURE_FOR_GLUDIN_HARBOR_WILL_TAKE_PLACE_IN_ONE_MINUTE(1622),
	// Message: Make haste! We will be departing for Gludin Harbor shortly…
	MAKE_HASTE__WE_WILL_BE_DEPARTING_FOR_GLUDIN_HARBOR_SHORTLY(1623),
	// Message: We are now departing for Gludin Harbor. Hold on and enjoy the ride!
	WE_ARE_NOW_DEPARTING_FOR_GLUDIN_HARBOR(1624),
	// Message: Departure for Rune Harbor will take place after anchoring for ten minutes.
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_AFTER_ANCHORING_FOR_TEN_MINUTES(1625),
	// Message: Departure for Rune Harbor will take place in five minutes!
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_IN_FIVE_MINUTES(1626),
	// Message: Departure for Rune Harbor will take place in one minute!
	DEPARTURE_FOR_RUNE_HARBOR_WILL_TAKE_PLACE_IN_ONE_MINUTE(1627),
	// Message: Make haste! We will be departing for Gludin Harbor shortly…
	MAKE_HASTE__WE_WILL_BE_DEPARTING_FOR_GLUDIN_HARBOR_SHORTLY_(1628),
	// Message: We are now departing for Rune Harbor. Hold on and enjoy the ride!
	WE_ARE_NOW_DEPARTING_FOR_RUNE_HARBOR(1629),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 15 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_15_MINUTES(1630),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 10 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_10_MINUTES(1631),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 5 minutes.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_5_MINUTES(1632),
	// Message: The ferry from Rune Harbor will be arriving at Gludin Harbor in approximately 1 minute.
	THE_FERRY_FROM_RUNE_HARBOR_WILL_BE_ARRIVING_AT_GLUDIN_HARBOR_IN_APPROXIMATELY_1_MINUTE(1633),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 15 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_15_MINUTES(1634),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 10 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_10_MINUTES(1635),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 5 minutes.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_5_MINUTES(1636),
	// Message: The ferry from Gludin Harbor will be arriving at Rune Harbor in approximately 1 minute.
	THE_FERRY_FROM_GLUDIN_HARBOR_WILL_BE_ARRIVING_AT_RUNE_HARBOR_IN_APPROXIMATELY_1_MINUTE(1637),
	// Message: You cannot fish while using a recipe book, private manufacture or private store.
	YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE(1638),
	// Message: Round $s1 of the Grand Olympiad Games has started!
	ROUND_S1_OF_THE_GRAND_OLYMPIAD_GAMES_HAS_STARTED(1639),
	// Message: Round $s1 of the Grand Olympiad Games has now ended.
	ROUND_S1_OF_THE_GRAND_OLYMPIAD_GAMES_HAS_NOW_ENDED(1640),
	// Message: Sharpen your swords, tighten the stitching in your armor, and make haste to a Grand Olympiad Manager! Battles in the Grand Olympiad Games are now taking place!
	SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_GRAND_OLYMPIAD_MANAGER__BATTLES_IN_THE_GRAND_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE(1641),
	// Message: Much carnage has been left for the cleanup crew of the Olympiad Stadium. Battles in the Grand Olympiad Games are now over!
	MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM(1642),
	// Message: Current Location: $s1, $s2, $s3 (Dimensional Gap)
	CURRENT_LOCATION_S1_S2_S3_DIMENSIONAL_GAP(1643),
	// Message: Play time is now accumulating.
	PLAY_TIME_IS_NOW_ACCUMULATING(1649),
	// Message: Due to high server traffic, your login attempt has failed. Please try again soon.
	DUE_TO_HIGH_SERVER_TRAFFIC_YOUR_LOGIN_ATTEMPT_HAS_FAILED(1650),
	// Message: The Grand Olympiad Games are not currently in progress.
	THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS(1651),
	// Message:
	YOU_HAVE_CAUGHT_A_MONSTER(1655),
	// Message: $c1 has earned $s2 points in the Grand Olympiad Games.
	C1_HAS_EARNED_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES(1657),
	// Message: $c1 has lost $s2 points in the Grand Olympiad Games.
	C1_HAS_LOST_S2_POINTS_IN_THE_GRAND_OLYMPIAD_GAMES(1658),
	// Message: Current Location: $s1, $s2, $s3 (Cemetery of the Empire).
	CURRENT_LOCATION_S1_S2_S3_CEMETERY_OF_THE_EMPIRE(1659),
	// Message: Channel Creator: $c1
	CHANNEL_CREATOR_C1(1660),
	// Message: $c1 has obtained $s3 $s2s.
	C1_HAS_OBTAINED_S3_S2S(1661),
	// Message: The fish are no longer biting here because you've caught too many! Try fishing in another location.
	THE_FISH_ARE_NO_LONGER_BITING_HERE_BECAUSE_YOUVE_CAUGHT_TOO_MANY__TRY_FISHING_IN_ANOTHER_LOCATION(1662),
	// Message: The clan crest was successfully registered. Remember, only a clan that owns a clan hall or castle can display a crest.
	THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED(1663),
	// Message: The fish is resisting your efforts to haul it in! Look at that bobber go!
	THE_FISH_IS_RESISTING_YOUR_EFFORTS_TO_HAUL_IT_IN__LOOK_AT_THAT_BOBBER_GO(1664),
	// Message: You've worn that fish out! It can't even pull the bobber under the water!
	YOUVE_WORN_THAT_FISH_OUT__IT_CANT_EVEN_PULL_THE_BOBBER_UNDER_THE_WATER(1665),
	// Message: You have obtained +$s1$s2.
	YOU_HAVE_OBTAINED_S1S2(1666),
	// Message: Lethal Strike!
	LETHAL_STRIKE(1667),
	// Message: Your lethal strike was successful!
	YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL(1668),
	// Message: There was nothing found inside.
	THERE_WAS_NOTHING_FOUND_INSIDE(1669),
	// Message: Due to your Reeling and/or Pumping skill being three or more levels higher than your Fishing skill, a 50 damage penalty will be applied.
	DUE_TO_YOUR_REELING_ANDOR_PUMPING_SKILL_BEING_THREE_OR_MORE_LEVELS_HIGHER_THAN_YOUR_FISHING_SKILL_A_50_DAMAGE_PENALTY_WILL_BE_APPLIED(1670),
	// Message:
	YOUR_REELING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_(1671),
	// Message:
	YOUR_PUMPING_WAS_SUCCESSFUL_MASTERY_PENALTYS1_(1672),
	// Message: For the current Grand Olympiad you have participated in $s1 match(es). $s2 win(s) and $s3 defeat(s). You currently have $s4 Olympiad Point(s).
	FOR_THE_CURRENT_GRAND_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCHES_S2_WINS_S3_DEFEATS_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINTS(1673),
	// Message: This command can only be used by a Noblesse.
	THIS_COMMAND_CAN_ONLY_BE_USED_BY_A_NOBLESSE(1674),
	// Message: A manor cannot be set up between 4:30 am and 8 pm.
	A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM(1675),
	// Message: You do not have a servitor or pet and therefore cannot use the automatic-use function.
	YOU_DO_NOT_HAVE_A_SERVITOR_OR_PET_AND_THEREFORE_CANNOT_USE_THE_AUTOMATICUSE_FUNCTION(1676),
	// Message: A cease-fire during a Clan War can not be called while members of your clan are engaged in battle.
	A_CEASEFIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE(1677),
	// Message: You have not declared a Clan War against the clan $s1.
	YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1(1678),
	// Message: Only the creator of a command channel can issue a global command.
	ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_ISSUE_A_GLOBAL_COMMAND(1679),
	// Message: $c1 has declined the channel invitation.
	C1_HAS_DECLINED_THE_CHANNEL_INVITATION(1680),
	// Message: Since $c1 did not respond, your channel invitation has failed.
	SINCE_C1_DID_NOT_RESPOND_YOUR_CHANNEL_INVITATION_HAS_FAILED(1681),
	// Message: Only the creator of a command channel can use the channel dismiss command.
	ONLY_THE_CREATOR_OF_A_COMMAND_CHANNEL_CAN_USE_THE_CHANNEL_DISMISS_COMMAND(1682),
	// Message: Only a party leader can leave a command channel.
	ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL(1683),
	// Message: A Clan War can not be declared against a clan that is being dissolved.
	A_CLAN_WAR_CAN_NOT_BE_DECLARED_AGAINST_A_CLAN_THAT_IS_BEING_DISSOLVED(1684),
	// Message: You are unable to equip this item when your PK count is greater than or equal to one.
	YOU_ARE_UNABLE_TO_EQUIP_THIS_ITEM_WHEN_YOUR_PK_COUNT_IS_GREATER_THAN_OR_EQUAL_TO_ONE(1685),
	// Message: Stones and mortar tumble to the earth - the castle wall has taken damage!
	STONES_AND_MORTAR_TUMBLE_TO_THE_EARTH__THE_CASTLE_WALL_HAS_TAKEN_DAMAGE(1686),
	// Message: This area cannot be entered while mounted atop of a Wyvern. You will be dismounted from your Wyvern if you do not leave!
	THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN(1687),
	// Message: You cannot enchant while operating a Private Store or Private Workshop.
	YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(1688),
	// Message: $c1 is already registered on the class match waiting list.
	C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST(1689),
	// Message: $c1 is already registered on the waiting list for the class irrelevant individual match.
	C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_CLASS_IRRELEVANT_INDIVIDUAL_MATCH(1690),
	// Message: You may not observe a Grand Olympiad Games match while you are on the waiting list.
	YOU_MAY_NOT_OBSERVE_A_GRAND_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST(1693),
	// Message: Only a clan leader that is a Noblesse can view the Siege War Status window during a siege war.
	ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_CAN_VIEW_THE_SIEGE_WAR_STATUS_WINDOW_DURING_A_SIEGE_WAR(1694),
	// Message: You can only use that during a Siege War!
	YOU_CAN_ONLY_USE_THAT_DURING_A_SIEGE_WAR(1695),
	// Message: Your accumulated play time is $s1.
	YOUR_ACCUMULATED_PLAY_TIME_IS_S1(1696),
	// Message: Your accumulated play time has reached Fatigue level, so you will receive experience or item drops at only 50 percent of the normal rate. For the sake of you physical
	// and emotional health, we encourage you to log out as soon as possible and take a break before returning.
	YOUR_ACCUMULATED_PLAY_TIME_HAS_REACHED_FATIGUE_LEVEL_SO_YOU_WILL_RECEIVE_EXPERIENCE_OR_ITEM_DROPS_AT_ONLY_50_PERCENT_OF_THE_NORMAL_RATE(1697),
	// Message: Your accumulated play time has reached Ill-health level, so you will no longer gain experience or item drops. For the sake of your physical and emotional health, please
	// log out as soon as possible and take a break. Once you have been logged out for at least 5 hours, the experience and item drop rate penalties will be removed.
	YOUR_ACCUMULATED_PLAY_TIME_HAS_REACHED_ILLHEALTH_LEVEL_SO_YOU_WILL_NO_LONGER_GAIN_EXPERIENCE_OR_ITEM_DROPS(1698),
	// Message: You cannot dismiss a party member by force.
	YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE(1699),
	// Message: You don't have enough spiritshots needed for a pet/servitor.
	YOU_DONT_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_PETSERVITOR(1700),
	// Message: You don't have enough soulshots needed for a pet/servitor.
	YOU_DONT_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_PETSERVITOR(1701),
	// Message: $s1 is using a third party program.
	S1_IS_USING_A_THIRD_PARTY_PROGRAM(1702),
	// Message: The previously investigated user is not using a third party program.
	THE_PREVIOUSLY_INVESTIGATED_USER_IS_NOT_USING_A_THIRD_PARTY_PROGRAM(1703),
	// Message: Please close the setup window for your private manufacturing store or private store, and try again.
	PLEASE_CLOSE_THE__SETUP_WINDOW_FOR_YOUR_PRIVATE_MANUFACTURING_STORE_OR_PRIVATE_STORE_AND_TRY_AGAIN(1704),
	// Message: PC Bang Points acquisition period. Points acquisition period left $s1 hour.
	PC_BANG_POINTS_ACQUISITION_PERIOD(1705),
	// Message: PC Bang Points use period. Points use period left $s1 hour.
	PC_BANG_POINTS_USE_PERIOD(1706),
	// Message: You acquired $s1 PC Bang Point.
	YOU_ACQUIRED_S1_PC_BANG_POINT(1707),
	// Message: Double points! You acquired $s1 PC Bang Point.
	DOUBLE_POINTS_YOU_ACQUIRED_S1_PC_BANG_POINT(1708),
	// Message: You are using $s1 point.
	YOU_ARE_USING_S1_POINT(1709),
	// Message: You are short of accumulated points.
	YOU_ARE_SHORT_OF_ACCUMULATED_POINTS(1710),
	// Message: PC Bang Points use period has expired.
	PC_BANG_POINTS_USE_PERIOD_HAS_EXPIRED(1711),
	// Message: The PC Bang Points accumulation period has expired.
	THE_PC_BANG_POINTS_ACCUMULATION_PERIOD_HAS_EXPIRED(1712),
	// Message: The games may be delayed due to an insufficient number of players waiting.
	THE_GAMES_MAY_BE_DELAYED_DUE_TO_AN_INSUFFICIENT_NUMBER_OF_PLAYERS_WAITING(1713),
	// Message: Current Location: $s1, $s2, $s3 (Near the Town of Schuttgart)
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_SCHUTTGART(1714),
	// Message: This is a Peaceful Zone\n- PvP is not allowed in this area.
	THIS_IS_A_PEACEFUL_ZONEN_PVP_IS_NOT_ALLOWED_IN_THIS_AREA(1715),
	// Message: Altered Zone
	ALTERED_ZONE(1716),
	// Message: Siege War Zone \n- A siege is currently in progress in this area. \n If a character dies in this zone, their resurrection ability may be restricted.
	SIEGE_WAR_ZONE_N_A_SIEGE_IS_CURRENTLY_IN_PROGRESS_IN_THIS_AREA(1717),
	// Message: General Field
	GENERAL_FIELD(1718),
	// Message: Seven Signs Zone \n- Although a character's level may increase while in this area, HP and MP \n will not be regenerated.
	SEVEN_SIGNS_ZONE_N_ALTHOUGH_A_CHARACTERS_LEVEL_MAY_INCREASE_WHILE_IN_THIS_AREA_HP_AND_MP_N_WILL_NOT_BE_REGENERATED(1719),
	// Message: Combat Zone
	COMBAT_ZONE(1721),
	// Message: Please enter the name of the item you wish to search for.
	PLEASE_ENTER_THE_NAME_OF_THE_ITEM_YOU_WISH_TO_SEARCH_FOR(1722),
	// Message: Please take a moment to provide feedback about the petition service.
	PLEASE_TAKE_A_MOMENT_TO_PROVIDE_FEEDBACK_ABOUT_THE_PETITION_SERVICE(1723),
	// Message: A servitor whom is engaged in battle cannot be de-activated.
	A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DEACTIVATED(1724),
	// Message: You have earned $s1 raid point(s).
	YOU_HAVE_EARNED_S1_RAID_POINTS(1725),
	// Message: $s1 has disappeared because its time period has expired.
	S1_HAS_DISAPPEARED_BECAUSE_ITS_TIME_PERIOD_HAS_EXPIRED(1726),
	// Message: $s1 has invited you to room <$s2>. Do you wish to accept?
	S1_HAS_INVITED_YOU_TO_ROOM_S2(1727),
	// Message: The recipient of your invitation did not accept the party matching invitation.
	THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION(1728),
	// Message: You cannot join a Command Channel while teleporting.
	YOU_CANNOT_JOIN_A_COMMAND_CHANNEL_WHILE_TELEPORTING(1729),
	// Message: To establish a Clan Academy, your clan must be Level 5 or higher.
	TO_ESTABLISH_A_CLAN_ACADEMY_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER(1730),
	// Message: Only the clan leader can create a Clan Academy.
	ONLY_THE_CLAN_LEADER_CAN_CREATE_A_CLAN_ACADEMY(1731),
	// Message: To create a Clan Academy, a Blood Mark is needed.
	TO_CREATE_A_CLAN_ACADEMY_A_BLOOD_MARK_IS_NEEDED(1732),
	// Message: You do not have enough adena to create a Clan Academy.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_CREATE_A_CLAN_ACADEMY(1733),
	// Message: To join a Clan Academy, characters must be Level 40 or below, not belong another clan and not yet completed their 2nd class transfer.
	TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER(1734),
	// Message: $s1 does not meet the requirements to join a Clan Academy.
	S1_DOES_NOT_MEET_THE_REQUIREMENTS_TO_JOIN_A_CLAN_ACADEMY(1735),
	// Message: The Clan Academy has reached its maximum enrollment.
	THE_CLAN_ACADEMY_HAS_REACHED_ITS_MAXIMUM_ENROLLMENT(1736),
	// Message: Your clan has not established a Clan Academy but is eligible to do so.
	YOUR_CLAN_HAS_NOT_ESTABLISHED_A_CLAN_ACADEMY_BUT_IS_ELIGIBLE_TO_DO_SO(1737),
	// Message: Your clan has already established a Clan Academy.
	YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY(1738),
	// Message: Would you like to create a Clan Academy?
	WOULD_YOU_LIKE_TO_CREATE_A_CLAN_ACADEMY(1739),
	// Message: Please enter the name of the Clan Academy.
	PLEASE_ENTER_THE_NAME_OF_THE_CLAN_ACADEMY(1740),
	// Message: Congratulations! The $s1's Clan Academy has been created.
	CONGRATULATIONS_THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED(1741),
	// Message: A message inviting $s1 to join the Clan Academy is being sent.
	A_MESSAGE_INVITING_S1_TO_JOIN_THE_CLAN_ACADEMY_IS_BEING_SENT(1742),
	// Message: To open a Clan Academy, the leader of a Level 5 clan or above must pay XX Proofs of Blood or a certain amount of adena.
	TO_OPEN_A_CLAN_ACADEMY_THE_LEADER_OF_A_LEVEL_5_CLAN_OR_ABOVE_MUST_PAY_XX_PROOFS_OF_BLOOD_OR_A_CERTAIN_AMOUNT_OF_ADENA(1743),
	// Message: There was no response to your invitation to join the Clan Academy, so the invitation has been rescinded.
	THERE_WAS_NO_RESPONSE_TO_YOUR_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_SO_THE_INVITATION_HAS_BEEN_RESCINDED(1744),
	// Message: The recipient of your invitation to join the Clan Academy has declined.
	THE_RECIPIENT_OF_YOUR_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_HAS_DECLINED(1745),
	// Message: You have already joined a Clan Academy.
	YOU_HAVE_ALREADY_JOINED_A_CLAN_ACADEMY(1746),
	// Message: $s1 has sent you an invitation to join the Clan Academy belonging to the $s2 clan. Do you accept?
	S1_HAS_SENT_YOU_AN_INVITATION_TO_JOIN_THE_CLAN_ACADEMY_BELONGING_TO_THE_S2_CLAN(1747),
	// Message: Clan Academy member $s1 has successfully completed the 2nd class transfer and obtained $s2 Clan Reputation points.
	CLAN_ACADEMY_MEMBER_S1_HAS_SUCCESSFULLY_COMPLETED_THE_2ND_CLASS_TRANSFER_AND_OBTAINED_S2_CLAN_REPUTATION_POINTS(1748),
	// Message: Congratulations! You will now graduate from the Clan Academy and leave your current clan. As a graduate of the academy, you can immediately join a clan as a regular
	// member without being subject to any penalties.
	CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN(1749),
	// Message: $c1 does not meet the participation requirements. The owner of $s2 cannot participate in the Olympiad.
	C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS____(1750),
	// Message: The Grand Master has given you a commemorative item.
	THE_GRAND_MASTER_HAS_GIVEN_YOU_A_COMMEMORATIVE_ITEM(1751),
	// Message: Since the clan has received a graduate of the Clan Academy, it has earned $s1 points toward its reputation score.
	SINCE_THE_CLAN_HAS_RECEIVED_A_GRADUATE_OF_THE_CLAN_ACADEMY_IT_HAS_EARNED_S1_POINTS_TOWARD_ITS_REPUTATION_SCORE(1752),
	// Message: The clan leader has decreed that that particular privilege cannot be granted to a Clan Academy member.
	THE_CLAN_LEADER_HAS_DECREED_THAT_THAT_PARTICULAR_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER(1753),
	// Message: That privilege cannot be granted to a Clan Academy member.
	THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER(1754),
	// Message: $s2 has been designated as the apprentice of clan member $s1.
	S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1(1755),
	// Message: Your apprentice, $c1, has logged in.
	YOUR_APPRENTICE_C1_HAS_LOGGED_IN(1756),
	// Message: Your apprentice, $c1, has logged out.
	YOUR_APPRENTICE_C1_HAS_LOGGED_OUT(1757),
	// Message: Your sponsor, $c1, has logged in.
	YOUR_SPONSOR_C1_HAS_LOGGED_IN(1758),
	// Message: Your sponsor, $c1, has logged out.
	YOUR_SPONSOR_C1_HAS_LOGGED_OUT(1759),
	// Message: Clan member $c1's title has been changed to $s2.
	CLAN_MEMBER_C1S_TITLE_HAS_BEEN_CHANGED_TO_S2(1760),
	// Message: Clan member $c1's privilege level has been changed to $s2.
	CLAN_MEMBER_C1S_PRIVILEGE_LEVEL_HAS_BEEN_CHANGED_TO_S2(1761),
	// Message: You do not have the right to dismiss an apprentice.
	YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE(1762),
	// Message: $s2, clan member $c1's apprentice, has been removed.
	S2_CLAN_MEMBER_C1S_APPRENTICE_HAS_BEEN_REMOVED(1763),
	// Message: This item can only be worn by a member of the Clan Academy.
	THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY(1764),
	// Message: As a graduate of the Clan Academy, you can no longer wear this item.
	AS_A_GRADUATE_OF_THE_CLAN_ACADEMY_YOU_CAN_NO_LONGER_WEAR_THIS_ITEM(1765),
	// Message: An application to join the clan has been sent to $c1 in $s2.
	AN_APPLICATION_TO_JOIN_THE_CLAN_HAS_BEEN_SENT_TO_C1_IN_S2(1766),
	// Message: An application to join the Clan Academy has been sent to $c1.
	AN_APPLICATION_TO_JOIN_THE_CLAN_ACADEMY_HAS_BEEN_SENT_TO_C1(1767),
	// Message: $c1 has invited you to join the Clan Academy of $s2 clan. Would you like to join?
	C1_HAS_INVITED_YOU_TO_JOIN_THE_CLAN_ACADEMY_OF_S2_CLAN(1768),
	// Message: $c1 has sent you an invitation to join the $s3 Order of Knights under the $s2 clan. Would you like to join?
	C1_HAS_SENT_YOU_AN_INVITATION_TO_JOIN_THE_S3_ORDER_OF_KNIGHTS_UNDER_THE_S2_CLAN(1769),
	// Message: The clan's reputation score has dropped below 0. The clan may face certain penalties as a result.
	THE_CLANS_REPUTATION_SCORE_HAS_DROPPED_BELOW_0(1770),
	// Message: Now that your clan level is above Level 5, it can accumulate clan reputation points.
	NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS(1771),
	// Message: Since your clan was defeated in a siege, $s1 points have been deducted from your clan's reputation score and given to the opposing clan.
	SINCE_YOUR_CLAN_WAS_DEFEATED_IN_A_SIEGE_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLANS_REPUTATION_SCORE_AND_GIVEN_TO_THE_OPPOSING_CLAN(1772),
	// Message: Since your clan emerged victorious from the siege, $s1 points have been added to your clan's reputation score.
	SINCE_YOUR_CLAN_EMERGED_VICTORIOUS_FROM_THE_SIEGE_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLANS_REPUTATION_SCORE(1773),
	// Message: Your clan's newly acquired contested clan hall has added $s1 points to your clan's reputation score.
	YOUR_CLANS_NEWLY_ACQUIRED_CONTESTED_CLAN_HALL_HAS_ADDED_S1_POINTS_TO_YOUR_CLANS_REPUTATION_SCORE(1774),
	// Message: Clan member $c1 was an active member of the highest-ranked party in the Festival of Darkness. $s2 points have been added to your clan's reputation score.
	CLAN_MEMBER_C1_WAS_AN_ACTIVE_MEMBER_OF_THE_HIGHESTRANKED_PARTY_IN_THE_FESTIVAL_OF_DARKNESS(1775),
	// Message: Clan member $c1 was named a hero. $2s points have been added to your clan's reputation score.
	CLAN_MEMBER_C1_WAS_NAMED_A_HERO(1776),
	// Message: You have successfully completed a clan quest. $s1 points have been added to your clan's reputation score.
	YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST(1777),
	// Message: An opposing clan has captured your clan's contested clan hall. $s1 points have been deducted from your clan's reputation score.
	AN_OPPOSING_CLAN_HAS_CAPTURED_YOUR_CLANS_CONTESTED_CLAN_HALL(1778),
	// Message: After losing the contested clan hall, 300 points have been deducted from your clan's reputation score.
	AFTER_LOSING_THE_CONTESTED_CLAN_HALL_300_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_CLANS_REPUTATION_SCORE(1779),
	// Message: Your clan has captured your opponent's contested clan hall. $s1 points have been deducted from your opponent's clan reputation score.
	YOUR_CLAN_HAS_CAPTURED_YOUR_OPPONENTS_CONTESTED_CLAN_HALL(1780),
	// Message: Your clan has added $1s points to its clan reputation score.
	YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE(1781),
	// Message: Your clan member, $c1, was killed. $s2 points have been deducted from your clan's reputation score and added to your opponent's clan reputation score.
	YOUR_CLAN_MEMBER_C1_WAS_KILLED(1782),
	// Message: For killing an opposing clan member, $s1 points have been deducted from your opponents' clan reputation score.
	FOR_KILLING_AN_OPPOSING_CLAN_MEMBER_S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_YOUR_OPPONENTS_CLAN_REPUTATION_SCORE(1783),
	// Message: Your clan has failed to defend the castle. $s1 points have been deducted from your clan's reputation score and added to your opponents'.
	YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE(1784),
	// Message: The clan you belong to has been initialized. $s1 points have been deducted from your clan reputation score.
	THE_CLAN_YOU_BELONG_TO_HAS_BEEN_INITIALIZED(1785),
	// Message: Your clan has failed to defend the castle. $s1 points have been deducted from your clan's reputation score.
	YOUR_CLAN_HAS_FAILED_TO_DEFEND_THE_CASTLE_(1786),
	// Message: $s1 points have been deducted from the clan's reputation score.
	S1_POINTS_HAVE_BEEN_DEDUCTED_FROM_THE_CLANS_REPUTATION_SCORE(1787),
	// Message: The clan skill $s1 has been added.
	THE_CLAN_SKILL_S1_HAS_BEEN_ADDED(1788),
	// Message: User $c1 has a history of using BOT.
	USER_C1_HAS_A_HISTORY_OF_USING_BOT(1800),
	// Message: The attempt to sell has failed.
	THE_ATTEMPT_TO_SELL_HAS_FAILED(1801),
	// Message: The attempt to trade has failed.
	THE_ATTEMPT_TO_TRADE_HAS_FAILED(1802),
	// Message: Participation requests are no longer being accepted.
	PARTICIPATION_REQUESTS_ARE_NO_LONGER_BEING_ACCEPTED(1803),
	// Message: Your account has been suspended for 7 days because an illicit cash/account transaction has been detected. For more information, please visit the Support Center on the
	// PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_7_DAYS_BECAUSE_AN_ILLICIT_CASHACCOUNT_TRANSACTION_HAS_BEEN_DETECTED(1804),
	// Message: Your account has been suspended for 30 days because an illicit cash/account transaction has been detected for the second time. For more information, please visit the
	// Support Center on the PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_30_DAYS_BECAUSE_AN_ILLICIT_CASHACCOUNT_TRANSACTION_HAS_BEEN_DETECTED_FOR_THE_SECOND_TIME(1805),
	// Message: Your account has been permanently suspended because an illicit cash/account transaction has been detected for the third time. For more information, please visit the
	// Support Center on the PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_HAS_BEEN_PERMANENTLY_SUSPENDED_BECAUSE_AN_ILLICIT_CASHACCOUNT_TRANSACTION_HAS_BEEN_DETECTED_FOR_THE_THIRD_TIME(1806),
	// Message: Your account has been suspended for 30 days because of your involvement in an illicit cash transaction. For more information, please visit the Support Center on the
	// PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_30_DAYS_BECAUSE_OF_YOUR_INVOLVEMENT_IN_AN_ILLICIT_CASH_TRANSACTION(1807),
	// Message: Your account has been permanently suspended because of your involvement in an illicit cash/account transaction. For more information, please visit the Support Center on
	// the PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_HAS_BEEN_PERMANENTLY_SUSPENDED_BECAUSE_OF_YOUR_INVOLVEMENT_IN_AN_ILLICIT_CASHACCOUNT_TRANSACTION(1808),
	// Message: Your account must be verified. For information on verification procedures, please visit the PlayNC website (http://us.ncsol2f.com/support/).
	YOUR_ACCOUNT_MUST_BE_VERIFIED(1809),
	// Message: The refuse invitation state has been activated.
	THE_REFUSE_INVITATION_STATE_HAS_BEEN_ACTIVATED(1810),
	// Message: The refuse invitation state has been removed.
	THE_REFUSE_INVITATION_STATE_HAS_BEEN_REMOVED(1811),
	// Message: Since the refuse invitation state is currently activated, no invitation can be made.
	SINCE_THE_REFUSE_INVITATION_STATE_IS_CURRENTLY_ACTIVATED_NO_INVITATION_CAN_BE_MADE(1812),
	// Message: $s1 has $s2 hour(s) of usage time remaining.
	S1_HAS_S2_HOURS_OF_USAGE_TIME_REMAINING(1813),
	// Message: $s1 has $s2 minute(s) of usage time remaining.
	S1_HAS_S2_MINUTES_OF_USAGE_TIME_REMAINING(1814),
	// Message: $s2 was dropped in the $s1 region.
	S2_WAS_DROPPED_IN_THE_S1_REGION(1815),
	// Message: The owner of $s2 has appeared in the $s1 region.
	THE_OWNER_OF_S2_HAS_APPEARED_IN_THE_S1_REGION(1816),
	// Message: $s2's owner has logged into the $s1 region.
	S2S_OWNER_HAS_LOGGED_INTO_THE_S1_REGION(1817),
	// Message: $s1 has disappeared.
	S1_HAS_DISAPPEARED_(1818),
	// Message: An evil is pulsating from $s2 in $s1.
	AN_EVIL_IS_PULSATING_FROM_S2_IN_S1(1819),
	// Message: $s1 is currently asleep.
	S1_IS_CURRENTLY_ASLEEP(1820),
	// Message: $s2's evil presence is felt in $s1.
	S2S_EVIL_PRESENCE_IS_FELT_IN_S1(1821),
	// Message: $s1 has been sealed.
	S1_HAS_BEEN_SEALED(1822),
	// Message: The registration period for a clan hall war has ended.
	THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED(1823),
	// Message: You have been registered for a clan hall war. Please move to the left side of the clan hall's arena and get ready.
	YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR(1824),
	// Message: You have failed in your attempt to register for the clan hall war. Please try again.
	YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR(1825),
	// Message: In $s1 minute(s), the game will begin. All players must hurry and move to the left side of the clan hall's arena.
	IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA(1826),
	// Message: In $s1 minute(s), the game will begin. All players, please enter the arena now.
	IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW(1827),
	// Message: In $s1 second(s), the game will begin.
	IN_S1_SECONDS_THE_GAME_WILL_BEGIN(1828),
	// Message: $c1 is not allowed to use the party room invite command. Please update the waiting list.
	C1_IS_NOT_ALLOWED_TO_USE_THE_PARTY_ROOM_INVITE_COMMAND(1830),
	// Message: $c1 does not meet the conditions of the party room. Please update the waiting list.
	C1_DOES_NOT_MEET_THE_CONDITIONS_OF_THE_PARTY_ROOM(1831),
	// Message: Only a room leader may invite others to a party room.
	ONLY_A_ROOM_LEADER_MAY_INVITE_OTHERS_TO_A_PARTY_ROOM(1832),
	// Message: The party room is full. No more characters can be invited in.
	THE_PARTY_ROOM_IS_FULL(1834),
	// Message: $s1 is full and cannot accept additional clan members at this time.
	S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME(1835),
	// Message: This clan hall war has been cancelled. Not enough clans have registered.
	THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED(1841),
	// Message: $c1 wishes to summon you from $s2. Do you accept?
	C1_WISHES_TO_SUMMON_YOU_FROM_S2(1842),
	// Message:
	S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED(1843),
	// Message:
	S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED(1844),
	// Message: The Clan Reputation Score is too low.
	THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW(1860),
	// Message: Your pet/servitor is unresponsive and will not obey any orders.
	YOUR_PETSERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS(1864),
	// Message: The preliminary match will begin in $s1 second(s). Prepare yourself.
	THE_PRELIMINARY_MATCH_WILL_BEGIN_IN_S1_SECONDS(1881),
	// Message: There are no offerings I own or I made a bid for.
	THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR(1883),
	// Message: Enter the PC Room coupon serial number:
	ENTER_THE_PC_ROOM_COUPON_SERIAL_NUMBER(1884),
	// Message: This serial number cannot be entered. Please try again in $s1 minute(s).
	THIS_SERIAL_NUMBER_CANNOT_BE_ENTERED(1885),
	// Message: This serial number has already been used.
	THIS_SERIAL_NUMBER_HAS_ALREADY_BEEN_USED(1886),
	// Message: Invalid serial number. Your attempt to enter the number has failed $s1 time(s). You will be allowed to make $s2 more attempt(s).
	INVALID_SERIAL_NUMBER(1887),
	// Message: Invalid serial number. Your attempt to enter the number has failed 5 times. Please try again in 4 hours.
	INVALID_SERIAL_NUMBER_(1888),
	// Message: Congratulations! You have received $s1.
	CONGRATULATIONS_YOU_HAVE_RECEIVED_S1(1889),
	// Message: Since you have already used this coupon, you may not use this serial number.
	SINCE_YOU_HAVE_ALREADY_USED_THIS_COUPON_YOU_MAY_NOT_USE_THIS_SERIAL_NUMBER(1890),
	// Message: You may not use items in a private store or private work shop.
	YOU_MAY_NOT_USE_ITEMS_IN_A_PRIVATE_STORE_OR_PRIVATE_WORK_SHOP(1891),
	// Message: The replay file for the previous version cannot be played.
	THE_REPLAY_FILE_FOR_THE_PREVIOUS_VERSION_CANNOT_BE_PLAYED(1892),
	// Message: This file cannot be replayed.
	THIS_FILE_CANNOT_BE_REPLAYED(1893),
	// Message: A sub-class cannot be created or changed while you are over your weight limit.
	A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT(1894),
	// Message: $c1 is in an area which blocks summoning.
	C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING(1895),
	// Message: $c1 has already been summoned.
	C1_HAS_ALREADY_BEEN_SUMMONED(1896),
	// Message: $s1 is required for summoning.
	S1_IS_REQUIRED_FOR_SUMMONING(1897),
	// Message: $c1 is currently trading or operating a private store and cannot be summoned.
	C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED(1898),
	// Message: Your target is in an area which blocks summoning.
	YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING(1899),
	// Message: $c1 has entered the party room.
	C1_HAS_ENTERED_THE_PARTY_ROOM(1900),
	// Message: $s1 has sent an invitation to room <$s2>.
	S1_HAS_SENT_AN_INVITATION_TO_ROOM_S2(1901),
	// Message: Incompatible item grade. This item cannot be used.
	INCOMPATIBLE_ITEM_GRADE(1902),
	// Message: A sub-class may not be created or changed while a servitor or pet is summoned.
	A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED(1904),
	// Message:
	YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD(1911),
	// Message: The game will end in $s1 second(s).
	THE_GAME_WILL_END_IN_S1_SECONDS_(1915),
	// Message:
	THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED(1916),
	// Message:
	THE_DEATH_PENALTY_HAS_BEEN_LIFTED(1917),
	// Message: Your pet is too high level to control.
	YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL(1918),
	// Message:
	COURT_MAGICIAN__THE_PORTAL_HAS_BEEN_CREATED(1923),
	// Message: Current Location: $s1, $s2, $s3 (near the Primeval Isle)
	CURRENT_LOCATION_S1_S2_S3_NEAR_THE_PRIMEVAL_ISLE(1924),
	// Message: Due to the affects of the Seal of Strife, it is not possible to summon at this time.
	DUE_TO_THE_AFFECTS_OF_THE_SEAL_OF_STRIFE_IT_IS_NOT_POSSIBLE_TO_SUMMON_AT_THIS_TIME(1925),
	// Message: There is no opponent to receive your challenge for a duel.
	THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL(1926),
	// Message: $c1 has been challenged to a duel.
	C1_HAS_BEEN_CHALLENGED_TO_A_DUEL(1927),
	// Message: $c1's party has been challenged to a duel.
	C1S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL(1928),
	// Message: $c1 has accepted your challenge to a duel. The duel will begin in a few moments.
	C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL(1929),
	// Message: You have accepted $c1's challenge a duel. The duel will begin in a few moments.
	YOU_HAVE_ACCEPTED_C1S_CHALLENGE_A_DUEL(1930),
	// Message: $c1 has declined your challenge to a duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1931),
	// Message: $c1 has declined your challenge to a duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL_(1932),
	// Message: You have accepted $c1's challenge to a party duel. The duel will begin in a few moments.
	YOU_HAVE_ACCEPTED_C1S_CHALLENGE_TO_A_PARTY_DUEL(1933),
	// Message: $s1 has accepted your challenge to duel against their party. The duel will begin in a few moments.
	S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY(1934),
	// Message: $c1 has declined your challenge to a party duel.
	C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_PARTY_DUEL(1935),
	// Message: The opposing party has declined your challenge to a duel.
	THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL(1936),
	// Message: Since the person you challenged is not currently in a party, they cannot duel against your party.
	SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY(1937),
	// Message: $c1 has challenged you to a duel.
	C1_HAS_CHALLENGED_YOU_TO_A_DUEL(1938),
	// Message: $c1's party has challenged your party to a duel.
	C1S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL(1939),
	// Message: You are unable to request a duel at this time.
	YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME(1940),
	// Message: In a moment, you will be transported to the site where the duel will take place.
	IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE(1944),
	// Message: The duel will begin in $s1 second(s).
	THE_DUEL_WILL_BEGIN_IN_S1_SECONDS(1945),
	// Message: Let the duel begin!
	LET_THE_DUEL_BEGIN(1949),
	// Message: $c1 has won the duel.
	C1_HAS_WON_THE_DUEL(1950),
	// Message: $c1's party has won the duel.
	C1S_PARTY_HAS_WON_THE_DUEL(1951),
	// Message: The duel has ended in a tie.
	THE_DUEL_HAS_ENDED_IN_A_TIE(1952),
	// Message: Since $c1 was disqualified, $s2 has won.
	SINCE_C1_WAS_DISQUALIFIED_S2_HAS_WON(1953),
	// Message: Since $c1's party was disqualified, $s2's party has won.
	SINCE_C1S_PARTY_WAS_DISQUALIFIED_S2S_PARTY_HAS_WON(1954),
	// Message: Since $c1 withdrew from the duel, $s2 has won.
	SINCE_C1_WITHDREW_FROM_THE_DUEL_S2_HAS_WON(1955),
	// Message: Since $c1's party withdrew from the duel, $s2's party has won.
	SINCE_C1S_PARTY_WITHDREW_FROM_THE_DUEL_S2S_PARTY_HAS_WON(1956),
	// Message: Select the item to be augmented.
	SELECT_THE_ITEM_TO_BE_AUGMENTED(1957),
	// Message: Select the catalyst for augmentation.
	SELECT_THE_CATALYST_FOR_AUGMENTATION(1958),
	// Message: Requires $s2 $s1.
	REQUIRES_S2_S1(1959),
	// Message: This is not a suitable item.
	THIS_IS_NOT_A_SUITABLE_ITEM(1960),
	// Message: Gemstone quantity is incorrect.
	GEMSTONE_QUANTITY_IS_INCORRECT(1961),
	// Message: The item was successfully augmented!
	THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED(1962),
	// Message: Select the item from which you wish to remove augmentation.
	SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION(1963),
	// Message: Augmentation removal can only be done on an augmented item.
	AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM(1964),
	// Message: Augmentation has been successfully removed from your $s1.
	AUGMENTATION_HAS_BEEN_SUCCESSFULLY_REMOVED_FROM_YOUR_S1(1965),
	// Message: Only the clan leader may issue commands.
	ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS(1966),
	// Message: S1
	S1(1983),
	// Message: Press the Augment button to begin.
	PRESS_THE_AUGMENT_BUTTON_TO_BEGIN(1984),
	// Message: $s1's drop area ($s2)
	S1S_DROP_AREA_S2(1985),
	// Message: $s1's owner ($s2)
	S1S_OWNER_S2(1986),
	// Message: The ferry has arrived at Primeval Isle.
	THE_FERRY_HAS_ARRIVED_AT_PRIMEVAL_ISLE(1988),
	// Message: The ferry will leave for Rune Harbor after anchoring for three minutes.
	THE_FERRY_WILL_LEAVE_FOR_RUNE_HARBOR_AFTER_ANCHORING_FOR_THREE_MINUTES(1989),
	// Message: The ferry is now departing Primeval Isle for Rune Harbor.
	THE_FERRY_IS_NOW_DEPARTING_PRIMEVAL_ISLE_FOR_RUNE_HARBOR(1990),
	// Message: The ferry will leave for Primeval Isle after anchoring for three minutes.
	THE_FERRY_WILL_LEAVE_FOR_PRIMEVAL_ISLE_AFTER_ANCHORING_FOR_THREE_MINUTES(1991),
	// Message: The ferry is now departing Rune Harbor for Primeval Isle.
	THE_FERRY_IS_NOW_DEPARTING_RUNE_HARBOR_FOR_PRIMEVAL_ISLE(1992),
	// Message: The ferry from Primeval Isle to Rune Harbor has been delayed.
	THE_FERRY_FROM_PRIMEVAL_ISLE_TO_RUNE_HARBOR_HAS_BEEN_DELAYED(1993),
	// Message: The ferry from Rune Harbor to Primeval Isle has been delayed.
	THE_FERRY_FROM_RUNE_HARBOR_TO_PRIMEVAL_ISLE_HAS_BEEN_DELAYED(1994),
	// Message: $s1 channel filtering option
	S1_CHANNEL_FILTERING_OPTION(1995),
	// Message: The attack has been blocked.
	THE_ATTACK_HAS_BEEN_BLOCKED(1996),
	// Message: $c1 is performing a counterattack.
	C1_IS_PERFORMING_A_COUNTERATTACK(1997),
	// Message: You countered $c1's attack.
	YOU_COUNTERED_C1S_ATTACK(1998),
	// Message: $c1 dodges the attack.
	C1_DODGES_THE_ATTACK(1999),
	// Message: You have avoided $c1's attack.
	YOU_HAVE_AVOIDED_C1S_ATTACK_(2000),
	// Message: Augmentation failed due to inappropriate conditions.
	AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS(2001),
	// Message: Trap failed.
	TRAP_FAILED(2002),
	// Message: You obtained an ordinary material.
	YOU_OBTAINED_AN_ORDINARY_MATERIAL(2003),
	// Message: You obtained a rare material.
	YOU_OBTAINED_A_RARE_MATERIAL(2004),
	// Message: You obtained a unique material.
	YOU_OBTAINED_A_UNIQUE_MATERIAL(2005),
	// Message: You obtained the only material of this kind.
	YOU_OBTAINED_THE_ONLY_MATERIAL_OF_THIS_KIND(2006),
	// Message: Please enter the recipient's name.
	PLEASE_ENTER_THE_RECIPIENTS_NAME(2007),
	// Message: Please enter the text.
	PLEASE_ENTER_THE_TEXT(2008),
	// Message: You cannot exceed 1500 characters.
	YOU_CANNOT_EXCEED_1500_CHARACTERS(2009),
	// Message: $s2 $s1
	S2_S1(2010),
	// Message: The augmented item cannot be discarded.
	THE_AUGMENTED_ITEM_CANNOT_BE_DISCARDED(2011),
	// Message: $s1 has been activated.
	S1_HAS_BEEN_ACTIVATED(2012),
	// Message: Your seed or remaining purchase amount is inadequate.
	YOUR_SEED_OR_REMAINING_PURCHASE_AMOUNT_IS_INADEQUATE(2013),
	// Message: You cannot proceed because the manor cannot accept any more crops. All crops have been returned and no adena withdrawn.
	YOU_CANNOT_PROCEED_BECAUSE_THE_MANOR_CANNOT_ACCEPT_ANY_MORE_CROPS(2014),
	// Message: A skill is ready to be used again.
	A_SKILL_IS_READY_TO_BE_USED_AGAIN(2015),
	// Message: A skill is ready to be used again but its re-use counter time has increased.
	A_SKILL_IS_READY_TO_BE_USED_AGAIN_BUT_ITS_REUSE_COUNTER_TIME_HAS_INCREASED(2016),
	// Message: $c1 cannot duel because $c1 is currently engaged in a private store or manufacture.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE(2017),
	// Message: $c1 cannot duel because $c1 is currently fishing.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING(2018),
	// Message: $c1 cannot duel because $c1's HP or MP is below 50%.
	C1_CANNOT_DUEL_BECAUSE_C1S_HP_OR_MP_IS_BELOW_50(2019),
	// Message: $c1 cannot make a challenge to a duel because $c1 is currently in a duel-prohibited area (Peaceful Zone / Seven Signs Zone / Near Water / Restart Prohibited Area).
	C1_CANNOT_MAKE_A_CHALLENGE_TO_A_DUEL_BECAUSE_C1_IS_CURRENTLY_IN_A_DUELPROHIBITED_AREA_PEACEFUL_ZONE__SEVEN_SIGNS_ZONE__NEAR_WATER__RESTART_PROHIBITED_AREA(2020),
	// Message: $c1 cannot duel because $c1 is currently engaged in battle.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE(2021),
	// Message: $c1 cannot duel because $c1 is already engaged in a duel.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL(2022),
	// Message: $c1 cannot duel because $c1 is in a chaotic state.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_STATE(2023),
	// Message: $c1 cannot duel because $c1 is participating in the Olympiad.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD(2024),
	// Message: $c1 cannot duel because $c1 is participating in a clan hall war.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_CLAN_HALL_WAR(2025),
	// Message: $c1 cannot duel because $c1 is participating in a siege war.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_A_SIEGE_WAR(2026),
	// Message: $c1 cannot duel because $c1 is currently riding a boat, steed, or strider.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_STEED_OR_STRIDER(2027),
	// Message: $c1 cannot receive a duel challenge because $c1 is too far away.
	C1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_C1_IS_TOO_FAR_AWAY(2028),
	// Message: A sub-class cannot be created or changed because you have exceeded your inventory limit.
	A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT(2033),
	// Message: There are $s1 hours(s) and $s2 minute(s) remaining until the item can be purchased again.
	THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_ITEM_CAN_BE_PURCHASED_AGAIN(2034),
	// Message: There are $s1 minute(s) remaining until the item can be purchased again.
	THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_ITEM_CAN_BE_PURCHASED_AGAIN(2035),
	// Message: Unable to invite because the party is locked.
	UNABLE_TO_INVITE_BECAUSE_THE_PARTY_IS_LOCKED(2036),
	// Message: Unable to create character. You are unable to create a new character on the selected server. A restriction is in place which restricts users from creating characters on
	// different servers where no previous character exists. Please choose another server.
	UNABLE_TO_CREATE_CHARACTER(2037),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to drop items and/or Adena. To unlock all of the features of Lineage II,
	// purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS(2038),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to trade items and/or Adena. To unlock all of the features of Lineage II,
	// purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_(2039),
	// Message: Cannot trade items with the targeted user.
	CANNOT_TRADE_ITEMS_WITH_THE_TARGETED_USER(2040),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to setup private stores. To unlock all of the features of Lineage II, purchase
	// the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS__(2041),
	// Message: This account has been suspended for non-payment based on the cell phone payment agreement.\n Please submit proof of payment by fax (02-2186-3499) and contact customer
	// service at 1600-0020.
	THIS_ACCOUNT_HAS_BEEN_SUSPENDED_FOR_NONPAYMENT_BASED_ON_THE_CELL_PHONE_PAYMENT_AGREEMENT(2042),
	// Message: You have exceeded your inventory volume limit and may not take this quest item. Please make room in your inventory and try again.
	YOU_HAVE_EXCEEDED_YOUR_INVENTORY_VOLUME_LIMIT_AND_MAY_NOT_TAKE_THIS_QUEST_ITEM(2043),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to set up private manufacturing stores. To unlock all of the features of
	// Lineage II, purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS___(2044),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to use private manufacturing stores. To unlock all of the features of Lineage
	// II, purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS____(2045),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed buy items from private stores. To unlock all of the features of Lineage II,
	// purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____(2046),
	// Message: Some Lineage II features have been limited for free trials. Trial accounts aren’t allowed to access clan warehouses. To unlock all of the features of Lineage II,
	// purchase the full version today.
	SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS______(2047),
	// Message: The shortcut in use conflicts with $s1. Do you wish to reset the conflicting shortcuts and use the saved shortcut?
	THE_SHORTCUT_IN_USE_CONFLICTS_WITH_S1(2048),
	// Message: The shortcut will be applied and saved in the server. Will you continue?
	THE_SHORTCUT_WILL_BE_APPLIED_AND_SAVED_IN_THE_SERVER(2049),
	// Message: $s1 clan is trying to display a flag.
	S1_CLAN_IS_TRYING_TO_DISPLAY_A_FLAG(2050),
	// Message: You must accept the User Agreement before this account can access Lineage II.\n Please try again after accepting the agreement on the PlayNC website
	// (http://us.ncsol2f.com).
	YOU_MUST_ACCEPT_THE_USER_AGREEMENT_BEFORE_THIS_ACCOUNT_CAN_ACCESS_LINEAGE_II(2051),
	// Message: A guardian's consent is required before this account can be used to play Lineage II.\nPlease try again after this consent is provided.
	A_GUARDIANS_CONSENT_IS_REQUIRED_BEFORE_THIS_ACCOUNT_CAN_BE_USED_TO_PLAY_LINEAGE_II(2052),
	// Message: This account has declined the User Agreement or is pending a withdrawal request. \nPlease try again after cancelling this request.
	THIS_ACCOUNT_HAS_DECLINED_THE_USER_AGREEMENT_OR_IS_PENDING_A_WITHDRAWAL_REQUEST(2053),
	// Message: This account has been suspended. \nFor more information, please call the Customer's Center (Tel. 1600-0020).
	THIS_ACCOUNT_HAS_BEEN_SUSPENDED(2054),
	// Message: Your account has been converted to an integrated account, and is unable to be accessed. \nPlease logon with the converted integrated account.
	YOUR_ACCOUNT_HAS_BEEN_CONVERTED_TO_AN_INTEGRATED_ACCOUNT_AND_IS_UNABLE_TO_BE_ACCESSED(2056),
	// Message: You are still under transform penalty and cannot be polymorphed.
	YOU_ARE_STILL_UNDER_TRANSFORM_PENALTY_AND_CANNOT_BE_POLYMORPHED(2061),
	// Message: You cannot polymorph when you have summoned a servitor/pet.
	YOU_CANNOT_POLYMORPH_WHEN_YOU_HAVE_SUMMONED_A_SERVITORPET(2062),
	// Message: You cannot polymorph while riding a pet.
	YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_PET(2063),
	// Message: You cannot polymorph while under the effect of a special skill.
	YOU_CANNOT_POLYMORPH_WHILE_UNDER_THE_EFFECT_OF_A_SPECIAL_SKILL(2064),
	// Message: That item cannot be taken off.
	THAT_ITEM_CANNOT_BE_TAKEN_OFF(2065),
	// Message: That weapon cannot perform any attacks.
	THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS(2066),
	// Message: That weapon cannot use any other skill except the weapon's skill.
	THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL(2067),
	// Message: You do not have all of the items needed to untrain the enchant skill.
	YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_UNTRAIN_THE_ENCHANT_SKILL(2068),
	// Message: Untrain of enchant skill was successful. Current level of enchant skill $s1 has been decreased by 1.
	UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL(2069),
	// Message: Untrain of enchant skill was successful. Current level of enchant skill $s1 became 0 and enchant skill will be initialized.
	UNTRAIN_OF_ENCHANT_SKILL_WAS_SUCCESSFUL_(2070),
	// Message: You do not have all of the items needed to enchant skill route change.
	YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_SKILL_ROUTE_CHANGE(2071),
	// Message: Enchant skill route change was successful. Lv of enchant skill $s1 has been decreased by $s2.
	ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL(2072),
	// Message: Enchant skill route change was successful. Lv of enchant skill $s1 will remain.
	ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_(2073),
	// Message: Skill enchant failed. Current level of enchant skill $s1 will remain unchanged.
	SKILL_ENCHANT_FAILED_(2074),
	// Message: It is not an auction period.
	IT_IS_NOT_AN_AUCTION_PERIOD(2075),
	// Message: Bidding is not allowed because the maximum bidding price exceeds 100 billion.
	BIDDING_IS_NOT_ALLOWED_BECAUSE_THE_MAXIMUM_BIDDING_PRICE_EXCEEDS_100_BILLION(2076),
	// Message: Your bid must be higher than the current highest bid.
	YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID(2077),
	// Message: You do not have enough adena for this bid.
	YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID(2078),
	// Message: You currently have the highest bid, but the reserve has not been met.
	YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_BUT_THE_RESERVE_HAS_NOT_BEEN_MET(2079),
	// Message: You have been outbid.
	YOU_HAVE_BEEN_OUTBID(2080),
	// Message: There are no funds presently due to you.
	THERE_ARE_NO_FUNDS_PRESENTLY_DUE_TO_YOU(2081),
	// Message: You have exceeded the total amount of adena allowed in inventory.
	YOU_HAVE_EXCEEDED_THE_TOTAL_AMOUNT_OF_ADENA_ALLOWED_IN_INVENTORY(2082),
	// Message: The auction has begun.
	THE_AUCTION_HAS_BEGUN(2083),
	// Message: Enemy Blood Pledges have intruded into the fortress.
	ENEMY_BLOOD_PLEDGES_HAVE_INTRUDED_INTO_THE_FORTRESS(2084),
	// Message: Shout and trade chatting cannot be used while possessing a cursed weapon.
	SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON(2085),
	// Message: Search on user $c2 for third-party program use will be completed in $s1 minute(s).
	SEARCH_ON_USER_C2_FOR_THIRDPARTY_PROGRAM_USE_WILL_BE_COMPLETED_IN_S1_MINUTES(2086),
	// Message: A fortress is under attack!
	A_FORTRESS_IS_UNDER_ATTACK(2087),
	// Message: $s1 minute(s) until the fortress battle starts.
	S1_MINUTES_UNTIL_THE_FORTRESS_BATTLE_STARTS(2088),
	// Message: $s1 second(s) until the fortress battle starts.
	S1_SECONDS_UNTIL_THE_FORTRESS_BATTLE_STARTS(2089),
	// Message: The fortress battle $s1 has begun.
	THE_FORTRESS_BATTLE_S1_HAS_BEGUN(2090),
	// Message: $c1 is in a location which cannot be entered, therefore it cannot be processed.
	C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED(2096),
	// Message: $c1's level does not correspond to the requirements for entry.
	C1S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY(2097),
	// Message: $c1's quest requirement is not sufficient and cannot be entered.
	C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED(2098),
	// Message: $c1's item requirement is not sufficient and cannot be entered.
	C1S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED(2099),
	// Message: $c1 may not re-enter yet.
	C1_MAY_NOT_REENTER_YET(2100),
	// Message: You are not currently in a party, so you cannot enter.
	YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER(2101),
	// Message: You cannot enter due to the party having exceeded the limit.
	YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT(2102),
	// Message: You cannot enter because you are not associated with the current command channel.
	YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL(2103),
	// Message: The maximum number of instance zones has been exceeded. You cannot enter.
	THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED(2104),
	// Message: You have entered another instance zone, therefore you cannot enter corresponding dungeon.
	YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON(2105),
	// Message: This dungeon will expire in $s1 minute(s). You will be forced out of the dungeon when the time expires.
	THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES(2106),
	// Message: You cannot convert this item.
	YOU_CANNOT_CONVERT_THIS_ITEM(2130),
	// Message:
	YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL(2131),
	// Message: You cannot add elemental power while operating a Private Store or Private Workshop.
	YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP(2143),
	// Использование усилителя стихий было отменено.
	ELEMENTAL_POWER_ENCHANCER_USAGE_HAS_BEEN_CANCELLED(2145),
	// Message: Elemental power enhancer usage requirement is not sufficient.
	ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT(2146),
	// Message: $s2 elemental power has been added successfully to $s1.
	S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1(2147),
	// Message: $s3 elemental power has been added successfully to +$s1 $s2.
	S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1_S2(2148),
	// Message: You have failed to add elemental power.
	YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER(2149),
	// Message: Another elemental power has already been added. This elemental power cannot be added.
	ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED(2150),
	// Message:
	YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED(2151),
	// Message: The target is not a flagpole so a flag cannot be displayed.
	THE_TARGET_IS_NOT_A_FLAGPOLE_SO_A_FLAG_CANNOT_BE_DISPLAYED(2154),
	// Message: A flag is already being displayed, another flag cannot be displayed.
	A_FLAG_IS_ALREADY_BEING_DISPLAYED_ANOTHER_FLAG_CANNOT_BE_DISPLAYED(2155),
	// Message: There are not enough necessary items to use the skill.
	THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL(2156),
	// Message: Force attack is impossible against a temporary allied member during a siege.
	FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE(2158),
	// Message:
	BIDDER_EXISTS__THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES(2159),
	// Message: Bidder exists, auction time has been extended by 3 minutes.
	BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES(2160),
	// Message: There is not enough space to move, the skill cannot be used.
	THERE_IS_NOT_ENOUGH_SPACE_TO_MOVE_THE_SKILL_CANNOT_BE_USED(2161),
	// Message: Your soul count has increased by $s1. It is now at $s2.
	YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1(2162),
	// Message: Soul cannot be increased anymore.
	SOUL_CANNOT_BE_INCREASED_ANYMORE(2163),
	// Message:
	BIDDER_EXISTS__AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES(2160),
	// Message: The barracks have been seized.
	THE_BARRACKS_HAVE_BEEN_SEIZED(2164),
	// Message: The barracks function has been restored.
	THE_BARRACKS_FUNCTION_HAS_BEEN_RESTORED(2165),
	// Message: All barracks are occupied.
	ALL_BARRACKS_ARE_OCCUPIED(2166),
	// Message: A malicious skill cannot be used in a peace zone.
	A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE(2167),
	// Message: $c1 has acquired the flag.
	C1_HAS_ACQUIRED_THE_FLAG(2168),
	// Message: Your clan has been registered to $s1's fortress battle.
	YOUR_CLAN_HAS_BEEN_REGISTERED_TO_S1S_FORTRESS_BATTLE(2169),
	// Message: A malicious skill cannot be used when an opponent is in the peace zone.
	A_MALICIOUS_SKILL_CANNOT_BE_USED_WHEN_AN_OPPONENT_IS_IN_THE_PEACE_ZONE(2170),
	// Message: This item cannot be crystallized.
	THIS_ITEM_CANNOT_BE_CRYSTALLIZED(2171),
	// Message: +$s1$s2's auction has ended.
	S1S2S_AUCTION_HAS_ENDED(2172),
	// Message: $s1's auction has ended.
	S1S_AUCTION_HAS_ENDED(2173),
	// Message: $c1 cannot duel because $c1 is currently polymorphed.
	C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_POLYMORPHED(2174),
	// Message: Party duel cannot be initiated due to a polymorphed party member.
	PARTY_DUEL_CANNOT_BE_INITIATED_DUE_TO_A_POLYMORPHED_PARTY_MEMBER(2175),
	// Message: $s1's $s2 attribute has been removed.
	S1S_S2_ATTRIBUTE_HAS_BEEN_REMOVED(2176),
	// Message: +$s1$s2's $s3 attribute has been removed.
	S1S2S_S3_ATTRIBUTE_HAS_BEEN_REMOVED(2177),
	// Message: Attribute removal has failed.
	ATTRIBUTE_REMOVAL_HAS_FAILED(2178),
	// Message: You have the highest bid submitted in a Giran Castle auction.
	YOU_HAVE_THE_HIGHEST_BID_SUBMITTED_IN_A_GIRAN_CASTLE_AUCTION(2179),
	// Message:
	DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_AGAINST_C2S_MAGIC(2280),
	// Message: You have highest the bid submitted in a Rune Castle auction.
	YOU_HAVE_HIGHEST_THE_BID_SUBMITTED_IN_A_RUNE_CASTLE_AUCTION(2181),
	// Message: You cannot polymorph while riding a boat.
	YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT(2182),
	// Message: The fortress battle of $s1 has finished.
	THE_FORTRESS_BATTLE_OF_S1_HAS_FINISHED(2183),
	// Message: $s1 is victorious in the fortress battle of $s2.
	S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2(2184),
	// Message: Only a party leader can make the request to enter.
	ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER(2185),
	// Message: Soul cannot be absorbed anymore.
	SOUL_CANNOT_BE_ABSORBED_ANYMORE(2186),
	// Message: The target is located where you cannot charge.
	THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE(2187),
	// Message: Another enchantment is in progress. Please complete the previous task, then try again
	ANOTHER_ENCHANTMENT_IS_IN_PROGRESS(2188),
	// Message: Current Location : $s1, $s2, $s3 (Near Kamael Village)
	CURRENT_LOCATION__S1_S2_S3_NEAR_KAMAEL_VILLAGE(2189),
	// Message: Current Location : $s1, $s2, $s3 (Near the south end of the Wastelands)
	CURRENT_LOCATION__S1_S2_S3_NEAR_THE_SOUTH_END_OF_THE_WASTELANDS(2190),
	// Message: To apply selected options, the game needs to be reloaded. If you don't apply now, it will be applied when you start the game next time. Will you apply now?
	TO_APPLY_SELECTED_OPTIONS_THE_GAME_NEEDS_TO_BE_RELOADED(2191),
	// Message: You have bid on an item auction.
	YOU_HAVE_BID_ON_AN_ITEM_AUCTION(2192),
	// Message: You are too far from the NPC for that to work.
	YOU_ARE_TOO_FAR_FROM_THE_NPC_FOR_THAT_TO_WORK(2193),
	// Message: Current polymorph form cannot be applied with corresponding effects.
	CURRENT_POLYMORPH_FORM_CANNOT_BE_APPLIED_WITH_CORRESPONDING_EFFECTS(2194),
	// Message: You do not have enough souls.
	YOU_DO_NOT_HAVE_ENOUGH_SOULS(2195),
	// Message: No Owned Clan.
	NO_OWNED_CLAN(2196),
	// Message: Owned by clan $s1.
	OWNED_BY_CLAN_S1(2197),
	// Message: You currently have the highest bid in an item auction.
	YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_IN_AN_ITEM_AUCTION(2198),
	// Message: You cannot enter this instance zone while the NPC server is down.
	YOU_CANNOT_ENTER_THIS_INSTANCE_ZONE_WHILE_THE_NPC_SERVER_IS_DOWN(2199),
	// Message: This instance zone will be terminated as the NPC server is down. You will be forcibly removed from the dungeon shortly.
	THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_AS_THE_NPC_SERVER_IS_DOWN(2200),
	// Message: $s1 year(s) $s2 month(s) $s3 day(s)
	S1_YEARS_S2_MONTHS_S3_DAYS(2201),
	// Message: $s1 hour(s) $s2 minute(s) $s3 second(s)
	S1_HOURS_S2_MINUTES_S3_SECONDS(2202),
	// Message: $s1/$s2
	S1S2(2203),
	// Message: $s1 hour(s)
	S1_HOURS(2204),
	// Message: You have entered an area where the mini map cannot be used. Your mini map has been closed.
	YOU_HAVE_ENTERED_AN_AREA_WHERE_THE_MINI_MAP_CANNOT_BE_USED(2205),
	// Message: You have entered an area where the mini map can now be used.
	YOU_HAVE_ENTERED_AN_AREA_WHERE_THE_MINI_MAP_CAN_NOW_BE_USED(2206),
	// Message: This is an area where you cannot use the mini map. The mini map cannot be opened.
	THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP(2207),
	// Message: You do not meet the skill level requirements.
	YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS(2208),
	// Message: This is an area where your radar cannot be used
	THIS_IS_AN_AREA_WHERE_YOUR_RADAR_CANNOT_BE_USED(2209),
	// Message: Your skill will be returned to an unenchanted state.
	YOUR_SKILL_WILL_BE_RETURNED_TO_AN_UNENCHANTED_STATE(2210),
	// Message: You must learn the Onyx Beast skill before you can acquire further skills.
	YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_ACQUIRE_FURTHER_SKILLS(2211),
	// Message: You have not completed the necessary quest for skill acquisition.
	YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION(2212),
	// Message: You cannot board a ship while you are polymorphed.
	YOU_CANNOT_BOARD_A_SHIP_WHILE_YOU_ARE_POLYMORPHED(2213),
	// Message: A new character will be created with the current settings. Continue?
	A_NEW_CHARACTER_WILL_BE_CREATED_WITH_THE_CURRENT_SETTINGS(2214),
	// Message: $s1 P. Def.
	S1_P(2215),
	// Message: The CPU driver is not up-to-date. Please download the latest driver.
	THE_CPU_DRIVER_IS_NOT_UPTODATE(2216),
	// Message: The ballista has been successfully destroyed. The clan's reputation will be increased.
	THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED(2217),
	// Message: This squad skill has already been acquired.
	THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED(2219),
	// Message: The previous level skill has not been learned.
	THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED(2220),
	// Message: It is not possible to register for the castle siege side or castle siege of a higher castle in the contract.
	IT_IS_NOT_POSSIBLE_TO_REGISTER_FOR_THE_CASTLE_SIEGE_SIDE_OR_CASTLE_SIEGE_OF_A_HIGHER_CASTLE_IN_THE_CONTRACT(2227),
	// Message: Instance zone time limit:
	INSTANCE_ZONE_TIME_LIMIT(2228),
	// Message: There is no instance zone under a time limit.
	THERE_IS_NO_INSTANCE_ZONE_UNDER_A_TIME_LIMIT(2229),
	// Message: $s1 will be available for re-use after $s2 hour(s) $s3 minute(s).
	S1_WILL_BE_AVAILABLE_FOR_REUSE_AFTER_S2_HOURS_S3_MINUTES(2230),
	// Message: Siege registration is not possible due to your castle contract.
	SIEGE_REGISTRATION_IS_NOT_POSSIBLE_DUE_TO_YOUR_CASTLE_CONTRACT(2233),
	// Message: You are participating in the siege of $s1. This siege is scheduled for 2 hours.
	YOU_ARE_PARTICIPATING_IN_THE_SIEGE_OF_S1_THIS_SIEGE_IS_SCHEDULED_FOR_2_HOURS(2238),
	// Message: $s1 minute(s) remaining.
	S1_MINUTES_REMAINING(2244),
	// Message: $s1 second(s) remaining.
	S1_SECONDS_REMAINING(2245),
	// Message: The contest will begin in $s1 minute(s).
	THE_CONTEST_WILL_BEGIN_IN_S1_MINUTES(2246),
	// Message: You cannot board an airship while transformed.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED(2247),
	// Message: You cannot board an airship while petrified.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED(2248),
	// Message: You cannot board an airship while dead.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD(2249),
	// Message: You cannot board an airship while fishing.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING(2250),
	// Message: You cannot board an airship while in battle.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE(2251),
	// Message: You cannot board an airship while in a duel.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL(2252),
	// Message: You cannot board an airship while sitting.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING(2253),
	// Message: You cannot board an airship while casting.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING(2254),
	// Message: You cannot board an airship when a cursed weapon is equipped.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED(2255),
	// Message: You cannot board an airship while holding a flag.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG(2256),
	// Message: You cannot board an airship while a pet or a servitor is summoned.
	YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED(2257),
	// Message: You have already boarded another airship.
	YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP(2258),
	// Message:
	CURRENT_LOCATION__S1_S2_S3_NEAR_FANTASY_ISLE(2259),
	// Message:
	C1_HAS_GIVEN_C2_DAMAGE_OF_S3(2261),
	// Message:
	C1_HAS_EVADED_C2S_ATTACK(2264),
	// Message:
	C1S_ATTACK_WENT_ASTRAY(2265),
	// Message:
	C1S_ATTACK_FAILED(2268),
	// Message: This skill cannot be learned while in the sub-class state. Please try again after changing to the main class.
	THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE(2273),
	// Message: You entered an area where you cannot throw away items.
	YOU_ENTERED_AN_AREA_WHERE_YOU_CANNOT_THROW_AWAY_ITEMS(2274),
	// Message: You are in an area where you cannot cancel pet summoning.
	YOU_ARE_IN_AN_AREA_WHERE_YOU_CANNOT_CANCEL_PET_SUMMONING(2275),
	// Message: The rebel army recaptured the fortress.
	THE_REBEL_ARMY_RECAPTURED_THE_FORTRESS(2276),
	// Message: Party of $s1
	PARTY_OF_S1(2277),
	// Message: Remaining Time $s1:$s2
	REMAINING_TIME_S1S2(2278),
	// Message: You can no longer add a quest to the Quest Alerts.
	YOU_CAN_NO_LONGER_ADD_A_QUEST_TO_THE_QUEST_ALERTS(2279),
	// Message: Damage is decreased because $c1 resisted $c2's magic.
	DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC(2280),
	// Message: $c1 hit you for $s3 damage and hit your servitor for $s4.
	C1_HIT_YOU_FOR_S3_DAMAGE_AND_HIT_YOUR_SERVITOR_FOR_S4(2281),
	// Message: Leave Fantasy Isle.
	LEAVE_FANTASY_ISLE(2282),
	// Message: You cannot transform while sitting.
	YOU_CANNOT_TRANSFORM_WHILE_SITTING(2283),
	// Message: You have obtained all the points you can get today in a place other than Internet Cafй.
	YOU_HAVE_OBTAINED_ALL_THE_POINTS_YOU_CAN_GET_TODAY_IN_A_PLACE_OTHER_THAN_INTERNET_CAF(2284),
	// Message: This skill cannot remove this trap.
	THIS_SKILL_CANNOT_REMOVE_THIS_TRAP(2285),
	// Message: You cannot wear $s1 because you are not wearing a bracelet.
	YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET(2286),
	// Message: You cannot equip $s1 because you do not have any available slots.
	YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS(2287),
	// Message
	RESURRECTION_WILL_OCCUR_IN_S1_SECONDS(2288),
	// Message
	THE_MATCH_BETWEEN_THE_PARTIES_IS_NOT_AVAILABLE_BECAUSE_ONE_OF_THE_PARTY_MEMBERS_IS_BEING(2289),
	// Message
	YOU_CANNOT_ASSIGN_SHORTCUT_KEYS_BEFORE_YOU_LOG_IN(2290),
	// Message
	YOU_CAN_OPERATE_THE_MACHINE_WHEN_YOU_PARTICIPATE_IN_THE_PARTY(2291),
	// Message: Agathion skills can be used only when your Agathion is summoned.
	AGATHION_SKILLS_CAN_BE_USED_ONLY_WHEN_YOUR_AGATHION_IS_SUMMONED(2292),
	// Message
	CURRENT_LOCATION__S1_S2_S3_INSIDE_THE_STEEL_CITADEL(2293),
	// Message
	THE_LENGTH_OF_THE_UPLOADED_BADGE_OR_INSIGNIA_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS(2295),
	// Message
	ROUND_S1(2297),
	// Message
	THE_COLOR_OF_THE_BADGE_OR_INSIGNIA_THAT_YOU_WANT_TO_REGISTER_DOES_NOT_MEET_THE_STANDARD(2298),
	// Message
	THE_FILE_FORMAT_OF_THE_BADGE_OR_INSIGNIA_THAT_YOU_WANT_TO_REGISTER_DOES_NOT_MEET_THE_STANDARD(2299),
	// Message
	FAILED_TO_LOAD_KEYBOARD_SECURITY_MODULE_FOR_EFFECTIVE_GAMING_FUNCTIONALITY_WHEN_THE_GAME_IS_OVER(2300),
	// Message
	CURRENT_LOCATION__STEEL_CITADEL_RESISTANCE(2301),
	// Message
	YOUR_VITAMIN_ITEM_HAS_ARRIVED_VISIT_THE_VITAMIN_MANAGER_IN_ANY_VILLAGE_TO_OBTAIN_IT(2302),
	// Message: There are $s2 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2303),
	// Message: There are $s2 minute(s), $s3 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2304),
	// Message: There are $s2 hour(s), $s3 minute(s), and $s4 second(s) remaining in $s1's re-use time.
	THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME(2305),
	// Message: Your Charm of Courage is trying to resurrect you. Would you like to resurrect now?
	YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU(2306),
	// Message: The target is using a Charm of Courage.
	THE_TARGET_IS_USING_A_CHARM_OF_COURAGE(2307),
	// Message: Remaining time: %s1 day(s)
	REMAINING_TIME_S1_DAYS(2308),
	// Message: Remaining time: %s1 hour(s)
	REMAINING_TIME_S1_HOURS(2309),
	// Message: Remaining time: %s1 minute(s)
	REMAINING_TIME_S1_MINUTES(2310),
	// Message: You do not have a servitor.
	YOU_DO_NOT_HAVE_A_SERVITOR(2311),
	// Message: You do not have a pet.
	YOU_DO_NOT_HAVE_A_PET(2312),
	// Message: The vitamin item has arrived.
	THE_VITAMIN_ITEM_HAS_ARRIVED(2313),
	// Message: Your Vitality is at maximum.
	YOUR_VITALITY_IS_AT_MAXIMUM(2314),
	// Message: Your Vitality has increased.
	YOUR_VITALITY_HAS_INCREASED(2315),
	// Message: Your Vitality has decreased.
	YOUR_VITALITY_HAS_DECREASED(2316),
	// Message: Your Vitality is fully exhausted.
	YOUR_VITALITY_IS_FULLY_EXHAUSTED(2317),
	// Message: Only an enhanced skill can be cancelled.
	ONLY_AN_ENHANCED_SKILL_CAN_BE_CANCELLED(2318),
	// Message: You have acquired $s1 reputation.
	YOU_HAVE_ACQUIRED_S1_REPUTATION(2319),
	// Message: Masterwork creation possible
	MASTERWORK_CREATION_POSSIBLE(2320),
	// Message: Current location: Inside Kamaloka
	CURRENT_LOCATION_INSIDE_KAMALOKA(2321),
	// Message: Current location: Inside Nia Kamaloka
	CURRENT_LOCATION_INSIDE_NIA_KAMALOKA(2322),
	// Message: Current location: Inside Rim Kamaloka
	CURRENT_LOCATION_INSIDE_RIM_KAMALOKA(2323),
	// Message: $c1, you cannot enter because you have insufficient PC cafe points.
	C1_YOU_CANNOT_ENTER_BECAUSE_YOU_HAVE_INSUFFICIENT_PC_CAFE_POINTS(2324),
	// Message: Another teleport is taking place. Please try again once the teleport in process ends.
	ANOTHER_TELEPORT_IS_TAKING_PLACE(2325),
	// Message: You have acquired 50 Clan Fame Points.
	YOU_HAVE_ACQUIRED_50_CLAN_FAME_POINTS(2326),
	// Message: You don't have enough reputation to do that.
	YOU_DONT_HAVE_ENOUGH_REPUTATION_TO_DO_THAT(2327),
	// Message: Only clans who are level 4 or above can register for battle at Devastated Castle and Fortress of the Dead.
	ONLY_CLANS_WHO_ARE_LEVEL_4_OR_ABOVE_CAN_REGISTER_FOR_BATTLE_AT_DEVASTATED_CASTLE_AND_FORTRESS_OF_THE_DEAD(2328),
	// Message: Vitality Level $s1 $s2
	VITALITY_LEVEL_S1_S2(2329),
	// Message: : Experience points boosted by $s1.
	_EXPERIENCE_POINTS_BOOSTED_BY_S1(2330),
	// Message: <Rare> $s1
	RARE_S1(2331),
	// Message: <Supply> $s1
	SUPPLY_S1(2332),
	// Message: You cannot receive the vitamin item because you have exceed your inventory weight/quantity limit.
	YOU_CANNOT_RECEIVE_THE_VITAMIN_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHTQUANTITY_LIMIT(2333),
	// Message: Score that shows a player's individual fame. Fame can be obtained by participating in a territory war, castle siege, fortress siege, hideout siege, the Underground
	// Coliseum, the Festival of Darkness and the Olympiad.
	SCORE_THAT_SHOWS_A_PLAYERS_INDIVIDUAL_FAME(2334),
	// Message: There are no more vitamin items to be found.
	THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND(2335),
	// Message: CP Siphon!
	CP_SIPHON(2336),
	// Message: Your CP was drained because you were hit with a CP siphon skill.
	YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL(2337),
	// Message: If it's a draw, the player who first entered is first
	IF_ITS_A_DRAW_THE_PLAYER_WHO_FIRST_ENTERED_IS_FIRST(2338),
	// Message: Please place the item to be enchanted in the empty slot below.
	PLEASE_PLACE_THE_ITEM_TO_BE_ENCHANTED_IN_THE_EMPTY_SLOT_BELOW(2339),
	// Message: Please place the item for rate increase.
	PLEASE_PLACE_THE_ITEM_FOR_RATE_INCREASE(2340),
	// Message: The enchant will begin once you press the Start button below.
	THE_ENCHANT_WILL_BEGIN_ONCE_YOU_PRESS_THE_START_BUTTON_BELOW(2341),
	// Message: Success! The item is now a $s1.
	SUCCESS_THE_ITEM_IS_NOW_A_S1(2342),
	// Message: Failed. You have obtained $s2 of $s1.
	FAILED(2343),
	// Message: You have been killed by an attack from $c1.
	YOU_HAVE_BEEN_KILLED_BY_AN_ATTACK_FROM_C1(2344),
	// Message: You have attacked and killed $c1.
	YOU_HAVE_ATTACKED_AND_KILLED_C1(2345),
	// Message: Your account may have been involved in identity thel2f. As such, it has been temporarily restricted. If this does not apply to you, you may obtain normal service by
	// going through self-identification on the homepage. Please refer to the plaync homepage (www.plaync.co.kr) customer center (Lineage 2) clause 1:1 for more details.
	YOUR_ACCOUNT_MAY_HAVE_BEEN_INVOLVED_IN_IDENTITY_THEFT(2346),
	// Message: $s1 seconds to game end!
	S1_SECONDS_TO_GAME_END(2347),
	// Message: You cannot use My Teleports during a battle.
	YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE(2348),
	// Message: You cannot use My Teleports while participating a large-scale battle such as a castle siege, fortress siege, or hideout siege.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGESCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_HIDEOUT_SIEGE(2349),
	// Message: You cannot use My Teleports during a duel.
	YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL(2350),
	// Message: You cannot use My Teleports while flying.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING(2351),
	// Message: You cannot use My Teleports while participating in an Olympiad match.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH(2352),
	// Message: You cannot use My Teleports while you are in a petrified or paralyzed state.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE(2353),
	// Message: You cannot use My Teleports while you are dead.
	YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD(2354),
	// Message: You cannot use My Teleports in this area.
	YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA(2355),
	// Message: You cannot use My Teleports underwater.
	YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER(2356),
	// Message: You cannot use My Teleports in an instant zone.
	YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE(2357),
	// Message: You have no space to save the teleport location.
	YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION(2358),
	// Message: You cannot teleport because you do not have a teleport item.
	YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM(2359),
	// Message: My Teleports Spellbk: $s1
	MY_TELEPORTS_SPELLBK_S1(2360),
	// Message: Current location: $s1
	CURRENT_LOCATION_S1(2361),
	// Message: The saved teleport location will be deleted. Do you wish to continue?
	THE_SAVED_TELEPORT_LOCATION_WILL_BE_DELETED(2362),
	// Message: Your account has been confirmed as using another person's name. All game services have been limited. Please inquire about additional details through the PlayNC
	// (www.plaync.co.kr) customer center.
	YOUR_ACCOUNT_HAS_BEEN_CONFIRMED_AS_USING_ANOTHER_PERSONS_NAME(2363),
	// Message: The item has expired after its $s1 period.
	THE_ITEM_HAS_EXPIRED_AFTER_ITS_S1_PERIOD(2364),
	// Message: The designated item has expired after its $s1 period.
	THE_DESIGNATED_ITEM_HAS_EXPIRED_AFTER_ITS_S1_PERIOD(2365),
	// Message: The limited-time item has disappeared because the remaining time ran out.
	THE_LIMITEDTIME_ITEM_HAS_DISAPPEARED_BECAUSE_THE_REMAINING_TIME_RAN_OUT(2366),
	// Message: $s1's blessing has recovered HP by $s2.
	S1S_BLESSING_HAS_RECOVERED_HP_BY_S2(2367),
	// Message: $s1's blessing has recovered MP by $s2.
	S1S_BLESSING_HAS_RECOVERED_MP_BY_S2(2368),
	// Message: $s1's blessing has fully recovered HP and MP.
	S1S_BLESSING_HAS_FULLY_RECOVERED_HP_AND_MP(2369),
	// Message: Resurrection will take place in the waiting room after $s1 seconds.
	RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS(2370),
	// Message: $c1 was reported as a BOT.
	C1_WAS_REPORTED_AS_A_BOT(2371),
	// Message: There is not much time remaining until the hunting helper pet leaves.
	THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_HUNTING_HELPER_PET_LEAVES(2372),
	// Message: The hunting helper pet is now leaving.
	THE_HUNTING_HELPER_PET_IS_NOW_LEAVING(2373),
	// Message: End match!
	END_MATCH(2374),
	// Message: The hunting helper pet cannot be returned because there is not much time remaining until it leaves.
	THE_HUNTING_HELPER_PET_CANNOT_BE_RETURNED_BECAUSE_THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_IT_LEAVES(2375),
	// Message: You cannot receive a vitamin item during an exchange.
	YOU_CANNOT_RECEIVE_A_VITAMIN_ITEM_DURING_AN_EXCHANGE(2376),
	// Message: You cannot report a character who is in a peace zone or a battlefield.
	YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEFIELD(2377),
	// Message: You cannot report when a clan war has been declared.
	YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED(2378),
	// Message: You cannot report a character who has not acquired any Exp. after connecting.
	YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_EXP(2379),
	// Message: You cannot report this person again at this time.
	YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME(2380),
	// Message: You cannot report this person again at this time.
	YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME_(2381),
	// Message: You cannot report this person again at this time.
	YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME__(2382),
	// Message: You cannot report this person again at this time.
	YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME___(2383),
	// Message: This item does not meet the requirements for the enhancement spellbook.
	THIS_ITEM_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_THE_ENHANCEMENT_SPELLBOOK(2384),
	// Message: This is an incorrect support enhancement spellbook.
	THIS_IS_AN_INCORRECT_SUPPORT_ENHANCEMENT_SPELLBOOK(2385),
	// Message: This item does not meet the requirements for the support enhancement spellbook.
	THIS_ITEM_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_THE_SUPPORT_ENHANCEMENT_SPELLBOOK(2386),
	// Message: Registration of the support enhancement spellbook has failed.
	REGISTRATION_OF_THE_SUPPORT_ENHANCEMENT_SPELLBOOK_HAS_FAILED(2387),
	// Message: A party cannot be formed in this area.
	A_PARTY_CANNOT_BE_FORMED_IN_THIS_AREA(2388),
	// Message: The maximum accumulation allowed of PC cafe points has been exceeded. You can no longer acquire PC cafe points.
	THE_MAXIMUM_ACCUMULATION_ALLOWED_OF_PC_CAFE_POINTS_HAS_BEEN_EXCEEDED(2389),
	// Message: Your number of My Teleports slots has reached its maximum limit.
	YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT(2390),
	// Message: You have used the Feather of Blessing to resurrect.
	YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT(2391),
	// Message: That pet/servitor skill cannot be used because it is recharging.
	THAT_PET_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING(2396),
	// Message: Instant Zone currently in use: $s1
	INSTANT_ZONE_CURRENTLY_IN_USE_S1(2400),
	// Message: Clan lord $c2, who leads clan $s1, has been declared the lord of the $s3 territory.
	CLAN_LORD_C2_WHO_LEADS_CLAN_S1_HAS_BEEN_DECLARED_THE_LORD_OF_THE_S3_TERRITORY(2401),
	// Message: The Territory War request period has ended.
	THE_TERRITORY_WAR_REQUEST_PERIOD_HAS_ENDED(2402),
	// Message: The Territory War begins in 10 minutes!
	THE_TERRITORY_WAR_BEGINS_IN_10_MINUTES(2403),
	// Message: The Territory War begins in 5 minutes!
	THE_TERRITORY_WAR_BEGINS_IN_5_MINUTES(2404),
	// Message: The Territory War begins in 1 minute!
	THE_TERRITORY_WAR_BEGINS_IN_1_MINUTE(2405),
	// Message: You are currently registered for a 3 vs. 3 class irrelevant team match.
	YOU_ARE_CURRENTLY_REGISTERED_FOR_A_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH(2408),
	// Message:
	THE_COLLECTION_HAS_FAILED(2424),
	// Message: $c1 is already registered on the waiting list for the 3 vs. 3 class irrelevant team match.
	C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_3_VS_3_CLASS_IRRELEVANT_TEAM_MATCH(2440),
	// Message: Only a party leader can request a team match.
	ONLY_A_PARTY_LEADER_CAN_REQUEST_A_TEAM_MATCH(2441),
	// Message: The request cannot be made because the requirements have not been met. To participate in a team match, you must first form a 3-member party.
	THE_REQUEST_CANNOT_BE_MADE_BECAUSE_THE_REQUIREMENTS_HAVE_NOT_BEEN_MET(2442),
	// Message: The battlefield channel has been activated.
	THE_BATTLEFIELD_CHANNEL_HAS_BEEN_ACTIVATED(2445),
	// Message: The battlefield channel has been deactivated.
	THE_BATTLEFIELD_CHANNEL_HAS_BEEN_DEACTIVATED(2446),
	// Message: Five years have passed since this character's creation.
	FIVE_YEARS_HAVE_PASSED_SINCE_THIS_CHARACTERS_CREATION(2447),
	// Message: Your birthday gift has arrived. You can obtain it from the Gatekeeper in any village.
	YOUR_BIRTHDAY_GIFT_HAS_ARRIVED(2448),
	// Message: There are $s1 days until your character's birthday. On that day, you can obtain a special gift from the Gatekeeper in any village.
	THERE_ARE_S1_DAYS_UNTIL_YOUR_CHARACTERS_BIRTHDAY(2449),
	// Message: $c1's birthday is $s3/$s4/$s2.
	C1S_BIRTHDAY_IS_S3S4S2(2450),
	// Message:
	THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_A_NECESSARY_ITEM_IS_NOT_EQUIPPED(2453),
	// Message: In order to acquire an airship, the clan's level must be level 5 or higher.
	IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER(2456),
	// Message: An airship cannot be summoned because either you have not registered your airship license, or the airship has not yet been summoned.
	AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE_AIRSHIP_HAS_NOT_YET_BEEN_SUMMONED(2457),
	// Message: Your clan's airship is already being used by another clan member.
	YOUR_CLANS_AIRSHIP_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER(2458),
	// Message: The Airship Summon License has already been acquired.
	THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED(2459),
	// Message: The clan owned airship already exists.
	THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS(2460),
	// Message: An airship cannot be summoned because you don't have enough $s1.
	AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1(2462),
	// Message: The airship's fuel (EP) will soon run out.
	THE_AIRSHIPS_FUEL_EP_WILL_SOON_RUN_OUT(2463),
	// Message: The airship's fuel (EP) has run out. The airship's speed will be greatly decreased in this condition.
	THE_AIRSHIPS_FUEL_EP_HAS_RUN_OUT(2464),
	// Message: Your ship cannot teleport because it does not have enough fuel for the trip.
	YOUR_SHIP_CANNOT_TELEPORT_BECAUSE_IT_DOES_NOT_HAVE_ENOUGH_FUEL_FOR_THE_TRIP(2491),
	// Message: The $s1 ward has been destroyed! $c2 now has the territory ward.
	THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD(2750),
	// Message: The character that acquired $s1's ward has been killed.
	THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED(2751),
	// Message: To maintain balance in the game the team was changed.
	THE_TEAM_WAS_ADJUSTED_BECAUSE_THE_POPULATION_RATIO_WAS_NOT_CORRECT(2703),
	// Message: You cannot enter because you do not meet the requirements.
	YOU_CANNOT_ENTER_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2706),
	// Message: During the 10-sec. after cancellation of registration in the competition you can not try again.
	YOU_CANNOT_MAKE_ANOTHER_REQUEST_FOR_10_SECONDS_AFTER_CANCELLING_A_MATCH_REGISTRATION(2707),
	// Message: You cannot register while in possession of a cursed weapon.
	YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON(2708),
	// Message: Applicants for the Olympiad, Underground Coliseum, or Kratei's Cube matches cannot register.
	APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEIS_CUBE_MATCHES_CANNOT_REGISTER(2709),
	// Message: Current location: $s1, $s2, $s3 (near the Keucereus Alliance Base)
	CURRENT_LOCATION_NEAR_KEUCEREUS_ALLIANCE_BASE(2710),
	// Message: Current location: $s1, $s2, $s3 (inside the Seed of Infinity)
	CURRENT_LOCATION_INSIDE_SEED_OF_INFINITY(2711),
	// Message: Current location: $s1, $s2, $s3 (outside the Seed of Infinity)
	CURRENT_LOCATION_OUTSIDE_SEED_OF_INFINITY(2712),
	// Message: Current location: $s1, $s2, $s3 (inside Aerial Cleft)
	CURRENT_LOCATION_INSIDE_AERIAL_CLEFT(2716),
	// Message: You cannot board because you do not meet the requirements.
	YOU_CANNOT_BOARD_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2727),
	// Message: You cannot control the helm while transformed.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED(2729),
	// Message: You cannot control the helm while you are petrified.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED(2730),
	// Message: You cannot control the helm when you are dead.
	YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD(2731),
	// Message: You cannot control the helm while fishing.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING(2732),
	// Message: You cannot control the helm while in a battle.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE(2733),
	// Message: You cannot control the helm while in a duel.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL(2734),
	// Message: You cannot control the helm while in a sitting position.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION(2735),
	// Message: You cannot control the helm while using a skill.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL(2736),
	// Message: You cannot control the helm while a cursed weapon is equipped.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED(2737),
	// Message: You cannot control the helm while holding a flag.
	YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG(2738),
	// Message: You cannot control the helm because you do not meet the requirements.
	YOU_CANNOT_CONTROL_THE_HELM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2739),
	// Message: This action is prohibited while steering.
	THIS_ACTION_IS_PROHIBITED_WHILE_STEERING(2740),
	// Message: This type of attack is prohibited when allied troops are the target.
	THIS_TYPE_OF_ATTACK_IS_PROHIBITED_WHEN_ALLIED_TROOPS_ARE_THE_TARGET(2753),
	// Message: You cannot be simultaneously registered for PVP matches such as the Olympiad, Underground Coliseum, Aerial Cleft, Kratei's Cube, and Handy's Block Checkers.
	YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS(2754),
	// Message: Your account has been suspended from all game services.\nFor more information, please visit the PlayNC website Customer's Center (http://us.ncsol2f.com).
	YOUR_ACCOUNT_HAS_BEEN_SUSPENDED_FROM_ALL_GAME_SERVICES(2055),
	// Message:
	C1_RESISTED_C2S_MAGIC(2269),
	// Message: Another player is probably controlling the target.
	ANOTHER_PLAYER_IS_PROBABLY_CONTROLLING_THE_TARGET(2756),
	// Message: You have blocked $c1.
	YOU_HAVE_BLOCKED_C1(2057),
	// Message: You already polymorphed and cannot polymorph again.
	YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN(2058),
	// Message: The nearby area is too narrow for you to polymorph. Please move to another area and try to polymorph again.
	THE_NEARBY_AREA_IS_TOO_NARROW_FOR_YOU_TO_POLYMORPH(2059),
	// Message: You cannot polymorph into the desired form in water.
	YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER(2060),
	// Message: You must target the one you wish to control.
	YOU_MUST_TARGET_THE_ONE_YOU_WISH_TO_CONTROL(2761),
	// Message: You cannot control because you are too far.
	YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR(2762),
	// Message: The effect of territory ward is disappearing.
	THE_EFFECT_OF_TERRITORY_WARD_IS_DISAPPEARING(2776),
	// Message: The airship summon license has been entered. Your clan can now summon the airship.
	THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED(2777),
	// Message: You cannot teleport while in possession of a ward.
	YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD(2778),
	// Message: Mercenary participation is requested in $s1 territory.
	MERCENARY_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY(2788),
	// Message: Mercenary participation request is cancelled in $s1 territory.
	MERCENARY_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY(2789),
	// Message: Clan participation is requested in $s1 territory.
	CLAN_PARTICIPATION_IS_REQUESTED_IN_S1_TERRITORY(2790),
	// Message: Clan participation request is cancelled in $s1 territory.
	CLAN_PARTICIPATION_REQUEST_IS_CANCELLED_IN_S1_TERRITORY(2791),
	// Message: You must have a minimum of ($s1) people to enter this Instant Zone. Your request for entry is denied.
	YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANT_ZONE(2793),
	// Message: The territory war channel and functions will now be deactivated.
	THE_TERRITORY_WAR_CHANNEL_AND_FUNCTIONS_WILL_NOW_BE_DEACTIVATED(2794),
	// Message: You've already requested a territory war in another territory elsewhere.
	YOUVE_ALREADY_REQUESTED_A_TERRITORY_WAR_IN_ANOTHER_TERRITORY_ELSEWHERE(2795),
	// Message: The clan who owns the territory cannot participate in the territory war as mercenaries.
	THE_CLAN_WHO_OWNS_THE_TERRITORY_CANNOT_PARTICIPATE_IN_THE_TERRITORY_WAR_AS_MERCENARIES(2796),
	// Message: It is not a territory war registration period, so a request cannot be made at this time.
	IT_IS_NOT_A_TERRITORY_WAR_REGISTRATION_PERIOD_SO_A_REQUEST_CANNOT_BE_MADE_AT_THIS_TIME(2797),
	// Message: The territory war will end in $s1-hour(s).
	THE_TERRITORY_WAR_WILL_END_IN_S1HOURS(2798),
	// Message: The territory war will end in $s1-minute(s).
	THE_TERRITORY_WAR_WILL_END_IN_S1MINUTES(2799),
	// Message: $s1-second(s) to the end of territory war!
	S1_SECONDS_TO_THE_END_OF_TERRITORY_WAR(2900),
	// Message: You cannot force attack a member of the same territory.
	YOU_CANNOT_FORCE_ATTACK_A_MEMBER_OF_THE_SAME_TERRITORY(2901),
	// Message: You've acquired the ward. Move quickly to your forces' outpost.
	YOUVE_ACQUIRED_THE_WARD(2902),
	// Message: Territory war has begun.
	TERRITORY_WAR_HAS_BEGUN(2903),
	// Message: Territory war has ended.
	TERRITORY_WAR_HAS_ENDED(2904),
	// Message: You've requested $c1 to be on your Friends List.
	YOUVE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST(2911),
	// Message: Clan $s1 has succeeded in capturing $s2's territory ward.
	CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD(2913),
	// Message: The territory war will begin in 20 minutes! Territory related functions (i.e.: battlefield channel, Disguise Scrolls, Transformations, etc...) can now be used.
	THE_TERRITORY_WAR_WILL_BEGIN_IN_20_MINUTES(2914),
	// Message: This clan member cannot withdraw or be expelled while participating in a territory war.
	THIS_CLAN_MEMBER_CANNOT_WITHDRAW_OR_BE_EXPELLED_WHILE_PARTICIPATING_IN_A_TERRITORY_WAR(2915),
	// Message: Only characters who are level 40 or above who have completed their second class transfer can register in a territory war.
	ONLY_CHARACTERS_WHO_ARE_LEVEL_40_OR_ABOVE_WHO_HAVE_COMPLETED_THEIR_SECOND_CLASS_TRANSFER_CAN_REGISTER_IN_A_TERRITORY_WAR(2918),
	// Message: The disguise scroll cannot be used because it is meant for use in a different territory.
	THE_DISGUISE_SCROLL_CANNOT_BE_USED_BECAUSE_IT_IS_MEANT_FOR_USE_IN_A_DIFFERENT_TERRITORY(2936),
	// Message: A territory owning clan member cannot use a disguise scroll.
	A_TERRITORY_OWNING_CLAN_MEMBER_CANNOT_USE_A_DISGUISE_SCROLL(2937),
	// Message: The disguise scroll cannot be used while you are engaged in a private store or manufacture workshop.
	THE_DISGUISE_SCROLL_CANNOT_BE_USED_WHILE_YOU_ARE_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE_WORKSHOP(2938),
	// Message: A disguise cannot be used when you are in a chaotic state.
	A_DISGUISE_CANNOT_BE_USED_WHEN_YOU_ARE_IN_A_CHAOTIC_STATE(2939),
	// Message: The territory war exclusive disguise and transformation can be used 20 minutes before the start of the territory war to 10 minutes after its end.
	THE_TERRITORY_WAR_EXCLUSIVE_DISGUISE_AND_TRANSFORMATION_CAN_BE_USED_20_MINUTES_BEFORE_THE_START_OF_THE_TERRITORY_WAR_TO_10_MINUTES_AFTER_ITS_END(2955),
	// Message: A character born on February 29 will receive a gift on February 28.
	A_CHARACTER_BORN_ON_FEBRUARY_29_WILL_RECEIVE_A_GIFT_ON_FEBRUARY_28(2957),
	// Message: An Agathion has already been summoned.
	AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED(2958),
	// Message: Your account has been temporarily suspended for playing the game in abnormal ways. If you did not use abnormal means, please visit the Support Center on the NCsoft
	// website (http://us.ncsol2f.com/support).
	YOUR_ACCOUNT_HAS_BEEN_TEMPORARILY_SUSPENDED_FOR_PLAYING_THE_GAME_IN_ABNORMAL_WAYS(2959),
	// Message: The item $s1 is required.
	THE_ITEM_S1_IS_REQUIRED(2960),
	// Message: $s2 unit(s) of the item $s1 is/are required.
	S2_UNITS_OF_THE_ITEM_S1_ISARE_REQUIRED(2961),
	// Message: This item cannot be used in the current transformation state.
	THIS_ITEM_CANNOT_BE_USED_IN_THE_CURRENT_TRANSFORMATION_STATE(2962),
	// Message: The opponent has not equipped $s1, so $s2 cannot be used.
	THE_OPPONENT_HAS_NOT_EQUIPPED_S1_SO_S2_CANNOT_BE_USED(2963),
	// Message: Being appointed as a Noblesse will cancel all related quests. Do you wish to continue?
	BEING_APPOINTED_AS_A_NOBLESSE_WILL_CANCEL_ALL_RELATED_QUESTS(2964),
	// Message: You cannot purchase and re-purchase the same type of item at the same time.
	YOU_CANNOT_PURCHASE_AND_REPURCHASE_THE_SAME_TYPE_OF_ITEM_AT_THE_SAME_TIME(2965),
	// Message: It's a Payment Request transaction. Please attach the item.
	ITS_A_PAYMENT_REQUEST_TRANSACTION(2966),
	// Message: You are attempting to send mail. Do you wish to proceed?
	YOU_ARE_ATTEMPTING_TO_SEND_MAIL(2967),
	// Message: The mail limit (240) has been exceeded and this cannot be forwarded.
	THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED(2968),
	// Message: The previous mail was forwarded less than 1 minute ago and this cannot be forwarded.
	THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED(2969),
	// Message: You cannot forward in a non-peace zone location.
	YOU_CANNOT_FORWARD_IN_A_NONPEACE_ZONE_LOCATION(2970),
	// Message: You cannot forward during an exchange.
	YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE(2971),
	// Message: You cannot forward because the private shop or workshop is in progress.
	YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2972),
	// Message: You cannot forward during an item enhancement or attribute enhancement.
	YOU_CANNOT_FORWARD_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2973),
	// Message: The item that you're trying to send cannot be forwarded because it isn't proper.
	THE_ITEM_THAT_YOURE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISNT_PROPER(2974),
	// Message: You cannot forward because you don't have enough adena.
	YOU_CANNOT_FORWARD_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA(2975),
	// Message: You cannot receive in a non-peace zone location.
	YOU_CANNOT_RECEIVE_IN_A_NONPEACE_ZONE_LOCATION(2976),
	// Message: You cannot receive during an exchange.
	YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE(2977),
	// Message: You cannot receive because the private shop or workshop is in progress.
	YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2978),
	// Message: You cannot receive during an item enhancement or attribute enhancement.
	YOU_CANNOT_RECEIVE_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2979),
	// Message: You cannot receive because you don't have enough adena.
	YOU_CANNOT_RECEIVE_BECAUSE_YOU_DONT_HAVE_ENOUGH_ADENA(2980),
	// Message: You could not receive because your inventory is full.
	YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL(2981),
	// Message: You cannot cancel in a non-peace zone location.
	YOU_CANNOT_CANCEL_IN_A_NONPEACE_ZONE_LOCATION(2982),
	// Message: You cannot cancel during an exchange.
	YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE(2983),
	// Message: You cannot cancel because the private shop or workshop is in progress.
	YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_SHOP_OR_WORKSHOP_IS_IN_PROGRESS(2984),
	// Message: You cannot cancel during an item enhancement or attribute enhancement.
	YOU_CANNOT_CANCEL_DURING_AN_ITEM_ENHANCEMENT_OR_ATTRIBUTE_ENHANCEMENT(2985),
	// Message: Please set the amount of adena to send.
	PLEASE_SET_THE_AMOUNT_OF_ADENA_TO_SEND(2986),
	// Message: Please set the amount of adena to receive.
	PLEASE_SET_THE_AMOUNT_OF_ADENA_TO_RECEIVE(2987),
	// Message: You could not cancel receipt because your inventory is full.
	YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL(2988),
	// Message: Vitamin item $s1 is being used.
	VITAMIN_ITEM_S1_IS_BEING_USED(2989),
	// Message: $2 units of vitamin item $s1 was consumed.
	_2_UNITS_OF_VITAMIN_ITEM_S1_WAS_CONSUMED(2990),
	// Message: True input must be entered by someone over 15 years old.
	TRUE_INPUT_MUST_BE_ENTERED_BY_SOMEONE_OVER_15_YEARS_OLD(2991),
	// Message: Please choose the 2nd stage type.
	PLEASE_CHOOSE_THE_2ND_STAGE_TYPE(2992),
	// Message: If the Command Channel leader leaves the party matching room, then the sessions ends. Do you really wish to exit the room?
	IF_THE_COMMAND_CHANNEL_LEADER_LEAVES_THE_PARTY_MATCHING_ROOM_THEN_THE_SESSIONS_ENDS(2993),
	// Message: The Command Channel matching room was cancelled.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED(2994),
	// Message: You cannot enter the Command Channel matching room because you do not meet the requirements.
	YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS(2996),
	// Message: You exited from the Command Channel matching room.
	YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM(2997),
	// Message: You were expelled from the Command Channel matching room.
	YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM(2998),
	// Message: The Command Channel affiliated party's party member cannot use the matching screen.
	THE_COMMAND_CHANNEL_AFFILIATED_PARTYS_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN(2999),
	// Message: The Command Channel matching room was created.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED(3000),
	// Message: The Command Channel matching room information was edited.
	THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED(3001),
	// Message: When the recipient doesn't exist or the character has been deleted, sending mail is not possible.
	WHEN_THE_RECIPIENT_DOESNT_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE(3002),
	// Message: $c1 entered the Command Channel matching room.
	C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM(3003),
	// Message: I'm sorry to give you a satisfactory response.\n\nIf you send your comments regarding the unsatisfying parts, we will be able to provide even greater service.\n\nPlease
	// send us your comments.
	IM_SORRY_TO_GIVE_YOU_A_SATISFACTORY_RESPONSE(3004),
	// Message: This skill cannot be enhanced.
	THIS_SKILL_CANNOT_BE_ENHANCED(3005),
	// Message: Newly used PC cafe $s1 points were withdrawn.
	NEWLY_USED_PC_CAFE_S1_POINTS_WERE_WITHDRAWN(3006),
	// Message: Shyeed's roar filled with wrath rings throughout the Stakato Nest.
	SHYEEDS_ROAR_FILLED_WITH_WRATH_RINGS_THROUGHOUT_THE_STAKATO_NEST(3007),
	// Message: The mail has arrived.
	THE_MAIL_HAS_ARRIVED(3008),
	// Message: Mail successfully sent.
	MAIL_SUCCESSFULLY_SENT(3009),
	// Message: Mail successfully returned.
	MAIL_SUCCESSFULLY_RETURNED(3010),
	// Message: Mail successfully cancelled.
	MAIL_SUCCESSFULLY_CANCELLED(3011),
	// Message: Mail successfully received.
	MAIL_SUCCESSFULLY_RECEIVED(3012),
	// Message: $c1 has successfully enchanted a +$s2 $s3.
	C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3(3013),
	// Message: Do you wish to erase the selected mail?
	DO_YOU_WISH_TO_ERASE_THE_SELECTED_MAIL(3014),
	// Message: Please select the mail to be deleted.
	PLEASE_SELECT_THE_MAIL_TO_BE_DELETED(3015),
	// Message: Item selection is possible up to 8.
	ITEM_SELECTION_IS_POSSIBLE_UP_TO_8(3016),
	// Message: You cannot use any skill enhancing system under your status. Check out the PC's current status.
	YOU_CANNOT_USE_ANY_SKILL_ENHANCING_SYSTEM_UNDER_YOUR_STATUS(3017),
	// Message: You cannot use skill enhancing system functions for the skills currently not acquired.
	YOU_CANNOT_USE_SKILL_ENHANCING_SYSTEM_FUNCTIONS_FOR_THE_SKILLS_CURRENTLY_NOT_ACQUIRED(3018),
	// Message: You cannot send a mail to yourself.
	YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF(3019),
	// Message: When not entering the amount for the payment request, you cannot send any mail.
	WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL(3020),
	// Message: Stand-by for the game to begin
	STANDBY_FOR_THE_GAME_TO_BEGIN(3021),
	// Message: The Kasha's Eye gives you a strange feeling.
	THE_KASHAS_EYE_GIVES_YOU_A_STRANGE_FEELING(3022),
	// Message: I can feel that the energy being flown in the Kasha's eye is getting stronger rapidly.
	I_CAN_FEEL_THAT_THE_ENERGY_BEING_FLOWN_IN_THE_KASHAS_EYE_IS_GETTING_STRONGER_RAPIDLY(3023),
	// Message: Kasha's eye pitches and tosses like it's about to explode.
	KASHAS_EYE_PITCHES_AND_TOSSES_LIKE_ITS_ABOUT_TO_EXPLODE(3024),
	// Message: $s2 has made a payment of $s1 Adena per your payment request mail.
	S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL(3025),
	// Message: You cannot use the skill enhancing function on this level. You can use the corresponding function on levels higher than 76Lv .
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_ON_THIS_LEVEL(3026),
	// Message: You cannot use the skill enhancing function in this class. You can use corresponding function when completing the third class change.
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS(3027),
	// Message: You cannot use the skill enhancing function in this class. You can use the skill enhancing function under off-battle status, and cannot use the function while
	// transforming, battling and on-board.
	YOU_CANNOT_USE_THE_SKILL_ENHANCING_FUNCTION_IN_THIS_CLASS_(3028),
	// Message: $s1 returned the mail.
	S1_RETURNED_THE_MAIL(3029),
	// Message: You cannot cancel sent mail since the recipient received it.
	YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT(3030),
	// Message: By using the skill of Einhasad's holy sword, defeat the evil Lilims!
	BY_USING_THE_SKILL_OF_EINHASADS_HOLY_SWORD_DEFEAT_THE_EVIL_LILIMS(3031),
	// Message: In order to help Anakim, activate the sealing device of the Emperor who is possessed by the evil magical curse! Magical curse is very powerful, so we must be careful!
	IN_ORDER_TO_HELP_ANAKIM_ACTIVATE_THE_SEALING_DEVICE_OF_THE_EMPEROR_WHO_IS_POSSESSED_BY_THE_EVIL_MAGICAL_CURSE_MAGICAL_CURSE_IS_VERY_POWERFUL_SO_WE_MUST_BE_CAREFUL(3032),
	// Message: By using the invisible skill, sneak into the Dawn's document storage!
	BY_USING_THE_INVISIBLE_SKILL_SNEAK_INTO_THE_DAWNS_DOCUMENT_STORAGE(3033),
	// Message: The door in front of us is the entrance to the Dawn's document storage! Approach to the Code Input Device!
	THE_DOOR_IN_FRONT_OF_US_IS_THE_ENTRANCE_TO_THE_DAWNS_DOCUMENT_STORAGE_APPROACH_TO_THE_CODE_INPUT_DEVICE(3034),
	// Message: My power's weakening. Please activate the sealing device possessed by Lilith's magical curse!
	MY_POWERS_WEAKENING(3035),
	// Message: You, such a fool! The victory over this war belongs to Shilien!
	YOU_SUCH_A_FOOL_THE_VICTORY_OVER_THIS_WAR_BELONGS_TO_SHILIEN(3036),
	// Message: Male guards can detect the concealment but the female guards cannot.
	MALE_GUARDS_CAN_DETECT_THE_CONCEALMENT_BUT_THE_FEMALE_GUARDS_CANNOT(3037),
	// Message: Female guards notice the disguises from far away better than the male guards do, so beware.
	FEMALE_GUARDS_NOTICE_THE_DISGUISES_FROM_FAR_AWAY_BETTER_THAN_THE_MALE_GUARDS_DO_SO_BEWARE(3038),
	// Message: By using the holy water of Einhasad, open the door possessed by the curse of flames.
	BY_USING_THE_HOLY_WATER_OF_EINHASAD_OPEN_THE_DOOR_POSSESSED_BY_THE_CURSE_OF_FLAMES(3039),
	// Message: By using the Court Magician's Magic Staff, open the door on which the magician's barrier is placed.
	BY_USING_THE_COURT_MAGICIANS_MAGIC_STAFF_OPEN_THE_DOOR_ON_WHICH_THE_MAGICIANS_BARRIER_IS_PLACED(3040),
	// Message: Around fifteen hundred years ago, the lands were riddled with heretics,
	AROUND_FIFTEEN_HUNDRED_YEARS_AGO_THE_LANDS_WERE_RIDDLED_WITH_HERETICS(3041),
	// Message: worshippers of Shilen, the Goddess of Death...
	WORSHIPPERS_OF_SHILEN_THE_GODDESS_OF_DEATH(3042),
	// Message: But a miracle happened at the enthronement of Shunaiman, the first emperor.
	BUT_A_MIRACLE_HAPPENED_AT_THE_ENTHRONEMENT_OF_SHUNAIMAN_THE_FIRST_EMPEROR(3043),
	// Message: Anakim, an angel of Einhasad, came down from the skies,
	ANAKIM_AN_ANGEL_OF_EINHASAD_CAME_DOWN_FROM_THE_SKIES(3044),
	// Message: surrounded by sacred flames and three pairs of wings.
	SURROUNDED_BY_SACRED_FLAMES_AND_THREE_PAIRS_OF_WINGS(3045),
	// Message: Thus empowered, the Emperor launched a war against 'Shilen's People.'
	THUS_EMPOWERED_THE_EMPEROR_LAUNCHED_A_WAR_AGAINST_SHILENS_PEOPLE(3046),
	// Message: The emperor's army led by Anakim attacked 'Shilen's People' relentlessly,
	THE_EMPERORS_ARMY_LED_BY_ANAKIM_ATTACKED_SHILENS_PEOPLE_RELENTLESSLY(3047),
	// Message: but in the end some survivors managed to hide in underground Catacombs.
	BUT_IN_THE_END_SOME_SURVIVORS_MANAGED_TO_HIDE_IN_UNDERGROUND_CATACOMBS(3048),
	// Message: A new leader emerged, Lilith, who sought to summon Shilen from the afterlife,
	A_NEW_LEADER_EMERGED_LILITH_WHO_SOUGHT_TO_SUMMON_SHILEN_FROM_THE_AFTERLIFE(3049),
	// Message: and to rebuild the Lilim army within the eight Necropolises.
	AND_TO_REBUILD_THE_LILIM_ARMY_WITHIN_THE_EIGHT_NECROPOLISES(3050),
	// Message: Now, in the midst of impending war, the merchant of Mammon struck a deal.
	NOW_IN_THE_MIDST_OF_IMPENDING_WAR_THE_MERCHANT_OF_MAMMON_STRUCK_A_DEAL(3051),
	// Message: He supplies Shunaiman with war funds in exchange for protection.
	HE_SUPPLIES_SHUNAIMAN_WITH_WAR_FUNDS_IN_EXCHANGE_FOR_PROTECTION(3052),
	// Message: And right now the document we're looking for is that contract.
	AND_RIGHT_NOW_THE_DOCUMENT_WERE_LOOKING_FOR_IS_THAT_CONTRACT(3053),
	// Message: Finally you're here! I'm Anakim, I need your help.
	FINALLY_YOURE_HERE_IM_ANAKIM_I_NEED_YOUR_HELP(3054),
	// Message: It's the seal devices... I need you to destroy them while I distract Lilith!
	ITS_THE_SEAL_DEVICES(3055),
	// Message: Please hurry. I don't have much time left!
	PLEASE_HURRY(3056),
	// Message: For Einhasad!
	FOR_EINHASAD(3057),
	// Message: Em.bry.o..
	EM(3058),
	// Message: $s1 did not receive it during the waiting time, so it was returned automatically. tntls.
	S1_DID_NOT_RECEIVE_IT_DURING_THE_WAITING_TIME_SO_IT_WAS_RETURNED_AUTOMATICALLY(3059),
	// Message: The sealing device glitters and moves. Activation complete normally!
	THE_SEALING_DEVICE_GLITTERS_AND_MOVES(3060),
	// Message: There comes a sound of opening the heavy door from somewhere.
	THERE_COMES_A_SOUND_OF_OPENING_THE_HEAVY_DOOR_FROM_SOMEWHERE(3061),
	// Message: Do you want to pay $s1 Adena?
	DO_YOU_WANT_TO_PAY_S1_ADENA(3062),
	// Message: Do you really want to forward?
	DO_YOU_REALLY_WANT_TO_FORWARD(3063),
	// Message: You have new mail.
	YOU_HAVE_NEW_MAIL(3064),
	// Message: Current location: Inside the Chamber of Delusion
	CURRENT_LOCATION_INSIDE_THE_CHAMBER_OF_DELUSION(3065),
	// Message: You cannot receive or send mail with attached items in non-peace zone regions.
	YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NONPEACE_ZONE_REGIONS(3066),
	// Message: $s1 canceled the sent mail.
	S1_CANCELED_THE_SENT_MAIL(3067),
	// Message: The mail was returned due to the exceeded waiting time.
	THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME(3068),
	// Message: Do you really want to return this mail to the sender?
	DO_YOU_REALLY_WANT_TO_RETURN_THIS_MAIL_TO_THE_SENDER(3069),
	// Message: Skill not available to be enhanced Check skill's Lv and current PC status.
	SKILL_NOT_AVAILABLE_TO_BE_ENHANCED_CHECK_SKILLS_LV_AND_CURRENT_PC_STATUS(3070),
	// Message: Do you really want to reset? 10,000,000(10 million) Adena will be consumed.
	DO_YOU_REALLY_WANT_TO_RESET_1000000010_MILLION_ADENA_WILL_BE_CONSUMED(3071),
	// Message: $s1 acquired the attached item to your mail.
	S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL(3072),
	// Message: You have acquired $s2 $s1.
	YOU_HAVE_ACQUIRED_S2_S1(3073),
	// Message: A user currently participating in the Olympiad cannot send party and friend invitations.
	A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS(3094),
	// Message: The couple action was denied.
	THE_COUPLE_ACTION_WAS_DENIED(3119),
	// Message: The request cannot be completed because the target does not meet location requirements.
	THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS(3120),
	// Message: The couple action was cancelled.
	THE_COUPLE_ACTION_WAS_CANCELLED(3121),
	// Message: $c1 is already participating in a couple action and cannot be requested for another couple action.
	C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION(3126),
	// Message:
	REQUESTING_APPROVAL_CHANGE_PARTY_LOOT_S1(3135),
	// Message:
	PARTY_LOOT_CHANGE_CANCELLED(3137),
	// Message:
	PARTY_LOOT_CHANGED_S1(3138),
	// Message: You have requested a couple action with $c1.
	YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1(3150),
	// Message: $c1 is set to refuse duel requests and cannot receive a duel request.
	C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST(3169),
	// Current Location S1 S2 S3 Outside the seed of Anihilation
	CURRENT_LOCATION_S1_S2_S3_OUTSIDE_THE_SEED_OF_ANNIHILATION(3170),
	// Message: $s1 was successfully added to your Contact List.
	S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST(3214),
	// Message: The name $s1% doesn't exist. Please try another name.
	THE_NAME_S1__DOESNT_EXIST(3215),
	// Message: The name already exists on the added list.
	THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST(3216),
	// Message: The name is not currently registered.
	THE_NAME_IS_NOT_CURRENTLY_REGISTERED(3217),
	// Message: $s1 was successfully deleted from your Contact List.
	S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST(3219),
	// Message: You cannot add your own name.
	YOU_CANNOT_ADD_YOUR_OWN_NAME(3221),
	// Message: The maximum number of names (100) has been reached. You cannot register any more.
	THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED(3222),
	// Message: The maximum matches you can participate in 1 week is 70.
	THE_MAXIMUM_MATCHES_YOU_CAN_PARTICIPATE_IN_1_WEEK_IS_70(3224),
	// Message: The total number of matches that can be entered in 1 week is 60 class irrelevant individual matches, 30 specific matches, and 10 team matches.
	THE_TOTAL_NUMBER_OF_MATCHES_THAT_CAN_BE_ENTERED_IN_1_WEEK_IS_60_CLASS_IRRELEVANT_INDIVIDUAL_MATCHES_30_SPECIFIC_MATCHES_AND_10_TEAM_MATCHES(3225),
	// Message: MP became 0 and the Arcane Shield is disappearing.
	MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING(3256),
	// Message: You have acquired $s1 EXP (Bonus: $s2) and $s3 SP (Bonus: $s4).
	YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4(3259),
	// Message: You have $s1 match(es) remaining that you can participate in this week ($s2 1 vs 1 Class matches, $s3 1 vs 1 matches, & $s4 3 vs 3 Team matches).
	YOU_HAVE_S1_MATCHES_REMAINING_THAT_YOU_CAN_PARTICIPATE_IN_THIS_WEEK_S2_1_VS_1_CLASS_MATCHES_S3_1_VS_1_MATCHES__S4_3_VS_3_TEAM_MATCHES(3261),
	// Message: There are $s2 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3263),
	// Message: There are $s2 minutes $s3 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3264),
	// Message: There are $s2 hours $s3 minutes $s4 seconds remaining for $s1's re-use time. It is reset every day at 6:30 AM.
	THERE_ARE_S2_HOURS_S3_MINUTES_S4_SECONDS_REMAINING_FOR_S1S_REUSE_TIME(3265),
	// Message: $c1 is set to refuse couple actions and cannot be requested for a couple action.
	C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION(3164),
	// Message:
	YOU_OBTAINED_S1_RECOMMENDS(3207),
	// Message: The angel Nevit has blessed you from above. You are imbued with full Vitality as well as a Vitality Replenishing effect. And should you die, you will not lose Exp!
	THE_ANGEL_NEVIT_HAS_BLESSED_YOU_FROM_ABOVE_YOU_ARE_IMBUED_WITH_FULL_VITALITY_AS_WELL_AS_A_VITALITY_REPLENISHING_EFFECT(3266),
	// Message: You are starting to feel the effects of Nevit's Advent Blessing.
	YOU_ARE_STARTING_TO_FEEL_THE_EFFECTS_OF_NEVITS_BLESSING(3267),
	// Message: You are further infused with the blessings of Nevit! Continue to battle evil wherever it may lurk.
	YOU_ARE_FURTHER_INFUSED_WITH_THE_BLESSINGS_OF_NEVIT_CONTINUE_TO_BATTLE_EVIL_WHEREVER_IT_MAY_LURK(3268),
	// Message: Nevit's Advent Blessing shines strongly from above. You can almost see his divine aura.
	NEVITS_BLESSING_SHINES_STRONGLY_FROM_ABOVE_YOU_CAN_ALMOST_SEE_HIS_DIVINE_AURA(3269),
	// Message: Nevit's Advent Blessing has ended. Continue your journey and you will surely meet his favor again sometime soon.
	NEVITS_BLESSING_HAS_ENDED_CONTINUE_YOUR_JOURNEY_AND_YOU_WILL_SURELY_MEET_HIS_FAVOR_AGAIN_SOMETIME_SOON(3275),
	// Message: The skill has been canceled because you have insufficient Energy.
	THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY(6042),
	// Message: Your energy cannot be replenished because conditions are not met.
	YOUR_ENERGY_CANNOT_BE_REPLENISHED_BECAUSE_CONDITIONS_ARE_NOT_MET(6043),
	// Message: Energy $s1 replenished.
	ENERGY_S1_REPLENISHED(6044),
	// Message: The premium item for this account was aprovied
	THE_PREMIUM_ITEM_FOR_THIS_ACCOUNT_WAS_PROVIDED(6046),
	// Message: The premium item cannot be received because the inventory weightquality mit has been exceeded
	THE_PREMIUM_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED(6047),
	// Message: The premium account has been terminated
	THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED(6048),
	// Message: The number of my teleports slots has been increased
	THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED(2409),
	// Message: You cannot bookmark this location because you do not have a My Teleport Flag.
	YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG(6501),
	// Вы попали в мистическое место.
	YOU_HAVE_ENTERED_A_LAND_WITH_MYSTERIOUS_POWERS(1054), YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_CHATTING_WILL_BE_BLOCKED_FOR_10_MINUTES(2473),
	// Message: You have been reported as an illegal program user, so your party participation will be blocked for 60 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_60_MINUTES(2474),
	// Message: You have been reported as an illegal program user, so your party participation will be blocked for 120 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_120_MINUTES(2475),
	// Message: You have been reported as an illegal program user, so your party participation will be blocked for 180 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_PARTY_PARTICIPATION_WILL_BE_BLOCKED_FOR_180_MINUTES(2476),
	// Message: You have been reported as an illegal program user, so your actions will be restricted for 120 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_120_MINUTES(2477),
	// Message: You have been reported as an illegal program user, so your actions will be restricted for 180 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_MINUTES(2478),
	// Message: You have been reported as an illegal program user, so your actions will be restricted for 180 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_WILL_BE_RESTRICTED_FOR_180_MINUTES_(2479),
	// Message: You have been reported as an illegal program user, so movement is prohibited for 120 minutes.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_MOVEMENT_IS_PROHIBITED_FOR_120_MINUTES(2480), C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_HAS_BEEN_INVESTIGATED(2481), // Персонаж
																																														// $c1
																																														// сообщил
																																														// о
																																														// том,
																																														// что
																																														// Вы
																																														// используете
																																														// нелегальную
																																														// программу,
																																														// поэтому
																																														// Вы
																																														// не
																																														// сможете
																																														// получить
																																														// награду.
	C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY(2482), // Персонаж $c1 сообщил о том, что Вы используете нелегальную программу, поэтому Вы не можете вступить
																					// в группу.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CHATTING_IS_NOT_ALLOWED(2483), // Получено сообщение о том, что Вы используете нелегальную программу, поэтому чат запрещен.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED(2484), // Получено сообщение о том, что Вы используете нелегальную программу, поэтому
																											// Вы не сможете вступить в группу.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_YOUR_ACTIVITIES_ARE_ONLY_ALLOWED_WITHIN(2485), // Получено сообщение о том, что Вы используете нелегальную программу, поэтому
																											// Вы будете ограничены в движениях.
	YOU_HAVE_BEEN_BLOCKED_DUE_TO_VERIFICATION_THAT_YOU_ARE_USING_A_THIRD_PARTY_PROGRAM_SUBSEQUENT(2486), // До сих пор Вы получали штрафы за использование нелегальных программ в
																											// соответствии с количеством имеющихся у Вас очков. Со следующего раза штраф
																											// будет предусматривать не только игровые штрафы,

	// Message: This character cannot make a report. You cannot make a report while located inside a peace zone or a battlefield, while you are an opposing clan member during a clan
	// war, or while participating in the Olympiad.
	THIS_CHARACTER_CANNOT_MAKE_A_REPORT(2470),
	// Message: This character cannot make a report. The target has already been reported by either your clan or alliance, or has already been reported from your current IP.
	THIS_CHARACTER_CANNOT_MAKE_A_REPORT_(2471),
	// Message: You cannot report when a clan war has been declared.
	CANNOT_REPORT_TARGET_IN_CLAN_WAR(2378),
	// Message: Instant zone: $s1's entry has been restricted. You can check the next possible entry time by using the command "/instancezone."
	INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE(2720),
	// Message: You have been reported as an illegal program user and cannot report other users.
	YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_REPORT_OTHER_USERS(2748),
	// Message: You can make another report in $s1-minute(s). You have $s2 points remaining on this account.
	YOU_CAN_REPORT_IN_S1_MINUTES_S2_REPORT_POINTS_REMAIN_IN_ACCOUNT(2774),
	// Вы ушли из мистического места.
	YOU_HAVE_LEFT_THE_LAND_WHICH_HAS_MYSTERIOUS_POWERS(1055), COUPLE_ACTION_CANNOT_C1_TARGET_IN_ANOTHER_COUPLE_ACTION(3126);

	private final L2GameServerPacket _message;
	private final int _id;
	private final byte _size;

	SystemMsg(int i)
	{
		this._id = i;

		if (this.name().contains("S4") || this.name().contains("C4"))
		{
			this._size = 4;
			this._message = null;
		}
		else if (this.name().contains("S3") || this.name().contains("C3"))
		{
			this._size = 3;
			this._message = null;
		}
		else if (this.name().contains("S2") || this.name().contains("C2"))
		{
			this._size = 2;
			this._message = null;
		}
		else if (this.name().contains("S1") || this.name().contains("C1"))
		{
			this._size = 1;
			this._message = null;
		}
		else
		{
			this._size = 0;
			this._message = new SystemMessage2(this);
		}
	}

	public int getId()
	{
		return this._id;
	}

	public byte size()
	{
		return this._size;
	}

	public static SystemMsg valueOf(int id)
	{
		for (SystemMsg m : values())
		{
			if (m.getId() == id)
			{
				return m;
			}
		}

		throw new NoSuchElementException("Not find SystemMsg by id: " + id);
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		if (this._message == null)
		{
			throw new NoSuchElementException("Running SystemMsg.packet(Player), but message require arguments: " + this.name());
		}

		return this._message;
	}
}
