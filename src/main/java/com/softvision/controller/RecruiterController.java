
package com.softvision.controller;

import com.softvision.helper.Loggable;
import com.softvision.model.Employee;
import com.softvision.service.EmployeeService;
import com.softvision.validation.ValidationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Path("/recruiter")
public class RecruiterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecruiterController.class);


    @Inject
    EmployeeService employeeService;

    @Inject
    DiscoveryClient discoveryClient;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getRecruiterDetails(@Suspended AsyncResponse asyncResponse,
                                    @PathParam("id") String id) {
        LOGGER.info("Eureka instances :{}", discoveryClient.getInstances("recruiter"));
        LOGGER.info("Recruiter ID is : {} ", id);
        CompletableFuture.supplyAsync(() -> employeeService.getEmployeeById(id))
                .thenApply(optional -> asyncResponse.resume(optional.get()))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Recruiter ID is [ " + id + " ] not available").build()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getAllRecruiters(@Suspended AsyncResponse asyncResponse,
                                 @QueryParam("size") Integer size,
                                 @QueryParam("sort") String sortOrder,
                                 @QueryParam("isDeleted") boolean isDeleted) {
        LOGGER.info("Number of elements request is {} , sort order is {} and isDeleted is {} ", size, sortOrder, isDeleted);
        if (StringUtils.isEmpty(sortOrder) && size < 1) {
            asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Number of elements request should not be 0 and sort order should be given").build());
        } else if (!isDeleted) {
            CompletableFuture.supplyAsync(() -> employeeService.getAllEmployers())
                    .thenApply(v -> (List<Employee>) v.get())
                    .thenApply(k -> asyncResponse.resume(k.stream().sorted().filter(p -> !p.isDeleted()).collect(Collectors.toList())))
                    .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()));
        } else {
            CompletableFuture.supplyAsync(() -> employeeService.getAllEmployers())
                    .thenApply(v -> (List<Employee>) v.get())
                    .thenApply(k -> asyncResponse.resume(k.stream().sorted().filter(p -> p.isDeleted()).collect(Collectors.toList())))
                    .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()));
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void addRecruiter(@Suspended AsyncResponse asyncResponse,
                             Employee employee) {
        ValidationUtil.validate(employee);
        CompletableFuture.supplyAsync(() -> employeeService.addEmployee(employee))
                .thenApply(recruiter1 -> asyncResponse.resume(employee))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Recruiter is not added").build()));
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void updateRecruiter(@Suspended AsyncResponse asyncResponse,
                                Employee employee, @PathParam("id") String id) {
        ValidationUtil.validate(employee);
        LOGGER.info("Update recruiter {} ", id);
        CompletableFuture.supplyAsync(() -> employeeService.updateEmployee(employee, id))
                .thenApply(recruiter1 -> asyncResponse.resume(employee))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()));
    }

    @DELETE
    @Path("/{id}")
    @Loggable
    public void deleteRecruiter(@Suspended AsyncResponse asyncResponse,
                                @PathParam("id") String id) {
        LOGGER.info("Deleting recruiter {} ", id);
        CompletableFuture future = CompletableFuture.runAsync(() -> employeeService.deleteEmployee(id));
        asyncResponse.resume(future.join());
    }

    @DELETE
    @Loggable
    public void deleteAllRecruiter(@Suspended AsyncResponse asyncResponse) {
        LOGGER.info(" Deleting All recruiters ");
        CompletableFuture future = CompletableFuture.runAsync(() -> employeeService.deleteAllEmployees());
        asyncResponse.resume(future.join());
    }


}
