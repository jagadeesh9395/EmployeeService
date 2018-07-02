package com.softvision.controller;

import com.softvision.helper.Loggable;
import com.softvision.model.Employee;
import com.softvision.model.InterviewerType;
import com.softvision.model.TechnologyCommunity;
import com.softvision.service.EmployeeService;
import com.softvision.validation.ValidationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Path("/interviewer")
public class InterviewerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterviewerController.class);

    @Inject
    EmployeeService employeeService;


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getInterviewerById(@Suspended AsyncResponse asyncResponse,
                                   @PathParam("id") String id) {
        LOGGER.info("Candidate ID is : {} ", id);
        if (StringUtils.isEmpty(id)) {
            asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Interviewer Id  cannot be NULL or Empty.").build());

        }
        CompletableFuture.supplyAsync(() -> employeeService.getEmployeeById(id))
                .thenApply(optional -> asyncResponse.resume(optional.get()))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()));
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void search(@Suspended AsyncResponse asyncResponse,
                       @QueryParam("str") String str) {
        LOGGER.info("Search string is  : {} ", str);
        if (StringUtils.isEmpty(str)) {
            asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(" Search string cannot be NULL or Empty.").build());

        }
        CompletableFuture.supplyAsync(() -> employeeService.search(str))
                .thenApply(list -> asyncResponse.resume(list))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Loggable
    public void getAllInterviewer(@Suspended AsyncResponse asyncResponse,
                                  @QueryParam("size") Integer size,
                                  @QueryParam("sort") String sortOrder,
                                  @QueryParam("isDeleted") boolean isDeleted) {

        LOGGER.info("Number of elements request is {} and sort order is {} and isDeleted {} ", size, sortOrder, isDeleted);
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
    public void addInterviewer(@Suspended AsyncResponse asyncResponse,
                               Employee employee) {
        ValidationUtil.validate(employee);
        CompletableFuture.supplyAsync(() -> employeeService.addEmployee(employee))
                .thenApply(optional -> asyncResponse.resume(optional.get()))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build()));
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void updateInterviewer(@Suspended AsyncResponse asyncResponse,
                                  Employee employee, @PathParam("id") String id) {
        if (StringUtils.isEmpty(id)) {
            asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity("Input missing").build());
        }
        ValidationUtil.validate(employee);
        CompletableFuture.supplyAsync(() -> employeeService.updateEmployee(employee, id))
                .thenApply(optional -> asyncResponse.resume(optional.get()))
                .exceptionally(e -> asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build()));

    }

    @DELETE
    @Path("/{id}")
    @Loggable
    public void deleteInterviewer(@Suspended AsyncResponse asyncResponse,
                                  @PathParam("id") String id) {

        LOGGER.info("Deleting candidate {} ", id);
        CompletableFuture future = CompletableFuture.runAsync(() -> employeeService.deleteEmployee(id));
        asyncResponse.resume(future.join());
    }

    @DELETE
    @Loggable
    public void deleteAllInterviewer(@Suspended AsyncResponse asyncResponse) {
        LOGGER.info(" Deleting All candidates ");

        CompletableFuture future = CompletableFuture.runAsync(() -> employeeService.deleteAllEmployees());
        Response.ResponseBuilder rb = Response.ok("the test response");
        Response response = rb.header("Access-Control-Allow-Origin", "*")
                .status(Response.Status.BAD_REQUEST).entity("Input missing").build();
        asyncResponse.resume(response);
    }

    @GET
    @Path("/bybandexp")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getAllInterviewerByBandExp(@Suspended AsyncResponse asyncResponse,
                                           @QueryParam("tc") String technologyCommunity,
                                           @QueryParam("be") int bandExperience) {

        if (StringUtils.isEmpty(technologyCommunity) && bandExperience < 3) {
            asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Technology Community should not be empty and the Band Experience should also not be empty").build());
        }
        LOGGER.info("Technology Community is {} and as per the Band Experience is {} ", technologyCommunity, bandExperience);
        CompletableFuture<Optional<List<Employee>>> future = CompletableFuture
                .supplyAsync(() -> employeeService.getAllEmployeesByBandExp(bandExperience, technologyCommunity));
        Optional<List<Employee>> interviewer = future.join();
        if (interviewer.isPresent()) {
            asyncResponse.resume(interviewer.get().stream()
                    .sorted(Comparator.comparing(Employee::getBandExperience))
                    .collect(Collectors.toList()));
        }
    }

    @GET
    @Path("/tech")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getTechStack(@Suspended AsyncResponse asyncResponse) {
        CompletableFuture<Optional<List<TechnologyCommunity>>> future = CompletableFuture
                .supplyAsync(() -> employeeService.getTechStack());
        asyncResponse.resume(future.join().get().stream()
                .sorted()
                .collect(Collectors.toList()));
    }

    @GET
    @Path("/interviewertype")
    @Produces(MediaType.APPLICATION_JSON)
    @Loggable
    public void getInterviewerType(@Suspended AsyncResponse asyncResponse) {
        CompletableFuture<Optional<List<InterviewerType>>> future = CompletableFuture
                .supplyAsync(() -> employeeService.getEmployeeType());
        asyncResponse.resume(future.join().get().stream()
                .sorted()
                .collect(Collectors.toList()));
    }
}
