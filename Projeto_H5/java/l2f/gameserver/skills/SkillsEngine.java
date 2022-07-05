package l2f.gameserver.skills;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import l2f.gameserver.Config;
import l2f.gameserver.model.Skill;
import l2f.gameserver.tables.SkillTable;

public class SkillsEngine
{
	private static final Logger _log = LoggerFactory.getLogger(SkillsEngine.class);

	private static final SkillsEngine _instance = new SkillsEngine();

	public static SkillsEngine getInstance()
	{
		return _instance;
	}

	public List<Skill> loadSkills(File file)
	{
		if (file == null)
		{
			_log.warn("SkillsEngine: File not found!");
			return null;
		}
		DocumentSkill doc = new DocumentSkill(file);
		doc.parse();
		return doc.getSkills();
	}

	public Skill loadSkill(int skillId, File file)
	{
		DocumentSkill doc = new DocumentSkill(file, skillId);
		doc.parse();
		List<Skill> parsedSkills = doc.getSkills();

		if (parsedSkills.isEmpty())
		{
			return null;
		}
		else
		{
			return parsedSkills.get(0);
		}
	}

	public Map<Integer, Skill> loadAllSkills()
	{
		File dir = new File(Config.DATAPACK_ROOT, "data/stats/skills");
		if (!dir.exists())
		{
			_log.info("Dir " + dir.getAbsolutePath() + " not exists");
			return Collections.emptyMap();
		}

		Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());
		Map<Integer, Skill> result = new HashMap<Integer, Skill>();
		int maxId = 0;
		int maxLvl = 0;

		for (File file : files)
		{
			List<Skill> s = loadSkills(file);
			if (s != null)
			{
				for (Skill skill : s)
				{
					result.put(Integer.valueOf(SkillTable.getSkillHashCode(skill)), skill);
					if (skill.getId() > maxId)
					{
						maxId = skill.getId();
					}
					if (skill.getLevel() > maxLvl)
					{
						maxLvl = skill.getLevel();
					}
				}
			}
		}

		_log.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
		return result;
	}
}