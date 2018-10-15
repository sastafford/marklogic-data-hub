package com.marklogic.hub;

import com.marklogic.client.datamovement.WriteBatcher;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SampleInputFlowTest {

    @Test
    public void runSampleInputFlowTest() {
        FlowRunner flowRunner = new FlowRunner();
        flowRunner.runFlowWithDocumentManager("sampleXml", "ingestSampleXml", "abc");
        assertEquals(1, 1);
    }

    @Test
    public void runSampleInputFlowTestWithDataMovement() throws IOException {
        FlowRunner flowRunner = new FlowRunner();
        flowRunner.runFlowWithDataMovement("sample", "ingestSample", "abc");
        assertEquals(1, 1);
    }


}
