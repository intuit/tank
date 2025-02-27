package com.intuit.tank.vmManager;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgentWatchdogTest {

    @Mock
    AmazonInstance amazonInstanceMock = new AmazonInstance(VMRegion.STANDALONE);

    @Test
    public void toStringTest() {
        VMTracker vmTracker = new VMTrackerImpl();
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);
        List<VMInformation> vmInfo = new ArrayList<>();
        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker);

        String result = agentWatchdog.toString();

        assertNotNull(result);
        assertTrue(result.endsWith("[sleepTime=30000,maxWaitForResponse=600000,maxRestarts=2]"));
    }

    @Test
    public void alreadyPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatus = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Running, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        Set<CloudVmStatus> set = Stream.of(vmstatus).collect(Collectors.toCollection(HashSet::new));
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(set);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInformation = new VMInformation();
        vmInformation.setState("pending");
        vmInformation.setInstanceId("i-123456789");
        List<VMInformation> vmInfo = Collections.singletonList( vmInformation );
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000);
        agentWatchdog.run();

        verify(amazonInstanceMock, never()).killInstances(Mockito.anyList());
        verify(amazonInstanceMock, never()).reboot(Mockito.anyList());
        verify(cloudVmStatusContainerMock, times(1)).getEndTime();
        verify(cloudVmStatusContainerMock, times(1)).getStatuses();
    }

    @Test
    public void progressToPendingRunTest(@Mock VMTracker vmTrackerMock, @Mock CloudVmStatusContainer cloudVmStatusContainerMock) {
        when(cloudVmStatusContainerMock.getEndTime()).thenReturn(null);
        CloudVmStatus vmstatusStarting = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.starting, new ValidationStatus(), 1, 1, new Date(), new Date());
        CloudVmStatus vmstatusPending = new CloudVmStatus("i-123456789", "123", "sg-123456", JobStatus.Starting, VMImageType.AGENT, VMRegion.STANDALONE, VMStatus.pending, new ValidationStatus(), 1, 1, new Date(), new Date());
        Set<CloudVmStatus> setStarting = Stream.of(vmstatusStarting).collect(Collectors.toCollection(HashSet::new));
        Set<CloudVmStatus> setPending = Stream.of(vmstatusPending).collect(Collectors.toCollection(HashSet::new));
        when(cloudVmStatusContainerMock.getStatuses()).thenReturn(setStarting).thenReturn(setPending);
        when(vmTrackerMock.getVmStatusForJob(null)).thenReturn(cloudVmStatusContainerMock);

        VMInformation vmInformation = new VMInformation();
        vmInformation.setState("pending");
        vmInformation.setInstanceId("i-123456789");
        List<VMInformation> vmInfo = Collections.singletonList( vmInformation );
        VMInstanceRequest instanceRequest = new VMInstanceRequest();
        instanceRequest.setRegion(VMRegion.STANDALONE);

        AgentWatchdog agentWatchdog = new AgentWatchdog(instanceRequest, vmInfo, vmTrackerMock, amazonInstanceMock, 10, 1000);
        agentWatchdog.run();

        verify(amazonInstanceMock, never()).killInstances(Mockito.anyList());
        verify(amazonInstanceMock, never()).reboot(Mockito.anyList());
        verify(cloudVmStatusContainerMock, times(2)).getEndTime();
        verify(cloudVmStatusContainerMock, times(2)).getStatuses();
    }
}
