/*
 * package l2mv.gameserver.model.entity.CCPHelpers;
 * import java.nio.charset.StandardCharsets;
 * import java.security.Security;
 * import java.sql.Connection;
 * import java.sql.PreparedStatement;
 * import java.sql.ResultSet;
 * import java.util.ArrayList;
 * import java.util.Date;
 * import java.util.List;
 * import java.util.Map;
 * import java.util.Properties;
 * import java.util.concurrent.ConcurrentHashMap;
 * import javax.mail.Message;
 * import javax.mail.Session;
 * import javax.mail.internet.InternetAddress;
 * import javax.mail.internet.MimeMessage;
 * import l2mv.gameserver.Config;
 * import l2mv.gameserver.ThreadPoolManager;
 * import l2mv.gameserver.data.htm.HtmCache;
 * import l2mv.gameserver.database.LoginDatabaseFactory;
 * import l2mv.gameserver.model.Player;
 * import l2mv.gameserver.network.serverpackets.ShowBoard;
 * import com.sun.mail.smtp.SMTPTransport;
 *//**
	* System to manage account recovering for players ingame
	* Will send a email to the inputted mail with all the accounts related to it
	*
	* @author Synerge
	*//*
		 * public class CCPAccountRecover
		 * {
		 * private static final long RECOVER_PENALTY = 30 * 60 * 1000;
		 * private static Map<String, Long> _recoverPenalties = new ConcurrentHashMap<>();
		 * public static void recoverAccounts(Player player, String email)
		 * {
		 * // Check if this player hwid already requested a recover in the last 30 minutes
		 * final Long penalty = _recoverPenalties.get(Config.ALLOW_HWID_ENGINE ? player.getHWID() : player.getIP());
		 * if (penalty != null && penalty > System.currentTimeMillis())
		 * {
		 * player.sendMessage("You have a penalty of 30 minutes between each account recover!");
		 * sendMainHtml(player);
		 * return;
		 * }
		 * // Remove the penalty after it expires
		 * if (penalty != null)
		 * _recoverPenalties.remove(Config.ALLOW_HWID_ENGINE ? player.getHWID() : player.getIP());
		 * // Retrieve all the accounts and passwords with that email registered
		 * final List<String[]> accounts = new ArrayList<>();
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection();
		 * PreparedStatement statement = con.prepareStatement("SELECT login,password,secondaryPassword FROM accounts WHERE email=?"))
		 * {
		 * statement.setString(1, email);
		 * try (ResultSet rset = statement.executeQuery())
		 * {
		 * while (rset.next())
		 * {
		 * accounts.add(new String[] { rset.getString("login"), rset.getString("password"), rset.getString("secondaryPassword") });
		 * }
		 * }
		 * }
		 * catch (Exception e)
		 * {
		 * player.sendMessage("Email is Invalid!");
		 * sendMainHtml(player);
		 * return;
		 * }
		 * if (accounts.isEmpty())
		 * {
		 * player.sendMessage("The email you entered doesnt exist. Make sure that you wrote it correctly");
		 * sendMainHtml(player);
		 * return;
		 * }
		 * // New penalty
		 * _recoverPenalties.put((Config.ALLOW_HWID_ENGINE ? player.getHWID() : player.getIP()), System.currentTimeMillis() + RECOVER_PENALTY);
		 * // Sending mail
		 * sendEmail(email, accounts);
		 * player.sendMessage("All the accounts registered to that mail were sent to its inbox");
		 * }
		 * public static void sendMainHtml(Player player)
		 * {
		 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgAccountRecover.htm", player));
		 * }
		 * private static void sendHtml(Player player, String html)
		 * {
		 * ShowBoard.separateAndSend(html, player);
		 * final NpcHtmlMessage msg = new NpcHtmlMessage(0);
		 * msg.setHtml(html);
		 * player.sendPacket(msg);
		 * }
		 * private static void sendEmail(String email, List<String[]> accounts)
		 * {
		 * final StringBuilder message = new StringBuilder();
		 * message.append("Accounts recovery requested from the server " + Config.SERVER_NAME + "\n\n");
		 * message.append("Accounts:\n");
		 * for (String[] account : accounts)
		 * {
		 * message.append("User: " + account[0] + "    -    Password: " + account[1] + "    -    Secondary Password: " + account[2] + "\n");
		 * }
		 * // Sends in a thread to not stuck the player
		 * ThreadPoolManager.getInstance().execute(() ->
		 * {
		 * try
		 * {
		 * Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		 * final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		 * // Get a Properties object
		 * Properties props = System.getProperties();
		 * props.setProperty("mail.smtps.host", "smtp.gmail.com");
		 * props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		 * props.setProperty("mail.smtp.socketFactory.fallback", "false");
		 * props.setProperty("mail.smtp.port", "465");
		 * props.setProperty("mail.smtp.socketFactory.port", "465");
		 * props.setProperty("mail.smtps.auth", "true");
		 * props.put("mail.smtps.quitwait", "false");
		 * Session session = Session.getInstance(props, null);
		 * // -- Create a new message --
		 * final MimeMessage msg = new MimeMessage(session);
		 * // -- Set the FROM and TO fields --
		 * msg.setFrom(new InternetAddress(Config.MAIL_USER.isEmpty() ? "support@" + Config.SERVER_NAME + ".com" : Config.MAIL_USER));
		 * msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
		 * msg.setSubject(Config.SERVER_NAME + " Account Recover");
		 * msg.setText(message.toString(), StandardCharsets.UTF_8.displayName(), "html");
		 * msg.setSentDate(new Date());
		 * SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
		 * t.connect("smtp.gmail.com", Config.MAIL_USER, Config.MAIL_PASS);
		 * t.sendMessage(msg, msg.getAllRecipients());
		 * t.close();
		 * }
		 * catch (Exception e)
		 * {
		 * e.printStackTrace();
		 * }
		 * });
		 * }
		 * }
		 */