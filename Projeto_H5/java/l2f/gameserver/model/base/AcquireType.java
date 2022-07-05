package l2f.gameserver.model.base;

public enum AcquireType
{
	NORMAL, FISHING, CLAN, SUB_UNIT, TRANSFORMATION, CERTIFICATION, COLLECTION, TRANSFER_CARDINAL, TRANSFER_EVA_SAINTS, TRANSFER_SHILLIEN_SAINTS;

	public static final AcquireType[] VALUES = AcquireType.values();

	public static AcquireType transferType(int classId)
	{
		switch (classId)
		{
		case 97:
			return TRANSFER_CARDINAL;
		case 105:
			return TRANSFER_EVA_SAINTS;
		case 112:
			return TRANSFER_SHILLIEN_SAINTS;
		}

		return null;
	}

	public int transferClassId()
	{
		switch (this)
		{
		case TRANSFER_CARDINAL:
			return 97;
		case TRANSFER_EVA_SAINTS:
			return 105;
		case TRANSFER_SHILLIEN_SAINTS:
			return 112;
		}

		return 0;
	}
}
