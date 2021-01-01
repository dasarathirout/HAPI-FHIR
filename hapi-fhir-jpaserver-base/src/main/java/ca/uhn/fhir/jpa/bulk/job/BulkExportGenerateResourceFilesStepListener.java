package ca.uhn.fhir.jpa.bulk.job;

/*-
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2021 University Health Network
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ca.uhn.fhir.jpa.bulk.model.BulkJobStatusEnum;
import ca.uhn.fhir.jpa.bulk.svc.BulkExportDaoSvc;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * This class sets the job status to ERROR if any failures occur while actually
 * generating the export files.
 */
public class BulkExportGenerateResourceFilesStepListener implements StepExecutionListener {

	@Autowired
	private BulkExportDaoSvc myBulkExportDaoSvc;

	@Override
	public void beforeStep(@Nonnull StepExecution stepExecution) {
		// nothing
	}

	@Override
	public ExitStatus afterStep(StepExecution theStepExecution) {
		if (theStepExecution.getExitStatus().getExitCode().equals(ExitStatus.FAILED.getExitCode())) {
			String jobUuid = theStepExecution.getJobExecution().getJobParameters().getString("jobUUID");
			assert isNotBlank(jobUuid);
			String exitDescription = theStepExecution.getExitStatus().getExitDescription();
			myBulkExportDaoSvc.setJobToStatus(jobUuid, BulkJobStatusEnum.ERROR, exitDescription);
		}
		return theStepExecution.getExitStatus();
	}
}
