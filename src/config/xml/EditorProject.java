package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("editorProject")
public class EditorProject implements IPreference {

	private String projectName;


	public EditorProject(String projectName) {
		this.projectName = projectName;
	}


	public String getProjectName() {
		return projectName;
	}


	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
