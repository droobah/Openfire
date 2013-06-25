package org.jivesoftware.openfire.plugin.gojara.messagefilter.processors;

import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.plugin.gojara.sessions.TransportSessionManager;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

public class GojaraAdminProcessor extends AbstractRemoteRosterProcessor {
	private TransportSessionManager transportSessionManager = TransportSessionManager.getInstance();

	public GojaraAdminProcessor() {
		Log.info("Created GojaraAdminProcessor");
	}

	/**
	 * Here we process the response of the remote command sent to Spectrum. We have to identify what kind of response it
	 * is, as no tag for the command being responded is being sent. Currently these commands are used in Gojara
	 * TransportSessionManager: online_users ( Chatmsg of online users for specific transport), usernames seperated by
	 * newlines
	 */
	@Override
	public void process(Packet packet, String subdomain, String to, String from) throws PacketRejectedException {
		Message message = (Message) packet;
		String body = message.getBody();
		// unregister <bare_jid>
		if (body.endsWith("unregistered.") || body.endsWith("registered")) {
			Log.info("Ignoring Message! " + message.toString());
		}
		// online_users
		else {
			String[] content = message.getBody().split("\\r?\\n");
			for (String user : content) {
				JID userjid = new JID(user);
				transportSessionManager.connectUserTo(subdomain, userjid.getNode());
			}
			Log.info("Found online Users!" + message.toString());
		}
	}
}