/*
 * package l2mv.gameserver.model.entity.CCPHelpers;
 * import java.sql.Connection;
 * import java.sql.PreparedStatement;
 * import java.sql.ResultSet;
 * import java.sql.SQLException;
 * import java.util.HashMap;
 * import java.util.Map;
 * import java.util.StringTokenizer;
 * import l2mv.gameserver.Config;
 * import l2mv.gameserver.data.htm.HtmCache;
 * import l2mv.gameserver.database.LoginDatabaseFactory;
 * import l2mv.gameserver.model.Player;
 * import l2mv.gameserver.network.serverpackets.NpcHtmlMessage;
 * import l2mv.gameserver.utils.Util;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * public class CCPPasswordRecover
 * {
 * private static final Logger _log = LoggerFactory.getLogger(CCPPasswordRecover.class);
 * private static final int MINUTES_FOR_CODE_ANSWER = 15;
 * private static final char[] VALID_CHARS = {'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m','@','.','
 * ','!','?','(',')','1','2','3','4','5','6','7','8','9','0'};
 * private static Map<Integer, Long[]> _generatedCodes = new HashMap<>();
 * public static void setupFirstStage(Player player, String[] args)
 * {
 * if (args.length != 4)
 * {
 * player.sendMessage("Fill every needed information!");
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * return;
 * }
 * if (!checkInvalidChars(args[1], true))
 * {
 * player.sendMessage("Question is Invalid!");
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * return;
 * }
 * if (!checkInvalidChars(args[2], true))
 * {
 * player.sendMessage("Answer is Invalid!");
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * return;
 * }
 * if (!checkInvalidChars(args[3], true) || !args[3].contains("@") || !args[3].contains(".") || args[3].endsWith("."))
 * {
 * player.sendMessage("Email is Invalid!");
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * return;
 * }
 * // Sending mail
 * // sendEmail(args[3], "Hello!<br>To complete your Password Recovery setup in website, use the code inside TextBox in game!<br>Code: " + generateCode(player) + "<br>Thank You!");
 * // Stage 2
 * String html = HtmCache.getInstance().getNotNull("command/cfgSPRecoverEmail.htm", player);
 * // html = html.replace("%question%", args[1]);
 * // html = html.replace("%answer%", args[2]);
 * // html = html.replace("%email%", args[3]);
 * sendHtml(player, html);
 * }
 * public static void setupSecondStage(Player player, String[] args)
 * {
 * if (args.length != 5)
 * {
 * player.sendMessage("Invalid Code!");
 * _generatedCodes.remove(player.getObjectId());
 * return;
 * }
 * String code = args[4];
 * Long[] correctCode = _generatedCodes.get(player.getObjectId());
 * if (correctCode == null || (correctCode[0] + MINUTES_FOR_CODE_ANSWER * 60000) < System.currentTimeMillis())
 * {
 * player.sendMessage("Too late! Code is already gone!");
 * _generatedCodes.remove(player.getObjectId());
 * return;
 * }
 * long realCode = -1;
 * try
 * {
 * realCode = Long.parseLong(code);
 * }
 * catch (NumberFormatException e)
 * {
 * realCode = -1;
 * }
 * if (realCode != correctCode[1])
 * {
 * player.sendMessage("Invalid Code!");
 * _generatedCodes.remove(player.getObjectId());
 * return;
 * }
 * saveRecovery(player, args[1], args[2], args[3]);
 * player.sendMessage("Setup finished successfully!");
 * }
 * public static void startPasswordRecover(Player player)
 * {
 * if (!CCPPasswordRecover.isRecoveryCompleted(player.getAccountName()))
 * {
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * }
 * else
 * {
 * CCPPasswordRecover.reset(player, "cfgSPRecoverConfirmQ | 0");
 * }
 * }
 *//**
	* Bypass of Setup Recovery
	* @param player
	* @param text
	*/
/*
 * public static void setup(Player player, String text)
 * {
 * StringTokenizer st = new StringTokenizer(text, "|");
 * String[] args = new String[st.countTokens()];
 * for (int i = 0; i < args.length; i++)
 * args[i] = st.nextToken().trim();
 * String index = args[0].substring(args[0].length() - 1);
 * switch (index)
 * {
 * case "1":
 * CCPPasswordRecover.setupFirstStage(player, args);
 * break;
 * case "2":
 * CCPPasswordRecover.setupSecondStage(player, args);
 * break;
 * }
 * }
 *//**
	* Bypass of resetting password and resetting setup recovery
	* @param player
	* @param text
	*/
