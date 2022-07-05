package l2f.gameserver.network.clientpackets;

import l2f.gameserver.data.xml.holder.CharTemplateHolder;
import l2f.gameserver.model.base.ClassId;
import l2f.gameserver.network.serverpackets.NewCharacterSuccess;

public class NewCharacter extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}

	@Override
	protected void runImpl()
	{
		NewCharacterSuccess ct = new NewCharacterSuccess();

		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.fighter, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.mage, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.elvenFighter, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.elvenMage, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.darkFighter, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.darkMage, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.orcFighter, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.orcMage, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.dwarvenFighter, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.maleSoldier, false));
		ct.addChar(CharTemplateHolder.getInstance().getTemplate(ClassId.femaleSoldier, false));

		sendPacket(ct);
	}
}