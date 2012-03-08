package config.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import config.IPreference;

/**
 * @author Bilal Hussain
 */
@XStreamAlias("editorProject")
public class EditorProject implements IPreference {

	private String projectName;

	/** @category Generated */
	public EditorProject(String projectName) {
		this.projectName = projectName;
	}

	/** @category Generated */
	public String getProjectName() {
		return projectName;
	}

	/** @category Generated */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

}
