package distributed;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import com.jcraft.jsch.UserInfo;
import com.sun.xml.internal.ws.util.NoCloseInputStream;

/**
 * Provides information to remotely access a computer.
 * 
 * @author Niels Billen
 * @version 0.1
 * 
 */
public class RemoteAuthentication implements UserInfo {
	/**
	 * The username for remote authentication.
	 */
	public final String username;

	/**
	 * The password for remote authentication.
	 */
	private String password;

	/**
	 * The passphrase for remote authentication (a passphrase is the same as a
	 * password, but it supports spaces).
	 */
	private String passphrase;

	/**
	 * 
	 */
	private final boolean useConsole;

	/**
	 * 
	 */
	private ReentrantLock monitor = new ReentrantLock();

	/**
	 * Creates a remote authenticator using the given username.
	 * 
	 * @param username
	 *            the name of the user.
	 * @param useConsole
	 *            whether to use the console for showing messages and requesting
	 *            input.
	 * @throws NullPointerException
	 *             when the given username is null.
	 * @throws IllegalArgumentException
	 *             when the given username is empty.
	 */
	public RemoteAuthentication(String username, boolean useConsole)
			throws NullPointerException, IllegalArgumentException {
		if (username == null)
			throw new NullPointerException("the given username is null!");
		if (username.isEmpty())
			throw new IllegalArgumentException("the given username is empty!");
		this.username = username;
		this.useConsole = useConsole;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#getPassphrase()
	 */
	@Override
	public String getPassphrase() {
		return passphrase;
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
	public boolean promptPassphrase(String message) {
		monitor.lock();
		try {
			if (passphrase == null) {
				passphrase = showPasswordDialog(message);
				return passphrase != null;
			} else
				return false;
		} finally {
			monitor.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
	 */
	@Override
	public boolean promptPassword(String message) {
		try {
			monitor.lock();
			if (password == null) {
				password = showPasswordDialog(message);
				return password != null;
			} else
				return true;
		} finally {
			monitor.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
	 */
	@Override
	public boolean promptYesNo(String message) {
		monitor.lock();
		try {
			if (message.startsWith("The authenticity of host"))
				return true;
			Object[] options = { "yes", "no" };
			int result = JOptionPane.showOptionDialog(null, message, "Warning",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			return result == 0;
		} finally {
			monitor.unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
	 */
	@Override
	public void showMessage(String message) {
		monitor.lock();
		try {
			JOptionPane.showMessageDialog(null, message);
		} finally {
			monitor.unlock();
		}
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private String showPasswordDialog(String message) {
		if (useConsole) {
			Console console = System.console();
			if (console == null) {
				try (NoCloseInputStream stream = new NoCloseInputStream(
						System.in);
						InputStreamReader reader = new InputStreamReader(stream);
						BufferedReader r = new BufferedReader(reader)) {

					System.out.println(message);
					System.out.print(">");
					return r.readLine();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			} else {
				System.out.println(message);
				return new String(console.readPassword());
			}

		} else {
			JPanel panel = new JPanel();
			JLabel label = new JLabel("Enter a password:");
			JPasswordField pass = new JPasswordField(30);
			panel.add(label);
			panel.add(pass);
			String[] options = new String[] { "OK", "Cancel" };
			int option = JOptionPane.showOptionDialog(null, panel, message,
					JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
					options, options[0]);
			if (option == 0)
				return new String(pass.getPassword());
			return null;
		}
	}
}
