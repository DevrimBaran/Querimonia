package de.fraunhofer.iao.querimonia.rest.manager;

import de.fraunhofer.iao.querimonia.db.repositories.ResponseComponentRepository;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponent;
import de.fraunhofer.iao.querimonia.response.generation.ResponseComponentBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResponseComponentManagerTest {

    //private ResponseComponentManager responseComponentManager = new ResponseComponentManager(
   //     responseComponentRepository, complaintRepository);
    private ResponseComponentRepository templateRepository;

    @Before
    public void setup() {
        templateRepository = mock(ResponseComponentRepository.class);
        when(templateRepository.save(any(ResponseComponent.class))).thenReturn(
            new ResponseComponentBuilder().createResponseComponent());
    }

    @Test
    public void testAddDefaultTemplates() {
        // TODO fix
        // responseComponentManager.addDefaultTemplates(templateRepository);
    }
}
