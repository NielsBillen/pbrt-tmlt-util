package ssh;

import javax.swing.JOptionPane;

import com.jcraft.jsch.UserInfo;

/**
 * Implementation of UserInfo which provides the password and yes/no prompts.
 * 
 * Prompts about the authenticity of the remote server are ignored.
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class SSHUserInfo implements UserInfo {
	public final String username;
	private final String password;

	/**
	 * 
	 * @param username
	 * @throws NullPointerException
	 */
	public SSHUserInfo(String username) throws NullPointerException {
		if (username == null)
			throw new NullPointerException("the given username is null!");

		this.username = username;
		this.password = JOptionPane.showInputDialog("Enter password:");
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @throws NullPointerException
	 */
	public SSHUserInfo(String username, String password)
			throws NullPointerException {
		if (username == null)
			throw new NullPointerException("the given username is null!");
		if (password == null)
			throw new NullPointerException("the given password is null!");

		this.username = username;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassphrase()
	 */
	@Override
	public String getPassphrase() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassword()
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
	 */
	@Override
	public boolean promptPassphrase(String arg0) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
	 */
	@Override
	public boolean promptPassword(String arg0) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
	 */
	@Override
	public boolean promptYesNo(String message) {
		if (message.startsWith("The authenticity of "))
			return true;
		Object[] options = { "yes", "no" };
		int foo = JOptionPane.showOptionDialog(null, message, "Warning",
				JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
				options, options[0]);
		return foo == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
	 */
	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(null, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return username;
	}
}
