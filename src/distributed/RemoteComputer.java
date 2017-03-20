package distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import pbrt.PBRTScene;
import task.RenderTaskInterface;
import task.RenderTaskProgressListener;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * 
 * @author Niels Billen
 * @version 0.1
 */
public class RemoteComputer extends Computer {
	private final String hostName;
	private final RemoteAuthentication authentication;

	/**
	 * 
	 * @param hostName
	 * @param authentication
	 * @throws NullPointerException
	 */
	public RemoteComputer(String hostName, RemoteAuthentication authentication)
			throws NullPointerException {
		super(0);

		if (hostName == null)
			throw new NullPointerException("the given host name is null!");
		if (authentication == null)
			throw new NullPointerException("the given authentication is null!");
		this.hostName = hostName;
		this.authentication = authentication;

		authentication.promptPassword("Enter password for "
				+ authentication.username + "@" + hostName + ":");
	}

	/**
	 * 
	 * @param hostName
	 * @param authentication
	 * @throws NullPointerException
	 */
	public RemoteComputer(String hostName, RemoteAuthentication authentication,
			int nCores) throws NullPointerException {
		super(nCores);

		if (hostName == null)
			throw new NullPointerException("the given host name is null!");
		if (authentication == null)
			throw new NullPointerException("the given authentication is null!");
		this.hostName = hostName;
		this.authentication = authentication;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		int index = hostName.indexOf(".");
		if (index < 0)
			return hostName;
		return hostName.substring(0, index);
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void get(String source, String destination) throws JSchException,
			SftpException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(authentication.username, hostName);
		session.setUserInfo(authentication);
		session.connect();

		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		sftp.get(source, destination);
		sftp.disconnect();
		session.disconnect();
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws JSchException
	 * @throws SftpException
	 */
	public void put(String source, String destination) throws JSchException,
			SftpException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(authentication.username, hostName);
		session.setUserInfo(authentication);
		session.connect();

		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		sftp.put(source, destination);
		sftp.disconnect();
		session.disconnect();
	}

	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	public void exec(String command) throws JSchException, SftpException,
			IOException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(authentication.username, hostName);
		session.setUserInfo(authentication);
		session.connect();

		ChannelExec exec = (ChannelExec) session.openChannel("exec");
		exec.setCommand(command);
		exec.connect();

		while (!exec.isClosed())
			Thread.yield();

		exec.disconnect();
		session.disconnect();
	}

	/**
	 * 
	 * @param command
	 * @param listener
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException
	 */
	private void pbrt(String command, RenderTaskProgressListener listener)
			throws JSchException, SftpException, IOException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(authentication.username, hostName);
		session.setUserInfo(authentication);
		session.connect();

		ChannelExec exec = (ChannelExec) session.openChannel("exec");
		exec.setCommand(command);

		InputStream in = exec.getInputStream();
		InputStreamReader reader = new InputStreamReader(in);
		BufferedReader buffered = new BufferedReader(reader);
		exec.connect();

		double percentage = 0;
		while (true) {
			if (buffered.ready()) {
				String line = buffered.readLine();
				percentage = updateProgress(line, percentage, listener);
			}
			if (exec.isClosed()) {
				if (buffered.ready())
					continue;
				break;
			}
			Thread.yield();
		}

		exec.disconnect();
		session.disconnect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see computer.Computer#execute(task.RenderTaskInterface,
	 * task.RenderTaskProgressListener)
	 */
	@Override
	public void execute(RenderTaskInterface task,
			RenderTaskProgressListener listener) throws NullPointerException,
			ExecutionException {
		final String sceneName = task.getSceneName();
		final String outputName = task.getFilename();

		/*----------------------------------------------------------------------
		 * Allocate the result directory
		 *--------------------------------------------------------------------*/

		final File resultDirectory = new File(task.getResultDirectory());
		if (!resultDirectory.exists()) {
			if (!resultDirectory.mkdirs())
				throw new ExecutionException("could not allocate the "
						+ "directory containing the results of the rendering!");
		} else if (!resultDirectory.isDirectory())
			throw new ExecutionException(
					"the requested results directory exists as a file!");

		/*----------------------------------------------------------------------
		 * Write the task to the result directory
		 *--------------------------------------------------------------------*/

		File sceneFile = new File(resultDirectory, outputName.concat(".pbrt"));
		PBRTScene scene = task.getScene();
		try {
			scene.print(sceneFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*----------------------------------------------------------------------
		 * Copy the file to the remote computer
		 *--------------------------------------------------------------------*/

		try {
			String source = sceneFile.getAbsolutePath();
			String destination = String.format(
					"/home/%s/workspace/pbrt-tmlt/scenes/%s/%s",
					authentication.username, task.getSceneName(),
					sceneFile.getName());
			put(source, destination);
		} catch (Exception e) {
			throw new ExecutionException("could not connect to " + hostName
					+ " to transfer the scene file using sftp!", e);
		}

		/*----------------------------------------------------------------------
		 * Allocate a directory for temporarely storing the resulting file
		 *--------------------------------------------------------------------*/

		try {
			String command = String.format("mkdir -p /tmp/%s",
					task.getSceneName());
			exec(command);
		} catch (Exception e) {
			throw new ExecutionException(
					"could not create a temporary directory on the remote computer!",
					e);
		}

		/*----------------------------------------------------------------------
		 * Execute the pbrt command
		 *--------------------------------------------------------------------*/

		String remoteSceneFile = String.format("scenes/%s/%s.pbrt", sceneName,
				outputName);
		String outFile = String.format("/tmp/%s/%s", sceneName, outputName);
		try {
			String command;

			if (nCores() <= 0)
				command = String
						.format("cd workspace/pbrt-tmlt && ./pbrt --outfile %s --seed %s %s",
								outFile, task.getSeed(), remoteSceneFile);
			else
				command = String
						.format("cd workspace/pbrt-tmlt && ./pbrt --outfile %s --seed %s --nthreads %d %s",
								outFile, task.getSeed(), nCores(),
								remoteSceneFile);
			pbrt(command, listener);
		} catch (Exception e) {
			throw new ExecutionException(
					"could execute pbrt on the remote pc!", e);
		}

		/*----------------------------------------------------------------------
		 * Retrieve the remote files
		 *--------------------------------------------------------------------*/

		try {
			get(outFile.concat(".png"), task.getResultDirectory());
			get(outFile.concat(".exr"), task.getResultDirectory());
			get(outFile.concat(".pfm"), task.getResultDirectory());
			get(outFile.concat(".txt"), task.getResultDirectory());
		} catch (Exception e) {
			throw new ExecutionException(
					"could not retrieve the rendered files!", e);
		}

		/*----------------------------------------------------------------------
		 * Perform cleanup
		 *--------------------------------------------------------------------*/

		try {
			exec(String.format("rm workspace/pbrt-tmlt/%s", remoteSceneFile));
		} catch (Exception e) {
			throw new ExecutionException(
					"could not cleand the remote files in /tmp folder!", e);
		}

		try {
			exec(String.format("rm %s", outFile.concat(".png")));
			exec(String.format("rm %s", outFile.concat(".exr")));
			exec(String.format("rm %s", outFile.concat(".pfm")));
			exec(String.format("rm %s", outFile.concat(".txt")));
		} catch (Exception e) {
			throw new ExecutionException(
					"could not cleand the remote files in /tmp folder!", e);
		}
	}
}
