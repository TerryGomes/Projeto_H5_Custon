package l2mv.gameserver.model.mail;

import java.util.HashSet;
import java.util.Set;

import l2mv.commons.dao.JdbcEntity;
import l2mv.commons.dao.JdbcEntityState;
import l2mv.gameserver.dao.MailDAO;
import l2mv.gameserver.model.items.ItemInstance;

public class Mail implements JdbcEntity, Comparable<Mail>
{
	public static enum SenderType
	{
		NORMAL, NEWS_INFORMER, NONE, BIRTHDAY;

		public static SenderType[] VALUES = values();
	}

	private static final long serialVersionUID = -8704970972611917153L;

	public static final int DELETED = 0;
	public static final int READED = 1;
	public static final int REJECTED = 2;

	private static final MailDAO _mailDAO = MailDAO.getInstance();

	private int messageId;
	private int senderId;
	private String senderName;
	private int receiverId;
	private String receiverName;
	private int expireTime;
	private String topic;
	private String body;
	private long price;
	private SenderType _type = SenderType.NORMAL;
	private boolean isUnread;
	private final Set<ItemInstance> attachments = new HashSet<ItemInstance>();

	private JdbcEntityState _state = JdbcEntityState.CREATED;

	public int getMessageId()
	{
		return messageId;
	}

	public void setMessageId(int messageId)
	{
		this.messageId = messageId;
	}

	public int getSenderId()
	{
		return senderId;
	}

	public void setSenderId(int senderId)
	{
		this.senderId = senderId;
	}

	public String getSenderName()
	{
		return senderName;
	}

	public void setSenderName(String senderName)
	{
		this.senderName = senderName;
	}

	public int getReceiverId()
	{
		return receiverId;
	}

	public void setReceiverId(int receiverId)
	{
		this.receiverId = receiverId;
	}

	public String getReceiverName()
	{
		return receiverName;
	}

	public void setReceiverName(String receiverName)
	{
		this.receiverName = receiverName;
	}

	public int getExpireTime()
	{
		return expireTime;
	}

	public void setExpireTime(int expireTime)
	{
		this.expireTime = expireTime;
	}

	public String getTopic()
	{
		return topic;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public String getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

	public boolean isPayOnDelivery()
	{
		return price > 0L;
	}

	public long getPrice()
	{
		return price;
	}

	public void setPrice(long price)
	{
		this.price = price;
	}

	public boolean isUnread()
	{
		return isUnread;
	}

	public void setUnread(boolean isUnread)
	{
		this.isUnread = isUnread;
	}

	public Set<ItemInstance> getAttachments()
	{
		return attachments;
	}

	public void addAttachment(ItemInstance item)
	{
		attachments.add(item);
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if ((o == null) || (o.getClass() != this.getClass()))
		{
			return false;
		}
		return ((Mail) o).getMessageId() == getMessageId();
	}

	@Override
	public void setJdbcState(JdbcEntityState state)
	{
		_state = state;
	}

	@Override
	public JdbcEntityState getJdbcState()
	{
		return _state;
	}

	@Override
	public void save()
	{
		_mailDAO.save(this);
	}

	@Override
	public void update()
	{
		_mailDAO.update(this);
	}

	@Override
	public void delete()
	{
		_mailDAO.delete(this);
	}

	public Mail reject()
	{
		Mail mail = new Mail();
		mail.setSenderId(getReceiverId());
		mail.setSenderName(getReceiverName());
		mail.setReceiverId(getSenderId());
		mail.setReceiverName(getSenderName());
		mail.setTopic(getTopic());
		mail.setBody(getBody());
		synchronized (getAttachments())
		{
			for (ItemInstance item : getAttachments())
			{
				mail.addAttachment(item);
			}
			getAttachments().clear();
		}
		mail.setType(SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		return mail;
	}

	public Mail reply()
	{
		Mail mail = new Mail();
		mail.setSenderId(getReceiverId());
		mail.setSenderName(getReceiverName());
		mail.setReceiverId(getSenderId());
		mail.setReceiverName(getSenderName());
		mail.setTopic("[Re]" + getTopic());
		mail.setBody(getBody());
		mail.setType(SenderType.NEWS_INFORMER);
		mail.setUnread(true);
		return mail;
	}

	@Override
	public int compareTo(Mail o)
	{
		return o.getMessageId() - getMessageId();
	}

	public SenderType getType()
	{
		return _type;
	}

	public void setType(SenderType type)
	{
		_type = type;
	}
}
