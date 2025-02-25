package com.intuit.tank.vmManager.environment.amazon;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.InstanceDescription;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.RunInstancesRequest;
import software.amazon.awssdk.services.ec2.model.RunInstancesResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmazonInstanceTest {

    @Mock
    private Ec2AsyncClient _mockEc2AsyncClient;

    AmazonInstance amazonInstance;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        amazonInstance = new AmazonInstance(_mockEc2AsyncClient, VMRegion.STANDALONE);
    }

    @Test
    public void constructorTest() {
        AmazonInstance amazonInstance = new AmazonInstance(VMRegion.STANDALONE);
    }

    @Test
    public void createTest_SUCCESS() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 23, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any())).thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder().build()));

        AWSXRay.beginSegment("TEST");
        amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(3)).runInstances(argumentCaptor.capture());

        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:8, m8g.xlarge:8, m8g.xlarge:7]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_ALTERNATIVE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 23, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(Ec2Exception.builder().build()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder().build()));

        AWSXRay.beginSegment("TEST");
        amazonInstance.create(vmRequest);
        AWSXRay.endSegment();

        ArgumentCaptor<RunInstancesRequest> argumentCaptor = ArgumentCaptor.forClass(RunInstancesRequest.class);
        verify(_mockEc2AsyncClient, times(4)).runInstances(argumentCaptor.capture());

        List<RunInstancesRequest> requests = argumentCaptor.getAllValues();
        assertEquals("[m8g.xlarge:8, m7g.xlarge:8, m8g.xlarge:8, m8g.xlarge:7]",
                requests.stream().map(request -> request.instanceType() + ":" + request.maxCount() ).toList().toString());
    }

    @Test
    public void createTest_FAILURE() {
        InstanceDescription instanceDescription = new TankConfig().getVmManagerConfig().getInstanceForRegionAndType(VMRegion.US_WEST_2, VMImageType.AGENT);
        VMRequest vmRequest = new VMInstanceRequest(VMProvider.Amazon, VMRegion.US_WEST_2, "m.xlarge",
                VMImageType.AGENT, 23, false, "testZone", instanceDescription);
        when(_mockEc2AsyncClient.runInstances((RunInstancesRequest) any()))
                .thenReturn(CompletableFuture.failedFuture(Ec2Exception.builder().build()))
                .thenReturn(CompletableFuture.failedFuture(Ec2Exception.builder().build()))
                .thenReturn(CompletableFuture.completedFuture(RunInstancesResponse.builder().build()));

        AWSXRay.beginSegment("TEST");
        assertThrows(RuntimeException.class, () -> amazonInstance.create(vmRequest));
        AWSXRay.endSegment();
    }
}
