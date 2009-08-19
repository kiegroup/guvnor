package org.gridcc.mce.mceworkflow.servlets;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.server.files.AssetFileServlet;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepositoryException;

/**
 * Allows to download workflow files from the VCR, to retrieve their content and
 * to save content to them. The following parameters must be passed in the
 * HttpServletRequest:
 * 
 * @param operation
 *            (Mandatory) The type of operation to be performed. That is one
 *            among the following values: - "download", to download the whole
 *            specified workflow in the form of a .bpr archive - "retrieve", to
 *            retrieve the content of a specified workflow file as plain text -
 *            "save", to save content to the specified workflow file (returns
 *            "true" if the operation is successful, "false" otherwise) -
 *            "getFromURL", to retrieve a file from a remote URL
 * @param fullPath
 *            (Mandatory) The full path of the specified workflow on the server
 * @param fileName
 *            (Mandatory) A name identifing a workflow file on the server
 * @param workflowName
 *            (Required for "save" and "retrieve" operations) The name
 *            identifing the full workflow on the server
 * @param fileContent
 *            (Required only for "save" operations) The content to save in the
 *            specified file on the server
 */
public class WorkflowManagerServlet extends AssetFileServlet {
	private static final long serialVersionUID = 6185939588315646350L;

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String operation = request.getParameter("operation");

