package com.marklogic.hub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.datamovement.WriteEvent;
import com.marklogic.client.document.ServerTransform;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FlowRunner {
    private Logger logger = LoggerFactory.getLogger(FlowRunner.class);
    private DatabaseClient databaseClient;

    public FlowRunner() {
        databaseClient = DatabaseClientFactory.newClient("localhost", 8010,
            new DatabaseClientFactory.DigestAuthContext("admin", "admin"));
    }

    public void runFlowWithDocumentManager(String entityName, String flowName, String jobId) {
        String doc = "<a/>";
        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        ServerTransform runFlow = new ServerTransform("ml:inputFlow");
        runFlow.addParameter("entity-name", entityName);
        runFlow.addParameter("flow-name", flowName);
        runFlow.addParameter("job-id", jobId);
        docMgr.write("/test.xml", new StringHandle(doc), runFlow);
    }

    public void runFlowWithDataMovement(String entityName, String flowName, String jobId) throws IOException {
        String collection = ".testSimple";
        ServerTransform runFlow = new ServerTransform("ml:sjsInputFlow");
        runFlow.addParameter("entity-name", entityName);
        runFlow.addParameter("flow-name", flowName);
        runFlow.addParameter("job-id", jobId);
        StringBuilder successBatch = new StringBuilder();
        StringBuilder failureBatch = new StringBuilder();
        DataMovementManager moveMgr = databaseClient.newDataMovementManager();
        WriteBatcher ihb1 =  moveMgr.newWriteBatcher()
            .withTransform(runFlow)
            .withBatchSize(1)
            .onBatchSuccess(
                batch -> {
                    logger.debug("[testSimple] batch: {}, items: {}",
                        batch.getJobBatchNumber(), batch.getItems().length);
                    for(WriteEvent w: batch.getItems()){
                        successBatch.append(w.getTargetUri()+":");
                    }
                })
            .onBatchFailure(
                (batch, throwable) -> {
                    for(WriteEvent w: batch.getItems()){
                        failureBatch.append(w.getTargetUri()+":");
                    }
                });

        DocumentMetadataHandle meta = new DocumentMetadataHandle();
        ihb1.add("/doc/jackson.json", meta, new JacksonHandle(new ObjectMapper().readTree("{\"test\":true}")));
        //ihb1.add("/doc/string.txt", meta, new StringHandle("test"));
        ihb1.flushAndWait();
        ihb1.awaitCompletion();
    }
}

