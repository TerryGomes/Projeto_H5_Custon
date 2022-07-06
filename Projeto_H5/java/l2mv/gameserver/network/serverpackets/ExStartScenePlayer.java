package l2mv.gameserver.network.serverpackets;

import l2mv.gameserver.network.serverpackets.components.SceneMovie;

public class ExStartScenePlayer extends L2GameServerPacket
{
	private final int _sceneId;

	public static final int SCENE_LINDVIOR = 1;
	public static final int SCENE_ECHMUS_OPENING = 2;
	public static final int SCENE_ECHMUS_SUCCESS = 3;
	public static final int SCENE_ECHMUS_FAIL = 4;
	public static final int SCENE_TIAT_OPENING = 5;
	public static final int SCENE_TIAT_SUCCESS = 6;
	public static final int SCENE_TIAT_FAIL = 7;
	public static final int SCENE_SSQ_SUSPICIOUS_DEATH = 8;
	public static final int SCENE_SSQ_DYING_MASSAGE = 9;
	public static final int SCENE_SSQ_CONTRACT_OF_MAMMON = 10;
	public static final int SCENE_SSQ_RITUAL_OF_PRIEST = 11;
	public static final int SCENE_SSQ_SEALING_EMPEROR_1ST = 12;
	public static final int SCENE_SSQ_SEALING_EMPEROR_2ND = 13;
	public static final int SCENE_SSQ_EMBRYO = 14;
	public static final int SCENE_BOSS_FREYA_OPENING = 15;
	public static final int SCENE_BOSS_FREYA_PHASE_A = 16;
	public static final int SCENE_BOSS_FREYA_PHASE_B = 17;
	public static final int SCENE_BOSS_KEGOR_INTRUSION = 18;
	public static final int SCENE_BOSS_FREYA_ENDING_A = 19;
	public static final int SCENE_BOSS_FREYA_ENDING_B = 20;
	public static final int SCENE_BOSS_FREYA_FORCED_DEFEAT = 21;
	public static final int SCENE_BOSS_FREYA_DEFEAT = 22;
	public static final int SCENE_ICE_HEAVYKNIGHT_SPAWN = 23;
	public static final int SCENE_SSQ2_HOLY_BURIAL_GROUND_OPENING = 24;
	public static final int SCENE_SSQ2_HOLY_BURIAL_GROUND_CLOSING = 25;
	public static final int SCENE_SSQ2_SOLINA_TOMB_OPENING = 26;
	public static final int SCENE_SSQ2_SOLINA_TOMB_CLOSING = 27;
	public static final int SCENE_SSQ2_ELYSS_NARRATION = 28;
	public static final int SCENE_SSQ2_BOSS_OPENING = 29;
	public static final int SCENE_SSQ2_BOSS_CLOSING = 30;

	public ExStartScenePlayer(int sceneId)
	{
		_sceneId = sceneId;
	}

	public ExStartScenePlayer(SceneMovie scene)
	{
		_sceneId = scene.getId();
	}

	@Override
	protected void writeImpl()
	{
		writeEx(0x99);
		writeD(_sceneId);
	}
}