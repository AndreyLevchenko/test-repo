package com.topshelfsolution.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.UserUtils;
import com.atlassian.sal.api.user.UserManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/test")
public class TestResource {
    private IssueService issueService;
    private UserManager userManager;

    public TestResource(IssueService issueService, UserManager userManager) {
        this.issueService = issueService;
        this.userManager = userManager;
    }

    
    
    
    @GET
//    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_HTML})
    public Response createTickets(){
        Boolean result = true;
        
        for(int i = 0;i<2501; i++ ){
            result = result && createTicket(10100L, i);
        }
        return Response.ok(result.toString()).build();
    }
    private boolean createTicket(Long projectId, int i){
        String userName = userManager.getRemoteUsername();

        User user = UserUtils.getUser(userName);
        
        String issueType = i%2 == 0?"1":"2";
        

        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
                issueInputParameters.setProjectId(projectId)
                    .setIssueTypeId(issueType)
                    .setSummary("test issue #" + Integer.toString(i))
                    .setReporterId(userName)
                    .setAssigneeId("admin")
                    .setOriginalAndRemainingEstimate(180L, 60L)
                    .setDescription("some text");
                

        CreateValidationResult createValidationResult = issueService.validateCreate(user, issueInputParameters);

        if (createValidationResult.isValid())
        {
            IssueResult createResult = issueService.create(user, createValidationResult);
            if (createResult.isValid())
            {
                return true;
            }else {
                System.out.println(createResult.getErrorCollection().toString());
            }
        } else {
            System.out.println(createValidationResult.getErrorCollection().toString());
        }
        return false;
    }
    
}
