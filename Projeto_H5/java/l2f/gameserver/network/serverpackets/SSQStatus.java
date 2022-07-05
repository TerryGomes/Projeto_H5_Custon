package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.SevenSigns;
import l2f.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2f.gameserver.templates.StatsSet;

/**
 * Seven Signs Record Update
 *
 * packet type id 0xf5
 * format:
 *
 * c cc	(Page Num = 1 -> 4, period)
 *
 * 1: [ddd cc dd ddd c ddd c]
 * 2: [hc [cd (dc (S))]
 * 3: [ccc (cccc)]
 * 4: [(cchh)]
 */
public class SSQStatus extends L2GameServerPacket
{
	private Player _player;
	private int _page, period;

	public SSQStatus(Player player, int recordPage)
	{
		_player = player;
		_page = recordPage;
		period = SevenSigns.getInstance().getCurrentPeriod();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xfb);

		writeC(_page);
		writeC(period); // current period?

		switch (_page)
		{
		case 1:
			// [ddd cc dd ddd c ddd c] // ddd cc QQ QQQ c QQQ c
			writeD(SevenSigns.getInstance().getCurrentCycle());

			switch (period)
			{
			case SevenSigns.PERIOD_COMP_RECRUITING:
				writeD(1183);
				break;
			case SevenSigns.PERIOD_COMPETITION:
				writeD(1176);
				break;
			case SevenSigns.PERIOD_COMP_RESULTS:
				writeD(1184);
				break;
			case SevenSigns.PERIOD_SEAL_VALIDATION:
				writeD(1177);
				break;
			}

			switch (period)
			{
			case SevenSigns.PERIOD_COMP_RECRUITING:
			case SevenSigns.PERIOD_COMP_RESULTS:
				writeD(1287);
				break;
			case SevenSigns.PERIOD_COMPETITION:
			case SevenSigns.PERIOD_SEAL_VALIDATION:
				writeD(1286);
				break;
			}

			writeC(SevenSigns.getInstance().getPlayerCabal(_player));
			writeC(SevenSigns.getInstance().getPlayerSeal(_player));

			writeQ(SevenSigns.getInstance().getPlayerStoneContrib(_player));
			writeQ(SevenSigns.getInstance().getPlayerAdenaCollect(_player));

			long dawnStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DAWN);
			long dawnFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DAWN);
			long dawnTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DAWN);

			long duskStoneScore = SevenSigns.getInstance().getCurrentStoneScore(SevenSigns.CABAL_DUSK);
			long duskFestivalScore = SevenSigns.getInstance().getCurrentFestivalScore(SevenSigns.CABAL_DUSK);
			long duskTotalScore = SevenSigns.getInstance().getCurrentScore(SevenSigns.CABAL_DUSK);

			long totalStoneScore = duskStoneScore + dawnStoneScore;
			totalStoneScore = totalStoneScore == 0 ? 1 : totalStoneScore; // Prevents divide by zero errors when competition begins.

			/*
			 * Scoring seems to be proportionate to a set base value, so base this on
			 * the maximum obtainable score from festivals, which is 500.
			 */
			long duskStoneScoreProp = Math.round(duskStoneScore * 500. / totalStoneScore);
			long dawnStoneScoreProp = Math.round(dawnStoneScore * 500. / totalStoneScore);

			long totalOverallScore = duskTotalScore + dawnTotalScore;
			totalOverallScore = totalOverallScore == 0 ? 1 : totalOverallScore; // Prevents divide by zero errors when competition begins.

			long dawnPercent = Math.round(dawnTotalScore * 110. / totalOverallScore);
			long duskPercent = Math.round(duskTotalScore * 110. / totalOverallScore);

			/* DUSK */
			writeQ(duskStoneScoreProp); // Seal Stone Score
			writeQ(duskFestivalScore); // Festival Score
			writeQ(duskTotalScore); // Total Score

			writeC((int) duskPercent); // Dusk %

			/* DAWN */
			writeQ(dawnStoneScoreProp); // Seal Stone Score
			writeQ(dawnFestivalScore); // Festival Score
			writeQ(dawnTotalScore); // Total Score

			writeC((int) dawnPercent); // Dawn %
			break;
		case 2:
			// c cc ccc [cdQc(S) Qc]
			writeH(/* SevenSigns.getInstance().isSealValidationPeriod() ? 0 : */1);
			writeC(5); // Total number of festivals

			for (int i = 0; i < 5; i++)
			{
				writeC(i + 1); // Current client-side festival ID
				writeD(SevenSignsFestival.FESTIVAL_LEVEL_SCORES[i]);

				long duskScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DUSK, i);
				long dawnScore = SevenSignsFestival.getInstance().getHighestScore(SevenSigns.CABAL_DAWN, i);

				// Dusk Score \\
				writeQ(duskScore);

				StatsSet highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DUSK, i);

				if (duskScore > 0)
				{
					String[] partyMembers = highScoreData.getString("names").split(",");
					writeC(partyMembers.length);
					for (String partyMember : partyMembers)
					{
						writeS(partyMember);
					}
				}
				else
				{
					writeC(0);
				}

				// Dawn Score \\
				writeQ(dawnScore);

				highScoreData = SevenSignsFestival.getInstance().getHighestScoreData(SevenSigns.CABAL_DAWN, i);

				if (dawnScore > 0)
				{
					String[] partyMembers = highScoreData.getString("names").split(",");
					writeC(partyMembers.length);
					for (String partyMember : partyMembers)
					{
						writeS(partyMember);
					}
				}
				else
				{
					writeC(0);
				}
			}
			break;
		case 3:
			// ccc [cccc]
			writeC(10); // Minimum limit for winning cabal to retain their seal
			writeC(35); // Minimum limit for winning cabal to claim a seal
			writeC(3); // Total number of seals

			int totalDawnProportion = 1;
			int totalDuskProportion = 1;

			for (int i = 1; i <= 3; i++)
			{
				totalDawnProportion += SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
				totalDuskProportion += SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);
			}

			// Prevents divide by zero errors.
			totalDawnProportion = Math.max(1, totalDawnProportion);
			totalDuskProportion = Math.max(1, totalDuskProportion);

			for (int i = 1; i <= 3; i++)
			{
				int dawnProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
				int duskProportion = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);

				writeC(i);
				writeC(SevenSigns.getInstance().getSealOwner(i));
				writeC(duskProportion * 100 / totalDuskProportion);
				writeC(dawnProportion * 100 / totalDawnProportion);
			}
			break;
		case 4:
			// c cc [cc (ccD)] CT 2.3 update

			int winningCabal = SevenSigns.getInstance().getCabalHighestScore();
			writeC(winningCabal); // Overall predicted winner
			writeC(3); // Total number of seals

			int dawnTotalPlayers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DAWN);
			int duskTotalPlayers = SevenSigns.getInstance().getTotalMembers(SevenSigns.CABAL_DUSK);

			for (int i = 1; i < 4; i++)
			{
				writeC(i);

				int dawnSealPlayers = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DAWN);
				int duskSealPlayers = SevenSigns.getInstance().getSealProportion(i, SevenSigns.CABAL_DUSK);
				int dawnProp = dawnTotalPlayers > 0 ? dawnSealPlayers * 100 / dawnTotalPlayers : 0;
				int duskProp = duskTotalPlayers > 0 ? duskSealPlayers * 100 / duskTotalPlayers : 0;
				int curSealOwner = SevenSigns.getInstance().getSealOwner(i);

				if (Math.max(dawnProp, duskProp) < 10) // печать будет потеряна если занята
				{
					writeC(SevenSigns.CABAL_NULL);
					if (curSealOwner == SevenSigns.CABAL_NULL)
					{ // печать останется свободной
						writeD(SystemMessage.SINCE_THE_SEAL_WAS_NOT_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_SINCE_LESS_THAN_35_PERCENT_OF_PEOPLE_HAVE_VOTED);
					}
					else
					{ // печать останется свободной
						// печать будет освобождена
						writeD(SystemMessage.ALTHOUGH_THE_SEAL_WAS_OWNED_DURING_THE_PREVIOUS_PERIOD_BECAUSE_LESS_THAN_10_PERCENT_OF_PEOPLE_HAVE_VOTED);
					}
				}
				else if (Math.max(dawnProp, duskProp) < 35) // печать будет сохранена если занята
				{
					writeC(curSealOwner);
					if (curSealOwner == SevenSigns.CABAL_NULL)
					{ // печать останется свободной
						writeD(SystemMessage.SINCE_THE_SEAL_WAS_NOT_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_SINCE_LESS_THAN_35_PERCENT_OF_PEOPLE_HAVE_VOTED);
					}
					else
					{ // печать останется свободной
						// печать будет сохранена
						writeD(SystemMessage.SINCE_THE_SEAL_WAS_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_10_PERCENT_OR_MORE_PEOPLE_HAVE_VOTED);
					}
				}
				else if (dawnProp == duskProp) // ничья, печать не получит никто
				{
					writeC(SevenSigns.CABAL_NULL);
					writeD(SystemMessage.IF_CURRENT_TRENDS_CONTINUE_IT_WILL_END_IN_A_TIE);
				}
				else
				// у кого-то есть перевес
				{
					int sealWinning = dawnProp > duskProp ? SevenSigns.CABAL_DAWN : SevenSigns.CABAL_DUSK;
					writeC(sealWinning);
					if (sealWinning == curSealOwner)
					{ // состояние не изменится
						writeD(SystemMessage.SINCE_THE_SEAL_WAS_OWNED_DURING_THE_PREVIOUS_PERIOD_AND_10_PERCENT_OR_MORE_PEOPLE_HAVE_VOTED);
					}
					else
					{ // состояние не изменится
						// состояние изменится
						writeD(SystemMessage.ALTHOUGH_THE_SEAL_WAS_NOT_OWNED_SINCE_35_PERCENT_OR_MORE_PEOPLE_HAVE_VOTED);
					}
				}
			}

			break;
		}
		_player = null;
	}
}