package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.model.Player;

public class ExBasicActionList extends L2GameServerPacket
{
	private static final int[] BasicActions =
	{
		0, // ​​switch / exit . (/ sit, / stand)
		1, // switch Run / Walk . (/ walk, / run)
		2, // Attack the selected goal (s) . Click while holding the key Ctrl, to force attack . (/ attack, / attackforce)
		3, // Request for trade with the selected player. (/ trade)
		4, // Select the nearest target for attack. (/ targetnext)
		5, // pick up items around . (/ pickup)
		6, // Switch on the target selected player . (/ assist)
		7, // Invite selected player in your group. (/ invite)
		8, // Leave group . (/ leave)
		9, // If you are the group leader , delete the selected player (s) from the group . (/ dismiss)
		10, // Reset the personal shop for sale items . (/ Vendor)
		11, // Display the window " Selection Panel " to find groups or members of your group . (/ partymatching)
		12, // Emotion : greet others. (/ socialhello)
		13, // Emotion : Show that you or someone else won ! (/ Socialvictory)
		14, // Emotion : Inspire your allies (/ socialcharge)
		15, // Your pet or follows you , or left in place.
		16, // Attack target.
		17, // Abort the current action .
		18, // Find nearby objects .
		19, // Removes Pet inventory.
		20, // Use special skill .
		21, // or your minions follow you , or remain in place.
		22, // Attack target.
		23, // Abort the current action .
		24, // Emotion : Reply in the affirmative. (/ socialyes)
		25, // Emotion : Reply negatively. (/ socialno)
		26, // Emotion : bow, as a sign of respect. (/ socialbow)
		27, // Use special skill .
		28, // Reset the personal shop to purchase items . (/ buy)
		29, // Emotion : I do not understand what is happening. (/ socialunaware)
		30, // Emotion : I'm waiting ... (/ socialwaiting)
		31, // Emotion : From a good laugh . (/ sociallaugh)
		32, // Toggle between attack / movement.
		33, // Emotion : Applause . (/ socialapplause)
		34, // Emotion : Show everyone your best dance. (/ socialdance)
		35, // Emotion : I am sad . (/ socialsad)
		36, // Poison Gas Attack .
		37, // Reset the personal studio to create objects using recipes Dwarves fee . (/ dwarvenmanufacture)
		38, // Switch to ride / dismount when you are near a pet that you can ride . (/ mount, / dismount, / mountdismount)
		39, // Friendly exploding corpses.
		40, // Increases score goal (/ evaluate)
		41, // Attack the castle gates , walls or staffs shot from a cannon .
		42, // Returns the damage back to the enemy.
		43, // Attack the enemy , creating a swirling vortex.
		44, // Attack the enemy with a powerful explosion.
		45, // Restores MP summoner .
		46, // Attack the enemy , calling destructive storm .
		47, // At the same time damages the enemy and heal his servant .
		48, // Attack the enemy shot from a cannon .
		49, // Attack in a fit of rage .
		50, // Selected group member becomes its leader . (/ Changepartyleader)
		51, // Create an object using the usual recipe for a fee . (/ Generalmanufacture)
		52, // Removes ties with EP and releases it .
		53, // Move to the target.
		54, // Move to the target.
		55, // record switch to stop recording and repeats. (/ start_videorecording, / end_videorecording, / startend_videorecording)
		56, // Invite a selected target in command channel . (/ channelinvite)
		57, // Displays personal messages and store personal workshop, containing the desired word . (/ findprivatestore)
		58, // Call another player to a duel . (/ duel)
		59, // Cancel the duel means a loss . (/ withdraw)
		60, // Call another group to a duel . (/ partyduel)
		61, // Opens the personal shop for sale packages (/ packagesale)
		62, // Charming posture (/ charm)
		63, // Starts fun and simple mini- game that you can play at any time. (command : / minigame)
		64, // Opens a free teleport , which allows you to freely move between locations with teleporters . (command : / teleportbookmark)
		65, // report suspicious behavior of an object whose actions suggest the use of a bot program.
		66, // Pose " Confusion " (command : / shyness)
		67, // Control the ship
		68, // Termination control of the ship
		69, // Departure ship
		70, // Descent from the ship
		71, // Bow
		72, // Give Five
		73, // Dance Together
		1000, // Attack the castle gates , walls and staffs a powerful blow .
		1001, // Reckless , but powerful attack , use it with caution.
		1002, // To provoke others to attack you .
		1003, // unexpected attack that deals damage and stuns the opponent.
		1004, // Instant significantly increases P. Def. Def . and Mag . Def . Use this skill can not move .
		1005, // Magic Attack
		1006, // Restores HP pet.
		1007, // In case of a successful application temporarily increases the attack power of the group and a chance for a critical hit .
		1008, // Temporarily increases P. Def. Atk . and accuracy of your group.
		1009, // There is a chance to remove the curse from the group members.
		1010, // Increases MP regeneration of your group.
		1011, // Decreases the cooldown spells your team.
		1012, // Removes the curse from your group.
		1013, // Taunt opponent and hit, curse , reducing the P. . Def . and Mag . Def .
		1014, // Provokes to attack many enemies and hit with a curse , lowering their P. . Def . and Mag . Def .
		1015, // Sacrifices HP to regenerate HP selected target.
		1016, // Strikes opponent powerful critical attack.
		1017, // Stunning explosion , causing damage and stunning the enemy.
		1018, // Overlay deadly curse , sucking the enemy's HP .
		1019, // skill number 2 , used Cat
		1020, // skill number 2 used Meow
		1021, // skill number 2 used Kai
		1022, // skill number 2 used Jupiter
		1023, // skill number 2 used Mirage
		1024, // number 2 Skill used Bekarev
		1025, // skill number 2 used Shadow
		1026, // Skill number 1 used Shadow
		1027, // skill number 2 used Hecate
		1028, // Skill number 1 used Resurrection
		1029, // Ability to number 2 , used Resurrection
		1030, // skill number 2 used vicious
		1031, // The King of Cats : A powerful cutting attack . Maximum damage.
		1032, // The King of Cats : Cuts surrounding enemies while rotating in the air. Maximum damage .
		1033, // The King of Cats : Freezes enemies standing close
		1034, // Magnus : Slam hind legs , striking and stunning the enemy. Maximum damage .
		1035, // Magnus : Strikes multiple objectives giant mass of water .
		1036, // Wraithlord : bursts corpse , striking adjacent foes.
		1037, // Wraithlord : The blades in each hand inflict devastating damage. Maximum damage .
		1038, // Curse of the adjacent enemies , poisoning and reducing them soon. Atk .
		1039, // Siege Gun: Fires a missile at a short distance . Consumes 4 units . Gunpowder sparkling .
		1040, // Siege Gun: Fires a shell for a long distance . Consumes 5 units . Sparkling powder.
		1041, // Horrible bite the enemy
		1042, // Scratch enemy with both paws . Causes bleeding .
		1043, // Suppress the enemy with a powerful roar
		1044, // Wakes secret power
		1045, // Decreases the P. . Atk . / Mag . Atk . at nearby enemies.
		1046, // Decreases Speed ​​. Atk . / Sprint . Mag . at nearby enemies.
		1047, // Horrible bite the enemy
		1048, // Brings double damage and stuns the enemy simultaneously .
		1049, // breathe fire in your direction.
		1050, // Suppresses surrounding enemies powerful roar.
		1051, // Increases max . amount of HP.
		1052, // Increases max . number of MP.
		1053, // Temporarily increases Atk. Atk .
		1054, // Temporarily increases speed reading spells.
		1055, // Decreases the MP cost of the selected target. Consumes Runestones .
		1056, // Temporarily increases M. Def . Atk .
		1057, // Rank Temporarily increases critical strike force and magical attacks
		1058, // Temporarily increases critical strike .
		1059, // Increases the critical strike chance
		1060, // Temporarily increases Accuracy
		1061, // A strong attack from an ambush. Can only be used when applying skill "Awakening" .
		1062, // Quick double attack
		1063, // Strong twisting attack does not only damage , but also stuns the enemy.
		1064, // Falling from the sky stones cause damage to enemies.
		1065, // Exits the latent state
		1066, // Friendly thunderous forces
		1067, // Quick magical enemies in sight
		1068, // Attacks multiple enemies by lightning
		1069, // slosh ambush . Can only be used when applying skill "Awakening" .
		1070, // Can not impose positive effects on the wearer. Operates 5 minutes .
		1071, // A strong attack on the facility
		1072, // Powerful penetrating attack on the facility
		1073, // Attack enemies disperse their ranks as a tornado hit
		1074, // Attack the enemy standing in front of a powerful throw spears
		1075, // Victory cry , enhancing their own skills
		1076, // A strong attack on the facility
		1077, // Attack the enemy standing in front of the internal energy
		1078, // Attack front facing enemies using electricity
		1079, // Shouting , enhancing their own skills
		1080, // fast approaching the enemy and inflicts
		1081, // Removes negative effects from the facility
		1082, // recline flame
		1083, // A powerful bite , inflicting damage to the enemy
		1084, // Switches between the attacking / defensive mode
		1086, // Limit the number of positive effects to one
		1087, // Increases dark side to 25
		1088, // Trims important skills
		1089, // Attack the enemy standing in front with the help of the tail.
		1090, // Horrible bite the enemy
		1091, // the enemy plunged in terror and were forced to run from the battlefield.
		1092, // Increases movement speed .
		1093, // Attacks the enemy with a little chance to reduce his speed .
		1094, // considerably increases the speed of the host.
		1095, // Attacks the enemy with a little chance to reduce his speed .
		1096, // Significantly increases the speed of the host , as well as members of his group .
		1097, // Leads host in Seed of Annihilation .
		1098, // Leads master and his group in the Seed of Annihilation .
		5000, // You can pat Rudolph . Fills the scale fidelity of 25%. Not be used during reincarnation !
		5001, // Increases Max . HP, Max . MP and Speed ​​by 20% , resistance to de-buff by 10%. Reuse time : 10 min . When using the skill spent 3 Essences Rose. Can not be used with
				// the Beyond Temptation . Duration : 5 min .
		5002, // Increases Max . HP / MP / CP, P. . Def . and Mag . Def . 30% , Speed ​​by 20 %, P. Def . Atk . 10% , Mag . Atk . by 20% and decreases MP consumption by 15%. Reuse time
				// : 40 min. When using the skill consumes 10 Essences Rose. Duration : 20 min.
		5003, // Strikes enemies power of thunder.
		5004, // Strikes enemies standing near a lightning magic attack .
		5005, // Strikes nearby enemies power of thunder.
		5006, // Do not allow to impose on host any effects . Duration : 5 min .
		5007, // Pet pierces the enemy in deadly attacks.
		5008, // Attacks nearby enemies .
		5009, // thrust the sword into the ranks vperedistoyaschego enemies.
		5010, // Enhances your skills .
		5011, // Attacks the enemy with a powerful blow .
		5012, // Explodes the accumulated energy in the body to series vperedistoyaschego enemies.
		5013, // Fires a shockwave on vperedistoyaschego enemy.
		5014, // Greatly enhances their skills .
		5015, // Change the attacker / auxiliary state pet.
		5016, // Instantly restores the master's HP by 10%. Increases the master's resistnace debuff attacks by 80%, resistance to buff-canceling attacks by 40% speed by 10, p. def by
				// 20%, and m. def by 20% and decreases MP comsmption for all skills by 50% for 1 minute?
	};

