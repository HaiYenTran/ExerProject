package com.ericsson.becrux.iles.loop;

import static org.junit.Assert.*;

import com.ericsson.becrux.base.common.loop.PhaseStatus;
import com.ericsson.becrux.iles.loop.StatusHandler;
import org.junit.Test;

import com.ericsson.becrux.iles.leo.domain.StatusType;

import hudson.model.Result;

public class StatusHandlerTests {
	
	@Test
	public void testSetEiffelStatus() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setEiffelStatus(PhaseStatus.ERROR);
		assertEquals(PhaseStatus.ERROR, sh.getEiffelStatus());
		
		sh.setEiffelStatus(PhaseStatus.FAILURE);
		assertEquals(PhaseStatus.ERROR, sh.getEiffelStatus());
		
		sh.setEiffelStatus(PhaseStatus.INCONCLUSIVE);
		assertEquals(PhaseStatus.INCONCLUSIVE, sh.getEiffelStatus());
	}
	
	@Test
	public void testSetJenkinsStatus() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setJenkinsStatus(Result.FAILURE);
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		
		sh.setJenkinsStatus(Result.SUCCESS);
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		
		sh.setJenkinsStatus(Result.ABORTED);
		assertEquals(Result.ABORTED, sh.getJenkinsStatus());
	}
	
	@Test
	public void testSetLeoStatus() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setLeoStatus(StatusType.FINISHED);
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
		
		sh.setLeoStatus(StatusType.STARTED);
		assertEquals(StatusType.STARTED, sh.getLeoStatus());
		
		sh.setLeoStatus(StatusType.ERROR);
		assertEquals(StatusType.ERROR, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuildSuccess() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.SUCCESS);
		assertEquals(PhaseStatus.PROGRESS, sh.getEiffelStatus());
		assertEquals(Result.SUCCESS, sh.getJenkinsStatus());
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuildUnstable() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.UNSTABLE);
		assertEquals(PhaseStatus.FAILURE, sh.getEiffelStatus());
		assertEquals(Result.UNSTABLE, sh.getJenkinsStatus());
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuildFailure() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.FAILURE);
		assertEquals(PhaseStatus.FAILURE, sh.getEiffelStatus());
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		assertEquals(StatusType.FAILED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuildAborted() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.ABORTED);
		assertEquals(PhaseStatus.ERROR, sh.getEiffelStatus());
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		assertEquals(StatusType.ABORTED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterFinalBuildSuccess() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setFinalStatusesAfterBuild(Result.SUCCESS);
		assertEquals(PhaseStatus.SUCCESS, sh.getEiffelStatus());
		assertEquals(Result.SUCCESS, sh.getJenkinsStatus());
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuilSuccessAndFinalBuildUnstable() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.SUCCESS);
		sh.setFinalStatusesAfterBuild(Result.UNSTABLE);
		assertEquals(PhaseStatus.FAILURE, sh.getEiffelStatus());
		assertEquals(Result.UNSTABLE, sh.getJenkinsStatus());
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
	}
	
	@Test
	public void testSetAfterBuilds() {
		
		StatusHandler sh = new StatusHandler();
		
		sh.setStatusesAfterBuild(Result.UNSTABLE);
		sh.setStatusesAfterBuild(Result.FAILURE);
		assertEquals(PhaseStatus.FAILURE, sh.getEiffelStatus());
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		assertEquals(StatusType.FAILED, sh.getLeoStatus());
		
		sh.setStatusesAfterBuild(Result.SUCCESS);
		assertEquals(PhaseStatus.FAILURE, sh.getEiffelStatus());
		assertEquals(Result.FAILURE, sh.getJenkinsStatus());
		assertEquals(StatusType.FINISHED, sh.getLeoStatus());
	}

}
