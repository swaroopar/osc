package org.eclipse.osc.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.osc.modules.ocl.loader.data.models.Ocl;
import org.eclipse.osc.orchestrator.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST interface methods for processing OCL.
 */
@Slf4j
@RestController
@RequestMapping("/osc")
public class OrchestratorApi {
    private final OrchestratorService orchestratorService;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    @Autowired
    public OrchestratorApi(OrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void register(@RequestBody Ocl ocl) throws Exception {
        this.orchestratorService.registerManagedService(ocl);
    }

    @PostMapping("/register/fetch")
    @ResponseStatus(HttpStatus.OK)
    public void fetch(@RequestHeader("ocl") String oclLocation) throws Exception {
        this.orchestratorService.registerManagedService(oclLocation);
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    public String health() {
        return "ready";
    }

    @GetMapping("/services/state/{managedServiceName}")
    @ResponseStatus(HttpStatus.OK)
    public String state(@PathVariable("managedServiceName") String managedServiceName) {
        return this.orchestratorService.getManagedServiceState(managedServiceName);
    }

    /**
     * Profiles the names of the managed services currently deployed.
     *
     * @return list of all services deployed.
     */
    @GetMapping("/services")
    @ResponseStatus(HttpStatus.OK)
    public String services() {
        StringBuilder builder = new StringBuilder();
        this.orchestratorService.getOrchestratorStorage().services()
                .forEach(service -> builder.append(service).append("\n"));
        return builder.toString();
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.OK)
    public void start(@PathVariable("managedServiceName") String managedServiceName)
            throws Exception {
        this.orchestratorService.startManagedService(managedServiceName);
    }

    @PostMapping("/stop")
    @ResponseStatus(HttpStatus.OK)
    public void stop(@PathVariable("managedServiceName") String managedServiceName)
            throws Exception {
        this.orchestratorService.stopManagedService(managedServiceName);
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("managedServiceName") String managedServiceName, Ocl ocl)
            throws Exception {
        this.orchestratorService.updateManagedService(managedServiceName, ocl);
    }

    @PutMapping("/update/fetch")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("managedServiceName") String managedServiceName,
                       @RequestHeader("ocl") String oclLocation) throws Exception {
        this.orchestratorService.updateManagedService(managedServiceName, oclLocation);
    }

}
