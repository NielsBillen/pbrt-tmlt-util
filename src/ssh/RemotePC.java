package ssh;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemotePC {
	private static final JSch jsch = new JSch();
	private final String hostName;
	private final SSHUserInfo user;

	/**
	 * 
	 */
	public RemotePC(String hostName, SSHUserInfo user)
			throws NullPointerException {
		if (hostName == null)
			throw new NullPointerException("the given hostname is null!");
		this.hostName = hostName;
		this.user = user;
	}

	/**
	 * 
	 * @return
	 * @throws JSchException
	 */
	private Session getSession() throws JSchException {
		Session result = jsch.getSession(user.username, hostName, 22);
		result.setUserInfo(user);
		result.connect(10000);
		return result;
	}

	/**
	 * 
	 * @param localFile
	 * @param remoteDestination
	 * @return
	 */
	public boolean cp(File localFile, String remoteFilename) {
		try {
			Session session = getSession();
			ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.put(localFile.getAbsolutePath(), remoteFilename,
					ChannelSftp.OVERWRITE);
			sftp.disconnect();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param localFile
	 * @param remoteDestination
	 * @return
	 */
	public boolean get(String remoteFilename, String localFilename) {
		try {
			Session session = getSession();
			ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.get(remoteFilename, localFilename);
			// sftp.put(localFile.getAbsolutePath(), remoteFilename,
			// ChannelSftp.OVERWRITE);
			sftp.disconnect();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param remoteFilename
	 * @return
	 */
	public boolean rm(String remoteFilename) {

		try {
			Session session = getSession();
			ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
			sftp.connect();
			sftp.rm(remoteFilename);
			sftp.disconnect();
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/**
	 * 
	 * @param remoteFilename
	 * @return
	 */
	public boolean execute(String... commands) {
		try {

			Session session = getSession();

			ChannelShell exec = (ChannelShell) session.openChannel("shell");
			OutputStream ops = exec.getOutputStream();
			PrintStream ps = new PrintStream(ops, true);

			exec.connect();
			InputStream in = exec.getInputStream();

			for (String command : commands)
				ps.println(command);
			ps.close();

			printResult(in, exec);

			exec.disconnect();
			session.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param input
	 * @param channel
	 * @throws Exception
	 */
	private static void printResult(InputStream input, Channel channel)
			throws Exception {
		int SIZE = 1024;
		byte[] tmp = new byte[SIZE];
		while (true) {
			while (input.available() > 0) {
				int i = input.read(tmp, 0, SIZE);
				if (i < 0)
					break;
				// System.out.print(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				// System.out.println("exit-status: " +
				// channel.getExitStatus());
				break;
			}
			try {
				Thread.sleep(300);
			} catch (Exception ee) {
			}
		}
	}
}
