/*
 * package l2mv.gameserver.handler.voicecommands.impl;
 * import java.sql.Connection;
 * import java.sql.PreparedStatement;
 * import java.sql.ResultSet;
 * import java.text.DateFormat;
 * import java.text.SimpleDateFormat;
 * import java.util.Date;
 * import java.util.HashMap;
 * import java.util.Map.Entry;
 * import l2mv.gameserver.Config;
 * import l2mv.gameserver.database.DatabaseFactory;
 * import l2mv.gameserver.handler.voicecommands.IVoicedCommandHandler;
 * import l2mv.gameserver.model.AbsServerMail;
 * import l2mv.gameserver.model.Player;
 * import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
 * import l2mv.gameserver.scripts.Functions;
 * public class Donate implements IVoicedCommandHandler
 * {
 * private final HashMap<Integer, Attempt> _commandAttempts = new HashMap<>();
 * private static final String[] COMMANDS =
 * {
 * "donate"
 * };
 * private Attempt getAttempt(Player player)
 * {
 * final Attempt att = _commandAttempts.get(player.getObjectId());
 * if (att == null)
 * {
 * final Attempt att2 = new Attempt();
 * _commandAttempts.put(player.getObjectId(), att2);
 * return att2;
 * }
 * return att;
 * }
 * @Override
 * public boolean useVoicedCommand(String command, Player player, String args)
 * {
 * if (args == null || args.length() == 0)
 * {
 * NpcHtmlMessage html = new NpcHtmlMessage(0);
 * html.setFile("command/donate.htm");
 * player.sendPacket(html);
 * return true;
 * }
 * if (!args.contains("@") || args.contains(" ") || !args.contains("."))
 * {
 * Functions.sendDebugMessage(player, args + " is not a valid email address!");
 * return false;
 * }
 * return retrieveDonation(args, player);
 * }
 * // (1e-9e NO Bonus)
 * // (10€-24€ 10%+ Bonus)
 * // (25€-99€ 15%+ Bonus)
 * // (100€-199€ 20%+ Bonus)
 * // (200€-299€ 25%+ Bonus)
 * // (300€+ 35%+ Bonus)
 * private synchronized final boolean retrieveDonation(String txn_id, Player player)
 * {
 * final Attempt attempt = getAttempt(player);
 * if (!attempt.allowAttempt())
 * {
 * player.sendMessage("You are temporarly bocked for 3 minutes from using this command! example: .donate email@gmail.com");
 * return false;
 * }
 * if (txn_id == null || player == null)
 * return false;
 * try (Connection con = DatabaseFactory.getInstance().getConnection();
 * PreparedStatement st = con.prepareStatement("SELECT * FROM donations WHERE email=? AND retrieved=?"))
 * {
 * st.setString(1, txn_id);
 * st.setString(2, "false");
 * try (ResultSet rs = st.executeQuery())
 * {
 * int amount = 0;
 * while (rs.next())
 * amount += rs.getInt("amount");
 * if (amount > 0)
 * {
 * int bonus = 1;
 * for (Entry<Integer, Integer> bonuses : Config.DONATION_REWARD_BONUSES.entrySet())
 * {
 * if (amount >= bonuses.getKey())
 * {
 * bonus = bonuses.getValue();
 * break;
 * }
 * }
 * amount *= Config.DONATION_REWARD_MULTIPLIER_PER_EURO;
 * final int toGive = amount * (100 + bonus) / 100;
 * if (bonus > 1)
 * player.sendMessage("You have obtained (" + amount + ") Donator Coins. \\n You gain a +" + bonus + "% Bonus(" + (toGive - amount) + "), for a total of (" + toGive + ").");
 * else
 * player.sendMessage("You have obtained " + toGive + " Donator Coins. There is No Bonus for your donation! Check Donation page on our website.");
 * attempt.onAllow();
 * new DonationSuccessMail(player, toGive);
 * try (PreparedStatement st2 = con.prepareStatement("UPDATE donations SET retrieved=?, retriever_ip=?, retriever_acc=?, retriever_char=?, retrieval_date=? WHERE email=?"))
 * {
 * st2.setString(1, "true");
 * st2.setString(2, player.getIP());
 * st2.setString(3, player.getAccountName());
 * st2.setString(4, player.getName());
 * st2.setString(5, formatDate(new Date(), "dd/MM/yyyy H:mm:ss"));
 * st2.setString(6, txn_id);
 * st2.executeUpdate();
 * }
 * return true;
 * }
 * else
 * {
 * attempt.onDeny();
 * if (attempt.getTries() == 3)
 * new DonationBlockedMail(player);
 * else
 * new DonationFailedMail(player);
 * }
 * }
 * }
 * catch (Exception e)
 * {
 * e.printStackTrace();
 * }
 * player.sendMessage("Please try again later or contact an Admin!");
 * return false;
 * }
 * public static String formatDate(Date date, String format)
 * {
 * final DateFormat dateFormat = new SimpleDateFormat(format);
 * if (date != null)
 * return dateFormat.format(date);
 * return null;
 * }
 * private static final class DonationSuccessMail extends AbsServerMail
 * {
 * public DonationSuccessMail(Player player, int toGive)
 * {
 * super(player, Config.DONATION_REWARD_ITEM_ID, toGive);
 * }
 * @Override
 * protected void prepare()
 * {
 * _mail.setTopic("Automatic Donation Success!");
 * _mail.setBody(" Thank you for your Donation! \\n Here are your Donator Coins, in case you need any admin support you can contact us on \\n Skype: apocalyps.eu");
 * }
 * }
 * private static final class DonationFailedMail extends AbsServerMail
 * {
 * public DonationFailedMail(Player player)
 * {
 * super(player, 0, 0);
 * }
 * @Override
 * protected void prepare()
 * {
 * _mail.setTopic("Automatic Donation Failed!");
 * _mail.
 * setBody(" Hello, \\n It looks like your donation is still not listed on our Account! \\n If is a mistake please try again carefully! \\n After 3 wrong tries you will have reuse for 3 minutes on this command! \\n Contact us: \\n Skype: apocalyps.eu "
 * );
 * }
 * }
 * private static final class DonationBlockedMail extends AbsServerMail
 * {
 * public DonationBlockedMail(Player player)
 * {
 * super(player, 0, 0);
 * }
 * @Override
 * protected void prepare()
 * {
 * _mail.setTopic("You are temporarly blocked from using .donate");
 * _mail.
 * setBody(" Hello, \\n You tries  \\n Remember: \\n After 3 wrong tries you will have reuse for 3 minutes on this command! \\n Contact us: \\n Skype: apocalyps.eu \\n E-Mail: admin@MultVerso.ro "
 * );
 * }
 * }
 * public static class Attempt
 * {
 * private int tries;
 * private long banEx;
 * public void onDeny()
 * {
 * if (++tries > 3)
 * {
 * banEx = System.currentTimeMillis() + 180000L;
 * tries = 0;
 * }
 * }
 * public void onAllow()
 * {
 * tries = 0;
 * banEx = 0;
 * }
 * public boolean allowAttempt()
 * {
 * if (banEx > System.currentTimeMillis())
 * return false;
 * return true;
 * }
 * public int getTries()
 * {
 * return tries;
 * }
 * }
 * @Override
 * public String[] getVoicedCommandList()
 * {
 * return COMMANDS;
 * }
 * }
 */