	private static final int[] TransformationActions =
	{
		1, // switch Run / Walk . (/ walk, / run)
		2, // Attack the selected goal (s) . Click while holding the key Ctrl, to force attack . (/ attack, / attackforce)
		3, // Request for trade with the selected player. (/ trade)
		4, // Select the nearest target for attack. (/ targetnext)
		5, // pick up items around . (/ pickup)
		6, // Switch on the target selected player . (/ assist)
		7, // Invite selected player in your group. (/ invite)
		8, // Leave group . (/ leave)
		9, // If you are the group leader , delete the selected player (s) from the group . (/ dismiss)
		11, // Display the window " Selection Panel " to find groups or members of your group . (/ partymatching)
		15, // or your pet follows you , or left in place.
		16, // Attack target.
		17, // Abort the current action .
		18, // Find nearby objects .
		19, // Removes Pet inventory.
		21, // or your minions follow you , or remain in place.
		22, // Attack target.
		23, // Abort the current action .
		40, // Increases score goal (/ evaluate)
		50, // Selected group member becomes its leader . (/ Changepartyleader)
		52, // Removes ties with EP and releases it .
		53, // Move to the target.
		54, // Move to the target.
		55, // record switch to stop recording and repeats. (/ start_videorecording, / end_videorecording, / startend_videorecording)
		56, // Invite a selected target in command channel . (/ channelinvite)
		57, // Displays personal messages and store personal workshop, containing the desired word . (/ findprivatestore)
		63, // Starts fun and simple mini- game that you can play at any time. (command : / minigame)
		64, // Opens a free teleport , which allows you to freely move between locations with teleporters . (command : / freeteleport)
		65, // report suspicious behavior of an object whose actions suggest BOT- use program.
		67, // Control the ship
		68, // Termination control of the ship
		69, // Departure ship
		70, // Descent from the ship
		1000, // Attack the castle gates , walls and staffs a powerful blow .
		1001, // Reckless , but powerful attack , use it with caution.
		1002, // To provoke others to attack you .
		1003, // unexpected attack that deals damage and stuns the opponent.
		1004, // Instant significantly increases P. Def. Def . and Mag . Def . Use this skill can not move .
		1005, // Magic Attack
		1006, // Restores HP pet.
		1007, // In case of a successful application temporarily increases the attack power of the group and a chance for a critical hit .
		1008, // Temporarily increases P. Def. Atk . and accuracy of your group.
		1009, // There is a chance to remove the curse from the group members.
		1010, // Increases MP regeneration of your group.
		1011, // Decreases the cooldown spells your team.
		1012, // Removes the curse from your group.
		1013, // Taunt opponent and hit, curse , reducing the P. . Def . and Mag . Def .
		1014, // Provokes to attack many enemies and hit with a curse , lowering their P. . Def . and Mag . Def .
		1015, // Sacrifices HP to regenerate HP selected target.
		1016, // Strikes opponent powerful critical attack.
		1017, // Stunning explosion , causing damage and stunning the enemy.
		1018, // Overlay deadly curse , sucking the enemy's HP .
		1019, // skill number 2 , used Cat
		1020, // skill number 2 used Meow
		1021, // skill number 2 used Kai
		1022, // skill number 2 used Jupiter
		1023, // skill number 2 used Mirage
		1024, // Ability to number 2 , used the Unicorn
		1025, // skill number 2 used Shadow
		1026, // Skill number 1 used Shadow
		1027, // skill number 2 used Hecate
		1028, // Skill number 1 used Resurrection
		1029, // Ability to number 2 , used Resurrection
		1030, // skill number 2 used vicious
		1031, // ​ The King of Cats : A powerful cutting attack . Maximum damage .
		1032, // The King of Cats : Cuts surrounding enemies while rotating in the air. Maximum damage .
		1033, // The King of Cats : Freezes enemies standing close
		1034, // Magnus : Slam hind legs , striking and stunning the enemy. Maximum damage .
		1035, // Magnus : Strikes multiple objectives giant mass of water .
		1036, // Wraithlord : bursts corpse , striking adjacent foes.
		1037, // Wraithlord : The blades in each hand inflict devastating damage. Maximum damage .
		1038, // Curse of the adjacent enemies , poisoning and reducing them soon. Atk .
		1039, // Siege Gun: Fires a missile at a short distance . Consumes 4 units . Gunpowder sparkling .
		1040, // Siege Gun: Fires a shell for a long distance . Consumes 5 units . Sparkling powder.
		1041, // Horrible bite the enemy
		1042, // Scratch enemy with both paws . Causes bleeding .
		1043, // Suppress the enemy with a powerful roar
		1044, // Wakes secret power
		1045, // Decreases the P. . Atk . / Mag . Atk . at nearby enemies.
		1046, // Decreases Speed ​​. Atk . / Sprint . Mag . at nearby enemies.
		1047, // Horrible bite the enemy
		1048, // Brings double damage and stuns the enemy simultaneously .
		1049, // breathe fire in your direction.
		1050, // Suppresses surrounding enemies powerful roar.
		1051, // Increases max . amount of HP.
		1052, // Increases max . number of MP.
		1053, // Temporarily increases Atk. Atk .
		1054, // Temporarily increases speed reading spells.
		1055, // Decreases the MP cost of the selected target. Consumes Runestones .
		1056, // Temporarily increases M. Def . Atk .
		1057, // Rank Temporarily increases critical strike force and magical attacks
		1058, // Temporarily increases critical strike .
		1059, // Increases the critical strike chance
		1060, // Temporarily increases Accuracy
		1061, // A strong attack from an ambush. Can only be used when applying skill "Awakening" .
		1062, // Quick double attack
		1063, // Strong twisting attack does not only damage , but also stuns the enemy.
		1064, // Falling from the sky stones cause damage to enemies.
		1065, // Exits the latent state
		1066, // Friendly thunderous forces
		1067, // Quick magical enemies in sight
		1068, // Attacks multiple enemies by lightning
		1069, // slosh ambush . Can only be used when applying skill "Awakening" .
		1070, // Can not impose positive effects on the wearer. Operates 5 minutes .
		1071, // A strong attack on the facility
		1072, // Powerful penetrating attack on the facility
		1073, // Attack enemies disperse their ranks as a tornado hit
		1074, // Attack the enemy standing in front of a powerful throw spears
		1075, // Victory cry , enhancing their own skills
		1076, // A strong attack on the facility
		1077, // Attack the enemy standing in front of the internal energy
		1078, // Attack front facing enemies using electricity
		1079, // Shouting , enhancing their own skills
		1080, // fast approaching the enemy and inflicts
		1081, // Removes negative effects from the facility
		1082, // recline flame
		1083, // A powerful bite , inflicting damage to the enemy
		1084, // Switches between the attacking / defensive mode
		1086, // Limit the number of positive effects to one
		1087, // Increases dark side to 25
		1088, // Trims important skills
		1089, // Attack the enemy standing in front with the help of the tail.
		1090, // Horrible bite the enemy
		1091, // the enemy plunged in terror and were forced to run from the battlefield.
		1092, // Increases movement speed .
		1093, // Attacks the enemy with a little chance to reduce his speed .
		1094, // considerably increases the speed of the host.
		1095, // Attacks the enemy with a little chance to reduce his speed .
		1096, // Significantly increases the speed of the host , as well as members of his group .
		1097, // Leads host in Seed of Annihilation .
		1098, // Leads master and his group in the Seed of Annihilation .
		5000, // You can pat Rudolph . Fills the scale fidelity of 25%. Not be used during reincarnation !
		5001, // Increases Max . HP, Max . MP and Speed ​​by 20% , resistance to de-buff by 10%. Reuse time : 10 min . When using the skill spent 3 Essences Rose. Can not be used with
				// the Beyond Temptation . Duration : 5 min .
		5002, // Increases Max . HP / MP / CP, P. . Def . and Mag . Def . 30% , Speed ​​by 20 %, P. Def . Atk . 10% , Mag . Atk . by 20% and decreases MP consumption by 15%. Reuse time
				// : 40 min. When using the skill consumes 10 Essences Rose. Duration : 20 min.
		5003, // Strikes enemies power of thunder.
		5004, // Strikes enemies standing near a lightning magic attack .
		5005, // Strikes nearby enemies power of thunder.
		5006, // Do not allow to impose on host any effects . Duration : 5 min .
		5007, // Pet pierces the enemy in deadly attacks.
		5008, // Attacks nearby enemies .
		5009, // thrust the sword into the ranks vperedistoyaschego enemies.
		5010, // Enhances your skills .
		5011, // Attacks the enemy with a powerful blow .
		5012, // Explodes the accumulated energy in the body to series vperedistoyaschego enemies.
		5013, // Fires a shockwave on vperedistoyaschego enemy.
		5014, // Greatly enhances their skills .
		5015, // Change the attacker / auxiliary state pet ..
		5016, // Instantly restores the master's HP by 10%. Increases the master's resistnace debuff attacks by 80%, resistance to buff-canceling attacks by 40% speed by 10, p. def by
				// 20%, and m. def by 20% and decreases MP comsmption for all skills by 50% for 1 minute?
	};

	private final int[] actions;

	public ExBasicActionList(Player activeChar)
	{
		actions = activeChar.getTransformation() == 0 ? BasicActions : TransformationActions;
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x5f);
		writeDD(actions, true);
	}
}