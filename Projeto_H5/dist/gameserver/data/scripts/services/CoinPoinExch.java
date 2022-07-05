package services;

import l2f.gameserver.Config;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.serverpackets.ExBR_GamePoint;
import l2f.gameserver.scripts.Functions;

public class CoinPoinExch extends Functions
{

	public void Show()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		String append = "Currency exchange<br>";
		append += "<br>";

		append += "Respected player!<br>";
		append += "Here you can exchange:<br>";
		append += "Game Coin to balance Item Mall<br>";
		append += "Balance in the Item Mall for Game Coin.<br>";
		append += "Please select a direction:<br>";
		append += "<button value=\"Coin -> ItemMall\" action=\"bypass -h scripts_services.CoinPoinExch:ShowC2P \" width=250 height=15><br>";
		append += "<button value=\"ItemMall -> Coin\" action=\"bypass -h scripts_services.CoinPoinExch:ShowP2C \" width=250 height=15><br>";
		show(append, player, null);
	}

	public void ShowP2C()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (player.getPremiumPoints() < 30)
		{
			String append = "Your balance is too small for execution of this function!";
			show(append, player, null);
			return;
		}

		String append2 = "Exchange rate: 30 balance ItemMall = 1 GameCoin<br>";
		append2 += "Specify the number that you exchange!<br>";
		append2 += "<edit var=\"exch2\" width=70> <br>";
		append2 += "<button value=\"exchange\" action=\"bypass -h scripts_services.CoinPoinExch:DoP2C $exch2\" width=150 height=15><br> <br>";
		show(append2, player, null);

	}

	public void DoP2C(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		String coinsToEx = param[0];
		if (!checkInteger(coinsToEx))
		{
			player.sendMessage("" + player.getName() + ", Write only numbers!");
			return;
		}
		int _coinsToEx = Integer.parseInt(param[0]);

		if (player.getPremiumPoints() < _coinsToEx || _coinsToEx < 30)
		{
			player.sendMessage("" + player.getName() + ", You do not have enough balance to exchange");
			return;
		}

		player.reducePremiumPoints(_coinsToEx);
		player.sendPacket(new ExBR_GamePoint(player));
		double _coinsToExDouble = _coinsToEx / 30;
		int _finalAmmount = (int) Math.ceil(_coinsToExDouble);
		addItem(player, Config._coinID, _finalAmmount, "DoP2C Transfer");
		player.sendMessage("" + player.getName() + ", successfully added " + _finalAmmount + " L2Game Coin");
		player.sendChanges();
	}

	public void ShowC2P()
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		if (player.getInventory().getCountOf(Config._coinID) <= 0)
		{
			String append = "You have no L2Game Coin in inventory!";
			show(append, player, null);
			return;
		}

		String append2 = "Exchange rate: 1 GameCoin = 30 balance ItemMall <br>";
		append2 += "Specify the number that you exchange!<br>";
		append2 += "<edit var=\"exch1\" width=70> <br>";
		append2 += "<button value=\"Exchange\" action=\"bypass -h scripts_services.CoinPoinExch:DoC2P $exch1\" width=150 height=15><br> <br>";
		show(append2, player, null);

	}

	public void DoC2P(String[] param)
	{
		Player player = getSelf();
		if (player == null)
		{
			return;
		}

		String coinsToEx = param[0];
		if (!checkInteger(coinsToEx))
		{
			player.sendMessage("" + player.getName() + ", Write only numbers!");
			return;
		}
		int _coinsToEx = Integer.parseInt(param[0]);

		if (player.getInventory().getCountOf(Config._coinID) < _coinsToEx || _coinsToEx <= 0)
		{
			player.sendMessage("" + player.getName() + ", You do not have enough things to share");
			return;
		}

		removeItem(player, Config._coinID, _coinsToEx, "DoC2P Transfer");
		int finPoint = (_coinsToEx * 30);
		finPoint *= -1;
		player.reducePremiumPoints(finPoint);
		player.sendPacket(new ExBR_GamePoint(player));
		player.sendMessage("" + player.getName() + ", successfully added " + _coinsToEx * 30 + " balance ItemMall");
		player.sendChanges();
	}

	public boolean checkInteger(String number)
	{
		try
		{
			int x = Integer.parseInt(number);
			number = Integer.toString(x);
			return true;
		}
		catch (NumberFormatException e)
		{
		}
		return false;
	}
}