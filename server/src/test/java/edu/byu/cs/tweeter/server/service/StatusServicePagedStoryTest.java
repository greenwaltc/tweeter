package edu.byu.cs.tweeter.server.service;

import static org.mockito.Mockito.*;

import org.junit.Assert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.model.net.request.StatusesRequest;
import edu.byu.cs.tweeter.model.net.response.StatusesResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.util.Pair;

public class StatusServicePagedStoryTest {

    private StatusService spyService;

    private DAOFactory mockDaoFactory;

    private StatusDAO mockStoryDao;

    private AuthTokenDAO mockAuthTokenDao;

    private Status storyStatus1 = new Status("test-post1", new User(), "test-datetime1", new ArrayList<>(), new ArrayList<>());
    private Status storyStatus2 = new Status("test-post2", new User(), "test-datetime2", new ArrayList<>(), new ArrayList<>());
    private Status storyStatus3 = new Status("test-post3", new User(), "test-datetime3", new ArrayList<>(), new ArrayList<>());

    private List<Status> story = new ArrayList<>() {
        {
            add(storyStatus1);
            add(storyStatus2);
            add(storyStatus3);
        }
    };

    private Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    private AuthToken authToken = new AuthToken("test-token", currentTime.toString());

    private AuthTokenDTO dbAuthToken = new AuthTokenDTO(authToken, "test-user-alias");

    @org.junit.Before
    public void setUp() throws Exception {

        // Mock up dependencies
        mockDaoFactory = mock(DAOFactory.class);
        mockStoryDao = mock(StatusDAO.class);
        mockAuthTokenDao = mock(AuthTokenDAO.class);

        spyService = spy(new StatusService(mockDaoFactory));

        // Stub mock methods
        when(mockDaoFactory.getStoryDAO()).thenReturn(mockStoryDao);
        when(mockDaoFactory.getAuthTokenDAO()).thenReturn(mockAuthTokenDao);
        when(mockAuthTokenDao.get(anyString())).thenReturn(dbAuthToken);

        doNothing().when(mockAuthTokenDao).update(any());
        doNothing().when(mockAuthTokenDao).delete(any());

        doReturn(new Pair(story, false)).when(mockStoryDao).getStatuses(anyString(), anyInt(), any());
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void getStory() {
        StatusesRequest request = new StatusesRequest(authToken, "test-user-alias", 10, null);
        StatusesResponse response = spyService.getStory(request);
        System.out.println();

        Assert.assertEquals(response.getItems(), story);
        Assert.assertEquals(response.getHasMorePages(), false);
    }
}