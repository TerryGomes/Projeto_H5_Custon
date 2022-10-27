package l2mv.gameserver.network.telnet;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import l2mv.gameserver.Config;

public class TelnetServer
{
	public TelnetServer()
	{
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newFixedThreadPool(1), Executors.newFixedThreadPool(1), 1));

		TelnetServerHandler handler = new TelnetServerHandler();
		bootstrap.setPipelineFactory(new TelnetPipelineFactory(handler));

		bootstrap.bind(new InetSocketAddress(Config.TELNET_HOSTNAME.equals("*") ? null : Config.TELNET_HOSTNAME, Config.TELNET_PORT));
	}
}
