package com.ericsson.becrux.base.common.data;

import com.ericsson.becrux.base.common.data.impl.BaseComponentImpl;
import com.ericsson.becrux.base.common.deploy.NodeConfiguration;
import com.ericsson.becrux.base.common.utils.TempFilesCreator;
import org.joda.time.DateTime;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class NodeTests {
    class DummyComponent extends Component {
        public DummyComponent() {
            super();
        }

        public DummyComponent(@Nonnull Version version) {
            super(version);
        }
    }

	private String artifactURL = "file:///proj/ims_lu/cba_cde/int_ci/cfg/artifactURL";

	@Test
	public void getDeployParametersTest() throws IllegalArgumentException, MalformedURLException, IOException {
		Component node = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"));
		File file = null;
		try {
			file = getFileWithParameters(getGoodParametersList());
			node.setArtifact(artifactURL);
			HashMap<String, String> map = node.getDeployParameters();
			assertEquals(new URL(artifactURL).getPath(),map.get(NodeConfiguration.VNF_FILE.toString()));
		}
		finally {
			if(file != null)
				file.delete();
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void getDeployParametersMissingArtifact() throws IllegalArgumentException, IOException {
		Component node = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"));
		node.getDeployParameters();
	}

	@Test
    public void testEqualsNegativeTest() {
	    Component aNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"));
        assertFalse(aNode.equals(null));

        assertFalse(aNode.equals(new DummyComponent()));

        Component otherNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.2"), "otherArtifact");
        assertFalse(aNode.equals(otherNode));

        aNode.setArtifact("anArtifact");
        assertFalse(aNode.equals(otherNode));

        aNode.setState(Component.State.BASELINE_APPROVED);
        otherNode.setArtifact("anArtifact");
        otherNode.setState(Component.State.BASELINE_REJECTED);
        assertFalse(aNode.equals(otherNode));

        otherNode.setState(Component.State.BASELINE_APPROVED);
        assertFalse(aNode.equals(otherNode));

        aNode = new BaseComponentImpl();
        aNode.setArtifact("anArtifact");
        aNode.setState(Component.State.BASELINE_APPROVED);
        otherNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.2"), "anArtifact");
        otherNode.setState(Component.State.BASELINE_APPROVED);

        assertFalse(aNode.equals(otherNode));
    }

    @Test
    public void testEqualsPositiveTest() {
        Component aNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"), "anArtifact");
        aNode.setState(Component.State.BASELINE_APPROVED);
        assertTrue(aNode.equals(aNode));

        Component otherNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"), "anArtifact");
        otherNode.setState(Component.State.BASELINE_APPROVED);
        assertTrue(aNode.equals(otherNode));

    }

    @Test
    public void testCompareTo() {
        Component aNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"));
        assertEquals(1, aNode.compareTo(null));

        Component otherNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.2"));
        assertEquals(-1, aNode.compareTo(otherNode));

        otherNode = new BaseComponentImpl(Version.createReleaseVersion("1.0.1"));
        assertEquals(0, aNode.compareTo(otherNode));

        otherNode = new DummyComponent(Version.createReleaseVersion("1.0.1"));
        assertEquals(0, aNode.compareTo(otherNode));
    }

    /* TODO Find way to exclude lines from code coverage, these methods shouldn't have to be tested */
    @Test
    public void testSettersGetters() {
        Component component = new BaseComponentImpl(Version.createReleaseVersion("1.0.0"), Component.State.NEW_BUILD);

        DateTime date = new DateTime();
        component.setVotingTimeOut(date);
        assertEquals(date, component.getVotingTimeOut());

        component.setInstallable(true);
        assertTrue(component.isInstallable());

        Long jobId = 123456789L;
        component.setJobId(jobId);
        assertEquals(jobId, component.getJobId());

        assertEquals(Component.State.NEW_BUILD, component.getState());

        Version version = Version.createReleaseVersion("1.0.1");
        component.setVersion(version);
        assertEquals(version, component.getVersion());
    }

	private List<String> getGoodParametersList() {
		List<String> parameters = new ArrayList<>();
		parameters.add("vnf_name=CSCFv");
		parameters.add("pdb_application_config=config_value");
		parameters.add("pdb_application_ssl=pdb_ssl");
		parameters.add("pdb_server_host=pdb_host");
		parameters.add("pdb_server_port=\"pdb_port\"");
		parameters.add("     ");
		parameters.add("#line with comment");
		parameters.add("###");
		parameters.add("image_SC=image_SC");
		parameters.add("image_PL=image_SC");
		parameters.add("template=template");
		parameters.add("env=env");
		parameters.add("valid_key=valid_value");
		return parameters;
	}

	private File getFileWithParameters(List<String> parameters) throws IOException {
		File file = TempFilesCreator.createTempFile("parameters", ".txt");
		try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
			for(String param : parameters) {
				out.write(param);
				out.newLine();
			}
		}
		return file;
	}
}