		if (operation != null) {

			/********************* DOWNLOAD OPERATION *********************/
			if (operation.equalsIgnoreCase("download")) {
				this.downloadWorkflow(request, response);
			}

			/********************* RETRIEVE OPERATION *********************/
			else if (operation.equalsIgnoreCase("retrieve")) {
				this.retrieveWorkflow(request, response);
			}

			/********************* SAVE OPERATION *********************/
			else if (operation.equalsIgnoreCase("save")) {
				this.saveWorkflow(request, response);
			}

			/********************* GET FROM URL OPERATION *********************/
			// else if (operation.equalsIgnoreCase("getFromURL")) {
			// this.saveFileFromURL(request, response);
			// } else if (operation.equalsIgnoreCase("getStatus")) {
			// this.sendDummyMonitoring(request, response);
			// }
		}
	}

	private String getFullPath(String path, String workflowName,
			String extension, String fileName) {
		if (fileName.indexOf("wsdlCatalog") >= 0) {
			return path + workflowName + "/META-INF";
		} else {
			return getFullPath(path, workflowName, extension);
		}
	}

	private String getFullPath(String path, String workflowName,
			String extension) {
		String fullPath = path;
		if (extension.equals("bpr")) {
			fullPath += workflowName + "/bpr";
		} else if (extension.equals("bpel")) {
			fullPath += workflowName + "/bpel/" + workflowName;
		} else if (extension.equals("wsdl") || extension.equals("xsd")) {
			fullPath += workflowName + "/wsdl/" + workflowName + "/wsdl";
		} else if (extension.equals("xml")) {
			fullPath += workflowName + "/submission";
		} else if (extension.equals("pdd")) {
			fullPath += workflowName + "/META-INF/pdd/" + workflowName;
		}
		return fullPath;
	}

	private void downloadWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String uuid = request.getParameter("uuid");

		if (uuid == null) {
			return;
		}

		try {
			processAttachmentDownload(uuid, response);
		} catch (Exception e) {
			response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
			writer.print("<h2>Sorry, file cannot be found!</h2>");
			writer.close();
		}
	}

	private void retrieveWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String uuid = request.getParameter("uuid");

		String fullPath = "";// request.getParameter("fullPath");
		String fileName = request.getParameter("fileName");
		String workflowName = request.getParameter("workflowName");

		if (fileName == null || workflowName == null || fullPath == null) {
			return;
		}
		String extension = fileName.substring(fileName.indexOf('.') + 1); // request.getParameter("extension");
		fullPath = this
				.getFullPath(fullPath, workflowName, extension, fileName);
		fullPath = fullPath + "/" + fileName;

		try {

			AssetItem item = getFileManager().getRepository().loadAssetByUUID(uuid);

			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			final JarInputStream jis = new JarInputStream(item
					.getBinaryContentAttachment());
			JarEntry entry;
			final byte[] buf = new byte[1024];
			int len;
			while ((entry = jis.getNextJarEntry()) != null) {
				if (entry.getName().equals(fullPath)) {
					while ((len = jis.read(buf)) >= 0) {
						out.write(buf, 0, len);
					}
					break;
				}
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(out.toByteArray())));

			String line = null;
			StringBuffer sb = new StringBuffer(4 * (9216));
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			out.close();
			jis.close();
			reader.close();
			response.setContentType("text/plain");
			PrintWriter writer = response.getWriter();
			writer.print(sb);
			writer.flush();
			writer.close();

		} catch (Exception e) {
			response.setContentType("text/html");
			PrintWriter writer = response.getWriter();
			writer.print("<h2>Sorry, that file cannot be found</h2>");
			writer.close();
		}
	}

	private void saveWorkflow(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String uuid = request.getParameter("uuid");

		String fullPath = ""; // request.getParameter("fullPath");
		String fileName = request.getParameter("fileName");
		String workflowName = request.getParameter("workflowName");

		if (fileName == null || workflowName == null || fullPath == null) {
			return;
		}
		String extension = fileName.substring(fileName.indexOf('.') + 1);
		String fullPathToFolder = this.getFullPath(fullPath, workflowName,
				extension, fileName);
		fullPath = fullPathToFolder + "/" + fileName;
		 
		AssetItem item = getFileManager().getRepository().loadAssetByUUID(uuid);

		InputStream in = item.getBinaryContentAttachment();

		String fileContent = request.getParameter("fileContent");

		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		JarOutputStream jos = new JarOutputStream(bais);
		JarInputStream jis = null;

		try {
			if (in == null) {

				jos.putNextEntry(new JarEntry(fullPath));

				ByteArrayInputStream i = new ByteArrayInputStream(fileContent
						.getBytes());

				int len = 0;
				byte[] copyBuf = new byte[1024];
				while (len != -1) {

					len = i.read(copyBuf, 0, copyBuf.length);
					if (len > 0) {
						jos.write(copyBuf, 0, len);
					}
				}

				i.close();
				jos.closeEntry();

			} else {

				jis = new JarInputStream(in);

				boolean found = false;
				JarEntry entry;
				while ((entry = jis.getNextJarEntry()) != null) {

					jos.putNextEntry(new JarEntry(entry.getName()));

					String name = entry.getName();
					if (fullPath.equals(name)) {
						found = true;

						ByteArrayInputStream i = new ByteArrayInputStream(
								fileContent.getBytes());

						int len = 0;
						byte[] copyBuf = new byte[1024];
						while (len != -1) {

							len = i.read(copyBuf, 0, copyBuf.length);
							if (len > 0) {
								jos.write(copyBuf, 0, len);
							}
						}

						i.close();

					} else {
						int len = 0;
						byte[] copyBuf = new byte[1024];
						while (len != -1) {

							len = jis.read(copyBuf, 0, copyBuf.length);
							if (len > 0) {
								jos.write(copyBuf, 0, len);
							}
						}
					}

					jis.closeEntry();
					jos.closeEntry();
				}

				// If the file is not found, add it
				if (!found) {
					jos.putNextEntry(new JarEntry(fullPath));

					ByteArrayInputStream i = new ByteArrayInputStream(
							fileContent.getBytes());

					int len = 0;
					byte[] copyBuf = new byte[1024];
					while (len != -1) {

						len = i.read(copyBuf, 0, copyBuf.length);
						if (len > 0) {
							jos.write(copyBuf, 0, len);
						}
					}

					i.close();
					jos.closeEntry();
				}
			}

			item.updateBinaryContentAttachment(new ByteArrayInputStream(bais
					.toByteArray()));

			item.checkin("Updated " + fileName);

		} catch (IOException e) {
			throw new RulesRepositoryException(e);
		} finally {
			if (jis != null) {
				jis.close();
			}
			jos.close();
		}
	}

	private Collection<String> createWorkflow(String fullPath,
			String workflowName) {
		Collection<String> list = new ArrayList<String>();

		list.add(fullPath + "/" + workflowName);
		list.add(getFullPath(fullPath, workflowName, "bpr"));
		list.add(getFullPath(fullPath, workflowName, "bpel"));
		list.add(getFullPath(fullPath, workflowName, "wsdl"));
		list.add(getFullPath(fullPath, workflowName, "pdd"));
		list.add(getFullPath(fullPath, workflowName, "xml"));

		return list;
	}
}