/*
 * public static void reset(Player player, String text)
 * {
 * StringTokenizer st = new StringTokenizer(text, "|");
 * String[] args = new String[st.countTokens()];
 * for (int i = 0; i < args.length; i++)
 * args[i] = st.nextToken().trim();
 * String pageIndex = args[0].substring(args[0].length() - 1);
 * String nextPage = args[0] + ".htm";
 * switch (pageIndex)
 * {
 * case "A":
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/" + nextPage, player));
 * break;
 * case "Q":// First Page - Checking account name and opening Question/Answer
 * if (args.length < 2)
 * {
 * player.sendMessage("Incorrect Values!");
 * return;
 * }
 * resetFirstPage(player, args[1], nextPage);
 * break;
 * case "E":// Second Page - Checking Answer, sending email and opening Code
 * if (args.length < 3)
 * {
 * player.sendMessage("Incorrect Values!");
 * return;
 * }
 * resetSecondPage(player, args[1], args[2], nextPage);
 * break;
 * case "P":// Third Page - Checking code, resetting password/Opening Passwords Page
 * if (args.length < 3)
 * {
 * player.sendMessage("Incorrect Values!");
 * return;
 * }
 * resetThirdPage(player, args[1], args[2], nextPage);
 * break;
 * case "F":// Fourth Page - Checking new Passwords
 * if (args.length < 4)
 * {
 * player.sendMessage("Incorrect Values!");
 * return;
 * }
 * chooseNewPasses(player, args[1], new String[]
 * {
 * args[2],
 * args[3]
 * });
 * break;
 * }
 * }
 *//**
	* First Page, Checking password, opening page with Security Question and Answer
	* @param player
	* @param account
	* @param page
	*/
/*
 * public static void resetFirstPage(Player player, String account, String page)
 * {
 * if (page.startsWith("cfgSPPassword"))
 * {
 * if (account == null || account.isEmpty() || !account.matches("[A-Za-z0-9]{4,14}"))
 * {
 * player.sendMessage("Incorrect Account!");
 * return;
 * }
 * if (!isRecoveryCompleted(account))
 * {
 * player.sendMessage("That account didn't Setup Password Recovery System!");
 * return;
 * }
 * }
 * else
 * account = player.getAccountName();
 * String html = HtmCache.getInstance().getNotNull("command/" + page, player);
 * html = html.replace("%question%", getSecurityQuestion(account));
 * html = html.replace("%account%", account);
 * sendHtml(player, html);
 * }
 *//**
	* Second Page, checking Security Answer, Sending Mail with code, opening code page
	* @param player
	* @param account
	* @param answer
	* @param page
	*/
/*
 * public static void resetSecondPage(Player player, String account, String answer, String page)
 * {
 * if (account == null || account.length() < 3)
 * account = player.getAccountName();
 * String[] emailAns = getSecurityEmailAnswer(account);
 * if (answer == null || answer.isEmpty() || !answer.equalsIgnoreCase(emailAns[1]))
 * {
 * player.kick();
 * return;
 * }
 * //String actionDesc = (page.startsWith("cfgSPPassword") ? "To choose new Passwords" : "To setup Password Recovery again");
 * // Sending mail
 * //sendEmail(emailAns[0], "Hello!<br>" + actionDesc + " in website, use the code inside TextBox in game!<br>Code: " + generateCode(player) + "<br>Thank You!");
 * String html = HtmCache.getInstance().getNotNull("command/" + page, player);
 * html = html.replace("%account%", account);
 * sendHtml(player, html);
 * }
 *//**
	* Third Page, Checking if code is correct, going to choose password page OR restarting recovering system
	* @param player
	* @param account
	* @param code
	* @param page
	*/
