#!/bin/sh                                                                                                                                                                                                                                    
vm_arguments="-Dnetworkaddress.cache.ttl=5 -Dnetworkaddress.cache.negative.ttl=0";
java -cp apiharness-1.0-all.jar $@ $vm_arguments com.intuit.tank.harness.APITestHarness
echo DONE
