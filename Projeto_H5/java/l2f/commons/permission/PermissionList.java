package l2f.commons.permission;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PermissionList<T>
{
	protected Set<Permission<T>> _permissions;

	public PermissionList()
	{
		_permissions = new CopyOnWriteArraySet<Permission<T>>();
	}

	public Collection<Permission<T>> getPermissions()
	{
		return _permissions;
	}

	public boolean add(Permission<T> permission)
	{
		return _permissions.add(permission);
	}

	public boolean remove(Permission<T> permission)
	{
		return _permissions.remove(permission);
	}

	public int size()
	{
		return _permissions.size();
	}
}
