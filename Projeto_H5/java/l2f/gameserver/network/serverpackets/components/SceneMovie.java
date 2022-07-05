package l2f.gameserver.network.serverpackets.components;

import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExStartScenePlayer;
import l2f.gameserver.network.serverpackets.L2GameServerPacket;

public enum SceneMovie implements IStaticPacket
{
	/** Simple camera movement */
	LINDVIOR_SPAWN(1, 45500),

	// Echmus
	/** Simple camera movement */
	ECHMUS_OPENING(2, 62000),
	/** Simple camera movement */
	ECHMUS_SUCCESS(3, 18000),
	/** Simple camera movement */
	ECHMUS_FAIL(4, 17000),

	// Tiat
	/** Simple camera movement */
	TIAT_OPENING(5, 54200),
	/** Simple camera movement */
	TIAT_SUCCESS(6, 26100),
	/** Simple camera movement */
	TIAT_FAIL(7, 24800),

	// Seven Signs Quests
	/** 1 Animation(Dwarf walking, assassin is appearing and killing the dwarf) */
	SSQ_SERIES_OF_DOUBT(8, 26000),
	/** 1 Animation(Priest walking to the altar) */
	SSQ_DYING_MESSAGE(9, 27000),
	/** 7 Scenes with subtitles:
	 * <ul>
	 *     <li>Monsters walking to Rune Castle</li>
	 *     <li>Knight praying to Anakim</li>
	 *     <li>Fight of the knights with the monsters</li>
	 *     <li>Escaping monsters</li>
	 *     <li>Lilith watching</li>
	 *     <li>Knight looking at monks</li>
	 *     <li>King</li>
	 * </ul>
	 */
	SSQ_MAMMONS_CONTRACT(10, 98000),
	/** Simple camera movement */
	SSQ_SECRET_RITUAL_PRIEST(11, 30000),
	/** Simple camera movement */
	SSQ_SEAL_EMPEROR_1(12, 18000),
	/** Simple camera movement */
	SSQ_SEAL_EMPEROR_2(13, 26000),
	/** Simple camera movement */
	SSQ_EMBRYO(14, 28000),

	// Freya
	/** Simple camera movement */
	FREYA_OPENING(15, 53500),
	/** Simple camera movement */
	FREYA_PHASE_CHANGE_A(16, 21100),
	/** Simple camera movement with Subtitles */
	FREYA_PHASE_CHANGE_B(17, 21500),
	/** Simple camera movement with Subtitles */
	KEGOR_INTRUSION(18, 27000),
	/** Simple camera movement */
	FREYA_ENDING_A(19, 16000),
	/** Simple camera movement with Subtitles */
	FREYA_ENDING_B(20, 56000),
	/** Simple camera movement */
	FREYA_FORCED_DEFEAT(21, 21000),
	/** Simple camera movement */
	FREYA_DEFEAT(22, 20500),
	/** Simple camera movement */
	ICE_HEAVY_KNIGHT_SPAWN(23, 7000),

	// High Five Seven Signs Quests
	/** Simple camera movement */
	SSQ2_HOLY_BURIAL_GROUND_OPENING(24, 23000),
	/** Simple camera movement */
	SSQ2_HOLY_BURIAL_GROUND_CLOSING(25, 22000),
	/** Simple camera movement */
	SSQ2_SOLINA_TOMB_OPENING(26, 25000),
	/** Simple camera movement */
	SSQ2_SOLINA_TOMB_CLOSING(27, 15000),
	/** 3 Scenes with subtitles:
	 * <ul>
	 *     <li>Cool Fighting Soldiers</li>
	 *     <li>Cool Death of traitors</li>
	 *     <li>Bad Monk looking at Light</li>
	 * </ul>
	 */
	SSQ2_ELYSS_NARRATION(28, 59000),
	/** Simple camera movement */
	SSQ2_BOSS_OPENING(29, 60000),
	/** Simple camera movement */
	SSQ2_BOSS_CLOSING(30, 60000),

	// Airship
	/** Simple camera movement */
	LANDING_KSERTH_LEFT(1000, 10000),
	/** Simple camera movement */
	LANDING_KSERTH_RIGHT(1001, 10000),
	/** Simple camera movement */
	LANDING_INFINITY(1002, 10000),
	/** Simple camera movement */
	LANDING_DESTRUCTION(1003, 10000),
	/** Simple camera movement */
	LANDING_ANNIHILATION(1004, 15000);

	private final int id;
	private final int duration;
	private final L2GameServerPacket packet;

	SceneMovie(int id, int duration)
	{
		this.id = id;
		this.duration = duration;
		packet = new ExStartScenePlayer(this);
	}

	public int getId()
	{
		return id;
	}

	public int getDuration()
	{
		return duration;
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		return packet;
	}
}
