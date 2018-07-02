package com.softvision.config;

import com.softvision.controller.CandidateController;
import com.softvision.controller.InterviewerController;
import com.softvision.controller.LoginController;
import com.softvision.controller.RecruiterController;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/admin")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(InterviewerController.class);
        register(LoginController.class);
        register(RecruiterController.class);
        register(CandidateController.class);
    }
}
