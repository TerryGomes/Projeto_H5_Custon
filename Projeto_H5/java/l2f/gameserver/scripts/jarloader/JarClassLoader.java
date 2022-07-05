package l2f.gameserver.scripts.jarloader;

public class JarClassLoader extends MultiClassLoader
{
	private JarResources jarResources;

	public JarClassLoader(String jarName)
	{
		jarResources = new JarResources(jarName);
	}

	@Override
	protected byte[] loadClassBytes(String className)
	{
		className = formatClassName(className);
		return jarResources.getResource(className);
	}

	public String[] getClassNames()
	{
		return jarResources.getResources().toArray(new String[] {});
	}
}