/*
 * public static void resetThirdPage(Player player, String account, String code, String page)
 * {
 * if (account == null || account.length() < 3)
 * account = player.getAccountName();
 * Long[] correctTimeCode = _generatedCodes.get(player.getObjectId());
 * if (code == null || code.isEmpty() || Long.parseLong(code) != correctTimeCode[1] || (correctTimeCode[0] + MINUTES_FOR_CODE_ANSWER * 60000) < System.currentTimeMillis())
 * {
 * player.sendMessage("Invalid Code!");
 * return;
 * }
 * if (page.startsWith("cfgSPPasswordChangeP"))
 * {
 * String html = HtmCache.getInstance().getNotNull("command/cfgSPPasswordChangeP.htm", player);
 * html = html.replace("%account%", account);
 * sendHtml(player, html);
 * }
 * else if (page.startsWith("cfgSPRecover"))
 * {
 * // Removing Security question, answer and email
 * saveRecovery(player, "", "", "");
 * // Going to choose question, answer and email page
 * sendHtml(player, HtmCache.getInstance().getNotNull("command/cfgSPRecover.htm", player));
 * }
 * }
 *//**
	* @param player
	* @param account
	* @param passes
	*
	*//*
		 * public static void chooseNewPasses(Player player, String account, String[] passes)
		 * {
		 * boolean invalid = false;
		 * if (passes.length != 2)
		 * {
		 * invalid = true;
		 * return;
		 * }
		 * for (int i = 0; i < passes.length; i++)
		 * {
		 * if ((passes[i].length() < 5) || (passes[i].length() > 20))
		 * {
		 * player.sendMessage("Incorrect size of the new password!");
		 * invalid = true;
		 * break;
		 * }
		 * if (!Util.isMatchingRegexp(passes[i], Config.APASSWD_TEMPLATE))
		 * {
		 * player.sendMessage("Incorrect value in new password!");
		 * invalid = true;
		 * break;
		 * }
		 * }
		 * if (invalid)
		 * {
		 * String html = HtmCache.getInstance().getNotNull("command/cfgSPPasswordChangeP.htm", player);
		 * html = html.replace("%account%", account);
		 * sendHtml(player, html);
		 * }
		 * else
		 * {
		 * changeLoginPassword(account, passes[0]);
		 * CCPSecondaryPassword.setSecondaryPassword(player, account, passes[1]);
		 * }
		 * }
		 * // private static long generateCode(Player player)
		 * // {
		 * // StringBuilder codeBuilder = new StringBuilder();
		 * // for (int i = 0; i < 5; i++)
		 * // codeBuilder.append(Rnd.get(9));
		 * //
		 * // long toLong = Long.parseLong(codeBuilder.toString());
		 * //
		 * // Long[] array =
		 * // {
		 * // System.currentTimeMillis(),
		 * // toLong
		 * // };
		 * // _generatedCodes.put(player.getObjectId(), array);
		 * //
		 * // return toLong;
		 * // }
		 * // private static void sendEmail(String email, String message)
		 * // {
		 * // ThreadPoolManager.getInstance().execute(new Runnable()
		 * // {
		 * // @Override
		 * // public void run()
		 * // {
		 * // try
		 * // {
		 * // finalSendMail("L2TalesRecovery", "22446688", email, "", "Website Code!", message);
		 * // }
		 * // catch (MessagingException e)
		 * // {
		 * // _log.error("Error while sending Email, email:" + email + " message:" + message + " ", e);
		 * // }
		 * // }
		 * // });
		 * // }
		 * protected static boolean checkInvalidChars(String s, boolean sizeCheck)
		 * {
		 * if (sizeCheck && (s.length() < 3 || s.length() > 45))
		 * {
		 * return false;
		 * }
		 * char[] chars = s.toLowerCase().toCharArray();
		 * for (int i = 0; i < chars.length; i++)
		 * {
		 * boolean contains = false;
		 * for (char c : VALID_CHARS)
		 * {
		 * if (chars[i] == c)
		 * {
		 * contains = true;
		 * break;
		 * }
		 * }
		 * if (!contains)
		 * {
		 * return false;
		 * }
		 * }
		 * return true;
		 * }
		 * private static void sendHtml(Player player, String html)
		 * {
		 * html = html.replace("%online%", CCPSmallCommands.showOnlineCount());
		 * NpcHtmlMessage msg = new NpcHtmlMessage(0);
		 * msg.setHtml(html);
		 * player.sendPacket(msg);
		 * }
		 * private static String getSecurityQuestion(String accountName)
		 * {
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement =
		 * con.prepareStatement("SELECT securityQuestion FROM accounts WHERE login='" + accountName + "'"); ResultSet rset = statement.executeQuery())
		 * {
		 * while (rset.next())
		 * {
		 * String quest = rset.getString("securityQuestion");
		 * if (quest != null && quest.length() > 0)
		 * {
		 * return quest;
		 * }
		 * }
		 * }
		 * catch (SQLException e)
		 * {
		 * _log.error("Error in isRecoveryCompleted ", e);
		 * }
		 * return null;
		 * }
		 * private static String[] getSecurityEmailAnswer(String accountName)
		 * {
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement =
		 * con.prepareStatement("SELECT securityAnswer, email FROM accounts WHERE login='" + accountName + "'"); ResultSet rset = statement.executeQuery())
		 * {
		 * while (rset.next())
		 * {
		 * String answer = rset.getString("securityAnswer");
		 * String email = rset.getString("email");
		 * return new String[]
		 * {
		 * email,
		 * answer
		 * };
		 * }
		 * }
		 * catch (SQLException e)
		 * {
		 * _log.error("Error in isRecoveryCompleted ", e);
		 * }
		 * return new String[]
		 * {
		 * "",
		 * ""
		 * };
		 * }
		 * public static boolean isRecoveryCompleted(String accountName)
		 * {
		 * boolean contains = false;
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT email FROM accounts WHERE login='" +
		 * accountName + "'"); ResultSet rset = statement.executeQuery())
		 * {
		 * while (rset.next())
		 * {
		 * String email = rset.getString("email");
		 * if (email != null && email.length() > 0)
		 * {
		 * contains = true;
		 * }
		 * }
		 * }
		 * catch (SQLException e)
		 * {
		 * _log.error("Error in isRecoveryCompleted ", e);
		 * }
		 * return contains;
		 * }
		 * private static void saveRecovery(Player player, String question, String answer, String email)
		 * {
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement =
		 * con.prepareStatement("UPDATE accounts SET securityQuestion=?,securityAnswer=?,email=? WHERE login=?"))
		 * {
		 * statement.setString(1, question);
		 * statement.setString(2, answer);
		 * statement.setString(3, email);
		 * statement.setString(4, player.getAccountName());
		 * statement.execute();
		 * }
		 * catch (SQLException e)
		 * {
		 * _log.info("Error while saving Recovery, player:" + player.getName() + " question:" + question + " answer:" + answer + " email:" + email, e);
		 * }
		 * }
		 * private static void changeLoginPassword(String accountName, String newPass)
		 * {
		 * try (Connection con = LoginDatabaseFactory.getInstance().getConnection(); PreparedStatement statement =
		 * con.prepareStatement("UPDATE accounts SET password = ? WHERE login = ?"))
		 * {
		 * statement.setString(1, newPass);
		 * statement.setString(2, accountName);
		 * statement.executeUpdate();
		 * }
		 * catch (Exception e1)
		 * {
		 * e1.printStackTrace();
		 * }
		 * }
		 * // private static void finalSendMail(String username, String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException,
		 * MessagingException
		 * // {
		 * // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		 * // final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		 * //
		 * // // Get a Properties object
		 * // Properties props = System.getProperties();
		 * // props.setProperty("mail.smtps.host", "smtp.gmail.com");
		 * // props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		 * // props.setProperty("mail.smtp.socketFactory.fallback", "false");
		 * // props.setProperty("mail.smtp.port", "465");
		 * // props.setProperty("mail.smtp.socketFactory.port", "465");
		 * // props.setProperty("mail.smtps.auth", "true");
		 * // props.put("mail.smtps.quitwait", "false");
		 * //
		 * // Session session = Session.getInstance(props, null);
		 * //
		 * // // -- Create a new message --
		 * // final MimeMessage msg = new MimeMessage(session);
		 * //
		 * // // -- Set the FROM and TO fields --
		 * // msg.setFrom(new InternetAddress(username + "@gmail.com"));
		 * // msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
		 * //
		 * // if (ccEmail.length() > 0)
		 * // {
		 * // msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
		 * // }
		 * //
		 * // msg.setSubject(title);
		 * // msg.setText(message, StandardCharsets.UTF_8.displayName(), "html");
		 * // msg.setSentDate(new Date());
		 * //
		 * // SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
		 * //
		 * // t.connect("smtp.gmail.com", username, password);
		 * // t.sendMessage(msg, msg.getAllRecipients());
		 * // t.close();
		 * // }
		 * }
		 */